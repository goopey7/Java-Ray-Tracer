/**
* Just making sure it works!
*
* @author Sam Collier
*/
import jcuda.*;
import jcuda.runtime.*;
public class JCudaTest
{
    public static void main(String args[])
    {
    	// Still learning, but I'm hoping to get this project rendered through the GPU
        Pointer pointer = new Pointer(); // Java representation of a void pointer
        JCuda.cudaMalloc(pointer, 4); // Allocate memory on GPU. Takes a pointer and (long) size in bytes.
        System.out.println("Pointer: "+pointer);
        JCuda.cudaFree(pointer); // Frees memory space pointed to by our pointer.
    }
}