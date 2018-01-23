# Riot Arts Residency Project

[![Build Status](https://travis-ci.org/thoughtworksarts/riot.svg?branch=master)](https://travis-ci.org/thoughtworksarts/riot)

# Before you start
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