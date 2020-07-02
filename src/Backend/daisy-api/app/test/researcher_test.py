import unittest
import json

from manage import app
from app.main import db

class ResearcherTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
	def test_post(self): #Post a new researcher
		payload = json.dumps({
			"id": 1000,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
		
		payload = json.dumps({
			"id": 1001,
			"username": "researcher1001",
			"email": "researcher1@mail.com"
		})

		response = self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
		
		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_get_all(self): #Get all researchers
		payload = json.dumps({
			"id": 1000,
			"researchername": "researcher1000",
			"pair_pin": "9999"
		})
		
		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
		
		payload = json.dumps({
			"id": 1000,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
		
		response = self.app.get('/researcher', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
   def test_get(self): #Get researcher 1000
		payload = json.dumps({
			"id": 1000,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
	
		response = self.app.get('/researcher/1000', headers={"Content-Type": "application/json"}, data=payload)
	 
	    self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
		response = self.app.get('/user/1002', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(404, response.status_code)
		#This should result in a 404 error as researcher 1002 does not exist
		
    def test_put(self): #Update pair pin
		payload = json.dumps({
			"id": 1000,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

		self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
	
        payload = json.dumps({
			"email": "researcher-update@mail.com"
        })

        response = self.app.put('/researcher/1000', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
    def test_delete(self): #Delete researcher
		payload = json.dumps({
			"id": 1000,
			"username": "researcher1000",
			"email": "researcher@mail.com"
		})

        self.app.post('/researcher', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.delete('/researcher/1000', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)