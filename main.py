import pyrebase

config = {
	"apiKey": "AIzaSyCkrviVAVRis0KfHyHq64kuMCKj-2rOY6M",
	"authDomain": "watering-from-twitter.firebaseapp.com",
	"databaseURL": "https://watering-from-twitter-default-rtdb.europe-west1.firebasedatabase.app",
	"storageBucket": "watering-from-twitter.appspot.com",
	"serviceAccount": "./watering-from-twitter-firebase-adminsdk-ox1ft-2158d5aed1.json"
}


firebase = pyrebase.initialize_app(config)
db = firebase.database()

plants = db.get()
print(plants.val())
