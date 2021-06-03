import sys
import pyrebase
import tweepy

firebaseConfig = {
	"apiKey": "AIzaSyCkrviVAVRis0KfHyHq64kuMCKj-2rOY6M",
	"authDomain": "watering-from-twitter.firebaseapp.com",
	"databaseURL": "https://watering-from-twitter-default-rtdb.europe-west1.firebasedatabase.app",
	"storageBucket": "watering-from-twitter.appspot.com",
	"serviceAccount": "/home/pi/Project/raspberry-pi/watering-from-twitter-firebase-adminsdk-ox1ft-2158d5aed1.json"
}

twitterConfig = {
	"consumer_key": "nnqfoFm92SsTzzDmqy2mtAFHI",
	"consumer_secret": "FKnrbqSSQKrd8ykFMN1jQeVwEQLBRJvTiTVgK1m11pTvNZt0Wu",
	"access_token": "1391426418886778882-JYMM6LLiY35ProkAf8VeO5aNAwvyY1",
	"access_token_secret": "MsUbAjlDy8YDcCohoKCNEfkE661wMhnOuKPvj2dzdBfeH"
}

firebase = pyrebase.initialize_app(firebaseConfig)
db = firebase.database()

auth = tweepy.OAuthHandler(twitterConfig.get("consumer_key"), twitterConfig.get("consumer_secret"))
auth.set_access_token(twitterConfig.get("access_token"), twitterConfig.get("access_token_secret"))
api = tweepy.API(auth)

firstStream = True

def periodic_update():
	global db
	global api
	file = open("/home/pi/Project/raspberry-pi/logs.txt", "a")

	tweet = 'Hey guys, an update on the plants:'
	for i in range(1,3):
		plantId = "Plant_"+ str(i)
		humidity = db.child("Stats").child(plantId).child("Humidity").get().val()
		temperature = db.child("Stats").child(plantId).child("Temperature").get().val()
		light = db.child("Stats").child(plantId).child("Light").get().val()
		tweet += '\n' + plantId + "'s humidity is at " + str(humidity) + '%, its temperature is at ' + str(temperature) + 'ºC and its light is at ' + str(light) + '%.' 

	file.write(tweet + '\n\n')
	file.close()

	print(tweet)
	api.update_status(tweet)

def stats_stream_handler(message):
	global firstStream
	global api

	if firstStream == True:
		firstStream = False
	else:
		path = message["path"].split('/')
		plantId = path[1]
		statId = path[2]
		value = float(message["data"])

		print(plantId + " says:")

		if statId == "Temperature":

			thresholdMin = db.child("Thresholds").child(plantId).child("Temperature").child("min").get()
			thresholdMax = db.child("Thresholds").child(plantId).child("Temperature").child("max").get()
			if value < float(thresholdMin.val()):
				print("EMERGENCY! " + statId + " got lower than threshold min at " + str(value))
				api.update_status(plantId + " is getting the chills at " + str(value) + "ºC... We're cranking up the temp.")
			elif value > float(thresholdMax.val()):
				print("EMERGENCY! " + statId + " got higher than threshold max at " + str(value))
				api.update_status("At " + str(value) + "ºC it's too hot for " + plantId + " to handle, we're bringing down the thermostat.")
			else:
				print("Just a periodic update, " + statId + " is now at " + str(value))

		elif statId == "Humidity":

			thresholdMin = db.child("Thresholds").child(plantId).child("Humidity").child("min").get()
			if value < float(thresholdMin.val()):
				print("EMERGENCY! Humidity got lower than threshold min at " + str(value))
				api.update_status("At " + str((value)/10) + chr(37) + " humidity " + plantId + " is getting dry mouth... We're getting her a drink!")
			else:
				print("Just a periodic update, " + statId + " is now at " + str(value))

		else:
			thresholdMin = db.child("Thresholds").child(plantId).child("Light").child("min").get()
			if value < float(thresholdMin.val()):
				print("EMERGENCY! Light got lower than threshold min at " + str(value))
				api.update_status(plantId + " is feeling afraid of the dark at " + str(value) + chr(37) + " light strength, we're turning on a few lights.")
			else:
				print("Just a periodic update, " + statId + " is now at " + str((value)/10))


def main():

	if format(len(sys.argv)) == '2':
		if sys.argv[1] == '1':
			periodic_update()
		elif sys.argv[1] == '2':
			print("Ready to stream")
			db.child("Stats").stream(stats_stream_handler)

if __name__ == "__main__":
	main()
