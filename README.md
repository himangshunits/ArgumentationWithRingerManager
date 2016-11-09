// TODO : Update this for S_D

# SmartRingerManager
Ringer Manager Application which adapts to it's surroundings to automatically set the ringer mode of your fone!

The code contains the following classes.

AttributeVectorInfo.java(keeps data of the final attributes)
CallerInfo.java (Information of one particular call)
Driver.java(The Driver class which simulates one full flow, also the one interacting with the network to get the data)
EnumCollection.java (Lists all the different enumeration used for different ordinal variables used in the modeling)
FeedbackInfo.java (Information on the feedbacks received from a call)
LocationManager.java (Keep track of all the locations currently being used for the simulation, their types and noise levels)
NeighborInfo.java (information on the neighbors, basically it tells about the relation with that person)
PeopleManager.java (manages all the people in the database)
RingerManagerCore.java (This is the main class which is responsible for owning and maintaining the decision tree structure, synthesizing the attributes and giving ten final recommendations)

We use an external library called DecsionTree for the Decision Tree related modellings. The URL of the Decsion Tree Repo is
https://github.com/mostafacs/DecisionTree

The project associated also contain the JAR file of the library under the directory /JavaApp/DecsionTrees

We also use Google's JSON Data Parsing libarry called GSON which is located in the directory /JavaApp/JSON

Please add the above two JARs in to the project before using it.

#######   DATA   #######

The Data Files are loaded in to the directory /JavaApp/Data. These files keet getting updated dynamically during the program run, so please provide appropriate access,



## The project

The Project here is an IntelliJ Idea project and will work best with the same IDE. Please add the JARs as described above. The Code has two modes of operation - the user data mode and random simulation mode. The Random Simulation mode allows you to run the full flow once with input values chosen from the collection in random. I the user mode, it will ask you for your username, the current RINGER_MODE of yours and the EXPECTED_RINGER_MODE of yours. Also it aks for a place you want to enter. Please give the string inputs as exact literals as finalised in the project description. Faluty input recovery is not implemented and will crash the system. Detailed desriptions are given in the inputs also.

One typical run flow of he program is given below,

Please Enter your's ID or type in 0 to Randomly Run once ! :: 
5031
You have Entered ID = 5031
Please Enter your's Current Ringer Mode. Use proper names as decided in the project description.(Loud, Vibrate, Silent) 
Loud
You have Entered Current Mode as = Loud
Please Enter your's Expected Ringer Mode. Use proper names as decided in the project description.(Loud, Vibrate, Silent) 
Vibrate
You have Expected Mode as = Vibrate
Please Enter the location you want to visit. Choose among (hunt, eb2, carmichael, oval, seminar, lab, meeting, party)
carmichael
You have location as = carmichael
Going to Make a series of calls now.
Going to Enter Place :: carmichael
Going to request for neighbor info.
The Neighbors Retrieved are ::
[NeighborInfo{id=1002, name='Bilbo', type=FRIEND, strength=HIGH, currRingerMode=Loud, expectedRingerMode=Loud}, NeighborInfo{id=1015, name='Sauron', type=COLLEAGUE, strength=MEDIUM, currRingerMode=Vibrate, expectedRingerMode=Loud}]
Going to Request a call.
The Caller's information below :: 
CallerInfo{callId=2969, callerId=1013, name='Orophin', type=FAMILY, strength=HIGH, urgency=CASUAL}
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
######################################################################
The Prediction made from the Social Benefit Function = Loud
######################################################################
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Sending Response and Getting feedbacks!
The Feedbacks Received :: 
{"callId":"2969","user":[{"id":"1013","feedback":"positive"},{"id":"1002","feedback":"negative"},{"id":"1015","feedback":"positive"}]}
Going to Exit Place.
Processing Feedbacks, going to push to the system and recreate the system.
Happy Feedback !! 
Positive =  2
Neutral = 0
Negative = 1
Feedbacks successfully pushed! The Decision trees are updated.
One Simulation Completed !
Please Press D + Enter to run again or any other key + Enter to exit.


Also, the code is privately uploaded in the following github link,
https://github.com/himangshunits/SmartRingerManager




please contact me in hborah@ncsu.edu or 9197858515 if at all you have any issues in running the code.




Few Known Issues:

Decision Tree Initialization error :: => Some issue in the format of the training file data/initial_rules.psv
please empty the contents of the file and just paste

LOCATION_TYPE|NOISE_LEVEL|NEIGHBOR_JUDGEMENT|CALLER_EXPECTATION|URGENCY|CLASS
LIBRARY|2|Vibrate|SHOULD_RECEIVE|URGENT|Vibrate

lines into it. It will reset the trees and start learning from ground again.
