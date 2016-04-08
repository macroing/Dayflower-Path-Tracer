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
package org.dayflower.pathtracer.camera;

import static org.dayflower.pathtracer.math.Math2.PI_DIVIDED_BY_TWO;
import static org.dayflower.pathtracer.math.Math2.PI_MULTIPLIED_BY_TWO;
import static org.dayflower.pathtracer.math.Math2.atan;
import static org.dayflower.pathtracer.math.Math2.cos;
import static org.dayflower.pathtracer.math.Math2.max;
import static org.dayflower.pathtracer.math.Math2.min;
import static org.dayflower.pathtracer.math.Math2.sin;
import static org.dayflower.pathtracer.math.Math2.sqrt;
import static org.dayflower.pathtracer.math.Math2.tan;
import static org.dayflower.pathtracer.math.Math2.toDegrees;
import static org.dayflower.pathtracer.math.Math2.toRadians;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.Objects;

//TODO: Add Javadocs.
public final class Camera {
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_APERTURE_RADIUS = 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_EYE = 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW = 4;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE = 6;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W = 7;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_RESOLUTION = 10;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_UP = 12;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 1 + 3 + 2 + 1 + 3 + 2 + 3;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean isWalkLockEnabled;
	private CameraPredicate cameraPredicate;
	private float centerX;
	private float centerY;
	private float centerZ;
	private float pitch;
	private float radius;
	private float viewDirectionX;
	private float viewDirectionY;
	private float viewDirectionZ;
	private float walkDirectionX;
	private float walkDirectionY;
	private float walkDirectionZ;
	private float yaw;
	private final float[] array = new float[SIZE];
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Camera() {
		this((oldX, oldY, oldZ, newX, newY, newZ) -> new boolean[] {true, true, true});
	}
	
//	TODO: Add Javadocs.
	public Camera(final CameraPredicate cameraPredicate) {
		setApertureRadius(0.4F);
		setCameraPredicate(cameraPredicate);
		setCenter(55.0F, 42.0F, 155.6F);
		setFieldOfViewX(40.0F);
		setFocalDistance(30.0F);
		setPitch(0.0F);
		setRadius(4.0F);
		setResolution(800.0F, 800.0F);
		setWalkLockEnabled(true);
		setYaw(0.0F);
		
		update();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public boolean isWalkLockEnabled() {
		return this.isWalkLockEnabled;
	}
	
//	TODO: Add Javadocs.
	public CameraPredicate getCameraPredicate() {
		return this.cameraPredicate;
	}
	
//	TODO: Add Javadocs.
	public float getApertureRadius() {
		return this.array[ABSOLUTE_OFFSET_OF_APERTURE_RADIUS];
	}
	
//	TODO: Add Javadocs.
	public float getCenterX() {
		return this.centerX;
	}
	
//	TODO: Add Javadocs.
	public float getCenterY() {
		return this.centerY;
	}
	
//	TODO: Add Javadocs.
	public float getCenterZ() {
		return this.centerZ;
	}
	
//	TODO: Add Javadocs.
	public float getEyeX() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE + 0];
	}
	
//	TODO: Add Javadocs.
	public float getEyeY() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE + 1];
	}
	
//	TODO: Add Javadocs.
	public float getEyeZ() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE + 2];
	}
	
//	TODO: Add Javadocs.
	public float getFieldOfViewX() {
		return this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 0];
	}
	
//	TODO: Add Javadocs.
	public float getFieldOfViewY() {
		return this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 1];
	}
	
//	TODO: Add Javadocs.
	public float getFocalDistance() {
		return this.array[ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisWX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 0];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisWY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 1];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisWZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 2];
	}
	
//	TODO: Add Javadocs.
	public float getPitch() {
		return this.pitch;
	}
	
//	TODO: Add Javadocs.
	public float getRadius() {
		return this.radius;
	}
	
//	TODO: Add Javadocs.
	public float getResolutionX() {
		return this.array[ABSOLUTE_OFFSET_OF_RESOLUTION + 0];
	}
	
//	TODO: Add Javadocs.
	public float getResolutionY() {
		return this.array[ABSOLUTE_OFFSET_OF_RESOLUTION + 1];
	}
	
//	TODO: Add Javadocs.
	public float getUpX() {
		return this.array[ABSOLUTE_OFFSET_OF_UP + 0];
	}
	
//	TODO: Add Javadocs.
	public float getUpY() {
		return this.array[ABSOLUTE_OFFSET_OF_UP + 1];
	}
	
//	TODO: Add Javadocs.
	public float getUpZ() {
		return this.array[ABSOLUTE_OFFSET_OF_UP + 2];
	}
	
//	TODO: Add Javadocs.
	public float getViewDirectionX() {
		return this.viewDirectionX;
	}
	
