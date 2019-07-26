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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;

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
import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.kernel.ConvolutionKernel;
import org.dayflower.pathtracer.kernel.RendererKernel;
import org.dayflower.pathtracer.math.AngleF;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.CameraObserver;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.compiler.CompiledScene;
import org.dayflower.pathtracer.scene.compiler.SceneCompiler;
import org.dayflower.pathtracer.util.Timer;
import org.dayflower.pathtracer.util.FPSCounter;
import org.dayflower.pathtracer.util.Files;

/**
 * An implementation of {@link AbstractApplication} that performs Path Tracing, Ray Casting or Ray Marching.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class DayflowerApplication extends AbstractApplication implements CameraObserver {
	private static final String ENGINE_NAME = "Dayflower - Path Tracer";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicInteger renderPass;
	private ConvolutionKernel convolutionKernel;
	private final Label labelFPS;
	private final Label labelKernelTime;
	private final Label labelPosition;
	private final Label labelRenderPass;
	private final Label labelRenderTime;
	private final Label labelSPS;
	private Range range;
	private RendererKernel rendererKernel;
	private RendererRunnable rendererRunnable;
	private final Setting settingFilterBlur;
	private final Setting settingFilterDetectEdges;
	private final Setting settingFilterEmboss;
	private final Setting settingFilterGradientHorizontal;
	private final Setting settingFilterGradientVertical;
	private final Setting settingFilterSharpen;
	private Scene scene;
	private Slider sliderPitch;
	private Slider sliderYaw;
	private final Timer timer;
	private byte[] pixels0;
	private byte[] pixels1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code TestApplication} instance.
	 */
	public DayflowerApplication() {
		this.renderPass = new AtomicInteger();
		this.labelFPS = new Label("FPS: 0");
		this.labelKernelTime = new Label("Kernel Time: 0 ms");
		this.labelPosition = new Label("Position: [0.0, 0.0, 0.0]");
		this.labelRenderPass = new Label("Pass: 0");
		this.labelRenderTime = new Label("Time: 00:00:00");
		this.labelSPS = new Label("SPS: 00000000");
		this.settingFilterBlur = new Setting("Filter.Blur");
		this.settingFilterDetectEdges = new Setting("Filter.DetectEdges");
		this.settingFilterEmboss = new Setting("Filter.Emboss");
		this.settingFilterGradientHorizontal = new Setting("Filter.Gradient.Horizontal");
		this.settingFilterGradientVertical = new Setting("Filter.Gradient.Vertical");
		this.settingFilterSharpen = new Setting("Filter.Sharpen");
		this.timer = new Timer();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes this {@code TestApplication} instance.
	 */
	@Override
	public void init() {
		this.scene = Scenes.getSceneByName(Dayflower.getSceneName());
		
		final String sceneFilename = Dayflower.getSceneFilename(String.format("%s.scene", this.scene.getName()));
		
		final File sceneFile = new File(sceneFilename);
		
		if(!sceneFile.isFile() || Dayflower.getSceneCompile()) {
			final
			CompiledScene compiledScene = SceneCompiler.compile(this.scene);
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
		
		this.rendererKernel = new RendererKernel(getKernelWidth(), getKernelHeight(), this.scene.getCamera(), this.scene.getSky(), sceneFilename);
		
		final
		Camera camera = this.scene.getCamera();
		camera.setResolution(getKernelWidth(), getKernelHeight());
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
		final RendererKernel rendererKernel = this.rendererKernel;
		
		final Scene scene = this.scene;
		
		final Camera camera = scene.getCamera();
		
//		Create the "File" Menu:
		final MenuItem menuItemSave = JavaFX.newMenuItem("Save", this::doOnMenuItemSave);
		final MenuItem menuItemExit = JavaFX.newMenuItem("Exit", e -> exit());
		
		final Menu menuFile = JavaFX.newMenu("File", menuItemSave, menuItemExit);
		
		menuBar.getMenus().add(menuFile);
		
//		Create the "Camera" Menu:
		final ToggleGroup toggleGroupCameraLens = new ToggleGroup();
		
		final CheckMenuItem checkMenuItemWalkLock = JavaFX.newCheckMenuItem("Walk Lock", e -> camera.setWalkLockEnabled(!camera.isWalkLockEnabled()), camera.isWalkLockEnabled());
		
		final RadioMenuItem radioMenuItemFisheye = JavaFX.newRadioMenuItem("Fisheye Camera Lens", e -> camera.setFisheyeCameraLens(true), camera.isFisheyeCameraLens(), toggleGroupCameraLens);
		final RadioMenuItem radioMenuItemThin = JavaFX.newRadioMenuItem("Thin Camera Lens", e -> camera.setThinCameraLens(true), camera.isThinCameraLens(), toggleGroupCameraLens);
		
		final Menu menuCamera = JavaFX.newMenu("Camera", checkMenuItemWalkLock, radioMenuItemFisheye, radioMenuItemThin);
		
		menuBar.getMenus().add(menuCamera);
		
//		Create the "Effect" Menu:
		final CheckMenuItem checkMenuItemBlur = JavaFX.newCheckMenuItem("Blur", e -> this.settingFilterBlur.toggle());
		final CheckMenuItem checkMenuItemDetectEdges = JavaFX.newCheckMenuItem("Detect Edges", e -> this.settingFilterDetectEdges.toggle());
		final CheckMenuItem checkMenuItemEmboss = JavaFX.newCheckMenuItem("Emboss", e -> this.settingFilterEmboss.toggle());
		final CheckMenuItem checkMenuItemGradientHorizontal = JavaFX.newCheckMenuItem("Gradient (Horizontal)", e -> this.settingFilterGradientHorizontal.toggle());
		final CheckMenuItem checkMenuItemGradientVertical = JavaFX.newCheckMenuItem("Gradient (Vertical)", e -> this.settingFilterGradientVertical.toggle());
		final CheckMenuItem checkMenuItemSharpen = JavaFX.newCheckMenuItem("Sharpen", e -> this.settingFilterSharpen.toggle());
		final CheckMenuItem checkMenuItemGrayscale = JavaFX.newCheckMenuItem("Grayscale", e -> rendererKernel.setEffectGrayScale(!rendererKernel.isEffectGrayScale()));
		final CheckMenuItem checkMenuItemSepiaTone = JavaFX.newCheckMenuItem("Sepia Tone", e -> rendererKernel.setEffectSepiaTone(!rendererKernel.isEffectSepiaTone()));
		
		final Menu menuEffect = JavaFX.newMenu("Effect", checkMenuItemBlur, checkMenuItemDetectEdges, checkMenuItemEmboss, checkMenuItemGradientHorizontal, checkMenuItemGradientVertical, checkMenuItemSharpen, checkMenuItemGrayscale, checkMenuItemSepiaTone);
		
		menuBar.getMenus().add(menuEffect);
		
//		Create the "Renderer" Menu:
		final ToggleGroup toggleGroupRenderer = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemAmbientOcclusion = JavaFX.newRadioMenuItem("Ambient Occlusion", e -> rendererKernel.setAmbientOcclusion(true), rendererKernel.isAmbientOcclusion(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemPathTracer = JavaFX.newRadioMenuItem("Path Tracer", e -> rendererKernel.setPathTracing(true), rendererKernel.isPathTracing(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayCaster = JavaFX.newRadioMenuItem("Ray Caster", e -> rendererKernel.setRayCasting(true), rendererKernel.isRayCasting(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayMarcher = JavaFX.newRadioMenuItem("Ray Marcher", e -> rendererKernel.setRayMarching(true), rendererKernel.isRayMarching(), toggleGroupRenderer);
		final RadioMenuItem radioMenuItemRayTracer = JavaFX.newRadioMenuItem("Ray Tracer", e -> rendererKernel.setRayTracing(true), rendererKernel.isRayTracing(), toggleGroupRenderer);
		
		final Menu menuRenderer = JavaFX.newMenu("Renderer", radioMenuItemAmbientOcclusion, radioMenuItemPathTracer, radioMenuItemRayCaster, radioMenuItemRayMarcher, radioMenuItemRayTracer);
		
		menuBar.getMenus().add(menuRenderer);
		
//		Create the "Scene" Menu:
		final ToggleGroup toggleGroupShading = new ToggleGroup();
		
		final CheckMenuItem checkMenuItemNormalMapping = JavaFX.newCheckMenuItem("Normal Mapping", e -> rendererKernel.setNormalMapping(!rendererKernel.isNormalMapping()), rendererKernel.isNormalMapping());
		final CheckMenuItem checkMenuItemWireframes = JavaFX.newCheckMenuItem("Wireframes", e -> rendererKernel.setRenderingWireframes(!rendererKernel.isRenderingWireframes()), rendererKernel.isRenderingWireframes());
		
		final MenuItem menuItemEnterScene = JavaFX.newMenuItem("Enter Scene", e -> enter());
		
		final RadioMenuItem radioMenuItemFlatShading = JavaFX.newRadioMenuItem("Flat Shading", e -> rendererKernel.setShadingFlat(), rendererKernel.isShadingFlat(), toggleGroupShading);
		final RadioMenuItem radioMenuItemGouraudShading = JavaFX.newRadioMenuItem("Gouraud Shading", e -> rendererKernel.setShadingGouraud(), rendererKernel.isShadingGouraud(), toggleGroupShading);
		
		final Menu menuScene = JavaFX.newMenu("Scene", checkMenuItemNormalMapping, checkMenuItemWireframes, menuItemEnterScene, radioMenuItemFlatShading, radioMenuItemGouraudShading);
		
		menuBar.getMenus().add(menuScene);
		
//		Create the "Tone Mapper" Menu:
		final ToggleGroup toggleGroupToneMapper = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemToneMapperFilmicCurve1 = JavaFX.newRadioMenuItem("Filmic Curve v.1", e -> rendererKernel.setToneMappingAndGammaCorrectionFilmicCurve1(), false, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperFilmicCurve2 = JavaFX.newRadioMenuItem("Filmic Curve v.2", e -> rendererKernel.setToneMappingAndGammaCorrectionFilmicCurve2(), true, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperLinear = JavaFX.newRadioMenuItem("Linear", e -> rendererKernel.setToneMappingAndGammaCorrectionLinear(), false, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperReinhard1 = JavaFX.newRadioMenuItem("Reinhard v.1", e -> rendererKernel.setToneMappingAndGammaCorrectionReinhard1(), false, toggleGroupToneMapper);
		final RadioMenuItem radioMenuItemToneMapperReinhard2 = JavaFX.newRadioMenuItem("Reinhard v.2", e -> rendererKernel.setToneMappingAndGammaCorrectionReinhard2(), false, toggleGroupToneMapper);
		
		final Menu menuToneMapper = JavaFX.newMenu("Tone Mapper", radioMenuItemToneMapperFilmicCurve1, radioMenuItemToneMapperFilmicCurve2, radioMenuItemToneMapperLinear, radioMenuItemToneMapperReinhard1, radioMenuItemToneMapperReinhard2);
		
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
		this.rendererKernel.updateLocalVariables(this.range.getLocalSize(0));
		this.rendererKernel.compile(this.pixels1, getKernelWidth(), getKernelHeight());
		this.rendererRunnable = new RendererRunnable(this.renderPass, this.convolutionKernel, this.range, this.rendererKernel, this.settingFilterBlur, this.settingFilterDetectEdges, this.settingFilterEmboss, this.settingFilterGradientHorizontal, this.settingFilterGradientVertical, this.settingFilterSharpen, this.pixels0, this.pixels1);
		
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
		hBox.getChildren().addAll(this.labelRenderPass, this.labelFPS, this.labelSPS, this.labelRenderTime, this.labelKernelTime, this.labelPosition);
	}
	
	/**
	 * Called when the {@code TabPane} can be configured.
	 * 
	 * @param tabPane the {@code TabPane} to configure
	 */
	@Override
	protected void configureTabPane(final TabPane tabPane) {
		final RendererKernel rendererKernel = this.rendererKernel;
		
		final Scene scene = this.scene;
		
		final Camera camera = scene.getCamera();
		
		final Sky sky = scene.getSky();
		
//		Create the Tab with the Camera settings:
		final Label labelFieldOfView = new Label("Field of View:");
		final Label labelApertureRadius = new Label("Aperture Radius:");
		final Label labelFocalDistance = new Label("Focal Distance:");
		final Label labelPitch = new Label("Pitch:");
		final Label labelYaw = new Label("Yaw:");
		
		final Slider sliderFieldOfView = JavaFX.newSlider(40.0D, 100.0D, camera.getFieldOfViewX(), 10.0D, 10.0D, true, true, false, this::doOnSliderFieldOfView);
		final Slider sliderApertureRadius = JavaFX.newSlider(0.0D, 25.0D, camera.getApertureRadius(), 1.0D, 5.0D, true, true, false, this::doOnSliderApertureRadius);
		final Slider sliderFocalDistance = JavaFX.newSlider(0.0D, 100.0D, camera.getFocalDistance(), 1.0D, 20.0D, true, true, false, this::doOnSliderFocalDistance);
		final Slider sliderPitch = JavaFX.newSlider(-90.0F, 90.0F, camera.getPitch().degrees, 10.0D, 20.0D, true, true, false, this::doOnSliderPitch);
		final Slider sliderYaw = JavaFX.newSlider(0.0D, 360.0F, camera.getYaw().degrees, 20.0D, 40.0D, true, true, false, this::doOnSliderYaw);
		
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
		
		final Slider sliderSunDirectionWorldX = JavaFX.newSlider(-1.0D, 1.0D, sky.getSunDirectionWorld().x, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldX);
		final Slider sliderSunDirectionWorldY = JavaFX.newSlider(0.0D, 1.0D, sky.getSunDirectionWorld().y, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldY);
		final Slider sliderSunDirectionWorldZ = JavaFX.newSlider(-1.0D, 1.0D, sky.getSunDirectionWorld().z, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldZ);
		final Slider sliderTurbidity = JavaFX.newSlider(2.0D, 8.0D, sky.getTurbidity(), 0.5D, 1.0D, true, true, false, this::doOnSliderTurbidity);
		
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
		final Label labelAmbientOcclusion = new Label("Ambient Occlusion");
		final Label labelMaximumDistance = new Label("Maximum Distance:");
		final Label labelPathTracer = new Label("Path Tracer");
		final Label labelMaximumRayDepth = new Label("Maximum Ray Depth:");
		final Label labelRayMarcher = new Label("Ray Marcher");
		final Label labelAmplitude = new Label("Amplitude:");
		final Label labelFrequency = new Label("Frequency:");
		final Label labelGain = new Label("Gain:");
		final Label labelLacunarity = new Label("Lacunarity:");
		
		labelAmbientOcclusion.setFont(Font.font(16.0D));
		labelAmbientOcclusion.setPadding(new Insets(0.0D, 0.0D, 10.0D, 0.0D));
		labelPathTracer.setFont(Font.font(16.0D));
		labelPathTracer.setPadding(new Insets(0.0D, 0.0D, 10.0D, 0.0D));
		labelRayMarcher.setFont(Font.font(16.0D));
		labelRayMarcher.setPadding(new Insets(10.0D, 0.0D, 10.0D, 0.0D));
		
		final Slider sliderMaximumDistance = JavaFX.newSlider(0.0D, 1000.0D, rendererKernel.getMaximumDistanceAO(), 50.0D, 200.0D, true, true, true, this::doOnSliderMaximumDistance);
		final Slider sliderMaximumRayDepth = JavaFX.newSlider(0.0D, 20.0D, rendererKernel.getDepthMaximum(), 1.0D, 5.0D, true, true, true, this::doOnSliderMaximumRayDepth);
		final Slider sliderAmplitude = JavaFX.newSlider(0.0D, 10.0D, rendererKernel.getAmplitude(), 1.0D, 5.0D, true, true, false, this::doOnSliderAmplitude);
		final Slider sliderFrequency = JavaFX.newSlider(0.0D, 10.0D, rendererKernel.getFrequency(), 1.0D, 5.0D, true, true, false, this::doOnSliderFrequency);
		final Slider sliderGain = JavaFX.newSlider(0.0D, 10.0D, rendererKernel.getGain(), 1.0D, 5.0D, true, true, false, this::doOnSliderGain);
		final Slider sliderLacunarity = JavaFX.newSlider(0.0D, 10.0D, rendererKernel.getLacunarity(), 1.0D, 5.0D, true, true, false, this::doOnSliderLacunarity);
		
		final
		VBox vBoxRenderer = new VBox();
		vBoxRenderer.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vBoxRenderer.getChildren().addAll(labelAmbientOcclusion, labelMaximumDistance, sliderMaximumDistance, labelPathTracer, labelMaximumRayDepth, sliderMaximumRayDepth, labelRayMarcher, labelAmplitude, sliderAmplitude, labelFrequency, sliderFrequency, labelGain, sliderGain, labelLacunarity, sliderLacunarity);
		
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
		final ConvolutionKernel convolutionKernel = this.convolutionKernel;
		
		final RendererKernel rendererKernel = this.rendererKernel;
		
		final RendererRunnable rendererRunnable = this.rendererRunnable;
		
		if(rendererRunnable != null) {
			rendererRunnable.stop();
		}
		
		if(rendererKernel != null) {
			rendererKernel.dispose();
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
		final
		Camera camera = this.scene.getCamera();
		camera.changeYaw(AngleF.degrees(-x * 0.5F));
		camera.changePitch(AngleF.degrees(-(y * 0.5F), -90.0F, 90.0F));
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
			final
			Camera camera = this.scene.getCamera();
			camera.changeYaw(AngleF.degrees(-x * 0.5F));
			camera.changePitch(AngleF.degrees(-(y * 0.5F), -90.0F, 90.0F));
		}
	}
	
	/**
	 * Called when rendering.
	 */
	@Override
	protected void render() {
		final
		FPSCounter fPSCounter = getFPSCounter();
		fPSCounter.update();
		
		final int renderPass = this.renderPass.get();
		
		final long renderTimeMillis = this.rendererRunnable.getRenderTimeMillis();
		final long fPS = fPSCounter.getFPS();
		final long sPS = fPS * getKernelWidth() * getKernelHeight();
		
		final Camera camera = this.scene.getCamera();
		
		final Float x = Float.valueOf(camera.getEyeX());
		final Float y = Float.valueOf(camera.getEyeY());
		final Float z = Float.valueOf(camera.getEyeZ());
		
		this.labelFPS.setText(String.format("FPS: %s", Long.toString(fPS)));
		this.labelKernelTime.setText(String.format("Kernel Time: %s ms", Long.valueOf(renderTimeMillis)));
		this.labelPosition.setText(String.format("Position: [%s, %s, %s]", x, y, z));
		this.labelRenderPass.setText(String.format("Pass: %s", Integer.toString(renderPass)));
		this.labelRenderTime.setText(String.format("Time: %s", this.timer.getTime()));
		this.labelSPS.setText(String.format("SPS: %08d", Long.valueOf(sPS)));
	}
	
	/**
	 * Called when updating.
	 */
	@Override
	protected void update() {
		final AtomicInteger renderPass = this.renderPass;
		
		final Camera camera = this.scene.getCamera();
		
		final RendererKernel rendererKernel = this.rendererKernel;
		
		final Timer timer = this.timer;
		
		final byte[] pixels1 = this.pixels1;
		
		final float velocity = rendererKernel.isRayMarching() ? 1.0F : 250.0F;
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
			synchronized(pixels1) {
				rendererKernel.toggleMaterial();
				rendererKernel.updateResetStatus();
				rendererKernel.reset();
			}
		}
		
		if(isKeyPressed(KeyCode.Q)) {
			camera.changeAltitude(0.5F);
		}
		
		if(isKeyPressed(KeyCode.R, true)) {
			synchronized(pixels1) {
				final int mouseX = getMouseX();
				final int mouseY = getMouseY();
				final int index = mouseY * getKernelWidth() + mouseX;
				
				final int[] primitiveOffsetsForPrimaryRay = rendererKernel.getPrimitiveOffsetsForPrimaryRay();
				
				if(index >= 0 && index < primitiveOffsetsForPrimaryRay.length) {
					final int selectedPrimitiveOffset = primitiveOffsetsForPrimaryRay[index];
					
					if(selectedPrimitiveOffset == rendererKernel.getSelectedPrimitiveOffset()) {
						rendererKernel.setSelectedPrimitiveOffset(-1);
					} else {
						rendererKernel.setSelectedPrimitiveOffset(selectedPrimitiveOffset);
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
		
		if(isMouseDragging() || isMouseMoving() && isMouseRecentering() || camera.hasUpdated() || rendererKernel.isResetRequired()) {
			synchronized(pixels1) {
				rendererKernel.updateResetStatus();
				rendererKernel.reset();
				
				renderPass.set(0);
				
				timer.restart();
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private BufferedImage doCreateBufferedImage() {
		final int width = getCanvasWidth();
		final int height = getCanvasHeight();
		
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		final int[] data = DataBufferInt.class.cast(bufferedImage.getRaster().getDataBuffer()).getData();
		
		synchronized(this.pixels1) {
			for(int i = 0, j = 0; i < data.length; i++, j += 4) {
				final int r = this.pixels1[j + 2];
				final int g = this.pixels1[j + 1];
				final int b = this.pixels1[j + 0];
				
				final int rGB = Color.toRGB(r, g, b);
				
				data[i] = rGB;
			}
		}
		
		return bufferedImage;
	}
	
	@SuppressWarnings("unused")
	private void doOnCheckBoxToggleClouds(final ActionEvent e) {
		synchronized(this.pixels1) {
			final
			RendererKernel rendererKernel = this.rendererKernel;
			rendererKernel.toggleClouds();
			rendererKernel.updateResetStatus();
			rendererKernel.reset();
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnCheckBoxToggleSunAndSky(final ActionEvent e) {
		synchronized(this.pixels1) {
			final
			RendererKernel rendererKernel = this.rendererKernel;
			rendererKernel.toggleSunAndSky();
			rendererKernel.updateResetStatus();
			rendererKernel.reset();
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnMenuItemSave(final ActionEvent e) {
		final File directory = new File(Dayflower.getImageDirectory());
		final File file = Files.findNextFile(directory, "Dayflower-Image-%s.png");
		
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		System.out.println("Saving image to \"" + file.getAbsolutePath() + "\".");
		
		final BufferedImage bufferedImage = doCreateBufferedImage();
		
		try {
			ImageIO.write(bufferedImage, "png", file);
		} catch(final IOException e1) {
//			Do nothing for now.
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderAmplitude(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.rendererKernel.setAmplitude(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderApertureRadius(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setApertureRadius(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderFieldOfView(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setFieldOfViewX(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderFocalDistance(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setFocalDistance(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderFrequency(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.rendererKernel.setFrequency(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderGain(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.rendererKernel.setGain(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderLacunarity(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.rendererKernel.setLacunarity(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderMaximumDistance(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.rendererKernel.setMaximumDistanceAO(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderMaximumRayDepth(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.rendererKernel.setDepthMaximum(newValue.intValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderPitch(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setPitch(AngleF.degrees(newValue.floatValue(), -90.0F, 90.0F));
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldX(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setX(newValue.floatValue());
		
		this.rendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldY(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setY(newValue.floatValue());
		
		this.rendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldZ(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setZ(newValue.floatValue());
		
		this.rendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderTurbidity(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setTurbidity(newValue.floatValue());
		
		this.rendererKernel.updateSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderYaw(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setYaw(AngleF.degrees(newValue.floatValue()));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class RendererRunnable implements Runnable {
		private final AtomicBoolean isRunning;
		private final AtomicInteger renderPass;
		private final AtomicLong renderTimeMillis;
		private final ConvolutionKernel convolutionKernel;
		private final Range range;
		private final RendererKernel rendererKernel;
		private final Setting settingFilterBlur;
		private final Setting settingFilterDetectEdges;
		private final Setting settingFilterEmboss;
		private final Setting settingFilterGradientHorizontal;
		private final Setting settingFilterGradientVertical;
		private final Setting settingFilterSharpen;
		private final byte[] pixels0;
		private final byte[] pixels1;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public RendererRunnable(final AtomicInteger renderPass, final ConvolutionKernel convolutionKernel, final Range range, final RendererKernel rendererKernel, final Setting settingFilterBlur, final Setting settingFilterDetectEdges, final Setting settingFilterEmboss, final Setting settingFilterGradientHorizontal, final Setting settingFilterGradientVertical, final Setting settingFilterSharpen, final byte[] pixels0, final byte[] pixels1) {
			this.renderPass = Objects.requireNonNull(renderPass, "renderPass == null");
			this.convolutionKernel = Objects.requireNonNull(convolutionKernel, "convolutionKernel == null");
			this.range = Objects.requireNonNull(range, "range == null");
			this.rendererKernel = Objects.requireNonNull(rendererKernel, "rendererKernel == null");
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
			final AtomicBoolean isRunning = this.isRunning;
			
			final AtomicInteger renderPass = this.renderPass;
			
			final AtomicLong renderTimeMillis = this.renderTimeMillis;
			
			final ConvolutionKernel convolutionKernel = this.convolutionKernel;
			
			final Range range = this.range;
			
			final RendererKernel rendererKernel = this.rendererKernel;
			
			final Setting settingFilterBlur = this.settingFilterBlur;
			final Setting settingFilterDetectEdges = this.settingFilterDetectEdges;
			final Setting settingFilterEmboss = this.settingFilterEmboss;
			final Setting settingFilterGradientHorizontal = this.settingFilterGradientHorizontal;
			final Setting settingFilterGradientVertical = this.settingFilterGradientVertical;
			final Setting settingFilterSharpen = this.settingFilterSharpen;
			
			final byte[] pixels0 = this.pixels0;
			final byte[] pixels1 = this.pixels1;
			
			while(isRunning.get()) {
				final long renderTimeMillis0 = System.currentTimeMillis();
				
				synchronized(pixels1) {
					rendererKernel.execute(range);
					rendererKernel.get(pixels1);
					
					if(settingFilterBlur.isEnabled()) {
						convolutionKernel.update();
						convolutionKernel.enableBlur();
						convolutionKernel.execute(range);
						convolutionKernel.get();
					}
					
					if(settingFilterDetectEdges.isEnabled()) {
						convolutionKernel.update();
						convolutionKernel.enableDetectEdges();
						convolutionKernel.execute(range);
						convolutionKernel.get();
					}
					
					if(settingFilterEmboss.isEnabled()) {
						convolutionKernel.update();
						convolutionKernel.enableEmboss();
						convolutionKernel.execute(range);
						convolutionKernel.get();
					}
					
					if(settingFilterGradientHorizontal.isEnabled()) {
						convolutionKernel.update();
						convolutionKernel.enableGradientHorizontal();
						convolutionKernel.execute(range);
						convolutionKernel.get();
					}
					
					if(settingFilterGradientVertical.isEnabled()) {
						convolutionKernel.update();
						convolutionKernel.enableGradientVertical();
						convolutionKernel.execute(range);
						convolutionKernel.get();
					}
					
					if(settingFilterSharpen.isEnabled()) {
						convolutionKernel.update();
						convolutionKernel.enableSharpen();
						convolutionKernel.execute(range);
						convolutionKernel.get();
					}
					
					synchronized(pixels0) {
						System.arraycopy(pixels1, 0, pixels0, 0, pixels0.length);
					}
				}
				
				renderPass.incrementAndGet();
				
				final long renderTimeMillis1 = System.currentTimeMillis();
				final long renderTimeMillis2 = renderTimeMillis1 - renderTimeMillis0;
				
				renderTimeMillis.set(renderTimeMillis2);
				
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