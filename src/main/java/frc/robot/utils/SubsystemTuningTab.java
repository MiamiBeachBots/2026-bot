package frc.robot.utils;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/** Helper for exposing live-editable subsystem tuning values on Shuffleboard. */
public class SubsystemTuningTab {
  private final ShuffleboardTab m_tab;

  public SubsystemTuningTab(String subsystemName) {
    m_tab = Shuffleboard.getTab(subsystemName + " Tuning");
  }

  public TunableDouble addDouble(String title, double defaultValue) {
    return new TunableDouble(
        m_tab.addPersistent(title, defaultValue).withWidget(BuiltInWidgets.kTextView).getEntry(),
        defaultValue);
  }

  public TunableBoolean addBoolean(String title, boolean defaultValue) {
    return new TunableBoolean(
        m_tab
            .addPersistent(title, defaultValue)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry(),
        defaultValue);
  }

  public static final class TunableDouble {
    private final GenericEntry m_entry;
    private final double m_defaultValue;

    private TunableDouble(GenericEntry entry, double defaultValue) {
      m_entry = entry;
      m_defaultValue = defaultValue;
    }

    public double get() {
      return m_entry.getDouble(m_defaultValue);
    }

    public void set(double value) {
      m_entry.setDouble(value);
    }
  }

  public static final class TunableBoolean {
    private final GenericEntry m_entry;
    private final boolean m_defaultValue;

    private TunableBoolean(GenericEntry entry, boolean defaultValue) {
      m_entry = entry;
      m_defaultValue = defaultValue;
    }

    public boolean get() {
      return m_entry.getBoolean(m_defaultValue);
    }

    public void set(boolean value) {
      m_entry.setBoolean(value);
    }
  }
}
