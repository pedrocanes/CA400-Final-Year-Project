import datetime

from app.main import db
from app.main.model.answer import Answer
from flask import jsonify
import json

'''
Creates a new answer object by first checking if the answer object already exists; 
it returns a success response_object if the answer object doesn’t exist 
else it returns an error code 409 and a failure response_object
'''
def save_new_answer(data):
    answer = Answer.query.filter_by(id=data['id']).first()
    if not answer:
        new_answer = Answer(
           id=data['id'],
           answer=data['answer'],
           question_ID=data['question_ID']
        )
        save_changes(new_answer)
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'answer already registered.',
        }
        return response_object, 409

'''
Return a list of all registered answers and a answer objects by 
providing the id respectively
'''
def get_all_answers():
    return Answer.query.all()

def get_an_answer(id):
    return Answer.query.filter_by(id=id).first()
	
def get_questions_answers(question_id):
    return Answer.query.filter_by(question_ID=question_id).all()

def delete_answer(id):
    answer  = Answer.query.filter_by(id=id).first()        
    if answer:           
        delete_object(answer)
        response_object = {
            'status': 'success',
            'message': 'answer successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'answer does not exist.'
        }
    return response_object, 204

def update_answer(id, data):
    answer = Answer.query.filter_by(id=id).first()
    if answer:
        new_answer = data['answer']
        if answer.answer == new_answer:
            response_object = {
                'status': 'fail',
                'message': 'answer given has already been returned.'
            }
            return response_object
        else:
            answer.answer = new_answer
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'answer has been successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'answer does not exist.'
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