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
package org.dayflower.pathtracer.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@code Scene} contains various {@link Shape}s and {@link Texture}s.
 * <p>
 * This class is mutable and therefore not suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Scene {
	private final List<Shape> shapes = new ArrayList<>();
	private final String name;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new empty {@code Scene} instance.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the name of this {@code Scene}
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	public Scene(final String name) {
		this.name = Objects.requireNonNull(name, "name == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code List} with all currently added {@link Shape}s.
	 * <p>
	 * Modifying the returned {@code List} will not affect this {@code Scene} instance.
	 * 
	 * @return a {@code List} with all currently added {@code Shape}s
	 */
	public List<Shape> getShapes() {
		return new ArrayList<>(this.shapes);
	}
	
	/**
	 * Returns the name of this {@code Scene} instance.
	 * 
	 * @return the name of this {@code Scene} instance
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Adds {@code shape} to this {@code Scene} instance.
	 * <p>
	 * If {@code shape} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param shape the {@link Shape} to add
	 * @throws NullPointerException thrown if, and only if, {@code shape} is {@code null}
	 */
	public void addShape(final Shape shape) {
		this.shapes.add(Objects.requireNonNull(shape, "shape == null"));
	}
}