/**
 * Copyright 2015 - 2020 J&#246;rgen Lundgren
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

import java.util.Objects;

import org.macroing.math4j.Matrix44F;
import org.macroing.math4j.Point3F;
import org.macroing.math4j.QuaternionF;
import org.macroing.math4j.Vector3F;

//TODO: Add Javadocs!
public final class Transform {
	private Matrix44F objectToWorld;
	private Matrix44F worldToObject;
	private Point3F position;
	private QuaternionF rotation;
	private Vector3F scale;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Transform() {
		this(new Point3F());
	}
	
//	TODO: Add Javadocs!
	public Transform(final Point3F position) {
		this(position, new QuaternionF());
	}
	
//	TODO: Add Javadocs!
	public Transform(final Point3F position, final QuaternionF rotation) {
		this(position, rotation, new Vector3F(1.0F, 1.0F, 1.0F));
	}
	
//	TODO: Add Javadocs!
	public Transform(final Point3F position, final QuaternionF rotation, final Vector3F scale) {
		this.position = Objects.requireNonNull(position, "position == null");
		this.rotation = Objects.requireNonNull(rotation, "rotation == null");
		this.scale = Objects.requireNonNull(scale, "scale == null");
		this.objectToWorld = null;
		this.worldToObject = null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Matrix44F getObjectToWorld() {
		if(this.objectToWorld == null) {
			doCalculateObjectToWorldAndWorldToObject();
		}
		
		return this.objectToWorld;
	}
	
//	TODO: Add Javadocs!
	public Matrix44F getWorldToObject() {
		if(this.worldToObject == null) {
			doCalculateObjectToWorldAndWorldToObject();
		}
		
		return this.worldToObject;
	}
	
//	TODO: Add Javadocs!
	public Point3F getPosition() {
		return this.position;
	}
	
//	TODO: Add Javadocs!
	public QuaternionF getRotation() {
		return this.rotation;
	}
	
//	TODO: Add Javadocs!
	public Transform copy() {
		return new Transform(this.position, this.rotation, this.scale);
	}
	
//	TODO: Add Javadocs!
	public Vector3F getScale() {
		return this.scale;
	}
	
//	TODO: Add Javadocs!
	public boolean isUpdateRequired() {
		return this.objectToWorld == null || this.worldToObject == null;
	}
	
//	TODO: Add Javadocs!
	public void lookAt(final Point3F lookAt, final Vector3F up) {
		rotate(QuaternionF.fromMatrix(Matrix44F.rotation(up, Vector3F.direction(this.position, lookAt).normalize())));
	}
	
//	TODO: Add Javadocs!
	public void moveX(final float x) {
		final float oldX = this.position.x;
		final float oldY = this.position.y;
		final float oldZ = this.position.z;
		
		final float newX = oldX + x;
		final float newY = oldY;
		final float newZ = oldZ;
		
		setPosition(new Point3F(newX, newY, newZ));
	}
	
//	TODO: Add Javadocs!
	public void moveY(final float y) {
		final float oldX = this.position.x;
		final float oldY = this.position.y;
		final float oldZ = this.position.z;
		
		final float newX = oldX;
		final float newY = oldY + y;
		final float newZ = oldZ;
		
		setPosition(new Point3F(newX, newY, newZ));
	}
	
//	TODO: Add Javadocs!
	public void moveZ(final float z) {
		final float oldX = this.position.x;
		final float oldY = this.position.y;
		final float oldZ = this.position.z;
		
		final float newX = oldX;
		final float newY = oldY;
		final float newZ = oldZ + z;
		
		setPosition(new Point3F(newX, newY, newZ));
	}
	
//	TODO: Add Javadocs!
	public void rotate(final QuaternionF q) {
		setRotation(q.multiply(this.rotation).normalize());
	}
	
//	TODO: Add Javadocs!
	public void setPosition(final Point3F position) {
		this.position = Objects.requireNonNull(position, "position == null");
		this.objectToWorld = null;
		this.worldToObject = null;
	}
	
//	TODO: Add Javadocs!
	public void setRotation(final QuaternionF rotation) {
		this.rotation = Objects.requireNonNull(rotation, "rotation == null");
		this.objectToWorld = null;
		this.worldToObject = null;
	}
	
//	TODO: Add Javadocs!
	public void setScale(final Vector3F scale) {
		this.scale = Objects.requireNonNull(scale, "scale == null");
		this.objectToWorld = null;
		this.worldToObject = null;
	}
	
//	TODO: Add Javadocs!
	public void update() {
		doCalculateObjectToWorldAndWorldToObject();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doCalculateObjectToWorldAndWorldToObject() {
		final Matrix44F translation = Matrix44F.translation(this.position);
		final Matrix44F rotation = Matrix44F.rotation(this.rotation);
		final Matrix44F scale = Matrix44F.scaling(this.scale.x, this.scale.y, this.scale.z);
		final Matrix44F objectToWorld = translation.multiply(rotation).multiply(scale);
		final Matrix44F worldToObject = objectToWorld.inverse();
		
		this.objectToWorld = objectToWorld;
		this.worldToObject = worldToObject;
	}
}