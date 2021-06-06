#include <WiFi.h>
#include <DallasTemperature.h>
#include <OneWire.h>
#include <ArduinoJson.h>

#define ONE_WIRE_BUS 2

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

typedef enum {
  LIGHT,
  TEMP,
  MOIST,
  R_LIGHT,
  R_TEMP,
  R_MOIST,
  ACT_LED,
  ACT_WATER,
  R_ACT_LED,
  R_ACT_WATER,
}TYPE;
