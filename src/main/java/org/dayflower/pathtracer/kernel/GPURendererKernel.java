/**
 * Copyright 2015 - 2020 J&#246;rgen Lundgren
 * 
 * This file is part of Dayflower.
 * 
 * Dayflower is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Dayflower is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dayflower.pathtracer.kernel;

import static org.macroing.math4j.MathF.PI;
import static org.macroing.math4j.MathF.PI_DIVIDED_BY_180;
import static org.macroing.math4j.MathF.PI_MULTIPLIED_BY_TWO;
import static org.macroing.math4j.MathF.PI_MULTIPLIED_BY_TWO_RECIPROCAL;
import static org.macroing.math4j.MathF.PI_RECIPROCAL;

import java.util.Arrays;
import java.util.List;

import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.bvh.BoundingVolumeHierarchy;
import org.dayflower.pathtracer.scene.compiler.CompiledScene;
import org.dayflower.pathtracer.scene.loader.SceneLoader;
import org.dayflower.pathtracer.scene.material.ClearCoatMaterial;
import org.dayflower.pathtracer.scene.material.GlassMaterial;
import org.dayflower.pathtracer.scene.material.LambertianMaterial;
import org.dayflower.pathtracer.scene.material.PhongMaterial;
import org.dayflower.pathtracer.scene.material.ReflectionMaterial;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Terrain;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.TriangleMesh;
import org.dayflower.pathtracer.scene.texture.BlendTexture;
import org.dayflower.pathtracer.scene.texture.BullseyeTexture;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ConstantTexture;
import org.dayflower.pathtracer.scene.texture.FractionalBrownianMotionTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SurfaceNormalTexture;
import org.dayflower.pathtracer.scene.texture.UVTexture;
import org.dayflower.pathtracer.util.FloatArrayThreadLocal;
import org.macroing.math4j.Matrix44F;

/**
 * A {@code GPURendererKernel} is an extension of the {@code AbstractRendererKernel} class that performs 3D-rendering on the GPU.
 * <p>
 * The main algorithms that are supported are the following:
 * <ul>
 * <li>Ambient Occlusion</li>
 * <li>Path Tracer</li>
 * <li>Ray Caster</li>
 * <li>Ray Marcher</li>
 * <li>Ray Tracer</li>
 * </ul>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class GPURendererKernel extends AbstractRendererKernel {
	private static final float COLOR_RECIPROCAL = 1.0F / 255.0F;
	private static final float REFRACTIVE_INDEX_AIR = 1.0F;
	private static final float REFRACTIVE_INDEX_GLASS = 1.5F;
	private static final int RELATIVE_OFFSET_INTERSECTION_DISTANCE = 0;
	private static final int RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_U = 10;
	private static final int RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_V = 13;
	private static final int RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_W = 16;
	private static final int RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET = 1;
	private static final int RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET = 23;
	private static final int RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE = 22;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT = 2;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL = 5;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING = 19;
	private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_TANGENT = 24;
	private static final int RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES = 8;
	private static final int SIZE_COLOR_RGB = 3;
	private static final int SIZE_INTERSECTION = 27;
	private static final int SIZE_MATRIX = 16;
	private static final int SIZE_RAY = 6;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final ThreadLocal<float[]> colorTemporarySamplesThreadLocal;
	private final ThreadLocal<float[]> raysThreadLocal;
	private double sunAndSkyZenithRelativeLuminance;
	private double sunAndSkyZenithX;
	private double sunAndSkyZenithY;
	private double[] sunAndSkyPerezRelativeLuminance_$constant$;
	private double[] sunAndSkyPerezX_$constant$;
	private double[] sunAndSkyPerezY_$constant$;
	private float sunAndSkyOrthoNormalBasisUX;
	private float sunAndSkyOrthoNormalBasisUY;
	private float sunAndSkyOrthoNormalBasisUZ;
	private float sunAndSkyOrthoNormalBasisVX;
	private float sunAndSkyOrthoNormalBasisVY;
	private float sunAndSkyOrthoNormalBasisVZ;
	private float sunAndSkyOrthoNormalBasisWX;
	private float sunAndSkyOrthoNormalBasisWY;
	private float sunAndSkyOrthoNormalBasisWZ;
//	private float sunAndSkySunColorB;
//	private float sunAndSkySunColorG;
//	private float sunAndSkySunColorR;
	private float sunAndSkySunDirectionWorldX;
	private float sunAndSkySunDirectionWorldY;
	private float sunAndSkySunDirectionWorldZ;
	private float sunAndSkySunDirectionX;
	private float sunAndSkySunDirectionY;
	private float sunAndSkySunDirectionZ;
	private float sunAndSkySunOriginX;
	private float sunAndSkySunOriginY;
	private float sunAndSkySunOriginZ;
	private float sunAndSkyTheta;
	private float[] colorTemporarySamples_$private$3;
	private float[] sceneCamera_$constant$;
	private float[] scenePoint2Fs_$constant$;
	private float[] scenePoint3Fs_$constant$;
	private float[] scenePrimitivesObjectToWorld_$constant$;
	private float[] scenePrimitivesWorldToObject_$constant$;
	private float[] sceneSpheres_$constant$;
	private float[] sceneSurfaces_$constant$;
	private float[] sceneTerrains_$constant$;
	private float[] sceneTextures_$constant$;
	private float[] sceneVector3Fs_$constant$;
	private float[] sunAndSkyColHistogram_$constant$;
	private float[] sunAndSkyImageHistogram_$constant$;
	private float[] intersections_$local$;
	private float[] rays_$private$6;
	private int scenePrimitivesCount;
//	private int scenePrimitivesEmittingLightCount;
	private int selectedPrimitiveIndex = -1;
	private int selectedPrimitiveOffset = -1;
	private int sunAndSkyIsSkyActive;
	private int sunAndSkyIsSunActive;
	private int[] primitiveOffsets;
	private int[] sceneBoundingVolumeHierarchies_$constant$;
	private int[] scenePlanes_$constant$;
	private int[] scenePrimitives_$constant$;
//	private int[] scenePrimitivesEmittingLight_$constant$;
	private int[] sceneTriangles_$constant$;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code GPURendererKernel} instance.
	 * <p>
	 * If {@code sceneLoader} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sceneLoader the {@link SceneLoader} to use
	 * @throws NullPointerException thrown if, and only if, {@code sceneLoader} is {@code null}
	 */
	public GPURendererKernel(final SceneLoader sceneLoader) {
		super(sceneLoader);
		
		final Scene scene = sceneLoader.loadScene();
		
		final CompiledScene compiledScene = sceneLoader.loadCompiledScene();
		
		final
		Camera camera = scene.getCamera();
		camera.setArray(compiledScene.getCamera());
		
		final Sky sky = scene.getSky();
		
		this.colorTemporarySamplesThreadLocal = new FloatArrayThreadLocal(SIZE_COLOR_RGB);
		this.raysThreadLocal = new FloatArrayThreadLocal(SIZE_RAY);
		
//		Initialize the scene variables:
		this.sceneCamera_$constant$ = compiledScene.getCamera();
		this.scenePoint2Fs_$constant$ = compiledScene.getPoint2Fs();
		this.scenePoint3Fs_$constant$ = compiledScene.getPoint3Fs();
		this.scenePrimitivesObjectToWorld_$constant$ = compiledScene.getPrimitivesObjectToWorld();
		this.scenePrimitivesWorldToObject_$constant$ = compiledScene.getPrimitivesWorldToObject();
		this.sceneSpheres_$constant$ = compiledScene.getSpheres();
		this.sceneSurfaces_$constant$ = compiledScene.getSurfaces();
		this.sceneTerrains_$constant$ = compiledScene.getTerrains();
		this.sceneTextures_$constant$ = compiledScene.getTextures();
		this.sceneVector3Fs_$constant$ = compiledScene.getVector3Fs();
		this.sceneBoundingVolumeHierarchies_$constant$ = compiledScene.getBoundingVolumeHierarchies();
		this.scenePlanes_$constant$ = compiledScene.getPlanes();
		this.scenePrimitives_$constant$ = compiledScene.getPrimitives();
		this.scenePrimitivesCount = this.scenePrimitives_$constant$.length / Primitive.SIZE;
//		this.scenePrimitivesEmittingLight_$constant$ = compiledScene.getPrimitivesEmittingLight();
//		this.scenePrimitivesEmittingLightCount = this.scenePrimitivesEmittingLight_$constant$[0];
		this.sceneTriangles_$constant$ = compiledScene.getTriangles();
		
//		Initialize the sun and sky variables:
		this.sunAndSkyColHistogram_$constant$ = sky.getColHistogram();
		this.sunAndSkyImageHistogram_$constant$ = sky.getImageHistogram();
		this.sunAndSkyIsSkyActive = BOOLEAN_TRUE;
		this.sunAndSkyIsSunActive = BOOLEAN_TRUE;
		this.sunAndSkyOrthoNormalBasisUX = sky.getOrthoNormalBasis().u.x;
		this.sunAndSkyOrthoNormalBasisUY = sky.getOrthoNormalBasis().u.y;
		this.sunAndSkyOrthoNormalBasisUZ = sky.getOrthoNormalBasis().u.z;
		this.sunAndSkyOrthoNormalBasisVX = sky.getOrthoNormalBasis().v.x;
		this.sunAndSkyOrthoNormalBasisVY = sky.getOrthoNormalBasis().v.y;
		this.sunAndSkyOrthoNormalBasisVZ = sky.getOrthoNormalBasis().v.z;
		this.sunAndSkyOrthoNormalBasisWX = sky.getOrthoNormalBasis().w.x;
		this.sunAndSkyOrthoNormalBasisWY = sky.getOrthoNormalBasis().w.y;
		this.sunAndSkyOrthoNormalBasisWZ = sky.getOrthoNormalBasis().w.z;
		this.sunAndSkyPerezRelativeLuminance_$constant$ = sky.getPerezRelativeLuminance();
		this.sunAndSkyPerezX_$constant$ = sky.getPerezX();
		this.sunAndSkyPerezY_$constant$ = sky.getPerezY();
//		this.sunAndSkySunColorB = sky.getSunColor().b;
//		this.sunAndSkySunColorG = sky.getSunColor().g;
//		this.sunAndSkySunColorR = sky.getSunColor().r;
		this.sunAndSkySunDirectionWorldX = sky.getSunDirectionWorld().x;
		this.sunAndSkySunDirectionWorldY = sky.getSunDirectionWorld().y;
		this.sunAndSkySunDirectionWorldZ = sky.getSunDirectionWorld().z;
		this.sunAndSkySunDirectionX = sky.getSunDirection().x;
		this.sunAndSkySunDirectionY = sky.getSunDirection().y;
		this.sunAndSkySunDirectionZ = sky.getSunDirection().z;
		this.sunAndSkySunOriginX = sky.getSunOrigin().x;
		this.sunAndSkySunOriginY = sky.getSunOrigin().y;
		this.sunAndSkySunOriginZ = sky.getSunOrigin().z;
		this.sunAndSkyTheta = sky.getTheta();
		this.sunAndSkyZenithRelativeLuminance = sky.getZenithRelativeLuminance();
		this.sunAndSkyZenithX = sky.getZenithX();
		this.sunAndSkyZenithY = sky.getZenithY();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the selected {@link Primitive} index or {@code -1} if no {@code Primitive} has been selected.
	 * 
	 * @return the selected {@code Primitive} index or {@code -1} if no {@code Primitive} has been selected
	 */
	@Override
	public int getSelectedPrimitiveIndex() {
		return this.selectedPrimitiveIndex;
	}
	
	/**
	 * Performs the rendering.
	 */
	@Override
	public void run() {
		doNoOpenCL();
		
		final boolean isSkyActive = this.sunAndSkyIsSkyActive == BOOLEAN_TRUE;
		final boolean isSunActive = this.sunAndSkyIsSunActive == BOOLEAN_TRUE;
		
		if(doCreatePrimaryRay()) {
			if(super.rendererType == RENDERER_TYPE_AMBIENT_OCCLUSION) {
				doRenderWithAmbientOcclusion(1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
			} else if(super.rendererType == RENDERER_TYPE_PATH_TRACER) {
				doRenderWithPathTracer(isSkyActive, isSunActive);
			} else if(super.rendererType == RENDERER_TYPE_RAY_CASTER) {
				doRenderWithRayCaster(isSkyActive);
			} else if(super.rendererType == RENDERER_TYPE_RAY_MARCHER) {
				doRenderWithRayMarcher(isSkyActive);
			} else if(super.rendererType == RENDERER_TYPE_RAY_TRACER) {
				doRenderWithRayTracer(isSkyActive);
			} else if(super.rendererType == RENDERER_TYPE_SURFACE_NORMALS) {
				doRenderSurfaceNormals(isSkyActive);
			} else {
				doRenderWithPathTracer(isSkyActive, isSunActive);
			}
			
			if(super.rendererWireframes == BOOLEAN_TRUE) {
				doRenderWireframes();
			}
		} else {
			filmSetColor(0.0F, 0.0F, 0.0F);
		}
		
		final int primitiveOffsetsOffset = getGlobalId();
		final int primitiveOffset = this.primitiveOffsets[primitiveOffsetsOffset];
		
		final float r = 0.0F;
		final float g = primitiveOffset > -1 && primitiveOffset == this.selectedPrimitiveOffset ? 1.0F : 0.0F;
		final float b = 0.0F;
		
		imageBegin();
		imageAddColor(r, g, b);
		
		final int toneMapperType = super.toneMapperType;
		
		final float toneMapperExposure = super.toneMapperExposure;
		
		if(toneMapperType == TONE_MAPPER_TYPE_REINHARD) {
			imageSetReinhard(toneMapperExposure);
		} else if(toneMapperType == TONE_MAPPER_TYPE_REINHARD_MODIFIED_1) {
			imageSetReinhardModified1(toneMapperExposure);
		} else if(toneMapperType == TONE_MAPPER_TYPE_REINHARD_MODIFIED_2) {
			imageSetReinhardModified2(toneMapperExposure);
		} else if(toneMapperType == TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED) {
			imageSetFilmicCurveACESModified(toneMapperExposure);
		}
		
		imageRedoGammaCorrection();
		imageEnd();
	}
	
	/**
	 * Toggles the material for the selected primitive.
	 */
	@Override
	public void togglePrimitiveMaterial() {
		final int selectedPrimitiveOffset = this.selectedPrimitiveOffset;
		
		if(selectedPrimitiveOffset != -1) {
			final int surfacesOffset = this.scenePrimitives_$constant$[selectedPrimitiveOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
			
			final int oldMaterialType = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL]);
			final int oldMaterialOrdinal = oldMaterialType - 1;
			
			final int[] materials = new int[] {
				ClearCoatMaterial.TYPE,
				GlassMaterial.TYPE,
				LambertianMaterial.TYPE,
				PhongMaterial.TYPE,
				ReflectionMaterial.TYPE
			};
			
			if(oldMaterialOrdinal >= 0 && oldMaterialOrdinal < materials.length) {
				final int newMaterialOrdinal = (oldMaterialOrdinal + 1) % materials.length;
				final int newMaterialType = materials[newMaterialOrdinal];
				
				this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL] = newMaterialType;
				
				put(this.sceneSurfaces_$constant$);
				
				setChanged(true);
			}
		}
	}
	
	/**
	 * Toggles the primitive selection.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 */
	@Override
	public void togglePrimitiveSelection(final int x, final int y) {
		final int index = y * getResolutionX() + x;
		
		get(this.primitiveOffsets);
		
		final int[] primitiveOffsets = this.primitiveOffsets;
		
		if(index >= 0 && index < primitiveOffsets.length) {
			final int primitiveOffset = primitiveOffsets[index];
			
			if(primitiveOffset == this.selectedPrimitiveOffset) {
				this.selectedPrimitiveIndex = -1;
				this.selectedPrimitiveOffset = -1;
			} else {
				this.selectedPrimitiveIndex = primitiveOffset / Primitive.SIZE;
				this.selectedPrimitiveOffset = primitiveOffset;
			}
		}
	}
	
	/**
	 * Toggles the sky.
	 */
	@Override
	public void toggleSky() {
		if(this.sunAndSkyIsSkyActive == BOOLEAN_FALSE) {
			this.sunAndSkyIsSkyActive = BOOLEAN_TRUE;
		} else {
			this.sunAndSkyIsSkyActive = BOOLEAN_FALSE;
		}
		
		setChanged(true);
	}
	
	/**
	 * Toggles the sky.
	 */
	@Override
	public void toggleSun() {
		if(this.sunAndSkyIsSunActive == BOOLEAN_FALSE) {
			this.sunAndSkyIsSunActive = BOOLEAN_TRUE;
		} else {
			this.sunAndSkyIsSunActive = BOOLEAN_FALSE;
		}
		
		setChanged(true);
	}
	
	/**
	 * Updates all necessary variables in this {@code GPURendererKernel} instance.
	 * <p>
	 * If {@code imageDataByte} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis
	 * @param resolutionY the resolution along the Y-axis
	 * @param imageDataByte a {@code byte} array with image data
	 * @param localSize the local size
	 * @throws NullPointerException thrown if, and only if, {@code imageDataByte} is {@code null}
	 */
	@Override
	public void update(final int resolutionX, final int resolutionY, final byte[] imageDataByte, final int localSize) {
		update(resolutionX, resolutionY, imageDataByte);
		
//		Initialize the color variables:
		this.colorTemporarySamples_$private$3 = new float[SIZE_COLOR_RGB];
		
		this.intersections_$local$ = new float[localSize * SIZE_INTERSECTION];
		this.primitiveOffsets = new int[resolutionX * resolutionY];
		this.rays_$private$6 = new float[SIZE_RAY];
		
		Arrays.fill(this.primitiveOffsets, -1);
		
		setExplicit(true);
//		setExecutionMode(EXECUTION_MODE.JTP);
		
		put(this.sceneBoundingVolumeHierarchies_$constant$);
		put(this.sceneCamera_$constant$);
		put(this.scenePlanes_$constant$);
		put(this.scenePoint2Fs_$constant$);
		put(this.scenePoint3Fs_$constant$);
		put(this.scenePrimitives_$constant$);
//		put(this.scenePrimitivesEmittingLight_$constant$);
		put(this.scenePrimitivesObjectToWorld_$constant$);
		put(this.scenePrimitivesWorldToObject_$constant$);
		put(this.sceneSpheres_$constant$);
		put(this.sceneSurfaces_$constant$);
		put(this.sceneTerrains_$constant$);
		put(this.sceneTextures_$constant$);
		put(this.sceneTriangles_$constant$);
		put(this.sceneVector3Fs_$constant$);
		
		put(this.sunAndSkyColHistogram_$constant$);
		put(this.sunAndSkyImageHistogram_$constant$);
		put(this.sunAndSkyPerezRelativeLuminance_$constant$);
		put(this.sunAndSkyPerezX_$constant$);
		put(this.sunAndSkyPerezY_$constant$);
		
		put(this.primitiveOffsets);
	}
	
	/**
	 * Updates the {@link Camera} and the variables related to it.
	 */
	@Override
	public void updateCamera() {
		final
		Camera camera = getCamera();
		camera.update();
		
		put(this.sceneCamera_$constant$);
	}
	
	/**
	 * Updates the {@link Primitive}s.
	 */
	@Override
	public void updatePrimitives() {
		final Scene scene = getScene();
		
		if(scene.isPrimitiveUpdateRequired()) {
			final List<Primitive> primitives = scene.getPrimitives();
			
			for(int i = 0; i < primitives.size(); i++) {
				final
				Primitive primitive = primitives.get(i);
				primitive.update();
				
				final Matrix44F objectToWorld = primitive.getTransform().getObjectToWorld();
				final Matrix44F worldToObject = primitive.getTransform().getWorldToObject();
				
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  0] = objectToWorld.element11;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  1] = objectToWorld.element12;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  2] = objectToWorld.element13;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  3] = objectToWorld.element14;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  4] = objectToWorld.element21;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  5] = objectToWorld.element22;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  6] = objectToWorld.element23;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  7] = objectToWorld.element24;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  8] = objectToWorld.element31;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX +  9] = objectToWorld.element32;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX + 10] = objectToWorld.element33;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX + 11] = objectToWorld.element34;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX + 12] = objectToWorld.element41;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX + 13] = objectToWorld.element42;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX + 14] = objectToWorld.element43;
				this.scenePrimitivesObjectToWorld_$constant$[i * SIZE_MATRIX + 15] = objectToWorld.element44;
				
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  0] = worldToObject.element11;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  1] = worldToObject.element12;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  2] = worldToObject.element13;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  3] = worldToObject.element14;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  4] = worldToObject.element21;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  5] = worldToObject.element22;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  6] = worldToObject.element23;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  7] = worldToObject.element24;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  8] = worldToObject.element31;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX +  9] = worldToObject.element32;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX + 10] = worldToObject.element33;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX + 11] = worldToObject.element34;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX + 12] = worldToObject.element41;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX + 13] = worldToObject.element42;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX + 14] = worldToObject.element43;
				this.scenePrimitivesWorldToObject_$constant$[i * SIZE_MATRIX + 15] = worldToObject.element44;
			}
			
			put(this.scenePrimitivesObjectToWorld_$constant$);
			put(this.scenePrimitivesWorldToObject_$constant$);
			
			setChanged(true);
		}
	}
	
	/**
	 * Updates the variables related to the sun and sky.
	 */
	@Override
	public void updateSunAndSky() {
		final Sky sky = getSky();
		
		this.sunAndSkyOrthoNormalBasisUX = sky.getOrthoNormalBasis().u.x;
		this.sunAndSkyOrthoNormalBasisUY = sky.getOrthoNormalBasis().u.y;
		this.sunAndSkyOrthoNormalBasisUZ = sky.getOrthoNormalBasis().u.z;
		this.sunAndSkyOrthoNormalBasisVX = sky.getOrthoNormalBasis().v.x;
		this.sunAndSkyOrthoNormalBasisVY = sky.getOrthoNormalBasis().v.y;
		this.sunAndSkyOrthoNormalBasisVZ = sky.getOrthoNormalBasis().v.z;
		this.sunAndSkyOrthoNormalBasisWX = sky.getOrthoNormalBasis().w.x;
		this.sunAndSkyOrthoNormalBasisWY = sky.getOrthoNormalBasis().w.y;
		this.sunAndSkyOrthoNormalBasisWZ = sky.getOrthoNormalBasis().w.z;
//		this.sunAndSkySunColorB = sky.getSunColor().b;
//		this.sunAndSkySunColorG = sky.getSunColor().g;
//		this.sunAndSkySunColorR = sky.getSunColor().r;
		this.sunAndSkySunDirectionWorldX = sky.getSunDirectionWorld().x;
		this.sunAndSkySunDirectionWorldY = sky.getSunDirectionWorld().y;
		this.sunAndSkySunDirectionWorldZ = sky.getSunDirectionWorld().z;
		this.sunAndSkySunDirectionX = sky.getSunDirection().x;
		this.sunAndSkySunDirectionY = sky.getSunDirection().y;
		this.sunAndSkySunDirectionZ = sky.getSunDirection().z;
		this.sunAndSkySunOriginX = sky.getSunOrigin().x;
		this.sunAndSkySunOriginY = sky.getSunOrigin().y;
		this.sunAndSkySunOriginZ = sky.getSunOrigin().z;
		this.sunAndSkyTheta = sky.getTheta();
		this.sunAndSkyZenithRelativeLuminance = sky.getZenithRelativeLuminance();
		this.sunAndSkyZenithX = sky.getZenithX();
		this.sunAndSkyZenithY = sky.getZenithY();
		
		System.arraycopy(sky.getColHistogram(), 0, this.sunAndSkyColHistogram_$constant$, 0, this.sunAndSkyColHistogram_$constant$.length);
		System.arraycopy(sky.getImageHistogram(), 0, this.sunAndSkyImageHistogram_$constant$, 0, this.sunAndSkyImageHistogram_$constant$.length);
		System.arraycopy(sky.getPerezRelativeLuminance(), 0, this.sunAndSkyPerezRelativeLuminance_$constant$, 0, this.sunAndSkyPerezRelativeLuminance_$constant$.length);
		System.arraycopy(sky.getPerezX(), 0, this.sunAndSkyPerezX_$constant$, 0, this.sunAndSkyPerezX_$constant$.length);
		System.arraycopy(sky.getPerezY(), 0, this.sunAndSkyPerezY_$constant$, 0, this.sunAndSkyPerezY_$constant$.length);
		
		put(this.sunAndSkyColHistogram_$constant$);
		put(this.sunAndSkyImageHistogram_$constant$);
		put(this.sunAndSkyPerezRelativeLuminance_$constant$);
		put(this.sunAndSkyPerezX_$constant$);
		put(this.sunAndSkyPerezY_$constant$);
		
		setChanged(true);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean doCreatePrimaryRay() {
//		Retrieve the global ID:
		final int globalId = getGlobalId();
		
//		Calculate the X- and Y-coordinates on the screen:
		final int y = globalId / super.resolutionX;
		final int x = globalId - y * super.resolutionX;
		
//		Retrieve the current X-, Y- and Z-coordinates of the camera lens (eye) in the scene:
		final float eyeX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_EYE_X];
		final float eyeY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_EYE_Y];
		final float eyeZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_EYE_Z];
		
