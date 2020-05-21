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
	public Colour shade(Colour c)
	{
		return new Colour(c.getR()*r,c.getG()*g,c.getB()*b);
	}
	public Colour tint(Colour c)
	{
		return new Colour(r+(1-r)*c.getR(),g+(1-g)*c.getG(),b+(1-b)*c.getB());
	}
	public boolean isBlack()
	{
		if(r==0&&g==0&&b==0)return true;
		return false;
	}
	public int toARGB()
	{
        int ir = (int)(Math.min(Math.max(r,0),1) * 255 + 0.1);
        int ig = (int)(Math.min(Math.max(g,0),1) * 255 + 0.1);
        int ib = (int)(Math.min(Math.max(b,0),1) * 255 + 0.1);
        return (ir << 16) | (ig << 8) | (ib << 0);
	}
}
