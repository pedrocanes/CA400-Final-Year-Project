import unittest
import json

from manage import app
from app.main import db


class HomeAssistantTest(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.db = db.get_db()
		
	def test_post(self): #Post a new home assistant
	payload = json.dumps({
		"id": 1000,
		"username": "user1000",
		"pair_pin": "9999"
	})

	self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) #Create a new home assistant
	
	payload = json.dumps({
	  "id": "1000",
	  "serial_key": "ABC123",
	  "lat_long": "test",
	  "user_ID": "1000"
	})

	response = self.app.post('/home assistant', headers={"Content-Type": "application/json"}, data=payload)

	self.assertEqual(int, type(response.json['id']))
	self.assertEqual(200, response.status_code)
		
    def test_get_all(self): #Get all home assistants
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) 
		
		payload = json.dumps({
		  "id": "1000",
		  "serial_key": "ABC123",
		  "lat_long": "test",
		  "user_ID": "1000"
		})

		self.app.post('/home assistant', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.get('/home assistant', headers={"Content-Type": "application/json"}, data=payload)

        self.assertEqual(int, type(response.json['id']))
        self.assertEqual(200, response.status_code)
		
   def test_get(self): #Get home assistant 1000
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) 
		
		payload = json.dumps({
		  "id": "1000",
		  "serial_key": "ABC123",
		  "lat_long": "test",
		  "user_ID": "1000"
		})

		self.app.post('/home assistant', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.get('/home assistant/1000', headers={"Content-Type": "application/json"}, data=payload)

	self.assertEqual(int, type(response.json['id']))
	self.assertEqual(200, response.status_code)
	
   def test_get_user_home_assistant(self): #Get user 1000s home assistant
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) 
		
		payload = json.dumps({
		  "id": "1001",
		  "serial_key": "ABC123",
		  "lat_long": "test",
		  "user_ID": "1000"
		})

		self.app.post('/home assistant', headers={"Content-Type": "application/json"}, data=payload)
		response = self.app.get('/home assistant/user/1000', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
	 
    def test_put(self): #Update lat_long
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) 
		
		payload = json.dumps({
		  "id": "1000",
		  "serial_key": "ABC123",
		  "lat_long": "test",
		  "user_ID": "1000"
		})

		self.app.post('/home assistant', headers={"Content-Type": "application/json"}, data=payload)
		
		payload = json.dumps({
		  "lat_long": "test-updated",
		})
		
		response = self.app.put('/home assistant/1000', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def test_delete(self): #Delete home assistant
		payload = json.dumps({
			"id": 1000,
			"username": "user1000",
			"pair_pin": "9999"
		})

		self.app.post('/user', headers={"Content-Type": "application/json"}, data=payload) 
		
		payload = json.dumps({
		  "id": "1000",
		  "serial_key": "ABC123",
		  "lat_long": "test",
		  "user_ID": "1000"
		})

		self.app.post('/home assistant', headers={"Content-Type": "application/json"}, data=payload)
		
		response = self.app.delete('/home assistant/1000', headers={"Content-Type": "application/json"}, data=payload)

		self.assertEqual(int, type(response.json['id']))
		self.assertEqual(200, response.status_code)
		
    def tearDown(self):
        # Delete Database collections after the test is complete
        for collection in self.db.list_collection_names():
            self.db.drop_collection(collection)