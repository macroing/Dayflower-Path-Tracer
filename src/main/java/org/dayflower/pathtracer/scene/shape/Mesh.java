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
package org.dayflower.pathtracer.scene.shape;

import java.io.IOException;
import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.scene.IndexedModel;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.OBJModel;
import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Texture;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.Vector4;
import org.dayflower.pathtracer.scene.Vertex;

//TODO: Add Javadocs!
public final class Mesh {
	private final List<Integer> indices;
	private final List<String> materials;
	private final List<Vertex> vertices;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public Mesh(final String fileName) throws IOException {
		this(fileName, 1.0F);
	}
	
//	TODO: Add Javadocs!
	public Mesh(final String fileName, final float scale) throws IOException {
		final OBJModel oBJModel = new OBJModel(fileName, scale);
		
		final IndexedModel indexedModel = oBJModel.toIndexedModel();
		
		this.vertices = new ArrayList<>();
		
		for(int i = 0; i < indexedModel.getPositions().size(); i++) {
			final String material = indexedModel.getMaterials().get(i);
			
			final Vector4 normal = indexedModel.getNormals().get(i);
			final Vector4 position = indexedModel.getPositions().get(i);
			final Vector4 textureCoordinate = indexedModel.getTextureCoordinates().get(i);
			
			final Vertex vertex = new Vertex(material, normal, position, textureCoordinate);
			
			this.vertices.add(vertex);
		}
		
		this.indices = indexedModel.getIndices();
		this.materials = indexedModel.getMaterials();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public int getIndexAt(final int index) {
		return this.indices.get(index).intValue();
	}
	
//	TODO: Add Javadocs!
	public int getIndexCount() {
		return this.indices.size();
	}
	
//	TODO: Add Javadocs!
	public int getVertexCount() {
		return this.vertices.size();
	}
	
//	TODO: Add Javadocs!
	public List<Triangle> getTriangles(final Texture textureAlbedo, final Texture textureNormal) {
		return getTriangles(textureAlbedo, textureNormal, new HashMap<>(), new HashMap<>());
	}
	
//	TODO: Add Javadocs!
	public List<Triangle> getTriangles(final Texture textureAlbedo, final Texture textureNormal, final Map<String, Material> materials, final Map<String, Texture> textureAlbedos) {
		final List<Triangle> triangles = new ArrayList<>();
		
		for(int i = 0; i < getIndexCount(); i += 3) {
			final Vertex vertex0 = getVertexAt(getIndexAt(i + 0));
			final Vertex vertex1 = getVertexAt(getIndexAt(i + 1));
			final Vertex vertex2 = getVertexAt(getIndexAt(i + 2));
			
			final String material = vertex0.getMaterial();
			
			final Material material0 = materials.get(material);
			final Material material1 = material0 != null ? material0 : Material.METAL;
			
			final Texture textureAlbedo0 = textureAlbedos.get(material);
			final Texture textureAlbedo1 = textureAlbedo0 != null ? textureAlbedo0 : textureAlbedo;
			
			final Vector4 position0 = vertex0.getPosition();
			final Vector4 position1 = vertex1.getPosition();
			final Vector4 position2 = vertex2.getPosition();
			
			final Vector4 surfaceNormal0 = vertex0.getNormal();
			final Vector4 surfaceNormal1 = vertex1.getNormal();
			final Vector4 surfaceNormal2 = vertex2.getNormal();
			
			final Vector4 textureCoordinates0 = vertex0.getTextureCoordinates();
			final Vector4 textureCoordinates1 = vertex1.getTextureCoordinates();
			final Vector4 textureCoordinates2 = vertex2.getTextureCoordinates();
			
			final Point3 a = new Point3(position0.x, position0.y, position0.z);
			final Point3 b = new Point3(position1.x, position1.y, position1.z);
			final Point3 c = new Point3(position2.x, position2.y, position2.z);
			
			final Vector3 surfaceNormalA = new Vector3(surfaceNormal0.x, surfaceNormal0.y, surfaceNormal0.z);
			final Vector3 surfaceNormalB = new Vector3(surfaceNormal1.x, surfaceNormal1.y, surfaceNormal1.z);
			final Vector3 surfaceNormalC = new Vector3(surfaceNormal2.x, surfaceNormal2.y, surfaceNormal2.z);
			
			final Point2 uVA = new Point2(textureCoordinates0.x, textureCoordinates0.y);
			final Point2 uVB = new Point2(textureCoordinates1.x, textureCoordinates1.y);
			final Point2 uVC = new Point2(textureCoordinates2.x, textureCoordinates2.y);
			
			final Triangle triangle = new Triangle(Color.BLACK, 0.0F, 0.0F, material1, textureAlbedo1, textureNormal, a, b, c, surfaceNormalA, surfaceNormalB, surfaceNormalC, uVA, uVB, uVC);
			
			triangles.add(triangle);
		}
		
		return triangles;
	}
	
//	TODO: Add Javadocs!
	public Vertex getVertexAt(final int index) {
		return this.vertices.get(index);
	}
}