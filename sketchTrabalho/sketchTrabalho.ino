#include <WiFi.h>
#include <DallasTemperature.h>
#include <OneWire.h>

#define ONE_WIRE_BUS 2 

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

/*
* 
Wireless credentials
*
*/
const char ssid[] = ".....";     // network SSID (name)
const char pass[] = ".....";    // network password
int status = WL_IDLE_STATUS;     // the Wifi radio's status

WiFiClient client;
const char serverName[] = "192.168.1.7";
const int serverPort = 80;
char pageAdd[64];
char tempString[] = "00.00";
int totalCount = 0;

const int sensorPin = A1; // humidity
const int limit = 600;    // threshold for humidity sensor
const int LED = 8;      
int sensorValue;    // humidity

void setup() {
  Serial.begin(9600);
  sensors.begin();
  pinMode(LED, OUTPUT);
  
  // attempt to connect using WPA2 encryption:

  Serial.println("Attempting to connect to WPA network...");

  status = WiFi.begin(ssid, pass);

  // if you're not connected, wait 5 seconds and try again:

  if(status != WL_CONNECTED) {
    Serial.println(F("Couldn't get a wifi connection"));
    while(status != WL_CONNECTED) {
      delay(5000);
      Serial.println(F("Trying again"));
      status = WiFi.begin(ssid, pass);
    }
    Serial.println(F("Connected to network"));
    printMacAddress();
  }

  // if you are connected, print out info about the connection:

  else {
    Serial.println(F("Connected to network"));
    printMacAddress();
  }
}


void loop() {  
  /**
  *
  Light sensor
  *
  **/
  int value = analogRead(A0);
  Serial.print(F("Light sensor: "));
  Serial.println(value);

  sprintf(pageAdd, "/firebaseLight.php?arduino_data=%d", value);
  //Serial.println(pageAdd);
  if(!getPage(serverName, serverPort, pageAdd)) Serial.print(F("Fail "));
  else Serial.print(F("Pass "));
  totalCount++;
  Serial.println(totalCount, DEC);
  
  delay(1000);
  
  /**
  *
  Temperature sensor
  *
  **/
  Serial.print(F("Requesting temperatures...")); 
  sensors.requestTemperatures();
  Serial.println("DONE");
  
  Serial.print(F("Temperature is: "));
  double temp = sensors.getTempCByIndex(0);
  Serial.println(temp);

  sprintf(pageAdd, "/firebaseTemp.php?arduino_data=%s", ftoa(tempString, temp, 2));
  //Serial.println(pageAdd);
  if(!getPage(serverName, serverPort, pageAdd)) Serial.print(F("Fail "));
  else Serial.print(F("Pass "));
  totalCount++;
  Serial.println(totalCount, DEC);
  
  delay(1000);
  
  /**
  *
  Humidity sensor
  *
  **/
  sensorValue = analogRead(sensorPin); 
  Serial.print(F("Moist sensor: "));
  Serial.println(sensorValue);
  Serial.println(F("\n**********************************\n"));
 
  if(sensorValue > limit) {   // plant is thirsty
    digitalWrite(LED, HIGH); 
  }
  else {
    digitalWrite(LED, LOW);
  }

  sprintf(pageAdd, "/firebaseMoist.php?arduino_data=%d", sensorValue);
  //Serial.println(pageAdd);
  if(!getPage(serverName, serverPort, pageAdd)) Serial.print(F("Fail "));
  else Serial.print(F("Pass "));
  totalCount++;
  Serial.println(totalCount, DEC);
 
  delay(10000); 
}

char *ftoa(char *a, double f, int precision) {
  long p[] = {0,10,100,1000,10000,100000,1000000,10000000,100000000};  
  char *ret = a;
  long heiltal = (long)f;
  itoa(heiltal, a, 10);
  while (*a != '\0') a++;
  *a++ = '.';
  long desimal = abs((long)((f - heiltal) * p[precision]));
  itoa(desimal, a, 10);
  return ret;
}

byte getPage(char *ipBuf, int thisPort, char *page) {
  int inChar;
  char outBuf[128];
 
  Serial.print(F("connecting..."));
 
  if(client.connect(ipBuf, thisPort)) {
    Serial.println(F("connected"));
 
    sprintf(outBuf, "GET %s HTTP/1.1", page);
    client.println(outBuf);
    sprintf(outBuf, "Host: %s", serverName);
    client.println(outBuf);
    client.println(F("Connection: close\r\n"));
  } 
  else {
    Serial.println(F("failed"));
    return 0;
  }

  // connectLoop controls the hardware fail timeout
  int connectLoop = 0;
 
  while(client.connected()) {
    while(client.available()) {
      inChar = client.read();
      Serial.write(inChar);
      // set connectLoop to zero if a packet arrives
      connectLoop = 0;
    }
 
    connectLoop++;
 
    // if more than 10000 milliseconds since the last packet
    if(connectLoop > 10000) {
      // then close the connection from this end.
      Serial.println();
      Serial.println(F("Timeout"));
      client.stop();
    }
    // this is a delay for the connectLoop timing
    delay(1);
  }
 
  Serial.println();
 
  Serial.println(F("disconnecting."));
  // close client end
  client.stop();
 
  return 1;
}

void printMacAddress() {

  // the MAC address of the Wifi shield

  byte mac[6];

  // print MAC address:

  WiFi.macAddress(mac);

  Serial.print("MAC: ");

  Serial.print(mac[5],HEX);

  Serial.print(":");

  Serial.print(mac[4],HEX);

  Serial.print(":");

  Serial.print(mac[3],HEX);

  Serial.print(":");

  Serial.print(mac[2],HEX);

  Serial.print(":");

  Serial.print(mac[1],HEX);

  Serial.print(":");

  Serial.println(mac[0],HEX);
}
