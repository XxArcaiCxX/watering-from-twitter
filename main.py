import pyrebase
import tweepy

def main():

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

	consumer_key = "nnqfoFm92SsTzzDmqy2mtAFHI"
	consumer_secret = "FKnrbqSSQKrd8ykFMN1jQeVwEQLBRJvTiTVgK1m11pTvNZt0Wu"
	access_token = "1391426418886778882-JYMM6LLiY35ProkAf8VeO5aNAwvyY1"
	access_token_secret = "MsUbAjlDy8YDcCohoKCNEfkE661wMhnOuKPvj2dzdBfeH"


	firebase = pyrebase.initialize_app(firebaseConfig)
	db = firebase.database()

	plants = db.get()
	print(plants.val())

	auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
	auth.set_access_token(access_token, access_token_secret)

	api = tweepy.API(auth)
	api.update_status(str(plants.val()))

if __name__ == "__main__":
	main()
