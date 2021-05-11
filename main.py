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
		"consumer_key": "",
		"consumer_secret": "",
		"access_token": "",
		"access_token_secret": ""
	}


	firebase = pyrebase.initialize_app(firebaseConfig)
	db = firebase.database()

	plants = db.get()
	print(plants.val())

if __name__ == "__main__":
	main()
