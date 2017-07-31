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
package org.dayflower.pathtracer.main;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Noise;

public final class NoiseRenderer {
	private NoiseRenderer() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final int width = 1024;
		final int height = 768;
		
		final
		Noise noise = new Noise();
		noise.setAmplitude(0.5F);
		noise.setFrequency(0.1F);
		noise.setLacunarity(4.0F);
		noise.setGain(1.0F / noise.getLacunarity());
		noise.setOctaves(8);
		
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		final WritableRaster writableRaster = bufferedImage.getRaster();
		
		final DataBuffer dataBuffer = writableRaster.getDataBuffer();
		
		if(dataBuffer instanceof DataBufferInt) {
			final DataBufferInt dataBufferInt = DataBufferInt.class.cast(dataBuffer);
			
			final int[] data = dataBufferInt.getData();
			
			for(int i = 0; i < data.length; i++) {
				final float x = (i % width) / 50.0F;
				final float y = (i / height) / 50.0F;
				final float z = noise.simplexFractalXY(x, y);// + 0.5F;
				
				data[i] = new Color(z, z, z).multiply(255.0F).toRGB();
			}
		}
		
		final JPanel jPanel = new JBufferedImagePanel(bufferedImage);
		
		final
		JFrame jFrame = new JFrame();
		jFrame.getContentPane().setLayout(new BorderLayout());
		jFrame.getContentPane().add(jPanel, BorderLayout.CENTER);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(width, height);
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class JBufferedImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private final BufferedImage bufferedImage;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public JBufferedImagePanel(final BufferedImage bufferedImage) {
			this.bufferedImage = Objects.requireNonNull(bufferedImage, "bufferedImage == null");
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		protected void paintComponent(final Graphics graphics) {
			final
			Graphics2D graphics2D = Graphics2D.class.cast(graphics);
			graphics2D.drawImage(this.bufferedImage, 0, 0, this.bufferedImage.getWidth(), this.bufferedImage.getHeight(), this);
		}
	}
}