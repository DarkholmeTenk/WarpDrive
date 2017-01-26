package cr0s.warpdrive.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumCameraType;

public class RenderOverlayCamera {
	private Minecraft mc;
	private int frameCount = 0;
	private static final int ANIMATION_FRAMES = 200;
	
	public RenderOverlayCamera(Minecraft parMinecraft) {
		mc = parMinecraft;
	}
	
	private static int colorGradient(float gradient, int start, int end) {
		return Math.max(0, Math.min(255, start + Math.round(gradient * (end - start))));
	}
	
	private void renderOverlay(int scaledWidth, int scaledHeight) {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		try {
			String strHelp;
			if (ClientCameraHandler.overlayType == EnumCameraType.SIMPLE_CAMERA) {
				mc.getTextureManager().bindTexture(new ResourceLocation("warpdrive", "textures/blocks/detection/cameraOverlay.png"));
				strHelp = "Left click to zoom / Right click to exit";
			} else {
				mc.getTextureManager().bindTexture(new ResourceLocation("warpdrive", "textures/blocks/weapon/laserCameraOverlay.png"));
				strHelp = "Left click to zoom / Right click to exit / Space to fire";
			}
			
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexBuffer = tessellator.getBuffer();
			
			vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexBuffer.pos(       0.0D, scaledHeight, -90.0D).tex(0.0D, 1.0D).endVertex();
			vertexBuffer.pos(scaledWidth, scaledHeight, -90.0D).tex(1.0D, 1.0D).endVertex();
			vertexBuffer.pos(scaledWidth,         0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
			vertexBuffer.pos(       0.0D,         0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
			tessellator.draw();
			
			frameCount++;
			if (frameCount >= ANIMATION_FRAMES) {
				frameCount = 0;
			}
			float time = Math.abs(frameCount * 2.0F / ANIMATION_FRAMES - 1.0F);
			int color = (colorGradient(time, 0x40, 0xA0) << 16) + (colorGradient(time, 0x80, 0x00) << 8) + colorGradient(time, 0x80, 0xFF);
			mc.fontRendererObj.drawString(strHelp,
					(scaledWidth - mc.fontRendererObj.getStringWidth(strHelp)) / 2,
					(int)(scaledHeight * 0.19) - mc.fontRendererObj.FONT_HEIGHT,
					color, true);
			
			String strZoom = "Zoom " + (ClientCameraHandler.originalFOV / mc.gameSettings.fovSetting) + "x";
			mc.fontRendererObj.drawString(strZoom,
					(int) (scaledWidth * 0.91) - mc.fontRendererObj.getStringWidth(strZoom),
					(int) (scaledHeight * 0.81),
					0x40A080, true);
			
			if (WarpDriveConfig.LOGGING_CAMERA) {
				mc.fontRendererObj.drawString(ClientCameraHandler.overlayLoggingMessage,
					(scaledWidth - mc.fontRendererObj.getStringWidth(ClientCameraHandler.overlayLoggingMessage)) / 2,
					(int)(scaledHeight * 0.19),
					0xFF008F, true);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre event) {
		if (ClientCameraHandler.isOverlayEnabled) {
			if (event.getType() == ElementType.HELMET) {
				renderOverlay(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
			} else if (event.getType() == ElementType.AIR
					|| event.getType() == ElementType.ARMOR
					|| event.getType() == ElementType.BOSSHEALTH
					|| event.getType() == ElementType.CROSSHAIRS
					|| event.getType() == ElementType.EXPERIENCE
					|| event.getType() == ElementType.FOOD
					|| event.getType() == ElementType.HEALTH
					|| event.getType() == ElementType.HEALTHMOUNT
					|| event.getType() == ElementType.HOTBAR
					|| event.getType() == ElementType.TEXT) {
				// Don't render other GUI parts
				if (event.isCancelable()) {
					event.setCanceled(true);
				}
			}
		}
	}
}