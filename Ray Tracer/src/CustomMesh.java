/**
* Loads in a custom 3D model!
*
* @author Sam Collier
*/
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import static jcuda.driver.JCudaDriver.*;

public class CustomMesh extends Surface
{
	public ArrayList<Triangle> triangles;
	public Point location;
    public Material mat;
    public int type;
    
    //Minimum distance for a valid collision. This prevents the sphere's rays from colliding with itself.
    public static double EPSILON = 1e-6;
    
    public CustomMesh(String objFileLocation,Point pos,Material m) throws IOException
    {
    	triangles=new ArrayList<Triangle>();
    	mat = m;
    	location=pos;
        type=0;
    	
    	// Read an OBJ file
        InputStream objInputStream = new FileInputStream(objFileLocation);
        Obj obj = ObjUtils.triangulate(ObjReader.read(objInputStream));
        
        // Fill the triangles ArrayList with triangles
        float[] vertices=new float[9]; // Triangles have 3 vertices, vertices have 3 components
        int ind=0;
        for(int i=0;i<obj.getNumFaces();i++)
        {
        	for(int j=0;j<obj.getFace(i).getNumVertices();j++)
        	{
        		float x=obj.getVertex(obj.getFace(i).getVertexIndex(j)).getX();
        		float y=obj.getVertex(obj.getFace(i).getVertexIndex(j)).getY();
        		float z=obj.getVertex(obj.getFace(i).getVertexIndex(j)).getZ();
        		vertices[ind]=x;
        		vertices[ind+1]=y;
        		vertices[ind+2]=z;
        		ind+=3;
        	}
        	ind=0;
        	triangles.add(new Triangle(
        			new Point(vertices[0],vertices[1],vertices[2]).add(location), //Change relative coordinates to world coordinates
        			new Point(vertices[3],vertices[4],vertices[5]).add(location), //By adding the "location"
        			new Point(vertices[6],vertices[7],vertices[8]).add(location),
        			null));
        }
        System.out.println("Loaded "+triangles.size()+" triangle 3D Model");
    }

    public int getType() {return type;}
    public Point getMeshLocation() {return location;}
	public Intersection intersect(Ray ray)
	{
		for(Triangle tri:triangles)
		{
	        double d = new Point(0,0,0).subtract(tri.v0).dot(tri.normal);
	        Point rayOrigin = ray.getPosition();
	        Vector rayOriginVec = new Vector(rayOrigin.getX(), rayOrigin.getY(), rayOrigin.getZ());
	        double distance = -(rayOriginVec.dot(tri.normal) + d)/(ray.getDirection().dot(tri.normal));
	        if (distance>EPSILON)
	        {
	            Point inter = ray.evaluate(distance);
	            double a = (tri.v1.subtract(tri.v0).cross(inter.subtract(tri.v0))).dot(tri.normal);
	            double b = (tri.v2.subtract(tri.v1).cross(inter.subtract(tri.v1))).dot(tri.normal);
	            double c = (tri.v0.subtract(tri.v2).cross(inter.subtract(tri.v2))).dot(tri.normal);
	            if (a>0 && b>0 && c>0)
	            {
	                if(tri.normal.dot(ray.getDirection()) > 0)
	                {
	                    return new Intersection(inter, tri.normal.scale(-1), distance, mat);
	                }
	                else 
	                {
	                    return new Intersection(inter, tri.normal, distance, mat);
	                }
	            }
	        }
		}
		return null;
	}
	
}
