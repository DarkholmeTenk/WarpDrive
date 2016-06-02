package cr0s.warpdrive.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.data.Vector3;

@SideOnly(Side.CLIENT)
public class EntityFXBeam extends EntityFX
{
    private static ResourceLocation TEXTURE = null;

    double movX = 0.0D;
    double movY = 0.0D;
    double movZ = 0.0D;

    private float length = 0.0F;
    private float rotYaw = 0.0F;
    private float rotPitch = 0.0F;
    private float prevYaw = 0.0F;
    private float prevPitch = 0.0F;
    private Vector3 target = new Vector3();
    private float endModifier = 1.0F;
    private boolean reverse = false;
    private boolean pulse = true;
    private int rotationSpeed = 20;
    private float prevSize = 0.0F;
    private int energy = 0;

    boolean a = false;

    public EntityFXBeam(World par1World, Vector3 position, float yaw, float pitch, float red, float green, float blue, int age, int energy)
    {
        super(par1World, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
        a = true;
        this.setRGB(red, green, blue);
        this.setSize(0.02F, 0.02F);
        this.noClip = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.length = 200;
        this.rotYaw = yaw;
        this.rotPitch = pitch;
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;
        this.particleMaxAge = age;
        this.energy = energy;

        if (red == 1 && green == 0 && blue == 0) {
            TEXTURE = new ResourceLocation("warpdrive", "textures/blocks/energy_grey.png");
        }

        /**
         * Sets the particle age based on distance.
         */
        EntityLivingBase entityRender = Minecraft.getMinecraft().renderViewEntity;
        int visibleDistance = 300;

        if (!Minecraft.getMinecraft().gameSettings.fancyGraphics)
        {
            visibleDistance = 100;
        }

        if (entityRender.getDistance(posX, posY, posZ) > visibleDistance)
        {
            particleMaxAge = 0;
        }
    }

    public EntityFXBeam(World par1World, Vector3 position, Vector3 target, float red, float green, float blue, int age, int energy)
    {
        super(par1World, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);
        this.setRGB(red, green, blue);
        this.setSize(0.02F, 0.02F);
        this.noClip = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.target = target;
        float xd = (float)(this.posX - this.target.x);
        float yd = (float)(this.posY - this.target.y);
        float zd = (float)(this.posZ - this.target.z);
        this.length = (float) new Vector3(this).distanceTo(this.target);
        double var7 = MathHelper.sqrt_double(xd * xd + zd * zd);
        this.rotYaw = ((float)(Math.atan2(xd, zd) * 180.0D / Math.PI));
        this.rotPitch = ((float)(Math.atan2(yd, var7) * 180.0D / Math.PI));
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;
        this.particleMaxAge = age;
        this.energy = energy;

        TEXTURE = new ResourceLocation("warpdrive", "textures/blocks/energy_grey.png");
        
        /**
         * Sets the particle age based on distance.
         */
        EntityLivingBase entityRender = Minecraft.getMinecraft().renderViewEntity;
        int visibleDistance = 300;

        if (!Minecraft.getMinecraft().gameSettings.fancyGraphics)
        {
            visibleDistance = 100;
        }

        if (entityRender.getDistance(posX, posY, posZ) > visibleDistance)
        {
            particleMaxAge = 0;
        }

        //this.pulse = (energy == 0);
        //if (TEXTURE != null) {
        //	System.out.println("BeamFX created. Texture: " + TEXTURE);
        //}
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        prevYaw = rotYaw;
        prevPitch = rotPitch;

        if (!a)
        {
            float xd = (float)(posX - target.x);
            float yd = (float)(posY - target.y);
            float zd = (float)(posZ - target.z);
            length = MathHelper.sqrt_float(xd * xd + yd * yd + zd * zd);
            double var7 = MathHelper.sqrt_double(xd * xd + zd * zd);
            rotYaw = ((float)(Math.atan2(xd, zd) * 180.0D / Math.PI));
            rotPitch = ((float)(Math.atan2(yd, var7) * 180.0D / Math.PI));
        }

        if (particleAge++ >= particleMaxAge)
        {
            setDead();
        }
    }

    public void setRGB(float r, float g, float b)
    {
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5)
    {
        tessellator.draw();
        GL11.glPushMatrix();
        float var9 = 1.0F;
        float slide = worldObj.getTotalWorldTime();
        float rot = worldObj.provider.getWorldTime() % (360 / rotationSpeed) * rotationSpeed + rotationSpeed * f;
        float size = 1.0F;

        if (pulse)
        {
            size = Math.min(particleAge / 4.0F, 1.0F);
            size = prevSize + (size - prevSize) * f;
        }
        else
        {
            size = Math.min(Math.max(energy / 50000F, 1.0F), 7F);
        }

        float op = 0.5F;

        if ((pulse) && (particleMaxAge - particleAge <= 4))
        {
            op = 0.5F - (4 - (particleMaxAge - particleAge)) * 0.1F;
        }

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        GL11.glDisable(GL11.GL_CULL_FACE);
        float var11 = slide + f;

        if (reverse)
        {
            var11 *= -1.0F;
        }

        float var12 = -var11 * 0.2F - MathHelper.floor_float(-var11 * 0.1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);
        float xx = (float)(prevPosX + (posX - prevPosX) * f - interpPosX);
        float yy = (float)(prevPosY + (posY - prevPosY) * f - interpPosY);
        float zz = (float)(prevPosZ + (posZ - prevPosZ) * f - interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float ry = prevYaw + (rotYaw - prevYaw) * f;
        float rp = prevPitch + (rotPitch - prevPitch) * f;
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F + ry, 0.0F, 0.0F, -1.0F);
        GL11.glRotatef(rp, 1.0F, 0.0F, 0.0F);
        double var44 = -0.15D * size;
        double var17 = 0.15D * size;
        double var44b = -0.15D * size * endModifier;
        double var17b = 0.15D * size * endModifier;
        GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);

        for (int t = 0; t < 3; t++)
        {
            double var29 = length * size * var9;
            double var31 = 0.0D;
            double var33 = 1.0D;
            double var35 = -1.0F + var12 + t / 3.0F;
            double var37 = length * size * var9 + var35;
            GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.setBrightness(200);
            tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, op);
            tessellator.addVertexWithUV(var44b, var29, 0.0D, var33, var37);
            tessellator.addVertexWithUV(var44, 0.0D, 0.0D, var33, var35);
            tessellator.addVertexWithUV(var17, 0.0D, 0.0D, var31, var35);
            tessellator.addVertexWithUV(var17b, var29, 0.0D, var31, var37);
            tessellator.draw();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        tessellator.startDrawingQuads();
        prevSize = size;
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("textures/particle/particles.png"));
    }
}