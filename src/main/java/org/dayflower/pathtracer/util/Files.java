/**
 * Copyright 2015 - 2021 J&#246;rgen Lundgren
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that consists exclusively of static methods that operates on or returns {@code File}s.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Files {
	private Files() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@code File} that denotes a file that does not currently exist.
	 * <p>
	 * The filename of the file is based on {@code filenameFormat} and a number that is calculated by this method. For this algorithm to work, {@code filenameFormat} needs to have exactly one occurrence of {@code "%s"} in it.
	 * <p>
	 * An example of {@code filenameFormat} could be {@code "Dayflower-Image-%s.png"}. This could result in a filename of {@code "Dayflower-Image-1.png"}. If a file with that filename already exists, the next would be {@code "Dayflower-Image-2.png"}.
	 * <p>
	 * If either {@code directory} or {@code filenameFormat} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param directory the directory of the files
	 * @param filenameFormat the filename format to use
	 * @return a {@code File} that denotes a file that does not currently exist
	 */
	public static File findNextFile(final File directory, final String filenameFormat) {
		Objects.requireNonNull(directory, "directory == null");
		Objects.requireNonNull(filenameFormat, "filenameFormat == null");
		
		int number = 0;
		
		if(directory.isDirectory()) {
			final List<File> filesInDirectory = Arrays.asList(directory.listFiles());
			final List<File> filesMatchingPattern = new ArrayList<>();
			
			final String filenamePattern = String.format("^" + filenameFormat.replace(".", "\\.") + "$", "([0-9]+)");
			
			final Pattern pattern = Pattern.compile(filenamePattern, Pattern.CASE_INSENSITIVE);
			
			for(final File fileInDirectory : filesInDirectory) {
				if(fileInDirectory.isFile() && pattern.matcher(fileInDirectory.getName()).matches()) {
					filesMatchingPattern.add(fileInDirectory);
				}
			}
			
			Collections.sort(filesMatchingPattern);
			
			boolean isNumberAvailable = true;
			
			do {
				number++;
				
				isNumberAvailable = true;
				
				for(final File fileMatchingPattern : filesMatchingPattern) {
					final Matcher matcher = pattern.matcher(fileMatchingPattern.getName());
					
					if(matcher.matches()) {
						final String number0 = matcher.group(1);
						final String number1 = Integer.toString(number);
						
						if(number0.equals(number1)) {
							isNumberAvailable = false;
						}
					}
				}
			} while(!isNumberAvailable);
		} else {
			number++;
		}
		
		final String filename = String.format(filenameFormat, Integer.toString(number));
		
		return new File(directory, filename);
	}
}