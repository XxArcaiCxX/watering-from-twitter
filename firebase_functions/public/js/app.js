const actLight = document.getElementById("actLight");
actLight.addEventListener('click', () => {
    var post = firebase.functions().httpsCallable('toggleActuator');
    post({ actuator: 'light' }).then(result => {
        console.log(result.data);
    });
})

const actWater = document.getElementById("actWater");
actWater.addEventListener('click', () => {
    var post = firebase.functions().httpsCallable('toggleActuator');
    post({ actuator: 'water' }).then(result => {
        console.log(result.data);
    });
})

const thresHumidity = document.getElementById("thresHumidity");
thresHumidity.addEventListener('click', () => {
    var minHumidity = document.getElementById("minHumidity").value;
    console.log(minHumidity);
    var post = firebase.functions().httpsCallable('changeThreshold');
    post({ threshold: 'Humidity', minValue: minHumidity, maxValue: ""}).then(result => {
        console.log(result.data);
    })
})

const threshLight = document.getElementById("threshLight");
threshLight.addEventListener('click', () => {
    var minLight = document.getElementById("minLight").value;
    var post = firebase.functions().httpsCallable('changeThreshold');
    post({ threshold: 'Light', minValue: `${minLight}`, maxValue: ""}).then(result => {
        console.log(result.data);
    })
})

const threshTemp = document.getElementById("threshTemp");
threshTemp.addEventListener('click', () => {
    var minTemp = document.getElementById("minTemp").value;
    var maxTemp = document.getElementById("maxTemp").value;
    var post = firebase.functions().httpsCallable('changeThreshold');
    post({ threshold: 'Temperature', minValue: `${minTemp}`, maxValue: `${maxTemp}`}).then(result => {
        console.log(result.data);
    })
})