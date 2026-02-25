#!/usr/bin/env python3
import time
import sys
import os
import json
import argparse
from datetime import datetime

try:
    from networktables import NetworkTables
except ImportError:
    print("\033[91mError: pynetworktables is not installed!\033[0m")
    print("Please install it by running:  pip install -r requirements.txt")
    sys.exit(1)

TEAM_NUMBER = 2026  # Change if team number differs
SERVER_IP = f"10.{TEAM_NUMBER // 100}.{TEAM_NUMBER % 100}.2"

# Values we explicitly do NOT want to backup because they are live telemetry, not configuration
IGNORED_ENDINGS = [
    "_Temp", "_OK", "Connected", "Voltage", "Speed Output", "Position", "Errors"
]

def is_tunable_config(key_name):
    """Filters out live telemetry data so we only save tunables/configs."""
    for ending in IGNORED_ENDINGS:
        if key_name.endswith(ending):
            return False
    return True

def connect_to_robot():
    """Connects to NetworkTables and blocks until connection is established."""
    NetworkTables.initialize(server=SERVER_IP)
    
    print(f"Connecting to Robot {TEAM_NUMBER} at {SERVER_IP}...")
    # Wait up to 5 seconds to connect
    for _ in range(50):
        if NetworkTables.isConnected():
            print("\033[92m[OK] Connected to NetworkTables!\033[0m\n")
            return NetworkTables.getTable("SmartDashboard")
        time.sleep(0.1)
        
    print("\033[91m[FAULT] Could not connect to the Robot. Is it turned on?\033[0m")
    sys.exit(1)

def backup_config(table):
    """Reads all tunable variables from SmartDashboard and saves them to a JSON file."""
    config_data = {}
    
    # Get all keys currently in the SmartDashboard table
    keys = table.getKeys()
    
    if not keys:
        print("\033[93m[WARNING] SmartDashboard is empty! No configuration found to backup.\033[0m")
        sys.exit(0)
    
    print("Extracting configuration values...")
    for key in keys:
        if is_tunable_config(key):
            value = table.getValue(key, None)
            if value is not None:
                config_data[key] = value
                print(f"  Found: {key} = {value}")
            
    if not config_data:
        print("\033[93m[WARNING] No tunable configurations found after filtering telemetry.\033[0m")
        sys.exit(0)
        
    # Generate timestamped filename
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    filename = f"config_backup_{timestamp}.json"
    filepath = os.path.join(os.path.dirname(__file__), filename)
    
    with open(filepath, 'w') as f:
        json.dump(config_data, f, indent=4)
        
    print(f"\n\033[92m[SUCCESS] Backed up {len(config_data)} variables to {filename}\033[0m")

def restore_config(table, filename):
    """Reads a JSON backup file and pushes all variables back into NetworkTables."""
    filepath = os.path.join(os.path.dirname(__file__), filename)
    
    if not os.path.exists(filepath):
        print(f"\033[91m[FAULT] File not found: {filename}\033[0m")
        sys.exit(1)
        
    print(f"Reading configuration from {filename}...")
    with open(filepath, 'r') as f:
        try:
            config_data = json.load(f)
        except json.JSONDecodeError:
            print(f"\033[91m[FAULT] Failed to parse {filename}. Is it valid JSON?\033[0m")
            sys.exit(1)
            
    print(f"Pushing {len(config_data)} variables back to Robot {TEAM_NUMBER}...")
    for key, value in config_data.items():
        # NetworkTables requires specific put methods (putBoolean, putNumber, putString)
        # depending on the data type, but putValue handles primitive inferencing in pynetworktables.
        is_success = table.putValue(key, value)
        if is_success:
             print(f"  Restored: {key} -> {value}")
        else:
             print(f"  \033[93m[WARNING] Failed to restore: {key}\033[0m")
             
    # Wait a tiny bit to ensure changes flush over the network
    NetworkTables.flush()
    time.sleep(0.5)
    
    print(f"\n\033[92m[SUCCESS] Restore complete!\033[0m")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Backup or Restore tuning variables via SmartDashboard")
    subparsers = parser.add_subparsers(dest="mode", required=True)
    
    # Backup Command
    backup_parser = subparsers.add_parser("backup", help="Saves current tuning variables to a JSON file")
    
    # Restore Command
    restore_parser = subparsers.add_parser("restore", help="Pushes a JSON config file back to the robot")
    restore_parser.add_argument("file", help="The config_backup_YYYYMMDD_HHMMSS.json file to restore")
    
    args = parser.parse_args()
    
    sd_table = connect_to_robot()
    
    if args.mode == "backup":
        backup_config(sd_table)
    elif args.mode == "restore":
        restore_config(sd_table, args.file)
