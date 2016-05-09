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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static Scene newCarScene() {
		try {
			final Texture textureGroundAlbedo = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
			final Texture textureGroundNormalMap = new SolidTexture(Color.BLACK);
			
			final Texture textureCarAlbedo = new SolidTexture(Color.BLACK);
			final Texture textureCarNormalMap = new SolidTexture(Color.BLACK);
			final Texture textureCarWindGlass = new SolidTexture(Color.WHITE);
			final Texture textureCarBodyPaint = new SolidTexture(Color.RED);
			final Texture textureCarChrome = new SolidTexture(Color.GRAY);
			final Texture textureCarDriver = new SolidTexture(new Color(227, 161, 115));
			final Texture textureCarDoorLine = new SolidTexture(Color.RED);
			final Texture textureCarTireBack = new SolidTexture(Color.BLACK);
			final Texture textureCarTireTread = new SolidTexture(Color.BLACK);
			final Texture textureCarTireSidewall = new SolidTexture(Color.BLACK);
			final Texture textureCarMisc = new SolidTexture(Color.GRAY);
			final Texture textureCarMaterial = new SolidTexture(Color.GRAY);
			
			final Map<String, Material> materials = new HashMap<>();
			final Map<String, Texture> textureAlbedos = new HashMap<>();
			
			materials.put("wind_glass", Material.REFRACTIVE);
			materials.put("Body_paint", Material.CLEAR_COAT);
			materials.put("Body_paint0", Material.CLEAR_COAT);
			materials.put("Body_paint1", Material.CLEAR_COAT);
			materials.put("Body_paint2", Material.CLEAR_COAT);
			materials.put("Body_paint3", Material.CLEAR_COAT);
			materials.put("Badging_Chrome", Material.METAL);
			materials.put("Misc_Chrome", Material.METAL);
			materials.put("Misc_Chrome0", Material.METAL);
			materials.put("Misc_Chrome1", Material.METAL);
			materials.put("Misc_Chrome2", Material.METAL);
			materials.put("Misc_Chrome3", Material.METAL);
			materials.put("Misc_Chrome4", Material.METAL);
			materials.put("Driver", Material.DIFFUSE);
			materials.put("DoorLine", Material.METAL);
			materials.put("Tire_Back", Material.DIFFUSE);
			materials.put("Tire_Tread", Material.DIFFUSE);
			materials.put("Tire_Sidewall", Material.DIFFUSE);
			materials.put("Misc", Material.METAL);
			materials.put("Misc0", Material.METAL);
			materials.put("Misc1", Material.METAL);
			materials.put("Misc2", Material.METAL);
			materials.put("Misc3", Material.METAL);
			materials.put("Misc4", Material.METAL);
			materials.put("Material__583", Material.METAL);
			materials.put("Material__586", Material.METAL);
			materials.put("Material__589", Material.METAL);
			materials.put("Material__593", Material.METAL);
			materials.put("Material__594", Material.METAL);
			materials.put("Material__597", Material.METAL);
			materials.put("Material__598", Material.METAL);
			materials.put("Material__600", Material.METAL);
			
			textureAlbedos.put("wind_glass", textureCarWindGlass);
			textureAlbedos.put("Body_paint", textureCarBodyPaint);
			textureAlbedos.put("Body_paint0", textureCarBodyPaint);
			textureAlbedos.put("Body_paint1", textureCarBodyPaint);
			textureAlbedos.put("Body_paint2", textureCarBodyPaint);
			textureAlbedos.put("Body_paint3", textureCarBodyPaint);
			textureAlbedos.put("Badging_Chrome", textureCarChrome);
			textureAlbedos.put("Misc_Chrome", textureCarChrome);
			textureAlbedos.put("Misc_Chrome0", textureCarChrome);
			textureAlbedos.put("Misc_Chrome1", textureCarChrome);
			textureAlbedos.put("Misc_Chrome2", textureCarChrome);
			textureAlbedos.put("Misc_Chrome3", textureCarChrome);
			textureAlbedos.put("Misc_Chrome4", textureCarChrome);
			textureAlbedos.put("Driver", textureCarDriver);
			textureAlbedos.put("DoorLine", textureCarDoorLine);
			textureAlbedos.put("Tire_Back", textureCarTireBack);
			textureAlbedos.put("Tire_Tread", textureCarTireTread);
			textureAlbedos.put("Tire_Sidewall", textureCarTireSidewall);
			textureAlbedos.put("Misc", textureCarMisc);
			textureAlbedos.put("Misc0", textureCarMisc);
			textureAlbedos.put("Misc1", textureCarMisc);
			textureAlbedos.put("Misc2", textureCarMisc);
			textureAlbedos.put("Misc3", textureCarMisc);
			textureAlbedos.put("Misc4", textureCarMisc);
			textureAlbedos.put("Material__583", textureCarMaterial);
			textureAlbedos.put("Material__586", textureCarMaterial);
			textureAlbedos.put("Material__589", textureCarMaterial);
			textureAlbedos.put("Material__593", textureCarMaterial);
			textureAlbedos.put("Material__594", textureCarMaterial);
			textureAlbedos.put("Material__597", textureCarMaterial);
			textureAlbedos.put("Material__598", textureCarMaterial);
			textureAlbedos.put("Material__600", textureCarMaterial);
			
			//			wind_glass
			//			Body_paint
			//			Misc
			//			Misc0
			//			Material__583
			//License
			//			Material__586
			//			Material__589
			//			DoorLine
			//			Badging_Chrome
			//			Misc1
			//			Misc_Chrome
			//			Misc_Chrome0
			//			Misc_Chrome1
			//Black
			//			Body_paint0
			//Bottom
			//Brake_Pads
			//Brake_Disc
			//Brake_Pads0
			//Brake_Disc0
			//			Driver
			//			Material__593
			//			Misc2
			//			Material__594
			//			Misc3
			//			Misc_Chrome2
			//			Body_paint1
			//			Misc4
			//			Misc_Chrome3
			//			Body_paint2
			//Interior
			//			Material__597
			//Interior0
			//			Misc_Chrome4
			//			Material__598
			//			Body_paint3
			//			Tire_Back
			//			Tire_Tread
			//			Tire_Sidewall
			//			Material__600
			//License0
			
			final Mesh mesh = new Mesh("resources/SL500.obj", 100.0F);
			
			final List<Triangle> triangles = mesh.getTriangles(textureCarAlbedo, textureCarNormalMap, materials, textureAlbedos);
			
			final
			Scene scene = new Scene();
			scene.addTexture(textureGroundAlbedo);
			scene.addTexture(textureGroundNormalMap);
			scene.addTexture(textureCarAlbedo);
			scene.addTexture(textureCarNormalMap);
			scene.addTexture(textureCarWindGlass);
			scene.addTexture(textureCarBodyPaint);
			scene.addTexture(textureCarChrome);
			scene.addTexture(textureCarDriver);
			scene.addTexture(textureCarDoorLine);
			scene.addTexture(textureCarTireBack);
			scene.addTexture(textureCarTireTread);
			scene.addTexture(textureCarTireSidewall);
			scene.addTexture(textureCarMisc);
			scene.addTexture(textureCarMaterial);
			
			scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, textureGroundAlbedo, textureGroundNormalMap, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
			
			final Vector3 v = Vector3.z();
			final Vector3 w = Vector3.y();
			
			for(final Triangle triangle : triangles) {
				scene.addShape(triangle.rotate(v, w).translateY(10.0F));
			}
			
			return scene;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
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
	
	public static Scene newGirlScene() {
		try {
			final Texture texture1 = new SolidTexture(new Color(227, 161, 115));
			final Texture texture2 = new SolidTexture(new Color(32, 53, 98));
			final Texture texture3 = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
			final Texture texture4 = new SolidTexture(Color.BLACK);
			final Texture texture5 = new SolidTexture(new Color(216, 192, 120));
			final Texture texture6 = new SolidTexture(Color.WHITE);
			
			final Mesh mesh = new Mesh("resources/aphroditegirl.obj", 100.0F);
			
			final Map<String, Material> materials = new HashMap<>();
			
			materials.put("01___Default", Material.DIFFUSE);
			materials.put("02___Default", Material.METAL);
			materials.put("03___Default", Material.CLEAR_COAT);
			materials.put("04___Default", Material.CLEAR_COAT);
			materials.put("05___Default", Material.DIFFUSE);
			
			final Map<String, Texture> textureAlbedos = new HashMap<>();
			
			textureAlbedos.put("01___Default", texture1);
			textureAlbedos.put("02___Default", texture2);
			textureAlbedos.put("03___Default", texture5);
			textureAlbedos.put("04___Default", texture6);
			textureAlbedos.put("05___Default", texture1);
			
			final List<Triangle> triangles = mesh.getTriangles(texture4, texture4, materials, textureAlbedos);
			
			final
			Scene scene = new Scene();
			scene.addTexture(texture1);
			scene.addTexture(texture2);
			scene.addTexture(texture3);
			scene.addTexture(texture4);
			scene.addTexture(texture5);
			scene.addTexture(texture6);
			scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture3, texture4, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
			
			for(final Triangle triangle : triangles) {
				scene.addShape(triangle.translateY(10.0F));
			}
			
			return scene;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
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
		try {
//			final Texture texture0 = new CheckerboardTexture(new Color(0.75F, 0.25F, 0.25F));
//			final Texture texture1 = new CheckerboardTexture(Color.GRAY.divide(2.0F), 0.05F, 0.05F);
			final Texture texture1 = ImageTexture.load(new File("resources/Texture_2.png"));//, 0.0F, 0.008F, 0.008F);
			final Texture texture2 = new CheckerboardTexture(Color.BLACK);
			final Texture texture3 = new SolidTexture(Color.WHITE);//new CheckerboardTexture(Color.GRAY, Color.WHITE);
//			final Texture texture4 = new SolidTexture(Color.GREEN);
			final Texture texture4 = new CheckerboardTexture(Color.RED, Color.ORANGE);
//			final Texture texture5 = new CheckerboardTexture(Color.CYAN);
			final Texture texture5 = ImageTexture.load(new File("resources/Texture_5.jpg"));
			final Texture texture6 = new CheckerboardTexture(Color.RED);
			final Texture texture7 = new CheckerboardTexture(Color.GRAY.divide(2.0F));
			final Texture texture8 = new SolidTexture(Color.RED);
			
			final Texture texture9 = ImageTexture.load(new File("resources/154.JPG"), 0.0F, 0.008F, 0.008F);
			final Texture texture10 = ImageTexture.load(new File("resources/154_norm.JPG"), 0.0F, 0.008F, 0.008F);
			
			final Texture texture11 = ImageTexture.load(new File("resources/154.JPG"));
			final Texture texture12 = ImageTexture.load(new File("resources/154_norm.JPG"));
			
			final Texture texture13 = new CheckerboardTexture(new Color(0.1F, 0.1F, 0.5F), new Color(0.1F, 0.1F, 0.9F), 0.008F, 0.008F, 0.0F);
			
			final Texture texture14 = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
			final Texture texture15 = new SolidTexture(Color.WHITE);
			
			final Map<String, Material> materials = new HashMap<>();
			final Map<String, Texture> textureAlbedos = new HashMap<>();
			
			//wind_glass
			//Body_paint
			//Misc
			//Misc0
			//Material__583
			//License
			//Material__586
			//Material__589
			//DoorLine
			//Badging_Chrome
			//Misc1
			//Misc_Chrome
			//Misc_Chrome0
			//Misc_Chrome1
			//Black
			//Body_paint0
			//Bottom
			//Brake_Pads
			//Brake_Disc
			//Brake_Pads0
			//Brake_Disc0
			//Driver
			//Material__593
			//Misc2
			//Material__594
			//Misc3
			//Misc_Chrome2
			//Body_paint1
			//Misc4
			//Misc_Chrome3
			//Body_paint2
			//Interior
			//Material__597
			//Interior0
			//Misc_Chrome4
			//Material__598
			//Body_paint3
			//Tire_Back
			//Tire_Tread
			//Tire_Sidewall
			//Material__600
			//License0
			
			final Mesh mesh = new Mesh("resources/SL500.obj", 100.0F);
			
			final List<Triangle> triangles = mesh.getTriangles(texture15, texture8, materials, textureAlbedos);
			
			final
			Scene scene = new Scene();
//			scene.addTexture(texture0);
			scene.addTexture(texture1);
//			scene.addTexture(texture2);
//			scene.addTexture(texture3);
//			scene.addTexture(texture4);
//			scene.addTexture(texture5);
//			scene.addTexture(texture6);
//			scene.addTexture(texture7);
			scene.addTexture(texture8);
//			scene.addTexture(texture9);
//			scene.addTexture(texture10);
//			scene.addTexture(texture11);
//			scene.addTexture(texture12);
//			scene.addTexture(texture13);
			scene.addTexture(texture14);
			scene.addTexture(texture15);
//			Left wall.
//			scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture8, 10000.0F, new Point3(10000.0F - 100.0F, 40.8F, 81.6F)));
//			Right wall.
//			scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture0, texture8, 10000.0F, new Point3(-10000.0F + 199.0F, 40.8F, 81.6F)));
//			Floor.
			scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.DIFFUSE, texture14, texture8, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
//			Roof.
//			scene.addShape(new Sphere(Color.WHITE, 0.0F, 0.0F, Material.DIFFUSE, texture2, texture8, 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
//			scene.addShape(new Sphere(Color.WHITE.multiply(12.0F), 0.0F, 0.0F, Material.DIFFUSE, texture2, texture8, 100.0F, new Point3(50.0F, 200.0F + 81.6F, 81.6F)));
//			Sphere 1.
//			scene.addShape(new Sphere(Color.BLACK, 0.3F, 1.0F, Material.SPECULAR, texture3, texture8, 16.5F, new Point3(27.0F, 16.5F, 47.0F)));
//			Sphere 2.
//			scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture4, texture8, 16.5F, new Point3(73.0F, 16.5F, 78.0F)));
//			Sphere 3.
//			scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture8, 16.5F, new Point3(100.0F, 16.5F, 50.0F)));
//			Sphere 4.
//			scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.REFRACTIVE, texture2, texture8, 16.5F, new Point3(73.0F, 16.5F, 30.0F)));
//			Sphere 5.
//			scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture6, texture8, 16.5F, new Point3(23.0F, 16.5F, 100.0F)));
//			Triangle 1.
//			scene.addShape(new Triangle(Color.BLACK, 0.0F, 0.0F, Material.SPECULAR, texture7, texture8, new Point3(100.0F, 16.0F, 80.0F), new Point3(140.0F, 16.0F, 80.0F), new Point3(120.0F, 48.0F, 80.0F), new Vector3(), new Vector3(), new Vector3(), new Point2(0.0F, 0.0F), new Point2(0.0F, 1.0F), new Point2(1.0F, 0.0F)));
			
			final Vector3 v = Vector3.z();
			final Vector3 w = Vector3.y();
			
			for(final Triangle triangle : triangles) {
				scene.addShape(triangle.rotate(v, w).translateY(10.0F));
			}
			
			return scene;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}