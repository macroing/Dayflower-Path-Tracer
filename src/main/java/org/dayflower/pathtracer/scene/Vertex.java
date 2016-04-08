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

//TODO: Add Javadocs.
public final class Vertex {
	private final Vector4 normal;
	private final Vector4 position;
	private final Vector4 textureCoordinates;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Vertex(final Vector4 normal, final Vector4 position, final Vector4 textureCoordinates) {
		this.normal = normal;
		this.position = position;
		this.textureCoordinates = textureCoordinates;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public boolean isInsideViewFrustum() {
		final boolean isInsideViewFrustumX = Math.abs(this.position.x) <= Math.abs(this.position.w);
		final boolean isInsideViewFrustumY = Math.abs(this.position.y) <= Math.abs(this.position.w);
		final boolean isInsideViewFrustumZ = Math.abs(this.position.z) <= Math.abs(this.position.w);
		
		return isInsideViewFrustumX && isInsideViewFrustumY && isInsideViewFrustumZ;
	}
	
//	TODO: Add Javadocs.
	public float get(final int index) {
		switch(index) {
			case 0:
				return this.position.x;
			case 1:
				return this.position.y;
			case 2:
				return this.position.z;
			case 3:
				return this.position.w;
			default:
				throw new IndexOutOfBoundsException();
		}
	}
	
//	TODO: Add Javadocs.
	public float getX() {
		return this.position.x;
	}
	
//	TODO: Add Javadocs.
	public float getY() {
		return this.position.y;
	}
	
//	TODO: Add Javadocs.
	public float calculateTriangleAreaTimesTwo(final Vertex b, final Vertex c) {
		final float x1 = b.getX() - this.position.x;
		final float y1 = b.getY() - this.position.y;
		
		final float x2 = c.getX() - this.position.x;
		final float y2 = c.getY() - this.position.y;
		
		return x1 * y2 - x2 * y1;
	}
	
//	TODO: Add Javadocs.
	public Vector4 getNormal() {
		return this.normal;
	}
	
//	TODO: Add Javadocs.
	public Vector4 getPosition() {
		return this.position;
	}
	
//	TODO: Add Javadocs.
	public Vector4 getTextureCoordinates() {
		return this.textureCoordinates;
	}
	
//	TODO: Add Javadocs.
	public Vertex linearInterpolation(final Vertex vertex, final float fraction) {
		return new Vertex(this.normal.linearInterpolation(vertex.getNormal(), fraction), this.position.linearInterpolation(vertex.getPosition(), fraction), this.textureCoordinates.linearInterpolation(vertex.getTextureCoordinates(), fraction));
	}
	
//	TODO: Add Javadocs.
	public Vertex perspectiveDivide() {
		return new Vertex(this.normal, new Vector4(this.position.x / this.position.w, this.position.y / this.position.w, this.position.z / this.position.w, this.position.w), this.textureCoordinates);
	}
}