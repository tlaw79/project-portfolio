/**
 * @file graphics_arena_viewer.cc
 *
 * @copyright 2017 3081 Staff, All rights reserved.
 */

/*******************************************************************************
 * Includes
 ******************************************************************************/
#include <vector>
#include <iostream>

#include "src/graphics_arena_viewer.h"
#include "src/arena_params.h"
#include "src/rgb_color.h"

/*******************************************************************************
 * Namespaces
 ******************************************************************************/
NAMESPACE_BEGIN(csci3081);

/*******************************************************************************
 * Constructors/Destructor
 ******************************************************************************/
GraphicsArenaViewer::GraphicsArenaViewer(
    const struct arena_params *const params,
    Arena * arena, Controller * controller) :
    GraphicsApp(
        params->x_dim + GUI_MENU_WIDTH + GUI_MENU_GAP * 2,
        params->y_dim,
        "Robot Simulation"),
    controller_(controller),
    arena_(arena) {
  auto *gui = new nanogui::FormHelper(screen());

  // Game status window (Win/loss, new game button)
  status_window_ =
        gui->addWindow(
          Eigen::Vector2i(500, 350),
          "Playing!");
  status_window_->setVisible(false);

  // Temporary new game button appears after win/loss
  new_game_button_ =
    gui->addButton(
      "New Game",
      std::bind(&GraphicsArenaViewer::OnNewGameBtnPressed, this));

  // Menu window
  nanogui::ref<nanogui::Window> window =
      gui->addWindow(
          Eigen::Vector2i(10 + GUI_MENU_GAP, 10),
          "Menu");

  window->setLayout(new nanogui::GroupLayout());

  // Simulation controls (Play/Pause and New Game buttons)
  gui->addGroup("Simulation Control");

  // Play/Pause button
  playing_button_ =
    gui->addButton(
      "Play",
      std::bind(&GraphicsArenaViewer::OnPlayingBtnPressed, this));

  playing_button_->setFixedWidth(GUI_BUTTON_WIDTH);

  // Permanent New Game button in menu window
  new_game_button_ =
    gui->addButton(
      "New Game",
      std::bind(&GraphicsArenaViewer::OnNewGameBtnPressed, this));

  new_game_button_->setFixedWidth(GUI_BUTTON_WIDTH);

  // Sliders
  gui->addGroup("Arena Configuration");

  nanogui::Widget *panel = new nanogui::Widget(window);

  // Number of Robots slider
  new nanogui::Label(panel, "Number of Robots", "sans-bold");
  nanogui::Slider *slider = new nanogui::Slider(panel);
  slider->setValue(1.0f);
  slider->setFixedWidth(GUI_SLIDER_WIDTH);

  // Text box displays value of the slider
  nanogui::TextBox *textBox = new nanogui::TextBox(panel);
  textBox->setFixedSize(nanogui::Vector2i(60, 25));
  textBox->setFontSize(20);
  textBox->setValue("10");

  // Slider callback function when user is moving slider.
  // Slider value is multiplied by 10 since the default
  // slider values range between [0, 1]
  slider->setCallback(
    [textBox](float value) {
      textBox->setValue(std::to_string(static_cast<int>(value*10)));
    });

  // Slider callback function when user stops moving slider.
  // robot_count_ will be given to Arena so it can add/remove
  // robots according to the slider value.
  slider->setFinalCallback(
    [&](float value) {
      robot_count_ = static_cast<int>(value*10);
    });

  // Number of Lights slider
  new nanogui::Label(panel, "Number of Lights", "sans-bold");
  nanogui::Slider *slider2 = new nanogui::Slider(panel);
  slider2->setValue(0.4f);
  slider2->setFixedWidth(GUI_SLIDER_WIDTH);

  // Text box displays value of the slider
  nanogui::TextBox *textBox2 = new nanogui::TextBox(panel);
  textBox2->setFixedSize(nanogui::Vector2i(60, 25));
  textBox2->setFontSize(20);
  textBox2->setValue("4");

  // Slider callback function when user is moving slider.
  // Slider value is multiplied by 10 since the default
  // slider values range between [0, 1]
  slider2->setCallback(
    [textBox2](float value) {
      textBox2->setValue(std::to_string(static_cast<int>(value*10)));
    });

  // Slider callback function when user stops moving slider.
  // light_count_ will be given to Arena so it can add/remove
  // robots according to the slider value.
  slider2->setFinalCallback(
    [&](float value) {
      light_count_ = static_cast<int>(value*10);
    });

  // Number of Food slider
  new nanogui::Label(panel, "Number of Food", "sans-bold");
  nanogui::Slider *slider3 = new nanogui::Slider(panel);
  slider3->setValue(0.4f);
  slider3->setFixedWidth(GUI_SLIDER_WIDTH);

  // Text box displays value of the slider
  nanogui::TextBox *textBox3 = new nanogui::TextBox(panel);
  textBox3->setFixedSize(nanogui::Vector2i(60, 25));
  textBox3->setFontSize(20);
  textBox3->setValue("4");

  // Slider callback function when user is moving slider.
  // Slider value is multiplied by 10 since the default
  // slider values range between [0, 1]
  slider3->setCallback(
    [textBox3](float value) {
      textBox3->setValue(std::to_string(static_cast<int>(value*10)));
    });

  // Slider callback function when user stops moving slider.
  // food_count_ will be given to Arena so it can add/remove
  // robots according to the slider value.
  slider3->setFinalCallback(
    [&](float value) {
      food_count_ = static_cast<int>(value*10);
    });

  // Ratio slider
  new nanogui::Label(panel, "Ratio", "sans-bold");
  nanogui::Slider *slider4 = new nanogui::Slider(panel);
  slider4->setValue(0.5f);
  slider4->setFixedWidth(GUI_SLIDER_WIDTH);

  // Text box displays value of the slider
  nanogui::TextBox *textBox4 = new nanogui::TextBox(panel);
  textBox4->setFixedSize(nanogui::Vector2i(60, 25));
  textBox4->setFontSize(20);
  textBox4->setValue("0.5");

  // Slider callback function when user is moving slider.
  slider4->setCallback(
    [textBox4](float value) {
      if ((value < 1.0) && (value > 0.0)) {
        textBox4->setValue(std::to_string(0.5f).substr(0, 3));
      } else {
        textBox4->setValue(std::to_string(static_cast<int>(value)));
      }
    });

  // Slider callback function when user stops moving slider.
  // ratio_ will be given to Arena so it can add/remove
  // robots according to the slider value.
  slider4->setFinalCallback(
    [&](float value) {
      if ((value < 1.0) && (value > 0.0)) {
        ratio_ = 0.5;
      } else {
        ratio_ = value;
      }
    });

  // Sensitivity slider
  new nanogui::Label(panel, "Sensitivity", "sans-bold");
  nanogui::Slider *slider5 = new nanogui::Slider(panel);
  slider5->setValue(0.5f);
  slider5->setFixedWidth(GUI_SLIDER_WIDTH);

  // Text box displays value of the slider
  nanogui::TextBox *textBox5 = new nanogui::TextBox(panel);
  textBox5->setFixedSize(nanogui::Vector2i(60, 25));
  textBox5->setFontSize(20);
  textBox5->setValue("5");

  // Slider callback function when user is moving slider.
  // Slider value is multiplied by 10 since the default
  // slider values range between [0, 1]
  slider5->setCallback(
    [textBox5](float value) {
      textBox5->setValue(std::to_string(static_cast<int>(value*10)));
    });

  // Slider callback function when user stops moving slider.
  // sensitivity_ will be given to Arena so it can add/remove
  // robots according to the slider value.
  slider5->setFinalCallback(
    [&](float value) {
      sensitivity_ = static_cast<int>(value*10);
    });

  // Toggle Food button
  toggle_food_button_ =
    gui->addButton(
      "Toggle Food Off",
      std::bind(&GraphicsArenaViewer::OnToggleFoodBtnPressed, this));

  toggle_food_button_->setFixedWidth(GUI_BUTTON_WIDTH);

  // Panel component spacing
  panel->setLayout(new nanogui::BoxLayout(nanogui::Orientation::Vertical,
                   nanogui::Alignment::Middle, 0, 15));

  // Set the top level window size (Not the arena boundary box size)
  screen()->setSize({X_DIM, Y_DIM});
  screen()->performLayout();
}

