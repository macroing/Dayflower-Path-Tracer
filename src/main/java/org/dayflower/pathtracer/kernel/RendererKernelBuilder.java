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
package org.dayflower.pathtracer.kernel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.dayflower.pathtracer.camera.Camera;
import org.dayflower.pathtracer.main.Scenes;
import org.dayflower.pathtracer.scene.Scene;

public final class RendererKernelBuilder {
	private static final AtomicInteger INDEX = new AtomicInteger();
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean isGrayScale;
	private boolean isSepiaTone;
	private final CompiledScene compiledScene;
	private RendererType rendererType = RendererType.PATH_TRACER;
	private ShadingType shadingType = ShadingType.GOURAUD;
	private ToneMapperType toneMapperType = ToneMapperType.FILMIC_CURVE;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public RendererKernelBuilder(final CompiledScene compiledScene) {
		this.compiledScene = Objects.requireNonNull(compiledScene, "compiledScene == null");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public AbstractRendererKernel build() {
		final String sourceCode = doGenerateSourceCode();
		
		return null;
	}
	
	public boolean isGrayScale() {
		return this.isGrayScale;
	}
	
	public boolean isSepiaTone() {
		return this.isSepiaTone;
	}
	
	public CompiledScene getCompiledScene() {
		return this.compiledScene;
	}
	
	public RendererKernelBuilder setGrayScale(final boolean isGrayScale) {
		this.isGrayScale = isGrayScale;
		
		return this;
	}
	
	public RendererKernelBuilder setRendererType(final RendererType rendererType) {
		this.rendererType = Objects.requireNonNull(rendererType, "rendererType == null");
		
		return this;
	}
	
	public RendererKernelBuilder setSepiaTone(final boolean isSepiaTone) {
		this.isSepiaTone = isSepiaTone;
		
		return this;
	}
	
	public RendererKernelBuilder setShadingType(final ShadingType shadingType) {
		this.shadingType = Objects.requireNonNull(shadingType, "shadingType == null");
		
		return this;
	}
	
	public RendererKernelBuilder setToneMapperType(final ToneMapperType toneMapperType) {
		this.toneMapperType = Objects.requireNonNull(toneMapperType, "toneMapperType == null");
		
		return this;
	}
	
	public RendererType getRendererType() {
		return this.rendererType;
	}
	
	public ShadingType getShadingType() {
		return this.shadingType;
	}
	
	public ToneMapperType getToneMapperType() {
		return this.toneMapperType;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final Scene scene = Scenes.newCornellBoxScene();
		
		final File file = new File(String.format("resources/%s.scene", scene.getName()));
		
		final Camera camera = new Camera();
		
		final CompiledScene compiledScene = CompiledScene.read(camera, file);
		
		final RendererKernelBuilder rendererKernelBuilder = new RendererKernelBuilder(compiledScene);
		
		final String sourceCode = rendererKernelBuilder.doGenerateSourceCode();
		
		try {
			Files.write(new File("src/main/java/org/dayflower/pathtracer/kernel/RendererKernel0.java").toPath(), sourceCode.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		} catch(final IOException e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static enum RendererType {
		PATH_TRACER,
		RAY_CASTER,
		RAY_MARCHER;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private RendererType() {
			
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static enum ShadingType {
		FLAT,
		GOURAUD;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private ShadingType() {
			
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static enum ToneMapperType {
		FILMIC_CURVE,
		LINEAR,
		REINHARD_1,
		REINHARD_2;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		private ToneMapperType() {
			
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private String doGenerateSourceCode() {
		final String className = String.format("RendererKernel%s", Integer.toString(INDEX.getAndIncrement()));
		
		final boolean isGrayScale = isGrayScale();
		final boolean isSepiaTone = isSepiaTone();
		
		final CompiledScene compiledScene = getCompiledScene();
		
		final RendererType rendererType = getRendererType();
		
		final ShadingType shadingType = getShadingType();
		
		final ToneMapperType toneMapperType = getToneMapperType();
		
		final Document document = new Document();
		
		doGenerateLicenseComment(document);
		doGeneratePackageDeclaration(document);
		doGenerateIndentation(document, 0);
		doGenerateImportDeclarations(document);
		doGenerateIndentation(document, 0);
		doGenerateClassStart(document, className);
		doGenerateFields(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateSeparatorComment(document, 4);
		doGenerateIndentation(document, 4);
		doGenerateConstructor0(document, className, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateConstructor1(document, className);
		doGenerateIndentation(document, 4);
		doGenerateConstructor2(document, className);
		doGenerateIndentation(document, 4);
		doGenerateConstructor3(document, className);
		doGenerateIndentation(document, 4);
		doGenerateSeparatorComment(document, 4);
		doGenerateIndentation(document, 4);
		doGenerateGetPixelsMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateGetDepthMaximumMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateGetDepthRussianRouletteMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateCompileMethod(document, className);
		doGenerateIndentation(document, 4);
		doGenerateResetMethod(document, className);
		doGenerateIndentation(document, 4);
		doGenerateUpdateLocalVariablesMethod(document, className);
		doGenerateIndentation(document, 4);
		doGenerateRunMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateSetDepthMaximumMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateSetDepthRussianRouletteMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateSeparatorComment(document, 4);
		doGenerateIndentation(document, 4);
		doGenerateDoCreatePrimaryRayMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoGetYMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoIntersectMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoIntersectPlaneMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoIntersectSphereMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoIntersectTriangleMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoPerlinNoiseMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoSimplexFractalXMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoSimplexFractalXYMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoSimplexNoiseXMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoSimplexNoiseXYMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoHashMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateColorMethod(document, rendererType, toneMapperType, isGrayScale, isSepiaTone);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateColorForSkyMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateSurfacePropertiesMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateSurfacePropertiesForPlaneMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateSurfacePropertiesForSphereMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateSurfacePropertiesForTriangleMethod(document, compiledScene, shadingType);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateTextureColorMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateTextureColorForCheckerboardTextureMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateTextureColorForImageTextureMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateDoCalculateTextureColorForSolidTextureMethod(document);
		doGenerateIndentation(document, 4);
		doGenerateDoPathTracingMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoPerformIntersectionTestMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoPerformIntersectionTestOnlyMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoPerformNormalMappingMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoPerformPerlinNoiseNormalMappingMethod(document, compiledScene);
		doGenerateIndentation(document, 4);
		doGenerateDoRayCastingMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoRayMarchingMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateSeparatorComment(document, 4);
		doGenerateIndentation(document, 4);
		doGenerateDoGradientXMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoGradientXYMethod(document, rendererType);
		doGenerateIndentation(document, 4);
		doGenerateDoFastFloorMethod(document, rendererType);
		doGenerateClassEnd(document);
		
		return document.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static String doComputeIndentation(final int spaces) {
		final StringBuilder stringBuilder = new StringBuilder();
		
		for(int i = 0; i < spaces; i++) {
			stringBuilder.append(" ");
		}
		
		return stringBuilder.toString();
	}
	
	private static void doGenerateClassEnd(final Document document) {
		document.linef("}");
	}
	
	private static void doGenerateClassStart(final Document document, final String className) {
		document.linef("public final class %s extends AbstractRendererKernel {", className);
	}
	
	private static void doGenerateCompileMethod(final Document document, final String className) {
		document.linef("    @Override");
		document.linef("    public %s compile(final byte[] pixels, final int width, final int height) {", className);
		document.linef("        this.pixels = Objects.requireNonNull(pixels, \"pixels == null\");");
		document.linef("        ");
		document.linef("//      setExecutionMode(EXECUTION_MODE.JTP);");
		document.linef("        setExplicit(true);");
		document.linef("        setSeed(System.nanoTime(), width * height);");
		document.linef("        ");
		document.linef("        updateTables();");
		document.linef("        ");
		document.linef("        put(this.pixels);");
		document.linef("        put(this.accumulatedPixelColors);");
		document.linef("        put(this.boundingVolumeHierarchy);");
		document.linef("        put(this.cameraArray);");
		document.linef("        put(this.point2s);");
		document.linef("        put(this.point3s);");
		document.linef("        put(this.perezRelativeLuminance);");
		document.linef("        put(this.perezX);");
		document.linef("        put(this.perezY);");
		document.linef("        put(this.shapes);");
		document.linef("        put(this.surfaces);");
		document.linef("        put(this.textures);");
		document.linef("        put(this.vector3s);");
		document.linef("        put(this.permutations0);");
		document.linef("        put(this.permutations1);");
		document.linef("        put(this.shapeOffsets);");
		document.linef("        put(this.subSamples);");
		document.linef("        ");
		document.linef("        return this;");
		document.linef("    }");
	}
	
	private static void doGenerateConstructor0(final Document document, final String className, final RendererType rendererType) {
		document.linef("    public %s(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final CompiledScene compiledScene) {", className);
		document.linef("        super(width, height, camera, compiledScene);");
		document.linef("        ");
		document.linef("        this.compiledScene = Objects.requireNonNull(compiledScene, \"compiledScene == null\");");
		
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("        this.amplitude = 0.5F;");
			document.linef("        this.frequency = 0.2F;");
			document.linef("        this.lacunarity = 2.0F;");
			document.linef("        this.gain = 1.0F / this.lacunarity;");
			document.linef("        this.octaves = 2;");
		}
		
		document.linef("        this.orthoNormalBasisUX = sky.getOrthoNormalBasis().u.x;");
		document.linef("        this.orthoNormalBasisUY = sky.getOrthoNormalBasis().u.y;");
		document.linef("        this.orthoNormalBasisUZ = sky.getOrthoNormalBasis().u.z;");
		document.linef("        this.orthoNormalBasisVX = sky.getOrthoNormalBasis().v.x;");
		document.linef("        this.orthoNormalBasisVY = sky.getOrthoNormalBasis().v.y;");
		document.linef("        this.orthoNormalBasisVZ = sky.getOrthoNormalBasis().v.z;");
		document.linef("        this.orthoNormalBasisWX = sky.getOrthoNormalBasis().w.x;");
		document.linef("        this.orthoNormalBasisWY = sky.getOrthoNormalBasis().w.y;");
		document.linef("        this.orthoNormalBasisWZ = sky.getOrthoNormalBasis().w.z;");
		document.linef("        this.isResettingFully = isResettingFully;");
		document.linef("        this.width = width;");
		document.linef("        this.boundingVolumeHierarchy = this.compiledScene.getBoundingVolumeHierarchy();");
		document.linef("        this.cameraArray = this.compiledScene.getCamera();");
		document.linef("        this.point2s = this.compiledScene.getPoint2s().length == 0 ? new float[2] : this.compiledScene.getPoint2s();");
		document.linef("        this.point3s = this.compiledScene.getPoint3s().length == 0 ? new float[3] : this.compiledScene.getPoint3s();");
		document.linef("        this.shapes = this.compiledScene.getShapes();");
		document.linef("        this.surfaces = this.compiledScene.getSurfaces();");
		document.linef("        this.textures = this.compiledScene.getTextures();");
		document.linef("        this.vector3s = this.compiledScene.getVector3s().length == 0 ? new float[3] : this.compiledScene.getVector3s();");
		document.linef("        this.accumulatedPixelColors = new float[width * height * 3];");
		document.linef("        this.shapeOffsets = this.compiledScene.getShapeOffsets();");
		document.linef("        this.shapeOffsetsLength = this.shapeOffsets.length;");
		document.linef("        this.subSamples = new long[width * height];");
		document.linef("        this.sunDirectionX = sky.getSunDirection().x;");
		document.linef("        this.sunDirectionY = sky.getSunDirection().y;");
		document.linef("        this.sunDirectionZ = sky.getSunDirection().z;");
		document.linef("        this.theta = sky.getTheta();");
		document.linef("        this.zenithRelativeLuminance = sky.getZenithRelativeLuminance();");
		document.linef("        this.zenithX = sky.getZenithX();");
		document.linef("        this.zenithY = sky.getZenithY();");
		document.linef("        this.perezRelativeLuminance = sky.getPerezRelativeLuminance();");
		document.linef("        this.perezX = sky.getPerezX();");
		document.linef("        this.perezY = sky.getPerezY();");
		document.linef("        ");
		document.linef("        for(int i = 0; i < this.permutations0.length; i++) {");
		document.linef("            this.permutations1[i] = this.permutations0[i];");
		document.linef("            this.permutations1[i + this.permutations0.length] = this.permutations0[i];");
		document.linef("        }");
		document.linef("    }");
	}
	
	private static void doGenerateConstructor1(final Document document, final String className) {
		document.linef("    public %s(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final Scene scene) {", className);
		document.linef("        this(isResettingFully, width, height, camera, sky, CompiledScene.compile(camera, scene));");
		document.linef("    }");
	}
	
	private static void doGenerateConstructor2(final Document document, final String className) {
		document.linef("    public %s(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final String filename) {", className);
		document.linef("        this(isResettingFully, width, height, camera, sky, filename, 1.0F);");
		document.linef("    }");
	}
	
	private static void doGenerateConstructor3(final Document document, final String className) {
		document.linef("    public %s(final boolean isResettingFully, final int width, final int height, final Camera camera, final Sky sky, final String filename, final float scale) {", className);
		document.linef("        this(isResettingFully, width, height, camera, sky, CompiledScene.read(camera, new File(Objects.requireNonNull(filename, \"filename == null\"))).scale(scale));");
		document.linef("    }");
	}
	
	private static void doGenerateDoCalculateColorMethod(final Document document, final RendererType rendererType, final ToneMapperType toneMapperType, final boolean isGrayScale, final boolean isSepiaTone) {
		document.linef("    private void doCalculateColor(final int pixelIndex) {");
		document.linef("//      Retrieve the offset to the pixels array:");
		document.linef("        final int pixelsOffset = pixelIndex * SIZE_PIXEL;");
		document.linef("        ");
		document.linef("//      Calculate the pixel index:");
		document.linef("        final int pixelIndex0 = pixelIndex * 3;");
		document.linef("        final int pixelIndex1 = getLocalId() * 3;");
		document.linef("        ");
		
		if(rendererType == RendererType.PATH_TRACER) {
			document.linef("//      Retrieve the current sub-sample:");
			document.linef("        final float subSample = this.subSamples[pixelIndex];");
			document.linef("        ");
			document.linef("//      Multiply the 'normalized' accumulated pixel color component values with the current sub-sample count:");
			document.linef("        this.accumulatedPixelColors[pixelIndex0] *= subSample;");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 1] *= subSample;");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 2] *= subSample;");
			document.linef("        ");
			document.linef("//      Add the current pixel color component values to the accumulated pixel color component values:");
			document.linef("        this.accumulatedPixelColors[pixelIndex0] += this.currentPixelColors[pixelIndex1];");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 1] += this.currentPixelColors[pixelIndex1 + 1];");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 2] += this.currentPixelColors[pixelIndex1 + 2];");
			document.linef("        ");
			document.linef("//      Increment the current sub-sample count by one:");
			document.linef("        this.subSamples[pixelIndex] += 1;");
			document.linef("        ");
			document.linef("//      Retrieve the current sub-sample count and calculate its reciprocal (inverse), such that no division is needed further on:");
			document.linef("        final float currentSubSamples = subSample + 1.0F;");
			document.linef("        final float currentSubSamplesReciprocal = 1.0F / currentSubSamples;");
			document.linef("        ");
			document.linef("//      Multiply the accumulated pixel color component values with the reciprocal of the current sub-sample count to 'normalize' it:");
			document.linef("        this.accumulatedPixelColors[pixelIndex0] *= currentSubSamplesReciprocal;");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 1] *= currentSubSamplesReciprocal;");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 2] *= currentSubSamplesReciprocal;");
		} else {
			document.linef("        this.accumulatedPixelColors[pixelIndex0] = this.currentPixelColors[pixelIndex1];");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 1] = this.currentPixelColors[pixelIndex1 + 1];");
			document.linef("        this.accumulatedPixelColors[pixelIndex0 + 2] = this.currentPixelColors[pixelIndex1 + 2];");
		}
		
		document.linef("        ");
		document.linef("//      Retrieve the 'normalized' accumulated pixel color component values again:");
		document.linef("        float r = this.accumulatedPixelColors[pixelIndex0];");
		document.linef("        float g = this.accumulatedPixelColors[pixelIndex0 + 1];");
		document.linef("        float b = this.accumulatedPixelColors[pixelIndex0 + 2];");
		document.linef("        ");
		
		switch(toneMapperType) {
			case LINEAR:
				document.linef("//      Calculate the maximum component value of the 'normalized' accumulated pixel color component values:");
				document.linef("        final float maximumComponentValue = max(r, max(g, b));");
				document.linef("        ");
				document.linef("//      Check if the maximum component value is greater than 1.0:");
				document.linef("        if(maximumComponentValue > 1.0F) {");
				document.linef("//          Calculate the reciprocal of the maximum component value, such that no division is needed further on:");
				document.linef("            final float maximumComponentValueReciprocal = 1.0F / maximumComponentValue;");
				document.linef("            ");
				document.linef("//          Multiply the 'normalized' accumulated pixel color component values with the reciprocal of the maximum component value for Tone Mapping:");
				document.linef("            r *= maximumComponentValueReciprocal;");
				document.linef("            g *= maximumComponentValueReciprocal;");
				document.linef("            b *= maximumComponentValueReciprocal;");
				document.linef("        }");
				document.linef("        ");
				document.linef("//      Perform Gamma Correction on the 'normalized' accumulated pixel color components:");
				document.linef("        r = pow(r, GAMMA_RECIPROCAL);");
				document.linef("        g = pow(g, GAMMA_RECIPROCAL);");
				document.linef("        b = pow(b, GAMMA_RECIPROCAL);");
				
				break;
			case REINHARD_1:
				document.linef("//      Perform Tone Mapping on the 'normalized' accumulated pixel color components:");
				document.linef("        r = r / (r + 1.0F);");
				document.linef("        g = g / (g + 1.0F);");
				document.linef("        b = b / (b + 1.0F);");
				document.linef("        ");
				document.linef("//      Perform Gamma Correction on the 'normalized' accumulated pixel color components:");
				document.linef("        r = pow(r, GAMMA_RECIPROCAL);");
				document.linef("        g = pow(g, GAMMA_RECIPROCAL);");
				document.linef("        b = pow(b, GAMMA_RECIPROCAL);");
				
				break;
			case REINHARD_2:
				document.linef("//      Set the exposure:");
				document.linef("        final float exposure = 1.5F;");
				document.linef("        ");
				document.linef("//      Perform Tone Mapping on the 'normalized' accumulated pixel color components:");
				document.linef("        r *= exposure / (1.0F + r / exposure);");
				document.linef("        g *= exposure / (1.0F + g / exposure);");
				document.linef("        b *= exposure / (1.0F + b / exposure);");
				document.linef("        ");
				document.linef("//      Perform Gamma Correction on the 'normalized' accumulated pixel color components:");
				document.linef("        r = pow(r, GAMMA_RECIPROCAL);");
				document.linef("        g = pow(g, GAMMA_RECIPROCAL);");
				document.linef("        b = pow(b, GAMMA_RECIPROCAL);");
				
				break;
			case FILMIC_CURVE:
			default:
				document.linef("//      Calculate the maximum pixel color component values:");
				document.linef("        final float rMaximum = max(r - 0.004F, 0.0F);");
				document.linef("        final float gMaximum = max(g - 0.004F, 0.0F);");
				document.linef("        final float bMaximum = max(b - 0.004F, 0.0F);");
				document.linef("        ");
				document.linef("//      Perform Tone Mapping and Gamma Correction:");
				document.linef("        r = (rMaximum * (6.2F * rMaximum + 0.5F)) / (rMaximum * (6.2F * rMaximum + 1.7F) + 0.06F);");
				document.linef("        g = (gMaximum * (6.2F * gMaximum + 0.5F)) / (gMaximum * (6.2F * gMaximum + 1.7F) + 0.06F);");
				document.linef("        b = (bMaximum * (6.2F * bMaximum + 0.5F)) / (bMaximum * (6.2F * bMaximum + 1.7F) + 0.06F);");
				
				break;
		}
		
		document.linef("        ");
		
		if(isGrayScale) {
			document.linef("        if(this.effectGrayScale == 1) {");
			document.linef("//          Perform a Grayscale effect based on Luminosity:");
			document.linef("            r = r * 0.21F + g * 0.72F + b * 0.07F;");
			document.linef("            g = r;");
			document.linef("            b = r;");
			document.linef("        }");
		}
		
		document.linef("        ");
		
		if(isSepiaTone) {
			document.linef("        if(this.effectSepiaTone == 1) {");
			document.linef("//          Perform a Sepia effect:");
			document.linef("            final float r1 = r * 0.393F + g * 0.769F + b * 0.189F;");
			document.linef("            final float g1 = r * 0.349F + g * 0.686F + b * 0.168F;");
			document.linef("            final float b1 = r * 0.272F + g * 0.534F + b * 0.131F;");
			document.linef("            ");
			document.linef("            r = r1;");
			document.linef("            g = g1;");
			document.linef("            b = b1;");
			document.linef("        }");
		}
		
		document.linef("        ");
		document.linef("//      Clamp the 'normalized' accumulated pixel color components to the range [0.0, 1.0]:");
		document.linef("        r = min(max(r, 0.0F), 1.0F);");
		document.linef("        g = min(max(g, 0.0F), 1.0F);");
		document.linef("        b = min(max(b, 0.0F), 1.0F);");
		document.linef("        ");
		document.linef("//      Multiply the 'normalized' accumulated pixel color components with 255.0, to lie in the range [0.0, 255.0], so they can be displayed:");
		document.linef("        r *= 255.0F;");
		document.linef("        g *= 255.0F;");
		document.linef("        b *= 255.0F;");
		document.linef("        ");
		document.linef("//      Update the pixels array with the actual color to display it:");
		document.linef("        this.pixels[pixelsOffset + 0] = (byte)(b);");
		document.linef("        this.pixels[pixelsOffset + 1] = (byte)(g);");
		document.linef("        this.pixels[pixelsOffset + 2] = (byte)(r);");
		document.linef("        this.pixels[pixelsOffset + 3] = (byte)(255);");
		document.linef("    }");
	}
	
	private static void doGenerateDoCalculateColorForSkyMethod(final Document document) {
		document.linef("    private void doCalculateColorForSky(final float directionX, final float directionY, final float directionZ) {");
		document.linef("//      Calculate the direction vector:");
		document.linef("        final float direction0X = directionX * this.orthoNormalBasisUX + directionY * this.orthoNormalBasisUY + directionZ * this.orthoNormalBasisUZ;");
		document.linef("        final float direction0Y = directionX * this.orthoNormalBasisVX + directionY * this.orthoNormalBasisVY + directionZ * this.orthoNormalBasisVZ;");
		document.linef("        final float direction0Z = directionX * this.orthoNormalBasisWX + directionY * this.orthoNormalBasisWY + directionZ * this.orthoNormalBasisWZ;");
		document.linef("        ");
		document.linef("//      if(direction0Z < 0.0F) {");
		document.linef("//          Calculate the pixel index:");
		document.linef("//          final int pixelIndex0 = pixelIndex * 3;");
		document.linef("            ");
		document.linef("//          Update the temporaryColors array with black:");
		document.linef("//          this.temporaryColors[pixelIndex0] = 0.0F;");
		document.linef("//          this.temporaryColors[pixelIndex0 + 1] = 0.0F;");
		document.linef("//          this.temporaryColors[pixelIndex0 + 2] = 0.0F;");
		document.linef("            ");
		document.linef("//          return;");
		document.linef("//      }");
		document.linef("        ");
		document.linef("//      Recalculate the direction vector:");
		document.linef("        final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);");
		document.linef("        final float direction1X = direction0X * direction0LengthReciprocal;");
		document.linef("        final float direction1Y = direction0Y * direction0LengthReciprocal;");
		document.linef("        final float direction1Z = max(direction0Z * direction0LengthReciprocal, 0.001F);");
		document.linef("        final float direction1LengthReciprocal = rsqrt(direction1X * direction1X + direction1Y * direction1Y + direction1Z * direction1Z);");
		document.linef("        final float direction2X = direction1X * direction1LengthReciprocal;");
		document.linef("        final float direction2Y = direction1Y * direction1LengthReciprocal;");
		document.linef("        final float direction2Z = direction1Z * direction1LengthReciprocal;");
		document.linef("        ");
		document.linef("//      Calculate the dot product between the direction vector and the sun direction vector:");
		document.linef("        final float dotProduct = direction2X * this.sunDirectionX + direction2Y * this.sunDirectionY + direction2Z * this.sunDirectionZ;");
		document.linef("        ");
		document.linef("//      Calculate some theta angles:");
		document.linef("        final float theta0 = this.theta;");
		document.linef("        final float theta1 = acos(max(min(direction2Z, 1.0F), -1.0F));");
		document.linef("        ");
		document.linef("//      Calculate the cosines of the theta angles:");
		document.linef("        final float cosTheta0 = cos(theta0);");
		document.linef("        final float cosTheta1 = cos(theta1);");
		document.linef("        final float cosTheta1Reciprocal = 1.0F / (cosTheta1 + 0.01F);");
		document.linef("        ");
		document.linef("//      Calculate the gamma:");
		document.linef("        final float gamma = acos(max(min(dotProduct, 1.0F), -1.0F));");
		document.linef("        ");
		document.linef("//      Calculate the cosine of the gamma:");
		document.linef("        final float cosGamma = cos(gamma);");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float perezRelativeLuminance0 = this.perezRelativeLuminance[0];");
		document.linef("        final float perezRelativeLuminance1 = this.perezRelativeLuminance[1];");
		document.linef("        final float perezRelativeLuminance2 = this.perezRelativeLuminance[2];");
		document.linef("        final float perezRelativeLuminance3 = this.perezRelativeLuminance[3];");
		document.linef("        final float perezRelativeLuminance4 = this.perezRelativeLuminance[4];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float zenithRelativeLuminance = this.zenithRelativeLuminance;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float perezX0 = this.perezX[0];");
		document.linef("        final float perezX1 = this.perezX[1];");
		document.linef("        final float perezX2 = this.perezX[2];");
		document.linef("        final float perezX3 = this.perezX[3];");
		document.linef("        final float perezX4 = this.perezX[4];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float perezY0 = this.perezY[0];");
		document.linef("        final float perezY1 = this.perezY[1];");
		document.linef("        final float perezY2 = this.perezY[2];");
		document.linef("        final float perezY3 = this.perezY[3];");
		document.linef("        final float perezY4 = this.perezY[4];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float zenithX = this.zenithX;");
		document.linef("        final float zenithY = this.zenithY;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float den0 = ((1.0F + perezRelativeLuminance0 * exp(perezRelativeLuminance1)) * (1.0F + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * theta0) + perezRelativeLuminance4 * cosTheta0 * cosTheta0));");
		document.linef("        final float num0 = ((1.0F + perezRelativeLuminance0 * exp(perezRelativeLuminance1 * cosTheta1Reciprocal)) * (1.0F + perezRelativeLuminance2 * exp(perezRelativeLuminance3 * gamma) + perezRelativeLuminance4 * cosGamma * cosGamma));");
		document.linef("        final float relativeLuminance = zenithRelativeLuminance * num0 / den0 * 1.0e-4F;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float den1 = ((1.0F + perezX0 * exp(perezX1)) * (1.0F + perezX2 * exp(perezX3 * theta1) + perezX4 * cosTheta0 * cosTheta0));");
		document.linef("        final float num1 = ((1.0F + perezX0 * exp(perezX1 * cosTheta1Reciprocal)) * (1.0F + perezX2 * exp(perezX3 * gamma) + perezX4 * cosGamma * cosGamma));");
		document.linef("        final float x = zenithX * num1 / den1;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float den2 = ((1.0F + perezY0 * exp(perezY1)) * (1.0F + perezY2 * exp(perezY3 * theta1) + perezY4 * cosTheta0 * cosTheta0));");
		document.linef("        final float num2 = ((1.0F + perezY0 * exp(perezY1 * cosTheta1Reciprocal)) * (1.0F + perezY2 * exp(perezY3 * gamma) + perezY4 * cosGamma * cosGamma));");
		document.linef("        final float y = zenithY * num2 / den2;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float v0 = 1.0F / (0.0241F + 0.2562F * x - 0.7341F * y);");
		document.linef("        final float v1 = (-1.3515F - 1.7703F * x + 5.9114F * y) * v0;");
		document.linef("        final float v2 = (0.03F - 31.4424F * x + 30.0717F * y) * v0;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float x3 = 10246.121F + v1 * 187.75537F + v2 * 213.14803F;");
		document.linef("        final float y3 = 10676.695F + v1 * 192.59653F + v2 * 76.29494F;");
		document.linef("        final float z3 = 12372.504F + v1 * 3482.8765F + v2 * -235.71611F;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float y3Reciprocal = 1.0F / y3;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float x4 = x3 * relativeLuminance * y3Reciprocal;");
		document.linef("        final float y4 = relativeLuminance;");
		document.linef("        final float z4 = z3 * relativeLuminance * y3Reciprocal;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        float r = 3.2410042F * x4 + -1.5373994F * y4 + -0.49861607F * z4;");
		document.linef("        float g = -0.9692241F * x4 + 1.8759298F * y4 + 0.041554242F * z4;");
		document.linef("        float b = 0.05563942F * x4 + -0.20401107F * y4 + 1.0571486F * z4;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float w0 = -min(0.0F, min(r, min(g, b)));");
		document.linef("        final float w1 = max(0.0F, w0);");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        r += w1;");
		document.linef("        g += w1;");
		document.linef("        b += w1;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final int pixelIndex0 = getLocalId() * 3;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        this.temporaryColors[pixelIndex0] = r;");
		document.linef("        this.temporaryColors[pixelIndex0 + 1] = g;");
		document.linef("        this.temporaryColors[pixelIndex0 + 2] = b;");
		document.linef("    }");
	}
	
	private static void doGenerateDoCalculateSurfacePropertiesMethod(final Document document, final CompiledScene compiledScene) {
		final boolean hasPlanes = compiledScene.hasPlanes();
		final boolean hasSpheres = compiledScene.hasSpheres();
		final boolean hasTriangles = compiledScene.hasTriangles();
		
		if(hasPlanes || hasSpheres || hasTriangles) {
			document.linef("    private void doCalculateSurfaceProperties(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {");
			
			if(hasPlanes && !hasSpheres && !hasTriangles) {
				document.linef("        doCalculateSurfacePropertiesForPlane(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
			} else if(!hasPlanes && hasSpheres && !hasTriangles) {
				document.linef("        doCalculateSurfacePropertiesForSphere(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
			} else if(!hasPlanes && !hasSpheres && hasTriangles) {
				document.linef("        doCalculateSurfacePropertiesForTriangle(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
			} else {
				document.linef("        final int type = (int)(this.shapes[shapesOffset]);");
				document.linef("        ");
				
				if(hasTriangles) {
					document.linef("        if(type == CompiledScene.TRIANGLE_TYPE) {");
					document.linef("            doCalculateSurfacePropertiesForTriangle(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
					document.linef("        }%s", hasPlanes || hasSpheres ? " else " : "");
				}
				
				if(hasPlanes) {
					document.linef("        if(type == CompiledScene.PLANE_TYPE) {");
					document.linef("            doCalculateSurfacePropertiesForPlane(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
					document.linef("        }%s", hasSpheres ? " else " : "");
				}
				
				if(hasSpheres) {
					document.linef("        if(type == CompiledScene.SPHERE_TYPE) {");
					document.linef("            doCalculateSurfacePropertiesForSphere(distance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
					document.linef("        }");
				}
			}
			
			document.linef("    }");
		} else {
			document.linef("//  private void doCalculateSurfaceProperties(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {}");
		}
	}
	
	private static void doGenerateDoCalculateSurfacePropertiesForPlaneMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasPlanes()) {
			document.linef("    private void doCalculateSurfacePropertiesForPlane(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {");
			document.linef("//      Calculate the surface intersection point:");
			document.linef("        final float surfaceIntersectionPointX = originX + directionX * distance;");
			document.linef("        final float surfaceIntersectionPointY = originY + directionY * distance;");
			document.linef("        final float surfaceIntersectionPointZ = originZ + directionZ * distance;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_A_POINT3S_OFFSET]);");
			document.linef("        final int offsetB = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_B_POINT3S_OFFSET]);");
			document.linef("        final int offsetC = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_C_POINT3S_OFFSET]);");
			document.linef("        final int offsetSurfaceNormal = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_SURFACE_NORMAL_VECTOR3S_OFFSET]);");
			document.linef("        ");
			document.linef("//      Retrieve the point A of the plane:");
			document.linef("        final float a0X = this.point3s[offsetA];");
			document.linef("        final float a0Y = this.point3s[offsetA + 1];");
			document.linef("        final float a0Z = this.point3s[offsetA + 2];");
			document.linef("        ");
			document.linef("//      Retrieve the point B of the plane:");
			document.linef("        final float b0X = this.point3s[offsetB];");
			document.linef("        final float b0Y = this.point3s[offsetB + 1];");
			document.linef("        final float b0Z = this.point3s[offsetB + 2];");
			document.linef("        ");
			document.linef("//      Retrieve the point C of the plane:");
			document.linef("        final float c0X = this.point3s[offsetC];");
			document.linef("        final float c0Y = this.point3s[offsetC + 1];");
			document.linef("        final float c0Z = this.point3s[offsetC + 2];");
			document.linef("        ");
			document.linef("//      Retrieve the surface normal:");
			document.linef("        final float surfaceNormalX = this.vector3s[offsetSurfaceNormal];");
			document.linef("        final float surfaceNormalY = this.vector3s[offsetSurfaceNormal + 1];");
			document.linef("        final float surfaceNormalZ = this.vector3s[offsetSurfaceNormal + 2];");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float absSurfaceNormalX = abs(surfaceNormalX);");
			document.linef("        final float absSurfaceNormalY = abs(surfaceNormalY);");
			document.linef("        final float absSurfaceNormalZ = abs(surfaceNormalZ);");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final boolean isX = absSurfaceNormalX > absSurfaceNormalY && absSurfaceNormalX > absSurfaceNormalZ;");
			document.linef("        final boolean isY = absSurfaceNormalY > absSurfaceNormalZ;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float a1X = isX ? a0Y : isY ? a0Z : a0X;");
			document.linef("        final float a1Y = isX ? a0Z : isY ? a0X : a0Y;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float b1X = isX ? c0Y - a0X : isY ? c0Z - a0X : c0X - a0X;");
			document.linef("        final float b1Y = isX ? c0Z - a0Y : isY ? c0X - a0Y : c0Y - a0Y;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float c1X = isX ? b0Y - a0X : isY ? b0Z - a0X : b0X - a0X;");
			document.linef("        final float c1Y = isX ? b0Z - a0Y : isY ? b0X - a0Y : b0Y - a0Y;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float determinant = b1X * c1Y - b1Y * c1X;");
			document.linef("        final float determinantReciprocal = 1.0F / determinant;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float bNU = -b1Y * determinantReciprocal;");
			document.linef("        final float bNV = b1X * determinantReciprocal;");
			document.linef("        final float bND = (b1Y * a1X - b1X * a1Y) * determinantReciprocal;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float cNU = c1Y * determinantReciprocal;");
			document.linef("        final float cNV = -c1X * determinantReciprocal;");
			document.linef("        final float cND = (c1X * a1Y - c1Y * a1X) * determinantReciprocal;");
			document.linef("        ");
			document.linef("//      TODO: Write explanation!");
			document.linef("        final float hU = isX ? surfaceIntersectionPointY : isY ? surfaceIntersectionPointZ : surfaceIntersectionPointX;");
			document.linef("        final float hV = isX ? surfaceIntersectionPointZ : isY ? surfaceIntersectionPointX : surfaceIntersectionPointY;");
			document.linef("        ");
			document.linef("//      Calculate the UV-coordinates:");
			document.linef("        final float u = hU * bNU + hV * bNV + bND;");
			document.linef("        final float v = hU * cNU + hV * cNV + cND;");
			document.linef("        ");
			document.linef("//      Get the intersections offset:");
			document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        ");
			document.linef("//      Calculate some offsets:");
			document.linef("        final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;");
			document.linef("        final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("        final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;");
			document.linef("        ");
			document.linef("//      Update the intersections array:");
			document.linef("        this.intersections[intersectionsOffset0] = distance;");
			document.linef("        this.intersections[intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;");
			document.linef("        this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormalX;");
			document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormalY;");
			document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormalZ;");
			document.linef("        this.intersections[offsetIntersectionUVCoordinates] = u;");
			document.linef("        this.intersections[offsetIntersectionUVCoordinates + 1] = v;");
			document.linef("    }");
		} else {
			document.linef("//  private void doCalculateSurfacePropertiesForPlane(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {}");
		}
	}
	
	private static void doGenerateDoCalculateSurfacePropertiesForSphereMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasSpheres()) {
			document.linef("    private void doCalculateSurfacePropertiesForSphere(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {");
			document.linef("//      Calculate the surface intersection point:");
			document.linef("        final float surfaceIntersectionPointX = originX + directionX * distance;");
			document.linef("        final float surfaceIntersectionPointY = originY + directionY * distance;");
			document.linef("        final float surfaceIntersectionPointZ = originZ + directionZ * distance;");
			document.linef("        ");
			document.linef("//      Retrieve the offset of the position:");
			document.linef("        final int offsetPosition = (int)(this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_POSITION_POINT3S_OFFSET]);");
			document.linef("        ");
			document.linef("//      Retrieve the X-, Y- and Z-components of the position:");
			document.linef("        final float x = this.point3s[offsetPosition];");
			document.linef("        final float y = this.point3s[offsetPosition + 1];");
			document.linef("        final float z = this.point3s[offsetPosition + 2];");
			document.linef("        ");
			document.linef("//      Calculate the surface normal:");
			document.linef("        final float surfaceNormal0X = surfaceIntersectionPointX - x;");
			document.linef("        final float surfaceNormal0Y = surfaceIntersectionPointY - y;");
			document.linef("        final float surfaceNormal0Z = surfaceIntersectionPointZ - z;");
			document.linef("        final float lengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);");
			document.linef("        final float surfaceNormal1X = surfaceNormal0X * lengthReciprocal;");
			document.linef("        final float surfaceNormal1Y = surfaceNormal0Y * lengthReciprocal;");
			document.linef("        final float surfaceNormal1Z = surfaceNormal0Z * lengthReciprocal;");
			document.linef("        ");
			document.linef("//      Calculate the UV-coordinates:");
			document.linef("        final float direction0X = x - surfaceIntersectionPointX;");
			document.linef("        final float direction0Y = y - surfaceIntersectionPointY;");
			document.linef("        final float direction0Z = z - surfaceIntersectionPointZ;");
			document.linef("        final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);");
			document.linef("        final float direction1X = direction0X * direction0LengthReciprocal;");
			document.linef("        final float direction1Y = direction0Y * direction0LengthReciprocal;");
			document.linef("        final float direction1Z = direction0Z * direction0LengthReciprocal;");
			document.linef("        final float u = 0.5F + atan2(direction1Z, direction1X) * PI_MULTIPLIED_BY_TWO_RECIPROCAL;");
			document.linef("        final float v = 0.5F - asinpi(direction1Y);");
			document.linef("        ");
			document.linef("//      Get the intersections offset:");
			document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        ");
			document.linef("//      Retrieve offsets for the surface intersection point, surface normal and UV-coordinates:");
			document.linef("        final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;");
			document.linef("        final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("        final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;");
			document.linef("        ");
			document.linef("//      Update the intersections array:");
			document.linef("        this.intersections[intersectionsOffset0] = distance;");
			document.linef("        this.intersections[intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;");
			document.linef("        this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal1X;");
			document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;");
			document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;");
			document.linef("        this.intersections[offsetIntersectionUVCoordinates] = u;");
			document.linef("        this.intersections[offsetIntersectionUVCoordinates + 1] = v;");
			document.linef("    }");
		} else {
			document.linef("//  private void doCalculateSurfacePropertiesForSphere(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {}");
		}
	}
	
	private static void doGenerateDoCalculateSurfacePropertiesForTriangleMethod(final Document document, final CompiledScene compiledScene, final ShadingType shadingType) {
		if(compiledScene.hasTriangles()) {
			document.linef("    private void doCalculateSurfacePropertiesForTriangle(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {");
			document.linef("//      Calculate the surface intersection point:");
			document.linef("        final float surfaceIntersectionPointX = originX + directionX * distance;");
			document.linef("        final float surfaceIntersectionPointY = originY + directionY * distance;");
			document.linef("        final float surfaceIntersectionPointZ = originZ + directionZ * distance;");
			document.linef("        ");
			document.linef("//      Retrieve the offsets for the positions, UV-coordinates and surface normals:");
			document.linef("        final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_A_POINT3S_OFFSET]);");
			document.linef("        final int offsetB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_B_POINT3S_OFFSET]);");
			document.linef("        final int offsetC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_C_POINT3S_OFFSET]);");
			document.linef("        final int offsetUVA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_A_POINT2S_OFFSET]);");
			document.linef("        final int offsetUVB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_B_POINT2S_OFFSET]);");
			document.linef("        final int offsetUVC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_UV_C_POINT2S_OFFSET]);");
			document.linef("        final int offsetSurfaceNormalA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_A_VECTOR3S_OFFSET]);");
			document.linef("        final int offsetSurfaceNormalB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_B_VECTOR3S_OFFSET]);");
			document.linef("        final int offsetSurfaceNormalC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_SURFACE_NORMAL_C_VECTOR3S_OFFSET]);");
			document.linef("        ");
			document.linef("//      Calculate the Barycentric-coordinates:");
			document.linef("        final float aX = this.point3s[offsetA];");
			document.linef("        final float aY = this.point3s[offsetA + 1];");
			document.linef("        final float aZ = this.point3s[offsetA + 2];");
			document.linef("        final float bX = this.point3s[offsetB];");
			document.linef("        final float bY = this.point3s[offsetB + 1];");
			document.linef("        final float bZ = this.point3s[offsetB + 2];");
			document.linef("        final float cX = this.point3s[offsetC];");
			document.linef("        final float cY = this.point3s[offsetC + 1];");
			document.linef("        final float cZ = this.point3s[offsetC + 2];");
			document.linef("        final float edge0X = bX - aX;");
			document.linef("        final float edge0Y = bY - aY;");
			document.linef("        final float edge0Z = bZ - aZ;");
			document.linef("        final float edge1X = cX - aX;");
			document.linef("        final float edge1Y = cY - aY;");
			document.linef("        final float edge1Z = cZ - aZ;");
			document.linef("        final float v0X = directionY * edge1Z - directionZ * edge1Y;");
			document.linef("        final float v0Y = directionZ * edge1X - directionX * edge1Z;");
			document.linef("        final float v0Z = directionX * edge1Y - directionY * edge1X;");
			document.linef("        final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;");
			document.linef("        final float determinantReciprocal = 1.0F / determinant;");
			document.linef("        final float v1X = originX - aX;");
			document.linef("        final float v1Y = originY - aY;");
			document.linef("        final float v1Z = originZ - aZ;");
			document.linef("        final float u0 = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;");
			document.linef("        final float v2X = v1Y * edge0Z - v1Z * edge0Y;");
			document.linef("        final float v2Y = v1Z * edge0X - v1X * edge0Z;");
			document.linef("        final float v2Z = v1X * edge0Y - v1Y * edge0X;");
			document.linef("        final float v0 = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;");
			document.linef("        final float w = 1.0F - u0 - v0;");
			document.linef("        ");
			document.linef("//      Get the intersections offset:");
			document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        ");
			document.linef("//      Calculate some offsets:");
			document.linef("        final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;");
			document.linef("        final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("        final int offsetIntersectionUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;");
			document.linef("        ");
			document.linef("//      Update the intersections array:");
			document.linef("        this.intersections[intersectionsOffset0] = distance;");
			document.linef("        this.intersections[intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = shapesOffset;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint] = surfaceIntersectionPointX;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1] = surfaceIntersectionPointY;");
			document.linef("        this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2] = surfaceIntersectionPointZ;");
			document.linef("        ");
			
			if(shadingType == ShadingType.FLAT) {
				document.linef("//      Calculate the UV-coordinates for Flat Shading:");
				document.linef("        final float aU = this.point2s[offsetUVA];");
				document.linef("        final float aV = this.point2s[offsetUVA + 1];");
				document.linef("        final float bU = this.point2s[offsetUVB];");
				document.linef("        final float bV = this.point2s[offsetUVB + 1];");
				document.linef("        final float cU = this.point2s[offsetUVC];");
				document.linef("        final float cV = this.point2s[offsetUVC + 1];");
				document.linef("        final float u1 = w * aU + u0 * bU + v0 * cU;");
				document.linef("        final float v1 = w * aV + u0 * bV + v0 * cV;");
				document.linef("        ");
				document.linef("//      Calculate the surface normal for Flat Shading:");
				document.linef("        final float surfaceNormalAX = this.vector3s[offsetSurfaceNormalA];");
				document.linef("        final float surfaceNormalAY = this.vector3s[offsetSurfaceNormalA + 1];");
				document.linef("        final float surfaceNormalAZ = this.vector3s[offsetSurfaceNormalA + 2];");
				document.linef("        final float surfaceNormal0X = edge0Y * edge1Z - edge0Z * edge1Y;");
				document.linef("        final float surfaceNormal0Y = edge0Z * edge1X - edge0X * edge1Z;");
				document.linef("        final float surfaceNormal0Z = edge0X * edge1Y - edge0Y * edge1X;");
				document.linef("        final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);");
				document.linef("        final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;");
				document.linef("        final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;");
				document.linef("        final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;");
				document.linef("        final float dotProduct = surfaceNormalAX != 0.0F && surfaceNormalAY != 0.0F && surfaceNormalAZ != 0.0F ? surfaceNormal1X * surfaceNormalAX + surfaceNormal1Y * surfaceNormalAY + surfaceNormal1Z * surfaceNormalAZ : 0.0F;");
				document.linef("        final float surfaceNormal2X = dotProduct < 0.0F ? -surfaceNormal1X : surfaceNormal1X;");
				document.linef("        final float surfaceNormal2Y = dotProduct < 0.0F ? -surfaceNormal1Y : surfaceNormal1Y;");
				document.linef("        final float surfaceNormal2Z = dotProduct < 0.0F ? -surfaceNormal1Z : surfaceNormal1Z;");
				document.linef("        ");
				document.linef("//      Update the intersections array based on Flat Shading:");
				document.linef("        this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal2X;");
				document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;");
				document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;");
				document.linef("        this.intersections[offsetIntersectionUVCoordinates] = u1;");
				document.linef("        this.intersections[offsetIntersectionUVCoordinates + 1] = v1;");
			} else if(shadingType == ShadingType.GOURAUD) {
				document.linef("//      Calculate the UV-coordinates for Gouraud Shading:");
				document.linef("        final float aU = this.point2s[offsetUVA];");
				document.linef("        final float aV = this.point2s[offsetUVA + 1];");
				document.linef("        final float bU = this.point2s[offsetUVB];");
				document.linef("        final float bV = this.point2s[offsetUVB + 1];");
				document.linef("        final float cU = this.point2s[offsetUVC];");
				document.linef("        final float cV = this.point2s[offsetUVC + 1];");
				document.linef("        final float u2 = aU * w + bU * u0 + cU * v0;");
				document.linef("        final float v2 = aV * w + bV * u0 + cV * v0;");
				document.linef("        ");
				document.linef("//      Calculate the surface normal for Gouraud Shading:");
				document.linef("        final float surfaceNormalAX = this.vector3s[offsetSurfaceNormalA];");
				document.linef("        final float surfaceNormalAY = this.vector3s[offsetSurfaceNormalA + 1];");
				document.linef("        final float surfaceNormalAZ = this.vector3s[offsetSurfaceNormalA + 2];");
				document.linef("        final float surfaceNormalBX = this.vector3s[offsetSurfaceNormalB];");
				document.linef("        final float surfaceNormalBY = this.vector3s[offsetSurfaceNormalB + 1];");
				document.linef("        final float surfaceNormalBZ = this.vector3s[offsetSurfaceNormalB + 2];");
				document.linef("        final float surfaceNormalCX = this.vector3s[offsetSurfaceNormalC];");
				document.linef("        final float surfaceNormalCY = this.vector3s[offsetSurfaceNormalC + 1];");
				document.linef("        final float surfaceNormalCZ = this.vector3s[offsetSurfaceNormalC + 2];");
				document.linef("        final float surfaceNormal3X = surfaceNormalAX * w + surfaceNormalBX * u0 + surfaceNormalCX * v0;");
				document.linef("        final float surfaceNormal3Y = surfaceNormalAY * w + surfaceNormalBY * u0 + surfaceNormalCY * v0;");
				document.linef("        final float surfaceNormal3Z = surfaceNormalAZ * w + surfaceNormalBZ * u0 + surfaceNormalCZ * v0;");
				document.linef("        final float surfaceNormal3LengthReciprocal = rsqrt(surfaceNormal3X * surfaceNormal3X + surfaceNormal3Y * surfaceNormal3Y + surfaceNormal3Z * surfaceNormal3Z);");
				document.linef("        final float surfaceNormal4X = surfaceNormal3X * surfaceNormal3LengthReciprocal;");
				document.linef("        final float surfaceNormal4Y = surfaceNormal3Y * surfaceNormal3LengthReciprocal;");
				document.linef("        final float surfaceNormal4Z = surfaceNormal3Z * surfaceNormal3LengthReciprocal;");
				document.linef("        final float dotProduct = surfaceNormalAX != 0.0F && surfaceNormalAY != 0.0F && surfaceNormalAZ != 0.0F ? surfaceNormal4X * surfaceNormalAX + surfaceNormal4Y * surfaceNormalAY + surfaceNormal4Z * surfaceNormalAZ : 0.0F;");
				document.linef("        final float surfaceNormal5X = dotProduct < 0.0F ? -surfaceNormal4X : surfaceNormal4X;");
				document.linef("        final float surfaceNormal5Y = dotProduct < 0.0F ? -surfaceNormal4Y : surfaceNormal4Y;");
				document.linef("        final float surfaceNormal5Z = dotProduct < 0.0F ? -surfaceNormal4Z : surfaceNormal4Z;");
				document.linef("        ");
				document.linef("//      Update the intersections array based on Gouraud Shading:");
				document.linef("        this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal5X;");
				document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal5Y;");
				document.linef("        this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal5Z;");
				document.linef("        this.intersections[offsetIntersectionUVCoordinates] = u2;");
				document.linef("        this.intersections[offsetIntersectionUVCoordinates + 1] = v2;");
			}
			
			document.linef("    }");
		} else {
			document.linef("//  private void doCalculateSurfacePropertiesForTriangle(final float distance, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ, final int shapesOffset) {}");
		}
	}
	
	private static void doGenerateDoCalculateTextureColorMethod(final Document document) {
		document.linef("    private void doCalculateTextureColor(final int relativeOffsetTextures, final int shapesOffset) {");
		document.linef("        final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);");
		document.linef("        final int texturesOffset = (int)(this.surfaces[surfacesOffset + relativeOffsetTextures]);");
		document.linef("        final int textureType = (int)(this.textures[texturesOffset]);");
		document.linef("        ");
		document.linef("        if(textureType == CompiledScene.CHECKERBOARD_TEXTURE_TYPE) {");
		document.linef("            doCalculateTextureColorForCheckerboardTexture(texturesOffset);");
		document.linef("        } else if(textureType == CompiledScene.IMAGE_TEXTURE_TYPE) {");
		document.linef("            doCalculateTextureColorForImageTexture(texturesOffset);");
		document.linef("        } else if(textureType == CompiledScene.SOLID_TEXTURE_TYPE) {");
		document.linef("            doCalculateTextureColorForSolidTexture(texturesOffset);");
		document.linef("        }");
		document.linef("    }");
	}
	
	private static void doGenerateDoCalculateTextureColorForCheckerboardTextureMethod(final Document document) {
		document.linef("    private void doCalculateTextureColorForCheckerboardTexture(final int texturesOffset) {");
		document.linef("//      Get the intersections offset:");
		document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final int offsetUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;");
		document.linef("        final int offsetColor0 = texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_0;");
		document.linef("        final int offsetColor1 = texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_COLOR_1;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float u = this.intersections[offsetUVCoordinates];");
		document.linef("        final float v = this.intersections[offsetUVCoordinates + 1];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float color0R = this.textures[offsetColor0];");
		document.linef("        final float color0G = this.textures[offsetColor0 + 1];");
		document.linef("        final float color0B = this.textures[offsetColor0 + 2];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float color1R = this.textures[offsetColor1];");
		document.linef("        final float color1G = this.textures[offsetColor1 + 1];");
		document.linef("        final float color1B = this.textures[offsetColor1 + 2];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float sU = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_U];");
		document.linef("        final float sV = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_SCALE_V];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float cosAngle = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_RADIANS_COS];");
		document.linef("        final float sinAngle = this.textures[texturesOffset + CompiledScene.CHECKERBOARD_TEXTURE_RELATIVE_OFFSET_RADIANS_SIN];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float textureU = modulo((u * cosAngle - v * sinAngle) * sU);");
		document.linef("        final float textureV = modulo((v * cosAngle + u * sinAngle) * sV);");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final boolean isDarkU = textureU > 0.5F;");
		document.linef("        final boolean isDarkV = textureV > 0.5F;");
		document.linef("        final boolean isDark = isDarkU ^ isDarkV;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final int pixelIndex0 = getLocalId() * 3;");
		document.linef("        ");
		document.linef("        if(color0R == color1R && color0G == color1G && color0B == color1B) {");
		document.linef("//          TODO: Write explanation!");
		document.linef("            final float textureMultiplier = isDark ? 0.8F : 1.2F;");
		document.linef("            ");
		document.linef("//          TODO: Write explanation!");
		document.linef("            final float r = color0R * textureMultiplier;");
		document.linef("            final float g = color0G * textureMultiplier;");
		document.linef("            final float b = color0B * textureMultiplier;");
		document.linef("            ");
		document.linef("//          TODO: Write explanation!");
		document.linef("            this.temporaryColors[pixelIndex0] = r;");
		document.linef("            this.temporaryColors[pixelIndex0 + 1] = g;");
		document.linef("            this.temporaryColors[pixelIndex0 + 2] = b;");
		document.linef("        } else {");
		document.linef("//          TODO: Write explanation!");
		document.linef("            final float r = isDark ? color0R : color1R;");
		document.linef("            final float g = isDark ? color0G : color1G;");
		document.linef("            final float b = isDark ? color0B : color1B;");
		document.linef("            ");
		document.linef("//          TODO: Write explanation!");
		document.linef("            this.temporaryColors[pixelIndex0] = r;");
		document.linef("            this.temporaryColors[pixelIndex0 + 1] = g;");
		document.linef("            this.temporaryColors[pixelIndex0 + 2] = b;");
		document.linef("        }");
		document.linef("    }");
	}
	
	private static void doGenerateDoCalculateTextureColorForImageTextureMethod(final Document document) {
		document.linef("    private void doCalculateTextureColorForImageTexture(final int texturesOffset) {");
		document.linef("//      Get the intersections offset:");
		document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final int offsetUVCoordinates = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float u = this.intersections[offsetUVCoordinates];");
		document.linef("        final float v = this.intersections[offsetUVCoordinates + 1];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float width = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_WIDTH];");
		document.linef("        final float height = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_HEIGHT];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float scaleU = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_U];");
		document.linef("        final float scaleV = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_SCALE_V];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float cosAngle = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_RADIANS_COS];");
		document.linef("        final float sinAngle = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_RADIANS_SIN];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float x0 = (int)((u * cosAngle - v * sinAngle) * (width * scaleU));");
		document.linef("        final float y0 = (int)((v * cosAngle + u * sinAngle) * (height * scaleV));");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float x1 = abs(x0);");
		document.linef("        final float y1 = abs(y0);");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float x2 = remainder(x1, width);");
		document.linef("        final float y2 = remainder(y1, height);");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final int index = (int)((y2 * width + x2) * 3);");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final float r = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index];");
		document.linef("        final float g = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index + 1];");
		document.linef("        final float b = this.textures[texturesOffset + CompiledScene.IMAGE_TEXTURE_RELATIVE_OFFSET_DATA + index + 2];");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        final int pixelIndex0 = getLocalId() * 3;");
		document.linef("        ");
		document.linef("//      TODO: Write explanation!");
		document.linef("        this.temporaryColors[pixelIndex0] = r;");
		document.linef("        this.temporaryColors[pixelIndex0 + 1] = g;");
		document.linef("        this.temporaryColors[pixelIndex0 + 2] = b;");
		document.linef("    }");
	}
	
	private static void doGenerateDoCalculateTextureColorForSolidTextureMethod(final Document document) {
		document.linef("    private void doCalculateTextureColorForSolidTexture(final int texturesOffset) {");
		document.linef("//      Calculate the color offset:");
		document.linef("        final int offsetColor0 = texturesOffset + CompiledScene.SOLID_TEXTURE_RELATIVE_OFFSET_COLOR;");
		document.linef("        ");
		document.linef("//      Retrieve the R-, G- and B-component values of the texture:");
		document.linef("        final float r = this.textures[offsetColor0];");
		document.linef("        final float g = this.textures[offsetColor0 + 1];");
		document.linef("        final float b = this.textures[offsetColor0 + 2];");
		document.linef("        ");
		document.linef("//      Calculate the pixel index:");
		document.linef("        final int pixelIndex0 = getLocalId() * 3;");
		document.linef("        ");
		document.linef("//      Update the temporaryColors array with the color of the texture:");
		document.linef("        this.temporaryColors[pixelIndex0] = r;");
		document.linef("        this.temporaryColors[pixelIndex0 + 1] = g;");
		document.linef("        this.temporaryColors[pixelIndex0 + 2] = b;");
		document.linef("    }");
	}
	
	private static void doGenerateDoCreatePrimaryRayMethod(final Document document, final RendererType rendererType) {
		document.linef("    private boolean doCreatePrimaryRay(final int pixelIndex) {");
		document.linef("//      Calculate the X- and Y-coordinates on the screen:");
		document.linef("        final int y = pixelIndex / this.width;");
		document.linef("        final int x = pixelIndex - y * this.width;");
		document.linef("        ");
		document.linef("//      Retrieve the current X-, Y- and Z-coordinates of the camera lens (eye) in the scene:");
		document.linef("        final float eyeX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE_X];");
		document.linef("        final float eyeY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE_Y];");
		document.linef("        final float eyeZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_EYE_Z];");
		document.linef("        ");
		document.linef("//      Retrieve the current U-vector for the orthonormal basis frame of the camera lens (eye) in the scene:");
		document.linef("        final float uX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_X];");
		document.linef("        final float uY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Y];");
		document.linef("        final float uZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U_Z];");
		document.linef("        ");
		document.linef("//      Retrieve the current V-vector for the orthonormal basis frame of the camera lens (eye) in the scene:");
		document.linef("        final float vX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_X];");
		document.linef("        final float vY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Y];");
		document.linef("        final float vZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V_Z];");
		document.linef("        ");
		document.linef("//      Retrieve the current W-vector for the orthonormal basis frame of the camera lens (eye) in the scene:");
		document.linef("        final float wX = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_X];");
		document.linef("        final float wY = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Y];");
		document.linef("        final float wZ = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W_Z];");
		document.linef("        ");
		document.linef("//      Calculate the Field of View:");
		document.linef("        final float fieldOfViewX0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_X];");
		document.linef("        final float fieldOfViewX1 = tan(fieldOfViewX0 * PI_DIVIDED_BY_360);");
		document.linef("        final float fieldOfViewY0 = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FIELD_OF_VIEW_Y];");
		document.linef("        final float fieldOfViewY1 = tan(-fieldOfViewY0 * PI_DIVIDED_BY_360);");
		document.linef("        ");
		document.linef("//      Calculate the horizontal direction:");
		document.linef("        final float horizontalX = uX * fieldOfViewX1;");
		document.linef("        final float horizontalY = uY * fieldOfViewX1;");
		document.linef("        final float horizontalZ = uZ * fieldOfViewX1;");
		document.linef("        ");
		document.linef("//      Calculate the vertical direction:");
		document.linef("        final float verticalX = vX * fieldOfViewY1;");
		document.linef("        final float verticalY = vY * fieldOfViewY1;");
		document.linef("        final float verticalZ = vZ * fieldOfViewY1;");
		document.linef("        ");
		document.linef("//      Calculate the pixel jitter:");
		document.linef("        final float jitterX = %s;", rendererType == RendererType.PATH_TRACER ? "nextFloat() - 0.5F" : "0.5F");
		document.linef("        final float jitterY = %s;", rendererType == RendererType.PATH_TRACER ? "nextFloat() - 0.5F" : "0.5F");
		document.linef("        ");
		document.linef("//      Calculate the pixel sample point:");
		document.linef("        final float sx = (jitterX + x) / (this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_RESOLUTION_X] - 1.0F);");
		document.linef("        final float sy = (jitterY + y) / (this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_RESOLUTION_Y] - 1.0F);");
		document.linef("        final float sx0 = 2.0F * sx - 1.0F;");
		document.linef("        final float sy0 = 2.0F * sy - 1.0F;");
		document.linef("        ");
		document.linef("//      Initialize w to 1.0F:");
		document.linef("        float w = 1.0F;");
		document.linef("        ");
		document.linef("//      Retrieve whether or not this camera uses a Fisheye camera lens:");
		document.linef("        final boolean isFisheyeCameraLens = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_CAMERA_LENS] == Camera.CAMERA_LENS_FISHEYE;");
		document.linef("        ");
		document.linef("        if(isFisheyeCameraLens) {");
		document.linef("//          Calculate the dot product that will be used in determining if the current pixel is outside the camera lens:");
		document.linef("            final float dotProduct = sx0 * sx0 + sy0 * sy0;");
		document.linef("            ");
		document.linef("            if(dotProduct > 1.0F) {");
		document.linef("                localBarrier();");
		document.linef("                ");
		document.linef("                return false;");
		document.linef("            }");
		document.linef("            ");
		document.linef("//          Update the w variable:");
		document.linef("            w = sqrt(1.0F - dotProduct);");
		document.linef("        }");
		document.linef("        ");
		document.linef("//      Calculate the middle point:");
		document.linef("        final float middleX = eyeX + wX * w;");
		document.linef("        final float middleY = eyeY + wY * w;");
		document.linef("        final float middleZ = eyeZ + wZ * w;");
		document.linef("        ");
		document.linef("//      Calculate the point on the plane one unit away from the eye:");
		document.linef("        final float pointOnPlaneOneUnitAwayFromEyeX = middleX + (horizontalX * sx0) + (verticalX * sy0);");
		document.linef("        final float pointOnPlaneOneUnitAwayFromEyeY = middleY + (horizontalY * sx0) + (verticalY * sy0);");
		document.linef("        final float pointOnPlaneOneUnitAwayFromEyeZ = middleZ + (horizontalZ * sx0) + (verticalZ * sy0);");
		document.linef("        ");
		document.linef("//      Retrieve the focal distance:");
		document.linef("        final float focalDistance = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_FOCAL_DISTANCE];");
		document.linef("        ");
		document.linef("//      Calculate the point on the image plane:");
		document.linef("        final float pointOnImagePlaneX = eyeX + (pointOnPlaneOneUnitAwayFromEyeX - eyeX) * focalDistance;");
		document.linef("        final float pointOnImagePlaneY = eyeY + (pointOnPlaneOneUnitAwayFromEyeY - eyeY) * focalDistance;");
		document.linef("        final float pointOnImagePlaneZ = eyeZ + (pointOnPlaneOneUnitAwayFromEyeZ - eyeZ) * focalDistance;");
		document.linef("        ");
		document.linef("//      Retrieve the aperture radius:");
		document.linef("        final float apertureRadius = this.cameraArray[Camera.ABSOLUTE_OFFSET_OF_APERTURE_RADIUS];");
		document.linef("        ");
		document.linef("//      Initialize the X-, Y- and Z-coordinates of the aperture point:");
		document.linef("        float aperturePointX = eyeX;");
		document.linef("        float aperturePointY = eyeY;");
		document.linef("        float aperturePointZ = eyeZ;");
		document.linef("        ");
		document.linef("//      Check if Depth of Field (DoF) is enabled:");
		document.linef("        if(apertureRadius > 0.00001F) {");
		document.linef("//          Calculate two random values:");
		document.linef("            final float random1 = nextFloat();");
		document.linef("            final float random2 = nextFloat();");
		document.linef("            ");
		document.linef("//          Calculate the angle:");
		document.linef("            final float angle = PI_MULTIPLIED_BY_TWO * random1;");
		document.linef("            ");
		document.linef("//          Calculate the distance:");
		document.linef("            final float distance = apertureRadius * sqrt(random2);");
		document.linef("            ");
		document.linef("//          Calculate the aperture:");
		document.linef("            final float apertureX = cos(angle) * distance;");
		document.linef("            final float apertureY = sin(angle) * distance;");
		document.linef("            ");
		document.linef("//          Update the aperture point:");
		document.linef("            aperturePointX = eyeX + uX * apertureX + vX * apertureY;");
		document.linef("            aperturePointY = eyeY + uY * apertureX + vY * apertureY;");
		document.linef("            aperturePointZ = eyeZ + uZ * apertureX + vZ * apertureY;");
		document.linef("        }");
		document.linef("        ");
		document.linef("//      Calculate the aperture to image plane:");
		document.linef("        final float apertureToImagePlane0X = pointOnImagePlaneX - aperturePointX;");
		document.linef("        final float apertureToImagePlane0Y = pointOnImagePlaneY - aperturePointY;");
		document.linef("        final float apertureToImagePlane0Z = pointOnImagePlaneZ - aperturePointZ;");
		document.linef("        final float apertureToImagePlane0LengthReciprocal = rsqrt(apertureToImagePlane0X * apertureToImagePlane0X + apertureToImagePlane0Y * apertureToImagePlane0Y + apertureToImagePlane0Z * apertureToImagePlane0Z);");
		document.linef("        final float apertureToImagePlane1X = apertureToImagePlane0X * apertureToImagePlane0LengthReciprocal;");
		document.linef("        final float apertureToImagePlane1Y = apertureToImagePlane0Y * apertureToImagePlane0LengthReciprocal;");
		document.linef("        final float apertureToImagePlane1Z = apertureToImagePlane0Z * apertureToImagePlane0LengthReciprocal;");
		document.linef("        ");
		document.linef("//      Calculate the offset in the rays array:");
		document.linef("        final int raysOffset = getLocalId() * SIZE_RAY;");
		document.linef("        ");
		document.linef("//      Update the rays array with information:");
		document.linef("        this.rays[raysOffset + 0] = aperturePointX;");
		document.linef("        this.rays[raysOffset + 1] = aperturePointY;");
		document.linef("        this.rays[raysOffset + 2] = aperturePointZ;");
		document.linef("        this.rays[raysOffset + 3] = apertureToImagePlane1X;");
		document.linef("        this.rays[raysOffset + 4] = apertureToImagePlane1Y;");
		document.linef("        this.rays[raysOffset + 5] = apertureToImagePlane1Z;");
		document.linef("        ");
		document.linef("        localBarrier();");
		document.linef("        ");
		document.linef("        return true;");
		document.linef("    }");
	}
	
	private static void doGenerateDoFastFloorMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private static int doFastFloor(final float value) {");
			document.linef("        final int i = (int)(value);");
			document.linef("        ");
			document.linef("        return value < i ? i - 1 : i;");
			document.linef("    }");
		} else {
			document.linef("//  private static int doFastFloor(final float value) {}");
		}
	}
	
	private static void doGenerateDoGetYMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private float doGetY(final float x, final float z) {");
			document.linef("        return doSimplexFractalXY(2, x, z);");
			document.linef("    }");
		} else {
			document.linef("//  private float doGetY(final float x, final float z) {}");
		}
	}
	
	private static void doGenerateDoGradientXMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    /*");
			document.linef("    private static float doGradientX(final int hash, final float x) {");
			document.linef("        final int hash0 = hash & 0x0F;");
			document.linef("        ");
			document.linef("        float gradient = 1.0F + (hash0 & 7);");
			document.linef("        ");
			document.linef("        if((hash0 & 8) != 0) {");
			document.linef("            gradient = -gradient;");
			document.linef("        }");
			document.linef("        ");
			document.linef("        return gradient * x;");
			document.linef("    }");
			document.linef("    */");
		} else {
			document.linef("//  private static float doGradientX(final int hash, final float x) {}");
		}
	}
	
	private static void doGenerateDoGradientXYMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private static float doGradientXY(final int hash, final float x, final float y) {");
			document.linef("        final int hash0 = hash & 0x3F;");
			document.linef("        ");
			document.linef("        final float u = hash0 < 4 ? x : y;");
			document.linef("        final float v = hash0 < 4 ? y : x;");
			document.linef("        ");
			document.linef("        return ((hash0 & 1) == 1 ? -u : u) + ((hash0 & 2) == 1 ? -2.0F * v : 2.0F * v);");
			document.linef("    }");
		} else {
			document.linef("//  private static float doGradientXY(final int hash, final float x, final float y) {}");
		}
	}
	
	private static void doGenerateDoHashMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private int doHash(final int index) {");
			document.linef("        return this.permutations1[index %% this.permutations1.length];");
			document.linef("    }");
		} else {
			document.linef("//  private int doHash(final int index) {}");
		}
	}
	
	private static void doGenerateDoIntersectMethod(final Document document, final CompiledScene compiledScene) {
		final boolean hasPlanes = compiledScene.hasPlanes();
		final boolean hasSpheres = compiledScene.hasSpheres();
		
		if(hasPlanes || hasSpheres) {
			document.linef("    private float doIntersect(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {");
			
			if(hasPlanes && !hasSpheres) {
				document.linef("        return doIntersectPlane(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);");
			} else if(!hasPlanes && hasSpheres) {
				document.linef("        return doIntersectSphere(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);");
			} else {
				document.linef("//      Retrieve the type of the shape:");
				document.linef("        final int type = (int)(this.shapes[shapesOffset]);");
				document.linef("        ");
				document.linef("        if(type == CompiledScene.PLANE_TYPE) {");
				document.linef("            return doIntersectPlane(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);");
				document.linef("        } else if(type == CompiledScene.SPHERE_TYPE) {");
				document.linef("            return doIntersectSphere(shapesOffset, originX, originY, originZ, directionX, directionY, directionZ);");
				document.linef("        }");
				document.linef("        ");
				document.linef("//      Return no hit:");
				document.linef("        return INFINITY;");
			}
			
			document.linef("    }");
		} else {
			document.linef("//  private float doIntersect(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {}");
		}
	}
	
	private static void doGenerateDoIntersectPlaneMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasPlanes()) {
			document.linef("    private float doIntersectPlane(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {");
			document.linef("//      Calculate the offset to the surface normal of the plane:");
			document.linef("        final int offsetSurfaceNormal = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_SURFACE_NORMAL_VECTOR3S_OFFSET]);");
			document.linef("        ");
			document.linef("//      Retrieve the surface normal of the plane:");
			document.linef("        final float surfaceNormalX = this.vector3s[offsetSurfaceNormal];");
			document.linef("        final float surfaceNormalY = this.vector3s[offsetSurfaceNormal + 1];");
			document.linef("        final float surfaceNormalZ = this.vector3s[offsetSurfaceNormal + 2];");
			document.linef("        ");
			document.linef("//      Calculate the dot product between the surface normal and the ray direction:");
			document.linef("        final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;");
			document.linef("        ");
			document.linef("//      Check that the dot product is not 0.0:");
			document.linef("        if(dotProduct < 0.0F || dotProduct > 0.0F) {");
			document.linef("//          Calculate the offset to the point denoted as A of the plane:");
			document.linef("            final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.PLANE_RELATIVE_OFFSET_A_POINT3S_OFFSET]);");
			document.linef("            ");
			document.linef("//          Retrieve the X-, Y- and Z-coordinates of the point A:");
			document.linef("            final float aX = this.point3s[offsetA];");
			document.linef("            final float aY = this.point3s[offsetA + 1];");
			document.linef("            final float aZ = this.point3s[offsetA + 2];");
			document.linef("            ");
			document.linef("//          Calculate the distance:");
			document.linef("            final float distance = ((aX - originX) * surfaceNormalX + (aY - originY) * surfaceNormalY + (aZ - originZ) * surfaceNormalZ) / dotProduct;");
			document.linef("            ");
			document.linef("//          Check that the distance is greater than an epsilon value and return it if so:");
			document.linef("            if(distance > EPSILON) {");
			document.linef("                return distance;");
			document.linef("            }");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      Return no hit:");
			document.linef("        return INFINITY;");
			document.linef("    }");
		} else {
			document.linef("//  private float doIntersectPlane(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {}");
		}
	}
	
	private static void doGenerateDoIntersectSphereMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasSpheres()) {
			document.linef("    private float doIntersectSphere(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {");
			document.linef("//      Calculate the offset to the center position of the sphere:");
			document.linef("        final int offsetPosition = (int)(this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_POSITION_POINT3S_OFFSET]);");
			document.linef("        ");
			document.linef("//      Retrieve the center position of the sphere:");
			document.linef("        final float positionX = this.point3s[offsetPosition];");
			document.linef("        final float positionY = this.point3s[offsetPosition + 1];");
			document.linef("        final float positionZ = this.point3s[offsetPosition + 2];");
			document.linef("        ");
			document.linef("//      Retrieve the radius of the sphere:");
			document.linef("        final float radius = this.shapes[shapesOffset + CompiledScene.SPHERE_RELATIVE_OFFSET_RADIUS];");
			document.linef("        ");
			document.linef("//      Calculate the direction to the sphere center:");
			document.linef("        final float x = positionX - originX;");
			document.linef("        final float y = positionY - originY;");
			document.linef("        final float z = positionZ - originZ;");
			document.linef("        ");
			document.linef("//      Calculate the dot product between the ray direction and the direction to the sphere center position:");
			document.linef("        final float b = x * directionX + y * directionY + z * directionZ;");
			document.linef("        ");
			document.linef("//      Calculate the determinant:");
			document.linef("        final float determinant0 = b * b - (x * x + y * y + z * z) + radius * radius;");
			document.linef("        ");
			document.linef("//      Check that the determinant is positive:");
			document.linef("        if(determinant0 >= 0.0F) {");
			document.linef("//          Calculate the square root of the determinant:");
			document.linef("            final float determinant1 = sqrt(determinant0);");
			document.linef("            ");
			document.linef("//          Calculate the first distance:");
			document.linef("            final float distance1 = b - determinant1;");
			document.linef("            ");
			document.linef("//          Check that the first distance is greater than an epsilon value and return it if so:");
			document.linef("            if(distance1 > EPSILON) {");
			document.linef("                return distance1;");
			document.linef("            }");
			document.linef("            ");
			document.linef("//          Calculate the second distance:");
			document.linef("            final float distance2 = b + determinant1;");
			document.linef("            ");
			document.linef("//          Check that the second distance is greater than an epsilon value and return it if so:");
			document.linef("            if(distance2 > EPSILON) {");
			document.linef("                return distance2;");
			document.linef("            }");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      Return no hit:");
			document.linef("        return INFINITY;");
			document.linef("    }");
		} else {
			document.linef("//  private float doIntersectSphere(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {}");
		}
	}
	
	private static void doGenerateDoIntersectTriangleMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasTriangles()) {
			document.linef("    private float doIntersectTriangle(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {");
			document.linef("//      Calculate the offsets to the points A, B and C of the triangle:");
			document.linef("        final int offsetA = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_A_POINT3S_OFFSET]);");
			document.linef("        final int offsetB = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_B_POINT3S_OFFSET]);");
			document.linef("        final int offsetC = (int)(this.shapes[shapesOffset + CompiledScene.TRIANGLE_RELATIVE_OFFSET_POINT_C_POINT3S_OFFSET]);");
			document.linef("        ");
			document.linef("//      Retrieve point A of the triangle:");
			document.linef("        final float aX = this.point3s[offsetA];");
			document.linef("        final float aY = this.point3s[offsetA + 1];");
			document.linef("        final float aZ = this.point3s[offsetA + 2];");
			document.linef("        ");
			document.linef("//      Retrieve point B of the triangle:");
			document.linef("        final float bX = this.point3s[offsetB];");
			document.linef("        final float bY = this.point3s[offsetB + 1];");
			document.linef("        final float bZ = this.point3s[offsetB + 2];");
			document.linef("        ");
			document.linef("//      Retrieve point C of the triangle:");
			document.linef("        final float cX = this.point3s[offsetC];");
			document.linef("        final float cY = this.point3s[offsetC + 1];");
			document.linef("        final float cZ = this.point3s[offsetC + 2];");
			document.linef("        ");
			document.linef("//      Calculate the first edge between the points A and B:");
			document.linef("        final float edge0X = bX - aX;");
			document.linef("        final float edge0Y = bY - aY;");
			document.linef("        final float edge0Z = bZ - aZ;");
			document.linef("        ");
			document.linef("//      Calculate the second edge between the points A and C:");
			document.linef("        final float edge1X = cX - aX;");
			document.linef("        final float edge1Y = cY - aY;");
			document.linef("        final float edge1Z = cZ - aZ;");
			document.linef("        ");
			document.linef("//      Calculate the cross product:");
			document.linef("        final float v0X = directionY * edge1Z - directionZ * edge1Y;");
			document.linef("        final float v0Y = directionZ * edge1X - directionX * edge1Z;");
			document.linef("        final float v0Z = directionX * edge1Y - directionY * edge1X;");
			document.linef("        ");
			document.linef("//      Calculate the determinant:");
			document.linef("        final float determinant = edge0X * v0X + edge0Y * v0Y + edge0Z * v0Z;");
			document.linef("        ");
			document.linef("//      Initialize the distance to a value denoting no hit:");
			document.linef("        float t = INFINITY;");
			document.linef("        ");
			document.linef("//      Check that the determinant is anything other than in the range of negative epsilon and posive epsilon:");
			document.linef("        if(determinant < -EPSILON || determinant > EPSILON) {");
			document.linef("//          Calculate the reciprocal of the determinant:");
			document.linef("            final float determinantReciprocal = 1.0F / determinant;");
			document.linef("            ");
			document.linef("//          Calculate the direction to the point A:");
			document.linef("            final float v1X = originX - aX;");
			document.linef("            final float v1Y = originY - aY;");
			document.linef("            final float v1Z = originZ - aZ;");
			document.linef("            ");
			document.linef("//          Calculate the U value:");
			document.linef("            final float u = (v1X * v0X + v1Y * v0Y + v1Z * v0Z) * determinantReciprocal;");
			document.linef("            ");
			document.linef("//          Check that the U value is between 0.0 and 1.0:");
			document.linef("            if(u >= 0.0F && u <= 1.0F) {");
			document.linef("//              Calculate the cross product:");
			document.linef("                final float v2X = v1Y * edge0Z - v1Z * edge0Y;");
			document.linef("                final float v2Y = v1Z * edge0X - v1X * edge0Z;");
			document.linef("                final float v2Z = v1X * edge0Y - v1Y * edge0X;");
			document.linef("                ");
			document.linef("//              Calculate the V value:");
			document.linef("                final float v = (directionX * v2X + directionY * v2Y + directionZ * v2Z) * determinantReciprocal;");
			document.linef("                ");
			document.linef("//              Update the distance value:");
			document.linef("                t = v >= 0.0F && u + v <= 1.0F ? (edge1X * v2X + edge1Y * v2Y + edge1Z * v2Z) * determinantReciprocal : EPSILON;");
			document.linef("                t = t > EPSILON ? t : INFINITY;");
			document.linef("            }");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      Return the distance:");
			document.linef("        return t;");
			document.linef("    }");
		} else {
			document.linef("//  private float doIntersectTriangle(final int shapesOffset, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {}");
		}
	}
	
	private static void doGenerateDoPathTracingMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.PATH_TRACER) {
			document.linef("    private void doPathTracing() {");
			document.linef("//      Retrieve the maximum depth allowed and the depth at which to use Russian Roulette to test for path termination:");
			document.linef("        final int depthMaximum = this.depthMaximum;");
			document.linef("        final int depthRussianRoulette = this.depthRussianRoulette;");
			document.linef("        ");
			document.linef("//      Calculate the current offsets to the intersections and rays arrays:");
			document.linef("        final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        final int raysOffset = getLocalId() * SIZE_RAY;");
			document.linef("        ");
			document.linef("//      Initialize the current depth:");
			document.linef("        int depthCurrent = 0;");
			document.linef("        ");
			document.linef("//      Retrieve the offsets of the ray origin and the ray direction:");
			document.linef("        final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;");
			document.linef("        final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;");
			document.linef("        ");
			document.linef("//      Initialize the origin from the primary ray:");
			document.linef("        float originX = this.rays[offsetOrigin];");
			document.linef("        float originY = this.rays[offsetOrigin + 1];");
			document.linef("        float originZ = this.rays[offsetOrigin + 2];");
			document.linef("        ");
			document.linef("//      Initialize the direction from the primary ray:");
			document.linef("        float directionX = this.rays[offsetDirection];");
			document.linef("        float directionY = this.rays[offsetDirection + 1];");
			document.linef("        float directionZ = this.rays[offsetDirection + 2];");
			document.linef("        ");
			document.linef("//      Initialize the pixel color to black:");
			document.linef("        float pixelColorR = 0.0F;");
			document.linef("        float pixelColorG = 0.0F;");
			document.linef("        float pixelColorB = 0.0F;");
			document.linef("        ");
			document.linef("//      Initialize the radiance multiplier to white:");
			document.linef("        float radianceMultiplierR = 1.0F;");
			document.linef("        float radianceMultiplierG = 1.0F;");
			document.linef("        float radianceMultiplierB = 1.0F;");
			document.linef("        ");
			document.linef("//      Retrieve the pixel index:");
			document.linef("        final int pixelIndex0 = getLocalId() * 3;");
			document.linef("        ");
			document.linef("//      Initialize the offset of the shape to skip to -1:");
			document.linef("        int shapesOffsetToSkip = -1;");
			document.linef("        ");
			document.linef("//      Run the following do-while-loop as long as the current depth is less than the maximum depth and Russian Roulette does not terminate:");
			document.linef("        do {");
			document.linef("//          Perform an intersection test:");
			document.linef("            doPerformIntersectionTest(shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("            ");
			document.linef("//          Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:");
			document.linef("            final float distance = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];");
			document.linef("            ");
			document.linef("//          Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:");
			document.linef("            final int shapesOffset = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);");
			document.linef("            ");
			document.linef("//          Test that an intersection was actually made, and if not, return black color (or possibly the background color):");
			document.linef("            if(distance == INFINITY || shapesOffset == -1) {");
			document.linef("//              Calculate the color for the sky in the current direction:");
			document.linef("                doCalculateColorForSky(directionX, directionY, directionZ);");
			document.linef("                ");
			document.linef("//              Add the color for the sky to the current pixel color:");
			document.linef("                pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];");
			document.linef("                pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];");
			document.linef("                pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];");
			document.linef("                ");
			document.linef("//              Update the current pixel color:");
			document.linef("                this.currentPixelColors[pixelIndex0] = pixelColorR;");
			document.linef("                this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;");
			document.linef("                this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;");
			document.linef("                ");
			document.linef("                return;");
			document.linef("            }");
			document.linef("            ");
			document.linef("//          Retrieve the offset to the surfaces array for the given shape:");
			document.linef("            final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);");
			document.linef("            ");
			document.linef("//          Update the offset of the shape to skip to the current offset:");
			document.linef("            shapesOffsetToSkip = shapesOffset;");
			document.linef("            ");
			document.linef("//          Retrieve the offsets of the surface intersection point and the surface normal:");
			document.linef("            final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;");
			document.linef("            final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("            ");
			document.linef("//          Retrieve the surface intersection point:");
			document.linef("            final float surfaceIntersectionPointX = this.intersections[offsetIntersectionSurfaceIntersectionPoint];");
			document.linef("            final float surfaceIntersectionPointY = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1];");
			document.linef("            final float surfaceIntersectionPointZ = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2];");
			document.linef("            ");
			document.linef("//          Retrieve the surface normal:");
			document.linef("            final float surfaceNormalX = this.intersections[offsetIntersectionSurfaceNormal];");
			document.linef("            final float surfaceNormalY = this.intersections[offsetIntersectionSurfaceNormal + 1];");
			document.linef("            final float surfaceNormalZ = this.intersections[offsetIntersectionSurfaceNormal + 2];");
			document.linef("            ");
			document.linef("//          Calculate the albedo texture color for the intersected shape:");
			document.linef("            doCalculateTextureColor(CompiledScene.SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO, shapesOffset);");
			document.linef("            ");
			document.linef("//          Get the color of the shape from the albedo texture color that was looked up:");
			document.linef("            float albedoColorR = this.temporaryColors[pixelIndex0];");
			document.linef("            float albedoColorG = this.temporaryColors[pixelIndex0 + 1];");
			document.linef("            float albedoColorB = this.temporaryColors[pixelIndex0 + 2];");
			document.linef("            ");
			document.linef("//          Retrieve the offset of the emission:");
			document.linef("            final int offsetEmission = surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_EMISSION;");
			document.linef("            ");
			document.linef("//          Retrieve the emission from the intersected shape:");
			document.linef("            final float emissionR = this.surfaces[offsetEmission];");
			document.linef("            final float emissionG = this.surfaces[offsetEmission + 1];");
			document.linef("            final float emissionB = this.surfaces[offsetEmission + 2];");
			document.linef("            ");
			document.linef("//          Add the current radiance multiplied by the emission of the intersected shape to the current pixel color:");
			document.linef("            pixelColorR += radianceMultiplierR * emissionR;");
			document.linef("            pixelColorG += radianceMultiplierG * emissionG;");
			document.linef("            pixelColorB += radianceMultiplierB * emissionB;");
			document.linef("            ");
			document.linef("//          Increment the current depth:");
			document.linef("            depthCurrent++;");
			document.linef("            ");
			document.linef("//          Check if the current depth is great enough to perform Russian Roulette to probabilistically terminate the path:");
			document.linef("            if(depthCurrent >= depthRussianRoulette) {");
			document.linef("//              Calculate the Russian Roulette Probability Density Function (PDF) using the maximum color component of the albedo of the intersected shape:");
			document.linef("                final float probabilityDensityFunction = max(albedoColorR, max(albedoColorG, albedoColorB));");
			document.linef("                ");
			document.linef("//              Calculate a random number that will be used when determining whether or not the path should be terminated:");
			document.linef("                final float random = nextFloat();");
			document.linef("                ");
			document.linef("//              If the random number is greater than or equal to the Russian Roulette PDF, then terminate the path:");
			document.linef("                if(random >= probabilityDensityFunction) {");
			document.linef("//                  Perform an intersection test:");
			document.linef("                    doPerformIntersectionTestOnly(shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("                    ");
			document.linef("//                  Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:");
			document.linef("                    final float distance0 = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];");
			document.linef("                    ");
			document.linef("//                  Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:");
			document.linef("                    final int shapesOffset0 = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);");
			document.linef("                    ");
			document.linef("//                  Test that an intersection was actually made, and if not, return black color (or possibly the background color):");
			document.linef("                    if(distance0 == INFINITY || shapesOffset0 == -1) {");
			document.linef("//                      Calculate the color for the sky in the current direction:");
			document.linef("                        doCalculateColorForSky(directionX, directionY, directionZ);");
			document.linef("                        ");
			document.linef("//                      Add the color for the sky to the current pixel color:");
			document.linef("                        pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];");
			document.linef("                        pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];");
			document.linef("                        pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];");
			document.linef("                    }");
			document.linef("                    ");
			document.linef("//                  Update the current pixel color:");
			document.linef("                    this.currentPixelColors[pixelIndex0] = pixelColorR;");
			document.linef("                    this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;");
			document.linef("                    this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;");
			document.linef("                    ");
			document.linef("                    return;");
			document.linef("                }");
			document.linef("                ");
			document.linef("//              Calculate the reciprocal of the Russian Roulette PDF, so no divisions are needed next:");
			document.linef("                final float probabilityDensityFunctionReciprocal = 1.0F / probabilityDensityFunction;");
			document.linef("                ");
			document.linef("//              Because the path was not terminated this time, the albedo color has to be multiplied with the reciprocal of the Russian Roulette PDF:");
			document.linef("                albedoColorR *= probabilityDensityFunctionReciprocal;");
			document.linef("                albedoColorG *= probabilityDensityFunctionReciprocal;");
			document.linef("                albedoColorB *= probabilityDensityFunctionReciprocal;");
			document.linef("            }");
			document.linef("            ");
			document.linef("//          Retrieve the material type of the intersected shape:");
			document.linef("            final int material = (int)(this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_MATERIAL]);");
			document.linef("            ");
			document.linef("//          Calculate the dot product between the surface normal of the intersected shape and the current ray direction:");
			document.linef("            final float dotProduct = surfaceNormalX * directionX + surfaceNormalY * directionY + surfaceNormalZ * directionZ;");
			document.linef("            final float dotProductMultipliedByTwo = dotProduct * 2.0F;");
			document.linef("            ");
			document.linef("//          Check if the surface normal is correctly oriented:");
			document.linef("            final boolean isCorrectlyOriented = dotProduct < 0.0F;");
			document.linef("            ");
			document.linef("//          Retrieve the correctly oriented surface normal:");
			document.linef("            final float w0X = isCorrectlyOriented ? surfaceNormalX : -surfaceNormalX;");
			document.linef("            final float w0Y = isCorrectlyOriented ? surfaceNormalY : -surfaceNormalY;");
			document.linef("            final float w0Z = isCorrectlyOriented ? surfaceNormalZ : -surfaceNormalZ;");
			document.linef("            ");
			document.linef("//          Pre-compute the random values that will be used later:");
			document.linef("            final float randomA = nextFloat();");
			document.linef("            final float randomB = nextFloat();");
			document.linef("            final float randomC = nextFloat();");
			document.linef("            ");
			document.linef("            if(material == MATERIAL_CLEAR_COAT) {");
			document.linef("//              TODO: Write explanation!");
			document.linef("                final float nnt = REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1;");
			document.linef("                ");
			document.linef("//              Calculate the dot product between the W direction and the current ray direction:");
			document.linef("                final float dotProductOfW0AndDirection = w0X * directionX + w0Y * directionY + w0Z * directionZ;");
			document.linef("                ");
			document.linef("//              Calculate the total internal reflection:");
			document.linef("                final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);");
			document.linef("                ");
			document.linef("//              Calculate the reflection direction:");
			document.linef("                final float reflectionDirectionX = directionX - surfaceNormalX * dotProductMultipliedByTwo;");
			document.linef("                final float reflectionDirectionY = directionY - surfaceNormalY * dotProductMultipliedByTwo;");
			document.linef("                final float reflectionDirectionZ = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;");
			document.linef("                ");
			document.linef("//              Initialize the specular color component values to be used:");
			document.linef("                final float specularColorR = 1.0F;");
			document.linef("                final float specularColorG = 1.0F;");
			document.linef("                final float specularColorB = 1.0F;");
			document.linef("                ");
			document.linef("                if(totalInternalReflection < 0.0F) {");
			document.linef("//                  Update the ray origin for the next iteration:");
			document.linef("                    originX = surfaceIntersectionPointX + w0X * 0.02F;");
			document.linef("                    originY = surfaceIntersectionPointY + w0Y * 0.02F;");
			document.linef("                    originZ = surfaceIntersectionPointZ + w0Z * 0.02F;");
			document.linef("                    ");
			document.linef("//                  Update the ray direction for the next iteration:");
			document.linef("                    directionX = reflectionDirectionX;");
			document.linef("                    directionY = reflectionDirectionY;");
			document.linef("                    directionZ = reflectionDirectionZ;");
			document.linef("                    ");
			document.linef("//                  Multiply the current radiance multiplier with the specular color:");
			document.linef("                    radianceMultiplierR *= specularColorR;");
			document.linef("                    radianceMultiplierG *= specularColorG;");
			document.linef("                    radianceMultiplierB *= specularColorB;");
			document.linef("                } else {");
			document.linef("//                  Calculate some angles:");
			document.linef("                    final float angle1 = -dotProductOfW0AndDirection;");
			document.linef("                    final float angle2 = 1.0F - angle1;");
			document.linef("                    ");
			document.linef("//                  Calculate the reflectance:");
			document.linef("                    final float reflectance = REFRACTIVE_INDEX_R0 + (1.0F - REFRACTIVE_INDEX_R0) * angle2 * angle2 * angle2 * angle2 * angle2;");
			document.linef("                    ");
			document.linef("//                  Calculate the transmittance:");
			document.linef("                    final float transmittance = 1.0F - reflectance;");
			document.linef("                    ");
			document.linef("//                  Calculate a probability for the reflection- or the transmission direction:");
			document.linef("                    final float probability = 0.25F + 0.5F * reflectance;");
			document.linef("                    ");
			document.linef("//                  Calculate the probability that the direction for the next iteration will be the reflection direction:");
			document.linef("                    final float reflectanceProbability = reflectance / probability;");
			document.linef("                    ");
			document.linef("//                  Calculate the probability that the direction for the next iteration will be the transmission direction:");
			document.linef("                    final float transmittanceProbability = transmittance / (1.0F - probability);");
			document.linef("                    ");
			document.linef("//                  Check if the direction for the next iteration is the reflection direction or the transmission direction:");
			document.linef("                    final boolean isReflectionDirection = randomA < probability;");
			document.linef("                    ");
			document.linef("//                  Retrieve the value to multiply the current radiance multiplier with:");
			document.linef("                    final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;");
			document.linef("                    ");
			document.linef("//                  Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:");
			document.linef("                    radianceMultiplierR *= multiplier;");
			document.linef("                    radianceMultiplierG *= multiplier;");
			document.linef("                    radianceMultiplierB *= multiplier;");
			document.linef("                    ");
			document.linef("                    if(isReflectionDirection) {");
			document.linef("//                      Update the ray origin for the next iteration:");
			document.linef("                        originX = surfaceIntersectionPointX + w0X * 0.02F;");
			document.linef("                        originY = surfaceIntersectionPointY + w0Y * 0.02F;");
			document.linef("                        originZ = surfaceIntersectionPointZ + w0Z * 0.02F;");
			document.linef("                        ");
			document.linef("//                      Update the ray direction for the next iteration:");
			document.linef("                        directionX = reflectionDirectionX;");
			document.linef("                        directionY = reflectionDirectionY;");
			document.linef("                        directionZ = reflectionDirectionZ;");
			document.linef("                        ");
			document.linef("//                      Multiply the current radiance multiplier with the specular color:");
			document.linef("                        radianceMultiplierR *= specularColorR;");
			document.linef("                        radianceMultiplierG *= specularColorG;");
			document.linef("                        radianceMultiplierB *= specularColorB;");
			document.linef("                    } else {");
			document.linef("//                      Compute some random values:");
			document.linef("                        final float random0 = PI_MULTIPLIED_BY_TWO * randomB;");
			document.linef("                        final float random0Cos = cos(random0);");
			document.linef("                        final float random0Sin = sin(random0);");
			document.linef("                        final float random1 = randomC;");
			document.linef("                        final float random1Squared0 = sqrt(random1);");
			document.linef("                        final float random1Squared1 = sqrt(1.0F - random1);");
			document.linef("                        ");
			document.linef("//                      Calculate the orthonormal basis W vector:");
			document.linef("                        final float w0LengthReciprocal = rsqrt(w0X * w0X + w0Y * w0Y + w0Z * w0Z);");
			document.linef("                        final float w1X = w0X * w0LengthReciprocal;");
			document.linef("                        final float w1Y = w0Y * w0LengthReciprocal;");
			document.linef("                        final float w1Z = w0Z * w0LengthReciprocal;");
			document.linef("                        ");
			document.linef("//                      Check if the direction is the Y-direction:");
			document.linef("                        final boolean isY = abs(w1X) > 0.1F;");
			document.linef("                        ");
			document.linef("//                      Calculate the orthonormal basis U vector:");
			document.linef("                        final float u0X = isY ? 0.0F : 1.0F;");
			document.linef("                        final float u0Y = isY ? 1.0F : 0.0F;");
			document.linef("                        final float u1X = u0Y * w1Z;");
			document.linef("                        final float u1Y = -(u0X * w1Z);");
			document.linef("                        final float u1Z = u0X * w1Y - u0Y * w1X;");
			document.linef("                        final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);");
			document.linef("                        final float u2X = u1X * u1LengthReciprocal;");
			document.linef("                        final float u2Y = u1Y * u1LengthReciprocal;");
			document.linef("                        final float u2Z = u1Z * u1LengthReciprocal;");
			document.linef("                        ");
			document.linef("//                      Calculate the orthonormal basis V vector:");
			document.linef("                        final float v0X = w0Y * u2Z - w0Z * u2Y;");
			document.linef("                        final float v0Y = w0Z * u2X - w0X * u2Z;");
			document.linef("                        final float v0Z = w0X * u2Y - w0Y * u2X;");
			document.linef("                        ");
			document.linef("//                      Calculate the direction for the next iteration:");
			document.linef("                        final float direction0X = u2X * random0Cos * random1Squared0 + v0X * random0Sin * random1Squared0 + w0X * random1Squared1;");
			document.linef("                        final float direction0Y = u2Y * random0Cos * random1Squared0 + v0Y * random0Sin * random1Squared0 + w0Y * random1Squared1;");
			document.linef("                        final float direction0Z = u2Z * random0Cos * random1Squared0 + v0Z * random0Sin * random1Squared0 + w0Z * random1Squared1;");
			document.linef("                        final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);");
			document.linef("                        ");
			document.linef("//                      Update the ray origin for the next iteration:");
			document.linef("                        originX = surfaceIntersectionPointX + w0X * 0.01F;");
			document.linef("                        originY = surfaceIntersectionPointY + w0Y * 0.01F;");
			document.linef("                        originZ = surfaceIntersectionPointZ + w0Z * 0.01F;");
			document.linef("                        ");
			document.linef("//                      Update the ray direction for the next iteration:");
			document.linef("                        directionX = direction0X * direction0LengthReciprocal;");
			document.linef("                        directionY = direction0Y * direction0LengthReciprocal;");
			document.linef("                        directionZ = direction0Z * direction0LengthReciprocal;");
			document.linef("                        ");
			document.linef("//                      Multiply the current radiance multiplier with the albedo:");
			document.linef("                        radianceMultiplierR *= albedoColorR;");
			document.linef("                        radianceMultiplierG *= albedoColorG;");
			document.linef("                        radianceMultiplierB *= albedoColorB;");
			document.linef("                    }");
			document.linef("                }");
			document.linef("                ");
			document.linef("//              FIXME: Find out why the \"child list broken\" Exception occurs if the following line is not present!");
			document.linef("                depthCurrent = depthCurrent + 0;");
			document.linef("            } else if(material == MATERIAL_LAMBERTIAN_DIFFUSE) {");
			document.linef("//              Compute some random values:");
			document.linef("                final float theta = PI_MULTIPLIED_BY_TWO * randomA;");
			document.linef("                final float cosTheta = cos(theta);");
			document.linef("                final float sinTheta = sin(theta);");
			document.linef("                final float sqrtR0 = sqrt(randomB);");
			document.linef("                final float sqrtR1 = sqrt(1.0F - randomB);");
			document.linef("                ");
			document.linef("//              Check if the direction is the Y-direction:");
			document.linef("                final boolean isY = abs(w0X) > 0.1F;");
			document.linef("                ");
			document.linef("//              Calculate the orthonormal basis U vector:");
			document.linef("                final float u0X = isY ? 0.0F : 1.0F;");
			document.linef("                final float u0Y = isY ? 1.0F : 0.0F;");
			document.linef("                final float u1X = u0Y * w0Z;");
			document.linef("                final float u1Y = -(u0X * w0Z);");
			document.linef("                final float u1Z = u0X * w0Y - u0Y * w0X;");
			document.linef("                final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);");
			document.linef("                final float u2X = u1X * u1LengthReciprocal;");
			document.linef("                final float u2Y = u1Y * u1LengthReciprocal;");
			document.linef("                final float u2Z = u1Z * u1LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Calculate the orthonormal basis V vector:");
			document.linef("                final float v0X = w0Y * u2Z - w0Z * u2Y;");
			document.linef("                final float v0Y = w0Z * u2X - w0X * u2Z;");
			document.linef("                final float v0Z = w0X * u2Y - w0Y * u2X;");
			document.linef("                ");
			document.linef("//              Calculate the direction for the next iteration:");
			document.linef("                final float direction0X = u2X * cosTheta * sqrtR0 + v0X * sinTheta * sqrtR0 + w0X * sqrtR1;");
			document.linef("                final float direction0Y = u2Y * cosTheta * sqrtR0 + v0Y * sinTheta * sqrtR0 + w0Y * sqrtR1;");
			document.linef("                final float direction0Z = u2Z * cosTheta * sqrtR0 + v0Z * sinTheta * sqrtR0 + w0Z * sqrtR1;");
			document.linef("                final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);");
			document.linef("                ");
			document.linef("//              Update the ray origin for the next iteration:");
			document.linef("                originX = surfaceIntersectionPointX + w0X * 0.01F;");
			document.linef("                originY = surfaceIntersectionPointY + w0Y * 0.01F;");
			document.linef("                originZ = surfaceIntersectionPointZ + w0Z * 0.01F;");
			document.linef("                ");
			document.linef("//              Update the ray direction for the next iteration:");
			document.linef("                directionX = direction0X * direction0LengthReciprocal;");
			document.linef("                directionY = direction0Y * direction0LengthReciprocal;");
			document.linef("                directionZ = direction0Z * direction0LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Multiply the current radiance multiplier with the albedo:");
			document.linef("                radianceMultiplierR *= albedoColorR;");
			document.linef("                radianceMultiplierG *= albedoColorG;");
			document.linef("                radianceMultiplierB *= albedoColorB;");
			document.linef("            } else if(material == MATERIAL_PHONG_METAL) {");
			document.linef("//              Compute some random values:");
			document.linef("                final float random0 = PI_MULTIPLIED_BY_TWO * randomA;");
			document.linef("                final float random0Cos = cos(random0);");
			document.linef("                final float random0Sin = sin(random0);");
			document.linef("                final float random1 = randomB;");
			document.linef("                ");
			document.linef("//              Calculate the cos and sin values of theta:");
			document.linef("                final float cosTheta = pow(1.0F - random1, PHONE_EXPONENT_PLUS_ONE_RECIPROCAL);");
			document.linef("                final float sinTheta = sqrt(1.0F - cosTheta * cosTheta);");
			document.linef("                ");
			document.linef("//              Calculate the orthonormal basis W vector:");
			document.linef("                final float w1X = directionX - surfaceNormalX * dotProductMultipliedByTwo;");
			document.linef("                final float w1Y = directionY - surfaceNormalY * dotProductMultipliedByTwo;");
			document.linef("                final float w1Z = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;");
			document.linef("                final float w1LengthReciprocal = rsqrt(w1X * w1X + w1Y * w1Y + w1Z * w1Z);");
			document.linef("                final float w2X = w1X * w1LengthReciprocal;");
			document.linef("                final float w2Y = w1Y * w1LengthReciprocal;");
			document.linef("                final float w2Z = w1Z * w1LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Check if the direction is the Y-direction:");
			document.linef("                final boolean isY = abs(w2X) > 0.1F;");
			document.linef("                ");
			document.linef("//              Calculate the orthonormal basis U vector:");
			document.linef("                final float u0X = isY ? 0.0F : 1.0F;");
			document.linef("                final float u0Y = isY ? 1.0F : 0.0F;");
			document.linef("                final float u1X = u0Y * w2Z;");
			document.linef("                final float u1Y = -(u0X * w2Z);");
			document.linef("                final float u1Z = u0X * w2Y - u0Y * w2X;");
			document.linef("                final float u1LengthReciprocal = rsqrt(u1X * u1X + u1Y * u1Y + u1Z * u1Z);");
			document.linef("                final float u2X = u1X * u1LengthReciprocal;");
			document.linef("                final float u2Y = u1Y * u1LengthReciprocal;");
			document.linef("                final float u2Z = u1Z * u1LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Calculate the orthonormal basis V vector:");
			document.linef("                final float v0X = w2Y * u2Z - w2Z * u2Y;");
			document.linef("                final float v0Y = w2Z * u2X - w2X * u2Z;");
			document.linef("                final float v0Z = w2X * u2Y - w2Y * u2X;");
			document.linef("                ");
			document.linef("//              Calculate the direction for the next iteration:");
			document.linef("                final float direction0X = u2X * random0Cos * sinTheta + v0X * random0Sin * sinTheta + w1X * cosTheta;");
			document.linef("                final float direction0Y = u2Y * random0Cos * sinTheta + v0Y * random0Sin * sinTheta + w1Y * cosTheta;");
			document.linef("                final float direction0Z = u2Z * random0Cos * sinTheta + v0Z * random0Sin * sinTheta + w1Z * cosTheta;");
			document.linef("                final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);");
			document.linef("                ");
			document.linef("//              Update the ray origin for the next iteration:");
			document.linef("                originX = surfaceIntersectionPointX + w2X * 0.01F;");
			document.linef("                originY = surfaceIntersectionPointY + w2Y * 0.01F;");
			document.linef("                originZ = surfaceIntersectionPointZ + w2Z * 0.01F;");
			document.linef("                ");
			document.linef("//              Update the ray direction for the next iteration:");
			document.linef("                directionX = direction0X * direction0LengthReciprocal;");
			document.linef("                directionY = direction0Y * direction0LengthReciprocal;");
			document.linef("                directionZ = direction0Z * direction0LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Multiply the current radiance multiplier with the albedo:");
			document.linef("                radianceMultiplierR *= albedoColorR;");
			document.linef("                radianceMultiplierG *= albedoColorG;");
			document.linef("                radianceMultiplierB *= albedoColorB;");
			document.linef("            } else if(material == MATERIAL_GLASS) {");
			document.linef("//              Check if the current ray is going in towards the same shape or out of it:");
			document.linef("                final boolean isGoingIn = surfaceNormalX * w0X + surfaceNormalY * w0Y + surfaceNormalZ * w0Z > 0.0F;");
			document.linef("                ");
			document.linef("//              TODO: Write explanation!");
			document.linef("                final float nnt = isGoingIn ? REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1 : REFRACTIVE_INDEX_1_DIVIDED_BY_REFRACTIVE_INDEX_0;");
			document.linef("                ");
			document.linef("//              Calculate the dot product between the orthonormal basis W vector and the current direction vector:");
			document.linef("                final float dotProductOfW0AndDirection = w0X * directionX + w0Y * directionY + w0Z * directionZ;");
			document.linef("                ");
			document.linef("//              Calculate the total internal reflection:");
			document.linef("                final float totalInternalReflection = 1.0F - nnt * nnt * (1.0F - dotProductOfW0AndDirection * dotProductOfW0AndDirection);");
			document.linef("                ");
			document.linef("//              Calculate the reflection direction:");
			document.linef("                final float reflectionDirectionX = directionX - surfaceNormalX * dotProductMultipliedByTwo;");
			document.linef("                final float reflectionDirectionY = directionY - surfaceNormalY * dotProductMultipliedByTwo;");
			document.linef("                final float reflectionDirectionZ = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;");
			document.linef("                ");
			document.linef("                if(totalInternalReflection < 0.0F) {");
			document.linef("//                  Update the ray origin for the next iteration:");
			document.linef("                    originX = surfaceIntersectionPointX + w0X * 0.02F;");
			document.linef("                    originY = surfaceIntersectionPointY + w0Y * 0.02F;");
			document.linef("                    originZ = surfaceIntersectionPointZ + w0Z * 0.02F;");
			document.linef("                    ");
			document.linef("//                  Update the ray direction for the next iteration:");
			document.linef("                    directionX = reflectionDirectionX;");
			document.linef("                    directionY = reflectionDirectionY;");
			document.linef("                    directionZ = reflectionDirectionZ;");
			document.linef("                } else {");
			document.linef("//                  Calculate the square root of the total internal reflection:");
			document.linef("                    final float sqrtTotalInternalReflection = sqrt(totalInternalReflection);");
			document.linef("                    ");
			document.linef("//                  Calculate the transmission direction:");
			document.linef("                    final float scalar = isGoingIn ? dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection : -(dotProductOfW0AndDirection * nnt + sqrtTotalInternalReflection);");
			document.linef("                    final float direction0X = directionX * nnt - surfaceNormalX * scalar;");
			document.linef("                    final float direction0Y = directionY * nnt - surfaceNormalY * scalar;");
			document.linef("                    final float direction0Z = directionZ * nnt - surfaceNormalZ * scalar;");
			document.linef("                    final float direction0LengthReciprocal = rsqrt(direction0X * direction0X + direction0Y * direction0Y + direction0Z * direction0Z);");
			document.linef("                    final float transmissionDirectionX = direction0X * direction0LengthReciprocal;");
			document.linef("                    final float transmissionDirectionY = direction0Y * direction0LengthReciprocal;");
			document.linef("                    final float transmissionDirectionZ = direction0Z * direction0LengthReciprocal;");
			document.linef("                    ");
			document.linef("//                  Calculate some angles:");
			document.linef("                    final float angle1 = (isGoingIn ? -dotProductOfW0AndDirection : transmissionDirectionX * surfaceNormalX + transmissionDirectionY * surfaceNormalY + transmissionDirectionZ * surfaceNormalZ);");
			document.linef("                    final float angle2 = 1.0F - angle1;");
			document.linef("                    ");
			document.linef("//                  Calculate the reflectance:");
			document.linef("                    final float reflectance = REFRACTIVE_INDEX_R0 + (1.0F - REFRACTIVE_INDEX_R0) * angle2 * angle2 * angle2 * angle2 * angle2;");
			document.linef("                    ");
			document.linef("//                  Calculate the transmittance:");
			document.linef("                    final float transmittance = 1.0F - reflectance;");
			document.linef("                    ");
			document.linef("//                  Calculate a probability for the reflection- or the transmission direction:");
			document.linef("                    final float probability = 0.25F + 0.5F * reflectance;");
			document.linef("                    ");
			document.linef("//                  Calculate the probability that the direction for the next iteration will be the reflection direction:");
			document.linef("                    final float reflectanceProbability = reflectance / probability;");
			document.linef("                    ");
			document.linef("//                  Calculate the probability that the direction for the next iteration will be the transmission direction:");
			document.linef("                    final float transmittanceProbability = transmittance / (1.0F - probability);");
			document.linef("                    ");
			document.linef("//                  Check if the direction for the next iteration is the reflection direction or the transmission direction:");
			document.linef("                    final boolean isReflectionDirection = randomA < probability;");
			document.linef("                    ");
			document.linef("//                  Retrieve the value to multiply the current radiance multiplier with:");
			document.linef("                    final float multiplier = isReflectionDirection ? reflectanceProbability : transmittanceProbability;");
			document.linef("                    ");
			document.linef("//                  Multiply the current radiance multiplier with either the reflectance probability or the transmittance probability:");
			document.linef("                    radianceMultiplierR *= multiplier;");
			document.linef("                    radianceMultiplierG *= multiplier;");
			document.linef("                    radianceMultiplierB *= multiplier;");
			document.linef("                    ");
			document.linef("//                  Retrieve the epsilon value that offsets the ray origin to mitigate self intersections:");
			document.linef("                    final float epsilon = isReflectionDirection ? 0.01F : 0.000001F;");
			document.linef("                    ");
			document.linef("//                  Update the ray origin for the next iteration:");
			document.linef("                    originX = surfaceIntersectionPointX + w0X * epsilon;");
			document.linef("                    originY = surfaceIntersectionPointY + w0Y * epsilon;");
			document.linef("                    originZ = surfaceIntersectionPointZ + w0Z * epsilon;");
			document.linef("                    ");
			document.linef("//                  Update the ray direction for the next iteration:");
			document.linef("                    directionX = isReflectionDirection ? reflectionDirectionX : transmissionDirectionX;");
			document.linef("                    directionY = isReflectionDirection ? reflectionDirectionY : transmissionDirectionY;");
			document.linef("                    directionZ = isReflectionDirection ? reflectionDirectionZ : transmissionDirectionZ;");
			document.linef("                }");
			document.linef("                ");
			document.linef("//              FIXME: Find out why the \"child list broken\" Exception occurs if the following line is not present!");
			document.linef("                depthCurrent = depthCurrent + 0;");
			document.linef("            } else if(material == MATERIAL_MIRROR) {");
			document.linef("//              Update the ray origin for the next iteration:");
			document.linef("                originX = surfaceIntersectionPointX + surfaceNormalX * 0.000001F;");
			document.linef("                originY = surfaceIntersectionPointY + surfaceNormalY * 0.000001F;");
			document.linef("                originZ = surfaceIntersectionPointZ + surfaceNormalZ * 0.000001F;");
			document.linef("                ");
			document.linef("//              Update the ray direction for the next iteration:");
			document.linef("                directionX = directionX - surfaceNormalX * dotProductMultipliedByTwo;");
			document.linef("                directionY = directionY - surfaceNormalY * dotProductMultipliedByTwo;");
			document.linef("                directionZ = directionZ - surfaceNormalZ * dotProductMultipliedByTwo;");
			document.linef("                ");
			document.linef("//              Multiply the current radiance multiplier with the albedo:");
			document.linef("                radianceMultiplierR *= albedoColorR;");
			document.linef("                radianceMultiplierG *= albedoColorG;");
			document.linef("                radianceMultiplierB *= albedoColorB;");
			document.linef("            }");
			document.linef("        } while(depthCurrent < depthMaximum);");
			document.linef("        ");
			document.linef("//      Perform an intersection test:");
			document.linef("        doPerformIntersectionTestOnly(shapesOffsetToSkip, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("        ");
			document.linef("//      Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:");
			document.linef("        final float distance0 = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];");
			document.linef("        ");
			document.linef("//      Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:");
			document.linef("        final int shapesOffset0 = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);");
			document.linef("        ");
			document.linef("//      Test that an intersection was actually made, and if not, return black color (or possibly the background color):");
			document.linef("        if(distance0 == INFINITY || shapesOffset0 == -1) {");
			document.linef("//          Calculate the color for the sky in the current direction:");
			document.linef("            doCalculateColorForSky(directionX, directionY, directionZ);");
			document.linef("            ");
			document.linef("//          Add the color for the sky to the current pixel color:");
			document.linef("            pixelColorR += radianceMultiplierR * this.temporaryColors[pixelIndex0];");
			document.linef("            pixelColorG += radianceMultiplierG * this.temporaryColors[pixelIndex0 + 1];");
			document.linef("            pixelColorB += radianceMultiplierB * this.temporaryColors[pixelIndex0 + 2];");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      Update the current pixel color:");
			document.linef("        this.currentPixelColors[pixelIndex0] = pixelColorR;");
			document.linef("        this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;");
			document.linef("        this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;");
			document.linef("    }");
		} else {
			document.linef("//  private void doPathTracing() {}");
		}
	}
	
	private static void doGenerateDoPerformIntersectionTestMethod(final Document document, final CompiledScene compiledScene) {
		document.linef("    private void doPerformIntersectionTest(final int shapesOffsetToSkip, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {");
		document.linef("//      Calculate the offset to the intersections array:");
		document.linef("        final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;");
		document.linef("        ");
		document.linef("//      Initialize the distance to the closest shape to INFINITY:");
		document.linef("        float minimumDistance = INFINITY;");
		document.linef("        ");
		document.linef("//      Initialize the offset to the closest shape to -1:");
		document.linef("        int shapesOffset = -1;");
		document.linef("        ");
		
		if(compiledScene.hasTriangles()) {
			document.linef("//      Calculate the reciprocal of the ray direction vector:");
			document.linef("        final float directionReciprocalX = 1.0F / directionX;");
			document.linef("        final float directionReciprocalY = 1.0F / directionY;");
			document.linef("        final float directionReciprocalZ = 1.0F / directionZ;");
			document.linef("        ");
			document.linef("//      Initialize the offset to the root of the BVH structure (which is 0):");
			document.linef("        int boundingVolumeHierarchyOffset = 0;");
			document.linef("        ");
			document.linef("//      Loop through the BVH structure as long as the offset to the next node is not -1:");
			document.linef("        do {");
			document.linef("//          Retrieve the minimum point location of the current bounding box:");
			document.linef("            final float minimumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 2];");
			document.linef("            final float minimumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3];");
			document.linef("            final float minimumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 4];");
			document.linef("            ");
			document.linef("//          Retrieve the maximum point location of the current bounding box:");
			document.linef("            final float maximumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 5];");
			document.linef("            final float maximumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 6];");
			document.linef("            final float maximumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 7];");
			document.linef("            ");
			document.linef("//          Calculate the distance to the minimum point location of the bounding box:");
			document.linef("            final float t0X = (minimumX - originX) * directionReciprocalX;");
			document.linef("            final float t0Y = (minimumY - originY) * directionReciprocalY;");
			document.linef("            final float t0Z = (minimumZ - originZ) * directionReciprocalZ;");
			document.linef("            ");
			document.linef("//          Calculate the distance to the maximum point location of the bounding box:");
			document.linef("            final float t1X = (maximumX - originX) * directionReciprocalX;");
			document.linef("            final float t1Y = (maximumY - originY) * directionReciprocalY;");
			document.linef("            final float t1Z = (maximumZ - originZ) * directionReciprocalZ;");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum X-components:");
			document.linef("            final float tMaximumX = max(t0X, t1X);");
			document.linef("            final float tMinimumX = min(t0X, t1X);");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum Y-components:");
			document.linef("            final float tMaximumY = max(t0Y, t1Y);");
			document.linef("            final float tMinimumY = min(t0Y, t1Y);");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum Z-components:");
			document.linef("            final float tMaximumZ = max(t0Z, t1Z);");
			document.linef("            final float tMinimumZ = min(t0Z, t1Z);");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum distance values of the X-, Y- and Z-components above:");
			document.linef("            final float tMaximum = min(tMaximumX, min(tMaximumY, tMaximumZ));");
			document.linef("            final float tMinimum = max(tMinimumX, max(tMinimumY, tMinimumZ));");
			document.linef("            ");
			document.linef("//          Check if the maximum distance is greater than or equal to the minimum distance:");
			document.linef("            if(tMaximum < tMinimum) {");
			document.linef("//              Retrieve the offset to the next node in the BVH structure, relative to the current one:");
			document.linef("                boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);");
			document.linef("            } else {");
			document.linef("//              Retrieve the type of the current BVH node:");
			document.linef("                final float type = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset];");
			document.linef("                ");
			document.linef("                if(type == CompiledScene.BVH_NODE_TYPE_TREE) {");
			document.linef("//                  This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:");
			document.linef("                    boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);");
			document.linef("                } else {");
			document.linef("//                  Retrieve the triangle count in the current BVH node:");
			document.linef("                    final int triangleCount = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);");
			document.linef("                    ");
			document.linef("                    int i = 0;");
			document.linef("                    ");
			document.linef("//                  Loop through all triangles in the current BVH node:");
			document.linef("                    while(i < triangleCount) {");
			document.linef("//                      Retrieve the offset to the current triangle:");
			document.linef("                        final int offset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);");
			document.linef("                        ");
			document.linef("                        if(offset != shapesOffsetToSkip) {");
			document.linef("//                          Perform an intersection test with the current triangle:");
			document.linef("                            final float currentDistance = doIntersectTriangle(offset, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("                            ");
			document.linef("//                          Check if the current distance is less than the distance to the closest shape so far:");
			document.linef("                            if(currentDistance < minimumDistance) {");
			document.linef("//                              Update the distance to the closest shape with the current one:");
			document.linef("                                minimumDistance = currentDistance;");
			document.linef("                                ");
			document.linef("//                              Update the offset to the closest shape with the current one:");
			document.linef("                                shapesOffset = offset;");
			document.linef("                            }");
			document.linef("                        }");
			document.linef("                        ");
			document.linef("                        i++;");
			document.linef("                    }");
			document.linef("                    ");
			document.linef("//                  Retrieve the offset to the next node in the BVH structure, relative to the current one:");
			document.linef("                    boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);");
			document.linef("                }");
			document.linef("                ");
			document.linef("//              FIXME: Find out why the \"child list broken\" Exception occurs if the following line is not present!");
			document.linef("                boundingVolumeHierarchyOffset = boundingVolumeHierarchyOffset + 0;");
			document.linef("            }");
			document.linef("        } while(boundingVolumeHierarchyOffset != -1);");
		}
		
		document.linef("        ");
		
		if(compiledScene.hasPlanes() || compiledScene.hasSpheres()) {
			document.linef("//      Loop through any other shapes, that are not triangles:");
			document.linef("        for(int i = 0; i < this.shapeOffsetsLength; i++) {");
			document.linef("//          Retrieve the offset to the shape:");
			document.linef("            final int currentShapesOffset = this.shapeOffsets[i];");
			document.linef("            ");
			document.linef("            if(currentShapesOffset != shapesOffsetToSkip) {");
			document.linef("//              Perform an intersection test with the current shape:");
			document.linef("                final float currentDistance = doIntersect(currentShapesOffset, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("                ");
			document.linef("//              Check if the current distance is less than the distance to the closest shape so far:");
			document.linef("                if(currentDistance < minimumDistance) {");
			document.linef("//                  Update the distance to the closest shape with the current one:");
			document.linef("                    minimumDistance = currentDistance;");
			document.linef("                    ");
			document.linef("//                  Update the offset to the closest shape with the current one:");
			document.linef("                    shapesOffset = currentShapesOffset;");
			document.linef("                }");
			document.linef("            }");
			document.linef("        }");
		}
		
		document.linef("        ");
		document.linef("        if(minimumDistance < INFINITY && shapesOffset > -1) {");
		document.linef("//          Calculate the surface properties for the intersected shape:");
		document.linef("            doCalculateSurfaceProperties(minimumDistance, originX, originY, originZ, directionX, directionY, directionZ, shapesOffset);");
		document.linef("            ");
		
		if(compiledScene.hasNormalMapping()) {
			document.linef("//          Perform standard Normal Mapping:");
			document.linef("            doPerformNormalMapping(shapesOffset);");
		}
		
		document.linef("            ");
		
		if(compiledScene.hasPerlinNoiceNormalMapping()) {
			document.linef("//          Perform Perlin Noise Normal Mapping:");
			document.linef("            doPerformPerlinNoiseNormalMapping(shapesOffset);");
		}
		
		document.linef("        } else {");
		document.linef("//          Reset the information in the intersections array:");
		document.linef("            this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;");
		document.linef("            this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;");
		document.linef("        }");
		document.linef("    }");
	}
	
	private static void doGenerateDoPerformIntersectionTestOnlyMethod(final Document document, final CompiledScene compiledScene) {
		final boolean hasTriangles = compiledScene.hasTriangles();
		
		document.linef("    private void doPerformIntersectionTestOnly(final int shapesOffsetToSkip, final float originX, final float originY, final float originZ, final float directionX, final float directionY, final float directionZ) {");
		document.linef("//      Calculate the offset to the intersections array:");
		document.linef("        final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;");
		document.linef("        ");
		
		if(hasTriangles) {
			document.linef("//      Calculate the reciprocal of the ray direction vector:");
			document.linef("        final float directionReciprocalX = 1.0F / directionX;");
			document.linef("        final float directionReciprocalY = 1.0F / directionY;");
			document.linef("        final float directionReciprocalZ = 1.0F / directionZ;");
			document.linef("        ");
			document.linef("//      Initialize the offset to the root of the BVH structure (which is 0):");
			document.linef("        int boundingVolumeHierarchyOffset = 0;");
			document.linef("        ");
			document.linef("//      Initialize a predicate:");
			document.linef("        boolean hasFoundIntersection = false;");
		}
		
		document.linef("        ");
		document.linef("//      Reset the information in the intersections array:");
		document.linef("        this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = INFINITY;");
		document.linef("        this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = -1;");
		document.linef("        ");
		
		if(hasTriangles) {
			document.linef("//      Loop through the BVH structure as long as the offset to the next node is not -1:");
			document.linef("        do {");
			document.linef("//          Retrieve the minimum point location of the current bounding box:");
			document.linef("            final float minimumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 2];");
			document.linef("            final float minimumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 3];");
			document.linef("            final float minimumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 4];");
			document.linef("            ");
			document.linef("//          Retrieve the maximum point location of the current bounding box:");
			document.linef("            final float maximumX = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 5];");
			document.linef("            final float maximumY = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 6];");
			document.linef("            final float maximumZ = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 7];");
			document.linef("            ");
			document.linef("//          Calculate the distance to the minimum point location of the bounding box:");
			document.linef("            final float t0X = (minimumX - originX) * directionReciprocalX;");
			document.linef("            final float t0Y = (minimumY - originY) * directionReciprocalY;");
			document.linef("            final float t0Z = (minimumZ - originZ) * directionReciprocalZ;");
			document.linef("            ");
			document.linef("//          Calculate the distance to the maximum point location of the bounding box:");
			document.linef("            final float t1X = (maximumX - originX) * directionReciprocalX;");
			document.linef("            final float t1Y = (maximumY - originY) * directionReciprocalY;");
			document.linef("            final float t1Z = (maximumZ - originZ) * directionReciprocalZ;");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum X-components:");
			document.linef("            final float tMaximumX = max(t0X, t1X);");
			document.linef("            final float tMinimumX = min(t0X, t1X);");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum Y-components:");
			document.linef("            final float tMaximumY = max(t0Y, t1Y);");
			document.linef("            final float tMinimumY = min(t0Y, t1Y);");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum Z-components:");
			document.linef("            final float tMaximumZ = max(t0Z, t1Z);");
			document.linef("            final float tMinimumZ = min(t0Z, t1Z);");
			document.linef("            ");
			document.linef("//          Calculate the minimum and maximum distance values of the X-, Y- and Z-components above:");
			document.linef("            final float tMaximum = min(tMaximumX, min(tMaximumY, tMaximumZ));");
			document.linef("            final float tMinimum = max(tMinimumX, max(tMinimumY, tMinimumZ));");
			document.linef("            ");
			document.linef("//          Check if the maximum distance is greater than or equal to the minimum distance:");
			document.linef("            if(tMaximum < tMinimum) {");
			document.linef("//              Retrieve the offset to the next node in the BVH structure, relative to the current one:");
			document.linef("                boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);");
			document.linef("            } else {");
			document.linef("//              Retrieve the type of the current BVH node:");
			document.linef("                final float type = this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset];");
			document.linef("                ");
			document.linef("                if(type == CompiledScene.BVH_NODE_TYPE_TREE) {");
			document.linef("//                  This BVH node is a tree node, so retrieve the offset to the next node in the BVH structure, relative to the current one:");
			document.linef("                    boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);");
			document.linef("                } else {");
			document.linef("//                  Retrieve the triangle count in the current BVH node:");
			document.linef("                    final int triangleCount = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 8]);");
			document.linef("                    ");
			document.linef("                    int i = 0;");
			document.linef("                    ");
			document.linef("//                  Loop through all triangles in the current BVH node:");
			document.linef("                    while(i < triangleCount) {");
			document.linef("//                      Retrieve the offset to the current triangle:");
			document.linef("                        final int offset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 9 + i]);");
			document.linef("                        ");
			document.linef("                        if(offset != shapesOffsetToSkip) {");
			document.linef("//                          Perform an intersection test with the current triangle:");
			document.linef("                            final float currentDistance = doIntersectTriangle(offset, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("                            ");
			document.linef("//                          Check if the current distance is less than the distance to the closest shape so far:");
			document.linef("                            if(currentDistance < INFINITY) {");
			document.linef("//                              Update the predicate:");
			document.linef("                                hasFoundIntersection = true;");
			document.linef("                                ");
			document.linef("//                              Set the information in the intersections array:");
			document.linef("                                this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = currentDistance;");
			document.linef("                                this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = offset;");
			document.linef("                                ");
			document.linef("                                i = triangleCount;");
			document.linef("                            }");
			document.linef("                        }");
			document.linef("                        ");
			document.linef("                        i++;");
			document.linef("                    }");
			document.linef("                    ");
			document.linef("                    if(hasFoundIntersection) {");
			document.linef("                        boundingVolumeHierarchyOffset = -1;");
			document.linef("                    } else {");
			document.linef("//                      Retrieve the offset to the next node in the BVH structure, relative to the current one:");
			document.linef("                        boundingVolumeHierarchyOffset = (int)(this.boundingVolumeHierarchy[boundingVolumeHierarchyOffset + 1]);");
			document.linef("                    }");
			document.linef("                }");
			document.linef("                ");
			document.linef("//              FIXME: Find out why the \"child list broken\" Exception occurs if the following line is not present!");
			document.linef("                boundingVolumeHierarchyOffset = boundingVolumeHierarchyOffset + 0;");
			document.linef("            }");
			document.linef("        } while(boundingVolumeHierarchyOffset != -1);");
		}
		
		document.linef("        ");
		
		if(compiledScene.hasPlanes() || compiledScene.hasSpheres()) {
			if(hasTriangles) {
				document.linef("        if(!hasFoundIntersection) {");
			}
			
			document.linef("//          Loop through any other shapes, that are not triangles:");
			document.linef("            for(int i = 0; i < this.shapeOffsetsLength; i++) {");
			document.linef("//              Retrieve the offset to the shape:");
			document.linef("                final int currentShapesOffset = this.shapeOffsets[i];");
			document.linef("                ");
			document.linef("                if(currentShapesOffset != shapesOffsetToSkip) {");
			document.linef("//                  Perform an intersection test with the current shape:");
			document.linef("                    final float currentDistance = doIntersect(currentShapesOffset, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("                    ");
			document.linef("//                  Check if the current distance is less than the distance to the closest shape so far:");
			document.linef("                    if(currentDistance < INFINITY) {");
			
			if(hasTriangles) {
				document.linef("//                      Update the predicate:");
				document.linef("                        hasFoundIntersection = true;");
			}
			
			document.linef("                        ");
			document.linef("//                      Set the information in the intersections array:");
			document.linef("                        this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE] = currentDistance;");
			document.linef("                        this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET] = currentShapesOffset;");
			document.linef("                        ");
			document.linef("                        i = this.shapeOffsetsLength;");
			document.linef("                    }");
			document.linef("                }");
			document.linef("            }");
			
			if(hasTriangles) {
				document.linef("        }");
			}
		}
		
		document.linef("    }");
	}
	
	private static void doGenerateDoPerformNormalMappingMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasNormalMapping()) {
			document.linef("    private void doPerformNormalMapping(final int shapesOffset) {");
			document.linef("//      Retrieve the offset in the textures array and the type of the texture:");
			document.linef("        final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);");
			document.linef("        final int texturesOffset = (int)(this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_NORMAL]);");
			document.linef("        final int textureType = (int)(this.textures[texturesOffset]);");
			document.linef("        ");
			document.linef("//      Get the intersections offset:");
			document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        ");
			document.linef("        if(textureType == CompiledScene.IMAGE_TEXTURE_TYPE) {");
			document.linef("//          Calculate the texture color:");
			document.linef("            doCalculateTextureColorForImageTexture(texturesOffset);");
			document.linef("            ");
			document.linef("//          Calculate the index into the temporaryColors array:");
			document.linef("            final int pixelIndex0 = getLocalId() * 3;");
			document.linef("            ");
			document.linef("//          Retrieve the R-, G- and B-component values:");
			document.linef("            final float r = 2.0F * this.temporaryColors[pixelIndex0] - 1.0F;");
			document.linef("            final float g = 2.0F * this.temporaryColors[pixelIndex0 + 1] - 1.0F;");
			document.linef("            final float b = 2.0F * this.temporaryColors[pixelIndex0 + 2] - 1.0F;");
			document.linef("            ");
			document.linef("//          Retrieve the offset of the surface normal in the intersections array:");
			document.linef("            final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("            ");
			document.linef("//          Retrieve the orthonormal basis W-vector:");
			document.linef("            final float wX = this.intersections[offsetIntersectionSurfaceNormal];");
			document.linef("            final float wY = this.intersections[offsetIntersectionSurfaceNormal + 1];");
			document.linef("            final float wZ = this.intersections[offsetIntersectionSurfaceNormal + 2];");
			document.linef("            ");
			document.linef("//          Calculate the absolute values of the orthonormal basis W-vector:");
			document.linef("            final float absWX = abs(wX);");
			document.linef("            final float absWY = abs(wY);");
			document.linef("            final float absWZ = abs(wZ);");
			document.linef("            ");
			document.linef("//          Check the direction of the orthonormal basis:");
			document.linef("            final boolean isWX = absWX < absWY && absWX < absWZ;");
			document.linef("            final boolean isWY = absWY < absWZ;");
			document.linef("            ");
			document.linef("//          Calculate the orthonormal basis V-vector:");
			document.linef("            final float v0X = isWX ? 0.0F : isWY ? wZ : wY;");
			document.linef("            final float v0Y = isWX ? wZ : isWY ? 0.0F : -wX;");
			document.linef("            final float v0Z = isWX ? -wY : isWY ? -wX : 0.0F;");
			document.linef("            final float v0LengthReciprocal = rsqrt(v0X * v0X + v0Y * v0Y + v0Z * v0Z);");
			document.linef("            final float v1X = v0X * v0LengthReciprocal;");
			document.linef("            final float v1Y = v0Y * v0LengthReciprocal;");
			document.linef("            final float v1Z = v0Z * v0LengthReciprocal;");
			document.linef("            ");
			document.linef("//          Calculate the orthonormal basis U-vector:");
			document.linef("            final float u0X = v1Y * wZ - v1Z * wY;");
			document.linef("            final float u0Y = v1Z * wX - v1X * wZ;");
			document.linef("            final float u0Z = v1X * wY - v1Y * wX;");
			document.linef("            final float u0LengthReciprocal = rsqrt(u0X * u0X + u0Y * u0Y + u0Z * u0Z);");
			document.linef("            final float u1X = u0X * u0LengthReciprocal;");
			document.linef("            final float u1Y = u0Y * u0LengthReciprocal;");
			document.linef("            final float u1Z = u0Z * u0LengthReciprocal;");
			document.linef("            ");
			document.linef("//          Calculate the new surface normal:");
			document.linef("            final float surfaceNormal0X = r * u1X + g * v1X + b * wX;");
			document.linef("            final float surfaceNormal0Y = r * u1Y + g * v1Y + b * wY;");
			document.linef("            final float surfaceNormal0Z = r * u1Z + g * v1Z + b * wZ;");
			document.linef("            final float surfaceNormal0LengthReciprocal = rsqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);");
			document.linef("            final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;");
			document.linef("            final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;");
			document.linef("            final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;");
			document.linef("            ");
			document.linef("//          Update the intersections array:");
			document.linef("            this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal1X;");
			document.linef("            this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal1Y;");
			document.linef("            this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal1Z;");
			document.linef("        }");
			document.linef("    }");
		} else {
			document.linef("//  private void doPerformNormalMapping(final int shapesOffset) {}");
		}
	}
	
	private static void doGenerateDoPerformPerlinNoiseNormalMappingMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasPerlinNoiceNormalMapping()) {
			document.linef("    private void doPerformPerlinNoiseNormalMapping(final int shapesOffset) {");
			document.linef("//      Get the intersections offset:");
			document.linef("        final int intersectionsOffset0 = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        ");
			document.linef("//      Retrieve the offset to the surfaces array:");
			document.linef("        final int surfacesOffset = (int)(this.shapes[shapesOffset + CompiledScene.SHAPE_RELATIVE_OFFSET_SURFACES_OFFSET]);");
			document.linef("        ");
			document.linef("//      Retrieve the Perlin noise amount from the current shape:");
			document.linef("        final float amount = this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_AMOUNT];");
			document.linef("        ");
			document.linef("//      Retrieve the Perlin noise scale from the current shape:");
			document.linef("        final float scale = this.surfaces[surfacesOffset + CompiledScene.SURFACE_RELATIVE_OFFSET_PERLIN_NOISE_SCALE];");
			document.linef("        ");
			document.linef("//      Check that the Perlin noise amount and Perlin noise scale are greater than 0.0:");
			document.linef("        if(amount > 0.0F && scale > 0.0F) {");
			document.linef("//          Retrieve the surface intersection point and the surface normal from the current shape:");
			document.linef("            final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;");
			document.linef("            final int offsetIntersectionSurfaceNormal = intersectionsOffset0 + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("            ");
			document.linef("//          Retrieve the X-, Y- and Z-component values from the surface intersection point:");
			document.linef("            final float x0 = this.intersections[offsetIntersectionSurfaceIntersectionPoint];");
			document.linef("            final float y0 = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1];");
			document.linef("            final float z0 = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2];");
			document.linef("            ");
			document.linef("//          Compute the reciprocal of the Perlin noise scale:");
			document.linef("            final float scaleReciprocal = 1.0F / scale;");
			document.linef("            ");
			document.linef("//          Scale the X-, Y- and Z-component values:");
			document.linef("            final float x1 = x0 * scaleReciprocal;");
			document.linef("            final float y1 = y0 * scaleReciprocal;");
			document.linef("            final float z1 = z0 * scaleReciprocal;");
			document.linef("            ");
			document.linef("//          Compute the Perlin noise given the X-, Y- and Z-component values:");
			document.linef("            final float noiseX = doPerlinNoise(x1, y1, z1);");
			document.linef("            final float noiseY = doPerlinNoise(y1, z1, x1);");
			document.linef("            final float noiseZ = doPerlinNoise(z1, x1, y1);");
			document.linef("            ");
			document.linef("//          Calculate the surface normal:");
			document.linef("            final float surfaceNormal0X = this.intersections[offsetIntersectionSurfaceNormal];");
			document.linef("            final float surfaceNormal0Y = this.intersections[offsetIntersectionSurfaceNormal + 1];");
			document.linef("            final float surfaceNormal0Z = this.intersections[offsetIntersectionSurfaceNormal + 2];");
			document.linef("            final float surfaceNormal1X = surfaceNormal0X + noiseX * amount;");
			document.linef("            final float surfaceNormal1Y = surfaceNormal0Y + noiseY * amount;");
			document.linef("            final float surfaceNormal1Z = surfaceNormal0Z + noiseZ * amount;");
			document.linef("            final float surfaceNormal1LengthReciprocal = rsqrt(surfaceNormal1X * surfaceNormal1X + surfaceNormal1Y * surfaceNormal1Y + surfaceNormal1Z * surfaceNormal1Z);");
			document.linef("            final float surfaceNormal2X = surfaceNormal1X * surfaceNormal1LengthReciprocal;");
			document.linef("            final float surfaceNormal2Y = surfaceNormal1Y * surfaceNormal1LengthReciprocal;");
			document.linef("            final float surfaceNormal2Z = surfaceNormal1Z * surfaceNormal1LengthReciprocal;");
			document.linef("            ");
			document.linef("//          Update the intersections array with the new surface normal:");
			document.linef("            this.intersections[offsetIntersectionSurfaceNormal] = surfaceNormal2X;");
			document.linef("            this.intersections[offsetIntersectionSurfaceNormal + 1] = surfaceNormal2Y;");
			document.linef("            this.intersections[offsetIntersectionSurfaceNormal + 2] = surfaceNormal2Z;");
			document.linef("        }");
			document.linef("    }");
		} else {
			document.linef("//  private void doPerformPerlinNoiseNormalMapping(final int shapesOffset) {}");
		}
	}
	
	private static void doGenerateDoPerlinNoiseMethod(final Document document, final CompiledScene compiledScene) {
		if(compiledScene.hasPerlinNoiceNormalMapping()) {
			document.linef("    private float doPerlinNoise(final float x, final float y, final float z) {");
			document.linef("//      Calculate the floor of the X-, Y- and Z-coordinates:");
			document.linef("        final float floorX = floor(x);");
			document.linef("        final float floorY = floor(y);");
			document.linef("        final float floorZ = floor(z);");
			document.linef("        ");
			document.linef("//      Cast the previously calculated floors of the X-, Y- and Z-coordinates to ints:");
			document.linef("        final int x0 = (int)(floorX) & 0xFF;");
			document.linef("        final int y0 = (int)(floorY) & 0xFF;");
			document.linef("        final int z0 = (int)(floorZ) & 0xFF;");
			document.linef("        ");
			document.linef("//      Calculate the fractional parts of the X-, Y- and Z-coordinates by subtracting their respective floor values:");
			document.linef("        final float x1 = x - floorX;");
			document.linef("        final float y1 = y - floorY;");
			document.linef("        final float z1 = z - floorZ;");
			document.linef("        ");
			document.linef("//      Calculate the U-, V- and W-coordinates:");
			document.linef("        final float u = x1 * x1 * x1 * (x1 * (x1 * 6.0F - 15.0F) + 10.0F);");
			document.linef("        final float v = y1 * y1 * y1 * (y1 * (y1 * 6.0F - 15.0F) + 10.0F);");
			document.linef("        final float w = z1 * z1 * z1 * (z1 * (z1 * 6.0F - 15.0F) + 10.0F);");
			document.linef("        ");
			document.linef("//      Calculate some hash values:");
			document.linef("        final int a0 = this.permutations1[x0] + y0;");
			document.linef("        final int a1 = this.permutations1[a0] + z0;");
			document.linef("        final int a2 = this.permutations1[a0 + 1] + z0;");
			document.linef("        final int b0 = this.permutations1[x0 + 1] + y0;");
			document.linef("        final int b1 = this.permutations1[b0] + z0;");
			document.linef("        final int b2 = this.permutations1[b0 + 1] + z0;");
			document.linef("        final int hash0 = this.permutations1[a1] & 15;");
			document.linef("        final int hash1 = this.permutations1[b1] & 15;");
			document.linef("        final int hash2 = this.permutations1[a2] & 15;");
			document.linef("        final int hash3 = this.permutations1[b2] & 15;");
			document.linef("        final int hash4 = this.permutations1[a1 + 1] & 15;");
			document.linef("        final int hash5 = this.permutations1[b1 + 1] & 15;");
			document.linef("        final int hash6 = this.permutations1[a2 + 1] & 15;");
			document.linef("        final int hash7 = this.permutations1[b2 + 1] & 15;");
			document.linef("        ");
			document.linef("//      Calculate the gradients:");
			document.linef("        final float gradient0U = hash0 < 8 || hash0 == 12 || hash0 == 13 ? x1 : y1;");
			document.linef("        final float gradient0V = hash0 < 4 || hash0 == 12 || hash0 == 13 ? y1 : z1;");
			document.linef("        final float gradient0 = ((hash0 & 1) == 0 ? gradient0U : -gradient0U) + ((hash0 & 2) == 0 ? gradient0V : -gradient0V);");
			document.linef("        final float gradient1U = hash1 < 8 || hash1 == 12 || hash1 == 13 ? x1 - 1.0F : y1;");
			document.linef("        final float gradient1V = hash1 < 4 || hash1 == 12 || hash1 == 13 ? y1 : z1;");
			document.linef("        final float gradient1 = ((hash1 & 1) == 0 ? gradient1U : -gradient1U) + ((hash1 & 2) == 0 ? gradient1V : -gradient1V);");
			document.linef("        final float gradient2U = hash2 < 8 || hash2 == 12 || hash2 == 13 ? x1 : y1 - 1.0F;");
			document.linef("        final float gradient2V = hash2 < 4 || hash2 == 12 || hash2 == 13 ? y1 - 1.0F : z1;");
			document.linef("        final float gradient2 = ((hash2 & 1) == 0 ? gradient2U : -gradient2U) + ((hash2 & 2) == 0 ? gradient2V : -gradient2V);");
			document.linef("        final float gradient3U = hash3 < 8 || hash3 == 12 || hash3 == 13 ? x1 - 1.0F : y1 - 1.0F;");
			document.linef("        final float gradient3V = hash3 < 4 || hash3 == 12 || hash3 == 13 ? y1 - 1.0F : z1;");
			document.linef("        final float gradient3 = ((hash3 & 1) == 0 ? gradient3U : -gradient3U) + ((hash3 & 2) == 0 ? gradient3V : -gradient3V);");
			document.linef("        final float gradient4U = hash4 < 8 || hash4 == 12 || hash4 == 13 ? x1 : y1;");
			document.linef("        final float gradient4V = hash4 < 4 || hash4 == 12 || hash4 == 13 ? y1 : z1 - 1.0F;");
			document.linef("        final float gradient4 = ((hash4 & 1) == 0 ? gradient4U : -gradient4U) + ((hash4 & 2) == 0 ? gradient4V : -gradient4V);");
			document.linef("        final float gradient5U = hash5 < 8 || hash5 == 12 || hash5 == 13 ? x1 - 1.0F : y1;");
			document.linef("        final float gradient5V = hash5 < 4 || hash5 == 12 || hash5 == 13 ? y1 : z1 - 1.0F;");
			document.linef("        final float gradient5 = ((hash5 & 1) == 0 ? gradient5U : -gradient5U) + ((hash5 & 2) == 0 ? gradient5V : -gradient5V);");
			document.linef("        final float gradient6U = hash6 < 8 || hash6 == 12 || hash6 == 13 ? x1 : y1 - 1.0F;");
			document.linef("        final float gradient6V = hash6 < 4 || hash6 == 12 || hash6 == 13 ? y1 - 1.0F : z1 - 1.0F;");
			document.linef("        final float gradient6 = ((hash6 & 1) == 0 ? gradient6U : -gradient6U) + ((hash6 & 2) == 0 ? gradient6V : -gradient6V);");
			document.linef("        final float gradient7U = hash7 < 8 || hash7 == 12 || hash7 == 13 ? x1 - 1.0F : y1 - 1.0F;");
			document.linef("        final float gradient7V = hash7 < 4 || hash7 == 12 || hash7 == 13 ? y1 - 1.0F : z1 - 1.0F;");
			document.linef("        final float gradient7 = ((hash7 & 1) == 0 ? gradient7U : -gradient7U) + ((hash7 & 2) == 0 ? gradient7V : -gradient7V);");
			document.linef("        ");
			document.linef("//      Perform linear interpolation:");
			document.linef("        final float lerp0 = gradient0 + u * (gradient1 - gradient0);");
			document.linef("        final float lerp1 = gradient2 + u * (gradient3 - gradient2);");
			document.linef("        final float lerp2 = gradient4 + u * (gradient5 - gradient4);");
			document.linef("        final float lerp3 = gradient6 + u * (gradient7 - gradient6);");
			document.linef("        final float lerp4 = lerp0 + v * (lerp1 - lerp0);");
			document.linef("        final float lerp5 = lerp2 + v * (lerp3 - lerp2);");
			document.linef("        final float lerp6 = lerp4 + w * (lerp5 - lerp4);");
			document.linef("        ");
			document.linef("        return lerp6;");
			document.linef("    }");
		} else {
			document.linef("//  private float doPerlinNoise(final float x, final float y, final float z) {}");
		}
	}
	
	private static void doGenerateDoRayCastingMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_CASTER) {
			document.linef("    private void doRayCasting() {");
			document.linef("//      Calculate the current offsets to the intersections and rays arrays:");
			document.linef("        final int intersectionsOffset = getLocalId() * SIZE_INTERSECTION;");
			document.linef("        final int raysOffset = getLocalId() * SIZE_RAY;");
			document.linef("        ");
			document.linef("//      Retrieve the offsets of the ray origin and the ray direction:");
			document.linef("        final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;");
			document.linef("        final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;");
			document.linef("        ");
			document.linef("//      Initialize the origin from the primary ray:");
			document.linef("        float originX = this.rays[offsetOrigin];");
			document.linef("        float originY = this.rays[offsetOrigin + 1];");
			document.linef("        float originZ = this.rays[offsetOrigin + 2];");
			document.linef("        ");
			document.linef("//      Initialize the direction from the primary ray:");
			document.linef("        float directionX = this.rays[offsetDirection];");
			document.linef("        float directionY = this.rays[offsetDirection + 1];");
			document.linef("        float directionZ = this.rays[offsetDirection + 2];");
			document.linef("        ");
			document.linef("//      Initialize the pixel color to black:");
			document.linef("        float pixelColorR = 0.0F;");
			document.linef("        float pixelColorG = 0.0F;");
			document.linef("        float pixelColorB = 0.0F;");
			document.linef("        ");
			document.linef("//      Retrieve the pixel index:");
			document.linef("        final int pixelIndex0 = getLocalId() * 3;");
			document.linef("        ");
			document.linef("//      Perform an intersection test:");
			document.linef("        doPerformIntersectionTest(-1, originX, originY, originZ, directionX, directionY, directionZ);");
			document.linef("        ");
			document.linef("//      Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:");
			document.linef("        final float distance = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];");
			document.linef("        ");
			document.linef("//      Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:");
			document.linef("        final int shapesOffset = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);");
			document.linef("        ");
			document.linef("//      Test that an intersection was actually made, and if not, return black color (or possibly the background color):");
			document.linef("        if(distance == INFINITY || shapesOffset == -1) {");
			document.linef("//          Calculate the color for the sky in the current direction:");
			document.linef("            doCalculateColorForSky(directionX, directionY, directionZ);");
			document.linef("            ");
			document.linef("//          Add the color for the sky to the current pixel color:");
			document.linef("            pixelColorR = this.temporaryColors[pixelIndex0];");
			document.linef("            pixelColorG = this.temporaryColors[pixelIndex0 + 1];");
			document.linef("            pixelColorB = this.temporaryColors[pixelIndex0 + 2];");
			document.linef("            ");
			document.linef("//          Update the current pixel color:");
			document.linef("            this.currentPixelColors[pixelIndex0] = pixelColorR;");
			document.linef("            this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;");
			document.linef("            this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;");
			document.linef("            ");
			document.linef("            return;");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      Calculate the albedo texture color for the intersected shape:");
			document.linef("        doCalculateTextureColor(CompiledScene.SURFACE_RELATIVE_OFFSET_TEXTURES_OFFSET_ALBEDO, shapesOffset);");
			document.linef("        ");
			document.linef("//      Get the color of the shape from the albedo texture color that was looked up:");
			document.linef("        float albedoColorR = this.temporaryColors[pixelIndex0];");
			document.linef("        float albedoColorG = this.temporaryColors[pixelIndex0 + 1];");
			document.linef("        float albedoColorB = this.temporaryColors[pixelIndex0 + 2];");
			document.linef("        ");
			document.linef("//      Retrieve the sun direction:");
			document.linef("        final float lightDirectionX = -this.sunDirectionX;");
			document.linef("        final float lightDirectionY = -this.sunDirectionY;");
			document.linef("        final float lightDirectionZ = -this.sunDirectionZ;");
			document.linef("        ");
			document.linef("//      Retrieve the offsets of the surface intersection point and the surface normal in the intersections array:");
			document.linef("        final int offsetIntersectionSurfaceIntersectionPoint = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT;");
			document.linef("        final int offsetIntersectionSurfaceNormal = intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL;");
			document.linef("        ");
			document.linef("//      Retrieve the surface intersection point from the intersections array:");
			document.linef("        final float surfaceIntersectionPointX = this.intersections[offsetIntersectionSurfaceIntersectionPoint];");
			document.linef("        final float surfaceIntersectionPointY = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 1];");
			document.linef("        final float surfaceIntersectionPointZ = this.intersections[offsetIntersectionSurfaceIntersectionPoint + 2];");
			document.linef("        ");
			document.linef("//      Retrieve the surface normal from the intersections array:");
			document.linef("        final float surfaceNormalX = this.intersections[offsetIntersectionSurfaceNormal];");
			document.linef("        final float surfaceNormalY = this.intersections[offsetIntersectionSurfaceNormal + 1];");
			document.linef("        final float surfaceNormalZ = this.intersections[offsetIntersectionSurfaceNormal + 2];");
			document.linef("        ");
			document.linef("//      Initialize the pixel color components with ambient lighting:");
			document.linef("        pixelColorR = albedoColorR * 0.5F;");
			document.linef("        pixelColorG = albedoColorG * 0.5F;");
			document.linef("        pixelColorB = albedoColorB * 0.5F;");
			document.linef("        ");
			document.linef("//      Perform an intersection test:");
			document.linef("        doPerformIntersectionTestOnly(shapesOffset, surfaceIntersectionPointX, surfaceIntersectionPointY, surfaceIntersectionPointZ, lightDirectionX, lightDirectionY, lightDirectionZ);");
			document.linef("        ");
			document.linef("//      Retrieve the distance to the closest intersected shape, or INFINITY if no shape were intersected:");
			document.linef("        final float distance0 = this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_DISTANCE];");
			document.linef("        ");
			document.linef("//      Retrieve the offset in the shapes array of the closest intersected shape, or -1 if no shape were intersected:");
			document.linef("        final int shapesOffset0 = (int)(this.intersections[intersectionsOffset + RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET]);");
			document.linef("        ");
			document.linef("        if(distance0 != INFINITY && shapesOffset0 != -1) {");
			document.linef("//          Initialize the intensity of the light:");
			document.linef("            final float lightIntensity = 1.0F;");
			document.linef("            ");
			document.linef("//          Calculate the dot product between the surface normal and the sun direction:");
			document.linef("            final float dotProduct = surfaceNormalX * lightDirectionX + surfaceNormalY * lightDirectionY + surfaceNormalZ * lightDirectionZ;");
			document.linef("            final float dotProductOrZero = max(dotProduct, 0.0F);");
			document.linef("            ");
			document.linef("            final float diffuseColorR = albedoColorR * dotProductOrZero * lightIntensity;");
			document.linef("            final float diffuseColorG = albedoColorG * dotProductOrZero * lightIntensity;");
			document.linef("            final float diffuseColorB = albedoColorB * dotProductOrZero * lightIntensity;");
			document.linef("            ");
			document.linef("            final float reflectionX = surfaceNormalX * 2.0F * dotProduct - lightDirectionX;");
			document.linef("            final float reflectionY = surfaceNormalY * 2.0F * dotProduct - lightDirectionY;");
			document.linef("            final float reflectionZ = surfaceNormalZ * 2.0F * dotProduct - lightDirectionZ;");
			document.linef("            ");
			document.linef("            final float dotProduct0 = -directionX * reflectionX + -directionY * reflectionY + -directionZ * reflectionZ;");
			document.linef("            ");
			document.linef("            final float specularPower = 25.0F;");
			document.linef("            final float specularAmount = dotProduct0 < 0.0F ? 0.0F : pow(dotProduct0, specularPower) * lightIntensity;");
			document.linef("            final float specularColorR = specularAmount;");
			document.linef("            final float specularColorG = specularAmount;");
			document.linef("            final float specularColorB = specularAmount;");
			document.linef("            ");
			document.linef("//          Calculate the lighting and add to the pixel color:");
			document.linef("            pixelColorR += diffuseColorR + specularColorR;");
			document.linef("            pixelColorG += diffuseColorG + specularColorG;");
			document.linef("            pixelColorB += diffuseColorB + specularColorB;");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      Update the current pixel color:");
			document.linef("        this.currentPixelColors[pixelIndex0] = pixelColorR;");
			document.linef("        this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;");
			document.linef("        this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;");
			document.linef("    }");
		} else {
			document.linef("//  private void doRayCasting() {}");
		}
	}
	
	private static void doGenerateDoRayMarchingMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private void doRayMarching() {");
			document.linef("        final int pixelIndex0 = getLocalId() * 3;");
			document.linef("        final int raysOffset = getLocalId() * SIZE_RAY;");
			document.linef("        final int offsetOrigin = raysOffset + RELATIVE_OFFSET_RAY_ORIGIN;");
			document.linef("        final int offsetDirection = raysOffset + RELATIVE_OFFSET_RAY_DIRECTION;");
			document.linef("        ");
			document.linef("        float originX = this.rays[offsetOrigin];");
			document.linef("        float originY = this.rays[offsetOrigin + 1];");
			document.linef("        float originZ = this.rays[offsetOrigin + 2];");
			document.linef("        ");
			document.linef("        originY = doGetY(originX, originZ) + 0.1F;//max(doGetY(originX, originZ) + 0.1F, 0.022F);");
			document.linef("        ");
			document.linef("        float directionX = this.rays[offsetDirection];");
			document.linef("        float directionY = this.rays[offsetDirection + 1];");
			document.linef("        float directionZ = this.rays[offsetDirection + 2];");
			document.linef("        ");
			document.linef("        float delT = 0.01F;");
			document.linef("        ");
			document.linef("        final float minT = 0.001F;");
			document.linef("        final float maxT = max(originY, 1.0F) * 20.0F;");
			document.linef("        ");
			document.linef("//      float lh = 0.0F;");
			document.linef("//      float ly = 0.0F;");
			document.linef("        ");
			document.linef("//      float hitT = -1.0F;");
			document.linef("        ");
			document.linef("        doCalculateColorForSky(directionX, directionY, directionZ);");
			document.linef("        ");
			document.linef("        float pixelColorR = this.temporaryColors[pixelIndex0];");
			document.linef("        float pixelColorG = this.temporaryColors[pixelIndex0 + 1];");
			document.linef("        float pixelColorB = this.temporaryColors[pixelIndex0 + 2];");
			document.linef("        ");
			document.linef("        final float sunDirectionX = this.sunDirectionX;");
			document.linef("        final float sunDirectionY = this.sunDirectionY;");
			document.linef("        final float sunDirectionZ = this.sunDirectionZ;");
			document.linef("        ");
			document.linef("        final float sunDistance = 1000.0F;");
			document.linef("        ");
			document.linef("        final float sunPositionX = sunDirectionX * sunDistance;");
			document.linef("        final float sunPositionY = sunDirectionY * sunDistance;");
			document.linef("        final float sunPositionZ = sunDirectionZ * sunDistance;");
			document.linef("        ");
			document.linef("        final float sunAmbientCoefficient = 0.2F;");
			document.linef("        ");
			document.linef("//      final float sunLightIntensity = 3.0F;");
			document.linef("        ");
			document.linef("        for(float t = minT; t < maxT; t += delT) {");
			document.linef("            final float surfaceIntersectionPointX = originX + directionX * t;");
			document.linef("            final float surfaceIntersectionPointY = originY + directionY * t;");
			document.linef("            final float surfaceIntersectionPointZ = originZ + directionZ * t;");
			document.linef("            ");
			document.linef("            final float height = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ);");
			document.linef("            ");
			document.linef("            if(surfaceIntersectionPointY < height) {");
			document.linef("//              hitT = t - delT + delT * (lh - ly) / (surfaceIntersectionPointY - ly - height + lh);");
			document.linef("                ");
			document.linef("                final float gray = 0.5F;");
			document.linef("                ");
			document.linef("//              Calculate the albedo color of the surface intersection point:");
			document.linef("                float albedoColorR = gray;");
			document.linef("                float albedoColorG = gray;");
			document.linef("                float albedoColorB = gray;");
			document.linef("                ");
			document.linef("                /*");
			document.linef("                if(height > 0.1F) {");
			document.linef("                    albedoColorR = gray;");
			document.linef("                    albedoColorG = gray;");
			document.linef("                    albedoColorB = gray;");
			document.linef("                } else if(height > 0.02F) {");
			document.linef("                    albedoColorR = gray;");
			document.linef("                    albedoColorG = gray;");
			document.linef("                    albedoColorB = gray;");
			document.linef("                } else {");
			document.linef("                    albedoColorR = gray;");
			document.linef("                    albedoColorG = gray;");
			document.linef("                    albedoColorB = gray;");
			document.linef("                }");
			document.linef("                */");
			document.linef("                ");
			document.linef("//              Calculate the surface normal at the surface intersection point:");
			document.linef("                final float surfaceNormal0X = doGetY(surfaceIntersectionPointX - EPSILON, surfaceIntersectionPointZ) - doGetY(surfaceIntersectionPointX + EPSILON, surfaceIntersectionPointZ);");
			document.linef("                final float surfaceNormal0Y = 2.0F * EPSILON;");
			document.linef("                final float surfaceNormal0Z = doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ - EPSILON) - doGetY(surfaceIntersectionPointX, surfaceIntersectionPointZ + EPSILON);");
			document.linef("                final float surfaceNormal0LengthReciprocal = 1.0F / sqrt(surfaceNormal0X * surfaceNormal0X + surfaceNormal0Y * surfaceNormal0Y + surfaceNormal0Z * surfaceNormal0Z);");
			document.linef("                final float surfaceNormal1X = surfaceNormal0X * surfaceNormal0LengthReciprocal;");
			document.linef("                final float surfaceNormal1Y = surfaceNormal0Y * surfaceNormal0LengthReciprocal;");
			document.linef("                final float surfaceNormal1Z = surfaceNormal0Z * surfaceNormal0LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Calculate the sun and sky color given the surface normal at the surface intersection point:");
			document.linef("//              doCalculateColorForSky(pixelIndex, surfaceNormal1X, surfaceNormal1Y, surfaceNormal1Z);");
			document.linef("                ");
			document.linef("//              final float sunAndSkyColorR = this.temporaryColors[pixelIndex0];");
			document.linef("//              final float sunAndSkyColorG = this.temporaryColors[pixelIndex0 + 1];");
			document.linef("//              final float sunAndSkyColorB = this.temporaryColors[pixelIndex0 + 2];");
			document.linef("                ");
			document.linef("//              Calculate the direction from the surface intersection point to the sun:");
			document.linef("                final float surfaceToSun0X = sunPositionX - surfaceIntersectionPointX;");
			document.linef("                final float surfaceToSun0Y = sunPositionY - surfaceIntersectionPointY;");
			document.linef("                final float surfaceToSun0Z = sunPositionZ - surfaceIntersectionPointZ;");
			document.linef("                final float surfaceToSun0LengthReciprocal = 1.0F / sqrt(surfaceToSun0X * surfaceToSun0X + surfaceToSun0Y * surfaceToSun0Y + surfaceToSun0Z * surfaceToSun0Z);");
			document.linef("                final float surfaceToSun1X = surfaceToSun0X * surfaceToSun0LengthReciprocal;");
			document.linef("                final float surfaceToSun1Y = surfaceToSun0Y * surfaceToSun0LengthReciprocal;");
			document.linef("                final float surfaceToSun1Z = surfaceToSun0Z * surfaceToSun0LengthReciprocal;");
			document.linef("                ");
			document.linef("//              Calculate the direction from the surface intersection point to the camera:");
			document.linef("                final float surfaceToCameraX = -directionX;");
			document.linef("                final float surfaceToCameraY = -directionY;");
			document.linef("                final float surfaceToCameraZ = -directionZ;");
			document.linef("                ");
			document.linef("//              Calculate the ambient color:");
			document.linef("                final float ambientColorR = sunAmbientCoefficient * albedoColorR * 1.0F;//sunAndSkyColorR;");
			document.linef("                final float ambientColorG = sunAmbientCoefficient * albedoColorG * 1.0F;//sunAndSkyColorG;");
			document.linef("                final float ambientColorB = sunAmbientCoefficient * albedoColorB * 1.0F;//sunAndSkyColorB;");
			document.linef("                ");
			document.linef("//              Calculate the diffuse color:");
			document.linef("                final float dotProductSurfaceNormalAndSurfaceToSun = surfaceNormal1X * surfaceToSun1X + surfaceNormal1Y * surfaceToSun1Y + surfaceNormal1Z * surfaceToSun1Z;");
			document.linef("                final float diffuseCoefficient = max(dotProductSurfaceNormalAndSurfaceToSun, 0.0F);");
			document.linef("                final float diffuseColorR = diffuseCoefficient * albedoColorR * 1.0F;//sunAndSkyColorR;");
			document.linef("                final float diffuseColorG = diffuseCoefficient * albedoColorG * 1.0F;//sunAndSkyColorG;");
			document.linef("                final float diffuseColorB = diffuseCoefficient * albedoColorB * 1.0F;//sunAndSkyColorB;");
			document.linef("                ");
			document.linef("//              Calculate the specular color:");
			document.linef("                float specularCoefficient = 0.0F;");
			document.linef("                ");
			document.linef("                if(diffuseCoefficient > 0.0F) {");
			document.linef("                    final float incidentX = -surfaceToSun1X;");
			document.linef("                    final float incidentY = -surfaceToSun1Y;");
			document.linef("                    final float incidentZ = -surfaceToSun1Z;");
			document.linef("                    ");
			document.linef("                    final float dotProductSurfaceNormalAndIncident = surfaceNormal1X * incidentX + surfaceNormal1Y * incidentY + surfaceNormal1Z * incidentZ;");
			document.linef("                    ");
			document.linef("                    final float reflectionX = incidentX - 2.0F * dotProductSurfaceNormalAndIncident * surfaceNormal1X;");
			document.linef("                    final float reflectionY = incidentY - 2.0F * dotProductSurfaceNormalAndIncident * surfaceNormal1Y;");
			document.linef("                    final float reflectionZ = incidentZ - 2.0F * dotProductSurfaceNormalAndIncident * surfaceNormal1Z;");
			document.linef("                    ");
			document.linef("                    final float dotProductSurfaceToCameraAndReflection = surfaceToCameraX * reflectionX + surfaceToCameraY * reflectionY + surfaceToCameraZ * reflectionZ;");
			document.linef("                    ");
			document.linef("                    final float specularPower = 50.0F;");
			document.linef("                    ");
			document.linef("                    specularCoefficient = pow(max(dotProductSurfaceToCameraAndReflection, 0.0F), specularPower);");
			document.linef("                }");
			document.linef("                ");
			document.linef("                final float specularColorR = specularCoefficient * 1.0F * 1.0F;//sunAndSkyColorR;");
			document.linef("                final float specularColorG = specularCoefficient * 1.0F * 1.0F;//sunAndSkyColorG;");
			document.linef("                final float specularColorB = specularCoefficient * 1.0F * 1.0F;//sunAndSkyColorB;");
			document.linef("                ");
			document.linef("//              Calculate the final color in linear color space:");
			document.linef("                pixelColorR = ambientColorR + diffuseColorR + specularColorR;");
			document.linef("                pixelColorG = ambientColorG + diffuseColorG + specularColorG;");
			document.linef("                pixelColorB = ambientColorB + diffuseColorB + specularColorB;");
			document.linef("                ");
			document.linef("                /*");
			document.linef("                final float dotProduct = surfaceNormal1X * sunDirectionX + surfaceNormal1Y * sunDirectionY + surfaceNormal1Z * sunDirectionZ;");
			document.linef("                final float dotProductOrZero = max(dotProduct, 0.0F);");
			document.linef("                ");
			document.linef("                final float ambientColorR = albedoColorR * this.temporaryColors[pixelIndex0];");
			document.linef("                final float ambientColorG = albedoColorG * this.temporaryColors[pixelIndex0 + 1];");
			document.linef("                final float ambientColorB = albedoColorB * this.temporaryColors[pixelIndex0 + 2];");
			document.linef("                ");
			document.linef("                final float diffuseColorR = albedoColorR * dotProductOrZero * sunLightIntensity;");
			document.linef("                final float diffuseColorG = albedoColorG * dotProductOrZero * sunLightIntensity;");
			document.linef("                final float diffuseColorB = albedoColorB * dotProductOrZero * sunLightIntensity;");
			document.linef("                ");
			document.linef("                final float reflectionX = surfaceNormal1X * 2.0F * dotProduct - sunDirectionX;");
			document.linef("                final float reflectionY = surfaceNormal1Y * 2.0F * dotProduct - sunDirectionY;");
			document.linef("                final float reflectionZ = surfaceNormal1Z * 2.0F * dotProduct - sunDirectionZ;");
			document.linef("                ");
			document.linef("                final float dotProduct0 = -directionX * reflectionX + -directionY * reflectionY + -directionZ * reflectionZ;");
			document.linef("                ");
			document.linef("                final float specularPower = 128.0F;");
			document.linef("                final float specularAmount = dotProduct0 < 0.0F ? 0.0F : pow(dotProduct0, specularPower) * sunLightIntensity;");
			document.linef("                final float specularColorR = specularAmount;");
			document.linef("                final float specularColorG = specularAmount;");
			document.linef("                final float specularColorB = specularAmount;");
			document.linef("                ");
			document.linef("                pixelColorR = ambientColorR + diffuseColorR + specularColorR;");
			document.linef("                pixelColorG = ambientColorG + diffuseColorG + specularColorG;");
			document.linef("                pixelColorB = ambientColorB + diffuseColorB + specularColorB;");
			document.linef("                */");
			document.linef("                ");
			document.linef("//              final float fogColorR = 0.5F;");
			document.linef("//              final float fogColorG = 0.6F;");
			document.linef("//              final float fogColorB = 0.7F;");
			document.linef("                ");
			document.linef("//              final float distance = 20.0F;//hitT;");
			document.linef("//              final float falloff = 0.2F;");
			document.linef("//              final float exponent = exp(-distance * falloff);");
			document.linef("                ");
			document.linef("//              pixelColorR = pixelColorR * (1.0F - exponent) + fogColorR * exponent;");
			document.linef("//              pixelColorG = pixelColorG * (1.0F - exponent) + fogColorG * exponent;");
			document.linef("//              pixelColorB = pixelColorB * (1.0F - exponent) + fogColorB * exponent;");
			document.linef("                ");
			document.linef("                t = maxT;");
			document.linef("            }");
			document.linef("            ");
			document.linef("            delT = 0.01F * t;");
			document.linef("            ");
			document.linef("//          lh = height;");
			document.linef("//          ly = surfaceIntersectionPointY;");
			document.linef("        }");
			document.linef("        ");
			document.linef("//      if(originY <= 0.02F) {");
			document.linef("//          pixelColorB += 0.4F;");
			document.linef("//      }");
			document.linef("        ");
			document.linef("        this.currentPixelColors[pixelIndex0] = pixelColorR;");
			document.linef("        this.currentPixelColors[pixelIndex0 + 1] = pixelColorG;");
			document.linef("        this.currentPixelColors[pixelIndex0 + 2] = pixelColorB;");
			document.linef("    }");
		} else {
			document.linef("//  private void doRayMarching() {}");
		}
	}
	
	private static void doGenerateDoSimplexFractalXMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    /*");
			document.linef("    private float doSimplexFractalX(final int octaves, final float x) {");
			document.linef("        float result = 0.0F;");
			document.linef("        ");
			document.linef("        float amplitude = this.amplitude;");
			document.linef("        float frequency = this.frequency;");
			document.linef("        ");
			document.linef("        for(int i = 0; i < octaves; i++) {");
			document.linef("            result += amplitude * doSimplexNoiseX(x * frequency);");
			document.linef("            ");
			document.linef("            amplitude *= this.gain;");
			document.linef("            frequency *= this.lacunarity;");
			document.linef("        }");
			document.linef("        ");
			document.linef("        return result;");
			document.linef("    }");
			document.linef("    */");
		} else {
			document.linef("//  private float doSimplexFractalX(final int octaves, final float x) {}");
		}
	}
	
	private static void doGenerateDoSimplexFractalXYMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private float doSimplexFractalXY(final int octaves, final float x, final float y) {");
			document.linef("        float result = 0.0F;");
			document.linef("        ");
			document.linef("        float amplitude = this.amplitude;");
			document.linef("        float frequency = this.frequency;");
			document.linef("        ");
			document.linef("        for(int i = 0; i < octaves; i++) {");
			document.linef("            result += amplitude * doSimplexNoiseXY(x * frequency, y * frequency);");
			document.linef("            ");
			document.linef("            amplitude *= this.gain;");
			document.linef("            frequency *= this.lacunarity;");
			document.linef("        }");
			document.linef("        ");
			document.linef("        return result;");
			document.linef("    }");
		} else {
			document.linef("//  private float doSimplexFractalXY(final int octaves, final float x, final float y) {}");
		}
	}
	
	private static void doGenerateDoSimplexNoiseXMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    /*");
			document.linef("    private float doSimplexNoiseX(final float x) {");
			document.linef("        final int i0 = doFastFloor(x);");
			document.linef("        final int i1 = i0 + 1;");
			document.linef("        ");
			document.linef("        final float x0 = x - i0;");
			document.linef("        final float x1 = x0 - 1.0F;");
			document.linef("        ");
			document.linef("        final float t00 = 1.0F - x0 * x0;");
			document.linef("        final float t01 = t00 * t00;");
			document.linef("        ");
			document.linef("        final float t10 = 1.0F - x1 * x1;");
			document.linef("        final float t11 = t10 * t10;");
			document.linef("        ");
			document.linef("        final float n0 = t01 * t01 * doGradientX(doHash(i0), x0);");
			document.linef("        final float n1 = t11 * t11 * doGradientX(doHash(i1), x1);");
			document.linef("        ");
			document.linef("        return 0.395F * (n0 + n1);");
			document.linef("    }");
			document.linef("    */");
		} else {
			document.linef("//  private float doSimplexNoiseX(final float x) {}");
		}
	}
	
	private static void doGenerateDoSimplexNoiseXYMethod(final Document document, final RendererType rendererType) {
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private float doSimplexNoiseXY(final float x, final float y) {");
			document.linef("        final float a = 0.366025403F;");
			document.linef("        final float b = 0.211324865F;");
			document.linef("        ");
			document.linef("        final float s = (x + y) * a;");
			document.linef("        final float sx = s + x;");
			document.linef("        final float sy = s + y;");
			document.linef("        ");
			document.linef("        final int i0 = doFastFloor(sx);");
			document.linef("        final int j0 = doFastFloor(sy);");
			document.linef("        ");
			document.linef("        final float t = (i0 + j0) * b;");
			document.linef("        ");
			document.linef("        final float x00 = i0 - t;");
			document.linef("        final float y00 = j0 - t;");
			document.linef("        final float x01 = x - x00;");
			document.linef("        final float y01 = y - y00;");
			document.linef("        ");
			document.linef("        final int i1 = x01 > y01 ? 1 : 0;");
			document.linef("        final int j1 = x01 > y01 ? 0 : 1;");
			document.linef("        ");
			document.linef("        final float x1 = x01 - i1 + b;");
			document.linef("        final float y1 = y01 - j1 + b;");
			document.linef("        final float x2 = x01 - 1.0F + 2.0F * b;");
			document.linef("        final float y2 = y01 - 1.0F + 2.0F * b;");
			document.linef("        ");
			document.linef("        final float t00 = 0.5F - x01 * x01 - y01 * y01;");
			document.linef("        final float t01 = t00 < 0.0F ? t00 : t00 * t00;");
			document.linef("        ");
			document.linef("        final float n0 = t00 < 0.0F ? 0.0F : t01 * t01 * doGradientXY(doHash(i0 + doHash(j0)), x01, y01);");
			document.linef("        ");
			document.linef("        final float t10 = 0.5F - x1 * x1 - y1 * y1;");
			document.linef("        final float t11 = t10 < 0.0F ? t10 : t10 * t10;");
			document.linef("        ");
			document.linef("        final float n1 = t10 < 0.0F ? 0.0F : t11 * t11 * doGradientXY(doHash(i0 + i1 + doHash(j0 + j1)), x1, y1);");
			document.linef("        ");
			document.linef("        final float t20 = 0.5F - x2 * x2 - y2 * y2;");
			document.linef("        final float t21 = t20 < 0.0F ? t20 : t20 * t20;");
			document.linef("        ");
			document.linef("        final float n2 = t20 < 0.0F ? 0.0F : t21 * t21 * doGradientXY(doHash(i0 + 1 + doHash(j0 + 1)), x2, y2);");
			document.linef("        ");
			document.linef("        return 45.23065F * (n0 + n1 + n2);");
			document.linef("    }");
		} else {
			document.linef("//  private float doSimplexNoiseXY(final float x, final float y) {}");
		}
	}
	
	private static void doGenerateFields(final Document document, final RendererType rendererType) {
		document.linef("    private static final float PHONG_EXPONENT = 20.0F;");
		document.linef("    private static final float PHONE_EXPONENT_PLUS_ONE_RECIPROCAL = 1.0F / (PHONG_EXPONENT + 1.0F);");
		document.linef("    private static final float REFRACTIVE_INDEX_0 = 1.0F;");
		document.linef("    private static final float REFRACTIVE_INDEX_1 = 1.5F;");
		document.linef("    private static final float REFRACTIVE_INDEX_A = REFRACTIVE_INDEX_1 - REFRACTIVE_INDEX_0;");
		document.linef("    private static final float REFRACTIVE_INDEX_B = REFRACTIVE_INDEX_1 + REFRACTIVE_INDEX_0;");
		document.linef("    private static final float REFRACTIVE_INDEX_0_DIVIDED_BY_REFRACTIVE_INDEX_1 = REFRACTIVE_INDEX_0 / REFRACTIVE_INDEX_1;");
		document.linef("    private static final float REFRACTIVE_INDEX_1_DIVIDED_BY_REFRACTIVE_INDEX_0 = REFRACTIVE_INDEX_1 / REFRACTIVE_INDEX_0;");
		document.linef("    private static final float REFRACTIVE_INDEX_R0 = (REFRACTIVE_INDEX_A * REFRACTIVE_INDEX_A) / (REFRACTIVE_INDEX_B * REFRACTIVE_INDEX_B);");
		document.linef("    private static final int MATERIAL_CLEAR_COAT = 0;");
		document.linef("    private static final int MATERIAL_GLASS = 1;");
		document.linef("    private static final int MATERIAL_LAMBERTIAN_DIFFUSE = 2;");
		document.linef("    private static final int MATERIAL_MIRROR = 3;");
		document.linef("    private static final int MATERIAL_PHONG_METAL = 4;");
		document.linef("    private static final int RELATIVE_OFFSET_INTERSECTION_DISTANCE = 0;");
		document.linef("    private static final int RELATIVE_OFFSET_INTERSECTION_SHAPES_OFFSET = 1;");
		document.linef("    private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_INTERSECTION_POINT = 2;");
		document.linef("    private static final int RELATIVE_OFFSET_INTERSECTION_SURFACE_NORMAL = 5;");
		document.linef("    private static final int RELATIVE_OFFSET_INTERSECTION_UV_COORDINATES = 8;");
		document.linef("    private static final int RELATIVE_OFFSET_RAY_DIRECTION = 3;");
		document.linef("    private static final int RELATIVE_OFFSET_RAY_ORIGIN = 0;");
		document.linef("    private static final int SIZE_INTERSECTION = 10;");
		document.linef("    private static final int SIZE_PIXEL = 4;");
		document.linef("    private static final int SIZE_RAY = 6;");
		document.linef("    ");
		document.linef("    ////////////////////////////////////////////////////////////////////////////////////////////////////");
		document.linef("    ");
		document.linef("    private final boolean isResettingFully;");
		document.linef("    private byte[] pixels;");
		document.linef("    private final CompiledScene compiledScene;");
		
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private float amplitude;");
			document.linef("    private float frequency;");
			document.linef("    private float gain;");
			document.linef("    private float lacunarity;");
		}
		
		document.linef("    private float orthoNormalBasisUX;");
		document.linef("    private float orthoNormalBasisUY;");
		document.linef("    private float orthoNormalBasisUZ;");
		document.linef("    private float orthoNormalBasisVX;");
		document.linef("    private float orthoNormalBasisVY;");
		document.linef("    private float orthoNormalBasisVZ;");
		document.linef("    private float orthoNormalBasisWX;");
		document.linef("    private float orthoNormalBasisWY;");
		document.linef("    private float orthoNormalBasisWZ;");
		document.linef("    private float sunDirectionX;");
		document.linef("    private float sunDirectionY;");
		document.linef("    private float sunDirectionZ;");
		document.linef("    private float theta;");
		document.linef("    private float zenithRelativeLuminance;");
		document.linef("    private float zenithX;");
		document.linef("    private float zenithY;");
		document.linef("    private final float[] accumulatedPixelColors;");
		document.linef("    @Constant");
		document.linef("    private final float[] boundingVolumeHierarchy;");
		document.linef("    @Constant");
		document.linef("    private final float[] cameraArray;");
		document.linef("    @Local");
		document.linef("    private float[] currentPixelColors;");
		document.linef("    @Local");
		document.linef("    private float[] intersections;");
		document.linef("    @Constant");
		document.linef("    private final float[] perezRelativeLuminance;");
		document.linef("    @Constant");
		document.linef("    private final float[] perezX;");
		document.linef("    @Constant");
		document.linef("    private final float[] perezY;");
		document.linef("    @Constant");
		document.linef("    private final float[] point2s;");
		document.linef("    @Constant");
		document.linef("    private final float[] point3s;");
		document.linef("    @Local");
		document.linef("    private float[] rays;");
		document.linef("    @Constant");
		document.linef("    private final float[] shapes;");
		document.linef("    @Constant");
		document.linef("    private final float[] surfaces;");
		document.linef("    @Local");
		document.linef("    private float[] temporaryColors;");
		document.linef("    @Constant");
		document.linef("    private final float[] textures;");
		document.linef("    @Constant");
		document.linef("    private final float[] vector3s;");
		document.linef("    private int depthMaximum = 1;");
		document.linef("    private int depthRussianRoulette = 5;");
		
		if(rendererType == RendererType.RAY_MARCHER) {
			document.linef("    private int octaves;");
		}
		
		document.linef("    private int shapeOffsetsLength;");
		document.linef("    private final int width;");
		document.linef("    @Constant");
		document.linef("    private final int[] permutations0 = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 23, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};");
		document.linef("    @Constant");
		document.linef("    private final int[] permutations1 = new int[512];");
		document.linef("    @Constant");
		document.linef("    private final int[] shapeOffsets;");
		document.linef("    private final long[] subSamples;");
	}
	
	private static void doGenerateGetDepthMaximumMethod(final Document document) {
		document.linef("    @Override");
		document.linef("    public int getDepthMaximum() {");
		document.linef("        return this.depthMaximum;");
		document.linef("    }");
	}
	
	private static void doGenerateGetDepthRussianRouletteMethod(final Document document) {
		document.linef("    @Override");
		document.linef("    public int getDepthRussianRoulette() {");
		document.linef("        return this.depthRussianRoulette;");
		document.linef("    }");
	}
	
	private static void doGenerateGetPixelsMethod(final Document document) {
		document.linef("    @Override");
		document.linef("    public byte[] getPixels() {");
		document.linef("        return this.pixels;");
		document.linef("    }");
	}
	
	private static void doGenerateImportDeclarations(final Document document) {
		document.linef("import java.io.File;");
		document.linef("import java.util.Objects;");
		document.linef("");
		document.linef("import org.dayflower.pathtracer.camera.Camera;");
		document.linef("import org.dayflower.pathtracer.scene.Scene;");
		document.linef("import org.dayflower.pathtracer.scene.Sky;");
	}
	
	private static void doGenerateIndentation(final Document document, final int spaces) {
		document.linef("%s", doComputeIndentation(spaces));
	}
	
	private static void doGenerateLicenseComment(final Document document) {
		document.linef("/**");
		document.linef(" * Copyright 2009 - 2017 J&#246;rgen Lundgren");
		document.linef(" * ");
		document.linef(" * This file is part of Dayflower.");
		document.linef(" * ");
		document.linef(" * Dayflower is free software: you can redistribute it and/or modify");
		document.linef(" * it under the terms of the GNU Lesser General Public License as published by");
		document.linef(" * the Free Software Foundation, either version 3 of the License, or");
		document.linef(" * (at your option) any later version.");
		document.linef(" * ");
		document.linef(" * Dayflower is distributed in the hope that it will be useful,");
		document.linef(" * but WITHOUT ANY WARRANTY; without even the implied warranty of");
		document.linef(" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
		document.linef(" * GNU Lesser General Public License for more details.");
		document.linef(" * ");
		document.linef(" * You should have received a copy of the GNU Lesser General Public License");
		document.linef(" * along with Dayflower. If not, see <http://www.gnu.org/licenses/>.");
		document.linef(" */");
	}
	
	private static void doGeneratePackageDeclaration(final Document document) {
		document.linef("package org.dayflower.pathtracer.kernel;");
	}
	
	private static void doGenerateResetMethod(final Document document, final String className) {
		document.linef("    @Override");
		document.linef("    public %s reset() {", className);
		document.linef("        for(int i = 0; i < this.subSamples.length; i++) {");
		document.linef("            if(this.isResettingFully) {");
		document.linef("                final int pixelIndex = i * 3;");
		document.linef("                ");
		document.linef("                this.accumulatedPixelColors[pixelIndex + 0] = 0.0F;");
		document.linef("                this.accumulatedPixelColors[pixelIndex + 1] = 0.0F;");
		document.linef("                this.accumulatedPixelColors[pixelIndex + 2] = 0.0F;");
		document.linef("                this.subSamples[i] = 0L;");
		document.linef("            } else {");
		document.linef("                this.subSamples[i] = 1L;");
		document.linef("            }");
		document.linef("        }");
		document.linef("        ");
		document.linef("        getCamera().ifPresent(camera -> {");
		document.linef("            camera.update();");
		document.linef("            ");
		document.linef("            put(this.cameraArray);");
		document.linef("        });");
		document.linef("        ");
		document.linef("        if(this.isResettingFully) {");
		document.linef("            put(this.accumulatedPixelColors);");
		document.linef("        }");
		document.linef("        ");
		document.linef("        put(this.subSamples);");
		document.linef("        ");
		document.linef("        return this;");
		document.linef("    }");
	}
	
	private static void doGenerateRunMethod(final Document document, final RendererType rendererType) {
		document.linef("    @Override");
		document.linef("    public void run() {");
		document.linef("        final int pixelIndex = getGlobalId();");
		document.linef("        ");
		document.linef("        if(doCreatePrimaryRay(pixelIndex)) {");
		document.linef("            %s();", rendererType == RendererType.PATH_TRACER ? "doPathTracing" : rendererType == RendererType.RAY_CASTER ? "doRayCasting" : "doRayMarching");
		document.linef("        } else {");
		document.linef("            final int pixelIndex0 = pixelIndex * 3;");
		document.linef("            final int pixelIndex1 = getLocalId() * 3;");
		document.linef("            ");
		document.linef("            this.accumulatedPixelColors[pixelIndex0] = 0.0F;");
		document.linef("            this.accumulatedPixelColors[pixelIndex0 + 1] = 0.0F;");
		document.linef("            this.accumulatedPixelColors[pixelIndex0 + 2] = 0.0F;");
		document.linef("            ");
		document.linef("            this.currentPixelColors[pixelIndex1] = 0.0F;");
		document.linef("            this.currentPixelColors[pixelIndex1 + 1] = 0.0F;");
		document.linef("            this.currentPixelColors[pixelIndex1 + 2] = 0.0F;");
		document.linef("            ");
		document.linef("            this.subSamples[pixelIndex] = 1L;");
		document.linef("        }");
		document.linef("        ");
		document.linef("        doCalculateColor(pixelIndex);");
		document.linef("    }");
	}
	
	private static void doGenerateSeparatorComment(final Document document, final int spaces) {
		document.linef("%s////////////////////////////////////////////////////////////////////////////////////////////////////", doComputeIndentation(spaces));
	}
	
	private static void doGenerateSetDepthMaximumMethod(final Document document) {
		document.linef("    @Override");
		document.linef("    public void setDepthMaximum(final int depthMaximum) {");
		document.linef("        this.depthMaximum = depthMaximum;");
		document.linef("    }");
	}
	
	private static void doGenerateSetDepthRussianRouletteMethod(final Document document) {
		document.linef("    @Override");
		document.linef("    public void setDepthRussianRoulette(final int depthRussianRoulette) {");
		document.linef("        this.depthRussianRoulette = depthRussianRoulette;");
		document.linef("    }");
	}
	
	private static void doGenerateUpdateLocalVariablesMethod(final Document document, final String className) {
		document.linef("    @Override");
		document.linef("    public %s updateLocalVariables(final int localSize) {", className);
		document.linef("        this.currentPixelColors = new float[localSize * 3];");
		document.linef("        this.intersections = new float[localSize * SIZE_INTERSECTION];");
		document.linef("        this.rays = new float[localSize * SIZE_RAY];");
		document.linef("        this.temporaryColors = new float[localSize * 3];");
		document.linef("        ");
		document.linef("        return this;");
		document.linef("    }");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final class Document {
		private final StringBuilder stringBuilder = new StringBuilder();
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		public Document() {
			
		}
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		@Override
		public String toString() {
			return this.stringBuilder.toString();
		}
		
		public void linef(final String format, final Object... objects) {
			this.stringBuilder.append(String.format(format + (format.endsWith("%n") ? "" : "%n"), objects));
		}
	}
}