const functions = require("firebase-functions");
const axios = require("axios");

const runtimeOpts = {
  memory: "128MB",
};

// function to toggle the Actuators
exports.toggleActuator = functions
    .region("europe-west2")
    .runWith(runtimeOpts)
    .https.onCall((dat, context) => {
      const act = dat.actuator;

      axios.post("https://ptsv2.com/t/act/post", {
        actuator: `${act}`,
      })
          .then(function(response) {
            console.log(response);
          })
          .catch(function(error) {
            console.error(error);
          });

      return `${act}`;
    });

exports.changeThreshold = functions
    .region("europe-west2")
    .runWith(runtimeOpts)
    .https.onCall((dat, context) => {
      const thresh = dat.threshold;
      const minValue = dat.minValue;
      const maxValue = dat.maxValue;

      axios.post("https://ptsv2.com/t/thresh/post", {
        threshold: `${thresh}`,
        minValue: `${minValue}`,
        maxValue: `${maxValue}`,
      })
          .then(function(response) {
            console.log(response);
          })
          .catch(function(error) {
            console.error(error);
          });
    });
