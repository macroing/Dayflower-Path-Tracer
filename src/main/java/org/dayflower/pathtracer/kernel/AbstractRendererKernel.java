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

import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.compiler.CompiledScene;
import org.dayflower.pathtracer.scene.loader.SceneLoader;

/**
 * This {@code AbstractRendererKernel} class is an abstract extension of {@link AbstractImageKernel} with the basic functionality that is needed by Dayflower to render.
 * <p>
 * The implementations of this class should take care of the rendering itself. Some implementations may run on the CPU, whereas others might run on the GPU.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractRendererKernel extends AbstractImageKernel {
	/**
	 * An {@code int} representation of a {@code boolean} of {@code false}.
	 */
	public static final int BOOLEAN_FALSE = 0;
	
	/**
	 * An {@code int} representation of a {@code boolean} of {@code true}.
	 */
	public static final int BOOLEAN_TRUE = 1;
	
	/**
	 * The renderer type for the Ambient Occlusion, which is {@code 1}.
	 */
	public static final int RENDERER_TYPE_AMBIENT_OCCLUSION = 1;
	
	/**
	 * The renderer type for the Path Tracer, which is {@code 2}.
	 */
	public static final int RENDERER_TYPE_PATH_TRACER = 2;
	
	/**
	 * The renderer type for the Ray Caster, which is {@code 3}.
	 */
	public static final int RENDERER_TYPE_RAY_CASTER = 3;
	
	/**
	 * The renderer type for the Ray Marcher, which is {@code 4}.
	 */
	public static final int RENDERER_TYPE_RAY_MARCHER = 4;
	
	/**
	 * The renderer type for the Ray Tracer, which is {@code 5}.
	 */
	public static final int RENDERER_TYPE_RAY_TRACER = 5;
	
	/**
	 * The renderer type for rendering surface normals, which is {@code 6}.
	 */
	public static final int RENDERER_TYPE_SURFACE_NORMALS = 6;
	
	/**
	 * The shader type for flat shading, which is {@code 1}.
	 */
	public static final int SHADER_TYPE_FLAT = 1;
	
	/**
	 * The shader type for Gouraud shading, which is {@code 2}.
	 */
	public static final int SHADER_TYPE_GOURAUD = 2;
	
	/**
	 * The tone mapper type for the modified ACES Filmic Curve, which is {@code 1}.
	 */
	public static final int TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED = 1;
	
	/**
	 * The tone mapper type for Reinhard, which is {@code 2}.
	 */
	public static final int TONE_MAPPER_TYPE_REINHARD = 2;
	
	/**
	 * The tone mapper type for v.1 of the modified Reinhard, which is {@code 3}.
	 */
	public static final int TONE_MAPPER_TYPE_REINHARD_MODIFIED_1 = 3;
	
	/**
	 * The tone mapper type for v.2 of the modified Reinhard, which is {@code 4}.
	 */
	public static final int TONE_MAPPER_TYPE_REINHARD_MODIFIED_2 = 4;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The maximum distance for Ambient Occlusion.
	 */
	protected float rendererAOMaximumDistance;
	
	/**
	 * The tone mapper exposure.
	 */
	protected float toneMapperExposure;
	
	/**
	 * The normal mapping state for the renderer.
	 */
	protected int rendererNormalMapping;
	
	/**
	 * The maximum ray depth for Path Tracing.
	 */
	protected int rendererPTRayDepthMaximum;
	
	/**
	 * The ray depth to begin Russian Roulette path termination for Path Tracing.
	 */
	protected int rendererPTRayDepthRussianRoulette;
	
	/**
	 * The renderer type.
	 */
	protected int rendererType;
	
	/**
	 * The wireframes state for the renderer.
	 */
	protected int rendererWireframes;
	
	/**
	 * The shader type.
	 */
	protected int shaderType;
	
	/**
	 * The tone mapper type.
	 */
	protected int toneMapperType;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final CompiledScene compiledScene;
	private final Scene scene;
	private final SceneLoader sceneLoader;
	private boolean hasChanged;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractRendererKernel} instance.
	 * <p>
	 * If {@code sceneLoader} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sceneLoader a {@link SceneLoader}
	 * @throws NullPointerException thrown if, and only if, {@code sceneLoader} is {@code null}
	 */
	protected AbstractRendererKernel(final SceneLoader sceneLoader) {
		this.rendererAOMaximumDistance = 200.0F;
		this.toneMapperExposure = 1.0F;
		this.rendererNormalMapping = BOOLEAN_TRUE;
		this.rendererPTRayDepthMaximum = 5;
		this.rendererPTRayDepthRussianRoulette = 5;
		this.rendererType = RENDERER_TYPE_PATH_TRACER;
		this.rendererWireframes = BOOLEAN_FALSE;
		this.shaderType = SHADER_TYPE_GOURAUD;
		this.toneMapperType = TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED;
		this.compiledScene = sceneLoader.loadCompiledScene();
		this.scene = sceneLoader.loadScene();
		this.sceneLoader = sceneLoader;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Camera} instance.
	 * 
	 * @return the {@code Camera} instance
	 */
	public final Camera getCamera() {
		return this.scene.getCamera();
	}
	
	/**
	 * Returns the {@link CompiledScene} instance.
	 * 
	 * @return the {@code CompiledScene} instance
	 */
	public final CompiledScene getCompiledScene() {
		return this.compiledScene;
	}
	
	/**
	 * Returns the {@link Scene} instance.
	 * 
	 * @return the {@code Scene} instance
	 */
	public final Scene getScene() {
		return this.scene;
	}
	
	/**
	 * Returns the {@link SceneLoader} instance.
	 * 
	 * @return the {@code SceneLoader} instance
	 */
	public final SceneLoader getSceneLoader() {
		return this.sceneLoader;
	}
	
	/**
	 * Returns the {@link Sky} instance.
	 * 
	 * @return the {@code Sky} instance
	 */
	public final Sky getSky() {
		return this.scene.getSky();
	}
	
	/**
	 * Returns {@code true} if, and only if, this {@code AbstractRendererKernel} has changed, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, this {@code AbstractRendererKernel} has changed, {@code false} otherwise
	 */
	public final boolean hasChanged() {
		return this.hasChanged;
	}
	
	/**
	 * Returns {@code true} if, and only if, the renderer type is Ambient Occlusion, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the renderer type is Ambient Occlusion, {@code false} otherwise
	 */
	public final boolean isRendererTypeAmbientOcclusion() {
		return this.rendererType == RENDERER_TYPE_AMBIENT_OCCLUSION;
	}
	
	/**
	 * Returns {@code true} if, and only if, the renderer type is Path Tracer, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the renderer type is Path Tracer, {@code false} otherwise
	 */
	public final boolean isRendererTypePathTracer() {
		return this.rendererType == RENDERER_TYPE_PATH_TRACER;
	}
	
	/**
	 * Returns {@code true} if, and only if, the renderer type is Ray Caster, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the renderer type is Ray Caster, {@code false} otherwise
	 */
	public final boolean isRendererTypeRayCaster() {
		return this.rendererType == RENDERER_TYPE_RAY_CASTER;
	}
	
	/**
	 * Returns {@code true} if, and only if, the renderer type is Ray Marcher, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the renderer type is Ray Marcher, {@code false} otherwise
	 */
	public final boolean isRendererTypeRayMarcher() {
		return this.rendererType == RENDERER_TYPE_RAY_MARCHER;
	}
	
	/**
	 * Returns {@code true} if, and only if, the renderer type is Ray Tracer, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the renderer type is Ray Tracer, {@code false} otherwise
	 */
	public final boolean isRendererTypeRayTracer() {
		return this.rendererType == RENDERER_TYPE_RAY_TRACER;
	}
	
	/**
	 * Returns {@code true} if, and only if, the renderer type is surface normals, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the renderer type is surface normals, {@code false} otherwise
	 */
	public final boolean isRendererTypeSurfaceNormals() {
		return this.rendererType == RENDERER_TYPE_SURFACE_NORMALS;
	}
	
	/**
	 * Returns the maximum distance for Ambient Occlusion.
	 * 
	 * @return the maximum distance for Ambient Occlusion
	 */
	public final float getRendererAOMaximumDistance() {
		return this.rendererAOMaximumDistance;
	}
	
	/**
	 * Returns the exposure for the tone mapper.
	 * 
	 * @return the exposure for the tone mapper
	 */
	public final float getToneMapperExposure() {
		return this.toneMapperExposure;
	}
	
	/**
	 * Returns the normal mapping state for the renderer.
	 * 
	 * @return the normal mapping state for the renderer
	 */
	public final int getRendererNormalMapping() {
		return this.rendererNormalMapping;
	}
	
	/**
	 * Returns the maximum ray depth for Path Tracing.
	 * 
	 * @return the maximum ray depth for Path Tracing
	 */
	public final int getRendererPTRayDepthMaximum() {
		return this.rendererPTRayDepthMaximum;
	}
	
	/**
	 * Returns the ray depth to begin Russian Roulette path termination for Path Tracing.
	 * 
	 * @return the ray depth to begin Russian Roulette path termination for Path Tracing
	 */
	public final int getRendererPTRayDepthRussianRoulette() {
		return this.rendererPTRayDepthRussianRoulette;
	}
	
	/**
	 * Returns the renderer type that is currently enabled.
	 * 
	 * @return the renderer type that is currently enabled
	 */
	public final int getRendererType() {
		return this.rendererType;
	}
	
	/**
	 * Returns the wireframes state for the renderer.
	 * 
	 * @return the wireframes state for the renderer
	 */
	public final int getRendererWireframes() {
		return this.rendererWireframes;
	}
	
	/**
	 * Returns the selected {@link Primitive} index or {@code -1} if no {@code Primitive} has been selected.
	 * 
	 * @return the selected {@code Primitive} index or {@code -1} if no {@code Primitive} has been selected
	 */
	public abstract int getSelectedPrimitiveIndex();
	
	/**
	 * Returns the shader type that is currently enabled.
	 * 
	 * @return the shader type that is currently enabled
	 */
	public final int getShaderType() {
		return this.shaderType;
	}
	
	/**
	 * Sets the changed state for this {@code AbstractRendererKernel} instance.
	 * <p>
	 * Because this state change feature is global, most callers should only call {@code setChanged(true)}. Only select few should turn off the state change by calling {@code setChanged(false)}. This should preferably be the top-most component that
	 * governs the overall aspects of the program. Otherwise, certain state changes might be missed.
	 * 
	 * @param hasChanged {@code true} if, and only if, a change has occurred, {@code false} otherwise
	 */
	public final void setChanged(final boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	
	/**
	 * Sets the maximum distance for Ambient Occlusion.
	 * 
	 * @param rendererAOMaximumDistance the maximum distance for Ambient Occlusion
	 */
	public final void setRendererAOMaximumDistance(final float rendererAOMaximumDistance) {
		if(Float.compare(this.rendererAOMaximumDistance, rendererAOMaximumDistance) != 0) {
			this.rendererAOMaximumDistance = rendererAOMaximumDistance;
			this.hasChanged = true;
		}
	}
	
	/**
	 * Sets the normal mapping state for the renderer.
	 * <p>
	 * The normal mapping state can be one of:
	 * <ul>
	 * <li>{@code BOOLEAN_FALSE}</li>
	 * <li>{@code BOOLEAN_TRUE}</li>
	 * </ul>
	 * 
	 * @param rendererNormalMapping the normal mapping state for the renderer
	 */
	public final void setRendererNormalMapping(final int rendererNormalMapping) {
		if(this.rendererNormalMapping != rendererNormalMapping) {
			switch(rendererNormalMapping) {
				case BOOLEAN_FALSE:
				case BOOLEAN_TRUE:
					this.rendererNormalMapping = rendererNormalMapping;
					this.hasChanged = true;
					
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sets the maximum ray depth for Path Tracing.
	 * 
	 * @param rendererPTRayDepthMaximum the maximum ray depth for Path Tracing
	 */
	public final void setRendererPTRayDepthMaximum(final int rendererPTRayDepthMaximum) {
		if(this.rendererPTRayDepthMaximum != rendererPTRayDepthMaximum) {
			this.rendererPTRayDepthMaximum = rendererPTRayDepthMaximum;
			this.hasChanged = true;
		}
	}
	
	/**
	 * Sets the ray depth to begin Russian Roulette path termination for Path Tracing.
	 * 
	 * @param rendererPTRayDepthRussianRoulette the ray depth to begin Russian Roulette path termination for Path Tracing
	 */
	public void setRendererPTRayDepthRussianRoulette(final int rendererPTRayDepthRussianRoulette) {
		if(this.rendererPTRayDepthRussianRoulette != rendererPTRayDepthRussianRoulette) {
			this.rendererPTRayDepthRussianRoulette = rendererPTRayDepthRussianRoulette;
			this.hasChanged = true;
		}
	}
	
	/**
	 * Sets the renderer type.
	 * <p>
	 * The renderer type can be one of:
	 * <ul>
	 * <li>{@code RENDERER_TYPE_AMBIENT_OCCLUSION}</li>
	 * <li>{@code RENDERER_TYPE_PATH_TRACER}</li>
	 * <li>{@code RENDERER_TYPE_RAY_CASTER}</li>
	 * <li>{@code RENDERER_TYPE_RAY_MARCHER}</li>
	 * <li>{@code RENDERER_TYPE_RAY_TRACER}</li>
	 * <li>{@code RENDERER_TYPE_SURFACE_NORMALS}</li>
	 * </ul>
	 * 
	 * @param rendererType the renderer type
	 */
	public final void setRendererType(final int rendererType) {
		if(this.rendererType != rendererType) {
			switch(rendererType) {
				case RENDERER_TYPE_AMBIENT_OCCLUSION:
				case RENDERER_TYPE_PATH_TRACER:
				case RENDERER_TYPE_RAY_CASTER:
				case RENDERER_TYPE_RAY_MARCHER:
				case RENDERER_TYPE_RAY_TRACER:
				case RENDERER_TYPE_SURFACE_NORMALS:
					this.rendererType = rendererType;
					this.hasChanged = true;
					
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sets the wireframes state for the renderer.
	 * <p>
	 * The wireframes state can be one of:
	 * <ul>
	 * <li>{@code BOOLEAN_FALSE}</li>
	 * <li>{@code BOOLEAN_TRUE}</li>
	 * </ul>
	 * 
	 * @param rendererWireframes the wireframes state for the renderer
	 */
	public final void setRendererWireframes(final int rendererWireframes) {
		if(this.rendererWireframes != rendererWireframes) {
			switch(rendererWireframes) {
				case BOOLEAN_FALSE:
				case BOOLEAN_TRUE:
					this.rendererWireframes = rendererWireframes;
					this.hasChanged = true;
					
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sets the shader type.
	 * <p>
	 * The shader type can be one of:
	 * <ul>
	 * <li>{@code SHADER_TYPE_FLAT}</li>
	 * <li>{@code SHADER_TYPE_GOURAUD}</li>
	 * </ul>
	 * 
	 * @param shaderType the shader type
	 */
	public final void setShaderType(final int shaderType) {
		if(this.shaderType != shaderType) {
			switch(shaderType) {
				case SHADER_TYPE_FLAT:
				case SHADER_TYPE_GOURAUD:
					this.shaderType = shaderType;
					this.hasChanged = true;
					
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sets the tone mapper type.
	 * <p>
	 * The tone mapper type can be one of:
	 * <ul>
	 * <li>{@code TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED}</li>
	 * <li>{@code TONE_MAPPER_TYPE_REINHARD}</li>
	 * <li>{@code TONE_MAPPER_TYPE_REINHARD_MODIFIED_1}</li>
	 * <li>{@code TONE_MAPPER_TYPE_REINHARD_MODIFIED_2}</li>
	 * </ul>
	 * 
	 * @param toneMapperType the tone mapper type
	 */
	public final void setToneMapperType(final int toneMapperType) {
		if(this.toneMapperType != toneMapperType) {
			switch(toneMapperType) {
				case TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED:
				case TONE_MAPPER_TYPE_REINHARD:
				case TONE_MAPPER_TYPE_REINHARD_MODIFIED_1:
				case TONE_MAPPER_TYPE_REINHARD_MODIFIED_2:
					this.toneMapperType = toneMapperType;
					this.hasChanged = true;
					
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Sets the exposure for the tone mapper.
	 * 
	 * @param toneMapperExposure the new exposure
	 */
	public final void setToneMapperExposure(final float toneMapperExposure) {
		if(Float.compare(this.toneMapperExposure, toneMapperExposure) != 0) {
			this.toneMapperExposure = toneMapperExposure;
			this.hasChanged = true;
		}
	}
	
	/**
	 * Toggles the material for the selected primitive.
	 */
	public abstract void togglePrimitiveMaterial();
	
	/**
	 * Toggles the primitive selection.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 */
	public abstract void togglePrimitiveSelection(final int x, final int y);
	
	/**
	 * Toggles the normal mapping state for the renderer.
	 */
	public final void toggleRendererNormalMapping() {
		switch(this.rendererNormalMapping) {
			case BOOLEAN_FALSE:
				this.rendererNormalMapping = BOOLEAN_TRUE;
				this.hasChanged = true;
				
				break;
			case BOOLEAN_TRUE:
				this.rendererNormalMapping = BOOLEAN_FALSE;
				this.hasChanged = true;
				
				break;
			default:
				break;
		}
	}
	
	/**
	 * Toggles to the next renderer type.
	 */
	public final void toggleRendererType() {
		switch(this.rendererType) {
			case RENDERER_TYPE_AMBIENT_OCCLUSION:
				this.rendererType = RENDERER_TYPE_PATH_TRACER;
				this.hasChanged = true;
				
				break;
			case RENDERER_TYPE_PATH_TRACER:
				this.rendererType = RENDERER_TYPE_RAY_CASTER;
				this.hasChanged = true;
				
				break;
			case RENDERER_TYPE_RAY_CASTER:
				this.rendererType = RENDERER_TYPE_RAY_MARCHER;
				this.hasChanged = true;
				
				break;
			case RENDERER_TYPE_RAY_MARCHER:
				this.rendererType = RENDERER_TYPE_RAY_TRACER;
				this.hasChanged = true;
				
				break;
			case RENDERER_TYPE_RAY_TRACER:
				this.rendererType = RENDERER_TYPE_SURFACE_NORMALS;
				this.hasChanged = true;
				
				break;
			case RENDERER_TYPE_SURFACE_NORMALS:
				this.rendererType = RENDERER_TYPE_AMBIENT_OCCLUSION;
				this.hasChanged = true;
				
				break;
			default:
				break;
		}
	}
	
	/**
	 * Toggles the wireframes state for the renderer.
	 */
	public final void toggleRendererWireframes() {
		switch(this.rendererWireframes) {
			case BOOLEAN_FALSE:
				this.rendererWireframes = BOOLEAN_TRUE;
				this.hasChanged = true;
				
				break;
			case BOOLEAN_TRUE:
				this.rendererWireframes = BOOLEAN_FALSE;
				this.hasChanged = true;
				
				break;
			default:
				break;
		}
	}
	
	/**
	 * Toggles to the next shader type.
	 */
	public final void toggleShaderType() {
		switch(this.shaderType) {
			case SHADER_TYPE_FLAT:
				this.shaderType = SHADER_TYPE_GOURAUD;
				this.hasChanged = true;
				
				break;
			case SHADER_TYPE_GOURAUD:
				this.shaderType = SHADER_TYPE_FLAT;
				this.hasChanged = true;
				
				break;
			default:
				break;
		}
	}
	
	/**
	 * Toggles the sky.
	 */
	public abstract void toggleSky();
	
	/**
	 * Toggles the sun.
	 */
	public abstract void toggleSun();
	
	/**
	 * Updates all necessary variables in this {@code AbstractRendererKernel} instance.
	 * <p>
	 * If {@code imageDataByte} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param resolutionX the resolution along the X-axis
	 * @param resolutionY the resolution along the Y-axis
	 * @param imageDataByte a {@code byte} array with image data
	 * @param localSize the local size
	 * @throws NullPointerException thrown if, and only if, {@code imageDataByte} is {@code null}
	 */
	public abstract void update(final int resolutionX, final int resolutionY, final byte[] imageDataByte, final int localSize);
	
	/**
	 * Updates the {@link Camera} and the variables related to it.
	 */
	public abstract void updateCamera();
	
	/**
	 * Updates the {@link Primitive}s.
	 */
	public abstract void updatePrimitives();
	
	/**
	 * Updates the variables related to the sun and sky.
	 */
	public abstract void updateSunAndSky();
}