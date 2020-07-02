'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource

from ..util.dto import HomeAssistDto
from ..service.home_assistant_service import save_new_home_assistant, get_all_home_assistants, get_a_home_assistant, get_users_home_assistant, delete_home_assistant, update_home_assistant

api = HomeAssistDto.api
_homeAssist = HomeAssistDto.home_assist

parser = api.parser()
parser.add_argument('lat_long', type=str, default='latitude longitude coordinates', required=True)

@api.route('/')
class HomeAssistList(Resource):
    @api.doc('list_of_registered_home_assistants')
    @api.marshal_list_with(_homeAssist, envelope='data')
    def get(self):
        """List all registered home assistants"""
        return get_all_home_assistants()

    @api.response(201, 'Home Assistant successfully registered.')
    @api.doc('create a new home assistant')
    @api.expect(_homeAssist, validate=True)
    def post(self):
        """Register a new home assistant """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'home assistant': data
        }
        save_new_home_assistant(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The Home Assistant identifier')
class HomeAssist(Resource):
    @api.doc('Get a home assistant')
    @api.marshal_with(_homeAssist)
    def get(self, id):
        """Get a home assistant given its identifier"""
        home_assistant = get_a_home_assistant(id)
        if not home_assistant:
            api.abort(404, 'Home Assistant not found.')
        else:
            return home_assistant

    @api.response(200, 'Home Assistant successfully deleted.')
    @api.doc('Delete a home assistant')
    @api.marshal_with(_homeAssist)
    def delete(self, id):
        """Delete a home assistant given its identifier"""
        home_assistant = get_a_home_assistant(id)
        if not home_assistant:
            api.abort(404, 'Home Assistant not found.')
        else:
            delete_home_assistant(id)
            return home_assistant

    @api.response(200, 'Home Assistants GPS info successfully updated.')
    @api.doc('Update home assistants gps coordinates')
    @api.expect(parser)
    def put(self, id):
        """Update a home assistants gps coordinates given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"lat_long": "value"}
        home_assistant = get_a_home_assistant(id)
        if not home_assistant:
            api.abort(404, 'Home Assistant not found.')
        else:
            data = request.json
            return update_home_assistant(id, data=args)
			
@api.route('/user/<user_id>')
@api.param('user_id', 'The user details user identifier')
class HomeAssist(Resource):
	@api.doc('Get phone from user details')
	@api.marshal_with(_homeAssist)
	def get(self, user_id):
		"""Get users user details given its identifier"""
		user_home_assistant = get_users_home_assistant(user_id)
		if not user_home_assistant:
			api.abort(404, 'user not found.')
		else:
			return user_home_assistant

