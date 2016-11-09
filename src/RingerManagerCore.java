import com.machine.learning.decisiontrees.DecisionTree;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Himangshu Ranjan Borah on 10/21/16.
 *
 * This is the core class of the ringer manager which will be responsible for creating and maintaing the models needed for implementing the
 * Social Benefit Function which will decide the suggested ringer mode give the inout data.
 * The possible outcomes of the public main API getRecommendedRingerMode() will output a value from {SILENT, VIBRATE, LOUD}
 * The inputs of the function are as follows.
 * 1. Location
 * 2. Noise Level
 * 3. Neighbors - Type, Strength, Current Ringer Mode, Expected Ringer Mode
 * 4. CallerInfo - ID, RelationShipType, RelationshipStrength
 * 5. Urgency of the call.
 *
 * The inputs will be further aggregated to finally form one vector with 5 attributes that are given above.
 * The decision tree maintained internally by this class will finally give us the required prediction.
 * The internal Decision tree will be updated from the driver program by oushing the feedbacks gathered from the external world.
 * The inital DecisioTree maintained in the system is derived from some ground level condition action rules defined in the file
 * "intial_rules.psv" which is a pipe separated format of the Dataset used for initial training.
 * When a ned data point comes in, then we check to see if the point exists in the train data, if yes then replace
 * Or else add it to the corpus of the train data and build the tree again.
 * We use a library called DecisonTree for the tree implementations https://github.com/mostafacs/DecisionTree
 * which is freely distributed.
 */
public class RingerManagerCore {
    //private LocationManager mLocationManagaer;
    private DecisionTree mDecisionTree;
    private RationaleManager mRationaleManager;
    private static LocationManager mLocationManagaer;
    private static String TRAIN_FILE_PATH = "data/initial_rules.psv";


    public RingerManagerCore(){
        //mLocationManagaer = new LocationManager();
        mDecisionTree = new DecisionTree();
        // Make the initial decision tree
        // Train your Decision Tree, will be update later
        mDecisionTree.train(new File(TRAIN_FILE_PATH));
        //System.out.println(tree.getRootNode());
        mLocationManagaer = new LocationManager();
        mRationaleManager = new RationaleManager(mDecisionTree);
    }


    /* Exported API for the Driver to use */
    public EnumCollection.RINGER_MODE getPreferenceFromLocationName(String locName){
        EnumCollection.LOCATION_TYPE locType = mLocationManagaer.getLocationTypeForLocation(locName);
        return mRationaleManager.getPreferenceFromLocationType(locType);
    }



