/**
* Scene
*
* @author Sam Collier
*/
import java.util.ArrayList;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import jcuda.runtime.dim3;

import static jcuda.driver.JCudaDriver.*;
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
	public ColourImage render(int xRes, int yRes, boolean bCuda)
	{
		if(!bCuda) //if the user is a pleb
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
		else //GPU go zoom
		{
			ColourImage outImage=new ColourImage(xRes,yRes);
			for(Surface s:surfaces)
			{
				if(s.getType()==0) //Custom Mesh Identifier
				{
					ColourImage meshChanges=cudaRenderCustomMesh(xRes,yRes);
					for(int i=0;i<xRes;i++)
						for(int j=0;j<yRes;j++)
							if(!meshChanges.getColour(i,j).isBlack())
								outImage.setColour(i,j,meshChanges.getColour(i,j));
				}
				else if(s.getType()==1) //Triangle Identifier
				{
					ColourImage triChanges=cpuRenderSurface(xRes,yRes,s); //It's actually faster on the CPU for now
					for(int i=0;i<xRes;i++)
						for(int j=0;j<yRes;j++)
							if(!triChanges.getColour(i,j).isBlack())
								outImage.setColour(i,j,triChanges.getColour(i,j));
				}
				else if(s.getType()==2) //Sphere Identifier
				{
					ColourImage sphereChanges=cpuRenderSurface(xRes,yRes,s); //It's actually faster on the CPU for now
					for(int i=0;i<xRes;i++)
						for(int j=0;j<yRes;j++)
							if(!sphereChanges.getColour(i,j).isBlack())
								outImage.setColour(i,j,sphereChanges.getColour(i,j));
				}
			}
			return outImage;
		}
	}
	
	public ColourImage cpuRenderSurface(int xRes,int yRes,Surface s)
	{
		ColourImage image=new ColourImage(xRes,yRes);
		for(int i=0;i<image.getWidth();i++)
		{
			for(int j=0;j<image.getHeight();j++)
			{
				double u=(i+.5)/xRes;
				double v=(j+.5)/yRes;
				Ray ray=camera.generateRay(u, v);
				if(s.intersect(ray)!=null)
				{
					image.setColour(i, j, new Colour(0,157,255));
				}
			}
		}
		return image;
	}
	
	public ColourImage cudaRenderCustomMesh(int xRes,int yRes)
	{
		// Enable exceptions and omit all subsequent error checks
		JCudaDriver.setExceptionsEnabled(true);

		String ptxFileName = "src/cudarender.ptx";

		cuInit(0);
		CUdevice device = new CUdevice();
		cuDeviceGet(device,0);
		CUcontext context = new CUcontext();
		cuCtxCreate(context,0,device);

		CUmodule module = new CUmodule();
		cuModuleLoad(module,ptxFileName);

		CUfunction function = new CUfunction();
		cuModuleGetFunction(function,module,"render");

		CustomMesh mesh=(CustomMesh)surfaces.get(0);
		int n=mesh.triangles.size();
		double camX=camera.getCameraLocation().getX();
		double camY=camera.getCameraLocation().getY();
		double camZ=camera.getCameraLocation().getZ();
		double camforX=camera.getForwardVector().getDX();
		double camforY=camera.getForwardVector().getDY();
		double camforZ=camera.getForwardVector().getDZ();
		double camupX=camera.getUpVector().getDX();
		double camupY=camera.getUpVector().getDY();
		double camupZ=camera.getUpVector().getDZ();
		double fov=camera.getFOV();
		double aspectRatio=camera.getAspectRatio();
		double[] v0X=new double[n];
		double[] v0Y=new double[n];
		double[] v0Z=new double[n];
		double[] v1X=new double[n];
		double[] v1Y=new double[n];
		double[] v1Z=new double[n];
		double[] v2X=new double[n];
		double[] v2Y=new double[n];
		double[] v2Z=new double[n];
		double meshposX=mesh.getMeshLocation().getX();
		double meshposY=mesh.getMeshLocation().getY();
		double meshposZ=mesh.getMeshLocation().getZ();

		for(int i=0;i<n;i++)
		{
			v0X[i]=mesh.triangles.get(i).v0.getX();
			v0Y[i]=mesh.triangles.get(i).v0.getY();
			v0Z[i]=mesh.triangles.get(i).v0.getZ();
			v1X[i]=mesh.triangles.get(i).v1.getX();
			v1Y[i]=mesh.triangles.get(i).v1.getY();
			v1Z[i]=mesh.triangles.get(i).v1.getZ();
			v2X[i]=mesh.triangles.get(i).v2.getX();
			v2Y[i]=mesh.triangles.get(i).v2.getY();
			v2Z[i]=mesh.triangles.get(i).v2.getZ();
		}

		//Sorry about the naming, I'll address it later.
		CUdeviceptr v0XPtr=new CUdeviceptr();
		cuMemAlloc(v0XPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v0XPtr,Pointer.to(v0X),n*Sizeof.DOUBLE);
		CUdeviceptr v0YPtr=new CUdeviceptr();
		cuMemAlloc(v0YPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v0YPtr,Pointer.to(v0Y),n*Sizeof.DOUBLE);
		CUdeviceptr v0ZPtr=new CUdeviceptr();
		cuMemAlloc(v0ZPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v0ZPtr,Pointer.to(v0Z),n*Sizeof.DOUBLE);
		CUdeviceptr v1XPtr=new CUdeviceptr();
		cuMemAlloc(v1XPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v1XPtr,Pointer.to(v1X),n*Sizeof.DOUBLE);
		CUdeviceptr v1YPtr=new CUdeviceptr();
		cuMemAlloc(v1YPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v1YPtr,Pointer.to(v1Y),n*Sizeof.DOUBLE);
		CUdeviceptr v1ZPtr=new CUdeviceptr();
		cuMemAlloc(v1ZPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v1ZPtr,Pointer.to(v1Z),n*Sizeof.DOUBLE);
		CUdeviceptr v2XPtr=new CUdeviceptr();
		cuMemAlloc(v2XPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v2XPtr,Pointer.to(v2X),n*Sizeof.DOUBLE);
		CUdeviceptr v2YPtr=new CUdeviceptr();
		cuMemAlloc(v2YPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v2YPtr,Pointer.to(v2Y),n*Sizeof.DOUBLE);
		CUdeviceptr v2ZPtr=new CUdeviceptr();
		cuMemAlloc(v2ZPtr,n*Sizeof.DOUBLE);
		cuMemcpyHtoD(v2ZPtr,Pointer.to(v2Z),n*Sizeof.DOUBLE);

		double[] outR=new double[xRes*yRes];
		double[] outG=new double[xRes*yRes];
		double[] outB=new double[xRes*yRes];

		for(int i=0;i<xRes;i++)
		{
			for(int j=0;j<yRes;j++)
			{
				outR[i+j*xRes]=(double)0;
				outG[i+j*xRes]=(double)0;
				outB[i+j*xRes]=(double)0;
			}
		}

		CUdeviceptr out0=new CUdeviceptr();
		cuMemAlloc(out0,xRes*yRes*Sizeof.DOUBLE);
		cuMemcpyHtoD(out0,Pointer.to(outR),xRes*yRes*Sizeof.DOUBLE);
		CUdeviceptr out1=new CUdeviceptr();
		cuMemAlloc(out1,xRes*yRes*Sizeof.DOUBLE);
		cuMemcpyHtoD(out1,Pointer.to(outG),xRes*yRes*Sizeof.DOUBLE);
		CUdeviceptr out2=new CUdeviceptr();
		cuMemAlloc(out2,xRes*yRes*Sizeof.DOUBLE);
		cuMemcpyHtoD(out2,Pointer.to(outB),xRes*yRes*Sizeof.DOUBLE);

		Pointer kernelParams = Pointer.to(
				Pointer.to(new int[] {n}), 
				Pointer.to(new int[] {xRes}), 
				Pointer.to(new int[] {yRes}),
				Pointer.to(new double[] {camX}),
				Pointer.to(new double[] {camY}),
				Pointer.to(new double[] {camZ}),
				Pointer.to(new double[] {camforX}),
				Pointer.to(new double[] {camforY}),
				Pointer.to(new double[] {camforZ}),
				Pointer.to(new double[] {camupX}),
				Pointer.to(new double[] {camupY}),
				Pointer.to(new double[] {camupZ}),
				Pointer.to(new double[] {fov}),
				Pointer.to(new double[] {aspectRatio}),
				Pointer.to(v0XPtr),
				Pointer.to(v0YPtr),
				Pointer.to(v0ZPtr),
				Pointer.to(v1XPtr),
				Pointer.to(v1YPtr),
				Pointer.to(v1ZPtr),
				Pointer.to(v2XPtr),
				Pointer.to(v2YPtr),
				Pointer.to(v2ZPtr),
				Pointer.to(new double[] {meshposX}),
				Pointer.to(new double[] {meshposY}),
				Pointer.to(new double[] {meshposZ}),
				Pointer.to(out0),
				Pointer.to(out1),
				Pointer.to(out2)
				);
		JCudaDriver.cuCtxSetLimit(CUlimit.CU_LIMIT_PRINTF_FIFO_SIZE,4096);
		//Call the function
		int blockSizeX=22;
		int blockSizeY=22;
		int gridSizeX = (int)Math.ceil((double)xRes/blockSizeX);
		int gridSizeY = (int)Math.ceil((double)yRes/blockSizeY);
		cuLaunchKernel(function,
				gridSizeX,gridSizeY,1, //grid size
				22,22,1, //block size
				0,null, //shared memory size and stream TODO probably worth understanding this one
				kernelParams,null //kernel parameters, extra parameters. ONE OF THESE HAS TO BE NULL
				);

		//Wait for the GPU to finish
		cuCtxSynchronize();

		cuMemcpyDtoH(Pointer.to(outR),out0,xRes*yRes*Sizeof.DOUBLE);
		cuMemcpyDtoH(Pointer.to(outG),out1,xRes*yRes*Sizeof.DOUBLE);
		cuMemcpyDtoH(Pointer.to(outB),out2,xRes*yRes*Sizeof.DOUBLE);

		cuMemFree(v0XPtr);
		cuMemFree(v0YPtr);
		cuMemFree(v0ZPtr);
		cuMemFree(v1XPtr);
		cuMemFree(v1YPtr);
		cuMemFree(v1ZPtr);
		cuMemFree(v2XPtr);
		cuMemFree(v2YPtr);
		cuMemFree(v2ZPtr);
		cuMemFree(out0);
		cuMemFree(out1);
		cuMemFree(out2);

		ColourImage image=new ColourImage(xRes,yRes);
		int countR=0,countG=0,countB=0;
		for(int i=0;i<xRes;i++)
		{
			for(int j=0;j<yRes;j++)
			{
				image.setColour(i, j, new Colour(outR[i+j*xRes],outG[i+j*xRes],outB[i+j*xRes]));
			}
		}
		return image;
	}
}
