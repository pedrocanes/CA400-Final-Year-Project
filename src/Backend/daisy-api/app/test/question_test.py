import unittest
import json

from manage import app
from app.main import db

class QuestionTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
	def test_post(self): #Post a new question
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
		response = self.app.post('/question', headers={"Content-Type": "application/json"}, data=payload) #create new question

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_get_all(self): #Get all questions
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
		
		response = self.app.get('/question', headers={"Content-Type": "application/json"}, data=payload) #get all questions

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
   def test_get(self): #Get question 1000
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
		
		response = self.app.get('/question/1001', headers={"Content-Type": "application/json"}, data=payload) #get all questions

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
	 	 
    def test_put(self): #Update question
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
		  "question": "How are you today",
		})
		
		response = self.app.put('/question/1001', headers={"Content-Type": "application/json"}, data=payload) #update question 1000

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
		
		response = self.app.delete('/question/1001', headers={"Content-Type": "application/json"}, data=payload) #delete question 1000

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)