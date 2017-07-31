/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.main;

import static org.dayflower.pathtracer.math.Math2.tan;
import static org.dayflower.pathtracer.math.Math2.toRadians;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import com.amd.aparapi.Range;

import org.dayflower.pathtracer.application.AbstractApplication;
import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.filter.ConvolutionFilters;
import org.dayflower.pathtracer.kernel.AbstractRendererKernel;
import org.dayflower.pathtracer.kernel.CompiledScene;
import org.dayflower.pathtracer.kernel.RendererKernel;
import org.dayflower.pathtracer.scene.Matrix44;
import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.util.FPSCounter;

/**
 * An implementation of {@link AbstractApplication} that performs Path Tracing, Ray Casting or Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class TestApplication extends AbstractApplication {
	private static final String ENGINE_NAME = "Dayflower Engine";
	private static final String ENGINE_VERSION = "v.0.0.20";
	private static final String SETTING_NAME_BOUNDING_VOLUME_HIERARCHY_WIREFRAME = "BoundingVolumeHierarchy.Wireframe";
	private static final String SETTING_NAME_FILTER_BLUR = "Filter.Blur";
	private static final String SETTING_NAME_FILTER_DETECT_EDGES = "Filter.DetectEdges";
	private static final String SETTING_NAME_FILTER_EMBOSS = "Filter.Emboss";
	private static final String SETTING_NAME_FILTER_GRADIENT_HORIZONTAL = "Filter.Gradient.Horizontal";
	private static final String SETTING_NAME_FILTER_GRADIENT_VERTICAL = "Filter.Gradient.Vertical";
	private static final String SETTING_NAME_FILTER_SHARPEN = "Filter.Sharpen";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicBoolean hasInitialized = new AtomicBoolean();
	private byte[] pixels;
	private final Camera camera = new Camera();
	private final Label labelApertureRadius = new Label("Aperture radius: N/A");
	private final Label labelFieldOfView = new Label("FOV: N/A - N/A");
	private final Label labelFocalDistance = new Label("Focal distance: N/A");
	private final Label labelFPS = new Label("FPS: 0");
	private final Label labelRenderMode = new Label("Mode: GPU");
	private final Label labelRenderPass = new Label("Pass: 0");
	private final Label labelRenderTime = new Label("Time: 00:00:00");
	private final Label labelRenderType = new Label("Type: Path Tracer");
	private final Label labelSPS = new Label("SPS: 00000000");
	private final AbstractRendererKernel abstractRendererKernel;
	private final Scene scene = Scenes.newMaterialShowcaseScene();
	private final Sky sky;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code TestApplication} instance.
	 */
	public TestApplication() {
		super(String.format("%s %s", ENGINE_NAME, ENGINE_VERSION));
		
		this.sky = new Sky();
		this.abstractRendererKernel = new RendererKernel(false, getCanvasWidth(), getCanvasHeight(), this.camera, this.sky, String.format("%s", Dayflower.getSceneFilename(this.scene.getName() + ".scene")), 1.0F);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Called when pixels can be configured at start.
	 * 
	 * @param pixels a {@code byte} array with pixel data
	 */
	@Override
	protected void doConfigurePixels(final byte[] pixels) {
		this.pixels = pixels;
		
		final
		Camera camera = this.camera;
		camera.setApertureRadius(0.0F);
		camera.setCameraPredicate(this::doTest);
		camera.setCenter(55.0F, 42.0F, 155.6F);
		camera.setFieldOfViewX(70.0F);
		camera.setFocalDistance(30.0F);
		camera.setPitch(0.0F);
		camera.setRadius(16.0F);
		camera.setResolution(800.0F / getCanvasWidthScale(), 800.0F / getCanvasHeightScale());
		camera.setWalkLockEnabled(true);
		camera.setYaw(0.0F);
		camera.update();
		
		this.hasInitialized.set(true);
	}
	
	/**
	 * Called when UI-configuration can be performed at start.
	 * 
	 * @param hBox a {@code HBox} to add UI-controls to
	 */
	@Override
	protected void doConfigureUI(final HBox hBox) {
		final
		Region region0 = new Region();
		region0.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region1 = new Region();
		region1.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region2 = new Region();
		region2.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region3 = new Region();
		region3.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region4 = new Region();
		region4.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region5 = new Region();
		region5.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region6 = new Region();
		region6.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		Region region7 = new Region();
		region7.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		hBox.getChildren().addAll(this.labelRenderPass, region0, this.labelFPS, region1, this.labelSPS, region2, this.labelRenderTime, region3, this.labelRenderMode, region4, this.labelRenderType, region5, this.labelApertureRadius, region6, this.labelFocalDistance, region7, this.labelFieldOfView);
		
		printf("Engine Name: %s", ENGINE_NAME);
		printf("Engine Version: %s", ENGINE_VERSION);
		print("");
		print("Keys:");
		print("- ESC: Exit");
		print("- W: Walk forward");
		print("- A: Strafe left");
		print("- S: Walk backward");
		print("- D: Strafe right");
		print("- E: Increase altitude");
		print("- R: Decrease altitude");
		print("- T: Increase aperture diameter");
		print("- Y: Decrease aperture diameter");
		print("- U: Increase focal distance");
		print("- I: Decrease focal distance");
		print("- O: Increase field of view for X");
		print("- P: Decrease field of view for X");
		print("- F: Toggle Wireframe");
		print("- H: Toggle between Path Tracing, Ray Casting and Ray Marching");
		print("- K: Toggle walk-lock");
		print("- L: Toggle mouse recentering and cursor visibility");
		print("- C: Toggle between camera lenses");
		print("- B: Toggle Normal Mapping");
		print("- M: Increase maximum ray depth");
		print("- N: Decrease maximum ray depth");
		print("- UP ARROW: Decrease pitch");
		print("- LEFT ARROW: Increase yaw");
		print("- DOWN ARROW: Increase pitch");
		print("- RIGHT ARROW: Decrease yaw");
		print("- MOVE MOUSE: Look around");
		print("- 1: Toggle Blur effect");
		print("- 2: Toggle Edge Detection effect");
		print("- 3: Toggle Emboss effect");
		print("- 4: Toggle Horizontal Gradient effect");
		print("- 5: Toggle Vertical Gradient effect");
		print("- 6: Toggle Sharpen effect");
		print("- 7: Toggle Grayscale effect");
		print("- 8: Toggle Sepia Tone effect");
		print("- 9: Toggle between Flat Shading and Gouraud Shading");
		print("- NUMPAD 0: Use Tone Mapping and Gamma Correction Filmic Curve");
		print("- NUMPAD 1: Use Tone Mapping and Gamma Correction Linear");
		print("- NUMPAD 2: Use Tone Mapping and Gamma Correction Reinhard version 1");
		print("- NUMPAD 3: Use Tone Mapping and Gamma Correction Reinhard version 2");
		print("");
		print("Supported Features:");
		print("- Shape: Plane");
		print("- Shape: Sphere");
		print("- Shape: Triangle");
		print("- Material: Clear Coat (Reflection + Diffuse)");
		print("- Material: Diffuse (Lambertian)");
		print("- Material: Metal (Phong)");
		print("- Material: Glass (Reflection + Refraction)");
		print("- Material: Mirror (Reflection)");
		print("- Texture: Checkerboard");
		print("- Texture: Image");
		print("- Texture: Solid");
		print("- Perez Sun Sky Model");
		print("- Normal Mapping: Texture: Image");
		print("- Normal Mapping: Perlin Noise");
		print("- Cosine-Weighted Hemisphere-Sampling of Sun");
		print("- Acceleration Structure: Bounding Volume Hierarchy");
		print("- Camera Lens: Thin");
		print("- Camera Lens: Fisheye");
		print("- Effect: Blur");
		print("- Effect: Detect Edges");
		print("- Effect: Emboss");
		print("- Effect: Gradient: Horizontal");
		print("- Effect: Gradient: Vertical");
		print("- Effect: Grayscale");
		print("- Effect: Sepia Tone");
		print("- Effect: Sharpen");
		print("- Renderer: Path Tracing");
		print("- Renderer: Ray Casting");
		print("- Renderer: Ray Marching");
		print("- Tone Mapping and Gamma Correction: Filmic Curve");
		print("- Tone Mapping and Gamma Correction: Linear");
		print("- Tone Mapping and Gamma Correction: Reinhard version 1");
		print("- Tone Mapping and Gamma Correction: Reinhard version 2");
		
		setCursorHidden(true);
		setRecenteringMouse(true);
	}
	
	/**
	 * Called when the mouse is dragged.
	 * 
	 * @param x the new X-coordinate
	 * @param y the new Y-coordinate
	 */
	@Override
	protected void onMouseDragged(final float x, final float y) {
		this.camera.changeYaw(x * 0.005F);
		this.camera.changePitch(-(y * 0.005F));
	}
	
	/**
	 * Called when the mouse is moved.
	 * 
	 * @param x the new X-coordinate
	 * @param y the new Y-coordinate
	 */
	@Override
	protected void onMouseMoved(final float x, final float y) {
		if(isRecenteringMouse()) {
			this.camera.changeYaw(x * 0.005F);
			this.camera.changePitch(-(y * 0.005F));
		}
	}
	
	/**
	 * Called when rendering.
	 */
	@Override
	public void run() {
		while(!this.hasInitialized.get()) {
			try {
				Thread.sleep(100L);
			} catch(final InterruptedException e) {
//				Do nothing.
			}
		}
		
		final AtomicInteger renderPass = new AtomicInteger();
		
		final AtomicLong currentTimeMillis = new AtomicLong(System.currentTimeMillis());
		
		final Camera camera = this.camera;
		
		final FPSCounter fPSCounter = getFPSCounter();
		
		final Range range = Range.create(getCanvasWidth() * getCanvasHeight());
		
		final
		AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
		abstractRendererKernel.updateLocalVariables(range.getLocalSize(0));
		abstractRendererKernel.compile(this.pixels, getCanvasWidth(), getCanvasHeight());
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> abstractRendererKernel.dispose()));
		
		while(true) {
			final float velocity = abstractRendererKernel.isRayMarching() ? 1.0F : 250.0F;
			final float movement = fPSCounter.getFrameTimeMillis() / 1000.0F * velocity;
			
			if(isKeyPressed(KeyCode.A)) {
				camera.strafe(-movement);
			}
			
			if(isKeyPressed(KeyCode.B, true)) {
				abstractRendererKernel.setNormalMapping(!abstractRendererKernel.isNormalMapping());
			}
			
			if(isKeyPressed(KeyCode.C, true)) {
				camera.setThinCameraLens(!camera.isThinCameraLens());
			}
			
			if(isKeyPressed(KeyCode.D)) {
				camera.strafe(movement);
			}
			
			if(isKeyPressed(KeyCode.DIGIT1, true)) {
				toggleSetting(SETTING_NAME_FILTER_BLUR);
			}
			
			if(isKeyPressed(KeyCode.DIGIT2, true)) {
				toggleSetting(SETTING_NAME_FILTER_DETECT_EDGES);
			}
			
			if(isKeyPressed(KeyCode.DIGIT3, true)) {
				toggleSetting(SETTING_NAME_FILTER_EMBOSS);
			}
			
			if(isKeyPressed(KeyCode.DIGIT4, true)) {
				toggleSetting(SETTING_NAME_FILTER_GRADIENT_HORIZONTAL);
			}
			
			if(isKeyPressed(KeyCode.DIGIT5, true)) {
				toggleSetting(SETTING_NAME_FILTER_GRADIENT_VERTICAL);
			}
			
			if(isKeyPressed(KeyCode.DIGIT6, true)) {
				toggleSetting(SETTING_NAME_FILTER_SHARPEN);
			}
			
			if(isKeyPressed(KeyCode.DIGIT7, true)) {
				abstractRendererKernel.setEffectGrayScale(!abstractRendererKernel.isEffectGrayScale());
			}
			
			if(isKeyPressed(KeyCode.DIGIT8, true)) {
				abstractRendererKernel.setEffectSepiaTone(!abstractRendererKernel.isEffectSepiaTone());
			}
			
			if(isKeyPressed(KeyCode.DIGIT9, true)) {
				abstractRendererKernel.toggleShading();
			}
			
			if(isKeyPressed(KeyCode.DOWN)) {
				camera.changePitch(0.02F);
			}
			
			if(isKeyPressed(KeyCode.E)) {
				camera.changeAltitude(0.5F);
			}
			
			if(isKeyPressed(KeyCode.ESCAPE)) {
				abstractRendererKernel.dispose();
				
				Platform.exit();
				
				break;
			}
			
			if(isKeyPressed(KeyCode.F, true)) {
				toggleSetting(SETTING_NAME_BOUNDING_VOLUME_HIERARCHY_WIREFRAME);
			}
			
			if(isKeyPressed(KeyCode.G, true)) {
				this.sky.set(new Vector3(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), -1.0F).normalize());
				
				abstractRendererKernel.updateSky();
			}
			
			if(isKeyPressed(KeyCode.H, true)) {
				abstractRendererKernel.toggleRenderer();
			}
			
			if(isKeyPressed(KeyCode.I)) {
				camera.changeFocalDistance(-0.1F);
			}
			
			if(isKeyPressed(KeyCode.J)) {
				abstractRendererKernel.setAmplitude(0.5F);
				abstractRendererKernel.setFrequency(0.2F);
				abstractRendererKernel.setLacunarity(10.0F);
				abstractRendererKernel.setGain(1.0F / abstractRendererKernel.getLacunarity());
			}
			
			if(isKeyPressed(KeyCode.K, true)) {
				camera.setWalkLockEnabled(!camera.isWalkLockEnabled());
			}
			
			if(isKeyPressed(KeyCode.L, true)) {
				setCursorHidden(!isCursorHidden());
				setRecenteringMouse(!isRecenteringMouse());
			}
			
			if(isKeyPressed(KeyCode.LEFT)) {
				camera.changeYaw(0.02F);
			}
			
			if(isKeyPressed(KeyCode.M, true)) {
				abstractRendererKernel.setDepthMaximum(abstractRendererKernel.getDepthMaximum() + 1);
			}
			
			if(isKeyPressed(KeyCode.N, true)) {
				abstractRendererKernel.setDepthMaximum(Math.max(abstractRendererKernel.getDepthMaximum() - 1, 1));
			}
			
			if(isKeyPressed(KeyCode.NUMPAD0, true)) {
				abstractRendererKernel.setToneMappingAndGammaCorrectionFilmicCurve();
			}
			
			if(isKeyPressed(KeyCode.NUMPAD1, true)) {
				abstractRendererKernel.setToneMappingAndGammaCorrectionLinear();
			}
			
			if(isKeyPressed(KeyCode.NUMPAD2, true)) {
				abstractRendererKernel.setToneMappingAndGammaCorrectionReinhard1();
			}
			
			if(isKeyPressed(KeyCode.NUMPAD3, true)) {
				abstractRendererKernel.setToneMappingAndGammaCorrectionReinhard2();
			}
			
			if(isKeyPressed(KeyCode.NUMPAD4, false)) {
				abstractRendererKernel.setAmplitude(abstractRendererKernel.getAmplitude() + 0.005F);
			}
			
			if(isKeyPressed(KeyCode.NUMPAD5, true)) {
				abstractRendererKernel.setFrequency(abstractRendererKernel.getFrequency() + 0.005F);
			}
			
			if(isKeyPressed(KeyCode.NUMPAD6, false)) {
				abstractRendererKernel.setGain(abstractRendererKernel.getGain() + 0.005F);
			}
			
			if(isKeyPressed(KeyCode.NUMPAD7, false)) {
				abstractRendererKernel.setAmplitude(abstractRendererKernel.getAmplitude() - 0.005F);
			}
			
			if(isKeyPressed(KeyCode.NUMPAD8, true)) {
				abstractRendererKernel.setFrequency(abstractRendererKernel.getFrequency() - 0.005F);
			}
			
			if(isKeyPressed(KeyCode.NUMPAD9, false)) {
				abstractRendererKernel.setGain(abstractRendererKernel.getGain() - 0.005F);
			}
			
			if(isKeyPressed(KeyCode.O)) {
				camera.changeFieldOfViewX(0.1F);
			}
			
			if(isKeyPressed(KeyCode.P)) {
				camera.changeFieldOfViewX(-0.1F);
			}
			
			if(isKeyPressed(KeyCode.R)) {
				camera.changeAltitude(-0.5F);
			}
			
			if(isKeyPressed(KeyCode.RIGHT)) {
				camera.changeYaw(-0.02F);
			}
			
			if(isKeyPressed(KeyCode.S)) {
				camera.forward(-movement);
			}
			
			if(isKeyPressed(KeyCode.T)) {
				camera.changeApertureDiameter(0.1F);
			}
			
			if(isKeyPressed(KeyCode.U)) {
				camera.changeFocalDistance(0.1F);
			}
			
			if(isKeyPressed(KeyCode.UP)) {
				camera.changePitch(-0.02F);
			}
			
			if(isKeyPressed(KeyCode.W)) {
				camera.forward(movement);
			}
			
			if(isKeyPressed(KeyCode.Y)) {
				camera.changeApertureDiameter(-0.1F);
			}
			
			if(isDraggingMouse() || isMovingMouse() && isRecenteringMouse() || isPressingKey()) {
				abstractRendererKernel.reset();
				
				renderPass.set(0);
				
				currentTimeMillis.set(System.currentTimeMillis());
			}
			
			final
			Lock lock = getLock();
			lock.lock();
			
			try {
				abstractRendererKernel.execute(range);
				
				fPSCounter.update();
				
				abstractRendererKernel.get(abstractRendererKernel.getPixels());
				
				if(isSettingEnabled(SETTING_NAME_BOUNDING_VOLUME_HIERARCHY_WIREFRAME)) {
					final float width = abstractRendererKernel.getWidth();
					final float height = abstractRendererKernel.getHeight();
					final float angle = toRadians(40.0F);
//					final float distance = width * 0.5F / tan(angle * 0.5F);
					final float zNear = 1.0F;
					final float zFar = 1000.0F;//this.camera.getFocalDistance();
					
					final float eyeX = this.camera.getEyeX();
					final float eyeY = this.camera.getEyeY();
					final float eyeZ = this.camera.getEyeZ();
					
					final float uX = this.camera.getOrthoNormalBasisUX();
					final float uY = this.camera.getOrthoNormalBasisUY();
					final float uZ = this.camera.getOrthoNormalBasisUZ();
					final float vX = this.camera.getOrthoNormalBasisVX();
					final float vY = this.camera.getOrthoNormalBasisVY();
					final float vZ = this.camera.getOrthoNormalBasisVZ();
					final float wX = this.camera.getOrthoNormalBasisWX();
					final float wY = this.camera.getOrthoNormalBasisWY();
					final float wZ = this.camera.getOrthoNormalBasisWZ();
					
					final Matrix44 cameraToScreen = Matrix44.perspective(angle, width / height, zNear, zFar);
					final Matrix44 cameraToWorld = Matrix44.rotation(new Vector3(uX, uY, uZ), new Vector3(vX, vY, vZ), new Vector3(wX, wY, wZ)).multiply(Matrix44.translation(new Point3(eyeX, eyeY, eyeZ)));
//					final Matrix44 screenToCamera = cameraToScreen.inverse();
//					final Matrix44 screenToRaster = doCreateScreenToRaster(width, height);
//					final Matrix44 rasterToScreen = screenToRaster.inverse();
//					final Matrix44 rasterToCamera = screenToCamera.multiply(rasterToScreen);
//					final Matrix44 cameraToRaster = rasterToCamera.inverse();
					final Matrix44 worldToCamera = cameraToWorld.inverse();
//					final Matrix44 worldToRaster = cameraToRaster.multiply(worldToCamera);
					final Matrix44 worldToScreen = cameraToScreen.multiply(worldToCamera);
//					final Matrix44 rasterToWorld = worldToCamera.multiply(cameraToRaster);
//					final Matrix44 screenToWorld = worldToCamera.multiply(cameraToScreen);
					
//					final Matrix44 projection = Matrix44.perspective(angle, width / height, zNear, zFar);
//					final Matrix44 cameraToWorld = Matrix44.rotation(new Vector3(uX, uY, uZ), new Vector3(vX, vY, vZ), new Vector3(wX, wY, wZ)).multiply(Matrix44.translation(new Point3(eyeX, eyeY, eyeZ)));
//					final Matrix44 worldToCamera = cameraToWorld.inverse();
					
					final CompiledScene compiledScene = abstractRendererKernel.getCompiledScene();
					
					final float[] boundingVolumeHierarchy = compiledScene.getBoundingVolumeHierarchy();
					
					int boundingVolumeHierarchyOffset = 0;
					
					do {
						final float minimumX = boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 2];
						final float minimumY = boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3];
						final float minimumZ = boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 4];
						
						final float maximumX = boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 5];
						final float maximumY = boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 6];
						final float maximumZ = boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 7];
						
						final Point3 a = new Point3(minimumX, maximumY, minimumZ).transform(worldToScreen);
						final Point3 b = new Point3(minimumX, minimumY, minimumZ).transform(worldToScreen);
						final Point3 c = new Point3(maximumX, minimumY, minimumZ).transform(worldToScreen);
						final Point3 d = new Point3(maximumX, maximumY, minimumZ).transform(worldToScreen);
						final Point3 e = new Point3(minimumX, maximumY, maximumZ).transform(worldToScreen);
						final Point3 f = new Point3(minimumX, minimumY, maximumZ).transform(worldToScreen);
						final Point3 g = new Point3(maximumX, minimumY, maximumZ).transform(worldToScreen);
						final Point3 h = new Point3(maximumX, maximumY, maximumZ).transform(worldToScreen);
						
