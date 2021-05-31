<?php
require_once 'firebaseLib.php';
// --- This is your Firebase URL
$url = 'https://watering-from-twitter-default-rtdb.europe-west1.firebasedatabase.app/';
// --- Use your token from Firebase here
$token = 'sTCZTZKC1fwVBAM3GQ6e4UsAwAKg4BGtUTOl4MW3';
// --- Here is your parameter from the http GET
$arduino_data_1 = $_GET['arg1'];
$arduino_data_2 = $_GET['arg2'];
$arduino_data_3 = $_GET['arg3'];
// --- $arduino_data_post = $_POST['name'];
// --- Set up your Firebase url structure here
$firebasePath = '/Stats/Plant_2';
/// --- Making calls
$fb = new fireBase($url, $token);
$arr = array(
	'Humidity' => intval($arduino_data_1),
	'Light' => intval($arduino_data_2),
	'Temperature' => floatval($arduino_data_3),
);
$response = $fb->update($firebasePath, $arr);
//$response = $fb->push($firebasePath, $arr);
sleep(2);