/*******************************************************************************
 * Member Functions
 ******************************************************************************/

// This is the primary driver for state change in the arena.
// It will be called at each iteration of nanogui::mainloop()
void GraphicsArenaViewer::UpdateSimulation(double dt) {
  // Stop updating arena when game is paused, lost or won.
  if ((arena_->get_game_status() == PLAYING) &&
      (!arena_->get_paused_status())) {
    controller_->AdvanceTime(dt);
  }
}

/*******************************************************************************
 * Handlers for User Keyboard and Mouse Events
 ******************************************************************************/
void GraphicsArenaViewer::OnToggleFoodBtnPressed() {
  food_ = !food_;
  if (food_) {
    toggle_food_button_->setCaption("Toggle Food Off");
  } else {
    toggle_food_button_->setCaption("Toggle Food On");
  }
}

void GraphicsArenaViewer::OnNewGameBtnPressed() {
  // Controller handles new game button behavior.
  controller_->SetParams(robot_count_, light_count_, food_count_,
                         ratio_, sensitivity_, food_);
  controller_->AcceptCommunication(kNewGame);

  status_window_->setVisible(false);
  playing_button_->setCaption("Play");
  paused_ = true;
}

void GraphicsArenaViewer::OnPlayingBtnPressed() {
  // Controller handles pause button behavior.
  if (!paused_) {
    controller_->AcceptCommunication(kPause);
    playing_button_->setCaption("Play");
    paused_ = true;
  } else {
    controller_->AcceptCommunication(kPlay);
    playing_button_->setCaption("Pause");
    paused_ = false;
  }
}

