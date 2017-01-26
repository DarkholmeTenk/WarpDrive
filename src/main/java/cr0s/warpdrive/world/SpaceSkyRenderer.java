package cr0s.warpdrive.world;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;

import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class SpaceSkyRenderer extends IRenderHandler {
	private static final ResourceLocation overworldTexture = new ResourceLocation("warpdrive:textures/earth.png");
	private static final ResourceLocation sunTexture = new ResourceLocation("warpdrive:textures/sun.png");
	
	public int starGLCallList = GLAllocation.generateDisplayLists(3);
	public int glSkyList;
	public int glSkyList2;
	
	private static final boolean MORE_STARS = false;
	
	private net.minecraft.client.renderer.vertex.VertexBuffer starVBO;
	private net.minecraft.client.renderer.vertex.VertexBuffer skyVBO;
	private net.minecraft.client.renderer.vertex.VertexBuffer sky2VBO;
	
	public void renderVanilla(float partialTicks, WorldClient world, Minecraft mc, int pass) {
		GlStateManager.disableTexture2D();
		Vec3d vec3d = world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
		float f = (float)vec3d.xCoord;
		float f1 = (float)vec3d.yCoord;
		float f2 = (float)vec3d.zCoord;
		
		if (pass != 2)
		{
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}
	
		GlStateManager.color(f, f1, f2);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.depthMask(false);
		GlStateManager.enableFog();
		GlStateManager.color(f, f1, f2);
	
		boolean vboEnabled = OpenGlHelper.useVbo();
		if (vboEnabled)
		{
			skyVBO.bindBuffer();
			GlStateManager.glEnableClientState(32884);
			GlStateManager.glVertexPointer(3, 5126, 12, 0);
			skyVBO.drawArrays(7);
			skyVBO.unbindBuffer();
			GlStateManager.glDisableClientState(32884);
		}
		else
		{
			GlStateManager.callList(glSkyList);
		}
	
		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
	
		if (afloat != null)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(7425);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			float f6 = afloat[0];
			float f7 = afloat[1];
			float f8 = afloat[2];
	
			if (pass != 2)
			{
				float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
				float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
				float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
				f6 = f9;
				f7 = f10;
				f8 = f11;
			}
	
			vertexbuffer.begin(6, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();
			int j = 16;
	
			for (int l = 0; l <= 16; ++l)
			{
				float f21 = (float)l * ((float)Math.PI * 2F) / 16.0F;
				float f12 = MathHelper.sin(f21);
				float f13 = MathHelper.cos(f21);
				vertexbuffer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
			}
	
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.shadeModel(7424);
		}
	
		GlStateManager.enableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		float f16 = 1.0F - world.getRainStrength(partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
		float f17 = 30.0F;
		Minecraft.getMinecraft().getTextureManager().bindTexture(sunTexture);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(-f17), 100.0D, (double)(-f17)).tex(0.0D, 0.0D).endVertex();
		vertexbuffer.pos((double)f17, 100.0D, (double)(-f17)).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos((double)f17, 100.0D, (double)f17).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)(-f17), 100.0D, (double)f17).tex(0.0D, 1.0D).endVertex();
		tessellator.draw();
		f17 = 20.0F;
		Minecraft.getMinecraft().getTextureManager().bindTexture(overworldTexture);
		int i = world.getMoonPhase();
		int k = i % 4;
		int i1 = i / 4 % 2;
		float f22 = (float)(k) / 4.0F;
		float f23 = (float)(i1) / 2.0F;
		float f24 = (float)(k + 1) / 4.0F;
		float f14 = (float)(i1 + 1) / 2.0F;
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(-f17), -100.0D, (double)f17).tex((double)f24, (double)f14).endVertex();
		vertexbuffer.pos((double)f17, -100.0D, (double)f17).tex((double)f22, (double)f14).endVertex();
		vertexbuffer.pos((double)f17, -100.0D, (double)(-f17)).tex((double)f22, (double)f23).endVertex();
		vertexbuffer.pos((double)(-f17), -100.0D, (double)(-f17)).tex((double)f24, (double)f23).endVertex();
		tessellator.draw();
		GlStateManager.disableTexture2D();
		float f15 = world.getStarBrightness(partialTicks) * f16;
	
		if (f15 > 0.0F)
		{
			GlStateManager.color(f15, f15, f15, f15);
	
			if (vboEnabled)
			{
				starVBO.bindBuffer();
				GlStateManager.glEnableClientState(32884);
				GlStateManager.glVertexPointer(3, 5126, 12, 0);
				starVBO.drawArrays(7);
				starVBO.unbindBuffer();
				GlStateManager.glDisableClientState(32884);
			}
			else
			{
				GlStateManager.callList(this.starGLCallList);
			}
		}
	
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableFog();
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0.0F, 0.0F, 0.0F);
		double d0 = mc.thePlayer.getPositionEyes(partialTicks).yCoord - world.getHorizon();
	
		if (d0 < 0.0D)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 12.0F, 0.0F);
	
			if (vboEnabled)
			{
				sky2VBO.bindBuffer();
				GlStateManager.glEnableClientState(32884);
				GlStateManager.glVertexPointer(3, 5126, 12, 0);
				sky2VBO.drawArrays(7);
				sky2VBO.unbindBuffer();
				GlStateManager.glDisableClientState(32884);
			}
			else
			{
				GlStateManager.callList(this.glSkyList2);
			}
	
			GlStateManager.popMatrix();
			float f18 = 1.0F;
			float f19 = -((float)(d0 + 65.0D));
			float f20 = -1.0F;
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			vertexbuffer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			vertexbuffer.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
		}
	
		if (world.provider.isSkyColored())
		{
			GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
		}
		else
		{
			GlStateManager.color(f, f1, f2);
		}
	
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -((float)(d0 - 16.0D)), 0.0F);
		GlStateManager.callList(glSkyList2);
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
	
	}

	public SpaceSkyRenderer() {
		GL11.glPushMatrix();
		GL11.glNewList(starGLCallList, GL11.GL_COMPILE);
		renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer vertexBuffer = tessellator.getBuffer();
		glSkyList = starGLCallList + 1;
		GL11.glNewList(glSkyList, GL11.GL_COMPILE);
		final byte step = 64;
		final int range = 256 / step + 2;
		float f = 16F;
		
		for (int x = -step * range; x <= step * range; x += step) {
			for (int y = -step * range; y <= step * range; y += step) {
				vertexBuffer.begin(7, DefaultVertexFormats.POSITION);
				vertexBuffer.pos(x       , f, y       ).endVertex();
				vertexBuffer.pos(x + step, f, y       ).endVertex();
				vertexBuffer.pos(x + step, f, y + step).endVertex();
				vertexBuffer.pos(x       , f, y + step).endVertex();
				tessellator.draw();
			}
		}
		
		GL11.glEndList();
		glSkyList2 = starGLCallList + 2;
		GL11.glNewList(glSkyList2, GL11.GL_COMPILE);
		f = -16F;
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION);
		
		for (int x = -step * range; x <= step * range; x += step) {
			for (int y = -step * range; y <= step * range; y += step) {
				vertexBuffer.pos(x + step, f, y       ).endVertex();
				vertexBuffer.pos(x       , f, y       ).endVertex();
				vertexBuffer.pos(x       , f, y + step).endVertex();
				vertexBuffer.pos(x + step, f, y + step).endVertex();
			}
		}

		tessellator.draw();
		GL11.glEndList();
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		SpaceWorldProvider spaceProvider = null;

		if (world.provider instanceof SpaceWorldProvider) {
			spaceProvider = (SpaceWorldProvider) world.provider;
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		final Vec3d var2 = getCustomSkyColor();
		float var3 = (float) var2.xCoord * (1 - world.getStarBrightness(partialTicks) * 2);
		float var4 = (float) var2.yCoord * (1 - world.getStarBrightness(partialTicks) * 2);
		float var5 = (float) var2.zCoord * (1 - world.getStarBrightness(partialTicks) * 2);
		float var8;

		if (mc.gameSettings.anaglyph) {
			final float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
			final float var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
			var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
			var3 = var6;
			var4 = var7;
			var5 = var8;
		}

		GL11.glColor3f(1, 1, 1);
		final Tessellator tessellator = Tessellator.getInstance();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(0, 0, 0);
		GL11.glCallList(glSkyList);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderHelper.disableStandardItemLighting();
		float var10;
		float var11;
		float var12;
		float var20 = 0;

		if (spaceProvider != null) {
			var20 = spaceProvider.getStarBrightness(partialTicks);
		}

		if (var20 > 0.0F) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var20);
			GL11.glCallList(starGLCallList);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glPushMatrix();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 5F);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
		var12 = 30.0F;
		Minecraft.getMinecraft().getTextureManager().bindTexture(sunTexture);
		final VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(-var12, 150.0D, -var12).tex(0.0D, 0.0D).endVertex();
		vertexBuffer.pos( var12, 150.0D, -var12).tex(1.0D, 0.0D).endVertex();
		vertexBuffer.pos( var12, 150.0D,  var12).tex(1.0D, 1.0D).endVertex();
		vertexBuffer.pos(-var12, 150.0D,  var12).tex(0.0D, 1.0D).endVertex();
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_BLEND);
		// HOME:
		var12 = 10.0F;
		final float earthRotation = (float) (world.getSpawnPoint().getZ() - mc.thePlayer.posZ) * 0.01F;
		GL11.glScalef(0.6F, 0.6F, 0.6F);
		GL11.glRotatef(earthRotation, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(200F, 1.0F, 0.0F, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(overworldTexture);
		world.getMoonPhase();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(-var12, -100.0D,  var12).tex(0, 1).endVertex();
		vertexBuffer.pos( var12, -100.0D,  var12).tex(1, 1).endVertex();
		vertexBuffer.pos( var12, -100.0D, -var12).tex(1, 0).endVertex();
		vertexBuffer.pos(-var12, -100.0D, -var12).tex( 0, 0).endVertex();
		tessellator.draw();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0F, 0.0F, 0.0F);
		final double var25 = mc.thePlayer.getPositionEyes(partialTicks).yCoord - world.getHorizon();

		if (var25 < 0.0D) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(glSkyList2);
			GL11.glPopMatrix();
			var10 = 1.0F;
			var11 = -((float) (var25 + 65.0D));
			var12 = -var10;
			GlStateManager.color(0, 0, 0, 255);
			vertexBuffer.begin(7, DefaultVertexFormats.POSITION);
			vertexBuffer.pos(-var10, var11,  var10).endVertex();
			vertexBuffer.pos( var10, var11,  var10).endVertex();
			vertexBuffer.pos( var10, var12,  var10).endVertex();
			vertexBuffer.pos(-var10, var12,  var10).endVertex();
			vertexBuffer.pos(-var10, var12, -var10).endVertex();
			vertexBuffer.pos( var10, var12, -var10).endVertex();
			vertexBuffer.pos( var10, var11, -var10).endVertex();
			vertexBuffer.pos(-var10, var11, -var10).endVertex();
			vertexBuffer.pos( var10, var12, -var10).endVertex();
			vertexBuffer.pos( var10, var12,  var10).endVertex();
			vertexBuffer.pos( var10, var11,  var10).endVertex();
			vertexBuffer.pos( var10, var11, -var10).endVertex();
			vertexBuffer.pos(-var10, var11, -var10).endVertex();
			vertexBuffer.pos(-var10, var11,  var10).endVertex();
			vertexBuffer.pos(-var10, var12,  var10).endVertex();
			vertexBuffer.pos(-var10, var12, -var10).endVertex();
			vertexBuffer.pos(-var10, var12, -var10).endVertex();
			vertexBuffer.pos(-var10, var12,  var10).endVertex();
			vertexBuffer.pos( var10, var12,  var10).endVertex();
			vertexBuffer.pos( var10, var12, -var10).endVertex();
			tessellator.draw();
		}

		GL11.glColor3f(70F / 256F, 70F / 256F, 70F / 256F);
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float) (var25 - 16.0D)), 0.0F);
		GL11.glCallList(glSkyList2);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
	}

	private void renderStars() {
		final Random var1 = new Random(10842L);
		final Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION);
		
		for (int var3 = 0; var3 < (MORE_STARS ? 20000 : 6000); ++var3) {
			double var4 = var1.nextFloat() * 2.0F - 1.0F;
			double var6 = var1.nextFloat() * 2.0F - 1.0F;
			double var8 = var1.nextFloat() * 2.0F - 1.0F;
			final double var10 = 0.15F + var1.nextFloat() * 0.1F;
			double var12 = var4 * var4 + var6 * var6 + var8 * var8;

			if (var12 < 1.0D && var12 > 0.01D) {
				var12 = 1.0D / Math.sqrt(var12);
				var4 *= var12;
				var6 *= var12;
				var8 *= var12;
				final double var14 = var4 * (MORE_STARS ? var1.nextDouble() * 100D + 150D : 100.0D);
				final double var16 = var6 * (MORE_STARS ? var1.nextDouble() * 100D + 150D : 100.0D);
				final double var18 = var8 * (MORE_STARS ? var1.nextDouble() * 100D + 150D : 100.0D);
				final double var20 = Math.atan2(var4, var8);
				final double var22 = Math.sin(var20);
				final double var24 = Math.cos(var20);
				final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
				final double var28 = Math.sin(var26);
				final double var30 = Math.cos(var26);
				final double var32 = var1.nextDouble() * Math.PI * 2.0D;
				final double var34 = Math.sin(var32);
				final double var36 = Math.cos(var32);

				for (int var38 = 0; var38 < 4; ++var38) {
					final double var39 = 0.0D;
					final double var41 = ((var38 & 2) - 1) * var10;
					final double var43 = ((var38 + 1 & 2) - 1) * var10;
					final double var47 = var41 * var36 - var43 * var34;
					final double var49 = var43 * var36 + var41 * var34;
					final double var53 = var47 * var28 + var39 * var30;
					final double var55 = var39 * var28 - var47 * var30;
					final double var57 = var55 * var22 - var49 * var24;
					final double var61 = var49 * var22 + var55 * var24;
					vertexBuffer.pos(var14 + var57, var16 + var53, var18 + var61).endVertex();
				}
			}
		}

		tessellator.draw();
	}

	private static Vec3d getCustomSkyColor() {
		return new Vec3d(0.26796875D, 0.1796875D, 0.0D);
	}
}
