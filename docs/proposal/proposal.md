# School of Computing &mdash; Year 4 Project Proposal Form



## SECTION A

|                     |                   |
|---------------------|-------------------|
|Project Title:       | PARKLET           |
|Student 1 Name:      | Shaun Kinsella    |
|Student 1 ID:        | 14740175          |
|Student 2 Name:      | Mike Purcell      |
|Student 2 ID:        | 15446908          |
|Project Supervisor:  | Stephen Blott     |



## SECTION B

### Introduction
> "Airbnb for parking"

The aim of our project will be to develop an android app that will allow 
homeowners to easily rent out their driveways as parking spaces to commuters 
with minimal effort. And likewise enable commuters to easily view and book 
parking spots with competitive rates.

### Outline

The project will be an android based app which will utilize firebase for our back-end as well as real time synchronization to our database.
We plan on using the google maps api for locating available parking for the customer. We also hope to implement a nfc check in/check out system
for a clean way for users to notify each other on their arrival/departure. This nfc system will also allow the home owner to know whether the
customer has left on time or not.

### Background

> Where did the ideas come from?

We were searching for ideas that could fit the AirBnB model of owning and renting a good or service, due to living in Dublin 
we are well aware of the problems that commuting causes. With limited parking available in large areas of Dublin and the lack
of use of most parking spots such as drive-ways during work hours, we thought this would be a great solution while also being
a useful passive income for the parking spot owners.

### Achievements

> What functions will the project provide? Who will the users be?

**Functionality:**

The app will allow homeowners to list their driveway property as available to rent. 
They will be able to set their own rates on a daily, weekly or even monthly basis and can choose to 
give discounts for booking multiple days. The homeowner can choose to allow overnight parking or a 
time frame such as parking between 8am to 6pm regardless of the amount of days booked.

From the renters perspective the app will allow them to choose an area they wish to park nearby 
and show them all available spaces to rent that fit their requirements. They will be able to view
availability of the driveways to rent for their preferred dates and compare the rates for each property.

We will be utilising NFC to provide a hassle free way to check-in for both renters and homeowners.
The homeowner can optionally register a property nfc tag that the renter can use in conjunction with the app
to tap and alert the owner that they have arrived. Likewise when the renter is leaving they will tap and
the homeowner will be notified through the app's push notification that their driveway is now free.

It is planned to implement pricing advice for both homeowner and renter, that allows them to view previous
rates for the past 30 days for the local area to make sure that they are setting competitive rates 
and getting a good deal respectively.

**Users:**
- Daily commuters in congested areas with limited parking available, offering 
  them an alternative parking solution. Whether its a cheaper alternative or to 
  allow closer access than normal car parking services offer.
- Holiday makers looking for cheaper alternatives to airport parking in the 
  case of weekly or monthly parking rental.
- Events like Slane or the Skerries 100 put a massive strain on local car 
  parking services. Festival goers could book a car parking space outside of the
  festival grounds to avoid the panic of trying to secure a spot and the 
  congestion that follows trying to leave the venue car park.
- Homeowners with available car parking space on their driveway. They might be 
  homeowners who do not drive, commute elsewhere during the day or just have the
  extra space to facilitate more cars. This app will give them the ability to 
  generate extra income with minimal effort.


### Justification

> Why/when/where/how will it be useful?

In towns or cities where there is a large amount of daily commuters such as Dublin, parking is at a 
premium and quite limited in availability. This app will help alleviate the pressure of finding affordable parking and generate extra revenue for homeowners.
A census taken in [2016](https://www.cso.ie/en/releasesandpublications/ep/p-cp6ci/p6cii/p6www/) shows that 130,447 people 
commute daily to Dublin from around the country. Of each of the locations contained in this census, 
the lowest percentage of people travelling by car was 60%, roughly 78,000 cars assuming no carpooling is taking place.

There are approximately 20,000 private ticket or "pay and display"
spots available in the city centre often at quite high daily or hourly rates. 
Our project hopes to combat the uncertainty of getting a 
parking space and that homeowners can compete with the rates of the private car parks.

As mentioned before, the apps justification can be extended to large events such as Slane to open up more available parking just outside
the area of the event.

### Programming language(s)

- Java

### Programming tools / Tech stack

- [Android Studio](https://developer.android.com/studio): Our main IDE for this project.
- [Firebase](https://firebase.google.com/products/realtime-database/): Realtime database allowing us to query and sync information in 
  real time.
- [Google maps api](https://cloud.google.com/maps-platform/): Leveraging google maps api will allow us to implement the geographical aspect of our app.
- [NFC technology](https://developer.android.com/guide/topics/connectivity/nfc): To allow us to implement the hassle free check-in system.
- [Android-Geofire](https://github.com/firebase/geofire-android): library that allows 
  real time queries of services based on their geographical location.
- [Espresso](https://developer.android.com/training/testing/espresso): Testing framework allowing us to run automated ui tests.
- [Mockito](https://site.mockito.org) Mocking framework allowing us to write unit tests with mocked dependencies.
- [AWS Device Farm](https://aws.amazon.com/device-farm/) Allows us to test our application on multiple devices and versions of Android in parallel.


### Hardware

- Android phone capable of running minimum Android Oreo (8.0) with nfc reader.
- Compatible NFC tags

### Learning Challenges

**Common learning challenges:**
- Android development
- Android studio
- Firebase
- Implementing NFC based technology

**Shaun's learning challenges:**
- Previous projects & internship experience utilized functional paradigm 
  languages such as Clojure and Clojurescript. I've never undertaken an Object 
  Oriented project of this size. 

**Mike's learning challenges**
- Using google maps api

### Breakdown of work

> Clearly identify who will undertake which parts of the project.
>
> It must be clear from the explanation of this breakdown of work both that each student is responsible for
> separate, clearly-defined tasks, and that those responsibilities substantially cover all of the work required
> for the project.

This breakdown of work is based on our current understanding of the requirements of this project.
As part of our workflow we will also be utilizing pair programming, auditing each others commits and taking
an equal share in the documentation and architecture design of this project.

Each member will be responsible for the design and implementation of various tests for each 
component that they are responsible for to ensure proper test coverage.

#### Student 1: Shaun

- I will be working on the base android ui template which will involve the setting up of the project and 
  developing the skeleton of the application which will facilitate the adding of the below features. 

- I will be working on the registration and login features, this will include the ui elements
  that will allow a user to swap between listing a property or renting an available property.

- I will be working on the google map integration including the displaying of 
  available parking spots to rent and interactions with the corresponding map pins.

- I will be implementing the rental statistics for both customers and homeowners
  to better understand the demand in a given area.


#### Student 2: Mike

- Design of firebase data structures, how the documents will be stored, such as 
  bookings, user information, and property locations. 

- Implementation of NFC enabled check-in functionality. Writing the nfc tags, associating the tag with our app
  and the push notifications to the homeowner when a customer checks in or out of the property.

- The implementation of a star based review system for both homeowners and customers.

- I will be working on the implementation of the booking system including notifying the owner of new bookings. 