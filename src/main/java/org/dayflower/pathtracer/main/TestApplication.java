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
package org.dayflower.pathtracer.main;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.amd.aparapi.Range;

import org.dayflower.pathtracer.application.AbstractApplication;
import org.dayflower.pathtracer.application.JavaFX;
import org.dayflower.pathtracer.kernel.AbstractRendererKernel;
import org.dayflower.pathtracer.kernel.CompiledScene;
import org.dayflower.pathtracer.kernel.ConvolutionKernel;
import org.dayflower.pathtracer.kernel.RendererKernel;
import org.dayflower.pathtracer.math.AngleF;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.CameraObserver;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;

/**
 * An implementation of {@link AbstractApplication} that performs Path Tracing, Ray Casting or Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class TestApplication extends AbstractApplication implements CameraObserver {
	private static final String ENGINE_NAME = "Dayflower - Path Tracer";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private AbstractRendererKernel abstractRendererKernel;
	private final AtomicInteger renderPass = new AtomicInteger();
	private final AtomicLong currentTimeMillis = new AtomicLong(System.currentTimeMillis());
	private byte[] pixels0;
	private byte[] pixels1;
	private final Camera camera = new Camera();
	private ConvolutionKernel convolutionKernel;
	private final Label labelFPS = new Label("FPS: 0");
	private final Label labelKernelTime = new Label("Kernel Time: 0 ms");
	private final Label labelRenderPass = new Label("Pass: 0");
	private final Label labelRenderTime = new Label("Time: 00:00:00");
	private final Label labelSPS = new Label("SPS: 00000000");
	private Range range;
	private RendererRunnable rendererRunnable;
	private final Setting settingFilterBlur = new Setting("Filter.Blur");
	private final Setting settingFilterDetectEdges = new Setting("Filter.DetectEdges");
	private final Setting settingFilterEmboss = new Setting("Filter.Emboss");
	private final Setting settingFilterGradientHorizontal = new Setting("Filter.Gradient.Horizontal");
	private final Setting settingFilterGradientVertical = new Setting("Filter.Gradient.Vertical");
	private final Setting settingFilterSharpen = new Setting("Filter.Sharpen");
	private final Sky sky = new Sky();
	private Slider sliderPitch;
	private Slider sliderYaw;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code TestApplication} instance.
	 */
	public TestApplication() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
		
		setCanvasWidth(Dayflower.getCanvasWidth());
		setCanvasHeight(Dayflower.getCanvasHeight());
		setKernelWidth(Dayflower.getKernelWidth());
		setKernelHeight(Dayflower.getKernelHeight());
		
		this.abstractRendererKernel = new RendererKernel(false, getKernelWidth(), getKernelHeight(), this.camera, this.sky, sceneFilename, 1.0F);
		
		final
		Camera camera = this.camera;
		camera.setApertureRadius(0.0F);
		camera.setEye(55.0F, 42.0F, 155.6F);
		camera.setFieldOfViewX(90.0F);
		camera.setFocalDistance(30.0F);
		camera.setPitch(AngleF.pitch(Vector3F.x()));
		camera.setResolution(getKernelWidth(), getKernelHeight());
		camera.setWalkLockEnabled(true);
		camera.setYaw(AngleF.yaw(Vector3F.y()));
		camera.update();
		camera.addCameraObserver(this);
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::onExit));
	}
	
	@Override
	public void pitchChanged(final Camera camera, final AngleF pitch) {
		final Slider sliderPitch = this.sliderPitch;
		
		if(sliderPitch != null) {
			if(Platform.isFxApplicationThread()) {
				sliderPitch.setValue(pitch.degrees);
			} else {
				Platform.runLater(() -> sliderPitch.setValue(pitch.degrees));
			}
		}
	}
	
	@Override
	public void yawChanged(final Camera camera, final AngleF yaw) {
		final Slider sliderYaw = this.sliderYaw;
		
		if(sliderYaw != null) {
			if(Platform.isFxApplicationThread()) {
				sliderYaw.setValue(yaw.degrees);
			} else {
				Platform.runLater(() -> sliderYaw.setValue(yaw.degrees));
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
	
	/**
	 * Called when the {@code MenuBar} can be configured.
	 * 
	 * @param menuBar the {@code MenuBar} to configure
	 */
	@Override
	protected void configureMenuBar(final MenuBar menuBar) {
//		Create the "File" Menu:
		final MenuItem menuItemExit = JavaFX.newMenuItem("Exit", e -> exit());
		
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
		final CheckMenuItem checkMenuItemBlur = JavaFX.newCheckMenuItem("Blur", e -> this.settingFilterBlur.toggle());
		final CheckMenuItem checkMenuItemDetectEdges = JavaFX.newCheckMenuItem("Detect Edges", e -> this.settingFilterDetectEdges.toggle());
		final CheckMenuItem checkMenuItemEmboss = JavaFX.newCheckMenuItem("Emboss", e -> this.settingFilterEmboss.toggle());
		final CheckMenuItem checkMenuItemGradientHorizontal = JavaFX.newCheckMenuItem("Gradient (Horizontal)", e -> this.settingFilterGradientHorizontal.toggle());
		final CheckMenuItem checkMenuItemGradientVertical = JavaFX.newCheckMenuItem("Gradient (Vertical)", e -> this.settingFilterGradientVertical.toggle());
		final CheckMenuItem checkMenuItemSharpen = JavaFX.newCheckMenuItem("Sharpen", e -> this.settingFilterSharpen.toggle());
		final CheckMenuItem checkMenuItemGrayscale = JavaFX.newCheckMenuItem("Grayscale", e -> this.abstractRendererKernel.setEffectGrayScale(!this.abstractRendererKernel.isEffectGrayScale()));
		final CheckMenuItem checkMenuItemSepiaTone = JavaFX.newCheckMenuItem("Sepia Tone", e -> this.abstractRendererKernel.setEffectSepiaTone(!this.abstractRendererKernel.isEffectSepiaTone()));
		
		final Menu menuEffect = JavaFX.newMenu("Effect", checkMenuItemBlur, checkMenuItemDetectEdges, checkMenuItemEmboss, checkMenuItemGradientHorizontal, checkMenuItemGradientVertical, checkMenuItemSharpen, checkMenuItemGrayscale, checkMenuItemSepiaTone);
		
		menuBar.getMenus().add(menuEffect);
		
//		Create the "Renderer" Menu:
		final ToggleGroup toggleGroupRenderer = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemAmbientOcclusion = JavaFX.newRadioMenuItem("Ambient Occlusion", e -> this.abstractRendererKernel.setAmbientOcclusion(true), this.abstractRendererKernel.isAmbientOcclusion(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemPathTracer = JavaFX.newRadioMenuItem("Path Tracer", e -> this.abstractRendererKernel.setPathTracing(true), this.abstractRendererKernel.isPathTracing(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayCaster = JavaFX.newRadioMenuItem("Ray Caster", e -> this.abstractRendererKernel.setRayCasting(true), this.abstractRendererKernel.isRayCasting(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayMarcher = JavaFX.newRadioMenuItem("Ray Marcher", e -> this.abstractRendererKernel.setRayMarching(true), this.abstractRendererKernel.isRayMarching(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayTracer = JavaFX.newRadioMenuItem("Ray Tracer", e -> this.abstractRendererKernel.setRayTracing(true), this.abstractRendererKernel.isRayTracing(), toggleGroupRenderer);
		
		final Menu menuRenderer = JavaFX.newMenu("Renderer", radioMenuItemAmbientOcclusion, radioMenuItemPathTracer, radioMenuItemRayCaster, radioMenuItemRayMarcher, radioMenuItemRayTracer);
		
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
	}
	
	/**
	 * Called when pixels can be configured at start.
	 * 
	 * @param pixels a {@code byte} array with pixel data
	 */
	@Override
	protected void configurePixels(final byte[] pixels) {
		this.pixels0 = pixels;
		this.pixels1 = pixels.clone();
		this.convolutionKernel = new ConvolutionKernel(this.pixels1, getKernelWidth(), getKernelHeight());
		this.range = Range.create(getKernelWidth() * getKernelHeight());
		this.abstractRendererKernel.updateLocalVariables(this.range.getLocalSize(0));
		this.abstractRendererKernel.compile(this.pixels1, getKernelWidth(), getKernelHeight());
		this.rendererRunnable = new RendererRunnable(this.abstractRendererKernel, this.renderPass, this.convolutionKernel, this.range, this.settingFilterBlur, this.settingFilterDetectEdges, this.settingFilterEmboss, this.settingFilterGradientHorizontal, this.settingFilterGradientVertical, this.settingFilterSharpen, this.pixels0, this.pixels1);
		
		final
		Thread thread = new Thread(this.rendererRunnable);
		thread.start();
	}
	
	/**
	 * Called when the primary {@code Stage} can be configured.
	 * 
	 * @param stage the primary {@code Stage} to configure
	 */
	@Override
	protected void configureStage(final Stage stage) {
		stage.setTitle(String.format("%s %s", ENGINE_NAME, Dayflower.getVersion()));
	}
	
	/**
	 * Called when the status bar can be configured.
	 * 
	 * @param hBox a {@code HBox} that acts as a status bar
	 */
	@Override
	protected void configureStatusBar(final HBox hBox) {
		hBox.getChildren().addAll(this.labelRenderPass, this.labelFPS, this.labelSPS, this.labelRenderTime, this.labelKernelTime);
	}
	
	/**
	 * Called when the {@code TabPane} can be configured.
	 * 
	 * @param tabPane the {@code TabPane} to configure
	 */
	@Override
	protected void configureTabPane(final TabPane tabPane) {
//		Create the Tab with the Camera settings:
		final Label labelFieldOfView = new Label("Field of View:");
		final Label labelApertureRadius = new Label("Aperture Radius:");
		final Label labelFocalDistance = new Label("Focal Distance:");
		final Label labelPitch = new Label("Pitch:");
		final Label labelYaw = new Label("Yaw:");
		
		final Slider sliderFieldOfView = JavaFX.newSlider(40.0D, 100.0D, this.camera.getFieldOfViewX(), 10.0D, 10.0D, true, true, false, this::doOnSliderFieldOfView);
		final Slider sliderApertureRadius = JavaFX.newSlider(0.0D, 25.0D, this.camera.getApertureRadius(), 1.0D, 5.0D, true, true, false, this::doOnSliderApertureRadius);
		final Slider sliderFocalDistance = JavaFX.newSlider(0.0D, 100.0D, this.camera.getFocalDistance(), 1.0D, 20.0D, true, true, false, this::doOnSliderFocalDistance);
		final Slider sliderPitch = JavaFX.newSlider(-90.0F, 90.0F, this.camera.getPitch().degrees, 10.0D, 20.0D, true, true, false, this::doOnSliderPitch);
		final Slider sliderYaw = JavaFX.newSlider(0.0D, 360.0F, this.camera.getYaw().degrees, 20.0D, 40.0D, true, true, false, this::doOnSliderYaw);
		
		this.sliderPitch = sliderPitch;
		this.sliderYaw = sliderYaw;
		
		final
		VBox vBoxCamera = new VBox();
		vBoxCamera.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vBoxCamera.getChildren().addAll(labelFieldOfView, sliderFieldOfView, labelApertureRadius, sliderApertureRadius, labelFocalDistance, sliderFocalDistance, labelPitch, sliderPitch, labelYaw, sliderYaw);
		
		final
		Tab tabCamera = new Tab();
		tabCamera.setClosable(false);
		tabCamera.setContent(vBoxCamera);
		tabCamera.setText("Camera");
		
		tabPane.getTabs().add(tabCamera);
		
//		Create the Tab with the Sun and Sky settings:
		final Label labelSunDirectionWorldX = new Label("Sun Direction X:");
		final Label labelSunDirectionWorldY = new Label("Sun Direction Y:");
		final Label labelSunDirectionWorldZ = new Label("Sun Direction Z:");
		final Label labelTurbidity = new Label("Turbidity:");
		
		final Slider sliderSunDirectionWorldX = JavaFX.newSlider(-1.0D, 1.0D, this.sky.getSunDirectionWorld().x, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldX);
		final Slider sliderSunDirectionWorldY = JavaFX.newSlider(0.0D, 1.0D, this.sky.getSunDirectionWorld().y, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldY);
		final Slider sliderSunDirectionWorldZ = JavaFX.newSlider(-1.0D, 1.0D, this.sky.getSunDirectionWorld().z, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldZ);
		final Slider sliderTurbidity = JavaFX.newSlider(2.0D, 8.0D, this.sky.getTurbidity(), 0.5D, 1.0D, true, true, false, this::doOnSliderTurbidity);
		
		final CheckBox checkBoxToggleSunAndSky = JavaFX.newCheckBox("Toggle Sun & Sky", this::doOnCheckBoxToggleSunAndSky, true);
		final CheckBox checkBoxToggleClouds = JavaFX.newCheckBox("Toggle Clouds", this::doOnCheckBoxToggleClouds, false);
		
		final
		VBox vBoxSunAndSky = new VBox();
		vBoxSunAndSky.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vBoxSunAndSky.getChildren().addAll(labelSunDirectionWorldX, sliderSunDirectionWorldX, labelSunDirectionWorldY, sliderSunDirectionWorldY, labelSunDirectionWorldZ, sliderSunDirectionWorldZ, labelTurbidity, sliderTurbidity, checkBoxToggleSunAndSky, checkBoxToggleClouds);
		
		final
		Tab tabSunAndSky = new Tab();
		tabSunAndSky.setClosable(false);
		tabSunAndSky.setContent(vBoxSunAndSky);
		tabSunAndSky.setText("Sun & Sky");
		
		tabPane.getTabs().add(tabSunAndSky);
		
//		Create the Tab with the settings for the renderers:
		final Label labelPathTracer = new Label("Path Tracer");
		final Label labelMaximumRayDepth = new Label("Maximum Ray Depth:");
		final Label labelRayMarcher = new Label("Ray Marcher");
		final Label labelAmplitude = new Label("Amplitude:");
		final Label labelFrequency = new Label("Frequency:");
		final Label labelGain = new Label("Gain:");
		final Label labelLacunarity = new Label("Lacunarity:");
		
		labelPathTracer.setFont(Font.font(16.0D));
		labelPathTracer.setPadding(new Insets(0.0D, 0.0D, 10.0D, 0.0D));
		labelRayMarcher.setFont(Font.font(16.0D));
		labelRayMarcher.setPadding(new Insets(10.0D, 0.0D, 10.0D, 0.0D));
		
		final Slider sliderMaximumRayDepth = JavaFX.newSlider(0.0D, 20.0D, this.abstractRendererKernel.getDepthMaximum(), 1.0D, 5.0D, true, true, true, this::doOnSliderMaximumRayDepth);
		final Slider sliderAmplitude = JavaFX.newSlider(0.0D, 10.0D, this.abstractRendererKernel.getAmplitude(), 1.0D, 5.0D, true, true, false, this::doOnSliderAmplitude);
		final Slider sliderFrequency = JavaFX.newSlider(0.0D, 10.0D, this.abstractRendererKernel.getFrequency(), 1.0D, 5.0D, true, true, false, this::doOnSliderFrequency);
		final Slider sliderGain = JavaFX.newSlider(0.0D, 10.0D, this.abstractRendererKernel.getGain(), 1.0D, 5.0D, true, true, false, this::doOnSliderGain);
		final Slider sliderLacunarity = JavaFX.newSlider(0.0D, 10.0D, this.abstractRendererKernel.getLacunarity(), 1.0D, 5.0D, true, true, false, this::doOnSliderLacunarity);
		
		final
		VBox vBoxRenderer = new VBox();
		vBoxRenderer.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vBoxRenderer.getChildren().addAll(labelPathTracer, labelMaximumRayDepth, sliderMaximumRayDepth, labelRayMarcher, labelAmplitude, sliderAmplitude, labelFrequency, sliderFrequency, labelGain, sliderGain, labelLacunarity, sliderLacunarity);
		
		final
		Tab tabRenderer = new Tab();
		tabRenderer.setClosable(false);
		tabRenderer.setContent(vBoxRenderer);
		tabRenderer.setText("Renderer");
		
		tabPane.getTabs().add(tabRenderer);
		
//		Select the default Tab:
		tabPane.getSelectionModel().select(tabCamera);
	}
	
	@Override
	protected void onExit() {
		final AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
		final ConvolutionKernel convolutionKernel = this.convolutionKernel;
		final RendererRunnable rendererRunnable = this.rendererRunnable;
		
		if(rendererRunnable != null) {
			rendererRunnable.stop();
		}
		
		if(abstractRendererKernel != null) {
			abstractRendererKernel.dispose();
		}
		
		if(convolutionKernel != null) {
			convolutionKernel.dispose();
		}
	}
	
	/**
	 * Called when the mouse is dragged.
	 * 
	 * @param x the new X-coordinate
	 * @param y the new Y-coordinate
	 */
	@Override
	protected void onMouseDragged(final float x, final float y) {
		this.camera.changeYaw(AngleF.degrees(-x * 0.5F));
		this.camera.changePitch(AngleF.degrees(-(y * 0.5F), -90.0F, 90.0F));
	}
	
	/**
	 * Called when the mouse is moved.
	 * 
	 * @param x the new X-coordinate
	 * @param y the new Y-coordinate
	 */
	@Override
	protected void onMouseMoved(final float x, final float y) {
		if(isMouseRecentering()) {
			this.camera.changeYaw(AngleF.degrees(-x * 0.5F));
			this.camera.changePitch(AngleF.degrees(-(y * 0.5F), -90.0F, 90.0F));
		}
	}
	
	/**
	 * Called when rendering.
	 */
	@Override
	protected void render() {
		getFPSCounter().update();
		
		final int renderPass = this.renderPass.get();
		
		final long renderTimeMillis = this.rendererRunnable.getRenderTimeMillis();
		final long elapsedTimeMillis = System.currentTimeMillis() - this.currentTimeMillis.get();
		final long fPS = getFPSCounter().getFPS();
		final long sPS = fPS * getKernelWidth() * getKernelHeight();
		
		final long hours = elapsedTimeMillis / (60L * 60L * 1000L);
		final long minutes = (elapsedTimeMillis - (hours * 60L * 60L * 1000L)) / (60L * 1000L);
		final long seconds = (elapsedTimeMillis - ((hours * 60L * 60L * 1000L) + (minutes * 60L * 1000L))) / 1000L;
		
		this.labelFPS.setText(String.format("FPS: %s", Long.toString(fPS)));
		this.labelKernelTime.setText(String.format("Kernel Time: %s ms", Long.valueOf(renderTimeMillis)));
		this.labelRenderPass.setText(String.format("Pass: %s", Integer.toString(renderPass)));
		this.labelRenderTime.setText(String.format("Time: %02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)));
		this.labelSPS.setText(String.format("SPS: %08d", Long.valueOf(sPS)));
	}
	
	/**
	 * Called when updating.
	 */
	@Override
	protected void update() {
		final AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
		
		final Camera camera = this.camera;
		
		final float velocity = abstractRendererKernel.isRayMarching() ? 1.0F : 250.0F;
		final float movement = getFPSCounter().getFrameTimeMillis() / 1000.0F * velocity;
		
		if(isKeyPressed(KeyCode.A)) {
			camera.strafe(-movement);
		}
		
		if(isKeyPressed(KeyCode.D)) {
			camera.strafe(movement);
		}
		
		if(isKeyPressed(KeyCode.E)) {
			camera.changeAltitude(-0.5F);
		}
		
		if(isKeyPressed(KeyCode.ENTER, true) && !hasEntered()) {
			enter();
		}
		
		if(isKeyPressed(KeyCode.ESCAPE, true)) {
			exit();
		}
		
		if(isKeyPressed(KeyCode.M, true)) {
			synchronized(this.pixels1) {
				abstractRendererKernel.toggleMaterial();
				abstractRendererKernel.updateResetStatus();
				abstractRendererKernel.reset();
			}
		}
		
		if(isKeyPressed(KeyCode.Q)) {
			camera.changeAltitude(0.5F);
		}
		
		if(isKeyPressed(KeyCode.R, true)) {
			synchronized(this.pixels1) {
				final int mouseX = getMouseX();
				final int mouseY = getMouseY();
				final int index = mouseY * getKernelWidth() + mouseX;
				
				final int[] shapeOffsetsForPrimaryRay = abstractRendererKernel.getShapeOffsetsForPrimaryRay();
				
				if(index >= 0 && index < shapeOffsetsForPrimaryRay.length) {
					final int selectedShapeIndex = shapeOffsetsForPrimaryRay[index];
					
					if(selectedShapeIndex == abstractRendererKernel.getSelectedShapeIndex()) {
						abstractRendererKernel.setSelectedShapeIndex(-1);
					} else {
						abstractRendererKernel.setSelectedShapeIndex(selectedShapeIndex);
					}
				}
			}
		}
		
		if(isKeyPressed(KeyCode.S)) {
			camera.forward(-movement);
		}
		
		if(isKeyPressed(KeyCode.W)) {
			camera.forward(movement);
		}
		
		if(isMouseDragging() || isMouseMoving() && isMouseRecentering() || camera.hasUpdated() || abstractRendererKernel.isResetRequired()) {
			synchronized(this.pixels1) {
				camera.resetUpdateStatus();
				
				abstractRendererKernel.updateResetStatus();
				abstractRendererKernel.reset();
				
				this.renderPass.set(0);
				
				this.currentTimeMillis.set(System.currentTimeMillis());
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("unused")
	private void doOnCheckBoxToggleClouds(final ActionEvent e) {
		synchronized(this.pixels1) {
			this.abstractRendererKernel.toggleClouds();
			this.abstractRendererKernel.updateResetStatus();
			this.abstractRendererKernel.reset();
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnCheckBoxToggleSunAndSky(final ActionEvent e) {
		synchronized(this.pixels1) {
			this.abstractRendererKernel.toggleSunAndSky();
			this.abstractRendererKernel.updateResetStatus();
			this.abstractRendererKernel.reset();
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderAmplitude(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setAmplitude(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderApertureRadius(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.camera.setApertureRadius(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderFieldOfView(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.camera.setFieldOfViewX(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderFocalDistance(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.camera.setFocalDistance(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderFrequency(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setFrequency(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderGain(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setGain(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderLacunarity(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setLacunarity(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderMaximumRayDepth(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setDepthMaximum(newValue.intValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderPitch(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.camera.setPitch(AngleF.degrees(newValue.floatValue(), -90.0F, 90.0F));
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldX(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.sky.setX(newValue.floatValue());
		this.abstractRendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldY(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.sky.setY(newValue.floatValue());
		this.abstractRendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldZ(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.sky.setZ(newValue.floatValue());
		this.abstractRendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderTurbidity(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.sky.setTurbidity(newValue.floatValue());
		this.abstractRendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderYaw(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.camera.setYaw(AngleF.degrees(newValue.floatValue()));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class RendererRunnable implements Runnable {
		private final AbstractRendererKernel abstractRendererKernel;
		private final AtomicBoolean isRunning;
		private final AtomicInteger renderPass;
		private final AtomicLong renderTimeMillis;
		private final ConvolutionKernel convolutionKernel;
		private final Range range;
		private final Setting settingFilterBlur;
		private final Setting settingFilterDetectEdges;
		private final Setting settingFilterEmboss;
		private final Setting settingFilterGradientHorizontal;
		private final Setting settingFilterGradientVertical;
		private final Setting settingFilterSharpen;
		private final byte[] pixels0;
		private final byte[] pixels1;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public RendererRunnable(final AbstractRendererKernel abstractRendererKernel, final AtomicInteger renderPass, final ConvolutionKernel convolutionKernel, final Range range, final Setting settingFilterBlur, final Setting settingFilterDetectEdges, final Setting settingFilterEmboss, final Setting settingFilterGradientHorizontal, final Setting settingFilterGradientVertical, final Setting settingFilterSharpen, final byte[] pixels0, final byte[] pixels1) {
			this.abstractRendererKernel = Objects.requireNonNull(abstractRendererKernel, "abstractRendererKernel == null");
			this.renderPass = Objects.requireNonNull(renderPass, "renderPass == null");
			this.convolutionKernel = Objects.requireNonNull(convolutionKernel, "convolutionKernel == null");
			this.range = Objects.requireNonNull(range, "range == null");
			this.settingFilterBlur = Objects.requireNonNull(settingFilterBlur, "settingFilterBlur == null");
			this.settingFilterDetectEdges = Objects.requireNonNull(settingFilterDetectEdges, "settingFilterDetectEdges == null");
			this.settingFilterEmboss = Objects.requireNonNull(settingFilterEmboss, "settingFilterEmboss == null");
			this.settingFilterGradientHorizontal = Objects.requireNonNull(settingFilterGradientHorizontal, "settingFilterGradientHorizontal == null");
			this.settingFilterGradientVertical = Objects.requireNonNull(settingFilterGradientVertical, "settingFilterGradientVertical == null");
			this.settingFilterSharpen = Objects.requireNonNull(settingFilterSharpen, "settingFilterSharpen == null");
			this.pixels0 = Objects.requireNonNull(pixels0, "pixels0 == null");
			this.pixels1 = Objects.requireNonNull(pixels1, "pixels1 == null");
			this.isRunning = new AtomicBoolean(true);
			this.renderTimeMillis = new AtomicLong();
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public long getRenderTimeMillis() {
			return this.renderTimeMillis.get();
		}
		
		@Override
		public void run() {
			while(this.isRunning.get()) {
				final long renderTimeMillis0 = System.currentTimeMillis();
				
				synchronized(this.pixels1) {
					this.abstractRendererKernel.execute(this.range);
					this.abstractRendererKernel.get(this.pixels1);
					
					if(this.settingFilterBlur.isEnabled()) {
						this.convolutionKernel.update();
						this.convolutionKernel.enableBlur();
						this.convolutionKernel.execute(this.range);
						this.convolutionKernel.get();
					}
					
					if(this.settingFilterDetectEdges.isEnabled()) {
						this.convolutionKernel.update();
						this.convolutionKernel.enableDetectEdges();
						this.convolutionKernel.execute(this.range);
						this.convolutionKernel.get();
					}
					
					if(this.settingFilterEmboss.isEnabled()) {
						this.convolutionKernel.update();
						this.convolutionKernel.enableEmboss();
						this.convolutionKernel.execute(this.range);
						this.convolutionKernel.get();
					}
					
					if(this.settingFilterGradientHorizontal.isEnabled()) {
						this.convolutionKernel.update();
						this.convolutionKernel.enableGradientHorizontal();
						this.convolutionKernel.execute(this.range);
						this.convolutionKernel.get();
					}
					
					if(this.settingFilterGradientVertical.isEnabled()) {
						this.convolutionKernel.update();
						this.convolutionKernel.enableGradientVertical();
						this.convolutionKernel.execute(this.range);
						this.convolutionKernel.get();
					}
					
					if(this.settingFilterSharpen.isEnabled()) {
						this.convolutionKernel.update();
						this.convolutionKernel.enableSharpen();
						this.convolutionKernel.execute(this.range);
						this.convolutionKernel.get();
					}
					
					synchronized(this.pixels0) {
						System.arraycopy(this.pixels1, 0, this.pixels0, 0, this.pixels0.length);
					}
				}
				
				this.renderPass.incrementAndGet();
				
				final long renderTimeMillis1 = System.currentTimeMillis();
				final long renderTimeMillis2 = renderTimeMillis1 - renderTimeMillis0;
				
				this.renderTimeMillis.set(renderTimeMillis2);
				
				try {
					Thread.sleep(1);
				} catch(final InterruptedException e) {
//					Do nothing!
				}
			}
		}
		
		public void stop() {
			this.isRunning.set(false);
		}
	}
}