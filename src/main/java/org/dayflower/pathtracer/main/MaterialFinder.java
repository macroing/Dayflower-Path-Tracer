/**
 * Copyright 2009 - 2017 J&#246;rgen Lundgren
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
package org.dayflower.pathtracer.main;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO: Add Javadocs.
public final class MaterialFinder {
	private MaterialFinder() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static void main(final String[] args) {
		final List<String> lines0 = doReadAllLines("resources/house interior.obj");
		final List<String> lines1 = doFindAllUseMtlLines(lines0);
		
		for(final String line : lines1) {
			System.out.println(line);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static List<String> doFindAllUseMtlLines(final List<String> lines) {
		final Pattern pattern = Pattern.compile("\\s*usemtl (.*)");
		
		final Set<String> lines0 = new LinkedHashSet<>();
		
		for(final String line : lines) {
			final Matcher matcher = pattern.matcher(line);
			
			if(matcher.matches()) {
				lines0.add(matcher.group(1));
			}
		}
		
		return new ArrayList<>(lines0);
	}
	
	private static List<String> doReadAllLines(final String filename) {
		try {
			return Files.readAllLines(new File(Objects.requireNonNull(filename, "filename == null")).toPath());
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}