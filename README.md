# SmartRingerManager with Argumentaion and Rationales Enabled

Ringer Manager Application which adapts to it's surroundings to automatically set the ringer mode of your fone!
It sends it's predictions to it's neigbors and also shares the Rationale behind that prediction. Depending on the feebacks received from the beigbors, it updates it's models making the predictions. It also has another module which takes care of analysing the rationales shared by their neighbors and provide second feedbacks.

## The code contains the following classes.

- AttributeVectorInfo.java(keeps data of the final attributes)
- CallerInfo.java (Information of one particular call)
- Driver.java(The Driver class which simulates one full flow, also the one interacting with the network to get the data)
- EnumCollection.java (Lists all the different enumeration used for different ordinal variables used in the modeling)
- FeedbackInfo.java (Information on the feedbacks received from a call)
- LocationManager.java (Keep track of all the locations currently being used for the simulation, their types and noise levels)
- NeighborInfo.java (information on the neighbors, basically it tells about the relation with that person)
- PeopleManager.java (manages all the people in the database)
- RingerManagerCore.java (This is the main class which is responsible for owning and maintaining the decision tree structure, synthesizing the attributes and giving ten final recommendations)


## New Classes Added for the Argumentation Theory:

- ArgumentInfo.java -> Structure for storing one Argument which has a context keyword, predicates if any and the values.
- CallInfo.java -> Information about one particular call. used when analysing the calls of the neighbors.
- DynamicArgumentManager.java -> This is the main class which keeps the ground truth beliefs for generating and analysing the Rationales. It does three main tasks, creates the Rationales before sending out a prediction, analyses the incoming Rationales from the neigbors and keeps updating it's models.
- RationalInfo.java -> Keeps information of the whole Rationale. It has two kinds of components. It stores the arguments, the predictions and the connectives for both ArgInFavor and the ArgInOpp counterparts.
- RationaleManager.java -> Wrapper class for the DynamicArgumentManager class which own one DynamicArgumentManager in it's memory and makes the decisions on how to handle the Rationales. It uses the Static Class and RationaleSerializerParser for generating and parsing the Rationales.
- RationaleSerializerParser.java -> Utility class with static helper functions for generating the Rationale String to send out of the system and also to parse the incoming Rationale Strings from the neighbors.




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

Silent

You have Entered Current Mode as = Silent

Please Enter your's Expected Ringer Mode. Use proper names as decided in the project description.(Loud, Vibrate, Silent) 

Vibrate

You have Expected Mode as = Vibrate

Please Enter the location you want to visit. Choose among (hunt, eb2, carmichael, oval, seminar, lab, meeting, party)

seminar

You have location as = seminar

Going to Simulate the flows now.

Going to Enter Place :: seminar

Going to request for neighbor info.

The Neighbors Retrieved are ::
[NeighborInfo{id=1001, name='Arwen', type=FAMILY, strength=HIGH, currRingerMode=Silent, expectedRingerMode=Silent}, NeighborInfo{id=1007, name='Gandalf', type=COLLEAGUE, strength=HIGH, currRingerMode=Vibrate, expectedRingerMode=Silent}]

Going to Request a call.

The Caller's information below :: 

CallerInfo{callId=869, callerId=1001, name='Arwen', type=FAMILY, strength=HIGH, urgency=URGENT}

Flag! Not present in the initial training set, adding now and rebuilding the Decision Trees!

Searching for Line Part to remove = MEETING|5|5|Vibrate|MUST_RECEIVE|URGENT

Going to add = MEETING|5|5|Vibrate|MUST_RECEIVE|URGENT|Vibrate

Models Updated with new Data, sending new recommendations.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
######################################################################
The Prediction made from the Social Benefit Function = Vibrate
######################################################################
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
######################################################################
The Rationale of Prediction = ArgInFav(ringermode-IS-VIBRATE+WHEN+Majority(expected_mode)-IS-VIBRATE+AND+noise-IS-5)++ArgInOpp(ringermode-IS-LOUD+WHEN+call_reason-IS-URGENT+OR+AtleastOne(expected_mode)-IS-VIBRATE+OR+place-IS-SEMINAR)
######################################################################
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Sending Responses!

Getting feedbacks!

The Feedbacks Received from everyone :: 

[FeedbackInfo{userID=1001, userName='Arwen', isCaller=true, relType=FAMILY, feedback=NEUTRAL, feedbackUpdated=null}, FeedbackInfo{userID=1001, userName='Arwen', isCaller=true, relType=FAMILY, feedback=POSITIVE, feedbackUpdated=null}, FeedbackInfo{userID=1007, userName='Gandalf', isCaller=false, relType=COLLEAGUE, feedback=POSITIVE, feedbackUpdated=null}]



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
####################   New Flows Here ! Getting the Calls in Current Place  ####################



Getting Calls in Current place!

Going to Give Feedbacks.

First Feedback Sent = NEUTRAL

Going to ask for second feedback synthesis with rationale = ArgInFav(ringermode-IS-VIBRATE+WHEN+Majority(expected_mode)-IS-VIBRATE+AND+noise-IS-5)++ArgInOpp(ringermode-IS-LOUD+WHEN+call_reason-IS-URGENT+OR+AtleastOne(expected_mode)-IS-VIBRATE+OR+place-IS-SEMINAR)

Going to Give Feedbacks.

Updated 2nd Feedback Sent = POSITIVE



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
####################   New Flows End here!  ###########################



Going to Exit Place.

Processing Feedbacks, going to push to the system and recreate the system.

Happy Feedback !! The following are the weighted feedback rankings according to the relationships. 

Positive =  35

Neutral = 30

Negative = 0

Feedbacks successfully pushed! The Decision trees are updated.

One Simulation Completed !

Please Press D + Enter to run again or any other key + Enter to exit.


Also, the code is privately uploaded in the following github link,
https://github.com/himangshunits/SmartRingerManager




## Few Known Issues:

Decision Tree Initialization error :: => Some issue in the format of the training file data/initial_rules.psv
please empty the contents of the file and just paste

LOCATION_TYPE|NOISE_LEVEL|BRIGHTNESS_LEVEL|NEIGHBOR_JUDGEMENT|CALLER_EXPECTATION|URGENCY|CLASS
PARTY|7|3|Loud|MUST_RECEIVE|URGENT|Loud
PARTY|8|7|Loud|SHOULD_RECEIVE|CASUAL|Loud

lines into it. It will reset the trees and start learning from ground again.


Also, sometimes the Core Decision Tress making the predictions can be corrupted because of the updates from bad inputs from the neigbors. In that case it will show appropriate messages and please act accordingly. If you see the following message ::

"ABORT! Decision Trees Corrupted by bad feedbacks from neigbors! Please clean the initial_rules.psv manually. Abort!"

Then you must open the initial_rules.psv file manually and check for inconsistencies in the training set.

Finally, please don't try to use the Randomization mode as it is normally for the purpose of initial training and can corrupt the training set immensely. So please be careful.



Please contact me in hborah@ncsu.edu or 9197858515 if at all you have any issues in running the code or need access for the git Repository.
