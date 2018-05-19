/**
 * @file arena.cc
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <algorithm>
#include <iostream>

#include "src/arena.h"
#include "src/arena_params.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Constructors/Destructor
 ******************************************************************************/
Arena::Arena(const struct arena_params *const params)
    : x_dim_(params->x_dim),
      y_dim_(params->y_dim),
      factory_(new EntityFactory),
      entities_(),
      mobile_entities_(),
      game_status_(PLAYING),
      paused_status_(true) {
  AddRobot(5, FEAR);
  AddRobot(5, EXPLORE);
  AddEntity(kFood, params->n_foods);
  AddEntity(kLight, params->n_lights);
  UpdateSensorPositions();
}

Arena::~Arena() {
  for (auto ent : entities_) {
    delete ent;
  }
}

/*******************************************************************************
 * Member Functions
 ******************************************************************************/
void Arena::AddRobot(int quantity, int movement_type) {
  StrategyMovement * strategy = new StrategyMovement();
  if (movement_type == EXPLORE) {
    strategy = new StrategyExplore();
  } else if (movement_type == FEAR) {
    strategy = new StrategyFear();
  } else if (movement_type == AGGRESSIVE) {
    strategy = new StrategyAggressive();
  }
  // Robots belong to the entity set and the mobile subset of entities.
  for (int i = 0; i < quantity; i++) {
    Robot * robot = dynamic_cast<Robot *>(factory_->CreateEntity(kRobot));
    robot->set_strategy(strategy);
    entities_.push_back(robot);
    mobile_entities_.push_back(robot);
    AddSensor(robot, LEFT_SENSOR, LIGHT);
    AddSensor(robot, RIGHT_SENSOR, LIGHT);
    AddSensor(robot, LEFT_SENSOR, FOOD);
    AddSensor(robot, RIGHT_SENSOR, FOOD);
  }
}

void Arena::AddEntity(EntityType type, int quantity) {
  for (int i = 0; i < quantity; i++) {
    // Lights belong to the entity set and the mobile subset of entities.
    if (type == kLight) {
      Light * light =
        dynamic_cast<Light *>(factory_->CreateEntity(kLight));
      entities_.push_back(light);
      mobile_entities_.push_back(light);
    // Foods belong to the entity set only.
    } else if (type == kFood) {
      Food * food = dynamic_cast<Food *>(factory_->CreateEntity(kFood));
      entities_.push_back(food);
    }
  }
}

void Arena::AddSensor(Robot * robot, int side, int type) {
  if (type == LIGHT) {
    LightSensor * light_sensor =
      dynamic_cast<LightSensor *>(factory_->CreateEntity(kLightSensor));
    light_sensor->set_side(side);
    light_sensor->set_sensitivity(sensitivity_);
    if (side == LEFT_SENSOR) {
      robot->AttachLeftLightSensor(light_sensor);
    } else if (side == RIGHT_SENSOR) {
      robot->AttachRightLightSensor(light_sensor);
    }
    entities_.push_back(light_sensor);
    mobile_entities_.push_back(light_sensor);
  } else if (type == FOOD) {
    FoodSensor * food_sensor =
      dynamic_cast<FoodSensor *>(factory_->CreateEntity(kFoodSensor));
    food_sensor->set_side(side);
    if (side == LEFT_SENSOR) {
      robot->AttachLeftFoodSensor(food_sensor);
    } else if (side == RIGHT_SENSOR) {
      robot->AttachRightFoodSensor(food_sensor);
    }
    entities_.push_back(food_sensor);
    mobile_entities_.push_back(food_sensor);
  }
}

void Arena::UpdateHungerTimers() {
  for (auto ent : mobile_entities_) {
    if (ent->get_type() == kRobot) {
      Robot * robot = dynamic_cast<Robot *>(ent);
      robot->set_hunger_timer(robot->get_hunger_timer() + 1);
      // Robot becomes hungry
      if (robot->get_hunger_timer() > 475) {
        robot->set_hunger_state(true);
      }
      // Robot becomes really hungry
      if (robot->get_hunger_timer() > 1900) {
        robot->set_really_hungry_state(true);
      }
      // Simulation ends if a Robot starves
      if (robot->get_hunger_timer() > 2375) {
        set_game_status(LOST);
      }
    }
  }
}

