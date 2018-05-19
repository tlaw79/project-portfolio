/**
 * @file entity_factory.cc
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <string>
#include <ctime>
#include <iostream>

#include "src/common.h"
#include "src/entity_factory.h"
#include "src/entity_type.h"
#include "src/params.h"
#include "src/pose.h"
#include "src/rgb_color.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Class Definitions
 ******************************************************************************/

EntityFactory::EntityFactory() {
  srand(time(nullptr));
}

ArenaEntity* EntityFactory::CreateEntity(EntityType etype) {
  switch (etype) {
    case (kRobot):
      return CreateRobot();
      break;
    case (kLight):
      return CreateLight();
      break;
    case (kFood):
      return CreateFood();
      break;
    case (kLightSensor):
      return CreateLightSensor();
      break;
    case (kFoodSensor):
      return CreateFoodSensor();
      break;
    default:
      std::cout << "FATAL: Bad entity type on creation\n";
      assert(false);
  }
  return nullptr;
}

Robot* EntityFactory::CreateRobot() {
  auto* robot = new Robot;
  robot->set_type(kRobot);
  robot->set_color(ROBOT_COLOR);
  robot->set_pose(SetPoseRandomly());

  int rad = random() % ROBOT_MAX_RADIUS + ROBOT_MIN_RADIUS;
  robot->set_radius(rad);
  ++entity_count_;
  ++robot_count_;
  robot->set_id(robot_count_);
  return robot;
}

Light* EntityFactory::CreateLight() {
  auto* light = new Light;
  light->set_type(kLight);
  light->set_color(LIGHT_COLOR);
  light->set_radius(LIGHT_RADIUS);
  light->set_pose(SetPoseRandomly());
  ++entity_count_;
  ++light_count_;
  light->set_id(light_count_);
  return light;
}

Food* EntityFactory::CreateFood() {
  auto* food = new Food;
  food->set_type(kFood);
  food->set_color(FOOD_COLOR);
  food->set_pose(SetPoseRandomly());
  food->set_radius(FOOD_RADIUS);
  ++entity_count_;
  ++food_count_;
  food->set_id(food_count_);
  return food;
}

LightSensor* EntityFactory::CreateLightSensor() {
  auto* light_sensor = new LightSensor;
  light_sensor->set_type(kLightSensor);
  ++entity_count_;
  ++sensor_count_;
  light_sensor->set_id(sensor_count_);
  return light_sensor;
}

FoodSensor* EntityFactory::CreateFoodSensor() {
  auto* food_sensor = new FoodSensor;
  food_sensor->set_type(kFoodSensor);
  ++entity_count_;
  ++sensor_count_;
  food_sensor->set_id(sensor_count_);
  return food_sensor;
}

Pose EntityFactory::SetPoseRandomly() {
  // Dividing arena into 19x14 grid. Each grid square is 50x50
  return {static_cast<double>((30 + (random() % 19) * 50)),
        static_cast<double>((30 + (random() % 14) * 50))};
}

NAMESPACE_END(csci3081);
