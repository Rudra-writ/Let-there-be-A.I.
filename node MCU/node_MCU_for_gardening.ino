#include <Firebase.h>
#include <FirebaseArduino.h>
#include <FirebaseCloudMessaging.h>
#include <FirebaseError.h>
#include <FirebaseHttpClient.h>
#include <FirebaseObject.h>



#include <ESP8266WiFi.h>


#define FIREBASE_HOST "paste Your own host"
#define FIREBASE_AUTH "paste your own auth code"
#define WIFI_SSID "paste your own wifi ssid"
#define WIFI_PASSWORD "paste your wifi password"


float call;
void setup() {
  Serial.begin(9600);

pinMode(D5,OUTPUT);

pinMode(A0,INPUT);


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
  Firebase.setString("Moisture","dry");
  Firebase.setString("pump","off");
  
}

float limit = 200;

void loop() {
String Human_control = Firebase.getString("Human_control");
float moisture = analogRead(A0);
if (moisture > limit)
{
  Firebase.setString("Moisture","dry");
}
else
{
  Firebase.setString("Moisture","wet");
}
if (Human_control == "false")
{
String pump = Firebase.getString("pump");
if(pump == "on")
{
 digitalWrite(D5, HIGH);
}
else if (pump == "off")
{
  digitalWrite(D5,LOW);
}
}

else if(Human_control == "true")
{
String manual_pump = Firebase.getString("manual_pump");
if(manual_pump == "on")
{
 digitalWrite(D5, HIGH);
}
else if (manual_pump == "off")
{
  digitalWrite(D5,LOW);
}

}
 delay(100); 
}
