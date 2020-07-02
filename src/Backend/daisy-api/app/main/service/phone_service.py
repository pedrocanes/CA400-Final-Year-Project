import datetime

from app.main import db
from app.main.model.phone import Phone
from flask import jsonify
import json

'''
Creates a new phone object by first checking if the phone object already exists; 
it returns a success response_object if the phone object doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_phone(data):
    phone = Phone.query.filter_by(id=data['id']).first()
    if not phone:
        new_phone = Phone(
           id=data['id'],
           serial_key=data['serial_key'],
           lat_long=data['lat_long'],
           user_ID=data['user_ID']
        )
        save_changes(new_phone)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'phone already registered.',
        }
        return response_object, 409

'''
Return a list of all registered phones and a phone objects by 
providing the id respectively
'''
def get_all_phones():
    return Phone.query.all()

def get_a_phone(id):
    return Phone.query.filter_by(id=id).first()
	
def get_users_phone(user_id):
    return Phone.query.filter_by(user_ID=user_id).first()

def delete_phone(id):
    phone  = Phone.query.filter_by(id=id).first()        
    if phone:           
        delete_object(phone)
        response_object = {
            'status': 'success',
            'message': 'phone successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'phone does not exist.'
        }
    return response_object, 204

def update_phone(id, data):
    phone = Phone.query.filter_by(id=id).first()
    if phone:
        new_phone_gps = data['lat_long']
        if phone.lat_long == new_phone_gps:
            response_object = {
                'status': 'fail',
                'message': 'phone already has those coordinates.'
            }
            return response_object
        else:
            phone.lat_long = new_phone_gps
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'phone coordinates successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'phone does not exist.'
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