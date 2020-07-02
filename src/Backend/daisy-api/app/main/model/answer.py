from .. import db

class Answer(db.Model):
    __tablename__ = "answer"
    id = db.Column(db.String(28), primary_key=True)
    answer = db.Column(db.String(100))
    question_ID = db.Column(db.String(28), db.ForeignKey("question.id"))
    answer_returned_ID = db.relationship("AnswerReturned", backref="Answer")

    def __init__(self, id, answer, question_ID):
        self.id = id
        self.answer = answer
        self.question_ID = question_ID

    def json(self):        
        return {'ID': self.id, 'Answer': self.answer, 'Question ID': self.question_ID}