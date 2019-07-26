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

/**
 * A {@code Camera} represents a camera in the scene from which to render an image.
 * <p>
 * This {@code Camera} class uses a {@code float} array to store certain parameters. This {@code float} array can be accessed by calling {@link #getArray()}. It is never cloned, so use it with caution.
 * <p>
 * Because the {@code float} array is never cloned, changing its element values will change the state of this class, however, the {@link #hasUpdated()} method will not be aware of this state change. The preferred way to change the parameters, is to use
 * the methods of this class.
 * <p>
 * When the pitch and yaw parameters have been changed, it's required to call the {@link #update()} method, which updates the OrthoNormal Basis.
 * <p>
 * The {@code float} array has the following format:
 * <pre>
 * {@code
 * 00 array[ABSOLUTE_OFFSET_APERTURE_RADIUS]:       Aperture Radius
 * 01 array[ABSOLUTE_OFFSET_EYE_X]:                 Eye X
 * 02 array[ABSOLUTE_OFFSET_EYE_Y]:                 Eye Y
 * 03 array[ABSOLUTE_OFFSET_EYE_Z]:                 Eye Z
 * 04 array[ABSOLUTE_OFFSET_FIELD_OF_VIEW_X]:       Field of View X in Degrees
 * 05 array[ABSOLUTE_OFFSET_FIELD_OF_VIEW_Y]:       Field of View Y in Degrees
 * 06 array[ABSOLUTE_OFFSET_FOCAL_DISTANCE]:        Focal Distance
 * 07 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X]: OrthoNormal Basis U X
 * 08 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y]: OrthoNormal Basis U Y
 * 09 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z]: OrthoNormal Basis U Z
 * 10 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X]: OrthoNormal Basis V X
 * 11 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y]: OrthoNormal Basis V Y
 * 12 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z]: OrthoNormal Basis V Z
 * 13 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X]: OrthoNormal Basis W X
 * 14 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y]: OrthoNormal Basis W Y
 * 15 array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z]: OrthoNormal Basis W Z
 * 16 array[ABSOLUTE_OFFSET_RESOLUTION_X]:          Resolution X or Width
 * 17 array[ABSOLUTE_OFFSET_RESOLUTION_Y]:          Resolution Y or Height
 * 18 array[ABSOLUTE_OFFSET_CAMERA_LENS]:           Camera Lens (one of CAMERA_LENS_FISHEYE (2) and CAMERA_LENS_THIN (1))
 * }
 * </pre>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Camera {
	/**
	 * The absolute offset of the Aperture Radius parameter in the {@code float} array. The value is {@code 0}.
	 */
	public static final int ABSOLUTE_OFFSET_APERTURE_RADIUS = 0;
	
	/**
	 * The absolute offset of the Camera Lens parameter in the {@code float} array. The value is {@code 18}.
	 */
	public static final int ABSOLUTE_OFFSET_CAMERA_LENS = 18;
	
	/**
	 * The absolute offset of the Eye parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int ABSOLUTE_OFFSET_EYE = 1;
	
	/**
	 * The absolute offset of the Eye X parameter in the {@code float} array. The value is {@code 1}.
	 */
	public static final int ABSOLUTE_OFFSET_EYE_X = ABSOLUTE_OFFSET_EYE + 0;
	
	/**
	 * The absolute offset of the Eye Y parameter in the {@code float} array. The value is {@code 2}.
	 */
	public static final int ABSOLUTE_OFFSET_EYE_Y = ABSOLUTE_OFFSET_EYE + 1;
	
	/**
	 * The absolute offset of the Eye Z parameter in the {@code float} array. The value is {@code 3}.
	 */
	public static final int ABSOLUTE_OFFSET_EYE_Z = ABSOLUTE_OFFSET_EYE + 2;
	
	/**
	 * The absolute offset of the Field of View parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int ABSOLUTE_OFFSET_FIELD_OF_VIEW = 4;
	
	/**
	 * The absolute offset of the Field of View X parameter in the {@code float} array. The value is {@code 4}.
	 */
	public static final int ABSOLUTE_OFFSET_FIELD_OF_VIEW_X = ABSOLUTE_OFFSET_FIELD_OF_VIEW + 0;
	
	/**
	 * The absolute offset of the Field of View Y parameter in the {@code float} array. The value is {@code 5}.
	 */
	public static final int ABSOLUTE_OFFSET_FIELD_OF_VIEW_Y = ABSOLUTE_OFFSET_FIELD_OF_VIEW + 1;
	
	/**
	 * The absolute offset of the Focal Distance parameter in the {@code float} array. The value is {@code 6}.
	 */
	public static final int ABSOLUTE_OFFSET_FOCAL_DISTANCE = 6;
	
	/**
	 * The absolute offset of the OrthoNormal Basis U parameter in the {@code float} array. The value is {@code 7}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U = 7;
	
	/**
	 * The absolute offset of the OrthoNormal Basis U X parameter in the {@code float} array. The value is {@code 7}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U + 0;
	
	/**
	 * The absolute offset of the OrthoNormal Basis U Y parameter in the {@code float} array. The value is {@code 8}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U + 1;
	
	/**
	 * The absolute offset of the OrthoNormal Basis U Z parameter in the {@code float} array. The value is {@code 9}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U + 2;
	
	/**
	 * The absolute offset of the OrthoNormal Basis V parameter in the {@code float} array. The value is {@code 10}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V = 10;
	
	/**
	 * The absolute offset of the OrthoNormal Basis V X parameter in the {@code float} array. The value is {@code 10}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V + 0;
	
	/**
	 * The absolute offset of the OrthoNormal Basis V Y parameter in the {@code float} array. The value is {@code 11}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V + 1;
	
	/**
	 * The absolute offset of the OrthoNormal Basis V Z parameter in the {@code float} array. The value is {@code 12}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V + 2;
	
	/**
	 * The absolute offset of the OrthoNormal Basis W parameter in the {@code float} array. The value is {@code 13}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W = 13;
	
	/**
	 * The absolute offset of the OrthoNormal Basis W X parameter in the {@code float} array. The value is {@code 13}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W + 0;
	
	/**
	 * The absolute offset of the OrthoNormal Basis W Y parameter in the {@code float} array. The value is {@code 14}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W + 1;
	
	/**
	 * The absolute offset of the OrthoNormal Basis W Z parameter in the {@code float} array. The value is {@code 15}.
	 */
	public static final int ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z = ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W + 2;
	
	/**
	 * The absolute offset of the Resolution parameter in the {@code float} array. The value is {@code 16}.
	 */
	public static final int ABSOLUTE_OFFSET_RESOLUTION = 16;
	
	/**
	 * The absolute offset of the Resolution X parameter in the {@code float} array. The value is {@code 16}.
	 */
	public static final int ABSOLUTE_OFFSET_RESOLUTION_X = ABSOLUTE_OFFSET_RESOLUTION + 0;
	
	/**
	 * The absolute offset of the Resolution Y parameter in the {@code float} array. The value is {@code 17}.
	 */
	public static final int ABSOLUTE_OFFSET_RESOLUTION_Y = ABSOLUTE_OFFSET_RESOLUTION + 1;
	
	/**
	 * A constant denoting the Camera Lens parameter value for a Fisheye camera lens. The value is {@code 2}.
	 */
	public static final int CAMERA_LENS_FISHEYE = 2;
	
	/**
	 * A constant denoting the Camera Lens parameter value for a Thin camera lens. The value is {@code 1}.
	 */
	public static final int CAMERA_LENS_THIN = 1;
	
	/**
	 * A constant containing the size of the {@code float} array. The size is {@code 19}.
	 */
	public static final int SIZE = 1 + 3 + 2 + 1 + 3 + 3 + 3 + 2 + 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private AngleF pitch;
	private AngleF yaw;
	private boolean hasUpdated;
	private boolean isWalkLockEnabled;
	private float[] array;
	private final List<CameraObserver> cameraObservers;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new default {@code Camera} instance.
	 */
	public Camera() {
		this.array = new float[SIZE];
		this.cameraObservers = new ArrayList<>();
		
		setApertureRadius(0.0F);
		setCameraLens(CAMERA_LENS_THIN);
		setEye(55.0F, 42.0F, 155.6F);
		setFieldOfViewX(90.0F);
		setFocalDistance(30.0F);
		setPitch(AngleF.pitch(Vector3F.x()));
		setResolution(800.0F, 800.0F);
		setWalkLockEnabled(true);
		setYaw(AngleF.yaw(Vector3F.y()));
		
		update();
	}
	
	/**
	 * Constructs a new {@code Camera} instance given {@code array} as the {@code float} array to store certain parameters.
	 * <p>
	 * This constructor does not clone {@code array}.
	 * 
	 * @param array the {@code float} array to store certain parameters
	 */
	public Camera(final float[] array) {
		this.array = array;
		this.cameraObservers = new ArrayList<>();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns an {@link AngleF} with the current pitch angle.
	 * 
	 * @return an {@code AngleF} with the current pitch angle
	 */
	public AngleF getPitch() {
		return this.pitch;
	}
	
	/**
	 * Returns an {@link AngleF} with the current yaw angle.
	 * 
	 * @return an {@code AngleF} with the current yaw angle
	 */
	public AngleF getYaw() {
		return this.yaw;
	}
	
	/**
	 * Returns {@code true} if, and only if, the state of this {@code Camera} instance has been updated, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the state of this {@code Camera} instance has been updated, {@code false} otherwise
	 */
	public boolean hasUpdated() {
		return this.hasUpdated;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Camera Lens parameter is set to Fisheye camera lens, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Camera Lens parameter is set to Fisheye camera lens, {@code false} otherwise
	 */
	public boolean isFisheyeCameraLens() {
		return this.array[ABSOLUTE_OFFSET_CAMERA_LENS] == CAMERA_LENS_FISHEYE;
	}
	
	/**
	 * Returns {@code true} if, and only if, the Camera Lens parameter is set to Thin camera lens, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, the Camera Lens parameter is set to Thin camera lens, {@code false} otherwise
	 */
	public boolean isThinCameraLens() {
		return this.array[ABSOLUTE_OFFSET_CAMERA_LENS] == CAMERA_LENS_THIN;
	}
	
	/**
	 * Returns {@code true} if, and only if, walk lock is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, walk lock is enabled, {@code false} otherwise
	 */
	public boolean isWalkLockEnabled() {
		return this.isWalkLockEnabled;
	}
	
	/**
	 * Returns the value of the Aperture Radius parameter.
	 * 
	 * @return the value of the Aperture Radius parameter
	 */
	public float getApertureRadius() {
		return this.array[ABSOLUTE_OFFSET_APERTURE_RADIUS];
	}
	
	/**
	 * Returns the value of the Eye X parameter.
	 * 
	 * @return the value of the Eye X parameter
	 */
	public float getEyeX() {
		return this.array[ABSOLUTE_OFFSET_EYE_X];
	}
	
	/**
	 * Returns the value of the Eye Y parameter.
	 * 
	 * @return the value of the Eye Y parameter
	 */
	public float getEyeY() {
		return this.array[ABSOLUTE_OFFSET_EYE_Y];
	}
	
	/**
	 * Returns the value of the Eye Z parameter.
	 * 
	 * @return the value of the Eye Z parameter
	 */
	public float getEyeZ() {
		return this.array[ABSOLUTE_OFFSET_EYE_Z];
	}
	
	/**
	 * Returns the value of the Field of View X parameter.
	 * 
	 * @return the value of the Field of View X parameter
	 */
	public float getFieldOfViewX() {
		return this.array[ABSOLUTE_OFFSET_FIELD_OF_VIEW_X];
	}
	
	/**
	 * Returns the value of the Field of View Y parameter.
	 * 
	 * @return the value of the Field of View Y parameter
	 */
	public float getFieldOfViewY() {
		return this.array[ABSOLUTE_OFFSET_FIELD_OF_VIEW_Y];
	}
	
	/**
	 * Returns the value of the Focal Distance parameter.
	 * 
	 * @return the value of the Focal Distance parameter
	 */
	public float getFocalDistance() {
		return this.array[ABSOLUTE_OFFSET_FOCAL_DISTANCE];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis U X parameter.
	 * 
	 * @return the value of the OrthoNormal Basis U X parameter
	 */
	public float getOrthoNormalBasisUX() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis U Y parameter.
	 * 
	 * @return the value of the OrthoNormal Basis U Y parameter
	 */
	public float getOrthoNormalBasisUY() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis U Z parameter.
	 * 
	 * @return the value of the OrthoNormal Basis U Z parameter
	 */
	public float getOrthoNormalBasisUZ() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis V X parameter.
	 * 
	 * @return the value of the OrthoNormal Basis V X parameter
	 */
	public float getOrthoNormalBasisVX() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis V Y parameter.
	 * 
	 * @return the value of the OrthoNormal Basis V Y parameter
	 */
	public float getOrthoNormalBasisVY() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis V Z parameter.
	 * 
	 * @return the value of the OrthoNormal Basis V Z parameter
	 */
	public float getOrthoNormalBasisVZ() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis W X parameter.
	 * 
	 * @return the value of the OrthoNormal Basis W X parameter
	 */
	public float getOrthoNormalBasisWX() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis W Y parameter.
	 * 
	 * @return the value of the OrthoNormal Basis W Y parameter
	 */
	public float getOrthoNormalBasisWY() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y];
	}
	
	/**
	 * Returns the value of the OrthoNormal Basis W Z parameter.
	 * 
	 * @return the value of the OrthoNormal Basis W Z parameter
	 */
	public float getOrthoNormalBasisWZ() {
		return this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z];
	}
	
	/**
	 * Returns the value of the Resolution X parameter.
	 * 
	 * @return the value of the Resolution X parameter
	 */
	public float getResolutionX() {
		return this.array[ABSOLUTE_OFFSET_RESOLUTION_X];
	}
	
	/**
	 * Returns the value of the Resolution Y parameter.
	 * 
	 * @return the value of the Resolution Y parameter
	 */
	public float getResolutionY() {
		return this.array[ABSOLUTE_OFFSET_RESOLUTION_Y];
	}
	
	/**
	 * Returns the {@code float} array that stores certain parameters.
	 * <p>
	 * This method will return the actual array and not a clone of it.
	 * 
	 * @return the {@code float} array that stores certain parameters
	 */
	public float[] getArray() {
		return this.array;
	}
	
	/**
	 * Adds {@code cameraObserver} to this {@code Camera} instance.
	 * <p>
	 * If {@code cameraObserver} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param cameraObserver the {@link CameraObserver} to add
	 * @throws NullPointerException thrown if, and only if, {@code cameraObserver} is {@code null}
	 */
	public void addCameraObserver(final CameraObserver cameraObserver) {
		this.cameraObservers.add(Objects.requireNonNull(cameraObserver, "cameraObserver == null"));
	}
	
	/**
	 * Changes the altitude of this {@code Camera} instance.
	 * <p>
	 * This method changes the Eye Y parameter.
	 * 
	 * @param y the value to add to the Eye Y parameter
	 */
	public void changeAltitude(final float y) {
		setEye(getEyeX(), getEyeY() + y, getEyeZ());
	}
	
