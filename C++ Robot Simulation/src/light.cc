/**
 * @file light.cc
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "src/light.h"
#include "src/params.h"
#include "src/pose.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Constructors/Destructor
 ******************************************************************************/
Light::Light() :
    motion_handler_(this),
    motion_behavior_(this),
    state_(RUNNING) {
  set_color(LIGHT_COLOR);
  set_pose(LIGHT_POSITION);
  set_radius(LIGHT_RADIUS);
  set_type(kLight);
  motion_handler_.set_velocity(LIGHT_INIT_SPEED, LIGHT_INIT_SPEED);
  motion_handler_.set_max_speed(LIGHT_MAX_SPEED);
  motion_handler_.set_max_angle(LIGHT_MAX_ANGLE);
}

/*******************************************************************************
 * Member Functions
 ******************************************************************************/
void Light::TimestepUpdate(unsigned int dt) {
  avoid_timer_++;
  // Stay in avoidance state for about 2 seconds before going back to
  // running state.
  if (avoid_timer_ > 35) {
    // Slightly adjust the heading angle after leaving avoidance state.
    if (state_ == AVOID) {
      Pose old_pose = get_pose();
      Pose new_pose = Pose(old_pose.x, old_pose.y, old_pose.theta - 45);
      set_pose(new_pose);
    }
    state_ = RUNNING;
    avoid_timer_ = 0;
  }

  // Change Light behavior based on its current state.
  if (state_ == RUNNING) {
    motion_handler_.Run();
  } else if (state_ == AVOID) {
    motion_handler_.Avoid();
  }

  // Use velocity and position to update position
  motion_behavior_.UpdatePose(dt, motion_handler_.get_velocity());
}

void Light::Reset() {
  state_ = RUNNING;
  set_pose(set_position_randomly());
  motion_handler_.set_velocity(LIGHT_INIT_SPEED, LIGHT_INIT_SPEED);
}

void Light::HandleCollision() {
  // Light reverses heading if it collides while still in avoidance state.
  // Since Light is still in avoidance state, it will still arc away
  // from the collision.
  if (state_ == AVOID) {
    Pose old_pose = get_pose();
    Pose new_pose = Pose(old_pose.x, old_pose.y, old_pose.theta + 180);
    set_pose(new_pose);
  }
  avoid_timer_ = 0;
  state_ = AVOID;
}

NAMESPACE_END(csci3081);
