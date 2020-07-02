import os
import unittest
from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from app.main import create_app, db
from app import blueprint
from app.main.model import user, researcher, question, answer, answer_returned, home_assistant, phone, user_details

app = create_app('prod')
app.app_context().push()
manager = Manager(app)
migrate = Migrate(app, db)
manager.add_command('db', MigrateCommand)
app = create_app('prod')
app.register_blueprint(blueprint)
app.app_context().push()

@manager.command
def run():
    app.run()

@manager.command
def test():
    """Runs the unit tests."""
    tests = unittest.TestLoader().discover('app/test', pattern='test*.py')
    result = unittest.TextTestRunner(verbosity=2).run(tests)
    if result.wasSuccessful():
        return 0
    return 1

if __name__ == '__main__':
    manager.run()