//	TODO: Add Javadocs.
	public float getViewDirectionY() {
		return this.viewDirectionY;
	}
	
//	TODO: Add Javadocs.
	public float getViewDirectionZ() {
		return this.viewDirectionZ;
	}
	
//	TODO: Add Javadocs.
	public float getWalkDirectionX() {
		return this.walkDirectionX;
	}
	
//	TODO: Add Javadocs.
	public float getWalkDirectionY() {
		return this.walkDirectionY;
	}
	
//	TODO: Add Javadocs.
	public float getWalkDirectionZ() {
		return this.walkDirectionZ;
	}
	
//	TODO: Add Javadocs.
	public float getYaw() {
		return this.yaw;
	}
	
//	TODO: Add Javadocs.
	public float[] getArray() {
		return this.array;
	}
	
//	TODO: Add Javadocs.
	public void changeAltitude(final float y) {
		setCenter(getCenterX(), getCenterY() + y, getCenterZ());
	}
	
//	TODO: Add Javadocs.
	public void changeApertureDiameter(final float multiplier) {
		setApertureRadius(getApertureRadius() + ((getApertureRadius() + 0.01F) * multiplier));
	}
	
//	TODO: Add Javadocs.
	public void changeFieldOfViewX(final float fieldOfViewX) {
		setFieldOfViewX(getFieldOfViewX() + fieldOfViewX);
	}
	
//	TODO: Add Javadocs.
	public void changeFocalDistance(final float focalDistance) {
		setFocalDistance(getFocalDistance() + focalDistance);
	}
	
//	TODO: Add Javadocs.
	public void changePitch(final float pitch) {
		setPitch(getPitch() + pitch);
	}
	
//	TODO: Add Javadocs.
	public void changeRadius(final float multiplier) {
		setRadius(getRadius() * multiplier);
	}
	
//	TODO: Add Javadocs.
	public void changeYaw(final float yaw) {
		setYaw(getYaw() + yaw);
	}
	
//	TODO: Add Javadocs.
	public void forward(final float distance) {
		if(isWalkLockEnabled()) {
			setCenter(getCenterX() + getWalkDirectionX() * distance, getCenterY() + getWalkDirectionY() * distance, getCenterZ() + getWalkDirectionZ() * distance);
		} else {
			setCenter(getCenterX() + getViewDirectionX() * distance, getCenterY() + getViewDirectionY() * distance, getCenterZ() + getViewDirectionZ() * distance);
		}
	}
	
//	TODO: Add Javadocs.
	public void rotateRight(final float distance) {
		final float yaw = getYaw() + distance;
		final float pitch = getPitch();
		final float x = sin(yaw) * cos(pitch);
		final float y = sin(pitch);
		final float z = cos(yaw) * cos(pitch);
		
		setViewDirection(-x, -y, -z);
		setWalkDirection(-x, getWalkDirectionY(), -z);
	}
	
//	TODO: Add Javadocs.
	public void setApertureRadius(final float apertureRadius) {
		this.array[ABSOLUTE_OFFSET_OF_APERTURE_RADIUS] = max(min(apertureRadius, 25.0F), 0.0F);
	}
	
//	TODO: Add Javadocs.
	public void setCameraPredicate(final CameraPredicate cameraPredicate) {
		this.cameraPredicate = Objects.requireNonNull(cameraPredicate, "cameraPredicate == null");
	}
	
//	TODO: Add Javadocs.
	public void setCenter(final float centerX, final float centerY, final float centerZ) {
		final boolean[] test = this.cameraPredicate.test(this.centerX, this.centerY, this.centerZ, centerX, centerY, centerZ);
		
		if(test[0]) {
			this.centerX = centerX;
		}
		
		if(test[1]) {
			this.centerY = centerY;
		}
		
		if(test[2]) {
			this.centerZ = centerZ;
		}
	}
	
//	TODO: Add Javadocs.
	public void setEye(final float eyeX, final float eyeY, final float eyeZ) {
		this.array[ABSOLUTE_OFFSET_OF_EYE + 0] = eyeX;
		this.array[ABSOLUTE_OFFSET_OF_EYE + 1] = eyeY;
		this.array[ABSOLUTE_OFFSET_OF_EYE + 2] = eyeZ;
	}
	
//	TODO: Add Javadocs.
	public void setFieldOfViewX(final float fieldOfViewX) {
		this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 0] = fieldOfViewX;
		this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 1] = toDegrees(atan(tan(toRadians(fieldOfViewX) * 0.5F) * (getResolutionX() / getResolutionY())) * 2.0F);
	}
	
//	TODO: Add Javadocs.
	public void setFocalDistance(final float focalDistance) {
		this.array[ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE] = max(min(focalDistance, 100.0F), 0.2F);
	}
	
