# Getting Assistance

Need help with the 2026-bot project? This guide explains how to report issues and get support.

## Opening an Issue for Code Errors

If you encounter a bug, compilation error, or unexpected behavior in the code:

1. **Navigate to the Issues tab** on GitHub: [https://github.com/MiamiBeachBots/2026-bot/issues](https://github.com/MiamiBeachBots/2026-bot/issues)

2. **Click "New Issue"** to create a new issue.

3. **Provide a clear title** that summarizes the problem:
   - ✅ Good: "SwerveSubsystem throws NullPointerException on initialization"
   - ❌ Bad: "Code broken"

4. **Include the following details in your issue description:**
   - **Description:** What went wrong? What did you expect to happen?
   - **Steps to Reproduce:** How can someone else trigger this error?
   - **Error Messages:** Copy and paste any error messages or stack traces
   - **Environment:** Mention if this occurred during simulation, on the practice robot, or competition robot
   - **Code References:** Link to specific files or line numbers if applicable

5. **Add appropriate labels** (if you have permission):
   - `bug` for errors or defects
   - `help wanted` if you need assistance fixing it

**Example:**

```
Title: Motor inversion causes robot to drive backward

Description:
When using field-oriented drive, the robot moves in the opposite direction 
of the joystick input. Expected the robot to move forward when pushing the 
joystick forward.

Steps to Reproduce:
1. Deploy code to robot
2. Enable teleop mode
3. Push forward on the driver controller

Environment: Competition robot, tested on 2025-12-14

Suspected Issue: Drive motors may need inversion in Constants.java
```

## Opening an Issue for General Questions or Other Matters

For questions, feature requests, documentation improvements, or other non-bug topics:

1. **Navigate to the Issues tab** on GitHub: [https://github.com/MiamiBeachBots/2026-bot/issues](https://github.com/MiamiBeachBots/2026-bot/issues)

2. **Click "New Issue"** to create a new issue.

3. **Use a descriptive title:**
   - ✅ Good: "Add documentation for PathPlanner configuration"
   - ✅ Good: "Feature request: Implement auto-climb command"
   - ✅ Good: "Question: How to calibrate swerve module offsets?"

4. **Clearly explain your question or request:**
   - For **questions**, provide context about what you're trying to accomplish
   - For **feature requests**, describe the proposed feature and its benefits
   - For **documentation requests**, specify what needs clarification

5. **Add appropriate labels:**
   - `question` for general inquiries
   - `enhancement` for feature requests
   - `documentation` for docs-related issues

## Direct Contact

For urgent matters, sensitive issues, or general assistance, you can reach out directly to:

**Thalia**  
Email: [thaliathenerd@proton.me](mailto:thaliathenerd@proton.me)

Please use direct email sparingly and prefer GitHub issues when possible, as issues create a public record that benefits the entire team.

---

*For contribution guidelines, see [Contributing Guide](Contribguide.md)*
