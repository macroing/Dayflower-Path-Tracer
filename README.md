Dayflower - Path Tracer v.0.0.20
================================
Dayflower - Path Tracer is a photo-realistic realtime renderer written in Java.

This project is mainly a test project for the real renderer, Dayflower.

![alt text](https://github.com/macroing/Dayflower-Path-Tracer/blob/master/images/Dayflower-Scene-1.png "Dayflower Path Tracer")

Supported Features
------------------
* Acceleration Structure: Bounding Volume Hierarchy (BVH)
* Camera: Depth of Field
* Camera: Field of View
* Camera: Free Movement
* Camera: Fisheye Camera Lens
* Camera: Thin Camera Lens
* Effect: Blur
* Effect: Detect Edges
* Effect: Emboss
* Effect: Gradient - Horizontal
* Effect: Gradient - Vertical
* Effect: Grayscale - Luminosity
* Effect: Sepia Tone
* Effect: Sharpen
* Material: Diffuse using Lambertian reflection
* Material: Clear Coat
* Material: Glass
* Material: Metal using Phong
* Material: Specular (Mirror)
* Normal Mapping: Image Texture
* Normal Mapping: Perlin Noise
* Rendering Algorithm: Path Tracing
* Rendering Algorithm: Ray Casting
* Rendering Algorithm: Ray Marching
* Shape: Plane
* Shape: Sphere
* Shape: Triangle
* Shape: Triangle Mesh
* Sun and Sky Model: Perez
* Texture: Checkerboard
* Texture: Constant (Solid)
* Texture: Image
* Tone Mapping and Gamma Correction: Filmic Curve
* Tone Mapping and Gamma Correction: Linear
* Tone Mapping and Gamma Correction: Reinhard v.1
* Tone Mapping and Gamma Correction: Reinhard v.2

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
* F: Toggle Wireframe
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

Dependencies
------------
 - [Java 8](http://www.java.com).
 - [Aparapi](https://github.com/aparapi/aparapi).