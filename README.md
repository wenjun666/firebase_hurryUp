# firebase_hurryUp

Team Never Fail: Tianyang Zhong, Yehui Huang, Wenjun Shen, Linshan Jiang

# Introduction
The intuitive idea of this project is to design and develop an android social mobile application that can help people to become more time-aware. By developing such application, which our team named it HurryUp, we try to encourage good time management and change bad behaviors. In this paper, we will first discuss in detail for each functionality, how we implemented it by showing application screenshots and code fragments, and what challenge we had encountered and solved. In the second part, we will include future improvements of our application.

# Database Structure
The Database we use is Firebase, which is a NoSQL real-time database. 
The events structure includes event key with date, eventName, latitude, longitude and time.
The users structure includes user key with email, event an user involves, friend list, gender, location, name, phone, and score.

# Functionalities and respective implementations/challenges


Login and register

Implementation: 
Our app supports Google login, Facebook login, and firebase authentication login.
For Google login, we build a GoogleApiClient with access to the Google Sign-In API and the options specified by GoogleSignInOptions. For Facebook login, we instantiate a Facebook callback Manager and log in with the access token. Our app also supports signup with email through firebase.
Firebase provides us with a easy way to access current user authentication.
FirebaseAuth auth = FirebaseAmuth.getInstance();
We then could access user email or any login info using auth. And Every activity in this application that initiates FirebaseAuth could have access to user authentication.
 
Upcoming event

Implementation:
Under each user, it has a event child node, which contains all the event id that user
 Will participate. First, we need to make query to database by using the user id as the key(Line 79).  And then we set addListenerForSingleValueEvent listener. We retrieve all the event id, and save it into eventListView . 
 
Challenges:
The challenge here is to find all the event informations using the event id. The onDataChange methods are executed concurrently. We need to nest the second query under the first onDataChange method in order to make the operations in the second query get executed after we retrieve the eventListView . Next step is to retrieve each event’s information by iterating (for(int i =0; i<eventListView.length;i++)), and store them separately ineventDateList; eventNameList; eventTimeList; eventLong;eventLat;. Last step is to pass the arraylists to the custom adapter and inflate the view. 

Creating Event

Implementation:
When user clicks on date and time,  onClickListener calls getSupportFragmentManager()and inflates the DatePickerDialog fragment. And then set edit text fields to user’s selection on the calendar instance. After user enters all fields, the app updates the data to database.

User Notification
One of the most useful functionalities of HurryUp is that we can interact with the user in real-time. We want user to receive Notification every time he/she is invited to a new event. The other kind of Notification will remind the user that he/she has an upcoming event soon.

Notification(Invitation):

Implementation:
We have two ways to inform the user that they have been invited to an new event. As Professor suggested we went for the option that pulling information from the server periodically. First we have an attribute under users called events. It is a list of events that user is invited to. Under the list we have each event represented by a unique event id as keys in our Json. Then the value of that key is a boolean. We choose to have an service running in the background checking this list periodically for events that have value:true. If so, the service will create a notification tell user that he/she is invited to an new event and set the value of that event to false.


Challenges
The problem here is how we keep the service running in background without causing any problem. To keep the service running we set a alarm manager to restart this service in a short time interval. 
Then the real pinch point here is how we prevent this service to cause problems since it requires user to be logged in and it will keep checking user’s information in database. If the user logged out the service can’t access to that event list it will crush. After all we decide to start the service when user have logged in and done generating his profile activity.
Also, the first thing we do when people hit log-out button is to stop that service and cancel the pending service(it self) in the alarm manager. 
By doing this we are able to keep pulling information from database while the user is logged in.

Notification(upcoming Event in 5 Mins):

Implementation:
This is easier than the previous notification. We have a button called “accept this invitation” when user click on that button, we need to set up a alarm-manager and pending intent which wraps our upcomingEvents activity. Then we pass the intent to alarm-manager and set the time to 5 mins before the real event time. We can get all the information we need from upcomingEvent activity.

Challenges:
The challenge here is how should I convert the event time from string to timeInMillie() that alarm-manager requires. We decided to create a calendar object then set the time in this object with parsed string time from database. Then use  to get the time in Millisecond form. At first we can’t get the correct timeInMillie() result. After testing and testing, we discovered that, in calendar object, the month start from index 0 instead of 1. It means 0 is January and 11 is December. After this we are doing fine and figure out the function.

Ranking System - Top Ten Users

Implementation:
Thanks to Firebase query API, the build in method orderByChild("score").limitToLast(10) helps retrieving 10 Users with highest “score” field. The Code segment in TopTenUsersActivity.java is shown below. First we reference to users in database, then execute this query that orders by score. The key thing to note is this function returns 10 user objects with highest scores in the database by calling onChildAdded ten times, with each user object stored in dataSnapshot. However it is in ascending order - the user with lowest score within these 10 users get to be returned first. Thus we need to reverse back to descending order by inserting into the top of an arraylist returnUserNameList. This way, we get our ten highest score users in descending order.

