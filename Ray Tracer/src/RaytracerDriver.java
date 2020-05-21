import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Driver for the various stages of the image generation and saving process.
 * 
 * @author Ben Farrar with a bit of pizzazz from Sam Collier
 */
public class RaytracerDriver {
    public static void main(String[] args) throws IOException
    {
        //Size of the final image. This will DRAMATICALLY affect the runtime.
        final int xResolution = 1920;
        final int yResolution = 1080;
        
        //Create the scene. You can change this when you make other scene creation methods to select
        //which scene to render.
        System.out.println("Creating scene...");
        Scene s = SceneCreator.scene1(xResolution, yResolution);
        
        //Render the scene into a ColourImage
        System.out.println("Rendering image...");
        long elapsed=System.currentTimeMillis();
        ColourImage image = s.render(xResolution,yResolution,true);
        System.out.println("Rendered in "+((double)(System.currentTimeMillis()-elapsed)/1000)+" seconds");
        
        //Save the image out as a png
        System.out.println("Saving file...");
        String filename = "scene1.png";
        saveImage(filename, image);
        
        
        /* Simple image write. Use this to test if image writing is broken.
           If this doesn't work, that means something is wrong with your Java installation.
           If it DOES work, and you get a colour gradient image written out as "testGradient.png",
           but the normal saveImage does not work, that means something is wrong with your raytracing code.
           */
        //saveTestImage();
        
        System.out.println("Done");
    }
    
    /**
     * Reads in each pixel from a ColourImage, and then writes the image out to a PNG file.
     */
    public static void saveImage(String filename, ColourImage image)
    {
        try 
        {
            BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            for(int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    //This line reverses the y axis. Use the following line instead if your image is upside down.
                    bi.setRGB(x,image.getHeight()-1-y,image.getColour(x,y).toARGB());
                    //bi.setRGB(x,y,image.getColour(x,y).toARGB());
                }
            }
            ImageIO.write(bi, "PNG", new File(filename));
        } 
        catch(Exception e) 
        {
            System.out.println("Problem saving image: " + filename);
            System.out.println(e);
            System.exit(1);
        }
    }
}