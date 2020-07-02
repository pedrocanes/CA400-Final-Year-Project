import datetime

from app.main import db
from app.main.model.researcher import Researcher
from flask import jsonify
import json

'''
Creates a new researcher object by first checking if the researcher object already exists; 
it returns a success response_object if the researcher object doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_researcher(data):
    researcher = Researcher.query.filter_by(username=data['username']).first()
    if not researcher:
        new_researcher = Researcher(
           id=data['id'],
           username=data['username'],
           email=data['email']
        )
        save_changes(new_researcher)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'researcher already registered.',
        }
        return response_object, 409

'''
Return a list of all registered researchers and a researcher objects by 
providing the id respectively
'''
def get_all_researchers():
    return Researcher.query.all()

def get_a_researcher(id):
    return Researcher.query.filter_by(id=id).first()

def delete_researcher(id):
    researcher  = Researcher.query.filter_by(id=id).first()        
    if researcher:           
        delete_object(researcher)
        response_object = {
            'status': 'success',
            'message': 'researcher successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'researcher does not exist.'
        }
    return response_object, 204

def update_researcher(id, data):
    researcher = Researcher.query.filter_by(id=id).first()
    if researcher:
        new_researcher_username = data['username']
        if researcher.username == new_researcher_username:
            response_object = {
                'status': 'fail',
                'message': 'researcher already has that username.'
            }
            return response_object
        else:
            researcher.username = new_researcher_username
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'researcher username successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'researcher does not exist.'
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