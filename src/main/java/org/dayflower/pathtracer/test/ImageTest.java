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
package org.dayflower.pathtracer.test;

import java.io.File;

import org.dayflower.pathtracer.util.Image;

public final class ImageTest {
	private ImageTest() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final
		Image image = new Image(new File("../07.jpg"));
//		image.clear(0.1F, 0.2F, 0.3F);
		image.gammaCorrectionUndo();
//		image.drawLine(0, 0, image.getResolutionX(), image.getResolutionY(), 0.0F, 1.0F, 0.0F);
//		image.drawTriangle(10, 10, 110, 10, 10, 110, 1.0F, 0.0F, 0.0F);
//		image.convolutionFilter33Random();
//		image.toneMappingFilmicCurveACES(1.0F);
//		image.effectSepia();
//		image.toneMappingUnreal3(1.0F);
		image.toneMappingFilmicCurveACES2(1.0F);
		image.gammaCorrectionRedo();
		image.save("../Test-Image.png");
	}
}