/**
 * Copyright 2015 - 2018 J&#246;rgen Lundgren
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

import static org.dayflower.pathtracer.math.MathF.atan;
import static org.dayflower.pathtracer.math.MathF.cos;
import static org.dayflower.pathtracer.math.MathF.max;
import static org.dayflower.pathtracer.math.MathF.min;
import static org.dayflower.pathtracer.math.MathF.sin;
import static org.dayflower.pathtracer.math.MathF.sqrt;
import static org.dayflower.pathtracer.math.MathF.tan;
import static org.dayflower.pathtracer.math.MathF.toDegrees;
import static org.dayflower.pathtracer.math.MathF.toRadians;

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dayflower.pathtracer.math.AngleF;
import org.dayflower.pathtracer.math.Vector3F;

//TODO: Add Javadocs.
public final class Camera {
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_APERTURE_RADIUS = 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_CAMERA_LENS = 18;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_EYE = 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_EYE_X = ABSOLUTE_OFFSET_OF_EYE + 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_EYE_Y = ABSOLUTE_OFFSET_OF_EYE + 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_EYE_Z = ABSOLUTE_OFFSET_OF_EYE + 2;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW = 4;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_X = ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_Y = ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW + 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE = 6;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U = 7;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_X = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U + 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Y = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U + 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Z = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U + 2;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V = 10;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_X = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V + 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Y = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V + 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Z = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V + 2;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W = 13;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_X = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Y = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 1;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Z = ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 2;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_RESOLUTION = 16;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_RESOLUTION_X = ABSOLUTE_OFFSET_OF_RESOLUTION + 0;
	
//	TODO: Add Javadocs.
	public static final int ABSOLUTE_OFFSET_OF_RESOLUTION_Y = ABSOLUTE_OFFSET_OF_RESOLUTION + 1;
	
//	TODO: Add Javadocs.
	public static final int CAMERA_LENS_FISHEYE = 2;
	
//	TODO: Add Javadocs.
	public static final int CAMERA_LENS_THIN = 1;
	
//	TODO: Add Javadocs.
	public static final int SIZE = 1 + 3 + 2 + 1 + 3 + 3 + 3 + 2 + 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private AngleF pitch;
	private AngleF yaw;
	private boolean hasUpdated;
	private boolean isWalkLockEnabled;
	private CameraPredicate cameraPredicate;
	private final float[] array;
	private final List<CameraObserver> cameraObservers;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public Camera() {
		this((oldX, oldY, oldZ, newX, newY, newZ) -> new boolean[] {true, true, true});
	}
	
//	TODO: Add Javadocs.
	public Camera(final CameraPredicate cameraPredicate) {
		this.array = new float[SIZE];
		this.cameraObservers = new ArrayList<>();
		
		setApertureRadius(0.4F);
		setCameraLens(CAMERA_LENS_THIN);
		setCameraPredicate(cameraPredicate);
		setEye(55.0F, 42.0F, 155.6F);
		setFieldOfViewX(40.0F);
		setFocalDistance(30.0F);
		setPitch(AngleF.pitch(Vector3F.x()));
		setResolution(800.0F, 800.0F);
		setWalkLockEnabled(true);
		setYaw(AngleF.yaw(Vector3F.y()));
		
		update();
	}
	
//	TODO: Add Javadocs.
	public Camera(final float[] array) {
		this(array, (oldX, oldY, oldZ, newX, newY, newZ) -> new boolean[] {true, true, true});
	}
	
//	TODO: Add Javadocs.
	public Camera(final float[] array, final CameraPredicate cameraPredicate) {
		this.array = array;
		this.cameraObservers = new ArrayList<>();
		
		setCameraPredicate(cameraPredicate);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public AngleF getPitch() {
		return this.pitch;
	}
	
//	TODO: Add Javadocs.
	public AngleF getYaw() {
		return this.yaw;
	}
	
//	TODO: Add Javadocs.
	public boolean hasUpdated() {
		return this.hasUpdated;
	}
	
//	TODO: Add Javadocs.
	public boolean isFisheyeCameraLens() {
		return this.array[ABSOLUTE_OFFSET_OF_CAMERA_LENS] == CAMERA_LENS_FISHEYE;
	}
	
//	TODO: Add Javadocs.
	public boolean isThinCameraLens() {
		return this.array[ABSOLUTE_OFFSET_OF_CAMERA_LENS] == CAMERA_LENS_THIN;
	}
	
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
	public float getEyeX() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE_X];
	}
	
//	TODO: Add Javadocs.
	public float getEyeY() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE_Y];
	}
	
//	TODO: Add Javadocs.
	public float getEyeZ() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE_Z];
	}
	
//	TODO: Add Javadocs.
	public float getFieldOfViewX() {
		return this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_X];
	}
	
//	TODO: Add Javadocs.
	public float getFieldOfViewY() {
		return this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_Y];
	}
	
//	TODO: Add Javadocs.
	public float getFocalDistance() {
		return this.array[ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisUX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_X];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisUY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Y];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisUZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Z];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisVX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_X];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisVY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Y];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisVZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Z];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisWX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_X];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisWY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Y];
	}
	
//	TODO: Add Javadocs.
	public float getOrthoNormalBasisWZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Z];
	}
	
//	TODO: Add Javadocs.
	public float getResolutionX() {
		return this.array[ABSOLUTE_OFFSET_OF_RESOLUTION_X];
	}
	
//	TODO: Add Javadocs.
	public float getResolutionY() {
		return this.array[ABSOLUTE_OFFSET_OF_RESOLUTION_Y];
	}
	
//	TODO: Add Javadocs.
	public float[] getArray() {
		return this.array;
	}
	
//	TODO: Add Javadocs.
	public void addCameraObserver(final CameraObserver cameraObserver) {
		this.cameraObservers.add(Objects.requireNonNull(cameraObserver, "cameraObserver == null"));
	}
	
//	TODO: Add Javadocs.
	public void changeAltitude(final float y) {
		setEye(getEyeX(), getEyeY() + y, getEyeZ());
	}
	
//	TODO: Add Javadocs.
	public void changePitch(final AngleF pitch) {
		setPitch(getPitch().add(pitch));
	}
	
//	TODO: Add Javadocs.
	public void changeYaw(final AngleF yaw) {
		setYaw(getYaw().add(yaw));
	}
	
//	TODO: Add Javadocs.
	public void forward(final float distance) {
		if(isWalkLockEnabled()) {
			setEye(getEyeX() + getOrthoNormalBasisWX() * distance, getEyeY(), getEyeZ() + getOrthoNormalBasisWZ() * distance);
		} else {
			setEye(getEyeX() + getOrthoNormalBasisWX() * distance, getEyeY() + getOrthoNormalBasisWY() * distance, getEyeZ() + getOrthoNormalBasisWZ() * distance);
		}
	}
	
//	TODO: Add Javadocs.
	public void removeCameraObserver(final CameraObserver cameraObserver) {
		this.cameraObservers.remove(Objects.requireNonNull(cameraObserver, "cameraObserver == null"));
	}
	
//	TODO: Add Javadocs.
	public void resetUpdateStatus() {
		this.hasUpdated = false;
	}
	
//	TODO: Add Javadocs.
	public void setApertureRadius(final float apertureRadius) {
		this.array[ABSOLUTE_OFFSET_OF_APERTURE_RADIUS] = max(min(apertureRadius, 25.0F), 0.0F);
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setCameraLens(final int cameraLens) {
		this.array[ABSOLUTE_OFFSET_OF_CAMERA_LENS] = cameraLens;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setCameraPredicate(final CameraPredicate cameraPredicate) {
		this.cameraPredicate = Objects.requireNonNull(cameraPredicate, "cameraPredicate == null");
	}
	
//	TODO: Add Javadocs.
	public void setEye(final float eyeX, final float eyeY, final float eyeZ) {
		final boolean[] test = this.cameraPredicate.test(getEyeX(), getEyeY(), getEyeZ(), eyeX, eyeY, eyeZ);
		
		if(test[0]) {
			this.array[ABSOLUTE_OFFSET_OF_EYE_X] = eyeX;
			this.hasUpdated = true;
		}
		
		if(test[1]) {
			this.array[ABSOLUTE_OFFSET_OF_EYE_Y] = eyeY;
			this.hasUpdated = true;
		}
		
		if(test[2]) {
			this.array[ABSOLUTE_OFFSET_OF_EYE_Z] = eyeZ;
			this.hasUpdated = true;
		}
	}
	
//	TODO: Add Javadocs.
	public void setFieldOfViewX(final float fieldOfViewX) {
		this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_X] = fieldOfViewX;
		this.array[ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_Y] = toDegrees(atan(tan(toRadians(fieldOfViewX) * 0.5F) * (getResolutionX() / getResolutionY())) * 2.0F);
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setFisheyeCameraLens(final boolean isFisheyeCameraLens) {
		this.array[ABSOLUTE_OFFSET_OF_CAMERA_LENS] = isFisheyeCameraLens ? CAMERA_LENS_FISHEYE : CAMERA_LENS_THIN;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setFocalDistance(final float focalDistance) {
		this.array[ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE] = max(min(focalDistance, 100.0F), 0.2F);
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setPitch(final AngleF pitch) {
		this.pitch = Objects.requireNonNull(pitch, "pitch == null");
		this.hasUpdated = true;
		this.cameraObservers.forEach(cameraObserver -> cameraObserver.pitchChanged(this, this.pitch));
	}
	
//	TODO: Add Javadocs.
	public void setResolution(final float resolutionX, final float resolutionY) {
		this.array[ABSOLUTE_OFFSET_OF_RESOLUTION_X] = resolutionX;
		this.array[ABSOLUTE_OFFSET_OF_RESOLUTION_Y] = resolutionY;
		this.hasUpdated = true;
		
		setFieldOfViewX(getFieldOfViewX());
	}
	
//	TODO: Add Javadocs.
	public void setThinCameraLens(final boolean isThinCameraLens) {
		this.array[ABSOLUTE_OFFSET_OF_CAMERA_LENS] = isThinCameraLens ? CAMERA_LENS_THIN : CAMERA_LENS_FISHEYE;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setWalkLockEnabled(final boolean isWalkLockEnabled) {
		this.isWalkLockEnabled = isWalkLockEnabled;
	}
	
//	TODO: Add Javadocs.
	public void setYaw(final AngleF yaw) {
		this.yaw = Objects.requireNonNull(yaw, "yaw == null");
		this.hasUpdated = true;
		this.cameraObservers.forEach(cameraObserver -> cameraObserver.yawChanged(this, this.yaw));
	}
	
//	TODO: Add Javadocs.
	public void strafe(final float distance) {
		final float uX = getOrthoNormalBasisUX();
		final float uY = getOrthoNormalBasisUY();
		final float uZ = getOrthoNormalBasisUZ();
		
		if(isWalkLockEnabled()) {
			setEye(getEyeX() + uX * distance, getEyeY(), getEyeZ() + uZ * distance);
		} else {
			setEye(getEyeX() + uX * distance, getEyeY() + uY * distance, getEyeZ() + uZ * distance);
		}
	}
	
//	TODO: Add Javadocs.
	public void update() {
		final float pitch = getPitch().radians;
		final float yaw = getYaw().radians;
		
		final float orthoNormalBasisWX = -(sin(yaw) * cos(pitch));
		final float orthoNormalBasisWY = -sin(pitch);
		final float orthoNormalBasisWZ = -(cos(yaw) * cos(pitch));
		
		doSetOrthoNormalBasisW(orthoNormalBasisWX, orthoNormalBasisWY, orthoNormalBasisWZ);
		doCalculateOrthoNormalBasisU();
		doCalculateOrthoNormalBasisV();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void doCalculateOrthoNormalBasisU() {
		final float upX = 0.0F;
		final float upY = 1.0F;
		final float upZ = 0.0F;
		
		final float wX = getOrthoNormalBasisWX();
		final float wY = getOrthoNormalBasisWY();
		final float wZ = getOrthoNormalBasisWZ();
		
		final float u0X = upY * wZ - upZ * wY;
		final float u0Y = upZ * wX - upX * wZ;
		final float u0Z = upX * wY - upY * wX;
		final float u0LengthReciprocal = 1.0F / sqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);
		final float u1X = u0X * u0LengthReciprocal;
		final float u1Y = u0Y * u0LengthReciprocal;
		final float u1Z = u0Z * u0LengthReciprocal;
		
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_X] = u1X;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Y] = u1Y;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Z] = u1Z;
	}
	
	private void doCalculateOrthoNormalBasisV() {
		final float uX = getOrthoNormalBasisUX();
		final float uY = getOrthoNormalBasisUY();
		final float uZ = getOrthoNormalBasisUZ();
		
		final float wX = getOrthoNormalBasisWX();
		final float wY = getOrthoNormalBasisWY();
		final float wZ = getOrthoNormalBasisWZ();
		
		final float v0X = wY * uZ - wZ * uY;
		final float v0Y = wZ * uX - wX * uZ;
		final float v0Z = wX * uY - wY * uX;
		final float v0LengthReciprocal = 1.0F / sqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);
		final float v1X = v0X * v0LengthReciprocal;
		final float v1Y = v0Y * v0LengthReciprocal;
		final float v1Z = v0Z * v0LengthReciprocal;
		
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_X] = v1X;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Y] = v1Y;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Z] = v1Z;
	}
	
	private void doSetOrthoNormalBasisW(final float orthoNormalBasisWX, final float orthoNormalBasisWY, final float orthoNormalBasisWZ) {
		final float orthoNormalBasisWLengthReciprocal = 1.0F / sqrt(orthoNormalBasisWX * orthoNormalBasisWX + orthoNormalBasisWY * orthoNormalBasisWY + orthoNormalBasisWZ * orthoNormalBasisWZ);
		
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_X] = orthoNormalBasisWX * orthoNormalBasisWLengthReciprocal;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Y] = orthoNormalBasisWY * orthoNormalBasisWLengthReciprocal;
		this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Z] = orthoNormalBasisWZ * orthoNormalBasisWLengthReciprocal;
		this.hasUpdated = true;
	}
}