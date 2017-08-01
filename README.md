Dayflower - Path Tracer v.0.0.20
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

#### Tone Mapping and Gamma Correction
* Filmic Curve
* Linear
* Reinhard v.1
* Reinhard v.2

Supported Controls
------------------
* ESC: Exit
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
* H: Toggle between Path Tracing, Ray Casting and Ray Marching
* K: Toggle Walk-Lock
* L: Toggle Mouse Recentering and Cursor Visibility
* C: Toggle between Thin Camera Lens and Fisheye Camera Lens
* B: Toggle Normal Mapping
* M: Increase Maximum Ray Depth
* N: Decrease Maximum Ray Depth
* DOWN ARROW: Increase Pitch
* UP ARROW: Decrease Pitch
* LEFT ARROW: Increase Yaw
* RIGHT ARROW: Decrease Yaw
* MOVE MOUSE: Free Movement
* 1: Toggle Blur Effect
* 2: Toggle Edge Detection Effect
* 3: Toggle Emboss Effect
* 4: Toggle Horizontal Gradient Effect
* 5: Toggle Vertical Gradient Effect
* 6: Toggle Sharpen Effect
* 7: Toggle Grayscale Effect
* 8: Toggle Sepia Tone Effect
* 9: Toggle between Flat Shading and Gouraud Shading
* NUMPAD 0: Use Tone Mapping and Gamma Correction Filmic Curve
* NUMPAD 1: Use Tone Mapping and Gamma Correction Linear
* NUMPAD 2: Use Tone Mapping and Gamma Correction Reinhard v.1
* NUMPAD 3: Use Tone Mapping and Gamma Correction Reinhard v.2

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