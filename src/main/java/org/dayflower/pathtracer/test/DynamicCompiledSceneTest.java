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

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.compiler.DynamicCompiledScene;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ConstantTexture;

public final class DynamicCompiledSceneTest {
	private DynamicCompiledSceneTest() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final DynamicCompiledScene dynamicCompiledScene = new DynamicCompiledScene();
		
		System.out.println("* IndexOf: " + dynamicCompiledScene.indexOf(new ConstantTexture(Color.BLACK)));
		System.out.println("* Add: " + dynamicCompiledScene.add(new ConstantTexture(Color.BLACK)));
		System.out.println("* IndexOf: " + dynamicCompiledScene.indexOf(new ConstantTexture(Color.BLACK)));
		System.out.println("* Add: " + dynamicCompiledScene.add(new ConstantTexture(Color.BLACK)));
		System.out.println("* Add: " + dynamicCompiledScene.add(new CheckerboardTexture(Color.RED, Color.BLACK)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new CheckerboardTexture(Color.RED, Color.BLACK)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new ConstantTexture(Color.BLACK)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new CheckerboardTexture(Color.RED, Color.BLACK)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new ConstantTexture(Color.BLACK)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new ConstantTexture(Color.BLACK)));
	}
}