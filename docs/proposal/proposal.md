# School of Computing &mdash; Year 4 Project Proposal Form

> Edit (then commit and push) this document to complete your proposal form.
> Make use of figures / diagrams where appropriate.
>
> Do not rename this file.

## SECTION A

|                     |                   |
|---------------------|-------------------|
|Project Title:       | Performing Ecological Momentary Assessments (EMA) via Picroft or mobile app|
|Student 1 Name:      | Santiago de Arribas de Renedo|
|Student 1 ID:        | 16449272|
|Student 2 Name:      | Pedro Marques Canes|
|Student 2 ID:        | 16770821|
|Project Supervisor:  | Prof Tomas Ward|

> Ensure that the Supervisor formally agrees to supervise your project; this is only recognised once the
> Supervisor assigns herself/himself via the project Dashboard.
>
> Project proposals without an assigned
> Supervisor will not be accepted for presentation to the Approval Panel.

## SECTION B

> Guidance: This document is expected to be approximately 3 pages in length, but it can exceed this page limit.
> It is also permissible to carry forward content from this proposal to your later documents (e.g. functional
> specification) as appropriate.
>
> Your proposal must include *at least* the following sections.


### Introduction

> Describe the general area covered by the project.

* This project would build a system (DAISY) that uses context detection and machine learning to perform Ecological Momentary Assessments (EMA) which asks users survey questions in real time.
* It pairs with Picroft: An open source voice assistant on a Raspberry Pi, and a mobile app to push private notifications via an app on a users phone. 
* The system could be enabled to use ePRO (electronic patient-reported outcome) methods to know what questions to ask users.
* Context detection can be used to determine appropriate times of when to ask these questions.
* The context detection would use the microphone and possibly digital signal processing to determine if the user is in a social situation or to pick up their voice if there are background sounds.
* If the user is not near the Picroft or is in a social situation then the system can send notifications to their smartphone for private responses.
* The picroft and phone app can then use APIs to send data back to the system for processing via Machine Learning.


### Outline

> Outline the proposed project.

* A system (Daisy) that performs Ecological Momentary Assessments (EMA) which asks users survey questions in real time. 
* Using context detection, the system can identify whether the user is in a social situation or away from the Picroft and send questions to their phone instead.
* This data can be retrieved and stored from the Picroft or mobile app to process.
* The system will be hosted on the Raspberry Pi along with the Picroft software.


### Background

> Where did the ideas come from?

* Initial context detection idea acquired from a supervisor's project ideas page - Prof Tomas Ward.
* Researching home assistants and discussing context detection helped build the idea around the Picroft and smartphone application.


### Achievements

> What functions will the project provide? Who will the users be?

* The system will allow us to collect data in real time from a home assistant and mobile app for processing via machine learning.
* Potential future users would be those with mental health problems i.e. Depression.
* The system will enable researchers to send survey questions to users and record responses in real time for more accurate data analysis.



### Justification

> Why/when/where/how will it be useful?

* The system could record data to process and perform certain actions.
* It can be used in or out of the users home, whenever they're near their phone or Picroft device, and at any time that is appropriate for the user. i.e. If the system can detect that the user is in a particular mood and asks questions as needed.
* It will be useful for researchers to allow the collection of data from users in real time.


### Programming language(s)

> List the proposed language(s) to be used.

*  Python
*  Java
*  JavaScript

### Programming tools / Tech stack

> Describe the compiler, database, web server, etc., and any other software tools you plan to use.

* Compiler
* Interpreter
* MySQL database
* APIs - Google Awareness API, Mycroft Skills AI API

### Hardware

> Describe any non-standard hardware components which will be required.

*  Mobile phone
*  Raspberry pi
*  PS3 Eye (microphone)
*  Logitech z50 (speakers)

### Learning Challenges

> List the main new things (technologies, languages, tools, etc) that you will have to learn.

* Using the Raspberry Pi and building the initial Picroft system will be the first challenge of this project.
* A mobile app will need to be built which will require learning how to use an appropriate IDE and programming for a new device. i.e. A smartphone.
* The system will need to send requests to the app and/or picroft and collect data.
* A challenge will be knowing when to use either the phone or Picroft and send requests to either appropriately - either based on social context or users proximity to each device.
* Implementing the context detection may require some digital signal proccesing to identify if the user is in a social situation.
* Machine learning will also be added to this project for simple data processing to potentially allow the system to know what times are best suited to ask users questions.
* These will both be contained in the main system (DAISY) which will be hosted on the raspberry pi but abstracted from the other project elements.


### Breakdown of work

> Clearly identify who will undertake which parts of the project.
>
> It must be clear from the explanation of this breakdown of work both that each student is responsible for
> separate, clearly-defined tasks, and that those responsibilities substantially cover all of the work required
> for the project.

The project will require setting up the Picroft, implementing the context detection and machine learning system and
it will also require a phone application to use this system and work with the Picroft as needed.

#### Student 1

> *Student 1 should complete this section.*

* Setting up Picroft.
* Use API to send data to DAISY.
* Develop Machine Learning and Context Detection System.

#### Student 2

> *Student 2 should complete this section.*

* Build smartphone application for android.
* Use API to send data to DAISY.
* Develop Machine Learning and Context Detection System.

## Example

An example of the project in action: Context detection and machine learning system (DAISY) detects that user is in a social situation and uses the phone appropriately.

<!-- Basically, just use HTML! -->

<p align="center">
  <img src="./res/daisy_system_diagram.jpg" width="800px">
</p>

