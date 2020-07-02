import datetime

from app.main import db
from app.main.model.user import User
from flask import jsonify
import json

'''
Creates a new user by first checking if the user already exists; 
it returns a success response_object if the user doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_user(data):
    user = User.query.filter_by(username=data['username']).first()
    if not user:
        new_user = User(
           id=data['id'],
           username=data['username'],
           pair_pin=data['pair_pin']
        )
        save_changes(new_user)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'User already exists. Please Log in.',
        }
        return response_object, 409

'''
Return a list of all registered users and a user object by 
providing the id respectively
'''
def get_all_users():
    return User.query.all()

def get_a_user(id):
    return User.query.filter_by(id=id).first()

def delete_user(id):
    user  = User.query.filter_by(id=id).first()        
    if user:           
        delete_object(user)
        response_object = {
            'status': 'success',
            'message': 'User successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'User does not exist.'
        }
    return response_object, 204

def update_user(id, data):
    user = User.query.filter_by(id=id).first()
    if user:
        new_pair_pin = data['pair_pin']
        if user.pair_pin == new_pair_pin:
            response_object = {
                'status': 'fail',
                'message': 'User already has that pair pin.'
            }
            return response_object
        else:
            user.pair_pin = new_pair_pin
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'User successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'User does not exist.'
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