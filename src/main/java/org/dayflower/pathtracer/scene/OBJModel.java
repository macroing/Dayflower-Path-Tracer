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
package org.dayflower.pathtracer.scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;//TODO: Add Javadocs.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

//TODO: Add Javadocs!
public final class OBJModel {
	private final AtomicBoolean hasNormals = new AtomicBoolean();
	private final AtomicBoolean hasTextureCoordinates = new AtomicBoolean();
	private final List<OBJIndex> indices = new ArrayList<>();
	private final List<Vector4> normals = new ArrayList<>();
	private final List<Vector4> positions = new ArrayList<>();
	private final List<Vector4> textureCoordinates = new ArrayList<>();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public OBJModel(final String fileName) throws IOException {
		try(final BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
			for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
				final String[] tokens = doRemoveEmptyStrings(line.split(" "));
				
				if(tokens.length == 0 || tokens[0].equals("#")) {
					continue;
				} else if(tokens[0].equals("v")) {
					this.positions.add(new Vector4(Float.valueOf(tokens[1]).floatValue(), Float.valueOf(tokens[2]).floatValue(), Float.valueOf(tokens[3]).floatValue(), 1.0F));
				} else if(tokens[0].equals("vt")) {
					this.textureCoordinates.add(new Vector4(Float.valueOf(tokens[1]).floatValue(), 1.0F - Float.valueOf(tokens[2]).floatValue(), 0.0F, 0.0F));
				} else if(tokens[0].equals("vn")) {
					this.normals.add(new Vector4(Float.valueOf(tokens[1]).floatValue(), Float.valueOf(tokens[2]).floatValue(), Float.valueOf(tokens[3]).floatValue(), 0.0F));
				} else if(tokens[0].equals("f")) {
					for(int i = 0; i < tokens.length - 3; i++) {
						this.indices.add(doParseOBJIndex(tokens[1 + 0]));
						this.indices.add(doParseOBJIndex(tokens[2 + i]));
						this.indices.add(doParseOBJIndex(tokens[3 + i]));
					}
				}
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	TODO: Add Javadocs!
	public IndexedModel toIndexedModel() {
		final IndexedModel indexedModel0 = new IndexedModel();
		final IndexedModel indexedModel1 = new IndexedModel();
		
		final Map<OBJIndex, Integer> modelVertexIndices = new HashMap<>();
		final Map<Integer, Integer> normalModelIndices0 = new HashMap<>();
		final Map<Integer, Integer> normalModelIndices1 = new HashMap<>();
		
		for(int i = 0; i < this.indices.size(); i++) {
			final OBJIndex currentOBJIndex = this.indices.get(i);
			
			final Vector4 currentNormal = this.hasNormals.get() ? this.normals.get(currentOBJIndex.getNormalIndex()) : new Vector4(0.0F, 0.0F, 0.0F, 0.0F);
			final Vector4 currentPosition = this.positions.get(currentOBJIndex.getVertexIndex());
			final Vector4 currentTextureCoordinate = this.hasTextureCoordinates.get() ? this.textureCoordinates.get(currentOBJIndex.getTextureCoordinateIndex()) : new Vector4(0.0F, 0.0F, 0.0F, 0.0F);
			
			Integer modelVertexIndex = modelVertexIndices.get(currentOBJIndex);
			
			if(modelVertexIndex == null) {
				modelVertexIndex = Integer.valueOf(indexedModel0.getPositions().size());
				
				modelVertexIndices.put(currentOBJIndex, modelVertexIndex);
				
				indexedModel0.getPositions().add(currentPosition);
				indexedModel0.getTextureCoordinates().add(currentTextureCoordinate);
				
				if(this.hasNormals.get()) {
					indexedModel0.getNormals().add(currentNormal);
				}
			}
			
			Integer normalModelIndex = normalModelIndices0.get(Integer.valueOf(currentOBJIndex.getVertexIndex()));
			
			if(normalModelIndex == null) {
				normalModelIndex = Integer.valueOf(indexedModel1.getPositions().size());
				
				normalModelIndices0.put(Integer.valueOf(currentOBJIndex.getVertexIndex()), normalModelIndex);
				
				indexedModel1.getPositions().add(currentPosition);
				indexedModel1.getTextureCoordinates().add(currentTextureCoordinate);
				indexedModel1.getNormals().add(currentNormal);
				indexedModel1.getTangents().add(new Vector4(0.0F, 0.0F, 0.0F, 0.0F));
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