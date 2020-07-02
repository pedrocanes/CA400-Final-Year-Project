from .. import db

class Researcher(db.Model):
    __tablename__ = "researcher"
    id = db.Column(db.String(28), primary_key=True)
    username = db.Column(db.String(20), unique=True)
    email = db.Column(db.String(50), unique=True)
    question_ID = db.relationship("Question", backref="researcher")

    def __init__(self, id, username, email):
        self.id = id
        self.username = username
        self.email = email

    def json(self):        
        return {'ID': self.id, 'Username': self.username, 'Email': self.email}