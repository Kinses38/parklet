'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const dateFormat = require('dateformat');
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

            try{
                    const message = {
                            notification: {
                                    title: 'New booking',
                                    body: `${booking.renterName} just booked your driveway at ${booking.propertyAddress.split(",", 1)} for\n${dates.join(', ')}`
                            }
                    };
                    const result = await admin.messaging().sendToDevice(ownerToken, message);

            }catch (e) {
                    console.log(e);
            }
        }catch (e) {
                console.log(e);
        }
    });

