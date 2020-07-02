'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource

from ..util.dto import AnswerReturnedDto
from ..service.answer_returned_service import save_new_answer_returned, get_all_answers_returned, get_an_answer_returned, get_users_answers_returned, delete_answer_returned, update_answer_returned

api = AnswerReturnedDto.api
_answerReturned = AnswerReturnedDto.answer_returned

parser = api.parser()
parser.add_argument('answer_returned', type=str, default='answer returned', required=True)

@api.route('/')
class AnswerReturnedList(Resource):
    @api.doc('list_of_registered_answer_returned')
    @api.marshal_list_with(_answerReturned, envelope='data')
    def get(self):
        """List all registered answers returned"""
        return get_all_answers_returned()

    @api.response(201, 'AnswerReturned successfully registered.')
    @api.doc('register a new answer returned')
    @api.expect(_answerReturned, validate=True)
    def post(self):
        """Register a new answer returned """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'answer_returned': data
        }
        save_new_answer_returned(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The answer returned identifier')
class AnswerReturned(Resource):
    @api.doc('Get an answer_returned')
    @api.marshal_with(_answerReturned)
    def get(self, id):
        """Get an answer returned given its identifier"""
        answer_returned = get_an_answer_returned(id)
        if not answer_returned:
            api.abort(404, 'answer_returned not found.')
        else:
            return answer_returned

    @api.response(200, 'answer returned successfully deleted.')
    @api.doc('Delete an answer_returned')
    @api.marshal_with(_answerReturned)
    def delete(self, id):
        """Delete an answer returned given its identifier"""
        answer_returned = get_an_answer_returned(id)
        if not answer_returned:
            api.abort(404, 'answer returned not found.')
        else:
            delete_answer_returned(id)
            return answer_returned

    @api.response(200, 'Answer Returned successfully updated.')
    @api.doc('Update answer returned')
    @api.expect(parser)
    def put(self, id):
        """Update an answer returned given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"answer_returned": "value"}
        answer_returned = get_an_answer_returned(id)
        if not answer_returned:
            api.abort(404, 'answer returned not found.')
        else:
            data = request.json
            return update_answer_returned(id, data=args)
			
@api.route('/user/<user_id>')
@api.param('user_id', 'The user details user identifier')
class AnswerReturned(Resource):
	@api.doc('Get phone from user details')
	@api.marshal_with(_answerReturned)
	def get(self, user_id):
		"""Get users user details given its identifier"""
		users_answer_returned = get_users_answers_returned(user_id)
		if not users_answer_returned:
			api.abort(404, 'user not found.')
		else:
			return users_answer_returned
