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
	public ColorImage render(int xRes, int yRes)
	{
		ColorImage image=new ColorImage(xRes,yRes);
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
						image.setColor(i, j, new Color(0,157,255));
					}
				}
			}
		}
		return image;
	}
}
