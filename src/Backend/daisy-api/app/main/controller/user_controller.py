'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource


from ..util.dto import UserDto
from ..service.user_service import save_new_user, get_all_users, get_a_user, delete_user, update_user

api = UserDto.api
_user = UserDto.user

parser = api.parser()
parser.add_argument('pair_pin', type=str, default='pair_pin', required=True)

@api.route('/')
class UserList(Resource):
    @api.doc('list_of_registered_users')
    @api.marshal_list_with(_user, envelope='data')
    def get(self):
        """List all registered users"""
        return get_all_users()

    @api.response(201, 'User successfully created.')
    @api.doc('create a new user')
    @api.expect(_user, validate=True)
    def post(self):
        """Creates a new User """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'user': data
        }
        save_new_user(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The User identifier')
class User(Resource):
    @api.doc('Get a user')
    @api.marshal_with(_user)
    def get(self, id):
        """Get a user given its identifier"""
        user = get_a_user(id)
        if not user:
            api.abort(404, 'User not found.')
        else:
            return user

    @api.response(200, 'User successfully deleted.')
    @api.doc('Delete a user')
    @api.marshal_with(_user)
    def delete(self, id):
        """Delete a user given its identifier"""
        user = get_a_user(id)
        if not user:
            api.abort(404, 'User not found.')
        else:
            delete_user(id)
            return user

    @api.response(200, 'User pair pin successfully updated.')
    @api.doc('Update a users pair pin')
    @api.expect(parser)
    def put(self, id):
        """Update a user pair pin given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"pair_pin": "value"}
        user = get_a_user(id)
        if not user:
            api.abort(404, 'User not found.')
        else:
            data = request.json
            return update_user(id, data=args)
