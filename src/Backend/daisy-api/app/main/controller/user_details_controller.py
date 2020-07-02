'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource
from flask_restplus import inputs

from ..util.dto import UserDetailsDto
from ..service.user_details_service import save_new_user_details, get_all_users_details, get_user_details, delete_user_details, update_user_details, get_users_user_details

api = UserDetailsDto.api
_userDetails = UserDetailsDto.user_details

parser = api.parser()
parser.add_argument('ask_question', type=inputs.boolean, default=False, required=False)
parser.add_argument('device_to_use', type=str, default='device to use: <phone>/<home-assistant>', required=False)
parser.add_argument('user_available', type=str, default='null', required=False)


@api.route('/')
class UserDetailsList(Resource):
	@api.doc('list_of_registered_user_details')
	@api.marshal_list_with(_userDetails, envelope='data')
	def get(self):
		"""List all registered user details"""
		return get_all_users_details()

	@api.response(201, 'UserDetails successfully registered.')
	@api.doc('register new user details')
	@api.expect(_userDetails, validate=True)
	def post(self):
		"""Register new user details """
		data = request.json
		response_object = {
			'status': 'success',
			'message': 'Successfully registered.',
			'user_details': data
		}
		save_new_user_details(data=data)
		return response_object


@api.route('/<id>')
@api.param('id', 'The user details identifier')
class UserDetails(Resource):
	@api.doc('Get a users details')
	@api.marshal_with(_userDetails)
	def get(self, id):
		"""Get user details given its identifier"""
		user_details = get_user_details(id)
		if not user_details:
			api.abort(404, 'user details not found.')
		else:
			print("Using THIS get request")
			return user_details

	@api.response(200, 'user details successfully deleted.')
	@api.doc('Delete a users details')
	@api.marshal_with(_userDetails)
	def delete(self, id):
		"""Delete a users details given its identifier"""
		user_details = get_user_details(id)
		if not user_details:
			api.abort(404, 'user details not found.')
		else:
			delete_user_details(id)
			return user_details
		
@api.route('/user/<user_id>')
@api.param('user_id', 'The user details user identifier')
class UserDetails(Resource):
	@api.doc('Get all a users details')
	@api.marshal_with(_userDetails)
	def get(self, user_id):
		"""Get users user details given its identifier"""
		user_details = get_users_user_details(user_id)
		if not user_details:
			api.abort(404, 'user details not found.')
		else:
			return user_details
			
	@api.response(200, 'User details successfully updated.')
	@api.doc('Update user details')
	@api.expect(parser)
	def put(self, user_id):
		"""Update a users details given it's identifier"""
		args = parser.parse_args(strict=True) #This passes through the following: {"ask_question": "value", "device_to_use": "value", "user_available": "value"}
		user = get_users_user_details(user_id)
		if not user:
			api.abort(404, 'user details not found.')
		else:
			data = request.json
			return update_user_details(user_id, data=args)

