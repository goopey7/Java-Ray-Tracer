
import jcuda.*;
import jcuda.runtime.*;
public class JCudaTest
{
    public static void main(String args[])
    {
    	// Still learning, but I'm hoping to get this project rendered through the GPU
        Pointer pointer = new Pointer();
        JCuda.cudaMalloc(pointer, 4);
        System.out.println("Pointer: "+pointer);
        JCuda.cudaFree(pointer);;
    }
}