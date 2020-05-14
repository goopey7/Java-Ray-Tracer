
public class ColorImage 
{
	private int width,height;
	private Color[][] colors;
	public ColorImage(int newWidth, int newHeight)
	{
		width=newWidth;
		height=newHeight;
		colors = new Color[width][height];
		for(int i=0; i<colors.length;i++)
		{
			for(int j=0;j<colors[i].length;j++)
			{
				if(((int)(Math.random()*2))==1)
				colors[i][j] = new Color(255,0,255);
				else colors[i][j] = new Color(0,0,0);
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
