
public class Camera
{
	private Point location;
	private Vector forward,up,right;
	private double xFoV,yFoV;
	
	public Camera(Point position, Vector forwardVector, 
			Vector upVector, double fieldOfView,
			double aspectRatio)
	{
		location=position;
		forward=forwardVector.normalize();
		up=upVector.normalize();
		right=forward.cross(up);
		xFoV=Math.toRadians(fieldOfView);
		yFoV=Math.atan(Math.tan(xFoV)/aspectRatio);
	}
	public Point imagePlanePoint(double u, double v)
	{
		return location.add(forward).add(right.scale(2*(u-.5)*Math.tan(xFoV))).add(
				up.scale(2*(v-.5)*Math.tan(yFoV)));
	}
	public Ray generateRay(double u, double v)
	{
		return new Ray(location, imagePlanePoint(u,v).subtract(location));
	}
}