//		Retrieve the current U-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float uX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X];
		final float uY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y];
		final float uZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z];
		
//		Retrieve the current V-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float vX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X];
		final float vY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y];
		final float vZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z];
		
//		Retrieve the current W-vector for the orthonormal basis frame of the camera lens (eye) in the scene:
		final float wX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X];
		final float wY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y];
		final float wZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z];
		
//		Calculate the Field of View:
		final float fieldOfViewX0 = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_FIELD_OF_VIEW_X];
		final float fieldOfViewX1 = tan(fieldOfViewX0 * 0.5F * PI_DIVIDED_BY_180);
		final float fieldOfViewY0 = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_FIELD_OF_VIEW_Y];
		final float fieldOfViewY1 = tan(-fieldOfViewY0 * 0.5F * PI_DIVIDED_BY_180);
		
//		Calculate the horizontal direction:
		final float horizontalX = uX * fieldOfViewX1;
		final float horizontalY = uY * fieldOfViewX1;
		final float horizontalZ = uZ * fieldOfViewX1;
		
//		Calculate the vertical direction:
		final float verticalX = vX * fieldOfViewY1;
		final float verticalY = vY * fieldOfViewY1;
		final float verticalZ = vZ * fieldOfViewY1;
		
//		Calculate the pixel sample:
		float sampleX = 0.5F;
		float sampleY = 0.5F;
		
		if(super.rendererType == RENDERER_TYPE_PATH_TRACER) {
			sampleX = nextFloat();
			sampleY = nextFloat();
			
//			Box Filter:
//			sampleX -= 0.5F;
//			sampleY -= 0.5F;
			
//			Triangle Filter (Tent Filter):
			sampleX = sampleX < 0.5F ? sqrt(2.0F * sampleX) - 1.0F : 1.0F - sqrt(2.0F - 2.0F * sampleX);
			sampleY = sampleY < 0.5F ? sqrt(2.0F * sampleY) - 1.0F : 1.0F - sqrt(2.0F - 2.0F * sampleY);
		}
		
//		Calculate the pixel sample point:
		final float sx = (sampleX + x) / (this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_RESOLUTION_X] - 1.0F);
		final float sy = (sampleY + y) / (this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_RESOLUTION_Y] - 1.0F);
		final float sx0 = 2.0F * sx - 1.0F;
		final float sy0 = 2.0F * sy - 1.0F;
		
//		Initialize w to 1.0F:
		float w = 1.0F;
		
//		Retrieve whether or not this camera uses a Fisheye camera lens:
		final boolean isFisheyeCameraLens = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_CAMERA_LENS] == Camera.CAMERA_LENS_FISHEYE;
		
		if(isFisheyeCameraLens) {
//			Calculate the dot product that will be used in determining if the current pixel is outside the camera lens:
			final float dotProduct = sx0 * sx0 + sy0 * sy0;
			
			if(dotProduct > 1.0F) {
				return false;
			}
			
//			Update the w variable:
			w = sqrt(1.0F - dotProduct);
		}
		
//		Calculate the middle point:
		final float middleX = eyeX + wX * w;
		final float middleY = eyeY + wY * w;
		final float middleZ = eyeZ + wZ * w;
		
//		Calculate the point on the plane one unit away from the eye:
		final float pointOnPlaneOneUnitAwayFromEyeX = middleX + (horizontalX * sx0) + (verticalX * sy0);
		final float pointOnPlaneOneUnitAwayFromEyeY = middleY + (horizontalY * sx0) + (verticalY * sy0);
		final float pointOnPlaneOneUnitAwayFromEyeZ = middleZ + (horizontalZ * sx0) + (verticalZ * sy0);
		
//		Retrieve the focal distance:
		final float focalDistance = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_FOCAL_DISTANCE];
		
//		Calculate the point on the image plane:
		final float pointOnImagePlaneX = eyeX + (pointOnPlaneOneUnitAwayFromEyeX - eyeX) * focalDistance;
		final float pointOnImagePlaneY = eyeY + (pointOnPlaneOneUnitAwayFromEyeY - eyeY) * focalDistance;
		final float pointOnImagePlaneZ = eyeZ + (pointOnPlaneOneUnitAwayFromEyeZ - eyeZ) * focalDistance;
		
//		Retrieve the aperture radius:
		final float apertureRadius = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_APERTURE_RADIUS];
		
//		Initialize the X-, Y- and Z-coordinates of the aperture point:
		float aperturePointX = eyeX;
		float aperturePointY = eyeY;
		float aperturePointZ = eyeZ;
		
//		Check if Depth of Field (DoF) is enabled:
		if(apertureRadius > 0.00001F) {
//			Calculate two random values:
			final float random1 = nextFloat();
			final float random2 = nextFloat();
			
//			Calculate the angle:
			final float angle = PI_MULTIPLIED_BY_TWO * random1;
			
//			Calculate the distance:
			final float distance = apertureRadius * sqrt(random2);
			
//			Calculate the aperture:
			final float apertureX = cos(angle) * distance;
			final float apertureY = sin(angle) * distance;
			
//			Update the aperture point:
			aperturePointX = eyeX + uX * apertureX + vX * apertureY;
			aperturePointY = eyeY + uY * apertureX + vY * apertureY;
			aperturePointZ = eyeZ + uZ * apertureX + vZ * apertureY;
		}
		
//		Calculate the aperture to image plane:
		final float apertureToImagePlane0X = pointOnImagePlaneX - aperturePointX;
		final float apertureToImagePlane0Y = pointOnImagePlaneY - aperturePointY;
		final float apertureToImagePlane0Z = pointOnImagePlaneZ - aperturePointZ;
		final float apertureToImagePlane0LengthReciprocal = rsqrt(apertureToImagePlane0X * apertureToImagePlane0X + apertureToImagePlane0Y * apertureToImagePlane0Y + apertureToImagePlane0Z * apertureToImagePlane0Z);
		final float apertureToImagePlane1X = apertureToImagePlane0X * apertureToImagePlane0LengthReciprocal;
		final float apertureToImagePlane1Y = apertureToImagePlane0Y * apertureToImagePlane0LengthReciprocal;
		final float apertureToImagePlane1Z = apertureToImagePlane0Z * apertureToImagePlane0LengthReciprocal;
		
//		Update the rays_$private$6 array with information:
		this.rays_$private$6[0] = aperturePointX;
		this.rays_$private$6[1] = aperturePointY;
		this.rays_$private$6[2] = aperturePointZ;
		this.rays_$private$6[3] = apertureToImagePlane1X;
		this.rays_$private$6[4] = apertureToImagePlane1Y;
		this.rays_$private$6[5] = apertureToImagePlane1Z;
		
		return true;
	}
	
	private float doGetY(final float x, final float z) {
		return simplexFractalXY(getGlobalAmplitude(), getGlobalFrequency(), getGlobalGain(), getGlobalLacunarity(), getGlobalOctaves(), x, z);
	}
	
	@SuppressWarnings("unused")
	private float doIntersectPrimitivesOld(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final boolean isTesting) {
//		Compute the offset for the array containing intersection data:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the distance to the closest primitive to INFINITY:
		float closestDistance = INFINITY;
		
//		Initialize the offset to the closest primitive, the shape type of the closest primitive and the shape offset of the closest primitive to -1:
		int closestPrimitiveOffset = -1;
		int closestShapeType = -1;
		int closestShapeOffset = -1;
		
//		Calculate the reciprocal of the ray direction vector:
		final float directionReciprocalX = 1.0F / directionX;
		final float directionReciprocalY = 1.0F / directionY;
		final float directionReciprocalZ = 1.0F / directionZ;
		
		final int scenePrimitivesCount = this.scenePrimitivesCount;
		
		for(int i = 0; i < scenePrimitivesCount; i++) {
			float currentDistance = INFINITY;
			
			final int currentPrimitiveOffset = i * Primitive.SIZE;
			
			int currentShapeType = this.scenePrimitives_$constant$[currentPrimitiveOffset + Primitive.RELATIVE_OFFSET_SHAPE_TYPE];
			int currentShapeOffset = this.scenePrimitives_$constant$[currentPrimitiveOffset + Primitive.RELATIVE_OFFSET_SHAPE_OFFSET];
			
			if(currentShapeType == TriangleMesh.TYPE) {
//				Initialize the offset to the root of the BVH structure:
				int boundingVolumeHierarchyAbsoluteOffset = currentShapeOffset;
				int boundingVolumeHierarchyRelativeOffset = 0;
				
//				Loop through the BVH structure as long as the offset to the next node is not -1:
				while(boundingVolumeHierarchyRelativeOffset != -1) {
//					Calculate the current offset in the BVH structure:
					final int boundingVolumeHierarchyOffset = boundingVolumeHierarchyAbsoluteOffset + boundingVolumeHierarchyRelativeOffset;
					
//					Retrieve the offsets to the points defining the minimum and maximum locations of the current bounding box:
					final int minimumOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 2];
					final int maximumOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 3];
					
//					Retrieve the minimum point location of the current bounding box:
					final float minimumX = this.scenePoint3Fs_$constant$[minimumOffset + 0];
					final float minimumY = this.scenePoint3Fs_$constant$[minimumOffset + 1];
					final float minimumZ = this.scenePoint3Fs_$constant$[minimumOffset + 2];
					
//					Retrieve the maximum point location of the current bounding box:
					final float maximumX = this.scenePoint3Fs_$constant$[maximumOffset + 0];
					final float maximumY = this.scenePoint3Fs_$constant$[maximumOffset + 1];
					final float maximumZ = this.scenePoint3Fs_$constant$[maximumOffset + 2];
					
//					Calculate the distance to the minimum point location of the bounding box:
					final float t0X = (minimumX - originX) * directionReciprocalX;
					final float t0Y = (minimumY - originY) * directionReciprocalY;
					final float t0Z = (minimumZ - originZ) * directionReciprocalZ;
					
//					Calculate the distance to the maximum point location of the bounding box:
					final float t1X = (maximumX - originX) * directionReciprocalX;
					final float t1Y = (maximumY - originY) * directionReciprocalY;
					final float t1Z = (maximumZ - originZ) * directionReciprocalZ;
					
//					Calculate the minimum and maximum X-components:
					final float tMaximumX = max(t0X, t1X);
					final float tMinimumX = min(t0X, t1X);
					
//					Calculate the minimum and maximum Y-components:
					final float tMaximumY = max(t0Y, t1Y);
					final float tMinimumY = min(t0Y, t1Y);
					
//					Calculate the minimum and maximum Z-components:
					final float tMaximumZ = max(t0Z, t1Z);
					final float tMinimumZ = min(t0Z, t1Z);
					
//					Calculate the minimum and maximum distance values of the X-, Y- and Z-components above:
					final float tMaximum = min(tMaximumX, min(tMaximumY, tMaximumZ));
					final float tMinimum = max(tMinimumX, max(tMinimumY, tMinimumZ));
					
//					Check if the maximum distance is greater than or equal to the minimum distance:
					if(tMaximum < 0.0F || tMinimum > tMaximum || closestDistance < tMinimum) {
//						Retrieve the offset to the next node in the BVH structure, relative to the current one:
						boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 1];
					} else {
//						Retrieve the type of the current BVH node:
						final int type = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset];
						
						if(type == BoundingVolumeHierarchy.NODE_TYPE_TREE) {
//							This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:
							boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 4];
						} else {
//							Retrieve the triangle count in the current BVH node:
							final int triangleCount = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 4];
							
							int j = 0;
							
//							Loop through all triangles in the current BVH node:
							while(j < triangleCount) {
//								Retrieve the offset to the current triangle:
								final int currentTriangleOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 5 + j];
								
								final int offsetAPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
								final int offsetBPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
								final int offsetCPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
								
								final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
								final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
								final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
								final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
								final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
								final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
								final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
								final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
								final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
								
//								Perform an intersection test with the current triangle:
								currentDistance = doIntersectTriangle(originX, originY, originZ, directionX, directionY, directionZ, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, 0.001F, closestDistance);
								
//								Check if the current distance is less than the distance to the closest primitive so far:
								if(currentDistance < closestDistance) {
									closestDistance = currentDistance;
									closestPrimitiveOffset = currentPrimitiveOffset;
									closestShapeType = Triangle.TYPE;
									closestShapeOffset = currentTriangleOffset;
								}
								
								if(isTesting && closestPrimitiveOffset != -1) {
									return closestDistance;
								}
								
								j++;
							}
							
//							Retrieve the offset to the next node in the BVH structure, relative to the current one:
							boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 1];
						}
					}
				}
			} else if(currentShapeType == Plane.TYPE) {
				final int offsetA = this.scenePlanes_$constant$[currentShapeOffset + Plane.RELATIVE_OFFSET_A_OFFSET];
				final int offsetSurfaceNormal = this.scenePlanes_$constant$[currentShapeOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET];
				
				final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
				final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
				final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
				
				final float surfaceNormalX = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 0];
				final float surfaceNormalY = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 1];
				final float surfaceNormalZ = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 2];
				
				currentDistance = doIntersectPlane(originX, originY, originZ, directionX, directionY, directionZ, aX, aY, aZ, surfaceNormalX, surfaceNormalY, surfaceNormalZ, 0.001F, closestDistance);
			} else if(currentShapeType == Sphere.TYPE) {
				final int offsetPosition = (int)(this.sceneSpheres_$constant$[currentShapeOffset + Sphere.RELATIVE_OFFSET_POSITION_OFFSET]);
				
				final float positionX = this.scenePoint3Fs_$constant$[offsetPosition + 0];
				final float positionY = this.scenePoint3Fs_$constant$[offsetPosition + 1];
				final float positionZ = this.scenePoint3Fs_$constant$[offsetPosition + 2];
				
				final float radius = this.sceneSpheres_$constant$[currentShapeOffset + Sphere.RELATIVE_OFFSET_RADIUS];
				
				currentDistance = doIntersectSphere(originX, originY, originZ, directionX, directionY, directionZ, positionX, positionY, positionZ, radius, 0.001F, closestDistance);
			} else if(currentShapeType == Terrain.TYPE) {
				final float frequency = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_FREQUENCY];
				final float gain = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_GAIN];
				final float minimum = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_MINIMUM];
				final float maximum = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_MAXIMUM];
				
				final int octaves = (int)(this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_OCTAVES]);
				
				currentDistance = doIntersectTerrain(originX, originY, originZ, directionX, directionY, directionZ, frequency, gain, minimum, maximum, octaves);
			} else if(currentShapeType == Triangle.TYPE) {
				final int offsetAPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
				final int offsetBPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
				final int offsetCPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
				
				final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
				final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
				final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
				final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
				final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
				final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
				final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
				final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
				final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
				
				currentDistance = doIntersectTriangle(originX, originY, originZ, directionX, directionY, directionZ, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, 0.001F, closestDistance);
			}
			
			if(currentDistance < closestDistance) {
				closestDistance = currentDistance;
				closestPrimitiveOffset = currentPrimitiveOffset;
				closestShapeType = currentShapeType;
				closestShapeOffset = currentShapeOffset;
			}
			
			if(isTesting && closestPrimitiveOffset != -1) {
				return closestDistance;
			}
		}
		
		if(!isTesting) {
			if(closestPrimitiveOffset != -1) {
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = closestDistance;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET] = closestPrimitiveOffset;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE] = closestShapeType;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET] = closestShapeOffset;
				
				if(closestShapeType == Plane.TYPE) {
					final int offsetA = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_A_OFFSET];
					final int offsetB = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_B_OFFSET];
					final int offsetC = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_C_OFFSET];
					final int offsetSurfaceNormal = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET];
					
					final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
					final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
					final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
					final float bX = this.scenePoint3Fs_$constant$[offsetB + 0];
					final float bY = this.scenePoint3Fs_$constant$[offsetB + 1];
					final float bZ = this.scenePoint3Fs_$constant$[offsetB + 2];
					final float cX = this.scenePoint3Fs_$constant$[offsetC + 0];
					final float cY = this.scenePoint3Fs_$constant$[offsetC + 1];
					final float cZ = this.scenePoint3Fs_$constant$[offsetC + 2];
					
					final float surfaceNormalX = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 0];
					final float surfaceNormalY = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 1];
					final float surfaceNormalZ = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 2];
					
					doCalculateSurfacePropertiesForPlane(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, aX, aY, aZ, bX, bY, bZ, cX, cY, cZ, surfaceNormalX, surfaceNormalY, surfaceNormalZ);
				} else if(closestShapeType == Sphere.TYPE) {
					final int offsetPosition = (int)(this.sceneSpheres_$constant$[closestShapeOffset + Sphere.RELATIVE_OFFSET_POSITION_OFFSET]);
					
					final float positionX = this.scenePoint3Fs_$constant$[offsetPosition + 0];
					final float positionY = this.scenePoint3Fs_$constant$[offsetPosition + 1];
					final float positionZ = this.scenePoint3Fs_$constant$[offsetPosition + 2];
					
					doCalculateSurfacePropertiesForSphere(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, positionX, positionY, positionZ);
				} else if(closestShapeType == Terrain.TYPE) {
					final float frequency = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_FREQUENCY];
					final float gain = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_GAIN];
					final float minimum = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_MINIMUM];
					final float maximum = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_MAXIMUM];
					
					final int octaves = (int)(this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_OCTAVES]);
					
					doCalculateSurfacePropertiesForTerrain(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, frequency, gain, minimum, maximum, octaves);
				} else if(closestShapeType == Triangle.TYPE) {
					final int offsetAPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
					final int offsetBPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
					final int offsetCPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
					
					final int offsetASurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_SURFACE_NORMAL_OFFSET];
					final int offsetBSurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_SURFACE_NORMAL_OFFSET];
					final int offsetCSurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_SURFACE_NORMAL_OFFSET];
					
					final int offsetASurfaceTangent = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_SURFACE_TANGENT_OFFSET];
					final int offsetBSurfaceTangent = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_SURFACE_TANGENT_OFFSET];
					final int offsetCSurfaceTangent = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_SURFACE_TANGENT_OFFSET];
					
					final int offsetATextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_TEXTURE_COORDINATES_OFFSET];
					final int offsetBTextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_TEXTURE_COORDINATES_OFFSET];
					final int offsetCTextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_TEXTURE_COORDINATES_OFFSET];
					
					final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
					final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
					final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
					final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
					final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
					final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
					final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
					final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
					final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
					
					final float aSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 0];
					final float aSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 1];
					final float aSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 2];
					final float bSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 0];
					final float bSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 1];
					final float bSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 2];
					final float cSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 0];
					final float cSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 1];
					final float cSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 2];
					
					final float aSurfaceTangentX = this.sceneVector3Fs_$constant$[offsetASurfaceTangent + 0];
					final float aSurfaceTangentY = this.sceneVector3Fs_$constant$[offsetASurfaceTangent + 1];
					final float aSurfaceTangentZ = this.sceneVector3Fs_$constant$[offsetASurfaceTangent + 2];
					final float bSurfaceTangentX = this.sceneVector3Fs_$constant$[offsetBSurfaceTangent + 0];
					final float bSurfaceTangentY = this.sceneVector3Fs_$constant$[offsetBSurfaceTangent + 1];
					final float bSurfaceTangentZ = this.sceneVector3Fs_$constant$[offsetBSurfaceTangent + 2];
					final float cSurfaceTangentX = this.sceneVector3Fs_$constant$[offsetCSurfaceTangent + 0];
					final float cSurfaceTangentY = this.sceneVector3Fs_$constant$[offsetCSurfaceTangent + 1];
					final float cSurfaceTangentZ = this.sceneVector3Fs_$constant$[offsetCSurfaceTangent + 2];
					
					final float aTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetATextureCoordinates + 0];
					final float aTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetATextureCoordinates + 1];
					final float bTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetBTextureCoordinates + 0];
					final float bTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetBTextureCoordinates + 1];
					final float cTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetCTextureCoordinates + 0];
					final float cTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetCTextureCoordinates + 1];
					
					doCalculateSurfacePropertiesForTriangle(originX, originY, originZ, directionX, directionY, directionZ, closestDistance, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, aSurfaceNormalX, aSurfaceNormalY, aSurfaceNormalZ, bSurfaceNormalX, bSurfaceNormalY, bSurfaceNormalZ, cSurfaceNormalX, cSurfaceNormalY, cSurfaceNormalZ, aSurfaceTangentX, aSurfaceTangentY, aSurfaceTangentZ, bSurfaceTangentX, bSurfaceTangentY, bSurfaceTangentZ, cSurfaceTangentX, cSurfaceTangentY, cSurfaceTangentZ, aTextureCoordinatesU, aTextureCoordinatesV, bTextureCoordinatesU, bTextureCoordinatesV, cTextureCoordinatesU, cTextureCoordinatesV);
				}
				
//				Perform Normal Mapping via Image Texture:
				doPerformNormalMappingViaImageTexture(closestPrimitiveOffset);
				
//				Perform Normal Mapping via Noise:
				doPerformNormalMappingViaNoise(closestPrimitiveOffset);
			} else {
//				Reset the information in the intersections array:
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET] = -1;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE] = -1;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET] = -1;
			}
		}
		
		return closestDistance;
	}
	
	private float doIntersectPrimitives(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final boolean isTesting) {
//		Compute the offset for the array containing intersection data:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the distance to the closest primitive to INFINITY:
		float closestDistance = INFINITY;
		
//		Initialize the offset to the closest primitive, the shape type of the closest primitive and the shape offset of the closest primitive to -1:
		int closestMatrixOffset = -1;
		int closestPrimitiveOffset = -1;
		int closestShapeType = -1;
		int closestShapeOffset = -1;
		
		final int scenePrimitivesCount = this.scenePrimitivesCount;
		
		for(int i = 0; i < scenePrimitivesCount; i++) {
			float currentDistance = INFINITY;
			
			final int currentPrimitiveOffset = i * Primitive.SIZE;
			final int currentMatrixOffset = i * SIZE_MATRIX;
			
			int currentShapeType = this.scenePrimitives_$constant$[currentPrimitiveOffset + Primitive.RELATIVE_OFFSET_SHAPE_TYPE];
			int currentShapeOffset = this.scenePrimitives_$constant$[currentPrimitiveOffset + Primitive.RELATIVE_OFFSET_SHAPE_OFFSET];
			
			final float worldToObjectElement11 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  0];
			final float worldToObjectElement12 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  1];
			final float worldToObjectElement13 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  2];
			final float worldToObjectElement14 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  3];
			final float worldToObjectElement21 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  4];
			final float worldToObjectElement22 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  5];
			final float worldToObjectElement23 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  6];
			final float worldToObjectElement24 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  7];
			final float worldToObjectElement31 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  8];
			final float worldToObjectElement32 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset +  9];
			final float worldToObjectElement33 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset + 10];
			final float worldToObjectElement34 = this.scenePrimitivesWorldToObject_$constant$[currentMatrixOffset + 11];
			
			final float originXObjectSpace = worldToObjectElement11 * originX + worldToObjectElement12 * originY + worldToObjectElement13 * originZ + worldToObjectElement14;
			final float originYObjectSpace = worldToObjectElement21 * originX + worldToObjectElement22 * originY + worldToObjectElement23 * originZ + worldToObjectElement24;
			final float originZObjectSpace = worldToObjectElement31 * originX + worldToObjectElement32 * originY + worldToObjectElement33 * originZ + worldToObjectElement34;
			
			final float directionXObjectSpace = worldToObjectElement11 * directionX + worldToObjectElement12 * directionY + worldToObjectElement13 * directionZ;
			final float directionYObjectSpace = worldToObjectElement21 * directionX + worldToObjectElement22 * directionY + worldToObjectElement23 * directionZ;
			final float directionZObjectSpace = worldToObjectElement31 * directionX + worldToObjectElement32 * directionY + worldToObjectElement33 * directionZ;
			
			final float directionXObjectSpaceReciprocal = 1.0F / directionXObjectSpace;
			final float directionYObjectSpaceReciprocal = 1.0F / directionYObjectSpace;
			final float directionZObjectSpaceReciprocal = 1.0F / directionZObjectSpace;
			
			if(currentShapeType == TriangleMesh.TYPE) {
//				Initialize the offset to the root of the BVH structure:
				int boundingVolumeHierarchyAbsoluteOffset = currentShapeOffset;
				int boundingVolumeHierarchyRelativeOffset = 0;
				
//				Loop through the BVH structure as long as the offset to the next node is not -1:
				while(boundingVolumeHierarchyRelativeOffset != -1) {
//					Calculate the current offset in the BVH structure:
					final int boundingVolumeHierarchyOffset = boundingVolumeHierarchyAbsoluteOffset + boundingVolumeHierarchyRelativeOffset;
					
//					Retrieve the offsets to the points defining the minimum and maximum locations of the current bounding box:
					final int minimumOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 2];
					final int maximumOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 3];
					
//					Retrieve the minimum point location of the current bounding box:
					final float minimumX = this.scenePoint3Fs_$constant$[minimumOffset + 0];
					final float minimumY = this.scenePoint3Fs_$constant$[minimumOffset + 1];
					final float minimumZ = this.scenePoint3Fs_$constant$[minimumOffset + 2];
					
