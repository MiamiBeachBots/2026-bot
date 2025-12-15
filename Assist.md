# Getting Help & Support

Need assistance with the 2026-bot project? This guide explains how to report issues, ask questions, and get support.

## Opening Issues on GitHub

GitHub Issues is our primary method for tracking bugs, feature requests, hardware problems, documentation improvements, and general questions. Follow the guidelines below to ensure your issue is handled efficiently.

### Before You Open an Issue

1. **Search existing issues** - Check if someone has already reported the same problem or requested the same feature
2. **Check the documentation** - Review the README, style guides, and other docs to see if your question is already answered
3. **Gather information** - Collect relevant details like error messages, logs, version numbers, and steps to reproduce
4. **Choose the right template** - Use the appropriate issue template for your type of issue

### Issue Templates Overview

We provide several issue templates to help you report problems effectively:

| Template | Use For | Label |
|----------|---------|-------|
| 🐛 **Bug Report** | Code errors, crashes, unexpected behavior | `bug`, `needs-triage` |
| 🔧 **Hardware Issue** | Physical robot problems, electrical issues | `hardware`, `needs-triage` |
| ✨ **Feature Request** | New features, enhancements, improvements | `enhancement`, `needs-triage` |
| 📚 **Documentation Update** | Missing, incorrect, or unclear documentation | `documentation` |

### 🐛 Reporting Code Bugs

Use the **Bug Report** template when you encounter software errors, crashes, compilation failures, or unexpected behavior in the robot code.

**When to use this template:**
- Robot code crashes or throws exceptions
- Code compiles but doesn't behave as expected
- Simulation behaves differently than the physical robot
- Build system errors or dependency issues
- Unit tests fail unexpectedly

**How to report a bug:**

