package snownee.cuisine.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = Cuisine.MODID, value = Side.CLIENT)
public class HoloProfiles
{
    public static final Int2ObjectOpenHashMap<HoloProfile> PROFILES = new Int2ObjectOpenHashMap<>();
    public static BlockPos focusingPos = BlockPos.ORIGIN;

    public static HoloProfile get(TileEntity tile)
    {
        HoloProfile hp = PROFILES.get(tile.getPos().hashCode());
        if (hp == null)
        {
            PROFILES.put(tile.getPos().hashCode(), hp = new HoloProfile(tile.getPos()));
        }
        return hp;
    }

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event)
    {
        PROFILES.remove(event.getPos().hashCode());
    }

    @SubscribeEvent
    public static void joinWorld(EntityJoinWorldEvent event)
    {
        if (event.getEntity() == Minecraft.getMinecraft().player)
        {
            PROFILES.clear();
        }
    }

    public static class HoloProfile
    {
        public final BlockPos pos;
        public float alpha;
        public float icon0, icon1, icon2;
        public float minProgress, maxProgress;
        public float extraWidth;

        public HoloProfile(BlockPos pos)
        {
            this.pos = pos;
            icon0 = 0;
            icon1 = 0.8f;
            icon2 = 0.8f;
        }

        public float update(boolean focusing, float minHeat, float heat, float maxHeat, float partialTicks)
        {
            if (focusing)
            {
                focusingPos = pos;
            }
            else if (focusingPos.equals(pos))
            {
                focusingPos = BlockPos.ORIGIN;
            }

            int level = 1;
            float target0 = 0;
            float target1 = heat / (maxHeat - minHeat) - 0.1f;
            float target2 = 1;

            float delta = partialTicks / 20;
            icon0 = chase(icon0, target0, delta * Math.abs(target0 - icon0));
            icon1 = chase(icon1, target1, delta * Math.abs(target1 - icon1));
            icon2 = chase(icon2, target2, delta * Math.abs(target2 - icon2));

            float targetMin, targetMax;
            targetMin = minHeat;
            targetMax = maxHeat;
            minProgress = chase(minProgress, targetMin, delta * Math.abs(targetMin - minProgress));
            maxProgress = chase(maxProgress, targetMax, delta * Math.abs(targetMax - maxProgress));

            return alpha = chase(alpha, focusing ? 1 : 0, delta * 2);
        }

        public static float chase(float raw, float target, float delta)
        {
            if (target == raw)
            {
                return target;
            }
            float min = Math.min(raw, target);
            float max = Math.max(raw, target);
            return MathHelper.clamp(raw + (target > raw ? delta : -delta), min, max);
        }

        @Override
        public int hashCode()
        {
            return pos.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return pos.equals(obj);
        }
    }
}
