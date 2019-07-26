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
package org.dayflower.pathtracer.scene;

import java.util.ArrayList;
import java.util.Collection;
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
	private final Camera camera;
	private final List<Primitive> primitives;
	private final Sky sky;
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
		this.camera = new Camera();
		this.primitives = new ArrayList<>();
		this.sky = new Sky();
		this.name = Objects.requireNonNull(name, "name == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the {@link Camera} instance used by this {@code Scene} instance.
	 * 
	 * @return the {@code Camera} instance used by this {@code Scene} instance
	 */
	public Camera getCamera() {
		return this.camera;
	}
	
	/**
	 * Returns a {@code List} with all currently added {@link Primitive}s.
	 * <p>
	 * Modifying the returned {@code List} will not affect this {@code Scene} instance.
	 * 
	 * @return a {@code List} with all currently added {@code Primitive}s
	 */
	public List<Primitive> getPrimitives() {
		return new ArrayList<>(this.primitives);
	}
	
	/**
	 * Returns the {@link Sky} instance used by this {@code Scene} instance.
	 * 
	 * @return the {@code Sky} instance used by this {@code Scene} instance
	 */
	public Sky getSky() {
		return this.sky;
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
	 * Adds {@code primitive} to this {@code Scene} instance.
	 * <p>
	 * If {@code primitive} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitive the {@link Primitive} to add
	 * @throws NullPointerException thrown if, and only if, {@code primitive} is {@code null}
	 */
	public void addPrimitive(final Primitive primitive) {
		this.primitives.add(Objects.requireNonNull(primitive, "primitive == null"));
	}
	
	/**
	 * Adds all {@link Primitive}s in {@code primitives} to this {@code Scene} instance.
	 * <p>
	 * If either {@code primitives} or a {@code Primitive} in {@code primitives} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitives the {@code Primitive}s to add
	 * @throws NullPointerException thrown if, and only if, either {@code primitives} or a {@code Primitive} in {@code primitives} are {@code null}
	 */
	public void addPrimitives(final Collection<Primitive> primitives) {
		for(final Primitive primitive : primitives) {
			addPrimitive(primitive);
		}
	}
	
	/**
	 * Removes {@code primitive} from this {@code Scene} instance.
	 * <p>
	 * If {@code primitive} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitive the {@link Primitive} to remove
	 * @throws NullPointerException thrown if, and only if, {@code primitive} is {@code null}
	 */
	public void removePrimitive(final Primitive primitive) {
		this.primitives.remove(Objects.requireNonNull(primitive, "primitive == null"));
	}
	
	/**
	 * Removes all {@link Primitive}s in {@code primitives} from this {@code Scene} instance.
	 * <p>
	 * If either {@code primitives} or a {@code Primitive} in {@code primitives} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param primitives the {@code Primitive}s to remove
	 * @throws NullPointerException thrown if, and only if, either {@code primitives} or a {@code Primitive} in {@code primitives} are {@code null}
	 */
	public void removePrimitives(final Collection<Primitive> primitives) {
		for(final Primitive primitive : primitives) {
			removePrimitive(primitive);
		}
	}
}