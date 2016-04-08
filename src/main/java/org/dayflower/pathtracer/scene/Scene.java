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
package org.dayflower.pathtracer.scene;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//TODO: Add Javadocs.
public final class Scene {
	private final List<Shape> shapes = new ArrayList<>();
	private final List<Texture> textures = new ArrayList<>();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Scene() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public List<Shape> getShapes() {
		return this.shapes;
	}
	
//	TODO: Add Javadocs.
	public List<Texture> getTextures() {
		return this.textures;
	}
	
//	TODO: Add Javadocs.
	public void addShape(final Shape shape) {
		this.shapes.add(Objects.requireNonNull(shape, "shape == null"));
		
		for(int i = 0, j = 0; i < this.shapes.size() - 1; i++) {
			j += this.shapes.get(i).size();
			
			shape.setOffset(j);
		}
	}
	
//	TODO: Add Javadocs.
	public void addTexture(final Texture texture) {
		this.textures.add(Objects.requireNonNull(texture, "texture == null"));
		
		for(int i = 0, j = 0; i < this.textures.size() - 1; i++) {
			j += this.textures.get(i).size();
			
			texture.setOffset(j);
		}
	}
}