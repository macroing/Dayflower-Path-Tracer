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

import org.dayflower.pathtracer.kernel.CompiledScene;
import org.dayflower.pathtracer.scene.Camera;
import org.dayflower.pathtracer.scene.Scene;

/**
 * A class that compiles {@link Scene}s so they can be used by Dayflower - Path Tracer.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SceneCompiler {
	private SceneCompiler() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * The main entry-point for this class.
	 * 
	 * @param args these arguments are not used
	 */
	public static void main(final String[] args) {
		final Camera camera = new Camera();
		
		doCompileAndWriteScene(camera, Scenes.newCarScene());
		doCompileAndWriteScene(camera, Scenes.newCornellBoxScene());
		doCompileAndWriteScene(camera, Scenes.newCornellBoxScene2());
		doCompileAndWriteScene(camera, Scenes.newGirlScene());
		doCompileAndWriteScene(camera, Scenes.newHouseScene());
//		doCompileAndWriteScene(camera, Scenes.newHouseScene2());
		doCompileAndWriteScene(camera, Scenes.newMaterialShowcaseScene());
		doCompileAndWriteScene(camera, Scenes.newMonkeyScene());
//		doCompileAndWriteScene(camera, Scenes.newSponzaScene());
		doCompileAndWriteScene(camera, Scenes.newTerrainScene());
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void doCompileAndWriteScene(final Camera camera, final Scene scene) {
		System.out.printf("Compiling and writing scene \"%s\"...%n", scene.getName());
		
		final long currentTimeMillis0 = System.currentTimeMillis();
		
		final
		CompiledScene compiledScene = CompiledScene.compile(camera, scene);
		compiledScene.write();
		
		final long currentTimeMillis1 = System.currentTimeMillis();
		final long elapsedTimeMillis = currentTimeMillis1 - currentTimeMillis0;
		
		System.out.printf("Scene \"%s\" successfully compiled and written in %s milliseconds.%n", scene.getName(), Long.toString(elapsedTimeMillis));
	}
}