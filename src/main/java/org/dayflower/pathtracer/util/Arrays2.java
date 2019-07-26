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
package org.dayflower.pathtracer.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

//TODO: Add Javadocs.
public final class Arrays2 {
	private Arrays2() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static <T> float[] toFloatArray(final List<T> list, final Function<T, float[]> function) {
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
		
		return floatArray;
	}
	
//	TODO: Add Javadocs.
	public static float[] readFloatArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final float[] array = new float[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readFloat();
		}
		
		return array;
	}
	
//	TODO: Add Javadocs.
	public static float[] readFloatArray(final DataInputStream dataInputStream, final float[] array) throws IOException {
		final int length = dataInputStream.readInt();
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readFloat();
		}
		
		return array;
	}
	
//	TODO: Add Javadocs.
	public static int[] readIntArray(final DataInputStream dataInputStream) throws IOException {
		final int length = dataInputStream.readInt();
		
		final int[] array = new int[length];
		
		for(int i = 0; i < length; i++) {
			array[i] = dataInputStream.readInt();
		}
		
		return array;
	}
	
//	TODO: Add Javadocs.
	public static void writeFloatArray(final DataOutputStream dataOutputStream, final float[] array) throws IOException {
		dataOutputStream.writeInt(array.length);
		
		for(final float value : array) {
			dataOutputStream.writeFloat(value);
		}
	}
	
//	TODO: Add Javadocs.
	public static void writeIntArray(final DataOutputStream dataOutputStream, final int[] array) throws IOException {
		dataOutputStream.writeInt(array.length);
		
		for(final int value : array) {
			dataOutputStream.writeInt(value);
		}
	}
}