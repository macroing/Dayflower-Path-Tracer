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

//TODO: Add Javadocs.
public final class IndexedModel {
	private final List<Integer> indices = new ArrayList<>();
	private final List<Vector4> normals = new ArrayList<>();
	private final List<Vector4> positions = new ArrayList<>();
	private final List<Vector4> tangents = new ArrayList<>();
	private final List<Vector4> textureCoordinates = new ArrayList<>();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public IndexedModel() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public List<Integer> getIndices() {
		return this.indices;
	}
	
//	TODO: Add Javadocs.
	public List<Vector4> getNormals() {
		return this.normals;
	}
	
//	TODO: Add Javadocs.
	public List<Vector4> getPositions() {
		return this.positions;
	}
	
//	TODO: Add Javadocs.
	public List<Vector4> getTangents() {
		return this.tangents;
	}
	
//	TODO: Add Javadocs.
	public List<Vector4> getTextureCoordinates() {
		return this.textureCoordinates;
	}
	
//	TODO: Add Javadocs.
	public void calculateNormals() {
		for(int i = 0; i < this.indices.size(); i += 3) {
			final int index0 = this.indices.get(i + 0).intValue();
			final int index1 = this.indices.get(i + 1).intValue();
			final int index2 = this.indices.get(i + 2).intValue();
			
			final Vector4 edge0 = this.positions.get(index1).subtract(this.positions.get(index0));
			final Vector4 edge1 = this.positions.get(index2).subtract(this.positions.get(index0));
			final Vector4 normal = edge0.crossProduct(edge1).normalize();
			
			this.normals.set(index0, this.normals.get(index0).add(normal));
			this.normals.set(index1, this.normals.get(index1).add(normal));
			this.normals.set(index2, this.normals.get(index2).add(normal));
		}
		
		for(int i = 0; i < this.normals.size(); i++) {
			this.normals.set(i, this.normals.get(i).normalize());
		}
	}
	
//	TODO: Add Javadocs.
	public void calculateTangents() {
		for(int i = 0; i < this.indices.size(); i += 3) {
			final int index0 = this.indices.get(i + 0).intValue();
			final int index1 = this.indices.get(i + 1).intValue();
			final int index2 = this.indices.get(i + 2).intValue();
			
			final Vector4 edge0 = this.positions.get(index1).subtract(this.positions.get(index0));
			final Vector4 edge1 = this.positions.get(index2).subtract(this.positions.get(index0));
			
			final float deltaU0 = this.textureCoordinates.get(index1).x - this.textureCoordinates.get(index0).x;
			final float deltaV0 = this.textureCoordinates.get(index1).y - this.textureCoordinates.get(index0).y;
			final float deltaU1 = this.textureCoordinates.get(index2).x - this.textureCoordinates.get(index0).x;
			final float deltaV1 = this.textureCoordinates.get(index2).y - this.textureCoordinates.get(index0).y;
			
			final float dividend = (deltaU0 * deltaV1 - deltaU1 * deltaV0);
			final float fraction = dividend == 0.0F ? 0.0F : 1.0F / dividend;
			
			final float x = fraction * (deltaV1 * edge0.x - deltaV0 * edge1.x);
			final float y = fraction * (deltaV1 * edge0.y - deltaV0 * edge1.y);
			final float z = fraction * (deltaV1 * edge0.y - deltaV0 * edge1.y);
			final float w = 0.0F;
			
			final Vector4 tangent = new Vector4(x, y, z, w);
			
			this.tangents.set(index0, this.tangents.get(index0).add(tangent));
			this.tangents.set(index1, this.tangents.get(index1).add(tangent));
			this.tangents.set(index2, this.tangents.get(index2).add(tangent));
		}
		
		for(int i = 0; i < this.tangents.size(); i++) {
			this.tangents.set(i, this.tangents.get(i).normalize());
		}
	}
}