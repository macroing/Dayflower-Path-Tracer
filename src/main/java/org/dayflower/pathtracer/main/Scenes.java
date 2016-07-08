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
import org.dayflower.pathtracer.scene.shape.Mesh.MeshConfigurator;
import org.dayflower.pathtracer.scene.shape.Plane;
import org.dayflower.pathtracer.scene.shape.Sphere;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.Triangle.Vertex;
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
		Scene scene = new Scene("Car_Scene");
		scene.addShape(new Plane(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureGroundAlbedo, textureGroundNormalMap), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		final Vector3 v = Vector3.z();
		final Vector3 w = Vector3.y();
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.rotate(v, w).translateY(10.0F));
		}
		
		return scene;
	}
	
	/*
	 *  scene.addShape(Sphere.newInstance(1.e5D, SpecularMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(1.e5D + 1.0D, 40.8D, 81.6D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.75D, 0.25D, 0.25D))));
		scene.addShape(Sphere.newInstance(1.e5D, DiffuseMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(-1.e5D + 99.0D, 40.8D, 81.6D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.25D, 0.25D, 0.75D))));
		scene.addShape(Sphere.newInstance(1.e5D, SpecularMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(50.0D, 40.8D, 1.e5D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.75D, 0.75D, 0.75D))));
		scene.addShape(Sphere.newInstance(1.e5D, DiffuseMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(50.0D, 40.8D, -1.e5D + 170.0D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.5D, 0.5D, 0.5D))));
		scene.addShape(Sphere.newInstance(1.e5D, DiffuseMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(50.0D, 1.e5D, 81.6D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.75D, 0.75D, 0.75D))));
		scene.addShape(Sphere.newInstance(1.e5D, DiffuseMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(50.0D, -1.e5D + 81.6D, 81.6D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.75D, 0.75D, 0.75D))));
		scene.addShape(Sphere.newInstance(16.5D, DiffuseMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(27.0D, 16.5D, 47.0D), SimpleTexture.newInstance("./resources/texture2.png")));//SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(0.5D * 0.999D, 1.0D * 0.999D, 0.5D * 0.999D))));
		scene.addShape(Sphere.newInstance(16.5D, RefractiveMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.black()), Point.valueOf(73.0D, 16.5D, 78.0D), SolidTexture.newInstance(1, 1, RGBSpectrum.valueOf(1.0D * 0.999D, 1.0D * 0.999D, 1.0D * 0.999D))));
		scene.addShape(Sphere.newInstance(600.0D, DiffuseMaterial.newInstance(Material.REFRACTIVE_INDEX_GLASS, RGBSpectrum.valueOf(12.0D, 12.0D, 12.0D)), Point.valueOf(50.0D, 681.6D - 0.27D, 81.6D), SolidTexture.newInstance(1, 1, RGBSpectrum.black())));
	 */
	
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
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, textureAlbedo0, textureNormal), 1.0e4F, new Point3(1.0e4F + 1.0F, 40.8F, 81.6F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo1, textureNormal), 1.0e4F, new Point3(-1.0e4F + 99.0F, 40.8F, 81.6F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, textureAlbedo2, textureNormal), 1.0e4F, new Point3(50.0F, 40.8F, 1.0e4F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo3, textureNormal), 1.0e4F, new Point3(50.0F, 40.8F, -1.0e4F + 170.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo4, textureNormal), 1.0e4F, new Point3(50.0F, 1.0e4F, 81.6F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo5, textureNormal), 1.0e4F, new Point3(50.0F, -1.0e4F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo6, textureNormal), 16.5F, new Point3(27.0F, 16.5F, 47.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.GLASS, textureAlbedo7, textureNormal), 16.5F, new Point3(73.0F, 16.5F, 78.0F)));
		scene.addShape(new Sphere(new Surface(new Color(12.0F, 12.0F, 12.0F), 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo8, textureNormal), 600.0F, new Point3(50.0F, 681.6F - 0.27F, 81.6F)));
		
		return scene;
	}
	
	public static Scene newGirlScene() {
		final Texture texture1 = new SolidTexture(new Color(227, 161, 115));
		final Texture texture2 = new CheckerboardTexture(Color.BLACK, Color.WHITE, 0.05F, 0.05F, 0.0F);//new SolidTexture(new Color(32, 53, 98));
		final Texture texture3 = new CheckerboardTexture(Color.GRAY, Color.WHITE, 0.005F, 0.005F, 0.0F);//ImageTexture.load(new File("resources/Texture_2.png"), 0.0F, 0.008F, 0.008F);
		final Texture texture4 = new SolidTexture(Color.BLACK);
		final Texture texture5 = new SolidTexture(new Color(216, 192, 120));
		final Texture texture6 = new SolidTexture(Color.WHITE);
		final Texture texture7 = new SolidTexture(Color.RED);
		
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
		Scene scene = new Scene("Girl_Scene");
		scene.addShape(new Plane(new Surface(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture3, texture4), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture7, texture4), 16.5F, new Point3(20.0F, 16.5F, 200.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
	
	public static Scene newHouseScene() {
		final Texture textureAlbedo = new SolidTexture(Color.WHITE);
		final Texture textureNormal = new SolidTexture(Color.BLACK);
		
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
				return Material.LAMBERTIAN_DIFFUSE;
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
		
		final Mesh mesh = Mesh.loadFromOBJModel(meshConfigurator, "resources/trail.obj", 1.0F);
		
		final List<Triangle> triangles = mesh.getTriangles();
		
		final Scene scene = new Scene("House_Scene");
		
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
		Scene scene = new Scene("Material_Showcase_Scene");
		scene.addShape(new Plane(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture0, texture2), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
//		scene.addShape(new Sphere(new Surface(Color.WHITE, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 100000.0F, new Point3(50.0F, -100000.0F + 81.6F, 81.6F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.CLEAR_COAT, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 40.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 80.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.PHONG_METAL, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 120.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.GLASS, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 160.0F)));
		scene.addShape(new Sphere(new Surface(Color.BLACK, 0.0F, 0.0F, Material.MIRROR, texture1, texture2), 16.5F, new Point3(20.0F, 16.5F, 200.0F)));
		
		return scene;
	}
	
	public static Scene newPBSPScene() {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("resources/q1map.pbsp"))))) {
			final float[] planes = new float[dataInputStream.readShort() * 4];
			
			for(int i = 0; i < planes.length; i += 4) {
				planes[i + 0] = dataInputStream.readByte() / 127.0F;
				planes[i + 1] = dataInputStream.readByte() / 127.0F;
				planes[i + 2] = dataInputStream.readByte() / 127.0F;
				planes[i + 3] = dataInputStream.readShort();
			}
			
			final float[] vertices = new float[dataInputStream.readShort() * 3];
			
			for(int i = 0; i < vertices.length; i += 3) {
				vertices[i + 0] = dataInputStream.readShort();
				vertices[i + 1] = dataInputStream.readShort();
				vertices[i + 2] = dataInputStream.readShort();
			}
			
			final Material material = Material.LAMBERTIAN_DIFFUSE;
			
			final Texture textureAlbedo = new SolidTexture(Color.GRAY);
			final Texture textureNormal = new SolidTexture(Color.BLACK);
			
			final List<Triangle> triangles = new ArrayList<>();
			
			for(int i = 0; i + 11 < vertices.length; i += 12) {
				final Point3 p0 = new Point3(vertices[i + 11], vertices[i + 10], vertices[i + 9]);
				final Point3 p1 = new Point3(vertices[i +  8], vertices[i +  7], vertices[i + 6]);
				final Point3 p2 = new Point3(vertices[i +  5], vertices[i +  4], vertices[i + 3]);
				final Point3 p3 = new Point3(vertices[i +  2], vertices[i +  1], vertices[i + 0]);
				
				final Vector3 surfaceNormal0 = Vector3.normalNormalized(p0, p1, p2);
				final Vector3 surfaceNormal1 = Vector3.normalNormalized(p0, p2, p3);
				
				final Vertex vertexA0 = new Vertex(new Point2(), p0, "", surfaceNormal0);
				final Vertex vertexB0 = new Vertex(new Point2(), p1, "", surfaceNormal0);
				final Vertex vertexC0 = new Vertex(new Point2(), p2, "", surfaceNormal0);
				
				final Vertex vertexA1 = new Vertex(new Point2(), p0, "", surfaceNormal1);
				final Vertex vertexB1 = new Vertex(new Point2(), p2, "", surfaceNormal1);
				final Vertex vertexC1 = new Vertex(new Point2(), p3, "", surfaceNormal1);
				
				triangles.add(new Triangle(new Surface(Color.BLACK, 0.0F, 0.0F, material, textureAlbedo, textureNormal), vertexA0, vertexB0, vertexC0));
				triangles.add(new Triangle(new Surface(Color.BLACK, 0.0F, 0.0F, material, textureAlbedo, textureNormal), vertexA1, vertexB1, vertexC1));
			}
			
			final
			Scene scene = new Scene("PBSP_Scene");
			scene.addShape(new Plane(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
			
			for(final Triangle triangle : triangles) {
				scene.addShape(triangle);
			}
			
			return scene;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
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
		Scene scene = new Scene("Terrain_Scene");
		scene.addShape(new Plane(new Surface(Color.BLACK, 0.0F, 0.0F, Material.LAMBERTIAN_DIFFUSE, textureAlbedo, textureNormal), new Point3(0.0F, 0.0F, 0.0F), new Point3(1.0F, 0.0F, 0.0F), new Point3(0.0F, 0.0F, 1.0F)));
		
		for(final Triangle triangle : triangles) {
			scene.addShape(triangle.translateY(10.0F));
		}
		
		return scene;
	}
}