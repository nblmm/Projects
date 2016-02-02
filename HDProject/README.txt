The Health Diary Project implements functions to continuously collect subject's location data and prompt survey questions at a random moment between 8 AM to 8 PM each day.

1. Continuous location data collection
1.1 Sample location information from GPS and network sensors. Sampling frequencies: once per 20 meters with the time interval no less than 10 seconds when moving; once per 5 minutes when staying.
1.2 Using Significant Motion Sensor (if applicable), Linear Accelerometer (if applicable), and Accelerometer readings to determine moving or staying. Sampling rate: once per 3 seconds.
1.3 When battery is low (notified by system), using network positioning at the frequency of once per 5 minutes. 
1.4 When the error of GPS is larger than 200m for more than five minutes, lower the GPS sampling frequency and using network positioning.
The location data is saved in local files as the format of (source, time, latitude, longitude, speed, error).

2. Survey questions
Not released.

Others£º
1. Auto startup
2. Auto login
3. Forground notification





