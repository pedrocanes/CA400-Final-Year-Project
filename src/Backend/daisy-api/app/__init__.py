from flask_restplus import Api
from flask import Blueprint

from .main.controller.user_controller import api as user_ns
from .main.controller.researcher_controller import api as researcher_ns
from .main.controller.home_assistant_controller import api as home_assistant_ns
from .main.controller.phone_controller import api as phone_ns
from .main.controller.question_controller import api as question_ns
from .main.controller.answer_controller import api as answer_ns
from .main.controller.answer_returned_controller import api as answer_returned_ns
from .main.controller.user_details_controller import api as user_details_ns

blueprint = Blueprint('api', __name__)

api = Api(blueprint,
          title='Daisy API',
          version='1.0',
          description='The Daisy API is used to interact with the daisy-db for use within the Daisy system.'
          )

api.add_namespace(user_ns, path='/user')
api.add_namespace(researcher_ns, path='/researcher')
api.add_namespace(home_assistant_ns, path='/home-assistant')
api.add_namespace(phone_ns, path='/phone')
api.add_namespace(question_ns, path='/question')
api.add_namespace(answer_ns, path='/answer')
api.add_namespace(answer_returned_ns, path='/answer-returned')
api.add_namespace(user_details_ns, path='/user-details')
