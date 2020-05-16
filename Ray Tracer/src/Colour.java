/**
* Colour
*
* @author Sam Collier
*/
public class Colour 
{
	private double r,g,b;
	public Colour(double newR, double newG, double newB)
	{
		r=newR;
		g=newG;
		b=newB;
	}
	public double getR()
	{
		return r;
	}
	public double getG()
	{
		return g;
	}
	public double getB()
	{
		return b;
	}
	public Colour add(Colour c)
	{
		return new Colour(r+c.getR(),g+c.getG(),b+c.getB());
	}
	public Colour scale(double scalar)
	{
		return new Colour(r*scalar,g*scalar,b*scalar);
	}
	public int toARGB()
	{
        int ir = (int)(Math.min(Math.max(r,0),1) * 255 + 0.1);
        int ig = (int)(Math.min(Math.max(g,0),1) * 255 + 0.1);
        int ib = (int)(Math.min(Math.max(b,0),1) * 255 + 0.1);
        return (ir << 16) | (ig << 8) | (ib << 0);
	}
}
