This README provides instrunctions as to the execution of the Stelhound application.

**********System requirments***************
Java 1.7 or higher
mongoDB 3.2 or higher
*******************************************

1. Ensure mongoDB is installed on the device. To see how to install mongoDB use the following guide: 
https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/

2. Start mongoDb by typing "service mongod start" into the command line

3. Verify that mongo is working correctly
    - Type "mongo" into the command line
    - now type "use stelhound"
    - Type  "db.music.find( {}, {title: 1,artist: 1 } )" into the command line
    - if there is no result the database is empty

4. To run the administrator app type "java -jar stelhound_Admin.jar"

5. To run the user app type "java -jar stelhound_User.jar"

To edit and update the source code, the source files need to be placed into an IDE
like Eclipse. Once the source code is in, add the libraries in the libs folder to the 
programs build path. There should now be no errors in the program.

The previous.txt file is generated and can be modified or deleted manually without
consequence.

The unit_files contain files used in the unit testing of the program. Without these
files test cases with throw errors and potentially crash.
