/**
 * @file params.h
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

#ifndef SRC_PARAMS_H_
#define SRC_PARAMS_H_

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include "src/common.h"

/*******************************************************************************
 * Macros
 ******************************************************************************/
/*
 * @file. Constructors for classes should use reasonable default values as
 * defined here. An exception might be if the value is 0.
 */

// Graphics
#define X_DIM 1024
#define Y_DIM 768
#define TEXT_BOX_WIDTH 50
#define GUI_MENU_WIDTH 180
#define GUI_MENU_GAP 10
#define GUI_BUTTON_WIDTH 130
#define GUI_SLIDER_WIDTH 100

// Arena
#define N_LIGHTS 4
#define N_FOODS 4
#define N_ROBOTS 10
#define ARENA_X_DIM X_DIM
#define ARENA_Y_DIM Y_DIM

// Game status
#define WON 0
#define LOST 1
#define PLAYING 2

// Entity
#define DEFAULT_POSE \
  { 200, 200, 0}
#define DEFAULT_COLOR \
  { 255, 255, 255 }
#define DEFAULT_RADIUS 20

// Mobile entity
#define STARTING_VELOCITY 0.0

// Robot
#define ROBOT_START_LIVES 9
#define ROBOT_ANGLE_DELTA 1
#define ROBOT_SPEED_DELTA 1
#define ROBOT_COLLISION_DELTA 1
#define ROBOT_RADIUS 30
#define ROBOT_MIN_RADIUS 8
#define ROBOT_MAX_RADIUS 14
#define ROBOT_INIT_POS \
  { 550, 420 , 0 }
#define ROBOT_COLOR \
  { 90, 0, 190 }
#define ROBOT_INVINCIBLE_COLOR \
  { 0, 255, 255 }
#define ROBOT_HEADING 270
#define ROBOT_INIT_SPEED 0
#define ROBOT_MAX_SPEED 10
#define ROBOT_MAX_ANGLE 360

// Food
#define FOOD_RADIUS 20
#define FOOD_COLLISION_DELTA 1
#define FOOD_INIT_POS \
  { 400, 400 }
#define FOOD_COLOR \
  { 255, 0, 0 }
#define FOOD_COLOR_CAPTURE \
  { 0, 255, 0 }

// Light
#define LIGHT_POSITION \
  { 700, 400 }
#define LIGHT_RADIUS 40
#define LIGHT_MIN_RADIUS 50
#define LIGHT_MAX_RADIUS 50
#define LIGHT_COLOR \
  { 255, 255, 0 }
#define LIGHT_INIT_SPEED 5
#define LIGHT_MAX_SPEED 10
#define LIGHT_MAX_ANGLE 360

// Movement state
#define AVOID 0
#define RUNNING 1

// Sensor
#define SENSOR_RADIUS 3
#define SENSOR_INIT_POS \
  { 500, 500 , 0 }
#define SENSOR_COLOR \
  { 0, 255, 0 }

// Arena constants
#define LEFT_SENSOR 0
#define RIGHT_SENSOR 1
#define LIGHT 0
#define FOOD 1
#define FEAR 0
#define EXPLORE 1
#define AGGRESSIVE 2

#endif  // SRC_PARAMS_H_
