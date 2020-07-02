from .. import db

class Question(db.Model):
    __tablename__ = "question"
    id = db.Column(db.String(28), primary_key=True)
    question = db.Column(db.String(100))
    researcher_ID = db.Column(db.String(28), db.ForeignKey("researcher.id"))
    answer_ID = db.relationship("Answer", backref="question")
    user_ID = db.relationship("UserDetails", backref="question")

    def __init__(self, id, question, researcher_ID):
        self.id = id
        self.question = question
        self.researcher_ID = researcher_ID

    def json(self):        
        return {'ID': self.id, 'Question': self.question, 'Researcher ID': self.researcher_ID}