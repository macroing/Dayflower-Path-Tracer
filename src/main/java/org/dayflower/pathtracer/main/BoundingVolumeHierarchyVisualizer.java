package org.dayflower.pathtracer.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.kernel.CompiledScene;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.util.Strings;

public final class BoundingVolumeHierarchyVisualizer {
	private BoundingVolumeHierarchyVisualizer() {
		
	}
	
	public static void main(final String[] args) {
		final Camera camera = new Camera();
		
		final Scene scene = Scenes.newMeshScene();
		
		final CompiledScene compiledScene = CompiledScene.compile(camera, scene);
		
		final float[] boundingVolumeHierarchy = compiledScene.getBoundingVolumeHierarchy();
		
		final StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < boundingVolumeHierarchy.length; i += 16) {
			final int type = (int)(boundingVolumeHierarchy[i + 0]);
			final int depth = (int)(boundingVolumeHierarchy[i + 2]);
			final int next = (int)(boundingVolumeHierarchy[i + 3]);
			final int left = type == 1 ? (int)(boundingVolumeHierarchy[i + 10]) : -1;
			final int right = type == 1 ? (int)(boundingVolumeHierarchy[i + 11]) : -1;
			
			final String indentation = Strings.repeat("\t", depth);
			
			if(type == 1) {
				stringBuilder.append(String.format("%06d: %sNext=%06d, Left=%06d, Right=%06d%n", Integer.valueOf(i), indentation, Integer.valueOf(next), Integer.valueOf(left), Integer.valueOf(right)));
			} else if(type == 2) {
				stringBuilder.append(String.format("%06d: %sNext=%06d%n", Integer.valueOf(i), indentation, Integer.valueOf(next)));
			}
		}
		
		try {
			Files.write(new File("BoundingVolumeHierarchy.txt").toPath(), stringBuilder.toString().getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
}