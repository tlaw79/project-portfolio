/**
 * @file food_sensor.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_FOOD_SENSOR_H_
#define SRC_FOOD_SENSOR_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <string>
#include "src/sensor.h"
#include "src/food.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/
/**
 * @brief Class representing a food sensor.
 */
class FoodSensor : public Sensor {
 public:
  /**
   * @brief Constructor.
   */
  FoodSensor() {
    set_type(kFoodSensor);
    set_color(SENSOR_COLOR);
    set_pose(SENSOR_INIT_POS);
    set_radius(SENSOR_RADIUS);
  }

  /**
   * @brief Get the name of the FoodSensor for visualization and for debugging.
   */
  std::string get_name() const override { return ""; }

  /**
   * @brief Calculate the food sensor reading.
   * Based on python code provided by 3081 Staff.
   */
  void CalculateReading(Food * food) {
    double distance = (pose_ - food->get_pose()) - food->get_radius();
    reading_ += 1200/(pow(1.08, distance/6));
    if (reading_ > 1000) {
      reading_ = 1000;
    }
  }

  int get_side() { return side_; }
  void set_side(int side) { side_ = side; }

 private:
  // Left or right sensor
  int side_{0};
};

NAMESPACE_END(csci3081);

#endif  // SRC_FOOD_SENSOR_H_
