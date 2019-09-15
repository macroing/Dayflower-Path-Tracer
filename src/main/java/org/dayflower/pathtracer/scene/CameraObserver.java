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

import org.macroing.math4j.AngleF;

/**
 * A {@code CameraObserver} can be used to observe changes to a {@link Camera} instance.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface CameraObserver {
	/**
	 * Called by a {@link Camera} instance when its pitch has changed.
	 * 
	 * @param camera the {@code Camera} that called this method
	 * @param pitch the new pitch
	 */
	void pitchChanged(final Camera camera, final AngleF pitch);
	
	/**
	 * Called by a {@link Camera} instance when its yaw has changed.
	 * 
	 * @param camera the {@code Camera} that called this method
	 * @param yaw the new yaw
	 */
	void yawChanged(final Camera camera, final AngleF yaw);
}