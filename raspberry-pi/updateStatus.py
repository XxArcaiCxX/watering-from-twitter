from crontab import CronTab
import pyrebase
import tweepy

firebaseConfig = {
	"apiKey": "AIzaSyCkrviVAVRis0KfHyHq64kuMCKj-2rOY6M",
	"authDomain": "watering-from-twitter.firebaseapp.com",
	"databaseURL": "https://watering-from-twitter-default-rtdb.europe-west1.firebasedatabase.app",
	"storageBucket": "watering-from-twitter.appspot.com",
	"serviceAccount": "./watering-from-twitter-firebase-adminsdk-ox1ft-2158d5aed1.json"
}

twitterConfig = {
	"consumer_key": "nnqfoFm92SsTzzDmqy2mtAFHI",
	"consumer_secret": "FKnrbqSSQKrd8ykFMN1jQeVwEQLBRJvTiTVgK1m11pTvNZt0Wu",
	"access_token": "1391426418886778882-JYMM6LLiY35ProkAf8VeO5aNAwvyY1",
	"access_token_secret": "MsUbAjlDy8YDcCohoKCNEfkE661wMhnOuKPvj2dzdBfeH"
}

firebase = pyrebase.initialize_app(firebaseConfig)
db = firebase.database()

firstStream = True

def deleteLastTweets(api, number):
	print(number)
	for tweet in api.home_timeline(number):
		api.destroy_status(tweet.id)

def stats_stream_handler(message):
	#print(message["event"])
	print(message["path"])
	#print(message["data"])
	global firstStream

	if firstStream == True:
		firstStream = False
	else:
		path = message["path"].split('/')
		plantId = path[1]
		statId = path[2]
		value = float(message["data"])

		print(plantId + " says:")

		if statId == "Humidity": # Humidity is the only stat with no upper limit
			thresholdMin = db.child("Thresholds").child(plantId).child(statId).child("min").get()
			if value < float(thresholdMin.val()):
				print("EMERGENCY! Humidity got lower than threshold min at " + str(value))
			else:
				print("Just a periodic update, " + statId + " is now at " + str(value))
		else:
			thresholdMin = db.child("Thresholds").child(plantId).child(statId).child("min").get()
			thresholdMax = db.child("Thresholds").child(plantId).child(statId).child("max").get()
			if value < float(thresholdMin.val()):
				print("EMERGENCY! " + statId + " got lower than threshold min at " + str(value))
			elif value > thresholdMax.val():
				print("EMERGENCY! " + statId + " got higher than threshold max at " + str(value))
			else:
				print("Just a periodic update, " + statId + " is now at " + str(value))

def main():

	print("Ready to stream")
	statsStream = db.child("Stats").stream(stats_stream_handler)

	#plants = db.child("Plants").get()


	#auth = tweepy.OAuthHandler(twitterConfig.get("consumer_key"), twitterConfig.get("consumer_secret"))
	#auth.set_access_token(twitterConfig.get("access_token"), twitterConfig.get("access_token_secret"))

	#api = tweepy.API(auth)
	#deleteLastTweets(api, 1)

	#tweet = ""
	#for i in range(2):
		#tweet += "Plant_" + str(i+1) + ": " + str(plants.val().get("Plant_" + str(i+1))) + "\n"

	#api.update_status(tweet)

if __name__ == "__main__":
	main()
