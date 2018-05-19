/**
 * @file motion_handler_robot.h
 *
 * @copyright 2018 3081 Staff, All rights reserved.
 */

#ifndef SRC_MOTION_HANDLER_ROBOT_H_
#define SRC_MOTION_HANDLER_ROBOT_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <cassert>
#include <iostream>

#include "src/common.h"
#include "src/motion_handler.h"
#include "src/communication.h"
#include "src/strategy_explore.h"
#include "src/strategy_fear.h"
#include "src/strategy_aggressive.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/

/**
 * @brief Class managing a Robot's speed and heading angle based
 * on collisions and user inputs.
 */
class MotionHandlerRobot : public MotionHandler {
 public:
  explicit MotionHandlerRobot(ArenaMobileEntity * ent)
      : MotionHandler(ent),
        strategy_(),
        hunger_strategy_(new StrategyAggressive) {}

  MotionHandlerRobot(const MotionHandlerRobot& other) = default;
  MotionHandlerRobot& operator=(const MotionHandlerRobot& other) = default;

  /**
  * @brief Update the speed and the pose angle according to the sensor readings.
  *
  * Currently does not change speed.
  *
  * @param[in] pose The current pose.
  * @param[in] st A SensorTouch to be read.
  */
  void UpdateVelocity() override;

  /**
   * @brief Robot avoidance behavior on collision.
   */
  void Avoid();

  void set_strategy(StrategyMovement * strategy) { strategy_ = strategy; }

  bool get_hunger_state() { return hungry_; }
  void set_hunger_state(bool state) { hungry_ = state; }

  bool get_really_hungry_state() { return really_hungry_; }
  void set_really_hungry_state(bool state) { really_hungry_ = state; }

 private:
  double clamp_vel(double vel);
  // Movement strategy
  StrategyMovement * strategy_;
  // Aggressive movement strategy
  StrategyAggressive * hunger_strategy_;
  // Hunger state
  bool hungry_{false};
  // Really hungry state
  bool really_hungry_{false};
};

NAMESPACE_END(csci3081);

#endif  // SRC_MOTION_HANDLER_ROBOT_H_
