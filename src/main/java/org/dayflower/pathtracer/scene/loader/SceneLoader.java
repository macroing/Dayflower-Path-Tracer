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
package org.dayflower.pathtracer.scene.loader;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.compiler.CompiledScene;
import org.dayflower.pathtracer.scene.compiler.PrintingSceneCompilerObserver;
import org.dayflower.pathtracer.scene.compiler.SceneCompiler;

/**
 * A class that loads {@link Scene}s and {@link CompiledScene}s.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class SceneLoader {
	private final AtomicReference<String> name;
	private final File directory;
	private final boolean isCompilingExistingScene;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructs a new {@code SceneLoader} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new SceneLoader(new File("."))}.
	 */
	public SceneLoader() {
		this(new File("."));
	}
	
	/**
	 * Constructs a new {@code SceneLoader} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new SceneLoader(directory, false)}.
	 * <p>
	 * If {@code directory} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param directory the root directory to use
	 * @throws NullPointerException thrown if, and only if, {@code directory} is {@code null}
	 */
	public SceneLoader(final File directory) {
		this(directory, false);
	}
	
	/**
	 * Constructs a new {@code SceneLoader} instance.
	 * <p>
	 * Calling this constructor is equivalent to {@code new SceneLoader(directory, isCompilingExistingScene, "Material_Showcase_Scene")}.
	 * <p>
	 * If {@code directory} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param directory the root directory to use
	 * @param isCompilingExistingScene {@code true} if, and only if, compilation should be performed if the scene has already been compiled, {@code false} otherwise
	 * @throws NullPointerException thrown if, and only if, {@code directory} is {@code null}
	 */
	public SceneLoader(final File directory, final boolean isCompilingExistingScene) {
		this(directory, isCompilingExistingScene, "Material_Showcase_Scene");
	}
	
	/**
	 * Constructs a new {@code SceneLoader} instance.
	 * <p>
	 * If either {@code directory} or {@code name} are {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param directory the root directory to use
	 * @param isCompilingExistingScene {@code true} if, and only if, compilation should be performed if the scene has already been compiled, {@code false} otherwise
	 * @param name the name of the scene
	 * @throws NullPointerException thrown if, and only if, either {@code directory} or {@code name} are {@code null}
	 */
	public SceneLoader(final File directory, final boolean isCompilingExistingScene, final String name) {
		this.name = new AtomicReference<>(Objects.requireNonNull(name, "name == null"));
		this.directory = Objects.requireNonNull(directory, "directory == null");
		this.isCompilingExistingScene = isCompilingExistingScene;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns a {@link CompiledScene} given the name provided by {@code getName()}.
	 * 
	 * @return a {@code CompiledScene} given the name provided by {@code getName()}
	 */
	public CompiledScene loadCompiledScene() {
		final Scene scene = loadScene();
		
		final File sceneFile = Scenes.getSceneFile(getDirectory(), scene);
		
		if(!sceneFile.isFile() || isCompilingExistingScene()) {
			final
			SceneCompiler sceneCompiler = new SceneCompiler();
			sceneCompiler.addSceneCompilerObserver(new PrintingSceneCompilerObserver());
			
			final
			CompiledScene compiledScene = sceneCompiler.compile(scene);
			compiledScene.write(sceneFile);
			
			return compiledScene;
		}
		
		return CompiledScene.read(sceneFile);
	}
	
	/**
	 * Returns a {@code File} with the root directory.
	 * 
	 * @return a {@code File} with the root directory
	 */
	public File getDirectory() {
		return this.directory;
	}
	
	/**
	 * Returns a {@link Scene} given the name provided by {@code getName()}.
	 * 
	 * @return a {@code Scene} given the name provided by {@code getName()}
	 */
	public Scene loadScene() {
		return Scenes.getSceneByName(getDirectory(), getName());
	}
	
	/**
	 * Returns the scene name.
	 * 
	 * @return the scene name
	 */
	public String getName() {
		return this.name.get();
	}
	
	/**
	 * Returns {@code true} if, and only if, compilation of existing scene is enabled, {@code false} otherwise.
	 * 
	 * @return {@code true} if, and only if, compilation of existing scene is enabled, {@code false} otherwise
	 */
	public boolean isCompilingExistingScene() {
		return this.isCompilingExistingScene;
	}
	
	/**
	 * Sets the scene name.
	 * <p>
	 * If {@code name} is {@code null}, a {@code NullPointerException} will be thrown.
	 * 
	 * @param name the new scene name
	 * @throws NullPointerException thrown if, and only if, {@code name} is {@code null}
	 */
	public void setName(final String name) {
		this.name.set(Objects.requireNonNull(name, "name == null"));
	}
}