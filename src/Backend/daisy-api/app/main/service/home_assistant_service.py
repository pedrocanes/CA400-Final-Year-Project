import datetime

from app.main import db
from app.main.model.home_assistant import HomeAssist
from flask import jsonify
import json

'''
Creates a new home_assistant by first checking if the home_assistant already exists; 
it returns a success response_object if the home_assistant doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_home_assistant(data):
    home_assistant = HomeAssist.query.filter_by(id=data['id']).first()
    if not home_assistant:
        new_home_assist = HomeAssist(
           id=data['id'],
           serial_key=data['serial_key'],
           lat_long=data['lat_long'],
           user_ID=data['user_ID']
        )
        save_changes(new_home_assist)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'Home Assistant already registered.',
        }
        return response_object, 409

'''
Return a list of all registered home assistants and a home assistant objects by 
providing the id respectively
'''
def get_all_home_assistants():
    return HomeAssist.query.all()

def get_a_home_assistant(id):
    return HomeAssist.query.filter_by(id=id).first()
	
def get_users_home_assistant(user_id):
    return HomeAssist.query.filter_by(user_ID=user_id).first()

def delete_home_assistant(id):
    home_assistant  = HomeAssist.query.filter_by(id=id).first()        
    if home_assistant:           
        delete_object(home_assistant)
        response_object = {
            'status': 'success',
            'message': 'Home Assistant successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'Home Assistant does not exist.'
        }
    return response_object, 204

def update_home_assistant(id, data):
    home_assistant = HomeAssist.query.filter_by(id=id).first()
    if home_assistant:
        new_home_assistant_gps = data['lat_long']
        if home_assistant.lat_long == new_home_assistant_gps:
            response_object = {
                'status': 'fail',
                'message': 'Home Assistant already has those coordinates.'
            }
            return response_object
        else:
            home_assistant.lat_long = new_home_assistant_gps
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'Home Assistant coordinates successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'Home Assistant does not exist.'
        }
    return response_object, 204

#Save changes to database
def save_changes(data):
    db.session.add(data)
    db.session.commit()

#Delete object in the database
def delete_object(data):
    db.session.delete(data)
    db.session.commit()