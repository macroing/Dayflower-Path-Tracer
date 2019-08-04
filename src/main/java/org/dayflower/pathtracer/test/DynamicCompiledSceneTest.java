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

import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.scene.compiler.DynamicCompiledScene;

public final class DynamicCompiledSceneTest {
	private DynamicCompiledSceneTest() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final DynamicCompiledScene dynamicCompiledScene = new DynamicCompiledScene();
		
		System.out.println("* IndexOf: " + dynamicCompiledScene.indexOf(new Point2F(0.0F, 1.0F)));
		System.out.println("* Add: " + dynamicCompiledScene.add(new Point2F(0.0F, 1.0F)));
		System.out.println("* IndexOf: " + dynamicCompiledScene.indexOf(new Point2F(0.0F, 1.0F)));
		System.out.println("* Add: " + dynamicCompiledScene.add(new Point2F(0.0F, 1.0F)));
		System.out.println("* Add: " + dynamicCompiledScene.add(new Point2F(1.0F, 2.0F)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new Point2F(0.0F, 1.0F)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new Point2F(1.0F, 2.0F)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new Point2F(0.0F, 1.0F)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new Point2F(0.0F, 1.0F)));
		System.out.println("* Remove: " + dynamicCompiledScene.remove(new Point2F(1.0F, 2.0F)));
	}
}