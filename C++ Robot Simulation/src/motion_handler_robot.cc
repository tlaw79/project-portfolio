/**
 * @file motion_handler_robot.cc
 *
 * @copyright 2018 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "src/motion_handler_robot.h"
#include "src/motion_behavior_differential.h"
#include "src/light_sensor.h"
#include "src/robot.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Member Functions
 ******************************************************************************/
void MotionHandlerRobot::UpdateVelocity() {
  Robot * robot = dynamic_cast<Robot *>(get_entity());
  LightSensor * left_light_sensor = robot->get_left_light_sensor();
  LightSensor * right_light_sensor = robot->get_right_light_sensor();
  double left_light_reading = left_light_sensor->get_reading();
  double right_light_reading = right_light_sensor->get_reading();

  FoodSensor * left_food_sensor = robot->get_left_food_sensor();
  FoodSensor * right_food_sensor = robot->get_right_food_sensor();
  double left_food_reading = left_food_sensor->get_reading();
  double right_food_reading = right_food_sensor->get_reading();

  // Give Strategy the sensor readings.
  strategy_->set_left_sensor_reading(left_light_reading);
  strategy_->set_right_sensor_reading(right_light_reading);
  hunger_strategy_->set_left_sensor_reading(left_food_reading);
  hunger_strategy_->set_right_sensor_reading(right_food_reading);

  if (!really_hungry_) {
    if (hungry_ == true) {
      // Calculate wheel velocities according to the client-specified
      // movement strategy and the aggressive (hunger) strategy.
      set_velocity(clamp_vel(strategy_->StrategyVelocityLeft()/2 +
                             hunger_strategy_->StrategyVelocityLeft()),
                   clamp_vel(strategy_->StrategyVelocityRight()/2 +
                             hunger_strategy_->StrategyVelocityRight()));
    } else {
      // Calculate wheel velocities according to the client-specified
      // movement strategy only.
      set_velocity(clamp_vel(strategy_->StrategyVelocityLeft()),
                   clamp_vel(strategy_->StrategyVelocityRight()));
    }
  } else {
    set_velocity(clamp_vel(hunger_strategy_->StrategyVelocityLeft()),
                 clamp_vel(hunger_strategy_->StrategyVelocityRight()));
  }
}

// Backing up in an arc.
void MotionHandlerRobot::Avoid() {
  set_velocity(-10.0, -9.3);
}

double MotionHandlerRobot::clamp_vel(double vel) {
  double clamped = 0.0;

  // Don't exceed maximum velocity.
  if (vel >= 0) {
    clamped = (vel > get_max_speed()) ?
              get_max_speed():
              vel;
  // Don't allow negative velocity.
  } else {
    clamped = 0;
  }
  return clamped;
}

NAMESPACE_END(csci3081);
