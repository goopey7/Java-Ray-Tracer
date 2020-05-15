import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import static jcuda.driver.JCudaDriver.*;

// Probably won't be useful for 
public class JCudaMatrixAdd
{
	public static void main(String[] args)
	{
		int arraySize = 100000000;
		System.out.println("============================= CPU =============================");
		float[] a = new float[arraySize];
		float[] b = new float[arraySize];
		for(int i=0; i<arraySize; i++)
		{
			a[i]=1;
			b[i]=2;
		}
		long timeElapsed=System.currentTimeMillis();
		for(int i=0; i<arraySize; i++)
		{
			b[i]=a[i]+b[i];
		}
		timeElapsed=System.currentTimeMillis()-timeElapsed;
		boolean passed=true;
		for(float out:b)
		{
			if(out!=3)passed=false;
		}
		System.out.println(passed?"SUCCESS":"FAIL");
		System.out.println(timeElapsed+" milliseconds elapsed");
		System.out.println("============================= GPU =============================");
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);
        
        // Create ptx file by using NVCC Ex. "nvcc -ptx jcudaVectorAddition.cu"
		String ptxFileName = "bin/jcudaVectorAddition.ptx";
		
		// Initialise the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);
        
        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFileName);

        // Obtain a function pointer to the "add" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "add");
        
        float[] x = new float[arraySize];
        float[] y = new float[arraySize];
        for(int i=0; i<arraySize; i++)
        {
        	x[i]=1;
        	y[i]=2;
        }
        // Allocate the device input data, and copy the
        // host input data to the device
        CUdeviceptr deviceInputA = new CUdeviceptr();
        cuMemAlloc(deviceInputA, arraySize * Sizeof.FLOAT);
        cuMemcpyHtoD(deviceInputA, Pointer.to(x),
                arraySize * Sizeof.FLOAT);
        
        CUdeviceptr deviceInputB = new CUdeviceptr();
        cuMemAlloc(deviceInputB, arraySize * Sizeof.FLOAT);
        cuMemcpyHtoD(deviceInputB, Pointer.to(y),
                arraySize * Sizeof.FLOAT);
        
        // Set up the kernel parameters: A pointer to an array
        // of pointers which point to the actual values.
        Pointer kernelParameters = Pointer.to(
                Pointer.to(new int[]{arraySize}),
                Pointer.to(deviceInputA),
                Pointer.to(deviceInputB)
        );
        
        // Call the kernel function.
        int blockSizeX = 256;
        int gridSizeX = (int)Math.ceil((double)arraySize/blockSizeX);
        timeElapsed = System.currentTimeMillis();
        cuLaunchKernel(function,
                gridSizeX,  1, 1,      // Grid dimension
                blockSizeX, 1, 1,      // Block dimension
                0, null,               // Shared memory size and stream
                kernelParameters, null // Kernel- and extra parameters
        );
        cuCtxSynchronize(); // Wait until function is complete
        timeElapsed=System.currentTimeMillis()-timeElapsed;
        
        // Allocate host output memory and copy the device output
        // to the host.
        float output[] = new float[arraySize];
        cuMemcpyDtoH(Pointer.to(output), deviceInputB, arraySize * Sizeof.FLOAT);
        
        //Test to see if it worked
        boolean bWorks=true;
        for(float out : output)
        {
        	if(out!=3)bWorks=false;
        	break;
        }
        System.out.println(bWorks?"SUCCESS":"FAILED");
        System.out.println(timeElapsed+" milliseconds elapsed.");
        
        // Clean up.
        cuMemFree(deviceInputA);
        cuMemFree(deviceInputB);
	}
}
