Dayflower - Path Tracer v.0.0.21
================================
Dayflower - Path Tracer is a photo-realistic realtime renderer written in Java.

This project is mainly a test project for the real renderer, Dayflower.

![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Scene-1.png "Dayflower Path Tracer")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Scene-2.png "Dayflower Path Tracer")
![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Material-Showcase.png "Dayflower Path Tracer")

Supported Features
------------------
#### Acceleration Structure
* Bounding Volume Hierarchy (BVH)

#### Camera
* Depth of Field
* Field of View
* Free Movement
* Fisheye Camera Lens
* Thin Camera Lens

#### Effect
* Blur
* Detect Edges
* Emboss
* Gradient - Horizontal
* Gradient - Vertical
* Grayscale - Luminosity
* Sepia Tone
* Sharpen

#### Material
* Diffuse using Lambertian reflection
* Clear Coat
* Glass
* Metal using Phong
* Specular (Mirror)

#### Normal Mapping
* Image Texture
* Perlin Noise

#### Rendering Algorithm
* Path Tracing
* Ray Casting
* Ray Marching

#### Shape
* Plane
* Sphere
* Triangle
* Triangle Mesh

#### Sun and Sky Model
* Perez

#### Texture
* Checkerboard
* Constant (Solid)
* Image
* Surface Normal

#### Tone Mapping and Gamma Correction
* Filmic Curve
* Linear
* Reinhard v.1
* Reinhard v.2

Supported Keyboard Controls
---------------------------
* ESC: Exit or leave the Scene if it has been entered like an FPS-game
* ENTER: Enter the Scene like an FPS-game
* W: Walk Forward
* A: Strafe Left
* S: Walk Backward
* D: Strafe Right
* E: Increase Altitude
* R: Decrease Altitude
* T: Increase Aperture Diameter
* Y: Decrease Aperture Diameter
* U: Increase Focal Distance
* I: Decrease Focal Distance
* O: Increase Field of View for the X-axis
* P: Decrease Field of View for the X-axis
* J: Reset Amplitude, Frequency, Lacunarity and Gain for Ray Marching
* M: Increase Maximum Ray Depth
* N: Decrease Maximum Ray Depth
* DOWN ARROW: Increase Pitch
* UP ARROW: Decrease Pitch
* LEFT ARROW: Increase Yaw
* RIGHT ARROW: Decrease Yaw
* MOVE MOUSE: Free Movement
* NUMPAD 4: Increase Amplitude for Ray Marching
* NUMPAD 5: Increase Frequency for Ray Marching
* NUMPAD 6: Increase Gain for Ray Marching
* NUMPAD 7: Decrease Amplitude for Ray Marching
* NUMPAD 8: Decrease Frequency for Ray Marching
* NUMPAD 9: Decrease Gain for Ray Marching

Supported Menu Controls
-----------------------
* File > Exit: Exit
* Camera > Walk Lock: Toggle walk lock
* Camera > Fisheye Camera Lens: Use a Fisheye camera lens
* Camera > Thin Camera Lens: Use a Thin camera lens
* Effect > Blur: Toggle the Blur effect
* Effect > Detect Edges: Toggle the Detect Edges effect
* Effect > Emboss: Toggle the Emboss effect
* Effect > Gradient (Horizontal): Toggle the horizontal Gradient effect
* Effect > Gradient (Vertical): Toggle the vertical Gradient effect
* Effect > Sharpen: Toggle the Sharpen effect
* Effect > Grayscale: Toggle the Grayscale effect
* Effect > Sepia Tone: Toggle the Sepia Tone effect
* Renderer > Path Tracer: Use the Path Tracer to render
* Renderer > Ray Caster: Use the Ray Caster to render
* Renderer > Ray Marcher: Use the Ray Marcher to render
* Scene > Normal Mapping: Toggle Normal Mapping
* Scene > Random Sun and Sky: Generate a new random Sun and Sky
* Scene > Flat Shading: Use Flat Shading for Triangles
* Scene > Gouraud Shading: Use Gouraud Shading for Triangles
* Tone Mapper > Filmic Curve: Use the Filmic Curve Tone Mapper
* Tone Mapper > Linear: Use the Linear Tone Mapper
* Tone Mapper > Reinhard v.1: Use the Reindard v.1 Tone Mapper
* Tone Mapper > Reinhard v.2: Use the Reinhard v.2 Tone Mapper

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

Settings
--------
In the ``settings.properties`` file you can change a few settings. The supported settings are the following.
* ``width`` - The width of the canvas being rendered to.
* ``width.scale`` - The width scale to use for the kernel. The width for the kernel is ``width / width.scale``.
* ``height`` - The height of the canvas being rendered to.
* ``height.scale`` - The height scale to use for the kernel. The height for the kernel is ``height / height.scale``.
* ``scene.compile`` - If ``true``, scene compilation will be performed whether or not the scene already exists.
* ``scene.name`` - The name of the scene to use.

The following is a list of scene names that can be used.
* Car_Scene
* Cornell_Box_Scene
* Cornell_Box_Scene_2
* Girl_Scene
* House_Scene
* Material_Showcase_Scene (Default)
* Monkey_Scene
* Terrain_Scene

Dependencies
------------
 - [Java 8](http://www.java.com).
 - [Aparapi](https://github.com/macroing/aparapi).

Note
----
This library hasn't been released yet. So, even though it says it's version 1.0.0 in all Java source code files, it shouldn't be treated as such. When this library reaches version 1.0.0, it will be tagged and available on the "releases" page.