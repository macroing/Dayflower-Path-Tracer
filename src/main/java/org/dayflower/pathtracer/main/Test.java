package org.dayflower.pathtracer.main;

import org.dayflower.pathtracer.util.Image;

public final class Test {
	private Test() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final
		Image image = new Image();
		image.clear(0.1F, 0.2F, 0.3F);
		image.drawLine(0, 0, image.getResolutionX(), image.getResolutionY(), 0.0F, 1.0F, 0.0F);
		image.drawTriangle(10, 10, 110, 10, 10, 110, 1.0F, 0.0F, 0.0F);
		image.save("Test-Image.png");
	}
}