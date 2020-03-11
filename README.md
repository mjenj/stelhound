## Stelhound
A music identification tool, which allows you to match songs between a database and microphone

# Executing the Stelhound application.
Ensure you have the correct version

## System requirements
Java 1.7 or higher
mongoDB 3.2 or higher

## Uploading songs to the DB for matching

1. Ensure mongoDB is installed on the device. To see how to install mongoDB use the following guide: 
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

2. Start mongoDb by typing "service mongod start" into the command line

3. Verify that mongo is working correctly
    - Type "mongo" into the command line
    - now type "use stelhound"
    - Type  "db.music.find( {}, {title: 1,artist: 1 } )" into the command line
    - if there is no result the database is empty

4. Run the administrator app type "java -jar stelhound_Admin.jar". This wil open a GUI which will allow you to upload tracks

## Running the matcher application

To run the user app type "java -jar stelhound_User.jar"

# Developing on the app further
## Editing

To edit and update the source code, the source files need to be placed into an IDE
like Eclipse. Once the source code is in, add the libraries in the libs folder to the 
programs build path. There should now be no errors in the program.

The previous.txt file is generated and can be modified or deleted manually without
consequence.

## Notes

The unit_files contain files used in the unit testing of the program. Without these
files test cases with throw errors and potentially crash.
