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
package org.dayflower.pathtracer.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An {@code FPSCounter} is used for calculating frames per second (FPS).
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class FPSCounter {
	private final AtomicLong newFPS = new AtomicLong();
	private final AtomicLong newFPSReferenceTimeMillis = new AtomicLong();
	private final AtomicLong newFrameTimeMillis = new AtomicLong();
	private final AtomicLong oldFPS = new AtomicLong();
	private final AtomicLong oldFrameTimeMillis = new AtomicLong();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code FPSCounter} instance.
	 */
	public FPSCounter() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the current FPS.
	 * 
	 * @return the current FPS
	 */
	public long getFPS() {
		return this.oldFPS.get();
	}
	
	/**
	 * Returns the milliseconds since the last update.
	 * 
	 * @return the milliseconds since the last update
	 */
	public long getFrameTimeMillis() {
		return this.oldFrameTimeMillis.get();
	}
	
	/**
	 * Updates this {@code FPSCounter}.
	 * <p>
	 * This method should be called once every frame.
	 */
	public void update() {
		final long currentTimeMillis = System.currentTimeMillis();
		
		this.newFPS.incrementAndGet();
		this.newFPSReferenceTimeMillis.compareAndSet(0L, currentTimeMillis);
		this.oldFrameTimeMillis.set(currentTimeMillis - this.newFrameTimeMillis.get());
		this.newFrameTimeMillis.set(currentTimeMillis);
		
		final long newFPSReferenceTimeMillis = this.newFPSReferenceTimeMillis.get();
		final long newFPSElapsedTimeMillis = currentTimeMillis - newFPSReferenceTimeMillis;
		
		if(newFPSElapsedTimeMillis >= 1000L) {
			this.oldFPS.set(this.newFPS.get());
			this.newFPS.set(0L);
			this.newFPSReferenceTimeMillis.set(currentTimeMillis);
		}
	}
}