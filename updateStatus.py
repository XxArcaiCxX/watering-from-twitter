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


def deleteLastTweets(api, number):
	print(number)
	for tweet in api.home_timeline(number):
		api.destroy_status(tweet.id)

def main():

	firebase = pyrebase.initialize_app(firebaseConfig)
	db = firebase.database()

	plants = db.child("Plants").get()

	auth = tweepy.OAuthHandler(twitterConfig.get("consumer_key"), twitterConfig.get("consumer_secret"))
	auth.set_access_token(twitterConfig.get("access_token"), twitterConfig.get("access_token_secret"))

	api = tweepy.API(auth)
	#deleteLastTweets(api, 1)

	tweet = ""
	for i in range(2):
		tweet += "Plant_" + str(i+1) + ": " + str(plants.val().get("Plant_" + str(i+1))) + "\n"

	api.update_status(tweet)

if __name__ == "__main__":
	main()
