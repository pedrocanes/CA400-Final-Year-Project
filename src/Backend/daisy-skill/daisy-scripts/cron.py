from crontab import CronTab
import os

cron = CronTab(user='pi')
root_dir = "/opt/mycroft/skills/daisy-skill/daisy-scripts"
script_path = root_dir + "/get_questions.py"
job = cron.new(command='python3 ' + script_path)
job.minute.every(1)

cron.write()

