'''
The user controller class handles all the incoming HTTP 
requests relating to the user
'''

from flask import request, jsonify
from flask_restplus import Resource

from ..util.dto import ResearcherDto
from ..service.researcher_service import save_new_researcher, get_all_researchers, get_a_researcher, delete_researcher, update_researcher

api = ResearcherDto.api
_researcher = ResearcherDto.researcher

parser = api.parser()
parser.add_argument('username', type=str, default='username', required=True)

@api.route('/')
class ResearcherList(Resource):
    @api.doc('list_of_registered_researchers')
    @api.marshal_list_with(_researcher, envelope='data')
    def get(self):
        """List all registered researchers"""
        return get_all_researchers()

    @api.response(201, 'Researcher successfully registered.')
    @api.doc('create a new researcher')
    @api.expect(_researcher, validate=True)
    def post(self):
        """Register a new researcher """
        data = request.json
        response_object = {
            'status': 'success',
            'message': 'Successfully registered.',
            'researcher': data
        }
        save_new_researcher(data=data)
        return response_object


@api.route('/<id>')
@api.param('id', 'The researcher identifier')
class Researcher(Resource):
    @api.doc('Get a researcher')
    @api.marshal_with(_researcher)
    def get(self, id):
        """Get a researcher given its identifier"""
        researcher = get_a_researcher(id)
        if not researcher:
            api.abort(404, 'researcher not found.')
        else:
            return researcher

    @api.response(200, 'researcher successfully deleted.')
    @api.doc('Delete a researcher')
    @api.marshal_with(_researcher)
    def delete(self, id):
        """Delete a researcher given its identifier"""
        researcher = get_a_researcher(id)
        if not researcher:
            api.abort(404, 'researcher not found.')
        else:
            delete_researcher(id)
            return researcher

    @api.response(200, 'Researchers username successfully updated.')
    @api.doc('Update researcher username')
    @api.expect(parser)
    def put(self, id):
        """Update researcher username given it's identifier"""
        args = parser.parse_args(strict=True) #This passes through the following: {"username": "value"}
        researcher = get_a_researcher(id)
        if not researcher:
            api.abort(404, 'researcher not found.')
        else:
            data = request.json
            return update_researcher(id, data=args)
