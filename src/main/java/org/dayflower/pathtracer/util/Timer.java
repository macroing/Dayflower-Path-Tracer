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

/**
 * A {@code Timer} represents a timer that can be used to measure the time that an activity has taken so far.
 * <p>
 * When you create a new {@code Timer} instance, it starts immediately. If you want to restart it, consider calling {@link #restart()}.
 * <p>
 * The default methods of this class uses {@code System.currentTimeMillis()} in the calculations. But all default methods have an overloaded method that requires a {@code long milliseconds} parameter. Prefer to use these overloaded methods whenever
 * you need to call multiple methods at the same "time". Otherwise, the results you get may not end up as you would expect.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Timer {
	private final AtomicLong milliseconds;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code Timer} instance and starts it.
	 * <p>
	 * Calling this constructor is equivalent to {@code new Timer(System.currentTimeMillis())}.
	 */
	public Timer() {
		this(System.currentTimeMillis());
	}
	
	/**
	 * Constructs a new {@code Timer} instance and starts it.
	 * 
	 * @param milliseconds the milliseconds to measure from
	 */
	public Timer(final long milliseconds) {
		this.milliseconds = new AtomicLong(milliseconds);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code String} with the hours, minutes and seconds that have passed between the time at which this {@code Timer} instance was started or restarted and now.
	 * <p>
	 * Calling this method is equivalent to {@code getTime(System.currentTimeMillis())}.
	 * 
	 * @return a {@code String} with the hours, minutes and seconds that have passed between the time at which this {@code Timer} instance was started or restarted and now
	 */
	public String getTime() {
		return getTime(System.currentTimeMillis());
	}
	
	/**
	 * Returns a {@code String} with the hours, minutes and seconds that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}.
	 * <p>
	 * This method uses {@code getHours(milliseconds)}, {@code getMinutesRemaining(milliseconds)} and {@code getSecondsRemaining(milliseconds)} to get the hours, minutes and seconds, respectively.
	 * 
	 * @param milliseconds the milliseconds to measure to
	 * @return a {@code String} with the hours, minutes and seconds that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}
	 */
	public String getTime(final long milliseconds) {
		final long hours = getHours(milliseconds);
		final long minutes = getMinutesRemaining(milliseconds);
		final long seconds = getSecondsRemaining(milliseconds);
		
		return String.format("%02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds));
	}
	
	/**
	 * Returns the hours that have passed between the time at which this {@code Timer} instance was started or restarted and now.
	 * <p>
	 * Calling this method is equivalent to {@code getHours(System.currentTimeMillis())}.
	 * 
	 * @return the hours that have passed between the time at which this {@code Timer} instance was started or restarted and now
	 */
	public long getHours() {
		return getHours(System.currentTimeMillis());
	}
	
	/**
	 * Returns the hours that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}.
	 * 
	 * @param milliseconds the milliseconds to measure to
	 * @return the hours that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}
	 */
	public long getHours(final long milliseconds) {
		return (milliseconds - this.milliseconds.get()) / (60L * 60L * 1000L);
	}
	
	/**
	 * Returns the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and now.
	 * <p>
	 * Calling this method is equivalent to {@code getMinutes(System.currentTimeMillis())}.
	 * 
	 * @return the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and now
	 */
	public long getMinutes() {
		return getMinutes(System.currentTimeMillis());
	}
	
	/**
	 * Returns the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}.
	 * 
	 * @param milliseconds the milliseconds to measure to
	 * @return the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}
	 */
	public long getMinutes(final long milliseconds) {
		return (milliseconds - this.milliseconds.get()) / (60L * 1000L);
	}
	
	/**
	 * Returns the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and now, excluding the minutes that are represented by full hours.
	 * <p>
	 * Calling this method is equivalent to {@code getMinutesRemaining(System.currentTimeMillis())}.
	 * 
	 * @return the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and now, excluding the minutes that are represented by full hours
	 */
	public long getMinutesRemaining() {
		return getMinutesRemaining(System.currentTimeMillis());
	}
	
	/**
	 * Returns the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}, excluding the minutes that are represented by full hours.
	 * 
	 * @param milliseconds the milliseconds to measure to
	 * @return the minutes that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}, excluding the minutes that are represented by full hours
	 */
	public long getMinutesRemaining(final long milliseconds) {
		return getMinutes(milliseconds) - getHours(milliseconds) * 60L;
	}
	
	/**
	 * Returns the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and now.
	 * <p>
	 * Calling this method is equivalent to {@code getSeconds(System.currentTimeMillis())}.
	 * 
	 * @return the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and now
	 */
	public long getSeconds() {
		return getSeconds(System.currentTimeMillis());
	}
	
	/**
	 * Returns the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}.
	 * 
	 * @param milliseconds the milliseconds to measure to
	 * @return the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}
	 */
	public long getSeconds(final long milliseconds) {
		return (milliseconds - this.milliseconds.get()) / 1000L;
	}
	
	/**
	 * Returns the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and now, excluding the seconds that are represented by full hours and minutes.
	 * <p>
	 * Calling this method is equivalent to {@code getSecondsRemaining(System.currentTimeMillis())}.
	 * 
	 * @return the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and now, excluding the seconds that are represented by full hours and minutes
	 */
	public long getSecondsRemaining() {
		return getSecondsRemaining(System.currentTimeMillis());
	}
	
	/**
	 * Returns the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}, excluding the seconds that are represented by full hours and minutes.
	 * 
	 * @param milliseconds the milliseconds to measure to
	 * @return the seconds that have passed between the time at which this {@code Timer} instance was started or restarted and {@code milliseconds}, excluding the seconds that are represented by full hours and minutes
	 */
	public long getSecondsRemaining(final long milliseconds) {
		return getSeconds(milliseconds) - getHours(milliseconds) * 60L * 60L - getMinutesRemaining(milliseconds) * 60L;
	}
	
	/**
	 * Restarts this {@code Timer} instance.
	 * <p>
	 * Calling this method is equivalent to {@code restart(System.currentTimeMillis())}.
	 */
	public void restart() {
		restart(System.currentTimeMillis());
	}
	
	/**
	 * Restarts this {@code Timer} instance.
	 * 
	 * @param milliseconds the milliseconds to measure from
	 */
	public void restart(final long milliseconds) {
		this.milliseconds.set(milliseconds);
	}
}