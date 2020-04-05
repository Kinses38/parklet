'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const moment = require('moment-timezone');
admin.initializeApp();

//https://firebase.google.com/docs/functions/database-events

/**
 * Notifies home owner of new booking being placed at their property
 * @type {CloudFunction<DataSnapshot>}
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
            let dates = Array.from(timestamps, date => {
                let currentMoment = moment(date);
                return currentMoment.tz('Europe/Dublin').format("ddd Do MMM");
            });

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

/**
 * Notifies owner of renters arrival or departure from their property with timestamp
 * @type {CloudFunction<Change<DataSnapshot>>}
 */
exports.checkInStatusChanged = functions.region("europe-west2").database.ref('bookings/{bookingID}/renterAtProperty')
    .onUpdate(async (change, context) => {
        try {
            const oldVal = change.before.val();
            const newVal = change.after.val();
            const bookingID = context.params.bookingID;

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

/**
 * Notifies renter of owner cancelling their booking.
 * @type {CloudFunction<Change<DataSnapshot>>}
 */
exports.ownerBookingCancellation = functions.region("europe-west2").database.ref('bookings/{bookingID}/ownerCancelled')
    .onUpdate(async (change, context) => {
        const newVal = change.after.val();
        const bookingID = context.params.bookingID;

        let booking = (await admin.database().ref(`bookings/${bookingID}`).once('value')).val();
        let renterUID = booking.renterUID;

        let renterToken = (await admin.database().ref(`users/${renterUID}/fcmToken`).once('value')).val();

        let timestamps = booking.bookingDates;
        let dates = Array.from(timestamps, date => {
            let currentMoment = moment(date);
            return currentMoment.tz('Europe/Dublin').format("ddd Do MMM");
        });

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

/**
 * Notifies owner that a renter has cancelled their booking.
 * @type {CloudFunction<Change<DataSnapshot>>}
 */
exports.renterBookingCancellation = functions.region("europe-west2").database.ref('bookings/{bookingID}/renterCancelled')
    .onUpdate(async (change, context) => {
        const newVal = change.after.val();
        const bookingID = context.params.bookingID;

        let booking = (await admin.database().ref(`bookings/${bookingID}`).once('value')).val();
        let ownerUID = booking.ownerUID;

        let ownerToken = (await admin.database().ref(`users/${ownerUID}/fcmToken`).once('value')).val();

        let timestamps = booking.bookingDates;
        let dates = Array.from(timestamps, date => {
            let currentMoment = moment(date);
            return currentMoment.tz('Europe/Dublin').format("ddd Do MMM");
        });

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


/**
 * When a user adds a new property this function is called to aggregate the prices in their area for the average.
 * By using the geohash of a property, we can truncate it from 10 characters to 5 giving a geographical grid of 4.9km squared.
 * We can then use these shortened geohashes as buckets to contain aggregated data for an properties falling into that area.
 * Calculating a running average price so it is available for when a renter queries for property.
 *
 * @type {CloudFunction<Change<DataSnapshot>>}
 */
exports.aggregatePropertyPrices = functions.region("europe-west2").database.ref('propertyLocations/{propertyKey}')
    .onWrite(async (change, context) => {
        //returns only the new property
        const newState = change.after.val();
        const oldState = change.before.val();
        const propertyUID = context.params.propertyKey;
        console.log("state", newState);
        console.log("Property", propertyUID);

        //If property was added
        if (newState !== null) {
            const geoHash = newState.g;
            //get geohash, reduce to 5 points for 4.9km x 4.9km (Some precision bits are square others are rectangles with a greater height)
            const geoBucket = geoHash.substring(0, 6);
            //get property price to store in bucket as when the user deletes the property we wont be able to retrieve it to update average.
            let propertyPrice = (await admin.database().ref(`properties/${propertyUID}/dailyRate`).once('value')).val();
            console.log("Property Price:", propertyPrice);


            //Create or update bucket using transaction to ensure atomic transaction.
            console.log("Geohash before: ", geoHash + " after: " + geoBucket);
            //recursive func here
            await recursiveAddToBucket(propertyUID, propertyPrice, geoBucket);

            //end
        } else if (oldState !== null) {
            // The property was just deleted
            console.log("Deleted: ", oldState);
            const geoHash = oldState.g;
            const geoBucket = geoHash.substring(0, 6);
            console.log("Geohash before: ", geoHash + " after: " + geoBucket);
            await recursiveRemoveFromBucket(propertyUID, geoBucket)
        }
    });


async function recursiveAddToBucket(propertyUID, propertyPrice, geoBucket) {
    if (geoBucket.length > 3) {
        await admin.database().ref(`geoPriceBucket/${geoBucket}`).transaction((currentBucket) => {
            if (currentBucket === null) {
                console.log("Bucket doesnt exist, creating");
                return {
                    average: propertyPrice,
                    count: 1,
                    [propertyUID]: propertyPrice
                };
            } else {
                // running average
                currentBucket.average = (currentBucket.count * currentBucket.average + propertyPrice) / (currentBucket.count + 1);
                currentBucket.count = (currentBucket.count + 1);
                currentBucket[propertyUID] = propertyPrice;
                console.log("Bucket updated", geoBucket + ': ' + currentBucket);
                return currentBucket;
            }
        });
        return recursiveAddToBucket(propertyUID, propertyPrice, geoBucket.substring(0, geoBucket.length - 1));
    } else {
        return 0;
    }

}

async function recursiveRemoveFromBucket(propertyUID, geoBucket) {
    if (geoBucket.length > 3) {
        await admin.database().ref(`geoPriceBucket/${geoBucket}`).transaction((currentBucket) => {
            if (currentBucket === null) {
                //edge case for currentBucket returning null. It will reattempt.
                return null;
            } else {
                if (currentBucket.count > 1) {
                    //propertyUID acts as a key to the given properties price within the bucket.
                    currentBucket.average = (currentBucket.count * currentBucket.average - currentBucket[propertyUID]) / (currentBucket.count - 1);
                    currentBucket.count = (currentBucket.count - 1);
                    currentBucket[propertyUID] = null;
                    console.log("Bucket updated", geoBucket + ': ' + currentBucket);
                    return currentBucket;
                } else {
                    //last property in a bucket so set to null and firebase will prune.
                    console.log("Last property, deleting bucket", geoBucket + ': ' + currentBucket);
                    return currentBucket = {};
                }
            }
        });
        return recursiveRemoveFromBucket(propertyUID, geoBucket.substring(0, geoBucket.length - 1));
    } else {
        return 0;
    }

}