#include "sketchTrabalho.h"

/*

  Wireless credentials

*/
const char ssid[] = "*****";                // network SSID (name)
const char pass[] = "*****";                // network password
int status = WL_IDLE_STATUS;                // the Wifi radio's status

WiFiClient client;
const IPAddress serverIP(*****);
const char serverIPstr[] = "*****";
const int serverPort = 80;
char pageAdd[64];
char tempString[] = "00.00";
int totalCount = 0;

const int LED = 8;
const int sensorPin = A1;                   // humidity
int limitMoist = 600;                       // threshold for humidity sensor
double limitTemp[] = {17.0, 24.0};          // threshold for temperature sensor
int limitLight = 400;                       // threshold for light sensor
int sensorValue;                            // humidity
String response = "";
String lastModLed = "";
String lastModWater = "";
StaticJsonDocument<256> doc;

unsigned long previousMillis = 0;
//const long interval = 3600000;
const long interval = 180000;

void setup() {
  Serial.begin(9600);
  sensors.begin();
  pinMode(LED, OUTPUT);

  // attempt to connect using WPA2 encryption:

  Serial.println(F("Attempting to connect to WPA network..."));

  status = WiFi.begin(ssid, pass);

  while (status != WL_CONNECTED) {
    delay(1000);
  }

  Serial.println(F("Connected to network"));
}

void loop() {
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    periodicUpdate();
  }

  // Check thresholds on firebase to updated with the most recent values
  update(NULL, NULL, "", R_MOIST);
  update(NULL, NULL, "", R_LIGHT);
  update(NULL, NULL, "", R_TEMP);
  update(NULL, NULL, "", R_ACT_LED);
  if (response != lastModLed && response != "") {
    //   Serial.println("LED TURNED ON!!!");
    lastModLed = response;
    activateLED();
  }
  update(NULL, NULL, "", R_ACT_WATER);
  if (response != lastModWater && response != "") {
    //   Serial.println("\n\nWATER TURNED ON!!!");
    lastModWater = response;
    activateWater();
  }

  // Emergency updates
  sensorValue = 1000 - analogRead(sensorPin);
  sensorValue < 0 ? sensorValue = 0 : sensorValue = sensorValue;
  if (sensorValue < limitMoist) {  // plant is thirsty
    digitalWrite(LED, HIGH);
    update(sensorValue, NULL, "", MOIST);
    Serial.println(F("**PLANT 2** is thirsty\nTurning on actuator..."));
    activateWater();
  }
  else digitalWrite(LED, LOW);

  int value = analogRead(A0);
  if (value < limitLight) {
    update(value, NULL, "", LIGHT);
    Serial.println(F("**PLANT 2** has little light\nTurning on actuator..."));
    activateLED();
  }

  sensors.requestTemperatures();
  double temp = sensors.getTempCByIndex(0);
  if (temp < limitTemp[0]) {
    update(NULL, temp, "", TEMP);
    Serial.println(F("**PLANT 2** is cold\nTurning on actuator..."));
    activateLED();
  }
  else if (temp > limitTemp[1]) {
    update(NULL, temp, "", TEMP);
    Serial.println(F("**PLANT 2** is hot\nTurning on actuator..."));
    activateLED();
  }
}

void periodicUpdate() {
  /**

    Humidity sensor

  **/
  Serial.println("\n\n******PERIODIC UPDATE******");
  sensorValue = 1000 - analogRead(sensorPin);
  sensorValue < 0 ? sensorValue = 0 : sensorValue = sensorValue;
  //  Serial.print(F("Moist sensor: "));
  //  Serial.println(sensorValue);

  if (sensorValue > limitMoist) {  // plant is thirsty
    digitalWrite(LED, HIGH);
  }
  else {
    digitalWrite(LED, LOW);
  }

  /**

    Light sensor

  **/
  int value = analogRead(A0);
  //  Serial.print(F("Light sensor: "));
  //  Serial.println(value);

  /**

    Temperature sensor

  **/
  // Requesting temperatures...
  sensors.requestTemperatures();

  double temp = sensors.getTempCByIndex(0);
  //  Serial.print(F("Temperature is: "));
  //  Serial.println(temp);

  updateAll(sensorValue, value, temp);
}

char *ftoa(char *a, double f, int precision) {
  long p[] = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
  char *ret = a;
  long heiltal = (long)f;
  itoa(heiltal, a, 10);
  while (*a != '\0') a++;
  *a++ = '.';
  long desimal = abs((long)((f - heiltal) * p[precision]));
  itoa(desimal, a, 10);
  return ret;
}

