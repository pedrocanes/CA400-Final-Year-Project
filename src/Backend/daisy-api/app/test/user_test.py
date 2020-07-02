import unittest
import json

from manage import app
from app.main import db

class UserTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
	def test_post(self): #Post a new user
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		response = self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
		payload = json.dumps({
		"id": 1001,
		"username": "user1001",
		"pair_pin": "0000000000"
		})

		response = self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)
		#This should result in  a 500 error as there cannot be a pair pin longer than 9 characters

		self.assertEqual(500, response.status_code)
		
    def test_get_all(self): #Get all users
        payload = json.dumps({
            "id": 1000,
            "username": "user1000",
            "pair_pin": "9999"
        })
		
        self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)
		
		payload = json.dumps({
		"id": 1001,
		"username": "user1001",
		"pair_pin": "0000000000"
		})


        self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.get('/user', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
   def test_get(self): #Get user 1000
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.get('/user/1000', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
		response = self.app.get('/user/1002', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(404, response.status_code)
		#This should result in a 404 error as user 1002 does not exist
	 
	 
    def test_put(self): #Update pair pin
	   payload = json.dumps({
		"id": 1000,
		"username": "user1000",
		"pair_pin": "9999"
	})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)
	
        payload = json.dumps({
            "pair_pin": "0000"
        })

        response = self.app.put('/user/1000', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
		response = self.app.get('/user/1002', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(404, response.status_code)
		#This should result in a 404 error as user 1002 does not exist
		
    def test_delete(self): #Delete user
       payload = json.dumps({
            "id": 1000,
            "username": "user1000",
            "pair_pin": "9999"
        })

        self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.delete('/user/1000', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
		response = self.app.get('/user/1002', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(404, response.status_code)
		#This should result in a 404 error as user 1002 does not exist
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)