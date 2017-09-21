Dayflower - Path Tracer v.0.0.24
================================
Dayflower - Path Tracer is a photo-realistic realtime renderer written in Java.

The engine primarily uses a rendering technique called Path Tracing, which is in the family of Ray Tracing algorithms. As secondary rendering techniques, Ray Casting and Ray Marching may be used.

This early test implementation of Dayflower, uses a library called Aparapi. This library is responsible for decompiling the Java bytecode into OpenCL C99 on the fly. This OpenCL C99 is then compiled into binary code that is executable by your specific GPU.

![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Engine.png "Dayflower Path Tracer")

_________________________________________________________________________________

Supported Features
-----------------------------------------------------------------------
| Category               | Feature                                              |
| ---------------------- | ---------------------------------------------------- |
| Acceleration Structure | Bounding Volume Hierarchy (BVH)                      |
| Camera                 | Depth of Field                                       |
| Camera                 | Field of View                                        |
| Camera                 | Free Movement                                        |
| Camera                 | Fisheye camera lens                                  |
| Camera                 | Thin camera lens                                     |
| Effect                 | Blur                                                 |
| Effect                 | Detect Edges                                         |
| Effect                 | Emboss                                               |
| Effect                 | Gradient - Horizontal                                |
| Effect                 | Gradient - Vertical                                  |
| Effect                 | Grayscale - Luminosity                               |
| Effect                 | Sepia Tone                                           |
| Effect                 | Sharpen                                              |
| Material               | Diffuse using Lambertian reflectance                 |
| Material               | Clear Coat with Fresnel                              |
| Material               | Glass with Fresnel                                   |
| Material               | Metal using Phong                                    |
| Material               | Specular (Mirror)                                    |
| Normal Mapping         | Image Texture                                        |
| Normal Mapping         | Perlin Noise                                         |
| Rendering Algorithm    | Path Tracing                                         |
| Rendering Algorithm    | Ray Casting                                          |
| Rendering Algorithm    | Ray Marching                                         |
| Shape                  | Plane                                                |
| Shape                  | Sphere                                               |
| Shape                  | Triangle                                             |
| Shape                  | Triangle Mesh                                        |
| Sund and Sky Model     | Perez                                                |
| Texture                | Checkerboard                                         |
| Texture                | Constant (Solid)                                     |
| Texture                | Image                                                |
| Texture                | Surface Normal                                       |
| Tone Mapper            | Filmic Curve                                         |
| Tone Mapper            | Linear                                               |
| Tone Mapper            | Reinhard v.1                                         |
| Tone Mapper            | Reinhard v.2                                         |

_________________________________________________________________________________

Supported Keyboard Controls
-----------------------------------------------------------------------
| Key         | Description                                                     |
| ----------- | --------------------------------------------------------------- |
| ESCAPE      | Exit or leave the scene if it has been entered like an FPS-game |
| ENTER       | Enter the scene like an FPS-game                                |
| W           | Walk forward                                                    |
| A           | Strafe left                                                     |
| S           | Walk backward                                                   |
| D           | Strafe right                                                    |
| E           | Increase altitude                                               |
| R           | Decrease altitude                                               |
| DOWN ARROW  | Increase pitch                                                  |
| UP ARROW    | Decrease pitch                                                  |
| LEFT ARROW  | Increase yaw                                                    |
| RIGHT ARROW | Decrease yaw                                                    |

_________________________________________________________________________________

Supported Menu Controls
-----------------------------------------------------------------------
| Menu Item                      | Description                                  |
| ------------------------------ | -------------------------------------------- |
| File > Exit                    | Exit                                         |
| Camera > Walk Lock             | Toggle walk lock                             |
| Camera > Fisheye Camera Lens   | Use a Fisheye camera lens                    |
| Camera > Thin Camera Lens      | Use a Thin camera lens                       |
| Effect > Blur                  | Toggle the Blur effect                       |
| Effect > Detect Edges          | Toggle the Detect Edges effect               |
| Effect > Emboss                | Toggle the Emboss effect                     |
| Effect > Gradient (Horizontal) | Toggle the horizontal Gradient effect        |
| Effect > Gradient (Vertical)   | Toggle the vertical Gradient effect          |
| Effect > Sharpen               | Toggle the Sharpen effect                    |
| Effect > Grayscale             | Toggle the Grayscale effect                  |
| Effect > Sepia Tone            | Toggle the Sepia Tone effect                 |
| Renderer > Path Tracer         | Use the Path Tracer to render                |
| Renderer > Ray Caster          | Use the Ray Caster to render                 |
| Renderer > Ray Marcher         | Use the Ray Marcher to render                |
| Scene > Normal Mapping         | Toggle Normal Mapping                        |
| Scene > Flat Shading           | Use Flat Shading for Triangles               |
| Scene > Gouraud Shading        | Use Gouraud Shading for Triangles            |
| Tone Mapper > Filmic Curve     | Use the Filmic Curve Tone Mapper             |
| Tone Mapper > Linear           | Use the Linear Tone Mapper                   |
| Tone Mapper > Reinhard v.1     | Use the Reindard v.1 Tone Mapper             |
| Tone Mapper > Reinhard v.2     | Use the Reinhard v.2 Tone Mapper             |

_________________________________________________________________________________

Getting Started
-----------------------------------------------------------------------
To clone this repository, build the project and run it, you can type the following in Git Bash. You need Apache Ant though.
```bash
git clone https://github.com/macroing/Dayflower-Path-Tracer.git
cd Dayflower-Path-Tracer
ant
cd distribution/org.dayflower.pathtracer
java -Djava.library.path=lib -jar org.dayflower.pathtracer.jar
```

_________________________________________________________________________________

Settings
-----------------------------------------------------------------------
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
* ``Material_Showcase_Scene`` (Default)
* ``Monkey_Scene``
* ``Terrain_Scene``

_________________________________________________________________________________

Dependencies
-----------------------------------------------------------------------
 - [Java 8](http://www.java.com).
 - [Aparapi](https://github.com/macroing/aparapi).

_________________________________________________________________________________

Note
-----------------------------------------------------------------------
This program hasn't been released yet. So, even though it says it's version 1.0.0 in all Java source code files, it shouldn't be treated as such. When this program reaches version 1.0.0, it will be tagged and available on the "releases" page.