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
package org.dayflower.pathtracer.main;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import com.amd.aparapi.Range;

import org.dayflower.pathtracer.application.AbstractApplication;
import org.dayflower.pathtracer.application.JavaFX;
import org.dayflower.pathtracer.kernel.AbstractRendererKernel;
import org.dayflower.pathtracer.kernel.CPURendererKernel;
import org.dayflower.pathtracer.kernel.GPURendererKernel;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.CameraObserver;
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Sky;
import org.dayflower.pathtracer.scene.loader.SceneLoader;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.util.Timer;
import org.dayflower.pathtracer.util.Files;
import org.dayflower.pathtracer.util.Strings;
import org.macroing.image4j.Image;
import org.macroing.math4j.AngleF;
import org.macroing.math4j.QuaternionF;
import org.macroing.math4j.Vector3F;

/**
 * An implementation of {@link AbstractApplication} that performs Ambient Occlusion, Path Tracing, Ray Casting, Ray Marching or Ray Tracing.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class DayflowerApplication extends AbstractApplication implements CameraObserver {
	private static final String ENGINE_NAME = "Dayflower - Path Tracer";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private AbstractRendererKernel abstractRendererKernel;
	private final AtomicBoolean isRendering;
	private final AtomicInteger renderPass;
	private final Configuration configuration;
	private final Label labelFPS;
	private final Label labelKernelTime;
	private final Label labelPosition;
	private final Label labelRenderPass;
	private final Label labelRenderTime;
	private final Label labelSPS;
	private Range range;
	private RendererRunnable rendererRunnable;
	private Scene scene;
	private SceneLoader sceneLoader;
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
		this.isRendering = new AtomicBoolean();
		this.renderPass = new AtomicInteger();
		this.configuration = new Configuration();
		this.labelFPS = new Label("FPS: 0");
		this.labelKernelTime = new Label("Kernel Time: 0 ms");
		this.labelPosition = new Label("Position: [0.0, 0.0, 0.0]");
		this.labelRenderPass = new Label("Pass: 0");
		this.labelRenderTime = new Label("Time: 00:00:00");
		this.labelSPS = new Label("SPS: 00000000");
		this.sceneLoader = new SceneLoader(new File(this.configuration.getRootDirectory()), this.configuration.getSceneCompile(), this.configuration.getSceneName());
		this.timer = new Timer();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializes this {@code TestApplication} instance.
	 */
	@Override
	public void init() {
		setCanvasWidth(this.configuration.getCanvasWidth());
		setCanvasHeight(this.configuration.getCanvasHeight());
		setKernelWidth(this.configuration.getKernelWidth());
		setKernelHeight(this.configuration.getKernelHeight());
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::onExit));
	}
	
	/**
	 * Called by a {@link Camera} instance when its pitch has changed.
	 * 
	 * @param camera the {@code Camera} that called this method
	 * @param pitch the new pitch
	 */
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
	
	/**
	 * Called by a {@link Camera} instance when its yaw has changed.
	 * 
	 * @param camera the {@code Camera} that called this method
	 * @param yaw the new yaw
	 */
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
	 * Called when rendering.
	 * <p>
	 * Returns {@code true} if, and only if, rendering to the image is active, {@code false} otherwise.
	 * 
	 * @param graphicsContext a {@code GraphicsContext} that can be rendered to
	 * @return {@code true} if, and only if, rendering to the image is active, {@code false} otherwise
	 */
	@Override
	protected boolean render(final GraphicsContext graphicsContext) {
		final boolean isRendering = this.isRendering.get();
		
		if(isRendering) {
			final int renderPass = this.renderPass.get();
			
			final long renderTimeMillis = this.rendererRunnable.getRenderTimeMillis();
			final long fPS = renderTimeMillis > 0L ? 1000L / renderTimeMillis : 0L;
			final long sPS = renderTimeMillis > 0L ? 1000L / renderTimeMillis * getKernelWidth() * getKernelHeight() : 0L;
			
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
		} else {
			final double w = graphicsContext.getCanvas().getWidth();
			final double h = graphicsContext.getCanvas().getHeight();
			
			final int dotCount = (int)(this.timer.getSeconds() % 4L);
			
			final String dotString = Strings.repeat(".", dotCount);
			final String spaceString = Strings.repeat(" ", 3 - dotCount);
			final String string = dotString + spaceString;
			
//			Fill the background:
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.fillRect(0.0D, 0.0D, w, h);
			
//			Fill the text:
			graphicsContext.setFont(Font.font("Dialog", 24.0D));
			graphicsContext.setTextAlign(TextAlignment.CENTER);
			graphicsContext.setTextBaseline(VPos.CENTER);
			graphicsContext.setFill(Color.LIMEGREEN);
			graphicsContext.fillText("Loading" + string, w / 2.0D, h / 2.0D);
		}
		
		return isRendering;
	}
	
	/**
	 * Called when the {@code MenuBar} can be configured.
	 * 
	 * @param menuBar the {@code MenuBar} to configure
	 */
	@Override
	protected void configureMenuBar(final MenuBar menuBar) {
//		Create the "File" Menu:
		final MenuItem menuItemSave = JavaFX.newMenuItem("Save", this::doOnMenuItemSave);
		final MenuItem menuItemExit = JavaFX.newMenuItem("Exit", e -> exit());
		
		final Menu menuFile = JavaFX.newMenu("File", menuItemSave, menuItemExit);
		
		menuBar.getMenus().add(menuFile);
		
//		Create the "Camera" Menu:
		final ToggleGroup toggleGroupCameraLens = new ToggleGroup();
		
		final CheckMenuItem checkMenuItemWalkLock = JavaFX.newCheckMenuItem("Walk Lock", e -> doGetCamera().setWalkLockEnabled(!doGetCamera().isWalkLockEnabled()), doGetCamera().isWalkLockEnabled());
		
		final RadioMenuItem radioMenuItemFisheye = JavaFX.newRadioMenuItem("Fisheye Camera Lens", e -> doGetCamera().setFisheyeCameraLens(true), toggleGroupCameraLens, doGetCamera().isFisheyeCameraLens());
		final RadioMenuItem radioMenuItemThin = JavaFX.newRadioMenuItem("Thin Camera Lens", e -> doGetCamera().setThinCameraLens(true), toggleGroupCameraLens, doGetCamera().isThinCameraLens());
		
		final Menu menuCamera = JavaFX.newMenu("Camera", checkMenuItemWalkLock, radioMenuItemFisheye, radioMenuItemThin);
		
		menuBar.getMenus().add(menuCamera);
		
//		Create the "Renderer" Menu:
		final ToggleGroup toggleGroupRenderer = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemAmbientOcclusion = JavaFX.newRadioMenuItem("Ambient Occlusion", e -> doGetAbstractRendererKernel().setRendererType(AbstractRendererKernel.RENDERER_TYPE_AMBIENT_OCCLUSION), toggleGroupRenderer, doGetAbstractRendererKernel().isRendererTypeAmbientOcclusion());
		final RadioMenuItem radioMenuItemPathTracer = JavaFX.newRadioMenuItem("Path Tracer", e -> doGetAbstractRendererKernel().setRendererType(AbstractRendererKernel.RENDERER_TYPE_PATH_TRACER), toggleGroupRenderer, doGetAbstractRendererKernel().isRendererTypePathTracer());
		final RadioMenuItem radioMenuItemRayCaster = JavaFX.newRadioMenuItem("Ray Caster", e -> doGetAbstractRendererKernel().setRendererType(AbstractRendererKernel.RENDERER_TYPE_RAY_CASTER), toggleGroupRenderer, doGetAbstractRendererKernel().isRendererTypeRayCaster());
		final RadioMenuItem radioMenuItemRayMarcher = JavaFX.newRadioMenuItem("Ray Marcher", e -> doGetAbstractRendererKernel().setRendererType(AbstractRendererKernel.RENDERER_TYPE_RAY_MARCHER), toggleGroupRenderer, doGetAbstractRendererKernel().isRendererTypeRayMarcher());
		final RadioMenuItem radioMenuItemRayTracer = JavaFX.newRadioMenuItem("Ray Tracer", e -> doGetAbstractRendererKernel().setRendererType(AbstractRendererKernel.RENDERER_TYPE_RAY_TRACER), toggleGroupRenderer, doGetAbstractRendererKernel().isRendererTypeRayTracer());
		final RadioMenuItem radioMenuItemSurfaceNormals = JavaFX.newRadioMenuItem("Surface Normals", e -> doGetAbstractRendererKernel().setRendererType(AbstractRendererKernel.RENDERER_TYPE_SURFACE_NORMALS), toggleGroupRenderer, doGetAbstractRendererKernel().isRendererTypeSurfaceNormals());
		
		final Menu menuRenderer = JavaFX.newMenu("Renderer", radioMenuItemAmbientOcclusion, radioMenuItemPathTracer, radioMenuItemRayCaster, radioMenuItemRayMarcher, radioMenuItemRayTracer, radioMenuItemSurfaceNormals);
		
		menuBar.getMenus().add(menuRenderer);
		
//		Create the "Scene" Menu:
		final ToggleGroup toggleGroupShading = new ToggleGroup();
		
		final CheckMenuItem checkMenuItemNormalMapping = JavaFX.newCheckMenuItem("Normal Mapping", e -> doGetAbstractRendererKernel().toggleRendererNormalMapping(), doGetAbstractRendererKernel().getRendererNormalMapping() == AbstractRendererKernel.BOOLEAN_TRUE);
		final CheckMenuItem checkMenuItemWireframes = JavaFX.newCheckMenuItem("Wireframes", e -> doGetAbstractRendererKernel().toggleRendererWireframes(), doGetAbstractRendererKernel().getRendererWireframes() == AbstractRendererKernel.BOOLEAN_TRUE);
		
		final MenuItem menuItemEnterScene = JavaFX.newMenuItem("Enter Scene", e -> enter());
		
		final RadioMenuItem radioMenuItemFlatShading = JavaFX.newRadioMenuItem("Flat Shading", e -> doGetAbstractRendererKernel().setShaderType(AbstractRendererKernel.SHADER_TYPE_FLAT), toggleGroupShading, doGetAbstractRendererKernel().getShaderType() == AbstractRendererKernel.SHADER_TYPE_FLAT);
		final RadioMenuItem radioMenuItemGouraudShading = JavaFX.newRadioMenuItem("Gouraud Shading", e -> doGetAbstractRendererKernel().setShaderType(AbstractRendererKernel.SHADER_TYPE_GOURAUD), toggleGroupShading, doGetAbstractRendererKernel().getShaderType() == AbstractRendererKernel.SHADER_TYPE_GOURAUD);
		
		final Menu menuScene = JavaFX.newMenu("Scene", checkMenuItemNormalMapping, checkMenuItemWireframes, menuItemEnterScene, radioMenuItemFlatShading, radioMenuItemGouraudShading);
		
		menuBar.getMenus().add(menuScene);
		
//		Create the "Tone Mapper" Menu:
		final ToggleGroup toggleGroupToneMapper = new ToggleGroup();
		
		final RadioMenuItem radioMenuItemToneMapperFilmicCurveACESModified = JavaFX.newRadioMenuItem("Filmic Curve ACES Modified", e -> doGetAbstractRendererKernel().setToneMapperType(AbstractRendererKernel.TONE_MAPPER_TYPE_FILMIC_CURVE_ACES_MODIFIED), toggleGroupToneMapper, true);
		final RadioMenuItem radioMenuItemToneMapperReinhard = JavaFX.newRadioMenuItem("Reinhard", e -> doGetAbstractRendererKernel().setToneMapperType(AbstractRendererKernel.TONE_MAPPER_TYPE_REINHARD), toggleGroupToneMapper, false);
		final RadioMenuItem radioMenuItemToneMapperReinhardModified1 = JavaFX.newRadioMenuItem("Reinhard Modified v.1", e -> doGetAbstractRendererKernel().setToneMapperType(AbstractRendererKernel.TONE_MAPPER_TYPE_REINHARD_MODIFIED_1), toggleGroupToneMapper, false);
		final RadioMenuItem radioMenuItemToneMapperReinhardModified2 = JavaFX.newRadioMenuItem("Reinhard Modified v.2", e -> doGetAbstractRendererKernel().setToneMapperType(AbstractRendererKernel.TONE_MAPPER_TYPE_REINHARD_MODIFIED_2), toggleGroupToneMapper, false);
		
		final Menu menuToneMapper = JavaFX.newMenu("Tone Mapper", radioMenuItemToneMapperFilmicCurveACESModified, radioMenuItemToneMapperReinhard, radioMenuItemToneMapperReinhardModified1, radioMenuItemToneMapperReinhardModified2);
		
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
		
		this.range = Range.create(getKernelWidth() * getKernelHeight());
		
//		this.abstractRendererKernel = new CPURendererKernel(this.sceneLoader);
		this.abstractRendererKernel = new GPURendererKernel(this.sceneLoader);
		this.abstractRendererKernel.update(getKernelWidth(), getKernelHeight(), this.pixels1, this.range.getLocalSize(0));
		
		this.scene = this.abstractRendererKernel.getScene();
		
		final
		Camera camera = this.scene.getCamera();
		camera.setResolution(getKernelWidth(), getKernelHeight());
		camera.update();
		camera.addCameraObserver(this);
		
		this.rendererRunnable = new RendererRunnable(this.abstractRendererKernel, this.isRendering, this.renderPass, this.range, this.pixels0, this.pixels1);
		
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
		stage.setTitle(String.format("%s %s", ENGINE_NAME, this.configuration.getVersion()));
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
//		Create the Tab with the Camera settings:
		final Label labelFieldOfView = new Label("Field of View:");
		final Label labelApertureRadius = new Label("Aperture Radius:");
		final Label labelFocalDistance = new Label("Focal Distance:");
		final Label labelPitch = new Label("Pitch:");
		final Label labelYaw = new Label("Yaw:");
		
		final Slider sliderFieldOfView = JavaFX.newSlider(40.0D, 100.0D, doGetCamera().getFieldOfViewX(), 10.0D, 10.0D, true, true, false, this::doOnSliderFieldOfView);
		final Slider sliderApertureRadius = JavaFX.newSlider(0.0D, 25.0D, doGetCamera().getApertureRadius(), 1.0D, 5.0D, true, true, false, this::doOnSliderApertureRadius);
		final Slider sliderFocalDistance = JavaFX.newSlider(0.0D, 100.0D, doGetCamera().getFocalDistance(), 1.0D, 20.0D, true, true, false, this::doOnSliderFocalDistance);
		final Slider sliderPitch = JavaFX.newSlider(-90.0F, 90.0F, doGetCamera().getPitch().degrees, 10.0D, 20.0D, true, true, false, this::doOnSliderPitch);
		final Slider sliderYaw = JavaFX.newSlider(0.0D, 360.0F, doGetCamera().getYaw().degrees, 20.0D, 40.0D, true, true, false, this::doOnSliderYaw);
		
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
		
		final Slider sliderSunDirectionWorldX = JavaFX.newSlider(-1.0D, 1.0D, doGetSky().getSunDirectionWorld().x, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldX);
		final Slider sliderSunDirectionWorldY = JavaFX.newSlider(0.0D, 1.0D, doGetSky().getSunDirectionWorld().y, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldY);
		final Slider sliderSunDirectionWorldZ = JavaFX.newSlider(-1.0D, 1.0D, doGetSky().getSunDirectionWorld().z, 0.1D, 0.5D, true, true, false, this::doOnSliderSunDirectionWorldZ);
		final Slider sliderTurbidity = JavaFX.newSlider(2.0D, 8.0D, doGetSky().getTurbidity(), 0.5D, 1.0D, true, true, false, this::doOnSliderTurbidity);
		
		final CheckBox checkBoxToggleSky = JavaFX.newCheckBox("Toggle Sky", this::doOnCheckBoxToggleSky, true);
		final CheckBox checkBoxToggleSun = JavaFX.newCheckBox("Toggle Sun", this::doOnCheckBoxToggleSun, true);
		
		final
		VBox vBoxSunAndSky = new VBox();
		vBoxSunAndSky.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vBoxSunAndSky.getChildren().addAll(labelSunDirectionWorldX, sliderSunDirectionWorldX, labelSunDirectionWorldY, sliderSunDirectionWorldY, labelSunDirectionWorldZ, sliderSunDirectionWorldZ, labelTurbidity, sliderTurbidity, checkBoxToggleSky, checkBoxToggleSun);
		
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
		
		final Slider sliderMaximumDistance = JavaFX.newSlider(0.0D, 1000.0D, doGetAbstractRendererKernel().getRendererAOMaximumDistance(), 50.0D, 200.0D, true, true, true, this::doOnSliderMaximumDistance);
		final Slider sliderMaximumRayDepth = JavaFX.newSlider(0.0D, 20.0D, doGetAbstractRendererKernel().getRendererPTRayDepthMaximum(), 1.0D, 5.0D, true, true, true, this::doOnSliderMaximumRayDepth);
		final Slider sliderAmplitude = JavaFX.newSlider(0.0D, 10.0D, doGetAbstractRendererKernel().getGlobalAmplitude(), 1.0D, 5.0D, true, true, false, this::doOnSliderAmplitude);
		final Slider sliderFrequency = JavaFX.newSlider(0.0D, 10.0D, doGetAbstractRendererKernel().getGlobalFrequency(), 1.0D, 5.0D, true, true, false, this::doOnSliderFrequency);
		final Slider sliderGain = JavaFX.newSlider(0.0D, 10.0D, doGetAbstractRendererKernel().getGlobalGain(), 1.0D, 5.0D, true, true, false, this::doOnSliderGain);
		final Slider sliderLacunarity = JavaFX.newSlider(0.0D, 10.0D, doGetAbstractRendererKernel().getGlobalLacunarity(), 1.0D, 5.0D, true, true, false, this::doOnSliderLacunarity);
		
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
		
//		Create the Tab with the settings for the tone mappers:
		final Label labelExposure = new Label("Exposure");
		
		final Slider sliderExposure = JavaFX.newSlider(0.0D, 2.0D, doGetAbstractRendererKernel().getToneMapperExposure(), 0.2D, 0.4D, true, true, false, this::doOnSliderExposure);
		
		final
		VBox vBoxToneMapper = new VBox();
		vBoxToneMapper.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		vBoxToneMapper.getChildren().addAll(labelExposure, sliderExposure);
		
		final
		Tab tabToneMapper = new Tab();
		tabToneMapper.setClosable(false);
		tabToneMapper.setContent(vBoxToneMapper);
		tabToneMapper.setText("Tone Mapper");
		
		tabPane.getTabs().add(tabToneMapper);
		
//		Select the default Tab:
		tabPane.getSelectionModel().select(tabCamera);
	}
	
	/**
	 * Called before this {@code DayflowerApplication} is finally exiting.
	 */
	@Override
	protected void onExit() {
		final AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
		
		final RendererRunnable rendererRunnable = this.rendererRunnable;
		
		if(rendererRunnable != null) {
			rendererRunnable.stop();
		}
		
		if(abstractRendererKernel != null) {
			abstractRendererKernel.dispose();
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
	
//	private long previousTime = System.nanoTime();
	
	/**
	 * Called when updating.
	 */
	@Override
	protected void update() {
		final AtomicInteger renderPass = this.renderPass;
		
		final Scene scene = this.scene;
		
		final Camera camera = scene.getCamera();
		
		final AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
		
		final Timer timer = this.timer;
		
		final byte[] pixels1 = this.pixels1;
		
		final float velocity = abstractRendererKernel.isRendererTypeRayMarcher() ? 1.0F : 5.0F;
		final float movement = velocity;
		
		final int selectedPrimitiveIndex = abstractRendererKernel.getSelectedPrimitiveIndex();
		
		final Optional<Primitive> optionalSelectedPrimitive = scene.getSelectedPrimitive(selectedPrimitiveIndex);
		
		final Primitive selectedPrimitive = optionalSelectedPrimitive.orElse(null);
		
//		final long currentTime = System.nanoTime();
		
//		final float delta = (float)((currentTime - this.previousTime) / 1000000000.0D);
		
//		this.previousTime = currentTime;
		
//		for(final Primitive primitive : scene.getPrimitives()) {
//			if(!(primitive.getShape() instanceof Plane)) {
//				primitive.getTransform().rotate(QuaternionF.fromVector(Vector3F.x(), AngleF.radians(delta)));
//			}
//		}
		
		if(isKeyPressed(KeyCode.A)) {
			if(selectedPrimitive != null) {
				selectedPrimitive.getTransform().moveX(1.0F);
			} else {
				camera.strafe(-movement);
			}
		}
		
		if(isKeyPressed(KeyCode.D)) {
			if(selectedPrimitive != null) {
				selectedPrimitive.getTransform().moveX(-1.0F);
			} else {
				camera.strafe(movement);
			}
		}
		
		if(isKeyPressed(KeyCode.E)) {
			if(selectedPrimitive != null) {
				selectedPrimitive.getTransform().moveY(-1.0F);
			} else {
				camera.changeAltitude(-0.5F);
			}
		}
		
		if(isKeyPressed(KeyCode.ENTER, true) && !hasEntered()) {
			enter();
		}
		
		if(isKeyPressed(KeyCode.ESCAPE, true)) {
			exit();
		}
		
		if(isKeyPressed(KeyCode.M, true)) {
			abstractRendererKernel.togglePrimitiveMaterial();
		}
		
		if(isKeyPressed(KeyCode.Q)) {
			if(selectedPrimitive != null) {
				selectedPrimitive.getTransform().moveY(1.0F);
			} else {
				camera.changeAltitude(0.5F);
			}
		}
		
		if(isKeyPressed(KeyCode.R, true)) {
			synchronized(pixels1) {
				abstractRendererKernel.togglePrimitiveSelection(getMouseX(), getMouseY());
			}
		}
		
		if(isKeyPressed(KeyCode.S)) {
			if(selectedPrimitive != null) {
				selectedPrimitive.getTransform().moveZ(1.0F);
			} else {
				camera.forward(-movement);
			}
		}
		
		if(isKeyPressed(KeyCode.W)) {
			if(selectedPrimitive != null) {
				selectedPrimitive.getTransform().moveZ(-1.0F);
			} else {
				camera.forward(movement);
			}
		}
		
		if(isKeyPressed(KeyCode.X) && selectedPrimitive != null) {
			selectedPrimitive.getTransform().rotate(QuaternionF.fromVector(Vector3F.x(), AngleF.degrees(1.0F)));
		}
		
		if(isKeyPressed(KeyCode.Z) && selectedPrimitive != null) {
			selectedPrimitive.getTransform().rotate(QuaternionF.fromVector(Vector3F.z(), AngleF.degrees(1.0F)));
		}
		
		if(scene.isPrimitiveUpdateRequired()) {
			abstractRendererKernel.clear();
			abstractRendererKernel.updatePrimitives();
		}
		
		if(isMouseDragging() || isMouseMoving() && isMouseRecentering() || camera.hasUpdated() || abstractRendererKernel.hasChanged()) {
			synchronized(pixels1) {
				abstractRendererKernel.clear();
				abstractRendererKernel.setChanged(false);
				abstractRendererKernel.updateCamera();
				
				renderPass.set(0);
				
				timer.restart();
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private AbstractRendererKernel doGetAbstractRendererKernel() {
		return this.abstractRendererKernel;
	}
	
	private Camera doGetCamera() {
		return this.scene.getCamera();
	}
	
	private Image doCreateImage() {
		synchronized(this.pixels1) {
			return Image.toImage(getCanvasWidth(), getCanvasHeight(), this.pixels1);
		}
	}
	
	private Sky doGetSky() {
		return this.scene.getSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnCheckBoxToggleSky(final ActionEvent e) {
		synchronized(this.pixels1) {
			final
			AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
			abstractRendererKernel.toggleSky();
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnCheckBoxToggleSun(final ActionEvent e) {
		synchronized(this.pixels1) {
			final
			AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
			abstractRendererKernel.toggleSun();
		}
	}
	
	@SuppressWarnings("unused")
	private void doOnMenuItemSave(final ActionEvent e) {
		final File directory = new File(this.configuration.getImageDirectory());
		final File file = Files.findNextFile(directory, "Dayflower-Image-%s.png");
		
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		System.out.println("Saving image to \"" + file.getAbsolutePath() + "\".");
		
		final
		Image image = doCreateImage();
		image.save(file);
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderAmplitude(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setGlobalAmplitude(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderApertureRadius(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setApertureRadius(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderExposure(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setToneMapperExposure(newValue.floatValue());
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
		this.abstractRendererKernel.setGlobalFrequency(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderGain(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setGlobalGain(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderLacunarity(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setGlobalLacunarity(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderMaximumDistance(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setRendererAOMaximumDistance(newValue.floatValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderMaximumRayDepth(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.abstractRendererKernel.setRendererPTRayDepthMaximum(newValue.intValue());
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderPitch(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setPitch(AngleF.degrees(newValue.floatValue(), -90.0F, 90.0F));
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldX(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setSunDirectionWorldX(newValue.floatValue());
		
		this.abstractRendererKernel.updateSunAndSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldY(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setSunDirectionWorldY(newValue.floatValue());
		
		this.abstractRendererKernel.updateSunAndSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderSunDirectionWorldZ(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setSunDirectionWorldZ(newValue.floatValue());
		
		this.abstractRendererKernel.updateSunAndSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderTurbidity(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getSky().setTurbidity(newValue.floatValue());
		
		this.abstractRendererKernel.updateSunAndSky();
	}
	
	@SuppressWarnings("unused")
	private void doOnSliderYaw(final ObservableValue<? extends Number> observableValue, final Number oldValue, final Number newValue) {
		this.scene.getCamera().setYaw(AngleF.degrees(newValue.floatValue()));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class RendererRunnable implements Runnable {
		private final AbstractRendererKernel abstractRendererKernel;
		private final AtomicBoolean isRendering;
		private final AtomicBoolean isRunning;
		private final AtomicInteger renderPass;
		private final AtomicLong renderTimeMillis;
		private final Range range;
		private final byte[] pixels0;
		private final byte[] pixels1;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public RendererRunnable(final AbstractRendererKernel abstractRendererKernel, final AtomicBoolean isRendering, final AtomicInteger renderPass, final Range range, final byte[] pixels0, final byte[] pixels1) {
			this.abstractRendererKernel = Objects.requireNonNull(abstractRendererKernel, "abstractRendererKernel == null");
			this.isRendering = Objects.requireNonNull(isRendering, "isRendering == null");
			this.renderPass = Objects.requireNonNull(renderPass, "renderPass == null");
			this.range = Objects.requireNonNull(range, "range == null");
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
			final AbstractRendererKernel abstractRendererKernel = this.abstractRendererKernel;
			
			final AtomicBoolean isRendering = this.isRendering;
			final AtomicBoolean isRunning = this.isRunning;
			
			final AtomicInteger renderPass = this.renderPass;
			
			final AtomicLong renderTimeMillis = this.renderTimeMillis;
			
			final Range range = this.range;
			
			final byte[] pixels0 = this.pixels0;
			final byte[] pixels1 = this.pixels1;
			
			isRendering.set(false);
			
			while(isRunning.get()) {
				final long renderTimeMillis0 = System.currentTimeMillis();
				
				synchronized(pixels1) {
					abstractRendererKernel.execute(range);
					abstractRendererKernel.clearFilmFlags();
					abstractRendererKernel.get(pixels1);
					
					isRendering.set(true);
					
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