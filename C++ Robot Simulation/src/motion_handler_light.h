/**
 * @file motion_handler_light.h
 *
 * @copyright 2018 3081 Staff, All rights reserved.
 */

#ifndef SRC_MOTION_HANDLER_LIGHT_H_
#define SRC_MOTION_HANDLER_LIGHT_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <cassert>
#include <iostream>

#include "src/common.h"
#include "src/motion_handler.h"
#include "src/communication.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Classes
 ******************************************************************************/

/**
 * @brief Class managing an Light's speed and heading angle based
 * on collisions and automated behavior.
 */
class MotionHandlerLight : public MotionHandler {
 public:
  explicit MotionHandlerLight(ArenaMobileEntity * ent)
      : MotionHandler(ent) {
          set_velocity(1.0, 1.0);
        }

  MotionHandlerLight(const MotionHandlerLight& other) = default;
  MotionHandlerLight& operator=
    (const MotionHandlerLight& other) = default;

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
   * @brief Normal Light running behavior.
   */
  void Run();

  /**
   * @brief Light avoidance behavior on collision.
   */
  void Avoid();
};

NAMESPACE_END(csci3081);

#endif  // SRC_MOTION_HANDLER_LIGHT_H_
