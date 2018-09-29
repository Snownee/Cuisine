package snownee.cuisine.tiles;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.items.ItemIngredient;
import snownee.cuisine.util.ItemNBTUtil;

@SuppressWarnings("deprecation")
public class TileChoppingBoard extends TileInventoryBase
{
    // Facing of item, only for rendering. set when player insert item in.
    private EnumFacing facing = EnumFacing.NORTH;
    private boolean isAxe = false;
    private int chopped = 0;
    private final boolean isItem;

    public static final ItemStack DEFAULT_COVER = new ItemStack(Blocks.LOG);
    private ItemStack cover = ItemStack.EMPTY;

    public TileChoppingBoard()
    {
        this(false);
    }

    public TileChoppingBoard(boolean isItem)
    {
        super(1, 1);
        this.isItem = isItem;
    }

    public void resetProcess()
    {
        isAxe = false;
        chopped = 0;
    }

    public ItemStack insertItem(EntityPlayerMP player, ItemStack stack)
    {
        if (stacks.getStackInSlot(0).isEmpty() && stack.getCount() >= 2 && (stack.getItem() == CuisineRegistry.INGREDIENT || (CulinaryHub.API_INSTANCE.isKnownMaterial(stack) && !CulinaryHub.API_INSTANCE.findMaterial(stack).getValidForms().isEmpty())) && SkillUtil.hasPlayerLearnedSkill(player, CulinaryHub.CommonSkills.DOUBLE_CHOPPING))
        {
            ItemStack copy = stack.copy();
            copy.setCount(2);
            stacks.setStackInSlot(0, copy);
            stack.shrink(2);
            return stack;
        }
        ItemStack ret = stacks.insertItem(0, stack, false);
        ItemStack con = stacks.getStackInSlot(0);
        isAxe = CuisineConfig.PROGRESSION.axeChopping && !con.isEmpty() && con.getItem() != CuisineRegistry.KITCHEN_KNIFE && con.getItem() != CuisineRegistry.INGREDIENT && !CulinaryHub.API_INSTANCE.isKnownMaterial(con) && (Processing.CHOPPING.findRecipe(con) != null);
        if (!isAxe && SkillUtil.hasPlayerLearnedSkill(player, CulinaryHub.CommonSkills.DOUBLE_CHOPPING))
        {

        }
        return ret;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return stack.getItem() == CuisineRegistry.KITCHEN_KNIFE || stack.getItem() == CuisineRegistry.INGREDIENT || (CulinaryHub.API_INSTANCE.isKnownMaterial(stack) && !CulinaryHub.API_INSTANCE.findMaterial(stack).getValidForms().isEmpty()) || (CuisineConfig.PROGRESSION.axeChopping && Processing.CHOPPING.findRecipe(stack) != null);
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if (compound.hasKey("facing", Constants.NBT.TAG_INT))
        {
            facing = EnumFacing.byHorizontalIndex(compound.getInteger("facing"));
        }
        if (compound.hasKey("axe", Constants.NBT.TAG_BYTE))
        {
            isAxe = compound.getBoolean("axe");
        }
        if (compound.hasKey("chopped", Constants.NBT.TAG_INT))
        {
            chopped = compound.getInteger("chopped");
        }
        cover = compound.hasKey("cover", Constants.NBT.TAG_COMPOUND) ? new ItemStack(compound.getCompoundTag("cover"))
                : DEFAULT_COVER;
        if (cover.isEmpty())
        {
            setCover(DEFAULT_COVER);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("facing", facing.getHorizontalIndex());
        compound.setBoolean("axe", isAxe);
        compound.setInteger("chopped", chopped);
        if (cover.isEmpty())
        {
            setCover(DEFAULT_COVER);
        }
        compound.setTag("cover", cover.serializeNBT());
        return compound;
    }

    @Nonnull
    @Override
    protected NBTTagCompound writePacketData(NBTTagCompound data)
    {
        data.setInteger("facing", facing.getHorizontalIndex());
        data.setBoolean("axe", isAxe);
        if (cover.isEmpty())
        {
            setCover(DEFAULT_COVER);
        }
        data.setTag("cover", cover.serializeNBT());
        return super.writePacketData(data);
    }

    @Override
    protected void readPacketData(NBTTagCompound data)
    {
        super.readPacketData(data);
        facing = EnumFacing.byHorizontalIndex(data.getInteger("facing"));
        isAxe = data.getBoolean("axe");
        cover = new ItemStack(data.getCompoundTag("cover"));
        if (cover.isEmpty())
        {
            setCover(DEFAULT_COVER);
        }
    }

    public void setCover(ItemStack cover)
    {
        this.cover = cover;
    }

    public IBlockState getCover()
    {
        return Block.getBlockFromItem(cover.getItem()).getStateFromMeta(cover.getMetadata());
    }

    public ItemStack getSelfItem()
    {
        return CuisineRegistry.CHOPPING_BOARD.getItemStack(cover);
    }

    public boolean isItem()
    {
        return isItem;
    }

    public void process(EntityPlayer playerIn, ItemStack tool, ProcessionType type, @Nullable Integer harvestlevel)
    {
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        if (world.isRemote)
        {
            return;
        }

        ItemStack stack = stacks.getStackInSlot(0);
        if (ProcessionType.AXE != type)
        {
            Ingredient processingIngredient = tryConvert(stack);
            if (processingIngredient == null)
            {
                return;
            }
            if (playerIn instanceof EntityPlayerMP)
            {
                if (SkillUtil.hasPlayerLearnedSkill((EntityPlayerMP) playerIn, CulinaryHub.CommonSkills.SKILLED_CHOPPING))
                {
                    if (processingIngredient.getMaterial() == CulinaryHub.CommonMaterials.PUFFERFISH)
                    {
                        processingIngredient.removeEffect(CulinaryHub.CommonEffects.PUFFERFISH_POISON);
                    }
                }
                else if (processingIngredient.getMaterial() == CulinaryHub.CommonMaterials.ONION)
                {
                    AxisAlignedBB axisalignedbb = new AxisAlignedBB(pos).grow(4.0D, 2.0D, 4.0D);
                    List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

                    if (!list.isEmpty())
                    {
                        for (EntityLivingBase entitylivingbase : list)
                        {
                            if (entitylivingbase.canBeHitWithPotion())
                            {
                                double d0 = entitylivingbase.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                                if (d0 < 16.0D)
                                {
                                    double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                                    int i = (int) (d1 * 400);

                                    if (i > 20)
                                    {
                                        entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, i, 0, true, false));
                                        entitylivingbase.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, i, 0, true, false));
                                        if (entitylivingbase instanceof EntityPlayerMP)
                                        {
                                            SkillUtil.increasePoint((EntityPlayerMP) entitylivingbase, CulinarySkillPoint.EXPERTISE, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int[] actions = ItemIngredient.getActions(stack);

            int i = type == ProcessionType.KNIFE_VERTICAL ? 1 : 0;
            if (actions[i] < type.getMaxProcessLimit())
            {
                ++actions[i];
            }
            if (playerIn instanceof EntityPlayerMP && actions[i] < 10 && getWorld().rand.nextInt(5) == 0)
            {
                SkillUtil.increasePoint((EntityPlayerMP) playerIn, CulinarySkillPoint.PROFICIENCY, 1);
            }
            boolean fewerLosses = playerIn instanceof EntityPlayerMP && SkillUtil.hasPlayerLearnedSkill((EntityPlayerMP) playerIn, CulinaryHub.CommonSkills.FEWER_LOSSES);
            stacks.setStackInSlot(0, craftMaterial(stack, processingIngredient, actions, fewerLosses, world.rand));
        }
        else if (isAxe)
        {
            harvestlevel = harvestlevel == null ? 1 : harvestlevel + 1;
            tool.damageItem(1, playerIn);
            chopped += harvestlevel;
            if (tool.isEmpty() || chopped >= type.getMaxProcessLimit()) // small feature to have a fatal hit
            {
                Chopping recipe = Processing.CHOPPING.findRecipe(stacks.getStackInSlot(0));
                if (recipe != null)
                {
                    recipe.consume(stacks);
                    if (!world.isRemote)
                    {
                        // Never trust API will always provide a copy to you. Never. Even if the API is designed by yourself.
                        ItemStack output = recipe.getOutput().copy();
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output.splitStack(output.getCount() / 2));
                        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output);
                    }
                    resetProcess();
                }
            }
        }
    }

    public static ItemStack craftMaterial(ItemStack raw, Ingredient ingredient, int[] actions, boolean fewerLosses, Random rand)
    {
        Form form = Form.byActions(actions[0], actions[1]);
        if (ingredient.getMaterial().isValidForm(form))
        {
            ingredient.setForm(form);
        }
        else if (raw.getItem() != CuisineRegistry.INGREDIENT)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setIntArray(ItemIngredient.KEY_ACTIONS, actions);
            raw.setTagCompound(tag);
            return raw;
        }
        if (ingredient.getSize() > 0.5)
        {
            ingredient.decreaseSizeBy(rand.nextFloat() * (fewerLosses ? 0.02F : 0.05F));
        }
        ItemStack itemIngredient = ItemIngredient.make(ingredient);
        itemIngredient.setCount(raw.getCount());
        ItemNBTUtil.setIntArray(itemIngredient, ItemIngredient.KEY_ACTIONS, actions);
        return itemIngredient;
    }

    private static Ingredient tryConvert(ItemStack stack)
    {
        if (stack.getItem() == CuisineRegistry.INGREDIENT)
        {
            NBTTagCompound data;
            if ((data = stack.getTagCompound()) == null)
            {
                return null;
            }
            else
            {
                return CuisinePersistenceCenter.deserializeIngredient(data);
            }
        }
        else
        {
            return Ingredient.make(stack, 1);
        }
    }

    public enum ProcessionType
    {
        KNIFE_HORIZONTAL, KNIFE_VERTICAL, AXE;

        public int getMaxProcessLimit()
        {
            return this != AXE ? 10 : 8;
        }
    }

    @Override
    public void onContentsChanged(int slot)
    {
        if (!isItem)
        {
            refresh();
        }
    }
}
