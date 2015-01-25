package sprite;

import java.lang.Math;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

abstract public class Sprite {
    
    /**************************************************************************
    ***********************************DATA************************************
    **************************************************************************/

    // border, (x,y), highlighted and solid status, and image
    private Rectangle2D.Float border;
    int X, Y;
    float scale = 1.0f;
    int rotangle;
    boolean isHighlighted;
    boolean isSolid;
    private Image image;
    String imgPath;

    /**************************************************************************
    ******************************CONSTRUCTOR(S)*******************************
    **************************************************************************/

    public Sprite() {}

    /**
     * construct a Sprite
     * @param imgpath the path for where to find the image for the sprite
     * @param startX the starting x position
     * @param startY the starting y position
     * @param sz the initial size for the enemy sprite
     * @param iss a boolean true if the Sprite is solid and false otherwise
    */
    Sprite(String imgpath, int startX, int startY, float sz, boolean iss) {
        rotangle = 0;
        // set regular information
        X = startX;
        Y = startY;
        scale = sz;
        imgPath = new String(imgpath);
        image = new ImageIcon(imgpath).getImage();
        border = new Rectangle2D.Float(X, Y, image.getWidth(null),
                                       image.getHeight(null));
        isSolid = iss;
    }

    /**
     * construct a Sprite
     * @param imgpath the path for where to find the image for the sprite
     * @param startX the starting x position
     * @param startY the starting y position
     * @param sz the initial size for the enemy sprite
     * @param ra the rotation angle in degrees of the Sprite
     * @param iss a boolean true if the Sprite is solid and false otherwise
    */
    Sprite(String imgpath, int startX, int startY, float sz, int ra, boolean iss) {
        rotangle = ra;
        // set regular information
        X = startX;
        Y = startY;
        scale = sz;
        imgPath = new String(imgpath);
        image = new ImageIcon(imgpath).getImage();
        border = new Rectangle2D.Float(X, Y, image.getWidth(null),
                                       image.getHeight(null));
        isSolid = iss;
    }


    /**************************************************************************
    **********************************METHODS**********************************
    **************************************************************************/

    /**
     * draw the sprite onto the Graphics2D context g
     * @param g the Graphics2D context
    */
    public void draw(Graphics2D g) {
        g.rotate(Math.toRadians(rotangle), X + (border.getWidth() / 2),
                 Y + (border.getHeight() / 2));
        g.drawImage(image, X, Y, (int) (scale * image.getWidth(null)),
                    (int) (scale * image.getHeight(null)), null);
        if (isHighlighted) {
            g.setColor(Color.BLACK);
            g.draw(border);
        }
    }

    /**
     * select/deselect the shape
     * @param b a boolean that is true if the shape is to be selected and false
     * otherwise
    */
    public void highlight(boolean b) {
        isHighlighted = b;
    }

    /**
     * check to see if a point (x,y) is contained by a sprite
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return true if the point is contained by the sprite and false otherwise
    */
    public boolean contains(int x, int y) {
        return border.contains(x, y);
    }

    /**
     * translate the sprite
     * @param dx the number of units to translate to the right
     * @param dy the number of units to translate down
    */
    public void move(int newX, int newY) {
        border = new Rectangle2D.Float(newX, newY, (int)border.getWidth(), (int)border.getHeight());
        X = newX;
        Y = newY;
    }

    /**
     * rotate the sprite
     * @param theta the angle (in degrees) to rotate the image to
    */
    public void rotate(int theta) {
        rotangle = theta;
    }

    /**
     * scale the sprite
     * @param sz the scale factor
     */
    public void scale(float sz) {
        scale = sz;
        border = new Rectangle2D.Float(X, Y, (int) (scale * border.getWidth()),
                                       (int) (scale * border.getHeight()));
    }

    /**
     * change the solid status of the sprite
     * @param isSolid a boolean true if the image should be solid and false
     * otherwise
    */
    public void setSolidStatus(boolean solidStatus) {
        isSolid = solidStatus;
    }

    /**
     * convert the sprite to a string
     * @return a string representation of the sprite
    */
    public String toString() {
        String[] prts = imgPath.split("/");
        return prts[prts.length - 1];
    }

    /**
     * compare this sprite with another
     * @return true if the two have the same data
    */
    public boolean equals(Object obj) {
        if (obj instanceof Sprite) {
            Sprite other = (Sprite) obj;
            return (X == other.X)
                && (Y == other.Y)
                && (scale == other.scale)
                && (rotangle == other.rotangle)
                && (isHighlighted == other.isHighlighted)
                && (isSolid == other.isSolid)
                && image.equals(other.image)
                && imgPath.equals(other.imgPath);
        }
        else return false;
    }

    /**
     * get the type of the current sprite
     * @return the type of the current spirite
    */
    abstract public String getType();

    /**
     * get the information of the sprite in string form
     * @return a string containing the sprite's information
    */
    abstract public String saveData();
}

