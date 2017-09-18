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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.dayflower.pathtracer.util.FPSCounter;

/**
 * An extension of {@code Application} that adds a bunch of functionality such as scaling and cursor visibility.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public abstract class AbstractApplication extends Application implements Runnable {
	/**
	 * The default height scale.
	 */
	private static final int CANVAS_HEIGHT_SCALE = 1;
	
	/**
	 * The default height.
	 */
	private static final int CANVAS_HEIGHT = 768 / CANVAS_HEIGHT_SCALE;
	
	/**
	 * The default width scale.
	 */
	private static final int CANVAS_WIDTH_SCALE = 1;
	
	/**
	 * The default width.
	 */
	private static final int CANVAS_WIDTH = 1024 / CANVAS_WIDTH_SCALE;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final long MOUSE_MOVEMENT_TIMEOUT = 100L;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final AtomicBoolean hasUpdatedCursor = new AtomicBoolean();
	private final AtomicBoolean hasUpdatedResolution = new AtomicBoolean(true);
	private final AtomicBoolean isCursorHidden = new AtomicBoolean();
	private final AtomicBoolean isDraggingMouse = new AtomicBoolean();
	private final AtomicBoolean isRecenteringMouse = new AtomicBoolean();
	private final AtomicInteger canvasHeight = new AtomicInteger(CANVAS_HEIGHT);
	private final AtomicInteger canvasHeightScale = new AtomicInteger(CANVAS_HEIGHT_SCALE);
	private final AtomicInteger canvasWidth = new AtomicInteger(CANVAS_WIDTH);
	private final AtomicInteger canvasWidthScale = new AtomicInteger(CANVAS_WIDTH_SCALE);
	private final AtomicInteger mouseDraggedDeltaX = new AtomicInteger();
	private final AtomicInteger mouseDraggedDeltaY = new AtomicInteger();
	private final AtomicInteger mouseMovedDeltaX = new AtomicInteger();
	private final AtomicInteger mouseMovedDeltaY = new AtomicInteger();
	private final AtomicInteger keysPressed = new AtomicInteger();
	private final AtomicInteger mouseDraggedX = new AtomicInteger();
	private final AtomicInteger mouseDraggedY = new AtomicInteger();
	private final AtomicInteger mouseMovedX = new AtomicInteger();
	private final AtomicInteger mouseMovedY = new AtomicInteger();
	private final AtomicLong mouseMovementTime = new AtomicLong();
	private final boolean[] isKeyPressed = new boolean[KeyCode.values().length];
	private final boolean[] isKeyPressedOnce = new boolean[KeyCode.values().length];
	private Canvas canvas;
	private final FPSCounter fPSCounter = new FPSCounter();
	private final Lock lock = new ReentrantLock();
	private final Map<String, Boolean> settings = new HashMap<>();
	private Robot robot;
	private final String title;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code AbstractApplication} with no title.
	 * <p>
	 * Calling this constructor is equivalent to calling {@code AbstractApplication("")}.
	 */
	protected AbstractApplication() {
		this("");
	}
	
	/**
	 * Constructs a new {@code AbstractApplication} with a title of {@code title}.
	 * <p>
	 * If {@code title} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param title the title to use
	 * @throws NullPointerException thrown if, and only if, {@code title} is {@code null}
	 */
	protected AbstractApplication(final String title) {
		this.title = Objects.requireNonNull(title, "title == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Disables the setting with a name of {@code name}.
	 * <p>
	 * Returns the state of the setting, which should be {@code false} at all times.
	 * <p>
	 * Calling this method is equivalent to calling {@code set(name, false)}.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of the setting
	 * @return the state of the setting, which should be {@code false} at all times
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	protected final boolean disableSetting(final String name) {
		return setSetting(name, false);
	}
	
	/**
	 * Enables the setting with a name of {@code name}.
	 * <p>
	 * Returns the state of the setting, which should be {@code true} at all times.
	 * <p>
	 * Calling this method is equivalent to calling {@code set(name, true)}.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of the setting
	 * @return the state of the setting, which should be {@code true} at all times
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	protected final boolean enableSetting(final String name) {
		return setSetting(name, true);
	}
	
//	TODO: Add Javadocs!
	protected final boolean hasEntered() {
		return isCursorHidden() || isRecenteringMouse();
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
	 * Returns {@code true} if, and only if, the mouse is being dragged, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the mouse is being dragged, {@code false} otherwise
	 */
	protected final boolean isDraggingMouse() {
		return this.isDraggingMouse.get();
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
	 * Returns {@code true} if, and only if, the mouse is being moved, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the mouse is being moved, {@code false} otherwise
	 */
	protected final boolean isMovingMouse() {
		return System.currentTimeMillis() - this.mouseMovementTime.get() <= MOUSE_MOVEMENT_TIMEOUT;
	}
	
	/**
	 * Returns {@code true} if, and only if, at least one key is being pressed, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, at least one key is being pressed, {@code false} otherwise
	 */
	protected final boolean isPressingKey() {
		return this.keysPressed.get() > 0;
	}
	
	/**
	 * Returns {@code true} if, and only if, mouse re-centering is being performed, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, mouse re-centering is being performed, {@code false} otherwise
	 */
	protected final boolean isRecenteringMouse() {
		return this.isRecenteringMouse.get();
	}
	
	/**
	 * Returns {@code true} if, and only if, the setting with a name of {@code name} is disabled, {@code false} otherwise.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of the setting
	 * @return {@code true} if, and only if, the setting with a name of {@code name} is disabled, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	protected final boolean isSettingDisabled(final String name) {
		return !isSettingEnabled(name);
	}
	
	/**
	 * Returns {@code true} if, and only if, the setting with a name of {@code name} is enabled, {@code false} otherwise.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of the setting
	 * @return {@code true} if, and only if, the setting with a name of {@code name} is enabled, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	protected final boolean isSettingEnabled(final String name) {
		return this.settings.computeIfAbsent(Objects.requireNonNull(name, "name == null"), key -> Boolean.FALSE).booleanValue();
	}
	
	/**
	 * Sets the setting with a name of {@code name} to the value of {@code isEnabled}.
	 * <p>
	 * Returns the state of the setting, which should be {@code isEnabled} at all times.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of the setting
	 * @param isEnabled {@code true} if, and only if, the setting should be enabled, {@code false} otherwise
	 * @return the state of the setting, which should be {@code isEnabled} at all times
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	protected final boolean setSetting(final String name, final boolean isEnabled) {
		return this.settings.compute(Objects.requireNonNull(name, "name == null"), (key, value) -> Boolean.valueOf(isEnabled)).booleanValue();
	}
	
	/**
	 * Toggles the setting with a name of {@code name}.
	 * <p>
	 * Returns the state of the setting.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of the setting
	 * @return the state of the setting
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	protected final boolean toggleSetting(final String name) {
		return this.settings.compute(Objects.requireNonNull(name, "name == null"), (key, value) -> value == null ? Boolean.TRUE : Boolean.valueOf(!value.booleanValue())).booleanValue();
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
	 * Returns the height.
	 * 
	 * @return the height
	 */
	protected final int getCanvasHeight() {
		return this.canvasHeight.get();
	}
	
	/**
	 * Returns the height scale.
	 * 
	 * @return the height scale
	 */
	protected final int getCanvasHeightScale() {
		return this.canvasHeightScale.get();
	}
	
	/**
	 * Returns the width.
	 * 
	 * @return the width
	 */
	protected final int getCanvasWidth() {
		return this.canvasWidth.get();
	}
	
	/**
	 * Returns the width scale.
	 * 
	 * @return the width scale
	 */
	protected final int getCanvasWidthScale() {
		return this.canvasWidthScale.get();
	}
	
	/**
	 * Returns the {@code Lock} associated with this {@code AbstractApplication}.
	 * 
	 * @return the {@code Lock} associated with this {@code AbstractApplication}
	 */
	protected final Lock getLock() {
		return this.lock;
	}
	
	/**
	 * Called when pixels can be configured at start.
	 * 
	 * @param pixels a {@code byte} array with pixel data
	 */
	protected abstract void doConfigurePixels(final byte[] pixels);
	
	/**
	 * Called when UI-configuration can be performed at start.
	 * 
	 * @param hBox a {@code HBox} to add UI-controls to
	 * @param menuBar a {@code MenuBar} to add UI-controls to
	 * @param vBox a {@code VBox} to add UI-controls to
	 */
	protected abstract void doConfigureUI(final HBox hBox, final MenuBar menuBar, final VBox vBox);
	
//	TODO: Add Javadocs!
	protected final void enter() {
		setCursorHidden(true);
		setRecenteringMouse(true);
	}
	
//	TODO: Add Javadocs!
	protected final void leave() {
		setCursorHidden(false);
		setRecenteringMouse(false);
	}
	
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
	 * Sets a new height.
	 * 
	 * @param canvasHeight a new height
	 */
	protected final void setCanvasHeight(final int canvasHeight) {
		this.canvasHeight.set(canvasHeight);
		this.hasUpdatedResolution.set(true);
	}
	
	/**
	 * Sets a new height scale.
	 * 
	 * @param canvasHeightScale a new height scale
	 */
	protected final void setCanvasHeightScale(final int canvasHeightScale) {
		this.canvasHeightScale.set(canvasHeightScale);
		this.hasUpdatedResolution.set(true);
	}
	
	/**
	 * Sets a new width.
	 * 
	 * @param canvasWidth a new width
	 */
	protected final void setCanvasWidth(final int canvasWidth) {
		this.canvasWidth.set(canvasWidth);
		this.hasUpdatedResolution.set(true);
	}
	
	/**
	 * Sets a new width scale.
	 * 
	 * @param canvasWidthScale a new width scale
	 */
	protected final void setCanvasWidthScale(final int canvasWidthScale) {
		this.canvasWidthScale.set(canvasWidthScale);
		this.hasUpdatedResolution.set(true);
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
	 * Sets the mouse re-centering.
	 * 
	 * @param isRecenteringMouse {@code true} if, and only if, mouse re-centering should be performed, {@code false} otherwise
	 */
	protected final void setRecenteringMouse(final boolean isRecenteringMouse) {
		this.isRecenteringMouse.set(isRecenteringMouse);
	}
	
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
		
		final
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(menuBar);
		borderPane.setCenter(this.canvas);
		borderPane.setBottom(hBox);
		borderPane.setLeft(vBox);
		
		doConfigureUI(hBox, menuBar, vBox);
		
		final Scene scene = new Scene(borderPane);
		
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle(this.title);
		stage.sizeToScene();
		stage.show();
		
		final PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
		
		final AtomicBoolean hasUpdatedCursor = this.hasUpdatedCursor;
		final AtomicBoolean hasUpdatedResolution = this.hasUpdatedResolution;
		
		final PixelWriter[] pixelWriter = new PixelWriter[1];
		
		final ByteBuffer[] byteBuffer = new ByteBuffer[1];
		
		final Canvas canvas = this.canvas;
		
		new AnimationTimer() {
			@Override
			public void handle(final long now) {
				if(hasUpdatedResolution.compareAndSet(true, false)) {
					final WritableImage writableImage = new WritableImage(getCanvasWidth(), getCanvasHeight());
					
					pixelWriter[0] = writableImage.getPixelWriter();
					
					byteBuffer[0] = ByteBuffer.allocate(getCanvasWidth() * getCanvasHeight() * 4);
					
					final byte[] pixels = byteBuffer[0].array();
					
					imageView.setImage(writableImage);
					imageView.setViewport(new Rectangle2D(0.0D, 0.0D, getCanvasWidth(), getCanvasHeight()));
					
					doConfigurePixels(pixels);
				}
				
				if(hasUpdatedCursor.compareAndSet(true, false)) {
					scene.setCursor(isCursorHidden() ? Cursor.NONE : Cursor.DEFAULT);
				}
				
				final
				Lock lock = getLock();
				lock.lock();
				
				try {
					final PixelWriter pixelWriter0 = pixelWriter[0];
					
					final ByteBuffer byteBuffer0 = byteBuffer[0];
					
					if(pixelWriter0 != null && byteBuffer0 != null) {
						pixelWriter0.setPixels(0, 0, getCanvasWidth(), getCanvasHeight(), pixelFormat, byteBuffer0, getCanvasWidth() * 4);
					}
					
					final WritableImage writableImage = imageView.snapshot(null, null);
					
					final
					GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
					graphicsContext.drawImage(writableImage, 0.0D, 0.0D);
				} finally {
					lock.unlock();
				}
			}
		}.start();
		
		new Thread(this).start();
	}
	
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
		
		onMouseMoved(this.mouseMovedX.getAndSet(0), this.mouseMovedY.getAndSet(0));
		
		if(isRecenteringMouse()) {
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
		this.isDraggingMouse.set(true);
		this.mouseDraggedDeltaX.set((int)(e.getScreenX()));
		this.mouseDraggedDeltaY.set((int)(e.getScreenY()));
	}
	
	@SuppressWarnings("unused")
	private void doOnMouseReleased(final MouseEvent e) {
		this.isDraggingMouse.set(false);
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