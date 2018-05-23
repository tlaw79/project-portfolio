package cemeteryfuntimes.Code.Shared;
/**
* Globals interface contains global constants.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 Add PORTALSIZE, PORTALX, PORTALY, PORTALCOLLISION.
*/
public interface Globals {
    
    //Game dimensions
    public final static int SCREENHEIGHT=800;
    public final static int SCREENWIDTH=1200;
    public final static int GAMEHEIGHT=800;
    public final static int GAMEWIDTH=800;
    public final static int GAMEBORDER=(SCREENWIDTH-GAMEWIDTH)/2;
    
    //Movement constants
    public final static float PLAYERACCEL=.25f;
    public final static float PLAYERMAXSPEED=1.75f;
    public final static float FRICTION=.05f; //Friction coeffecient
    public final static float PLAYERCOLLISIONVEL=3f; //Player accel upon enemy collision
    public final static float BOSSCOLLISIONVEL=7f; //Player accel upon boss collision
    
    //Ballistic constants
    public final static float PROJECTILEBOOST=.25f;  //This constant represents the fraction of the players velocity that is added to the projectile speed
    
    //Creature dimensions
    public final static int PLAYERSIZE=100;
    
    //Player commands
    public final static int LEFT=0;
    public final static int RIGHT=1;
    public final static int UP=2;
    public final static int DOWN=3;
    public final static int HORIZONTAL=0;
    public final static int VERTICAL=1;
    public final static double[] ROTATION = {
        0,Math.PI,Math.PI/2,3*Math.PI/2
    };  
    public final static int NEXTWEAPON=0;
    public final static int PREVIOUSWEAPON=1;
    
    //Other
    public final static int TIMERDELAY=10;
    public final static long INVINCFRAMES=500;
    public final static int LEFTWALL=0;
    public final static int RIGHTWALL=1;
    public final static int TOPWALL=2;
    public final static int BOTTOMWALL=3;
    public final static double MINIMUMROTATION=0.0174533; //1 degrees in radians
    public final static String IMAGEPATH="src/cemeteryfuntimes/Resources/Images/";
    public final static String TEMPLATEPATH="src/cemeteryfuntimes/Resources/Templates/";
    public final static int PORTALSIZE=200; //DKOZLOFF+3 06/22
    public final static int PORTALX=GAMEWIDTH/2;
    public final static int PORTALY=GAMEHEIGHT/2;
    public final static int PORTALCOLLISION=4;
    
    //Player Weapons
    public final static int PISTOL=1;
    public final static int MACHINEGUN=2;
    public final static int SHOTGUN=3;
    public final static int ROCKETLAUNCHER=4;
    public final static int FLAMETHROWER=5;
    public final static int LASER=6;
    
    //Weapon Types
    public final static int REGULARBALLISTIC=1; //Simple firearms: pistol, machinegun, etc.
    public final static int SINGLEBULLET=2; //Firearms that only have one projectile: flamethrower, single solid laser
    public final static int AOEBALLISTIC=3; //Radiating projectiles
    
    //Room Types
    public final static int NORMALROOM=0;
    public final static int BOSSROOM=1;
    public final static int STOREROOM=2;
    
    //Number of keys
    public final static int ROOMKEYS=Utilities.countRoomKeys(); //Number of entries in Rooms.xml
    public final static int PICKUPTYPES=2; //Number of types of pickups
    
    //Action Types
    //These represent the various actions that a user can do in the game
    public final static int MOVEMENT=0;
    public final static int SHOOT=1;
    public final static int CHANGEWEAPON=2;
    public final static int CHANGESPECIFICWEAPON=3;
    public final static int GAME=4; //Place holder, things like pausing? 
    
    //Enemy Globals:
    //Enemy Types
    public final static int MELEEENEMY=1;
    public final static int PROJECTILEENEMY=2;
    //Enemy Movement Types
    public final static int STDTOWARDPLAYER=1;
    public final static int STDRANDOM=2;
    
    //Enemy Keys
    public final static int ZOMBIE=1;
    public final static int BAT=2;
    public final static int BLOATER=3;
    public final static int CULTIST=4;
    public final static int SPIKEMONSTER=5;
    
    //Boss Keys
    public final static int BOSSES=2; //Number of bosses
    public final static int BATLORD=0;
    public final static int GHOULIE=1;
    
    //Enemy Weapons 
    public final static int BLOATERBALL=20;
    public final static int FIREBALL=21;
    public final static int SPIKEBALL=22;
    public final static int FIREBAT=23;
    public final static int GHOULIE1=24;
    public final static int GHOULIE2=25;
    public final static int GHOULIE3=26;
}