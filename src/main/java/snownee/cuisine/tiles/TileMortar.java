package snownee.cuisine.tiles;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Material;
import snownee.cuisine.api.process.Grinding;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.items.ItemIngredient;
import snownee.cuisine.library.RarityManager;
import snownee.cuisine.util.StacksUtil;

public class TileMortar extends TileInventoryBase
{
    private int processTime = 0;
    public boolean pestle = false;
    private Grinding recipe;
    private boolean makingPaste;

    public TileMortar()
    {
        super(5);
    }

    public void process(EntityPlayer player)
    {
        pestle = !pestle;
        if (world == null || world.isRemote)
        {
            return;
        }

        if (!pestle)
        {
            return;
        }

        processTime++;
        if (recipe != null)
        {
            if (processTime >= recipe.getStep())
            {
                processTime = 0;
                StacksUtil.spawnItemStack(world, getPos(), recipe.getOutput().copy(), true);
                recipe.consume(this.stacks);
                this.recipe = null;
                SkillUtil.increasePoint(player, CulinarySkillPoint.EXPERTISE, 1);
            }
        }
        else if (makingPaste)
        {
            if (processTime >= 5)
            {
                processTime = 0;
                ItemStack input = this.stacks.getStackInSlot(0);
                Material material = CulinaryHub.API_INSTANCE.findMaterial(stacks.getStackInSlot(0));
                if (material != null && material.isValidForm(Form.PASTE))
                {
                    ItemStack output = ItemIngredient.make(material, Form.PASTE, RarityManager.getRarity(input) == EnumRarity.COMMON ? 0.8F : 1.2F);
                    StacksUtil.spawnItemStack(world, getPos(), output, true);
                    this.recipe = null; // Stop things from happening
                    input.shrink(1);
                    SkillUtil.increasePoint(player, CulinarySkillPoint.PROFICIENCY, 3);
                }
                makingPaste = false;
            }
        }
        else
        {
            recipe = Processing.GRINDING.findRecipe(stacks.getStacks().toArray(new Object[5]));
            if (recipe == null)
            {
                Material material = CulinaryHub.API_INSTANCE.findMaterial(stacks.getStackInSlot(0));
                if (material != null && material.isValidForm(Form.PASTE))
                {
                    makingPaste = true;
                }
                else
                {
                    processTime = 0; // Reset counter when no recipe found to avoid exploit
                }
            }
        }

    }

    public ItemStack insertItem(ItemStack stack)
    {
        return ItemHandlerHelper.insertItemStacked(stacks, stack, false);
    }

    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        pestle = compound.getBoolean("Pestle");
        processTime = compound.getInteger("ProcessTime");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("ProcessTime", processTime);
        compound.setBoolean("Pestle", pestle);
        return compound;
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setBoolean("Pestle", pestle);
        data.setInteger("ProcessTime", processTime);
        return super.writePacketData(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        super.readPacketData(data);
        this.pestle = data.getBoolean("Pestle");
        this.processTime = data.getInteger("ProcessTime");
        if (world.isRemote)
        {
            // Call this on client to ensure that rendering get a refresh
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
