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

import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Scene;

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
	private static final int POINT2F_LENGTH = 2;
	private static final int POINT3F_LENGTH = 3;
	private static final int VECTOR3F_LENGTH = 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private float[] point2Fs;
	private float[] point3Fs;
	private float[] vector3Fs;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code DynamicCompiledScene} instance.
	 */
	public DynamicCompiledScene() {
		this.point2Fs = new float[1];
		this.point3Fs = new float[1];
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
//		Retrieve the index for point2F, if it exists:
		final int index = indexOf(point2F);
		
		if(index != -1) {
//			An index for point2F exists, so return it:
			return index;
		}
		
//		Retrieve the old array and create a new array:
		final float[] oldPoint2Fs = this.point2Fs;
		final float[] newPoint2Fs = oldPoint2Fs == null || oldPoint2Fs.length < POINT2F_LENGTH ? new float[POINT2F_LENGTH] : new float[oldPoint2Fs.length + POINT2F_LENGTH];
		
//		Copy the contents in the old array to the new array:
		if(oldPoint2Fs != null && oldPoint2Fs.length >= POINT2F_LENGTH) {
			System.arraycopy(oldPoint2Fs, 0, newPoint2Fs, 0, oldPoint2Fs.length);
		}
		
//		Add point2F to the end of the new array:
		newPoint2Fs[newPoint2Fs.length - POINT2F_LENGTH + 0] = point2F.x;
		newPoint2Fs[newPoint2Fs.length - POINT2F_LENGTH + 1] = point2F.y;
		
//		Update the old array with the new array:
		this.point2Fs = newPoint2Fs;
		
//		Return the index for point2F:
		return newPoint2Fs.length - POINT2F_LENGTH;
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
//		Retrieve the index for point3F, if it exists:
		final int index = indexOf(point3F);
		
		if(index != -1) {
//			An index for point3F exists, so return it:
			return index;
		}
		
//		Retrieve the old array and create a new array:
		final float[] oldPoint3Fs = this.point3Fs;
		final float[] newPoint3Fs = oldPoint3Fs == null || oldPoint3Fs.length < POINT3F_LENGTH ? new float[POINT3F_LENGTH] : new float[oldPoint3Fs.length + POINT3F_LENGTH];
		
//		Copy the contents in the old array to the new array:
		if(oldPoint3Fs != null && oldPoint3Fs.length >= POINT3F_LENGTH) {
			System.arraycopy(oldPoint3Fs, 0, newPoint3Fs, 0, oldPoint3Fs.length);
		}
		
//		Add point3F to the end of the new array:
		newPoint3Fs[newPoint3Fs.length - POINT3F_LENGTH + 0] = point3F.x;
		newPoint3Fs[newPoint3Fs.length - POINT3F_LENGTH + 1] = point3F.y;
		newPoint3Fs[newPoint3Fs.length - POINT3F_LENGTH + 2] = point3F.z;
		
//		Update the old array with the new array:
		this.point3Fs = newPoint3Fs;
		
//		Return the index for point3F:
		return newPoint3Fs.length - POINT3F_LENGTH;
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
//		Retrieve the index for vector3F, if it exists:
		final int index = indexOf(vector3F);
		
		if(index != -1) {
//			An index for vector3F exists, so return it:
			return index;
		}
		
//		Retrieve the old array and create a new array:
		final float[] oldVector3Fs = this.vector3Fs;
		final float[] newVector3Fs = oldVector3Fs == null || oldVector3Fs.length < VECTOR3F_LENGTH ? new float[VECTOR3F_LENGTH] : new float[oldVector3Fs.length + VECTOR3F_LENGTH];
		
//		Copy the contents in the old array to the new array:
		if(oldVector3Fs != null && oldVector3Fs.length >= VECTOR3F_LENGTH) {
			System.arraycopy(oldVector3Fs, 0, newVector3Fs, 0, oldVector3Fs.length);
		}
		
//		Add vector3F to the end of the new array:
		newVector3Fs[newVector3Fs.length - VECTOR3F_LENGTH + 0] = vector3F.x;
		newVector3Fs[newVector3Fs.length - VECTOR3F_LENGTH + 1] = vector3F.y;
		newVector3Fs[newVector3Fs.length - VECTOR3F_LENGTH + 2] = vector3F.z;
		
//		Update the old array with the new array:
		this.vector3Fs = newVector3Fs;
		
//		Return the index for vector3F:
		return newVector3Fs.length - VECTOR3F_LENGTH;
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
//		Retrieve the array:
		final float[] point2Fs = this.point2Fs;
		
		if(point2Fs == null) {
//			The array is null, so return -1:
			return -1;
		}
		
		if(point2Fs.length < POINT2F_LENGTH) {
//			The array has no entries, so return -1:
			return -1;
		}
		
		for(int i = 0; i < point2Fs.length; i += POINT2F_LENGTH) {
//			Retrieve the X- and Y-coordinates of the current entry:
			final float x = point2Fs[i + 0];
			final float y = point2Fs[i + 1];
			
			if(Float.compare(point2F.x, x) == 0 && Float.compare(point2F.y, y) == 0) {
//				The X- and Y-coordinates of the current entry matches point2F, so return its index:
				return i;
			}
		}
		
//		No entry in the array matched point2F, so return -1:
		return -1;
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
//		Retrieve the array:
		final float[] point3Fs = this.point3Fs;
		
		if(point3Fs == null) {
//			The array is null, so return -1:
			return -1;
		}
		
		if(point3Fs.length < POINT3F_LENGTH) {
//			The array has no entries, so return -1:
			return -1;
		}
		
		for(int i = 0; i < point3Fs.length; i += POINT3F_LENGTH) {
//			Retrieve the X-, Y- and Z-coordinates of the current entry:
			final float x = point3Fs[i + 0];
			final float y = point3Fs[i + 1];
			final float z = point3Fs[i + 2];
			
			if(Float.compare(point3F.x, x) == 0 && Float.compare(point3F.y, y) == 0 && Float.compare(point3F.z, z) == 0) {
//				The X-, Y- and Z-coordinates of the current entry matches point3F, so return its index:
				return i;
			}
		}
		
//		No entry in the array matched point3F, so return -1:
		return -1;
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
//		Retrieve the array:
		final float[] vector3Fs = this.vector3Fs;
		
		if(vector3Fs == null) {
//			The array is null, so return -1:
			return -1;
		}
		
		if(vector3Fs.length < VECTOR3F_LENGTH) {
//			The array has no entries, so return -1:
			return -1;
		}
		
		for(int i = 0; i < vector3Fs.length; i += VECTOR3F_LENGTH) {
//			Retrieve the X-, Y- and Z-coordinates of the current entry:
			final float x = vector3Fs[i + 0];
			final float y = vector3Fs[i + 1];
			final float z = vector3Fs[i + 2];
			
			if(Float.compare(vector3F.x, x) == 0 && Float.compare(vector3F.y, y) == 0 && Float.compare(vector3F.z, z) == 0) {
//				The X-, Y- and Z-coordinates of the current entry matches vector3F, so return its index:
				return i;
			}
		}
		
//		No entry in the array matched vector3F, so return -1:
		return -1;
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
//		Retrieve the index for point2F, if it exists:
		int index = indexOf(point2F);
		
		if(index == -1) {
//			No entry for point2F exists, so return -1:
			return -1;
		}
		
//		Retrieve the old array and create a new array:
		final float[] oldPoint2Fs = this.point2Fs;
		final float[] newPoint2Fs = oldPoint2Fs == null || oldPoint2Fs.length - POINT2F_LENGTH < POINT2F_LENGTH ? new float[1] : new float[oldPoint2Fs.length - POINT2F_LENGTH];
		
//		Initialize the index to -1:
		index = -1;
		
		if(oldPoint2Fs != null && oldPoint2Fs.length >= POINT2F_LENGTH) {
			for(int i = 0, j = 0; i < oldPoint2Fs.length; i += POINT2F_LENGTH, j += POINT2F_LENGTH) {
//				Retrieve the X- and Y-coordinates of the current entry:
				final float x = oldPoint2Fs[i + 0];
				final float y = oldPoint2Fs[i + 1];
				
				if(Float.compare(point2F.x, x) == 0 && Float.compare(point2F.y, y) == 0) {
//					Update the index to return to the index of the entry to remove:
					index = j;
					
//					Because the current entry was removed, we need to decrement the index for the new array, in order to continue:
					j -= POINT2F_LENGTH;
				} else {
//					Add the current entry from the old array to the new array:
					newPoint2Fs[j + 0] = x;
					newPoint2Fs[j + 1] = y;
				}
			}
		}
		
//		Update the old array with the new array:
		this.point2Fs = newPoint2Fs;
		
//		Return the index of the entry that was removed:
		return index;
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
//		Retrieve the index for point3F, if it exists:
		int index = indexOf(point3F);
		
		if(index == -1) {
//			No entry for point3F exists, so return -1:
			return -1;
		}
		
//		Retrieve the old array and create a new array:
		final float[] oldPoint3Fs = this.point3Fs;
		final float[] newPoint3Fs = oldPoint3Fs == null || oldPoint3Fs.length - POINT3F_LENGTH < POINT3F_LENGTH ? new float[1] : new float[oldPoint3Fs.length - POINT3F_LENGTH];
		
//		Initialize the index to -1:
		index = -1;
		
		if(oldPoint3Fs != null && oldPoint3Fs.length >= POINT3F_LENGTH) {
			for(int i = 0, j = 0; i < oldPoint3Fs.length; i += POINT3F_LENGTH, j += POINT3F_LENGTH) {
//				Retrieve the X-, Y- and Z-coordinates of the current entry:
				final float x = oldPoint3Fs[i + 0];
				final float y = oldPoint3Fs[i + 1];
				final float z = oldPoint3Fs[i + 2];
				
				if(Float.compare(point3F.x, x) == 0 && Float.compare(point3F.y, y) == 0 && Float.compare(point3F.z, z) == 0) {
//					Update the index to return to the index of the entry to remove:
					index = j;
					
//					Because the current entry was removed, we need to decrement the index for the new array, in order to continue:
					j -= POINT3F_LENGTH;
				} else {
//					Add the current entry from the old array to the new array:
					newPoint3Fs[j + 0] = x;
					newPoint3Fs[j + 1] = y;
					newPoint3Fs[j + 2] = z;
				}
			}
		}
		
//		Update the old array with the new array:
		this.point3Fs = newPoint3Fs;
		
//		Return the index of the entry that was removed:
		return index;
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
//		Retrieve the index for vector3F, if it exists:
		int index = indexOf(vector3F);
		
		if(index == -1) {
//			No entry for vector3F exists, so return -1:
			return -1;
		}
		
//		Retrieve the old array and create a new array:
		final float[] oldVector3Fs = this.vector3Fs;
		final float[] newVector3Fs = oldVector3Fs == null || oldVector3Fs.length - VECTOR3F_LENGTH < VECTOR3F_LENGTH ? new float[1] : new float[oldVector3Fs.length - VECTOR3F_LENGTH];
		
//		Initialize the index to -1:
		index = -1;
		
		if(oldVector3Fs != null && oldVector3Fs.length >= VECTOR3F_LENGTH) {
			for(int i = 0, j = 0; i < oldVector3Fs.length; i += VECTOR3F_LENGTH, j += VECTOR3F_LENGTH) {
//				Retrieve the X-, Y- and Z-coordinates of the current entry:
				final float x = oldVector3Fs[i + 0];
				final float y = oldVector3Fs[i + 1];
				final float z = oldVector3Fs[i + 2];
				
				if(Float.compare(vector3F.x, x) == 0 && Float.compare(vector3F.y, y) == 0 && Float.compare(vector3F.z, z) == 0) {
//					Update the index to return to the index of the entry to remove:
					index = j;
					
//					Because the current entry was removed, we need to decrement the index for the new array, in order to continue:
					j -= VECTOR3F_LENGTH;
				} else {
//					Add the current entry from the old array to the new array:
					newVector3Fs[j + 0] = x;
					newVector3Fs[j + 1] = y;
					newVector3Fs[j + 2] = z;
				}
			}
		}
		
//		Update the old array with the new array:
		this.vector3Fs = newVector3Fs;
		
//		Return the index of the entry that was removed:
		return index;
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
	
	
}