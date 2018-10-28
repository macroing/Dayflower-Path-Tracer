/**
 * Copyright 2015 - 2018 J&#246;rgen Lundgren
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

import static org.dayflower.pathtracer.math.MathF.max;
import static org.dayflower.pathtracer.math.MathF.min;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.shape.Mesh;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.Triangle.Vertex;
import org.dayflower.pathtracer.scene.texture.CheckerboardTexture;
import org.dayflower.pathtracer.scene.texture.FractionalBrownianMotionTexture;
import org.dayflower.pathtracer.scene.texture.ImageTexture;
import org.dayflower.pathtracer.scene.texture.SolidTexture;
import org.dayflower.pathtracer.scene.texture.SurfaceNormalTexture;

final class Scenes {
	private Scenes() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
			case "House_Scene_3":
			case "House_Scene_3.scene":
				return newHouseScene3();
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
			case "Test_Scene":
			case "Test_Scene.scene":
				return newTestScene();
			default:
				return newMaterialShowcaseScene();
		}
	}
	
	public static Scene newCarScene() {
		final Texture textureGroundAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureGroundNormalMap = new SolidTexture(Color.BLACK);
		
		final Texture textureCarAlbedo = new SolidTexture(Color.GRAY);
		final Texture textureCarNormalMap = new SolidTexture(Color.BLACK);
		final Texture textureCarWindGlass = new SolidTexture(Color.WHITE);
		final Texture textureCarBodyPaint = new SolidTexture(Color.RED);
		final Texture textureCarChrome = new SolidTexture(Color.GRAY);
		final Texture textureCarDriver = new SolidTexture(new Color(227, 161, 115));
		final Texture textureCarDoorLine = new SolidTexture(Color.RED);
		final Texture textureCarTireBack = new SolidTexture(new Color(0.1F, 0.1F, 0.1F));
		final Texture textureCarTireTread = new SolidTexture(new Color(0.1F, 0.1F, 0.1F));
		final Texture textureCarTireSidewall = new SolidTexture(new Color(0.1F, 0.1F, 0.1F));
		final Texture textureCarMisc = new SolidTexture(Color.GRAY);
		final Texture textureCarMaterial = new SolidTexture(Color.GRAY);
		final Texture textureCarLicense = new SolidTexture(Color.WHITE);
		final Texture textureCarLicense0 = new SolidTexture(Color.WHITE);
		final Texture textureCarInterior = new SolidTexture(new Color(222, 184, 135));
		final Texture textureCarInterior0 = new SolidTexture(new Color(222, 184, 135));
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureCarAlbedo, textureCarNormalMap);
		
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
		surfaces.put("License", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarLicense, textureCarNormalMap));
		surfaces.put("License0", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarLicense0, textureCarNormalMap));
		surfaces.put("Interior", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarInterior, textureCarNormalMap));
		surfaces.put("Interior0", Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureCarInterior0, textureCarNormalMap));
		
		//			wind_glass
		//			Body_paint
		//			Misc
		//			Misc0
		//			Material__583
		//			License
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
		//			Bottom
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
		//			Interior
		//			Material__597
		//			Interior0
		//			Misc_Chrome4
		//			Material__598
		//			Body_paint3
		//			Tire_Back
		//			Tire_Tread
		//			Tire_Sidewall
		//			Material__600
		//			License0
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> {
//			if(materialName.equals("Body_paint")) {
//				return surfaces.getOrDefault(materialName, surface);
//			}
			
			return surfaces.getOrDefault(materialName, surface);
		}, Dayflower.getModelFilename("SL500.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Car_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormalMap), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		
		final Vector3F w = Vector3F.y();
		final Vector3F v = Vector3F.z();
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.rotate(w, v).translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newCornellBoxScene() {
		final Texture textureAlbedo0 = new SolidTexture(new Color(0.75F, 0.25F, 0.25F));
		final Texture textureAlbedo1 = new SolidTexture(new Color(0.25F, 0.25F, 0.75F));
		final Texture textureAlbedo2 = new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo3 = new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo4 = new FractionalBrownianMotionTexture(new Color(0.05F, 0.05F, 0.05F), Color.WHITE, 0.5F, 0.8F, 16);//new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo5 = new FractionalBrownianMotionTexture(new Color(0.05F, 0.05F, 0.05F), Color.WHITE, 0.5F, 0.8F, 16);//new SolidTexture(new Color(0.75F, 0.75F, 0.75F));
		final Texture textureAlbedo6 = new FractionalBrownianMotionTexture(new Color(0.5F * 0.999F, 1.0F * 0.999F, 0.5F * 0.999F), Color.WHITE, 0.5F, 0.8F, 16);//new SolidTexture(new Color(0.5F * 0.999F, 1.0F * 0.999F, 0.5F * 0.999F));//TODO
		final Texture textureAlbedo7 = new SolidTexture(new Color(1.0F * 0.999F, 1.0F * 0.999F, 1.0F * 0.999F));
		final Texture textureAlbedo8 = new SolidTexture(Color.BLACK);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final
		Scene scene = new Scene("Cornell_Box_Scene");
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.MIRROR, textureAlbedo0, textureNormal), 1.0e4F, new Point3F(1.0e4F + 1.0F, 40.8F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.MIRROR, textureAlbedo1, textureNormal), 1.0e4F, new Point3F(-1.0e4F + 99.0F, 40.8F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.MIRROR, textureAlbedo2, textureNormal), 1.0e4F, new Point3F(50.0F, 40.8F, 1.0e4F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo3, textureNormal), 1.0e4F, new Point3F(50.0F, 40.8F, -1.0e4F + 170.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo4, textureNormal), 1.0e4F, new Point3F(50.0F, 1.0e4F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo5, textureNormal), 1.0e4F, new Point3F(50.0F, -1.0e4F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.PHONG_METAL, textureAlbedo6, textureNormal), 16.5F, new Point3F(27.0F, 16.5F, 47.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.GLASS, textureAlbedo7, textureNormal), 16.5F, new Point3F(73.0F, 16.5F, 78.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(12.0F, 12.0F, 12.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo8, textureNormal), 600.0F, new Point3F(50.0F, 681.6F - 0.27F, 81.6F)));
		
		return scene;
	}
	
	public static Scene newCornellBoxScene2() {
		final Texture textureGroundAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureGroundNormalMap = new SolidTexture(Color.BLACK);
		
		final Texture textureAlbedo0 = new SolidTexture(Color.BLACK);
		final Texture textureAlbedo1 = new SolidTexture(new Color(1.0F, 0.0F, 0.0F));
		
		final Surface surface0 = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo1, textureAlbedo0);
		
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
		scene.addShape(doCreateTriangle(surface0, 0.0F, 1.0F, 0.0F, 500.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 500.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 500.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F));
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormalMap), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		
		return scene;
	}
	
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
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture3, texture4), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture7, texture4), 16.5F, new Point3F(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture7, texture4), 16.5F, new Point3F(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture7, texture4), 16.5F, new Point3F(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture7, texture4), 16.5F, new Point3F(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture7, texture4), 16.5F, new Point3F(20.0F, 16.5F, 200.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newHouseScene() {
		final Texture textureAlbedo = new SolidTexture(Color.WHITE);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		final Texture texture0 = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture texture1 = new CheckerboardTexture(Color.GRAY, Color.WHITE);
		final Texture texture2 = new SolidTexture(Color.GRAY);
		final Texture texture3 = new CheckerboardTexture(Color.RED, Color.WHITE);
		final Texture texture4 = new CheckerboardTexture(Color.GREEN, Color.WHITE);
		final Texture texture5 = new CheckerboardTexture(Color.BLUE, Color.WHITE);
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
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("House_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture0, textureNormal), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
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
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newHouseScene3() {
		final Texture textureFloor = new CheckerboardTexture(Color.GRAY, Color.WHITE);
		final Texture textureGround = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		final Texture textureRoof = new SolidTexture(Color.WHITE);
		final Texture textureWall = new SolidTexture(Color.WHITE);
		
		final Surface surfaceFloor = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, textureFloor, textureNormal);
		final Surface surfaceRoof = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureRoof, textureNormal);
		final Surface surfaceWall = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureWall, textureNormal);
		
		final Triangle[] trianglesWall0 = doCreateRectangleXY(surfaceWall, 0.0F, 0.0F, 500.0F, 50.0F, 0.0F);
		final Triangle[] trianglesWall1 = doCreateRectangleXY(surfaceWall, 0.0F, 50.0F, 200.0F, 100.0F, 0.0F);
		final Triangle[] trianglesWall2 = doCreateRectangleXY(surfaceWall, 300.0F, 50.0F, 500.0F, 100.0F, 0.0F);
		final Triangle[] trianglesWall3 = doCreateRectangleXY(surfaceWall, 0.0F, 100.0F, 500.0F, 200.0F, 0.0F);
		
		final Triangle[] trianglesWall4 = doCreateRectangleXY(surfaceWall, 0.0F, 0.0F, 500.0F, 50.0F, 500.0F);
		final Triangle[] trianglesWall5 = doCreateRectangleXY(surfaceWall, 0.0F, 50.0F, 200.0F, 100.0F, 500.0F);
		final Triangle[] trianglesWall6 = doCreateRectangleXY(surfaceWall, 300.0F, 50.0F, 500.0F, 100.0F, 500.0F);
		final Triangle[] trianglesWall7 = doCreateRectangleXY(surfaceWall, 0.0F, 100.0F, 500.0F, 200.0F, 500.0F);
		
		final Triangle[] trianglesWall8 = doCreateRectangleYZ(surfaceWall, 0.0F, 0.0F, 50.0F, 500.0F, 0.0F);
		final Triangle[] trianglesWall9 = doCreateRectangleYZ(surfaceWall, 50.0F, 0.0F, 100.0F, 200.0F, 0.0F);
		final Triangle[] trianglesWall10 = doCreateRectangleYZ(surfaceWall, 50.0F, 300.0F, 100.0F, 500.0F, 0.0F);
		final Triangle[] trianglesWall11 = doCreateRectangleYZ(surfaceWall, 100.0F, 0.0F, 200.0F, 500.0F, 0.0F);
		
		final Triangle[] trianglesWall12 = doCreateRectangleYZ(surfaceWall, 0.0F, 0.0F, 50.0F, 500.0F, 500.0F);
		final Triangle[] trianglesWall13 = doCreateRectangleYZ(surfaceWall, 50.0F, 0.0F, 100.0F, 200.0F, 500.0F);
		final Triangle[] trianglesWall14 = doCreateRectangleYZ(surfaceWall, 50.0F, 300.0F, 100.0F, 500.0F, 500.0F);
		final Triangle[] trianglesWall15 = doCreateRectangleYZ(surfaceWall, 100.0F, 0.0F, 200.0F, 500.0F, 500.0F);
		
		final Triangle[] triangleFloor = doCreateRectangleXZ(surfaceFloor, 0.0F, 0.0F, 500.0F, 500.0F, 0.2F);
		final Triangle[] triangleRoof = doCreateRectangleXZ(surfaceRoof, 0.0F, 0.0F, 500.0F, 500.0F, 200.0F);
		
		final
		Scene scene = new Scene("House_Scene_3");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(10.0F, 1.0F, 1.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), 10.0F, new Point3F(2.0F, 180.0F, 2.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(1.0F, 10.0F, 1.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), 10.0F, new Point3F(2.0F, 180.0F, 498.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(1.0F, 1.0F, 10.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), 10.0F, new Point3F(498.0F, 180.0F, 2.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(10.0F, 10.0F, 10.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGround, textureNormal), 10.0F, new Point3F(498.0F, 180.0F, 498.0F)));
		scene.addShape(trianglesWall0[0]);
		scene.addShape(trianglesWall0[1]);
		scene.addShape(trianglesWall1[0]);
		scene.addShape(trianglesWall1[1]);
		scene.addShape(trianglesWall2[0]);
		scene.addShape(trianglesWall2[1]);
		scene.addShape(trianglesWall3[0]);
		scene.addShape(trianglesWall3[1]);
		scene.addShape(trianglesWall4[0]);
		scene.addShape(trianglesWall4[1]);
		scene.addShape(trianglesWall5[0]);
		scene.addShape(trianglesWall5[1]);
		scene.addShape(trianglesWall6[0]);
		scene.addShape(trianglesWall6[1]);
		scene.addShape(trianglesWall7[0]);
		scene.addShape(trianglesWall7[1]);
		scene.addShape(trianglesWall8[0]);
		scene.addShape(trianglesWall8[1]);
		scene.addShape(trianglesWall9[0]);
		scene.addShape(trianglesWall9[1]);
		scene.addShape(trianglesWall10[0]);
		scene.addShape(trianglesWall10[1]);
		scene.addShape(trianglesWall11[0]);
		scene.addShape(trianglesWall11[1]);
		scene.addShape(trianglesWall12[0]);
		scene.addShape(trianglesWall12[1]);
		scene.addShape(trianglesWall13[0]);
		scene.addShape(trianglesWall13[1]);
		scene.addShape(trianglesWall14[0]);
		scene.addShape(trianglesWall14[1]);
		scene.addShape(trianglesWall15[0]);
		scene.addShape(trianglesWall15[1]);
		scene.addShape(triangleFloor[0]);
		scene.addShape(triangleFloor[1]);
		scene.addShape(triangleRoof[0]);
		scene.addShape(triangleRoof[1]);
		
		return scene;
	}
	
	public static Scene newMaterialShowcaseScene() {
		final Texture texture0 = new SolidTexture(new Color(135.0F / 255.0F, 206.0F / 255.0F, 235.0F / 255.0F));//new FractionalBrownianMotionTexture(new Color(0.05F, 0.05F, 0.5F), Color.WHITE, 0.5F, 0.05F, 16);//ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2.jpg")), 0.0F, 0.008F, 0.008F);
		final Texture texture1 = new SolidTexture(Color.WHITE);
		final Texture texture2 = new SolidTexture(Color.BLACK);
		final Texture texture3 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2_normal.jpg")), 0.0F, 0.008F, 0.008F);
		final Texture texture4 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2.jpg")));
		final Texture texture5 = ImageTexture.load(new File(Dayflower.getTextureFilename("bricks2_normal.jpg")));
		final Texture texture6 = new CheckerboardTexture(Color.RED, Color.WHITE);
		final Texture texture7 = new SurfaceNormalTexture();
		final Texture texture8 = new FractionalBrownianMotionTexture(new Color(0.05F, 0.05F, 0.05F), Color.WHITE, 0.5F, 0.8F, 16);
		
		final
		Scene scene = new Scene("Material_Showcase_Scene");
//		scene.addShape(new Terrain(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 0.5F, 0.8F, -2.0F, 0.0F, 2));
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.MIRROR, texture0, texture3), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture6, texture2), 16.5F, new Point3F(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 16.5F, new Point3F(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture1, texture2), 16.5F, new Point3F(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture1, texture2), 16.5F, new Point3F(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(Surface.getInstance(new Color(5.0F, 5.0F, 5.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 16.5F, new Point3F(20.0F, 100.0F, 180.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture1, texture2), 16.5F, new Point3F(20.0F, 16.5F, 200.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.GLASS, texture2, texture2), 16.5F, new Point3F(20.0F, 16.5F, 240.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture4, texture5), 16.5F, new Point3F(20.0F, 16.5F, 280.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture7, texture5), 16.5F, new Point3F(20.0F, 16.5F, 320.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture8, texture5), 16.5F, new Point3F(20.0F, 16.5F, 360.0F)));
		
		return scene;
	}
	
	public static Scene newMonkeyScene() {
		final Texture textureGroundAlbedo = new SolidTexture(new Color(135.0F / 255.0F, 206.0F / 255.0F, 235.0F / 255.0F));//new FractionalBrownianMotionTexture(new Color(0.05F, 0.05F, 0.5F), Color.WHITE, 0.5F, 0.05F, 16);//ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureGroundNormal = new SolidTexture(Color.BLACK);
		final Texture textureMonkeyAlbedo = new FractionalBrownianMotionTexture(new Color(0.5F, 0.05F, 0.05F), Color.WHITE, 0.5F, 0.1F, 16);//new SolidTexture(Color.RED);
		final Texture textureMonkeyNormal = new SolidTexture(Color.BLACK);
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, textureMonkeyAlbedo, textureMonkeyNormal);
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surface, Dayflower.getModelFilename("smoothMonkey2.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Monkey_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 1.0F, 4.0F, Material.MIRROR, textureGroundAlbedo, textureGroundNormal), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F), new Point3F(1.0F, 0.0F, 0.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(100.0F));
		}
		
		return scene;
	}
	
	public static Scene newSponzaScene() {
		final Texture textureAlbedo = new SolidTexture(Color.GRAY);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final Material material = Material.LAMBERTIAN_DIFFUSE;
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, material, textureAlbedo, textureNormal);
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surface, Dayflower.getModelFilename("sponza.obj"), 1.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Sponza_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(1.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newTerrainScene() {
		final Texture textureAlbedo = ImageTexture.load(new File(Dayflower.getTextureFilename("Texture_2.png")), 0.0F, 0.008F, 0.008F);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final Material material = Material.LAMBERTIAN_DIFFUSE;
		
		final Surface surface = Surface.getInstance(Color.BLACK, 0.0F, 0.0F, material, textureAlbedo, textureNormal);
		
		final Mesh mesh = Mesh.loadFromOBJModel(materialName -> surface, Dayflower.getModelFilename("terrain2.obj"), 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene("Terrain_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(1.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newTestScene() {
		final Texture textureAlbedoGround = new CheckerboardTexture(Color.GRAY, Color.WHITE, 0.08F, 0.08F);//new SolidTexture(Color.WHITE);
		final Texture textureNormalGround = new SolidTexture(Color.BLACK);
		final Texture textureAlbedoSphere = new FractionalBrownianMotionTexture(new Color(0.5F, 0.05F, 0.05F), Color.WHITE, 0.5F, 0.4F, 16);//new SolidTexture(Color.RED);
		final Texture textureNormalSphere = new SolidTexture(Color.BLACK);
		
		final
		Scene scene = new Scene("Test_Scene");
		scene.addShape(new Plane(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedoGround, textureNormalGround), new Point3F(0.0F, 0.0F, 0.0F), new Point3F(1.0F, 0.0F, 0.0F), new Point3F(0.0F, 0.0F, 1.0F)));
		scene.addShape(new Sphere(Surface.getInstance(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedoSphere, textureNormalSphere), 16.5F, new Point3F(20.0F, 16.5F, 40.0F)));
		
		return scene;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static Triangle doCreateTriangle(final Surface surface, final float textureCoordinateAX, final float textureCoordinateAY, final float positionAX, final float positionAY, final float positionAZ, final float surfaceNormalAX, final float surfaceNormalAY, final float surfaceNormalAZ, final float textureCoordinateBX, final float textureCoordinateBY, final float positionBX, final float positionBY, final float positionBZ, final float surfaceNormalBX, final float surfaceNormalBY, final float surfaceNormalBZ, final float textureCoordinateCX, final float textureCoordinateCY, final float positionCX, final float positionCY, final float positionCZ, final float surfaceNormalCX, final float surfaceNormalCY, final float surfaceNormalCZ) {
		final Vertex a = new Vertex(new Point2F(textureCoordinateAX, textureCoordinateAY), new Point3F(positionAX, positionAY, positionAZ), new Vector3F(surfaceNormalAX, surfaceNormalAY, surfaceNormalAZ));
		final Vertex b = new Vertex(new Point2F(textureCoordinateBX, textureCoordinateBY), new Point3F(positionBX, positionBY, positionBZ), new Vector3F(surfaceNormalBX, surfaceNormalBY, surfaceNormalBZ));
		final Vertex c = new Vertex(new Point2F(textureCoordinateCX, textureCoordinateCY), new Point3F(positionCX, positionCY, positionCZ), new Vector3F(surfaceNormalCX, surfaceNormalCY, surfaceNormalCZ));
		
		return new Triangle(surface, a, b, c);
	}
	
	private static Triangle[] doCreateRectangleXY(final Surface surface, final float x0, final float y0, final float x1, final float y1, final float z) {
		final float minX = min(x0, x1);
		final float minY = min(y0, y1);
		
		final float maxX = max(x0, x1);
		final float maxY = max(y0, y1);
		
		final Point3F position0 = new Point3F(minX, maxY, z);
		final Point3F position1 = new Point3F(minX, minY, z);
		final Point3F position2 = new Point3F(maxX, minY, z);
		final Point3F position3 = new Point3F(maxX, maxY, z);
		
		final Vector3F surfaceNormal0 = Vector3F.normalNormalized(position0, position1, position2);
		final Vector3F surfaceNormal1 = Vector3F.normalNormalized(position2, position3, position0);
		
		final Triangle triangle0 = doCreateTriangle(surface, 0.0F, 0.0F, minX, maxY, z, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z, 0.0F, 1.0F, minX, minY, z, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z, 1.0F, 1.0F, maxX, minY, z, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z);
		final Triangle triangle1 = doCreateTriangle(surface, 1.0F, 1.0F, maxX, minY, z, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z, 1.0F, 0.0F, maxX, maxY, z, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z, 0.0F, 0.0F, minX, maxY, z, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z);
		
		return new Triangle[] {triangle0, triangle1};
	}
	
	private static Triangle[] doCreateRectangleXZ(final Surface surface, final float x0, final float z0, final float x1, final float z1, final float y) {
		final float minX = min(x0, x1);
		final float minZ = min(z0, z1);
		
		final float maxX = max(x0, x1);
		final float maxZ = max(z0, z1);
		
		final Point3F position0 = new Point3F(minX, y, maxZ);
		final Point3F position1 = new Point3F(minX, y, minZ);
		final Point3F position2 = new Point3F(maxX, y, minZ);
		final Point3F position3 = new Point3F(maxX, y, maxZ);
		
		final Vector3F surfaceNormal0 = Vector3F.normalNormalized(position0, position1, position2);
		final Vector3F surfaceNormal1 = Vector3F.normalNormalized(position2, position3, position0);
		
		final Triangle triangle0 = doCreateTriangle(surface, 0.0F, 0.0F, minX, y, maxZ, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z, 0.0F, 1.0F, minX, y, minZ, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z, 1.0F, 1.0F, maxX, y, minZ, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z);
		final Triangle triangle1 = doCreateTriangle(surface, 1.0F, 1.0F, maxX, y, minZ, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z, 1.0F, 0.0F, maxX, y, maxZ, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z, 0.0F, 0.0F, minX, y, maxZ, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z);
		
		return new Triangle[] {triangle0, triangle1};
	}
	
	private static Triangle[] doCreateRectangleYZ(final Surface surface, final float y0, final float z0, final float y1, final float z1, final float x) {
		final float minY = min(y0, y1);
		final float minZ = min(z0, z1);
		
		final float maxY = max(y0, y1);
		final float maxZ = max(z0, z1);
		
		final Point3F position0 = new Point3F(x, minY, maxZ);
		final Point3F position1 = new Point3F(x, minY, minZ);
		final Point3F position2 = new Point3F(x, maxY, minZ);
		final Point3F position3 = new Point3F(x, maxY, maxZ);
		
		final Vector3F surfaceNormal0 = Vector3F.normalNormalized(position0, position1, position2);
		final Vector3F surfaceNormal1 = Vector3F.normalNormalized(position2, position3, position0);
		
		final Triangle triangle0 = doCreateTriangle(surface, 0.0F, 0.0F, x, minY, maxZ, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z, 0.0F, 1.0F, x, minY, minZ, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z, 1.0F, 1.0F, x, maxY, minZ, surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z);
		final Triangle triangle1 = doCreateTriangle(surface, 1.0F, 1.0F, x, maxY, minZ, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z, 1.0F, 0.0F, x, maxY, maxZ, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z, 0.0F, 0.0F, x, minY, maxZ, surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z);
		
		return new Triangle[] {triangle0, triangle1};
	}
}