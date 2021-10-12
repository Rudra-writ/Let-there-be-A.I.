import cv2
import numpy as np

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import datetime
from Artificial_Intelligence import AI
import time

cred = credentials.Certificate('D:\service_account/artificial_brain.json')
firebase_admin.initialize_app(cred, {
    'databaseURL' : 'https://artificial-brain.firebaseio.com'
})

root = db.reference()
root.set({
    'Human_present':'false',
    'Temperature' : 'cold',
    'Lights':'off',
    'Moisture':'dry',
    'Dark':'true',
    'fan':'off',
    'pump':'off',
    'Human_control':'false',
    'manual_light':'off',
    'manual_fan':'off',
    'manual_pump':'off'
    
    })

agent = AI(3,4,0.9)

PossibleActions = ['light_on','light_off','fan_on','fan_off']  #lights on , lights off, fan on, fan off, pump on, pump off

face = cv2.CascadeClassifier('haarcascade_frontalface_alt.xml')

eyes = cv2.CascadeClassifier('haarcascade_eye.xml')


if face.empty():
    raise IOError('Unable to Load haarcascade_frontalface_alt.xml file')


if eyes.empty():
    raise IOError('Unable to load haarcascade_eye.xml file')
    

start_capture = cv2.VideoCapture(0)

previous_minute = 0

last_reward = -0.1

light = False
fan= False
pump = False
last_action = None
discount = 0
human = False
human_control = "false"


def evaluate_reward(ishuman,isdark,ishot,last_reward,light,fan,pump):
    if  (ishuman == 10 and isdark == 10 and light == 'off') or (ishot == 10 and ishuman == 10 and fan == 'off')   or (ishuman == 5 and isdark == 10 and light == 'on') or (ishot == 5 and ishuman == 10 and fan == 'on') or (ishot == 10 and ishuman == 5 and fan == 'on') or (ishot ==5 and ishuman == 5 and fan == 'on'):
        reward = 0
        if (ishuman == 10 and isdark == 10 and light == 'off'):
            reward = reward - 0.25
        if (ishot == 10 and ishuman == 10 and fan == 'off'):
            reward = reward - 0.25
       
        if (ishuman == 5 and isdark == 10 and light == 'on'):
            reward = reward - 0.25
        if (ishot == 5 and ishuman == 10 and fan == 'on'):
            reward = reward - 0.25
        if (ishot == 10 and ishuman == 5 and fan == 'on'):
            reward = reward - 0.25
        if (ishot == 5 and ishuman == 5 and fan == 'on'):
            reward = reward - 0.25
      
        last_reward = reward
        return last_reward
    
    
    elif (ishuman == 10 and isdark ==10 and light == 'on') or (ishot == 10 and ishuman == 10 and fan == 'on') :
        last_reward = 0.8
    elif (ishuman == 5 and isdark ==10 and light == 'off') or (ishot == 5 and ishuman == 5 and fan == 'off') or (ishot == 10 and ishuman == 5 and fan == 'off') :
        last_reward = 0.8
   
    return last_reward



if input("would you like to load last memory? y/n?") == 'y':
    agent.load()
    


while True:
    minute = datetime.datetime.now().minute
    hour = datetime.datetime.now().hour
    eye = 0
    
    if human_control == 'false':
          ret, frame = start_capture.read()
    
          resize_frame = cv2.resize(frame, None, fx=0.5, fy=0.5, 
          interpolation=cv2.INTER_AREA)
          gray = cv2.cvtColor(resize_frame, cv2.COLOR_BGR2GRAY)
          face_found = face.detectMultiScale(gray, 1.3, 5)
          for(x,y,width,height) in face_found:
             cv2.rectangle(resize_frame,(x,y),(x+width,y+height),(0,0,255),10)
             gray_roi=gray[y:y+height,x:x+width] #get the region of interest from the gray image
             eye_found = eyes.detectMultiScale(gray_roi) # detect eyes, if present in the gray region of interest
             
             if (len(eye_found) != 0) and human_control == "false":  
               root.update(
                 {
                       'Human_present': 'true'
                       })
               eye = 1
               present_minute = minute
         
    cv2.imshow("Real-time Detection", resize_frame)
    
    
  
    # Read data from firebase data base
    read_db = root.get()
    read_human_present = read_db['Human_present']
    if read_human_present == 'true':
        ishuman = 10
    else:
        ishuman = 5
    read_dark = read_db['Dark']
    if read_dark == 'true':
        isdark = 10
    else:
        isdark = 5
  
    read_temperature = read_db['Temperature']
    if read_temperature == 'hot':
        ishot = 10
    else:
        ishot = 5
    read_moisture =  read_db['Moisture']
    if read_moisture == 'dry':
        isdry = 10
    else:
        isdry = 5
    light = read_db['Lights']
    fan = read_db['fan']
    pump = read_db['pump']
    human_control = read_db['Human_control']
    
   
    #Update A.I with new values and get the action to play
    new_state = [ishuman,isdark,ishot]    
    action = agent.update(last_reward, new_state)
    final_action = PossibleActions[action]
    
    
     #Evaluate rewards for training A.I
    print(final_action)
   
    last_reward = evaluate_reward(ishuman,isdark,ishot,last_reward,light,fan,pump)
   
    print(last_reward)
  
    
    #Evaluation complete...
  
    
    # Upload the action suggested by A.I to firebase database
    if final_action == 'light_on':
        root.update(
            {
               'Lights': 'on' }
            
            )
      
    elif final_action == 'light_off':
        root.update(
            {
               'Lights': 'off' }
            )
       
    elif final_action == 'fan_on':
        root.update(
            {
               'fan': 'on' }
            )
      
    elif final_action == 'fan_off':
        root.update(
            {
               'fan': 'off' }
            )
       
    if hour == 7 and isdry == 10 and (minute-previous_minute) <= 10 :
        root.update(
            {
               'pump': 'on' }
            )
    else:
        root.update(
                {
                    'pump': 'off' }
                )
    if eye == 0 and human_control == 'false':
         root.update(
                  {   'Human_present': 'false'
                      })  
                   
        
        
    
    c = cv2.waitKey(1)
    if c == 27:
        break
 
# Close the capturing device

#agent.save()
start_capture.release()

# Close all windows
cv2.destroyAllWindows()
