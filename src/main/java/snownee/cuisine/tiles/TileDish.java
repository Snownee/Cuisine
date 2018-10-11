package snownee.cuisine.tiles;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CompositeFood;
import snownee.cuisine.api.CulinaryCapabilities;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.internal.CuisineSharedSecrets;
import snownee.cuisine.internal.food.Dish;

public class TileDish extends TileBase
{
    // Whoever assigns null on this one shall just burn into ashes in the furious flame of a wonky wok
    private ItemStack dishContainer = ItemStack.EMPTY;

    public void readDish(ItemStack dish)
    {
        FoodContainer container = dish.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            this.dishContainer = dish;
            if (!dish.isEmpty())
            {
                IBlockState state = this.world.getBlockState(this.pos);
                world.notifyBlockUpdate(this.pos, state, state, 1 | 2);
            }
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
        FoodContainer container = this.dishContainer.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood dish;
        if (container != null && (dish = container.get()) != null)
        {
            return dish.getOrComputeModelType();
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
        FoodContainer container = this.dishContainer.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        CompositeFood dish;
        if (container != null && (dish = container.get()) != null)
        {
            if (!(player instanceof FakePlayer) && hasWorld() && player.canEat(dish.alwaysEdible()))
            {
                player.getFoodStats().addStats(dish.getFoodLevel(), dish.getSaturationModifier());
                dish.setServes(dish.getServes() - 1);
                getWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
                dish.onEaten(this.dishContainer, getWorld(), player);

                if (dish.getServes() <= 0)
                {
                    world.removeTileEntity(pos);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        this.dishContainer = new ItemStack(data.getCompoundTag("DishContainer"));
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setTag("DishContainer", this.dishContainer.serializeNBT());
        return data;
    }
}
