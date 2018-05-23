package cemeteryfuntimes;
import cemeteryfuntimes.Code.Shared.Globals;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import cemeteryfuntimes.Code.*;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
/**
* Screen class enables rendering, handles keyboard input and
* sets up the game loop.
* @author David Kozloff & Tyler Law
*/
public class Screen extends JPanel implements Globals {
    
    private final Game game;
    public Game getGame() {
        return game;
    }
    private final java.util.Timer gameUpdater; 
    private final boolean gameRunning;
    
    private BufferedImage backgroundImage;
    private BufferedImage gameBackgroundImage;
    private BufferedImage heartContainer;
    private long lastUpdate;
    //This is used to stop the repeatedly firing pressed events
    private final int[][] keysReleased;
    
    /**
    * Enumerator stores information about mapping keyboard inputs.
    * Each item contains a key code, its callback function,
    * and a key code identifier to be used in the callback function.
    */
    private enum Action {
        //This enum is for storing all keyboard events that map to specific game actions
        //These are the movement key events i.e. arrow keys
        UPMOVEMENT(KeyEvent.VK_W,MOVEMENT,UP),DOWNMOVEMENT(KeyEvent.VK_S,MOVEMENT,DOWN),LEFTMOVEMENT(KeyEvent.VK_A,MOVEMENT,LEFT),RIGHTMOVEMENT(KeyEvent.VK_D,MOVEMENT,RIGHT),
        //These are the shooting key events i.e. wasd
        SHOOTINGUP(KeyEvent.VK_UP,SHOOT,UP),SHOOTINGDOWN(KeyEvent.VK_DOWN,SHOOT,DOWN),SHOOTINGLEFT(KeyEvent.VK_LEFT,SHOOT,LEFT),SHOOTINGRIGHT(KeyEvent.VK_RIGHT,SHOOT,RIGHT),
        //These are the changing weapon key events
        WEAPONPREV(KeyEvent.VK_Q,CHANGEWEAPON,PREVIOUSWEAPON),WEAPONNEXT(KeyEvent.VK_E,CHANGEWEAPON,NEXTWEAPON),
        WEAPONONE(KeyEvent.VK_1,CHANGESPECIFICWEAPON,PISTOL) ,WEAPONTWO(KeyEvent.VK_2,CHANGESPECIFICWEAPON,MACHINEGUN),WEAPONFIVE(KeyEvent.VK_4,CHANGESPECIFICWEAPON,FLAMETHROWER),
        WEAPONTHREE(KeyEvent.VK_3,CHANGESPECIFICWEAPON,SHOTGUN);
        private final int keyCode;
        private final int actionType;
        private final int gameCode;
        
        /**
        * Enumerator constructor.
        * @param keyCode    The key code corresponding to a specific key.
        * @param actionType Identifies the callback function for the key event, i.e. the event handler.
        * @param gameCode   An argument for the callback function.  Allows the callback function to handle different key events.
        */
        private Action(int keyCode, int actionType, int gameCode) {
            this.keyCode = keyCode;
            this.actionType=actionType;
            this.gameCode=gameCode;
        }
        
        public int getKeyCode() {
            return keyCode;
        }
        
        public int getActionType() {
            return actionType;
        }
        
        public int getGameCode() {
            return gameCode;
        }
    }
    
    /** Screen class constructor sets up key bindings, creates the Game object, and begins the game loop.*/
    public Screen() {
        keysReleased = new int[4][6];
        for (int i = 0; i < keysReleased.length; i++)
            for (int j = 0; j < keysReleased[0].length; j++)
                keysReleased[i][j] = 1;
        setKeyBindings();
        setupImages();
        game = new Game();
        gameRunning = true;
        
        //Start updating game 
        gameUpdater = new Timer();
        gameUpdater.schedule(new GameUpdater(), 0, TIMERDELAY);
      
    }
    
    /** 
    * GameUpdater class forms the game loop, updating and rendering the game at a fixed rate.
    * TIMERDELAY dictates the update rate.
    */
    private class GameUpdater extends java.util.TimerTask {
        // Updates and renders the game in the game loop.
        // The game loop stops if gameRunning is set to false.
        @Override
        public void run() {
//            long now = System.currentTimeMillis();
//            if (game.getPlayer().getHealth() <= 0) {
//                if (now - lastUpdate < 3000) {
//                    return;
//                }
//                game = new Game();
//            }
//            lastUpdate = now;
            game.update();
            repaint();
            if (!gameRunning) {
                gameUpdater.cancel();
            }
        }
    }
    
