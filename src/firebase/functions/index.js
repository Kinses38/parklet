'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const dateFormat = require('dateformat');
const moment = require('moment-timezone');
admin.initializeApp();

//https://firebase.google.com/docs/functions/database-events

/**
 * Notifies home owner of new booking being placed at their property
 */
exports.newBookingNotification = functions.region('europe-west2').database.ref('bookings/{bookingID}')
    .onCreate(async (snapshot, context) => {
        try {
            const booking = snapshot.val();
            console.log("Booking captured: ", context.params, booking);
            console.log("Owner :", booking.ownerUID);
            const ownerUID = booking.ownerUID;
            const ownerToken = (await admin.database().ref(`users/${ownerUID}/fcmToken`).once('value')).val();
            console.log("Owner :", ownerToken);

            let timestamps = booking.bookingDates;
            let dates = Array.from(timestamps, date => dateFormat(new Date(date), "ddd d mmm"))
            console.log("Converted dates: ", dates);

            try {
                const message = {
                    notification: {
                        title: 'New booking',
                        body: `${booking.renterName} just booked your driveway at ${booking.propertyAddress.split(",", 1)} for\n${dates.join(',\n')}`
                    }
                };
                const result = await admin.messaging().sendToDevice(ownerToken, message);

            } catch (e) {
                console.log(e);
            }
        } catch (e) {
            console.log(e);
        }
    });

exports.checkInStatusChanged = functions.region("europe-west2").database.ref('bookings/{bookingID}/renterAtProperty')
    .onUpdate(async (change, context) => {
        try {
            const oldVal = change.before.val();
            const newVal = change.after.val();
            const bookingID = context.params.bookingID;
            /*
               https://stackoverflow.com/questions/9529368/how-do-i-set-my-default-timezone-to-ireland-dublin-in-javascript
               Hard to get time for dst
            */

            const today = new Date();
            let momentTime = moment(today);
            let convertedTime = momentTime.tz('Europe/Dublin').format("h:mm a");

            console.log("Converted moment:" + convertedTime);
            console.log("Booking captured: ", bookingID);
            console.log("Before: ", oldVal);
            console.log("After: ", newVal);
            let booking = (await admin.database().ref(`bookings/${bookingID}`).once('value')).val();
            let ownerUID = booking.ownerUID;

            let ownerToken = (await admin.database().ref(`users/${ownerUID}/fcmToken`).once('value')).val();
            console.log("Booking: ", booking);
            console.log("OwnerUID: ", ownerUID);
            console.log("Owner FCM: ", ownerToken);

            let payloadTitle;
            let payloadBody;
            /*
               If old was true and changed to false then checkout.
               To avoid any potential errors with the creation of the booking.
               onUpdate() should take care of it but to be sure.
            */
            if (oldVal === true) {
                payloadTitle = 'Check-out';
                payloadBody = `${booking.renterName} just checked out of ${booking.propertyAddress.split(",", 1)} at ${convertedTime}. \nVehicle reg is ${booking.renterVehicleReg}!`;
            } else {
                payloadTitle = 'Check-in';
                payloadBody = `${booking.renterName} just checked into ${booking.propertyAddress.split(",", 1)} at ${convertedTime}. \nVehicle reg is ${booking.renterVehicleReg}!`;
            }

            try {
                const message = {
                    notification: {
                        title: payloadTitle,
                        body: payloadBody
                    }
                };
                const result = await admin.messaging().sendToDevice(ownerToken, message);
            } catch (e) {
                console.log(e)
            }
        } catch (e) {
            console.log(e);
        }
    });

exports.ownerBookingCancellation = functions.region("europe-west2").database.ref('bookings/{bookingID}/ownerCancelled')
    .onUpdate(async (change, context) => {
        const newVal = change.after.val();
        const bookingID = context.params.bookingID;

        let booking = (await admin.database().ref(`bookings/${bookingID}`).once('value')).val();
        let renterUID = booking.renterUID;

        let renterToken = (await admin.database().ref(`users/${renterUID}/fcmToken`).once('value')).val();

        let timestamps = booking.bookingDates;
        let dates = Array.from(timestamps, date => dateFormat(new Date(date), "ddd d mmm"))
        console.log("Converted dates: ", dates);

        let payloadBody;
        if (newVal === true) {
            payloadBody = `Hi ${booking.renterName.split(" ", 1)}, unfortunately your booking for `
            + `${booking.propertyAddress.split(",", 1)} on:\n${dates.join(',\n')}`
            + `\nwas cancelled by the owner. Your refund is on the way. Please have a look for alternative driveways!`;

            try {
                const message = {
                    notification: {
                        title: "Booking cancellation",
                        body: payloadBody
                    }
                };
                const result = await admin.messaging().sendToDevice(renterToken, message);
            } catch (e) {
                console.log(e);
            }
        }
    });

exports.renterBookingCancellation = functions.region("europe-west2").database.ref('bookings/{bookingID}/renterCancelled')
    .onUpdate( async (change, context) => {
        const newVal = change.after.val();
        const bookingID = context.params.bookingID;

        let booking = (await admin.database().ref(`bookings/${bookingID}`).once('value')).val();
        let ownerUID = booking.ownerUID;

        let ownerToken = (await admin.database().ref(`users/${ownerUID}/fcmToken`).once('value')).val();

        let timestamps = booking.bookingDates;
        let dates = Array.from(timestamps, date => dateFormat(new Date(date), "ddd d mmm"))
        console.log("Converted dates: ", dates);

        let payloadBody;
        if (newVal === true) {
            payloadBody = `Hi ${booking.ownerName.split(" ", 1)}, unfortunately your booking for `
                + `${booking.propertyAddress.split(",", 1)} on:\n${dates.join(',\n')}`
                + `\nwas cancelled by the renter. Your property is now re-listed as available for these days`;

            try {
                const message = {
                    notification: {
                        title: "Booking cancellation",
                        body: payloadBody
                    }
                };
                const result = await admin.messaging().sendToDevice(ownerToken, message);
            } catch (e) {
                console.log(e);
            }
        }
    });
