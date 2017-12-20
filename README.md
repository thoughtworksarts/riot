# Riot Arts Residency Project

# Before you start
### On Windows

You need to execute the gradle task to download the audio dependency,
DLL and Driver for windows.

    gradlew downloadAsioDependency

After that, you need to execute and install the driver (you can also
download directly from the provider as the URL is in the `build.gradle` file.)

## Run the application

    ./gradlew run


## Build locally

    ./gradlew build
    
    
### Import to IDE

If you are using IntelliJ Idea you will need to use install the [lombok
plugin.](https://projectlombok.org/setup/intellij)

If you are using Eclipse you must 
[integrate Lombok with your Eclipse installation](https://projectlombok.org/setup/eclipse).