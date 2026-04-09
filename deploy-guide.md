---
layout: default
title: "Deploy Guide | 2026-bot"
---

# Deployment

## 1. Setup Your Machine
We use setup scripts to keep everyone on the same page.

- **Windows:** Run `setup.bat` as Admin.
- **Mac / Linux:** Run `./setup.sh`.

Both will install the 2026 WPILib toolchain, Python, and Rust.

## 2. Deploying
1. Turn on the robot and connect to the radio.
2. Check your ping: `10.20.26.2`.
3. Run `./gradlew deploy`.
4. If it fails, check your laptop's firewall.

---

*If you're tuning, use `cd thalia-tweaks && cargo run` to launch the GUI.*
