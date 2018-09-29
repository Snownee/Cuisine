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
import snownee.cuisine.api.FoodContainer;
import snownee.cuisine.internal.CuisinePersistenceCenter;

public class TileDish extends TileBase
{
    // Maintain nullability on your own!
    private CompositeFood dish = null;

    public void readDish(ItemStack dish)
    {
        FoodContainer container = dish.getCapability(CulinaryCapabilities.FOOD_CONTAINER, null);
        if (container != null)
        {
            this.dish = container.get();
            if (this.dish != null)
            {
                this.dish.getOrComputeModelType();
                IBlockState state = this.world.getBlockState(this.pos);
                world.notifyBlockUpdate(this.pos, state, state, 1 | 2);
            }
        }
    }

    public ItemStack getItem()
    {
        return (dish == null || dish.isEmpty()) ? new ItemStack(CuisineRegistry.PLACED_DISH) : dish.makeItemStack();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            this.dish = CuisinePersistenceCenter.deserialize(compound.getCompoundTag("dish"));
        }
        else
        {
            this.dish = null;
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        if (dish != null)
        {
            compound.setTag("dish", CuisinePersistenceCenter.serialize(this.dish));
        }
        return super.writeToNBT(compound);
    }

    @Nonnull
    public String getDishModelType()
    {
        return this.dish == null ? "empty" : this.dish.getOrComputeModelType();
    }

    public boolean onEatenBy(EntityPlayer player)
    {
        if (dish != null && !(player instanceof FakePlayer) && hasWorld() && player.canEat(dish.alwaysEdible()))
        {
            ItemStack stack = getItem();
            if (stack.isEmpty())
            {
                return false;
            }
            player.getFoodStats().addStats(dish.getFoodLevel(), dish.getSaturationModifier());
            dish.setServes(dish.getServes() - 1);
            getWorld().playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
            dish.onEaten(stack, getWorld(), player);

            if (dish.getServes() <= 0)
            {
                world.removeTileEntity(pos);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        if (data.hasKey("dish", Constants.NBT.TAG_COMPOUND))
        {
            this.dish = CuisinePersistenceCenter.deserialize(data.getCompoundTag("dish"));
        }
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        if (this.dish != null)
        {
            data.setTag("dish", CuisinePersistenceCenter.serialize(this.dish));
        }
        return data;
    }
}
