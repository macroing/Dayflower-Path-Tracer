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

import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.concurrent.atomic.AtomicLong;

//TODO: Add Javadocs.
public final class FPSCounter {
	private final AtomicLong newFPS = new AtomicLong();
	private final AtomicLong newFPSReferenceTimeMillis = new AtomicLong();
	private final AtomicLong newFrameTimeMillis = new AtomicLong();
	private final AtomicLong oldFPS = new AtomicLong();
	private final AtomicLong oldFrameTimeMillis = new AtomicLong();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public FPSCounter() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public long getFPS() {
		return this.oldFPS.get();
	}
	
//	TODO: Add Javadocs.
	public long getFrameTimeMillis() {
		return this.oldFrameTimeMillis.get();
	}
	
//	TODO: Add Javadocs.
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