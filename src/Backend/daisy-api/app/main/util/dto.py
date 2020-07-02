from flask_restplus import Namespace, fields

class UserDto:
    api = Namespace('User', description='User related operations')
    user = api.model('User', {
        'id': fields.String(required=True, description='User Identifier'),
        'username': fields.String(required=True, description='User Username'),
        'pair_pin': fields.String(required=True, description='User Pair Pin')
    })

class UserDetailsDto:
    api = Namespace('User Details', description='User Details related operations')
    user_details = api.model('User Details', {
        'id': fields.String(required=True, description='User Details Identifier'),
        'user_ID': fields.String(required=True, description='User Details related User ID'),
        'question_ID': fields.String(required=True, description='User Details related Question ID'),
		'ask_question': fields.Boolean(required=False, default=False, description='Set to Bool deending on whether this question is to be asked'),
		'user_available': fields.String(required=False, default="null", description='Set to Bool deending on whether user is available for picroft'),
		'device_to_use': fields.String(default='phone/home_assistant', required=False, description='<phone>/<home-assistant>')
    })

class ResearcherDto:
    api = Namespace('Researcher', description='Researcher related operations')
    researcher = api.model('Researcher', {
        'id': fields.String(required=True, description='Researcher Identifier'),
        'username': fields.String(required=True, description='Researcher Username'),
        'email': fields.String(required=True, description='Researcher Email')
    })

class HomeAssistDto:
    api = Namespace('Home Assistant', description='Home assistant related operations')
    home_assist = api.model('Home Assistant', {
        'id': fields.String(required=True, description='Home Assistant Identifier'),
        'serial_key': fields.String(required=True, description='Home Assistant Serial Key'),
        'lat_long': fields.String(required=True, description='Home Assistant GPS coordinates'),
        'user_ID': fields.String(required=True, description='Home Assistants related User ID')
    })

class PhoneDto:
    api = Namespace('Phone', description='Phone related operations')
    phone = api.model('Phone', {
        'id': fields.String(required=True, description='Phone Identifier'),
        'serial_key': fields.String(required=True, description='Phone serial key'),
        'lat_long': fields.String(required=True, description='Phone GPS coordinates'),
        'user_ID': fields.String(required=True, description='Phone related User ID')
    })

class QuestionDto:
    api = Namespace('Question', description='Question related operations')
    question = api.model('Question', {
        'id': fields.String(required=True, description='Question Identifier'),
        'question': fields.String(required=True, description='Question Details'),
        'researcher_ID': fields.String(required=True, description='Question related Researcher ID')
    })

class AnswerDto:
    api = Namespace('Answer', description='Answer related operations')
    answer = api.model('Answer', {
        'id': fields.String(required=True, description='Answer Identifier'),
        'answer': fields.String(required=True, description='Answer Details'),
        'question_ID': fields.String(required=True, description='Answer related Question ID')
    })

class AnswerReturnedDto:
    api = Namespace('Answer Returned', description='Answer Returned related operations')
    answer_returned = api.model('Answer Returned', {
        'id': fields.String(required=True, description='Answer Returned Identifier'),
	    'answer_time': fields.String(required=True, description='Time Answer was Returned in format: yyyy-mm-dd hh-mm-ss'),
	    'device_used': fields.String(required=True, description='Device Used for Answer <phone>/<home-assistant>'),
        'user_ID': fields.String(required=True, description='Answer related User ID'),
        'answer_ID': fields.String(required=True, description='Answer related Answer ID')
    })