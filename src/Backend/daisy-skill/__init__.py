import time
from datetime import datetime
from adapt.intent import IntentBuilder
from mycroft import MycroftSkill, intent_handler
from mycroft.skills.core import MycroftSkill
from mycroft.util.log import LOG
import requests
import os
from os.path import join, exists
import uuid
import json
from word2number import w2n

def getserial():
    # Extract serial from cpuinfo file
    cpuserial = "0000000000000000"
    try:
        f = open('/proc/cpuinfo','r')
        for line in f:
            if line[0:6]=='Serial':
                cpuserial = line[10:26]
        f.close()
    except:
        cpuserial = "ERROR000000000"
    return cpuserial

class Daisy(MycroftSkill):
    def __init__(self):
        MycroftSkill.__init__(self)
        self.serial_key = getserial()
        self.home_assistant_id = str(uuid.uuid4())[0:28]
        self.answers_returned_id = str(uuid.uuid4())[0:28]
        self.user_id = None
        self.username = None
        self.registered = False
        self.questions_answers = {}
        self.answers = {}

        self.start_cron = join(self.root_dir, 'daisy-scripts/cron.py')
        self.ask_questions_bool = False
        self.update_gps = join(self.root_dir, 'daisy-scripts/update_gps.py')
        self.cred_file = join(self.root_dir, 'daisy-scripts/cred')
        self.questions_file = join(self.root_dir, 'daisy-scripts/questions')

    @intent_handler(IntentBuilder('StartDaisy').require('Start').require('Daisy'))
    def handle_start_daisy(self, Message):
        self.check_cred()
        if self.registered == False:
            response = self.get_response("Hi, have you registered on the daisy app")
            if response == "yes":
                code = self.get_response("Whats your pair code")
                self.check_user(code)
                if self.registered == False:
                    self.speak("User does not exist. Please register on the daisy app and try pairing again with hi daisy")
                elif self.registered == True:
                    if self.register_home_assist() is "SUCCESS":
                        self.save_cred()
                        self.update_location()
                        self.speak("Welcome {}. You have been registered".format(self.username))
                        self.start_cron_job()
                    else:
                        self.speak("There has been an error. Please wait and try pairing again with hi daisy later")
                else:
                    self.speak("There has been an error. Please wait and try pairing again with hi daisy later")
            elif response == "no":
                self.speak("Please register on the daisy app and try pairing again with hi daisy")
            else:
                self.speak("Invalid response use yes or no. try pairing again with hi daisy")        
        else:
            self.speak("Welcome {}".format(self.username))
            self.get_questions()
            if self.ask_questions_bool == False:
                self.speak("You have no questions.")
            else:
                self.ask_questions()

    def check_user(self, code):
        formatted_code = code.replace('-', '').replace(' ', '')
        LOG.info('CODE: {}'.format(formatted_code))
        url = "https://daisy-project.herokuapp.com/user/"
        response = requests.get(url)
        if response.status_code == 200:
            output = response.json()
            data_output = output["data"]
            LOG.info(data_output)
            for user in data_output:
                LOG.info(user["pair_pin"])
                LOG.info(formatted_code)
                if user["pair_pin"] == formatted_code:
                    self.user_id = user["id"]
                    self.username = user["username"]
                    self.registered = True
                    return True
            self.registered = False
            return False
        else:
            self.registered = "ERROR"
            LOG.info('Check User Error Occured')

    def register_home_assist(self):
        data={
            "id": self.home_assistant_id,
            "serial_key": self.serial_key,
            "lat_long": "PLACEHOLDER",
            "user_ID": self.user_id
        }
        url = "https://daisy-project.herokuapp.com/home-assistant/"
        response = requests.post(url, json=data)
        if response.status_code == 200:
            return "SUCCESS"
        else:
            return "ERROR"
            LOG.info('Register Home Assistant Error Occured')

    def start_cron_job(self):
        os.system("python " + self.start_cron)
        LOG.info("Question cron job started...")

    def check_cred(self):
        if os.stat(self.cred_file).st_size == 0:
            self.registered = False
        else:
            with open(self.cred_file) as f:
                cred_dict = json.load(f)
                self.user_id = cred_dict["id"]
                self.username = cred_dict["username"]
                self.registered = True

    def save_cred(self):
        cred_dict = {
            "id": self.user_id,
            "username": self.username
        }
        with open(self.cred_file, "w") as f:
            json.dump(cred_dict, f)
            self.registered = True

    def initialize(self):
        self.add_event('question', self.handler_question)

    def handler_question(self, message):
        LOG.info('QUESTION RECEIVED!')
        self.get_questions()
        self.ask_questions()

    def get_questions(self):
        if os.stat(self.questions_file).st_size == 0:
            self.ask_questions_bool = False
            return False
        else:
            with open(self.questions_file) as f:
                questions_dict = json.load(f)
                self.questions_answers = questions_dict
                self.ask_questions_bool = True
                return True
    
    def ask_questions(self):
        response = self.get_response("You have new questions would you like to answer")
        if response == "no":
            LOG.info("Responses sent to phone...")
            self.send_to_phone()
        elif response == "yes":
            LOG.info("Asking home assistant...")
            self.speak("Ok here are your questions")
            time.sleep(1)
            self.ask_via_home_assistant()
            self.speak("Responses recorded")
            self.send_questions()
        else:
            self.speak("Could not understand please respond with yes or no. Start again with Hi Daisy.")

    def send_questions(self):
        LOG.info("Sending questions...")
        url = "https://daisy-project.herokuapp.com/answer-returned/"
        for answer in self.answers:
            data = {
                "id": self.answers_returned_id,
                "user_ID": self.user_id,
                "answer_ID": answer,
                "answer_time": self.answers[answer],
                "device_used": "home-assistant"
            }
            response = requests.post(url, json=data)
            if response.status_code == 200:
                LOG.info("Responses sent SUCCESS")
                open(self.questions_file, 'w').close() #wipe file
            else:
                LOG.info("Responses sending FAILED")
    
    def ask_via_home_assistant(self):
        for i, question in enumerate(self.questions_answers):
            self.speak("Question {} {}".format(i+1, self.questions_answers[question][0]))
            time.sleep(1)
            self.speak("Your responses are")
            for answer in self.questions_answers[question][1]:
                self.speak("{}".format(self.questions_answers[question][1][answer]))
                time.sleep(0.5)
            logged_answer = self.get_response("Which response do you pick")
            self.save_answer(question, logged_answer)
            self.speak("Your response {} has been logged". format(logged_answer))

    def save_answer(self, question, logged_answer):
        for answer_id, answer in self.questions_answers[question][1].items():
            if logged_answer == answer:
                self.answers[answer_id] = str(datetime.now())[:19]
                return True
            else:
                return False
    
    def send_to_phone(self):
        self.speak("Question has been sent to your phone. Please respond when you are available")
        base_url = "https://daisy-project.herokuapp.com/user-details/user/"
        url = base_url + self.user_id
        headers = {"content-type": "application/json"}
        payload = {"ask_question": False, "device_to_use": "phone", "user_available": False}
        requests.put(url, json=payload, headers=headers)
        e

    def update_location(self):
        os.system("python " + self.update_gps)
        LOG.info("GPS updated")

def create_skill():
    return Daisy()