/** OnSpecialKeyDown is called when the user presses down on one of the
  * special keys (e.g. the arrow keys).
  */
/**
 * @TODO: Check for arrow key presses using GLFW macros, then
 * convert to appropriate enum Communication and relay to controller
 */
void GraphicsArenaViewer::OnSpecialKeyDown(int key,
  __unused int scancode, __unused int modifiers) {
  Communication key_value = kNone;
  // Arrow key behavior is disabled while paused.
  if (!arena_->get_paused_status()) {
    switch (key) {
      case GLFW_KEY_UP:
        key_value = kKeyUp;
          break;
      case GLFW_KEY_DOWN:
        key_value = kKeyDown;
          break;
      case GLFW_KEY_LEFT:
        key_value = kKeyLeft;
          break;
      case GLFW_KEY_RIGHT:
        key_value = kKeyRight;
          break;
      default: {}
    }
    controller_->AcceptCommunication(key_value);
  }
}

/*******************************************************************************
 * Drawing of Entities in Arena
 ******************************************************************************/
void GraphicsArenaViewer::DrawRobot(NVGcontext *ctx,
                                     const Robot *const robot) {
  // translate and rotate all graphics calls that follow so that they are
  // centered, at the position and heading of this robot
  nvgSave(ctx);
  nvgTranslate(ctx,
               static_cast<float>(robot->get_pose().x),
               static_cast<float>(robot->get_pose().y));
  nvgRotate(ctx,
            static_cast<float>(robot->get_pose().theta * M_PI / 180.0));

  // robot's circle
  nvgBeginPath(ctx);
  nvgCircle(ctx, 0.0, 0.0, static_cast<float>(robot->get_radius()));
  nvgFillColor(ctx,
               nvgRGBA(robot->get_color().r, robot->get_color().g,
                       robot->get_color().b, 255));
  nvgFill(ctx);
  nvgStrokeColor(ctx, nvgRGBA(0, 0, 0, 255));
  nvgStroke(ctx);

  // robot id text label
  nvgSave(ctx);
  nvgRotate(ctx, static_cast<float>(M_PI / 2.0));
  nvgFillColor(ctx, nvgRGBA(0, 0, 0, 255));
  nvgFontSize(ctx, 18.0f);
  nvgText(ctx, 0.0, 10.0, robot->get_name().c_str(), nullptr);
  nvgRestore(ctx);
  nvgRestore(ctx);
}
void GraphicsArenaViewer::DrawArena(NVGcontext *ctx) {
  nvgBeginPath(ctx);
  // Creates new rectangle shaped sub-path.
  nvgRect(ctx, 0, 0, arena_->get_x_dim(), arena_->get_y_dim());
  nvgStrokeColor(ctx, nvgRGBA(255, 255, 255, 255));
  nvgStroke(ctx);
}

void GraphicsArenaViewer::DrawEntity(NVGcontext *ctx,
                                       const ArenaEntity *const entity) {
  // Light's circle
  nvgBeginPath(ctx);
  nvgCircle(ctx,
            static_cast<float>(entity->get_pose().x),
            static_cast<float>(entity->get_pose().y),
            static_cast<float>(entity->get_radius()));
  nvgFillColor(ctx,
               nvgRGBA(entity->get_color().r, entity->get_color().g,
                       entity->get_color().b, 255));
  nvgFill(ctx);
  nvgStrokeColor(ctx, nvgRGBA(0, 0, 0, 255));
  nvgStroke(ctx);

  // Light id text label
  nvgFillColor(ctx, nvgRGBA(0, 0, 0, 255));
  nvgFontSize(ctx, 10.0f);
  nvgText(ctx,
          static_cast<float>(entity->get_pose().x),
          static_cast<float>(entity->get_pose().y),
          entity->get_name().c_str(), nullptr);
}

void GraphicsArenaViewer::DisplayGameOver() {
  int status = arena_->get_game_status();
  if (status != PLAYING) {
    status_window_->setTitle(arena_->ShowStatus(arena_->get_game_status()));
    status_window_->setVisible(true);
  }
}

void GraphicsArenaViewer::DrawUsingNanoVG(NVGcontext *ctx) {
  // initialize text rendering settings
  nvgFontSize(ctx, 18.0f);
  nvgFontFace(ctx, "sans-bold");
  nvgTextAlign(ctx, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
  DrawArena(ctx);
  std::vector<ArenaEntity *> entities = arena_->get_entities();
  for (auto &entity : entities) {
    if (entity->get_type() == kRobot) {
      DrawRobot(ctx, dynamic_cast<Robot *>(entity));
    } else {
      DrawEntity(ctx, entity);
    }
  }

  // Display game over window if player has lost or won.
  DisplayGameOver();
}

NAMESPACE_END(csci3081);
