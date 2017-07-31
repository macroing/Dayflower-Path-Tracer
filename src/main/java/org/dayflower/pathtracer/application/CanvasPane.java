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

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

final class CanvasPane extends Pane {
	private final Canvas canvas;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public CanvasPane(final Canvas canvas) {
		this.canvas = canvas;
		
		getChildren().add(this.canvas);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	protected void layoutChildren() {
		final int top = (int)(snappedTopInset());
		final int right = (int)(snappedRightInset());
		final int bottom = (int)(snappedBottomInset());
		final int left = (int)(snappedLeftInset());
		final int width = (int)(getWidth()) - left - right;
		final int height = (int)(getHeight()) - top - bottom;
		
		this.canvas.setLayoutX(left);
		this.canvas.setLayoutY(top);
		
		if(width != this.canvas.getWidth() || height != this.canvas.getHeight()) {
			this.canvas.setWidth(width);
			this.canvas.setHeight(height);
		}
	}
}