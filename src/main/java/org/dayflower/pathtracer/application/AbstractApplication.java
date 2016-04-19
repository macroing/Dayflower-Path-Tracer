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
import java.lang.reflect.Field;//TODO: Add Javadocs.
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

//TODO: Add Javadocs.
public abstract class AbstractApplication extends Application implements Runnable {
//	TODO: Add Javadocs.
	public static final int CANVAS_HEIGHT_SCALE = 2;
	
//	TODO: Add Javadocs.
	public static final int CANVAS_WIDTH_SCALE = 2;
	
//	TODO: Add Javadocs.
	public static final int CANVAS_HEIGHT = 768 / CANVAS_HEIGHT_SCALE;
	
//	TODO: Add Javadocs.
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
	private final CopyOnWriteArrayList<Consumer<String>> printConsumers = new CopyOnWriteArrayList<>();
	private final FPSCounter fPSCounter = new FPSCounter();
	private final Lock lock = new ReentrantLock();
	private final String title;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	protected AbstractApplication() {
		this("");
	}
	
//	TODO: Add Javadocs.
	protected AbstractApplication(final String title) {
		this.title = Objects.requireNonNull(title, "title == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	protected final boolean addPrintConsumer(final Consumer<String> printConsumer) {
		return this.printConsumers.addIfAbsent(Objects.requireNonNull(printConsumer, "printConsumer == null"));
	}
	
//	TODO: Add Javadocs.
	protected final boolean isCursorHidden() {
		return this.isCursorHidden.get();
	}
	
//	TODO: Add Javadocs.
	protected final boolean isDraggingMouse() {
		return this.isDraggingMouse.get();
	}
	
//	TODO: Add Javadocs.
	protected final boolean isKeyPressed(final KeyCode keyCode) {
		return this.isKeyPressed[keyCode.ordinal()];
	}
	
//	TODO: Add Javadocs.
	protected final boolean isMovingMouse() {
		return System.currentTimeMillis() - this.mouseMovementTime.get() <= MOUSE_MOVEMENT_TIMEOUT;
	}
	
//	TODO: Add Javadocs.
	protected final boolean isPressingKey() {
		return this.keysPressed.get() > 0;
	}
	
//	TODO: Add Javadocs.
	protected final boolean isRecenteringMouse() {
		return this.isRecenteringMouse.get();
	}
	
//	TODO: Add Javadocs.
	protected final boolean removePrintConsumer(final Consumer<String> printConsumer) {
		return this.printConsumers.remove(Objects.requireNonNull(printConsumer, "printConsumer == null"));
	}
	
//	TODO: Add Javadocs.
	protected final FPSCounter getFPSCounter() {
		return this.fPSCounter;
	}
	
//	TODO: Add Javadocs.
	protected final int getCanvasHeight() {
		return this.canvasHeight.get();
	}
	
//	TODO: Add Javadocs.
	protected final int getCanvasHeightScale() {
		return this.canvasHeightScale.get();
	}
	
//	TODO: Add Javadocs.
	protected final int getCanvasWidth() {
		return this.canvasWidth.get();
	}
	
//	TODO: Add Javadocs.
	protected final int getCanvasWidthScale() {
		return this.canvasWidthScale.get();
	}
	
//	TODO: Add Javadocs.
	protected final Lock getLock() {
		return this.lock;
	}
	
//	TODO: Add Javadocs.
	protected abstract void doConfigurePixels(final byte[] pixels);
	
//	TODO: Add Javadocs.
	protected abstract void doConfigureUI(final HBox hBox);
	
//	TODO: Add Javadocs.
	protected abstract void onMouseDragged(final float x, final float y);
	
//	TODO: Add Javadocs.
	protected abstract void onMouseMoved(final float x, final float y);
	
//	TODO: Add Javadocs.
	protected final void print(final String string) {
		this.printConsumers.forEach(printConsumer -> printConsumer.accept(Objects.requireNonNull(string, "string == null")));
	}
	
//	TODO: Add Javadocs.
	protected final void printf(final String format, final Object... objects) {
		print(String.format(Objects.requireNonNull(format, "format == null"), Objects.requireNonNull(objects, "objects == null")));
	}
	
//	TODO: Add Javadocs.
	protected final void setCanvasHeight(final int canvasHeight) {
		this.canvasHeight.set(canvasHeight);
		this.hasUpdatedResolution.set(true);
	}
	
//	TODO: Add Javadocs.
	protected final void setCanvasHeightScale(final int canvasHeightScale) {
		this.canvasHeightScale.set(canvasHeightScale);
		this.hasUpdatedResolution.set(true);
	}
	
//	TODO: Add Javadocs.
	protected final void setCanvasWidth(final int canvasWidth) {
		this.canvasWidth.set(canvasWidth);
		this.hasUpdatedResolution.set(true);
	}
	
//	TODO: Add Javadocs.
	protected final void setCanvasWidthScale(final int canvasWidthScale) {
		this.canvasWidthScale.set(canvasWidthScale);
		this.hasUpdatedResolution.set(true);
	}
	
//	TODO: Add Javadocs.
	protected final void setCursorHidden(final boolean isCursorHidden) {
		if(this.isCursorHidden.compareAndSet(!isCursorHidden, isCursorHidden)) {
			this.hasUpdatedCursor.set(true);
		}
	}
	
//	TODO: Add Javadocs.
	protected final void setRecenteringMouse(final boolean isRecenteringMouse) {
		this.isRecenteringMouse.set(isRecenteringMouse);
	}
	
//	TODO: Add Javadocs.
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