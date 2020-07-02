import unittest
import json

from manage import app
from app.main import db

class AnswerTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
	def test_post(self): #Post a new answer
		payload = json.dumps({
		  "id": "1000",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1001",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
	
		payload = json.dumps({
		  "id": "1002",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		response = self.app.post('/answer', headers={"Content-Type": "application/json"}, data=payload) #create new answer

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_get_all(self): #Get all answers
 		payload = json.dumps({
		  "id": "1000",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1001",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
	
		payload = json.dumps({
		  "id": "1002",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/answer', headers={"Content-Type": "application/json"}, data=payload) #create new answer
		
		response = self.app.get('/answer', headers={"Content-Type": "application/json"}, data=payload) #get all answers

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
   def test_get(self): #Get answer 1000
 		payload = json.dumps({
		  "id": "1000",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1001",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
	
		payload = json.dumps({
		  "id": "1002",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/answer', headers={"Content-Type": "application/json"}, data=payload) #create new answer
		
		response = self.app.get('/answer/1000', headers={"Content-Type": "application/json"}, data=payload) #get all answers

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
	 	 
    def test_put(self): #Update answer
 		payload = json.dumps({
		  "id": "1000",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1001",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
	
		payload = json.dumps({
		  "id": "1002",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/answer', headers={"Content-Type": "application/json"}, data=payload) #create new answer
		
		payload = json.dumps({
		  "answer": "Bad",
		})
		
		response = self.app.put('/answer/1000', headers={"Content-Type": "application/json"}, data=payload) #update answer 1000

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_delete(self): #Delete user
 		payload = json.dumps({
		  "id": "1000",
		  "username": "researcher",
		  "email": "researcher@mail.com"
		})
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload) #create new researcher
			
		payload = json.dumps({
		  "id": "1001",
		  "question": "How are you",
		  "researcher_ID": "1000"
		})
		self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question
	
		payload = json.dumps({
		  "id": "1002",
		  "answer": "Good",
		  "question_ID": "1001"
		})
		self.app.post('/answer', headers={"Content-Type": "application/json"}, data=payload) #create new answer
		
		response = self.app.delete('/answer/1000', headers={"Content-Type": "application/json"}, data=payload) #delete answer 1000

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)