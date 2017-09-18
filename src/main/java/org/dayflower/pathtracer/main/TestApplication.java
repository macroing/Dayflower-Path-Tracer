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

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.amd.aparapi.Range;

import org.dayflower.pathtracer.application.AbstractApplication;
import org.dayflower.pathtracer.application.JavaFX;
import org.dayflower.pathtracer.kernel.AbstractRendererKernel;
import org.dayflower.pathtracer.kernel.CompiledScene;
import org.dayflower.pathtracer.kernel.ConvolutionKernel;
import org.dayflower.pathtracer.kernel.RendererKernel;
import org.dayflower.pathtracer.math.Vector3;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.util.FPSCounter;

/**
 * An implementation of {@link AbstractApplication} that performs Path Tracing, Ray Casting or Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class TestApplication extends AbstractApplication {
	private static final String ENGINE_NAME = "Dayflower - Path Tracer";
	private static final String SETTING_NAME_FILTER_BLUR = "Filter.Blur";
	private static final String SETTING_NAME_FILTER_DETECT_EDGES = "Filter.DetectEdges";
	private static final String SETTING_NAME_FILTER_EMBOSS = "Filter.Emboss";
	private static final String SETTING_NAME_FILTER_GRADIENT_HORIZONTAL = "Filter.Gradient.Horizontal";
	private static final String SETTING_NAME_FILTER_GRADIENT_VERTICAL = "Filter.Gradient.Vertical";
	private static final String SETTING_NAME_FILTER_SHARPEN = "Filter.Sharpen";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicBoolean hasRequestedToExit = new AtomicBoolean();
	private final AtomicBoolean hasInitialized = new AtomicBoolean();
	private byte[] pixels;
	private final Camera camera = new Camera();
	private ConvolutionKernel convolutionKernel;
	private final Label labelApertureRadius = new Label("Aperture radius: N/A");
	private final Label labelFieldOfView = new Label("FoV: N/A - N/A");
	private final Label labelFocalDistance = new Label("Focal distance: N/A");
	private final Label labelFPS = new Label("FPS: 0");
	private final Label labelRenderMode = new Label("Mode: GPU");
	private final Label labelRenderPass = new Label("Pass: 0");
	private final Label labelRenderTime = new Label("Time: 00:00:00");
	private final Label labelRenderType = new Label("Type: Path Tracer");
	private final Label labelSPS = new Label("SPS: 00000000");
	private AbstractRendererKernel abstractRendererKernel;
	private final Sky sky = new Sky();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code TestApplication} instance.
	 */
	public TestApplication() {
		super(String.format("%s %s", ENGINE_NAME, Dayflower.getVersion()));
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
		this.convolutionKernel = new ConvolutionKernel(pixels, getCanvasWidth(), getCanvasHeight());
		
		final
		Camera camera = this.camera;
		camera.setApertureRadius(0.0F);
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
	 * @param menuBar a {@code MenuBar} to add UI-controls to
	 * @param vBox a {@code VBox} to add UI-controls to
	 */
	@Override
	protected void doConfigureUI(final HBox hBox, final MenuBar menuBar, final VBox vBox) {
//		Create the "File" Menu:
		final MenuItem menuItemExit = JavaFX.newMenuItem("Exit", e -> this.hasRequestedToExit.set(true));
		
		final Menu menuFile = JavaFX.newMenu("File", menuItemExit);
		
		menuBar.getMenus().add(menuFile);
		
//		Create the "Camera" Menu:
		final ToggleGroup toggleGroupCameraLens = new ToggleGroup();
		
		final CheckMenuItem checkMenuItemWalkLock = JavaFX.newCheckMenuItem("Walk Lock", e -> this.camera.setWalkLockEnabled(!this.camera.isWalkLockEnabled()), this.camera.isWalkLockEnabled());
		
		final RadioMenuItem radioMenuItemFisheye = JavaFX.newRadioMenuItem("Fisheye Camera Lens", e -> this.camera.setFisheyeCameraLens(true), this.camera.isFisheyeCameraLens(), toggleGroupCameraLens);
		final RadioMenuItem radioMenuItemThin = JavaFX.newRadioMenuItem("Thin Camera Lens", e -> this.camera.setThinCameraLens(true), this.camera.isThinCameraLens(), toggleGroupCameraLens);
		
		final Menu menuCamera = JavaFX.newMenu("Camera", checkMenuItemWalkLock, radioMenuItemFisheye, radioMenuItemThin);
		
		menuBar.getMenus().add(menuCamera);
		
//		Create the "Effect" Menu:
		final CheckMenuItem checkMenuItemBlur = JavaFX.newCheckMenuItem("Blur", e -> toggleSetting(SETTING_NAME_FILTER_BLUR));
		final CheckMenuItem checkMenuItemDetectEdges = JavaFX.newCheckMenuItem("Detect Edges", e -> toggleSetting(SETTING_NAME_FILTER_DETECT_EDGES));
		final CheckMenuItem checkMenuItemEmboss = JavaFX.newCheckMenuItem("Emboss", e -> toggleSetting(SETTING_NAME_FILTER_EMBOSS));
		final CheckMenuItem checkMenuItemGradientHorizontal = JavaFX.newCheckMenuItem("Gradient (Horizontal)", e -> toggleSetting(SETTING_NAME_FILTER_GRADIENT_HORIZONTAL));
		final CheckMenuItem checkMenuItemGradientVertical = JavaFX.newCheckMenuItem("Gradient (Vertical)", e -> toggleSetting(SETTING_NAME_FILTER_GRADIENT_VERTICAL));
		final CheckMenuItem checkMenuItemSharpen = JavaFX.newCheckMenuItem("Sharpen", e -> toggleSetting(SETTING_NAME_FILTER_SHARPEN));
		final CheckMenuItem checkMenuItemGrayscale = JavaFX.newCheckMenuItem("Grayscale", e -> this.abstractRendererKernel.setEffectGrayScale(!this.abstractRendererKernel.isEffectGrayScale()));
		final CheckMenuItem checkMenuItemSepiaTone = JavaFX.newCheckMenuItem("Sepia Tone", e -> this.abstractRendererKernel.setEffectSepiaTone(!this.abstractRendererKernel.isEffectSepiaTone()));
		
		final Menu menuEffect = JavaFX.newMenu("Effect", checkMenuItemBlur, checkMenuItemDetectEdges, checkMenuItemEmboss, checkMenuItemGradientHorizontal, checkMenuItemGradientVertical, checkMenuItemSharpen, checkMenuItemGrayscale, checkMenuItemSepiaTone);
		
		menuBar.getMenus().add(menuEffect);
		
//		Create the "Renderer" Menu:
		final ToggleGroup toggleGroupRenderer = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemPathTracer = JavaFX.newRadioMenuItem("Path Tracer", e -> this.abstractRendererKernel.setPathTracing(true), this.abstractRendererKernel.isPathTracing(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayCaster = JavaFX.newRadioMenuItem("Ray Caster", e -> this.abstractRendererKernel.setRayCasting(true), this.abstractRendererKernel.isRayCasting(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayMarcher = JavaFX.newRadioMenuItem("Ray Marcher", e -> this.abstractRendererKernel.setRayMarching(true), this.abstractRendererKernel.isRayMarching(), toggleGroupRenderer);
		
		final Menu menuRenderer = JavaFX.newMenu("Renderer", radioMenuItemPathTracer, radioMenuItemRayCaster, radioMenuItemRayMarcher);
		
		menuBar.getMenus().add(menuRenderer);
		
//		Create the "Scene" Menu:
		final ToggleGroup toggleGroupShading = new ToggleGroup();
		
		final CheckMenuItem checkMenuItemNormalMapping = JavaFX.newCheckMenuItem("Normal Mapping", e -> this.abstractRendererKernel.setNormalMapping(!this.abstractRendererKernel.isNormalMapping()), this.abstractRendererKernel.isNormalMapping());
		
		final MenuItem menuItemEnterScene = JavaFX.newMenuItem("Enter Scene", e -> enter());
		
		final RadioMenuItem radioMenuItemFlatShading = JavaFX.newRadioMenuItem("Flat Shading", e -> this.abstractRendererKernel.setShadingFlat(), this.abstractRendererKernel.isShadingFlat(), toggleGroupShading);
		final RadioMenuItem radioMenuItemGouraudShading = JavaFX.newRadioMenuItem("Gouraud Shading", e -> this.abstractRendererKernel.setShadingGouraud(), this.abstractRendererKernel.isShadingGouraud(), toggleGroupShading);
		
		final Menu menuScene = JavaFX.newMenu("Scene", checkMenuItemNormalMapping, menuItemEnterScene, radioMenuItemFlatShading, radioMenuItemGouraudShading);
		
		menuBar.getMenus().add(menuScene);
		
//		Create the "Tone Mapper" Menu:
		final ToggleGroup toggleGroupToneMapper = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemToneMapperFilmicCurve = JavaFX.newRadioMenuItem("Filmic Curve", e -> this.abstractRendererKernel.setToneMappingAndGammaCorrectionFilmicCurve(), true, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperLinear = JavaFX.newRadioMenuItem("Linear", e -> this.abstractRendererKernel.setToneMappingAndGammaCorrectionLinear(), false, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperReinhard1 = JavaFX.newRadioMenuItem("Reinhard v.1", e -> this.abstractRendererKernel.setToneMappingAndGammaCorrectionReinhard1(), false, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperReinhard2 = JavaFX.newRadioMenuItem("Reinhard v.2", e -> this.abstractRendererKernel.setToneMappingAndGammaCorrectionReinhard2(), false, toggleGroupToneMapper);
		
		final Menu menuToneMapper = JavaFX.newMenu("Tone Mapper", radioMenuItemToneMapperFilmicCurve, radioMenuItemToneMapperLinear, radioMenuItemToneMapperReinhard1, radioMenuItemToneMapperReinhard2);
		
		menuBar.getMenus().add(menuToneMapper);
		
//		Create and add all sections to the bottom of the window:
		final Region region0 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region1 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region2 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region3 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region4 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region5 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region6 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		final Region region7 = JavaFX.newRegion(10.0D, 10.0D, 10.0D, 10.0D);
		
		hBox.getChildren().addAll(this.labelRenderPass, region0, this.labelFPS, region1, this.labelSPS, region2, this.labelRenderTime, region3, this.labelRenderMode, region4, this.labelRenderType, region5, this.labelApertureRadius, region6, this.labelFocalDistance, region7, this.labelFieldOfView);
		
		final Label labelSunDirectionWorldX = new Label("Sun Direction X:");
		final Label labelSunDirectionWorldY = new Label("Sun Direction Y:");
		final Label labelSunDirectionWorldZ = new Label("Sun Direction Z:");
		final Label labelTurbidity = new Label("Turbidity:");
		
		final
		Slider sliderSunDirectionWorldX = new Slider(-1.0D, 1.0D, this.sky.getSunDirectionWorld().x);
		sliderSunDirectionWorldX.setShowTickMarks(true);
		sliderSunDirectionWorldX.setShowTickLabels(true);
		sliderSunDirectionWorldX.setMajorTickUnit(0.5D);
		sliderSunDirectionWorldX.setBlockIncrement(0.1D);
		
		final
		Slider sliderSunDirectionWorldY = new Slider(0.0D, 2.0D, this.sky.getSunDirectionWorld().y);
		sliderSunDirectionWorldY.setShowTickMarks(true);
		sliderSunDirectionWorldY.setShowTickLabels(true);
		sliderSunDirectionWorldY.setMajorTickUnit(0.5D);
		sliderSunDirectionWorldY.setBlockIncrement(0.1D);
		
		final
		Slider sliderSunDirectionWorldZ = new Slider(-1.0D, 1.0D, this.sky.getSunDirectionWorld().z);
		sliderSunDirectionWorldZ.setShowTickMarks(true);
		sliderSunDirectionWorldZ.setShowTickLabels(true);
		sliderSunDirectionWorldZ.setMajorTickUnit(0.5D);
		sliderSunDirectionWorldZ.setBlockIncrement(0.1D);
		
		final
		Slider sliderTurbidity = new Slider(2.0D, 8.0D, this.sky.getTurbidity());
		sliderTurbidity.setShowTickMarks(true);
		sliderTurbidity.setShowTickLabels(true);
		sliderTurbidity.setMajorTickUnit(1.0D);
		sliderTurbidity.setBlockIncrement(0.5D);
		
		sliderSunDirectionWorldX.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			this.sky.set(new Vector3(newValue.floatValue(), (float)(sliderSunDirectionWorldY.getValue()), (float)(sliderSunDirectionWorldZ.getValue())).normalize(), (float)(sliderTurbidity.getValue()));
			this.abstractRendererKernel.updateSky();
		});
		
		sliderSunDirectionWorldY.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			this.sky.set(new Vector3((float)(sliderSunDirectionWorldX.getValue()), newValue.floatValue(), (float)(sliderSunDirectionWorldZ.getValue())).normalize(), (float)(sliderTurbidity.getValue()));
			this.abstractRendererKernel.updateSky();
		});
		
		sliderSunDirectionWorldZ.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			this.sky.set(new Vector3((float)(sliderSunDirectionWorldX.getValue()), (float)(sliderSunDirectionWorldY.getValue()), newValue.floatValue()).normalize(), (float)(sliderTurbidity.getValue()));
			this.abstractRendererKernel.updateSky();
		});
		
		sliderTurbidity.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			this.sky.set(new Vector3((float)(sliderSunDirectionWorldX.getValue()), (float)(sliderSunDirectionWorldY.getValue()), (float)(sliderSunDirectionWorldZ.getValue())).normalize(), newValue.floatValue());
			this.abstractRendererKernel.updateSky();
		});
		
		vBox.getChildren().addAll(labelSunDirectionWorldX, sliderSunDirectionWorldX, labelSunDirectionWorldY, sliderSunDirectionWorldY, labelSunDirectionWorldZ, sliderSunDirectionWorldZ, labelTurbidity, sliderTurbidity);
	}
	
	/**
	 * Initializes this {@code TestApplication} instance.
	 */
	@Override
	public void init() {
		final Scene scene = Scenes.getSceneByName(Dayflower.getSceneName());
		
		final String sceneFilename = Dayflower.getSceneFilename(String.format("%s.scene", scene.getName()));
		
		final File sceneFile = new File(sceneFilename);
		
		if(!sceneFile.isFile() || Dayflower.getSceneCompile()) {
			final
			CompiledScene compiledScene = CompiledScene.compile(this.camera, scene);
			compiledScene.write(sceneFile);
			
			try {
				Thread.sleep(100L);
			} catch(final InterruptedException e) {
//				Do nothing.
			}
		}
		
		setCanvasWidthScale(Dayflower.getWidthScale());
		setCanvasWidth(Dayflower.getWidth() / getCanvasWidthScale());
		setCanvasHeightScale(Dayflower.getHeightScale());
		setCanvasHeight(Dayflower.getHeight() / getCanvasHeightScale());
		
		this.abstractRendererKernel = new RendererKernel(false, getCanvasWidth(), getCanvasHeight(), this.camera, this.sky, sceneFilename, 1.0F);
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
		
		final ConvolutionKernel convolutionKernel = this.convolutionKernel;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			abstractRendererKernel.dispose();
			
			convolutionKernel.dispose();
		}));
		
		while(true) {
			if(this.hasRequestedToExit.get()) {
				if(hasEntered()) {
					this.hasRequestedToExit.set(false);
					
					leave();
				} else {
					abstractRendererKernel.dispose();
					
					convolutionKernel.dispose();
					
					Platform.exit();
					
					break;
				}
			}
			
			final float velocity = abstractRendererKernel.isRayMarching() ? 1.0F : 250.0F;
			final float movement = fPSCounter.getFrameTimeMillis() / 1000.0F * velocity;
			
			if(isKeyPressed(KeyCode.A)) {
				camera.strafe(-movement);
			}
			
			if(isKeyPressed(KeyCode.D)) {
				camera.strafe(movement);
			}
			
			if(isKeyPressed(KeyCode.DOWN)) {
				camera.changePitch(0.02F);
			}
			
			if(isKeyPressed(KeyCode.E)) {
				camera.changeAltitude(0.5F);
			}
			
			if(isKeyPressed(KeyCode.ENTER, true) && !hasEntered()) {
				enter();
			}
			
			if(isKeyPressed(KeyCode.ESCAPE, true)) {
				this.hasRequestedToExit.set(true);
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
			
			if(isKeyPressed(KeyCode.LEFT)) {
				camera.changeYaw(0.02F);
			}
			
			if(isKeyPressed(KeyCode.M, true)) {
				abstractRendererKernel.setDepthMaximum(abstractRendererKernel.getDepthMaximum() + 1);
			}
			
			if(isKeyPressed(KeyCode.N, true)) {
				abstractRendererKernel.setDepthMaximum(Math.max(abstractRendererKernel.getDepthMaximum() - 1, 1));
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
			
			if(isDraggingMouse() || isMovingMouse() && isRecenteringMouse() || isPressingKey() || camera.hasUpdated() || abstractRendererKernel.isResetRequired()) {
				camera.resetUpdateStatus();
				
				abstractRendererKernel.updateResetStatus();
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
				
				if(isSettingEnabled(SETTING_NAME_FILTER_BLUR)) {
					convolutionKernel.update();
					convolutionKernel.enableBlur();
					convolutionKernel.execute(range);
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_DETECT_EDGES)) {
					convolutionKernel.update();
					convolutionKernel.enableDetectEdges();
					convolutionKernel.execute(range);
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_EMBOSS)) {
					convolutionKernel.update();
					convolutionKernel.enableEmboss();
					convolutionKernel.execute(range);
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_GRADIENT_HORIZONTAL)) {
					convolutionKernel.update();
					convolutionKernel.enableGradientHorizontal();
					convolutionKernel.execute(range);
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_GRADIENT_VERTICAL)) {
					convolutionKernel.update();
					convolutionKernel.enableGradientVertical();
					convolutionKernel.execute(range);
				}
				
				if(isSettingEnabled(SETTING_NAME_FILTER_SHARPEN)) {
					convolutionKernel.update();
					convolutionKernel.enableSharpen();
					convolutionKernel.execute(range);
				}
			} finally {
				lock.unlock();
			}
			
			final int renderPass0 = renderPass.incrementAndGet();
			
			final long elapsedTimeMillis = System.currentTimeMillis() - currentTimeMillis.get();
			final long fPS = fPSCounter.getFPS();
			final long sPS = fPS * getCanvasWidth() * getCanvasHeight();
			
			final float apertureRadius = camera.getApertureRadius();
			final float fieldOfViewX = camera.getFieldOfViewX();
			final float fieldOfViewY = camera.getFieldOfViewY();
			final float focalDistance = camera.getFocalDistance();
			
			final String rendererType = abstractRendererKernel.isPathTracing() ? "Path Tracer" : abstractRendererKernel.isRayCasting() ? "Ray Caster" : "Ray Marcher";
			
			Platform.runLater(() -> {
				final long hours = elapsedTimeMillis / (60L * 60L * 1000L);
				final long minutes = (elapsedTimeMillis - (hours * 60L * 60L * 1000L)) / (60L * 1000L);
				final long seconds = (elapsedTimeMillis - ((hours * 60L * 60L * 1000L) + (minutes * 60L * 1000L))) / 1000L;
				
				this.labelApertureRadius.setText(String.format("Aperture radius: %.2f", Float.valueOf(apertureRadius)));
				this.labelFieldOfView.setText(String.format("FoV: %.2f - %.2f", Float.valueOf(fieldOfViewX), Float.valueOf(fieldOfViewY)));
				this.labelFocalDistance.setText(String.format("Focal distance: %.2f", Float.valueOf(focalDistance)));
				this.labelFPS.setText(String.format("FPS: %s", Long.toString(fPS)));
				this.labelRenderPass.setText(String.format("Pass: %s", Integer.toString(renderPass0)));
				this.labelRenderTime.setText(String.format("Time: %02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)));
				this.labelRenderType.setText(String.format("Type: %s", rendererType));
				this.labelSPS.setText(String.format("SPS: %08d", Long.valueOf(sPS)));
			});
			
			try {
				Thread.sleep(1L);
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
}