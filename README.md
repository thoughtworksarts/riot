# Riot Arts Residency Project

[![Build Status](https://travis-ci.org/thoughtworksarts/riot.svg?branch=master)](https://travis-ci.org/thoughtworksarts/riot)

# Before you start

### Production Mac State and Setup for Perception IO (And how to mimic locally)

As a brief overview, as of 10/29/19 Perception IO has been installed on mac on museum exhibit. The laptop is equipped with a custom setup to run the exhibit, as outlined below. As many of these settings and configurations are machine-specific, they are not committed to the general code base, hence requiring each person who handles the mac or any similar environments to do some manual configurations to mimic the production setting. 

This application is sensitive to the operating system of the mac, hence why there are even some Gradle build file settings that should not be checked in to the codebase. This document should demystify the setup process for future work. 


#### Hardware Setup at the Museum
- Mac is a 15-inch Mojave (Note: App will not run on Catalina, so far)
- Specs: 
    - 2.8 GHz Intel Core i7
    - 16 GB 2133 MHz LPDDR3
    - Radeon Pro 555 2048 MB
      Intel HD Graphics 630 1536 MB
- Two HDMI/USB adaptors
- Arduino button

#### Installation steps to set up the computer:
1. Install IntelliJ, iTerm (if you want), Java 12, Cursorcer (http://doomlaser.com/cursorcerer-hide-your-cursor-at-will/)
1. download latest resources zip file (ask team members)
1. download the current version of the movie (ask team members)
1. Pull the code from github and git clone into your repo
    1. You may have to install XCode tools, click yes
1. Open the project in IntelliJ by selecting Import Project and selecting the `build.gradle` file from the repository you just cloned
    1. It will start to build, but once you’re ready I recommend doing builds via an external terminal, Terminal or iTerm, not IntelliJ. Ignore reports that IntelliJ gives you about being able to build the project. 
1. Make these changes in the build.gradle file:
    1. Inside the `task fatJar() { }` section, add the line `zip64 true` on its own line
    1. Add the following two sections right below (but outside of) the fatJar section:
        1. ``` shadowJar { zip64 true} ```
        1. ```distZip { zip64 true} ```
    1. Find this line and add the second flag so it looks like this:
        1. `applicationDefaultJvmArgs = [“-Djava.library.path=asio”, “-Dprism.verbose=true”]`
1. Replace the `src/main/resources` directory with the most recent resources folder from the google drive, unzipped.
1. Replace the `resources/video/final-film.m4v` with whatever the latest video file is (has been changed multiple times for subtitles, audio assistance, and video quality and is too big of a file to be checked into the repo)
1. Run `./gradlew build` in your terminal. If it works, you have successfully set up the project. 

#### Getting the Machine Ready to Run:

1. Open the application Automator
1. Create a new Application to Run a Shell Script with a bash script
1. The contents of the bash script are 
    ```
    echo Moving to riot repository...
    cd ~/Desktop/riot
        
    echo Starting application
    ./gradlew run --offline -i 2>&1 | tee file.txt
1. Now add the new Automator application to you Login Items by going to `System Preferences > Users and Groups > Login Items` and 
    1. Add your new application
    1. Check the box to hide Cursorcer, which should be in your Login Items since you set it up
1. Instructions for automatic shutdown at midnight every night:
    1. Create a file somewhere `shutdown.sh` and paste this into the file
        ```
        killall -HUP java
        osascript -e 'tell app "System Events" to restart'
        
    1. go to terminal and type the command: crontab -e. This command opens a vim editor where you can store cron jobs. 
       1. If you want, read more about it here:https://askubuntu.com/questions/567955/automatic-shutdown-at-specified-times
    1. In your vim editor type out the following command: `59 23 * * 0-6 bash /path/to/shutdown.sh`
    1. After you write this, exit vim by typing `escape-key :wq` and then press `enter`. Once you enter this command, it will save the cron job and ask you to confirm. Once this is set up, the computer should automatically force quit the java application and restart at midnight every night.

#### Restarting the computer manually:
1. You have to click restart and then wait for the prompts to force quit Java. If you don’t click continue and force quit, it will just hang and the computer will not restart
1. Instructions to shut down the app but leave the computer on: 
    1. Right click the Java icon in the dock and click `Quit`
    1. Wait a while, then repeat and click `Force Quit`


#### Exhibition proofing: Laptop Prep Notes (Settings applied for this exhibit)
Based on Installation4Evr (https://github.com/laserpilot/Installation_Up_4evr)

- Disable your screensaver. Set it’s time to Never
- Set desktop to Gray
- Turn Display Sleep and Computer Sleep to Never.
- Turn off Require Password after sleeping / disable screen lock in Security & Privacy
- Software Update: Disable automatic updates
- Advanced - disable update check
- Disable bluetooth
- Notification center - harden Do Not Disturb
- Clean Dock apps out of Dock
- Add perception app to Dock and add to Login Items
- Auto-hide Dock and menu bars
- Remove desktop icons (https://www.maketecheasier.com/hide-desktop-icons-mac/)


### On Windows

You need to execute the gradle task to download the audio dependency,
DLL and Driver for windows.

    gradlew assemble

After that, you need to execute and install the driver (you can also
download directly from the provider as the URL is in the `build.gradle` file.)

You can find the downloaded files in the newly created directory `asio` once you have finished
executing the task.

## Run the application

In the `src/main/resources/media/` directory add a .m4v video and corresponding .wav audio file.
Create directory `src/main/resources/trainingModels/` and add a .h5  and corresponding .json file.

    ./gradlew run

## Build locally

    ./gradlew build
    
    
### Import to IDE

If you are using IntelliJ Idea you will need to use install the [lombok
plugin.](https://projectlombok.org/setup/intellij)

If you are using Eclipse you must 
[integrate Lombok with your Eclipse installation](https://projectlombok.org/setup/eclipse).