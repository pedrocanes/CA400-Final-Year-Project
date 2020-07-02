from .. import db

class UserDetails(db.Model):
	__tablename__ = "user_details"
	id = db.Column(db.String(28), primary_key=True)
	user_ID = db.Column(db.String(28), db.ForeignKey("user.id"))
	question_ID = db.Column(db.String(28), db.ForeignKey("question.id"))
	ask_question = db.Column(db.Boolean, default=False)
	user_available = db.Column(db.String(20))
	device_to_use = db.Column(db.String(20))
	
	def __init__(self, id, user_ID, question_ID, ask_question, device_to_use, user_available):
		self.id = id
		self.user_ID = user_ID
		self.question_ID = question_ID
		self.ask_question = ask_question
		self.device_to_use = device_to_use
		self.user_available = user_available
		
	def json(self):
		return {'ID': self.id, 'User ID': self.user_ID, 'Question ID': self.question_ID, 'Ask Question': ask_question, 'User Available': user_available, 'Device to Use': device_to_use}