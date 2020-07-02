import unittest
import json

from manage import app
from app.main import db

class AnswerReturnedTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
	def test_post(self): #Post a new answer returned
		payload = json.dumps({
		  "id": "1000",
		  "username": "user1000",
		  "pair_pin": "0000"
		})
		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #create new user
		
		payload = json.dumps({
		  "id": "1001",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1002",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
			
		payload = json.dumps({
		  "id": "1003",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new answer
	
		payload = json.dumps({
		  "id": "1004",
		  "answer_time": "2020-05-16 12:00:00",
		  "device_used": "null",
		  "user_ID": "1000",
		  "answer_ID": "1003"
		})
		response = self.app.post('/answer returned', headers={"Content-Type": "application/json"}, data=payload) #create new answer returned

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_get_all(self): #Get all answers returned
 		payload = json.dumps({
		  "id": "1000",
		  "username": "user1000",
		  "pair_pin": "0000"
		})
		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #create new user
		
		payload = json.dumps({
		  "id": "1001",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1002",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
			
		payload = json.dumps({
		  "id": "1003",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new answer
	
		payload = json.dumps({
		  "id": "1004",
		  "answer_time": "2020-05-16 12:00:00",
		  "device_used": "null",
		  "user_ID": "1000",
		  "answer_ID": "1003"
		})
		self.app.post('/answer returned', headers={"Content-Type": "application/json"}, data=payload) #create new answer returned

		response = self.app.get('/answer returned', headers={"Content-Type": "application/json"}, data=payload) #get all answer returned 

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
   def test_get(self): #Get answer returned 1000
		payload = json.dumps({
		  "id": "1000",
		  "username": "user1000",
		  "pair_pin": "0000"
		})
		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #create new user
		
		payload = json.dumps({
		  "id": "1001",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1002",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
			
		payload = json.dumps({
		  "id": "1003",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new answer
	
		payload = json.dumps({
		  "id": "1004",
		  "answer_time": "2020-05-16 12:00:00",
		  "device_used": "null",
		  "user_ID": "1000",
		  "answer_ID": "1003"
		})
		app.post('/answer returned', headers={"Content-Type": "application/json"}, data=payload) #create new answer returned

		response = self.app.get('/answer returned/1004', headers={"Content-Type": "application/json"}, data=payload) #get answer returned 1004
		
		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
	 	 
    def test_put(self): #Update answer returned
		payload = json.dumps({
		  "id": "1000",
		  "username": "user1000",
		  "pair_pin": "0000"
		})
		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #create new user
		
		payload = json.dumps({
		  "id": "1001",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1002",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
			
		payload = json.dumps({
		  "id": "1003",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new answer
		
		payload = json.dumps({
		  "id": "1005",
		  "answer": "Bad",
		  "question_ID": "1001"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new answer to use for update
	
		payload = json.dumps({
		  "id": "1004",
		  "answer_time": "2020-05-16 12:00:00",
		  "device_used": "null",
		  "user_ID": "1000",
		  "answer_ID": "1003"
		})
		self.app.post('/answer returned', headers={"Content-Type": "application/json"}, data=payload) #create new answer returned

		payload = json.dumps({
		  "answer_ID": "1005"
		})
		
		response = self.app.put('/answer returned/1004', headers={"Content-Type": "application/json"}, data=payload) #update answer returned 1004 with answer id 1005
		
		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_delete(self): #Delete user
		payload = json.dumps({
		  "id": "1000",
		  "username": "user1000",
		  "pair_pin": "0000"
		})
		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #create new user
		
		payload = json.dumps({
		  "id": "1001",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1002",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
			
		payload = json.dumps({
		  "id": "1003",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new answer
	
		payload = json.dumps({
		  "id": "1004",
		  "answer_time": "2020-05-16 12:00:00",
		  "device_used": "null",
		  "user_ID": "1000",
		  "answer_ID": "1003"
		})
		self.app.post('/answer returned', headers={"Content-Type": "application/json"}, data=payload) #create new answer returned
		
		response = self.app.delete('/answer returned/1004', headers={"Content-Type": "application/json"}, data=payload) #delete answer returned 1004

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)