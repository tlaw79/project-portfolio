package cemeteryfuntimes.Code.Shared;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
* Utilities class contains methods shared by multiple classes.
* @author David Kozloff & Tyler Law
*/
public class Utilities implements Globals {
    /**
    * Convenience method that returns a scaled instance of the
    * provided {@code BufferedImage}.
    *
    * @param filepath     The path to the image you want to render.
    * @param targetWidth  The desired width of the scaled instance, in pixels.
    * @param targetHeight The desired height of the scaled instance, in pixels.
    * @return             A scaled version of the original {@code BufferedImage}.
    */
    public static BufferedImage getScaledInstance(String filepath,
                                           int targetWidth,
                                           int targetHeight)
    {
        BufferedImage img;
        try { 
            img = ImageIO.read(new File(filepath));
        } catch (IOException e) { return null; }
        
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        // Use one-step technique: scale directly from original
        // size to target size with a single drawImage() call
        w = targetWidth;
        h = targetHeight;
        
        do {
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
    /**
    * Rotates an image based on a specified angle, in radians.
    * 
    * @param image   The buffered image to rotate.
    * @param radians The amount of rotation, in radians.
    * @return        The rotated buffered image.
    */
    public static BufferedImage rotateImage(BufferedImage image, double radians) {
        //Rotate the given image by the angle
        if (radians == 0) { return image; }
        AffineTransform at = AffineTransform.getRotateInstance(radians,image.getWidth()/2d,image.getHeight()/2d);
        BufferedImage rotatedImage = getEmptyImageDist(at,image);
        AffineTransformOp atOp = new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
        atOp.filter(image,rotatedImage);
        return rotatedImage;
    }
    /**
     * Helper function for rotateImage.
     *
     * @param rotation The affine transform that contains the rotation.
     * @param image    The buffered image we are rotating.
     * @return         Correctly sized buffered image for rotating.
     */
    private static BufferedImage getEmptyImageDist(AffineTransform rotation, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Point[] points = {
            new Point(0, 0),
            new Point(width, 0),
            new Point(width, height),
            new Point(0, height)
        };
        rotation.transform(points, 0, points, 0, 4);
        // get destination rectangle bounding box
        Point min = new Point(points[0]);
        Point max = new Point(points[0]);
        for (int i = 1, n = points.length; i < n; i ++) {
            Point p = points[i];
            double pX = p.getX(), pY = p.getY();
            // update min/max x
            if (pX < min.getX()) min.setLocation(pX, min.getY());
            if (pX > max.getX()) max.setLocation(pX, max.getY());
            // update min/max y
            if (pY < min.getY()) min.setLocation(min.getX(), pY);
            if (pY > max.getY()) max.setLocation(max.getX(), pY);
        }
        width = (int) (max.getX() - min.getX());
        height = (int) (max.getY() - min.getY());
        // determine required translation
        double tx = min.getX();
        double ty = min.getY();

        // append required translation
        AffineTransform translation = new AffineTransform();
        translation.translate(-tx, -ty);
        rotation.preConcatenate(translation);
        BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
        
        return rotatedImage;
    }
    /**
    * Loads data from an xml file.  Used to create variants of Enemy objects, Weapon objects, etc.
    * 
    * @param file         The path to the xml file.
    * @param elementStart The tag name in the xml file.
    * @param key          The key corresponding to a specific object variant.
    * @return             A NamedNodeMap containing the requested data, or null if an error occurs.
    */
    public static NamedNodeMap loadTemplate(String file, String elementStart, int key) {
        //Loads the contents of an xml template
         try {	
            File inputFile = new File(TEMPLATEPATH + file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(elementStart);
         
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i); 
                Element template = (Element) nNode;
                if (key == Integer.parseInt(template.getAttribute("Key"))) {
                    return template.getAttributes();
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }
    /**
    * Counts how many room keys the game uses.  Gets assigned to the
    * global ROOMKEYS in Globals class.  ROOMKEYS used to facilitate quick
    * addition and deletion of new room variants in NormalRoom class.
    * 
    * @return   The number of room keys in the Rooms.xml file.
    */
    public final static int countRoomKeys() {
        try {	
            File inputFile = new File(TEMPLATEPATH + "Rooms.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Room");
            int count = 0;
            for (int i = 0; i < nList.getLength(); i++) {
                count++;
            } return count;
        } catch (Exception ex) {
            return -1;
        }
    }
    /**
    * Returns the wall opposite the specified wall, represented as an integer constant.
    * 
    * @param side The wall to get the opposite side of
    * @return     The opposite wall, or -1 if the input is not a wall.
    */
     public static int otherSide(int side) {
        switch(side) {
            case RIGHTWALL:
                return LEFTWALL;
            case LEFTWALL:
                return RIGHTWALL;
            case TOPWALL:
                return BOTTOMWALL;
            case BOTTOMWALL:
                return TOPWALL;
        }
        return -1;
    }
    /**
    * Returns an int array with useful constants for doing math with horizontal or vertical collisions
    * 
    * @param side The relevant wall
    * @return     Int[0] = 1 if right wall, -1 if left wall, 0 if neither
    *             Int[1] = 1 if bottom wall, -1 if top wall, 0 if neither
    */
    public static int[] getHorizontalVertical(int side) {
        int[] horizontalVertical = new int[2];
        int positive = (side == RIGHT || side == DOWN) ? 1 : -1;
        horizontalVertical[HORIZONTAL] = positive * ((side == LEFT || side == RIGHT) ? 1 : 0);
        horizontalVertical[VERTICAL] = positive * ((side == UP || side == DOWN) ? 1 : 0);
        return horizontalVertical;
    }
}
