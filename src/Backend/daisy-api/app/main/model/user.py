from .. import db

class User(db.Model):
    __tablename__ = "user"
    id = db.Column(db.String(28), primary_key=True)
    username = db.Column(db.String(20), unique=True)
    pair_pin = db.Column(db.String(9), unique=False)
    home_assist_ID = db.relationship("HomeAssist", backref="user")
    phone_ID = db.relationship("Phone", backref="user")
    answer_returned_ID = db.relationship("AnswerReturned", backref="user")
    user_details_ID = db.relationship("UserDetails", backref="user")

    def __init__(self, id, username, pair_pin):
        self.id = id
        self.username = username
        self.pair_pin = pair_pin

    def json(self):        
        return {'ID': self.id, 'Username': self.username, 'Pair Pin': self.pair_pin}