package cemeteryfuntimes.Code;
import cemeteryfuntimes.Code.Rooms.Room;
import cemeteryfuntimes.Code.Shared.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
* Game class contains variables and methods related to game state.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 Call RoomEntered, upon entering a new room.
*/
public class Game implements Globals {
    private final Player player;
    public Player getPlayer() {
        return player;
    }
    private Level level;
    private Room room;
    private BufferedImage heartContainer;
    private BufferedImage halfHeartContainer;
    private BufferedImage coin;
    private Boolean gameOver;
    
    //Constants
    private final static int HEARTSIZE=40;
    private final static int HEARTPADDING=10;
    /**
    * Game class constructor initializes variables related to game state.
    */
    public Game() {
        player = new Player(PLAYERSIZE/2,PLAYERSIZE/2);
        level = new Level(player,1);
        room = level.getCurrentRoom();
        gameOver = false;
        // Image setup.
        setupImages();
    }
    /**
    * Updates the game.
    * 
    * //DKOZLOFF 06/22 Call RoomEntered. Add code for going to next level.
    */    
    public void update() {
        if (!gameOver) {
            int collisionResult = Collision.checkCollisions(player,room); //DKOZLOFF+6 06/22
            if (collisionResult == PORTALCOLLISION) {
                int currentDepth = level.Depth();
                level = new Level(player,currentDepth+1);
                room = level.getCurrentRoom();
            }
            else if (level.changeRoom(collisionResult)) {
                room = level.getCurrentRoom();
                room.RoomEntered(); //DKOZLOFF 06/22
            }
            player.update();
            room.update();
        }
    }
    /**
    * Renders the game.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
        room.draw(g);
        player.draw(g);
        drawHUD(g);
        level.draw(g);
        if (this.player.getHealth() <= 0) {
            gameOver = true;
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier", 1, 100));
            g.drawString("GAME OVER", 300, 375);
        }
    }
    /**
    * Renders the player's heads up display.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    * 
    * //DKOZLOFF 06/22 Add display for current level.
    */
    public void drawHUD(Graphics2D g) {
        //Draw player's heart containers
        int index = 0;
        for (int i=2; i<player.getHealth()+1; i=i+2) {
            g.drawImage(this.heartContainer,(i/2)*(HEARTSIZE+HEARTPADDING)+HEARTPADDING-50,HEARTPADDING,null);
            index = i;
        }
        if (player.getHealth() % 2 == 1) {
            g.drawImage(this.halfHeartContainer,(1+(index/2))*(HEARTSIZE+HEARTPADDING)+HEARTPADDING-50,HEARTPADDING,null);
        }
        //Draw player's coins.
        g.drawImage(this.coin,10,70,null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier", 1, 25));
        Integer coinInt = player.getCoins();
        String coinStr = coinInt.toString();
        g.drawString(coinStr, 55, 99);
        g.setFont(new Font("Courier", 1, 25));
        g.drawString("Level: " + level.Depth(), 20, 750);
    }
    /**
    * Calls the callback method triggered by a set of related key events.
    * Movement keys.
    * 
    * @param gameCode  The movement direction key currently being pressed.
    * @param isPressed Returns true if the key is currently being pressed.
    */
    public void movementAction(int gameCode, boolean isPressed) {
        //Game code is the relevant global in the "Player Commands" section of globals
        player.movementKeyChanged(gameCode, isPressed);
    }
    /**
    * Calls the callback method triggered by a set of related key events.
    * Shooting keys.
    * 
    * @param gameCode  The shooting direction key currently being pressed.
    * @param isPressed Returns true if the key is currently being pressed.
    */
    public void shootAction(int gameCode, boolean isPressed) {
        //Game code is the relevant global in the "Player Commands" section of globals
        player.shootKeyChanged(gameCode, isPressed);
    }
    /**
    * Calls the callback method triggered by a set of related key events.
    * Changing weapons keys.
    * 
    * @param gameCode  The change weapon key currently being pressed.
    * @param isPressed Returns true if the key is currently being pressed.
    */
    public void changeWeaponAction(int gameCode, boolean isPressed) {
        //Game code is the relevant global in the "Player Commands" section of globals
        player.changeWeaponKeyChanged(gameCode,isPressed,false);
    }
    /**
    * Calls the callback method triggered by a set of related key events.
    * Changing weapons keys.
    * 
    * @param gameCode  The change weapon key currently being pressed.
    * @param isPressed Returns true if the key is currently being pressed.
    */
    public void changeSpecificWeaponAction(int gameCode, boolean isPressed) {
        //Game code is the relevant global in the "Player Commands" section of globals
        player.changeWeaponKeyChanged(gameCode,isPressed,true);
    }
    /**
    * Initializes BufferedImage objects, which are used to render images.
    */
    private void setupImages() {
       //Initialize always relevent images images
       ImageLoader.loadImage("General/heart.png",HEARTSIZE,HEARTSIZE);
       ImageLoader.loadImage("General/halfheart.png",HEARTSIZE/2,HEARTSIZE);
       ImageLoader.loadImage("General/coin.png",HEARTSIZE,HEARTSIZE);
       ImageLoader.loadImage("General/machinegun.png",HEARTSIZE*2,HEARTSIZE);
       ImageLoader.loadImage("General/flamethrower.png",HEARTSIZE*2,HEARTSIZE*3/4);
       ImageLoader.loadImage("General/shotgun.png",HEARTSIZE*2,HEARTSIZE*3/4);
       this.heartContainer = ImageLoader.getImage("General/heart.png",0);
       this.halfHeartContainer = ImageLoader.getImage("General/halfheart.png",0);
       this.coin = ImageLoader.getImage("General/coin.png",0);
       //halfHeartContainer = cemeteryfuntimes.Resources.Shared.Other.getScaledInstance("General/halfHeart.png",HEARTSIZE/2,HEARTSIZE);
    }
}