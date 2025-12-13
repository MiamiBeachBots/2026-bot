Contributing to 2026-bot

getting Started

Clone the Repo:

git clone [https://github.com/mbbots/2026-bot.git](https://github.com/mbbots/2026-bot.git)


Install Vendor Deps:

This project uses YAGSL.

Run ./gradlew build immediately to download required libraries (Phoenix 6, REVLib).

Development Environment

For VS Code Users (eeewwwwwww)(Windows/Mac)

Install the WPILib 2025 suite.

Open the project folder in VS Code.

Accept the prompt to "Import Gradle Project".

For CLI Users (Cool kids)

Ensure you have JDK 17+ installed.

Use ./gradlew build to compile.

Use ./gradlew deploy to push to the robot.

Pull Requests

Create a new branch for your feature: git checkout -b feat/shooter-control (PLEASE OPEN BRANCHES)

Make your changes.

Test: Run ./gradlew test (if unit tests exist) or simulate.

Commit using the style guide: [Feat] Add PID control to shooter

Push and open a PR.

Code Style

We use standard Java formatting. 

Check STYLEGUIDE.md before pushing.

Do not commit commented-out code blocks. (this is like a solid idea you still can like yk good luck just like dont commit a commented file please please)