//	TODO: Add Javadocs.
	public void setOrthoNormalBasisW(final float orthoNormalBasisWX, final float orthoNormalBasisWY, final float orthoNormalBasisWZ) {
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 0] = orthoNormalBasisWX;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 1] = orthoNormalBasisWY;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 2] = orthoNormalBasisWZ;
	}
	
//	TODO: Add Javadocs.
	public void setPitch(final float pitch) {
		this.pitch = max(min(pitch, PI_DIVIDED_BY_TWO + 0.05F), -PI_DIVIDED_BY_TWO + 0.05F);
	}
	
//	TODO: Add Javadocs.
	public void setRadius(final float radius) {
		this.radius = max(min(radius, 100.0F), 0.2F);
	}
	
//	TODO: Add Javadocs.
	public void setResolution(final float resolutionX, final float resolutionY) {
		this.array[ABSOLUTE_OFFSET_OF_RESOLUTION + 0] = resolutionX;
		this.array[ABSOLUTE_OFFSET_OF_RESOLUTION + 1] = resolutionY;
		
		setFieldOfViewX(getFieldOfViewX());
	}
	
//	TODO: Add Javadocs.
	public void setUp(final float upX, final float upY, final float upZ) {
		this.array[ABSOLUTE_OFFSET_OF_UP + 0] = upX;
		this.array[ABSOLUTE_OFFSET_OF_UP + 1] = upY;
		this.array[ABSOLUTE_OFFSET_OF_UP + 2] = upZ;
	}
	
//	TODO: Add Javadocs.
	public void setViewDirection(final float viewDirectionX, final float viewDirectionY, final float viewDirectionZ) {
		this.viewDirectionX = viewDirectionX;
		this.viewDirectionY = viewDirectionY;
		this.viewDirectionZ = viewDirectionZ;
	}
	
//	TODO: Add Javadocs.
	public void setWalkDirection(final float walkDirectionX, final float walkDirectionY, final float walkDirectionZ) {
		this.walkDirectionX = walkDirectionX;
		this.walkDirectionY = walkDirectionY;
		this.walkDirectionZ = walkDirectionZ;
	}
	
//	TODO: Add Javadocs.
	public void setWalkLockEnabled(final boolean isWalkLockEnabled) {
		this.isWalkLockEnabled = isWalkLockEnabled;
	}
	
//	TODO: Add Javadocs.
	public void setYaw(final float yaw) {
		this.yaw = yaw % PI_MULTIPLIED_BY_TWO;
	}
	
//	TODO: Add Javadocs.
	public void strafe(final float distance) {
		final boolean isWalkLockEnabled = isWalkLockEnabled();
		
		final float x0 = isWalkLockEnabled ? getWalkDirectionX() : getViewDirectionX();
		final float y0 = isWalkLockEnabled ? getWalkDirectionY() : getViewDirectionY();
		final float z0 = isWalkLockEnabled ? getWalkDirectionZ() : getViewDirectionZ();
		
		final float x1 = 0.0F;
		final float y1 = 1.0F;
		final float z1 = 0.0F;
		
		final float x2 = y0 * z1 - z0 * y1;
		final float y2 = z0 * x1 - x0 * z1;
		final float z2 = x0 * y1 - y0 * x1;
		
		final float lengthReciprocal = 1.0F / sqrt(x2 * x2 + y2 * y2 + z2 * z2);
		
		final float x3 = x2 * lengthReciprocal;
		final float y3 = y2 * lengthReciprocal;
		final float z3 = z2 * lengthReciprocal;
		
		setCenter(getCenterX() + x3 * distance, getCenterY() + y3 * distance, getCenterZ() + z3 * distance);
	}
	
//	TODO: Add Javadocs.
	public void update() {
		final boolean isWalkLockEnabled = isWalkLockEnabled();
		
		final float centerX = getCenterX();
		final float centerY = getCenterY();
		final float centerZ = getCenterZ();
		
		final float radius = getRadius();
		
		final float pitch = getPitch();
		final float yaw = getYaw();
		
		final float direction0X = sin(yaw) * cos(pitch);
		final float direction0Y = sin(pitch);
		final float direction0Z = cos(yaw) * cos(pitch);
		
		final float direction1X = -direction0X;
		final float direction1Y = -direction0Y;
		final float direction1Z = -direction0Z;
		
		setEye(centerX + direction1X * radius, centerY + (isWalkLockEnabled ? 0.0F : direction1Y * radius), centerZ + direction1Z * radius);
		setOrthoNormalBasisW(direction1X, direction1Y, direction1Z);
		setUp(0.0F, 1.0F, 0.0F);
		setViewDirection(direction1X, direction1Y, direction1Z);
		setWalkDirection(direction1X, getWalkDirectionY(), direction1Z);
	}
}