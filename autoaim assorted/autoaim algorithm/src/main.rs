// =============================================================================
// Autoaim 3D Simulation
// =============================================================================
// A fully 3D interactive simulation of a robot with a turret targeting one 
// specific face of a cube target. Uses Macroquad's built-in 3D camera and 
// primitive rendering (cubes, spheres, lines).
//
// Controls:
//   WASD / Arrows  - Drive the robot on the XZ ground plane
//   Space          - Fire a projectile (affected by gravity + robot momentum)
//
// The GREEN face of the cube is the designated target face.
// The BLUE faces are the non-target sides.
//
// Projectiles that hit the green face vanish instantly (success).
// Projectiles that hit a blue face blink red for ~1 second, then vanish.
// =============================================================================

use autoaim::calculate_aim_angle;
use macroquad::prelude::*;
use std::f32::consts::PI;

// --- SIMULATION CONSTANTS ---
const ROBOT_SPEED: f32 = 8.0;       // units per second on the XZ plane
const ROBOT_ROT_SPEED: f32 = 2.5;   // radians per second (chassis yaw)
const TURRET_ROT_SPEED: f32 = 5.0;  // radians per second (turret yaw tracking)
const GRAVITY: f32 = 9.81;          // m/s^2 downward (-Y in world space)
const PROJECTILE_SPEED: f32 = 20.0; // horizontal launch speed of the ball (m/s)
const ROBOT_HEIGHT: f32 = 0.5;      // height of the turret above the ground
const TARGET_SIZE: f32 = 2.0;       // side length of the cube target

// --- PROJECTILE STATE MACHINE ---
// Each projectile can be in one of three states:
//   Active   - flying through the air, affected by gravity
//   HitGreen - struck the designated front face (instant deletion)
//   HitBlue  - struck a non-target face (blinks red, then deleted)
#[derive(PartialEq)]
enum ProjectileState {
    Active,
    HitGreen,
    HitBlue,
}

struct Projectile {
    pos: Vec3,         // current world position
    vel: Vec3,         // current world velocity (gravity applied every frame)
    life: f32,         // seconds remaining before auto-despawn
    state: ProjectileState,
    blink_timer: f32,  // countdown for the red-blink effect on blue hits
}

// --- COLLISION HELPERS ---

/// Check if a sphere (center, radius) is within `threshold` distance of a 
/// finite quad defined by four coplanar corners (in order).
/// Returns true if the sphere center is close enough to the plane of the quad
/// AND the closest point on the quad is within radius.
fn sphere_hits_quad(
    center: Vec3, radius: f32,
    a: Vec3, b: Vec3, c: Vec3, d: Vec3,
) -> bool {
    // Compute quad normal via cross product of two edges
    let edge1 = b - a;
    let edge2 = d - a;
    let normal = edge1.cross(edge2).normalize();

    // Signed distance from sphere center to the plane of the quad
    let dist_to_plane = (center - a).dot(normal);
    if dist_to_plane.abs() > radius {
        return false;
    }

    // Project sphere center onto the plane
    let projected = center - normal * dist_to_plane;

    // Check if projected point is inside the quad using edge cross tests
    let ap = projected - a;
    let bp = projected - b;
    let cp = projected - c;
    let dp = projected - d;

    let ab = b - a;
    let bc = c - b;
    let cd = d - c;
    let da = a - d;

    let c1 = ab.cross(ap).dot(normal);
    let c2 = bc.cross(bp).dot(normal);
    let c3 = cd.cross(cp).dot(normal);
    let c4 = da.cross(dp).dot(normal);

    // All same sign means inside quad
    (c1 >= 0.0 && c2 >= 0.0 && c3 >= 0.0 && c4 >= 0.0)
        || (c1 <= 0.0 && c2 <= 0.0 && c3 <= 0.0 && c4 <= 0.0)
}