    /**
    * Handles the rendering instructions for the GPU during the game loop.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, null);
        g.drawImage(gameBackgroundImage, GAMEBORDER, 0, null);
        Graphics2D g2d = (Graphics2D) g;
        game.draw(g2d);
    }
    
    /**
    * Sets the window dimensions specified in Globals.java
    * 
    * @return A Dimension object used by Screen to determine window dimensions.
    */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SCREENWIDTH,SCREENHEIGHT);
    }
    
    /**
    * Sets up the key bindings for the game using the Action enumerator.
    */
    private void setKeyBindings() {
        //Sets up basic keybindings for game
        //Maps WASD and arrow keys to their respective actions
        InputMap inMap = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actMap = getActionMap();
        int keyCode;
        int actionType;
        int gameCode;
        for (final Action gameAction : Action.values()) {
            keyCode = gameAction.getKeyCode();
            actionType = gameAction.getActionType();
            gameCode = gameAction.getGameCode();
            KeyStroke pressed = KeyStroke.getKeyStroke(keyCode, 0, false);
            KeyStroke released = KeyStroke.getKeyStroke(keyCode, 0, true);
            inMap.put(pressed,keyCode+"pressed");
            inMap.put(released,keyCode+"released");
            actMap.put(keyCode+"pressed", new GameAction(gameCode,actionType,true));
            actMap.put(keyCode+"released",new GameAction(gameCode,actionType,false));
        }
    }
    
    /**
    * GameAction class uses the Action enumerator to specify the 
    * callback function and its arguments for a specific key event.
    */
    private class GameAction extends AbstractAction {
        //This class is to perform actions when a key is pressed
        int gameCode; //Integer that correspond to specific actions see "Player Commands" in Globals
        boolean isPressed; //True if this action is for key pressed false if for key released
        int actionType; //Type of action performed see "Action Types" in globals
        Method action = null;
        
        /**
        * GameAction class constructor uses data from the Action enumerator.
        * 
        * @param actionType Identifies the callback function for the key event, i.e. the event handler.
        * @param gameCode   An argument for the callback function.  Allows the callback function to handle different key events.
        * @param isPressed  A boolean which indicates if the specified key is currently pressed.
        */
        GameAction(int gameCode, int actionType, boolean isPressed) {
            this.gameCode=gameCode;
            this.isPressed=isPressed;
            this.actionType=actionType;
            String methodName = "";
            switch (actionType) {
                case MOVEMENT:
                    methodName = "movementAction";
                    break;
                case SHOOT:
                    methodName = "shootAction";
                    break;
                case CHANGEWEAPON:
                    methodName = "changeWeaponAction";
                    break;
                case CHANGESPECIFICWEAPON:
                    methodName = "changeSpecificWeaponAction";
                    break;
            }
            try {
                action = Game.class.getMethod(methodName,Integer.TYPE,Boolean.TYPE);
            }
            catch(NoSuchMethodException ex) {}
        }
        
        /**
        * Calls the callback function identified by actionType 
        * for the given element in the Action enumerator, represented as an ActionEvent object.
        * This method is called whenever a key event listed in the Action enumerator occurs.
        * 
        * @param e Unused
        */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isPressed) { keysReleased[actionType][gameCode] = 1; }
            else if (isPressed && keysReleased[actionType][gameCode]==0) { return; }
            else { keysReleased[actionType][gameCode] = 0; }
            try {
                action.invoke(game,gameCode,isPressed);
            }
            catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {}
        }
    }
    /**
    * Initializes BufferedImage objects, which are used to render images.
    */
    private void setupImages() {
        backgroundImage = cemeteryfuntimes.Code.Shared.Utilities.getScaledInstance(IMAGEPATH+"General/background.jpg",SCREENWIDTH,SCREENHEIGHT);
        gameBackgroundImage = cemeteryfuntimes.Code.Shared.Utilities.getScaledInstance(IMAGEPATH+"General/gameBackground.jpg",GAMEWIDTH,GAMEHEIGHT);
    }
}
