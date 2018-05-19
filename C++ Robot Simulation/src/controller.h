/**
 * @file controller.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_CONTROLLER_H_
#define SRC_CONTROLLER_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <nanogui/nanogui.h>
#include <string>

#include "src/arena.h"
#include "src/common.h"
#include "src/communication.h"
#include "src/graphics_arena_viewer.h"
#include "src/params.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

class GraphicsArenaViewer;

/*******************************************************************************
 * Class Definitions
 ******************************************************************************/
/**
 * @brief Controller that mediates Arena and GraphicsArenaViewer communication.
 *
 * The Controller instantiates the Arena and the GraphicsArenaViewer. The
 * viewer contains the main loop that keeps it live, but at each update, the
 * viewer sends a message to the controller to update its time.
 *
 * Other types of communication between Arena and Viewer include:
 * - keypresses intercepted by the Viewer.
 * - Play/Pause/New Game user input via the Viewer.
 * - Game status from arena to the viewer.
 **/
class Controller {
 public:
  /**
   * @brief Controller's constructor that will create Arena and Viewer.
   */
  Controller();


  /**
   * @brief Run launches the graphics and starts the game.
   */
  void Run();

  /**
   * @brief AdvanceTime is communication from the Viewer to advance the
   * simulation.
   */
  void AdvanceTime(double dt);

  /**
   * @brief Set params of the Arena according to the simulation control
   * GUI.
   * 
   * @param num_robots Number of Robots
   * @param num_lights Number of Lights
   * @param num_food Number of Food
   * @param ratio Ratio of fear Robots to explore Robots
   * @param sensitivity Light sensor sensitivity
   * @param food Toggle On/Off
   */
  void SetParams(int num_robots, int num_lights, int num_food,
                  float ratio, int sensitivity, bool food);

  /**
   * @brief AcceptCommunication from either the viewer or the Arena
   */
  void AcceptCommunication(Communication com);

  /**
  * @brief Converts the communication from one to send to the other.
  *
  * Used primarily for testing purposes to insure communication is being
  * correctly received, interpreted, and relayed.
  */
  Communication ConvertComm(Communication com);

 private:
  double last_dt_{0};
  Arena* arena_{nullptr};
  GraphicsArenaViewer* viewer_{nullptr};
};

NAMESPACE_END(csci3081);

#endif /* SRC_CONTROLLER_H_ */
