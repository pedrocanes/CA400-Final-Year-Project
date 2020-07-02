#!/home/pi/mycroft-core/.venv/bin/python

import subprocess
import requests
import math
import os
import json
from datetime import datetime

def get_user_id():
    root_dir = "/opt/mycroft/skills/daisy-skill/daisy-scripts/"
    cred_file = root_dir + "cred"
    with open(cred_file) as f:
        cred_dict = json.load(f)
    return cred_dict["id"]

#Check if any questions are true in user details, if yes, return list of questions
def check_true_questions(user_id):
    base_url = "https://daisy-project.herokuapp.com/user-details/user/"
    url = base_url + user_id
    questions_id = []
    response = requests.get(url)
    data_lst = response.json()
    for item in data_lst:
        if item["ask_question"] == True:
            questions_id.append(item["question_ID"])
    if questions_id == []:
        return None
    else:
        return questions_id

def get_device_id(base_url, user_id):
    url = base_url + user_id
    response = requests.get(url)
    data = response.json()
    device_id = data["id"]
    return device_id

#Check user availability for picroft
#True means picroft can be used
def check_user(user_id):
    base_url = "https://daisy-project.herokuapp.com/user-details/user/"
    url = base_url + user_id
    response = requests.get(url)
    data_lst = response.json()
    for item in data_lst:
        if item["user_available"] == "True":
            return True
        elif item["user_available"] == "False":
            return False
        else:
            return None

#Pull the actual questions and their ids:
#{id: question}
def get_questions(questions_id_lst):
    base_url = "https://daisy-project.herokuapp.com/question/"
    questions = {}
    for questions_id in questions_id_lst:
        url = base_url + questions_id
        response = requests.get(url)
        data = response.json()
        questions[questions_id] = data["question"]
    return questions

#Link questions to answers by id in the form:
#{question id: [question, {answer id: answer}]}
def questions_answers(questions_dict):
    base_url = "https://daisy-project.herokuapp.com/answer/"
    questions_answers = {}
    for question in questions_dict:
        answers_dict = {}
        questions_answers_lst = []
        questions_answers_lst.append(questions_dict[question])
        url = base_url + "question/" + question #Get answers for each question
        response = requests.get(url)
        data = response.json()
        for answer in data:
            answers_dict[answer["id"]] = answer["answer"]
        questions_answers_lst.append(answers_dict)
        questions_answers[question] = questions_answers_lst
    return questions_answers

def get_gps(base_url, user_id):
    user_url = base_url + "user/" + user_id
    while True:
        response = requests.get(user_url)
        data = response.json()
        if data["lat_long"] == "PLACEHOLDER": #for home assistant
            home_assistant_url = base_url + data["id"]
            requests.delete(home_assistant_url)
        else:
            return data["lat_long"]

def find_dist(gps1, gps2):
    earth_radius = 6373.0
    gps1_lst = gps1.split(",")
    gps2_lst = gps2.split(",")

    lat1 = math.radians(float(gps1_lst[0]))
    lon1 = math.radians(float(gps1_lst[1]))
    lat2 = math.radians(float(gps2_lst[0]))
    lon2 = math.radians(float(gps2_lst[1]))

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = math.sin(dlat / 2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon / 2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance_in_m = earth_radius * c

    return distance_in_m

def choose_device(dist, user_availability):
    if dist >= 10: #distance threshold is 10meters
        return "phone"
    elif dist <= 10 and user_availability == False:
        return "phone"
    else:
        return "home-assistant"

def set_device(device, user_id):
    base_url = "https://daisy-project.herokuapp.com/user-details/user/"
    url = base_url + user_id
    headers = {"content-type": "application/json"}
    payload = {"ask_question": False, "device_to_use": device, "user_available": False}
    response = requests.put(url, json=payload, headers=headers)
    print(response.json())

#If questions are being asked, old questions are no longer needed
#Wipe previous file and add questions
def save_details(questions_dict):
    root_dir = "/opt/mycroft/skills/daisy-skill/daisy-scripts/"
    questions_file = root_dir + "questions"
    open('file.txt', 'w').close() #wipe file
    with open(questions_file, "w") as f:
        json.dump(questions_dict, f)

def prompt_mycroft():
    os.system("sh /opt/mycroft/skills/daisy-skill/daisy-scripts/ask_question.sh")
    #subprocess.call(['./ask_question.sh'])
    #send notification to picroft

def main():
    print("Checking for questions...")
    root_dir = "/opt/mycroft/skills/daisy-skill/daisy-scripts"
    logs = root_dir + "/access_logs.txt"
    myFile = open(logs, "a")
    myFile.write("\nGET_QUESTIONS.PY: Accessed on " + str(datetime.now()))
    user_details_user_url = "https://daisy-project.herokuapp.com/user-details/user/"
    questions_url = "https://daisy-project.herokuapp.com/question/"
    answers_url = "https://daisy-project.herokuapp.com/answer/"
    phone_url = "https://daisy-project.herokuapp.com/phone/"
    home_assistant_url = "https://daisy-project.herokuapp.com/home-assistant/"
    user_id = get_user_id()
    phone_gps = get_gps(phone_url, user_id)
    home_assistant_gps = get_gps(home_assistant_url, user_id)
    questions_id_lst = check_true_questions(user_id)
    user_availability = check_user(user_id)
    if user_id is not None and questions_id_lst is not None and user_availability is not None: #check if there are any questions
        print("Found questions to ask!")
        #test_dist = 9.9 #to use for testing purposes and checking if the system picks the right device
        dist = find_dist(phone_gps, home_assistant_gps)
        if choose_device(dist, user_availability) == "home-assistant":
            print("Sending to home-assistant...")
            questions_dict = get_questions(questions_id_lst)
            question_answers_dict = questions_answers(questions_dict)
            print("Saving questions...")
            print(question_answers_dict)
            save_details(question_answers_dict)
            set_device("home-assistant", user_id)
            prompt_mycroft()
        else:
            print("Sending to phone...")
            set_device("phone", user_id)
    else:
        print("Found no questions.")
        pass

if __name__ == "__main__":
    main()
