from .. import db

class Phone(db.Model):
    __tablename__ = "phone"
    id = db.Column(db.String(28), primary_key=True)
    serial_key = db.Column(db.String(100))
    lat_long = db.Column(db.String(50))
    user_ID = db.Column(db.String(28), db.ForeignKey("user.id"))

    def __init__(self, id, serial_key, lat_long, user_ID):
        self.id = id
        self.serial_key = serial_key
        self.lat_long = lat_long
        self.user_ID = user_ID

    def json(self):        
        return {'ID': self.id, 'Serial Key': self.serial_key, 'GPS': self.lat_long, 'User ID': self.user_ID}