//					Retrieve the maximum point location of the current bounding box:
					final float maximumX = this.scenePoint3Fs_$constant$[maximumOffset + 0];
					final float maximumY = this.scenePoint3Fs_$constant$[maximumOffset + 1];
					final float maximumZ = this.scenePoint3Fs_$constant$[maximumOffset + 2];
					
//					Calculate the distance to the minimum point location of the bounding box:
					final float t0X = (minimumX - originXObjectSpace) * directionXObjectSpaceReciprocal;
					final float t0Y = (minimumY - originYObjectSpace) * directionYObjectSpaceReciprocal;
					final float t0Z = (minimumZ - originZObjectSpace) * directionZObjectSpaceReciprocal;
					
//					Calculate the distance to the maximum point location of the bounding box:
					final float t1X = (maximumX - originXObjectSpace) * directionXObjectSpaceReciprocal;
					final float t1Y = (maximumY - originYObjectSpace) * directionYObjectSpaceReciprocal;
					final float t1Z = (maximumZ - originZObjectSpace) * directionZObjectSpaceReciprocal;
					
//					Calculate the minimum and maximum distance values of the X-, Y- and Z-components above:
					final float tMaximum = min(max(t0X, t1X), min(max(t0Y, t1Y), max(t0Z, t1Z)));
					final float tMinimum = max(min(t0X, t1X), max(min(t0Y, t1Y), min(t0Z, t1Z)));
					final float t = tMinimum > tMaximum ? -1.0F : tMinimum > 0.001F && tMinimum < closestDistance ? tMinimum : tMaximum > 0.001F && tMaximum < closestDistance ? tMaximum : -1.0F;
					
					if(t < 0.0F) {
//						Retrieve the offset to the next node in the BVH structure, relative to the current one:
						boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 1];
					} else {
//						Retrieve the type of the current BVH node:
						final int type = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset];
						
						if(type == BoundingVolumeHierarchy.NODE_TYPE_TREE) {
//							This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:
							boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 4];
						} else {
//							Retrieve the triangle count in the current BVH node:
							final int triangleCount = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 4];
							
							int j = 0;
							
//							Loop through all triangles in the current BVH node:
							while(j < triangleCount) {
//								Retrieve the offset to the current triangle:
								final int currentTriangleOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 5 + j];
								
								final int offsetAPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
								final int offsetBPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
								final int offsetCPosition = this.sceneTriangles_$constant$[currentTriangleOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
								
								final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
								final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
								final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
								final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
								final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
								final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
								final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
								final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
								final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
								
//								Perform an intersection test with the current triangle:
								currentDistance = doIntersectTriangle(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, 0.001F, closestDistance);
								
//								Check if the current distance is less than the distance to the closest primitive so far:
								if(currentDistance < closestDistance) {
									closestDistance = currentDistance;
									closestMatrixOffset = currentMatrixOffset;
									closestPrimitiveOffset = currentPrimitiveOffset;
									closestShapeType = Triangle.TYPE;
									closestShapeOffset = currentTriangleOffset;
								}
								
								if(isTesting && closestPrimitiveOffset != -1) {
									return closestDistance;
								}
								
								j++;
							}
							
//							Retrieve the offset to the next node in the BVH structure, relative to the current one:
							boundingVolumeHierarchyRelativeOffset = this.sceneBoundingVolumeHierarchies_$constant$[boundingVolumeHierarchyOffset + 1];
						}
					}
				}
			} else if(currentShapeType == Plane.TYPE) {
				final int offsetA = this.scenePlanes_$constant$[currentShapeOffset + Plane.RELATIVE_OFFSET_A_OFFSET];
				final int offsetSurfaceNormal = this.scenePlanes_$constant$[currentShapeOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET];
				
				final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
				final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
				final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
				
				final float surfaceNormalX = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 0];
				final float surfaceNormalY = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 1];
				final float surfaceNormalZ = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 2];
				
				currentDistance = doIntersectPlane(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, aX, aY, aZ, surfaceNormalX, surfaceNormalY, surfaceNormalZ, 0.001F, closestDistance);
			} else if(currentShapeType == Sphere.TYPE) {
				final int offsetPosition = (int)(this.sceneSpheres_$constant$[currentShapeOffset + Sphere.RELATIVE_OFFSET_POSITION_OFFSET]);
				
				final float positionX = this.scenePoint3Fs_$constant$[offsetPosition + 0];
				final float positionY = this.scenePoint3Fs_$constant$[offsetPosition + 1];
				final float positionZ = this.scenePoint3Fs_$constant$[offsetPosition + 2];
				
				final float radius = this.sceneSpheres_$constant$[currentShapeOffset + Sphere.RELATIVE_OFFSET_RADIUS];
				
				currentDistance = doIntersectSphere(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, positionX, positionY, positionZ, radius, 0.001F, closestDistance);
			} else if(currentShapeType == Terrain.TYPE) {
				final float frequency = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_FREQUENCY];
				final float gain = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_GAIN];
				final float minimum = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_MINIMUM];
				final float maximum = this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_MAXIMUM];
				
				final int octaves = (int)(this.sceneTerrains_$constant$[currentShapeOffset + Terrain.RELATIVE_OFFSET_OCTAVES]);
				
				currentDistance = doIntersectTerrain(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, frequency, gain, minimum, maximum, octaves);
			} else if(currentShapeType == Triangle.TYPE) {
				final int offsetAPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
				final int offsetBPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
				final int offsetCPosition = this.sceneTriangles_$constant$[currentShapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
				
				final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
				final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
				final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
				final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
				final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
				final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
				final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
				final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
				final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
				
				currentDistance = doIntersectTriangle(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, 0.001F, closestDistance);
			}
			
			if(currentDistance < closestDistance) {
				closestDistance = currentDistance;
				closestMatrixOffset = currentMatrixOffset;
				closestPrimitiveOffset = currentPrimitiveOffset;
				closestShapeType = currentShapeType;
				closestShapeOffset = currentShapeOffset;
			}
			
			if(isTesting && closestPrimitiveOffset != -1) {
				return closestDistance;
			}
		}
		
		if(!isTesting) {
			if(closestPrimitiveOffset != -1) {
				final float objectToWorldElement11 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  0];
				final float objectToWorldElement12 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  1];
				final float objectToWorldElement13 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  2];
				final float objectToWorldElement14 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  3];
				final float objectToWorldElement21 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  4];
				final float objectToWorldElement22 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  5];
				final float objectToWorldElement23 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  6];
				final float objectToWorldElement24 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  7];
				final float objectToWorldElement31 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  8];
				final float objectToWorldElement32 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset +  9];
				final float objectToWorldElement33 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset + 10];
				final float objectToWorldElement34 = this.scenePrimitivesObjectToWorld_$constant$[closestMatrixOffset + 11];
				
				final float worldToObjectElement11 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  0];
				final float worldToObjectElement12 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  1];
				final float worldToObjectElement13 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  2];
				final float worldToObjectElement14 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  3];
				final float worldToObjectElement21 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  4];
				final float worldToObjectElement22 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  5];
				final float worldToObjectElement23 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  6];
				final float worldToObjectElement24 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  7];
				final float worldToObjectElement31 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  8];
				final float worldToObjectElement32 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset +  9];
				final float worldToObjectElement33 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset + 10];
				final float worldToObjectElement34 = this.scenePrimitivesWorldToObject_$constant$[closestMatrixOffset + 11];
				
				final float originXObjectSpace = worldToObjectElement11 * originX + worldToObjectElement12 * originY + worldToObjectElement13 * originZ + worldToObjectElement14;
				final float originYObjectSpace = worldToObjectElement21 * originX + worldToObjectElement22 * originY + worldToObjectElement23 * originZ + worldToObjectElement24;
				final float originZObjectSpace = worldToObjectElement31 * originX + worldToObjectElement32 * originY + worldToObjectElement33 * originZ + worldToObjectElement34;
				
				final float directionXObjectSpace = worldToObjectElement11 * directionX + worldToObjectElement12 * directionY + worldToObjectElement13 * directionZ;
				final float directionYObjectSpace = worldToObjectElement21 * directionX + worldToObjectElement22 * directionY + worldToObjectElement23 * directionZ;
				final float directionZObjectSpace = worldToObjectElement31 * directionX + worldToObjectElement32 * directionY + worldToObjectElement33 * directionZ;
				
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = closestDistance;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET] = closestPrimitiveOffset;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE] = closestShapeType;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET] = closestShapeOffset;
				
				if(closestShapeType == Plane.TYPE) {
					final int offsetA = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_A_OFFSET];
					final int offsetB = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_B_OFFSET];
					final int offsetC = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_C_OFFSET];
					final int offsetSurfaceNormal = this.scenePlanes_$constant$[closestShapeOffset + Plane.RELATIVE_OFFSET_SURFACE_NORMAL_OFFSET];
					
					final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
					final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
					final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
					final float bX = this.scenePoint3Fs_$constant$[offsetB + 0];
					final float bY = this.scenePoint3Fs_$constant$[offsetB + 1];
					final float bZ = this.scenePoint3Fs_$constant$[offsetB + 2];
					final float cX = this.scenePoint3Fs_$constant$[offsetC + 0];
					final float cY = this.scenePoint3Fs_$constant$[offsetC + 1];
					final float cZ = this.scenePoint3Fs_$constant$[offsetC + 2];
					
					final float surfaceNormalX = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 0];
					final float surfaceNormalY = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 1];
					final float surfaceNormalZ = this.sceneVector3Fs_$constant$[offsetSurfaceNormal + 2];
					
					doCalculateSurfacePropertiesForPlane(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, closestDistance, aX, aY, aZ, bX, bY, bZ, cX, cY, cZ, surfaceNormalX, surfaceNormalY, surfaceNormalZ);
				} else if(closestShapeType == Sphere.TYPE) {
					final int offsetPosition = (int)(this.sceneSpheres_$constant$[closestShapeOffset + Sphere.RELATIVE_OFFSET_POSITION_OFFSET]);
					
					final float positionX = this.scenePoint3Fs_$constant$[offsetPosition + 0];
					final float positionY = this.scenePoint3Fs_$constant$[offsetPosition + 1];
					final float positionZ = this.scenePoint3Fs_$constant$[offsetPosition + 2];
					
					doCalculateSurfacePropertiesForSphere(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, closestDistance, positionX, positionY, positionZ);
				} else if(closestShapeType == Terrain.TYPE) {
					final float frequency = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_FREQUENCY];
					final float gain = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_GAIN];
					final float minimum = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_MINIMUM];
					final float maximum = this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_MAXIMUM];
					
					final int octaves = (int)(this.sceneTerrains_$constant$[closestShapeOffset + Terrain.RELATIVE_OFFSET_OCTAVES]);
					
					doCalculateSurfacePropertiesForTerrain(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, closestDistance, frequency, gain, minimum, maximum, octaves);
				} else if(closestShapeType == Triangle.TYPE) {
					final int offsetAPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
					final int offsetBPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
					final int offsetCPosition = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
					
					final int offsetASurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_SURFACE_NORMAL_OFFSET];
					final int offsetBSurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_SURFACE_NORMAL_OFFSET];
					final int offsetCSurfaceNormal = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_SURFACE_NORMAL_OFFSET];
					
					final int offsetASurfaceTangent = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_SURFACE_TANGENT_OFFSET];
					final int offsetBSurfaceTangent = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_SURFACE_TANGENT_OFFSET];
					final int offsetCSurfaceTangent = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_SURFACE_TANGENT_OFFSET];
					
					final int offsetATextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_A_TEXTURE_COORDINATES_OFFSET];
					final int offsetBTextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_B_TEXTURE_COORDINATES_OFFSET];
					final int offsetCTextureCoordinates = this.sceneTriangles_$constant$[closestShapeOffset + Triangle.RELATIVE_OFFSET_C_TEXTURE_COORDINATES_OFFSET];
					
					final float aPositionX = this.scenePoint3Fs_$constant$[offsetAPosition + 0];
					final float aPositionY = this.scenePoint3Fs_$constant$[offsetAPosition + 1];
					final float aPositionZ = this.scenePoint3Fs_$constant$[offsetAPosition + 2];
					final float bPositionX = this.scenePoint3Fs_$constant$[offsetBPosition + 0];
					final float bPositionY = this.scenePoint3Fs_$constant$[offsetBPosition + 1];
					final float bPositionZ = this.scenePoint3Fs_$constant$[offsetBPosition + 2];
					final float cPositionX = this.scenePoint3Fs_$constant$[offsetCPosition + 0];
					final float cPositionY = this.scenePoint3Fs_$constant$[offsetCPosition + 1];
					final float cPositionZ = this.scenePoint3Fs_$constant$[offsetCPosition + 2];
					
					final float aSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 0];
					final float aSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 1];
					final float aSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetASurfaceNormal + 2];
					final float bSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 0];
					final float bSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 1];
					final float bSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetBSurfaceNormal + 2];
					final float cSurfaceNormalX = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 0];
					final float cSurfaceNormalY = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 1];
					final float cSurfaceNormalZ = this.sceneVector3Fs_$constant$[offsetCSurfaceNormal + 2];
					
					final float aSurfaceTangentX = this.sceneVector3Fs_$constant$[offsetASurfaceTangent + 0];
					final float aSurfaceTangentY = this.sceneVector3Fs_$constant$[offsetASurfaceTangent + 1];
					final float aSurfaceTangentZ = this.sceneVector3Fs_$constant$[offsetASurfaceTangent + 2];
					final float bSurfaceTangentX = this.sceneVector3Fs_$constant$[offsetBSurfaceTangent + 0];
					final float bSurfaceTangentY = this.sceneVector3Fs_$constant$[offsetBSurfaceTangent + 1];
					final float bSurfaceTangentZ = this.sceneVector3Fs_$constant$[offsetBSurfaceTangent + 2];
					final float cSurfaceTangentX = this.sceneVector3Fs_$constant$[offsetCSurfaceTangent + 0];
					final float cSurfaceTangentY = this.sceneVector3Fs_$constant$[offsetCSurfaceTangent + 1];
					final float cSurfaceTangentZ = this.sceneVector3Fs_$constant$[offsetCSurfaceTangent + 2];
					
					final float aTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetATextureCoordinates + 0];
					final float aTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetATextureCoordinates + 1];
					final float bTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetBTextureCoordinates + 0];
					final float bTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetBTextureCoordinates + 1];
					final float cTextureCoordinatesU = this.scenePoint2Fs_$constant$[offsetCTextureCoordinates + 0];
					final float cTextureCoordinatesV = this.scenePoint2Fs_$constant$[offsetCTextureCoordinates + 1];
					
					doCalculateSurfacePropertiesForTriangle(originXObjectSpace, originYObjectSpace, originZObjectSpace, directionXObjectSpace, directionYObjectSpace, directionZObjectSpace, closestDistance, aPositionX, aPositionY, aPositionZ, bPositionX, bPositionY, bPositionZ, cPositionX, cPositionY, cPositionZ, aSurfaceNormalX, aSurfaceNormalY, aSurfaceNormalZ, bSurfaceNormalX, bSurfaceNormalY, bSurfaceNormalZ, cSurfaceNormalX, cSurfaceNormalY, cSurfaceNormalZ, aSurfaceTangentX, aSurfaceTangentY, aSurfaceTangentZ, bSurfaceTangentX, bSurfaceTangentY, bSurfaceTangentZ, cSurfaceTangentX, cSurfaceTangentY, cSurfaceTangentZ, aTextureCoordinatesU, aTextureCoordinatesV, bTextureCoordinatesU, bTextureCoordinatesV, cTextureCoordinatesU, cTextureCoordinatesV);
				}
				
				doTransformIntersectionToWorldSpace(objectToWorldElement11, objectToWorldElement12, objectToWorldElement13, objectToWorldElement14, objectToWorldElement21, objectToWorldElement22, objectToWorldElement23, objectToWorldElement24, objectToWorldElement31, objectToWorldElement32, objectToWorldElement33, objectToWorldElement34, worldToObjectElement11, worldToObjectElement12, worldToObjectElement13, worldToObjectElement21, worldToObjectElement22, worldToObjectElement23, worldToObjectElement31, worldToObjectElement32, worldToObjectElement33);
				
//				Perform Normal Mapping via Image Texture:
				doPerformNormalMappingViaImageTexture(closestPrimitiveOffset);
				
