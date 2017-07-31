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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.shape.Mesh;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.Triangle.Vertex;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;

//TODO: Add Javadocs.
public final class Scenes {
	private Scenes() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs.
	public static Scene getSceneByName(final String name) {
		switch(name) {
			case "Car_Scene":
			case "Car_Scene.scene":
				return newCarScene();
			case "Cornell_Box_Scene":
			case "Cornell_Box_Scene.scene":
				return newCornellBoxScene();
			case "Cornell_Box_Scene_2":
			case "Cornell_Box_Scene_2.scene":
				return newCornellBoxScene2();
			case "Girl_Scene":
			case "Girl_Scene.scene":
				return newGirlScene();
			case "House_Scene":
			case "House_Scene.scene":
				return newHouseScene();
			case "House_Scene_2":
			case "House_Scene_2.scene":
				return newHouseScene2();
			case "Material_Showcase_Scene":
			case "Material_Showcase_Scene.scene":
				return newMaterialShowcaseScene();
			case "Monkey_Scene":
			case "Monkey_Scene.scene":
				return newMonkeyScene();
			case "Sponza_Scene":
			case "Sponza_Scene.scene":
				return newSponzaScene();
			case "Terrain_Scene":
			case "Terrain_Scene.scene":
				return newTerrainScene();
			default:
				return newMaterialShowcaseScene();
		}
	}
	
//	TODO: Add Javadocs.
	public static Scene newCarScene() {
		final Texture textureGroundAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
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
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarAlbedo, textureCarNormalMap);
		
		final Map<String, Surface> surfaces = new HashMap<>();
		
