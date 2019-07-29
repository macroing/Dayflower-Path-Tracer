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
package org.dayflower.pathtracer.scene.wavefront;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import org.dayflower.pathtracer.color.Color;
import org.dayflower.pathtracer.math.Point2F;
import org.dayflower.pathtracer.math.Point3F;
import org.dayflower.pathtracer.math.Vector3F;
import org.dayflower.pathtracer.scene.Material;
import org.dayflower.pathtracer.scene.Primitive;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.shape.Mesh;
import org.dayflower.pathtracer.scene.shape.Triangle;
import org.dayflower.pathtracer.scene.shape.Triangle.Vertex;
import org.dayflower.pathtracer.scene.texture.ConstantTexture;

/**
 * This {@code ObjectLoader} class is used for loading Wavefront Object (.obj) models into {@link Primitive} instances.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class ObjectLoader {
	private ObjectLoader() {
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(file, 1.0F)}.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representing the file to load from
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final File file) {
		return load(file, 1.0F);
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(file, scale, (groupName, materialName) -> new Surface(Material.LAMBERTIAN_DIFFUSE, new ConstantTexture(Color.GRAY), new ConstantTexture(Color.BLACK), new ConstantTexture(Color.BLACK)))}.
	 * <p>
	 * If {@code file} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representing the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, {@code file} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final File file, final float scale) {
		return load(file, scale, (groupName, materialName) -> new Surface(Material.LAMBERTIAN_DIFFUSE, new ConstantTexture(Color.GRAY), new ConstantTexture(Color.BLACK), new ConstantTexture(Color.BLACK)));
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(file, scale, surfaceMapper, 0.0F, 0.0F, 0.0F)}.
	 * <p>
	 * If either {@code file} or {@code surfaceMapper} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representing the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @param surfaceMapper a {@code BiFunction} that maps a group name or a material name into a specific {@link Surface} instance
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, either {@code file} or {@code surfaceMapper} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final File file, final float scale, final BiFunction<String, String, Surface> surfaceMapper) {
		return load(file, scale, surfaceMapper, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * If either {@code file} or {@code surfaceMapper} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representing the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @param surfaceMapper a {@code BiFunction} that maps a group name or a material name into a specific {@link Surface} instance
	 * @param translateX use this to translate the Wavefront Object model in the X-direction
	 * @param translateY use this to translate the Wavefront Object model in the Y-direction
	 * @param translateZ use this to translate the Wavefront Object model in the Z-direction
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, either {@code file} or {@code surfaceMapper} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final File file, final float scale, final BiFunction<String, String, Surface> surfaceMapper, final float translateX, final float translateY, final float translateZ) {
		try {
			final List<Primitive> primitives = new ArrayList<>();
			
			final ObjectModel objectModel = new ObjectModel(Objects.requireNonNull(file, "file == null"), scale);
			
			final IndexedModel indexedModel = objectModel.toIndexedModel();
			
			final List<Integer> indices = indexedModel.getIndices();
			final List<Point2F> textureCoordinates = indexedModel.getTextureCoordinates();
			final List<Point3F> positions = indexedModel.getPositions();
			final List<String> groups = indexedModel.getGroups();
			final List<String> materials = indexedModel.getMaterials();
			final List<Triangle> triangles = new ArrayList<>();
			final List<Vector3F> normals = indexedModel.getNormals();
			
			String previousGroup = "";
			String previousMaterial = "";
			
			for(int i = 0; i < indices.size(); i += 3) {
				final int indexA = indices.get(i + 0).intValue();
				final int indexB = indices.get(i + 1).intValue();
				final int indexC = indices.get(i + 2).intValue();
				
				final String currentGroup = groups.get(indexA);
				final String currentMaterial = materials.get(indexA);
				
				if(!previousGroup.equals(currentGroup)) {
					if(triangles.size() > 0) {
						primitives.add(new Primitive(new Mesh(triangles), surfaceMapper.apply(previousGroup, previousMaterial)));
						
						triangles.clear();
					}
					
					previousGroup = currentGroup;
				}
				
				if(!previousMaterial.equals(currentMaterial)) {
					previousMaterial = currentMaterial;
				}
				
				final Vertex a = new Vertex(textureCoordinates.get(indexA), positions.get(indexA), normals.get(indexA));
				final Vertex b = new Vertex(textureCoordinates.get(indexB), positions.get(indexB), normals.get(indexB));
				final Vertex c = new Vertex(textureCoordinates.get(indexC), positions.get(indexC), normals.get(indexC));
				
				Triangle triangle = new Triangle(a, b, c);
				
				if(Float.compare(translateX, 0.0F) != 0) {
					triangle = triangle.translateX(translateX);
				}
				
				if(Float.compare(translateY, 0.0F) != 0) {
					triangle = triangle.translateY(translateY);
				}
				
				if(Float.compare(translateZ, 0.0F) != 0) {
					triangle = triangle.translateZ(translateZ);
				}
				
				triangles.add(triangle);
			}
			
			primitives.add(new Primitive(new Mesh(triangles), surfaceMapper.apply(previousGroup, previousMaterial)));
			
			return primitives;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(file, scale, (groupName, materialName) -> surface)}.
	 * <p>
	 * If either {@code file} or {@code surface} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param file a {@code File} representing the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @param surface the {@link Surface} to use on the Wavefront Object model
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, either {@code file} or {@code surface} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final File file, final float scale, final Surface surface) {
		Objects.requireNonNull(surface, "surface == null");
		
		return load(file, scale, (groupName, materialName) -> surface);
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(filename, 1.0F)}.
	 * <p>
	 * If {@code filename} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename a {@code String} representing the filename of the file to load from
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, {@code filename} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final String filename) {
		return load(filename, 1.0F);
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(filename, scale, (groupName, materialName) -> new Surface(Material.LAMBERTIAN_DIFFUSE, new ConstantTexture(Color.GRAY), new ConstantTexture(Color.BLACK), new ConstantTexture(Color.BLACK)))}.
	 * <p>
	 * If {@code filename} is {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename a {@code String} representing the filename of the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, {@code filename} is {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final String filename, final float scale) {
		return load(filename, scale, (groupName, materialName) -> new Surface(Material.LAMBERTIAN_DIFFUSE, new ConstantTexture(Color.GRAY), new ConstantTexture(Color.BLACK), new ConstantTexture(Color.BLACK)));
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(filename, scale, surfaceMapper, 0.0F, 0.0F, 0.0F)}.
	 * <p>
	 * If either {@code filename} or {@code surfaceMapper} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename a {@code String} representing the filename of the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @param surfaceMapper a {@code BiFunction} that maps a group name or a material name into a specific {@link Surface} instance
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, either {@code filename} or {@code surfaceMapper} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final String filename, final float scale, final BiFunction<String, String, Surface> surfaceMapper) {
		return load(filename, scale, surfaceMapper, 0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * If either {@code filename} or {@code surfaceMapper} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename a {@code String} representing the filename of the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @param surfaceMapper a {@code BiFunction} that maps a group name or a material name into a specific {@link Surface} instance
	 * @param translateX use this to translate the Wavefront Object model in the X-direction
	 * @param translateY use this to translate the Wavefront Object model in the Y-direction
	 * @param translateZ use this to translate the Wavefront Object model in the Z-direction
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, either {@code filename} or {@code surfaceMapper} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final String filename, final float scale, final BiFunction<String, String, Surface> surfaceMapper, final float translateX, final float translateY, final float translateZ) {
		return load(new File(Objects.requireNonNull(filename, "filename == null")), scale, surfaceMapper, translateX, translateY, translateZ);
	}
	
	/**
	 * Loads a Wavefront Object (.obj) model.
	 * <p>
	 * Returns a {@code List} of {@link Primitive}s that represents the loaded Wavefront Object model.
	 * <p>
	 * Calling this method is equivalent to {@code load(filename, scale, (groupName, materialName) -> surface)}.
	 * <p>
	 * If either {@code filename} or {@code surface} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O error occurs, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param filename a {@code String} representing the filename of the file to load from
	 * @param scale the scale to use on the Wavefront Object model
	 * @param surface the {@link Surface} to use on the Wavefront Object model
	 * @return a {@code List} of {@code Primitive}s that represents the loaded Wavefront Object model
	 * @throws NullPointerException thrown if, and only if, either {@code filename} or {@code surface} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O error occurs
	 */
	public static List<Primitive> load(final String filename, final float scale, final Surface surface) {
		Objects.requireNonNull(surface, "surface == null");
		
		return load(new File(Objects.requireNonNull(filename, "filename == null")), scale, (groupName, materialName) -> surface);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class IndexedModel {
		private final List<Integer> indices = new ArrayList<>();
		private final List<Point2F> textureCoordinates = new ArrayList<>();
		private final List<Point3F> positions = new ArrayList<>();
		private final List<String> groups = new ArrayList<>();
		private final List<String> materials = new ArrayList<>();
		private final List<Vector3F> normals = new ArrayList<>();
		private final List<Vector3F> tangents = new ArrayList<>();
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public IndexedModel() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public List<Integer> getIndices() {
			return this.indices;
		}
		
		public List<String> getGroups() {
			return this.groups;
		}
		
		public List<String> getMaterials() {
			return this.materials;
		}
		
		public List<Vector3F> getNormals() {
			return this.normals;
		}
		
		public List<Point3F> getPositions() {
			return this.positions;
		}
		
		public List<Vector3F> getTangents() {
			return this.tangents;
		}
		
		public List<Point2F> getTextureCoordinates() {
			return this.textureCoordinates;
		}
		
		public void calculateNormals() {
			for(int i = 0; i < this.indices.size(); i += 3) {
				final int index0 = this.indices.get(i + 0).intValue();
				final int index1 = this.indices.get(i + 1).intValue();
				final int index2 = this.indices.get(i + 2).intValue();
				
				final Vector3F edge0 = Vector3F.direction(this.positions.get(index0), this.positions.get(index1));
				final Vector3F edge1 = Vector3F.direction(this.positions.get(index0), this.positions.get(index2));
				final Vector3F normal = edge0.crossProduct(edge1).normalize();
				
				this.normals.set(index0, this.normals.get(index0).add(normal));
				this.normals.set(index1, this.normals.get(index1).add(normal));
				this.normals.set(index2, this.normals.get(index2).add(normal));
			}
			
			for(int i = 0; i < this.normals.size(); i++) {
				this.normals.set(i, this.normals.get(i).normalize());
			}
		}
		
		public void calculateTangents() {
			for(int i = 0; i < this.indices.size(); i += 3) {
				final int index0 = this.indices.get(i + 0).intValue();
				final int index1 = this.indices.get(i + 1).intValue();
				final int index2 = this.indices.get(i + 2).intValue();
				
				final Vector3F edge0 = Vector3F.direction(this.positions.get(index0), this.positions.get(index1));
				final Vector3F edge1 = Vector3F.direction(this.positions.get(index0), this.positions.get(index2));
				
				final float deltaU0 = this.textureCoordinates.get(index1).x - this.textureCoordinates.get(index0).x;
				final float deltaV0 = this.textureCoordinates.get(index1).y - this.textureCoordinates.get(index0).y;
				final float deltaU1 = this.textureCoordinates.get(index2).x - this.textureCoordinates.get(index0).x;
				final float deltaV1 = this.textureCoordinates.get(index2).y - this.textureCoordinates.get(index0).y;
				
				final float dividend = (deltaU0 * deltaV1 - deltaU1 * deltaV0);
				final float fraction = dividend == 0.0F ? 0.0F : 1.0F / dividend;
				
				final float x = fraction * (deltaV1 * edge0.x - deltaV0 * edge1.x);
				final float y = fraction * (deltaV1 * edge0.y - deltaV0 * edge1.y);
				final float z = fraction * (deltaV1 * edge0.y - deltaV0 * edge1.y);
				
				final Vector3F tangent = new Vector3F(x, y, z);
				
				this.tangents.set(index0, this.tangents.get(index0).add(tangent));
				this.tangents.set(index1, this.tangents.get(index1).add(tangent));
				this.tangents.set(index2, this.tangents.get(index2).add(tangent));
			}
			
			for(int i = 0; i < this.tangents.size(); i++) {
				this.tangents.set(i, this.tangents.get(i).normalize());
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class ObjectModel {
		private final AtomicBoolean hasNormals = new AtomicBoolean();
		private final AtomicBoolean hasTextureCoordinates = new AtomicBoolean();
		private final List<ObjectIndex> indices = new ArrayList<>();
		private final List<Point2F> textureCoordinates = new ArrayList<>();
		private final List<Point3F> positions = new ArrayList<>();
		private final List<String> groups = new ArrayList<>();
		private final List<String> materials = new ArrayList<>();
		private final List<Vector3F> normals = new ArrayList<>();
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public ObjectModel(final File file, final float scale) throws IOException {
			String group = "";
			String material = "";
			
			try(final BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
				for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
					final String[] tokens = doRemoveEmptyStrings(line.split(" "));
					
					if(tokens.length == 0 || tokens[0].equals("#")) {
						continue;
					} else if(tokens[0].equals("g")) {
						group = tokens[1];
					} else if(tokens[0].equals("usemtl")) {
						material = tokens[1];
					} else if(tokens[0].equals("v")) {
						this.positions.add(new Point3F(Float.valueOf(tokens[1]).floatValue() * scale, Float.valueOf(tokens[2]).floatValue() * scale, Float.valueOf(tokens[3]).floatValue() * scale));
					} else if(tokens[0].equals("vt")) {
						this.textureCoordinates.add(new Point2F(Float.valueOf(tokens[1]).floatValue() * scale, 1.0F * scale - Float.valueOf(tokens[2]).floatValue() * scale));
					} else if(tokens[0].equals("vn")) {
						this.normals.add(new Vector3F(Float.valueOf(tokens[1]).floatValue(), Float.valueOf(tokens[2]).floatValue(), Float.valueOf(tokens[3]).floatValue()));
					} else if(tokens[0].equals("f")) {
						for(int i = 0; i < tokens.length - 3; i++) {
							this.indices.add(doParseObjectIndex(tokens[1 + 0]));
							this.indices.add(doParseObjectIndex(tokens[2 + i]));
							this.indices.add(doParseObjectIndex(tokens[3 + i]));
							
							this.groups.add(group);
							this.groups.add(group);
							this.groups.add(group);
							
							this.materials.add(material);
							this.materials.add(material);
							this.materials.add(material);
						}
					}
				}
			}
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public IndexedModel toIndexedModel() {
			final IndexedModel indexedModel0 = new IndexedModel();
			final IndexedModel indexedModel1 = new IndexedModel();
			
			final Map<ObjectIndex, Integer> modelVertexIndices = new HashMap<>();
			final Map<Integer, Integer> normalModelIndices0 = new HashMap<>();
			final Map<Integer, Integer> normalModelIndices1 = new HashMap<>();
			
			for(int i = 0; i < this.indices.size(); i++) {
				final ObjectIndex currentObjectIndex = this.indices.get(i);
				
				final Point2F currentTextureCoordinate = this.hasTextureCoordinates.get() ? this.textureCoordinates.get(currentObjectIndex.getTextureCoordinateIndex()) : new Point2F();
				
				final Point3F currentPosition = this.positions.get(currentObjectIndex.getVertexIndex());
				
				final String currentGroup = this.groups.get(i);
				
				final String currentMaterial = this.materials.get(i);
				
				final Vector3F currentNormal = this.hasNormals.get() ? this.normals.get(currentObjectIndex.getNormalIndex()) : new Vector3F();
				
				Integer modelVertexIndex = modelVertexIndices.get(currentObjectIndex);
				
				if(modelVertexIndex == null) {
					modelVertexIndex = Integer.valueOf(indexedModel0.getPositions().size());
					
					modelVertexIndices.put(currentObjectIndex, modelVertexIndex);
					
					indexedModel0.getTextureCoordinates().add(currentTextureCoordinate);
					indexedModel0.getPositions().add(currentPosition);
					indexedModel0.getGroups().add(currentGroup);
					indexedModel0.getMaterials().add(currentMaterial);
					
					if(this.hasNormals.get()) {
						indexedModel0.getNormals().add(currentNormal);
					}
				}
				
				Integer normalModelIndex = normalModelIndices0.get(Integer.valueOf(currentObjectIndex.getVertexIndex()));
				
				if(normalModelIndex == null) {
					normalModelIndex = Integer.valueOf(indexedModel1.getPositions().size());
					
					normalModelIndices0.put(Integer.valueOf(currentObjectIndex.getVertexIndex()), normalModelIndex);
					
					indexedModel1.getTextureCoordinates().add(currentTextureCoordinate);
					indexedModel1.getPositions().add(currentPosition);
					indexedModel1.getGroups().add(currentGroup);
					indexedModel1.getMaterials().add(currentMaterial);
					indexedModel1.getNormals().add(currentNormal);
					indexedModel1.getTangents().add(new Vector3F());
				}
				
				indexedModel0.getIndices().add(modelVertexIndex);
				indexedModel1.getIndices().add(normalModelIndex);
				
				normalModelIndices1.put(modelVertexIndex, normalModelIndex);
			}
			
			if(!this.hasNormals.get()) {
				indexedModel1.calculateNormals();
				
				for(int i = 0; i < indexedModel0.getPositions().size(); i++) {
					indexedModel0.getNormals().add(indexedModel1.getNormals().get(normalModelIndices1.get(Integer.valueOf(i)).intValue()));
				}
			}
			
			indexedModel1.calculateTangents();
			
			for(int i = 0; i < indexedModel0.getPositions().size(); i++) {
				indexedModel0.getTangents().add(indexedModel1.getTangents().get(normalModelIndices1.get(Integer.valueOf(i)).intValue()));
			}
			
			return indexedModel0;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private ObjectIndex doParseObjectIndex(final String token) {
			final String[] values = token.split("/");
			
			final
			ObjectIndex objectIndex = new ObjectIndex();
			objectIndex.setVertexIndex(Integer.parseInt(values[0]) - 1);
			
			if(values.length > 1) {
				if(!values[1].isEmpty()) {
					this.hasTextureCoordinates.set(true);
					
					objectIndex.setTextureCoordinateIndex(Integer.parseInt(values[1]) - 1);
				}
				
				if(values.length > 2) {
					this.hasNormals.set(true);
					
					objectIndex.setNormalIndex(Integer.parseInt(values[2]) - 1);
				}
			}
			
			return objectIndex;
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private static String[] doRemoveEmptyStrings(final String[] oldLines) {
			final List<String> newLines = new ArrayList<>();
			
			for(int i = 0; i < oldLines.length; i++) {
				if(!oldLines[i].equals("")) {
					newLines.add(oldLines[i]);
				}
			}
			
			return newLines.toArray(new String[newLines.size()]);
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private static final class ObjectIndex {
			private int normalIndex;
			private int textureCoordinateIndex;
			private int vertexIndex;
			
			////////////////////////////////////////////////////////////////////////////////////////////////////
			
			ObjectIndex() {
				
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////
			
			@Override
			public boolean equals(final Object object) {
				if(object == this) {
					return true;
				} else if(!(object instanceof ObjectIndex)) {
					return false;
				} else if(ObjectIndex.class.cast(object).normalIndex != this.normalIndex) {
					return false;
				} else if(ObjectIndex.class.cast(object).textureCoordinateIndex != this.textureCoordinateIndex) {
					return false;
				} else if(ObjectIndex.class.cast(object).vertexIndex != this.vertexIndex) {
					return false;
				} else {
					return true;
				}
			}
			
			public int getNormalIndex() {
				return this.normalIndex;
			}
			
			public int getTextureCoordinateIndex() {
				return this.textureCoordinateIndex;
			}
			
			public int getVertexIndex() {
				return this.vertexIndex;
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(Integer.valueOf(this.normalIndex), Integer.valueOf(this.textureCoordinateIndex), Integer.valueOf(this.vertexIndex));
			}
			
			public void setNormalIndex(final int normalIndex) {
				this.normalIndex = normalIndex;
			}
			
			public void setTextureCoordinateIndex(final int textureCoordinateIndex) {
				this.textureCoordinateIndex = textureCoordinateIndex;
			}
			
			public void setVertexIndex(final int vertexIndex) {
				this.vertexIndex = vertexIndex;
			}
		}
	}
}