    /* This API will help pushing the feedbacks, to update the mDecisionTree object*/
    public void pushFeedback(AttibuteVectorInfo vector, EnumCollection.RINGER_MODE lastMode, List<FeedbackInfo> currFeedback){
        // TODO_DONE : The Signature of FeedbackInfo has changed Drastically. Please handle NULL Values Too.
        // TODO_DONE : Consider the relationship type while incorporating the feedbacks!
        // TODO_DONE : Also, if it's the caller feedback then the handling should be different as compared to neighbors.

        // Check in the corpus what was the value of the Ringer mode for the curr vector and the last ringer mode predicted,
        // if combination not there, then add it.
        // Else replace the original one.
        // Aggregate the feedbacks from all the users
        int positiveFeedbackCount = 0;
        int neutralFeedBackCount = 0;
        int negativeFeedbackCount = 0;
        // Giving more weightage to Caller!
        EnumCollection.FEEDBACK_TYPE usefulFeedback;
        for(FeedbackInfo item:currFeedback){
            // Take the updated feedback as priority.
            if(item.feedbackUpdated != null){
                usefulFeedback = item.feedbackUpdated;
            } else if(item.feedback != null){
                usefulFeedback = item.feedback;
            } else {
                continue;
            }

            if(usefulFeedback.equals(EnumCollection.FEEDBACK_TYPE.POSITIVE)){
                if(item.isCaller){
                    positiveFeedbackCount += 2 * item.relType.getValue();
                } else {
                    positiveFeedbackCount += 1 * item.relType.getValue();
                }
            }

            if(usefulFeedback.equals(EnumCollection.FEEDBACK_TYPE.NEUTRAL)){
                if(item.isCaller){
                    neutralFeedBackCount += 2 * item.relType.getValue();
                } else {
                    neutralFeedBackCount += 1 * item.relType.getValue();
                }
            }

            if(usefulFeedback.equals(EnumCollection.FEEDBACK_TYPE.NEGATIVE)){
                if(item.isCaller){
                    negativeFeedbackCount += 2 * item.relType.getValue();
                } else {
                    negativeFeedbackCount += 1 * item.relType.getValue();
                }
            }
        }

        if(negativeFeedbackCount > positiveFeedbackCount + neutralFeedBackCount){
            // RED FLAG!!!!!!
            System.out.println("RED FLAG!!!! Going to update the Decision Tree.");
            System.out.println("Positive =  " + positiveFeedbackCount);
            System.out.println("Neutral = " + neutralFeedBackCount);
            System.out.println("Negative = " + negativeFeedbackCount);

            String attrVector = vector.locationType.name() + "|" + vector.noiseLevel.toString() + "|" + vector.brightness.toString() + "|" + vector.neighborJudgement.name()
                    + "|" + vector.callerExpectation.name() + "|" + vector.urgency.name();
            String lastPrediction = lastMode.name();
            String newPrediction;
            if(lastMode.equals(EnumCollection.RINGER_MODE.Loud)){
                newPrediction = EnumCollection.RINGER_MODE.Vibrate.name();
            } else if(lastMode.equals(EnumCollection.RINGER_MODE.Vibrate)){
                newPrediction = EnumCollection.RINGER_MODE.Silent.name();
            } else {
                newPrediction = lastMode.name();
            }

            //remove the original line if present
            try {
                removeLineFromFile(attrVector);
                // Add the new line to the file
                addToFileAndReTrain(attrVector, newPrediction);
            } catch (Exception e){
                System.out.println("Error in ATTR removal = " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Happy Feedback !! ");
            System.out.println("Positive =  " + positiveFeedbackCount);
            System.out.println("Neutral = " + neutralFeedBackCount);
            System.out.println("Negative = " + negativeFeedbackCount);
        }
        String[] feedbackLine = new String[3];
        feedbackLine[0] = String.valueOf(positiveFeedbackCount);
        feedbackLine[1] = String.valueOf(neutralFeedBackCount);
        feedbackLine[2] = String.valueOf(negativeFeedbackCount);
        try{
            //writeFeedbacksToFile(String.join(",", feedbackLine));
        } catch(Exception e) {
            System.out.println("Error in writing the Feedback = " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* This private API will aggregate the Caller Info and give us a caller's expectation in {MUST_RECEIVE, SHOULD_RECEIVE} */
    private EnumCollection.CALLER_EXPECATION getCallerExpectation(CallerInfo caller){
        EnumCollection.STRENGTH_TYPE strength = caller.strength;
        EnumCollection.RELATIONSHIP_TYPE relType = caller.type;
        // Get the integer valus of the types and then do an equal linear combination fo them.
        // Maximum of STRENGTH is 10, Max of REL type is 15, both minimums are 1
        // Aggregated min, with fraction 0.5 is 10 * 0.5 + 15 * 0.5 = 12.5
        // Aggregated min = 1 * 0.5 + 1 * 0.5 = 1
        // Dividing the range in 2, we have 1 - 6.75, 6.75 - 12.5 with SHOULD_RECEIVE, MUST_RECEIVE
        double alpha = 0.5; // This is the parameter which controls the divide between type and strength
        double combineStr = alpha * strength.getValue() + (1 - alpha) * relType.getValue();
        if(combineStr < 6.75) {
            return EnumCollection.CALLER_EXPECATION.SHOULD_RECEIVE;
        } else if(combineStr >= 6.75 && combineStr <= 12.5){
            return EnumCollection.CALLER_EXPECATION.MUST_RECEIVE;
        } else {
            System.out.println("getCallerExpectation : Never get here! The Agg value = " + combineStr);
            return null;
        }
    }


    /* This API will aggregate the average expectations of all the neighbors and also their relationships
     * Gives us a final judgement from the crowd in EnumCollection.RINGER_MODE */
    private EnumCollection.RINGER_MODE getAggregatedNeigborJudgement(List<NeighborInfo> neighborList){
        // Here first we do a majority voting for the ringer modes of the neighbors and the expected modes and take the highest voted.
        // Here we have N different neighbors, so we must normalise the values by N, so that the maximum range is fixed.
        // Max values of TYPE, STRENGTH, MODE, EXPE_MODE : 15, 10, 10, 10 and Min Values are 1,1,1,1
        // When we normalise by the size, then the max and the mins will remain in the range. No problem.
        // There are 4 different attributes, so we must give equal importance to all of them by a product with 0.25
        // Max value is 15 * 0.25 + 10 * 0.25 + 10 * 0.25 + 10 * 0.25 = 11.25; Min is = 1
        // So we divide the range into three to give the crowd's decision : 1 - 3.4, 3.4 - 7.8, 7.8 - 12.25
        double alpha = 0.25;
        int N = neighborList.size();
        double totalTypeRank = 0;
        double totalStrengthRank = 0;
        double totalCurrModeRank = 0;
        double totalExpectedModeRank = 0;
        for (NeighborInfo item: neighborList){
            totalTypeRank += item.type.getValue();
            totalStrengthRank += item.strength.getValue();
            totalCurrModeRank += item.currRingerMode.getValue();
            totalExpectedModeRank += item.expectedRingerMode.getValue();
        }

        // Normalize the ranks.
        totalTypeRank /= N;
        totalStrengthRank /= N;
        totalCurrModeRank /= N;
        totalExpectedModeRank /= N;

        // Find aggregated Rank
        double aggRank = alpha * totalTypeRank + alpha * totalStrengthRank + alpha * totalCurrModeRank + alpha * totalExpectedModeRank;
        if(aggRank < 3.4){
            return EnumCollection.RINGER_MODE.Silent;
        } else if (aggRank >= 3.4 && aggRank < 7.8) {
            return EnumCollection.RINGER_MODE.Vibrate;
        } else if (aggRank >= 7.8 && aggRank <= 11.25){
            return EnumCollection.RINGER_MODE.Loud;
        } else {
            System.out.println("getAggregatedNeigborJudgement :: Never Get here! The Agg value = " + aggRank);
            return null;
        }
    }



    /* This function synthesizes the Attribute Vector */
    public AttibuteVectorInfo synthesizeAttributeVector(String location, List<NeighborInfo> neighborList, CallerInfo call){
        EnumCollection.LOCATION_TYPE locationType = mLocationManagaer.getLocationTypeForLocation(location);
        Integer noiseLevel = mLocationManagaer.getNoiseLevelForLocation(location);
        EnumCollection.RINGER_MODE neighborJudgement = getAggregatedNeigborJudgement(neighborList);
        EnumCollection.CALLER_EXPECATION callerExp = getCallerExpectation(call);
        EnumCollection.URGENCY_TYPE urg = call.urgency;
        Integer brightness = mLocationManagaer.getBrightnessLevelForLocation(location);;//TODO : make it Dynamic
        return new AttibuteVectorInfo(locationType,noiseLevel,neighborJudgement, callerExp,urg, brightness);
    }



    /* This is the main public API , the so called SOCIAL BENEFIT FUNCTION which will determine the final ringer mode for a particular set of data of one call.
     * It takes all the parameters, aggregates some of those and finally forms the 5 main variables on which our main decision tree is based on.
      * The 5 attributes it will finally form are LOCATION_TYPE, NOISE LEVEL, NEIGHBOR'S JUDGEMENT, CALLER'S EXPECTATION, URGENCY */
    public EnumCollection.RINGER_MODE getRecommendedRingerMode(String location, List<NeighborInfo> neighborList, CallerInfo call, StringBuilder rationaleOut){
        /* MAke the vector, and the the return the prediction. */
        AttibuteVectorInfo vector = synthesizeAttributeVector(location, neighborList, call);
        // make the test attribue from the vector! use whatever you need as of now. No support for the Brightness in the decision tree corpus.
        //HOSPITAL|N8|Loud|SHOULD_RECEIVE|NONE|
        String testData = vector.locationType.name() + "|" + vector.noiseLevel.toString() + "|" + vector.brightness.toString() + "|" + vector.neighborJudgement.name()
                + "|" + vector.callerExpectation.name() + "|" + vector.urgency.name();
        String newReco = mDecisionTree.classify(testData);
        if(newReco.equals("Cann't Find Class -- Please Learn Tree with more examples")) {
            System.out.println("Flag! Not present in the initial training set, adding now and rebuilding the Decision Trees!");

            // Still check once and remove if present, to be safe!
            try{
                removeLineFromFile(testData);
            } catch(Exception e){
                System.out.println("Error is file writing = " + e.getMessage());
                e.printStackTrace();
            }

            addToFileAndReTrain(testData, vector.neighborJudgement.name());
            // Now send the Curr neighbor Judgement as Fail Proof
            newReco = vector.neighborJudgement.name();
            System.out.println("Models Updated with new Data, sending new recommendations.");

            // Set the rationale.
            rationaleOut.append(mRationaleManager.sythesizeMostProbableCauses(vector, location, EnumCollection.RINGER_MODE.valueOf(newReco)));
            return EnumCollection.RINGER_MODE.valueOf(newReco);
        }else {
            // Set the rationale.
            rationaleOut.append(mRationaleManager.sythesizeMostProbableCauses(vector, location, EnumCollection.RINGER_MODE.valueOf(newReco)));
            return EnumCollection.RINGER_MODE.valueOf(newReco);
        }
    }

    private void addToFileAndReTrain(String testData, String classname){
        String lineToAdd = testData + "|" + classname;
        System.out.println("Going to add = " + lineToAdd);
        try{
            BufferedWriter output = new BufferedWriter(new FileWriter(TRAIN_FILE_PATH, true));
            output.newLine();
            output.write(lineToAdd);
            output.close();
            mDecisionTree = null;
            mDecisionTree = new DecisionTree();
            mDecisionTree.train(new File(TRAIN_FILE_PATH));
        } catch(Exception e){
            System.out.println("Error in file write = " + e.getMessage());
            e.printStackTrace();
        }
    }




    private boolean removeLineFromFile(String linePart) throws IOException{
        System.out.println("Searching for Line Part to remove = " + linePart);
        File inputFile = new File(TRAIN_FILE_PATH);
        File tempFile = new File("myTempFile.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        boolean first = true;

        while((currentLine = reader.readLine()) != null) {
            String trimmedLine = currentLine.trim();
            if(trimmedLine.contains(linePart)) continue;
            if (first){
                first = false;
            } else {
                writer.write(System.getProperty("line.separator"));
            }
            writer.write(currentLine);
        }
        writer.close();
        reader.close();
        boolean successful = tempFile.renameTo(inputFile);
        return successful;
    }

    private void writeFeedbacksToFile(String posNeuNeg) throws IOException{
        File tempFile = new File("feedbackHistory.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true));
        String trimmedLine = posNeuNeg.trim();
        writer.write(trimmedLine);
        writer.write(System.getProperty("line.separator"));
        writer.close();
    }

    public EnumCollection.FEEDBACK_TYPE getUpdatedFeedbackFromRationale(String rationale) {
        return mRationaleManager.getUpdatedFeedbackFromRationale(rationale);
    }
}
