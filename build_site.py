#!/usr/bin/env python3
"""
Miami Beach Bots – Multi-Page Static Site Generator
Generates index + 8 section pages for the tw-pages GitHub Pages site.
"""
import pathlib, textwrap

OUT = pathlib.Path("/tmp/mbb_preview")
OUT.mkdir(exist_ok=True)

CSS_PATH = "/home/thalia/2026-bot/assets/css/style.css"
CSS = pathlib.Path(CSS_PATH).read_text()

PAGES = [
    ("index.html", "Home"),
    ("status.html", "Status"),
    ("quickstart.html", "Quick Start"),
    ("build.html", "Build & Deploy"),
    ("structure.html", "Structure"),
    ("docs.html", "Documentation"),
    ("can.html", "CAN Map"),
    ("roadmap.html", "Roadmap"),
    ("team.html", "Team"),
]

def nav(active="index.html"):
    links = [
        ("index.html", "Home"),
        ("status.html", "Status"),
        ("quickstart.html", "Quick Start"),
        ("build.html", "Build & Deploy"),
        ("structure.html", "Structure"),
        ("docs.html", "Docs"),
        ("can.html", "CAN Map"),
        ("roadmap.html", "Roadmap"),
        ("team.html", "Team"),
    ]
    items = "\n".join(
        f'<a href="{href}" class="{"nav-active" if href == active else ""}">{label}</a>'
        for href, label in links
    )
    return f'<nav class="site-nav"><div class="nav-inner">{items}</div></nav>'

def wrap(title, active_page, hero_html, body_html):
    return f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>{title} | Miami Beach Bots 2026</title>
  <meta name="description" content="Miami Beach Bots FRC Team 2026 — {title}"/>
  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;900&family=JetBrains+Mono:wght@400;600&display=swap" rel="stylesheet"/>
  <style>
{CSS}

/* ── ACTIVE NAV LINK ── */
.nav-inner a.nav-active {{
  color: var(--pink-soft);
  background: rgba(255, 45, 156, 0.14);
}}

/* ── MINI HERO (sub-pages) ── */
.mini-hero {{
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(ellipse 70% 90% at 10% 20%, rgba(255, 45, 156, 0.12) 0%, transparent 55%),
    radial-gradient(ellipse 60% 80% at 90% 80%, rgba(0, 180, 255, 0.10) 0%, transparent 55%),
    linear-gradient(180deg, #0b0718 0%, #080c1c 100%);
  padding: 64px 24px 52px;
  text-align: center;
  border-bottom: 1px solid var(--border);
}}
.mini-hero .header-badge {{ margin-bottom: 18px; }}
.mini-hero h1.page-title {{
  font-size: clamp(1.8rem, 5vw, 3.2rem);
  font-weight: 900;
  letter-spacing: -0.025em;
  background: linear-gradient(110deg, var(--pink) 0%, #dd88ff 45%, var(--blue) 100%);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
  margin: 0 0 10px;
}}
.mini-hero p.page-lead {{
  color: var(--text-dim); font-size: 1rem; max-width: 600px; margin: 0 auto;
}}
.mini-deco {{ position: absolute; inset: 0; pointer-events: none; }}
.mini-deco .deco-bar {{
  position: absolute; bottom: 0; left: 0; right: 0; height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--pink) 35%, var(--blue) 65%, transparent 100%);
  opacity: 0.5;
}}

