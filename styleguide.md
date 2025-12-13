# Project Style Guide

## Naming Conventions

We follow standard Java naming conventions. Consistency makes the code readable.

* **Classes & Enums:** `PascalCase`
    * `SwerveSubsystem`, `RobotContainer`, `GameState`
* **Methods & Variables:** `camelCase`
    * `drive()`, `getPose()`, `targetVelocity`
* **Constants (`static final`):** `SCREAMING_SNAKE_CASE`
    * `MAX_SPEED_METERS`, `DRIVE_GEAR_RATIO`
* **Local Variables:** Short and descriptive.
    * Use `i` for loops, but `moduleState` for objects. No generic `data` or `thing`.

## Formatting

* **Indentation:** 2 spaces (Standard WPILib/GradleRIO). No tabs.
* **Braces:** Open braces on the same line (K&R style).
    ```java
    if (condition) {
      // do something
    } else {
      // do something else
    }
    ```
* **Imports:** Remove unused imports. Do not use wildcards (`import java.util.*;`). List explicit classes.

## Documentation

* **Javadocs:** Required for all public subsystems and complex methods.
* **Comments:** Explain *why*, not *what*.
    * BAD: `// Sets speed to 0`
    * GOOD: `// Stop motors to prevent brownout during initialization`

## File Structure

* **Subsystems:** `frc.robot.subsystems` (e.g., `DriveSubsystem.java`)
* **Commands:** `frc.robot.commands` (e.g., `TeleopDrive.java`)
* **Constants:** Keep physical constants in `Constants.java`. Do not hardcode magic numbers in logic files.

## Git Commit Messages

* Use the imperative mood.
* Format: `[Scope] Short description`
    * `[Swerve] Fix module offsets`
    * `[Auto] Add 3-piece taxi routine`
