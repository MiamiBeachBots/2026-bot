---
layout: default
title: "Contributing | 2026-bot"
---

# Contributing

Want to help out? Great. We use a **branch-based workflow** directly on the main repo. Standard team members don't fork; we just branch and PR.

```bash
git clone git@github.com:MiamiBeachBots/2026-bot.git
cd 2026-bot
```

Make sure your SSH keys are set up on GitHub before you clone.

---

## Setup

We have scripts to automate the boring parts of setting up your environment.

### Windows
Run `setup.bat` as Admin. It'll grab the WPILib toolchain, Python, and Rust.

### macOS / Linux
Run `./setup.sh`.

---

## The Workflow

### 1. Sync
Always pull the latest before you start.
```bash
git checkout main
git pull origin main
```

### 2. Branch
Use your initials and a short name.
```bash
git checkout -b tw-shooter-fix
```

### 3. Code
Follow the [Style Guide](styleguide). Don't leave commented-out code in there.

### 4. PR
Push and open a PR on GitHub. Tag someone for a review. Don't merge your own code unless it's an emergency.

---

## Standards

- **Indent:** 2 spaces.
- **Brackets:** K&R (same line).
- **Naming:** `camelCase` for variables, `PascalCase` for classes.
- **Constants:** Everything in `Constants.java`. No magic numbers.

---

*Need help? See the [Assist Guide](Assist).*