byte getPage(IPAddress ipBuf, int thisPort, char *page) {
  char inChar;
  char outBuf[128];
  response = "";

  delay(200);
  Serial.print("connecting...");
  if (client.connect(ipBuf, thisPort)) {
    Serial.println("connected");

    sprintf(outBuf, "GET %s HTTP/1.1", page);
    client.println(outBuf);
    sprintf(outBuf, "Host: %s", serverIPstr);
    client.println(outBuf);
    client.println("Connection: close");
    client.println();
  }
  else {
    Serial.println("failed");
    client.stop();
    return 0;
  }

  // connectLoop controls the hardware fail timeout
  int connectLoop = 0;

  while (client.connected()) {
    while (client.available()) {
      inChar = client.read();
      Serial.write(inChar);
      if (inChar == '\n') response = "";
      else response += inChar;
      // set connectLoop to zero if a packet arrives
      connectLoop = 0;
    }

    connectLoop++;

    // if more than 10000 milliseconds since the last packet
    if (connectLoop > 10000) {
      // then close the connection from this end.
      Serial.println();
      Serial.println("Timeout");
      client.stop();
    }
    // this is a delay for the connectLoop timing
    delay(1);
  }

  Serial.println();

  Serial.println("disconnecting.");
  // close client end
  client.stop();
  return 1;
}

void update(int v1, double v2, char* v3, TYPE t) {
  switch (t) {
    case LIGHT: {
        sprintf(pageAdd, "/firebaseLightP2.php?arduino_data=%d", v1);
        sendItToServer();
        break;
    } case TEMP: {
        sprintf(pageAdd, "/firebaseTempP2.php?arduino_data=%s", ftoa(tempString, v2, 2));
        sendItToServer();
        break;
    } case MOIST: {
        sprintf(pageAdd, "/firebaseMoistP2.php?arduino_data=%d", v1);
        sendItToServer();
        break;
    } case R_LIGHT: {
        sprintf(pageAdd, "/thresholdsLightP2.txt");
        sendItToServer();
        DeserializationError error = deserializeJson(doc, response);
        if (error) {
          Serial.print("deserializeJson() failed: ");
          Serial.println(error.c_str());
        }
        else limitLight = doc["minValue"];
        break;
    } case R_TEMP: {
        sprintf(pageAdd, "/thresholdsTempP2.txt");
        sendItToServer();
        DeserializationError error = deserializeJson(doc, response);
        if (error) {
          Serial.print("deserializeJson() failed: ");
          Serial.println(error.c_str());
        }
        else {
          limitTemp[0] = doc["minValue"];
          limitTemp[1] = doc["maxValue"];
        }
        break;
    } case R_MOIST: {
        sprintf(pageAdd, "/thresholdsMoistP2.txt");
        sendItToServer();
        DeserializationError error = deserializeJson(doc, response);
        if (error) {
          Serial.print("deserializeJson() failed: ");
          Serial.println(error.c_str());
        }
        else limitMoist = doc["minValue"];
        break;
    } case ACT_LED: {
        sprintf(pageAdd, "/firebaseActLedP2.php?arduino_data=%s", v3);
        sendItToServer();
        break;
    } case ACT_WATER: {
        sprintf(pageAdd, "/firebaseActWateringP2.php?arduino_data=%s", v3);
        sendItToServer();
        break;
    } case R_ACT_LED: {
        sprintf(pageAdd, "/actuatorLedP2.txt");
        sendItToServer();
        break;
    } case R_ACT_WATER: {
        sprintf(pageAdd, "/actuatorWaterP2.txt");
        sendItToServer();
        break;
      }
  }
}

void updateAll(int v1, int v2, double v3) {
  sprintf(pageAdd, "/firebaseAllP2.php?arg1=%d&arg2=%d&arg3=%s", v1, v2, ftoa(tempString, v3, 2));
  sendItToServer();
}

void sendItToServer() {
  if (!getPage(serverIP, serverPort, pageAdd)) Serial.print(F("Fail "));
  else Serial.print("Pass ");
  totalCount++;
  Serial.println(totalCount, DEC);
}

void activateLED() {
  update(NULL, NULL, "true", ACT_LED);
  delay(7000);                              // If built, actuator's code should replace this delay
  update(NULL, NULL, "false", ACT_LED);
}

void activateWater() {
  update(NULL, NULL, "true", ACT_WATER);
  delay(7000);                              // If built, actuator's code should replace this delay
  update(NULL, NULL, "false", ACT_WATER);
}
