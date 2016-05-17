# Mapmaker

The Application was programmed in Android Studio and is called Mapmaker. The Project was to make an Application for a Smartphone (this i for Android-Devices).
The challenge was to let the Roboterball Sphero run in a little Test-environment and let him map the environment.

Connect Sphero:
To connect Sphero with the app it has to be coupled via Bluetooth first. Afterwards you can start the app and the coupled Sphero device will automatically connect.
Now you can make Sphero run around and get the coordinates of the environment. Sphero will drive automatic with an algorithm to check for walls.
The coordinates Sphero is collecting are realtime painted to the card in the app, so you can see the walls he detects.
Every Kollision is painted as a little red dot, all dots together form the groundsize of the environment.


Important for developers:
- There is till now no multilanguage support, the standard is german.
- The project contains 2 main classes:
    - one for Sphero detect walls with the inner implemented collision detection
    - the other for make Sphero detect walls with the comparison of Position Data
    (if you want to switch between the classes, change the main-class import in the activity-main and Andoird-Manifest.
    