1. **Navigate to the Issues tab:**  
   Go to [https://github.com/MiamiBeachBots/2026-bot/issues](https://github.com/MiamiBeachBots/2026-bot/issues)

2. **Click "New Issue"** and select **Bug Report**

3. **Fill out the template fields:**

   - **Severity:** Critical, High, Medium, or Low
   - **Location:** Competition Robot, Practice Robot, Simulation, or Build System
   - **Bug Description:** Clear explanation of what's broken
   - **Steps to Reproduce:** Detailed steps to trigger the bug
   - **Expected Behavior:** What should happen
   - **Actual Behavior:** What actually happens
   - **Logs and Error Messages:** Full stack traces and console output
   - **Git Commit/Branch:** Which version of the code has the bug
   - **Additional Context:** Screenshots, videos, or other helpful information

4. **Submit the issue** and monitor for responses

**Example Bug Report:**

```
Title: [Bug] Robot code crashes on startup with NullPointerException

Severity: Critical (Robot won't run)
Location: Competition Robot, Simulation

Bug Description:
When deploying the robot code to the RoboRIO, the code crashes immediately 
after initialization with a NullPointerException in SwerveSubsystem.java.

Steps to Reproduce:
1. Deploy code using `./gradlew deploy`
2. Enable the robot via Driver Station
3. Observe crash in Driver Station console

Expected Behavior:
Robot should initialize all subsystems successfully and be ready for operation.

Actual Behavior:
Code crashes with:
```
java.lang.NullPointerException: Cannot invoke SwerveModule.getPosition() because "this.module" is null
  at frc.robot.subsystems.SwerveSubsystem.periodic(SwerveSubsystem.java:45)
```

Logs and Error Messages:
[Full stack trace from Driver Station console pasted here]

Git Commit/Branch: main / commit a1b2c3d

Additional Context:
This started happening after the latest pull from main. The code worked fine yesterday.
```

**Tips for effective bug reports:**
- Be specific and detailed in your description
- Include complete error messages and stack traces
- Provide the exact steps to reproduce the bug
- Mention what you've already tried to fix it
- Include screenshots or videos if they help explain the issue
- Note if the bug is intermittent or consistent

### 🔧 Reporting Hardware Issues

Use the **Hardware Issue** template for physical robot problems, electrical issues, mechanical failures, or sensor malfunctions.

**When to use this template:**
- Motors not responding or behaving erratically
- Sensors returning incorrect values or not working
- Pneumatic system failures
- Power distribution problems
- Physical damage to robot components
- Wiring or connection issues
- RoboRIO, radio, or networking problems
- Vision system or camera issues

**⚠️ SAFETY FIRST:** If there is an immediate safety concern (sparks, smoke, burning smell, exposed wiring), notify team leadership immediately and power down the robot before filing an issue.

**How to report a hardware issue:**

1. **Navigate to the Issues tab**
2. **Click "New Issue"** and select **Hardware Issue**
3. **Fill out the template fields:**

   - **Severity:** Critical, High, Medium, or Low
   - **Component Type:** Motor, Sensor, Pneumatics, PDP, RoboRIO, Swerve Module, etc.
   - **Component ID/Location:** CAN ID, physical location, or identifier
   - **Which Robot:** Competition, Practice, or Both
   - **Issue Description:** What's wrong with the hardware
   - **Symptoms/Behavior:** Observable problems (noises, LED patterns, errors)
   - **Troubleshooting Steps Taken:** What you've already tried
   - **Impact on Robot Operation:** How this affects functionality
   - **When Did This Occur:** When the issue first appeared
   - **Additional Context:** Photos, videos, or other documentation

4. **Verify safety** and submit the issue

**Example Hardware Issue:**

```
Title: [Hardware] Front Left Swerve Module Not Responding

Severity: High (Major functionality broken)
Component Type: Swerve Module
Component ID/Location: CAN ID 3, Front Left Module
Which Robot: Competition Robot

Issue Description:
The front left swerve module is completely unresponsive. The drive motor 
doesn't spin and the steering motor doesn't rotate.

Symptoms/Behavior:
- No motor movement from either motor
- Driver Station shows "CAN bus off" warning for IDs 3 and 4
- Module LED on motor controller is solid red
- Other three modules work normally
- No unusual sounds or smells

Troubleshooting Steps Taken:
- Checked CAN wiring connections - all secure
- Verified motor controller firmware is up to date
- Swapped CAN cables with known-good cables
- Checked Phoenix Tuner - devices not appearing
- Tested voltage at motor controller - 12V present

Impact on Robot Operation:
Robot cannot drive properly with only three functioning modules. Cannot compete.

When Did This Occur:
Issue appeared during practice on 12/14 after a collision with the field wall.

Additional Context:
[Photos of motor controller, wiring, and LED status attached]
```

**Tips for effective hardware reports:**
- Include component CAN IDs and physical locations
- Document LED status indicators on motor controllers
- Take photos of any physical damage or unusual wiring
- Note when the issue started and what might have triggered it
- Describe the impact on robot functionality
- List what troubleshooting you've already done

### ✨ Requesting New Features

Use the **Feature Request** template to suggest new functionality, enhancements, or improvements to the robot code or systems.

**When to use this template:**
- Proposing new autonomous routines
- Suggesting drivetrain improvements
- Requesting vision processing features
- Recommending new driver controls or interfaces
- Proposing telemetry or logging enhancements
- Suggesting build system or CI improvements

**How to request a feature:**

1. **Navigate to the Issues tab**
2. **Click "New Issue"** and select **Feature Request**
3. **Fill out the template fields:**

   - **Feature Category:** Autonomous, Drivetrain, Vision, Game Mechanism, etc.
   - **Priority:** Critical, High, Medium, or Low
   - **Problem Statement:** What problem does this solve?
   - **Proposed Solution:** How should it work?
   - **Alternatives Considered:** Other approaches you've thought about
   - **Implementation Details:** Technical requirements and dependencies
   - **Additional Context:** Diagrams, examples, or references

4. **Submit the issue** and participate in discussion

**Example Feature Request:**

```
Title: [Feature] Implement PathPlanner for Autonomous Routines

Feature Category: Autonomous
Priority: High (Significant improvement)

Problem Statement:
Currently, we're creating autonomous routines manually using timed commands 
and hardcoded positions. This is error-prone, difficult to tune, and doesn't 
adapt well to field variations. We need a more robust solution for complex 
autonomous paths.

Proposed Solution:
Integrate PathPlanner library to enable visual path creation, automatic 
trajectory generation, and field-relative autonomous routines. This would 
allow us to:
- Design paths visually in PathPlanner GUI
- Automatically generate optimized trajectories
- Use event markers for actions during paths
- Easily adjust paths without code changes

Alternatives Considered:
1. WPILib Trajectory API - more manual, less visual
2. Custom path following - reinventing the wheel
3. Staying with current timed approach - limits competition performance

Implementation Details:
- Add PathPlanner vendordep
- Configure PathPlanner for swerve drive
- Create PathPlanner path files in deploy directory
- Implement path following commands
- Add auto chooser to select paths
- Test in simulation before deploying to robot

Additional Context:
Many top FRC teams use PathPlanner successfully. Documentation: 
https://pathplanner.dev/
```

**Tips for effective feature requests:**
- Explain why the feature is needed, not just what it should do
- Consider the time and effort required for implementation
- Align with team goals and competition strategy
- Provide examples or references if available
- Be open to discussion and alternative approaches

### 📚 Reporting Documentation Issues

Use the **Documentation Update** template for issues with READMEs, guides, code comments, Javadocs, or configuration files.

**When to use this template:**
- Documentation is missing or incomplete
- Information is incorrect or outdated
- Instructions are unclear or confusing
- Typos or grammar errors
- Missing code examples
- Configuration files lack explanations

**How to report a documentation issue:**

1. **Navigate to the Issues tab**
2. **Click "New Issue"** and select **Documentation Update**
3. **Fill out the template fields:**

   - **Documentation Type:** README, Style Guide, Javadocs, Code Comments, etc.
   - **Issue Type:** Missing, Incorrect, Unclear, Typo, etc.
   - **Location:** File path and line numbers or section name
   - **Current Documentation:** What it says now (if applicable)
   - **Proposed Change:** What it should say instead
   - **Additional Context:** Why the change is needed

4. **Submit the issue**

**Example Documentation Issue:**

```
Title: [Docs] Add instructions for calibrating swerve module offsets

Documentation Type: README.md
Issue Type: Missing documentation

Location:
README.md, "Hardware Integration" section (around line 130)

Current Documentation:
The section mentions verifying module configurations but doesn't explain 
how to calibrate the absolute encoder offsets.

Proposed Change:
Add a subsection called "Calibrating Swerve Module Offsets" with:

1. Procedure for manually aligning wheels to forward position
2. How to read absolute encoder values using Phoenix Tuner
3. Where to update offset values in swerve JSON config files
4. How to verify calibration is correct

Example text:
```markdown
#### Calibrating Swerve Module Offsets

1. Position all wheels pointing forward (aligned with robot chassis)
2. Open Phoenix Tuner and connect to the robot
3. Read the absolute encoder value for each steering motor
4. Update the `absoluteEncoderOffset` values in `deploy/swerve/modules/`
5. Deploy code and verify wheels maintain forward alignment
```

Additional Context:
New team members struggled with this during initial swerve setup. Clear 
documentation would save time and prevent configuration errors.
```

**Tips for effective documentation reports:**
- Be specific about the location of the issue
- Provide suggested text or outline of what should be added
- Explain why the documentation change is helpful
- Follow the repository's documentation style
- Include examples if they help clarify your suggestion

### 💡 General Questions and Discussions

For questions that aren't bug reports, feature requests, or documentation issues, you have several options:

**GitHub Discussions:**  
Use [GitHub Discussions](https://github.com/MiamiBeachBots/2026-bot/discussions) for:
- General team questions
- Strategy discussions
- Learning resources and tutorials
- Brainstorming new ideas
- Community help and collaboration

**Blank Issues:**  
You can also create a blank issue for questions that don't fit the templates. Just click "New Issue" and select "Open a blank issue" at the bottom.

**When to use discussions vs. issues:**
- Use **Issues** for actionable items (bugs to fix, features to implement, docs to update)
- Use **Discussions** for open-ended conversations, questions, and brainstorming

## Issue Labels Explained

Our repository uses labels to categorize and prioritize issues. Here's what each label means:

### Status Labels
- `needs-triage` - New issue that hasn't been reviewed yet
- `in-progress` - Someone is actively working on this
- `blocked` - Cannot proceed due to dependencies or external factors
- `on-hold` - Temporarily paused, will resume later

### Type Labels
- `bug` - Something isn't working correctly
- `enhancement` - New feature or improvement request
- `documentation` - Improvements or additions to documentation
- `hardware` - Physical robot or electrical issue
- `question` - Further information is requested
- `discussion` - Open-ended topic for team discussion

### Priority Labels
- `critical` - Must be fixed immediately (safety issues, competition blockers)
- `high-priority` - Should be addressed soon
- `medium-priority` - Important but not urgent
- `low-priority` - Nice to have, can wait

### Component Labels
- `drivetrain` - Swerve drive system
- `autonomous` - Autonomous routines and path planning
- `vision` - Camera and vision processing
- `controls` - Driver controls and input handling
- `telemetry` - Logging, debugging, and dashboard
- `build-system` - Gradle, CI/CD, deployment

### Special Labels
- `good-first-issue` - Good for newcomers to the codebase
- `help-wanted` - Extra attention is needed from team members
- `duplicate` - This issue already exists
- `wontfix` - This will not be worked on
- `invalid` - This doesn't seem right or is not applicable

## Issue Lifecycle and Triage Process

Understanding how issues are managed helps you know what to expect after submitting one.

### 1. **Issue Created** (Label: `needs-triage`)
- You submit an issue using one of the templates
- The issue is automatically labeled with `needs-triage`

### 2. **Triage** (Within 24-48 hours)
- Maintainers review the issue
- Appropriate labels are added (type, priority, component)
- Issue may be assigned to a team member
- Questions may be asked for clarification
- `needs-triage` label is removed

### 3. **In Progress** (Label: `in-progress`)
- Someone begins working on the issue
- Issue is assigned to the person working on it
- Updates are posted in comments

### 4. **Pull Request Created**
- A PR is opened to fix the issue
- PR references the issue number (e.g., "Fixes #123")
- Issue is linked to the PR automatically

### 5. **Review and Testing**
- PR is reviewed by team members
- Changes are tested on robot or in simulation
- Feedback is provided and addressed

### 6. **Merged and Closed**
- PR is merged into main branch
- Issue is automatically closed
- Changes are deployed to robot

### 7. **Verification** (Optional)
- Reporter verifies the fix works
- Issue can be reopened if problem persists

## Best Practices for Issue Reporting

Follow these guidelines to create high-quality issues that get resolved quickly:

### Do's ✅

1. **Search first** - Check if the issue already exists before creating a duplicate
2. **Use templates** - Fill out the provided issue templates completely
3. **Be specific** - Provide exact error messages, steps to reproduce, and detailed descriptions
4. **Include context** - Add screenshots, logs, videos, or code snippets
5. **One issue per report** - Don't combine multiple unrelated problems in one issue
6. **Follow up** - Respond to questions and provide additional information when asked
7. **Be patient** - Give maintainers time to review and respond
8. **Be respectful** - We're all learning and working together as a team

### Don'ts ❌

1. **Don't be vague** - "It doesn't work" isn't helpful; explain what specifically is wrong
2. **Don't skip the template** - Templates exist to gather the information we need
3. **Don't demand immediate fixes** - We prioritize based on severity and available time
4. **Don't post duplicates** - Search existing issues first
5. **Don't hijack issues** - If you have a different problem, create a new issue
6. **Don't post sensitive info** - No passwords, API keys, or personal information
7. **Don't spam** - Avoid commenting "any updates?" repeatedly; be patient

### Writing Effective Titles

Good titles help team members quickly understand the issue:

**✅ Good Titles:**
- `[Bug] Front left swerve module rotates in wrong direction`
- `[Feature] Add autonomous routine for three-piece auto`
- `[Hardware] CAN bus errors on motor controller ID 7`
- `[Docs] Missing instructions for installing vendor dependencies`

**❌ Poor Titles:**
- `doesn't work` (too vague)
- `URGENT HELP NEEDED!!!` (no description of the problem)
- `question` (not descriptive)
- `Fix the robot` (not specific)

### Providing Useful Information

The more information you provide, the easier it is to help you:

**For Bugs:**
- Exact error messages and stack traces (copy-paste, don't summarize)
- Step-by-step reproduction instructions
- What you expected vs. what actually happened
- Git commit hash or branch name
- Screenshots or videos showing the problem

**For Hardware Issues:**
- Component CAN IDs and physical locations
- LED status indicators
- Photos of wiring or damage
- When the issue started and what changed
- What troubleshooting you've already done

**For Feature Requests:**
- Clear explanation of the problem you're trying to solve
- How the feature would improve the robot or workflow
- Technical requirements and dependencies
- Priority and timeline considerations

## When to Use Each Communication Channel

Choose the right channel for your needs:

| Channel | Best For | Response Time |
|---------|----------|---------------|
| **GitHub Issues** | Bugs, features, hardware problems, documentation | Hours to days |
| **GitHub Discussions** | Questions, ideas, general discussion | Hours to days |
| **Direct Contact** | Urgent matters, private concerns | ASAP to 24 hours |
| **Team Meetings** | Strategy, planning, complex decisions | Weekly |
| **Driver Station Console** | Real-time debugging during practice | Immediate |

## Direct Contact

For urgent matters, private concerns, or questions that don't fit GitHub Issues, you can reach out directly to:

**Thalia**  
📧 Email: [thaliathenerd@proton.me](mailto:thaliathenerd@proton.me)

**When to use direct contact:**
- Safety-critical issues that need immediate attention
- Private team matters or concerns
- Time-sensitive competition-related questions
- Issues with GitHub access or repository permissions

**Please note:** Use direct contact sparingly and prefer GitHub Issues for transparency and team visibility. Issues posted publicly help the entire team learn and stay informed.

## Advanced Issue Reporting Tips

### Using Markdown Formatting

Make your issues easier to read by using markdown formatting:

**Code blocks:**
\`\`\`java
// Use triple backticks for code
public void periodic() {
  swerve.drive(xSpeed, ySpeed, rot);
}
\`\`\`

**Inline code:**
Use single backticks for `variable names`, `file names`, or `commands`

**Lists:**
- Bullet points for unordered lists
1. Numbers for ordered steps
- [ ] Checkboxes for task lists

**Bold and italic:**
- **Bold** for emphasis using `**text**`
- *Italic* for lighter emphasis using `*text*`

**Links:**
[Link text](https://example.com)

**Images:**
Drag and drop images directly into the issue description or comments

### Attaching Files and Logs

**Driver Station Logs:**
1. Open Driver Station
2. Click "View Logs" or navigate to `%USERPROFILE%\FRCLogs` (Windows)
3. Find the log file from when the issue occurred
4. Attach to the GitHub issue (drag and drop)

**Console Output:**
- Copy the full output from terminal/console
- Paste into a code block using triple backticks
- Don't truncate or summarize error messages

**Screenshots and Videos:**
- Take screenshots of error dialogs, unexpected behavior, or configuration screens
- Record short videos (< 1 minute) for issues that are hard to explain in text
- Drag and drop directly into the issue description

**Configuration Files:**
- If the issue involves configuration (YAGSL, PathPlanner, etc.), attach the relevant files
- Use code blocks to paste file contents
- Specify the file path so we know where it belongs

### Referencing Other Issues and PRs

Link to related issues or pull requests:
- `#123` - References issue or PR #123
- `Fixes #123` - In a PR, automatically closes issue #123 when merged
- `Related to #123` - Shows a connection without auto-closing

### Mentioning Team Members

Use `@username` to notify specific team members:
- Use sparingly to avoid notification spam
- Mention when you need input from someone specific
- Maintainers are automatically notified of all new issues

### Issue Templates from Command Line

For advanced users, you can create issues from the command line using GitHub CLI:

```bash
# Install GitHub CLI
# https://cli.github.com/

# Create a bug report
gh issue create --template bug_report.yml

# Create a feature request
gh issue create --template feature_request.yml

# Create a blank issue
gh issue create --title "Your title" --body "Your description"
```

## Common Issues and Quick Solutions

Before creating an issue, check if your problem matches one of these common scenarios:

### Build and Deployment Issues

**Problem:** `gradlew build` fails with "Could not resolve dependencies"  
**Solution:**
1. Check your internet connection
2. Run `./gradlew build --refresh-dependencies`
3. Verify vendordeps files are valid JSON
4. Check WPILib version matches project requirements

**Problem:** "Could not deploy to robot" or "RoboRIO not found"  
**Solution:**
1. Verify robot is powered on
2. Check WiFi/Ethernet connection to robot
3. Ping the RoboRIO: `ping 10.20.26.2`
4. Check firewall settings
5. Verify team number in build.gradle matches your team (2026)

**Problem:** Code compiles but robot doesn't move  
**Solution:**
1. Check Driver Station is connected and showing green
2. Verify robot is enabled (not E-stopped)
3. Check controller is connected to Driver Station
4. Review console for error messages
5. Verify CAN IDs match hardware configuration

### Hardware Troubleshooting

**Problem:** Motor controller not showing up in Phoenix Tuner  
**Solution:**
1. Verify CAN wiring is secure (yellow and green wires)
2. Check CAN bus termination (120Ω resistor at each end)
3. Power cycle the robot
4. Update motor controller firmware
5. Try a different CAN ID to rule out ID conflict

**Problem:** Swerve module spinning in place instead of driving  
**Solution:**
1. Check module wheel alignment and offsets
2. Verify drive and steering motors are on correct CAN IDs
3. Test individual modules in Phoenix Tuner
4. Review YAGSL configuration files
5. Check for inverted motor settings

**Problem:** Intermittent CAN bus errors  
**Solution:**
1. Inspect all CAN connections for loose wires
2. Check for damaged wire insulation
3. Verify CAN bus termination is present
4. Look for wire routing near motors or high-EMI areas
5. Check for proper wire gauge (22-24 AWG recommended)

### Software and Configuration Issues

**Problem:** `NullPointerException` during robot initialization  
**Solution:**
1. Check for null safety guards around hardware instantiation
2. Verify `RobotBase.isReal()` checks for simulation
3. Look for missing configuration files
4. Check constructor order and dependencies
5. Add null checks before using hardware objects

**Problem:** Code works in simulation but not on real robot  
**Solution:**
1. Verify hardware is properly configured and connected
2. Check for hardcoded values that work in sim but not reality
3. Review sensor values and ensure they're within expected ranges
4. Add logging to compare sim vs. real behavior
5. Test subsystems individually before full robot test

**Problem:** PathPlanner paths not loading  
**Solution:**
1. Verify path files are in `src/main/deploy/pathplanner/`
2. Check path file JSON is valid
3. Ensure path names match what's referenced in code
4. Review PathPlanner configuration for swerve
5. Check that vendordep is properly installed

## Escalation Path for Critical Issues

For issues that need immediate attention:

### Severity Levels

**Critical (P0):**
- Safety hazards (fire, smoke, exposed wiring)
- Robot completely inoperable before competition
- Data loss or corruption

**High (P1):**
- Major functionality broken but workarounds exist
- Competition day issues affecting performance
- Multiple subsystems affected

**Medium (P2):**
- Single subsystem degraded
- Annoying but not blocking
- Non-critical features broken

**Low (P3):**
- Minor bugs or cosmetic issues
- Future enhancements
- Documentation improvements

### Escalation Steps

1. **For P0 Critical Issues:**
   - Power down robot immediately if safety concern
   - Notify team leadership in person or via phone
   - Create GitHub issue for documentation
   - If at competition, notify pit crew and drive team

2. **For P1 High Priority Issues:**
   - Create GitHub issue with `critical` or `high-priority` label
   - Mention relevant team members who can help
   - If at competition, coordinate with pit crew
   - Monitor issue for updates

3. **For P2 and P3 Issues:**
   - Create normal GitHub issue
   - Wait for triage and prioritization
   - Participate in discussion as needed

## Additional Resources

- **[Contributing Guide](Contribguide.md)** - Guidelines for contributing code
- **[Style Guide](styleguide.md)** - Code formatting and naming conventions
- **[Commit Guide](commitguide.md)** - Git commit message standards
- **[WPILib Documentation](https://docs.wpilib.org/)** - Official FRC programming resources
- **[YAGSL Documentation](https://github.com/BroncBotz3481/YAGSL)** - Swerve drive library
- **[GitHub Markdown Guide](https://guides.github.com/features/mastering-markdown/)** - Formatting help

---

*Built by Miami Beach Bots - FRC Team 2026*
