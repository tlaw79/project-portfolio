/*  
 *  L-system
 *  Tyler Law and UMN CSCI 4611 staff 
 *  2017
 */

import java.util.Stack;

// File name of currently loaded example (rendered on the bottom of the
// screen for your convenience).
String currentFile;

/*****************************************************
 * Place variables for describing the L-System here. *
 * These might include the final expansion of turtle *
 * commands, the step size d, etc.                   *
 *****************************************************/
String[] spec;

// Step size d.  Changes the scale of drawn images.
double d = 4;

// Origin point of the images.
int originX = 150;
int originY = 360;

// LineSegment array saves line coordinates used by the
// line() function when rendering.
ArrayList<LineSegment> lineSegments = new ArrayList<LineSegment>();

/*
 * This method is automatically called whenever a new L-System is
 * loaded (either by pressing 1 - 6, or 'o' to open a file dialog).
 *
 * The lines array will contain every line from the selected 
 * description file. You can assume it's a valid description file,
 * so it will have a length of 6:
 *
 *   lines[0] = number of production rule applications
 *   lines[1] = angle increment in degrees
 *   lines[2] = initial axiom
 *   lines[3] = production rule for F (or the string 'nil')
 *   lines[4] = production rule for X (or the string 'nil')
 *   lines[5] = production rule for Y (or the string 'nil')
 */
 
void processLSystem(String[] lines) {
  // You should write code within this method to process the L-system
  // and produce whatever data structures you'll need to use to
  // draw the L-system when drawLSystem() is called.
  spec = lines;

  final String n_str = lines[0];
  final int n = Integer.parseInt(n_str);
  final String angle_str = lines[1];
  final double angleIncrement = Double.parseDouble(angle_str);
  String fullExpression = lines[2];
  final String fRule = lines[3];
  final String xRule = lines[4];
  final String yRule = lines[5];

  // Angle = 0 corresponds to turtle pointing straight upward.
  double currentAngle = 0;
  double currentX = originX;
  double currentY = originY;
  
  // Stack holds turtle states.
  final Stack stateStack = new Stack();
  
  // Expand axiom according to production rules.
  for (int i = 0; i < n; i++) {
    String axiom = fullExpression;
    fullExpression = "";
    while (axiom.length() > 0) {
      switch (axiom.charAt(0)) {
        case 'F': 
          fullExpression = fullExpression + fRule;
          break;
        case 'X':
          fullExpression = fullExpression + xRule;
          break;
        case 'Y':
          fullExpression = fullExpression + yRule;
          break;
        default:
          fullExpression = fullExpression + axiom.charAt(0);
          break;
      }
      axiom = axiom.substring(1);
    }
  }
  
  // Calculate arraylist of lineSegment objects.
  for (int i = 0; i < 2; i++) {
    while (fullExpression.length() > 0) {
      switch (fullExpression.charAt(0)) {
        case 'F':
          double newX = d * Math.sin(Math.toRadians(currentAngle));
          double newY = d * Math.cos(Math.toRadians(currentAngle));
          LineSegment lineSegment = new LineSegment(currentX, currentY, currentX+newX, currentY-newY);
          lineSegments.add(lineSegment);
          currentX += newX;
          currentY -= newY;
          break;
        case '+':
          currentAngle += angleIncrement;
          break;
        case '-':
          currentAngle -= angleIncrement;
          break;
        case '[':
          TurtleState newState = new TurtleState(currentX, currentY, currentAngle);
          stateStack.push(newState);
          break;
        case ']':
          TurtleState oldState = (TurtleState) stateStack.pop();
          currentX = oldState.getX();
          currentY = oldState.getY();
          currentAngle = oldState.getAngle();
          break;
      }
      fullExpression = fullExpression.substring(1);
    }
  }
}

// TurtleState class contains position and angle of
// the turtle's state.
final public class TurtleState {
  final private double x;
  final private double y;
  final private double angle;
  
  public TurtleState (double x, double y, double angle) {
    this.x = x;
    this.y = y;
    this.angle = angle;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getAngle() {
    return this.angle;
  }
}

// Contains the coordinates of two points, representing a line.
final public class LineSegment {
  final private double xSrc;
  final private double ySrc;
  final private double xDest;
  final private double yDest;

  // Constructor takes in coordinates of two points.
  public LineSegment (double x1, double y1, double x2, double y2) {
    this.xSrc = x1;
    this.ySrc = y1;
    this.xDest = x2;
    this.yDest = y2;
  }

  public double getxSrc() {
    return this.xSrc;
  }

  public double getySrc() {
    return this.ySrc;
  }

  public double getxDest() {
    return this.xDest;
  }

  public double getyDest() {
    return this.yDest;
  }
}

/*
 * This method is called every frame after the background has been
 * cleared to white, but before the current file name is written to
 * the screen.
 *
 * It is not called if there is no loaded file.
 */
void drawLSystem() {
  // Implement your LSystem rendering here.
  for (int i = 0; i < 6; i++) {
    text(spec[i], 10, 20 + 15 * i);
  }
  
  // Render lines using data from lineSegments arraylist.
  for (int i = 0; i < lineSegments.size(); i++) {
    LineSegment seg = lineSegments.get(i);
    line((float) seg.getxSrc(), (float) seg.getySrc(), (float) seg.getxDest(), (float) seg.getyDest());
  }
}

void setup() {
  size(500, 500);
}

void draw() {
  background(0,0,0);
  stroke(0,255,0);
  strokeWeight(1);
  
  if (currentFile != null) {
    drawLSystem();
  }

  fill(0,255,0);
  textSize(15);
  if (currentFile == null) {
    text("Press [1-6] to load an example, or 'o' to open a dialog", 5, 495);
  } else {
    text("Current l-system: " + currentFile, 5, 495);
  }
}

void keyReleased() {
  /*********************************************************
   * The examples loaded by pressing 1 - 6 must be placed  *
   * in the data folder within your sketch directory.      *
   * The same goes for any of your own files you'd like to *
   * load with relative paths.                             *
   *********************************************************/

  if (key == 'o' || key == 'O') {
    // NOTE: This option will not work if you're running the
    // Processing sketch with JavaScript and your browser.
    selectInput("Select a file to load:", "fileSelected");
  } else if (key == '1') {
    loadLSystem("example1.txt");
  } else if (key == '2') {
    loadLSystem("example2.txt");
  } else if (key == '3') {
    loadLSystem("example3.txt");
  } else if (key == '4') {
    loadLSystem("example4.txt");
  } else if (key == '5') {
    loadLSystem("example5.txt");
  } else if (key == '6') {
    loadLSystem("example6.txt");
  } else if (key == '-') {
    if ((currentFile != null) && (d >0)) {
      d -= .5;
      loadLSystem(currentFile);
    }
  } else if (key == '=') {
    if (currentFile != null) {
      d += .5;
      loadLSystem(currentFile);
    }
  } else if (key == 'd') {
    loadLSystem("dragon.txt");
  }
}

import java.io.File;
void fileSelected(File selection) {
  if (selection == null) {
    println("File selection cancelled.");
  } else {
    loadLSystem(selection.getAbsolutePath());
  }
}

void loadLSystem(String filename) {
  lineSegments.clear();
  String[] contents = loadStrings(filename);
  processLSystem(contents);
  currentFile = filename;
}