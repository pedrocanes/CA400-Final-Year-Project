'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource

from ..util.dto import PhoneDto
from ..service.phone_service import save_new_phone, get_all_phones, get_a_phone, get_users_phone, delete_phone, update_phone

api = PhoneDto.api
_phone = PhoneDto.phone

parser = api.parser()
parser.add_argument('lat_long', type=str, default='latitude longitude coordinates', required=True)

@api.route('/')
class PhoneList(Resource):
    @api.doc('list_of_registered_phone')
    @api.marshal_list_with(_phone, envelope='data')
    def get(self):
        """List all registered phones"""
        return get_all_phones()

    @api.response(201, 'Phone successfully registered.')
    @api.doc('create a new phone')
    @api.expect(_phone, validate=True)
    def post(self):
        """Register a new phone """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'phone': data
        }
        save_new_phone(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The phone identifier')
class Phone(Resource):
    @api.doc('Get a phone')
    @api.marshal_with(_phone)
    def get(self, id):
        """Get a phone given its identifier"""
        phone = get_a_phone(id)
        if not phone:
            api.abort(404, 'phone not found.')
        else:
            return phone

    @api.response(200, 'phone successfully deleted.')
    @api.doc('Delete a phone')
    @api.marshal_with(_phone)
    def delete(self, id):
        """Delete a phone given its identifier"""
        phone = get_a_phone(id)
        if not phone:
            api.abort(404, 'phone not found.')
        else:
            delete_phone(id)
            return phone

    @api.response(200, 'Phones GPS info successfully updated.')
    @api.doc('Update phone gps coordinates')
    @api.expect(parser)
    def put(self, id):
        """Update phone gps coordinates given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"lat_long": "value"}
        phone = get_a_phone(id)
        if not phone:
            api.abort(404, 'phone not found.')
        else:
            data = request.json
            return update_phone(id, data=args)
			
@api.route('/user/<user_id>')
@api.param('user_id', 'The user details user identifier')
class Phone(Resource):
	@api.doc('Get phone from user details')
	@api.marshal_with(_phone)
	def get(self, user_id):
		"""Get users user details given its identifier"""
		user_phone = get_users_phone(user_id)
		if not user_phone:
			api.abort(404, 'user not found.')
		else:
			return user_phone
