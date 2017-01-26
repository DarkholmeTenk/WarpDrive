package cr0s.warpdrive.render;

import com.google.common.base.Function;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.forcefield.TileEntityForceFieldProjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class TileEntityForceFieldProjectorRenderer extends TileEntitySpecialRenderer<TileEntityForceFieldProjector> {

	private IBakedModel bakedModel;
	
	private enum TextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		INSTANCE;
		
		public TextureAtlasSprite apply(ResourceLocation location) {
			// WarpDrive.logger.info("TileEntityForceFieldProjectorRenderer texture location " + location);
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	}
	
	private IBakedModel getBakedModel() {
		// Since we cannot bake in preInit() we do lazy baking of the model as soon as we need it for rendering
		if (bakedModel == null) {
			IModel model;
			ResourceLocation resourceLocation = new ResourceLocation(WarpDrive.MODID, "block/forcefield/projector_ring.obj");
			try {
				model = ModelLoaderRegistry.getModel(resourceLocation);
			} catch (Exception exception) {
				WarpDrive.logger.info("getModel " + resourceLocation);
				throw new RuntimeException(exception);
			}
			bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, TextureGetter.INSTANCE);
		}
		return bakedModel;
	}
	
	private static List<BakedQuad> quads;
	@Override
	public void renderTileEntityAt(TileEntityForceFieldProjector tileEntityForceFieldProjector, double x, double y, double z, float partialTicks, int destroyStage) {
		if (!tileEntityForceFieldProjector.getWorld().isBlockLoaded(tileEntityForceFieldProjector.getPos(), false)) {
			return;
		}
		if (quads == null) {
			quads = getBakedModel().getQuads(null, null, 0L);
		}
		Tessellator tessellator = Tessellator.getInstance();
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
		switch (tileEntityForceFieldProjector.enumFacing) {
			case DOWN : GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F); break;
			case UP   : break;
			case NORTH: GlStateManager.rotate(+90.0F, 1.0F, 0.0F, 0.0F); GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); break;
			case SOUTH: GlStateManager.rotate(+90.0F, 1.0F, 0.0F, 0.0F); break;
			case WEST : GlStateManager.rotate(+90.0F, 1.0F, 0.0F, 0.0F); GlStateManager.rotate(+90.0F, 0.0F, 0.0F, 1.0F); break;
			case EAST : GlStateManager.rotate(+90.0F, 1.0F, 0.0F, 0.0F); GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F); break;
			default: break;
		}
		
		GlStateManager.blendFunc(770, 771);	// srcalpha, 1-srcalpha
		GlStateManager.enableBlend();
		// GlStateManager.disableCull();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		
		final float wheelRotation = tileEntityForceFieldProjector.rotation_deg + partialTicks * tileEntityForceFieldProjector.rotationSpeed_degPerTick;
		GlStateManager.rotate(wheelRotation, 0.0F, 1.0F, 0.0F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		VertexBuffer worldRenderer = tessellator.getBuffer();
		worldRenderer.setTranslation(-0.5, -0.5, -0.5);
		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		
		renderModelTESR(quads, worldRenderer, tileEntityForceFieldProjector.getWorld().getCombinedLight(tileEntityForceFieldProjector.getPos(), 15));
		
		tessellator.draw();
		worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
		
		RenderHelper.enableStandardItemLighting();
		GlStateManager.disableBlend();
		// GlStateManager.enableCull();
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}
	
	public static void renderModelTESR(List<BakedQuad> quads, VertexBuffer renderer, int brightness) {
		int l1 = (brightness >> 0x10) & 0xFFFF;
		int l2 = brightness & 0xFFFF;
		for (BakedQuad quad : quads) {
			int[] vData = quad.getVertexData();
			VertexFormat format = quad.getFormat();
			int size = format.getIntegerSize();
			int uv = format.getUvOffsetById(0) / 4;
			for (int i = 0; i < 4; ++i) {
				renderer
						.pos(	Float.intBitsToFloat(vData[size * i    ]),
								Float.intBitsToFloat(vData[size * i + 1]),
								Float.intBitsToFloat(vData[size * i + 2]))
						.color(255, 255, 255, 255)
						.tex(Float.intBitsToFloat(vData[size * i + uv]), Float.intBitsToFloat(vData[size * i + uv + 1]))
						.lightmap(l1, l2)
						.endVertex();
			}
		}
	}
}
