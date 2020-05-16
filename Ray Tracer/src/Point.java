/**
* 3D Coordinate/Point
*
* @author Sam Collier
*/
public class Point 
{
	private double x,y,z;
	public Point(double newX, double newY, double newZ)
	{
		x=newX;
		y=newY;
		z=newZ;
	}
	public double getX()
	{
		return x;
	}
	public double getY()
	{
		return y;
	}
	public double getZ()
	{
		return z;
	}
	public Point add(Vector v)
	{
		return new Point(x+v.getDX(),y+v.getDY(),z+v.getDZ());
	}
	public Point add(Point p)
	{
		return new Point(x+p.getX(),y+p.getY(),z+p.getZ());
	}
	public Vector subtract(Point p)
	{
		return new Vector(x-p.getX(),y-p.getY(),z-p.getZ());
	}
}
