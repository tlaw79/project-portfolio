/**
 * @file light.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_LIGHT_H_
#define SRC_LIGHT_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <string>
#include "src/arena_mobile_entity.h"
#include "src/common.h"
#include "src/motion_handler_light.h"
#include "src/motion_behavior_differential.h"
#include "src/entity_type.h"
#include "src/pose.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

class MotionBehaviorDifferential;

/*******************************************************************************
 * Class Definitions
 ******************************************************************************/
/**
 * @brief Class representing a mobile Light within the Arena.
 */
class Light : public ArenaMobileEntity {
 public:
  /**
   * @brief Constructor.
   */
  Light();

  /**
   * @brief Reset the Light to a newly constructed state (needed for reset
   * button to work in GUI).
   */
  void Reset() override;

  /**
   * @brief Update the Light's position and velocity after the specified
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
   * @brief Get the name of the Light for visualization purposes, and to
   * aid in debugging.
   */
  std::string get_name() const override {
    return "Light " + std::to_string(get_id());
  }

  MotionHandlerLight get_motion_handler() { return motion_handler_; }
  MotionBehaviorDifferential get_motion_behavior() { return motion_behavior_; }

 private:
  // Manages pose and wheel velocities that change with time and collisions.
  MotionHandlerLight motion_handler_;
  // Calculates changes in pose based on elapsed time and wheel velocities.
  MotionBehaviorDifferential motion_behavior_;
  // Whether the the Light is in avoidance state or moving normally
  int state_;
  // Timer for avoidance state.
  int avoid_timer_{0};
};

NAMESPACE_END(csci3081);

#endif  // SRC_LIGHT_H_
