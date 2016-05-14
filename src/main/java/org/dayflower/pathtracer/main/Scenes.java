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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Scene;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.shape.Mesh;
import org.dayflower.pathtracer.scene.shape.Mesh.MeshConfigurator;
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
		
		materials.put("wind_glass", Material.GLASS);
		materials.put("Body_paint", Material.CLEAR_COAT);
		materials.put("Body_paint0", Material.CLEAR_COAT);
		materials.put("Body_paint1", Material.CLEAR_COAT);
		materials.put("Body_paint2", Material.CLEAR_COAT);
		materials.put("Body_paint3", Material.CLEAR_COAT);
		materials.put("Badging_Chrome", Material.PHONG_METAL);
		materials.put("Misc_Chrome", Material.PHONG_METAL);
		materials.put("Misc_Chrome0", Material.PHONG_METAL);
		materials.put("Misc_Chrome1", Material.PHONG_METAL);
		materials.put("Misc_Chrome2", Material.PHONG_METAL);
		materials.put("Misc_Chrome3", Material.PHONG_METAL);
		materials.put("Misc_Chrome4", Material.PHONG_METAL);
		materials.put("Driver", Material.LAMBERTIAN_DIFFUSE);
		materials.put("DoorLine", Material.PHONG_METAL);
		materials.put("Tire_Back", Material.LAMBERTIAN_DIFFUSE);
		materials.put("Tire_Tread", Material.LAMBERTIAN_DIFFUSE);
		materials.put("Tire_Sidewall", Material.LAMBERTIAN_DIFFUSE);
		materials.put("Misc", Material.PHONG_METAL);
		materials.put("Misc0", Material.PHONG_METAL);
		materials.put("Misc1", Material.PHONG_METAL);
		materials.put("Misc2", Material.PHONG_METAL);
		materials.put("Misc3", Material.PHONG_METAL);
		materials.put("Misc4", Material.PHONG_METAL);
		materials.put("Material__583", Material.PHONG_METAL);
		materials.put("Material__586", Material.PHONG_METAL);
		materials.put("Material__589", Material.PHONG_METAL);
		materials.put("Material__593", Material.PHONG_METAL);
		materials.put("Material__594", Material.PHONG_METAL);
		materials.put("Material__597", Material.PHONG_METAL);
		materials.put("Material__598", Material.PHONG_METAL);
		materials.put("Material__600", Material.PHONG_METAL);
		
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
		
		final MeshConfigurator meshConfigurator = new MeshConfigurator() {
			@Override
			public Color getEmission(final String materialName) {
				return Color.BLACK;
			}
			
			@Override
			public float getPerlinNoiseAmount(final String materialName) {
				return 0.0F;
			}
			
			@Override
			public float getPerlinNoiseScale(final String materialName) {
				return 0.0F;
			}
			
			@Override
			public Material getMaterial(final String materialName) {
				return materials.getOrDefault(materialName, Material.LAMBERTIAN_DIFFUSE);
			}
			
			@Override
			public Texture getTextureAlbedo(final String materialName) {
				return textureAlbedos.getOrDefault(materialName, textureCarAlbedo);
			}
			
			@Override
			public Texture getTextureNormal(final String materialName) {
				return textureCarNormalMap;
			}
		};
		
		final Mesh mesh = Mesh.loadFromOBJModel(meshConfigurator, "resources/SL500.obj", 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
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
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormalMap, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		final Vector3 v = Vector3.z();
		final Vector3 w = Vector3.y();
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.rotate(v, w).translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newGirlScene() {
		final Texture texture1 = new SolidTexture(new Color(227, 161, 115));
		final Texture texture2 = new CheckerboardTexture(Color.BLACK, Color.WHITE, 0.05F, 0.05F, 0.0F);//new SolidTexture(new Color(32, 53, 98));
		final Texture texture3 = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
		final Texture texture4 = new SolidTexture(Color.BLACK);
		final Texture texture5 = new SolidTexture(new Color(216, 192, 120));
		final Texture texture6 = new SolidTexture(Color.WHITE);
		
		final Map<String, Material> materials = new HashMap<>();
		
		materials.put("01___Default", Material.LAMBERTIAN_DIFFUSE);
		materials.put("02___Default", Material.PHONG_METAL);
		materials.put("03___Default", Material.CLEAR_COAT);
		materials.put("04___Default", Material.CLEAR_COAT);
		materials.put("05___Default", Material.LAMBERTIAN_DIFFUSE);
		
		final Map<String, Texture> textureAlbedos = new HashMap<>();
		
		textureAlbedos.put("01___Default", texture1);
		textureAlbedos.put("02___Default", texture2);
		textureAlbedos.put("03___Default", texture5);
		textureAlbedos.put("04___Default", texture6);
		textureAlbedos.put("05___Default", texture1);
		
		final MeshConfigurator meshConfigurator = new MeshConfigurator() {
			@Override
			public Color getEmission(final String materialName) {
				return Color.BLACK;
			}
			
			@Override
			public float getPerlinNoiseAmount(final String materialName) {
				return 0.0F;
			}
			
			@Override
			public float getPerlinNoiseScale(final String materialName) {
				return 0.0F;
			}
			
			@Override
			public Material getMaterial(final String materialName) {
				return materials.getOrDefault(materialName, Material.LAMBERTIAN_DIFFUSE);
			}
			
			@Override
			public Texture getTextureAlbedo(final String materialName) {
				return textureAlbedos.getOrDefault(materialName, texture4);
			}
			
			@Override
			public Texture getTextureNormal(final String materialName) {
				return texture4;
			}
		};
		
		final Mesh mesh = Mesh.loadFromOBJModel(meshConfigurator, "resources/aphroditegirl.obj", 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene();
		scene.addTexture(texture1);
		scene.addTexture(texture2);
		scene.addTexture(texture3);
		scene.addTexture(texture4);
		scene.addTexture(texture5);
		scene.addTexture(texture6);
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture3, texture4, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
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
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture0, texture2, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
//		scene.addShape(new Sphere(Color.WHITE, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2, 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture1, texture2, 16.5F, new Point3(20.0F, 16.5F, 200.0F)));
		
		return scene;
	}
	
	public static Scene newTerrainScene() {
		final Texture textureAlbedo = ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
		final Material material = Material.LAMBERTIAN_DIFFUSE;
		
		final MeshConfigurator meshConfigurator = new MeshConfigurator() {
			@Override
			public Color getEmission(final String materialName) {
				return Color.BLACK;
			}
			
			@Override
			public float getPerlinNoiseAmount(final String materialName) {
				return 0.0F;
			}
			
			@Override
			public float getPerlinNoiseScale(final String materialName) {
				return 0.0F;
			}
			
			@Override
			public Material getMaterial(final String materialName) {
				return material;
			}
			
			@Override
			public Texture getTextureAlbedo(final String materialName) {
				return textureAlbedo;
			}
			
			@Override
			public Texture getTextureNormal(final String materialName) {
				return textureNormal;
			}
		};
		
		final Mesh mesh = Mesh.loadFromOBJModel(meshConfigurator, "resources/terrain2.obj", 100.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final
		Scene scene = new Scene();
		scene.addTexture(textureAlbedo);
		scene.addTexture(textureNormal);
		scene.addShape(new Plane(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal, new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
}