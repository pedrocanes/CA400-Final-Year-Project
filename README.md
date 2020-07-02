# CA400 Final Year Project: Daisy Voice Assitant

**Memebers:</br>
-Pedro Canes</br>
-Santiago de Arribas de Renedo**

The system is composed of a Raspberry Pi and an Android Mobile App. The system will allow researchers to ask users survey questions in order to perform ecological momentary assessment.
The researcher will ask a question and the system will send the question to the user either through the mobile app or through the Raspberry Pi. The chosen device is decided depending on the user's location and whether the user is in a social situation.

* The software (Daisy) will ask users survey questions in real time i.e. Ecological Momentary Assessment (EMA).
* Daisy to communicate to a backend Flask server.
* It will send requests to Picroft (home assistant) or mobile app based on:
    * If user is away from Picroft -> Send request to mobile app
    * If user is near the Picroft (and in a social situation) -> Send request to mobile app
    * If user is near the Picroft (and not in a social situation) -> Send request to Picroft
* Server receives data from mobile app and/or Picroft - via an API.
* Store data in database.
* Access this data to be processed via Machine Learning to either:
    * Predict next best time to ask questions.
    * Perform other predictions and perform appropriate actions.
