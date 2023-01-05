# Let-there-be-A.I.

This is an automation system based on reinforcement learning. The model learns in real time, which action (operation of electrical appliances) should be taken in a particular situation, based on a combination of various parameters like, time of day, ambient light, presence of a human, temperature and also the mood of an individual (the trainer). To train the model in real time an android app has been developed to set the rewards after an action is taken by the model.

There are two modes:
Manual: Where the control of the appliances can be done using the android app.
Automatic: Where the control is handed over to the trained model.

The Reinforcement learning model was developed and run on a desktop and it communicates with the android app and the actuating device (esp8266) through google firebase.



There are four folders in this repository and the Contents are as follows:

"Neural Network Model": Code for building the A.I.

"Face detection using open CV": Code to detect a human face

"Android App": Files to build an android app for the gardening system.

"node MCU": Contains 2 files for 2 esp8266 used in the project. 

