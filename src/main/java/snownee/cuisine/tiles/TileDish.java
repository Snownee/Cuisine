package snownee.cuisine.tiles;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.Event;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.api.events.ConsumeCompositeFoodEvent;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.food.Dish;
import snownee.kiwi.tile.TileBase;

public class TileDish extends TileBase
{
    // Whoever assigns null on this one shall just burn into ashes in the furious flame of a wonky wok
    private ItemStack dishContainer = ItemStack.EMPTY;

    private CompositeFood food = null;

    public void readDish(ItemStack dish)
    {
        if (dish.getTagCompound() == null)
        {
            return;
        }
        final ResourceLocation type = new ResourceLocation(dish.getTagCompound().getString(CuisineSharedSecrets.KEY_TYPE));
        CompositeFood food = CulinaryHub.API_INSTANCE.deserialize(type, dish.getTagCompound());
        if (food != null)
        {
            this.dishContainer = dish;
            this.food = food;
            IBlockState state = this.world.getBlockState(this.pos);
            world.notifyBlockUpdate(this.pos, state, state, 1 | 2);
        }
    }

    public ItemStack getItem()
    {
        return this.dishContainer.isEmpty() ? new ItemStack(CuisineRegistry.PLACED_DISH) : this.dishContainer.copy();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("DishContainer", Constants.NBT.TAG_COMPOUND))
        {
            this.dishContainer = new ItemStack(compound.getCompoundTag("DishContainer"));
        }
        // Backward compatibility. At this point, the only possibility is that
        // this is a serialized snownee.cuisine.internal.food.Dish object.
        else if (compound.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            NBTTagCompound data = compound.getCompoundTag("dish");
            // As stated above, since there was only one type of CompositeFood,
            // we assume that the data structure is the same, so we use the
            // corresponding deserializers to complete the migration.
            CompositeFood dish = Dish.deserialize(data);
            if (dish != null)
            {
                // And finally we wrap it in an ItemStack.
                this.dishContainer = dish.makeItemStack();
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("DishContainer", this.dishContainer.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Nonnull
    public String getDishModelType()
    {
        if (this.food != null)
        {
            return this.food.getOrComputeModelType();
        }
        else
        {
            return "empty";
        }
    }

    public boolean onEatenBy(EntityPlayer player)
    {
        if (this.dishContainer.isEmpty())
        {
            return false;
        }
        if (this.food != null)
        {
            ConsumeCompositeFoodEvent.Pre pre = new ConsumeCompositeFoodEvent.Pre(this.food, player, this.pos);
            if (!MinecraftForge.EVENT_BUS.post(pre) && pre.getResult() != Event.Result.DENY)
            {
                if (!(player instanceof FakePlayer) && hasWorld() && player.canEat(this.food.alwaysEdible()))
                {
                    this.food.setServes(this.food.getServes() - 1);
                    getWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
                    this.food.onEaten(this.dishContainer, getWorld(), player);

                    ConsumeCompositeFoodEvent.Post post = new ConsumeCompositeFoodEvent.Post(this.food, player, this.pos);
                    MinecraftForge.EVENT_BUS.post(post);

                    if (this.food.getServes() <= 0)
                    {
                        world.removeTileEntity(pos);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        this.dishContainer = new ItemStack(data.getCompoundTag("DishContainer"));
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
        // TODO (3TUSK): Sync food data
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setTag("DishContainer", this.dishContainer.serializeNBT());
        return data;
        // TODO (3TUSK): Sync food data
    }
}
