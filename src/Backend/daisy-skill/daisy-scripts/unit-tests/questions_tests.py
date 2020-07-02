import unittest
import json
import requests
import get_questions

class TestQuestion(unittest.TestCase):
    def test_user_details(self):
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		requests.post('/user', headers={"Content-Type": "application/json"}, data=payload) #First create new user
		
		payload = json.dumps({
			"id": 1001,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		requests.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #Create a new researcher
		
		payload = json.dumps({
			"id": 1002,
			"question": "How are you",
			"researcher_ID": "1001"
		})

		requests.post('/question', headers={"Content-Type": "application/json"}, data=payload) #Create a new question
		
		payload = json.dumps({
			  "id": "1003",
			  "user_ID": "1000",
			  "question_ID": "1002",
			  "ask_question": True,
			  "user_available": "True",
			  "device_to_use": "null"
			})

		requests.post('/user-details', headers={"Content-Type": "application/json"}, data=payload) #Create a new user-details object
		self.assertEqual(200, response.status_code) #Check if this was successful
		
	def test_check_user_available(self):
		check_user = check_user(1000)
		self.assertEqual("True", check_user) #check that user is available

if __name__ == '__main__':
    unittest.main()
			
	
