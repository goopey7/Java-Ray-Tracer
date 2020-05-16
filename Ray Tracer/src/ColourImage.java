/**
* 2D representation of colours
*
* @author Sam Collier
*/
public class ColourImage 
{
	private int width,height;
	private Colour[][] colours;
	public ColourImage(int newWidth, int newHeight)
	{
		width=newWidth;
		height=newHeight;
		colours = new Colour[width][height];
		for(int i=0; i<colours.length;i++)
		{
			for(int j=0;j<colours[i].length;j++)
			{
				if(((int)(Math.random()*2))==1)
				colours[i][j] = new Colour(255,0,255);
				else colours[i][j] = new Colour(0,0,0);
			}
		}
	}
	public int getWidth()
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}
	public Colour getColour(int col, int row)
	{
		return colours[col][row];
	}
	public void setColour(int col, int row, Colour c)
	{
		colours[col][row]=c;
	}
}
