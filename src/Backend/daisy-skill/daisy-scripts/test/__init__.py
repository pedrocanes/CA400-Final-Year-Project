from unittest.mock import MagicMock
from test.integrationtests.skills.skill_tester import SkillTest

def test_runner(skill, example, emitter, loader):

    # Get the daisy skill object from the skill path
    s = [s for s in loader.skills if s and s.root_dir == skill]

    # replace the best service with a mock
    s[0].best = MagicMock()
    # Set a valid return value for get_top_five
    s[0].best.get_top_five.return_value = {
        'one': 'http://example.com',
        'two': 'http://example.com',
        'three': 'http://example.com',
        'four': 'http://example.com',
        'five': 'http://example.com'
    }

    # Set a valid return value for get_best
    s[0].best.get_best.return_value = "http://example.com"

    return SkillTest(skill, example, emitter).run(loader)