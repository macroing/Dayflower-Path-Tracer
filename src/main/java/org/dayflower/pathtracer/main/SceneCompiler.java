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

import java.lang.reflect.Field;//TODO: Add Javadocs.

import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.kernel.CompiledScene;
import org.dayflower.pathtracer.scene.Scene;

//TODO: Add Javadocs.
public final class SceneCompiler {
	private SceneCompiler() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static void main(final String[] args) {
		final Camera camera = new Camera();
		
		doCompileAndWriteScene(camera, Scenes.newCarScene());
		doCompileAndWriteScene(camera, Scenes.newCornellBoxScene());
		doCompileAndWriteScene(camera, Scenes.newGirlScene());
		doCompileAndWriteScene(camera, Scenes.newHouseScene());
		doCompileAndWriteScene(camera, Scenes.newMaterialShowcaseScene());
		doCompileAndWriteScene(camera, Scenes.newTerrainScene());
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void doCompileAndWriteScene(final Camera camera, final Scene scene) {
		System.out.println("Compiling and writing scene " + scene.getName() + "...");
		
		final
		CompiledScene compiledScene = CompiledScene.compile(camera, scene);
		compiledScene.write();
		
		System.out.println("Scene " + scene.getName() + " successfully compiled and written.");
	}
}