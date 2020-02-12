package snownee.cuisine.tiles;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.Cuisine;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.*;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.client.gui.CuisineGUI;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.food.Dish;
import snownee.cuisine.items.ItemSpiceBottle;
import snownee.cuisine.network.PacketCustomEvent;
import snownee.cuisine.util.I18nUtil;
import snownee.kiwi.network.NetworkChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class TileWok extends TileFirePit implements CookingVessel
{

    public enum Status
    {
        IDLE, WORKING
    }

    public static class SeasoningInfo
    {
        public int volume;
        public int color;
    }

    static
    {
        NetworkChannel.INSTANCE.register(PacketIncrementalWokUpdate.class);
        NetworkChannel.INSTANCE.register(PacketWokSeasoningsUpdate.class);
    }

    private Status status = Status.IDLE;
    private Dish.Builder builder;
    private transient Dish completedDish;
    public byte actionCycle = 0;
    final transient Map<Ingredient, ItemStack> ingredientsForRendering = new LinkedHashMap<>(8);
    @Nullable
    public SeasoningInfo seasoningInfo;
    private boolean shouldRefresh = false;

    @Override
    public void update()
    {
        super.update();
        if (!world.isRemote && status == Status.WORKING)
        {
            if (builder != null && heatHandler.getHeatPower() > 0 && this.world.getTotalWorldTime() % 20 == 0)
            {
                builder.apply(new Heating(heatHandler), this);
                if (!builder.getIngredients().isEmpty())
                {
                    requiresRefresh();
                }
            }
            if (shouldRefresh && this.world.getTotalWorldTime() % 5 == 0)
            {
                refresh();
                shouldRefresh = false;
            }
        }
    }

    public Status getStatus()
    {
        return status;
    }

    public void onActivated(EntityPlayerMP playerIn, EnumHand hand, EnumFacing facing)
    {
        ItemStack heldThing = playerIn.getHeldItem(hand);
        switch (status)
        {
        case IDLE:
        {
            boolean isIngredient = heldThing.getItem() instanceof ItemSpiceBottle || CulinaryHub.API_INSTANCE.findIngredient(heldThing) != null;
            if (isIngredient || FuelHeatHandler.isFuel(heldThing, true))
            {
                if (isIngredient)
                {
                    this.builder = Dish.Builder.create();
                }
                boolean result = cook(playerIn, hand, heldThing, facing);
                if (isIngredient && result)
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
            if (cook(playerIn, hand, heldThing, facing))
            {
                break;
            }
            else if (heldThing.getItem() == Item.getItemFromBlock(CuisineRegistry.PLACED_DISH) && !builder.getIngredients().isEmpty())
            {
                Optional<Dish> result = builder.build(this, playerIn); // TODO THIS IS HACK, NO OBJECTION
                if (!result.isPresent())
                {
                    return;
                }

                this.completedDish = result.get();

                SkillUtil.increasePoint(playerIn, CulinarySkillPoint.EXPERTISE, (int) (completedDish.getFoodLevel() * completedDish.getSaturationModifier()));
                SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);

                if (!playerIn.isCreative())
                {
                    heldThing.shrink(1);
                }
                playerIn.openGui(Cuisine.getInstance(), CuisineGUI.NAME_FOOD, world, pos.getX(), pos.getY(), pos.getZ());

                break;
            }
            else
            {
                CookingStrategy strategy = determineCookingStrategy(heldThing);
                if (strategy == null)
                {
                    return;
                }
                double limit = builder.getMaxIngredientLimit();
                if (!SkillUtil.hasPlayerLearnedSkill(playerIn, CulinaryHub.CommonSkills.BIGGER_SIZE))
                {
                    limit *= 0.75;
                }
                if (builder.getIngredients().size() > limit)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation(I18nUtil.getFullKey("gui.wok_size_too_large")), true);
                    return;
                }

                builder.apply(strategy, this);
                if (getHeatHandler().getLevel() > 0 && getWorld().rand.nextInt(5) == 0)
                {
                    SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);
                }
                NetworkChannel.INSTANCE.sendToDimension(new PacketCustomEvent(3, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()), this.getWorld().provider.getDimension());
            }
            break;
        }
    }

    private boolean cook(EntityPlayerMP player, EnumHand hand, ItemStack heldThing, EnumFacing facing)
    {
        Ingredient ingredient;
        if (heldThing.getItem() instanceof ItemSpiceBottle)
        {
            if (CuisineRegistry.SPICE_BOTTLE.hasFluid(heldThing))
            {
                FluidStack fluid = CuisineRegistry.SPICE_BOTTLE.getFluidHandler(heldThing).drain(Integer.MAX_VALUE, false);
                if (fluid == null || fluid.amount < 100)
                {
                    return false;
                }
            }
            Spice spice = CuisineRegistry.SPICE_BOTTLE.getSpice(heldThing);
            if (spice != null)
            {
                if (!player.isCreative())
                {
                    CuisineRegistry.SPICE_BOTTLE.consume(heldThing, 1);
                }
                Seasoning seasoning = new Seasoning(spice);
                this.builder.addSeasoning(player, seasoning, this);
                refreshSeasoningInfo();
                return true;
            }
            else
            {
                return false;
            }
        }
        else if ((ingredient = CulinaryHub.API_INSTANCE.findIngredient(heldThing)) != null)
        {
            if (!SkillUtil.hasPlayerLearnedSkill(player, CulinaryHub.CommonSkills.BIGGER_SIZE))
            {
                if (this.builder.getIngredients().size() + 1 > this.builder.getMaxIngredientLimit() * 0.75)
                {
                    return false;
                }
            }
            if (this.builder.addIngredient(player, ingredient, this))
            {
                ItemStack newStack = ItemHandlerHelper.copyStackWithSize(heldThing, 1);
                if (!player.isCreative())
                {
                    heldThing.shrink(1);
                }
                this.ingredientsForRendering.put(ingredient, newStack);
                NetworkChannel.INSTANCE.sendToAll(new PacketIncrementalWokUpdate(this.getPos(), ingredient, newStack));
                return true;
            }
            else
            {
                player.sendStatusMessage(new TextComponentTranslation(I18nUtil.getFullKey("gui.cannot_add_more")), true);
                return false;
            }
        }
        else if (FuelHeatHandler.isFuel(heldThing, true))
        {
            ItemStack remain = heatHandler.addFuel(heldThing);
            if (!player.isCreative())
            {
                player.setHeldItem(hand, remain);
            }
            refresh();
            return true;
        }

        return false;
    }

    /**
     * Returning a list of ItemStack for rendering purpose.
     *
     * @return View of ingredients that added into this TileWok
     */
    public Map<Ingredient, ItemStack> getWokContents()
    {
        return Collections.unmodifiableMap(this.ingredientsForRendering);
    }

    public void refreshSeasoningInfo()
    {
        if (builder == null)
        {
            return;
        }
        if (seasoningInfo == null)
        {
            seasoningInfo = new SeasoningInfo();
        }

        List<Seasoning> seasonings = builder.getSeasonings();
        if (!seasonings.isEmpty())
        {
            Seasoning seasoning = null;
            int a = 0, r = 0, g = 0, b = 0, size = 0;
            for (Seasoning s : seasonings)
            {
                if (s.getSpice().isLiquid(s))
                {
                    int color = s.getSpice().getColorCode();
                    a += s.getSize() * (color >> 24 & 255);
                    r += s.getSize() * (color >> 16 & 255);
                    g += s.getSize() * (color >> 8 & 255);
                    b += s.getSize() * (color & 255);
                    size += s.getSize();
                    if (seasoning == null || seasoning.getSize() < s.getSize())
                    {
                        seasoning = s;
                    }
                }
            }
            if (seasoning == null)
            {
                seasoningInfo.volume = 0;
            }
            else if (seasoning.hasKeyword("water") && seasoning.getSize() == size)
            {
                seasoningInfo.volume = size;
                seasoningInfo.color = 0xFF4C57D1;
            }
            else
            {
                int color = seasoning.getSpice().getColorCode();
                a += seasoning.getSize() * (color >> 24 & 255);
                r += seasoning.getSize() * (color >> 16 & 255);
                g += seasoning.getSize() * (color >> 8 & 255);
                b += seasoning.getSize() * (color & 255);
                seasoningInfo.volume = size;
                size += seasoning.getSize();
                a = a / size;
                r = r / size;
                g = g / size;
                b = b / size;
                seasoningInfo.color = a << 24 | r << 16 | g << 8 | b;
            }
        }
        if (world != null && !world.isRemote)
        {
            NetworkChannel.INSTANCE.sendToAll(new PacketWokSeasoningsUpdate(this.getPos(), seasoningInfo));
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
        this.status = compound.getBoolean("status") ? Status.WORKING : Status.IDLE;
        if (compound.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            this.builder = Dish.Builder.fromNBT(compound.getCompoundTag("dish"));
            refreshSeasoningInfo();
        }
        NBTTagList items = compound.getTagList("rendering", Constants.NBT.TAG_COMPOUND);
        for (NBTBase tag : items)
        {
            if (tag instanceof NBTTagCompound)
            {
                NBTTagCompound tagCompound = (NBTTagCompound) tag;
                if (tagCompound.hasKey("ingredient", Constants.NBT.TAG_COMPOUND))
                {
                    this.ingredientsForRendering.put(CuisinePersistenceCenter.deserializeIngredient(tagCompound.getCompoundTag("ingredient")), new ItemStack(tagCompound.getCompoundTag("item")));
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setBoolean("status", this.status == Status.WORKING);
        if (builder != null)
        {
            compound.setTag("dish", Dish.Builder.toNBT(this.builder));
        }
        NBTTagList map = new NBTTagList();
        for (Entry<Ingredient, ItemStack> entry : this.ingredientsForRendering.entrySet())
        {
            NBTTagCompound tagEntry = new NBTTagCompound();
            tagEntry.setTag("ingredient", CuisinePersistenceCenter.serialize(entry.getKey()));
            tagEntry.setTag("item", entry.getValue().serializeNBT());
            map.appendTag(tagEntry);
        }
        compound.setTag("rendering", map);
        return super.writeToNBT(compound);
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        if (builder != null && !builder.getIngredients().isEmpty())
        {
            int[] donenesses = new int[builder.getIngredients().size()];
            int i = 0;
            for (Ingredient ingredient : builder.getIngredients())
            {
                donenesses[i] = ingredient.getDoneness();
                ++i;
            }
            data.setIntArray("donenesses", donenesses);
        }
        return super.writePacketData(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        if (data.hasKey("donenesses", Constants.NBT.TAG_INT_ARRAY))
        {
            int[] donenesses = data.getIntArray("donenesses");
            if (donenesses.length == ingredientsForRendering.size())
            {
                int i = 0;
                for (Entry<Ingredient, ItemStack> entry : ingredientsForRendering.entrySet())
                {
                    entry.getKey().setDoneness(donenesses[i]);
                    if (entry.getValue().getItem() == CuisineRegistry.INGREDIENT)
                    {
                        entry.getValue().getTagCompound().setInteger(CuisineSharedSecrets.KEY_DONENESS, donenesses[i]);
                    }
                    if (seasoningInfo != null && seasoningInfo.volume < 2 && donenesses[i] > 130 && world.rand.nextInt(10) == 0)
                    {
                        float f = (float) (world.rand.nextFloat() * Math.PI * 2);
                        double x = MathHelper.sin(f) * 0.1D;
                        double y = pos.getY() + 0.2D + world.rand.nextDouble() * 0.05D;
                        double z = MathHelper.cos(f) * 0.1D;
                        world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), pos.getX() + 0.5D + x, y, pos.getZ() + 0.5D + z, 0D, 0D, 0D);
                    }
                    ++i;
                }
            }
        }
        super.readPacketData(data);
    }

    static final class Heating implements CookingStrategy<Dish.Builder>
    {
        private HeatHandler heatHandler;
        //private int count = 0;
        private Dish.Builder builder;

        Heating(HeatHandler heatHandler)
        {
            this.heatHandler = heatHandler;
        }

        @Override
        public void beginCook(Dish.Builder food)
        {
            builder = food;
        }

        @Override
        public void preCook(Seasoning seasoning, CookingVessel vessel)
        {
        }

        @Override
        public void cook(Ingredient ingredient, CookingVessel vessel)
        {
            if (heatHandler.getHeatPower() <= 0)
                return;
            double progress;
            if (ingredient.getMaterial().getBoilHeat() <= heatHandler.getHeat())
                progress = Math.pow(ingredient.getMaterial().getBoilHeat() - heatHandler.getHeat(), 2) / Math.pow(ingredient.getMaterial().getBoilHeat(), 2) + 1;
            else
                progress = 1 - Math.pow(ingredient.getMaterial().getBoilHeat() - heatHandler.getHeat(), 2) / Math.pow(ingredient.getMaterial().getBoilHeat(), 2);
            boolean enoughWater = builder.getWaterAmount() >= 200 || builder.getWaterAmount() / builder.getIngredients().size() >= 100;
            int newDoneness = ingredient.getDoneness() + MathHelper.floor(MathHelper.clamp(progress, 0, ingredient.getMaterial().getBoilTime()));
            if (!(enoughWater && newDoneness > 110))
            {
                ingredient.setDoneness(newDoneness);
            }
        }

        @Override
        public void postCook(Dish.Builder food, CookingVessel vessel)
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
        NetworkChannel.INSTANCE.sendToAll(new PacketIncrementalWokUpdate(this.getPos(), null, ItemStack.EMPTY));

        return Optional.of(stack);
    }

    public void requiresRefresh()
    {
        shouldRefresh = true;
    }
}
