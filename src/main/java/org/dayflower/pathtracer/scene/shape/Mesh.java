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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dayflower.pathtracer.scene.Point2;
import org.dayflower.pathtracer.scene.Point3;
import org.dayflower.pathtracer.scene.Surface;
import org.dayflower.pathtracer.scene.Vector3;
import org.dayflower.pathtracer.scene.shape.Triangle.Vertex;

/**
 * A {@code Mesh} represents a triangle mesh.
 * <p>
 * This class is immutable and therefore suitable for concurrent use without external synchronization.
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Mesh {
	private final List<Triangle> triangles;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Mesh(final List<Triangle> triangles) {
		this.triangles = triangles;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compares {@code object} to this {@code Mesh} instance for equality.
	 * <p>
	 * Returns {@code true} if, and only if, {@code object} is an instance of {@code Mesh}, and their respective values are equal, {@code false} otherwise.
	 * 
	 * @param object the {@code Object} to compare to this {@code Mesh} instance for equality
	 * @return {@code true} if, and only if, {@code object} is an instance of {@code Mesh}, and their respective values are equal, {@code false} otherwise
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) {
			return true;
		} else if(!(object instanceof Mesh)) {
			return false;
		} else if(!Objects.equals(this.triangles, Mesh.class.cast(object).triangles)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns a hash code for this {@code Mesh} instance.
	 * 
	 * @return a hash code for this {@code Mesh} instance
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.triangles);
	}
	
	/**
	 * Returns a {@code List} with all {@link Triangle}s added to this {@code Mesh} instance.
	 * 
	 * @return a {@code List} with all {@code Triangle}s added to this {@code Mesh} instance
	 */
	public List<Triangle> getTriangles() {
		return new ArrayList<>(this.triangles);
	}
	
	/**
	 * Returns a {@code String} representation of this {@code Mesh} instance.
	 * 
	 * @return a {@code String} representation of this {@code Mesh} instance
	 */
	@Override
	public String toString() {
		return String.format("Mesh: [Triangles=%s]", this.triangles);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Loads a {@code Mesh} from an OBJ model.
	 * <p>
	 * Returns a new {@code Mesh} instance.
	 * <p>
	 * Calling this method is equivalent to calling {@code Mesh.loadFromOBJModel(meshConfigurator, file, 1.0F)}.
	 * <p>
	 * If either {@code meshConfigurator} or {@code file} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs while loading, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param meshConfigurator a {@link MeshConfigurator}
	 * @param file the file to load from
	 * @return a new {@code Mesh} instance
	 * @throws NullPointerException thrown if, and only if, either {@code meshConfigurator} or {@code file} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs while loading
	 */
	public static Mesh loadFromOBJModel(final MeshConfigurator meshConfigurator, final File file) {
		return loadFromOBJModel(meshConfigurator, file, 1.0F);
	}
	
	/**
	 * Loads a {@code Mesh} from an OBJ model.
	 * <p>
	 * Returns a new {@code Mesh} instance.
	 * <p>
	 * If either {@code meshConfigurator} or {@code file} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs while loading, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param meshConfigurator a {@link MeshConfigurator}
	 * @param file the file to load from
	 * @param scale a scale factor that will be applied to the loaded {@code Mesh}
	 * @return a new {@code Mesh} instance
	 * @throws NullPointerException thrown if, and only if, either {@code meshConfigurator} or {@code file} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs while loading
	 */
	public static Mesh loadFromOBJModel(final MeshConfigurator meshConfigurator, final File file, final float scale) {
		try {
			Objects.requireNonNull(meshConfigurator, "meshConfigurator == null");
			
			final OBJModel oBJModel = new OBJModel(Objects.requireNonNull(file, "file == null"), scale);
			
			final IndexedModel indexedModel = oBJModel.toIndexedModel();
			
			final List<Integer> indices = indexedModel.getIndices();
			final List<Point2> textureCoordinates = indexedModel.getTextureCoordinates();
			final List<Point3> positions = indexedModel.getPositions();
			final List<String> materials = indexedModel.getMaterials();
			final List<Triangle> triangles = new ArrayList<>();
			final List<Vector3> normals = indexedModel.getNormals();
			
			for(int i = 0; i < indices.size(); i += 3) {
				final int indexA = indices.get(i + 0).intValue();
				final int indexB = indices.get(i + 1).intValue();
				final int indexC = indices.get(i + 2).intValue();
				
				final String materialName = materials.get(indexA);
				
				final Surface surface = meshConfigurator.getSurface(materialName);
				
				final Vertex a = new Vertex(textureCoordinates.get(indexA), positions.get(indexA), materials.get(indexA), normals.get(indexA));
				final Vertex b = new Vertex(textureCoordinates.get(indexB), positions.get(indexB), materials.get(indexB), normals.get(indexB));
				final Vertex c = new Vertex(textureCoordinates.get(indexC), positions.get(indexC), materials.get(indexC), normals.get(indexC));
				
				final Triangle triangle = new Triangle(surface, a, b, c);
				
				triangles.add(triangle);
			}
			
			return new Mesh(triangles);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Loads a {@code Mesh} from an OBJ model.
	 * <p>
	 * Returns a new {@code Mesh} instance.
	 * <p>
	 * Calling this method is equivalent to calling {@code Mesh.loadFromOBJModel(meshConfigurator, fileName, 1.0F)}.
	 * <p>
	 * If either {@code meshConfigurator} or {@code fileName} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs while loading, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param meshConfigurator a {@link MeshConfigurator}
	 * @param fileName the name of the file to load from
	 * @return a new {@code Mesh} instance
	 * @throws NullPointerException thrown if, and only if, either {@code meshConfigurator} or {@code fileName} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs while loading
	 */
	public static Mesh loadFromOBJModel(final MeshConfigurator meshConfigurator, final String fileName) {
		return loadFromOBJModel(meshConfigurator, fileName, 1.0F);
	}
	
	/**
	 * Loads a {@code Mesh} from an OBJ model.
	 * <p>
	 * Returns a new {@code Mesh} instance.
	 * <p>
	 * Calling this method is equivalent to calling {@code Mesh.loadFromOBJModel(meshConfigurator, new File(fileName), 1.0F)}.
	 * <p>
	 * If either {@code meshConfigurator} or {@code fileName} are {@code null}, a {@code NullPointerException} will be thrown.
	 * <p>
	 * If an I/O-error occurs while loading, an {@code UncheckedIOException} will be thrown.
	 * 
	 * @param meshConfigurator a {@link MeshConfigurator}
	 * @param fileName the name of the file to load from
	 * @param scale a scale factor that will be applied to the loaded {@code Mesh}
	 * @return a new {@code Mesh} instance
	 * @throws NullPointerException thrown if, and only if, either {@code meshConfigurator} or {@code fileName} are {@code null}
	 * @throws UncheckedIOException thrown if, and only if, an I/O-error occurs while loading
	 */
	public static Mesh loadFromOBJModel(final MeshConfigurator meshConfigurator, final String fileName, final float scale) {
		return loadFromOBJModel(meshConfigurator, new File(Objects.requireNonNull(fileName, "fileName == null")), scale);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * A {@code MeshConfigurator} is used to configure a {@link Mesh} and its {@link Triangle}s.
	 * 
	 * @since 1.0.0
	 * @author J&#246;rgen Lundgren
	 */
	public static interface MeshConfigurator {
		/**
		 * Returns the {@link Surface} given a material name.
		 * 
		 * @param materialName the materialName
		 * @return the {@code Surface} given a material name
		 */
		Surface getSurface(final String materialName);
		
		/**
		 * Returns the emission given a material name.
		 * 
		 * @param materialName the material name
		 * @return the emission given a material name
		 */
//		Color getEmission(final String materialName);
		
		/**
		 * Returns the Perlin Noise amount given a material name.
		 * 
		 * @param materialName the material name
		 * @return the Perlin Noise amount given a material name
		 */
//		float getPerlinNoiseAmount(final String materialName);
		
		/**
		 * Returns the Perlin Noise scale given a material name.
		 * 
		 * @param materialName the material name
		 * @return the Perlin Noise scale given a material name
		 */
//		float getPerlinNoiseScale(final String materialName);
		
		/**
		 * Returns the {@link Material} given a material name.
		 * 
		 * @param materialName the material name
		 * @return the {@code Material} given a material name
		 */
//		Material getMaterial(final String materialName);
		
		/**
		 * Returns the Albedo {@link Texture} given a material name.
		 * 
		 * @param materialName the material name
		 * @return the Albedo {@code Texture} given a material name
		 */
//		Texture getTextureAlbedo(final String materialName);
		
		/**
		 * Returns the Normal Map {@link Texture} given a material name.
		 * 
		 * @param materialName the material name
		 * @return the Normal Map {@code Texture} given a material name
		 */
//		Texture getTextureNormal(final String materialName);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class IndexedModel {
		private final List<Integer> indices = new ArrayList<>();
		private final List<Point2> textureCoordinates = new ArrayList<>();
		private final List<Point3> positions = new ArrayList<>();
		private final List<String> materials = new ArrayList<>();
		private final List<Vector3> normals = new ArrayList<>();
		private final List<Vector3> tangents = new ArrayList<>();
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public IndexedModel() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public List<Integer> getIndices() {
			return this.indices;
		}
		
		public List<String> getMaterials() {
			return this.materials;
		}
		
		public List<Vector3> getNormals() {
			return this.normals;
		}
		
		public List<Point3> getPositions() {
			return this.positions;
		}
		
		public List<Vector3> getTangents() {
			return this.tangents;
		}
		
		public List<Point2> getTextureCoordinates() {
			return this.textureCoordinates;
		}
		
		public void calculateNormals() {
			for(int i = 0; i < this.indices.size(); i += 3) {
				final int index0 = this.indices.get(i + 0).intValue();
				final int index1 = this.indices.get(i + 1).intValue();
				final int index2 = this.indices.get(i + 2).intValue();
				
				final Vector3 edge0 = Vector3.direction(this.positions.get(index0), this.positions.get(index1));
				final Vector3 edge1 = Vector3.direction(this.positions.get(index0), this.positions.get(index2));
				final Vector3 normal = edge0.crossProduct(edge1).normalize();
				
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
				
				final Vector3 edge0 = Vector3.direction(this.positions.get(index0), this.positions.get(index1));
				final Vector3 edge1 = Vector3.direction(this.positions.get(index0), this.positions.get(index2));
				
				final float deltaU0 = this.textureCoordinates.get(index1).x - this.textureCoordinates.get(index0).x;
				final float deltaV0 = this.textureCoordinates.get(index1).y - this.textureCoordinates.get(index0).y;
				final float deltaU1 = this.textureCoordinates.get(index2).x - this.textureCoordinates.get(index0).x;
				final float deltaV1 = this.textureCoordinates.get(index2).y - this.textureCoordinates.get(index0).y;
				
				final float dividend = (deltaU0 * deltaV1 - deltaU1 * deltaV0);
				final float fraction = dividend == 0.0F ? 0.0F : 1.0F / dividend;
				
				final float x = fraction * (deltaV1 * edge0.x - deltaV0 * edge1.x);
				final float y = fraction * (deltaV1 * edge0.y - deltaV0 * edge1.y);
				final float z = fraction * (deltaV1 * edge0.y - deltaV0 * edge1.y);
				
				final Vector3 tangent = new Vector3(x, y, z);
				
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
	
	private static final class OBJModel {
		private final AtomicBoolean hasNormals = new AtomicBoolean();
		private final AtomicBoolean hasTextureCoordinates = new AtomicBoolean();
		private final List<OBJIndex> indices = new ArrayList<>();
		private final List<Point2> textureCoordinates = new ArrayList<>();
		private final List<Point3> positions = new ArrayList<>();
		private final List<String> materials = new ArrayList<>();
		private final List<Vector3> normals = new ArrayList<>();
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public OBJModel(final File file, final float scale) throws IOException {
			String material = "";
			
			try(final BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
				for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
					final String[] tokens = doRemoveEmptyStrings(line.split(" "));
					
					if(tokens.length == 0 || tokens[0].equals("#")) {
						continue;
					} else if(tokens[0].equals("usemtl")) {
						material = tokens[1];
					} else if(tokens[0].equals("v")) {
						this.positions.add(new Point3(Float.valueOf(tokens[1]).floatValue() * scale, Float.valueOf(tokens[2]).floatValue() * scale, Float.valueOf(tokens[3]).floatValue() * scale));
					} else if(tokens[0].equals("vt")) {
						this.textureCoordinates.add(new Point2(Float.valueOf(tokens[1]).floatValue() * scale, 1.0F - Float.valueOf(tokens[2]).floatValue() * scale));
					} else if(tokens[0].equals("vn")) {
						this.normals.add(new Vector3(Float.valueOf(tokens[1]).floatValue() * scale, Float.valueOf(tokens[2]).floatValue() * scale, Float.valueOf(tokens[3]).floatValue() * scale));
					} else if(tokens[0].equals("f")) {
						for(int i = 0; i < tokens.length - 3; i++) {
							this.indices.add(doParseOBJIndex(tokens[1 + 0]));
							this.indices.add(doParseOBJIndex(tokens[2 + i]));
							this.indices.add(doParseOBJIndex(tokens[3 + i]));
							
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
			
			final Map<OBJIndex, Integer> modelVertexIndices = new HashMap<>();
			final Map<Integer, Integer> normalModelIndices0 = new HashMap<>();
			final Map<Integer, Integer> normalModelIndices1 = new HashMap<>();
			
			for(int i = 0; i < this.indices.size(); i++) {
				final OBJIndex currentOBJIndex = this.indices.get(i);
				
				final Point2 currentTextureCoordinate = this.hasTextureCoordinates.get() ? this.textureCoordinates.get(currentOBJIndex.getTextureCoordinateIndex()) : new Point2();
				
				final Point3 currentPosition = this.positions.get(currentOBJIndex.getVertexIndex());
				
				final String currentMaterial = this.materials.get(i);
				
				final Vector3 currentNormal = this.hasNormals.get() ? this.normals.get(currentOBJIndex.getNormalIndex()) : new Vector3();
				
				Integer modelVertexIndex = modelVertexIndices.get(currentOBJIndex);
				
				if(modelVertexIndex == null) {
					modelVertexIndex = Integer.valueOf(indexedModel0.getPositions().size());
					
					modelVertexIndices.put(currentOBJIndex, modelVertexIndex);
					
					indexedModel0.getTextureCoordinates().add(currentTextureCoordinate);
					indexedModel0.getPositions().add(currentPosition);
					indexedModel0.getMaterials().add(currentMaterial);
					
					if(this.hasNormals.get()) {
						indexedModel0.getNormals().add(currentNormal);
					}
				}
				
				Integer normalModelIndex = normalModelIndices0.get(Integer.valueOf(currentOBJIndex.getVertexIndex()));
				
				if(normalModelIndex == null) {
					normalModelIndex = Integer.valueOf(indexedModel1.getPositions().size());
					
					normalModelIndices0.put(Integer.valueOf(currentOBJIndex.getVertexIndex()), normalModelIndex);
					
					indexedModel1.getTextureCoordinates().add(currentTextureCoordinate);
					indexedModel1.getPositions().add(currentPosition);
					indexedModel1.getMaterials().add(currentMaterial);
					indexedModel1.getNormals().add(currentNormal);
					indexedModel1.getTangents().add(new Vector3());
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
		
		private OBJIndex doParseOBJIndex(final String token) {
			final String[] values = token.split("/");
			
			final
			OBJIndex oBJIndex = new OBJIndex();
			oBJIndex.setVertexIndex(Integer.parseInt(values[0]) - 1);
			
			if(values.length > 1) {
				if(!values[1].isEmpty()) {
					this.hasTextureCoordinates.set(true);
					
					oBJIndex.setTextureCoordinateIndex(Integer.parseInt(values[1]) - 1);
				}
				
				if(values.length > 2) {
					this.hasNormals.set(true);
					
					oBJIndex.setNormalIndex(Integer.parseInt(values[2]) - 1);
				}
			}
			
			return oBJIndex;
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
		
		private static final class OBJIndex {
			private int normalIndex;
			private int textureCoordinateIndex;
			private int vertexIndex;
			
			////////////////////////////////////////////////////////////////////////////////////////////////////
			
			OBJIndex() {
				
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////
			
			@Override
			public boolean equals(final Object object) {
				if(object == this) {
					return true;
				} else if(!(object instanceof OBJIndex)) {
					return false;
				} else if(OBJIndex.class.cast(object).normalIndex != this.normalIndex) {
					return false;
				} else if(OBJIndex.class.cast(object).textureCoordinateIndex != this.textureCoordinateIndex) {
					return false;
				} else if(OBJIndex.class.cast(object).vertexIndex != this.vertexIndex) {
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