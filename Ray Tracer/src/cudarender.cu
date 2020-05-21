/**
* CUDA RENDER
*
* @author Sam Collier
*/
#include "math.h"

__device__
double dot3d(double x0,double y0,double z0,double x1,double y1,double z1)
{
	return x0*x1+y0*y1+z0*z1;
}
__device__
double cross3dX(double y0,double z0,double y1,double z1)
{
	return y0*z1-z0*y1;
}
__device__
double cross3dY(double x0,double z0,double x1,double z1)
{
	return z0*x1-x0*z1;
}
__device__
double cross3dZ(double x0,double y0,double x1,double y1)
{
	return x0*y1-y0*x1;
}
__device__
double imagePlanPoint(double camPos,double camForward,double camUp,
	double camRight,double u,double v,double xFoV,double yFoV)
{
	return camPos+camForward+camRight*2*(u-.5)*tan(xFoV)+
					camUp*2*(v-.5)*tan(yFoV);
}
extern "C"
__global__
void render(int n,int xRes,int yRes,
	double camX,double camY,double camZ,
	double camforX,double camforY,double camforZ,
	double camupX,double camupY,double camupZ,
	double fov,double aspectRatio,
	double* v0X,double* v0Y,double* v0Z,
	double* v1X,double* v1Y,double* v1Z,
	double* v2X,double* v2Y,double* v2Z,
	double meshposX,double meshposY,double meshposZ,
	double* outR,double* outG,double* outB)
{
	const int EPSILON=1e-6;
	int col=blockIdx.x*blockDim.x+threadIdx.x;
	int colStride = blockDim.x*gridDim.x;
	int row=blockIdx.y*blockDim.y+threadIdx.y;
	int rowStride=blockDim.y*gridDim.y;
	if(col<xRes&&row<yRes)
	{
		bool bIntersected=false;

		// Calculating right vector using cross product
		double rightX=cross3dX(camforY,camforZ,camupY,camupZ);
		double rightY=cross3dY(camforX,camforZ,camupX,camupZ);
		double rightZ=cross3dZ(camforX,camforY,camupX,camupY);
			
		double xFoV=fov*(acos(-1.0)/180); //convert from degree to radian
		double yFoV=atan(tan(xFoV)/aspectRatio); //getting yFoV from xFoV
		
		for(int i=col;i<xRes;i+=colStride)
		{
			for(int j=row;j<yRes;j+=rowStride)
			{
				double u=(i+.5)/xRes;
				double v=(j+.5)/yRes;
				

				double imagePlainPointX=imagePlanPoint(camX,camforX,
					camupX,rightX,u,v,xFoV,yFoV);
				double imagePlainPointY=imagePlanPoint(camY,camforY,
					camupY,rightY,u,v,xFoV,yFoV);
				double imagePlainPointZ=imagePlanPoint(camZ,camforZ,
					camupZ,rightZ,u,v,xFoV,yFoV);

				double reciprocalLength=rnorm3d(imagePlainPointX-camX,imagePlainPointY-camY,
					imagePlainPointZ-camZ);

				// Normalize each component
				double rayDirX=(imagePlainPointX-camX)*reciprocalLength;
				double rayDirY=(imagePlainPointY-camY)*reciprocalLength;
				double rayDirZ=(imagePlainPointZ-camZ)*reciprocalLength;

				// Check for intersection with every triangle in mesh
				for(int t=0;t<n;t++)
				{
					// v0, v1, v2 represent vertices
					// Adding the mesh position to all vertices
					// because the vertices' coords are relative
					// to the mesh, not the scene/world
					double meshV0X=v0X[t]+meshposX;
					double meshV0Y=v0Y[t]+meshposY;
					double meshV0Z=v0Z[t]+meshposZ;
					double meshV1X=v1X[t]+meshposX;
					double meshV1Y=v1Y[t]+meshposY;
					double meshV1Z=v1Z[t]+meshposZ;
					double meshV2X=v2X[t]+meshposX;
					double meshV2Y=v2Y[t]+meshposY;
					double meshV2Z=v2Z[t]+meshposZ;

					double aX=meshV1X-meshV0X;
					double aY=meshV1Y-meshV0Y;
					double aZ=meshV1Z-meshV0Z;
					double bX=meshV2X-meshV0X;
					double bY=meshV2Y-meshV0Y;
					double bZ=meshV2Z-meshV0Z;

					double cX=cross3dX(aY,aZ,bY,bZ);
					double cY=cross3dY(aX,aZ,bX,bZ);
					double cZ=cross3dZ(aX,aY,bX,bY);

					double normX=cX*rnorm3d(cX,cY,cZ);
					double normY=cY*rnorm3d(cX,cY,cZ);
					double normZ=cZ*rnorm3d(cX,cY,cZ);

					double d=dot3d(-meshV0X,-meshV0Y,-meshV0Z,
						normX,normY,normZ);
					
					double rayX=camX;
					double rayY=camY;
					double rayZ=camZ;
					double rayDotNorm=dot3d(rayX,rayY,rayZ,
						normX,normY,normZ);
					double rayDirDotNorm=dot3d(rayDirX,rayDirY,rayDirZ,
						normX,normY,normZ);
					
					double distance=-(rayDotNorm+d)/rayDirDotNorm;
					if(distance>EPSILON)
					{
						double interX = rayX+rayDirX*distance;
						double interY = rayY+rayDirY*distance;
						double interZ = rayZ+rayDirZ*distance;

						// calculations for a
						double eX=meshV1X-meshV0X;
						double eY=meshV1Y-meshV0Y;
						double eZ=meshV1Z-meshV0Z;
						double fX=interX-meshV0X;
						double fY=interY-meshV0Y;
						double fZ=interZ-meshV0Z;
						double gX=cross3dX(eY,eZ,fY,fZ);
						double gY=cross3dY(eX,eZ,fX,fZ);
						double gZ=cross3dZ(eX,eY,fX,fY);
						double a=dot3d(gX,gY,gZ,
							normX,normY,normZ);

						// calculations for b
						double hX=meshV2X-meshV1X;
						double hY=meshV2Y-meshV1Y;
						double hZ=meshV2Z-meshV1Z;
						double iX=interX-meshV1X;
						double iY=interY-meshV1Y;
						double iZ=interZ-meshV1Z;
						double jX=cross3dX(hY,hZ,iY,iZ);
						double jY=cross3dY(hX,hZ,iX,iZ);
						double jZ=cross3dZ(hX,hY,iX,iY);
						double b=dot3d(jX,jY,jZ,
							normX,normY,normZ);

						// calculations for c
						double kX=meshV0X-meshV2X;
						double kY=meshV0Y-meshV2Y;
						double kZ=meshV0Z-meshV2Z;
						double lX=interX-meshV2X;
						double lY=interY-meshV2Y;
						double lZ=interZ-meshV2Z;
						double mX=cross3dX(kY,kZ,lY,lZ);
						double mY=cross3dY(kX,kZ,lX,lZ);
						double mZ=cross3dZ(kX,kY,lX,lY);
						double c=dot3d(mX,mY,mZ,
							normX,normY,normZ);

						if(a>0&&b>0&&c>0)
						{
							bIntersected=true;
							break;
						}
					}
					if(bIntersected)break;
				}
				if(bIntersected)
				{
					outR[i+j*xRes]=237/255.0;
					outG[i+j*xRes]=142/255.0;
					outB[i+j*xRes]=0/255.0;
				}
			}
		}
	}
}