void Arena::HandleCollisions() {
  /* Determine if any mobile entity is colliding with wall.
   * Adjust the position accordingly so it doesn't overlap.
   */
  for (auto &ent1 : mobile_entities_) {
    EntityType wall = GetCollisionWall(ent1);
    // ArenaMobileEntity and wall collision
    // Sensors don't collide with walls
    if ((kUndefined != wall) && (ent1->get_type() != kLightSensor)
      && (ent1->get_type() != kFoodSensor)) {
      AdjustWallOverlap(ent1, wall);
      // Robot and wall collision
      if (ent1->get_type() == kRobot) {
        dynamic_cast<Robot *>(ent1)->HandleCollision();
      // Light and wall collision
      } else if (ent1->get_type() == kLight) {
        dynamic_cast<Light *>(ent1)->HandleCollision();
      }
    }
    /* Determine if that mobile entity is colliding with any other entity.
    *  Adjust the position accordingly so they don't overlap.
    */
    for (auto &ent2 : entities_) {
      if (ent2 == ent1) { continue; }
      // If a Robot is within 5 pixels of Food, it is no longer hungry.
      if ((ent1->get_type() == kRobot) && (ent2->get_type() == kFood)) {
        if ((ent1->get_pose() - ent2->get_pose() - ent1->get_radius()
           - ent2->get_radius()) <= 5) {
          Robot * robot = dynamic_cast<Robot *>(ent1);
          robot->set_hunger_state(false);
          robot->set_really_hungry_state(false);
          robot->set_hunger_timer(0);
        }
      }
      // ArenaMobileEntity and ArenaEntity collision
      // Sensors and food don't collide with any entity
      if (IsColliding(ent1, ent2)
      && (ent1->get_type() != kLightSensor)
      && (ent2->get_type() != kLightSensor)
      && (ent1->get_type() != kFoodSensor)
      && (ent2->get_type() != kFoodSensor)
      && (ent1->get_type() != kFood)
      && (ent2->get_type() != kFood)) {
        // Robots don't collide with lights
        if (!(((ent1->get_type() == kRobot) && (ent2->get_type() == kLight)) ||
              ((ent1->get_type() == kLight) && (ent2->get_type() == kRobot)))) {
          AdjustEntityOverlap(ent1, ent2);
          // Robot and ArenaEntity collision
          if (ent1->get_type() == kRobot) {
            dynamic_cast<Robot *>(ent1)->HandleCollision();
          // Light and ArenaEntity collision
          } else if (ent1->get_type() == kLight) {
            dynamic_cast<Light *>(ent1)->HandleCollision();
          }
        }
      }
    }
  }
}

void Arena::UpdateSensorPositions() {
  for (auto ent : mobile_entities_) {
    if (ent->get_type() == kRobot) {
      Robot * robot = dynamic_cast<Robot *>(ent);
      LightSensor * left_light_sensor = robot->get_left_light_sensor();
      LightSensor * right_light_sensor = robot->get_right_light_sensor();
      FoodSensor * left_food_sensor = robot->get_left_food_sensor();
      FoodSensor * right_food_sensor = robot->get_right_food_sensor();
      Pose * right_pose = robot->SensorLocation(40 * M_PI / 180);
      Pose * left_pose = robot->SensorLocation(-40 * M_PI / 180);

      // Set position of right sensors
      right_light_sensor->set_pose(*right_pose);
      right_food_sensor->set_pose(*right_pose);
      // Set position of left sensors
      left_light_sensor->set_pose(*left_pose);
      left_food_sensor->set_pose(*left_pose);
    }
  }
}

void Arena::UpdateSensorReadings() {
  for (auto ent : entities_) {
    if (ent->get_type() == kLightSensor) {
      dynamic_cast<LightSensor *>(ent)->ZeroReading();
    } else if (ent->get_type() == kFoodSensor) {
      dynamic_cast<FoodSensor *>(ent)->ZeroReading();
    }
    for (auto ent2 : entities_) {
      if ((ent->get_type() == kLightSensor) && (ent2->get_type() == kLight)) {
        LightSensor * light_sensor = dynamic_cast<LightSensor *>(ent);
        Light * light = dynamic_cast<Light *>(ent2);
        light_sensor->CalculateReading(light);
      } else if ((ent->get_type() == kFoodSensor)
              && (ent2->get_type() == kFood)) {
        FoodSensor * food_sensor = dynamic_cast<FoodSensor *>(ent);
        Food * food = dynamic_cast<Food *>(ent2);
        food_sensor->CalculateReading(food);
      }
    }
  }
}

// The primary driver of simulation movement. Called from the Controller
// but originated from the graphics viewer.
void Arena::AdvanceTime(double dt) {
  if (!(dt > 0)) {
    return;
  }
  for (size_t i = 0; i < 1; ++i) {
    UpdateEntitiesTimestep();
  }
}

void Arena::UpdateEntitiesTimestep() {
  /*
   * Update the position of all entities, according to their current
   * velocities.
   */
  for (auto ent : entities_) {
    ent->TimestepUpdate(1);
  }
  UpdateHungerTimers();
  HandleCollisions();
  UpdateSensorPositions();
  UpdateSensorReadings();
}

