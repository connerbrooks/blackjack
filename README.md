# BlackJack
This is a version of BlackJack written for my CS2 class.

## How to build

### Configure your phone
* Install drivers for your phone, you may need to add UDEV rules for your phone to be recognized by ADB.
* Open settings
* Navigate to 'About Phone' and tap build number mulitiple times until you have unlocked developer options.
* Go back to settings and select 'Developer Options' then enable USB debugging.
* If your computer recognizes your device the device will ask you if you trust the computer select yes and continue.

### Android Studio
* Download [Android Studio](https://developer.android.com/sdk/installing/studio.html)
* Select import project and select the top-level 'BlackJack/build.gradle' file.
* You may be asked to download the Android SDK, go to the android SDK manager (android icon with blue down arrow) and download SDK with API level 20.
* You will want to add the sdk to your path so that you have access to the build tools via command line.
* Press the play button, if everything is configured correctly you should see your phone as an option for install location select your device.
