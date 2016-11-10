import com.machine.learning.decisiontrees.DecisionTree;

import java.util.LinkedHashMap;

/**
 * Created by Himangshu on 11/8/16.
 * This Class will take the current prediction and check to see why that decision was made.
 * Then depending on some predefined importance order of the attributes, it decides who was the main player in the decision process.
 * The importance orders used currently are as follows.
 * LOCATION_TYPE|NOISE_LEVEL|BRIGHTNESS_LEVEL|NEIGHBOR_JUDGEMENT|CALLER_EXPECTATION|URGENCY|CLASS
 * URGENCY > CALLER_EXPECTATION > LOCATION_TYPE > NEIGHBOR_JUDGEMENT > NOISE_LEVEL > BRIGHTNESS_LEVEL
 * The following are the suggested decisions for every possible values of the Attributes.
 * URGENCY.NONE -> Silent
 * URGENCY.CASUAL -> Vibrate
 * URGENCY.URGENT -> Loud
 * CALLER_EXP.MUST_RCV -> Loud
 * CALLER_EXP.SHOULD_RCV -> Vibrate
 * LOCATION_TYPE(OUTDOOR, PARTY) -> Loud
 * LOCATION_TYPE(LAB, CLASSROOM) -> Vibrate
 * LOCATION_TYPE(LIBRARY, MEETING, HOSPITAL) -> Silent
 * NEIGHBOR_JUDGEMENT -> Value itself is the aggregated suggestion.
 * NOISE_LEVEL.10 - 7 -> Loud
 * NOISE_LEVEL.6 - 4 -> Vibrate
 * NOISE_LEVEL.3 - 1 -> Silent
 * BRIGHTNESS_LEVEL -> Don't consider for Rationale!
 */
public class RationaleManager {
    private DecisionTree mTreeInstance;
    private DynamicArgumentManager mArgumentManager;
    private LocationManager mLocationManager;

    public RationaleManager(DecisionTree mTreeInstance, LocationManager locMgr) {
        this.mTreeInstance = mTreeInstance;
        this.mLocationManager = locMgr;
        this.mArgumentManager = new DynamicArgumentManager(mLocationManager);
    }

    // TODO : The below 5 functions are now static, but that has to change. Going forward we will have an argument manager cass, which ill be owned by this class
    // That class will keep the information on what the ringer mode should be when one value for a field is given.
    // That class's instances will get updated when a new rationale comes into the system, depending on the credibility of the rationale.

    private EnumCollection.RINGER_MODE getPreferenceFromUrgency(EnumCollection.URGENCY_TYPE urgency){
        return mArgumentManager.getPreferenceFromUrgency(urgency);
    }


    private EnumCollection.RINGER_MODE getPreferenceFromCallerExpectation(EnumCollection.CALLER_EXPECATION exp){
        return mArgumentManager.getPreferenceFromCallerExpectation(exp);
    }


    public EnumCollection.RINGER_MODE getPreferenceFromLocationType(EnumCollection.LOCATION_TYPE locType){
        return mArgumentManager.getPreferenceFromLocationType(locType);
    }


    private EnumCollection.RINGER_MODE getPreferenceFromNoiseLevel(Integer noiseLevel){
        return mArgumentManager.getPreferenceFromNoiseLevel(noiseLevel);
    }


