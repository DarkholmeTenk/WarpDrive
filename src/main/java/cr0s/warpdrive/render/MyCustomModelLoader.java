package cr0s.warpdrive.render;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.block.forcefield.BlockForceFieldProjector;
import cr0s.warpdrive.data.EnumForceFieldShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.*;

// Wrapper around OBJLoader to re-texture faces depending on IExtendedBlockState
public enum MyCustomModelLoader implements ICustomModelLoader {
	INSTANCE;
	
	private static boolean spriteInitialisationDone = false; 
	private static TextureAtlasSprite spriteShape_none;
	private static HashMap<EnumForceFieldShape, TextureAtlasSprite> spriteShapes = new HashMap<>(EnumForceFieldShape.length);
	private static void initSprites() {
		if (!spriteInitialisationDone) {
			TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
			spriteShapes.put(EnumForceFieldShape.NONE      , textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_none"));
			spriteShapes.put(EnumForceFieldShape.CUBE      , textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_cube"));
			spriteShapes.put(EnumForceFieldShape.CYLINDER_H, textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_cylinder_h"));
			spriteShapes.put(EnumForceFieldShape.CYLINDER_V, textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_cylinder_v"));
			spriteShapes.put(EnumForceFieldShape.PLANE     , textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_plane"));
			spriteShapes.put(EnumForceFieldShape.SPHERE    , textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_sphere"));
			spriteShapes.put(EnumForceFieldShape.TUBE      , textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_tube"));
			spriteShapes.put(EnumForceFieldShape.TUNNEL    , textureMapBlocks.getAtlasSprite("warpdrive:blocks/forcefield/projector-shape_tunnel"));
			spriteShape_none = spriteShapes.get(EnumForceFieldShape.NONE);
		}
	}
	
	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		OBJLoader.INSTANCE.onResourceManagerReload(resourceManager);
		spriteInitialisationDone = false;
	}
	
	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return WarpDrive.MODID.equals(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".wobj");
	}
	
	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		return new MyModel(OBJLoader.INSTANCE.loadModel(modelLocation));
	}
	
	private class MyModel implements IModel {
		private final IModel model;
		
		MyModel(final IModel model) {
			this.model = model;
		}
		
		@Override
		public Collection<ResourceLocation> getDependencies() {
			return model.getDependencies();
		}
		
		@Override
		public Collection<ResourceLocation> getTextures() {
			return model.getTextures();
		}
		
		@Override
		public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			return new MyBakedModel(model.bake(state, format, bakedTextureGetter));
		}
		
		@Override
		public IModelState getDefaultState() {
			return model.getDefaultState();
		}
	}
	
	class MyBakedModel implements IPerspectiveAwareModel {
		
		private final IBakedModel bakedModel;
		
		MyBakedModel(final IBakedModel bakedModel) {
			this.bakedModel = bakedModel;
			initSprites();
		}
		
		@Nonnull
		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState blockState, @Nullable EnumFacing side, long rand) {
			
			List<BakedQuad> bakedQuadsIn = bakedModel.getQuads(blockState, side, rand);
			IExtendedBlockState exState = (IExtendedBlockState) blockState;
			EnumForceFieldShape enumForceFieldShape = exState != null ? exState.getValue(BlockForceFieldProjector.SHAPE) : EnumForceFieldShape.NONE;
			List<BakedQuad> bakedQuadsOut = Lists.newArrayList();
			for(BakedQuad bakedQuadIn : bakedQuadsIn) {
				if (bakedQuadIn.getSprite().equals(spriteShape_none)) {
					BakedQuad bakedQuadOut = new BakedQuadRetextured(bakedQuadIn, spriteShapes.get(enumForceFieldShape));
					bakedQuadsOut.add(bakedQuadOut);
				} else {
					bakedQuadsOut.add(bakedQuadIn);
				}
			}
			return ImmutableList.copyOf(bakedQuadsOut);
		}
		
		@Override
		public boolean isAmbientOcclusion() {
			return bakedModel.isAmbientOcclusion();
		}
		
		@Override
		public boolean isGui3d() {
			return bakedModel.isGui3d();
		}
		
		@Override
		public boolean isBuiltInRenderer() {
			return bakedModel.isBuiltInRenderer();
		}
		
		@Nonnull
		@Override
		public TextureAtlasSprite getParticleTexture() {
			return bakedModel.getParticleTexture();
		}
		
		@SuppressWarnings("deprecation")
		@Nonnull
		@Override
		public ItemCameraTransforms getItemCameraTransforms() {
			return bakedModel.getItemCameraTransforms();
		}
		
		@Nonnull
		@Override
		public ItemOverrideList getOverrides() {
			return bakedModel.getOverrides();
		}
		
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			if (bakedModel instanceof IPerspectiveAwareModel) {
				return ((IPerspectiveAwareModel) bakedModel).handlePerspective(cameraTransformType);
			}
			return null;
		}
	}
}
