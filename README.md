Dayflower - Path Tracer v.0.0.36
================================
Dayflower - Path Tracer is a photo-realistic realtime renderer written in Java.

The engine primarily uses a rendering technique called Path Tracing, which is in the family of Ray Tracing algorithms. As secondary rendering techniques, Ambient Occlusion, Ray Casting, Ray Marching and Ray Tracing may be used.

This early test implementation of Dayflower uses a library called Aparapi. Aparapi is responsible for decompiling the Java bytecode into OpenCL C99 on the fly. The OpenCL C99 is then compiled into binary code and an attempt to execute it by the current GPU is made. If the GPU fails to execute the binary code, it should still run, but in a Java Thread pool on the CPU.

### Images
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-House.png "Dayflower Path Tracer")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Car.png "Dayflower Path Tracer")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Water-6.png "Dayflower Path Tracer")

### Algorithm Comparison
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Comparison-Ambient-Occlusion.png "Ambient Occlusion")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Comparison-Ray-Caster.png "Ray Caster")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Comparison-Ray-Tracer.png "Ray Tracer")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Comparison-Path-Tracer.png "Path Tracer")

______________________________________________________________________________________

Supported Features
------------------
| Category               | Feature                                                    |
| ---------------------- | ---------------------------------------------------------- |
| Acceleration Structure | Bounding Volume Hierarchy (BVH)                            |
| Camera                 | Depth of Field                                             |
| Camera                 | Field of View                                              |
| Camera                 | Free Movement                                              |
| Camera                 | Fisheye camera lens                                        |
| Camera                 | Thin camera lens                                           |
| Effect                 | Blur                                                       |
| Effect                 | Detect Edges                                               |
| Effect                 | Emboss                                                     |
| Effect                 | Gradient - Horizontal                                      |
| Effect                 | Gradient - Vertical                                        |
| Effect                 | Grayscale - Luminosity                                     |
| Effect                 | Sepia Tone                                                 |
| Effect                 | Sharpen                                                    |
| Material               | Diffuse using Lambertian reflectance                       |
| Material               | Clear Coat with Fresnel                                    |
| Material               | Glass with Fresnel                                         |
| Material               | Metal using Phong                                          |
| Material               | Specular (Mirror)                                          |
| Normal Mapping         | Image Texture                                              |
| Normal Mapping         | Noise                                                      |
| Rendering Algorithm    | Ambient Occlusion                                          |
| Rendering Algorithm    | Path Tracing                                               |
| Rendering Algorithm    | Ray Casting                                                |
| Rendering Algorithm    | Ray Marching                                               |
| Rendering Algorithm    | Ray Tracing                                                |
| Shape                  | Plane                                                      |
| Shape                  | Sphere                                                     |
| Shape                  | Triangle                                                   |
| Shape                  | Triangle Mesh                                              |
| Sun and Sky Model      | Perez                                                      |
| Texture                | Checkerboard                                               |
| Texture                | Constant                                                   |
| Texture                | Fractional Brownian Motion                                 |
| Texture                | Image                                                      |
| Texture                | Surface Normal                                             |
| Tone Mapper            | Filmic Curve v.1                                           |
| Tone Mapper            | Filmic Curve v.2                                           |
| Tone Mapper            | Linear                                                     |
| Tone Mapper            | Reinhard v.1                                               |
| Tone Mapper            | Reinhard v.2                                               |

______________________________________________________________________________________

Supported Keyboard Controls
---------------------------
| Key         | Description                                                           |
| ----------- | --------------------------------------------------------------------- |
| ESCAPE      | Exit or leave the scene if it has been entered like an FPS-game       |
| ENTER       | Enter the scene like an FPS-game                                      |
| W           | Walk forward                                                          |
| A           | Strafe left                                                           |
| S           | Walk backward                                                         |
| D           | Strafe right                                                          |
| Q           | Increase altitude                                                     |
| E           | Decrease altitude                                                     |
| R           | Toggle selection of shapes                                            |
| M           | Toggle the material for the surface instance of the selected shape    |

______________________________________________________________________________________

Supported Mouse Controls
------------------------
| Type        | Description                                                           |
| ----------- | --------------------------------------------------------------------- |
| Drag mouse  | Drag mouse to look around                                             |
| Move mouse  | Move mouse to look around when it has been entered like an FPS-game   |

______________________________________________________________________________________

