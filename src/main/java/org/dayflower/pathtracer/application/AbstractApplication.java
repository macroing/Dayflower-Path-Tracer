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
package org.dayflower.pathtracer.application;

import java.awt.AWTException;
import java.awt.Robot;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.dayflower.pathtracer.util.FPSCounter;

/**
 * An extension of {@code Application} that adds a bunch of functionality such as scaling and cursor visibility.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractApplication extends Application {
	/**
	 * The default canvas height.
	 */
	private static final int CANVAS_HEIGHT = 800;
	
	/**
	 * The default canvas width.
	 */
	private static final int CANVAS_WIDTH = 800;
	
	/**
	 * The default kernel height.
	 */
	private static final int KERNEL_HEIGHT = 800;
	
	/**
	 * The default kernel width.
	 */
	private static final int KERNEL_WIDTH = 800;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final long MOUSE_MOVEMENT_TIMEOUT = 100L;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicBoolean hasRequestedToExit = new AtomicBoolean();
	private final AtomicBoolean hasUpdatedCursor = new AtomicBoolean();
	private final AtomicBoolean isCursorHidden = new AtomicBoolean();
	private final AtomicBoolean isMouseDragging = new AtomicBoolean();
	private final AtomicBoolean isMouseRecentering = new AtomicBoolean();
	private final AtomicInteger canvasHeight = new AtomicInteger(CANVAS_HEIGHT);
	private final AtomicInteger canvasWidth = new AtomicInteger(CANVAS_WIDTH);
	private final AtomicInteger kernelHeight = new AtomicInteger(KERNEL_HEIGHT);
	private final AtomicInteger kernelWidth = new AtomicInteger(KERNEL_WIDTH);
	private final AtomicInteger keysPressed = new AtomicInteger();
	private final AtomicInteger mouseDraggedDeltaX = new AtomicInteger();
	private final AtomicInteger mouseDraggedDeltaY = new AtomicInteger();
	private final AtomicInteger mouseDraggedX = new AtomicInteger();
	private final AtomicInteger mouseDraggedY = new AtomicInteger();
	private final AtomicInteger mouseMovedDeltaX = new AtomicInteger();
	private final AtomicInteger mouseMovedDeltaY = new AtomicInteger();
	private final AtomicInteger mouseMovedX = new AtomicInteger();
	private final AtomicInteger mouseMovedY = new AtomicInteger();
	private final AtomicInteger mouseX = new AtomicInteger();
	private final AtomicInteger mouseY = new AtomicInteger();
	private final AtomicLong mouseMovementTime = new AtomicLong();
	private final boolean[] isKeyPressed = new boolean[KeyCode.values().length];
	private final boolean[] isKeyPressedOnce = new boolean[KeyCode.values().length];
	private Canvas canvas;
	private final FPSCounter fPSCounter = new FPSCounter();
	private Robot robot;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractApplication} with no title.
	 */
	protected AbstractApplication() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts this {@code AbstractApplication} instance.
	 * 
	 * @param stage a {@code Stage}
	 */
	@Override
	public final void start(final Stage stage) {
		this.robot = doCreateRobot();
		
		final
		ImageView imageView = new ImageView();
		imageView.setSmooth(true);
		
		this.canvas = new Canvas(getCanvasWidth(), getCanvasHeight());
		this.canvas.addEventFilter(MouseEvent.ANY, e -> this.canvas.requestFocus());
		this.canvas.addEventFilter(KeyEvent.ANY, e -> this.canvas.requestFocus());
		this.canvas.setFocusTraversable(true);
		this.canvas.setOnKeyPressed(this::doOnKeyPressed);
		this.canvas.setOnKeyReleased(this::doOnKeyReleased);
		this.canvas.setOnMouseDragged(this::doOnMouseDragged);
		this.canvas.setOnMouseMoved(this::doOnMouseMoved);
		this.canvas.setOnMousePressed(this::doOnMousePressed);
		this.canvas.setOnMouseReleased(this::doOnMouseReleased);
		
		final MenuBar menuBar = new MenuBar();
		
		final
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		hBox.setSpacing(20.0D);
		
		final TabPane tabPane = new TabPane();
		
		final
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(menuBar);
		borderPane.setCenter(this.canvas);
		borderPane.setBottom(hBox);
		borderPane.setLeft(tabPane);
		
		configureMenuBar(menuBar);
		configureTabPane(tabPane);
		configureStatusBar(hBox);
		configureStage(stage);
		
		final Scene scene = new Scene(borderPane);
		
		stage.setResizable(false);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();
		
		final PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
		
		final AtomicBoolean hasRequestedToExit = this.hasRequestedToExit;
		final AtomicBoolean hasUpdatedCursor = this.hasUpdatedCursor;
		
		final WritableImage writableImage = new WritableImage(getKernelWidth(), getKernelHeight());
		
		final PixelWriter pixelWriter = writableImage.getPixelWriter();
		
		final ByteBuffer byteBuffer = ByteBuffer.allocate(getKernelWidth() * getKernelHeight() * 4);
		
		final byte[] pixels = byteBuffer.array();
		
		imageView.setImage(writableImage);
		imageView.setViewport(new Rectangle2D(0.0D, 0.0D, getKernelWidth(), getKernelHeight()));
		
		configurePixels(pixels);
		
		final Canvas canvas = this.canvas;
		
		new AnimationTimer() {
			@Override
			public void handle(final long now) {
				if(hasRequestedToExit()) {
					if(hasEntered()) {
						hasRequestedToExit.set(false);
						
						leave();
					} else {
						onExit();
						
						Platform.exit();
						
						return;
					}
				}
				
				update();
				render();
				
				if(hasUpdatedCursor.compareAndSet(true, false)) {
					scene.setCursor(isCursorHidden() ? Cursor.NONE : Cursor.DEFAULT);
				}
				
				if(pixelWriter != null) {
					synchronized(pixels) {
						pixelWriter.setPixels(0, 0, getKernelWidth(), getKernelHeight(), pixelFormat, byteBuffer, getKernelWidth() * 4);
					}
				}
				
				final WritableImage writableImage = imageView.snapshot(null, null);
				
				final
				GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
				graphicsContext.drawImage(writableImage, 0.0D, 0.0D, getCanvasWidth(), getCanvasHeight());
			}
		}.start();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code Setting} represents a setting that can be enabled or disabled.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static final class Setting {
		private final AtomicBoolean isEnabled;
		private final String name;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Constructs a new {@code Setting} instance.
		 * <p>
		 * Calling this constructor is equivalent to calling {@code new Setting(name, false)}.
		 * <p>
		 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param name the name to use
		 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
		 */
		public Setting(final String name) {
			this(name, false);
		}
		
		/**
		 * Constructs a new {@code Setting} instance.
		 * <p>
		 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
		 * 
		 * @param name the name to use
		 * @param isEnabled {@code true} if enabled, {@code false} otherwise
		 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
		 */
		public Setting(final String name, final boolean isEnabled) {
			this.name = Objects.requireNonNull(name, "name == null");
			this.isEnabled = new AtomicBoolean(isEnabled);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * Compares {@code object} to this {@code Setting} instance for equality.
		 * <p>
		 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Setting}, and their respective values are equal, {@code false} otherwise.
		 * 
		 * @param object the {@code Object} to compare to this {@code Setting} instance for equality
		 * @return {@code true} if, and only if, {@code object} is an instance of {@code Setting}, and their respective values are equal, {@code false} otherwise
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) {
				return true;
			} else if(!(object instanceof Setting)) {
				return false;
			} else if(!Objects.equals(getName(), Setting.class.cast(object).getName())) {
				return false;
			} else if(isEnabled() != Setting.class.cast(object).isEnabled()) {
				return false;
			} else {
				return true;
			}
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code Setting} is disabled, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code Setting} is disabled, {@code false} otherwise
		 */
		public boolean isDisabled() {
			return !isEnabled();
		}
		
		/**
		 * Returns {@code true} if, and only if, this {@code Setting} is enabled, {@code false} otherwise.
		 * 
		 * @return {@code true} if, and only if, this {@code Setting} is enabled, {@code false} otherwise
		 */
		public boolean isEnabled() {
			return this.isEnabled.get();
		}
		
		/**
		 * Returns a hash code for this {@code Setting} instance.
		 * 
		 * @return a hash code for this {@code Setting} instance
		 */
		@Override
		public int hashCode() {
			return Objects.hash(getName(), Boolean.valueOf(isEnabled()));
		}
		
		/**
		 * Returns the name of this {@code Setting} instance.
		 * 
		 * @return the name of this {@code Setting} instance
		 */
		public String getName() {
			return this.name;
		}
		
		/**
		 * Returns a {@code String} representation of this {@code Setting} instance.
		 * 
		 * @return a {@code String} representation of this {@code Setting} instance
		 */
		@Override
		public String toString() {
			return String.format("new Setting(\"%s\", %s)", getName(), Boolean.toString(isEnabled()));
		}
		
		/**
		 * Disables this {@code Setting} instance.
		 */
		public void disable() {
			set(false);
		}
		
		/**
		 * Enables this {@code Setting} instance.
		 */
		public void enable() {
			set(true);
		}
		
		/**
		 * Enables or disables this {@code Setting} instance.
		 * 
		 * @param isEnabled {@code true} if enabled, {@code false} otherwise
		 */
		public void set(final boolean isEnabled) {
			this.isEnabled.set(isEnabled);
		}
		
		/**
		 * Toggles this {@code Setting} instance.
		 */
		public void toggle() {
			set(!isEnabled());
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns {@code true} if, and only if, this {@code AbstractApplication} has been entered like an FPS-game, {@code false} otherwise.
	 * <p>
	 * To enter this {@code AbstractApplication} like an FPS-game, call {@link #enter()}.
	 * <p>
	 * If it has been entered like an FPS-game and you want to leave, call {@link #leave()}.
	 * <p>
	 * When it's been entered like an FPS-game, the cursor will be hidden and the mouse will be re-centering.
	 * 
	 * @return {@code true} if, and only if, this {@code AbstractApplication} has been entered like an FPS-game, {@code false} otherwise
	 */
	protected final boolean hasEntered() {
		return isCursorHidden() || isMouseRecentering();
	}
	
	/**
	 * Returns {@code true} if, and only if, exit has been requested, {@code false} otherwise.
	 * <p>
	 * To request this {@code AbstractApplication} to exit, call {@link #exit}.
	 * 
	 * @return {@code true} if, and only if, exit has been requested, {@code false} otherwise
	 */
	protected final boolean hasRequestedToExit() {
		return this.hasRequestedToExit.get();
	}
	
	/**
	 * Returns {@code true} if, and only if, the cursor is hidden, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the cursor is hidden, {@code false} otherwise
	 */
	protected final boolean isCursorHidden() {
		return this.isCursorHidden.get();
	}
	
	/**
	 * Returns {@code true} if, and only if, at least one key is being pressed, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, at least one key is being pressed, {@code false} otherwise
	 */
	protected final boolean isKeyPressed() {
		return this.keysPressed.get() > 0;
	}
	
	/**
	 * Returns {@code true} if, and only if, the key denoted by {@code keyCode} is being pressed, {@code false} otherwise.
	 * <p>
	 * Calling this method is equivalent to calling {@code isKeyPressed(keyCode, false)}.
	 * <p>
	 * If {@code keyCode} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param keyCode a {@code KeyCode}
	 * @return {@code true} if, and only if, the key denoted by {@code keyCode} is being pressed, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code keyCode} is {@code null}
	 */
	protected final boolean isKeyPressed(final KeyCode keyCode) {
		return isKeyPressed(keyCode, false);
	}
	
	/**
	 * Returns {@code true} if, and only if, the key denoted by {@code keyCode} is being pressed, {@code false} otherwise.
	 * <p>
	 * If {@code isKeyPressedOnce} is {@code true}, only the first call to this method will return {@code true} per press-release cycle given a specific key.
	 * <p>
	 * If {@code keyCode} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param keyCode a {@code KeyCode}
	 * @param isKeyPressedOnce {@code true} if, and only if, a key press should occur at most one time per press-release cycle, {@code false} otherwise
	 * @return {@code true} if, and only if, the key denoted by {@code keyCode} is being pressed, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code keyCode} is {@code null}
	 */
	protected final boolean isKeyPressed(final KeyCode keyCode, final boolean isKeyPressedOnce) {
		final boolean isKeyPressed = this.isKeyPressed[keyCode.ordinal()];
		
		if(isKeyPressedOnce) {
			final boolean isKeyPressedOnce0 = this.isKeyPressedOnce[keyCode.ordinal()];
			
			if(isKeyPressed && !isKeyPressedOnce0) {
				this.isKeyPressedOnce[keyCode.ordinal()] = true;
				
				return true;
			}
			
			return false;
		}
		
		return isKeyPressed;
	}
	
	/**
	 * Returns {@code true} if, and only if, the mouse is being dragged, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the mouse is being dragged, {@code false} otherwise
	 */
	protected final boolean isMouseDragging() {
		return this.isMouseDragging.get();
	}
	
	/**
	 * Returns {@code true} if, and only if, the mouse is being moved, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the mouse is being moved, {@code false} otherwise
	 */
	protected final boolean isMouseMoving() {
		return System.currentTimeMillis() - this.mouseMovementTime.get() <= MOUSE_MOVEMENT_TIMEOUT;
	}
	
	/**
	 * Returns {@code true} if, and only if, mouse re-centering is being performed, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, mouse re-centering is being performed, {@code false} otherwise
	 */
	protected final boolean isMouseRecentering() {
		return this.isMouseRecentering.get();
	}
	
	/**
	 * Returns the {@link FPSCounter} associated with this {@code AbstractApplication}.
	 * 
	 * @return the {@code FPSCounter} associated with this {@code AbstractApplication}
	 */
	protected final FPSCounter getFPSCounter() {
		return this.fPSCounter;
	}
	
	/**
	 * Returns the canvas height.
	 * 
	 * @return the canvas height
	 */
	protected final int getCanvasHeight() {
		return this.canvasHeight.get();
	}
	
	/**
	 * Returns the canvas width.
	 * 
	 * @return the canvas width
	 */
	protected final int getCanvasWidth() {
		return this.canvasWidth.get();
	}
	
	/**
	 * Returns the kernel height.
	 * 
	 * @return the kernel height
	 */
	protected final int getKernelHeight() {
		return this.kernelHeight.get();
	}
	
	/**
	 * Returns the kernel width.
	 * 
	 * @return the kernel width
	 */
	protected final int getKernelWidth() {
		return this.kernelWidth.get();
	}
	
	/**
	 * Returns the X-coordinate of the mouse.
	 * 
	 * @return the X-coordinate of the mouse
	 */
	protected final int getMouseX() {
		return this.mouseX.get();
	}
	
	/**
	 * Returns the Y-coordinate of the mouse.
	 * 
	 * @return the Y-coordinate of the mouse
	 */
	protected final int getMouseY() {
		return this.mouseY.get();
	}
	
	/**
	 * Called when the {@code MenuBar} can be configured.
	 * 
	 * @param menuBar the {@code MenuBar} to configure
	 */
	protected abstract void configureMenuBar(final MenuBar menuBar);
	
	/**
	 * Called when pixels can be configured at start.
	 * 
	 * @param pixels a {@code byte} array with pixel data
	 */
	protected abstract void configurePixels(final byte[] pixels);
	
	/**
	 * Called when the primary {@code Stage} can be configured.
	 * 
	 * @param stage the primary {@code Stage} to configure
	 */
	protected abstract void configureStage(final Stage stage);
	
	/**
	 * Called when the status bar can be configured.
	 * 
	 * @param hBox a {@code HBox} that acts as a status bar
	 */
	protected abstract void configureStatusBar(final HBox hBox);
	
	/**
	 * Called when the {@code TabPane} can be configured.
	 * 
	 * @param tabPane the {@code TabPane} to configure
	 */
	protected abstract void configureTabPane(final TabPane tabPane);
	
	/**
	 * Call this method when it's time to exit.
	 * <p>
	 * When this {@code AbstractApplication} is exiting, it will call {@link #onExit()} before finally exiting.
	 * <p>
	 * If this {@code AbstractApplication} has been entered like an FPS-game, it will leave this state instead of exiting. In this case you'll have to call this method twice in order to exit.
	 */
	protected final void exit() {
		this.hasRequestedToExit.set(true);
	}
	
	/**
	 * Enters the FPS-game mode.
	 * <p>
	 * If you want to leave this FPS-game mode, call {@link #leave()}.
	 */
	protected final void enter() {
		setCursorHidden(true);
		setMouseRecentering(true);
	}
	
	/**
	 * Leaves the FPS-game mode.
	 * <p>
	 * If you want to enter this FPS-game mode, call {@link #enter()}.
	 */
	protected final void leave() {
		setCursorHidden(false);
		setMouseRecentering(false);
	}
	
	/**
	 * Called before this {@code AbstractApplication} is finally exiting.
	 */
	protected abstract void onExit();
	
	/**
	 * Called when the mouse is dragged.
	 * 
	 * @param x the new X-coordinate
	 * @param y the new Y-coordinate
	 */
	protected abstract void onMouseDragged(final float x, final float y);
	
	/**
	 * Called when the mouse is moved.
	 * 
	 * @param x the new X-coordinate
	 * @param y the new Y-coordinate
	 */
	protected abstract void onMouseMoved(final float x, final float y);
	
	/**
	 * Called when rendering.
	 */
	protected abstract void render();
	
	/**
	 * Sets a new canvas height.
	 * 
	 * @param canvasHeight a new canvas height
	 */
	protected final void setCanvasHeight(final int canvasHeight) {
		this.canvasHeight.set(canvasHeight);
	}
	
	/**
	 * Sets a new canvas width.
	 * 
	 * @param canvasWidth a new canvas width
	 */
	protected final void setCanvasWidth(final int canvasWidth) {
		this.canvasWidth.set(canvasWidth);
	}
	
	/**
	 * Sets whether the cursor should be hidden or shown.
	 * 
	 * @param isCursorHidden {@code true} if, and only if, the cursor should be hidden, {@code false} otherwise
	 */
	protected final void setCursorHidden(final boolean isCursorHidden) {
		if(this.isCursorHidden.compareAndSet(!isCursorHidden, isCursorHidden)) {
			this.hasUpdatedCursor.set(true);
		}
	}
	
	/**
	 * Sets a new kernel height.
	 * 
	 * @param kernelHeight a new kernel height
	 */
	protected final void setKernelHeight(final int kernelHeight) {
		this.kernelHeight.set(kernelHeight);
	}
	
	/**
	 * Sets a new kernel width.
	 * 
	 * @param kernelWidth a new kernel width
	 */
	protected final void setKernelWidth(final int kernelWidth) {
		this.kernelWidth.set(kernelWidth);
	}
	
	/**
	 * Sets the mouse re-centering.
	 * 
	 * @param isMouseRecentering {@code true} if, and only if, mouse re-centering should be performed, {@code false} otherwise
	 */
	protected final void setMouseRecentering(final boolean isMouseRecentering) {
		this.isMouseRecentering.set(isMouseRecentering);
	}
	
	/**
	 * Called when updating.
	 */
	protected abstract void update();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doOnKeyPressed(final KeyEvent e) {
		if(!this.isKeyPressed[e.getCode().ordinal()]) {
			this.keysPressed.incrementAndGet();
		}
		
		this.isKeyPressed[e.getCode().ordinal()] = true;
	}
	
	private void doOnKeyReleased(final KeyEvent e) {
		if(this.isKeyPressed[e.getCode().ordinal()]) {
			this.keysPressed.decrementAndGet();
		}
		
		this.isKeyPressed[e.getCode().ordinal()] = false;
		this.isKeyPressedOnce[e.getCode().ordinal()] = false;
	}
	
	private void doOnMouseDragged(final MouseEvent e) {
		this.mouseDraggedX.addAndGet(this.mouseDraggedDeltaX.get() - (int)(e.getScreenX()));
		this.mouseDraggedY.addAndGet(this.mouseDraggedDeltaY.get() - (int)(e.getScreenY()));
		this.mouseDraggedDeltaX.set((int)(e.getScreenX()));
		this.mouseDraggedDeltaY.set((int)(e.getScreenY()));
		this.mouseMovedDeltaX.set(0);
		this.mouseMovedDeltaY.set(0);
		this.mouseMovedX.set(0);
		this.mouseMovedY.set(0);
		this.mouseX.set((int)(e.getX()));
		this.mouseY.set((int)(e.getY()));
		
		onMouseDragged(this.mouseDraggedX.getAndSet(0), this.mouseDraggedY.getAndSet(0));
	}
	
	private void doOnMouseMoved(final MouseEvent e) {
		final int mouseMovedDeltaX = this.mouseMovedDeltaX.get();
		final int mouseMovedDeltaY = this.mouseMovedDeltaY.get();
		
		this.mouseMovementTime.set(System.currentTimeMillis());
		
		if(mouseMovedDeltaX != 0 || mouseMovedDeltaY != 0) {
			this.mouseMovedX.addAndGet(this.mouseMovedDeltaX.get() - (int)(e.getScreenX()));
			this.mouseMovedY.addAndGet(this.mouseMovedDeltaY.get() - (int)(e.getScreenY()));
		}
		
		this.mouseX.set((int)(e.getX()));
		this.mouseY.set((int)(e.getY()));
		
		onMouseMoved(this.mouseMovedX.getAndSet(0), this.mouseMovedY.getAndSet(0));
		
		if(isMouseRecentering()) {
			final Bounds bounds = this.canvas.localToScreen(this.canvas.getBoundsInLocal());
			
			final int minX = (int)(bounds.getMinX());
			final int minY = (int)(bounds.getMinY());
			final int width = (int)(bounds.getWidth());
			final int height = (int)(bounds.getHeight());
			final int x = minX + width / 2;
			final int y = minY + height / 2;
			
			this.robot.mouseMove(x, y);
			
			this.mouseMovedDeltaX.set(x);
			this.mouseMovedDeltaY.set(y);
		} else {
			this.mouseMovedDeltaX.set((int)(e.getScreenX()));
			this.mouseMovedDeltaY.set((int)(e.getScreenY()));
		}
	}
	
	private void doOnMousePressed(final MouseEvent e) {
		this.isMouseDragging.set(true);
		this.mouseDraggedDeltaX.set((int)(e.getScreenX()));
		this.mouseDraggedDeltaY.set((int)(e.getScreenY()));
	}
	
	@SuppressWarnings("unused")
	private void doOnMouseReleased(final MouseEvent e) {
		this.isMouseDragging.set(false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Robot doCreateRobot() {
		try {
			return new Robot();
		} catch(final AWTException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}