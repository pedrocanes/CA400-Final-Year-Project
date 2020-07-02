import datetime

from app.main import db
from app.main.model.question import Question
from flask import jsonify
import json

'''
Creates a new question object by first checking if the question object already exists; 
it returns a success response_object if the question object doesn’t exist 
else it returns an error code 409 and a failure response_object
'''

def save_new_question(data):
    question = Question.query.filter_by(id=data['id']).first()
    if not question:
        new_question = Question(
           id=data['id'],
           question=data['question'],
           researcher_ID=data['researcher_ID']
        )
        save_changes(new_question)
        response_object = {
            'status': 'success',
            'message': 'successfully registered.'
        }
        return response_object, 201
    else:
        response_object = {
            'status': 'fail',
            'message': 'question already registered.',
        }
        return response_object, 409

'''
Return a list of all registered questions and a question objects by 
providing the id respectively
'''
def get_all_questions():
    return Question.query.all()

def get_a_question(id):
    return Question.query.filter_by(id=id).first()

def delete_question(id):
    question  = Question.query.filter_by(id=id).first()        
    if question:           
        delete_object(question)
        response_object = {
            'status': 'success',
            'message': 'question successfully deleted.'
        }
        return response_object, 200                  
    response_object = {
            'status': 'fail',
            'message': 'question does not exist.'
        }
    return response_object, 204

def update_question(id, data):
    question = Question.query.filter_by(id=id).first()
    if question:
        new_question = data['question']
        if question.question == new_question:
            response_object = {
                'status': 'fail',
                'message': 'question already exists.'
            }
            return response_object
        else:
            question.question = new_question
            db.session.commit()
            response_object = {
                'status': 'success',
                'message': 'question successfully updated.'
            }
            return response_object, 200
    response_object = {
        'status': 'fail',
        'message': 'question does not exist.'
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