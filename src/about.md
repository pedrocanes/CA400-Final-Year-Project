# Project system overview

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