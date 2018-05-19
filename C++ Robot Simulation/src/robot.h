/**
 * @file robot.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_ROBOT_H_
#define SRC_ROBOT_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <string>
#include "src/arena_mobile_entity.h"
#include "src/common.h"
#include "src/motion_handler_robot.h"
#include "src/motion_behavior_differential.h"
#include "src/entity_type.h"
#include "src/light_sensor.h"
#include "src/food_sensor.h"
#include "src/strategy_explore.h"
#include "src/strategy_fear.h"
#include "src/strategy_aggressive.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

class MotionBehaviorDifferential;

/*******************************************************************************
 * Class Definitions
 ******************************************************************************/
/**
 * @brief Class representing a robot within the arena.
 *
 * Robots are composed of a motion handler, motion behavior, and touch sensor.
 * These classes interact to maintain the pose (position and heading) of the
 * robot. At each time step, the wheel velocities are used to calculate the
 * next pose of the robot. The handler manages the pose and user requests. The
 * behavior calculates the new pose based on wheel velocities.
 *
 * Robots can be controlled through keypress, which modify wheel velocities.
 *
 * The touch sensor is activated when the robot collides with an object.
 * The heading is modified after a collision to move the robot away from the
 * other object.
 */
class Robot : public ArenaMobileEntity {
 public:
  /**
   * @brief Constructor using initialization values from params.h.
   */
  Robot();

  Robot(const Robot& other) = delete;
  Robot& operator=(const Robot& other) = delete;

  /**
   * @brief Reset the Robot to a newly constructed state (needed for reset
   * button to work in GUI).
   */
  void Reset() override;

  /**
   * @brief Update the Robot's position and velocity after the specified
   * duration has passed.
   *
   * @param dt The # of timesteps that have elapsed since the last update.
   */
  void TimestepUpdate(unsigned int dt) override;

  /**
   * @brief Handles the collision by setting the sensor to activated.
   */
  void HandleCollision();

  /**
   * @brief Get the name of the Robot for visualization and for debugging.
   */
  std::string get_name() const override { return ""; }

  /**
  * @brief Gives the location of a sensor based on the Robot's heading,
  * position and the angle offset from the Robot's heading.
  * Based on python code provided by 3081 Staff.
  * 
  * @param angle The angle offset from the Robot's heading.
  * 
  * @return Pose to be used by the sensor.
  */
  Pose* SensorLocation(double angle);

  /**
  * @brief Set the Robot's left light sensor.
  */
  void AttachLeftLightSensor(LightSensor * light_sensor) {
    left_light_sensor_ = light_sensor;
  }

  /**
  * @brief Set the Robot's right light sensor.
  */
  void AttachRightLightSensor(LightSensor * light_sensor) {
    right_light_sensor_ = light_sensor;
  }

  /**
  * @brief Set the Robot's left food sensor.
  */
  void AttachLeftFoodSensor(FoodSensor * food_sensor) {
    left_food_sensor_ = food_sensor;
  }

  /**
  * @brief Set the Robot's right food sensor.
  */
  void AttachRightFoodSensor(FoodSensor * food_sensor) {
    right_food_sensor_ = food_sensor;
  }

  MotionHandlerRobot get_motion_handler() { return motion_handler_; }
  MotionBehaviorDifferential get_motion_behavior() { return motion_behavior_; }

  int get_hunger_timer() { return hunger_timer_; }
  void set_hunger_timer(int time) { hunger_timer_ = time; }

  LightSensor* get_left_light_sensor() { return left_light_sensor_; }
  LightSensor* get_right_light_sensor() { return right_light_sensor_; }

  FoodSensor* get_left_food_sensor() { return left_food_sensor_; }
  FoodSensor* get_right_food_sensor() { return right_food_sensor_; }

  bool get_hunger_state() { return hungry_; }
  void set_hunger_state(bool state) { hungry_ = state; }

  bool get_really_hungry_state() { return really_hungry_; }
  void set_really_hungry_state(bool state) { really_hungry_ = state; }

  void set_strategy(StrategyMovement * strategy) {
    strategy_ = strategy;
    motion_handler_.set_strategy(strategy);
  }

 private:
  // Manages pose and wheel velocities that change with time and collisions.
  MotionHandlerRobot motion_handler_;
  // Calculates changes in pose based on elapsed time and wheel velocities.
  MotionBehaviorDifferential motion_behavior_;
  // Left light sensor.
  LightSensor * left_light_sensor_;
  // Right light sensor.
  LightSensor * right_light_sensor_;
  // Left food sensor.
  FoodSensor * left_food_sensor_;
  // Right food sensor.
  FoodSensor * right_food_sensor_;
  // Whether the the Light is in avoidance state or moving normally
  int state_;
  // Timer for avoidance state.
  int avoid_timer_{0};
  // Movement strategy
  StrategyMovement * strategy_;
  // Hunger state
  bool hungry_{false};
  // Really hungry state
  bool really_hungry_{false};
  // Hunger timer
  int hunger_timer_{0};
};

NAMESPACE_END(csci3081);

#endif  // SRC_ROBOT_H_
