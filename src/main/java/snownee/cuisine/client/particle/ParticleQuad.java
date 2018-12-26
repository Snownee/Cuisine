package snownee.cuisine.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleQuad extends Particle
{
    public ParticleQuad(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, float r, float g, float b)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        particleMaxAge = 50;
        particleScale = 3;
        particleGravity = 1;
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }

    public ParticleQuad(World world, BlockPos pos, int color)
    {
        super(world, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 0, 0.1, 0);
        particleMaxAge = 50;
        particleScale = 3;
        particleGravity = 1;
        particleAlpha = (color >> 24 & 255) / 255F;
        particleRed = (color >> 16 & 255) / 255F;
        particleGreen = (color >> 8 & 255) / 255F;
        particleBlue = (color & 255) / 255F;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public void onUpdate()
    {
        if (onGround)
        {
            setExpired();
            return;
        }
        super.onUpdate();
    }
}
