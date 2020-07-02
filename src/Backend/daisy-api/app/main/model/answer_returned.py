from .. import db

class AnswerReturned(db.Model):
    __tablename__ = "answer_returned"
    id = db.Column(db.String(28), primary_key=True)
    answer_time = db.Column(db.String(28))
    device_used = db.Column(db.String(20))
    user_ID = db.Column(db.String(28), db.ForeignKey("user.id"))
    answer_ID = db.Column(db.String(28), db.ForeignKey("answer.id"))

    def __init__(self, id, answer_time, device_used, user_ID, answer_ID):
        self.id = id
        self.answer_time = answer_time
        self.device_used = device_used
        self.user_ID = user_ID
        self.answer_ID = answer_ID

    def json(self):        
        return {'ID': self.id, 'Answer time': answer_time, 'Device used': device_used, 'User ID': self.user_ID, 'Answer ID': self.answer_ID}