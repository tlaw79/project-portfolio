/**
 * @file robot.cc
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "src/robot.h"
#include "src/params.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Constructors/Destructor
 ******************************************************************************/
Robot::Robot() :
    motion_handler_(this),
    motion_behavior_(this),
    left_light_sensor_(),
    right_light_sensor_(),
    left_food_sensor_(),
    right_food_sensor_(),
    state_(RUNNING),
    strategy_() {
  set_type(kRobot);
  set_color(ROBOT_COLOR);
  set_pose(ROBOT_INIT_POS);
  set_radius(ROBOT_RADIUS);
  motion_handler_.set_velocity(ROBOT_INIT_SPEED, ROBOT_INIT_SPEED);
  motion_handler_.set_max_speed(ROBOT_MAX_SPEED);
  motion_handler_.set_max_angle(ROBOT_MAX_ANGLE);
}

/*******************************************************************************
 * Member Functions
 ******************************************************************************/
void Robot::TimestepUpdate(unsigned int dt) {
  // Notify motion handler about the state of Robot's hunger.
  motion_handler_.set_hunger_state(hungry_);
  motion_handler_.set_really_hungry_state(really_hungry_);

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
    motion_handler_.UpdateVelocity();
  } else if (state_ == AVOID) {
    motion_handler_.Avoid();
  }

  // Use velocity and position to update position
  motion_behavior_.UpdatePose(dt, motion_handler_.get_velocity());
}

void Robot::Reset() {
  set_pose(set_position_randomly());
  set_color(ROBOT_COLOR);
  motion_handler_.set_velocity(ROBOT_INIT_SPEED, ROBOT_INIT_SPEED);
  state_ = RUNNING;
  hunger_timer_ = 0;
  hungry_ = false;
  really_hungry_ = false;
}

void Robot::HandleCollision() {
  // Robot reverses heading if it collides while still in avoidance state.
  // Since Robot is still in avoidance state, it will still arc away
  // from the collision.
  if (state_ == AVOID) {
    Pose old_pose = get_pose();
    Pose new_pose = Pose(old_pose.x, old_pose.y, old_pose.theta + 180);
    set_pose(new_pose);
  }
  avoid_timer_ = 0;
  state_ = AVOID;
}

Pose* Robot::SensorLocation(double angle) {
  double theta = (get_heading()* M_PI / 180.0) + angle;
  double robot_x = pose_.x;
  double robot_y = pose_.y;
  double x = get_radius() * cos(theta) + robot_x;
  double y = get_radius() * sin(theta) + robot_y;
  Pose * pose = new Pose(x, y);
  return pose;
}

NAMESPACE_END(csci3081);