// --- ENTRY POINT ---
#[macroquad::main("Autoaim 3D Simulation")]
async fn main() {
    // Robot state (position on XZ plane, Y is always 0)
    let mut robot_x: f32 = 0.0;
    let mut robot_z: f32 = 10.0;
    let mut robot_yaw: f32 = -PI / 2.0; // facing towards -Z (towards target)

    // Turret yaw (world-space, independent of chassis)
    let mut turret_yaw: f32 = robot_yaw;

    // Target cube sits at the origin, resting on the ground plane
    let target_pos = vec3(0.0, TARGET_SIZE / 2.0, 0.0);
    let target_rot: f32 = 0.0; // cube yaw rotation (0 = front face is +X)

    // Projectile pool
    let mut projectiles: Vec<Projectile> = vec![];

    // Physics output cache
    let mut current_distance: f64 = 0.0;
    let mut current_rpm: f64 = 0.0;

    loop {
        let dt = get_frame_time();

        // =================================================================
        // 1. DRIVING INPUTS (XZ plane movement)
        // =================================================================
        let mut robot_vx: f32 = 0.0;
        let mut robot_vz: f32 = 0.0;

        if is_key_down(KeyCode::Up) || is_key_down(KeyCode::W) {
            robot_vx = robot_yaw.cos() * ROBOT_SPEED;
            robot_vz = robot_yaw.sin() * ROBOT_SPEED;
        }
        if is_key_down(KeyCode::Down) || is_key_down(KeyCode::S) {
            robot_vx = -robot_yaw.cos() * ROBOT_SPEED;
            robot_vz = -robot_yaw.sin() * ROBOT_SPEED;
        }

        robot_x += robot_vx * dt;
        robot_z += robot_vz * dt;

        if is_key_down(KeyCode::Left) || is_key_down(KeyCode::A) {
            robot_yaw -= ROBOT_ROT_SPEED * dt;
        }
        if is_key_down(KeyCode::Right) || is_key_down(KeyCode::D) {
            robot_yaw += ROBOT_ROT_SPEED * dt;
        }

        // =================================================================
        // 2. AUTOAIM TARGETING (call into the Rust/C-ABI core algorithm)
        // =================================================================
        // The lib.rs algorithm works in 2D (top-down XZ mapped to XY).
        // We pass robot_x -> x, robot_z -> y, same for target.
        let mut target_angle_out: f64 = 0.0;
        let mut target_distance_out: f64 = 0.0;
        let mut target_rpm_out: f64 = 0.0;

        let can_hit = calculate_aim_angle(
            robot_x as f64,
            robot_z as f64,
            robot_vx as f64,
            robot_vz as f64,
            target_pos.x as f64,
            target_pos.z as f64,
            TARGET_SIZE as f64,
            target_rot as f64,
            &mut target_angle_out,
            &mut target_distance_out,
            &mut target_rpm_out,
        );

        // =================================================================
        // 3. TURRET TRACKING (smooth rotation towards target angle)
        // =================================================================
        if can_hit {
            let target_angle = target_angle_out as f32;
            current_distance = target_distance_out;
            current_rpm = target_rpm_out;

            // Shortest angular path
            let mut diff = target_angle - turret_yaw;
            diff = (diff + PI) % (2.0 * PI) - PI;
            if diff < -PI { diff += 2.0 * PI; }

            let max_step = TURRET_ROT_SPEED * dt;
            if diff.abs() <= max_step {
                turret_yaw = target_angle;
            } else {
                turret_yaw += diff.signum() * max_step;
            }
        } else {
            current_distance = 0.0;
            current_rpm = 0.0;

            // No target — revert turret to chassis forward
            let mut diff = robot_yaw - turret_yaw;
            diff = (diff + PI) % (2.0 * PI) - PI;
            if diff < -PI { diff += 2.0 * PI; }

            let max_step = TURRET_ROT_SPEED * dt;
            if diff.abs() <= max_step {
                turret_yaw = robot_yaw;
            } else {
                turret_yaw += diff.signum() * max_step;
            }
        }

        // Normalize turret_yaw to [-PI, PI]
        turret_yaw = (turret_yaw + PI) % (2.0 * PI) - PI;
        if turret_yaw < -PI { turret_yaw += 2.0 * PI; }

        // =================================================================
        // 4. FIRE PROJECTILE (Spacebar)
        // =================================================================
        // The projectile inherits the robot's current XZ velocity and adds
        // the turret's aimed direction at `PROJECTILE_SPEED`.
        // It also gets an upward Y velocity to create a gravity arc.
        if is_key_pressed(KeyCode::Space) {
            // Calculate required upward velocity to arc to target height
            let dist_xz = if current_distance > 0.0 {
                current_distance as f32
            } else {
                // Fallback: straight shot
                let dx = target_pos.x - robot_x;
                let dz = target_pos.z - robot_z;
                (dx * dx + dz * dz).sqrt()
            };

            let t_flight = dist_xz / PROJECTILE_SPEED;
            // To hit at target center height from ROBOT_HEIGHT:
            // dy = vy*t - 0.5*g*t^2  =>  vy = (dy + 0.5*g*t^2) / t
            let dy = target_pos.y - ROBOT_HEIGHT;
            let vy_launch = (dy + 0.5 * GRAVITY * t_flight * t_flight) / t_flight;

            // Lock in world-space velocity at the moment of firing
            let fire_vx = robot_vx + turret_yaw.cos() * PROJECTILE_SPEED;
            let fire_vz = robot_vz + turret_yaw.sin() * PROJECTILE_SPEED;

            let spawn_pos = vec3(
                robot_x + turret_yaw.cos() * 1.5,
                ROBOT_HEIGHT,
                robot_z + turret_yaw.sin() * 1.5,
            );

            projectiles.push(Projectile {
                pos: spawn_pos,
                vel: vec3(fire_vx, vy_launch, fire_vz),
                life: 5.0,
                state: ProjectileState::Active,
                blink_timer: 1.0,
            });
        }

        // =================================================================
        // 5. COMPUTE CUBE FACE VERTICES (for collision & rendering)
        // =================================================================
        let half = TARGET_SIZE / 2.0;
        let cy = target_pos.y; // center Y of cube
        let cx = target_pos.x;
        let cz = target_pos.z;
        let rc = target_rot.cos();
        let rs = target_rot.sin();

        // Helper: rotate a local XZ offset around target center
        let rot_xz = |lx: f32, lz: f32| -> (f32, f32) {
            (cx + lx * rc - lz * rs, cz + lx * rs + lz * rc)
        };

        // Front face (+X local) — the GREEN designated face
        let (f1x, f1z) = rot_xz(half, -half);
        let (f2x, f2z) = rot_xz(half, half);
        let front_bottom_right = vec3(f1x, cy - half, f1z);
        let front_top_right    = vec3(f1x, cy + half, f1z);
        let front_top_left     = vec3(f2x, cy + half, f2z);
        let front_bottom_left  = vec3(f2x, cy - half, f2z);

        // Back face (-X local)
        let (b1x, b1z) = rot_xz(-half, half);
        let (b2x, b2z) = rot_xz(-half, -half);
        let back_bottom_right = vec3(b2x, cy - half, b2z);
        let back_top_right    = vec3(b2x, cy + half, b2z);
        let back_top_left     = vec3(b1x, cy + half, b1z);
        let back_bottom_left  = vec3(b1x, cy - half, b1z);

        // Right face (+Z local)
        let (r1x, r1z) = rot_xz(half, half);
        let (r2x, r2z) = rot_xz(-half, half);
        let right_bl = vec3(r1x, cy - half, r1z);
        let right_tl = vec3(r1x, cy + half, r1z);
        let right_tr = vec3(r2x, cy + half, r2z);
        let right_br = vec3(r2x, cy - half, r2z);

        // Left face (-Z local)
        let (l1x, l1z) = rot_xz(-half, -half);
        let (l2x, l2z) = rot_xz(half, -half);
        let left_bl = vec3(l1x, cy - half, l1z);
        let left_tl = vec3(l1x, cy + half, l1z);
        let left_tr = vec3(l2x, cy + half, l2z);
        let left_br = vec3(l2x, cy - half, l2z);

        // =================================================================
        // 6. UPDATE PROJECTILE PHYSICS & COLLISION
        // =================================================================
        for proj in &mut projectiles {
            if proj.state == ProjectileState::Active {
                // Apply gravity to Y velocity
                proj.vel.y -= GRAVITY * dt;

                // Integrate position
                proj.pos += proj.vel * dt;
                proj.life -= dt;

                // Kill if it hits the ground
                if proj.pos.y < 0.0 {
                    proj.state = ProjectileState::HitBlue; // treat ground hit as miss
                }

                // Check collision against cube faces (sphere radius = 0.15)
                let r = 0.15;

                // Front face (GREEN) — SUCCESS
                if sphere_hits_quad(proj.pos, r,
                    front_bottom_right, front_top_right,
                    front_top_left, front_bottom_left)
                {
                    proj.state = ProjectileState::HitGreen;
                }
                // Back face
                else if sphere_hits_quad(proj.pos, r,
                    back_bottom_right, back_top_right,
                    back_top_left, back_bottom_left)
                {
                    proj.state = ProjectileState::HitBlue;
                }
                // Right face
                else if sphere_hits_quad(proj.pos, r,
                    right_bl, right_tl, right_tr, right_br)
                {
                    proj.state = ProjectileState::HitBlue;
                }
                // Left face
                else if sphere_hits_quad(proj.pos, r,
                    left_bl, left_tl, left_tr, left_br)
                {
                    proj.state = ProjectileState::HitBlue;
                }
            } else if proj.state == ProjectileState::HitBlue {
                proj.blink_timer -= dt;
            }
        }

        // Remove finished projectiles
        projectiles.retain(|p| {
            p.life > 0.0
                && p.state != ProjectileState::HitGreen
                && (p.state != ProjectileState::HitBlue || p.blink_timer > 0.0)
        });

        // =================================================================
        // 7. RENDERING
        // =================================================================
        clear_background(Color::new(0.15, 0.15, 0.2, 1.0));

        // --- 3D CAMERA ---
        // Follow camera positioned behind and above the robot
        let cam_dist = 12.0;
        let cam_height = 8.0;
        let cam_pos = vec3(
            robot_x - robot_yaw.cos() * cam_dist,
            cam_height,
            robot_z - robot_yaw.sin() * cam_dist,
        );
        let cam_target = vec3(robot_x, ROBOT_HEIGHT, robot_z);

        set_camera(&Camera3D {
            position: cam_pos,
            target: cam_target,
            up: vec3(0.0, 1.0, 0.0),
            ..Default::default()
        });

        // --- GROUND PLANE ---
        // Draw a large grid on the XZ plane
        let grid_size = 50;
        let grid_color = Color::new(0.3, 0.3, 0.35, 1.0);
        for i in -grid_size..=grid_size {
            let fi = i as f32;
            let gs = grid_size as f32;
            draw_line_3d(vec3(fi, 0.0, -gs), vec3(fi, 0.0, gs), grid_color);
            draw_line_3d(vec3(-gs, 0.0, fi), vec3(gs, 0.0, fi), grid_color);
        }

        // --- TARGET CUBE ---
        // Draw cube faces as colored lines (wireframe with thick edges)

        // Front face — GREEN (the target face)
        draw_line_3d(front_bottom_right, front_top_right, GREEN);
        draw_line_3d(front_top_right, front_top_left, GREEN);
        draw_line_3d(front_top_left, front_bottom_left, GREEN);
        draw_line_3d(front_bottom_left, front_bottom_right, GREEN);
        // X across to make it visually obvious
        draw_line_3d(front_bottom_right, front_top_left, GREEN);
        draw_line_3d(front_top_right, front_bottom_left, GREEN);

        // Back face — BLUE
        draw_line_3d(back_bottom_right, back_top_right, BLUE);
        draw_line_3d(back_top_right, back_top_left, BLUE);
        draw_line_3d(back_top_left, back_bottom_left, BLUE);
        draw_line_3d(back_bottom_left, back_bottom_right, BLUE);

        // Right face — BLUE
        draw_line_3d(right_bl, right_tl, BLUE);
        draw_line_3d(right_tl, right_tr, BLUE);
        draw_line_3d(right_tr, right_br, BLUE);
        draw_line_3d(right_br, right_bl, BLUE);

        // Left face — BLUE
        draw_line_3d(left_bl, left_tl, BLUE);
        draw_line_3d(left_tl, left_tr, BLUE);
        draw_line_3d(left_tr, left_br, BLUE);
        draw_line_3d(left_br, left_bl, BLUE);

        // Top edges — BLUE
        draw_line_3d(front_top_right, back_top_right, BLUE);
        draw_line_3d(front_top_left, back_top_left, BLUE);

        // Bottom edges — BLUE
        draw_line_3d(front_bottom_right, back_bottom_right, BLUE);
        draw_line_3d(front_bottom_left, back_bottom_left, BLUE);

        // Target center marker
        draw_sphere(target_pos, 0.1, None, RED);

        // --- PROJECTILES ---
        for proj in &projectiles {
            match proj.state {
                ProjectileState::Active => {
                    draw_sphere(proj.pos, 0.15, None, YELLOW);
                }
                ProjectileState::HitBlue => {
                    let blink_on = (proj.blink_timer * 10.0) as i32 % 2 == 0;
                    if blink_on {
                        draw_sphere(proj.pos, 0.2, None, RED);
                    }
                }
                ProjectileState::HitGreen => {} // already removed
            }
        }

        // --- ROBOT BODY ---
        // Draw robot as a cube sitting on the ground
        let robot_pos_3d = vec3(robot_x, 0.25, robot_z);
        draw_cube(robot_pos_3d, vec3(1.0, 0.5, 1.0), None, BLUE);
        draw_cube_wires(robot_pos_3d, vec3(1.0, 0.5, 1.0), DARKBLUE);

        // --- ROBOT FORWARD INDICATOR (chassis heading) ---
        let fwd_end = vec3(
            robot_x + robot_yaw.cos() * 1.5,
            0.4,
            robot_z + robot_yaw.sin() * 1.5,
        );
        draw_line_3d(vec3(robot_x, 0.4, robot_z), fwd_end, LIGHTGRAY);

        // --- TURRET ---
        // Draw turret as a sphere on top of the robot + barrel line
        let turret_pos = vec3(robot_x, ROBOT_HEIGHT, robot_z);
        draw_sphere(turret_pos, 0.3, None, ORANGE);

        let barrel_end = vec3(
            robot_x + turret_yaw.cos() * 2.0,
            ROBOT_HEIGHT,
            robot_z + turret_yaw.sin() * 2.0,
        );
        draw_line_3d(turret_pos, barrel_end, RED);

        // =================================================================
        // 8. HUD (switch back to 2D screen space)
        // =================================================================
        set_default_camera();

        // Semi-transparent side panel
        draw_rectangle(0.0, 0.0, 320.0, screen_height(), Color::new(0.05, 0.05, 0.05, 0.85));

        draw_text("Autoaim 3D Simulation", 10.0, 30.0, 24.0, WHITE);
        draw_text("========================", 10.0, 50.0, 20.0, GRAY);

        let hud_color = if can_hit { GREEN } else { RED };
        let lock_str = if can_hit { "LOCKED" } else { "NO SIGHTLINE" };

        draw_text(&format!("Target: {}", lock_str), 10.0, 80.0, 20.0, hud_color);
        draw_text(&format!("Distance:  {:.2} m", current_distance), 10.0, 110.0, 20.0, WHITE);
        draw_text(&format!("Req. RPM:  {:.0}", current_rpm), 10.0, 140.0, 20.0, ORANGE);
        draw_text(&format!("Robot Yaw: {:.0} deg", robot_yaw.to_degrees()), 10.0, 170.0, 20.0, WHITE);
        draw_text(&format!("Turret Yaw:{:.0} deg", turret_yaw.to_degrees()), 10.0, 200.0, 20.0, ORANGE);

        draw_text("========================", 10.0, 230.0, 20.0, GRAY);
        draw_text("Controls:", 10.0, 260.0, 20.0, WHITE);
        draw_text("[WASD]  Drive Chassis", 10.0, 285.0, 18.0, LIGHTGRAY);
        draw_text("[SPACE] Fire Projectile", 10.0, 310.0, 18.0, LIGHTGRAY);

        draw_text(&format!("Projectiles: {}", projectiles.len()), 10.0, 350.0, 18.0, YELLOW);
        draw_text(&format!("FPS: {}", get_fps()), 10.0, 375.0, 18.0, GRAY);

        next_frame().await
    }
}
