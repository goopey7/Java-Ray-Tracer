/**
* Scene
*
* @author Sam Collier
*/
import java.util.ArrayList;

public class Scene
{
	private Camera camera;
	private ArrayList<Surface> surfaces;
	public Scene(Camera newCam)
	{
		camera=newCam;
		surfaces=new ArrayList<Surface>();
	}
	public void setCamera(Camera newCam)
	{
		camera=newCam;
	}
	public void addSurface(Surface s)
	{
		surfaces.add(s);
	}
	public ColourImage render(int xRes, int yRes)
	{
		ColourImage image=new ColourImage(xRes,yRes);
		for(int i=0;i<image.getWidth();i++)
		{
			for(int j=0;j<image.getHeight();j++)
			{
				double u=(i+.5)/xRes;
				double v=(j+.5)/yRes;
				Ray ray=camera.generateRay(u, v);
				for(Surface s : surfaces)
				{
					if(s.intersect(ray)!=null)
					{
						image.setColour(i, j, new Colour(0,157,255));
					}
				}
			}
		}
		return image;
	}
}
