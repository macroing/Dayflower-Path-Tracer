/**
 * Copyright 2015 - 2021 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A class that consists exclusively of static methods that operates on or returns arrays of various types.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Arrays2 {
	private Arrays2() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reads {@code float} values from {@code dataInputStream} and adds them to a {@code float[]}.
	 * <p>
	 * Returns the {@code float[]}.
	 * <p>
	 * If {@code dataInputStream} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code IOException} will be thrown.
	 * 
	 * @param dataInputStream the {@code DataInputStream} to read from
	 * @return the {@code float[]}
	 * @throws IOException thrown if, and only if, an I/O error occurs
	 * @throws NullPointerException thrown if, and only if, {@code dataInputStream} is {@code null}
	 */
	public static float[] readFloatArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final float[] array = new float[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readFloat();
		}
		
		return array;
	}
	
	/**
	 * Reads {@code float} values from {@code dataInputStream} and adds them to {@code array}.
	 * <p>
	 * Returns {@code array}.
	 * <p>
	 * If {@code dataInputStream} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code IOException} will be thrown.
	 * <p>
	 * If {@code array.length} is less than the length read from {@code dataInputStream}, an {@code ArrayIndexOutOfBoundsException} will be thrown.
	 * 
	 * @param dataInputStream the {@code DataInputStream} to read from
	 * @param array the {@code float} array to write to and return
	 * @return {@code array}
	 * @throws ArrayIndexOutOfBoundsException thrown if, and only if, {@code array.length} is less than the length read from {@code dataInputStream}
	 * @throws IOException thrown if, and only if, an I/O error occurs
	 * @throws NullPointerException thrown if, and only if, {@code dataInputStream} is {@code null}
	 */
	public static float[] readFloatArray(final DataInputStream dataInputStream, final float[] array) throws IOException {
		final int length = dataInputStream.readInt();
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readFloat();
		}
		
		return array;
	}
	
	/**
	 * Returns a larger {@code float[]} by merging smaller {@code float[]} together.
	 * <p>
	 * Calling this method is equivalent to {@code toFloatArray(list, function, 0)}.
	 * <p>
	 * If either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an element in {@code list} is {@code null}, a {@code NullPointerException} might be thrown. But no guarantees can be made. This is up to {@code function}.
	 * 
	 * @param <T> the generic type to use
	 * @param list a {@code List}
	 * @param function a {@code Function} mapping {@code List}s elements to {@code float[]}
	 * @return a larger {@code float[]} by merging smaller {@code float[]} together
	 * @throws NullPointerException thrown if, and only if, either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}
	 */
	public static <T> float[] toFloatArray(final List<T> list, final Function<T, float[]> function) {
		return toFloatArray(list, function, 0);
	}
	
	/**
	 * Returns a larger {@code float[]} by merging smaller {@code float[]} together.
	 * <p>
	 * If either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an element in {@code list} is {@code null}, a {@code NullPointerException} might be thrown. But no guarantees can be made. This is up to {@code function}.
	 * <p>
	 * If the length of the larger {@code float[]} is less than {@code minimumLength}, a new empty {@code float[]} with {@code minimumLength} as length will be returned.
	 * 
	 * @param <T> the generic type to use
	 * @param list a {@code List}
	 * @param function a {@code Function} mapping {@code List}s elements to {@code float[]}
	 * @param minimumLength the minimum length of the {@code float[]} to return
	 * @return a larger {@code float[]} by merging smaller {@code float[]} together
	 * @throws NullPointerException thrown if, and only if, either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}
	 */
	public static <T> float[] toFloatArray(final List<T> list, final Function<T, float[]> function, final int minimumLength) {
		Objects.requireNonNull(list, "list == null");
		Objects.requireNonNull(function, "function == null");
		
		final List<Float> floatList = new ArrayList<>();
		
		for(final T listElement : list) {
			final float[] floatArray = function.apply(listElement);
			
			for(final float floatValue : floatArray) {
				floatList.add(Float.valueOf(floatValue));
			}
		}
		
		final float[] floatArray = new float[floatList.size()];
		
		for(int i = 0; i < floatList.size(); i++) {
			floatArray[i] = floatList.get(i).floatValue();
		}
		
		return floatArray.length >= minimumLength ? floatArray : new float[minimumLength];
	}
	
	/**
	 * Reads {@code int} values from {@code dataInputStream} and adds them to an {@code int[]}.
	 * <p>
	 * Returns the {@code int[]}.
	 * <p>
	 * If {@code dataInputStream} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code IOException} will be thrown.
	 * 
	 * @param dataInputStream the {@code DataInputStream} to read from
	 * @return the {@code int[]}
	 * @throws IOException thrown if, and only if, an I/O error occurs
	 * @throws NullPointerException thrown if, and only if, {@code dataInputStream} is {@code null}
	 */
	public static int[] readIntArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final int[] array = new int[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readInt();
		}
		
		return array;
	}
	
	/**
	 * Returns a larger {@code int[]} by merging smaller {@code int[]} together.
	 * <p>
	 * Calling this method is equivalent to {@code toIntArray(list, function, 0)}.
	 * <p>
	 * If either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an element in {@code list} is {@code null}, a {@code NullPointerException} might be thrown. But no guarantees can be made. This is up to {@code function}.
	 * 
	 * @param <T> the generic type to use
	 * @param list a {@code List}
	 * @param function a {@code Function} mapping {@code List}s elements to {@code int[]}
	 * @return a larger {@code int[]} by merging smaller {@code int[]} together
	 * @throws NullPointerException thrown if, and only if, either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}
	 */
	public static <T> int[] toIntArray(final List<T> list, final Function<T, int[]> function) {
		return toIntArray(list, function, 0);
	}
	
	/**
	 * Returns a larger {@code int[]} by merging smaller {@code int[]} together.
	 * <p>
	 * If either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an element in {@code list} is {@code null}, a {@code NullPointerException} might be thrown. But no guarantees can be made. This is up to {@code function}.
	 * <p>
	 * If the length of the larger {@code int[]} is less than {@code minimumLength}, a new empty {@code int[]} with {@code minimumLength} as length will be returned.
	 * 
	 * @param <T> the generic type to use
	 * @param list a {@code List}
	 * @param function a {@code Function} mapping {@code List}s elements to {@code int[]}
	 * @param minimumLength the minimum length of the {@code int[]} to return
	 * @return a larger {@code int[]} by merging smaller {@code int[]} together
	 * @throws NullPointerException thrown if, and only if, either {@code list} or {@code function} are {@code null}, or {@code function} returns {@code null}
	 */
	public static <T> int[] toIntArray(final List<T> list, final Function<T, int[]> function, final int minimumLength) {
		final List<Integer> integerList = new ArrayList<>();
		
		for(final T listElement : list) {
			final int[] intArray = function.apply(listElement);
			
			for(final int intValue : intArray) {
				integerList.add(Integer.valueOf(intValue));
			}
		}
		
		final int[] intArray = new int[integerList.size()];
		
		for(int i = 0; i < integerList.size(); i++) {
			intArray[i] = integerList.get(i).intValue();
		}
		
		return intArray.length >= minimumLength ? intArray : new int[minimumLength];
	}
	
	/**
	 * Writes the {@code float} values of {@code array} into {@code dataOutputStream}.
	 * <p>
	 * If either {@code dataOutputStream} or {@code array} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code IOException} will be thrown.
	 * 
	 * @param dataOutputStream the {@code DataOutputStream} to write to
	 * @param array the {@code float[]} with the {@code float} values to write to {@code dataOutputStream}
	 * @throws IOException thrown if, and only if, an I/O error occurs
	 * @throws NullPointerException thrown if, and only if, either {@code dataOutputStream} or {@code array} are {@code null}
	 */
	public static void writeFloatArray(final DataOutputStream dataOutputStream, final float[] array) throws IOException {
		dataOutputStream.writeInt(array.length);
		
		for(final float value : array) {
			dataOutputStream.writeFloat(value);
		}
	}
	
	/**
	 * Writes the {@code int} values of {@code array} into {@code dataOutputStream}.
	 * <p>
	 * If either {@code dataOutputStream} or {@code array} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code IOException} will be thrown.
	 * 
	 * @param dataOutputStream the {@code DataOutputStream} to write to
	 * @param array the {@code int[]} with the {@code int} values to write to {@code dataOutputStream}
	 * @throws IOException thrown if, and only if, an I/O error occurs
	 * @throws NullPointerException thrown if, and only if, either {@code dataOutputStream} or {@code array} are {@code null}
	 */
	public static void writeIntArray(final DataOutputStream dataOutputStream, final int[] array) throws IOException {
		dataOutputStream.writeInt(array.length);
		
		for(final int value : array) {
			dataOutputStream.writeInt(value);
		}
	}
}