    // This api analyses the input Data point and see which of the values of the fields match with the final prediction.
    // Returns a Rationale String
    public String sythesizeMostProbableCauses(AttibuteVectorInfo dataPoint, String location, EnumCollection.RINGER_MODE prediction){
        // URGENCY > CALLER_EXPECTATION > LOCATION_TYPE > NEIGHBOR_JUDGEMENT > NOISE_LEVEL > BRIGHTNESS_LEVEL
        // Find out which of the attributes are matching with the final prediction.
        boolean isUrgencyMatch = getPreferenceFromUrgency(dataPoint.urgency).equals(prediction);
        boolean isCallerExpMatch = getPreferenceFromCallerExpectation(dataPoint.callerExpectation).equals(prediction);
        boolean isLocTypeMatch = getPreferenceFromLocationType(dataPoint.locationType).equals(prediction);
        boolean isneighborJudgementMatch = dataPoint.neighborJudgement.equals(prediction);
        boolean isNoiseLevelMatch = getPreferenceFromNoiseLevel(dataPoint.noiseLevel).equals(prediction);

        // Now we will add some static rules for deciding the most probable thing.
        // Divide the booleans above ito 2 groups, FOR and AGAINST

        // Rule 1 : If Urgency Matched and caller expectation matched, then don't even bother about others!


        // Rule 2: If one of the top 2 guys match, then look for a match from the next three, if there's one,
        // then use that along with the first one and give a positive argument.

        // If all of above are false, then red alert!
        if(!isUrgencyMatch && !isCallerExpMatch && !isLocTypeMatch && !isneighborJudgementMatch && !isNoiseLevelMatch){
            System.out.println("ABORT! Decision Trees Corrupted by bad feedbacks from neigbors! Please clean the initial_rules.psv manually. Abort!");
            System.exit(-1);
        }


        // Else: Should not reach here normally, still if does, then add all according to the favor or opposition.
        if(isUrgencyMatch || isCallerExpMatch){
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("call_reason", null, dataPoint.urgency.name()));
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("expected_mode", "Majority", prediction.name()));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "OR");
            return tempRationale;
        } else if(isUrgencyMatch && isLocTypeMatch){
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("call_reason", null, dataPoint.urgency.name()));
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("place", null, location));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "AND");
            return tempRationale;
        } else if(isUrgencyMatch && isneighborJudgementMatch) {
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("call_reason", null, dataPoint.urgency.name()));
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("expected_mode", "Majority", prediction.name()));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "AND");
            return tempRationale;
        } else if(isUrgencyMatch && isNoiseLevelMatch){
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("call_reason", null, dataPoint.urgency.name()));
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("noise", null, dataPoint.noiseLevel.toString()));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "AND");
            return tempRationale;
        } else if(isCallerExpMatch && isLocTypeMatch){
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("expected_mode", "Majority", prediction.name()));
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("place", null, location));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "AND");
            return tempRationale;
        } else if(isCallerExpMatch && isneighborJudgementMatch) {
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("expected_mode", "Majority", prediction.name()));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "AND");
            return tempRationale;
        } else if(isCallerExpMatch && isNoiseLevelMatch){
            LinkedHashMap<ArgumentInfo, ArgumentInfo> map = new LinkedHashMap<>();
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("expected_mode", "Majority", prediction.name()));
            map.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("noise", null, dataPoint.noiseLevel.toString()));
            String tempRationale = RationaleSerializerParser.getArgumentInFavor(map, "AND");
            return tempRationale;
        } else {
            // Send all
            LinkedHashMap<ArgumentInfo, ArgumentInfo> mapOpp = new LinkedHashMap<>();
            LinkedHashMap<ArgumentInfo, ArgumentInfo> mapFavor = new LinkedHashMap<>();

            // First two obviously on opposition.
            mapOpp.put(new ArgumentInfo("ringermode", null, getPreferenceFromUrgency(dataPoint.urgency).name()), new ArgumentInfo("call_reason", null, dataPoint.urgency.name()));
            mapOpp.put(new ArgumentInfo("ringermode", null, getPreferenceFromCallerExpectation(dataPoint.callerExpectation).name()), new ArgumentInfo("expected_mode", "AtleastOne", prediction.name()));


            // URGENCY > CALLER_EXPECTATION > LOCATION_TYPE > NEIGHBOR_JUDGEMENT > NOISE_LEVEL > BRIGHTNESS_LEVEL
            if(isLocTypeMatch){
                mapFavor.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("place", null, location));
            } else {
                mapOpp.put(new ArgumentInfo("ringermode", null, getPreferenceFromLocationType(dataPoint.locationType).name()), new ArgumentInfo("place", null, location));
            }

            if(isneighborJudgementMatch){
                mapFavor.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("expected_mode", "Majority", prediction.name()));
            }

            if(isNoiseLevelMatch){
                mapFavor.put(new ArgumentInfo("ringermode", null, prediction.name()), new ArgumentInfo("noise", null, dataPoint.noiseLevel.toString()));
            } else {
                mapOpp.put(new ArgumentInfo("ringermode", null, getPreferenceFromNoiseLevel(dataPoint.noiseLevel).name()), new ArgumentInfo("noise", null, dataPoint.noiseLevel.toString()));
            }


            String tempRationale = RationaleSerializerParser.getBothArguments(mapFavor, "AND", mapOpp, "OR");
            return tempRationale;
        }
    }


    // Checks if the argument matches with your belief
    private boolean doesArgumentMatch(ArgumentInfo arg, EnumCollection.RINGER_MODE prediction){
        boolean result = false;
        switch (arg.contextKeyword){
            case "place":
                result = getPreferenceFromLocationType(mLocationManager.getLocationTypeForLocation(arg.value)).equals(prediction);
                break;
            case "neighbor_relationship":
                if(arg.predicate.equals("Majority")){
                    if(arg.value.equals("1")){
                        result = prediction.equals(EnumCollection.RINGER_MODE.Loud);
                    } else if(arg.value.equals("2") || arg.value.equals("3")){
                        result = prediction.equals(EnumCollection.RINGER_MODE.Vibrate);
                    } else {
                        result = prediction.equals(EnumCollection.RINGER_MODE.Silent);
                    }
                }
                break;
            case "expected_mode":
                if(arg.value.equals("Silent") || arg.value.equals("Vibrate")){
                    result = getPreferenceFromCallerExpectation(EnumCollection.CALLER_EXPECATION.SHOULD_RECEIVE).equals(prediction);

                } else {
                    result = getPreferenceFromCallerExpectation(EnumCollection.CALLER_EXPECATION.MUST_RECEIVE).equals(prediction);
                }
                break;
            case "noise":
                result = getPreferenceFromNoiseLevel(Integer.parseInt(arg.value)).equals(prediction);
                break;
            case "brightness":
                break;
            case "caller_relationship":
                if(arg.predicate.equals("Majority")){
                    if(arg.value.equals("1")){
                        result = prediction.equals(EnumCollection.RINGER_MODE.Loud);
                    } else if(arg.value.equals("2") || arg.value.equals("3")){
                        result = prediction.equals(EnumCollection.RINGER_MODE.Vibrate);
                    } else {
                        result = prediction.equals(EnumCollection.RINGER_MODE.Silent);
                    }
                }
                break;
            case "call_reason":
                result = getPreferenceFromUrgency(EnumCollection.URGENCY_TYPE.valueOf(arg.value)).equals(prediction);
                break;
            default:
                System.out.println("Invalid Context Keyword! = " + arg.contextKeyword);
        }
        return result;
    }

    // Get Updated feedbacks from a new Rationale.
    // Analyze one given Rationale and give a feedback on it, depending on your beleifs.
    public EnumCollection.FEEDBACK_TYPE getUpdatedFeedbackFromRationale(String rationale){
        RationaleInfo rationaleStruct = RationaleSerializerParser.getRationaleStructFromString(rationale);
        Integer noOfPositiveMatches = 0;
        Integer noOfNegativeMismatches = 0;
        for(ArgumentInfo item : rationaleStruct.argsInFavor){
            noOfPositiveMatches += (doesArgumentMatch(item, rationaleStruct.predictionInFavor)?1:0);
        }

        for(ArgumentInfo item: rationaleStruct.argsInOpp){
            noOfNegativeMismatches += (doesArgumentMatch(item, rationaleStruct.predictionInOpp)? 0:1);
        }


        if(noOfPositiveMatches > 0)
            return EnumCollection.FEEDBACK_TYPE.POSITIVE;
        else if (noOfNegativeMismatches > 0)
            return EnumCollection.FEEDBACK_TYPE.NEUTRAL;
        else {
            // This is the place where we need to learn!
            // TODO : Learn from the new rationales received from other people.
            mArgumentManager.updateModelsFromRationaleFeedback(rationaleStruct);
            return EnumCollection.FEEDBACK_TYPE.NEUTRAL;
        }
    }



}
