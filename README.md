Dayflower - Path Tracer v.0.2.1
===============================
Dayflower - Path Tracer is a photorealistic realtime renderer written in Java.

The engine primarily uses a rendering technique called Path Tracing, which is in the family of Ray Tracing algorithms. As secondary rendering techniques, Ambient Occlusion, Ray Casting, Ray Marching, Ray Tracing and Wireframe rendering may be used.

This early test implementation of Dayflower uses a library called Aparapi. Aparapi is responsible for decompiling the Java bytecode into OpenCL C99 on the fly. The OpenCL C99 is then compiled into binary code and an attempt to execute it by the current GPU is made. If the GPU fails to execute the binary code, it should still run, but in a Java Thread pool on the CPU.

A new version of Dayflower is available [here](https://github.com/macroing/Dayflower). That version contains more features and many improvements. It supports both CPU- and GPU-based rendering as well as more light sources, materials, shapes and textures. The new version will be the focus of development moving forward. This version will probably not get any more updates.

![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/random/Dayflower-Zealot.png "Dayflower Path Tracer")

For more images, check out [images](https://github.com/macroing/Dayflower-Path-Tracer/tree/master/images).

______________________________________________________________________________________

Supported Features
------------------
| Category               | Feature                                            |
| ---------------------- | -------------------------------------------------- |
| Acceleration Structure | Bounding Volume Hierarchy (BVH)                    |
| Camera                 | Depth of Field                                     |
| Camera                 | Field of View                                      |
| Camera                 | Free Movement                                      |
| Camera                 | Fisheye Camera Lens                                |
| Camera                 | Thin Camera Lens                                   |
| Light                  | Perez Sun and Sky Model                            |
| Light                  | Primitive-based Area Light via an Emission Texture |
| Material               | Clear Coat                                         |
| Material               | Glass                                              |
| Material               | Lambertian                                         |
| Material               | Phong                                              |
| Material               | Reflection                                         |
| Normal Mapping         | Image Texture                                      |
| Normal Mapping         | Noise                                              |
| Rendering Algorithm    | Ambient Occlusion                                  |
| Rendering Algorithm    | Path Tracing                                       |
| Rendering Algorithm    | Ray Casting                                        |
| Rendering Algorithm    | Ray Marching                                       |
| Rendering Algorithm    | Ray Tracing                                        |
| Rendering Algorithm    | Wireframe                                          |
| Shape                  | Plane                                              |
| Shape                  | Sphere                                             |
| Shape                  | Terrain                                            |
| Shape                  | Triangle                                           |
| Shape                  | Triangle Mesh                                      |
| Texture                | Blend                                              |
| Texture                | Bullseye                                           |
| Texture                | Checkerboard                                       |
| Texture                | Constant                                           |
| Texture                | Fractional Brownian Motion                         |
| Texture                | Image                                              |
| Texture                | Surface Normal                                     |
| Texture                | UV                                                 |
| Tone Mapper            | Filmic Curve ACES Modified                         |
| Tone Mapper            | Reinhard                                           |
| Tone Mapper            | Reinhard Modified v.1                              |
| Tone Mapper            | Reinhard Modified v.2                              |

______________________________________________________________________________

Supported Keyboard Controls
---------------------------
| Key         | Description                                                        |
| ----------- | ------------------------------------------------------------------ |
| ESCAPE      | Exit or leave the scene if it has been entered like an FPS-game    |
| ENTER       | Enter the scene like an FPS-game                                   |
| W           | Walk forward or move selected Primitive -Z                         |
| A           | Strafe left or move selected Primitive +X                          |
| S           | Walk backward or move selected Primitive +Z                        |
| D           | Strafe right or move selected Primitive -X                         |
| Q           | Increase altitude or move selected Primitive +Y                    |
| E           | Decrease altitude or move selected Primitive -Y                    |
| R           | Toggle selection of shapes                                         |
| M           | Toggle the material for the surface instance of the selected shape |
| X           | Rotate the selected primitive around the X-axis                    |
| Y           | Rotate the selected primitive around the Y-axis                    |
| Z           | Rotate the selected primitive around the Z-axis                    |

___________________________________________________________________________________

Supported Mouse Controls
------------------------
| Type        | Description                                                         |
| ----------- | ------------------------------------------------------------------- |
| Drag mouse  | Drag mouse to look around                                           |
| Move mouse  | Move mouse to look around when it has been entered like an FPS-game |

____________________________________________________________________________________

Supported Menu Controls
-----------------------
| Menu Item                                | Description                                    |
| ---------------------------------------- | ---------------------------------------------- |
| File > Save                              | Saves the current image                        |
| File > Exit                              | Exit                                           |
| Camera > Walk Lock                       | Toggle walk lock                               |
| Camera > Fisheye Camera Lens             | Use a Fisheye camera lens                      |
| Camera > Thin Camera Lens                | Use a Thin camera lens                         |
| Renderer > Ambient Occlusion             | Use the Ambient Occlusion renderer to render   |
| Renderer > Path Tracer                   | Use the Path Tracer to render                  |
| Renderer > Ray Caster                    | Use the Ray Caster to render                   |
| Renderer > Ray Marcher                   | Use the Ray Marcher to render                  |
| Renderer > Ray Tracer                    | Use the Ray Tracer to render                   |
| Renderer > Surface Normal                | Use the Surface Normal renderer                |
| Scene > Normal Mapping                   | Toggle Normal Mapping                          |
| Scene > Wireframes                       | Toggle Wireframes                              |
| Scene > Enter Scene                      | Enter the scene like an FPS-game               |
| Scene > Flat Shading                     | Use Flat Shading for Triangles                 |
| Scene > Gouraud Shading                  | Use Gouraud Shading for Triangles              |
| Tone Mapper > Filmic Curve ACES Modified | Use the tone mapper Filmic Curve ACES Modified |
| Tone Mapper > Reinhard                   | Use the tone mapper Reinhard                   |
| Tone Mapper > Reinhard Modified v.1      | Use the tone mapper Reinhard Modified v.1      |
| Tone Mapper > Reinhard Modified v.2      | Use the tone mapper Reinhard Modified v.2      |

____________________________________________________________________________________________

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
| Renderer > Maximum Distance     | Change the maximum distance for Ambient Occlusion |
| Renderer > Maximum Ray Depth    | Change the maximum ray depth                      |
| Renderer > Amplitude            | Change the Amplitude of the terrain generation    |
| Renderer > Frequency            | Change the Frequency of the terrain generation    |
| Renderer > Gain                 | Change the Gain of the terrain generation         |
| Renderer > Lacunarity           | Change the Lacunarity of the terrain generation   |
| Tone Mapper > Exposure          | Change the exposure for all Tone Mappers          |

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
* ``House_Scene``
* ``Material_Showcase_Scene`` (Default)
* ``Monkey_Scene``
* ``Terrain_Scene``
* ``Zealot_Scene``

______________________________________________________________________________________

Dependencies
------------
 - [Java 8](http://www.java.com)
 - [Aparapi](https://github.com/macroing/aparapi)
 - [Image4J](https://github.com/macroing/Image4J)
 - [Math4J](https://github.com/macroing/Math4J)

______________________________________________________________________________________

Note
----
This program hasn't been released yet. So, even though it says it's version 1.0.0 in all Java source code files, it shouldn't be treated as such. When this program reaches version 1.0.0, it will be tagged and available on the "releases" page.