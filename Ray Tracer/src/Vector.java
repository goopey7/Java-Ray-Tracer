
public class Vector 
{
	private double dx,dy,dz;
	public Vector(double newDX, double newDY, double newDZ)
	{
		dx=newDX;
		dy=newDY;
		dz=newDZ;
	}
	public double getDX()
	{
		return dx;
	}
	public double getDY()
	{
		return dy;
	}
	public double getDZ()
	{
		return dz;
	}
	public Vector scale(double scalar)
	{
		return new Vector(dx*scalar,dy*scalar,dz*scalar);
	}
	public Vector subtract(Vector v)
	{
		return new Vector(dx-v.getDX(),dy-v.getDY(),dz-v.getDZ());
	}
	public double dot(Vector v)
	{
		return dx*v.getDX()+dy*v.getDY()+dz*v.getDZ();
	}
	public Vector cross(Vector v)
	{
		return new Vector(dy*v.getDZ()-dz*v.getDY(),dz*v.getDX()-dx*v.getDZ(),dx*v.getDY()-dy*v.getDX());
	}
	public double length()
	{
		return Math.sqrt(dot(this));
	}
	public Vector normalize()
	{
		return scale(1/length());
	}
}
