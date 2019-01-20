package snownee.cuisine.tiles;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.CuisineConfig;
import snownee.cuisine.CuisineRegistry;
import snownee.cuisine.api.CulinaryHub;
import snownee.cuisine.api.CulinarySkillPoint;
import snownee.cuisine.api.Form;
import snownee.cuisine.api.Ingredient;
import snownee.cuisine.api.process.Chopping;
import snownee.cuisine.api.process.Processing;
import snownee.cuisine.api.util.SkillUtil;
import snownee.cuisine.client.particle.ParticleQuad;
import snownee.cuisine.internal.CuisinePersistenceCenter;
import snownee.cuisine.items.ItemIngredient;
import snownee.kiwi.tile.TileInventoryBase;
import snownee.kiwi.util.NBTHelper;
import snownee.kiwi.util.OreUtil;

public class TileChoppingBoard extends TileInventoryBase
{
    // Facing of item, only for rendering. set when player insert item in.
    private EnumFacing facing = EnumFacing.NORTH;
    private boolean isAxe = false;
    private int chopped = 0;
    public long tickLastChop;

    public static final ItemStack DEFAULT_COVER = new ItemStack(Blocks.LOG);
    private ItemStack cover = ItemStack.EMPTY;

    public TileChoppingBoard()
    {
        super(1, 1);
    }

    public void resetProcess()
    {
        isAxe = false;
        chopped = 0;
    }

    public ItemStack insertItem(EntityPlayer player, ItemStack stack)
    {
        Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack);

        if (ingredient != null && ingredient.getForm() != Form.JUICE && !ingredient.getMaterial().getValidForms().isEmpty() && (ingredient.getMaterial().getValidForms().size() > 1 || !ingredient.getMaterial().getValidForms().contains(Form.JUICE)) && stacks.getStackInSlot(0).isEmpty() && stack.getCount() >= 2 && SkillUtil.hasPlayerLearnedSkill(player, CulinaryHub.CommonSkills.DOUBLE_CHOPPING))
        {
            ItemStack copy = stack.copy();
            copy.setCount(2);
            stacks.setStackInSlot(0, copy);
            stack.shrink(2);
            return stack;
        }
        ItemStack ret = stacks.insertItem(0, stack, false);
        ItemStack con = stacks.getStackInSlot(0);
        isAxe = CuisineConfig.GENERAL.axeChopping && !con.isEmpty() && !OreUtil.doesItemHaveOreName(stack, "itemFoodCutter") && con.getItem() != CuisineRegistry.INGREDIENT && !CulinaryHub.API_INSTANCE.isKnownIngredient(con) && (Processing.CHOPPING.findRecipe(con) != null);
        return ret;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        if (OreUtil.doesItemHaveOreName(stack, "itemFoodCutter"))
        {
            return true;
        }
        if (CuisineConfig.GENERAL.axeChopping && Processing.CHOPPING.findRecipe(stack) != null)
        {
            return true;
        }
        Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack);
        return ingredient != null && ingredient.getForm() != Form.JUICE && !ingredient.getMaterial().getValidForms().isEmpty() && (ingredient.getMaterial().getValidForms().size() > 1 || !ingredient.getMaterial().getValidForms().contains(Form.JUICE));
    }

    public boolean hasKitchenKnife()
    {
        return OreUtil.doesItemHaveOreName(stacks.getStackInSlot(0), "itemFoodCutter");
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
        cover = compound.hasKey("cover", Constants.NBT.TAG_COMPOUND) ? new ItemStack(compound.getCompoundTag("cover")) : DEFAULT_COVER;
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
        // Refresh rendering after data are all set, for getActualState depends on them
        this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
    }

    public void setCover(ItemStack cover)
    {
        this.cover = cover;
    }

    public ItemStack getCover()
    {
        return cover;
    }

    public ItemStack getSelfItem()
    {
        return CuisineRegistry.CHOPPING_BOARD.getItemStack(cover);
    }

    public void process(EntityPlayer playerIn, ItemStack tool, ProcessionType type, @Nullable Integer harvestlevel)
    {
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 0.3F, 0.5F);
        ItemStack stack = stacks.getStackInSlot(0);
        if (world.isRemote && !stack.isEmpty())
        {
            updateTick(stack);
            return;
        }

        if (ProcessionType.AXE != type)
        {
            Ingredient processingIngredient = tryConvert(stack);
            if (processingIngredient == null)
            {
                return;
            }
            if (playerIn instanceof EntityPlayerMP)
            {
                tool.attemptDamageItem(1, world.rand, (EntityPlayerMP) playerIn);
                if (SkillUtil.hasPlayerLearnedSkill(playerIn, CulinaryHub.CommonSkills.SKILLED_CHOPPING))
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
                SkillUtil.increasePoint(playerIn, CulinarySkillPoint.PROFICIENCY, 1);
            }
            // boolean fewerLosses = playerIn instanceof EntityPlayerMP && SkillUtil.hasPlayerLearnedSkill(playerIn, CulinaryHub.CommonSkills.FEWER_LOSSES);
            stacks.setStackInSlot(0, craftMaterial(stack, processingIngredient, actions, world.rand));
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

    @SideOnly(Side.CLIENT)
    private void updateTick(ItemStack stack)
    {
        if (stack.getItem() == CuisineRegistry.KITCHEN_KNIFE)
        {
            return;
        }
        tickLastChop = Minecraft.getSystemTime();
        Minecraft mc = Minecraft.getMinecraft();
        if (stack.getItem() == CuisineRegistry.INGREDIENT)
        {
            Ingredient ingredient = CulinaryHub.API_INSTANCE.findIngredient(stack);
            if (ingredient == null)
            {
                return;
            }
            int color = ingredient.getMaterial().getColorCode(ingredient.getDoneness());
            for (int i = 0; i < 4; i++)
            {
                mc.effectRenderer.addEffect(new ParticleQuad(world, pos, color));
            }
        }
        else
        {
            for (int i = 0; i < 8; i++)
            {
                world.spawnParticle(EnumParticleTypes.ITEM_CRACK, false, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, (world.rand.nextDouble() - 0.5) * 0.2, 0.1, (world.rand.nextDouble() - 0.5) * 0.2, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
            }
        }
    }

    private static ItemStack craftMaterial(ItemStack raw, Ingredient ingredient, int[] actions, Random rand)
    {
        Form form = Form.byActions(actions[0], actions[1]);
        if (ingredient.getMaterial().isValidForm(form))
        {
            ingredient.setForm(form);
        }
        else if (raw.getItem() != CuisineRegistry.INGREDIENT)
        {
            return NBTHelper.of(raw).setIntArray(ItemIngredient.KEY_ACTIONS, actions).getItem();
        }
        ItemStack itemIngredient = ItemIngredient.make(ingredient);
        itemIngredient.setCount(raw.getCount());
        return NBTHelper.of(itemIngredient).setIntArray(ItemIngredient.KEY_ACTIONS, actions).getItem();
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
            return CulinaryHub.API_INSTANCE.findIngredient(stack);
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
        refresh();
    }
}
