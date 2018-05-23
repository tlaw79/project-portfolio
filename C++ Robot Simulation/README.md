<center><b><h1>Robot Simulation</h1></b></center>

<center><b><h2>Overview</h2></b></center>

<center><b>Robot Simulation</b></center><br><br>

This software is a 2D graphical simulation of autonomous robots moving in an arena and interacting with two types of stimuli: food and lights. This simulation was programmed in C++ by Tyler Law.  It uses the MinGfx graphics library with base code written by UMN CSCI 3081 staff and Dan Keefe. The movement of robots and lights is modeled using differential drive, meaning all objects move and turn by changing the velocities of their two wheels. Robots also move according to the Braitenberg vehicle model (discussed in further detail in the Braitenberg Vehicles section). Robots and lights back up in an arc for a short period of time after a collision.

<center><b>Graphical User Interface</b></center><br><br>

A graphical user interface allows the user to configure simulation parameters as well as pause and restart the simulation. The arena is reconfigured to reflect the user's configuration whenever the user restarts the simulation. The initial positions of all entities are randomized whenever the user starts or restarts the simulation. More details on the GUI are provided in the User Guide section.

<center><b><h2>Design and Implementation</h2></b></center>

<center><b>Braitenberg Vehicles</b></center><br><br>

In the Braitenberg vehicle model, a robot has two sensors on each side (in this simulation, 40 and -40 degrees from its heading) that detect how close that sensor is to a certain stimuli. As a sensor value gets larger, it can either increase or decrease the velocity of a wheel, and it can affect the velocity of either the left wheel or the right wheel. This results in four simple types of movement which resemble the behaviors fear, aggression, love and explore (this simulation currently only implements the behaviors fear, aggression and explore):

Fear increases wheel velocities when sensor readings increase, where sensors affect the wheel on the same side of the robot as the sensor. Objects in fear will move slowly when far away from stimuli and will quickly move away from stimuli when near stimuli, unless stimuli are directly in front of them, in which case they will speed up and run through them.

Aggression increases wheel velocities when sensor readings increase, where sensors affect the wheel on the opposite side of the robot as the sensor. Aggressive objects will move toward stimuli, speeding up as they get close, and run through them.

Love decreases wheel velocities when sensor readings increase, where sensors affect the wheel on the same side of the robot as the sensor. Objects in love will move toward stimuli and slow down next to them.

Explore decreases wheel velocities when sensor readings increase, where sensors affect the wheel on the opposite side of the robot as the sensor. Exploring objects will move quickly when not around stimuli and will move away from stimuli when near stimuli, unless stimuli are directly in front of them, in which case they will slow down next to them.

Robots can either fear or explore lights. The user can specify the ratio of robots that fear lights to robots that explore lights. Thirty seconds after the simulation starts or restarts, robots become hungry and become aggressive toward food. If a robot comes within five pixels of food, it is no longer hungry and is no longer aggressive toward food, but it will become hungry again after thirty seconds. If a robot has gone two minutes without eating, it will stop fearing or exploring lights and only act aggressively toward food. If a robot has gone two and a half minutes without eating, the simulation will stop and the user will be given the option to restart the simulation.

<center><b>Model-View-Controller</b></center><br><br>

This simulation uses the Model-view-controller paradigm, meaning the code for the data model, its visual representation and user input is separated into their own classes. The controller handles both user input and communication between the model and the view. The model is arena.cc, the view is graphics_arena_viewer.cc and the controller is controller.cc.

<center><b>Factory Method Pattern</b></center><br><br>

The class entity_factory.cc handles the creation of arena objects: robots, lights, food and sensors. The factory returns a pointer to an object in arena.cc, and arena.cc adds the object to the entity vector and the mobile entity vector subset if the object is a mobile entity. These collections are used to access every object in the arena.

<center><b>Observer Pattern</b></center><br><br>

Robots (observers) poll sensors (subjects) every update iteration to receive the latest reading for each of its sensors. Each robot passes that information to its motion_handler_robot.cc, which will update the robot's wheel velocities according to the robot's sensor values and the Braitenberg movement strategies the robot uses (fear, aggression, love, explore). The robot then updates its position and heading, represented as a struct in pose.h, according to its wheel velocities and the differential drive model in motion_behavior_differential.cc.

This implementation of the Observer Pattern minimizes coupling between robots and their sensors since sensors don't need to maintain references to their robot. This is beneficial because future changes to robot code won't require additional changes to sensor code. The trade-off of this implementation is the need to constantly poll for sensor readings, potentially wasting CPU cycles if the sensor readings don't change between update iterations. Since the simulation scale is small and computationally inexpensive, and because robots tend to be moving most of the time anyway, this trade-off was acceptable.

<center><b>Strategy Pattern</b></center><br><br>

The Strategy Pattern is used to allow client code to quickly and easily switch between different movement behaviors for each robot. A movement strategy interface defines method signatures that are implemented by concrete strategy classes, including the fear, aggression and explore movement strategies used by Braitenberg vehicles. These concrete strategy classes calculate wheel velocities based on robot sensor readings and the specified movement strategy.

Using the Strategy Pattern takes advantage of dynamic polymorphism by allowing different concrete strategies to all be treated as the same type of object with the same method signatures, but with different implementations. This allows client code to change a robot's movement strategy during run-time by simply changing which type of strategy object is being referenced. This also facilitates easily adding a new type of strategy, as it only involves creating a new concrete strategy class and changing which concrete strategy object is being referenced in the client code.

<center><b><h2>User Guide</h2></b></center>

<center><b>Linux Guide</b></center><br><br>

1) Download and extract project files.<br>
2) Navigate to the src directory.<br>
3) Enter the following command: make clean<br>
4) Enter the following command: make<br>
5) Enter the following command: ./../build/bin/arenaviewer

<center><b>User Controls</b></center><br><br>

NOTE: You must press the New Game button for your setting changes to take effect.

Play button: Play/Pause the simulation.<br>
New Game button: Restart the simulation.<br>
Number of Robots slider: Number of robots in the simulation.<br>
Number of Lights slider: Number of lights in the simulation.<br>
Number of Food slider: Number of food in the simulation.<br>
Ratio slider: Ratio of robots that fear to robots that explore.<br>
Sensitivity slider: How sensitive robots are to stimuli.<br>
Toggle Food button: Enable or disable food in the simulation.
