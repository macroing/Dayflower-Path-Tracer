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
package org.dayflower.pathtracer.color;

/**
 * A {@code ToneMapper} is an abstraction of a Tone Mapping operator to be applied on a {@link Color}.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public interface ToneMapper {
	/**
	 * Applies Tone Mapping to {@code color}.
	 * <p>
	 * Returns a {@link Color} instance denoting the Tone Mapped version of {@code color}.
	 * 
	 * @param color the {@code Color} to apply Tone Mapping to
	 * @return a {@code Color} instance denoting the Tone Mapped version of {@code color}
	 */
	Color applyToneMapping(final Color color);
}