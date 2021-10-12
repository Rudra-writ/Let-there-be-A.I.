#include <Firebase.h>
#include <FirebaseArduino.h>
#include <FirebaseCloudMessaging.h>
#include <FirebaseError.h>
#include <FirebaseHttpClient.h>
#include <FirebaseObject.h>



#include <ESP8266WiFi.h>


#define FIREBASE_HOST "paste your own firebase host"
#define FIREBASE_AUTH "paste your own firebase auth code"
#define WIFI_SSID "paste your own WiFi ssid"
#define WIFI_PASSWORD "paste your WiFi password"


float call;
void setup() {
  Serial.begin(9600);

pinMode(D7,OUTPUT);
pinMode(D5,OUTPUT);
pinMode(A0,INPUT);
pinMode(D2,INPUT);

  // connect to wifi.
  WiFi.begin(WIFI_SSID,WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.set("Dark","True");
  Firebase.setString("Temperature","cold");
  
}

int n = 0;

void loop() {
String Human_control = Firebase.getString("Human_control");
float temp_val = analogRead(A0);
float temp = ((temp_val*500)/1023);
Serial.println(temp);
if (temp >= 27 and Human_control == "false")
{
Firebase.setString("Temperature","hot");
}
else
{
Firebase.setString("Temperature", "cold");
}
int status = digitalRead(D2);
if(status == HIGH and Human_control == "false")
{
  Firebase.setString("Dark", "false");
  }
  else if (status == LOW)
  {
    Firebase.setString("Dark", "true");
  }

if (Human_control == "false")
{
String light = Firebase.getString("Lights");
if(light == "on")
{
 digitalWrite(D7, HIGH);
 Serial.println("lights are on");
}
else if (light == "off")
{
  digitalWrite(D7,LOW);
}
String fan = Firebase.getString("fan");
if(fan == "on")
{
 digitalWrite(D5, HIGH);
}
else if (fan == "off")
{
  digitalWrite(D5,LOW);
}
}

else if(Human_control == "true")
{
String manual_light = Firebase.getString("manual_light");
if(manual_light == "on")
{
 digitalWrite(D7, HIGH);
}
else if (manual_light == "off")
{
  digitalWrite(D7,LOW);
}
String manual_fan = Firebase.getString("manual_fan");
if(manual_fan == "on")
{
 digitalWrite(D5, HIGH);
}
else if (manual_fan == "off")
{
  digitalWrite(D5,LOW);
}
}
 delay(100); 
}
