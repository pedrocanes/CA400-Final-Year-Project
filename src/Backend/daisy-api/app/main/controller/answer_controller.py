'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource

from ..util.dto import AnswerDto
from ..service.answer_service import save_new_answer, get_all_answers, get_an_answer, get_questions_answers, delete_answer, update_answer

api = AnswerDto.api
_answer = AnswerDto.answer

parser = api.parser()
parser.add_argument('answer', type=str, default='answer', required=True)

@api.route('/')
class AnswerList(Resource):
    @api.doc('list_of_registered_answer')
    @api.marshal_list_with(_answer, envelope='data')
    def get(self):
        """List all registered answers"""
        return get_all_answers()

    @api.response(201, 'Answer successfully registered.')
    @api.doc('register a new answer')
    @api.expect(_answer, validate=True)
    def post(self):
        """Register a new answer """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'answer': data
        }
        save_new_answer(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The answer identifier')
class Answer(Resource):
    @api.doc('Get an answer')
    @api.marshal_with(_answer)
    def get(self, id):
        """Get an answer given its identifier"""
        answer = get_an_answer(id)
        if not answer:
            api.abort(404, 'answer not found.')
        else:
            return answer

    @api.response(200, 'answer successfully deleted.')
    @api.doc('Delete an answer')
    @api.marshal_with(_answer)
    def delete(self, id):
        """Delete an answer given its identifier"""
        answer = get_an_answer(id)
        if not answer:
            api.abort(404, 'answer not found.')
        else:
            delete_answer(id)
            return answer

    @api.response(200, 'Answer successfully updated.')
    @api.doc('Update answer')
    @api.expect(parser)
    def put(self, id):
        """Update an answer given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"answer": "value"}
        answer = get_an_answer(id)
        if not answer:
            api.abort(404, 'answer not found.')
        else:
            data = request.json
            return update_answer(id, data=args)
			
@api.route('/question/<question_id>')
@api.param('question_id', 'The question id')
class Answer(Resource):
	@api.doc('Get answer from question id')
	@api.marshal_with(_answer)
	def get(self, question_id):
		"""Get users user details given its identifier"""
		answers = get_questions_answers(question_id)
		if not answers:
			api.abort(404, 'answers not found.')
		else:
			return answers