//	TODO: Add Javadocs.
	public void changePitch(final AngleF pitch) {
		setPitch(AngleF.degrees(max(min(getPitch().degrees + pitch.degrees, 89.99F), -89.99F), -89.99F, 89.99F));
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
	
	/**
	 * Removes {@code cameraObserver} from this {@code Camera} instance.
	 * <p>
	 * If {@code cameraObserver} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param cameraObserver the {@link CameraObserver} to remove
	 * @throws NullPointerException thrown if, and only if, {@code cameraObserver} is {@code null}
	 */
	public void removeCameraObserver(final CameraObserver cameraObserver) {
		this.cameraObservers.remove(Objects.requireNonNull(cameraObserver, "cameraObserver == null"));
	}
	
//	TODO: Add Javadocs.
	public void setApertureRadius(final float apertureRadius) {
		this.array[ABSOLUTE_OFFSET_APERTURE_RADIUS] = max(min(apertureRadius, 25.0F), 0.0F);
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setArray(final float[] array) {
		this.array = array;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setCameraLens(final int cameraLens) {
		this.array[ABSOLUTE_OFFSET_CAMERA_LENS] = cameraLens;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setEye(final float eyeX, final float eyeY, final float eyeZ) {
		this.array[ABSOLUTE_OFFSET_EYE_X] = eyeX;
		this.array[ABSOLUTE_OFFSET_EYE_Y] = eyeY;
		this.array[ABSOLUTE_OFFSET_EYE_Z] = eyeZ;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setFieldOfViewX(final float fieldOfViewX) {
		this.array[ABSOLUTE_OFFSET_FIELD_OF_VIEW_X] = fieldOfViewX;
		this.array[ABSOLUTE_OFFSET_FIELD_OF_VIEW_Y] = toDegrees(atan(tan(toRadians(fieldOfViewX) * 0.5F) * (getResolutionX() / getResolutionY())) * 2.0F);
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setFisheyeCameraLens(final boolean isFisheyeCameraLens) {
		this.array[ABSOLUTE_OFFSET_CAMERA_LENS] = isFisheyeCameraLens ? CAMERA_LENS_FISHEYE : CAMERA_LENS_THIN;
		this.hasUpdated = true;
	}
	
//	TODO: Add Javadocs.
	public void setFocalDistance(final float focalDistance) {
		this.array[ABSOLUTE_OFFSET_FOCAL_DISTANCE] = max(min(focalDistance, 100.0F), 0.2F);
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
		this.array[ABSOLUTE_OFFSET_RESOLUTION_X] = resolutionX;
		this.array[ABSOLUTE_OFFSET_RESOLUTION_Y] = resolutionY;
		this.hasUpdated = true;
		
		setFieldOfViewX(getFieldOfViewX());
	}
	
//	TODO: Add Javadocs.
	public void setThinCameraLens(final boolean isThinCameraLens) {
		this.array[ABSOLUTE_OFFSET_CAMERA_LENS] = isThinCameraLens ? CAMERA_LENS_THIN : CAMERA_LENS_FISHEYE;
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
	
	/**
	 * Updates the OrthoNormal Basis parameter using the current pitch and yaw angles and clears the update flag.
	 */
	public void update() {
		final float pitch = getPitch().radians;
		final float yaw = getYaw().radians;
		
		final float orthoNormalBasisWX = -(sin(yaw) * cos(pitch));
		final float orthoNormalBasisWY = -sin(pitch);
		final float orthoNormalBasisWZ = -(cos(yaw) * cos(pitch));
		
		doSetOrthoNormalBasisW(orthoNormalBasisWX, orthoNormalBasisWY, orthoNormalBasisWZ);
		doCalculateOrthoNormalBasisU();
		doCalculateOrthoNormalBasisV();
		
		this.hasUpdated = false;
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
		
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_X] = u1X;
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Y] = u1Y;
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_U_Z] = u1Z;
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
		
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_X] = v1X;
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Y] = v1Y;
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_V_Z] = v1Z;
	}
	
	private void doSetOrthoNormalBasisW(final float orthoNormalBasisWX, final float orthoNormalBasisWY, final float orthoNormalBasisWZ) {
		final float orthoNormalBasisWLengthReciprocal = 1.0F / sqrt(orthoNormalBasisWX * orthoNormalBasisWX + orthoNormalBasisWY * orthoNormalBasisWY + orthoNormalBasisWZ * orthoNormalBasisWZ);
		
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_X] = orthoNormalBasisWX * orthoNormalBasisWLengthReciprocal;
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Y] = orthoNormalBasisWY * orthoNormalBasisWLengthReciprocal;
		this.array[ABSOLUTE_OFFSET_ORTHONORMAL_BASIS_W_Z] = orthoNormalBasisWZ * orthoNormalBasisWLengthReciprocal;
		
		this.hasUpdated = true;
	}
}