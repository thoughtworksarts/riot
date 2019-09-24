ECHO Printing current path %cd%
cd ..\perception-eye-tracking\ 
ECHO Changing paths %cd%
cd C:\Users\Kiosk\perception-eye-tracking\eye_tracking & set FLASK_APP=api.py & start /b flask run & ECHO Started app successfully
