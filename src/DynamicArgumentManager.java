import java.util.HashMap;

/**
 * Created by Himangshu on 11/8/16.
 * This class is responsible for keeping track of the arguments.
 * It starts with a basic set of suggested values for different values.
 * Then it keep updating the internal maps based on the feedback received from the neighbors. Starts with the below values.
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
public class DynamicArgumentManager {
    private HashMap<EnumCollection.URGENCY_TYPE, EnumCollection.RINGER_MODE> mUrgencyMap;
    private HashMap<EnumCollection.CALLER_EXPECATION, EnumCollection.RINGER_MODE> mCallerExpMap;
    private HashMap<EnumCollection.LOCATION_TYPE, EnumCollection.RINGER_MODE> mLocationTypeMap;
    private HashMap<Integer, EnumCollection.RINGER_MODE> mNoiseLevelMap;
    private LocationManager mLocationManager;
    private Integer updateRequestCount;
    private static final Integer MAX_UPDATE_REQUEST = 10;

    public DynamicArgumentManager(LocationManager locMgr) {
        mLocationManager = locMgr;
        mUrgencyMap = new HashMap<>();
        mCallerExpMap = new HashMap<>();
        mLocationTypeMap = new HashMap<>();
        mNoiseLevelMap = new HashMap<>();
        updateRequestCount = 0;
        InitLocalData();
    }

    private void InitLocalData() {
        /** URGENCY.NONE -> Silent
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
         **/

        mUrgencyMap.put(EnumCollection.URGENCY_TYPE.NONE, EnumCollection.RINGER_MODE.Silent);
        mUrgencyMap.put(EnumCollection.URGENCY_TYPE.CASUAL, EnumCollection.RINGER_MODE.Vibrate);
        mUrgencyMap.put(EnumCollection.URGENCY_TYPE.URGENT, EnumCollection.RINGER_MODE.Loud);

        mCallerExpMap.put(EnumCollection.CALLER_EXPECATION.MUST_RECEIVE, EnumCollection.RINGER_MODE.Loud);
        mCallerExpMap.put(EnumCollection.CALLER_EXPECATION.SHOULD_RECEIVE, EnumCollection.RINGER_MODE.Vibrate);

        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.OUTDOOR, EnumCollection.RINGER_MODE.Loud);
        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.PARTY, EnumCollection.RINGER_MODE.Loud);
        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.LAB, EnumCollection.RINGER_MODE.Vibrate);
        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.CLASSROOM, EnumCollection.RINGER_MODE.Vibrate);
        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.LIBRARY, EnumCollection.RINGER_MODE.Silent);
        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.MEETING, EnumCollection.RINGER_MODE.Silent);
        mLocationTypeMap.put(EnumCollection.LOCATION_TYPE.HOSPITAL, EnumCollection.RINGER_MODE.Silent);


        mNoiseLevelMap.put(1, EnumCollection.RINGER_MODE.Silent);
        mNoiseLevelMap.put(2, EnumCollection.RINGER_MODE.Silent);
        mNoiseLevelMap.put(3, EnumCollection.RINGER_MODE.Silent);
        mNoiseLevelMap.put(4, EnumCollection.RINGER_MODE.Vibrate);
        mNoiseLevelMap.put(5, EnumCollection.RINGER_MODE.Vibrate);
        mNoiseLevelMap.put(6, EnumCollection.RINGER_MODE.Vibrate);
        mNoiseLevelMap.put(7, EnumCollection.RINGER_MODE.Loud);
        mNoiseLevelMap.put(8, EnumCollection.RINGER_MODE.Loud);
        mNoiseLevelMap.put(9, EnumCollection.RINGER_MODE.Loud);
        mNoiseLevelMap.put(10, EnumCollection.RINGER_MODE.Loud);
    }


    public EnumCollection.RINGER_MODE getPreferenceFromUrgency(EnumCollection.URGENCY_TYPE urgency){
        if(mUrgencyMap.containsKey(urgency))
            return mUrgencyMap.get(urgency);
        else
            return null;
    }

    public EnumCollection.RINGER_MODE getPreferenceFromCallerExpectation(EnumCollection.CALLER_EXPECATION exp){
        if(mCallerExpMap.containsKey(exp))
            return mCallerExpMap.get(exp);
        else
            return null;
    }

    public EnumCollection.RINGER_MODE getPreferenceFromLocationType(EnumCollection.LOCATION_TYPE locType){
        if(mLocationTypeMap.containsKey(locType))
            return mLocationTypeMap.get(locType);
        else
            return null;
    }

    public EnumCollection.RINGER_MODE getPreferenceFromNoiseLevel(Integer noiseLevel){
        if(mNoiseLevelMap.containsKey(noiseLevel))
            return mNoiseLevelMap.get(noiseLevel);
        else
            return null;
    }


    public void updateModelsFromRationaleFeedback(RationaleInfo rationaleStruct){
        // Use the parser class here to find out the exact arguments.
        // Update the corresponding maps accordingly.
        updateRequestCount++;
        if(updateRequestCount >= MAX_UPDATE_REQUEST){
            // Enough! Time for a change. Go ahead and replace the first argument's value.
            updateIthArgument(rationaleStruct, 1);
        } else if(updateRequestCount >= MAX_UPDATE_REQUEST * 2){
            // Replace all arguments.
            updateAllArguments(rationaleStruct);
        } else {
            System.out.println("Deciding not to update as of now!");
        }
    }

    private void updateAllArguments(RationaleInfo rationaleStruct) {
        for(int i = 1; i <= rationaleStruct.argsInFavor.size(); i++){
            updateIthArgument(rationaleStruct, i);
        }
    }

    private void updateIthArgument(RationaleInfo rationaleStruct, int i) {
        if(rationaleStruct.argsInFavor.size() < i){
            System.out.println("Un-parsable Rationale! Skipping");
            return;
        }
        ArgumentInfo arg = rationaleStruct.argsInFavor.get(i - 1);
        EnumCollection.RINGER_MODE pred = rationaleStruct.predictionInFavor;
        switch (arg.contextKeyword){
            case "place":
                mLocationTypeMap.replace(mLocationManager.getLocationTypeForLocation(arg.value.toLowerCase()),
                        mLocationTypeMap.get(mLocationManager.getLocationTypeForLocation(arg.value.toLowerCase())), pred );
                break;
            case "neighbor_relationship":
                break;
            case "expected_mode":
                if(arg.value.equals("Loud")){
                    mCallerExpMap.replace(EnumCollection.CALLER_EXPECATION.MUST_RECEIVE,
                            mCallerExpMap.get(EnumCollection.CALLER_EXPECATION.MUST_RECEIVE), pred);
                } else {
                    mCallerExpMap.replace(EnumCollection.CALLER_EXPECATION.SHOULD_RECEIVE,
                            mCallerExpMap.get(EnumCollection.CALLER_EXPECATION.SHOULD_RECEIVE), pred);
                }
                break;
            case "noise":
                Integer noiseLevelInt = Integer.parseInt(arg.value);
                mNoiseLevelMap.replace(noiseLevelInt, mNoiseLevelMap.get(noiseLevelInt), pred);
                break;
            case "brightness":
                // No Action.
                break;
            case "caller_relationship":
                break;
            case "call_reason":
                EnumCollection.URGENCY_TYPE urgType = EnumCollection.URGENCY_TYPE.valueOf(arg.value);
                mUrgencyMap.replace(urgType, mUrgencyMap.get(urgType), pred);
                break;
            default:
                System.out.println("Invalid Context Keyword! = " + arg.contextKeyword);
        }
    }
}
