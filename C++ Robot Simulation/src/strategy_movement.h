/**
 * @file strategy_movement.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_STRATEGY_MOVEMENT_H_
#define SRC_STRATEGY_MOVEMENT_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/
/**
 * @brief A food class representing a movement strategy according to the
 * Braitenberg vehicle model.
 * 
 * Movement strategy subclasses include:
 * 
 * StrategyFear
 * StrategyExplore
 * StrategyAggressive
 * 
 */
class StrategyMovement {
 public:
  /**
   * @brief Constructor.
   */
  StrategyMovement() {}

  /**
   * @brief Default destructor -- as defined by compiler.
   */
  virtual ~StrategyMovement() = default;

  /**
   * @brief Calculate the left wheel velocity according to the movement
   * strategy subclass that inherits from this class.
   * 
   * @return The new wheel velocity.
   * 
   */
  virtual double StrategyVelocityLeft() const { return 0; }

  /**
   * @brief Calculate the right wheel velocity according to the movement
   * strategy subclass that inherits from this class.
   * 
   * @return The new wheel velocity.
   * 
   */
  virtual double StrategyVelocityRight() const { return 0; }

  void set_left_sensor_reading(double reading) {
    left_sensor_reading_ = reading;
  }

  void set_right_sensor_reading(double reading) {
    right_sensor_reading_ = reading;
  }

 protected:
  double left_sensor_reading_{};
  double right_sensor_reading_{};
};

NAMESPACE_END(csci3081);

#endif  // SRC_STRATEGY_MOVEMENT_H_
