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

import java.util.Optional;

import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.PrimitiveIntersection;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.loader.SceneLoader;
import org.macroing.image4j.Color;
import org.macroing.math4j.Ray3F;

/**
 * A {@code CPURendererKernel} is an extension of the {@code AbstractRendererKernel} class that performs 3D-rendering on the CPU.
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
public final class CPURendererKernel extends AbstractRendererKernel {
	private PrimitiveIntersection[] primitiveIntersections;
	private Ray3F[] primaryRays;
	private int selectedPrimitiveIndex;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code CPURendererKernel} instance.
	 * <p>
	 * If {@code sceneLoader} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param sceneLoader the {@link SceneLoader} to use
	 * @throws NullPointerException thrown if, and only if, {@code sceneLoader} is {@code null}
	 */
	public CPURendererKernel(final SceneLoader sceneLoader) {
		super(sceneLoader);
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
		if(doCreatePrimaryRay()) {
			final SceneLoader sceneLoader = getSceneLoader();
			
			final Scene scene = sceneLoader.loadScene();
			
			final Optional<PrimitiveIntersection> optionalPrimitiveIntersection = scene.intersection(doGetPrimaryRay());
			
			if(optionalPrimitiveIntersection.isPresent()) {
				final PrimitiveIntersection primitiveIntersection = optionalPrimitiveIntersection.get();
				
				final Texture textureEmission = primitiveIntersection.getPrimitive().getSurface().getTextureAlbedo();
				
				final Color color = textureEmission.getColor(primitiveIntersection);
				
				filmAddColor(color.r, color.g, color.b);
			} else {
				filmAddColor(0.0F, 0.0F, 0.0F);
			}
		} else {
			filmSetColor(0.0F, 0.0F, 0.0F);
		}
		
		imageBegin();
		
		if(super.toneMapperType == TONE_MAPPER_TYPE_REINHARD) {
			imageSetReinhard(super.toneMapperExposure);
		} else if(super.toneMapperType == TONE_MAPPER_TYPE_REINHARD_MODIFIED_1) {
			imageSetReinhardModified1(super.toneMapperExposure);
		} else if(super.toneMapperType == TONE_MAPPER_TYPE_REINHARD_MODIFIED_2) {
			imageSetReinhardModified2(super.toneMapperExposure);
		} else if(super.toneMapperType == TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED) {
			imageSetFilmicCurveACESModified(super.toneMapperExposure);
		}
		
		imageRedoGammaCorrection();
		imageEnd();
	}
	
	/**
	 * Toggles the material for the selected primitive.
	 */
	@Override
	public void togglePrimitiveMaterial() {
//		TODO: Implement!
	}
	
	/**
	 * Toggles the primitive selection.
	 * 
	 * @param x the X-coordinate
	 * @param y the Y-coordinate
	 */
	@Override
	public void togglePrimitiveSelection(final int x, final int y) {
//		TODO: Implement!
	}
	
	/**
	 * Toggles the sky.
	 */
	@Override
	public void toggleSky() {
//		TODO: Implement!
	}
	
	/**
	 * Toggles the sun.
	 */
	@Override
	public void toggleSun() {
//		TODO: Implement!
	}
	
	/**
	 * Updates all necessary variables in this {@code CPURendererKernel} instance.
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
	@SuppressWarnings("deprecation")
	public void update(final int resolutionX, final int resolutionY, final byte[] imageDataByte, final int localSize) {
		update(resolutionX, resolutionY, imageDataByte);
		
		this.primitiveIntersections = new PrimitiveIntersection[resolutionX * resolutionY];
		this.primaryRays = new Ray3F[resolutionX * resolutionY];
		
		setExplicit(true);
		setExecutionMode(EXECUTION_MODE.JTP);
	}
	
	/**
	 * Updates the {@link Camera} and the variables related to it.
	 */
	@Override
	public void updateCamera() {
		final
		Camera camera = getCamera();
		camera.update();
	}
	
	/**
	 * Updates the {@link Primitive}s.
	 */
	@Override
	public void updatePrimitives() {
//		TODO: Implement!
	}
	
	/**
	 * Updates the variables related to the sun and sky.
	 */
	@Override
	public void updateSunAndSky() {
		setChanged(true);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Ray3F doGetPrimaryRay() {
		return this.primaryRays[getGlobalId()];
	}
	
	private boolean doCreatePrimaryRay() {
//		Retrieve the global ID:
		final int globalId = getGlobalId();
		
//		Retrieve the index:
		final int index = globalId;
		
//		Calculate the X- and Y-coordinates on the screen:
		final int y = index / super.resolutionX;
		final int x = index - y * super.resolutionX;
		
		final float sampleX = isRendererTypePathTracer() ? doCreateTriangleFilter(nextFloat()) : 0.5F;
		final float sampleY = isRendererTypePathTracer() ? doCreateTriangleFilter(nextFloat()) : 0.5F;
		
		final Optional<Ray3F> optionalRay = getCamera().createPrimaryRay(x, y, sampleX, sampleY);
		
		if(optionalRay.isPresent()) {
			final Ray3F ray = optionalRay.get();
			
			this.primaryRays[index] = ray;
			
			return true;
		}
		
		return false;
	}
	
	private float doCreateTriangleFilter(final float sample) {
		return sample < 0.5F ? sqrt(2.0F * sample) - 1.0F : 1.0F - sqrt(2.0F - 2.0F * sample);
	}
}