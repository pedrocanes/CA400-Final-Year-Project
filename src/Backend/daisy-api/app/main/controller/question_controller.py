'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource

from ..util.dto import QuestionDto
from ..service.question_service import save_new_question, get_all_questions, get_a_question, delete_question, update_question

api = QuestionDto.api
_question = QuestionDto.question

parser = api.parser()
parser.add_argument('question', type=str, default='question', required=True)

@api.route('/')
class QuestionList(Resource):
    @api.doc('list_of_registered_questions')
    @api.marshal_list_with(_question, envelope='data')
    def get(self):
        """List all registered questions"""
        return get_all_questions()

    @api.response(201, 'Question successfully registered.')
    @api.doc('create a new question')
    @api.expect(_question, validate=True)
    def post(self):
        """Register a new question """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'question': data
        }
        save_new_question(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The question identifier')
class Question(Resource):
    @api.doc('Get a question')
    @api.marshal_with(_question)
    def get(self, id):
        """Get a question given its identifier"""
        question = get_a_question(id)
        if not question:
            api.abort(404, 'question not found.')
        else:
            return question

    @api.response(200, 'question successfully deleted.')
    @api.doc('Delete a question')
    @api.marshal_with(_question)
    def delete(self, id):
        """Delete a question given its identifier"""
        question = get_a_question(id)
        if not question:
            api.abort(404, 'question not found.')
        else:
            delete_question(id)
            return question

    @api.response(200, 'Question successfully updated.')
    @api.doc('Update question')
    @api.expect(parser)
    def put(self, id):
        """Update question given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"question": "value"}
        question = get_a_question(id)
        if not question:
            api.abort(404, 'question not found.')
        else:
            data = request.json
            return update_question(id, data=args)