Supported Menu Controls
-----------------------
| Menu Item                      | Description                                        |
| ------------------------------ | -------------------------------------------------- |
| File > Exit                    | Exit                                               |
| Camera > Walk Lock             | Toggle walk lock                                   |
| Camera > Fisheye Camera Lens   | Use a Fisheye camera lens                          |
| Camera > Thin Camera Lens      | Use a Thin camera lens                             |
| Effect > Blur                  | Toggle the Blur effect                             |
| Effect > Detect Edges          | Toggle the Detect Edges effect                     |
| Effect > Emboss                | Toggle the Emboss effect                           |
| Effect > Gradient (Horizontal) | Toggle the horizontal Gradient effect              |
| Effect > Gradient (Vertical)   | Toggle the vertical Gradient effect                |
| Effect > Sharpen               | Toggle the Sharpen effect                          |
| Effect > Grayscale             | Toggle the Grayscale effect                        |
| Effect > Sepia Tone            | Toggle the Sepia Tone effect                       |
| Renderer > Ambient Occlusion   | Use the Ambient Occlusion renderer to render       |
| Renderer > Path Tracer         | Use the Path Tracer to render                      |
| Renderer > Ray Caster          | Use the Ray Caster to render                       |
| Renderer > Ray Marcher         | Use the Ray Marcher to render                      |
| Renderer > Ray Tracer          | Use the Ray Tracer to render                       |
| Scene > Normal Mapping         | Toggle Normal Mapping                              |
| Scene > Enter Scene            | Enter the scene like an FPS-game                   |
| Scene > Flat Shading           | Use Flat Shading for Triangles                     |
| Scene > Gouraud Shading        | Use Gouraud Shading for Triangles                  |
| Tone Mapper > Filmic Curve v.1 | Use the Filmic Curve v.1 Tone Mapper               |
| Tone Mapper > Filmic Curve v.2 | Use the Filmic Curve v.2 Tone Mapper               |
| Tone Mapper > Linear           | Use the Linear Tone Mapper                         |
| Tone Mapper > Reinhard v.1     | Use the Reindard v.1 Tone Mapper                   |
| Tone Mapper > Reinhard v.2     | Use the Reinhard v.2 Tone Mapper                   |

______________________________________________________________________________________

Supported Tab Controls
----------------------
| Control                         | Description                                       |
| ------------------------------- | ------------------------------------------------- |
| Camera > Field of View          | Change the Field of View                          |
| Camera > Aperture Radius        | Change the Aperture Radius                        |
| Camera > Focal Distance         | Change the Focal Distance                         |
| Camera > Pitch                  | Change the Pitch angle                            |
| Camera > Yaw                    | Change the Yaw angle                              |
| Sun & Sky > Sun Direction X     | Change the X-axis of the sun direction            |
| Sun & Sky > Sun Direction Y     | Change the Y-axis of the sun direction            |
| Sun & Sky > Sun Direction Z     | Change the Z-axis of the sun direction            |
| Sun & Sky > Turbidity           | Change the turbidity of the sky                   |
| Sun & Sky > Toggle Sun & Sky    | Toggle the Sun and Sky                            |
| Sun & Sky > Toggle Clouds       | Toggle the clouds in the Sun and Sky model        |
| Renderer > Maximum Distance     | Change the maximum distance for Ambient Occlusion |
| Renderer > Maximum Ray Depth    | Change the maximum ray depth                      |
| Renderer > Amplitude            | Change the Amplitude of the terrain generation    |
| Renderer > Frequency            | Change the Frequency of the terrain generation    |
| Renderer > Gain                 | Change the Gain of the terrain generation         |
| Renderer > Lacunarity           | Change the Lacunarity of the terrain generation   |

______________________________________________________________________________________

Getting Started
---------------
To clone this repository, build the project and run it, you can type the following in Git Bash. You need Apache Ant though.
```bash
git clone https://github.com/macroing/Dayflower-Path-Tracer.git
cd Dayflower-Path-Tracer
ant
cd distribution/org.dayflower.pathtracer
java -Djava.library.path=lib -jar org.dayflower.pathtracer.jar
```
This project currently only works properly on Windows machines. Aparapi needs to be compiled for other operating systems, in order to use GPU-rendering. CPU-rendering should work on other operating systems, however.

______________________________________________________________________________________

Settings
--------
In the ``settings.properties`` file you can change a few settings. The supported settings are the following.
* ``canvas.width`` - The width of the canvas being rendered to.
* ``canvas.height`` - The height of the canvas being rendered to.
* ``kernel.width`` - The width of the kernel being processed.
* ``kernel.height`` - The height of the kernel being processed.
* ``scene.compile`` - If ``true``, scene compilation will be performed whether or not the scene already exists.
* ``scene.name`` - The name of the scene to use.

The following is a list of scene names that can be used.
* ``Car_Scene``
* ``Cornell_Box_Scene``
* ``Cornell_Box_Scene_2``
* ``Girl_Scene``
* ``House_Scene``
* ``House_Scene_2``
* ``House_Scene_3``
* ``Material_Showcase_Scene`` (Default)
* ``Monkey_Scene``
* ``Terrain_Scene``

______________________________________________________________________________________

Dependencies
------------
 - [Java 8](http://www.java.com).
 - [Aparapi](https://github.com/macroing/aparapi).

______________________________________________________________________________________

Note
----
This program hasn't been released yet. So, even though it says it's version 1.0.0 in all Java source code files, it shouldn't be treated as such. When this program reaches version 1.0.0, it will be tagged and available on the "releases" page.