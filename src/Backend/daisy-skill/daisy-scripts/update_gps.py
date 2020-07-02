#! /home/pi/mycroft-core/.venv/bin/python

import requests
import sys
import os
from os.path import join
import json
import gps
import json
from datetime import datetime

def return_user_id(cred_file):
    if os.stat(cred_file).st_size == 0:
        return None
    else:
        with open(cred_file) as f:
            cred_dict = json.load(f)
        return cred_dict["id"]

def get_home_assistant_id(base_url, user_id):
    url = base_url + user_id
    response = requests.get(url)
    data = response.json()
    home_assistant_id = data["id"]
    return home_assistant_id

def put_request(url, payload):
    headers = {'content-type': 'application/json'}
    requests.put(url, json=payload, headers=headers)

def get_gps():
    line = gps.readString()
    lines = line.split(",")
    if gps.checksum(line):
        if lines[0] == "GPRMC":
            latlng = gps.getLatLng("".join(lines[3:5]), "".join(lines[5:7]))
            return latlng
        else:
            return ("Unknown type:", lines[0])

def convert_tup_to_str(tup):
    return str(tup[0]) + "," + str(tup[1])

def put_gps(base_url, home_assistant_id):
    url = base_url + home_assistant_id
    payload = {}
    while True:
        gps_val = get_gps()
        if "Unknown type:" in gps_val:
            print("FAIL", gps_val)
            continue
        else:
            payload["lat_long"] = convert_tup_to_str(gps_val)
            put_request(url, payload)
            print("SUCCESS", gps_val)
            break
                
def main():
    print("Running...")
    root_dir = "/opt/mycroft/skills/daisy-skill/daisy-scripts"
    cred_file = join(root_dir, "cred")
    home_assistant_user_url = "https://daisy-project.herokuapp.com/home-assistant/user/"
    home_assistant_base_url = "https://daisy-project.herokuapp.com/home-assistant/"
    user_id = return_user_id(cred_file)
    if user_id is not None:
        home_assistant_id = get_home_assistant_id(home_assistant_user_url, user_id)
        put_gps(home_assistant_base_url, home_assistant_id)
        logs = root_dir + "/access_logs.txt"
        myFile = open(logs, "a")
        myFile.write("\nUPDATE_GPS.PY: Accessed on " + str(datetime.now()))
    else:
        pass

if __name__ == "__main__":
    main()
