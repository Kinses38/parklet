const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.region('europe-west2').https.onRequest((request, response) => {
    response.send("Hello from Firebase!");
});
