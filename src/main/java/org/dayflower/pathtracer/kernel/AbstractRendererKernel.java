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

import java.util.Objects;
import java.util.Optional;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Sky;

/**
 * An abstract extension of the {@code AbstractKernel} class that performs Path Tracing, Ray Casting and Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractRendererKernel extends AbstractKernel {
	private final Camera camera;
	private final CompiledScene compiledScene;
	private final int height;
	private final int width;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractRendererKernel} instance.
	 * <p>
	 * If either {@code camera} or {@code compiledScene} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param width the width to use
	 * @param height the height to use
	 * @param camera the {@link Camera} to use
	 * @param compiledScene the {@link CompiledScene} to use
	 * @throws NullPointerException thrown if, and only if, either {@code camera} or {@code compiledScene} are {@code null}
	 */
	protected AbstractRendererKernel(final int width, final int height, final Camera camera, final CompiledScene compiledScene) {
		this.width = width;
		this.height = height;
		this.camera = camera;
		this.compiledScene = Objects.requireNonNull(compiledScene, "compiledScene == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compiles this {@code AbstractRendererKernel} instance.
	 * <p>
	 * Returns itself for method chaining.
	 * <p>
	 * If {@code pixels} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param pixels a {@code byte} array with the pixels
	 * @param width the width to use
	 * @param height the height to use
	 * @return itself for method chaining
	 * @throws NullPointerException thrown if, and only if, {@code pixels} is {@code null}
	 */
	public AbstractRendererKernel compile(final byte[] pixels, final int width, final int height) {
		return this;
	}
	
	/**
	 * Resets this {@code AbstractRendererKernel} instance.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @return itself for method chaining
	 */
	public AbstractRendererKernel reset() {
		return this;
	}
	
	/**
	 * Updates the local variables.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @param localSize the local size
	 * @return itself for method chaining
	 */
	public AbstractRendererKernel updateLocalVariables(final int localSize) {
		return this;
	}
	
	/**
	 * Updates the variables related to the {@link Sky}.
	 * <p>
	 * Returns itself for method chaining.
	 * 
	 * @return itself for method chaining
	 */
	public AbstractRendererKernel updateSky() {
		return this;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ambient Occlusion is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ambient Occlusion is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isAmbientOcclusion() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Grayscale effect is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Grayscale effect is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isEffectGrayScale() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Sepia Tone effect is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Sepia Tone effect is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isEffectSepiaTone() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, Normal Mapping is activaded, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Normal Mapping is activaded, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isNormalMapping() {
		return true;
	}
	
	/**
	 * Returns {@code true} if, and only if, Path Tracing is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Path Tracing is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isPathTracing() {
		return true;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Casting is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Casting is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isRayCasting() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Marching is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Marching is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isRayMarching() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, Ray Tracing is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Ray Tracing is enabled, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isRayTracing() {
		return false;
	}
	
//	TODO: Add Javadocs!
	@SuppressWarnings("static-method")
	public boolean isResetRequired() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, Flat Shading is used, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Flat Shading is used, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isShadingFlat() {
		return false;
	}
	
	/**
	 * Returns {@code true} if, and only if, Gouraud Shading is used, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, Gouraud Shading is used, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isShadingGouraud() {
		return true;
	}
	
	/**
	 * Returns {@code true} if, and only if, the sky is showing clouds, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the sky is showing clouds, {@code false} otherwise
	 */
	@SuppressWarnings("static-method")
	public boolean isShowingClouds() {
		return false;
	}
	
	/**
	 * Returns the {@code byte} array with the pixels.
	 * 
	 * @return the {@code byte} array with the pixels
	 */
	@SuppressWarnings("static-method")
	public byte[] getPixels() {
		return new byte[3];
	}
	
	/**
	 * Returns the {@link CompiledScene}.
	 * 
	 * @return the {@code CompiledScene}
	 */
	public final CompiledScene getCompiledScene() {
		return this.compiledScene;
	}
	
	/**
	 * Returns the maximum distance for Ambient Occlusion.
	 * 
	 * @return the maximum distance for Ambient Occlusion
	 */
	@SuppressWarnings("static-method")
	public float getMaximumDistanceAO() {
		return 200.0F;
	}
	
	/**
	 * Returns the maximum depth for path termination.
	 * 
	 * @return the maximum depth for path termination
	 */
	public abstract int getDepthMaximum();
	
	/**
	 * Returns the height.
	 * 
	 * @return the height
	 */
	public final int getHeight() {
		return this.height;
	}
	
//	TODO: Add Javadocs!
	@SuppressWarnings("static-method")
	public int getSelectedShapeIndex() {
		return -1;
	}
	
	/**
	 * Returns the width.
	 * 
	 * @return the width
	 */
	public final int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the depth used for Russian Roulette path termination.
	 * 
	 * @return the depth used for Russian Roulette path termination
	 */
	public abstract int getDepthRussianRoulette();
	
//	TODO: Add Javadocs!
	public abstract int[] getShapeOffsetsForPrimaryRay();
	
	/**
	 * Returns the optional {@link Camera} instance assigned to this {@code AbstractRendererKernel} instance.
	 * 
	 * @return the optional {@code Camera} instance assigned to this {@code AbstractRendererKernel} instance
	 */
	public final Optional<Camera> getCamera() {
		return Optional.ofNullable(this.camera);
	}
	
	/**
	 * Draws a line from {@code x0} and {@code y0} to {@code x1} and {@code y1} with a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x0 the X-coordinate of the first end of the line
	 * @param y0 the Y-coordinate of the first end of the line
	 * @param x1 the X-coordinate of the second end of the line
	 * @param y1 the Y-coordinate of the second end of the line
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public void drawLine(final int x0, final int y0, final int x1, final int y1, final Color color) {
//		Do nothing.
	}
	
	/**
	 * Draws a rectangle from {@code x} and {@code y} to {@code x + width} and {@code y + height} with a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x the X-coordinate to start from
	 * @param y the Y-coordinate to start from
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public void drawRectangle(final int x, final int y, final int width, final int height, final Color color) {
//		Do nothing.
	}
	
	/**
	 * Draws a triangle with a color of {@code color}.
	 * <p>
	 * Calling this method is equivalent to the following:
	 * <pre>
	 * {@code
	 * drawLine(x0, y0, x1, y1, color);
	 * drawLine(x1, y1, x2, y2, color);
	 * drawLine(x2, y2, x0, y0, color);
	 * }
	 * </pre>
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x0 the X-coordinate of the first point
	 * @param y0 the Y-coordinate of the first point
	 * @param x1 the X-coordinate of the second point
	 * @param y1 the Y-coordinate of the second point
	 * @param x2 the X-coordinate of the third point
	 * @param y2 the Y-coordinate of the third point
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public void drawTriangle(final int x0, final int y0, final int x1, final int y1, final int x2, final int y2, final Color color) {
//		Do nothing.
	}
	
	/**
	 * Fills a circle with a center of {@code x} and {@code y}, a radius of {@code radius} and a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x the X-coordinate of the center
	 * @param y the Y-coordinate of the center
	 * @param radius the radius
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public void fillCircle(final int x, final int y, final int radius, final Color color) {
//		Do nothing.
	}
	
	/**
	 * Fills a rectangle from {@code x} and {@code y} to {@code x + width} and {@code y + height} with a color of {@code color}.
	 * <p>
	 * If {@code color} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param x the X-coordinate to start from
	 * @param y the Y-coordinate to start from
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param color the {@link Color} to use
	 * @throws NullPointerException thrown if, and only if, {@code color} is {@code null}
	 */
	public void fillRectangle(final int x, final int y, final int width, final int height, final Color color) {
//		Do nothing.
	}
	
	/**
	 * Sets whether Ambient Occlusion should be enabled or disabled.
	 * <p>
	 * If {@code isAmbientOcclusion} is {@code false}, the renderer will be a Path Tracer.
	 * 
	 * @param isAmbientOcclusion the Ambient Occlusion state to set
	 */
	public void setAmbientOcclusion(final boolean isAmbientOcclusion) {
//		Do nothing.
	}
	
	/**
	 * Sets the maximum depth to be used for path termination.
	 * 
	 * @param depthMaximum the maximum depth
	 */
	public void setDepthMaximum(final int depthMaximum) {
//		Do nothing.
	}
	
	/**
	 * Sets the depth to be used for Russian Roulette path termination.
	 * 
	 * @param depthRussianRoulette the depth to use
	 */
	public void setDepthRussianRoulette(final int depthRussianRoulette) {
//		Do nothing.
	}
	
	/**
	 * Enables or disables the Grayscale effect.
	 * 
	 * @param isEffectGrayScale {@code true} if the Grayscale effect is enabled, {@code false} otherwise
	 */
	public void setEffectGrayScale(final boolean isEffectGrayScale) {
//		Do nothing.
	}
	
	/**
	 * Enables or disables the Sepia Tone effect.
	 * 
	 * @param isEffectSepiaTone {@code true} if the Sepia Tone effect is enabled, {@code false} otherwise
	 */
	public void setEffectSepiaTone(final boolean isEffectSepiaTone) {
//		Do nothing.
	}
	
	/**
	 * Sets the maximum distance for Ambient Occlusion.
	 * 
	 * @param maximumDistanceAO the new maximum distance for Ambient Occlusion
	 */
	public void setMaximumDistanceAO(final float maximumDistanceAO) {
//		Do nothing.
	}
	
	/**
	 * Sets the Normal Mapping state.
	 * 
	 * @param isNormalMapping {@code true} if, and only if, Normal Mapping is activated, {@code false} otherwise
	 */
	public void setNormalMapping(final boolean isNormalMapping) {
//		Do nothing.
	}
	
	/**
	 * Sets whether Path Tracing should be enabled or disabled.
	 * <p>
	 * If {@code isPathTracing} is {@code false}, the renderer will be a Ray Caster.
	 * 
	 * @param isPathTracing the Path Tracing state to set
	 */
	public void setPathTracing(final boolean isPathTracing) {
//		Do nothing.
	}
	
	/**
	 * Sets whether Ray Casting should be enabled or disabled.
	 * <p>
	 * If {@code isRayCasting} is {@code false}, the renderer will be a Ray Marcher.
	 * 
	 * @param isRayCasting the Ray Casting state to set
	 */
	public void setRayCasting(final boolean isRayCasting) {
//		Do nothing.
	}
	
	/**
	 * Sets whether Ray Marching should be enabled or disabled.
	 * <p>
	 * If {@code isRayMarching} is {@code false}, the renderer will be a Ray Tracer.
	 * 
	 * @param isRayMarching the Ray Marching state to set
	 */
	public void setRayMarching(final boolean isRayMarching) {
//		Do nothing.
	}
	
	/**
	 * Sets whether Ray Tracing should be enabled or disabled.
	 * <p>
	 * If {@code isRayTracing} is {@code false}, the renderer will be Ambient Occlusion.
	 * 
	 * @param isRayTracing the Ray Tracing state to set
	 */
	public void setRayTracing(final boolean isRayTracing) {
//		Do nothing.
	}
	
//	TODO: Add Javadocs!
	public void setSelectedShapeIndex(@SuppressWarnings("unused") final int selectedShapeIndex) {
//		Do nothing.
	}
	
	/**
	 * Sets Flat Shading.
	 */
	public void setShadingFlat() {
//		Do nothing.
	}
	
	/**
	 * Sets Gouraud Shading.
	 */
	public void setShadingGouraud() {
//		Do nothing.
	}
	
	/**
	 * Sets whether the sky is showing clouds or not.
	 * 
	 * @param isShowingClouds {@code true} if, and only if, the sky is showing clouds, {@code false} otherwise
	 */
	public void setShowingClouds(final boolean isShowingClouds) {
//		Do nothing.
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Filmic Curve version 1.
	 */
	public void setToneMappingAndGammaCorrectionFilmicCurve1() {
//		Do nothing.
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Filmic Curve version 2.
	 */
	public void setToneMappingAndGammaCorrectionFilmicCurve2() {
//		Do nothing.
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Linear.
	 */
	public void setToneMappingAndGammaCorrectionLinear() {
//		Do nothing.
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Reinhard version 1.
	 */
	public void setToneMappingAndGammaCorrectionReinhard1() {
//		Do nothing.
	}
	
	/**
	 * Sets the Tone Mapping and Gamma Correction to Reinhard version 2.
	 */
	public void setToneMappingAndGammaCorrectionReinhard2() {
//		Do nothing.
	}
	
	/**
	 * Toggles the visibility for the clouds in the sky.
	 */
	public void toggleClouds() {
//		Do nothing.
	}
	
	/**
	 * Toggles the material for the selected shape.
	 */
	public void toggleMaterial() {
//		Do nothing.
	}
	
	/**
	 * Toggles to the next renderer.
	 */
	public void toggleRenderer() {
//		Do nothing.
	}
	
	/**
	 * Toggles to the next shading.
	 */
	public void toggleShading() {
//		Do nothing.
	}
	
	/**
	 * Toggles the sun and sky.
	 */
	public void toggleSunAndSky() {
//		Do nothing.
	}
	
//	TODO: Add Javadocs!
	public void updateResetStatus() {
//		Do nothing.
	}
}