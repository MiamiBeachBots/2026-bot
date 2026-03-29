---
layout: default
title: "Style Guide | 2026-bot"
---

# Style Guide
**Miami Beach Bots — FRC Team 2026**

Clean code > fast code. Matching the team style makes it easier for everyone to fix bugs during a competition.

> [!IMPORTANT]
> If you're using VS Code, use the WPILib formatter. It'll handle most of this for you.

## Quick Reference

| Rule | Standard |
|:---|:---|
| Indent | 2 spaces |
| Brackets | K&R style (opening on same line) |
| Width | Max 120 chars |
| Classes | `PascalCase` |
| Methods | `camelCase` |
| Constants | `SCREAMING_SNAKE_CASE` in `Constants.java` |
| Javadocs | All public classes/methods |

---

## 1. Naming

- **Classes:** `SwerveDrive`, `RobotContainer`
- **Methods:** `drive()`, `getPose()`
- **Variables:** `sensorValue`, `targetSpeed`
- **Constants:** `kMaxVelocity`, `kEncoderOffset` (nested in `Constants.java`)

---

## 2. Brackets & Spacing

Always use brackets, even for one-liners. It prevents stupid bugs.

```java
// Good
if (targetAcquired) {
  fire();
}

// Bad
if (targetAcquired) fire();
```

---

## 3. Comments

Explain **why** you're doing something, not **what** you're doing. The code should tell us what happened.

```java
// Stop motors before init to prevent brownouts
motor.set(0);
```

---

## 4. Best Practices

- **No Magic Numbers:** If it's a number, it belongs in `Constants.java`.
- **Imports:** No wildcards (`*`). Only import what you need.
- **Dead Code:** Delete it. Don't comment it out "just in case."

---

*Clean code is a team effort. If you see something messy, fix it.*
