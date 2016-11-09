/**
 * Created by Himangshu on 10/23/16.
 */
public class AttibuteVectorInfo {
    EnumCollection.LOCATION_TYPE locationType;
    Integer noiseLevel;
    EnumCollection.RINGER_MODE neighborJudgement;
    EnumCollection.CALLER_EXPECATION callerExpectation;
    EnumCollection.URGENCY_TYPE urgency;
    Integer brightness;

    public AttibuteVectorInfo(EnumCollection.LOCATION_TYPE locationType, Integer noiseLevel, EnumCollection.RINGER_MODE neighborJudgement, EnumCollection.CALLER_EXPECATION callerExpectation, EnumCollection.URGENCY_TYPE urgency, Integer brightness) {
        this.locationType = locationType;
        this.noiseLevel = noiseLevel;
        this.neighborJudgement = neighborJudgement;
        this.callerExpectation = callerExpectation;
        this.urgency = urgency;
        this.brightness = brightness;
    }
}