//				Perform Normal Mapping via Noise:
				doPerformNormalMappingViaNoise(closestPrimitiveOffset);
			} else {
//				Reset the information in the intersections array:
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET] = -1;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE] = -1;
				this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET] = -1;
			}
		}
		
		return closestDistance;
	}
	
	@SuppressWarnings("static-method")
	private float doIntersectPlane(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float aX, final float aY, final float aZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ, final float tMinimum, final float tMaximum) {
//		Calculate the dot product between the surface normal and the ray direction:
		final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;
		
//		Check that the dot product is not 0.0:
		if(dotProduct < 0.0F || dotProduct > 0.0F) {
//			Calculate t:
			final float t = ((aX - originX) * surfaceNormalX + (aY - originY) * surfaceNormalY + (aZ - originZ) * surfaceNormalZ) / dotProduct;
			
//			Check that t is greater than an epsilon value and return it if so:
			if(t > tMinimum && t < tMaximum) {
				return t;
			}
		}
		
//		Return no hit:
		return INFINITY;
	}
	
	private float doIntersectSphere(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float positionX, final float positionY, final float positionZ, final float radius, final float tMinimum, final float tMaximum) {
		final float positionToOriginX = originX - positionX;
		final float positionToOriginY = originY - positionY;
		final float positionToOriginZ = originZ - positionZ;
		
		final float radiusSquared = radius * radius;
		
		final float a = directionX * directionX + directionY * directionY + directionZ * directionZ;
		final float b = 2.0F * (positionToOriginX * directionX + positionToOriginY * directionY + positionToOriginZ * directionZ);
		final float c = (positionToOriginX * positionToOriginX + positionToOriginY * positionToOriginY + positionToOriginZ * positionToOriginZ) - radiusSquared;
		
		final float discriminantSquared = b * b - 4.0F * a * c;
		
		if(discriminantSquared >= 0.0F) {
			final float discriminant = sqrt(discriminantSquared);
			
			final float quadratic = -0.5F * (b < 0.0F ? b - discriminant : b + discriminant);
			
			final float result0 = quadratic / a;
			final float result1 = quadratic == 0 ? result0 : c / quadratic;
			
			final float t0 = min(result0, result1);
			final float t1 = max(result0, result1);
			
			if(t0 > tMinimum && t0 < tMaximum) {
				return t0;
			}
			
			if(t1 > tMinimum && t1 < tMaximum) {
				return t1;
			}
		}
		
		return INFINITY;
	}
	
	@SuppressWarnings("unused")
	private float doIntersectTerrain(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float frequency, final float gain, final float minimum, final float maximum, final int octaves) {
		final float scale = 10.0F;
		final float scaleReciprocal = 1.0F / scale;
		
		float t = 0.0F;
		
		final float tMinimum = 0.001F;
		final float tMaximum = 10.0F;
		
		float tDelta = 0.01F;
		
		float lH = 0.0F;
		float lY = 0.0F;
		
		for(float tCurrent = tMinimum; tCurrent < tMaximum; tCurrent += tDelta) {
			final float surfaceIntersectionPointX = originX * scaleReciprocal + directionX * tCurrent;
			final float surfaceIntersectionPointY = originY * scaleReciprocal + directionY * tCurrent;
			final float surfaceIntersectionPointZ = originZ * scaleReciprocal + directionZ * tCurrent;
			
			final float h = sin(surfaceIntersectionPointX) * sin(surfaceIntersectionPointZ);
			
			if(surfaceIntersectionPointY < h) {
				t = tCurrent - tDelta + tDelta * (lH - lY) / (surfaceIntersectionPointY - lY - h + lH);
				
				tCurrent = tMaximum;
			}
			
			tDelta = 0.01F * tCurrent;
			
			lH = h;
			lY = surfaceIntersectionPointY;
		}
		
		t = t > EPSILON ? t : INFINITY;
		
		return t;
		
		/*
		float t = 0.0F;
		
		final float tMinimum = 0.001F;
		final float tMaximum = 40.0F;
		final float tMultiplier = 0.1F;
		
		float tDelta = tMultiplier;
		
		for(float tCurrent = tMinimum; tCurrent < tMaximum; tCurrent += tDelta) {
			final float surfaceIntersectionPointX = originX / 40.0F + directionX * tCurrent;
			final float surfaceIntersectionPointY = originY / 40.0F + directionY * tCurrent;
			final float surfaceIntersectionPointZ = originZ / 40.0F + directionZ * tCurrent;
			
//			final float y = simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX, surfaceIntersectionPointZ);
//			final float y = (simplexFractalXY(getAmplitude(), getFrequency(), getGain(), getLacunarity(), getOctaves(), surfaceIntersectionPointX, surfaceIntersectionPointZ) + 1.0F) / 2.0F;
			final float y = (sin(surfaceIntersectionPointX) * sin(surfaceIntersectionPointZ));
			
			if(surfaceIntersectionPointY < y) {
				t = tCurrent;
				
				tCurrent = tMaximum;
			}
			
			tDelta = tMultiplier * tCurrent;
		}
		
		t = t > EPSILON ? t : INFINITY;
		
		return t;
		*/
	}
	
	@SuppressWarnings("static-method")
	private float doIntersectTriangle(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float aX, final float aY, final float aZ, final float bX, final float bY, final float bZ, final float cX, final float cY, final float cZ, final float tMinimum, final float tMaximum) {
//		Calculate the first edge between the points A and B:
		final float edge0X = bX - aX;
		final float edge0Y = bY - aY;
		final float edge0Z = bZ - aZ;
		
//		Calculate the second edge between the points A and C:
		final float edge1X = cX - aX;
		final float edge1Y = cY - aY;
		final float edge1Z = cZ - aZ;
		
//		Calculate the cross product:
		final float v0X = directionY * edge1Z - directionZ * edge1Y;
		final float v0Y = directionZ * edge1X - directionX * edge1Z;
		final float v0Z = directionX * edge1Y - directionY * edge1X;
		
//		Calculate the determinant:
		final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;
		
//		Initialize the distance to a value denoting no hit:
		float t = INFINITY;
		
//		Check that the determinant is anything other than in the range of negative epsilon to positive epsilon:
		if(determinant < -0.0001F || determinant > 0.0001F) {
//			Calculate the reciprocal of the determinant:
			final float determinantReciprocal = 1.0F / determinant;
			
//			Calculate the direction to the point A:
			final float v1X = originX - aX;
			final float v1Y = originY - aY;
			final float v1Z = originZ - aZ;
			
//			Calculate the U value:
			final float u = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;
			
//			Check that the U value is between 0.0 and 1.0:
			if(u >= 0.0F && u <= 1.0F) {
//				Calculate the cross product:
				final float v2X = v1Y * edge0Z - v1Z * edge0Y;
				final float v2Y = v1Z * edge0X - v1X * edge0Z;
				final float v2Z = v1X * edge0Y - v1Y * edge0X;
				
//				Calculate the V value:
				final float v = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;
				
//				Update the distance value:
				t = v >= 0.0F && u + v <= 1.0F ? (edge1X * v2X + edge1Y * v2Y + edge1Z * v2Z) * determinantReciprocal : tMinimum;
				t = t > tMinimum && t < tMaximum ? t : INFINITY;
			}
		}
		
//		Return the distance:
		return t;
	}
	
	private int doGetTextureColor(final int texturesOffset) {
		final int textureType = (int)(this.sceneTextures_$constant$[texturesOffset + Texture.RELATIVE_OFFSET_TYPE]);
		
		if(textureType == BlendTexture.TYPE) {
			return doGetTextureColorFromBlendTexture(texturesOffset);
		} else if(textureType == BullseyeTexture.TYPE) {
			return doGetTextureColorFromBullseyeTexture(texturesOffset);
		} else if(textureType == CheckerboardTexture.TYPE) {
			return doGetTextureColorFromCheckerboardTexture(texturesOffset);
		} else if(textureType == ConstantTexture.TYPE) {
			return doGetTextureColorFromConstantTexture(texturesOffset);
		} else if(textureType == FractionalBrownianMotionTexture.TYPE) {
			return doGetTextureColorFromFractionalBrownianMotionTexture(texturesOffset);
		} else if(textureType == ImageTexture.TYPE) {
			return doGetTextureColorFromImageTexture(texturesOffset);
		} else if(textureType == SurfaceNormalTexture.TYPE) {
			return doGetTextureColorFromSurfaceNormalTexture();
		} else if(textureType == UVTexture.TYPE) {
			return doGetTextureColorFromUVTexture();
		} else {
			return 0;
		}
	}
	
	private int doGetTextureColor2(final int texturesOffset) {
		final int textureType = (int)(this.sceneTextures_$constant$[texturesOffset + Texture.RELATIVE_OFFSET_TYPE]);
		
		if(textureType == BullseyeTexture.TYPE) {
			return doGetTextureColorFromBullseyeTexture(texturesOffset);
		} else if(textureType == CheckerboardTexture.TYPE) {
			return doGetTextureColorFromCheckerboardTexture(texturesOffset);
		} else if(textureType == ConstantTexture.TYPE) {
			return doGetTextureColorFromConstantTexture(texturesOffset);
		} else if(textureType == FractionalBrownianMotionTexture.TYPE) {
			return doGetTextureColorFromFractionalBrownianMotionTexture(texturesOffset);
		} else if(textureType == ImageTexture.TYPE) {
			return doGetTextureColorFromImageTexture(texturesOffset);
		} else if(textureType == SurfaceNormalTexture.TYPE) {
			return doGetTextureColorFromSurfaceNormalTexture();
		} else if(textureType == UVTexture.TYPE) {
			return doGetTextureColorFromUVTexture();
		} else {
			return 0;
		}
	}
	
	private int doGetTextureColorFromBlendTexture(final int texturesOffset) {
		final int textureAOffset = (int)(this.sceneTextures_$constant$[texturesOffset + BlendTexture.RELATIVE_OFFSET_TEXTURE_A_OFFSET]);
		final int textureBOffset = (int)(this.sceneTextures_$constant$[texturesOffset + BlendTexture.RELATIVE_OFFSET_TEXTURE_B_OFFSET]);
		
		final float factor = this.sceneTextures_$constant$[texturesOffset + BlendTexture.RELATIVE_OFFSET_FACTOR];
		
		final int textureAColorRGB = doGetTextureColor2(textureAOffset);
		final int textureBColorRGB = doGetTextureColor2(textureBOffset);
		
		final float textureAColorR = ((textureAColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float textureAColorG = ((textureAColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float textureAColorB = ((textureAColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float textureBColorR = ((textureBColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float textureBColorG = ((textureBColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float textureBColorB = ((textureBColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final int colorR = (int)(saturate((1.0F - factor) * textureAColorR + factor * textureBColorR, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int colorG = (int)(saturate((1.0F - factor) * textureAColorG + factor * textureBColorG, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int colorB = (int)(saturate((1.0F - factor) * textureAColorB + factor * textureBColorB, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int colorRGB = ((colorR & 0xFF) << 16) | ((colorG & 0xFF) << 8) | (colorB & 0xFF);
		
		return colorRGB;
	}
	
	private int doGetTextureColorFromBullseyeTexture(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final int offsetSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetColorA = texturesOffset + BullseyeTexture.RELATIVE_OFFSET_COLOR_A;
		final int offsetColorB = texturesOffset + BullseyeTexture.RELATIVE_OFFSET_COLOR_B;
		
		final float x = this.intersections_$local$[offsetSurfaceIntersectionPoint + 0];
		final float y = this.intersections_$local$[offsetSurfaceIntersectionPoint + 1];
		final float z = this.intersections_$local$[offsetSurfaceIntersectionPoint + 2];
		
		final int colorARGB = (int)(this.sceneTextures_$constant$[offsetColorA]);
		final int colorBRGB = (int)(this.sceneTextures_$constant$[offsetColorB]);
		
		final float colorAR = ((colorARGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float colorAG = ((colorARGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float colorAB = ((colorARGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float colorBR = ((colorBRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float colorBG = ((colorBRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float colorBB = ((colorBRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float length = sqrt(x * x + y * y + z * z);
		final float value = remainder(length * 0.25F, 1.0F);
		
		float colorR = value > 0.5F ? colorAR : colorBR;
		float colorG = value > 0.5F ? colorAG : colorBG;
		float colorB = value > 0.5F ? colorAB : colorBB;
		
		final float textureMultiplier = value > 0.5F ? 0.8F : 1.2F;
		
		if(colorAR == colorBR && colorAG == colorBG && colorAB == colorBB) {
			colorR *= textureMultiplier;
			colorG *= textureMultiplier;
			colorB *= textureMultiplier;
		}
		
		final int r = (int)(saturate(colorR, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(colorG, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate(colorB, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromCheckerboardTexture(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final int offsetUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		final int offsetColor0 = texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_0;
		final int offsetColor1 = texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_COLOR_1;
		
		final float u = this.intersections_$local$[offsetUVCoordinates + 0];
		final float v = this.intersections_$local$[offsetUVCoordinates + 1];
		
		final int color0RGB = (int)(this.sceneTextures_$constant$[offsetColor0]);
		final int color1RGB = (int)(this.sceneTextures_$constant$[offsetColor1]);
		
		final float color0R = ((color0RGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float color0G = ((color0RGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float color0B = ((color0RGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float color1R = ((color1RGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float color1G = ((color1RGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float color1B = ((color1RGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float sU = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_SCALE_U];
		final float sV = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_SCALE_V];
		
		final float cosAngle = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.sceneTextures_$constant$[texturesOffset + CheckerboardTexture.RELATIVE_OFFSET_RADIANS_SIN];
		
		final float textureU = modulo((u * cosAngle - v * sinAngle) * sU);
		final float textureV = modulo((v * cosAngle + u * sinAngle) * sV);
		
		final boolean isDarkU = textureU > 0.5F;
		final boolean isDarkV = textureV > 0.5F;
		final boolean isDark = isDarkU ^ isDarkV;
		
		final float textureMultiplier = isDark ? 0.8F : 1.2F;
		
		float colorR = isDark ? color0R : color1R;
		float colorG = isDark ? color0G : color1G;
		float colorB = isDark ? color0B : color1B;
		
		if(color0R == color1R && color0G == color1G && color0B == color1B) {
			colorR *= textureMultiplier;
			colorG *= textureMultiplier;
			colorB *= textureMultiplier;
		}
		
		final int r = (int)(saturate(colorR, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(colorG, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate(colorB, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromConstantTexture(final int texturesOffset) {
//		Retrieve the R-, G- and B-component values of the texture:
		final int colorRGB = (int)(this.sceneTextures_$constant$[texturesOffset + ConstantTexture.RELATIVE_OFFSET_COLOR]);
		
		return colorRGB;
	}
	
	private int doGetTextureColorFromFractionalBrownianMotionTexture(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final int offsetSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetAddend = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_ADDEND;
		final int offsetMultiplier = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_MULTIPLIER;
		final int offsetFrequency = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_FREQUENCY;
		final int offsetGain = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_GAIN;
		final int offsetOctaves = texturesOffset + FractionalBrownianMotionTexture.RELATIVE_OFFSET_OCTAVES;
		
		final float x = this.intersections_$local$[offsetSurfaceIntersectionPoint];
		final float y = this.intersections_$local$[offsetSurfaceIntersectionPoint + 1];
		final float z = this.intersections_$local$[offsetSurfaceIntersectionPoint + 2];
		
		final int addendRGB = (int)(this.sceneTextures_$constant$[offsetAddend]);
		final int multiplierRGB = (int)(this.sceneTextures_$constant$[offsetMultiplier]);
		
		final float addendR = ((addendRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float addendG = ((addendRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float addendB = ((addendRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float multiplierR = ((multiplierRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float multiplierG = ((multiplierRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float multiplierB = ((multiplierRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float frequency = this.sceneTextures_$constant$[offsetFrequency];
		final float gain = this.sceneTextures_$constant$[offsetGain];
		
		final int octaves = (int)(this.sceneTextures_$constant$[offsetOctaves]);
		
		final float noise = simplexFractionalBrownianMotionXYZ(frequency, gain, 0.0F, 1.0F, octaves, x, y, z);
		
		final int r = (int)(saturate(noise * multiplierR + addendR, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(noise * multiplierG + addendG, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate(noise * multiplierB + addendB, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromImageTexture(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final int offsetTextureCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
		final float u = this.intersections_$local$[offsetTextureCoordinates + 0];
		final float v = this.intersections_$local$[offsetTextureCoordinates + 1];
		
		final float width = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_WIDTH];
		final float height = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_HEIGHT];
		
		final float scaleU = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_V];
		
		final float cosAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_SIN];
		
		final float u0 = (u * cosAngle - v * sinAngle);
		final float v0 = (v * cosAngle + u * sinAngle);
		final float u1 = remainder(u0 * scaleU * width, width);
		final float v1 = remainder(v0 * scaleV * height, height);
		final float u2 = u1 >= 0.0F ? u1 : width - abs(u1);
		final float v2 = v1 >= 0.0F ? v1 : height - abs(v1);
		
		final int x = (int)(u2);
		final int y = (int)(v2);
		
		final int x00 = x + 0;
		final int y00 = y + 0;
		final int x01 = x + 1;
		final int y01 = y + 0;
		final int x10 = x + 0;
		final int y10 = y + 1;
		final int x11 = x + 1;
		final int y11 = y + 1;
		
		final int w = (int)(width);
		final int resolution = (int)(width * height);
		
		final int index00 = y00 * w + x00;
		final int index01 = y01 * w + x01;
		final int index10 = y10 * w + x10;
		final int index11 = y11 * w + x11;
		
		final int colorRGB00 = index00 >= 0 && index00 < resolution ? (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index00]) : 0;
		final int colorRGB01 = index01 >= 0 && index01 < resolution ? (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index01]) : 0;
		final int colorRGB10 = index10 >= 0 && index10 < resolution ? (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index10]) : 0;
		final int colorRGB11 = index11 >= 0 && index11 < resolution ? (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index11]) : 0;
		
		final float colorR00 = ((colorRGB00 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float colorG00 = ((colorRGB00 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float colorB00 = ((colorRGB00 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float colorR01 = ((colorRGB01 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float colorG01 = ((colorRGB01 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float colorB01 = ((colorRGB01 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float colorR10 = ((colorRGB10 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float colorG10 = ((colorRGB10 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float colorB10 = ((colorRGB10 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float colorR11 = ((colorRGB11 >> 16) & 0xFF) * COLOR_RECIPROCAL;
		final float colorG11 = ((colorRGB11 >>  8) & 0xFF) * COLOR_RECIPROCAL;
		final float colorB11 = ((colorRGB11 >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
		final float factorX = u2 - x;
		final float factorY = v2 - y;
		
		final int colorR = (int)(saturate(blerp(colorR00, colorR01, colorR10, colorR11, factorX, factorY), 0.0F, 1.0F) * 255.0F + 0.5F);
		final int colorG = (int)(saturate(blerp(colorG00, colorG01, colorG10, colorG11, factorX, factorY), 0.0F, 1.0F) * 255.0F + 0.5F);
		final int colorB = (int)(saturate(blerp(colorB00, colorB01, colorB10, colorB11, factorX, factorY), 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int colorRGB = ((colorR & 0xFF) << 16) | ((colorG & 0xFF) << 8) | (colorB & 0xFF);
		
		return colorRGB;
	}
	
	private int doGetTextureColorFromImageTextureSimple(final int texturesOffset) {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final int offsetTextureCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
		final float u = this.intersections_$local$[offsetTextureCoordinates + 0];
		final float v = this.intersections_$local$[offsetTextureCoordinates + 1];
		
		final float width = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_WIDTH];
		final float height = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_HEIGHT];
		
		final float scaleU = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_U];
		final float scaleV = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_SCALE_V];
		
		final float cosAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_COS];
		final float sinAngle = this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_RADIANS_SIN];
		
		final float u0 = (u * cosAngle - v * sinAngle);
		final float v0 = (v * cosAngle + u * sinAngle);
		final float u1 = remainder(u0 * scaleU * width, width);
		final float v1 = remainder(v0 * scaleV * height, height);
		final float u2 = u1 >= 0.0F ? u1 : width - abs(u1);
		final float v2 = v1 >= 0.0F ? v1 : height - abs(v1);
		
		final int x = (int)(u2);
		final int y = (int)(v2);
		
		final int w = (int)(width);
		final int resolution = (int)(width * height);
		
		final int index = y * w + x;
		
		final int colorRGB = index >= 0 && index < resolution ? (int)(this.sceneTextures_$constant$[texturesOffset + ImageTexture.RELATIVE_OFFSET_DATA + index]) : 0;
		
		return colorRGB;
	}
	
	private int doGetTextureColorFromSurfaceNormalTexture() {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final float surfaceNormalShadingX = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING + 0];
		final float surfaceNormalShadingY = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING + 1];
		final float surfaceNormalShadingZ = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING + 2];
		
		final int r = (int)(saturate((surfaceNormalShadingX + 1.0F) * 0.5F, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate((surfaceNormalShadingY + 1.0F) * 0.5F, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = (int)(saturate((surfaceNormalShadingZ + 1.0F) * 0.5F, 0.0F, 1.0F) * 255.0F + 0.5F);
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doGetTextureColorFromUVTexture() {
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
		final float u = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES + 0];
		final float v = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES + 1];
		
		final int r = (int)(saturate(u, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int g = (int)(saturate(v, 0.0F, 1.0F) * 255.0F + 0.5F);
		final int b = 0;
		
		final int rGB = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
		
		return rGB;
	}
	
	private int doShaderPhongReflectionModel0(final boolean isCheckingForIntersections, final float pX, final float pY, final float pZ, final float nX, final float nY, final float nZ, final float vX, final float vY, final float vZ, final float albedoR, final float albedoG, final float albedoB, final float kaR, final float kaG, final float kaB, final float kdR, final float kdG, final float kdB, final float ksR, final float ksG, final float ksB, final float ns) {
//		Initialize the color:
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;
		
//		Retrieve Ia, the ambient intensity:
		final float ia = 1.0F;
		
//		Compute and add the ambient light to the current color:
		r += albedoR * kaR * ia;
		g += albedoG * kaG * ia;
		b += albedoB * kaB * ia;
		
		final float lPositionX = this.sunAndSkySunOriginX;
		final float lPositionY = this.sunAndSkySunOriginY;
		final float lPositionZ = this.sunAndSkySunOriginZ;
		
//		Compute L, the normalized direction vector from the intersection point to the point light:
		final float lX = lPositionX - pX;
		final float lY = lPositionY - pY;
		final float lZ = lPositionZ - pZ;
		final float lLengthSquared = lX * lX + lY * lY + lZ * lZ;
		final float lLength = sqrt(lLengthSquared);
		final float lLengthReciprocal = 1.0F / lLength;
		final float lNormalizedX = lX * lLengthReciprocal;
		final float lNormalizedY = lY * lLengthReciprocal;
		final float lNormalizedZ = lZ * lLengthReciprocal;
		
		if(isCheckingForIntersections) {
//			Perform an intersection test:
			doIntersectPrimitives(pX, pY, pZ, lNormalizedX, lNormalizedY, lNormalizedZ, false);
		}
		
//		Compute t, the closest intersection from the intersection point in the direction of L:
		final float t = isCheckingForIntersections ? this.intersections_$local$[getLocalId() * SIZE_INTERSECTION + RELATIVE_OFFSET_INTERSECTION_DISTANCE] : INFINITY;
		
		if(t >= INFINITY || t > lLengthSquared) {
//			Compute the dot product between L and N:
			final float lDotN = lNormalizedX * nX + lNormalizedY * nY + lNormalizedZ * nZ;
			
//			Retrieve Id, the diffuse intensity:
			final float id = 1.0F;
			
//			Compute and add the diffuse light to the current color:
			r += albedoR * kdR * max(lDotN, 0.0F) * id;
			g += albedoG * kdG * max(lDotN, 0.0F) * id;
			b += albedoB * kdB * max(lDotN, 0.0F) * id;
			
			if(lDotN > 0.0F) {
				final float rX = 2.0F * lDotN * nX - lNormalizedX;
				final float rY = 2.0F * lDotN * nY - lNormalizedY;
				final float rZ = 2.0F * lDotN * nZ - lNormalizedZ;
				final float rLengthReciprocal = rsqrt(rX * rX + rY * rY + rZ * rZ);
				final float rNormalizedX = rX * rLengthReciprocal;
				final float rNormalizedY = rY * rLengthReciprocal;
				final float rNormalizedZ = rZ * rLengthReciprocal;
				
				final float rDotV = rNormalizedX * vX + rNormalizedY * vY + rNormalizedZ * vZ;
				
				final float shininess = pow(max(rDotV, 0.0F), ns);
				
//				Retrieve Is, the specular intensity:
				final float is = 1.0F;
				
//				Compute and add the specular light to the color:
				r += ksR * shininess * is;
				g += ksG * shininess * is;
				b += ksB * shininess * is;
			}
		}
		
		r = saturate(r, 0.0F, 1.0F);
		g = saturate(g, 0.0F, 1.0F);
		b = saturate(b, 0.0F, 1.0F);
		
		return (((int)(r * 255.0F + 0.5F) & 0xFF) << 16) | (((int)(g * 255.0F + 0.5F) & 0xFF) << 8) | (((int)(b * 255.0F + 0.5F) & 0xFF));
	}
	
	@SuppressWarnings("unused")
	private int doShaderPhongReflectionModel1(final boolean isCheckingForIntersections, final float surfaceIntersectionPointX, final float surfaceIntersectionPointY, final float surfaceIntersectionPointZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ, final float rayDirectionX, final float rayDirectionY, final float rayDirectionZ, final float albedoR, final float albedoG, final float albedoB) {
		float r = albedoR * 0.5F;
		float g = albedoG * 0.5F;
		float b = albedoB * 0.5F;
		
		final float woX = -rayDirectionX;
		final float woY = -rayDirectionY;
		final float woZ = -rayDirectionZ;
		final float woLengthReciprocal = rsqrt(woX * woX + woY * woY + woZ * woZ);
		final float woNormalizedX = woX * woLengthReciprocal;
		final float woNormalizedY = woY * woLengthReciprocal;
		final float woNormalizedZ = woZ * woLengthReciprocal;
		
		final float lPositionX = this.sunAndSkySunOriginX;
		final float lPositionY = this.sunAndSkySunOriginY;
		final float lPositionZ = this.sunAndSkySunOriginZ;
		
//		Compute L, the normalized direction vector from the intersection point to the point light:
		final float lX = lPositionX - surfaceIntersectionPointX;
		final float lY = lPositionY - surfaceIntersectionPointY;
		final float lZ = lPositionZ - surfaceIntersectionPointZ;
		final float lLengthSquared = lX * lX + lY * lY + lZ * lZ;
		final float lLength = sqrt(lLengthSquared);
		final float lLengthReciprocal = 1.0F / lLength;
		final float lNormalizedX = lX * lLengthReciprocal;
		final float lNormalizedY = lY * lLengthReciprocal;
		final float lNormalizedZ = lZ * lLengthReciprocal;
		
		if(isCheckingForIntersections) {
//			Perform an intersection test:
			doIntersectPrimitives(surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, lNormalizedX, lNormalizedY, lNormalizedZ, false);
		}
		
//		Compute t, the closest intersection from the intersection point in the direction of L:
		final float t = isCheckingForIntersections ? this.intersections_$local$[getLocalId() * SIZE_INTERSECTION + RELATIVE_OFFSET_INTERSECTION_DISTANCE] : INFINITY;
		
		if(t >= INFINITY || t > lLengthSquared) {
			final float wiX = lPositionX - surfaceIntersectionPointX;
			final float wiY = lPositionY - surfaceIntersectionPointY;
			final float wiZ = lPositionZ - surfaceIntersectionPointZ;
			final float wiLengthReciprocal = rsqrt(wiX * wiX + wiY * wiY + wiZ * wiZ);
			final float wiNormalizedX = wiX * wiLengthReciprocal;
			final float wiNormalizedY = wiY * wiLengthReciprocal;
			final float wiNormalizedZ = wiZ * wiLengthReciprocal;
			
			final float surfaceNormalDotWi = surfaceNormalX * wiNormalizedX + surfaceNormalY * wiNormalizedY + surfaceNormalZ * wiNormalizedZ;
			
			if(surfaceNormalDotWi > 0.0F) {
				final float reflectionX = -wiNormalizedX + (2.0F * surfaceNormalX * surfaceNormalDotWi);
				final float reflectionY = -wiNormalizedY + (2.0F * surfaceNormalY * surfaceNormalDotWi);
				final float reflectionZ = -wiNormalizedZ + (2.0F * surfaceNormalZ * surfaceNormalDotWi);
				
				final float reflectionDotWo = reflectionX * woNormalizedX + reflectionY * woNormalizedY + reflectionZ * woNormalizedZ;
				
				final float diffuseIntensity = 1.0F;
				final float diffuseColorR = albedoR * diffuseIntensity * PI_RECIPROCAL;
				final float diffuseColorG = albedoG * diffuseIntensity * PI_RECIPROCAL;
				final float diffuseColorB = albedoB * diffuseIntensity * PI_RECIPROCAL;
				
				if(reflectionDotWo > 0.0F) {
					final float specularIntensity = 1.0F;
					final float specularPower = 50.0F;
					final float specularComponent = pow(reflectionDotWo, specularPower) * specularIntensity;
					final float specularColorR = 1.0F * specularComponent;
					final float specularColorG = 1.0F * specularComponent;
					final float specularColorB = 1.0F * specularComponent;
					
					r += (diffuseColorR + specularColorR) * surfaceNormalDotWi;
					g += (diffuseColorG + specularColorG) * surfaceNormalDotWi;
					b += (diffuseColorB + specularColorB) * surfaceNormalDotWi;
				} else {
					r += diffuseColorR * surfaceNormalDotWi;
					g += diffuseColorG * surfaceNormalDotWi;
					b += diffuseColorB * surfaceNormalDotWi;
				}
			}
		}
		
		r = saturate(r, 0.0F, 1.0F);
		g = saturate(g, 0.0F, 1.0F);
		b = saturate(b, 0.0F, 1.0F);
		
		return (((int)(r * 255.0F + 0.5F) & 0xFF) << 16) | (((int)(g * 255.0F + 0.5F) & 0xFF) << 8) | (((int)(b * 255.0F + 0.5F) & 0xFF));
	}
	
	private void doCalculateColorForSky(final boolean isSkyActive, final float directionX, final float directionY, final float directionZ) {
//		Calculate the direction vector:
		float direction0X = directionX * this.sunAndSkyOrthoNormalBasisUX + directionY * this.sunAndSkyOrthoNormalBasisUY + directionZ * this.sunAndSkyOrthoNormalBasisUZ;
		float direction0Y = directionX * this.sunAndSkyOrthoNormalBasisVX + directionY * this.sunAndSkyOrthoNormalBasisVY + directionZ * this.sunAndSkyOrthoNormalBasisVZ;
		float direction0Z = directionX * this.sunAndSkyOrthoNormalBasisWX + directionY * this.sunAndSkyOrthoNormalBasisWY + directionZ * this.sunAndSkyOrthoNormalBasisWZ;
		
		float r = 0.01F;
		float g = 0.01F;
		float b = 0.01F;
		
		if(direction0Z >= 0.0F && isSkyActive) {
			direction0Z = max(direction0Z, 0.001F);
			
//			Recalculate the direction vector:
			final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
			final float direction1X = direction0X * direction0LengthReciprocal;
			final float direction1Y = direction0Y * direction0LengthReciprocal;
			final float direction1Z = direction0Z * direction0LengthReciprocal;
			
//			Calculate the dot product between the direction vector and the sun direction vector:
			final float dotProduct = direction1X * this.sunAndSkySunDirectionX + direction1Y * this.sunAndSkySunDirectionY + direction1Z * this.sunAndSkySunDirectionZ;
			
//			Calculate some theta angles:
			final double theta0 = this.sunAndSkyTheta;
			final double theta1 = acos(max(min(direction1Z, 1.0D), -1.0D));
			
//			Calculate the cosines of the theta angles:
			final double cosTheta0 = cos(theta0);
			final double cosTheta1 = cos(theta1);
			final double cosTheta1Reciprocal = 1.0D / (cosTheta1 + 0.01D);
			
//			Calculate the gamma:
			final double gamma = acos(max(min(dotProduct, 1.0D), -1.0D));
			
//			Calculate the cosine of the gamma:
			final double cosGamma = cos(gamma);
			
//			TODO: Write explanation!
			final double perezRelativeLuminance0 = this.sunAndSkyPerezRelativeLuminance_$constant$[0];
			final double perezRelativeLuminance1 = this.sunAndSkyPerezRelativeLuminance_$constant$[1];
			final double perezRelativeLuminance2 = this.sunAndSkyPerezRelativeLuminance_$constant$[2];
			final double perezRelativeLuminance3 = this.sunAndSkyPerezRelativeLuminance_$constant$[3];
			final double perezRelativeLuminance4 = this.sunAndSkyPerezRelativeLuminance_$constant$[4];
			
//			TODO: Write explanation!
			final double zenithRelativeLuminance = this.sunAndSkyZenithRelativeLuminance;
			
//			TODO: Write explanation!
			final double perezX0 = this.sunAndSkyPerezX_$constant$[0];
			final double perezX1 = this.sunAndSkyPerezX_$constant$[1];
			final double perezX2 = this.sunAndSkyPerezX_$constant$[2];
			final double perezX3 = this.sunAndSkyPerezX_$constant$[3];
			final double perezX4 = this.sunAndSkyPerezX_$constant$[4];
			
//			TODO: Write explanation!
			final double perezY0 = this.sunAndSkyPerezY_$constant$[0];
			final double perezY1 = this.sunAndSkyPerezY_$constant$[1];
			final double perezY2 = this.sunAndSkyPerezY_$constant$[2];
			final double perezY3 = this.sunAndSkyPerezY_$constant$[3];
			final double perezY4 = this.sunAndSkyPerezY_$constant$[4];
			
//			TODO: Write explanation!
			final double zenithX = this.sunAndSkyZenithX;
			final double zenithY = this.sunAndSkyZenithY;
			
//			TODO: Write explanation!
			final double relativeLuminanceDenominator = ((1.0D + perezRelativeLuminance0 * exp(perezRelativeLuminance1)) * (1.0D + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * theta0) + perezRelativeLuminance4 * cosTheta0 * cosTheta0));
			final double relativeLuminanceNumerator = ((1.0D + perezRelativeLuminance0 * exp(perezRelativeLuminance1 * cosTheta1Reciprocal)) * (1.0D + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * gamma) + perezRelativeLuminance4 * cosGamma * cosGamma));
			final double relativeLuminance = zenithRelativeLuminance * relativeLuminanceNumerator / relativeLuminanceDenominator * 1.0e-4D;
			
//			TODO: Write explanation!
			final double xDenominator = ((1.0D + perezX0 * exp(perezX1)) * (1.0D + perezX2 * exp(perezX3 * theta1) + perezX4 * cosTheta0 * cosTheta0));
			final double xNumerator = ((1.0D + perezX0 * exp(perezX1 * cosTheta1Reciprocal)) * (1.0D + perezX2 * exp(perezX3 * gamma) + perezX4 * cosGamma * cosGamma));
			final double x = zenithX * xNumerator / xDenominator;
			
//			TODO: Write explanation!
			final double yDenominator = ((1.0D + perezY0 * exp(perezY1)) * (1.0D + perezY2 * exp(perezY3 * theta1) + perezY4 * cosTheta0 * cosTheta0));
			final double yNumerator = ((1.0D + perezY0 * exp(perezY1 * cosTheta1Reciprocal)) * (1.0D + perezY2 * exp(perezY3 * gamma) + perezY4 * cosGamma * cosGamma));
			final double y = zenithY * yNumerator / yDenominator;
			
//			Calculates a CIE XYZ color:
			final float colorCIE0 = 1.0F / (0.0241F + 0.2562F * (float)(x) - 0.7341F * (float)(y));
			final float colorCIE1 = (-1.3515F - 1.7703F * (float)(x) + 5.9114F * (float)(y)) * colorCIE0;
			final float colorCIE2 = (0.03F - 31.4424F * (float)(x) + 30.0717F * (float)(y)) * colorCIE0;
			final float colorCIEX = 10246.121F + colorCIE1 * 187.75537F + colorCIE2 * 213.14803F;
			final float colorCIEY = 10676.695F + colorCIE1 * 192.59653F + colorCIE2 * 76.29494F;
			final float colorCIEZ = 12372.504F + colorCIE1 * 3482.8765F + colorCIE2 * -235.71611F;
			final float colorCIEYReciprocal = 1.0F / colorCIEY;
			final float colorCIER = (float)(colorCIEX * relativeLuminance * colorCIEYReciprocal);
			final float colorCIEG = (float)(relativeLuminance);
			final float colorCIEB = (float)(colorCIEZ * relativeLuminance * colorCIEYReciprocal);
			
//			Converts the CIE XYZ color to an sRGB color:
			r = 3.2410042F * colorCIER + -1.5373994F * colorCIEG + -0.49861607F * colorCIEB;
			g = -0.9692241F * colorCIER + 1.8759298F * colorCIEG + 0.041554242F * colorCIEB;
			b = 0.05563942F * colorCIER + -0.20401107F * colorCIEG + 1.0571486F * colorCIEB;
			
//			TODO: Write explanation!
			final float w = max(0.0F, -min(0.0F, min(r, min(g, b))));
			
//			TODO: Write explanation!
			r += w;
			g += w;
			b += w;
		}
		
		this.colorTemporarySamples_$private$3[0] = r;
		this.colorTemporarySamples_$private$3[1] = g;
		this.colorTemporarySamples_$private$3[2] = b;
	}
	
	private void doCalculateColorForSun(final boolean isSunActive, final float surfaceIntersectionPointX, final float surfaceIntersectionPointY, final float surfaceIntersectionPointZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ, final float albedoColorR, final float albedoColorG, final float albedoColorB) {
		float r = 0.0F;
		float g = 0.0F;
		float b = 0.0F;
		
		if(isSunActive) {
			final float sunDirectionWorldX = this.sunAndSkySunDirectionWorldX;
			final float sunDirectionWorldY = this.sunAndSkySunDirectionWorldY;
			final float sunDirectionWorldZ = this.sunAndSkySunDirectionWorldZ;
			
//			final float exponent = 500.0F;
//			final float u = nextFloat();
//			final float v = nextFloat();
//			final float phi = PI_MULTIPLIED_BY_TWO * u;
//			final float cosTheta = pow(1.0F - v, 1.0F / (exponent + 1.0F));
//			final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
//			final float x = cos(phi) * sinTheta;
//			final float y = sin(phi) * sinTheta;
//			final float z = cosTheta;
			
			final float sunRadius = 500.0F;
			final float sunRadiusSquared = sunRadius * sunRadius;
			
			final float sunOriginX = this.sunAndSkySunOriginX;
			final float sunOriginY = this.sunAndSkySunOriginY;
			final float sunOriginZ = this.sunAndSkySunOriginZ;
			
			final float directionToSunOriginX = sunOriginX - surfaceIntersectionPointX;
			final float directionToSunOriginY = sunOriginY - surfaceIntersectionPointY;
			final float directionToSunOriginZ = sunOriginZ - surfaceIntersectionPointZ;
			final float directionToSunOriginLengthSquared = directionToSunOriginX * directionToSunOriginX + directionToSunOriginY * directionToSunOriginY + directionToSunOriginZ * directionToSunOriginZ;
			
			final float u = nextFloat();
			final float v = nextFloat();
			final float sinThetaMaxSquared = sunRadiusSquared / directionToSunOriginLengthSquared;
			final float cosThetaMax = sqrt(max(0.0F, 1.0F - sinThetaMaxSquared));
			final float cosTheta = u * (cosThetaMax - 1.0F) + 1.0F;
			final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
			final float phi = PI_MULTIPLIED_BY_TWO * v;
//			final float cosTheta = (1.0F - u) + u * cosThetaMax;
//			final float sinTheta = sqrt(1.0F - cosTheta * cosTheta);
//			final float phi = v * PI_MULTIPLIED_BY_TWO;
			final float x = cos(phi) * sinTheta;
			final float y = sin(phi) * sinTheta;
			final float z = cosTheta;
			
			final float sunDirectionWorldWNormalizedX = sunDirectionWorldX;
			final float sunDirectionWorldWNormalizedY = sunDirectionWorldY;
			final float sunDirectionWorldWNormalizedZ = sunDirectionWorldZ;
			
			final float absSunDirectionWorldWNormalizedX = abs(sunDirectionWorldWNormalizedX);
			final float absSunDirectionWorldWNormalizedY = abs(sunDirectionWorldWNormalizedY);
			final float absSunDirectionWorldWNormalizedZ = abs(sunDirectionWorldWNormalizedZ);
			
			final float sunDirectionWorldVX = absSunDirectionWorldWNormalizedX < absSunDirectionWorldWNormalizedY && absSunDirectionWorldWNormalizedX < absSunDirectionWorldWNormalizedZ ? 0.0F : absSunDirectionWorldWNormalizedY < absSunDirectionWorldWNormalizedZ ? sunDirectionWorldWNormalizedZ : sunDirectionWorldWNormalizedY;
			final float sunDirectionWorldVY = absSunDirectionWorldWNormalizedX < absSunDirectionWorldWNormalizedY && absSunDirectionWorldWNormalizedX < absSunDirectionWorldWNormalizedZ ? sunDirectionWorldWNormalizedZ : absSunDirectionWorldWNormalizedY < absSunDirectionWorldWNormalizedZ ? 0.0F : -sunDirectionWorldWNormalizedX;
			final float sunDirectionWorldVZ = absSunDirectionWorldWNormalizedX < absSunDirectionWorldWNormalizedY && absSunDirectionWorldWNormalizedX < absSunDirectionWorldWNormalizedZ ? -sunDirectionWorldWNormalizedY : absSunDirectionWorldWNormalizedY < absSunDirectionWorldWNormalizedZ ? -sunDirectionWorldWNormalizedX : 0.0F;
			final float sunDirectionWorldVLengthReciprocal = rsqrt(sunDirectionWorldVX * sunDirectionWorldVX + sunDirectionWorldVY * sunDirectionWorldVY + sunDirectionWorldVZ * sunDirectionWorldVZ);
			final float sunDirectionWorldVNormalizedX = sunDirectionWorldVX * sunDirectionWorldVLengthReciprocal;
			final float sunDirectionWorldVNormalizedY = sunDirectionWorldVY * sunDirectionWorldVLengthReciprocal;
			final float sunDirectionWorldVNormalizedZ = sunDirectionWorldVZ * sunDirectionWorldVLengthReciprocal;
			
			final float sunDirectionWorldUNormalizedX = sunDirectionWorldVNormalizedY * sunDirectionWorldWNormalizedZ - sunDirectionWorldVNormalizedZ * sunDirectionWorldWNormalizedY;
			final float sunDirectionWorldUNormalizedY = sunDirectionWorldVNormalizedZ * sunDirectionWorldWNormalizedX - sunDirectionWorldVNormalizedX * sunDirectionWorldWNormalizedZ;
			final float sunDirectionWorldUNormalizedZ = sunDirectionWorldVNormalizedX * sunDirectionWorldWNormalizedY - sunDirectionWorldVNormalizedY * sunDirectionWorldWNormalizedX;
			
			final float randomSunDirectionWorldX = sunDirectionWorldUNormalizedX * x + sunDirectionWorldVNormalizedX * y + sunDirectionWorldWNormalizedX * z;
			final float randomSunDirectionWorldY = sunDirectionWorldUNormalizedY * x + sunDirectionWorldVNormalizedY * y + sunDirectionWorldWNormalizedY * z;
			final float randomSunDirectionWorldZ = sunDirectionWorldUNormalizedZ * x + sunDirectionWorldVNormalizedZ * y + sunDirectionWorldWNormalizedZ * z;
			final float randomSunDirectionWorldLengthReciprocal = rsqrt(randomSunDirectionWorldX * randomSunDirectionWorldX + randomSunDirectionWorldY * randomSunDirectionWorldY + randomSunDirectionWorldZ * randomSunDirectionWorldZ);
			final float randomSunDirectionWorldNormalizedX = randomSunDirectionWorldX * randomSunDirectionWorldLengthReciprocal;
			final float randomSunDirectionWorldNormalizedY = randomSunDirectionWorldY * randomSunDirectionWorldLengthReciprocal;
			final float randomSunDirectionWorldNormalizedZ = randomSunDirectionWorldZ * randomSunDirectionWorldLengthReciprocal;
			
			final float dotProduct0 = randomSunDirectionWorldNormalizedX * sunDirectionWorldX + randomSunDirectionWorldNormalizedY * sunDirectionWorldY + randomSunDirectionWorldNormalizedZ * sunDirectionWorldZ;
			final float dotProduct1 = randomSunDirectionWorldNormalizedX * surfaceNormalX + randomSunDirectionWorldNormalizedY * surfaceNormalY + randomSunDirectionWorldNormalizedZ * surfaceNormalZ;
			
			if(dotProduct0 > 0.0F && dotProduct1 > 0.0F) {
				final float originX = surfaceIntersectionPointX;
				final float originY = surfaceIntersectionPointY;
				final float originZ = surfaceIntersectionPointZ;
				
				final float directionX = randomSunDirectionWorldNormalizedX;
				final float directionY = randomSunDirectionWorldNormalizedY;
				final float directionZ = randomSunDirectionWorldNormalizedZ;
				
				final float t = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, true);
				
				if(t >= INFINITY - 0.0001F) {
//					final float sunColorR = 0.001F;
//					final float sunColorG = 0.001F;
//					final float sunColorB = 0.001F;
					final float sunColorR = 1.0F;
					final float sunColorG = 1.0F;
					final float sunColorB = 1.0F;
					
//					final float probabilityDensityFunctionValueReciprocal = 1.0F / (PI_MULTIPLIED_BY_TWO * (1.0F - cosThetaMax));
					final float probabilityDensityFunctionValueReciprocal = PI_RECIPROCAL;
					
					r = albedoColorR * sunColorR * dotProduct1 * probabilityDensityFunctionValueReciprocal;
					g = albedoColorG * sunColorG * dotProduct1 * probabilityDensityFunctionValueReciprocal;
					b = albedoColorB * sunColorB * dotProduct1 * probabilityDensityFunctionValueReciprocal;
				}
			}
		}
		
		this.colorTemporarySamples_$private$3[0] = r;
		this.colorTemporarySamples_$private$3[1] = g;
		this.colorTemporarySamples_$private$3[2] = b;
	}
	
	private void doCalculateSurfacePropertiesForPlane(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float aX, final float aY, final float aZ, final float bX, final float bY, final float bZ, final float cX, final float cY, final float cZ, final float surfaceNormalX, final float surfaceNormalY, final float surfaceNormalZ) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		TODO: Write explanation!
		final float absSurfaceNormalX = abs(surfaceNormalX);
		final float absSurfaceNormalY = abs(surfaceNormalY);
		final float absSurfaceNormalZ = abs(surfaceNormalZ);
		
//		TODO: Write explanation!
		final boolean isX = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ;
		final boolean isY = absSurfaceNormalY > absSurfaceNormalZ;
		
//		TODO: Write explanation!
		final float a1X = isX ? aY : isY ? aZ : aX;
		final float a1Y = isX ? aZ : isY ? aX : aY;
		
//		TODO: Write explanation!
		final float b1X = isX ? cY - aX : isY ? cZ - aX : cX - aX;
		final float b1Y = isX ? cZ - aY : isY ? cX - aY : cY - aY;
		
//		TODO: Write explanation!
		final float c1X = isX ? bY - aX : isY ? bZ - aX : bX - aX;
		final float c1Y = isX ? bZ - aY : isY ? bX - aY : bY - aY;
		
//		TODO: Write explanation!
		final float determinant = b1X * c1Y - b1Y * c1X;
		final float determinantReciprocal = 1.0F / determinant;
		
//		TODO: Write explanation!
		final float bNU = -b1Y * determinantReciprocal;
		final float bNV = b1X * determinantReciprocal;
		final float bND = (b1Y * a1X - b1X * a1Y) * determinantReciprocal;
		
//		TODO: Write explanation!
		final float cNU = c1Y * determinantReciprocal;
		final float cNV = -c1X * determinantReciprocal;
		final float cND = (c1X * a1Y - c1Y * a1X) * determinantReciprocal;
		
//		TODO: Write explanation!
		final float hU = isX ? surfaceIntersectionPointY : isY ? surfaceIntersectionPointZ : surfaceIntersectionPointX;
		final float hV = isX ? surfaceIntersectionPointZ : isY ? surfaceIntersectionPointX : surfaceIntersectionPointY;
		
//		Calculate the UV-coordinates:
		final float u = hU * bNU + hV * bNV + bND;
		final float v = hU * cNU + hV * cNV + cND;
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Calculate some offsets:
		final int offsetIntersectionOrthoNormalBasisU = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_U;
		final int offsetIntersectionOrthoNormalBasisV = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_V;
		final int offsetIntersectionOrthoNormalBasisW = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_W;
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		Calculate Ortho Normal Basis W:
		final float orthoNormalBasisWX = surfaceNormalX;
		final float orthoNormalBasisWY = surfaceNormalY;
		final float orthoNormalBasisWZ = surfaceNormalZ;
		
//		Calculate Ortho Normal Basis V:
		final float orthoNormalBasisV0X = absSurfaceNormalX < absSurfaceNormalY && absSurfaceNormalX < absSurfaceNormalZ ? 0.0F : absSurfaceNormalY < absSurfaceNormalZ ? orthoNormalBasisWZ : orthoNormalBasisWY;
		final float orthoNormalBasisV0Y = absSurfaceNormalX < absSurfaceNormalY && absSurfaceNormalX < absSurfaceNormalZ ? orthoNormalBasisWZ : absSurfaceNormalY < absSurfaceNormalZ ? 0.0F : -surfaceNormalX;
		final float orthoNormalBasisV0Z = absSurfaceNormalX < absSurfaceNormalY && absSurfaceNormalX < absSurfaceNormalZ ? -orthoNormalBasisWY : absSurfaceNormalY < absSurfaceNormalZ ? -surfaceNormalX : 0.0F;
		final float orthoNormalBasisV0LengthReciprocal = rsqrt(orthoNormalBasisV0X * orthoNormalBasisV0X + orthoNormalBasisV0Y * orthoNormalBasisV0Y + orthoNormalBasisV0Z * orthoNormalBasisV0Z);
		final float orthoNormalBasisV1X = orthoNormalBasisV0X * orthoNormalBasisV0LengthReciprocal;
		final float orthoNormalBasisV1Y = orthoNormalBasisV0Y * orthoNormalBasisV0LengthReciprocal;
		final float orthoNormalBasisV1Z = orthoNormalBasisV0Z * orthoNormalBasisV0LengthReciprocal;
		
//		Calculate Ortho Normal Basis U:
		final float orthoNormalBasisUX = orthoNormalBasisV1Y * orthoNormalBasisWZ - orthoNormalBasisV1Z * orthoNormalBasisWY;
		final float orthoNormalBasisUY = orthoNormalBasisV1Z * orthoNormalBasisWX - orthoNormalBasisV1X * orthoNormalBasisWZ;
		final float orthoNormalBasisUZ = orthoNormalBasisV1X * orthoNormalBasisWY - orthoNormalBasisV1Y * orthoNormalBasisWX;
		
//		Update the intersections array:
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 0] = orthoNormalBasisUX;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 1] = orthoNormalBasisUY;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 2] = orthoNormalBasisUZ;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 0] = orthoNormalBasisV1X;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 1] = orthoNormalBasisV1Y;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 2] = orthoNormalBasisV1Z;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 0] = orthoNormalBasisWX;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 1] = orthoNormalBasisWY;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 2] = orthoNormalBasisWZ;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormalX;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormalY;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormalZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormalX;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormalY;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormalZ;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 0] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	private void doCalculateSurfacePropertiesForSphere(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float positionX, final float positionY, final float positionZ) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Calculate the surface normal:
		final float surfaceNormal0X = surfaceIntersectionPointX - positionX;
		final float surfaceNormal0Y = surfaceIntersectionPointY - positionY;
		final float surfaceNormal0Z = surfaceIntersectionPointZ - positionZ;
		final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
		final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
		final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
		final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
		
//		Calculate the UV-coordinates:
		final float u = 0.5F + atan2(-surfaceNormal1Z, -surfaceNormal1X) * PI_MULTIPLIED_BY_TWO_RECIPROCAL;
		final float v = 0.5F - asinpi(-surfaceNormal1Y);
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Retrieve offsets for the surface intersection point, surface normal and UV-coordinates:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		Update the intersections array:
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormal1X;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormal1X;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 0] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
	}
	
	@SuppressWarnings("unused")
	private void doCalculateSurfacePropertiesForTerrain(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float frequency, final float gain, final float minimum, final float maximum, final int octaves) {
		final float scale = 10.0F;
		final float scaleReciprocal = 1.0F / scale;
		
		final float surfaceIntersectionPointX = originX * scaleReciprocal + directionX * distance;
		final float surfaceIntersectionPointY = originY * scaleReciprocal + directionY * distance;
		final float surfaceIntersectionPointZ = originZ * scaleReciprocal + directionZ * distance;
		
		final float epsilon = 0.02F;
		
//		final float surfaceNormalX = simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX - epsilon, surfaceIntersectionPointZ) - simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX + epsilon, surfaceIntersectionPointZ);
//		final float surfaceNormalY = 2.0F * epsilon;
//		final float surfaceNormalZ = simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX, surfaceIntersectionPointZ - epsilon) - simplexFractionalBrownianMotionXY(frequency, gain, minimum, maximum, octaves, surfaceIntersectionPointX, surfaceIntersectionPointZ + epsilon);
//		final float surfaceNormalX = (simplexFractalXY(getAmplitude(), getFrequency(), getGain(), getLacunarity(), getOctaves(), surfaceIntersectionPointX - epsilon, surfaceIntersectionPointZ) + 1.0F) / 2.0F - (simplexFractalXY(getAmplitude(), getFrequency(), getGain(), getLacunarity(), getOctaves(), surfaceIntersectionPointX + epsilon, surfaceIntersectionPointZ) + 1.0F) / 2.0F;
//		final float surfaceNormalY = 2.0F * epsilon;
//		final float surfaceNormalZ = (simplexFractalXY(getAmplitude(), getFrequency(), getGain(), getLacunarity(), getOctaves(), surfaceIntersectionPointX, surfaceIntersectionPointZ - epsilon) + 1.0F) / 2.0F - (simplexFractalXY(getAmplitude(), getFrequency(), getGain(), getLacunarity(), getOctaves(), surfaceIntersectionPointX, surfaceIntersectionPointZ + epsilon) + 1.0F) / 2.0F;
		final float surfaceNormalX = (sin(surfaceIntersectionPointX - epsilon) * sin(surfaceIntersectionPointZ)) - (sin(surfaceIntersectionPointX + epsilon) * sin(surfaceIntersectionPointZ));
		final float surfaceNormalY = 2.0F * epsilon;
		final float surfaceNormalZ = (sin(surfaceIntersectionPointX) * sin(surfaceIntersectionPointZ - epsilon)) - (sin(surfaceIntersectionPointX) * sin(surfaceIntersectionPointZ + epsilon));
		final float surfaceNormalLengthReciprocal = rsqrt(surfaceNormalX * surfaceNormalX + surfaceNormalY * surfaceNormalY + surfaceNormalZ * surfaceNormalZ);
		final float surfaceNormalNormalizedX = surfaceNormalX * surfaceNormalLengthReciprocal;
		final float surfaceNormalNormalizedY = surfaceNormalY * surfaceNormalLengthReciprocal;
		final float surfaceNormalNormalizedZ = surfaceNormalZ * surfaceNormalLengthReciprocal;
		
		final float u = surfaceIntersectionPointX;
		final float v = surfaceIntersectionPointZ;
		
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 0] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormalNormalizedX;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormalNormalizedY;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormalNormalizedZ;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormalNormalizedX;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormalNormalizedY;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormalNormalizedZ;
	}
	
	private void doCalculateSurfacePropertiesForTriangle(final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final float distance, final float aPositionX, final float aPositionY, final float aPositionZ, final float bPositionX, final float bPositionY, final float bPositionZ, final float cPositionX, final float cPositionY, final float cPositionZ, final float aSurfaceNormalX, final float aSurfaceNormalY, final float aSurfaceNormalZ, final float bSurfaceNormalX, final float bSurfaceNormalY, final float bSurfaceNormalZ, final float cSurfaceNormalX, final float cSurfaceNormalY, final float cSurfaceNormalZ, final float aSurfaceTangentX, final float aSurfaceTangentY, final float aSurfaceTangentZ, final float bSurfaceTangentX, final float bSurfaceTangentY, final float bSurfaceTangentZ, final float cSurfaceTangentX, final float cSurfaceTangentY, final float cSurfaceTangentZ, final float aTextureCoordinatesU, final float aTextureCoordinatesV, final float bTextureCoordinatesU, final float bTextureCoordinatesV, final float cTextureCoordinatesU, final float cTextureCoordinatesV) {
//		Calculate the surface intersection point:
		final float surfaceIntersectionPointX = originX + directionX * distance;
		final float surfaceIntersectionPointY = originY + directionY * distance;
		final float surfaceIntersectionPointZ = originZ + directionZ * distance;
		
//		Calculate the Barycentric-coordinates:
		final float edgeABX = bPositionX - aPositionX;
		final float edgeABY = bPositionY - aPositionY;
		final float edgeABZ = bPositionZ - aPositionZ;
		final float edgeACX = cPositionX - aPositionX;
		final float edgeACY = cPositionY - aPositionY;
		final float edgeACZ = cPositionZ - aPositionZ;
		
		final float v0X = directionY * edgeACZ - directionZ * edgeACY;
		final float v0Y = directionZ * edgeACX - directionX * edgeACZ;
		final float v0Z = directionX * edgeACY - directionY * edgeACX;
		final float v1X = originX - aPositionX;
		final float v1Y = originY - aPositionY;
		final float v1Z = originZ - aPositionZ;
		final float v2X = v1Y * edgeABZ - v1Z * edgeABY;
		final float v2Y = v1Z * edgeABX - v1X * edgeABZ;
		final float v2Z = v1X * edgeABY - v1Y * edgeABX;
		
		final float determinant = edgeABX * v0X + edgeABY * v0Y + edgeABZ * v0Z;
		final float determinantReciprocal = 1.0F / determinant;
		
		final float barycentricU = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;
		final float barycentricV = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;
		final float barycentricW = 1.0F - barycentricU - barycentricV;
		
//		Calculate the UV-coordinates:
		final float u = barycentricW * aTextureCoordinatesU + barycentricU * bTextureCoordinatesU + barycentricV * cTextureCoordinatesU;
		final float v = barycentricW * aTextureCoordinatesV + barycentricU * bTextureCoordinatesV + barycentricV * cTextureCoordinatesV;
//		final float u = barycentricU * aTextureCoordinatesU + barycentricV * bTextureCoordinatesU + barycentricW * cTextureCoordinatesU;
//		final float v = barycentricU * aTextureCoordinatesV + barycentricV * bTextureCoordinatesV + barycentricW * cTextureCoordinatesV;
		
//		Get the intersections offset:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Calculate some offsets:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionSurfaceTangent = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_TANGENT;
		final int offsetIntersectionUVCoordinates = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_TEXTURE_COORDINATES;
		
//		Update the intersections array:
		this.intersections_$local$[intersectionsOffset] = distance;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointX;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 0] = u;
		this.intersections_$local$[offsetIntersectionUVCoordinates + 1] = v;
		
		float surfaceNormal0X = 0.0F;
		float surfaceNormal0Y = 0.0F;
		float surfaceNormal0Z = 0.0F;
		
		float surfaceTangent0X = 0.0F;
		float surfaceTangent0Y = 0.0F;
		float surfaceTangent0Z = 0.0F;
		
		if(super.shaderType == SHADER_TYPE_FLAT) {
//			Calculate the surface normal for Flat Shading:
			surfaceNormal0X = edgeABY * edgeACZ - edgeABZ * edgeACY;
			surfaceNormal0Y = edgeABZ * edgeACX - edgeABX * edgeACZ;
			surfaceNormal0Z = edgeABX * edgeACY - edgeABY * edgeACX;
		} else if(super.shaderType == SHADER_TYPE_GOURAUD) {
//			Calculate the surface normal for Gouraud Shading:
			surfaceNormal0X = aSurfaceNormalX * barycentricW + bSurfaceNormalX * barycentricU + cSurfaceNormalX * barycentricV;
			surfaceNormal0Y = aSurfaceNormalY * barycentricW + bSurfaceNormalY * barycentricU + cSurfaceNormalY * barycentricV;
			surfaceNormal0Z = aSurfaceNormalZ * barycentricW + bSurfaceNormalZ * barycentricU + cSurfaceNormalZ * barycentricV;
			
			surfaceTangent0X = aSurfaceTangentX * barycentricW + bSurfaceTangentX * barycentricU + cSurfaceTangentX * barycentricV;
			surfaceTangent0Y = aSurfaceTangentY * barycentricW + bSurfaceTangentY * barycentricU + cSurfaceTangentY * barycentricV;
			surfaceTangent0Z = aSurfaceTangentZ * barycentricW + bSurfaceTangentZ * barycentricU + cSurfaceTangentZ * barycentricV;
			
//			surfaceNormal0X = aSurfaceNormalX * barycentricU + bSurfaceNormalX * barycentricV + cSurfaceNormalX * barycentricW;
//			surfaceNormal0Y = aSurfaceNormalY * barycentricU + bSurfaceNormalY * barycentricV + cSurfaceNormalY * barycentricW;
//			surfaceNormal0Z = aSurfaceNormalZ * barycentricU + bSurfaceNormalZ * barycentricV + cSurfaceNormalZ * barycentricW;
			
//			surfaceTangent0X = aSurfaceTangentX * barycentricU + bSurfaceTangentX * barycentricV + cSurfaceTangentX * barycentricW;
//			surfaceTangent0Y = aSurfaceTangentY * barycentricU + bSurfaceTangentY * barycentricV + cSurfaceTangentY * barycentricW;
//			surfaceTangent0Z = aSurfaceTangentZ * barycentricU + bSurfaceTangentZ * barycentricV + cSurfaceTangentZ * barycentricW;
		}
		
		final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
		
		final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
		final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
		final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
		
		final float surfaceTangent0LengthReciprocal = rsqrt(surfaceTangent0X * surfaceTangent0X + surfaceTangent0Y * surfaceTangent0Y + surfaceTangent0Z * surfaceTangent0Z);
		
		final float surfaceTangent1X = surfaceTangent0X * surfaceTangent0LengthReciprocal;
		final float surfaceTangent1Y = surfaceTangent0Y * surfaceTangent0LengthReciprocal;
		final float surfaceTangent1Z = surfaceTangent0Z * surfaceTangent0LengthReciprocal;
		
//		Update the intersections array based on Flat Shading:
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormal1X;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormal1X;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
		this.intersections_$local$[offsetIntersectionSurfaceTangent + 0] = surfaceTangent1X;
		this.intersections_$local$[offsetIntersectionSurfaceTangent + 1] = surfaceTangent1Y;
		this.intersections_$local$[offsetIntersectionSurfaceTangent + 2] = surfaceTangent1Z;
	}
	
	@NoCL
	private void doNoOpenCL() {
		this.colorTemporarySamples_$private$3 = this.colorTemporarySamplesThreadLocal.get();
		this.rays_$private$6 = this.raysThreadLocal.get();
	}
	
	private void doPerformNormalMappingViaNoise(final int primitivesOffset) {
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
//		Retrieve the offset to the surfaces array:
		final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
		
//		Retrieve the noise amount from the current shape:
		final float amount = this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_NOISE_AMOUNT];
		
//		Retrieve the noise scale from the current shape:
		final float scale = this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_NOISE_SCALE];
		
//		Check that the noise amount and noise scale are greater than 0.0:
		if(super.rendererNormalMapping == BOOLEAN_TRUE && amount > 0.0F && scale > 0.0F) {
//			Retrieve the surface intersection point and the surface normal from the current shape:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the X-, Y- and Z-component values from the surface intersection point:
			final float x0 = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint];
			final float y0 = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float z0 = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Compute the reciprocal of the noise scale:
			final float scaleReciprocal = 1.0F / scale;
			
//			Scale the X-, Y- and Z-component values:
			final float x1 = x0 * scaleReciprocal;
			final float y1 = y0 * scaleReciprocal;
			final float z1 = z0 * scaleReciprocal;
			
//			Compute the noise given the X-, Y- and Z-component values:
			final float noiseX = simplexFractionalBrownianMotionXYZ(scale, 0.5F, -0.26F, 0.26F, 16, x1, y1, z1);
			final float noiseY = simplexFractionalBrownianMotionXYZ(scale, 0.5F, -0.26F, 0.26F, 16, y1, z1, x1);
			final float noiseZ = simplexFractionalBrownianMotionXYZ(scale, 0.5F, -0.26F, 0.26F, 16, z1, x1, y1);
			
//			Calculate the surface normal:
			final float surfaceNormal0X = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float surfaceNormal0Y = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormal0Z = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			final float surfaceNormal1X = surfaceNormal0X + noiseX * amount;
			final float surfaceNormal1Y = surfaceNormal0Y + noiseY * amount;
			final float surfaceNormal1Z = surfaceNormal0Z + noiseZ * amount;
			final float surfaceNormal1LengthReciprocal = rsqrt(surfaceNormal1X * surfaceNormal1X + surfaceNormal1Y * surfaceNormal1Y + surfaceNormal1Z * surfaceNormal1Z);
			final float surfaceNormal2X = surfaceNormal1X * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Y = surfaceNormal1Y * surfaceNormal1LengthReciprocal;
			final float surfaceNormal2Z = surfaceNormal1Z * surfaceNormal1LengthReciprocal;
			
//			Update the intersections array with the new surface normal:
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading] = surfaceNormal2X;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal2Y;
			this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal2Z;
		}
	}
	
	private void doPerformNormalMappingViaImageTexture(final int primitivesOffset) {
//		Retrieve the offset in the textures array and the type of the texture:
		final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
		final int texturesOffset = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_NORMAL_OFFSET]);
		final int textureType = (int)(this.sceneTextures_$constant$[texturesOffset]);
		
//		Get the intersections offset:
		final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;
		
		if(super.rendererNormalMapping == BOOLEAN_TRUE && textureType == ImageTexture.TYPE) {
//			Calculate the texture color:
			final int rGB = doGetTextureColorFromImageTextureSimple(texturesOffset);
			
//			Retrieve the R-, G- and B-component values:
//			final float r = 2.0F * (((rGB >> 16) & 0xFF) * COLOR_RECIPROCAL) - 1.0F;
//			final float g = 2.0F * (((rGB >>  8) & 0xFF) * COLOR_RECIPROCAL) - 1.0F;
//			final float b = 2.0F * (((rGB >>  0) & 0xFF) * COLOR_RECIPROCAL) - 1.0F;
			final float r = ((((rGB >> 16) & 0xFF) * COLOR_RECIPROCAL) - 0.5F) * 2.0F;
			final float g = ((((rGB >>  8) & 0xFF) * COLOR_RECIPROCAL) - 0.5F) * 2.0F;
			final float b = ((((rGB >>  0) & 0xFF) * COLOR_RECIPROCAL) - 0.0F) * 2.0F;
			
//			Retrieve the offset of the surface normal in the intersections array:
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the orthonormal basis W-vector:
			final float wX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float wY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float wZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
			final int type = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SHAPE_TYPE];
			
			if(type == Sphere.TYPE) {
				final float v0X = -2.0F * PI * wY;//wZ?
				final float v0Y = 2.0F * PI * wX;
				final float v0Z = 0.0F;
				
				final float u0X = v0Y * wZ - v0Z * wY;
				final float u0Y = v0Z * wX - v0X * wZ;
				final float u0Z = v0X * wY - v0Y * wX;
				final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
				final float u1X = u0X * u0LengthReciprocal;
				final float u1Y = u0Y * u0LengthReciprocal;
				final float u1Z = u0Z * u0LengthReciprocal;
				
				final float v1X = wY * u1Z - wZ * u1Y;
				final float v1Y = wZ * u1X - wX * u1Z;
				final float v1Z = wX * u1Y - wY * u1X;
				final float v1LengthReciprocal = rsqrt(v1X * v1X + v1Y * v1Y + v1Z * v1Z);
				final float v2X = v1X * v1LengthReciprocal;
				final float v2Y = v1Y * v1LengthReciprocal;
				final float v2Z = v1Z * v1LengthReciprocal;
				
				final float surfaceNormal0X = r * u1X + g * v2X + b * wX;
				final float surfaceNormal0Y = r * u1Y + g * v2Y + b * wY;
				final float surfaceNormal0Z = r * u1Z + g * v2Z + b * wZ;
				final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
				final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
				
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormal1X;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
			} else if(type == Triangle.TYPE) {
				final int offsetIntersectionSurfaceTangent = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_TANGENT;
				
				final float uX = this.intersections_$local$[offsetIntersectionSurfaceTangent + 0];
				final float uY = this.intersections_$local$[offsetIntersectionSurfaceTangent + 1];
				final float uZ = this.intersections_$local$[offsetIntersectionSurfaceTangent + 2];
				
				final float v0X = wY * uZ - wZ * uY;
				final float v0Y = wZ * uX - wX * uZ;
				final float v0Z = wX * uY - wY * uX;
				final float v0LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
				final float v1X = v0X * v0LengthReciprocal;
				final float v1Y = v0Y * v0LengthReciprocal;
				final float v1Z = v0Z * v0LengthReciprocal;
				
				final float surfaceNormal0X = r * uX + g * v1X + b * wX;
				final float surfaceNormal0Y = r * uY + g * v1Y + b * wY;
				final float surfaceNormal0Z = r * uZ + g * v1Z + b * wZ;
				final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
				final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
				
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormal1X;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
			} else {
//				Calculate the absolute values of the orthonormal basis W-vector:
				final float absWX = abs(wX);
				final float absWY = abs(wY);
				final float absWZ = abs(wZ);
				
//				Check the direction of the orthonormal basis:
				final boolean isWX = absWX < absWY && absWX < absWZ;
				final boolean isWY = absWY < absWZ;
				
//				Calculate the orthonormal basis V-vector:
				final float v0X = isWX ? 0.0F : isWY ? wZ : wY;
				final float v0Y = isWX ? wZ : isWY ? 0.0F : -wX;
				final float v0Z = isWX ? -wY : isWY ? -wX : 0.0F;
				final float v0LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
				final float v1X = v0X * v0LengthReciprocal;
				final float v1Y = v0Y * v0LengthReciprocal;
				final float v1Z = v0Z * v0LengthReciprocal;
				
//				Calculate the orthonormal basis U-vector:
				final float u0X = v1Y * wZ - v1Z * wY;
				final float u0Y = v1Z * wX - v1X * wZ;
				final float u0Z = v1X * wY - v1Y * wX;
				final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
				final float u1X = u0X * u0LengthReciprocal;
				final float u1Y = u0Y * u0LengthReciprocal;
				final float u1Z = u0Z * u0LengthReciprocal;
				
//				Calculate the new surface normal:
				final float surfaceNormal0X = r * u1X + g * v1X + b * wX;
				final float surfaceNormal0Y = r * u1Y + g * v1Y + b * wY;
				final float surfaceNormal0Z = r * u1Z + g * v1Z + b * wZ;
				final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);
				final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;
				final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;
				
//				Update the intersections array:
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormal1X;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormal1Y;
				this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormal1Z;
			}
		}
	}
	
	private void doRenderSurfaceNormals(final boolean isSkyActive) {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
		this.primitiveOffsets[getGlobalId()] = primitivesOffset;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance == INFINITY || primitivesOffset == -1) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR = this.colorTemporarySamples_$private$3[0];
			pixelColorG = this.colorTemporarySamples_$private$3[1];
			pixelColorB = this.colorTemporarySamples_$private$3[2];
			
//			Update the current pixel color:
			filmAddColor(pixelColorR, pixelColorG, pixelColorB);
			
			return;
		}
		
//		Retrieve the offset of the surface normal in the intersections array:
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		
//		Retrieve the surface normal from the intersections array:
		final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
		final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
		final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
		
		pixelColorR = (surfaceNormalShadingX + 1.0F) * 0.5F;
		pixelColorG = (surfaceNormalShadingY + 1.0F) * 0.5F;
		pixelColorB = (surfaceNormalShadingZ + 1.0F) * 0.5F;
		
//		Update the current pixel color:
		filmAddColor(pixelColorR, pixelColorG, pixelColorB);
	}
	
	private void doRenderWireframes() {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
		final float lineWidth = PI * 0.5F / 4096.0F;
		final float lineWidthCos = cos(lineWidth);
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance != INFINITY && primitivesOffset != -1) {
//			Retrieve the type and offset of the shape:
			final int shapeType = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_TYPE]);
			final int shapeOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPE_OFFSET]);
			
			if(shapeType == Triangle.TYPE) {
//				Retrieve the offsets of the surface intersection point and the points A, B and C in the intersections array:
				final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
				final int offsetA = this.sceneTriangles_$constant$[shapeOffset + Triangle.RELATIVE_OFFSET_A_POSITION_OFFSET];
				final int offsetB = this.sceneTriangles_$constant$[shapeOffset + Triangle.RELATIVE_OFFSET_B_POSITION_OFFSET];
				final int offsetC = this.sceneTriangles_$constant$[shapeOffset + Triangle.RELATIVE_OFFSET_C_POSITION_OFFSET];
				
//				Retrieve the camera orthonormal basis:
				final float cameraOrthoNormalBasisUX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X];
				final float cameraOrthoNormalBasisUY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y];
				final float cameraOrthoNormalBasisUZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z];
				final float cameraOrthoNormalBasisVX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X];
				final float cameraOrthoNormalBasisVY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y];
				final float cameraOrthoNormalBasisVZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z];
				final float cameraOrthoNormalBasisWX = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X];
				final float cameraOrthoNormalBasisWY = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y];
				final float cameraOrthoNormalBasisWZ = this.sceneCamera_$constant$[Camera.ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z];
				
//				Retrieve the surface intersection point from the intersections array:
				final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
				final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
				final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
				final float surfaceIntersectionPointCameraX = surfaceIntersectionPointX * cameraOrthoNormalBasisUX + surfaceIntersectionPointY * cameraOrthoNormalBasisUY + surfaceIntersectionPointZ * cameraOrthoNormalBasisUZ;
				final float surfaceIntersectionPointCameraY = surfaceIntersectionPointX * cameraOrthoNormalBasisVX + surfaceIntersectionPointY * cameraOrthoNormalBasisVY + surfaceIntersectionPointZ * cameraOrthoNormalBasisVZ;
				final float surfaceIntersectionPointCameraZ = surfaceIntersectionPointX * cameraOrthoNormalBasisWX + surfaceIntersectionPointY * cameraOrthoNormalBasisWY + surfaceIntersectionPointZ * cameraOrthoNormalBasisWZ;
				final float surfaceIntersectionPointCameraN = rsqrt(surfaceIntersectionPointCameraX * surfaceIntersectionPointCameraX + surfaceIntersectionPointCameraY * surfaceIntersectionPointCameraY + surfaceIntersectionPointCameraZ * surfaceIntersectionPointCameraZ);
				
//				Retrieve point A of the triangle:
				final float aX = this.scenePoint3Fs_$constant$[offsetA + 0];
				final float aY = this.scenePoint3Fs_$constant$[offsetA + 1];
				final float aZ = this.scenePoint3Fs_$constant$[offsetA + 2];
				final float aCameraX = aX * cameraOrthoNormalBasisUX + aY * cameraOrthoNormalBasisUY + aZ * cameraOrthoNormalBasisUZ;
				final float aCameraY = aX * cameraOrthoNormalBasisVX + aY * cameraOrthoNormalBasisVY + aZ * cameraOrthoNormalBasisVZ;
				final float aCameraZ = aX * cameraOrthoNormalBasisWX + aY * cameraOrthoNormalBasisWY + aZ * cameraOrthoNormalBasisWZ;
				
//				Retrieve point B of the triangle:
				final float bX = this.scenePoint3Fs_$constant$[offsetB + 0];
				final float bY = this.scenePoint3Fs_$constant$[offsetB + 1];
				final float bZ = this.scenePoint3Fs_$constant$[offsetB + 2];
				final float bCameraX = bX * cameraOrthoNormalBasisUX + bY * cameraOrthoNormalBasisUY + bZ * cameraOrthoNormalBasisUZ;
				final float bCameraY = bX * cameraOrthoNormalBasisVX + bY * cameraOrthoNormalBasisVY + bZ * cameraOrthoNormalBasisVZ;
				final float bCameraZ = bX * cameraOrthoNormalBasisWX + bY * cameraOrthoNormalBasisWY + bZ * cameraOrthoNormalBasisWZ;
				
//				Retrieve point C of the triangle:
				final float cX = this.scenePoint3Fs_$constant$[offsetC + 0];
				final float cY = this.scenePoint3Fs_$constant$[offsetC + 1];
				final float cZ = this.scenePoint3Fs_$constant$[offsetC + 2];
				final float cCameraX = cX * cameraOrthoNormalBasisUX + cY * cameraOrthoNormalBasisUY + cZ * cameraOrthoNormalBasisUZ;
				final float cCameraY = cX * cameraOrthoNormalBasisVX + cY * cameraOrthoNormalBasisVY + cZ * cameraOrthoNormalBasisVZ;
				final float cCameraZ = cX * cameraOrthoNormalBasisWX + cY * cameraOrthoNormalBasisWY + cZ * cameraOrthoNormalBasisWZ;
				
				final float distanceSquaredAC = ((aCameraX - cCameraX) * (aCameraX - cCameraX)) + ((aCameraY - cCameraY) * (aCameraY - cCameraY)) + ((aCameraZ - cCameraZ) * (aCameraZ - cCameraZ));
				final float distanceSquaredBA = ((bCameraX - aCameraX) * (bCameraX - aCameraX)) + ((bCameraY - aCameraY) * (bCameraY - aCameraY)) + ((bCameraZ - aCameraZ) * (bCameraZ - aCameraZ));
				final float distanceSquaredCB = ((cCameraX - bCameraX) * (cCameraX - bCameraX)) + ((cCameraY - bCameraY) * (cCameraY - bCameraY)) + ((cCameraZ - bCameraZ) * (cCameraZ - bCameraZ));
				
				final float tAC0 = ((surfaceIntersectionPointCameraX - aCameraX) * (cCameraX - aCameraX)) + ((surfaceIntersectionPointCameraY - aCameraY) * (cCameraY - aCameraY)) + ((surfaceIntersectionPointCameraZ - aCameraZ) * (cCameraZ - aCameraZ));
				final float tAC1 = tAC0 / distanceSquaredAC;
				final float tBA0 = ((surfaceIntersectionPointCameraX - bCameraX) * (aCameraX - bCameraX)) + ((surfaceIntersectionPointCameraY - bCameraY) * (aCameraY - bCameraY)) + ((surfaceIntersectionPointCameraZ - bCameraZ) * (aCameraZ - bCameraZ));
				final float tBA1 = tBA0 / distanceSquaredBA;
				final float tCB0 = ((surfaceIntersectionPointCameraX - cCameraX) * (bCameraX - cCameraX)) + ((surfaceIntersectionPointCameraY - cCameraY) * (bCameraY - cCameraY)) + ((surfaceIntersectionPointCameraZ - cCameraZ) * (bCameraZ - cCameraZ));
				final float tCB1 = tCB0 / distanceSquaredCB;
				
				final float projectionACX = (1.0F - tAC1) * aCameraX + tAC1 * cCameraX;
				final float projectionACY = (1.0F - tAC1) * aCameraY + tAC1 * cCameraY;
				final float projectionACZ = (1.0F - tAC1) * aCameraZ + tAC1 * cCameraZ;
				final float projectionACN = rsqrt(projectionACX * projectionACX + projectionACY * projectionACY + projectionACZ * projectionACZ);
				final float projectionBAX = (1.0F - tBA1) * bCameraX + tBA1 * aCameraX;
				final float projectionBAY = (1.0F - tBA1) * bCameraY + tBA1 * aCameraY;
				final float projectionBAZ = (1.0F - tBA1) * bCameraZ + tBA1 * aCameraZ;
				final float projectionBAN = rsqrt(projectionBAX * projectionBAX + projectionBAY * projectionBAY + projectionBAZ * projectionBAZ);
				final float projectionCBX = (1.0F - tCB1) * cCameraX + tCB1 * bCameraX;
				final float projectionCBY = (1.0F - tCB1) * cCameraY + tCB1 * bCameraY;
				final float projectionCBZ = (1.0F - tCB1) * cCameraZ + tCB1 * bCameraZ;
				final float projectionCBN = rsqrt(projectionCBX * projectionCBX + projectionCBY * projectionCBY + projectionCBZ * projectionCBZ);
				
				final float dotProductAC = projectionACX * surfaceIntersectionPointCameraX + projectionACY * surfaceIntersectionPointCameraY + projectionACZ * surfaceIntersectionPointCameraZ;
				final float dotProductBA = projectionBAX * surfaceIntersectionPointCameraX + projectionBAY * surfaceIntersectionPointCameraY + projectionBAZ * surfaceIntersectionPointCameraZ;
				final float dotProductCB = projectionCBX * surfaceIntersectionPointCameraX + projectionCBY * surfaceIntersectionPointCameraY + projectionCBZ * surfaceIntersectionPointCameraZ;
				
				if(dotProductAC * projectionACN * surfaceIntersectionPointCameraN >= lineWidthCos || dotProductBA * projectionBAN * surfaceIntersectionPointCameraN >= lineWidthCos || dotProductCB * projectionCBN * surfaceIntersectionPointCameraN >= lineWidthCos) {
//					Update the current pixel color:
					filmSetColor(0.0F, 0.0F, 0.0F);
				}
			}
		}
	}
	
	private void doRenderWithAmbientOcclusion(final float brightR, final float brightG, final float brightB, final float darkR, final float darkG, final float darkB) {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
		this.primitiveOffsets[getGlobalId()] = primitivesOffset;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance != INFINITY && primitivesOffset != -1) {
//			Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the surface intersection point from the intersections array:
			final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint];
			final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal from the intersections array:
			final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading];
			final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
			final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
			
//			Check if the surface normal is correctly oriented:
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
//			Retrieve the correctly oriented surface normal:
			final float w0X = isCorrectlyOriented ? surfaceNormalShadingX : -surfaceNormalShadingX;
			final float w0Y = isCorrectlyOriented ? surfaceNormalShadingY : -surfaceNormalShadingY;
			final float w0Z = isCorrectlyOriented ? surfaceNormalShadingZ : -surfaceNormalShadingZ;
			
//			Calculate the orthonormal basis W vector:
			final float w0LengthReciprocal = rsqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);
			final float w1X = w0X * w0LengthReciprocal;
			final float w1Y = w0Y * w0LengthReciprocal;
			final float w1Z = w0Z * w0LengthReciprocal;
			
//			Check if the direction is the Y-direction:
			final boolean isY = abs(w1X) > 0.1F;
			
//			Calculate the orthonormal basis U vector:
			final float u0X = isY ? 0.0F : 1.0F;
			final float u0Y = isY ? 1.0F : 0.0F;
			final float u1X = u0Y * w1Z;
			final float u1Y = -(u0X * w1Z);
			final float u1Z = u0X * w1Y - u0Y * w1X;
			final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);
			final float u2X = u1X * u1LengthReciprocal;
			final float u2Y = u1Y * u1LengthReciprocal;
			final float u2Z = u1Z * u1LengthReciprocal;
			
//			Calculate the orthonormal basis V vector:
			final float v0X = w1Y * u2Z - w1Z * u2Y;
			final float v0Y = w1Z * u2X - w1X * u2Z;
			final float v0Z = w1X * u2Y - w1Y * u2X;
			
			final float xi = nextFloat();
			final float xj = nextFloat();
			final float phi = PI_MULTIPLIED_BY_TWO * xi;
			final float cosPhi = cos(phi);
			final float sinPhi = sin(phi);
			final float sinTheta = sqrt(xj);
			final float cosTheta = sqrt(1.0F - xj);
			
			final float direction0X = cosPhi * sinTheta;
			final float direction0Y = sinPhi * sinTheta;
			final float direction0Z = cosTheta;
			final float direction1X = direction0X * u2X + direction0Y * v0X + direction0Z * w1X;
			final float direction1Y = direction0X * u2Y + direction0Y * v0Y + direction0Z * w1Y;
			final float direction1Z = direction0X * u2Z + direction0Y * v0Z + direction0Z * w1Z;
			final float direction1LengthReciprocal = rsqrt(direction1X * direction1X + direction1Y * direction1Y + direction1Z * direction1Z);
			
			originX = surfaceIntersectionPointX + w1X * 0.01F;
			originY = surfaceIntersectionPointY + w1Y * 0.01F;
			originZ = surfaceIntersectionPointZ + w1Z * 0.01F;
			
			directionX = direction1X * direction1LengthReciprocal;
			directionY = direction1Y * direction1LengthReciprocal;
			directionZ = direction1Z * direction1LengthReciprocal;
			
			final float t = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
			
			final boolean isHit = t < this.rendererAOMaximumDistance;
			
			final float r = isHit ? brightR : darkR;
			final float g = isHit ? brightG : darkG;
			final float b = isHit ? brightB : darkB;
			
			pixelColorR += (1.0F - r) * brightR + r * darkR;
			pixelColorG += (1.0F - g) * brightG + g * darkG;
			pixelColorB += (1.0F - b) * brightB + b * darkB;
		}
		
//		Update the current pixel color:
		filmAddColor(pixelColorR, pixelColorG, pixelColorB);
	}
	
	private void doRenderWithPathTracer(final boolean isSkyActive, final boolean isSunActive) {
//		Retrieve the maximum depth allowed and the depth at which to use Russian Roulette to test for path termination:
		final int depthMaximum = this.rendererPTRayDepthMaximum;
		final int depthRussianRoulette = this.rendererPTRayDepthRussianRoulette;
		
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Initialize the radiance multiplier to white:
		float radianceMultiplierR = 1.0F;
		float radianceMultiplierG = 1.0F;
		float radianceMultiplierB = 1.0F;
		
//		Run the following do-while-loop as long as the current depth is less than the maximum depth and Russian Roulette does not terminate:
		do {
//			Perform an intersection test:
			doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
			final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
			
			if(depthCurrent == 0) {
				this.primitiveOffsets[getGlobalId()] = primitivesOffset;
			}
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || primitivesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += radianceMultiplierR * this.colorTemporarySamples_$private$3[0] * PI_RECIPROCAL;
				pixelColorG += radianceMultiplierG * this.colorTemporarySamples_$private$3[1] * PI_RECIPROCAL;
				pixelColorB += radianceMultiplierB * this.colorTemporarySamples_$private$3[2] * PI_RECIPROCAL;
				
//				Update the current pixel color:
				filmAddColor(pixelColorR, pixelColorG, pixelColorB);
				
				return;
			}
			
//			Retrieve the offset to the surfaces array for the given shape:
			final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
			
//			Retrieve the offsets of the surface intersection point and the surface normal:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the surface intersection point:
			final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
			final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal for shading:
			final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
			final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
//			Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
			final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
			final float dotProductMultipliedByTwo = dotProduct * 2.0F;
			
//			Check if the surface normal is correctly oriented:
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
//			Retrieve the correctly oriented surface normal:
			final float surfaceNormalWNormalizedX = isCorrectlyOriented ? surfaceNormalShadingX : -surfaceNormalShadingX;
			final float surfaceNormalWNormalizedY = isCorrectlyOriented ? surfaceNormalShadingY : -surfaceNormalShadingY;
			final float surfaceNormalWNormalizedZ = isCorrectlyOriented ? surfaceNormalShadingZ : -surfaceNormalShadingZ;
			
			final int textureOffsetAlbedo = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET]);
			final int textureOffsetEmission = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_EMISSION_OFFSET]);
			
//			Calculate the albedo texture color for the intersected primitive:
			final int albedoColorRGB = doGetTextureColor(textureOffsetAlbedo);
			
//			Get the color of the primitive from the albedo texture color that was looked up:
			float albedoColorR = ((albedoColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
			float albedoColorG = ((albedoColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
			float albedoColorB = ((albedoColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
			
//			Calculate the emission texture color for the intersected primitive:
			final int emissionColorRGB = doGetTextureColor(textureOffsetEmission);
			
//			Get the color of the primitive from the emission texture color that was looked up:
			float emissionColorR = ((emissionColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
			float emissionColorG = ((emissionColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
			float emissionColorB = ((emissionColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
			
//			Retrieve the material type of the intersected shape:
			final int material = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL]);
			
			if(material == ClearCoatMaterial.TYPE || material == LambertianMaterial.TYPE || material == PhongMaterial.TYPE) {
				doCalculateColorForSun(isSunActive, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalWNormalizedX, surfaceNormalWNormalizedY, surfaceNormalWNormalizedZ, albedoColorR, albedoColorG, albedoColorB);
			} else {
				this.colorTemporarySamples_$private$3[0] = 0.0F;
				this.colorTemporarySamples_$private$3[1] = 0.0F;
				this.colorTemporarySamples_$private$3[2] = 0.0F;
			}
			
			final float sunColorR = this.colorTemporarySamples_$private$3[0];
			final float sunColorG = this.colorTemporarySamples_$private$3[1];
			final float sunColorB = this.colorTemporarySamples_$private$3[2];
			
//			Add the current radiance multiplied by the emission of the intersected primitive to the current pixel color:
			pixelColorR += radianceMultiplierR * (emissionColorR + sunColorR);
			pixelColorG += radianceMultiplierG * (emissionColorG + sunColorG);
			pixelColorB += radianceMultiplierB * (emissionColorB + sunColorB);
			
//			Increment the current depth:
			depthCurrent++;
			
//			Check if the current depth is great enough to perform Russian Roulette to probabilistically terminate the path:
			if(depthCurrent >= depthRussianRoulette) {
//				Calculate the Russian Roulette Probability Density Function (PDF) using the maximum color component of the albedo of the intersected shape:
				final float probabilityDensityFunction = max(albedoColorR, max(albedoColorG, albedoColorB));
				
//				Calculate a random number that will be used when determining whether or not the path should be terminated:
				final float random = nextFloat();
				
//				If the random number is greater than or equal to the Russian Roulette PDF, then terminate the path:
				if(random >= probabilityDensityFunction) {
//					Perform an intersection test:
					final boolean isIntersecting = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, true) < INFINITY;
					
//					Test that an intersection was actually made, and if not, return black color (or possibly the background color):
					if(!isIntersecting) {
//						Calculate the color for the sky in the current direction:
						doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
						
//						Add the color for the sky to the current pixel color:
						pixelColorR += radianceMultiplierR * this.colorTemporarySamples_$private$3[0] * PI_RECIPROCAL;
						pixelColorG += radianceMultiplierG * this.colorTemporarySamples_$private$3[1] * PI_RECIPROCAL;
						pixelColorB += radianceMultiplierB * this.colorTemporarySamples_$private$3[2] * PI_RECIPROCAL;
					}
					
//					Update the current pixel color:
					filmAddColor(pixelColorR, pixelColorG, pixelColorB);
					
					return;
				}
				
//				Calculate the reciprocal of the Russian Roulette PDF, so no divisions are needed next:
				final float probabilityDensityFunctionReciprocal = 1.0F / probabilityDensityFunction;
				
//				Because the path was not terminated this time, the albedo color has to be multiplied with the reciprocal of the Russian Roulette PDF:
				albedoColorR *= probabilityDensityFunctionReciprocal;
				albedoColorG *= probabilityDensityFunctionReciprocal;
				albedoColorB *= probabilityDensityFunctionReciprocal;
			}
			
			if(material == ClearCoatMaterial.TYPE) {
				final float a = REFRACTIVE_INDEX_GLASS - REFRACTIVE_INDEX_AIR;
				final float b = REFRACTIVE_INDEX_GLASS + REFRACTIVE_INDEX_AIR;
				final float c = REFRACTIVE_INDEX_AIR / REFRACTIVE_INDEX_GLASS;
				final float e = (a * a) / (b * b);
				
//				TODO: Write explanation!
				final float nnt = c;
				
//				Calculate the dot product between the W direction and the current ray direction:
				final float dotProduct0 = surfaceNormalWNormalizedX * directionX + surfaceNormalWNormalizedY * directionY + surfaceNormalWNormalizedZ * directionZ;
				
//				Calculate the total internal reflection:
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProduct0 * dotProduct0);
				
//				Calculate the reflection direction:
				final float reflectionDirectionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				final float reflectionDirectionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				final float reflectionDirectionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				
//				Initialize the specular color component values to be used:
				final float specularColorR = 1.0F;
				final float specularColorG = 1.0F;
				final float specularColorB = 1.0F;
				
				if(totalInternalReflection < 0.0F) {
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.02F;
					originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.02F;
					originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.02F;
					
//					Update the ray direction for the next iteration:
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
					
//					Multiply the current radiance multiplier with the specular color:
					radianceMultiplierR *= specularColorR;
					radianceMultiplierG *= specularColorG;
					radianceMultiplierB *= specularColorB;
				} else {
//					Calculate some angles:
					final float angle1 = -dotProduct0;
					final float angle2 = 1.0F - angle1;
					
//					Calculate the reflectance:
					final float reflectance = e + (1.0F - e) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					Calculate the transmittance:
					final float transmittance = 1.0F - reflectance;
					
//					Calculate a probability for the reflection- or the transmission direction:
					final float probability = 0.25F + 0.5F * reflectance;
					
//					Calculate the probability that the direction for the next iteration will be the reflection direction:
					final float reflectanceProbability = reflectance / probability;
					
//					Calculate the probability that the direction for the next iteration will be the transmission direction:
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					Check if the direction for the next iteration is the reflection direction or the transmission direction:
					final boolean isReflectionDirection = nextFloat() < probability;
					
//					Retrieve the value to multiply the current radiance multiplier with:
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
					if(isReflectionDirection) {
//						Update the ray origin for the next iteration:
						originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.02F;
						originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.02F;
						originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.02F;
						
//						Update the ray direction for the next iteration:
						directionX = reflectionDirectionX;
						directionY = reflectionDirectionY;
						directionZ = reflectionDirectionZ;
						
//						Multiply the current radiance multiplier with the specular color:
						radianceMultiplierR *= specularColorR;
						radianceMultiplierG *= specularColorG;
						radianceMultiplierB *= specularColorB;
					} else {
//						Compute cosine weighted hemisphere sample:
						final float u = nextFloat();
						final float v = nextFloat();
						final float phi = PI_MULTIPLIED_BY_TWO * u;
						final float cosTheta = sqrt(v);
						final float sinTheta = sqrt(1.0F - v);
						final float x = cos(phi) * sinTheta;
						final float y = sin(phi) * sinTheta;
						final float z = cosTheta;
						
//						Check if the direction is the Y-direction:
						final boolean isY = abs(surfaceNormalWNormalizedX) > 0.1F;
						
//						Calculate the orthonormal basis U vector:
						final float surfaceNormalUX = (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedZ;
						final float surfaceNormalUY = -((isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedZ);
						final float surfaceNormalUZ = (isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedY - (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedX;
						final float surfaceNormalULengthReciprocal = rsqrt(surfaceNormalUX * surfaceNormalUX + surfaceNormalUY * surfaceNormalUY + surfaceNormalUZ * surfaceNormalUZ);
						final float surfaceNormalUNormalizedX = surfaceNormalUX * surfaceNormalULengthReciprocal;
						final float surfaceNormalUNormalizedY = surfaceNormalUY * surfaceNormalULengthReciprocal;
						final float surfaceNormalUNormalizedZ = surfaceNormalUZ * surfaceNormalULengthReciprocal;
						
//						Calculate the orthonormal basis V vector:
						final float surfaceNormalVNormalizedX = surfaceNormalWNormalizedY * surfaceNormalUNormalizedZ - surfaceNormalWNormalizedZ * surfaceNormalUNormalizedY;
						final float surfaceNormalVNormalizedY = surfaceNormalWNormalizedZ * surfaceNormalUNormalizedX - surfaceNormalWNormalizedX * surfaceNormalUNormalizedZ;
						final float surfaceNormalVNormalizedZ = surfaceNormalWNormalizedX * surfaceNormalUNormalizedY - surfaceNormalWNormalizedY * surfaceNormalUNormalizedX;
						
//						Calculate the direction for the next iteration:
						final float lambertianDirectionX = surfaceNormalUNormalizedX * x + surfaceNormalVNormalizedX * y + surfaceNormalWNormalizedX * z;
						final float lambertianDirectionY = surfaceNormalUNormalizedY * x + surfaceNormalVNormalizedY * y + surfaceNormalWNormalizedY * z;
						final float lambertianDirectionZ = surfaceNormalUNormalizedZ * x + surfaceNormalVNormalizedZ * y + surfaceNormalWNormalizedZ * z;
						final float lambertianDirectionLengthReciprocal = rsqrt(lambertianDirectionX * lambertianDirectionX + lambertianDirectionY * lambertianDirectionY + lambertianDirectionZ * lambertianDirectionZ);
						final float lambertianDirectionNormalizedX = lambertianDirectionX * lambertianDirectionLengthReciprocal;
						final float lambertianDirectionNormalizedY = lambertianDirectionY * lambertianDirectionLengthReciprocal;
						final float lambertianDirectionNormalizedZ = lambertianDirectionZ * lambertianDirectionLengthReciprocal;
						
//						Update the ray origin for the next iteration:
						originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.01F;
						originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.01F;
						originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.01F;
						
//						Update the ray direction for the next iteration:
						directionX = lambertianDirectionNormalizedX;
						directionY = lambertianDirectionNormalizedY;
						directionZ = lambertianDirectionNormalizedZ;
						
//						Multiply the current radiance multiplier with the albedo:
						radianceMultiplierR *= albedoColorR;
						radianceMultiplierG *= albedoColorG;
						radianceMultiplierB *= albedoColorB;
					}
				}
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent + 0;
			} else if(material == LambertianMaterial.TYPE) {
//				Compute cosine weighted hemisphere sample:
				final float u = nextFloat();
				final float v = nextFloat();
				final float phi = PI_MULTIPLIED_BY_TWO * u;
				final float cosTheta = sqrt(v);
				final float sinTheta = sqrt(1.0F - v);
				final float x = cos(phi) * sinTheta;
				final float y = sin(phi) * sinTheta;
				final float z = cosTheta;
				
//				Check if the direction is the Y-direction:
				final boolean isY = abs(surfaceNormalWNormalizedX) > 0.1F;
				
//				Calculate the orthonormal basis U vector:
				final float surfaceNormalUX = (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedZ;
				final float surfaceNormalUY = -((isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedZ);
				final float surfaceNormalUZ = (isY ? 0.0F : 1.0F) * surfaceNormalWNormalizedY - (isY ? 1.0F : 0.0F) * surfaceNormalWNormalizedX;
				final float surfaceNormalULengthReciprocal = rsqrt(surfaceNormalUX * surfaceNormalUX + surfaceNormalUY * surfaceNormalUY + surfaceNormalUZ * surfaceNormalUZ);
				final float surfaceNormalUNormalizedX = surfaceNormalUX * surfaceNormalULengthReciprocal;
				final float surfaceNormalUNormalizedY = surfaceNormalUY * surfaceNormalULengthReciprocal;
				final float surfaceNormalUNormalizedZ = surfaceNormalUZ * surfaceNormalULengthReciprocal;
				
//				Calculate the orthonormal basis V vector:
				final float surfaceNormalVNormalizedX = surfaceNormalWNormalizedY * surfaceNormalUNormalizedZ - surfaceNormalWNormalizedZ * surfaceNormalUNormalizedY;
				final float surfaceNormalVNormalizedY = surfaceNormalWNormalizedZ * surfaceNormalUNormalizedX - surfaceNormalWNormalizedX * surfaceNormalUNormalizedZ;
				final float surfaceNormalVNormalizedZ = surfaceNormalWNormalizedX * surfaceNormalUNormalizedY - surfaceNormalWNormalizedY * surfaceNormalUNormalizedX;
				
//				Calculate the direction for the next iteration:
				final float lambertianDirectionX = surfaceNormalUNormalizedX * x + surfaceNormalVNormalizedX * y + surfaceNormalWNormalizedX * z;
				final float lambertianDirectionY = surfaceNormalUNormalizedY * x + surfaceNormalVNormalizedY * y + surfaceNormalWNormalizedY * z;
				final float lambertianDirectionZ = surfaceNormalUNormalizedZ * x + surfaceNormalVNormalizedZ * y + surfaceNormalWNormalizedZ * z;
				final float lambertianDirectionLengthReciprocal = rsqrt(lambertianDirectionX * lambertianDirectionX + lambertianDirectionY * lambertianDirectionY + lambertianDirectionZ * lambertianDirectionZ);
				final float lambertianDirectionNormalizedX = lambertianDirectionX * lambertianDirectionLengthReciprocal;
				final float lambertianDirectionNormalizedY = lambertianDirectionY * lambertianDirectionLengthReciprocal;
				final float lambertianDirectionNormalizedZ = lambertianDirectionZ * lambertianDirectionLengthReciprocal;
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.01F;
				originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.01F;
				originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.01F;
				
//				Update the ray direction for the next iteration:
				directionX = lambertianDirectionNormalizedX;
				directionY = lambertianDirectionNormalizedY;
				directionZ = lambertianDirectionNormalizedZ;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			} else if(material == PhongMaterial.TYPE) {
//				Compute power cosine weighted hemisphere sample:
				final float exponent = 50.0F;
				final float u = nextFloat();
				final float v = nextFloat();
				final float phi = PI_MULTIPLIED_BY_TWO * u;
				final float cosTheta = pow(1.0F - v, 1.0F / (exponent + 1.0F));
				final float sinTheta = sqrt(max(0.0F, 1.0F - cosTheta * cosTheta));
				final float x = cos(phi) * sinTheta;
				final float y = sin(phi) * sinTheta;
				final float z = cosTheta;
				
//				Calculate the orthonormal basis W vector:
				final float reflectionDirectionWX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				final float reflectionDirectionWY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				final float reflectionDirectionWZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				final float reflectionDirectionWLengthReciprocal = rsqrt(reflectionDirectionWX * reflectionDirectionWX + reflectionDirectionWY * reflectionDirectionWY + reflectionDirectionWZ * reflectionDirectionWZ);
				final float reflectionDirectionWNormalizedX = reflectionDirectionWX * reflectionDirectionWLengthReciprocal;
				final float reflectionDirectionWNormalizedY = reflectionDirectionWY * reflectionDirectionWLengthReciprocal;
				final float reflectionDirectionWNormalizedZ = reflectionDirectionWZ * reflectionDirectionWLengthReciprocal;
				
//				Check if the direction is the Y-direction:
				final boolean isY = abs(reflectionDirectionWNormalizedX) > 0.1F;
				
//				Calculate the orthonormal basis U vector:
				final float reflectionDirectionUX = (isY ? 1.0F : 0.0F) * reflectionDirectionWNormalizedZ;
				final float reflectionDirectionUY = -((isY ? 0.0F : 1.0F) * reflectionDirectionWNormalizedZ);
				final float reflectionDirectionUZ = (isY ? 0.0F : 1.0F) * reflectionDirectionWNormalizedY - (isY ? 1.0F : 0.0F) * reflectionDirectionWNormalizedX;
				final float reflectionDirectionULengthReciprocal = rsqrt(reflectionDirectionUX * reflectionDirectionUX + reflectionDirectionUY * reflectionDirectionUY + reflectionDirectionUZ * reflectionDirectionUZ);
				final float reflectionDirectionUNormalizedX = reflectionDirectionUX * reflectionDirectionULengthReciprocal;
				final float reflectionDirectionUNormalizedY = reflectionDirectionUY * reflectionDirectionULengthReciprocal;
				final float reflectionDirectionUNormalizedZ = reflectionDirectionUZ * reflectionDirectionULengthReciprocal;
				
//				Calculate the orthonormal basis V vector:
				final float reflectionDirectionVNormalizedX = reflectionDirectionWNormalizedY * reflectionDirectionUNormalizedZ - reflectionDirectionWNormalizedZ * reflectionDirectionUNormalizedY;
				final float reflectionDirectionVNormalizedY = reflectionDirectionWNormalizedZ * reflectionDirectionUNormalizedX - reflectionDirectionWNormalizedX * reflectionDirectionUNormalizedZ;
				final float reflectionDirectionVNormalizedZ = reflectionDirectionWNormalizedX * reflectionDirectionUNormalizedY - reflectionDirectionWNormalizedY * reflectionDirectionUNormalizedX;
				
//				Calculate the direction for the next iteration:
				final float phongDirectionX = reflectionDirectionUNormalizedX * x + reflectionDirectionVNormalizedX * y + reflectionDirectionWX * z;
				final float phongDirectionY = reflectionDirectionUNormalizedY * x + reflectionDirectionVNormalizedY * y + reflectionDirectionWY * z;
				final float phongDirectionZ = reflectionDirectionUNormalizedZ * x + reflectionDirectionVNormalizedZ * y + reflectionDirectionWZ * z;
				final float phongDirectionLengthReciprocal = rsqrt(phongDirectionX * phongDirectionX + phongDirectionY * phongDirectionY + phongDirectionZ * phongDirectionZ);
				final float phongDirectionNormalizedX = phongDirectionX * phongDirectionLengthReciprocal;
				final float phongDirectionNormalizedY = phongDirectionY * phongDirectionLengthReciprocal;
				final float phongDirectionNormalizedZ = phongDirectionZ * phongDirectionLengthReciprocal;
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + reflectionDirectionWNormalizedX * 0.01F;
				originY = surfaceIntersectionPointY + reflectionDirectionWNormalizedY * 0.01F;
				originZ = surfaceIntersectionPointZ + reflectionDirectionWNormalizedZ * 0.01F;
				
//				Update the ray direction for the next iteration:
				directionX = phongDirectionNormalizedX;
				directionY = phongDirectionNormalizedY;
				directionZ = phongDirectionNormalizedZ;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			} else if(material == GlassMaterial.TYPE) {
				final float a = REFRACTIVE_INDEX_GLASS - REFRACTIVE_INDEX_AIR;
				final float b = REFRACTIVE_INDEX_GLASS + REFRACTIVE_INDEX_AIR;
				final float c = REFRACTIVE_INDEX_AIR / REFRACTIVE_INDEX_GLASS;
				final float d = REFRACTIVE_INDEX_GLASS / REFRACTIVE_INDEX_AIR;
				final float e = (a * a) / (b * b);
				
//				Check if the current ray is going in towards the same shape or out of it:
				final boolean isGoingIn = surfaceNormalShadingX * surfaceNormalWNormalizedX + surfaceNormalShadingY * surfaceNormalWNormalizedY + surfaceNormalShadingZ * surfaceNormalWNormalizedZ > 0.0F;
				
//				TODO: Write explanation!
				final float nnt = isGoingIn ? c : d;
				
//				Calculate the dot product between the orthonormal basis W vector and the current direction vector:
				final float dotProductOfW0AndDirection = surfaceNormalWNormalizedX * directionX + surfaceNormalWNormalizedY * directionY + surfaceNormalWNormalizedZ * directionZ;
				
//				Calculate the total internal reflection:
				final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);
				
//				Calculate the reflection direction:
				final float reflectionDirectionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				final float reflectionDirectionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				final float reflectionDirectionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				
				if(totalInternalReflection < 0.0F) {
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * 0.01F;
					originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * 0.01F;
					originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * 0.01F;
					
//					Update the ray direction for the next iteration:
					directionX = reflectionDirectionX;
					directionY = reflectionDirectionY;
					directionZ = reflectionDirectionZ;
				} else {
//					Calculate the square root of the total internal reflection:
					final float sqrtTotalInternalReflection = sqrt(totalInternalReflection);
					
//					Calculate the transmission direction:
					final float scalar = isGoingIn ? dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection : -(dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection);
					final float direction0X = directionX * nnt - surfaceNormalShadingX * scalar;
					final float direction0Y = directionY * nnt - surfaceNormalShadingY * scalar;
					final float direction0Z = directionZ * nnt - surfaceNormalShadingZ * scalar;
					final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);
					final float transmissionDirectionX = direction0X * direction0LengthReciprocal;
					final float transmissionDirectionY = direction0Y * direction0LengthReciprocal;
					final float transmissionDirectionZ = direction0Z * direction0LengthReciprocal;
					
//					Calculate some angles:
					final float angle1 = (isGoingIn ? -dotProductOfW0AndDirection : transmissionDirectionX * surfaceNormalShadingX + transmissionDirectionY * surfaceNormalShadingY + transmissionDirectionZ * surfaceNormalShadingZ);
					final float angle2 = 1.0F - angle1;
					
//					Calculate the reflectance:
					final float reflectance = e + (1.0F - e) * angle2 * angle2 * angle2 * angle2 * angle2;
					
//					Calculate the transmittance:
					final float transmittance = 1.0F - reflectance;
					
//					Calculate a probability for the reflection- or the transmission direction:
					final float probability = 0.25F + 0.5F * reflectance;
					
//					Calculate the probability that the direction for the next iteration will be the reflection direction:
					final float reflectanceProbability = reflectance / probability;
					
//					Calculate the probability that the direction for the next iteration will be the transmission direction:
					final float transmittanceProbability = transmittance / (1.0F - probability);
					
//					Check if the direction for the next iteration is the reflection direction or the transmission direction:
					final boolean isReflectionDirection = nextFloat() < probability;
					
//					Retrieve the value to multiply the current radiance multiplier with:
					final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;
					
//					Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:
					radianceMultiplierR *= multiplier;
					radianceMultiplierG *= multiplier;
					radianceMultiplierB *= multiplier;
					
//					Retrieve the epsilon value that offsets the ray origin to mitigate self intersections:
					final float epsilon = isReflectionDirection ? 0.01F : 0.000001F;
					
//					Update the ray origin for the next iteration:
					originX = surfaceIntersectionPointX + surfaceNormalWNormalizedX * epsilon;
					originY = surfaceIntersectionPointY + surfaceNormalWNormalizedY * epsilon;
					originZ = surfaceIntersectionPointZ + surfaceNormalWNormalizedZ * epsilon;
					
//					Update the ray direction for the next iteration:
					directionX = isReflectionDirection ? reflectionDirectionX : transmissionDirectionX;
					directionY = isReflectionDirection ? reflectionDirectionY : transmissionDirectionY;
					directionZ = isReflectionDirection ? reflectionDirectionZ : transmissionDirectionZ;
				}
				
//				FIXME: Find out why the "child list broken" Exception occurs if the following line is not present!
				depthCurrent = depthCurrent + 0;
			} else if(material == ReflectionMaterial.TYPE) {
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalShadingX * 0.000001F;
				originY = surfaceIntersectionPointY + surfaceNormalShadingY * 0.000001F;
				originZ = surfaceIntersectionPointZ + surfaceNormalShadingZ * 0.000001F;
				
//				Update the ray direction for the next iteration:
				directionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				directionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				directionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
				
//				Multiply the current radiance multiplier with the albedo:
				radianceMultiplierR *= albedoColorR;
				radianceMultiplierG *= albedoColorG;
				radianceMultiplierB *= albedoColorB;
			}
		} while(depthCurrent < depthMaximum);
		
//		Perform an intersection test:
		final boolean isIntersecting = doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, true) < INFINITY;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(!isIntersecting) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR += radianceMultiplierR * this.colorTemporarySamples_$private$3[0] * PI_RECIPROCAL;
			pixelColorG += radianceMultiplierG * this.colorTemporarySamples_$private$3[1] * PI_RECIPROCAL;
			pixelColorB += radianceMultiplierB * this.colorTemporarySamples_$private$3[2] * PI_RECIPROCAL;
		}
		
//		Update the current pixel color:
		filmAddColor(pixelColorR, pixelColorG, pixelColorB);
	}
	
	private void doRenderWithRayCaster(final boolean isSkyActive) {
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Perform an intersection test:
		doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
		
//		Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
		final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
		
//		Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:
		final int primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
		
//		Retrieve the offset to the surfaces array for the given shape:
		final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
		
		this.primitiveOffsets[getGlobalId()] = primitivesOffset;
		
//		Test that an intersection was actually made, and if not, return black color (or possibly the background color):
		if(distance == INFINITY || primitivesOffset == -1) {
//			Calculate the color for the sky in the current direction:
			doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
			
//			Add the color for the sky to the current pixel color:
			pixelColorR = this.colorTemporarySamples_$private$3[0];
			pixelColorG = this.colorTemporarySamples_$private$3[1];
			pixelColorB = this.colorTemporarySamples_$private$3[2];
			
//			Update the current pixel color:
			filmAddColor(pixelColorR, pixelColorG, pixelColorB);
			
			return;
		}
		
		final int textureOffsetAlbedo = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET]);
		
//		Calculate the albedo texture color for the intersected shape:
		final int albedoColorRGB = doGetTextureColor(textureOffsetAlbedo);
		
//		Get the color of the shape from the albedo texture color that was looked up:
		float albedoColorR = ((albedoColorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		float albedoColorG = ((albedoColorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		float albedoColorB = ((albedoColorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
//		Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
		final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		
//		Retrieve the surface intersection point from the intersections array:
		final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
		final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
		final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
		
//		Retrieve the surface normal from the intersections array:
		final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
		final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
		final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
		
//		Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
		final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
		
//		Check if the surface normal is correctly oriented:
		final boolean isCorrectlyOriented = dotProduct < 0.0F;
		
//		Retrieve the correctly oriented surface normal:
		final float surfaceNormalWNormalizedX = isCorrectlyOriented ? surfaceNormalShadingX : -surfaceNormalShadingX;
		final float surfaceNormalWNormalizedY = isCorrectlyOriented ? surfaceNormalShadingY : -surfaceNormalShadingY;
		final float surfaceNormalWNormalizedZ = isCorrectlyOriented ? surfaceNormalShadingZ : -surfaceNormalShadingZ;
		
		final int colorRGB = doShaderPhongReflectionModel0(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalWNormalizedX, surfaceNormalWNormalizedY, surfaceNormalWNormalizedZ, -directionX, -directionY, -directionZ, albedoColorR, albedoColorG, albedoColorB, 0.2F, 0.2F, 0.2F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 250.0F);
//		final int colorRGB = doShaderPhongReflectionModel1(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalWNormalizedX, surfaceNormalWNormalizedY, surfaceNormalWNormalizedZ, directionX, directionY, directionZ, albedoColorR, albedoColorG, albedoColorB);
		
		pixelColorR = ((colorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
		pixelColorG = ((colorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
		pixelColorB = ((colorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
		
//		Update the current pixel color:
		filmAddColor(pixelColorR, pixelColorG, pixelColorB);
	}
	
	private void doRenderWithRayMarcher(final boolean isSkyActive) {
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
		originY = doGetY(originX, originZ) + 0.1F;
		
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
		final float delTMultiplier = 0.01F;
		
		float delT = delTMultiplier;
		
		final float minT = 0.001F;
		final float maxT = max(originY, 1.0F) * 20.0F;
		
		doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
		
		float pixelColorR = this.colorTemporarySamples_$private$3[0];
		float pixelColorG = this.colorTemporarySamples_$private$3[1];
		float pixelColorB = this.colorTemporarySamples_$private$3[2];
		
		for(float t = minT; t < maxT; t += delT) {
			final float surfaceIntersectionPointX = originX + directionX * t;
			final float surfaceIntersectionPointY = originY + directionY * t;
			final float surfaceIntersectionPointZ = originZ + directionZ * t;
			
			final float height = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ);
			
			if(surfaceIntersectionPointY < height) {
//				Calculate the surface normal at the surface intersection point:
				final float surfaceNormalX = doGetY(surfaceIntersectionPointX - EPSILON, surfaceIntersectionPointZ) - doGetY(surfaceIntersectionPointX + EPSILON, surfaceIntersectionPointZ);
				final float surfaceNormalY = 2.0F * EPSILON;
				final float surfaceNormalZ = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ - EPSILON) - doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ + EPSILON);
				final float surfaceNormalLengthReciprocal = 1.0F / sqrt(surfaceNormalX * surfaceNormalX + surfaceNormalY * surfaceNormalY + surfaceNormalZ * surfaceNormalZ);
				final float surfaceNormalNormalizedX = surfaceNormalX * surfaceNormalLengthReciprocal;
				final float surfaceNormalNormalizedY = surfaceNormalY * surfaceNormalLengthReciprocal;
				final float surfaceNormalNormalizedZ = surfaceNormalZ * surfaceNormalLengthReciprocal;
				
				final float noise = simplexFractionalBrownianMotionXYZ(8.0F, 0.5F, 0.0F, 1.0F, 16, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ);
				
//				Calculate the albedo color of the surface intersection point:
				float albedoColorR = saturate(noise * 1.0F + 0.1F, 0.0F, 1.0F);
				float albedoColorG = saturate(noise * 1.0F + 0.1F, 0.0F, 1.0F);
				float albedoColorB = saturate(noise * 1.0F + 0.1F, 0.0F, 1.0F);
				
				final int colorRGB = doShaderPhongReflectionModel0(false, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalNormalizedX, surfaceNormalNormalizedY, surfaceNormalNormalizedZ, -directionX, -directionY, -directionZ, albedoColorR, albedoColorG, albedoColorB, 0.2F, 0.2F, 0.2F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 250.0F);
//				final int colorRGB = doShaderPhongReflectionModel1(false, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalNormalizedX, surfaceNormalNormalizedY, surfaceNormalNormalizedZ, directionX, directionY, directionZ, albedoColorR, albedoColorG, albedoColorB);
				
				pixelColorR = ((colorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
				pixelColorG = ((colorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
				pixelColorB = ((colorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
				
				t = maxT;
			}
			
			delT = delTMultiplier * t;
		}
		
		filmAddColor(pixelColorR, pixelColorG, pixelColorB);
	}
	
	private void doRenderWithRayTracer(final boolean isSkyActive) {
//		Retrieve the maximum depth allowed:
		final int depthMaximum = 5;
		
//		Calculate the current offset to the intersections array:
		final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;
		
//		Initialize the current depth:
		int depthCurrent = 0;
		
//		Initialize the origin from the primary ray:
		float originX = this.rays_$private$6[0];
		float originY = this.rays_$private$6[1];
		float originZ = this.rays_$private$6[2];
		
//		Initialize the direction from the primary ray:
		float directionX = this.rays_$private$6[3];
		float directionY = this.rays_$private$6[4];
		float directionZ = this.rays_$private$6[5];
		
//		Initialize the pixel color to black:
		float pixelColorR = 0.0F;
		float pixelColorG = 0.0F;
		float pixelColorB = 0.0F;
		
//		Initialize the offset to the primitive to -1:
		int primitivesOffset = -1;
		
		do {
//			Perform an intersection test:
			doIntersectPrimitives(originX, originY, originZ, directionX, directionY, directionZ, false);
			
//			Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:
			final float distance = this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];
			
//			Retrieve the offset in the primitives array of the closest intersected primitive, or -1 if no primitive were intersected:
			primitivesOffset = (int)(this.intersections_$local$[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_PRIMITIVE_OFFSET]);
			
			if(depthCurrent == 0) {
				this.primitiveOffsets[getGlobalId()] = primitivesOffset;
			}
			
//			Test that an intersection was actually made, and if not, return black color (or possibly the background color):
			if(distance == INFINITY || primitivesOffset == -1) {
//				Calculate the color for the sky in the current direction:
				doCalculateColorForSky(isSkyActive, directionX, directionY, directionZ);
				
//				Add the color for the sky to the current pixel color:
				pixelColorR += this.colorTemporarySamples_$private$3[0];
				pixelColorG += this.colorTemporarySamples_$private$3[1];
				pixelColorB += this.colorTemporarySamples_$private$3[2];
				
//				Update the current pixel color:
				filmAddColor(pixelColorR, pixelColorG, pixelColorB);
				
				return;
			}
			
//			Increment the current depth:
			depthCurrent++;
			
//			Retrieve the offset to the surfaces array for the given primitive:
			final int surfacesOffset = this.scenePrimitives_$constant$[primitivesOffset + Primitive.RELATIVE_OFFSET_SURFACE_OFFSET];
			
//			Retrieve the material type of the intersected primitive:
			final int material = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_MATERIAL]);
			
			final int textureOffsetAlbedo = (int)(this.sceneSurfaces_$constant$[surfacesOffset + Surface.RELATIVE_OFFSET_TEXTURE_ALBEDO_OFFSET]);
			
//			Calculate the albedo texture color for the intersected primitive:
			final int albedoColorRGB = doGetTextureColor(textureOffsetAlbedo);
			
//			Get the color of the shape from the albedo texture color that was looked up:
			float albedoColorR = ((albedoColorRGB >> 16) & 0xFF) / 255.0F;
			float albedoColorG = ((albedoColorRGB >>  8) & 0xFF) / 255.0F;
			float albedoColorB = ((albedoColorRGB >>  0) & 0xFF) / 255.0F;
			
//			Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:
			final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
			final int offsetIntersectionSurfaceNormalShading = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
			
//			Retrieve the surface intersection point from the intersections array:
			final float surfaceIntersectionPointX = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
			final float surfaceIntersectionPointY = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
			final float surfaceIntersectionPointZ = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
			
//			Retrieve the surface normal from the intersections array:
			final float surfaceNormalShadingX = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
			final float surfaceNormalShadingY = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
			final float surfaceNormalShadingZ = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
			
//			Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
			final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
			
//			Check if the surface normal is correctly oriented:
			final boolean isCorrectlyOriented = dotProduct < 0.0F;
			
//			Retrieve the correctly oriented surface normal:
			final float surfaceNormalWNormalizedX = isCorrectlyOriented ? surfaceNormalShadingX : -surfaceNormalShadingX;
			final float surfaceNormalWNormalizedY = isCorrectlyOriented ? surfaceNormalShadingY : -surfaceNormalShadingY;
			final float surfaceNormalWNormalizedZ = isCorrectlyOriented ? surfaceNormalShadingZ : -surfaceNormalShadingZ;
			
			final int colorRGB = doShaderPhongReflectionModel0(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalWNormalizedX, surfaceNormalWNormalizedY, surfaceNormalWNormalizedZ, -directionX, -directionY, -directionZ, albedoColorR, albedoColorG, albedoColorB, 0.2F, 0.2F, 0.2F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.5F, 250.0F);
//			final int colorRGB = doShaderPhongReflectionModel1(true, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, surfaceNormalWNormalizedX, surfaceNormalWNormalizedY, surfaceNormalWNormalizedZ, directionX, directionY, directionZ, albedoColorR, albedoColorG, albedoColorB);
			
			pixelColorR += ((colorRGB >> 16) & 0xFF) * COLOR_RECIPROCAL;
			pixelColorG += ((colorRGB >>  8) & 0xFF) * COLOR_RECIPROCAL;
			pixelColorB += ((colorRGB >>  0) & 0xFF) * COLOR_RECIPROCAL;
			
			if(material == ReflectionMaterial.TYPE) {
//				Calculate the dot product between the surface normal of the intersected shape and the current ray direction:
//				final float dotProduct = surfaceNormalShadingX * directionX + surfaceNormalShadingY * directionY + surfaceNormalShadingZ * directionZ;
				final float dotProductMultipliedByTwo = dotProduct * 2.0F;
				
//				Update the ray origin for the next iteration:
				originX = surfaceIntersectionPointX + surfaceNormalShadingX * 0.000001F;
				originY = surfaceIntersectionPointY + surfaceNormalShadingY * 0.000001F;
				originZ = surfaceIntersectionPointZ + surfaceNormalShadingZ * 0.000001F;
				
//				Update the ray direction for the next iteration:
				directionX = directionX - surfaceNormalShadingX * dotProductMultipliedByTwo;
				directionY = directionY - surfaceNormalShadingY * dotProductMultipliedByTwo;
				directionZ = directionZ - surfaceNormalShadingZ * dotProductMultipliedByTwo;
			} else {
				depthCurrent = depthMaximum;
			}
		} while(depthCurrent < depthMaximum);
		
//		Update the current pixel color:
		filmAddColor(pixelColorR, pixelColorG, pixelColorB);
	}
	
	private void doTransformIntersectionToWorldSpace(final float objectToWorldElement11, final float objectToWorldElement12, final float objectToWorldElement13, final float objectToWorldElement14, final float objectToWorldElement21, final float objectToWorldElement22, final float objectToWorldElement23, final float objectToWorldElement24, final float objectToWorldElement31, final float objectToWorldElement32, final float objectToWorldElement33, final float objectToWorldElement34, final float worldToObjectElement11, final float worldToObjectElement12, final float worldToObjectElement13, final float worldToObjectElement21, final float worldToObjectElement22, final float worldToObjectElement23, final float worldToObjectElement31, final float worldToObjectElement32, final float worldToObjectElement33) {
		final int offsetIntersection = getLocalId() * SIZE_INTERSECTION;
		final int offsetIntersectionOrthoNormalBasisU = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_U;
		final int offsetIntersectionOrthoNormalBasisV = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_V;
		final int offsetIntersectionOrthoNormalBasisW = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_ORTHO_NORMAL_BASIS_W;
		final int offsetIntersectionSurfaceIntersectionPoint = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;
		final int offsetIntersectionSurfaceNormal = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;
		final int offsetIntersectionSurfaceNormalShading = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL_SHADING;
		final int offsetIntersectionSurfaceTangent = offsetIntersection + RELATIVE_OFFSET_INTERSECTION_SURFACE_TANGENT;
		
		final float orthoNormalBasisUXObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 0];
		final float orthoNormalBasisUYObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 1];
		final float orthoNormalBasisUZObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 2];
		final float orthoNormalBasisVXObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 0];
		final float orthoNormalBasisVYObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 1];
		final float orthoNormalBasisVZObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 2];
		final float orthoNormalBasisWXObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 0];
		final float orthoNormalBasisWYObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 1];
		final float orthoNormalBasisWZObjectSpace = this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 2];
		final float surfaceIntersectionPointXObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0];
		final float surfaceIntersectionPointYObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1];
		final float surfaceIntersectionPointZObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2];
		final float surfaceNormalXObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceNormal + 0];
		final float surfaceNormalYObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceNormal + 1];
		final float surfaceNormalZObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceNormal + 2];
		final float surfaceNormalShadingXObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0];
		final float surfaceNormalShadingYObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1];
		final float surfaceNormalShadingZObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2];
		final float surfaceTangentXObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceTangent + 0];
		final float surfaceTangentYObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceTangent + 1];
		final float surfaceTangentZObjectSpace = this.intersections_$local$[offsetIntersectionSurfaceTangent + 2];
		
		final float orthoNormalBasisUXWorldSpace = worldToObjectElement11 * orthoNormalBasisUXObjectSpace + worldToObjectElement21 * orthoNormalBasisUYObjectSpace + worldToObjectElement31 * orthoNormalBasisUZObjectSpace;
		final float orthoNormalBasisUYWorldSpace = worldToObjectElement12 * orthoNormalBasisUXObjectSpace + worldToObjectElement22 * orthoNormalBasisUYObjectSpace + worldToObjectElement32 * orthoNormalBasisUZObjectSpace;
		final float orthoNormalBasisUZWorldSpace = worldToObjectElement13 * orthoNormalBasisUXObjectSpace + worldToObjectElement23 * orthoNormalBasisUYObjectSpace + worldToObjectElement33 * orthoNormalBasisUZObjectSpace;
		final float orthoNormalBasisVXWorldSpace = worldToObjectElement11 * orthoNormalBasisVXObjectSpace + worldToObjectElement21 * orthoNormalBasisVYObjectSpace + worldToObjectElement31 * orthoNormalBasisVZObjectSpace;
		final float orthoNormalBasisVYWorldSpace = worldToObjectElement12 * orthoNormalBasisVXObjectSpace + worldToObjectElement22 * orthoNormalBasisVYObjectSpace + worldToObjectElement32 * orthoNormalBasisVZObjectSpace;
		final float orthoNormalBasisVZWorldSpace = worldToObjectElement13 * orthoNormalBasisVXObjectSpace + worldToObjectElement23 * orthoNormalBasisVYObjectSpace + worldToObjectElement33 * orthoNormalBasisVZObjectSpace;
		final float orthoNormalBasisWXWorldSpace = worldToObjectElement11 * orthoNormalBasisWXObjectSpace + worldToObjectElement21 * orthoNormalBasisWYObjectSpace + worldToObjectElement31 * orthoNormalBasisWZObjectSpace;
		final float orthoNormalBasisWYWorldSpace = worldToObjectElement12 * orthoNormalBasisWXObjectSpace + worldToObjectElement22 * orthoNormalBasisWYObjectSpace + worldToObjectElement32 * orthoNormalBasisWZObjectSpace;
		final float orthoNormalBasisWZWorldSpace = worldToObjectElement13 * orthoNormalBasisWXObjectSpace + worldToObjectElement23 * orthoNormalBasisWYObjectSpace + worldToObjectElement33 * orthoNormalBasisWZObjectSpace;
		final float surfaceIntersectionPointXWorldSpace = objectToWorldElement11 * surfaceIntersectionPointXObjectSpace + objectToWorldElement12 * surfaceIntersectionPointYObjectSpace + objectToWorldElement13 * surfaceIntersectionPointZObjectSpace + objectToWorldElement14;
		final float surfaceIntersectionPointYWorldSpace = objectToWorldElement21 * surfaceIntersectionPointXObjectSpace + objectToWorldElement22 * surfaceIntersectionPointYObjectSpace + objectToWorldElement23 * surfaceIntersectionPointZObjectSpace + objectToWorldElement24;
		final float surfaceIntersectionPointZWorldSpace = objectToWorldElement31 * surfaceIntersectionPointXObjectSpace + objectToWorldElement32 * surfaceIntersectionPointYObjectSpace + objectToWorldElement33 * surfaceIntersectionPointZObjectSpace + objectToWorldElement34;
		final float surfaceNormalXWorldSpace = worldToObjectElement11 * surfaceNormalXObjectSpace + worldToObjectElement21 * surfaceNormalYObjectSpace + worldToObjectElement31 * surfaceNormalZObjectSpace;
		final float surfaceNormalYWorldSpace = worldToObjectElement12 * surfaceNormalXObjectSpace + worldToObjectElement22 * surfaceNormalYObjectSpace + worldToObjectElement32 * surfaceNormalZObjectSpace;
		final float surfaceNormalZWorldSpace = worldToObjectElement13 * surfaceNormalXObjectSpace + worldToObjectElement23 * surfaceNormalYObjectSpace + worldToObjectElement33 * surfaceNormalZObjectSpace;
		final float surfaceNormalShadingXWorldSpace = worldToObjectElement11 * surfaceNormalShadingXObjectSpace + worldToObjectElement21 * surfaceNormalShadingYObjectSpace + worldToObjectElement31 * surfaceNormalShadingZObjectSpace;
		final float surfaceNormalShadingYWorldSpace = worldToObjectElement12 * surfaceNormalShadingXObjectSpace + worldToObjectElement22 * surfaceNormalShadingYObjectSpace + worldToObjectElement32 * surfaceNormalShadingZObjectSpace;
		final float surfaceNormalShadingZWorldSpace = worldToObjectElement13 * surfaceNormalShadingXObjectSpace + worldToObjectElement23 * surfaceNormalShadingYObjectSpace + worldToObjectElement33 * surfaceNormalShadingZObjectSpace;
		final float surfaceTangentXWorldSpace = worldToObjectElement11 * surfaceTangentXObjectSpace + worldToObjectElement21 * surfaceTangentYObjectSpace + worldToObjectElement31 * surfaceTangentZObjectSpace;
		final float surfaceTangentYWorldSpace = worldToObjectElement12 * surfaceTangentXObjectSpace + worldToObjectElement22 * surfaceTangentYObjectSpace + worldToObjectElement32 * surfaceTangentZObjectSpace;
		final float surfaceTangentZWorldSpace = worldToObjectElement13 * surfaceTangentXObjectSpace + worldToObjectElement23 * surfaceTangentYObjectSpace + worldToObjectElement33 * surfaceTangentZObjectSpace;
		
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 0] = orthoNormalBasisUXWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 1] = orthoNormalBasisUYWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisU + 2] = orthoNormalBasisUZWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 0] = orthoNormalBasisVXWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 1] = orthoNormalBasisVYWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisV + 2] = orthoNormalBasisVZWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 0] = orthoNormalBasisWXWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 1] = orthoNormalBasisWYWorldSpace;
		this.intersections_$local$[offsetIntersectionOrthoNormalBasisW + 2] = orthoNormalBasisWZWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 0] = surfaceIntersectionPointXWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointYWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 0] = surfaceNormalXWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 1] = surfaceNormalYWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceNormal + 2] = surfaceNormalZWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 0] = surfaceNormalShadingXWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 1] = surfaceNormalShadingYWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceNormalShading + 2] = surfaceNormalShadingZWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceTangent + 0] = surfaceTangentXWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceTangent + 1] = surfaceTangentYWorldSpace;
		this.intersections_$local$[offsetIntersectionSurfaceTangent + 2] = surfaceTangentZWorldSpace;
	}
}