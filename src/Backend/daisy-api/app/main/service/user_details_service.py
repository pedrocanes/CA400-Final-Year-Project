import datetime

from app.main import db
from app.main.model.user_details import UserDetails
from flask import jsonify
from sqlalchemy.sql import case
import json

'''
Creates a new user_details by first checking if the user_details already exists; 
it returns a success response_object if the user_details doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_user_details(data):
    user_details = UserDetails.query.filter_by(id=data['id']).first()
    if not user_details:
        new_user_details = UserDetails(
           id=data['id'],
           user_ID=data['user_ID'],
           question_ID=data['question_ID'],
		   ask_question=data['ask_question'],
		   user_available=data['user_available'],
		   device_to_use=data['device_to_use']
        )
        save_changes(new_user_details)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'User Details already registered.',
        }
        return response_object, 409

'''
Return a list of all registered User Details and an User Details objects by 
providing the id respectively
'''
def get_all_users_details():
    return UserDetails.query.all()

def get_user_details(id):
    return UserDetails.query.filter_by(id=id).first()
		
def get_users_user_details(user_id):
    return UserDetails.query.filter_by(user_ID=user_id).all()

def delete_user_details(id):
    user_details  = UserDetails.query.filter_by(id=id).first()        
    if user_details:           
        delete_object(user_details)
        response_object = {
            'status': 'success',
            'message': 'User Details successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'User Details do not exist.'
        }
    return response_object, 204

def update_user_details(user_id, data):
	user = UserDetails.query.filter_by(user_ID=user_id).all()
	if user is not None:
		UserDetails.query.filter_by(user_ID=user_id).update(data)
		db.session.commit()
		response_object = {
				'status': 'success',
				'message': 'User Details successfully updated.'
				}
		return response_object, 200
	response_object = {
		'status': 'fail',
		'message': 'User Details does not exist.'
		}
	return response_object, 204

	'''
	Updating individual rows:
	user_details = UserDetails.query.filter_by(user_ID=user_id).all()
	if user_details is not None:
		for key, value in data.items():
			if data[key] is not None:
				setattr(user_details, key, value)
	'''

#Save changes to database
def save_changes(data):
    db.session.add(data)
    db.session.commit()

#Delete object in the database
def delete_object(data):
    db.session.delete(data)
    db.session.commit()