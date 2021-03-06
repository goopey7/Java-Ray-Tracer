import java.io.IOException;

/**
 * Details the static methods to creating various scenes for use in the raytracer.
 * scene1() is included as an example. You can add more static methods (for example scene2(),
 * scene3(), etc.) to create different scenes without affecting scene1.
 *
 * @author Ben Farrar & Sam Collier, but mostly Ben Farrar.
 * @version 2019.05.22
 */
public class SceneCreator {
    public static Scene scene1(double xResolution, double yResolution) throws IOException{
        Camera cam = new Camera(new Point(0,0,0),       // camera location
                                new Vector(0,0,-1),     // forward vector/view direction
                                new Vector(0,1,0),      // up vector
                                20,                     // field of view
                                xResolution/yResolution ); // aspect ratio
        Scene s = new Scene(cam);
        
        //Each sphere takes a Point (its centre), the radius, and a material.
        //For now, since we have not implemented the Material classes, we simply say they are null.
        Surface s1 = new Sphere(new Point(0,0,-20),3, null);
        //s.addSurface(s1);
        Surface s2 = new Sphere(new Point(0,4,-15),1, null);
        //s.addSurface(s2);
        Surface s3 = new Sphere(new Point(5,0,-20),1.5, null);
        //s.addSurface(s3);
        
        //Each triangle takes 3 Points (its vertexes), and a material.
        Surface t1 = new CustomMesh("data/Mark 42.obj",new Point(0,0,-10),null);
        s.addSurface(t1);
        Surface floor = new Triangle(new Point(0,-5,0), new Point(3000,-5,-1000), new Point(-3000,-5,-1000), null);
        s.addSurface(floor);
        
        return s;
    }
}
