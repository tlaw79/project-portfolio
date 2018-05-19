/**
 * @file light_sensor.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_LIGHT_SENSOR_H_
#define SRC_LIGHT_SENSOR_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <string>
#include "src/sensor.h"
#include "src/light.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/
/**
 * @brief Class representing a light sensor.
 */
class LightSensor : public Sensor {
 public:
  /**
   * @brief Constructor.
   */
  LightSensor() {
    set_type(kLightSensor);
    set_color(SENSOR_COLOR);
    set_pose(SENSOR_INIT_POS);
    set_radius(SENSOR_RADIUS);
  }

  /**
   * @brief Get the name of the LightSensor for visualization and for debugging.
   */
  std::string get_name() const override { return ""; }

  /**
   * @brief Calculate the light sensor reading.
   * Based on python code provided by 3081 Staff.
   */
  void CalculateReading(Light * light) {
    double distance = (pose_ - light->get_pose()) - light->get_radius();
    reading_ += 1200/(pow(1.08, distance/sensitivity_));
    if (reading_ > 1000) {
      reading_ = 1000;
    }
  }

  int get_side() { return side_; }
  void set_side(int side) { side_ = side; }

  void set_sensitivity(int sensitivity) { sensitivity_ = sensitivity; }

 private:
  // Left or right sensor
  int side_{0};
  int sensitivity_{5};
};

NAMESPACE_END(csci3081);

#endif  // SRC_LIGHT_SENSOR_H_
