package cemeteryfuntimes.Code.Shared;
import cemeteryfuntimes.Code.Bosses.Ghoulie;
import cemeteryfuntimes.Code.Player;
/**
* PosVel abstract class contains variables and methods related to
* object positions, velocities, collisions between two PosVels, and
* other dimensional variables.
* 
* @author David Kozloff & Tyler Law
*/
public abstract class PosVel implements Globals {
  
    //Member variables
    protected float xPos;   
    protected float yPos;
    protected float xVel;
    protected float yVel;
    protected int xRad;
    protected int yRad;
    protected int rad;
    protected float xSide;
    protected float ySide;
    protected double rotation;
    
    //Getters 
    public float xPos() {
        return xPos;
    }
    public float yPos() {
        return yPos;
    }
    public float xVel() {
        return xVel;
    }
    public float yVel() {
        return yVel;
    }
    public int xRad() {
        return xRad;
    }
    public int yRad() {
        return yRad;
    }
    public int rad() {
        return rad;
    }
    public double rotation() {
        return rotation;
    }
    /**
    * Constructor for PosVel, initiates position
    * 
    * @param xPos x Position
    * @param yPos y Position
    */
    public PosVel(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
    /**
    * Returns true if this PosVel is overlapping with another PosVel.
    * 
    * @param other The other PosVel.
    * @return      True if this PosVel is overlapping with another PosVel, false otherwise.
    */
    public boolean collide(PosVel other) {
        //Return true if the two PosVels are overlapping
        if ((this instanceof Ghoulie) && (other instanceof Player)) {
            // Fake radii used when Player collides with Ghoulie in order to
            // create a nicer looking collision in this particular case.
            Ghoulie ghoulie = (Ghoulie) this;
            if (Math.abs(other.xPos+other.xVel - (xPos+xVel)) < other.xRad + ghoulie.getFauxXRad()) {
                if (Math.abs(other.yPos+other.yVel - (yPos+yVel)) < other.yRad + ghoulie.getFauxYRad()) {
                    return true;
                }
            }
        } else {
            if (Math.abs(other.xPos+other.xVel - (xPos+xVel)) < other.xRad + xRad) {
                if (Math.abs(other.yPos+other.yVel - (yPos+yVel)) < other.yRad + yRad) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
    * Abstract method implememnted by PosVels that need custom code to handle getting damaged.
    * 
    * @param damage The damage being dealt to this PosVel.
    */
    public void damaged(float damage) {}
    
    /**
    * Determines which side of this PosVel is currently colliding with another PosVel.
    * 
    * @param other The other PosVel.
    * @return      An integer representing which wall of the PosVel experienced the collision.
    */
    public int sideCollided(PosVel other) {
        //Return an integer representing which wall is was the collision point
        //Assumes the two object are colliding (i.e. collide returned true)
        float xDiff = xPos - other.xPos;
        float yDiff = yPos - other.yPos;
        float absXDiff = Math.abs(xDiff);
        float absYDiff = Math.abs(yDiff);
        if (absXDiff > absYDiff) {
           if (xDiff < 0) return RIGHTWALL;
           else return LEFTWALL;
        } 
        else {
           if (yDiff < 0) return BOTTOMWALL;
           else return TOPWALL;
        }
    }
    /**
    * Returns true if the PosVel is colliding with a wall.
    * 
    * @return  True if the PosVel is colliding with a wall, false otherwise.
    */
    public boolean hitWall() {
        //Returns true if object has collided with a wall
        if (xPos + xRad > GAMEWIDTH) {
            return true;
        }
        else if (xPos - xRad < 0) {
            return true;
        }
        if (yPos + yRad > GAMEHEIGHT) {
            return true;
        }
        else if (yPos - yRad < 0) {
            return true;
        }
        return false;
    }
    /**
    * Returns an array of booleans indicating whether or not this PosVel is colliding with a specific wall.
    * 
    * @return  An array of booleans indicating whether or not this PosVel is colliding with a specific wall.
    */
    public boolean[] checkWallCollision() {
        //Returns an array of booleans indicating whether or not this object
        //has collided with that wall
        boolean[] wallsHit = new boolean[4];
        if (xPos + xRad > GAMEWIDTH) {
            wallsHit[RIGHTWALL]=true;
        }
        else if (xPos - xRad < 0) {
            wallsHit[LEFTWALL]=true;
        }
        if (yPos + yRad > GAMEHEIGHT) {
            wallsHit[BOTTOMWALL]=true;
        }
        else if (yPos - yRad < 0) {
            wallsHit[TOPWALL]=true;
        }
        return wallsHit;
    }
    /**
     * Used to find which wall an object has collided with
     * 
     * @return The wall collided with else 
     */
    public int hitSpecificWall() {
        //Returns which wall the object hit
        if (xPos + xRad > GAMEWIDTH) {
            return RIGHT;
        }
        else if (xPos - xRad < 0) {
            return LEFT;
        }
        if (yPos + yRad > GAMEHEIGHT) {
            return DOWN;
        }
        else if (yPos - yRad < 0) {
            return UP;
        }
        return -1;
    }
}