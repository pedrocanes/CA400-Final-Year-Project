import unittest
import json

from manage import app
from app.main import db


class UserDetailsTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
    def test_get(self): #Get user_details
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #First create new user
		
		payload = json.dumps({
			"id": 1001,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #Create a new researcher
		
		payload = json.dumps({
			"id": 1002,
			"question": "How are you",
			"researcher_ID": "1001"
		})

		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #Create a new question
		
		payload = json.dumps({
			  "id": "1003",
			  "user_ID": "1000",
			  "question_ID": "1002",
			  "ask_question": True,
			  "user_available": "True",
			  "device_to_use": "null"
			})

		response = self.app.post('/user-details', headers={"Content-Type": "application/json"}, data=payload) #Create a new user-details object
		self.assertEqual(200, response.status_code) #Check if this was successful
		 
    def test_put(self): #Update pair pin
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #First create new user
		
		payload = json.dumps({
			"id": 1001,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #Create a new researcher
		
		payload = json.dumps({
			"id": 1002,
			"question": "How are you",
			"researcher_ID": "1001"
		})

		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #Create a new question
		
		payload = json.dumps({
			  "id": "1003",
			  "user_ID": "1000",
			  "question_ID": "1002",
			  "ask_question": True,
			  "user_available": "True",
			  "device_to_use": "null"
			})

		self.app.post('/user-details', headers={"Content-Type": "application/json"}, data=payload) #Create a new user-details object
		
		payload = json.dumps({
			  "ask_question": False,
			  "user_available": "False",
			  "device_to_use": "null"
			})
			
		response = self.app.put('/user-details/1000', headers={"Content-Type": "application/json"}, data=payload) #Create a new user-details object
		self.assertEqual(200, response.status_code) #Check if this was successful
				
    def test_delete(self): #Delete user_details
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #First create new user
		
		payload = json.dumps({
			"id": 1001,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #Create a new researcher
		
		payload = json.dumps({
			"id": 1002,
			"question": "How are you",
			"researcher_ID": "1001"
		})

		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #Create a new question
		
		payload = json.dumps({
			  "id": "1003",
			  "user_ID": "1000",
			  "question_ID": "1002",
			  "ask_question": True,
			  "user_available": "True",
			  "device_to_use": "null"
			})

		response = self.app.delete('/user-details/1000', headers={"Content-Type": "application/json"}, data=payload) #Create a new user-details object
		self.assertEqual(200, response.status_code) #Check if this was successful
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)