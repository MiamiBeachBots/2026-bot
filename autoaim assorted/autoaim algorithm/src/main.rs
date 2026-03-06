use autoaim::calculate_aim_angle;
use macroquad::prelude::*;
use std::f32::consts::PI;

const ROBOT_SPEED: f32 = 200.0; // pixels per second
const ROBOT_ROT_SPEED: f32 = 2.5; // rad per second
const TURRET_ROT_SPEED: f32 = 5.0; // rad per second

#[macroquad::main("Autoaim Simulation")]
async fn main() {
    let mut robot_x = screen_width() / 2.0;
    let mut robot_y = screen_height() / 2.0 + 200.0;
    let mut robot_rot: f32 = -PI / 2.0; // facing up

    let mut turret_rot: f32 = 0.0; // relative to world

    let target_x = screen_width() / 2.0;
    let target_y = screen_height() / 2.0;
    let target_size = 100.0;
    let target_rot: f32 = 0.0; // facing right (+X) is the designated front face

    loop {
        let dt = get_frame_time();

        // Driving Inputs
        if is_key_down(KeyCode::Up) || is_key_down(KeyCode::W) {
            robot_x += robot_rot.cos() * ROBOT_SPEED * dt;
            robot_y += robot_rot.sin() * ROBOT_SPEED * dt;
        }
        if is_key_down(KeyCode::Down) || is_key_down(KeyCode::S) {
            robot_x -= robot_rot.cos() * ROBOT_SPEED * dt;
            robot_y -= robot_rot.sin() * ROBOT_SPEED * dt;
        }
        if is_key_down(KeyCode::Left) || is_key_down(KeyCode::A) {
            robot_rot -= ROBOT_ROT_SPEED * dt;
        }
        if is_key_down(KeyCode::Right) || is_key_down(KeyCode::D) {
            robot_rot += ROBOT_ROT_SPEED * dt;
        }

        // Calculate autoaim using out algorithm
        let mut target_angle_out: f64 = 0.0;
        let can_hit = calculate_aim_angle(
            robot_x as f64,
            robot_y as f64,
            target_x as f64,
            target_y as f64,
            target_size as f64,
            target_rot as f64,
            &mut target_angle_out,
        );

        // Move turret towards target angle respects TURRET_ROT_SPEED
        if can_hit {
            let target_angle = target_angle_out as f32;
            
            // Shortest path to target angle
            let mut diff = target_angle - turret_rot;
            diff = (diff + PI) % (2.0 * PI) - PI;
            if diff < -PI {
                diff += 2.0 * PI;
            }

            let max_step = TURRET_ROT_SPEED * dt;
            if diff.abs() <= max_step {
                turret_rot = target_angle;
            } else {
                turret_rot += diff.signum() * max_step;
            }
        } else {
            // Revert turret to robot's forward facing if no target
            let mut diff = robot_rot - turret_rot;
            diff = (diff + PI) % (2.0 * PI) - PI;
            if diff < -PI {
                diff += 2.0 * PI;
            }
            let max_step = TURRET_ROT_SPEED * dt;
            if diff.abs() <= max_step {
                turret_rot = robot_rot;
            } else {
                turret_rot += diff.signum() * max_step;
            }
        }

        // Ensure turret_rot stays within -PI to PI
        turret_rot = (turret_rot + PI) % (2.0 * PI) - PI;
        if turret_rot < -PI {
            turret_rot += 2.0 * PI;
        }

        clear_background(DARKGRAY);

        // Draw Instructions
        draw_text("WASD or Arrows to Drive", 20.0, 30.0, 30.0, WHITE);
        if can_hit {
            draw_text("Targeting: LOCKED", 20.0, 60.0, 30.0, GREEN);
        } else {
            draw_text("Targeting: NO LINE OF SIGHT", 20.0, 60.0, 30.0, RED);
        }

        // --- DRAW TARGET ---
        // We simulate the rotation by drawing lines for the square
        // designated face (East face if rot=0) drawn in a special color
        let tr_cos = target_rot.cos();
        let tr_sin = target_rot.sin();
        let half = target_size / 2.0;

        // four corners relative to center before rotation
        let c1 = vec2(half, half); // top-right
        let c2 = vec2(-half, half); // top-left
        let c3 = vec2(-half, -half); // bottom-left
        let c4 = vec2(half, -half); // bottom-right

        // rotate and translate
        let rot_p = |p: Vec2| -> Vec2 {
            vec2(
                target_x + p.x * tr_cos - p.y * tr_sin,
                target_y + p.x * tr_sin + p.y * tr_cos,
            )
        };

        let p1 = rot_p(c1);
        let p2 = rot_p(c2);
        let p3 = rot_p(c3);
        let p4 = rot_p(c4);

        // Faces: (p4->p1 is the front face +X)
        draw_line(p1.x, p1.y, p2.x, p2.y, 3.0, BLUE); // Back
        draw_line(p2.x, p2.y, p3.x, p3.y, 3.0, BLUE); // Side
        draw_line(p3.x, p3.y, p4.x, p4.y, 3.0, BLUE); // Side
        // Front Face (Designated target face)
        draw_line(p4.x, p4.y, p1.x, p1.y, 6.0, GREEN); 

        // Draw Target Center
        draw_circle(target_x, target_y, 4.0, RED);

        // --- DRAW ROBOT ---
        draw_circle(robot_x, robot_y, 25.0, BLUE);
        // Draw Robot Forward Indicator
        draw_line(
            robot_x, robot_y,
            robot_x + robot_rot.cos() * 35.0,
            robot_y + robot_rot.sin() * 35.0,
            4.0, LIGHTGRAY,
        );

        // --- DRAW TURRET ---
        draw_circle(robot_x, robot_y, 15.0, ORANGE);
        draw_line(
            robot_x, robot_y,
            robot_x + turret_rot.cos() * 50.0,
            robot_y + turret_rot.sin() * 50.0,
            6.0, RED,
        );

        next_frame().await
    }
}
