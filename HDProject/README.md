# Health Diary

### 1. Source files
The Health Diary Project includes all functions that have been developed for the Health Diary app.
  - Source files are located in folder .\app\src\main. 
  - The code files are located in .\app\src\main\java\app\healthdiary, including five packages: Activities, Helper, Listeners, Services and SrveyHelper. 
  - User interface related code files (.java) are located in package Activities: .\app\src\main\java\app\healthdiary\Activities.
  - User interface layout files (.xml) are located in .\app\src\main\res\layout.

Global string variable is defined in .\app\src\main\res\values\strings.xml

Other resources such as images are located in folders: .\app\src\drawable-*

### 2. Activities
Map between the user interface related code files and user interface layout files:
  - LoginScreen.java---loginscreen.xml (the login page)
  - MainPage.java---mainpage.xml (the main page after user login)
  - SettingActivity.java---activity_setting.xml (the setting page)
  - WeeklyQ1Direct.java---activity_weekly_q1_direct.xml (the first survey question)
  - WeeklyQ2Direct.java---activity_weekly_q2_direct.xml (the second survey question)
  - WeeklyQ3Direct.java---activity_weekly_q3_direct.xml (the third survey question)
  - RandomMomentContacts.java---activity_random_moment_contacts.xml (the fourth survey question)

After login, a main page shows. A set of survey questions prompts at a random moment through 8 AM to 8 PM each day.

### 2. Service: Location data collection

> A service runs in background collecting subjects' location data continuousely.
> Location data sampling frequency is dynamically adjusted 
> based on the motion status such as moving or staying.
> Location data sources (including GPS and network) is also switched 
> based on factors such as motion status, battery level and GPS accuracy.

Classes: 
-  LocationCollectionService: a service runs in background catching event broadcasts including screen off, battery power level low and battery power level recover. It runs a LocationCollector instance and a MotionDetector instance.
-  LocationCollector: an object collects location data. It collects location data utilizing Google LocationManager. It takes four types and combinations of location data sources including Passive, GPS, Network and GPS + Network. It also takes two modes of data collection for moving and staying. In the moving mode, location data is sampled every 20 meters with a minimun time interval of 10 seconds while in the staying mode, location data is sampled every 5 minutes. 
-  MotionDetector: an object collects motion sensor (Accelerometer) data and determines motion status. It takes the LocationCollector instance of the LocationCollectionService as a member variable and switch the location data sources and data collection modes of the LocationCollector instance according to the detected motion status.

Usage: Create and Start the LocationCollectionService with the starts of the application. Stop the service when closing the application or at the time you want to actively stop the service.

### Version
1.0.1


License
----

BSD
