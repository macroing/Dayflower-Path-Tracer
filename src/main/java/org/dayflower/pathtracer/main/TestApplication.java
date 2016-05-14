/**
 * Copyright 2009 - 2016 J&#246;rgen Lundgren
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

import java.lang.reflect.Field;//TODO: Add Javadocs.
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
import org.dayflower.pathtracer.kernel.RendererKernel;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Shape;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.util.FPSCounter;

//TODO: Add Javadocs.
public final class TestApplication extends AbstractApplication {
	private static final String ENGINE_NAME = "Dayflower Engine";
	private static final String ENGINE_VERSION = "v.0.0.11";
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final RendererKernel rendererKernel;
	private final Camera camera = new Camera();
	private final Label labelApertureRadius = new Label("Aperture radius: N/A");
	private final Label labelFieldOfView = new Label("Field of view: N/A - N/A");
	private final Label labelFocalDistance = new Label("Focal distance: N/A");
	private final Label labelFPS = new Label("FPS: 0");
	private final Label labelRenderMode = new Label("Render mode: GPU");
	private final Label labelRenderPass = new Label("Render pass: 0");
	private final Label labelRenderTime = new Label("Render time: 00:00:00");
	private final Label labelRenderType = new Label("Render type: Path Tracer");
	private final Scene scene = Scenes.newGirlScene();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public TestApplication() {
		super(String.format("%s %s", ENGINE_NAME, ENGINE_VERSION));
		
		this.rendererKernel = new RendererKernel(false, getCanvasWidth(), getCanvasHeight(), this.camera, this.scene);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	@Override
	protected void doConfigurePixels(final byte[] pixels) {
		this.rendererKernel.compile(pixels, getCanvasWidth(), getCanvasHeight());
		
		final
		Camera camera = this.camera;
		camera.setApertureRadius(0.004F);
		camera.setCameraPredicate(this::doTest);
		camera.setCenter(55.0F, 42.0F, 155.6F);
		camera.setFieldOfViewX(40.0F);
		camera.setFocalDistance(4.0F);
		camera.setPitch(0.0F);
		camera.setRadius(4.0F);
		camera.setResolution(800.0F / getCanvasWidthScale(), 800.0F / getCanvasHeightScale());
		camera.setWalkLockEnabled(true);
		camera.setYaw(0.0F);
		camera.update();
	}
	
//	TODO: Add Javadocs.
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
		
		hBox.getChildren().addAll(this.labelRenderPass, region0, this.labelFPS, region1, this.labelRenderTime, region2, this.labelRenderMode, region3, this.labelRenderType, region4, this.labelApertureRadius, region5, this.labelFocalDistance, region6, this.labelFieldOfView);
		
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
		print("- K: Toggle walk-lock");
		print("- L: Toggle mouse recentering and cursor visibility");
		print("- M: Increase maximum ray depth");
		print("- N: Decrease maximum ray depth");
		print("- UP ARROW: Decrease pitch");
		print("- LEFT ARROW: Increase yaw");
		print("- DOWN ARROW: Increase pitch");
		print("- RIGHT ARROW: Decrease yaw");
		print("- MOVE MOUSE: Look around");
		print("");
		print("Supported Features:");
		print("- Shape: Plane");
		print("- Shape: Sphere");
		print("- Shape: Triangle");
		print("- Material: Clear Coat");
		print("- Material: Diffuse");
		print("- Material: Metal");
		print("- Material: Refractive");
		print("- Material: Specular");
		print("- Texture: Checkerboard");
		print("- Texture: Image");
		print("- Texture: Solid");
		print("- Perez Sun Sky Model");
		print("- Normal Mapping: Texture: Image");
		print("- Normal Mapping: Perlin Noise");
		print("- Cosine-Weighted Hemisphere-Sampling of Sun");
		print("- Acceleration Structure: Bounding Volume Hierarchy");
		
		setCursorHidden(true);
		setRecenteringMouse(true);
	}
	
//	TODO: Add Javadocs.
	@Override
	protected void onMouseDragged(final float x, final float y) {
		this.camera.changeYaw(x * 0.005F);
		this.camera.changePitch(-(y * 0.005F));
	}
	
//	TODO: Add Javadocs.
	@Override
	protected void onMouseMoved(final float x, final float y) {
		if(isRecenteringMouse()) {
			this.camera.changeYaw(x * 0.005F);
			this.camera.changePitch(-(y * 0.005F));
		}
	}
	
//	TODO: Add Javadocs.
	@Override
	public void run() {
		final AtomicInteger renderPass = new AtomicInteger();
		
		final AtomicLong currentTimeMillis = new AtomicLong(System.currentTimeMillis());
		
		final Camera camera = this.camera;
		
		final FPSCounter fPSCounter = getFPSCounter();
		
		final RendererKernel rendererKernel = this.rendererKernel;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> rendererKernel.dispose()));
		
		while(true) {
			final Range range = Range.create(getCanvasWidth() * getCanvasHeight());
			
			final float velocity = 250.0F;
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
			
			if(isKeyPressed(KeyCode.ESCAPE)) {
				Platform.exit();
			}
			
			if(isKeyPressed(KeyCode.F, true)) {
				setCanvasWidthScale(Math.min(getCanvasWidthScale() + 1, 8));
				setCanvasHeightScale(Math.min(getCanvasHeightScale() + 1, 8));
				setCanvasWidth(1024 / getCanvasWidthScale());
				setCanvasHeight(768 / getCanvasHeightScale());
			}
			
			if(isKeyPressed(KeyCode.G, true)) {
				setCanvasWidthScale(Math.max(getCanvasWidthScale() - 1, 1));
				setCanvasHeightScale(Math.max(getCanvasHeightScale() - 1, 1));
				setCanvasWidth(1024 / getCanvasWidthScale());
				setCanvasHeight(768 / getCanvasHeightScale());
			}
			
			if(isKeyPressed(KeyCode.I)) {
				camera.changeFocalDistance(-0.1F);
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
				rendererKernel.setDepthMaximum(rendererKernel.getDepthMaximum() + 1);
			}
			
			if(isKeyPressed(KeyCode.N, true)) {
				rendererKernel.setDepthMaximum(Math.max(rendererKernel.getDepthMaximum() - 1, 1));
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
				rendererKernel.reset();
				
				renderPass.set(0);
				
				currentTimeMillis.set(System.currentTimeMillis());
			}
			
			rendererKernel.execute(range);
			
			fPSCounter.update();
			
			final long elapsedTimeMillis = System.currentTimeMillis() - currentTimeMillis.get();
			
			final
			Lock lock = getLock();
			lock.lock();
			
			try {
				rendererKernel.get(rendererKernel.getPixels());
			} finally {
				lock.unlock();
			}
			
			final int renderPass0 = renderPass.incrementAndGet();
			
			Platform.runLater(() -> {
				final long hours = elapsedTimeMillis / (60L * 60L * 1000L);
				final long minutes = (elapsedTimeMillis - (hours * 60L * 60L * 1000L)) / (60L * 1000L);
				final long seconds = (elapsedTimeMillis - ((hours * 60L * 60L * 1000L) + (minutes * 60L * 1000L))) / 1000L;
				
				this.labelApertureRadius.setText(String.format("Aperture radius: %.2f", Float.valueOf(camera.getApertureRadius())));
				this.labelFieldOfView.setText(String.format("Field of view: %.2f - %.2f", Float.valueOf(camera.getFieldOfViewX()), Float.valueOf(camera.getFieldOfViewY())));
				this.labelFocalDistance.setText(String.format("Focal distance: %.2f", Float.valueOf(camera.getFocalDistance())));
				this.labelFPS.setText(String.format("FPS: %s", Long.toString(fPSCounter.getFPS())));
				this.labelRenderPass.setText(String.format("Render pass: %s", Integer.toString(renderPass0)));
				this.labelRenderTime.setText(String.format("Render time: %02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)));
			});
			
			try {
				Thread.sleep(0L);
			} catch(final InterruptedException e) {
//				Do nothing.
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
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
}