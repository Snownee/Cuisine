package snownee.cuisine.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.cuisine.Cuisine;

@SideOnly(Side.CLIENT)
public class ParticleGrowth extends Particle
{
    // Full path is "assets/cuisine/textures/particle/growth.png"
    // "cuisine" is the resource domain
    // "particle/growth" is the resource path, notice that "textures/" path and
    // ".png" extension are omitted
    static final ResourceLocation PARTICLE = new ResourceLocation(Cuisine.MODID, "particle/growth");

    public ParticleGrowth(World world, double x, double y, double z)
    {
        this(world, x, y, z, 0.25F, 0.1F, 1F, 0F, false, 20);
    }

    public ParticleGrowth(World world, double x, double y, double z, double motionX, double motionY, double motionZ)
    {
        this(world, x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        particleGravity = motionY == 0.2 ? 0 : 0.5F;
    }

    public ParticleGrowth(World world, double x, double y, double z, float size, float red, float green, float blue, boolean distanceLimit, float maxAge)
    {
        super(world, x, y, z, 0, 0, 0);
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        particleAlpha = 1F;
        particleGravity = 0.5F;
        particleScale *= size;
        particleMaxAge = (int) ((Math.random() * 0.3D + 0.7D) * maxAge);
        canCollide = false;
        TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLE.toString());
        setParticleTexture(texture);
        Entity renderView = FMLClientHandler.instance().getClient().getRenderViewEntity();

        if (distanceLimit)
        {
            int visibleDistance = 50;
            if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics)
            {
                visibleDistance = 25;
            }

            if (renderView == null || renderView.getDistance(posX, posY, posZ) > visibleDistance)
            {
                particleMaxAge = 0;
            }
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }

    @Override
    public int getFXLayer()
    {
        return 1;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            f = this.particleTexture.getMinU();
            f1 = this.particleTexture.getMaxU();
            f2 = this.particleTexture.getMinV();
            f3 = this.particleTexture.getMaxV();
        }

        float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        Vec3d[] avec3d = new Vec3d[] {
                new Vec3d(-rotationX * f4 - rotationXY * f4, -rotationZ * f4, -rotationYZ * f4 - rotationXZ * f4),
                new Vec3d(-rotationX * f4 + rotationXY * f4, rotationZ * f4, -rotationYZ * f4 + rotationXZ * f4),
                new Vec3d(rotationX * f4 + rotationXY * f4, rotationZ * f4, rotationYZ * f4 + rotationXZ * f4),
                new Vec3d(rotationX * f4 - rotationXY * f4, -rotationZ * f4, rotationYZ * f4 - rotationXZ * f4) };

        if (this.particleAngle != 0.0F)
        {
            float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = MathHelper.cos(f8 * 0.5F);
            float f10 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.x;
            float f11 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.y;
            float f12 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.z;
            Vec3d vec3d = new Vec3d(f10, f11, f12);

            for (int l = 0; l < 4; ++l)
            {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale(f9 * f9 - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale(2.0F * f9));
            }
        }

        buffer.pos(f5 + avec3d[0].x, f6 + avec3d[0].y, f7 + avec3d[0].z).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        buffer.pos(f5 + avec3d[1].x, f6 + avec3d[1].y, f7 + avec3d[1].z).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        buffer.pos(f5 + avec3d[2].x, f6 + avec3d[2].y, f7 + avec3d[2].z).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
        buffer.pos(f5 + avec3d[3].x, f6 + avec3d[3].y, f7 + avec3d[3].z).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(240, 240).endVertex();
    }

    @Override
    public void onUpdate()
    {
        particleScale += 0.05F;
        particleAlpha = 1 - (float) particleAge / particleMaxAge;
        super.onUpdate();
    }

}
