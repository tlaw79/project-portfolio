/**
 * @file strategy_explore.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_STRATEGY_EXPLORE_H_
#define SRC_STRATEGY_EXPLORE_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "src/strategy_movement.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/
/**
 * @brief Class representing the explore movement strategy,
 * according to the Braitenberg vehicle model.
 */
class StrategyExplore : public StrategyMovement {
 public:
  /**
   * @brief Constructor.
   */
  StrategyExplore() {}

  /**
   * @brief Default destructor -- as defined by compiler.
   */
  virtual ~StrategyExplore() = default;

  /**
   * @brief Calculate the left wheel velocity.
   * 
   * @return The new wheel velocity.
   * 
   */
  double StrategyVelocityLeft() const override {
    return 10-right_sensor_reading_/100;
  }

  /**
   * @brief Calculate the right wheel velocity.
   * 
   * @return The new wheel velocity.
   * 
   */
  double StrategyVelocityRight() const override {
    return 10-left_sensor_reading_/100;
  }
};

NAMESPACE_END(csci3081);

#endif  // SRC_STRATEGY_EXPLORE_H_