Challenges:
The first challenge was before we decide to implement this functionality, we stored user score as a String field in database - Strings are typically easy to retrieve. Thus if we wanted this functionality, we would have to change the score field to Long type - the numeric type supported by Firebase is Long rather than Integer. And only this way could we use the orderBy query offered by Firebase. However, this change was relatively dramatic - the mapping class AppUser would need to be changed, and all other points where we retrieve and store user info follows as well. There was indeed a lemma between if we wanted to take the risk and make changes all over the app or to just abort this functionality. We decided to take the risk just to offer more fun to our app.
The second challenge is realizing the callback onChildAdded is executed on a separate thread. At the beginning, we put ListViewTopTen.setAdapter(itemsAdapter); (at line 48) after this method call. However, nothing was shown as “supposed to”. Besides, using debugger mode does not reveal anything of concurrency, which raises difficulty to debug. At last, the traditional method for debugging helped us: to use Toasts, and we noticed that the Toast below callback method was shown first, thus we finally realized this problem.
	
Geofence

Implementation:
In our project, we use Google's GeofencingApi which includes Geofence, GeofencingRequest, GeofenceApi, GeofencingEvent, and GeofenceStatusCodes. Geofence is an interface that represents a geographical area that should be monitored. During its creation, you set the monitored region, the geofence's expiration date, responsiveness, an identifier, and the kind of transitions that it should be looking for. We use this service to check if a user has arrived to the destination by setting a geofence around the destination.
We first set permission:
We create the geofence by using the Geofence.Builder.
The Geofencing Request class receives the geofences that should be monitored. You can create an instance by using the builder, it could take a geofence or a list of geofences, and setup the trigger type of the geofence.
To add a geofence, we need to call addGeofence() method and pass GeofencingRequest to it and shoots a PendingIntent when a geofence transition, entering or exiting the area, take place.
We also need to access the user's current location. The FusedLocationProviderApi interface gives us this information and allows a great level of control of the location request.
On the MapsActivity, we need two different markers. A location marker that given by the FusedLocationProviderApi to inform the device’s current location.
A geofence Marker is the Event location.


Geofence Transition Service integrated with Score addition and subtraction

Implementation:
We use PendingIntent object to call an Intentservice that will handle the geofence event.
We also draw the geofence on the map as a visual reference.
The startGeofence() is responsible for starting the geofence process in the MapsActivity.class
We call the startGeofence() on the menu bar by clicking “I’m here!” or “Show Geofence”.
We create the GeofenceTransitionService class to handle the geofence event. The class extends the IntentService. First, we get this event from the received event under the callback method onhandleIntent():
The geofence transition will be triggered at the time when the user enters, exits, or stays in the geofence area for a while. Once it is triggered, we check if the kind of geofencing transition that took place is of interest to us (entering) and we check if the time is within 5 minutes before the eventTime. 
If it is, we then send the notification to the user and add the score into the user’s account. We also label the user as arrived to avoid double counting.


Challenges:
 	One of the challenge we have met is how to avoid the multiple count for the adding score. When user entering the geofence, they could keep clicking the show geofence to add point to themselves. It is hard to check, and the solution we use is to passing a boolean value when the user first go into mapsActivity and we set the value as false. Once the point is added to the user, we set the boolean value to true. When the user just repeat clicking the show geofence, the system will if the boolean is true, if it is, we stop the service.
	Another pinch point was the Intent Service would not trigger at the first time we implemented it. Finally we found that we need to add the service clam into the manifest.xml to give the program permission.

Other Features
Other functionalities include adding friends and displaying friend list, changing profile pictures, and logging out.

# Future Improvements - Hurry up 2.0
Notification Adjustment
In the future, we want to be able to custom notification to users according to the distance from their current location to the event location. For example, if the user still have 5 miles to go before 10 minutes of the event, we could notify the user “You need to hurry! You still have 5 miles to go!”. The reason for not including this feature in the current version is that we require the user to go into the MapsActivity by clicking on the event he is about to attend. However, if a user already logged in our application and clicked on the event, he or she must already know to hurry. Thus in order for this feature to work in the future, we would need the application to keep running in the background to detect user location. So far, we provide a way for user to know how far he is by manually click on the google map icon at the bottom right corner at MapsActivity.
Detect all other users’ locations within an event.
	In our current version, we can only detect user’s own location. In the future, we want to show all user’s locations on the map in real time, so that each user can easily track other user’s locations in case the meet up location is vague. 
Recommendation for punishments according to your event.
	The app is able to pop up suggestions on how to punish your friends for being late. For example, if the event is under sport category, the app will offer many punishment options like doing push ups. Users who arrive on time can vote for options they like. And people who are late need to finish punishment with the highest votes.