void Arena::Reset() {
  // Delete all entities and create new entities based on the simulation
  // control GUI.
  mobile_entities_.clear();
  entities_.clear();

  // If the number of Robots is odd and the ratio is 0.5, then add an
  // additional Robot so that the number of Robots slider accurately
  // reflects the number of Robots in the Arena.
  if ((num_robots_ % 2) && (ratio_ > 0.4) && (ratio_ < 0.6)) {
    AddRobot(num_robots_*ratio_+1, FEAR);
  } else {
    AddRobot(num_robots_*ratio_, FEAR);
  }
  AddRobot(num_robots_*(1.0-ratio_), EXPLORE);

  // Add Food to Arena if Food Button is set to true
  if (food_) {
    AddEntity(kFood, num_food_);
  }
  AddEntity(kLight, num_lights_);

  for (auto ent : entities_) {
    ent->Reset();
  }
  set_game_status(PLAYING);
  set_paused_status(true);
  UpdateSensorPositions();
}

// Determine if the entity is colliding with a wall.
// Always returns an entity type. If not collision, returns kUndefined.
EntityType Arena::GetCollisionWall(ArenaMobileEntity *const ent) {
  if (ent->get_pose().x + ent->get_radius() >= x_dim_) {
    return kRightWall;  // at x = x_dim_
  } else if (ent->get_pose().x - ent->get_radius() <= 0) {
    return kLeftWall;  // at x = 0
  } else if (ent->get_pose().y + ent->get_radius() >= y_dim_) {
    return kBottomWall;  // at y = y_dim
  } else if (ent->get_pose().y - ent->get_radius() <= 0) {
    return kTopWall;  // at y = 0
  } else {
    return kUndefined;
  }
}

/* The entity type indicates which wall the entity is colliding with.
* This determines which way to move the entity to set it slightly off the wall. */
void Arena::AdjustWallOverlap(ArenaMobileEntity *const ent, EntityType object) {
  Pose entity_pos = ent->get_pose();
  switch (object) {
    case (kRightWall):  // at x = x_dim_
    ent->set_position(x_dim_-(ent->get_radius()+5), entity_pos.y);
    break;
    case (kLeftWall):  // at x = 0
    ent->set_position(ent->get_radius()+5, entity_pos.y);
    break;
    case (kTopWall):  // at y = 0
    ent->set_position(entity_pos.x, ent->get_radius()+5);
    break;
    case (kBottomWall):  // at y = y_dim_
    ent->set_position(entity_pos.x, y_dim_-(ent->get_radius()+5));
    break;
    default:
    {}
  }
}

/* Calculates the distance between the center points to determine overlap */
bool Arena::IsColliding(
  ArenaMobileEntity * const mobile_e,
  ArenaEntity * const other_e) {
    double delta_x = other_e->get_pose().x - mobile_e->get_pose().x;
    double delta_y = other_e->get_pose().y - mobile_e->get_pose().y;
    double distance_between = sqrt(delta_x*delta_x + delta_y*delta_y);
    return
    (distance_between <= (mobile_e->get_radius() + other_e->get_radius()));
}

/* This is called when it is known that the two entities overlap.
* We determine by how much they overlap then move the mobile entity to
* the edge of the other
*/
/* @TODO: Add functionality to Pose to determine the distance distance_between two instances (e.g. overload operator -)
*/
/* @BUG: The robot will pass through the home food on occasion. The problem
 * is likely due to the adjustment being in the wrong direction. This could
 * be because the cos/sin generate the wrong sign of the distance_to_move
 * when the collision is in a specific quadrant relative to the center of the
 * colliding entities..
 */
void Arena::AdjustEntityOverlap(ArenaMobileEntity * const mobile_e,
  ArenaEntity *const other_e) {
    double delta_x = mobile_e->get_pose().x - other_e->get_pose().x;
    double delta_y = mobile_e->get_pose().y - other_e->get_pose().y;
    double distance_between = sqrt(delta_x*delta_x + delta_y*delta_y);
    double distance_to_move =
      mobile_e->get_radius() + other_e->get_radius() - distance_between + 1;
    double angle = atan2(delta_y, delta_x);
    mobile_e->set_position(
      mobile_e->get_pose().x+cos(angle)*distance_to_move,
      mobile_e->get_pose().y+sin(angle)*distance_to_move);
}

// Accept communication from the controller. Dispatching as appropriate.
/** @TODO: Call the appropriate Robot functions to implement user input
  * for controlling the robot.
  */
void Arena::AcceptCommand(Communication com) {
  switch (com) {
    case(kPlay):
      paused_status_ = false;
      break;
    case(kPause):
      paused_status_ = true;
      break;
    // Reset every entity for a new game.
    case(kReset):
      Reset();
      break;
    case(kNone):
    default: break;
  }
}

NAMESPACE_END(csci3081);
