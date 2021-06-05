const http = require('http')
const path = require('path')
const url = require('url')
const express = require('express')
const app = express()
const port = 3000
const bodyParser = require('body-parser');
const fs = require('fs');

var first = true

if(first) {
	fs.writeFile('/var/www/html/actuatorLedP1.txt', '', function() {})
	fs.writeFile('/var/www/html/actuatorLedP2.txt', '', function() {})
	fs.writeFile('/var/www/html/actuatorWaterP1.txt', '', function() {})
	fs.writeFile('/var/www/html/actuatorWaterP2.txt', '', function() {})
	first = false
}

app.use(bodyParser.json());

app.post('/', (req, res) => {
	console.log('Got body:', req.body);
	res.sendStatus(200);
	if(req.body.plant == "Plant_1") {
		if(req.body.hasOwnProperty('threshold')) {
			if(req.body.threshold == "Humidity") {
				req.body.minValue = parseInt(req.body.minValue, 10);
				fs.writeFile('/var/www/html/thresholdsMoistP1.txt', JSON.stringify(req.body), (err) => {
					if(err) throw err;
				});
			}else if(req.body.threshold == "Light") {
				req.body.minValue = parseInt(req.body.minValue, 10);
				fs.writeFile('/var/www/html/thresholdsLightP1.txt', JSON.stringify(req.body), (err) => {
					if(err) throw err;
				});
			}else {
				req.body.minValue = parseFloat(req.body.minValue);
				req.body.maxValue = parseFloat(req.body.maxValue);
				fs.writeFile('/var/www/html/thresholdsTempP1.txt', JSON.stringify(req.body), (err) => {
					if(err) throw err;
				});
			}
		}
		else {
			if(req.body.actuator == "light") {
				fs.writeFile('/var/www/html/actuatorLedP1.txt', JSON.stringify(req.body) + "\n", (err) => {
					if(err) throw err;
				});
				fs.stat('/var/www/html/actuatorLedP1.txt', (err, stats) => {
					if(err) throw err;
					fs.appendFile('/var/www/html/actuatorLedP1.txt', stats.mtime, function(err) {
						if(err) throw err;
					});
				});
			}
			else {
				fs.writeFile('/var/www/html/actuatorWaterP1.txt', JSON.stringify(req.body) + "\n", (err) => {
					if(err) throw err;
				});
				fs.stat('/var/www/html/actuatorWaterP1.txt', (err, stats) => {
					if(err) throw err;
					fs.appendFile('/var/www/html/actuatorWaterP1.txt', stats.mtime, function(err) {
						if(err) throw err;
					});
				});
			}
		}
	}else {
		if(req.body.hasOwnProperty('threshold')) {
			if(req.body.threshold == "Humidity") {
				req.body.minValue = parseInt(req.body.minValue, 10);
				fs.writeFile('/var/www/html/thresholdsMoistP2.txt', JSON.stringify(req.body), (err) => {
					if(err) throw err;
				});
			}else if(req.body.threshold == "Light") {
				req.body.minValue = parseInt(req.body.minValue, 10);
				fs.writeFile('/var/www/html/thresholdsLightP2.txt', JSON.stringify(req.body), (err) => {
					if(err) throw err;
				});
			}else {
				req.body.minValue = parseFloat(req.body.minValue);
				req.body.maxValue = parseFloat(req.body.maxValue);
				fs.writeFile('/var/www/html/thresholdsTempP2.txt', JSON.stringify(req.body), (err) => {
					if(err) throw err;
				});
			}
		}
		else {
			if(req.body.actuator == "light") {
				fs.writeFile('/var/www/html/actuatorLedP2.txt', JSON.stringify(req.body) + "\n", (err) => {
					if(err) throw err;
				});
				fs.stat('/var/www/html/actuatorLedP2.txt', (err, stats) => {
					if(err) throw err;
					fs.appendFile('/var/www/html/actuatorLedP2.txt', stats.mtime, function(err) {
						if(err) throw err;
					});
				});
			}
			else {
				fs.writeFile('/var/www/html/actuatorWaterP2.txt', JSON.stringify(req.body) + "\n", (err) => {
					if(err) throw err;
				});
				fs.stat('/var/www/html/actuatorWaterP2.txt', (err, stats) => {
					if(err) throw err;
					fs.appendFile('/var/www/html/actuatorWaterP2.txt', stats.mtime, function(err) {
						if(err) throw err;
					});
				});
			}
		}
	}
});

app.listen(port, () => {
	console.log("Example app listening at http://localhost:" + `${port}`)
})
