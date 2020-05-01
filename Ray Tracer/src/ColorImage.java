
public class ColorImage 
{
	private int width,height;
	private Color[][] colors;
	public ColorImage(int newWidth, int newHeight)
	{
		width=newWidth;
		height=newHeight;
		colors = new Color[width][height];
		for(Color[] h : colors)
		{
			for(Color color : h)
			{
				if(((int)(Math.random()*2)+1)==2)
				color = new Color(255,0,255);
				else color = new Color(0,0,0);
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
	public Color getColor(int col, int row)
	{
		return colors[col][row];
	}
	public void setColor(int col, int row, Color c)
	{
		colors[col][row]=c;
	}
}