		surfaces.put("wind_glass", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.GLASS, textureCarWindGlass, textureCarNormalMap));
		surfaces.put("Body_paint", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, textureCarBodyPaint, textureCarNormalMap));
		surfaces.put("Body_paint0", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, textureCarBodyPaint, textureCarNormalMap));
		surfaces.put("Body_paint1", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, textureCarBodyPaint, textureCarNormalMap));
		surfaces.put("Body_paint2", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, textureCarBodyPaint, textureCarNormalMap));
		surfaces.put("Body_paint3", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, textureCarBodyPaint, textureCarNormalMap));
		surfaces.put("Badging_Chrome", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Misc_Chrome", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Misc_Chrome0", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Misc_Chrome1", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Misc_Chrome2", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Misc_Chrome3", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Misc_Chrome4", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarChrome, textureCarNormalMap));
		surfaces.put("Driver", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarDriver, textureCarNormalMap));
		surfaces.put("DoorLine", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarDoorLine, textureCarNormalMap));
		surfaces.put("Tire_Back", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarTireBack, textureCarNormalMap));
		surfaces.put("Tire_Tread", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarTireTread, textureCarNormalMap));
		surfaces.put("Tire_Sidewall", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarTireSidewall, textureCarNormalMap));
		surfaces.put("Misc", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMisc, textureCarNormalMap));
		surfaces.put("Misc0", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMisc, textureCarNormalMap));
		surfaces.put("Misc1", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMisc, textureCarNormalMap));
		surfaces.put("Misc2", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMisc, textureCarNormalMap));
		surfaces.put("Misc3", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMisc, textureCarNormalMap));
		surfaces.put("Misc4", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMisc, textureCarNormalMap));
		surfaces.put("Material__583", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__586", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__589", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__593", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__594", Surface.getInstance(Color.WHITE, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__597", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__598", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		surfaces.put("Material__600", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarMaterial, textureCarNormalMap));
		
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
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surfaces.getOrDefault(materialName, surface), Dayflower.getModelFilename("SL500.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Car_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormalMap), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
		
		final Vector3 v = Vector3.z();
		final Vector3 w = Vector3.y();
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.rotate(v, w).translateY(10.0F));
		}
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newCornellBoxScene() {
		final Texture textureAlbedo0 = new SolidTexture(new Color(0.75F, 0.25F, 0.25F));
		final Texture textureAlbedo1 = new SolidTexture(new Color(0.25F, 0.25F, 0.75F));
		final Texture textureAlbedo2 = new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo3 = new SolidTexture(new Color(0.5F, 0.5F, 0.5F));
		final Texture textureAlbedo4 = new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo5 = new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo6 = new SolidTexture(new Color(0.5F * 0.999F, 1.0F * 0.999F, 0.5F * 0.999F));
		final Texture textureAlbedo7 = new SolidTexture(new Color(1.0F * 0.999F, 1.0F * 0.999F, 1.0F * 0.999F));
		final Texture textureAlbedo8 = new SolidTexture(Color.BLACK);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final
		Scene scene = new Scene("Cornell_Box_Scene");
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, textureAlbedo0, textureNormal), 1.0e4F, new Point3(1.0e4F + 1.0F, 40.8F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo1, textureNormal), 1.0e4F, new Point3(-1.0e4F + 99.0F, 40.8F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, textureAlbedo2, textureNormal), 1.0e4F, new Point3(50.0F, 40.8F, 1.0e4F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo3, textureNormal), 1.0e4F, new Point3(50.0F, 40.8F, -1.0e4F + 170.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo4, textureNormal), 1.0e4F, new Point3(50.0F, 1.0e4F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo5, textureNormal), 1.0e4F, new Point3(50.0F, -1.0e4F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo6, textureNormal), 16.5F, new Point3(27.0F, 16.5F, 47.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.GLASS, textureAlbedo7, textureNormal), 16.5F, new Point3(73.0F, 16.5F, 78.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(12.0F, 12.0F, 12.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo8, textureNormal), 600.0F, new Point3(50.0F, 681.6F - 0.27F, 81.6F)));
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newCornellBoxScene2() {
		final Texture textureGroundAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureGroundNormalMap = new SolidTexture(Color.BLACK);
		
		final Texture textureAlbedo0 = new SolidTexture(Color.BLACK);
		final Texture textureAlbedo1 = new SolidTexture(new Color(1.0F, 0.0F, 0.0F));
		
		final Surface surface0 = Surface.getInstance(new Color(12.0F, 12.0F, 12.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo0, textureAlbedo0);
		final Surface surface1 = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo1, textureAlbedo0);
		
		/*
		 * doCreateTriangle(...):
		 * 
		 * For each Vertex, A, B and C, do the following...
		 * - Texture Coordinates (X, Y)
		 * - Position (X, Y, Z)
		 * - Surface Normal (X, Y, Z)
		 */
		
		final
		Scene scene = new Scene("Cornell_Box_Scene_2");
		scene.addShape(doCreateTriangle(surface1, 0.0F, 1.0F, 0.0F, 500.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 500.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 500.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F));
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormalMap), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newGirlScene() {
		final Texture texture1 = new SolidTexture(new Color(227, 161, 115));
		final Texture texture2 = new CheckerboardTexture(Color.BLACK, Color.WHITE, 0.05F, 0.05F, 0.0F);//new SolidTexture(new Color(32, 53, 98));
		final Texture texture3 = new CheckerboardTexture(Color.GRAY, Color.WHITE, 0.005F, 0.005F, 0.0F);//ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture texture4 = new SolidTexture(Color.BLACK);
		final Texture texture5 = new SolidTexture(new Color(216, 192, 120));
		final Texture texture6 = new SolidTexture(Color.WHITE);
		final Texture texture7 = new SolidTexture(Color.GRAY);
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture4, texture4);
		
		final Map<String, Surface> surfaces = new HashMap<>();
		
		surfaces.put("01___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture4));
		surfaces.put("02___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture2, texture4));
		surfaces.put("03___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture5, texture4));
		surfaces.put("04___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture6, texture4));
		surfaces.put("05___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture4));
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surfaces.getOrDefault(materialName, surface), Dayflower.getModelFilename("aphroditegirl.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Girl_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture3, texture4), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 200.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newHouseScene() {
		final Texture textureAlbedo = new SolidTexture(Color.WHITE);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		final Texture texture0 = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture texture1 = new CheckerboardTexture(Color.GRAY, Color.WHITE);
		final Texture texture2 = new SolidTexture(Color.GRAY);
		final Texture texture3 = new SolidTexture(Color.RED);
		final Texture texture4 = new SolidTexture(Color.GREEN);
		final Texture texture5 = new SolidTexture(Color.BLUE);
		final Texture texture6 = new SolidTexture(Color.GRAY);
		final Texture texture7 = new SolidTexture(Color.ORANGE);
		final Texture texture8 = new SolidTexture(Color.ORANGE);
		final Texture texture9 = new SolidTexture(Color.ORANGE);
		final Texture texture10 = new SolidTexture(Color.GRAY);
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal);
		
		final Map<String, Surface> surfaces = new HashMap<>();
		
		surfaces.put("floor_texture", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, textureNormal));
		surfaces.put("wire_115115115", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture2, textureNormal));
		surfaces.put("texture_1", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture3, textureNormal));
		surfaces.put("texture_2", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture4, textureNormal));
		surfaces.put("03___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture5, textureNormal));
		surfaces.put("crome", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture6, textureNormal));
		surfaces.put("table_wood_texture", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture7, textureNormal));
		surfaces.put("sopha_wood_texture", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture8, textureNormal));
		surfaces.put("20___Default", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture9, textureNormal));
		surfaces.put("double_sopha_wood_right_texture", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture10, textureNormal));
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surfaces.getOrDefault(materialName, surface), Dayflower.getModelFilename("house interior.obj"), 1.0F);
//		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surfaces.getOrDefault(materialName, surface), Dayflower.getModelFilename("trail.obj"), 1.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("House_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture0, textureNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newHouseScene2() {
		final Texture textureAlbedo = new SolidTexture(Color.WHITE);
		final Texture textureGround = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal);
		
		final Map<String, Surface> surfaces = new HashMap<>();
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surfaces.getOrDefault(materialName, surface), Dayflower.getModelFilename("luxury house interior.obj"), 1.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("House_Scene_2");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newMaterialShowcaseScene() {
		final Texture texture0 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2.jpg")), 0.0F, 0.008F, 0.008F);//ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture texture1 = new SolidTexture(Color.RED);//new CheckerboardTexture(Color.RED, Color.ORANGE);
		final Texture texture2 = new SolidTexture(Color.BLACK);
		final Texture texture3 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2_normal.jpg")), 0.0F, 0.008F, 0.008F);
		final Texture texture4 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2.jpg")));
		final Texture texture5 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2_normal.jpg")));
		
		final
		Scene scene = new Scene("Material_Showcase_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture0, texture3), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
//		scene.addShape(new Sphere(Surface.getInstance(Color.WHITE, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 200.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.GLASS, texture2, texture2), 16.5F, new Point3(20.0F, 16.5F, 240.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture4, texture5), 16.5F, new Point3(20.0F, 16.5F, 280.0F)));
//		scene.addShape(new Sphere(Surface.getInstance(new Color(100.0F, 100.0F, 100.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture2, texture2), 100.0F, new Point3(20.0F, 500.0F, 160.0F)));
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newMonkeyScene() {
		final Texture textureGroundAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureGroundNormal = new SolidTexture(Color.BLACK);
		final Texture textureMonkeyAlbedo = new SolidTexture(Color.RED);
		final Texture textureMonkeyNormal = new SolidTexture(Color.BLACK);
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureMonkeyAlbedo, textureMonkeyNormal);
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surface, Dayflower.getModelFilename("smoothMonkey2.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Monkey_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F), new Point3(1.0F, 0.0F, 0.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(100.0F));
		}
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newSponzaScene() {
		final Texture textureAlbedo = new SolidTexture(Color.GRAY);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final Material material = Material.LAMBERTIAN_DIFFUSE;
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, material, textureAlbedo, textureNormal);
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surface, Dayflower.getModelFilename("sponza.obj"), 1.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Sponza_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
//	TODO: Add Javadocs.
	public static Scene newTerrainScene() {
		final Texture textureAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final Material material = Material.LAMBERTIAN_DIFFUSE;
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, material, textureAlbedo, textureNormal);
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surface, Dayflower.getModelFilename("terrain2.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Terrain_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Triangle doCreateTriangle(final Surface surface, final float textureCoordinateAX, final float textureCoordinateAY, final float positionAX, final float positionAY, final float positionAZ, final float surfaceNormalAX, final float surfaceNormalAY, final float surfaceNormalAZ, final float textureCoordinateBX, final float textureCoordinateBY, final float positionBX, final float positionBY, final float positionBZ, final float surfaceNormalBX, final float surfaceNormalBY, final float surfaceNormalBZ, final float textureCoordinateCX, final float textureCoordinateCY, final float positionCX, final float positionCY, final float positionCZ, final float surfaceNormalCX, final float surfaceNormalCY, final float surfaceNormalCZ) {
		final Vertex a = new Vertex(new Point2(textureCoordinateAX, textureCoordinateAY), new Point3(positionAX, positionAY, positionAZ), new Vector3(surfaceNormalAX, surfaceNormalAY, surfaceNormalAZ));
		final Vertex b = new Vertex(new Point2(textureCoordinateBX, textureCoordinateBY), new Point3(positionBX, positionBY, positionBZ), new Vector3(surfaceNormalBX, surfaceNormalBY, surfaceNormalBZ));
		final Vertex c = new Vertex(new Point2(textureCoordinateCX, textureCoordinateCY), new Point3(positionCX, positionCY, positionCZ), new Vector3(surfaceNormalCX, surfaceNormalCY, surfaceNormalCZ));
		
		return new Triangle(surface, a, b, c);
	}
}