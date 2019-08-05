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
package org.dayflower.pathtracer.scene.compiler;

import java.util.function.Consumer;

import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;

/**
 * A {@code DynamicCompiledScene} represents a dynamic and compiled version of a {@link Scene} instance.
 * <p>
 * The difference between a {@code DynamicCompiledScene} and a {@link CompiledScene}, is that the structures of the former are dynamic, whereas the structures of the latter are static.
 * <p>
 * A {@code DynamicCompiledScene} allows you to add new and update or remove existing structures. A structure may only be removed when no other structure is referencing it. When a new structure has been added, or an existing updated, pruning can be
 * performed to automatically remove structures that are not referenced. To perform pruning, call {@link #prune()}.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class DynamicCompiledScene {
	private float[] point2Fs;
	private float[] point3Fs;
	private float[] surfaces;
	private float[] textures;
	private float[] vector3Fs;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code DynamicCompiledScene} instance.
	 */
	public DynamicCompiledScene() {
		this.point2Fs = new float[1];
		this.point3Fs = new float[1];
		this.surfaces = new float[1];
		this.textures = new float[1];
		this.vector3Fs = new float[1];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds {@code point2F} to this {@code DynamicCompiledScene} instance.
	 * <p>
	 * Returns the index of {@code point2F}.
	 * <p>
	 * If {@code point2F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point2F the {@link Point2F} to add
	 * @return the index of {@code point2F}
	 * @throws NullPointerException thrown if, and only if, {@code point2F} is {@code null}
	 */
	public synchronized int add(final Point2F point2F) {
		return doAdd(new float[] {point2F.x, point2F.y}, this.point2Fs, point2Fs -> this.point2Fs = point2Fs);
	}
	
	/**
	 * Adds {@code point3F} to this {@code DynamicCompiledScene} instance.
	 * <p>
	 * Returns the index of {@code point3F}.
	 * <p>
	 * If {@code point3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point3F the {@link Point3F} to add
	 * @return the index of {@code point3F}
	 * @throws NullPointerException thrown if, and only if, {@code point3F} is {@code null}
	 */
	public synchronized int add(final Point3F point3F) {
		return doAdd(new float[] {point3F.x, point3F.y, point3F.z}, this.point3Fs, point3Fs -> this.point3Fs = point3Fs);
	}
	
	/**
	 * Adds {@code surface} to this {@code DynamicCompiledScene} instance.
	 * <p>
	 * Returns the index of {@code surface}.
	 * <p>
	 * If {@code surface} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface the {@link Surface} to add
	 * @return the index of {@code surface}
	 * @throws NullPointerException thrown if, and only if, {@code surface} is {@code null}
	 */
	public synchronized int add(final Surface surface) {
		add(surface.getTextureAlbedo());
		add(surface.getTextureEmission());
		add(surface.getTextureNormal());
		
		return doAdd(doToArray(surface), this.surfaces, surfaces -> this.surfaces = surfaces);
	}
	
	/**
	 * Adds {@code texture} to this {@code DynamicCompiledScene} instance.
	 * <p>
	 * Returns the index of {@code texture}.
	 * <p>
	 * If {@code texture} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param texture the {@link Texture} to add
	 * @return the index of {@code texture}
	 * @throws NullPointerException thrown if, and only if, {@code texture} is {@code null}
	 */
	public synchronized int add(final Texture texture) {
		return doAdd(texture.toArray(), this.textures, textures -> this.textures = textures, Texture.RELATIVE_OFFSET_SIZE);
	}
	
	/**
	 * Adds {@code vector3F} to this {@code DynamicCompiledScene} instance.
	 * <p>
	 * Returns the index of {@code vector3F}.
	 * <p>
	 * If {@code vector3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param vector3F the {@link Vector3F} to add
	 * @return the index of {@code vector3F}
	 * @throws NullPointerException thrown if, and only if, {@code vector3F} is {@code null}
	 */
	public synchronized int add(final Vector3F vector3F) {
		return doAdd(new float[] {vector3F.x, vector3F.y, vector3F.z}, this.vector3Fs, vector3Fs -> this.vector3Fs = vector3Fs);
	}
	
	/**
	 * Returns the index of {@code point2F}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code point2F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point2F the {@link Point2F} to check
	 * @return the index of {@code point2F}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code point2F} is {@code null}
	 */
	public synchronized int indexOf(final Point2F point2F) {
		return doIndexOf(new float[] {point2F.x, point2F.y}, this.point2Fs);
	}
	
	/**
	 * Returns the index of {@code point3F}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code point3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point3F the {@link Point3F} to check
	 * @return the index of {@code point3F}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code point3F} is {@code null}
	 */
	public synchronized int indexOf(final Point3F point3F) {
		return doIndexOf(new float[] {point3F.x, point3F.y, point3F.z}, this.point3Fs);
	}
	
	/**
	 * Returns the index of {@code surface}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code surface} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface the {@link Surface} to check
	 * @return the index of {@code surface}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code surface} is {@code null}
	 */
	public synchronized int indexOf(final Surface surface) {
		return doIndexOf(doToArray(surface), this.surfaces);
	}
	
	/**
	 * Returns the index of {@code texture}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code texture} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param texture the {@link Texture} to check
	 * @return the index of {@code texture}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code texture} is {@code null}
	 */
	public synchronized int indexOf(final Texture texture) {
		return doIndexOf(texture.toArray(), this.textures, Texture.RELATIVE_OFFSET_SIZE);
	}
	
	/**
	 * Returns the index of {@code vector3F}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code vector3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param vector3F the {@link Vector3F} to check
	 * @return the index of {@code vector3F}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code vector3F} is {@code null}
	 */
	public synchronized int indexOf(final Vector3F vector3F) {
		return doIndexOf(new float[] {vector3F.x, vector3F.y, vector3F.z}, this.vector3Fs);
	}
	
	/**
	 * Returns the number of references to {@code point2F}.
	 * <p>
	 * If {@code point2F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point2F a {@link Point2F}
	 * @return the number of references to {@code point2F}
	 * @throws NullPointerException thrown if, and only if, {@code point2F} is {@code null}
	 */
	@SuppressWarnings("static-method")
	public synchronized int referencesTo(final Point2F point2F) {
		return 0;//TODO: Implement!
	}
	
	/**
	 * Returns the number of references to {@code point3F}.
	 * <p>
	 * If {@code point3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point3F a {@link Point3F}
	 * @return the number of references to {@code point3F}
	 * @throws NullPointerException thrown if, and only if, {@code point3F} is {@code null}
	 */
	@SuppressWarnings("static-method")
	public synchronized int referencesTo(final Point3F point3F) {
		return 0;//TODO: Implement!
	}
	
	/**
	 * Returns the number of references to {@code surface}.
	 * <p>
	 * If {@code surface} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface a {@link Surface}
	 * @return the number of references to {@code surface}
	 * @throws NullPointerException thrown if, and only if, {@code surface} is {@code null}
	 */
	@SuppressWarnings("static-method")
	public synchronized int referencesTo(final Surface surface) {
		return 0;//TODO: Implement!
	}
	
	/**
	 * Returns the number of references to {@code texture}.
	 * <p>
	 * If {@code texture} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param texture a {@link Texture}
	 * @return the number of references to {@code texture}
	 * @throws NullPointerException thrown if, and only if, {@code texture} is {@code null}
	 */
	@SuppressWarnings("static-method")
	public synchronized int referencesTo(final Texture texture) {
		return 0;//TODO: Implement!
	}
	
	/**
	 * Returns the number of references to {@code vector3F}.
	 * <p>
	 * If {@code vector3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param vector3F a {@link Vector3F}
	 * @return the number of references to {@code vector3F}
	 * @throws NullPointerException thrown if, and only if, {@code vector3F} is {@code null}
	 */
	@SuppressWarnings("static-method")
	public synchronized int referencesTo(final Vector3F vector3F) {
		return 0;//TODO: Implement!
	}
	
	/**
	 * Removes {@code point2F} from this {@code DynamicCompiledScene} instance, if it exists.
	 * <p>
	 * Returns the index of {@code point2F}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code point2F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point2F the {@link Point3F} to remove
	 * @return the index of {@code point2F}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code point2F} is {@code null}
	 */
	public synchronized int remove(final Point2F point2F) {
		return doRemove(new float[] {point2F.x, point2F.y}, this.point2Fs, point2Fs -> this.point2Fs = point2Fs);
	}
	
	/**
	 * Removes {@code point3F} from this {@code DynamicCompiledScene} instance, if it exists.
	 * <p>
	 * Returns the index of {@code point3F}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code point3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param point3F the {@link Point3F} to remove
	 * @return the index of {@code point3F}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code point3F} is {@code null}
	 */
	public synchronized int remove(final Point3F point3F) {
		return doRemove(new float[] {point3F.x, point3F.y, point3F.z}, this.point3Fs, point3Fs -> this.point3Fs = point3Fs);
	}
	
	/**
	 * Removes {@code surface} from this {@code DynamicCompiledScene} instance, if it exists.
	 * <p>
	 * Returns the index of {@code surface}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code surface} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param surface the {@link Surface} to remove
	 * @return the index of {@code surface}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code surface} is {@code null}
	 */
	public synchronized int remove(final Surface surface) {
		return doRemove(doToArray(surface), this.surfaces, surfaces -> this.surfaces = surfaces);
	}
	
	/**
	 * Removes {@code texture} from this {@code DynamicCompiledScene} instance, if it exists.
	 * <p>
	 * Returns the index of {@code texture}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code texture} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param texture the {@link Texture} to remove
	 * @return the index of {@code texture}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code texture} is {@code null}
	 */
	public synchronized int remove(final Texture texture) {
		return doRemove(texture.toArray(), this.textures, textures -> this.textures = textures, Texture.RELATIVE_OFFSET_SIZE);
	}
	
	/**
	 * Removes {@code vector3F} from this {@code DynamicCompiledScene} instance, if it exists.
	 * <p>
	 * Returns the index of {@code vector3F}, or {@code -1} if it does not exist.
	 * <p>
	 * If {@code vector3F} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param vector3F the {@link Vector3F} to remove
	 * @return the index of {@code vector3F}, or {@code -1} if it does not exist
	 * @throws NullPointerException thrown if, and only if, {@code vector3F} is {@code null}
	 */
	public synchronized int remove(final Vector3F vector3F) {
		return doRemove(new float[] {vector3F.x, vector3F.y, vector3F.z}, this.vector3Fs, vector3Fs -> this.vector3Fs = vector3Fs);
	}
	
	/**
	 * Prunes the structures of this {@code DynamicCompiledScene} instance.
	 * <p>
	 * The pruning process consists of removing structures that are currently not referenced from other structures.
	 * <p>
	 * Some structures cannot be referenced from other structures, so these will not be pruned.
	 * <p>
	 * In some cases, a flag has been associated with a given structure, indicating that the structure should be exempt from pruning. These structures are usually materials or textures that can be switched between.
	 */
	public synchronized void prune() {
//		TODO: Implement!
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private synchronized float[] doToArray(final Surface surface) {
		return new float[] {
			surface.getMaterial().getType(),
			indexOf(surface.getTextureAlbedo()),
			indexOf(surface.getTextureEmission()),
			indexOf(surface.getTextureNormal()),
			surface.getNoiseAmount(),
			surface.getNoiseScale()
		};
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static synchronized int doAdd(final float[] structure, final float[] structures, final Consumer<float[]> consumer) {
		return doAdd(structure, structures, consumer, -1);
	}
	
	private static synchronized int doAdd(final float[] structure, final float[] structures, final Consumer<float[]> consumer, final int relativeOffsetSize) {
		/*
		 * structure:			The structure to find in or add to 'structures'.
		 * structures:			The set of structures in which a search for 'structure' is performed.
		 * consumer:			A 'Consumer' that accepts the new array of structures.
		 * relativeOffsetSize:	A negative value (-1 or less) is used for static structures. They all have the same size. It can be determined from 'structure.length'.
		 * 						A positive value (0 or more) is used for dynamic structures. They all have varying sizes. In this case 'relativeOffsetSize' is used as a relative offset in each structure of the 'structures' array.
		 */
		
		if(structure == null) {
//			The supplied 'structure' array is 'null', so return '-1':
			return -1;
		}
		
		if(structure.length == 0) {
//			The length of the supplied 'structure' array is '0', so return '-1':
			return -1;
		}
		
//		Retrieve the index for 'structure', if it exists:
		final int index = doIndexOf(structure, structures, relativeOffsetSize);
		
		if(index != -1) {
//			A match for 'structure' was found, so return its index:
			return index;
		}
		
		if(structures == null || structures.length <= 1) {
//			Let 'consumer' accept a clone of 'structure' as the new array of structures:
			consumer.accept(structure.clone());
			
			return 0;
		}
		
		if(relativeOffsetSize < 0 && structures.length % structure.length != 0) {
			throw new IllegalArgumentException(String.format("structures.length %% structure.length != 0: structure.length=%s, structures.length=%s", Integer.toString(structure.length), Integer.toString(structures.length)));
		}
		
//		Retrieve the size of 'structure':
		final int size = relativeOffsetSize < 0 ? structure.length : (int)(structure[relativeOffsetSize]);
		
//		Create 'structuresUpdated' to hold all existing structures and 'structure':
		final float[] structuresUpdated = new float[structures.length + size];
		
//		Copy all existing structures from 'structures' to 'structuresUpdated':
		System.arraycopy(structures, 0, structuresUpdated, 0, structures.length);
		
		for(int i = 0; i < size; i++) {
//			Add the data in 'structure' to 'structuresUpdated':
			structuresUpdated[structures.length + i] = structure[i];
		}
		
//		Let 'consumer' accept 'structuresUpdated' as the new array of structures:
		consumer.accept(structuresUpdated);
		
//		Return the index of 'structure' in 'structuresUpdated':
		return structures.length;
	}
	
	private static synchronized int doIndexOf(final float[] structure, final float[] structures) {
		return doIndexOf(structure, structures, -1);
	}
	
	private static synchronized int doIndexOf(final float[] structure, final float[] structures, final int relativeOffsetSize) {
		/*
		 * structure:			The structure to find in the array 'structures'.
		 * structures:			The set of structures in which a search for 'structure' is performed.
		 * relativeOffsetSize:	A negative value (-1 or less) is used for static structures. They all have the same size. It can be determined from 'structure.length'.
		 * 						A positive value (0 or more) is used for dynamic structures. They all have varying sizes. In this case 'relativeOffsetSize' is used as a relative offset in each structure of the 'structures' array.
		 */
		
		if(structure == null) {
//			The supplied 'structure' array is 'null', so return '-1':
			return -1;
		}
		
		if(structure.length == 0) {
//			The length of the supplied 'structure' array is '0', so return '-1':
			return -1;
		}
		
		if(structures == null) {
//			The supplied 'structures' array is 'null', so return '-1':
			return -1;
		}
		
		if(structures.length <= 1) {
//			The length of the supplied 'structures' array is less than or equal to '1', which means it is empty, so return '-1':
			return -1;
		}
		
		if(relativeOffsetSize < 0 && structures.length % structure.length != 0) {
			throw new IllegalArgumentException(String.format("structures.length %% structure.length != 0: structure.length=%s, structures.length=%s", Integer.toString(structure.length), Integer.toString(structures.length)));
		}
		
		for(int i = 0; i < structures.length;) {
//			Initialize 'hasFoundMatch' to 'true':
			boolean hasFoundMatch = true;
			
			for(int j = 0; j < structure.length; j++) {
				if(Float.compare(structure[j], structures[i + j]) != 0) {
//					A difference between the 'structure' array and the current structure in the 'structures' array has been found, so update 'hasFoundMatch' to 'false':
					hasFoundMatch = false;
					
					break;
				}
			}
			
			if(hasFoundMatch) {
//				The 'structure' array supplied and the current structure in the 'structures' array match, so return its index:
				return i;
			}
			
//			Update the index to the next structure in the 'structures' array:
			i += relativeOffsetSize < 0 ? structure.length : structures[i + relativeOffsetSize];
		}
		
//		No match has been found in the 'structures' array, so return '-1':
		return -1;
	}
	
	private static synchronized int doRemove(final float[] structure, final float[] structures, final Consumer<float[]> consumer) {
		return doRemove(structure, structures, consumer, -1);
	}
	
	private static synchronized int doRemove(final float[] structure, final float[] structures, final Consumer<float[]> consumer, final int relativeOffsetSize) {
		/*
		 * structure:			The structure to find in and remove from the array 'structures'.
		 * structures:			The set of structures in which a search for 'structure' is performed.
		 * consumer:			A 'Consumer' that accepts the new array of structures.
		 * relativeOffsetSize:	A negative value (-1 or less) is used for static structures. They all have the same size. It can be determined from 'structure.length'.
		 * 						A positive value (0 or more) is used for dynamic structures. They all have varying sizes. In this case 'relativeOffsetSize' is used as a relative offset in each structure of the 'structures' array.
		 */
		
		if(structure == null) {
//			The supplied 'structure' array is 'null', so return '-1':
			return -1;
		}
		
		if(structure.length == 0) {
//			The length of the supplied 'structure' array is '0', so return '-1':
			return -1;
		}
		
		if(structures == null) {
//			The supplied 'structures' array is 'null', so return '-1':
			return -1;
		}
		
		if(structures.length <= 1) {
//			The length of the supplied 'structures' array is less than or equal to '1', which means it is empty, so return '-1':
			return -1;
		}
		
		if(relativeOffsetSize < 0 && structures.length % structure.length != 0) {
			throw new IllegalArgumentException(String.format("structures.length %% structure.length != 0: structure.length=%s, structures.length=%s", Integer.toString(structure.length), Integer.toString(structures.length)));
		}
		
//		Retrieve the index for 'structure', if it exists:
		final int index = doIndexOf(structure, structures, relativeOffsetSize);
		
		if(index == -1) {
//			No match for 'structure' was found, so return '-1':
			return -1;
		}
		
//		Retrieve the size of 'structure':
		final int size = relativeOffsetSize < 0 ? structure.length : (int)(structure[relativeOffsetSize]);
		
		if(structures.length == size) {
//			Let 'consumer' accept an "empty" array (an array with a length of '1') as the new array of structures:
			consumer.accept(new float[1]);
			
//			Return the index of 'structure' that was removed (which should be '0' in this case):
			return index;
		}
		
//		Create 'structuresUpdated' to hold all existing structures except for 'structure':
		final float[] structuresUpdated = new float[structures.length - size];
		
		for(int i = 0, j = 0; i < structures.length; i++) {
			if(i < index || i >= index + size) {
				structuresUpdated[j++] = structures[i];
			}
		}
		
//		Let 'consumer' accept 'structuresUpdated' as the new array of structures:
		consumer.accept(structuresUpdated);
		
//		Return the index of 'structure' that was removed:
		return index;
	}
}