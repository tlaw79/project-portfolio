package cemeteryfuntimes.Code.Shared;
import java.awt.image.BufferedImage;
import java.util.HashMap;
/**
* ImageLoader class loads, stores and retrieves images.
* 
* @author David Kozloff & Tyler Law
*/
public class ImageLoader implements Globals {
    
    private static final HashMap<String,BufferedImage> images = new HashMap();
    /**
    * Retrieves the requested image.
    * 
    * @param imageName  Image path name.
    * @param rotation   Image rotation.
    * @return           The requested image.
    */
    public static BufferedImage getImage(String imageName, double rotation) {
        BufferedImage image = images.get(imageName);
        if (image == null) { return null; }
        return Utilities.rotateImage(image, rotation);
    }
    /**
    * Loads the requested image.
    * 
    * @param imagePath  Image path name.
    * @param width      The image width.
    * @param height     The image height.
    */
    public static void loadImage(String imagePath, int width, int height) {
        if (images.get(imagePath) == null) {
            images.put(imagePath,Utilities.getScaledInstance(IMAGEPATH+imagePath,width,height));
        }
    }
    /**
    * Clears all images from the hash map.
    */
    public void clearImages() {
        images.clear();
    }
}