//						final Point3 a = doPerspectiveDivide(new Point3(minimumX, maximumY, minimumZ).transform(worldToCamera), width, height);
//						final Point3 b = doPerspectiveDivide(new Point3(minimumX, minimumY, minimumZ).transform(worldToCamera), width, height);
//						final Point3 c = doPerspectiveDivide(new Point3(maximumX, minimumY, minimumZ).transform(worldToCamera), width, height);
//						final Point3 d = doPerspectiveDivide(new Point3(maximumX, maximumY, minimumZ).transform(worldToCamera), width, height);
//						final Point3 e = doPerspectiveDivide(new Point3(minimumX, maximumY, maximumZ).transform(worldToCamera), width, height);
//						final Point3 f = doPerspectiveDivide(new Point3(minimumX, minimumY, maximumZ).transform(worldToCamera), width, height);
//						final Point3 g = doPerspectiveDivide(new Point3(maximumX, minimumY, maximumZ).transform(worldToCamera), width, height);
//						final Point3 h = doPerspectiveDivide(new Point3(maximumX, maximumY, maximumZ).transform(worldToCamera), width, height);
						
						abstractRendererKernel.drawLine((int)(a.x - 400.0F), (int)(-a.y + 600.0F), (int)(b.x - 400.0F), (int)(-b.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(b.x - 400.0F), (int)(-b.y + 600.0F), (int)(c.x - 400.0F), (int)(-c.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(c.x - 400.0F), (int)(-c.y + 600.0F), (int)(d.x - 400.0F), (int)(-d.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(d.x - 400.0F), (int)(-d.y + 600.0F), (int)(a.x - 400.0F), (int)(-a.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(c.x - 400.0F), (int)(-c.y + 600.0F), (int)(g.x - 400.0F), (int)(-g.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(g.x - 400.0F), (int)(-g.y + 600.0F), (int)(h.x - 400.0F), (int)(-h.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(h.x - 400.0F), (int)(-h.y + 600.0F), (int)(d.x - 400.0F), (int)(-d.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(h.x - 400.0F), (int)(-h.y + 600.0F), (int)(e.x - 400.0F), (int)(-e.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(e.x - 400.0F), (int)(-e.y + 600.0F), (int)(f.x - 400.0F), (int)(-f.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(f.x - 400.0F), (int)(-f.y + 600.0F), (int)(g.x - 400.0F), (int)(-g.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(a.x - 400.0F), (int)(-a.y + 600.0F), (int)(e.x - 400.0F), (int)(-e.y + 600.0F), Color.RED);
						abstractRendererKernel.drawLine((int)(b.x - 400.0F), (int)(-b.y + 600.0F), (int)(f.x - 400.0F), (int)(-f.y + 600.0F), Color.RED);
						
						final float type = boundingVolumeHierarchy[boundingVolumeHierarchyOffset];
						
						if(type == CompiledScene.BVH_NODE_TYPE_TREE) {
							boundingVolumeHierarchyOffset = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);
						} else {
							boundingVolumeHierarchyOffset = (int)(boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);
						}
					} while(boundingVolumeHierarchyOffset != -1);
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_BLUR)) {
					ConvolutionFilters.filterBlur(abstractRendererKernel.getPixels(), abstractRendererKernel.getWidth(), abstractRendererKernel.getHeight());
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_DETECT_EDGES)) {
					ConvolutionFilters.filterDetectEdges(abstractRendererKernel.getPixels(), abstractRendererKernel.getWidth(), abstractRendererKernel.getHeight());
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_EMBOSS)) {
					ConvolutionFilters.filterEmboss(abstractRendererKernel.getPixels(), abstractRendererKernel.getWidth(), abstractRendererKernel.getHeight());
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_GRADIENT_HORIZONTAL)) {
					ConvolutionFilters.filterGradientHorizontal(abstractRendererKernel.getPixels(), abstractRendererKernel.getWidth(), abstractRendererKernel.getHeight());
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_GRADIENT_VERTICAL)) {
					ConvolutionFilters.filterGradientVertical(abstractRendererKernel.getPixels(), abstractRendererKernel.getWidth(), abstractRendererKernel.getHeight());
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_SHARPEN)) {
					ConvolutionFilters.filterSharpen(abstractRendererKernel.getPixels(), abstractRendererKernel.getWidth(), abstractRendererKernel.getHeight());
				}
			} finally {
				lock.unlock();
			}
			
			final int renderPass0 = renderPass.incrementAndGet();
			
			final long elapsedTimeMillis = System.currentTimeMillis() - currentTimeMillis.get();
			
			Platform.runLater(() -> {
				final long hours = elapsedTimeMillis / (60L * 60L * 1000L);
				final long minutes = (elapsedTimeMillis - (hours * 60L * 60L * 1000L)) / (60L * 1000L);
				final long seconds = (elapsedTimeMillis - ((hours * 60L * 60L * 1000L) + (minutes * 60L * 1000L))) / 1000L;
				
				this.labelApertureRadius.setText(String.format("Aperture radius: %.2f", Float.valueOf(camera.getApertureRadius())));
				this.labelFieldOfView.setText(String.format("FOV: %.2f - %.2f", Float.valueOf(camera.getFieldOfViewX()), Float.valueOf(camera.getFieldOfViewY())));
				this.labelFocalDistance.setText(String.format("Focal distance: %.2f", Float.valueOf(camera.getFocalDistance())));
				this.labelFPS.setText(String.format("FPS: %s", Long.toString(fPSCounter.getFPS())));
				this.labelRenderPass.setText(String.format("Pass: %s", Integer.toString(renderPass0)));
				this.labelRenderTime.setText(String.format("Time: %02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)));
				this.labelRenderType.setText(String.format("Type: %s", abstractRendererKernel.isPathTracing() ? "Path Tracer" : abstractRendererKernel.isRayCasting() ? "Ray Caster" : "Ray Marcher"));
				this.labelSPS.setText(String.format("SPS: %08d", Long.valueOf(fPSCounter.getFPS() * getCanvasWidth() * getCanvasHeight())));
			});
			
			try {
				Thread.sleep(0L);
			} catch(final InterruptedException e) {
//				Do nothing.
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts this program.
	 * 
	 * @param args the arguments to this program
	 */
	public static void main(final String[] args) {
		launch(args);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean[] doTest(final float oldX, final float oldY, final float oldZ, final float newX, final float newY, final float newZ) {
		final boolean[] test = new boolean[] {true, true, true};
		
		for(final Shape shape : this.scene.getShapes()) {
			if(shape instanceof Sphere) {
				final Sphere sphere = Sphere.class.cast(shape);
				
				if(sphere.isWithinRadius(newX, oldY, oldZ, 5.0F)) {
					test[0] = false;
				}
				
				if(sphere.isWithinRadius(oldX, newY, oldZ, 5.0F)) {
					test[1] = false;
				}
				
				if(sphere.isWithinRadius(oldX, oldY, newZ, 5.0F)) {
					test[2] = false;
				}
			}
		}
		
		return test;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Matrix44 doCreateScreenToRaster(final float width, final float height) {
		final float aspectRatio = width / height;
		final float x0 = aspectRatio > 1.0F ? -aspectRatio : -1.0F;
		final float y0 = aspectRatio > 1.0F ? -1.0F : -1.0F / aspectRatio;
		final float x1 = aspectRatio > 1.0F ? aspectRatio : 1.0F;
		final float y1 = aspectRatio > 1.0F ? 1.0F : 1.0F / aspectRatio;
		
		return doCreateScreenToRaster(width, height, x0, y0, x1, y1);
	}
	
	private static Matrix44 doCreateScreenToRaster(final float width, final float height, final float x0, final float y0, final float x1, final float y1) {
		final float transform0X = width;
		final float transform0Y = height;
		final float transform0Z = 1.0F;
		
		final float transform1X = 1.0F / (x1 - x0);
		final float transform1Y = 1.0F / (y0 - y1);
		final float transform1Z = 1.0F;
		
		final float transform2X = -x0;
		final float transform2Y = -y1;
		final float transform2Z = 0.0F;
		
		final Matrix44 m0 = Matrix44.scale(new Vector3(transform0X, transform0Y, transform0Z));
		final Matrix44 m1 = Matrix44.scale(new Vector3(transform1X, transform1Y, transform1Z));
		final Matrix44 m2 = Matrix44.translation(new Point3(transform2X, transform2Y, transform2Z));
		final Matrix44 m3 = m0.multiply(m1).multiply(m2);
		
		return m3;
	}
	
	private static Point2 doProject(final float distance, final float width, final float height, final Point3 p) {
		final float z = 1.0F / p.z;
		final float x = distance * p.x * z + width * 0.5F;
		final float y = -(distance * p.y * z) + height * 0.5F;
		
		return new Point2(x, y);
	}
	
	private static Point3 doPerspectiveDivide(final Point3 p, final float width, final float height) {
		return new Point3((p.x / -p.z + 1.0F) / 2.0F * width, (p.y / -p.z + 1.0F) / 2.0F * height, p.z);
	}
}