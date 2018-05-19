/**
 * @file motion_handler_light.cc
 *
 * @copyright 2018 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "src/motion_handler_light.h"
#include "src/motion_behavior_differential.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Member Functions
 ******************************************************************************/

void MotionHandlerLight::UpdateVelocity() {}

// Moving forward.
void MotionHandlerLight::Run() {
  set_velocity(LIGHT_INIT_SPEED, LIGHT_INIT_SPEED);
}

// Backing up in an arc.
void MotionHandlerLight::Avoid() {
  set_velocity(-10.0, -9.3);
}

NAMESPACE_END(csci3081);
