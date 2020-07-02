import datetime

from app.main import db
from app.main.model.answer_returned import AnswerReturned
from flask import jsonify
import json

'''
Creates a new answer_returned by first checking if the answer_returned already exists; 
it returns a success response_object if the answer_returned doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_answer_returned(data):
    answer_returned = AnswerReturned.query.filter_by(id=data['id']).first()
    if not answer_returned:
        new_answer_returned = AnswerReturned(
           id=data['id'],
		   answer_time=data['answer_time'],
           device_used=data['device_used'],
           user_ID=data['user_ID'],
           answer_ID=data['answer_ID']
        )
        save_changes(new_answer_returned)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'Answer Returned already registered.',
        }
        return response_object, 409

'''
Return a list of all registered Answers Returned and an Answer Returned objects by 
providing the id respectively
'''
def get_all_answers_returned():
    return AnswerReturned.query.all()

def get_an_answer_returned(id):
    return AnswerReturned.query.filter_by(id=id).first()
	
def get_users_answers_returned(user_id):
    return AnswerReturned.query.filter_by(user_ID=user_id).all()

def delete_answer_returned(id):
    answer_returned  = AnswerReturned.query.filter_by(id=id).first()        
    if answer_returned:           
        delete_object(answer_returned)
        response_object = {
            'status': 'success',
            'message': 'Answer Returned successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'Answer Returned does not exist.'
        }
    return response_object, 204

def update_answer_returned(id, data):
    answer_returned = AnswerReturned.query.filter_by(id=id).first()
    if answer_returned:
        new_answer_returned = data['answer_returned']
        if answer_returned.answer_returned == new_answer_returned:
            response_object = {
                'status': 'fail',
                'message': 'Answer Returned already has that answer.'
            }
            return response_object
        else:
            answer_returned.answer_returned = new_answer_returned
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'Answer Returned successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'Answer Returned does not exist.'
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