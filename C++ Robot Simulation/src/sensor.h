/**
 * @file sensor.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_SENSOR_H_
#define SRC_SENSOR_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <string>
#include "src/arena_mobile_entity.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/
/**
 * @brief A base class from which all Sensors inherit.
 * 
 * Sensors are used to detect stimuli like Light and Food.
 *
 */
class Sensor : public ArenaMobileEntity {
 public:
  /**
   * @brief Constructor.
   */
  Sensor() {}

  /**
   * @brief Default destructor -- as defined by compiler.
   */
  virtual ~Sensor() = default;

  /**
   * @brief Reset the LightSensor to a newly constructed state (needed for reset
   * button to work in GUI).
   */
  void Reset() override { ZeroReading(); }

  /**
   * @brief Get the name of the Sensor for visualization and for debugging.
   */
  std::string get_name() const override { return ""; }

  /**
   * @brief Set reading to zero.
   */
  void ZeroReading() { reading_ = 0; }

  double get_reading() { return reading_; }

 protected:
  double reading_{0};
};

NAMESPACE_END(csci3081);

#endif  // SRC_SENSOR_H_
