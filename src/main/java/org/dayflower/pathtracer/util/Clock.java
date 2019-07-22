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

import java.util.concurrent.atomic.AtomicLong;

public final class Clock {
	private final AtomicLong milliseconds;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Clock() {
		this.milliseconds = new AtomicLong(System.currentTimeMillis());
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getTime() {
		return getTime(System.currentTimeMillis());
	}
	
	public String getTime(final long milliseconds) {
		final long hours = getHours(milliseconds);
		final long minutes = getMinutes(milliseconds) - (hours * 60L);
		final long seconds = getSeconds(milliseconds) - (hours * 60L * 60L) - (minutes * 60L);
		
		return String.format("%02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds));
	}
	
	public long getHours() {
		return getHours(System.currentTimeMillis());
	}
	
	public long getHours(final long milliseconds) {
		return (milliseconds - this.milliseconds.get()) / (60L * 60L * 1000L);
	}
	
	public long getMinutes() {
		return getMinutes(System.currentTimeMillis());
	}
	
	public long getMinutes(final long milliseconds) {
		return (milliseconds - this.milliseconds.get()) / (60L * 1000L);
	}
	
	public long getSeconds() {
		return getSeconds(System.currentTimeMillis());
	}
	
	public long getSeconds(final long milliseconds) {
		return (milliseconds - this.milliseconds.get()) / 1000L;
	}
	
	public void restart() {
		this.milliseconds.set(System.currentTimeMillis());
	}
}