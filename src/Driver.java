import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.binding.IntegerConstant;
import com.sun.org.apache.xpath.internal.Arg;
import sun.reflect.annotation.ExceptionProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * Created by Himangshu on 10/22/16.
 */
public class Driver {
    private static PeopleManager mPeopleManager = new PeopleManager();
    private static RingerManagerCore mRingerManagerCore = new RingerManagerCore();
    // TODO : Add all the Defauls for the People and the location dictionaries.
    // TODO : HAndle all the faulty inputs froom WEB Service.
    // If there are others at the same place where you are and if their relationships are not known, assume they are strangers.


    private static CallerInfo requestCall(int myId){
        System.out.println("Going to Request a call.");
        try {
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=requestCall&userId=" + myId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String nextLine;
            StringBuilder output = new StringBuilder();
            while((nextLine = br.readLine()) != null){
                output.append(nextLine);
            }
            Gson gson = new Gson();
            JsonElement element = gson.fromJson (output.toString().trim(), JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
            conn.disconnect();

            int callId = Integer.parseInt(jsonObj.get("callId").getAsString());
            int callerId = Integer.parseInt(jsonObj.get("callerId").getAsString());
            String name = jsonObj.get("callerName").getAsString();
            Integer relValue = Integer.parseInt(jsonObj.get("relationship").getAsString());
            EnumCollection.RELATIONSHIP_TYPE type = EnumCollection.RELATIONSHIP_TYPE.values()[relValue - 1];
            // Assigns high if not present.
            EnumCollection.STRENGTH_TYPE strength = mPeopleManager.getStrengthForName(name);
            EnumCollection.URGENCY_TYPE urg = EnumCollection.URGENCY_TYPE.valueOf(jsonObj.get("reason").getAsString().toUpperCase());
            CallerInfo result = new CallerInfo(callId, callerId, name, type, strength, urg);

            return result;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private static void enterPlace(String placeName, int myId, EnumCollection.RINGER_MODE myMode, EnumCollection.RINGER_MODE expectedMode){
        System.out.println("Going to Enter Place :: " + placeName);
        try {
            //?action=enterPlace&place=P&userId=U&myMode=M&expectedMode=E
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=enterPlace&place="
                    + placeName +"&userId=" + myId + "&myMode=" + myMode.toString() + "&expectedMode=" + expectedMode.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            return;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return;
    }

    private static void exitPlace(int myId){
        System.out.println("Going to Exit Place.");
        try {
            //?action=exitPlace&userId=U
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=exitPlace&userId=" + myId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            return;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return;
    }


    private static List<NeighborInfo> getNeighbors(int myId){
        System.out.println("Going to request for neighbor info.");
        List<NeighborInfo> result = new LinkedList<>();
        try {
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=getNeighbors&userId=" + myId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            StringBuilder output = new StringBuilder();
            String nextLine;
            while((nextLine = br.readLine()) != null){
                output.append(nextLine);
            }
            Gson gson = new Gson();
            JsonElement element = gson.fromJson (output.toString().trim(), JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
            conn.disconnect();
            JsonArray adjList = (JsonArray)jsonObj.get("user");
            for(JsonElement item : adjList){
                JsonObject tempObj = (JsonObject)item;
                /*
                * {
                      "id": "1",
                      "name": "null",
                      "ringer-mode": "Silent",
                      "expected": "Silent"
                    }
                * */
                int id = Integer.parseInt(tempObj.get("id").getAsString());
                String name = tempObj.get("name").getAsString();
                EnumCollection.RINGER_MODE currMode = EnumCollection.RINGER_MODE.valueOf(tempObj.get("ringer-mode").getAsString());
                EnumCollection.RINGER_MODE expMode = EnumCollection.RINGER_MODE.valueOf(tempObj.get("expected").getAsString());
                Integer relValue = Integer.parseInt(tempObj.get("relationship").getAsString());
                EnumCollection.RELATIONSHIP_TYPE type = EnumCollection.RELATIONSHIP_TYPE.values()[relValue - 1];
                NeighborInfo tempNeighbor = new NeighborInfo(id, name,
                        type, mPeopleManager.getStrengthForName(name), currMode, expMode);
                result.add(tempNeighbor);
            }
            return result;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static JsonObject sendResponseCall(int callId, EnumCollection.RINGER_MODE predictedMode, String rationale){
        System.out.println("Sending Responses!");
        try {
            //?action=responseCall&callId=C&ringerMode=R
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=responseCall&callId=" + callId + "&ringerMode=" + predictedMode.toString()
                    + "&rationale=" + rationale);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String nextline;
            StringBuilder output = new StringBuilder();
            while((nextline = br.readLine()) != null){
               output.append(nextline);
            }

            Gson gson = new Gson();
            JsonElement element = gson.fromJson (output.toString().trim(), JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
            conn.disconnect();
            return jsonObj;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return null;// Returns true on success
    }



    private static List<FeedbackInfo> listFeedbacks(Integer callId, Integer callerId, HashMap<Integer, String> idNameMap){
        System.out.println("Getting feedbacks!");
        try {
            //?action=listFeedbacks&callId=C
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=listFeedbacks&callId=" + callId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String nextline;
            StringBuilder output = new StringBuilder();
            while((nextline = br.readLine()) != null){
                output.append(nextline);
            }

            Gson gson = new Gson();
            JsonElement element = gson.fromJson (output.toString().trim(), JsonElement.class);
            JsonObject feedbacks = element.getAsJsonObject();


            //Analyse Feedbacks and push the new decision of the Crowd.
            int idNew = Integer.parseInt(feedbacks.get("callId").getAsString());
            // Check if the Call ID and the Call ID from the feedback was the same@! Must be same.
            if(callId != idNew){
                System.out.println("Feedback got for different Call!!!!!! Error");
            }
            List<FeedbackInfo> feedbackList = new LinkedList<>();
            JsonArray adjList = (JsonArray)feedbacks.get("feedbacks");


            for(int i = 0; i< adjList.size();i++){
                boolean isCaller;
                JsonObject tempObj = (JsonObject)adjList.get(i);
                // What is CalleeID here?
                Integer id = Integer.parseInt(tempObj.get("id").getAsString());
                if(id.equals(callerId))
                    isCaller = true;
                else
                    isCaller = false;
                String name = idNameMap.get(id);

                EnumCollection.FEEDBACK_TYPE fType = null;
                EnumCollection.FEEDBACK_TYPE fTypeUpdated = null;

                if(tempObj.get("feedback").getAsString().toUpperCase().equals("NULL")){
                    fType = null;
                } else {
                    fType = EnumCollection.FEEDBACK_TYPE.valueOf(tempObj.get("feedback").getAsString().toUpperCase());
                }


                if(tempObj.get("feedbackUpdated").getAsString().toUpperCase().equals("NULL")){
                    fTypeUpdated = null;
                } else {
                    fTypeUpdated = EnumCollection.FEEDBACK_TYPE.valueOf(tempObj.get("feedbackUpdated").getAsString().toUpperCase());
                }

                Integer relValue = Integer.parseInt(tempObj.get("relationship").getAsString());
                EnumCollection.RELATIONSHIP_TYPE type = EnumCollection.RELATIONSHIP_TYPE.values()[relValue - 1];
                feedbackList.add(new FeedbackInfo(id, name, isCaller, type, fType, fTypeUpdated));
            }

            conn.disconnect();
            return feedbackList;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return null;// Returns true on success
    }


    private static List<CallInfo> getCallsInCurrentPlace(Integer userId){
        LinkedList<CallInfo> result = new LinkedList<>();
        System.out.println("Getting Calls in Current place!");
        try {
            //?action=getCallsInCurrentPlace&userId=U
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=getCallsInCurrentPlace&userId=" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String nextline;
            StringBuilder output = new StringBuilder();
            while((nextline = br.readLine()) != null){
                output.append(nextline);
            }

            Gson gson = new Gson();
            JsonElement element = gson.fromJson (output.toString().trim(), JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
            conn.disconnect();
            // Parse the json.
            JsonArray callsList = (JsonArray)jsonObj.get("calls");
            for(Object item:callsList){
                JsonObject currItem = (JsonObject)item;
                /**
                 * callId": "335",
                 "callerId": "1006",
                 "calleeId": "5031",
                 "reason": "casual",
                 "ringermode": "Silent",
                 "rationale": "ArgInFav(ringermode-IS-SILENT WHEN place-IS-HUNT AND noise-IS-2)  ArgInOpp(ringermode-IS-VIBRATE WHEN call_reason-IS-CASUAL OR Majority(expected_mode)-IS-SILENT)",
                 "feedbacks": [
                 * */
                Integer callId = Integer.parseInt(currItem.get("callId").getAsString());
                Integer callerId = Integer.parseInt(currItem.get("callerId").getAsString());
                Integer calleeId = Integer.parseInt(currItem.get("calleeId").getAsString());
                String reason = currItem.get("reason").getAsString().trim().toUpperCase();
                String ringerMode = currItem.get("ringermode").getAsString().trim();
                String rationale = currItem.get("rationale").getAsString().trim();
                LinkedList<FeedbackInfo> feedbacks = new LinkedList<>();
                JsonArray feedbackList = (JsonArray)currItem.get("feedbacks");
                for(int i = 0; i< feedbackList.size();i++){
                    boolean isCaller = false;
                    JsonObject tempObj = (JsonObject)feedbackList.get(i);

                    Integer id = Integer.parseInt(tempObj.get("id").getAsString());

                    String name = null;

                    EnumCollection.FEEDBACK_TYPE fType = null;
                    EnumCollection.FEEDBACK_TYPE fTypeUpdated = null;

                    if(tempObj.get("feedback").getAsString().toUpperCase().equals("NULL")){
                        fType = null;
                    } else {
                        fType = EnumCollection.FEEDBACK_TYPE.valueOf(tempObj.get("feedback").getAsString().toUpperCase());
                    }


                    if(tempObj.get("feedbackUpdated").getAsString().toUpperCase().equals("NULL")){
                        fTypeUpdated = null;
                    } else {
                        fTypeUpdated = EnumCollection.FEEDBACK_TYPE.valueOf(tempObj.get("feedbackUpdated").getAsString().toUpperCase());
                    }

                    Integer relValue = Integer.parseInt(tempObj.get("relationship").getAsString());
                    EnumCollection.RELATIONSHIP_TYPE type = EnumCollection.RELATIONSHIP_TYPE.values()[relValue - 1];
                    feedbacks.add(new FeedbackInfo(id, name, isCaller, type, fType, fTypeUpdated));
                }

                /**
                 *     Integer callId;
                 Integer callerId;
                 Integer calleeId;
                 EnumCollection.URGENCY_TYPE reason;
                 EnumCollection.RINGER_MODE ringerMode;
                 String rationale;
                 List<FeedbackInfo> feedbacks;*/

                result.add(new CallInfo(callId, callerId, calleeId, EnumCollection.URGENCY_TYPE.valueOf(reason),
                        EnumCollection.RINGER_MODE.valueOf(ringerMode), rationale, feedbacks));

            }
            return result;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    private static void giveFeedbacks(Integer callId, Integer userId, EnumCollection.FEEDBACK_TYPE feedback, EnumCollection.FEEDBACK_TYPE updatedFeedback){
        System.out.println("Going to Give Feedbacks.");
        try {
            //?action=giveFeedback&callId=C&userId=U&feedback=F1&feedbackUpdated=F2
            URL url = new URL("http://yangtze.csc.ncsu.edu:9090/csc555sd/services.jsp?action=giveFeedback&callId=" +
                    callId + "&userId=" + userId + "&feedback="+ feedback.toString() +"&feedbackUpdated=" + updatedFeedback.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            return;
        } catch (MalformedURLException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error is = " + e.getMessage());
            e.printStackTrace();
        }
        return;
    }

    private static JsonObject getLogs(int myId){
        //TODO: Not sure where to use this!
        return null;
    }



    // Need to implement the following TODO:
    // List Feedbacks	?action=listFeedbacks&callId=C
    // Place Logs	?action=getCallsInCurrentPlace&userId=U


    private static void simulateOneFlow(int myId, EnumCollection.RINGER_MODE myRinderMode, EnumCollection.RINGER_MODE myExpectedMode, String place){
            /*
            * -Flow-
        Enter place
        List neighbors
        Request call
        Response call
        ...
        exit place
            *
            * */
        // Enter the place with a phone with ringer mode myRingerMode and with expectation from neighbors myExpectedMode

        /*############################################################*/
        enterPlace(place, myId, myRinderMode, myExpectedMode);
        /*############################################################*/

        /*############################################################*/
        List<NeighborInfo> tempList = getNeighbors(myId);
        /*############################################################*/

        // Print the neighbors.
        System.out.println("The Neighbors Retrieved are ::");
        System.out.println(tempList);
        // Create a mapping of the users and IDs.
        HashMap<Integer, String> idNameMap = new HashMap<>();
        for (NeighborInfo item:tempList){
            idNameMap.put(item.id, item.name);
        }


        /*############################################################*/
        CallerInfo cInfo = requestCall(myId);
        /*############################################################*/

        System.out.println("The Caller's information below :: ");
        System.out.println(cInfo);



        idNameMap.put(cInfo.callerId, cInfo.name);
        // Predict the mode here! Get feedback
        StringBuilder rationalOut = new StringBuilder();
        EnumCollection.RINGER_MODE prediction = mRingerManagerCore.getRecommendedRingerMode(place, tempList, cInfo, rationalOut);
        // get the rationale.
        String rationaleOfPrediction = rationalOut.toString();

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("######################################################################");
        System.out.println("The Prediction made from the Social Benefit Function = " + prediction);
        System.out.println("######################################################################");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");



        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("######################################################################");
        System.out.println("The Rationale of Prediction = " + rationaleOfPrediction);
        System.out.println("######################################################################");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");







        /*############################################################*/
        JsonObject responseCallData = sendResponseCall(cInfo.callId, prediction, rationaleOfPrediction);
        /*############################################################*/


        // TODO : Useless responseCallData?




        /*############################################################*/
        List<FeedbackInfo> feedbackList = listFeedbacks(cInfo.callId, cInfo.callerId, idNameMap);
        /*############################################################*/


        System.out.println("The Feedbacks Received :: ");
        System.out.println(feedbackList);



        /*############################################################*/
        // Here we can go ahead and do some work on the Get Neugbors in place thing.
        List<CallInfo> callsInHere = getCallsInCurrentPlace(myId);
        /*############################################################*/


        // TODO : Analyse the Calls In Here, and first send feedback based on my expected mode and place.

        // TODO :  Give the second feedback after analysing the rationale.
        for(CallInfo item : callsInHere){
            EnumCollection.RINGER_MODE hisPreiction = item.ringerMode;
            EnumCollection.RINGER_MODE placeExpectaion = mRingerManagerCore.getPreferenceFromLocationName(place);
            //myExpectedMode
            EnumCollection.FEEDBACK_TYPE feedbackToSend = null;
            EnumCollection.FEEDBACK_TYPE updatedFeedbackToSend = null;
            // if both match, then POSITIVE, if at leats one match then neutral else positive
            if(hisPreiction.equals(placeExpectaion) && hisPreiction.equals(myExpectedMode)){
                // send POSITIVE
                feedbackToSend = EnumCollection.FEEDBACK_TYPE.POSITIVE;
            } else if(hisPreiction.equals(placeExpectaion) || hisPreiction.equals(myExpectedMode)){
                feedbackToSend = EnumCollection.FEEDBACK_TYPE.NEUTRAL;
            } else {
                feedbackToSend = EnumCollection.FEEDBACK_TYPE.NEGATIVE;
            }
            // SEND the first feedback.
            // TODO : Check the null updated feedback scene
            //giveFeedbacks(item.callId, item.calleeId, feedbackToSend, updatedFeedbackToSend);
            // Get updated feedback from the Rational Manager. Exported By Ringer manager Core.
            // SEND the updated feedback, only if my first feedback was negative.
            updatedFeedbackToSend = mRingerManagerCore.getUpdatedFeedbackFromRationale(item.rationale);
            giveFeedbacks(item.callId, item.calleeId, feedbackToSend, updatedFeedbackToSend);
        }





        /*############################################################*/
        exitPlace(myId);
        /*############################################################*/





        System.out.println("Processing Feedbacks, going to push to the system and recreate the system.");
        /*############################################################*/
        mRingerManagerCore.pushFeedback(mRingerManagerCore.synthesizeAttributeVector(place, tempList, cInfo), prediction, feedbackList);
        System.out.println("Feedbacks successfully pushed! The Decision trees are updated.");
        //TODO: When you see new Data, be sure to add it to your Database.
        /*############################################################*/
    }



    private static void simulateRandomFlow(int iterations){
        if (iterations > 100){
            System.out.println("please enter value less than 100, exiting.");
            System.exit(0);
        }
        LocationManager locaMgr = new LocationManager();
        while(iterations >= 0){
            System.out.println("The Iteration = " + iterations);
            simulateOneFlow(5031,EnumCollection.RINGER_MODE.getRandomEnum() ,
                    EnumCollection.RINGER_MODE.getRandomEnum(), locaMgr.getRandomPlacename());
            iterations--;
        }
    }


    public static void main(String[] args){
        char c;
        do{
            Scanner scan = new Scanner(System.in);
            System.out.println("Please Enter your's ID or type in 0 to Randomly Run once ! :: ");
            int id = scan.nextInt();
            if(id == 0){
                System.out.println("You have opted for randomization. How many runs do you want to simulate?(Max 1000)");
                scan.nextLine();
                int passes = scan.nextInt();
                simulateRandomFlow(passes);
                return;
            }
            System.out.println("You have Entered ID = " + id);

            scan.nextLine();
            System.out.println("Please Enter your's Current Ringer Mode. Use proper names as decided in the project description.(Loud, Vibrate, Silent) ");
            String currMode = scan.nextLine();
            System.out.println("You have Entered Current Mode as = " + currMode);



            System.out.println("Please Enter your's Expected Ringer Mode. Use proper names as decided in the project description.(Loud, Vibrate, Silent) ");
            String expMode = scan.nextLine();
            System.out.println("You have Expected Mode as = " + expMode);


            System.out.println("Please Enter the location you want to visit. Choose among (hunt, eb2, carmichael, oval, seminar, lab, meeting, party)");
            String location = scan.nextLine();
            System.out.println("You have location as = " + location);


            System.out.println("Going to Make a series of calls now.");
            simulateOneFlow(id, EnumCollection.RINGER_MODE.valueOf(currMode), EnumCollection.RINGER_MODE.valueOf(expMode), location);
            System.out.println("One Simulation Completed !");
            System.out.println("Please Press D + Enter to run again or any other key + Enter to exit.");
            c = scan.next().trim().charAt(0);
        } while(c == 'D');
    }
}
