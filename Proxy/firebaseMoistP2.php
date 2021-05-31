<?php
require_once 'firebaseLib.php';
// --- This is your Firebase URL
$url = 'https://watering-from-twitter-default-rtdb.europe-west1.firebasedatabase.app/';
// --- Use your token from Firebase here
$token = 'sTCZTZKC1fwVBAM3GQ6e4UsAwAKg4BGtUTOl4MW3';
// --- Here is your parameter from the http GET
$arduino_data = $_GET['arduino_data'];
// --- $arduino_data_post = $_POST['name'];
// --- Set up your Firebase url structure here
$firebasePath = '/Stats/Plant_2';
/// --- Making calls
$fb = new fireBase($url, $token);
$arr = array('Humidity' => intval($arduino_data));
$response = $fb->update($firebasePath, $arr);
//$response = $fb->push($firebasePath, $arr);
sleep(2);
