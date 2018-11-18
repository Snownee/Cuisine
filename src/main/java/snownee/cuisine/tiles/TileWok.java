package snownee.cuisine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CookingStrategy;
import snownee.cuisine.api.CookingStrategyProvider;
import snownee.cuisine.api.CookingVessel;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.IngredientTrait;
import snownee.cuisine.api.Seasoning;
import snownee.cuisine.api.Spice;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.blocks.BlockFirePit;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.items.ItemSpiceBottle;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.network.NetworkChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TileWok extends TileBase implements CookingVessel, ITickable
{

    public enum Status
    {
        IDLE, WORKING
    }

    static
    {
        NetworkChannel.INSTANCE.register(PacketIncrementalWokUpdate.class);
    }

    private Status status = Status.IDLE;
    private Dish.Builder builder;
    private transient Dish completedDish;
    private int temperature, water, oil;
    public byte actionCycle = 0;
    final transient List<ItemStack> ingredientsForRendering = new ArrayList<>(8);
    final transient List<FluidStack> spicesForRendering = new ArrayList<>(8);

    @Override
    public void update()
    {
        if (!world.isRemote && status == Status.WORKING)
        {
            if (temperature < 300 && this.world.rand.nextInt(20) == 0)
            {
                this.temperature += this.world.rand.nextInt(5);
            }
            if (builder != null && this.world.getWorldTime() % 20 == 0)
            {
                builder.apply(new Heating(this), this);
            }
        }

        // WIP, do not bother!

        double pX = this.pos.getX() + 0.5 + (this.world.rand.nextDouble() - 1.0/2);
        double pY = this.pos.getY() + 1.0 + (this.world.rand.nextDouble() - 1.0/2);
        double pZ = this.pos.getZ() + 0.5 + (this.world.rand.nextDouble() - 1.0/2);
        this.world.spawnParticle(EnumParticleTypes.FLAME, pX, pY, pZ, 0, 0, 0);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (oldState.getBlock() != CuisineRegistry.FIRE_PIT || newState.getBlock() != CuisineRegistry.FIRE_PIT)
        {
            return true;
        }
        else
        {
            return oldState.getValue(BlockFirePit.COMPONENT) != newState.getValue(BlockFirePit.COMPONENT);
        }
    }

    public Status getStatus()
    {
        return status;
    }

    public int getTemperature()
    {
        return this.temperature;
    }

    public int getWaterAmount()
    {
        return this.water;
    }

    public int getOilAmount()
    {
        return this.oil;
    }

    public void onActivated(EntityPlayerMP playerIn, EnumHand hand, EnumFacing facing)
    {
        switch (status)
        {
            case IDLE:
            {
                ItemStack heldThing = playerIn.getHeldItem(hand);
                if (CulinaryHub.API_INSTANCE.findIngredient(heldThing) != null || heldThing.getItem() instanceof ItemSpiceBottle)
                {
                    this.builder = Dish.Builder.create();
                    boolean result = addIngredientsOrSpices(playerIn, hand, heldThing, facing);
                    if (result)
                    {
                        this.status = Status.WORKING;
                    }
                    else
                    {
                        builder = null;
                    }
                }
                break;
            }
            case WORKING:
                ItemStack heldThing = playerIn.getHeldItem(hand);
                if (addIngredientsOrSpices(playerIn, hand, heldThing, facing))
                {
                    break;
                }
                else if (heldThing.getItem() == Item.getItemFromBlock(CuisineRegistry.PLACED_DISH) && !builder.getIngredients().isEmpty())
                {
                    Optional<Dish> result = builder.build(this, playerIn);
                    if (!result.isPresent())
                    {
                        return;
                    }

                    this.completedDish = result.get(); // TODO THIS IS HACK, NO OBJECTION

                    SkillUtil.increasePoint(playerIn, CulinarySkillPoint.EXPERTISE, (int) (completedDish.getFoodLevel() * completedDish.getSaturationModifier()));
                    SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);

                    heldThing.shrink(1);
                    playerIn.openGui(Cuisine.getInstance(), CuisineGUI.NAME_FOOD, world, pos.getX(), pos.getY(), pos.getZ());

                    this.temperature = 0;
                    break;
                }
                else
                {
                    tryCook(playerIn, heldThing);
                }
                break;
        }

        // TODO uh.
        // this.world.playSound(null, this.pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 1F, 1F);
    }

    public void tryCook(EntityPlayer cook, ItemStack heldThing)
    {
        CookingStrategy strategy = this.determineCookingStrategy(heldThing);
        if (strategy == null)
        {
            return;
        }

        double limit = builder.getMaxSize();
        if (!SkillUtil.hasPlayerLearnedSkill(cook, CulinaryHub.CommonSkills.BIGGER_SIZE))
        {
            limit *= 0.75;
        }

        if (builder.getCurrentSize() > limit)
        {
            // FakePlayer won't see this message anyway. See FakePlayer#sendStatusMessage for details
            cook.sendStatusMessage(new TextComponentTranslation(I18nUtil.getFullKey("gui.wok_size_too_large")), true);
            return;
        }

        builder.apply(strategy, this);
        if (getWorld().rand.nextInt(5) == 0)
        {
            SkillUtil.increasePoint(cook, CulinarySkillPoint.PROFICIENCY, 1);
        }
        NetworkChannel.INSTANCE.sendToDimension(new PacketCustomEvent(3, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), this.getWorld().provider.getDimension());
    }

    private boolean addIngredientsOrSpices(EntityPlayerMP player, EnumHand hand, ItemStack heldThing, EnumFacing facing)
    {
        Ingredient ingredient;
        if (heldThing.getItem() instanceof ItemSpiceBottle)
        {
            Spice spice = CuisineRegistry.SPICE_BOTTLE.getSpice(heldThing);
            if (spice != null)
            {
                if (ItemSpiceBottle.hasFluid(heldThing))
                {
                    this.trackSpice(ItemSpiceBottle.getFluidHandler(heldThing).drain(1, false));
                }
                CuisineRegistry.SPICE_BOTTLE.consume(heldThing, 1);
                Seasoning seasoning = new Seasoning(spice);
                this.builder.addSeasoning(player, seasoning, this);
                return true;
            }
            else
            {
                return false;
            }
        }
        else if ((ingredient = CulinaryHub.API_INSTANCE.findIngredient(heldThing)) != null)
        {
            if (this.builder.addIngredient(player, ingredient, this))
            {
                ItemStack newStack = heldThing.splitStack(1);
                this.trackIngredient(newStack);
                NetworkChannel.INSTANCE.sendToAll(new PacketIncrementalWokUpdate(this.getPos(), newStack));
                return true;
            }
            else
            {
                player.sendStatusMessage(new TextComponentTranslation(I18nUtil.getFullKey("gui.cannot_add_more")), true);
                return false;
            }
        }

        return false;
    }

    /**
     * Returning a list of ItemStack for rendering purpose.
     *
     * @return View of ingredients that added into this TileWok
     */
    public List<ItemStack> getWokContents()
    {
        return Collections.unmodifiableList(this.ingredientsForRendering);
    }

    private void trackIngredient(ItemStack ingredient)
    {
        this.ingredientsForRendering.add(ingredient);
    }

    private void trackSpice(@Nullable FluidStack spice)
    {
        if (spice == null)
        {
            return;
        }
        for (FluidStack content : this.spicesForRendering)
        {
            if (content.isFluidEqual(spice))
            {
                content.amount += spice.amount;
                spice = null;
                break;
            }
        }
        if (spice != null)
        {
            spicesForRendering.add(spice);
        }
    }

    @Nullable
    private CookingStrategy determineCookingStrategy(ItemStack heldItem)
    {
        Item item = heldItem.getItem();
        if (item instanceof CookingStrategyProvider)
        {
            return ((CookingStrategyProvider) item).getCookingStrategy(heldItem);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.temperature = compound.getInteger("temperature");
        this.status = compound.getBoolean("status") ? Status.WORKING : Status.IDLE;
        if (compound.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            this.builder = Dish.Builder.fromNBT(compound.getCompoundTag("dish"));
        }
        NBTTagList items = compound.getTagList("rendering", Constants.NBT.TAG_COMPOUND);
        for (NBTBase tag : items)
        {
            if (tag instanceof NBTTagCompound)
            {
                this.ingredientsForRendering.add(new ItemStack((NBTTagCompound) tag));
            }
        }
        NBTTagList spices = compound.getTagList("rendering", Constants.NBT.TAG_COMPOUND);
        for (NBTBase tag : spices)
        {
            if (tag instanceof NBTTagCompound)
            {
                FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((NBTTagCompound) tag);
                if (fluidStack != null)
                {
                    this.spicesForRendering.add(fluidStack);
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("temperature", this.temperature);
        compound.setBoolean("status", this.status == Status.WORKING);
        if (builder != null)
        {
            compound.setTag("dish", Dish.Builder.toNBT(this.builder));
        }
        NBTTagList items = new NBTTagList();
        for (ItemStack item : this.ingredientsForRendering)
        {
            items.appendTag(item.serializeNBT());
        }
        compound.setTag("rendering", items);
        NBTTagList spices = new NBTTagList();
        for (FluidStack fluid : this.spicesForRendering)
        {
            spices.appendTag(fluid.writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("renderingSpices", spices);
        return super.writeToNBT(compound);
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        return data;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        // No-op
    }

    static final class Heating implements CookingStrategy
    {
        private final TileWok wok;

        Heating(TileWok wok)
        {
            this.wok = wok;
        }

        // TODO This is just a prototype, we need further refinement

        private int ingredientSize = 0;
        private int initialTemp = -1, currentHeat = -1;
        private int decrement = 0;

        @Override
        public void beginCook(CompositeFood.Builder<?> dish)
        {
            this.ingredientSize = dish.getIngredients().size();
        }

        @Override
        public void preCook(Seasoning seasoning, CookingVessel vessel)
        {
            if (ingredientSize < 1)
            {
                return;
            }
            currentHeat = initialTemp = this.wok.getTemperature();
            decrement = initialTemp / ingredientSize;
        }

        @Override
        public void cook(Ingredient ingredient, CookingVessel vessel)
        {
            if (ingredientSize < 1)
            {
                return;
            }
            int increment = Math.max(0, initialTemp / 4);
            ingredient.setHeat(Math.max(ingredient.getHeat() + increment, initialTemp));
            if (ingredient.getHeat() > 100)
            {
                // Unconditionally remove the undercooked trait, so that
                // we won't see both co-exist together
                ingredient.removeTrait(IngredientTrait.UNDERCOOKED);
                if (ingredient.getHeat() > 250 && Math.random() < 0.01)
                {
                    ingredient.addTrait(IngredientTrait.OVERCOOKED);
                }
            }
            currentHeat -= decrement;
        }

        @Override
        public void postCook(CompositeFood.Builder<?> dish, CookingVessel vessel)
        {

        }

        @Override
        public void endCook()
        {

        }

    }

    @Override
    public Optional<ItemStack> serve()
    {
        if (status == Status.IDLE || builder == null)
        {
            return Optional.empty();
        }

        ItemStack stack = new ItemStack(CuisineRegistry.DISH);
        FoodContainer container = stack.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            container.set(this.completedDish); // TODO AGAIN, THIS IS HACK
        }
        else
        {
            throw new NullPointerException("Null FoodContainer");
        }
        this.builder = null;
        this.completedDish = null;
        this.status = Status.IDLE;
        this.ingredientsForRendering.clear();
        this.spicesForRendering.clear();
        NetworkChannel.INSTANCE.sendToAll(new PacketIncrementalWokUpdate(this.getPos(), ItemStack.EMPTY));

        return Optional.of(stack);
    }
}