/* ── INFO CARDS ── */
.card-grid {{
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin: 2rem 0;
}}
.card {{
  background: var(--surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 24px 22px;
  text-decoration: none !important;
  transition: transform 0.18s, border-color 0.18s, box-shadow 0.18s;
  display: flex;
  flex-direction: column;
  gap: 8px;
}}
.card:hover {{
  transform: translateY(-4px);
  border-color: var(--pink);
  box-shadow: 0 8px 32px var(--pink-glow);
  text-decoration: none !important;
}}
.card-icon {{ font-size: 1.6rem; }}
.card-title {{ font-size: 1rem; font-weight: 700; color: var(--text); }}
.card-desc {{ font-size: 0.85rem; color: var(--text-dim); line-height: 1.5; }}

/* ── STATUS PILLS ── */
.pill {{
  display: inline-block;
  padding: 3px 12px;
  border-radius: 999px;
  font-size: 0.74rem;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}}
.pill-green  {{ background: rgba(0, 230, 120, 0.15); color: #00e678; border: 1px solid rgba(0,230,120,0.3); }}
.pill-yellow {{ background: rgba(255, 200, 0, 0.12); color: #ffd000; border: 1px solid rgba(255,200,0,0.3); }}
.pill-red    {{ background: rgba(255, 60, 60, 0.12);  color: #ff6060; border: 1px solid rgba(255,60,60,0.3); }}
.pill-blue   {{ background: rgba(0, 180, 255, 0.12);  color: var(--blue-soft); border: 1px solid rgba(0,180,255,0.3); }}
.pill-pink   {{ background: rgba(255, 45, 156, 0.12); color: var(--pink-soft); border: 1px solid rgba(255,45,156,0.3); }}

/* ── STATUS ROW TABLE ── */
.status-table {{ display: flex; flex-direction: column; gap: 1px; margin: 1.5rem 0; }}
.status-row {{
  display: flex; align-items: center; gap: 16px;
  padding: 14px 18px;
  background: var(--surface-2);
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
}}
.status-row:first-child {{ border-radius: var(--radius) var(--radius) var(--radius-sm) var(--radius-sm); }}
.status-row:last-child  {{ border-radius: var(--radius-sm) var(--radius-sm) var(--radius) var(--radius); }}
.status-row-label {{ flex: 1; color: var(--text); }}

/* ── STEP TIMELINE ── */
.steps {{ display: flex; flex-direction: column; gap: 4px; margin: 2rem 0; }}
.step {{
  display: flex; gap: 20px; align-items: flex-start;
  padding: 16px 18px;
  background: var(--surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  transition: border-color 0.15s;
}}
.step:hover {{ border-color: var(--blue); }}
.step-num {{
  flex-shrink: 0;
  width: 32px; height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--pink-dim), var(--blue-dim));
  color: #fff;
  font-size: 0.82rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}}
.step-body {{ flex: 1; }}
.step-title {{ font-weight: 700; color: var(--text); margin-bottom: 4px; font-size: 0.95rem; }}
.step-desc {{ color: var(--text-dim); font-size: 0.87rem; line-height: 1.55; }}

/* HOME HERO additions */
.home-hero {{ padding: 110px 24px 90px; }}
.home-subtitle {{ font-size: 0.9rem; color: var(--text-muted); margin-top: 16px; letter-spacing: 0.1em; text-transform: uppercase; }}

  </style>
</head>
<body>
  <header class="site-header {'home-hero' if active_page == 'index.html' else 'mini-hero'}">
    {hero_html}
    <div class="{'header-decoration' if active_page == 'index.html' else 'mini-deco'}">
      {"<div class='deco-circle deco-1'></div><div class='deco-circle deco-2'></div>" if active_page == "index.html" else ""}
      <div class="deco-bar"></div>
    </div>
  </header>
  {nav(active_page)}
  <main class="main-content"><article class="page-body">{body_html}</article></main>
  <footer class="site-footer">
    <div class="footer-inner">
      <p class="footer-brand">Miami Beach Bots &mdash; FRC Team 2026 &bull; Miami Beach, FL</p>
      <p class="footer-sub">Built with ❤️ by student engineers &bull; <a href="https://github.com/MiamiBeachBots/2026-bot" target="_blank">GitHub</a></p>
    </div>
  </footer>
</body>
</html>"""

# ══════════════════════════════════════════════════════════════
#  PAGE DEFINITIONS
# ══════════════════════════════════════════════════════════════

pages = {}

# ── INDEX ─────────────────────────────────────────────────────
pages["index.html"] = wrap("Home", "index.html",
  hero_html="""
    <div class="header-grid">
      <div class="header-badge">FRC 2026</div>
      <h1 class="header-title">Miami Beach Bots</h1>
      <p class="header-subtitle">2026-bot &mdash; <em>Aut viam inveniam aut faciam.</em></p>
      <div class="header-actions">
        <a href="https://github.com/MiamiBeachBots/2026-bot" class="btn btn-primary" target="_blank">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.387-1.333-1.757-1.333-1.757-1.09-.745.083-.729.083-.729 1.205.085 1.84 1.237 1.84 1.237 1.07 1.835 2.81 1.305 3.495.998.108-.776.418-1.305.762-1.605-2.665-.3-5.467-1.332-5.467-5.93 0-1.31.468-2.38 1.235-3.22-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.3 1.23a11.52 11.52 0 0 1 3.003-.404 11.52 11.52 0 0 1 3.003.404c2.29-1.552 3.297-1.23 3.297-1.23.653 1.652.242 2.873.118 3.176.77.84 1.235 1.91 1.235 3.22 0 4.61-2.807 5.625-5.48 5.92.43.372.815 1.102.815 2.222 0 1.606-.015 2.898-.015 3.293 0 .321.217.694.825.576C20.565 21.795 24 17.298 24 12c0-6.63-5.37-12-12-12z"/></svg>
          View on GitHub
        </a>
        <a href="quickstart.html" class="btn btn-ghost">Get Started →</a>
      </div>
      <p class="home-subtitle">Java 17 &bull; WPILib 2025 &bull; PathPlanner &bull; PhotonVision &bull; Swerve Drive</p>
    </div>""",
  body_html="""
    <h2>Welcome to 2026-bot</h2>
    <p>This is the official codebase for <strong>Miami Beach Bots, FRC Team 2026</strong>, competing in the 2025-2026 FIRST Robotics Competition season playing <em>REBUILT</em>. Navigate the sections below to explore every aspect of our robot software stack.</p>

    <div class="card-grid">
      <a href="status.html" class="card">
        <span class="card-icon">🚦</span>
        <span class="card-title">Project Status</span>
        <span class="card-desc">Current development state, milestone tracking, and active feature flags.</span>
      </a>
      <a href="quickstart.html" class="card">
        <span class="card-icon">⚡</span>
        <span class="card-title">Quick Start</span>
        <span class="card-desc">Clone the repo, install dependencies, and get your first build running in minutes.</span>
      </a>
      <a href="build.html" class="card">
        <span class="card-icon">🔧</span>
        <span class="card-title">Build & Deploy</span>
        <span class="card-desc">Compile, simulate, and deploy robot code to the RoboRIO over WiFi or Ethernet.</span>
      </a>
      <a href="structure.html" class="card">
        <span class="card-icon">📁</span>
        <span class="card-title">Project Structure</span>
        <span class="card-desc">A complete walkthrough of the directory layout, subsystems, and command architecture.</span>
      </a>
      <a href="docs.html" class="card">
        <span class="card-icon">📖</span>
        <span class="card-title">Documentation</span>
        <span class="card-desc">Contributing guide, style guide, commit standards, and assist resources.</span>
      </a>
      <a href="can.html" class="card">
        <span class="card-icon">🔌</span>
        <span class="card-title">CAN Bus Map</span>
        <span class="card-desc">Full CAN ID assignments for every motor controller and sensor on the robot.</span>
      </a>
      <a href="roadmap.html" class="card">
        <span class="card-icon">🗺️</span>
        <span class="card-title">Roadmap & TODO</span>
        <span class="card-desc">Planned features, in-progress work, and completed milestones.</span>
      </a>
      <a href="team.html" class="card">
        <span class="card-icon">🏝️</span>
        <span class="card-title">Team & Sponsors</span>
        <span class="card-desc">Meet the Miami Beach Bots and celebrate our generous sponsors.</span>
      </a>
    </div>

    <h2>Technology Stack</h2>
    <table>
      <thead><tr><th>Layer</th><th>Technology</th><th>Role</th></tr></thead>
      <tbody>
        <tr><td>Robot Controller</td><td><strong>WPILib 2025 / Java 17</strong></td><td>Core robot logic, subsystems, commands</td></tr>
        <tr><td>Autonomous</td><td><strong>PathPlanner</strong></td><td>Trajectory generation &amp; on-the-fly pathfinding</td></tr>
        <tr><td>Vision</td><td><strong>PhotonVision + AprilTags</strong></td><td>Absolute pose estimation on the field</td></tr>
        <tr><td>Motor Control</td><td><strong>CTRE Phoenix 6 / REVLib</strong></td><td>NEO &amp; Falcon 500 controllers</td></tr>
        <tr><td>AI Navigator</td><td><strong>Python / Ollama LLM</strong></td><td>Natural-language-to-pose translation on Driver Station</td></tr>
        <tr><td>ML Vision</td><td><strong>Google Coral Edge TPU</strong></td><td>Real-time game piece detection on Orange Pi</td></tr>
      </tbody>
    </table>
""")

# ── STATUS ────────────────────────────────────────────────────
pages["status.html"] = wrap("Project Status", "status.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Project Status</div>
    <h1 class="page-title">Development Status</h1>
    <p class="page-lead">Current milestone tracking and feature completion for the 2025-2026 season.</p>
  </div>""",
  body_html="""
    <h2>Overall State</h2>
    <p><strong>Current Phase:</strong> <span class="pill pill-yellow">Pre-Alpha / In Development</span></p>
    <p>The 2026-bot codebase is under active development targeting the REBUILT game. Major subsystems are being implemented and tested iteratively.</p>

    <h2>Feature Milestones</h2>
    <div class="status-table">
      <div class="status-row"><span class="status-row-label">Tank drive base initialized</span><span class="pill pill-green">Complete</span></div>
      <div class="status-row"><span class="status-row-label">Basic project structure &amp; Gradle configured</span><span class="pill pill-green">Complete</span></div>
      <div class="status-row"><span class="status-row-label">CAN ID assignments finalized</span><span class="pill pill-green">Complete</span></div>
      <div class="status-row"><span class="status-row-label">PhotonVision AprilTag pipeline</span><span class="pill pill-green">Complete</span></div>
      <div class="status-row"><span class="status-row-label">Driver Station controller mappings</span><span class="pill pill-green">Complete</span></div>
      <div class="status-row"><span class="status-row-label">Tank base physical testing</span><span class="pill pill-yellow">In Progress</span></div>
      <div class="status-row"><span class="status-row-label">FireControl flywheel calibration</span><span class="pill pill-yellow">In Progress</span></div>
      <div class="status-row"><span class="status-row-label">PathPlanner autonomous routines</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">LLM Commander integration</span><span class="pill pill-yellow">In Progress</span></div>
      <div class="status-row"><span class="status-row-label">Google Coral Fuel detection</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Full swerve drive conversion</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Electrical documentation</span><span class="pill pill-red">Pending</span></div>
    </div>

    <h2>Active Branches</h2>
    <table>
      <thead><tr><th>Branch</th><th>Purpose</th><th>State</th></tr></thead>
      <tbody>
        <tr><td><code>main</code></td><td>Stable production code</td><td><span class="pill pill-green">Stable</span></td></tr>
        <tr><td><code>tw-MLdrive</code></td><td>LLM Commander + PathPlanner Navigator</td><td><span class="pill pill-blue">Review Ready</span></td></tr>
        <tr><td><code>tw-april</code></td><td>AprilTag triangulation + Field2d visualization</td><td><span class="pill pill-blue">Review Ready</span></td></tr>
        <tr><td><code>tw-pages</code></td><td>GitHub Pages custom theme (this site)</td><td><span class="pill pill-pink">Active</span></td></tr>
      </tbody>
    </table>

    <h2>Known Issues</h2>
    <blockquote><strong>[Warning]</strong> CAN bus IDs are configured but physically untested on the new chassis. Motor direction may need reversing after first connect.</blockquote>
    <blockquote><strong>[Warning]</strong> The LLM Commander depends on Ollama running locally on the Framework Laptop. Ensure Ollama is started before each match.</blockquote>
""")

# ── QUICK START ───────────────────────────────────────────────
pages["quickstart.html"] = wrap("Quick Start", "quickstart.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Get Started</div>
    <h1 class="page-title">Quick Start</h1>
    <p class="page-lead">Everything you need to go from zero to a running build in under 10 minutes.</p>
  </div>""",
  body_html="""
    <h2>Prerequisites</h2>
    <table>
      <thead><tr><th>Dependency</th><th>Version</th><th>Download</th></tr></thead>
      <tbody>
        <tr><td><strong>Java Development Kit</strong></td><td>17 or higher</td><td><a href="https://adoptium.net/" target="_blank">adoptium.net</a></td></tr>
        <tr><td><strong>WPILib Suite</strong></td><td>2025 release</td><td><a href="https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html" target="_blank">WPILib Docs</a></td></tr>
        <tr><td><strong>Git</strong></td><td>Any modern version</td><td><a href="https://git-scm.com/" target="_blank">git-scm.com</a></td></tr>
        <tr><td><strong>Python</strong></td><td>3.10+</td><td>For LLM Commander only</td></tr>
        <tr><td><strong>Ollama</strong></td><td>Latest</td><td><a href="https://ollama.com" target="_blank">ollama.com</a></td></tr>
      </tbody>
    </table>

    <h2>Installation Steps</h2>
    <div class="steps">
      <div class="step"><div class="step-num">1</div><div class="step-body"><div class="step-title">Clone the repository</div><div class="step-desc"><code>git clone https://github.com/MiamiBeachBots/2026-bot.git &amp;&amp; cd 2026-bot</code></div></div></div>
      <div class="step"><div class="step-num">2</div><div class="step-body"><div class="step-title">Create a feature branch</div><div class="step-desc">Always branch off <code>main</code>: <code>git checkout -b feature/your-feature-name</code></div></div></div>
      <div class="step"><div class="step-num">3</div><div class="step-body"><div class="step-title">Build the Java project</div><div class="step-desc"><code>./gradlew build</code> — downloads all vendor deps and compiles. Expect ~2 min on first run.</div></div></div>
      <div class="step"><div class="step-num">4</div><div class="step-body"><div class="step-title">Run in simulation</div><div class="step-desc"><code>./gradlew simulateJava</code> — runs the robot code locally without hardware.</div></div></div>
      <div class="step"><div class="step-num">5</div><div class="step-body"><div class="step-title">(Optional) Set up LLM Commander</div><div class="step-desc"><code>cd driver_station_llm &amp;&amp; pip install -r requirements.txt</code>, then <code>python driver_station_llm.py</code></div></div></div>
    </div>

    <h2>Vendor Dependencies</h2>
    <p>All vendor libraries are auto-managed via <code>vendordeps/</code> JSON files and fetched by Gradle on first build.</p>
    <table>
      <thead><tr><th>Vendor</th><th>Library</th><th>Purpose</th></tr></thead>
      <tbody>
        <tr><td>CTRE</td><td>Phoenix 6</td><td>TalonFX, CANcoder, Pigeon 2</td></tr>
        <tr><td>CTRE</td><td>Phoenix 5</td><td>Legacy Talon SRX devices</td></tr>
        <tr><td>REV Robotics</td><td>REVLib</td><td>Spark MAX / NEO motor controllers</td></tr>
        <tr><td>PathPlanner</td><td>PathPlanner</td><td>Autonomous trajectory &amp; pathfinding</td></tr>
        <tr><td>Redux</td><td>ReduxLib</td><td>Additional sensor utilities</td></tr>
        <tr><td>Studica</td><td>StudicaLib</td><td>Hardware abstraction</td></tr>
        <tr><td>Maple</td><td>Maple-Sim</td><td>Swerve &amp; robot simulation</td></tr>
      </tbody>
    </table>

    <h2>Branching Model</h2>
    <p>We use a <strong>feature-branch model</strong>. All contributors clone and branch directly from <code>main</code> — no forks needed. Name branches as <code>tw-featurename</code> or <code>feature/description</code>.</p>
    <blockquote>Never commit directly to <code>main</code>. Open a Pull Request and request a code review from at least one other member.</blockquote>
""")

# ── BUILD ─────────────────────────────────────────────────────
pages["build.html"] = wrap("Build & Deploy", "build.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Build & Deploy</div>
    <h1 class="page-title">Build &amp; Deploy</h1>
    <p class="page-lead">Compile, simulate, and push code to the robot from your development machine.</p>
  </div>""",
  body_html="""
    <h2>Building the Code</h2>
    <p>Use Gradle to compile the robot code locally. The WPILib Gradle toolchain handles Java compilation and vendor dependency resolution.</p>
    <pre><code># Full build (compile + test)
./gradlew build

# Compile only (faster, skips tests)
./gradlew compileJava

# Run code style checks
./gradlew spotlessCheck

# Auto-fix code style
./gradlew spotlessApply</code></pre>

    <h2>Simulation</h2>
    <p>Test the robot code without physical hardware using WPILib's robot simulation. Open <strong>Glass</strong> or <strong>SmartDashboard</strong> alongside to interact with NetworkTables.</p>
    <pre><code>./gradlew simulateJava</code></pre>
    <p>Once simulation is running, open <strong>Glass</strong> (bundled with WPILib) to see the <code>Field2d</code> widget for live pose visualization, and use <strong>OutlineViewer</strong> to inspect any NetworkTable values.</p>

    <h2>Deploying to the Robot</h2>
    <div class="steps">
      <div class="step"><div class="step-num">1</div><div class="step-body"><div class="step-title">Connect to robot WiFi</div><div class="step-desc">Join the <code>2026</code> robot access point, or connect via Ethernet to the radio. Robot IP: <code>10.20.26.2</code></div></div></div>
      <div class="step"><div class="step-num">2</div><div class="step-body"><div class="step-title">Deploy</div><div class="step-desc"><code>./gradlew deploy</code> — compiles and pushes the JAR to the RoboRIO automatically.</div></div></div>
      <div class="step"><div class="step-num">3</div><div class="step-body"><div class="step-title">Enable the robot</div><div class="step-desc">Open the FRC Driver Station app and enable Teleop or Auto. Check the rio console for errors.</div></div></div>
    </div>

    <h2>CI / GitHub Actions</h2>
    <p>Every push to <code>main</code> and every Pull Request triggers a build check via GitHub Actions. The workflow lives at <code>.github/workflows/main.yml</code>.</p>
    <pre><code># The CI pipeline runs:
./gradlew build</code></pre>
    <blockquote>If CI is red, your PR cannot be merged. Fix all compilation errors and style violations before requesting review.</blockquote>

    <h2>Deploying the LLM Commander</h2>
    <p>The Python LLM Commander runs on the Framework Laptop at the Driver Station. Set it up with:</p>
    <pre><code>cd driver_station_llm
pip install -r requirements.txt
# Start Ollama first:
ollama run llama3
# Then run the commander:
python driver_station_llm.py</code></pre>
""")

# ── STRUCTURE ─────────────────────────────────────────────────
pages["structure.html"] = wrap("Project Structure", "structure.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Architecture</div>
    <h1 class="page-title">Project Structure</h1>
    <p class="page-lead">A deep dive into every directory, subsystem, and command in the codebase.</p>
  </div>""",
  body_html="""
    <h2>Top-Level Layout</h2>
    <pre><code>2026-bot/
├── src/                        # All source code
│   └── main/
│       ├── java/frc/robot/     # Java robot code
│       └── deploy/             # Config files deployed to RoboRIO
├── driver_station_llm/         # Python LLM Commander (Driver Station)
├── coral/                      # Google Coral Edge TPU ML models
├── dashboard/                  # Custom dashboard assets
├── vendordeps/                 # Vendor dependency JSON files
├── build.gradle                # Gradle build config
├── _layouts/                   # Jekyll HTML layouts (GitHub Pages)
└── assets/css/style.css        # GitHub Pages custom CSS theme</code></pre>

    <h2>Java Robot Code</h2>
    <pre><code>src/main/java/frc/robot/
├── Robot.java                  # Main robot lifecycle (init, periodic hooks)
├── RobotContainer.java         # Subsystem wiring + button bindings
├── Main.java                   # Entry point (do not modify)
├── DriveConstants.java         # Swerve drive tuning constants
├── AutoAimConstants.java       # Turret + auto-aim PID constants
│
├── commands/
│   ├── LLMDriveCommand.java    # Reads NT target pose → PathPlanner trajectory
│   └── ...                     # Additional commands
│
├── subsystems/
│   ├── DriveSubsystem.java     # Swerve drive base + odometry
│   ├── VisionSubsystem.java    # PhotonVision AprilTag pose estimation
│   ├── IntakeSubsystem.java    # Fuel intake mechanism
│   ├── LoaderSubsystem.java    # Fuel loader conveyor
│   ├── FireControlSubsystem.java # Turret + flywheel fire control
│   └── ...
│
└── utils/                      # Utility helpers (math, logging, etc.)</code></pre>

    <h2>Auto-Aim Pipeline</h2>
    <p>The fire control system uses a two-stage pipeline: vision targeting then a profiled PID + feedforward to spin up and fire.</p>
    <table>
      <thead><tr><th>Stage</th><th>Class</th><th>Output</th></tr></thead>
      <tbody>
        <tr><td>Pose input</td><td><code>DriveSubsystem</code></td><td>Current <code>Pose2d</code></td></tr>
        <tr><td>Target input</td><td><code>VisionSubsystem</code></td><td>Target <code>Pose3d</code></td></tr>
        <tr><td>Lead calculation</td><td><code>AutoAimCalculations</code></td><td>Robot-relative yaw offset</td></tr>
        <tr><td>PID control</td><td><code>ProfiledPIDController</code></td><td>PID voltage output</td></tr>
        <tr><td>Feedforward</td><td><code>SimpleMotorFeedforward</code></td><td>FF voltage</td></tr>
        <tr><td>Motor output</td><td><code>TalonFX (Turret)</code></td><td>Combined setVoltage</td></tr>
      </tbody>
    </table>

    <h2>LLM + Vision Pipeline</h2>
    <div class="steps">
      <div class="step"><div class="step-num">1</div><div class="step-body"><div class="step-title">Natural Language Input</div><div class="step-desc">Operator types a command into the Framework Laptop terminal (e.g. "Drive under the trench").</div></div></div>
      <div class="step"><div class="step-num">2</div><div class="step-body"><div class="step-title">Ollama LLM on Driver Station</div><div class="step-desc"><code>driver_station_llm.py</code> sends the command + REBUILT field context to Llama 3, receives a JSON <code>{{targetX, targetY, targetRotation}}</code>.</div></div></div>
      <div class="step"><div class="step-num">3</div><div class="step-body"><div class="step-title">NetworkTables Broadcast</div><div class="step-desc">The coordinates are published to the <code>LLMTarget</code> NT4 table at ~50 Hz.</div></div></div>
      <div class="step"><div class="step-num">4</div><div class="step-body"><div class="step-title">LLMDriveCommand on RoboRIO</div><div class="step-desc">When new coordinates appear, <code>LLMDriveCommand</code> cancels the previous path and schedules a new <code>AutoBuilder.pathfindToPose()</code> trajectory.</div></div></div>
      <div class="step"><div class="step-num">5</div><div class="step-body"><div class="step-title">AprilTag Fusion</div><div class="step-desc"><code>VisionSubsystem</code> feeds PhotonVision estimates into the <code>SwerveDrivePoseEstimator</code> continuously, keeping odometry accurate.</div></div></div>
    </div>
""")

# ── DOCS ─────────────────────────────────────────────────────
pages["docs.html"] = wrap("Documentation", "docs.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Documentation</div>
    <h1 class="page-title">Documentation</h1>
    <p class="page-lead">Contributing guides, coding standards, and team practices.</p>
  </div>""",
  body_html="""
    <h2>Key Documents</h2>
    <div class="card-grid">
      <a href="https://github.com/MiamiBeachBots/2026-bot/blob/main/Contribguide.md" class="card" target="_blank">
        <span class="card-icon">🤝</span>
        <span class="card-title">Contributing Guide</span>
        <span class="card-desc">How to open issues, create branches, write PRs, and get your code merged.</span>
      </a>
      <a href="https://github.com/MiamiBeachBots/2026-bot/blob/main/styleguide.md" class="card" target="_blank">
        <span class="card-icon">🎨</span>
        <span class="card-title">Style Guide</span>
        <span class="card-desc">Java formatting, naming conventions, and Spotless configuration.</span>
      </a>
      <a href="https://github.com/MiamiBeachBots/2026-bot/blob/main/commitguide.md" class="card" target="_blank">
        <span class="card-icon">💬</span>
        <span class="card-title">Commit Guide</span>
        <span class="card-desc">Conventional commit format for clean, semantic git history.</span>
      </a>
      <a href="https://github.com/MiamiBeachBots/2026-bot/blob/main/Assist.md" class="card" target="_blank">
        <span class="card-icon">🆘</span>
        <span class="card-title">Assist Guide</span>
        <span class="card-desc">How to get help, report bugs, and escalate blocking issues.</span>
      </a>
    </div>

    <h2>Commit Message Format</h2>
    <p>We follow <strong>Conventional Commits</strong>. Every commit message must start with a type prefix:</p>
    <table>
      <thead><tr><th>Type</th><th>When to Use</th></tr></thead>
      <tbody>
        <tr><td><code>feat:</code></td><td>New feature or significant new behavior</td></tr>
        <tr><td><code>fix:</code></td><td>Bug fix</td></tr>
        <tr><td><code>refactor:</code></td><td>Code restructure without behavior change</td></tr>
        <tr><td><code>chore:</code></td><td>Dependency updates, build config, tooling</td></tr>
        <tr><td><code>docs:</code></td><td>Documentation-only changes</td></tr>
        <tr><td><code>test:</code></td><td>Adding or modifying tests</td></tr>
        <tr><td><code>style:</code></td><td>Formatting, whitespace (no logic change)</td></tr>
      </tbody>
    </table>
    <pre><code>feat: Add VisionSubsystem for AprilTag triangulation
fix: Correct turret PID gains for hub aiming
chore: Bump WPILib to 2025.3.1</code></pre>

    <h2>Code Style</h2>
    <p>All Java code is auto-formatted by <strong>Spotless</strong> using the Google Java Format. Run before every commit:</p>
    <pre><code>./gradlew spotlessApply</code></pre>
    <p>Key rules from <a href="https://github.com/MiamiBeachBots/2026-bot/blob/main/styleguide.md" target="_blank">styleguide.md</a>:</p>
    <ul>
      <li>2-space indentation, no tabs</li>
      <li>Max line length: 120 characters</li>
      <li><code>camelCase</code> for variables &amp; methods, <code>PascalCase</code> for classes, <code>UPPER_SNAKE</code> for constants</li>
      <li>All public methods must have Javadoc comments</li>
    </ul>
""")

# ── CAN ──────────────────────────────────────────────────────
pages["can.html"] = wrap("CAN Bus Map", "can.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Electronics</div>
    <h1 class="page-title">CAN Bus Map</h1>
    <p class="page-lead">Complete CAN ID assignments for all motor controllers and sensors on the robot.</p>
  </div>""",
  body_html="""
    <h2>CAN ID Assignments</h2>
    <blockquote><strong>Note:</strong> CAN IDs are configured in code but physically untested on the new chassis. Verify directions after first enable.</blockquote>
    <table>
      <thead><tr><th>CAN ID</th><th>Subsystem</th><th>Device / Motor Name</th><th>Type</th></tr></thead>
      <tbody>
        <tr><td><strong>1</strong></td><td>DriveTrain</td><td>Front Right Drive Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>2</strong></td><td>DriveTrain</td><td>Back Right Drive Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>3</strong></td><td>DriveTrain</td><td>Front Left Drive Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>4</strong></td><td>DriveTrain</td><td>Back Left Drive Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>5</strong></td><td>Intake</td><td>Intake Main Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>6</strong></td><td>Intake</td><td>Intake Secondary Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>7</strong></td><td>Loader</td><td>Loader Motor 1</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>8</strong></td><td>Loader</td><td>Loader Motor 2</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>9</strong></td><td>Loader</td><td>Loader Motor 3</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>10</strong></td><td>Turret</td><td>Turret Rotation Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
        <tr><td><strong>11</strong></td><td>FireControl</td><td>Fire Kicker Motor</td><td>NEO Brushless (Spark MAX)</td></tr>
      </tbody>
    </table>

    <h2>CAN Bus Architecture</h2>
    <p>All devices communicate over the <strong>RoboRIO's built-in CAN bus</strong>. The bus is terminated at both ends. Device IDs are set via REV Hardware Client (Spark MAX) or Phoenix Tuner X (CTRE devices).</p>
    <table>
      <thead><tr><th>Device Type</th><th>Configuration Tool</th><th>Update Rate</th></tr></thead>
      <tbody>
        <tr><td>Spark MAX (REV)</td><td>REV Hardware Client</td><td>50 Hz (20ms)</td></tr>
        <tr><td>TalonFX (CTRE)</td><td>Phoenix Tuner X</td><td>100 Hz (10ms) via Phoenix 6</td></tr>
        <tr><td>CANcoder (CTRE)</td><td>Phoenix Tuner X</td><td>100 Hz</td></tr>
        <tr><td>Pigeon 2.0 (CTRE)</td><td>Phoenix Tuner X</td><td>100 Hz</td></tr>
      </tbody>
    </table>

    <h2>Electrical Notes</h2>
    <ul>
      <li>Total estimated current draw at peak: ~180A. Main breaker: <strong>120A</strong>.</li>
      <li>PDH (Power Distribution Hub) slot assignments should match CAN IDs where possible.</li>
      <li>All signal wires (CAN H/L) should be twisted-pair and routed away from motor leads.</li>
      <li>A 120Ω termination resistor must be placed at the far end of the CAN bus loop.</li>
    </ul>
""")

# ── ROADMAP ───────────────────────────────────────────────────
pages["roadmap.html"] = wrap("Roadmap", "roadmap.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Roadmap</div>
    <h1 class="page-title">Roadmap &amp; TODO</h1>
    <p class="page-lead">Planned features, in-progress work, and completed milestones for the 2025-2026 season.</p>
  </div>""",
  body_html="""
    <h2>Hardware Integration</h2>
    <div class="status-table">
      <div class="status-row"><span class="status-row-label">Verify tank drive motor CAN IDs and configurations</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Test individual drive motors via Phoenix Tuner / REV Client</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Verify encoder directions on swerve modules</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Swerve drive physical tuning (PID + FF)</span><span class="pill pill-yellow">In Progress</span></div>
    </div>

    <h2>Subsystems</h2>
    <div class="status-table">
      <div class="status-row"><span class="status-row-label">Initialize empty subsystems and placeholder files</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Complete tank drive testing</span><span class="pill pill-yellow">In Progress</span></div>
      <div class="status-row"><span class="status-row-label">Calibrate FireControlSubsystem motor speeds</span><span class="pill pill-yellow">In Progress</span></div>
      <div class="status-row"><span class="status-row-label">Implement Fuel intake + loader conveyor sequencing</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Climber mechanism (TBD if in game)</span><span class="pill pill-red">Pending</span></div>
    </div>

    <h2>Autonomous</h2>
    <div class="status-table">
      <div class="status-row"><span class="status-row-label">Initialize AutoAimCommand structure</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Configure PathPlanner NavGrid for REBUILT field obstacles</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Develop Left autonomous path</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Develop Center autonomous path</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Develop Right autonomous path</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">LLM Commander natural language pathfinding</span><span class="pill pill-yellow">In Progress</span></div>
    </div>

    <h2>Vision &amp; AI</h2>
    <div class="status-table">
      <div class="status-row"><span class="status-row-label">Set up PhotonVision on Orange Pi coprocessor</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Implement AprilTag pose estimation (VisionSubsystem)</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Field2d pose visualization in Glass</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Google Coral Fuel detection model training</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Real-time game piece detection pipeline</span><span class="pill pill-red">Pending</span></div>
    </div>

    <h2>Documentation</h2>
    <div class="status-table">
      <div class="status-row"><span class="status-row-label">Finalize README.md structure and badges</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Complete CAN bus map</span><span class="pill pill-green">Done</span></div>
      <div class="status-row"><span class="status-row-label">Document electrical connections &amp; wire routing</span><span class="pill pill-red">Pending</span></div>
      <div class="status-row"><span class="status-row-label">Add full Javadoc to all subsystems and commands</span><span class="pill pill-red">Pending</span></div>
    </div>
""")

# ── TEAM ─────────────────────────────────────────────────────
pages["team.html"] = wrap("Team & Sponsors", "team.html",
  hero_html="""<div class="header-grid">
    <div class="header-badge">Team 2026</div>
    <h1 class="page-title">Team &amp; Sponsors</h1>
    <p class="page-lead">The student engineers and generous organizations making Miami Beach Bots possible.</p>
  </div>""",
  body_html="""
    <h2>Miami Beach Bots — FRC Team 2026</h2>
    <p>We are a competitive FIRST Robotics Competition team based at <strong>Miami Beach Senior High School</strong> in Miami Beach, Florida. Our team motto — <em>Aut viam inveniam aut faciam</em> ("I will either find a way or make one") — drives everything we build.</p>

    <h2>Software Team Roles</h2>
    <table>
      <thead><tr><th>Role</th><th>Responsibilities</th></tr></thead>
      <tbody>
        <tr><td><strong>Lead Software Mentor</strong></td><td>Architecture decisions, code review, mentoring student devs</td></tr>
        <tr><td><strong>Robot Controls Lead</strong></td><td>Subsystems, commands, driver station integration</td></tr>
        <tr><td><strong>Vision Lead</strong></td><td>PhotonVision, AprilTags, camera calibration</td></tr>
        <tr><td><strong>Autonomous Lead</strong></td><td>PathPlanner routes, auto sequences, field strategy</td></tr>
        <tr><td><strong>AI/ML Engineering</strong></td><td>Coral TPU, Ollama LLM Commander, model training</td></tr>
      </tbody>
    </table>

    <h2>Our Sponsors</h2>
    <p>We are incredibly grateful for the support of our sponsors. Their contributions — financial, material, and in-kind — make our season possible.</p>
    <table>
      <thead><tr><th>Sponsor</th><th>Contribution</th></tr></thead>
      <tbody>
        <tr><td><strong>Gene Haas Foundation</strong></td><td>Primary engineering scholarship sponsor</td></tr>
        <tr><td><strong>Waldom Electronics</strong></td><td>Electronics components &amp; supplier support</td></tr>
        <tr><td><strong>Give Miami Day</strong></td><td>Community fundraising platform</td></tr>
        <tr><td><strong>Intuitive Foundation</strong></td><td>STEM education grant</td></tr>
        <tr><td><strong>MDCPS</strong></td><td>School district support &amp; facilities</td></tr>
        <tr><td><strong>Cordyceps Systems</strong></td><td>Software &amp; robotics hardware</td></tr>
        <tr><td><strong>MBSH PTSA</strong></td><td>Parent &amp; community association support</td></tr>
        <tr><td><strong>FIRST Robotics</strong></td><td>Competition organization &amp; program support</td></tr>
        <tr><td><strong>Metal Supermarkets</strong></td><td>Raw materials &amp; fabrication stock</td></tr>
      </tbody>
    </table>

    <blockquote>Interested in sponsoring Miami Beach Bots? Reach out to our team administration! Your support directly funds parts, travel, and registration for student engineers.</blockquote>

    <h2>FIRST Robotics Competition</h2>
    <p>FIRST (For Inspiration and Recognition of Science and Technology) is a global robotics competition for high school students. Each January, a new game is revealed with unique rules and field elements. Teams have <strong>6 weeks</strong> to design, build, program, and test a 125 lb robot that competes in regional and championship events.</p>
    <p>Learn more at <a href="https://www.firstinspires.org/" target="_blank">firstinspires.org</a>.</p>
""")

# ── Write all pages ───────────────────────────────────────────
for filename, content in pages.items():
    (OUT / filename).write_text(content)
    print(f"  Built: {filename}")

print(f"\n✅ All {len(pages)} pages built in {OUT}")
