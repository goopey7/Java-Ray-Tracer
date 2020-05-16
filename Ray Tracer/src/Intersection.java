/**
* Intersection
*
* @author Sam Collier
*/
public class Intersection 
{
	private Point position;
	private Vector normal;
	private double distance;
	private Material material;
	
	public Intersection(Point pos, Vector norm, double dist, 
			Material mat)
	{
		position=pos;
		normal=norm;
		distance=dist;
		material=mat;
	}
	public Point getPosition()
	{
		return position;
	}
	public Vector getNormal()
	{
		return normal;
	}
	public double getDistance()
	{
		return distance;
	}
	public Material getMaterial()
	{
		return material;
	}
}
