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
package org.dayflower.pathtracer.application;

import java.awt.AWTException;
import java.awt.Robot;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
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
public abstract class AbstractApplication extends Application implements Runnable {
	/**
	 * The default height scale.
	 */
	public static final int CANVAS_HEIGHT_SCALE = 2;
	
	/**
	 * The default height.
	 */
	public static final int CANVAS_HEIGHT = 768 / CANVAS_HEIGHT_SCALE;
	
	/**
	 * The default width scale.
	 */
	public static final int CANVAS_WIDTH_SCALE = 2;
	
	/**
	 * The default width.
	 */
	public static final int CANVAS_WIDTH = 1024 / CANVAS_WIDTH_SCALE;
	
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
	private final CopyOnWriteArrayList<Consumer<String>> printConsumers = new CopyOnWriteArrayList<>();
	private final FPSCounter fPSCounter = new FPSCounter();
	private final Lock lock = new ReentrantLock();
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
	 * Adds a print {@code Consumer} to this {@code AbstractApplication}, if absent.
	 * <p>
	 * Returns {@code true} if, and only if, {@code printConsumer} was added, {@code false} otherwise.
	 * <p>
	 * If {@code printConsumer} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param printConsumer the print {@code Consumer} to add
	 * @return {@code true} if, and only if, {@code printConsumer} was added, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code printConsumer} is {@code null}
	 */
	protected final boolean addPrintConsumer(final Consumer<String> printConsumer) {
		return this.printConsumers.addIfAbsent(Objects.requireNonNull(printConsumer, "printConsumer == null"));
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
	 * Removes a print {@code Consumer} from this {@code AbstractApplication}, if present.
	 * <p>
	 * Returns {@code true} if, and only if, {@code printConsumer} was removed, {@code false} otherwise.
	 * <p>
	 * If {@code printConsumer} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param printConsumer the print {@code Consumer} to remove
	 * @return {@code true} if, and only if, {@code printConsumer} was removed, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code printConsumer} is {@code null}
	 */
	protected final boolean removePrintConsumer(final Consumer<String> printConsumer) {
		return this.printConsumers.remove(Objects.requireNonNull(printConsumer, "printConsumer == null"));
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
	 */
	protected abstract void doConfigureUI(final HBox hBox);
	
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
	 * This method "prints" {@code string} as a message to all currently added print {@code Consumer}s.
	 * <p>
	 * If {@code message} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param string the message to print
	 * @throws NullPointerException thrown if, and only if, {@code string} is {@code null}
	 */
	protected final void print(final String string) {
		this.printConsumers.forEach(printConsumer -> printConsumer.accept(Objects.requireNonNull(string, "string == null")));
	}
	
	/**
	 * This method "prints" a message constructed via {@code String.format(format, objects)} to all currently added print {@code Consumer}s.
	 * <p>
	 * Calling this method is practically equivalent to {@code print(String.format(format, objects))}, assuming neither {@code format} nor {@code objects} are {@code null}.
	 * <p>
	 * If either {@code format} or {@code objects} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param format a format {@code String}
	 * @param objects an array of {@code Object}s to format
	 * @throws NullPointerException thrown if, and only if, either {@code format} or {@code objects} are {@code null}
	 */
	protected final void printf(final String format, final Object... objects) {
		print(String.format(Objects.requireNonNull(format, "format == null"), Objects.requireNonNull(objects, "objects == null")));
	}
	
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
		final Robot robot = doCreateRobot();
		
		final TextArea textArea = new TextArea();
		
		addPrintConsumer(string -> Platform.runLater(() -> textArea.appendText(string + "\n")));
		
		final
		ImageView imageView = new ImageView();
		imageView.setSmooth(true);
		
		final Canvas canvas = new Canvas(getCanvasWidth(), getCanvasHeight());
		
		final ScrollPane scrollPane = new ScrollPane(canvas);
		
		canvas.widthProperty().bind(scrollPane.widthProperty());
		canvas.heightProperty().bind(scrollPane.heightProperty());
		canvas.addEventFilter(MouseEvent.ANY, e -> canvas.requestFocus());
		canvas.widthProperty().addListener(observable -> doUpdateTransform(canvas, imageView));
		canvas.heightProperty().addListener(observable -> doUpdateTransform(canvas, imageView));
		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> {
			if(!this.isKeyPressed[e.getCode().ordinal()]) {
				this.keysPressed.incrementAndGet();
			}
			
			this.isKeyPressed[e.getCode().ordinal()] = true;
		});
		canvas.setOnKeyReleased(e -> {
			if(this.isKeyPressed[e.getCode().ordinal()]) {
				this.keysPressed.decrementAndGet();
			}
			
			this.isKeyPressed[e.getCode().ordinal()] = false;
			this.isKeyPressedOnce[e.getCode().ordinal()] = false;
		});
		canvas.setOnMouseMoved(e -> {
			final int mouseMovedDeltaX = this.mouseMovedDeltaX.get();
			final int mouseMovedDeltaY = this.mouseMovedDeltaY.get();
			
			this.mouseMovementTime.set(System.currentTimeMillis());
			
			if(mouseMovedDeltaX != 0 || mouseMovedDeltaY != 0) {
				this.mouseMovedX.addAndGet(this.mouseMovedDeltaX.get() - (int)(e.getScreenX()));
				this.mouseMovedY.addAndGet(this.mouseMovedDeltaY.get() - (int)(e.getScreenY()));
			}
			
			onMouseMoved(this.mouseMovedX.getAndSet(0), this.mouseMovedY.getAndSet(0));
			
			if(isRecenteringMouse()) {
				final Bounds bounds = canvas.localToScreen(canvas.getBoundsInLocal());
				
				final int minX = (int)(bounds.getMinX());
				final int minY = (int)(bounds.getMinY());
				final int width = (int)(bounds.getWidth());
				final int height = (int)(bounds.getHeight());
				final int x = minX + width / 2;
				final int y = minY + height / 2;
				
				robot.mouseMove(x, y);
				
				this.mouseMovedDeltaX.set(x);
				this.mouseMovedDeltaY.set(y);
			} else {
				this.mouseMovedDeltaX.set((int)(e.getScreenX()));
				this.mouseMovedDeltaY.set((int)(e.getScreenY()));
			}
		});
		canvas.setOnMousePressed(e -> {
			this.isDraggingMouse.set(true);
			this.mouseDraggedDeltaX.set((int)(e.getScreenX()));
			this.mouseDraggedDeltaY.set((int)(e.getScreenY()));
		});
		canvas.setOnMouseReleased(e -> this.isDraggingMouse.set(false));
		canvas.setOnMouseDragged(e -> {
			this.mouseDraggedX.addAndGet(this.mouseDraggedDeltaX.get() - (int)(e.getScreenX()));
			this.mouseDraggedY.addAndGet(this.mouseDraggedDeltaY.get() - (int)(e.getScreenY()));
			this.mouseDraggedDeltaX.set((int)(e.getScreenX()));
			this.mouseDraggedDeltaY.set((int)(e.getScreenY()));
			this.mouseMovedDeltaX.set(0);
			this.mouseMovedDeltaY.set(0);
			this.mouseMovedX.set(0);
			this.mouseMovedY.set(0);
			
			onMouseDragged(this.mouseDraggedX.getAndSet(0), this.mouseDraggedY.getAndSet(0));
		});
		
		final
		TabPane tabPane = new TabPane();
		tabPane.getTabs().add(doCreateTab(textArea, "Console"));
		
		final TreeItem<String> treeItem = new TreeItem<>("Scene");
		
		final
		TreeView<String> treeView = new TreeView<>(treeItem);
		treeView.setShowRoot(false);
		
		final
		SplitPane splitPane0 = new SplitPane();
		splitPane0.getItems().addAll(scrollPane, tabPane);
		splitPane0.setOrientation(Orientation.VERTICAL);
		splitPane0.setDividerPositions(0.7D);
		
		final
		SplitPane splitPane1 = new SplitPane();
		splitPane1.getItems().addAll(treeView, splitPane0);
		splitPane1.setDividerPositions(0.3D);
		splitPane1.setOrientation(Orientation.HORIZONTAL);
		
		final
		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.setOnAction(e -> Platform.exit());
		
		final
		Menu menuFile = new Menu("File");
		menuFile.getItems().addAll(menuItemExit);
		
		final
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuFile);
		
		final
		HBox hBox = new HBox();
		hBox.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		
		final
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(menuBar);
		borderPane.setCenter(splitPane1);
		borderPane.setBottom(hBox);
		
		doConfigureUI(hBox);
		
		final Scene scene = new Scene(borderPane, 1024.0D, 768.0D);
		
		stage.setScene(scene);
		stage.setTitle(this.title);
		stage.show();
		
		final PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteBgraPreInstance();
		
		final AtomicBoolean hasUpdatedCursor = this.hasUpdatedCursor;
		final AtomicBoolean hasUpdatedResolution = this.hasUpdatedResolution;
		
		final PixelWriter[] pixelWriter = new PixelWriter[1];
		
		final ByteBuffer[] byteBuffer = new ByteBuffer[1];
		
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
				} finally {
					lock.unlock();
				}
				
				final WritableImage writableImage = imageView.snapshot(null, null);
				
				final
				GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
				graphicsContext.drawImage(writableImage, 0.0D, 0.0D);
			}
		}.start();
		
		new Thread(this).start();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Robot doCreateRobot() {
		try {
			return new Robot();
		} catch(final AWTException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	
	private static Tab doCreateTab(final Node node, final String text) {
		final
		Tab tab = new Tab();
		tab.setClosable(false);
		tab.setContent(node);
		tab.setText(text);
		
		return tab;
	}
	
	private static void doUpdateTransform(final Canvas canvas, final ImageView imageView) {
		imageView.setFitWidth(canvas.getWidth());
		imageView.setFitHeight(canvas.getHeight());
	}
}