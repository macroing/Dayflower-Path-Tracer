/**
 * Copyright 2015 - 2019 J&#246;rgen Lundgren
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
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.compiler.CompiledScene;
import org.dayflower.pathtracer.scene.loader.SceneLoader;

/**
 * This {@code AbstractRendererKernel} class is an abstract extension of {@link AbstractKernel} with the basic functionality that is needed by Dayflower to render.
 * <p>
 * The implementations of this class should take care of the rendering itself. Some implementations may run on the CPU, whereas others might run on the GPU.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractRendererKernel extends AbstractKernel {
	/**
	 * An {@code int} representation of a {@code boolean} of {@code false}.
	 */
	public static final int BOOLEAN_FALSE = 0;
	
	/**
	 * An {@code int} representation of a {@code boolean} of {@code true}.
	 */
	public static final int BOOLEAN_TRUE = 1;
	
	/**
	 * The renderer type for the Ambient Occlusion, which is {@code 0}.
	 */
	public static final int RENDERER_TYPE_AMBIENT_OCCLUSION = 0;
	
	/**
	 * The renderer type for the Path Tracer, which is {@code 1}.
	 */
	public static final int RENDERER_TYPE_PATH_TRACER = 1;
	
	/**
	 * The renderer type for the Ray Caster, which is {@code 2}.
	 */
	public static final int RENDERER_TYPE_RAY_CASTER = 2;
	
	/**
	 * The renderer type for the Ray Marcher, which is {@code 3}.
	 */
	public static final int RENDERER_TYPE_RAY_MARCHER = 3;
	
	/**
	 * The renderer type for the Ray Tracer, which is {@code 4}.
	 */
	public static final int RENDERER_TYPE_RAY_TRACER = 4;
	
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
	
	float rendererAOMaximumDistance;
	float toneMapperExposure;
	int rendererPTRayDepthMaximum;
	int rendererPTRayDepthRussianRoulette;
	int rendererType;
	final int resolutionX;
	final int resolutionY;
	int toneMapperType;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final CompiledScene compiledScene;
	private final Scene scene;
	private final SceneLoader sceneLoader;
	private boolean hasChanged;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	protected AbstractRendererKernel(final int resolutionX, final int resolutionY, final SceneLoader sceneLoader) {
		this.rendererAOMaximumDistance = 200.0F;
		this.toneMapperExposure = 1.0F;
		this.rendererPTRayDepthMaximum = 5;
		this.rendererPTRayDepthRussianRoulette = 5;
		this.rendererType = RENDERER_TYPE_PATH_TRACER;
		this.resolutionX = resolutionX;
		this.resolutionY = resolutionY;
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
	 * Returns the resolution along the X-axis.
	 * <p>
	 * This is also known as the width.
	 * 
	 * @return the resolution along the X-axis
	 */
	public final int getResolutionX() {
		return this.resolutionX;
	}
	
	/**
	 * Returns the resolution along the Y-axis.
	 * <p>
	 * This is also known as the height.
	 * 
	 * @return the resolution along the Y-axis
	 */
	public final int getResolutionY() {
		return this.resolutionY;
	}
	
	/**
	 * Resets the changed flag.
	 */
	public final void resetChanged() {
		this.hasChanged = false;
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
					this.rendererType = rendererType;
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
				this.rendererType = RENDERER_TYPE_AMBIENT_OCCLUSION;
				this.hasChanged = true;
				
				break;
			default:
				break;
		}
	}
}