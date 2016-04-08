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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.shape.Mesh;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;

final class Scenes {
	private Scenes() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static Scene newCornellBoxScene() {
		final Texture texture0 = new SolidTexture(new Color(0.75F, 0.25F, 0.25F));
		final Texture texture1 = new SolidTexture(new Color(0.25F, 0.25F, 0.75F));
		final Texture texture2 = new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture texture3 = new SolidTexture(new Color(0.25F, 0.75F, 0.25F));
		final Texture texture4 = new SolidTexture(Color.WHITE);
		final Texture texture5 = new SolidTexture(Color.BLACK);
		final Texture texture6 = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
		
		final
		Scene scene = new Scene();
		scene.addTexture(texture0);
		scene.addTexture(texture1);
		scene.addTexture(texture2);
		scene.addTexture(texture3);
		scene.addTexture(texture4);
		scene.addTexture(texture5);
		scene.addTexture(texture6);
//		Left wall.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture5, 10000.0F, new Point3(10000.0F + 1.0F, 40.8F, 81.6F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture5, new Point3(-100.0F, 0.0F, -100.0F), new Point3(-100.0F, 0.0F, 400.0F), new Point3(-100.0F, 200.0F, 400.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture5, new Point3(-100.0F, 0.0F, -100.0F), new Point3(-100.0F, 200.0F, 400.0F), new Point3(-100.0F, 200.0F, -100.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
//		Right wall.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture1, texture5, 10000.0F, new Point3(-10000.0F + 99.0F, 40.8F, 81.6F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture5, new Point3(100.0F, 0.0F, -100.0F), new Point3(100.0F, 0.0F, 400.0F), new Point3(100.0F, 200.0F, 400.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture5, new Point3(100.0F, 0.0F, -100.0F), new Point3(100.0F, 200.0F, 400.0F), new Point3(100.0F, 200.0F, -100.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
//		Front wall.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture2, texture5, 10000.0F, new Point3(50.0F, 40.8F, 10000.0F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture2, texture5, new Point3(-200.0F, 0.0F, -200.0F), new Point3(400.0F, 0.0F, -200.0F), new Point3(400.0F, 200.0F, -200.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture2, texture5, new Point3(-200.0F, 0.0F, -200.0F), new Point3(400.0F, 200.0F, -200.0F), new Point3(-200.0F, 200.0F, -200.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
//		Back wall.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture3, texture5, 10000.0F, new Point3(50.0F, 40.8F, -10000.0F + 170.0F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture3, texture5, new Point3(-200.0F, 0.0F, 200.0F), new Point3(400.0F, 0.0F, 200.0F), new Point3(400.0F, 200.0F, 200.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture3, texture5, new Point3(-200.0F, 0.0F, 200.0F), new Point3(400.0F, 200.0F, 200.0F), new Point3(-200.0F, 200.0F, 200.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
//		Floor.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture3, texture5, 10000.0F, new Point3(50.0F, 10000.0F, 81.6F)));
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture6, texture5, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
//		Roof.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture3, texture5, 10000.0F, new Point3(50.0F, -10000.0F + 81.6F, 81.6F)));
//		Sphere 1.
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture4, texture5, 16.5F, new Point3(27.0F, 16.5F, 47.0F)));
//		Sphere 2.
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.REFRACTIVE, texture4, texture5, 16.5F, new Point3(73.0F, 16.5F, 78.0F)));
//		Light.
//		scene.addShape(new Sphere(new Color(12.0F, 12.0F, 12.0F), 0.0F, 0.0F, Material.DIFFUSE, texture5, texture5, 600.0F, new Point3(50.0F, 681.6F - 0.27F, 81.6F)));
		
		return scene;
	}
	
	public static Scene newMaterialShowcaseScene() {
		final Texture texture0 = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
		final Texture texture1 = new SolidTexture(Color.RED);//new CheckerboardTexture(Color.RED, Color.ORANGE);
		final Texture texture2 = new SolidTexture(Color.BLACK);
		
		final
		Scene scene = new Scene();
		scene.addTexture(texture0);
		scene.addTexture(texture1);
		scene.addTexture(texture2);
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture2, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
//		scene.addShape(new Sphere(Color.WHITE, 0.0F, 0.0F, Material.DIFFUSE, texture1, texture2, 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.METAL, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.REFRACTIVE, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 200.0F)));
		
		return scene;
	}
	
	public static Scene newMeshScene() {
		try {
			final Mesh mesh = new Mesh("resources/terrain2.obj");
			
			final Texture texture0 = ImageTexture.load(new File("resources/Texture_2.png"));
//			final Texture texture0 = new SolidTexture(new Color(0.0F, 0.4F, 0.0F));
			final Texture texture1 = new SolidTexture(Color.BLACK);
			
			final List<Triangle> triangles = mesh.getTriangles(texture0, texture1);
			
			final
			Scene scene = new Scene();
			scene.addTexture(texture0);
			scene.addTexture(texture1);
			
			for(final Triangle triangle : triangles) {
				scene.addShape(triangle);//.translateZ(-10.0F));//.translate(50.0F, 30.0F, 120.0F));
			}
			
//			scene.addShape(new Sphere(new Color(0.8F, 0.8F, 1.4F), 0.0F, 0.0F, Material.DIFFUSE, texture1, texture1, 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
			
			return scene;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static Scene newTestScene() {
//		final Texture texture0 = new CheckerboardTexture(new Color(0.75F, 0.25F, 0.25F));
//		final Texture texture1 = new CheckerboardTexture(Color.GRAY.divide(2.0F), 0.05F, 0.05F);
		final Texture texture1 = ImageTexture.load(new File("resources/Texture_2.png"));//, 0.0F, 0.008F, 0.008F);
		final Texture texture2 = new CheckerboardTexture(Color.BLACK);
		final Texture texture3 = new SolidTexture(Color.WHITE);//new CheckerboardTexture(Color.GRAY, Color.WHITE);
//		final Texture texture4 = new SolidTexture(Color.GREEN);
		final Texture texture4 = new CheckerboardTexture(Color.RED, Color.ORANGE);
//		final Texture texture5 = new CheckerboardTexture(Color.CYAN);
		final Texture texture5 = ImageTexture.load(new File("resources/Texture_5.jpg"));
		final Texture texture6 = new CheckerboardTexture(Color.RED);
		final Texture texture7 = new CheckerboardTexture(Color.GRAY.divide(2.0F));
		final Texture texture8 = new SolidTexture(Color.BLACK);
		
		final Texture texture9 = ImageTexture.load(new File("resources/154.JPG"), 0.0F, 0.008F, 0.008F);
		final Texture texture10 = ImageTexture.load(new File("resources/154_norm.JPG"), 0.0F, 0.008F, 0.008F);
		
		final Texture texture11 = ImageTexture.load(new File("resources/154.JPG"));
		final Texture texture12 = ImageTexture.load(new File("resources/154_norm.JPG"));
		
		final Texture texture13 = new CheckerboardTexture(new Color(0.1F, 0.1F, 0.5F), new Color(0.1F, 0.1F, 0.9F), 0.008F, 0.008F, 0.0F);
		
		final
		Scene scene = new Scene();
//		scene.addTexture(texture0);
		scene.addTexture(texture1);
		scene.addTexture(texture2);
		scene.addTexture(texture3);
		scene.addTexture(texture4);
		scene.addTexture(texture5);
		scene.addTexture(texture6);
		scene.addTexture(texture7);
		scene.addTexture(texture8);
		scene.addTexture(texture9);
		scene.addTexture(texture10);
		scene.addTexture(texture11);
		scene.addTexture(texture12);
		scene.addTexture(texture13);
//		Left wall.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture8, 10000.0F, new Point3(10000.0F - 100.0F, 40.8F, 81.6F)));
//		Right wall.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture8, 10000.0F, new Point3(-10000.0F + 199.0F, 40.8F, 81.6F)));
//		Floor.
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture9, texture10, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
//		Roof.
//		scene.addShape(new Sphere(Color.WHITE, 0.0F, 0.0F, Material.DIFFUSE, texture2, texture8, 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
//		scene.addShape(new Sphere(Color.WHITE.multiply(12.0F), 0.0F, 0.0F, Material.DIFFUSE, texture2, texture8, 100.0F, new Point3(50.0F, 200.0F + 81.6F, 81.6F)));
//		Sphere 1.
		scene.addShape(new Sphere(Color.BLACK, 0.3F, 1.0F, Material.SPECULAR, texture3, texture8, 16.5F, new Point3(27.0F, 16.5F, 47.0F)));
//		Sphere 2.
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture4, texture8, 16.5F, new Point3(73.0F, 16.5F, 78.0F)));
//		Sphere 3.
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture8, 16.5F, new Point3(100.0F, 16.5F, 50.0F)));
//		Sphere 4.
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.REFRACTIVE, texture2, texture8, 16.5F, new Point3(73.0F, 16.5F, 30.0F)));
//		Sphere 5.
//		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture6, texture8, 16.5F, new Point3(23.0F, 16.5F, 100.0F)));
//		Triangle 1.
		scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture7, texture8, new Point3(100.0F, 16.0F, 80.0F), new Point3(140.0F, 16.0F, 80.0F), new Point3(120.0F, 48.0F, 80.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
		
		return scene;
	}
}