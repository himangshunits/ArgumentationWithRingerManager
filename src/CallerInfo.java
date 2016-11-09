/**
 * Created by Himangshu on 10/21/16.
 */
public class CallerInfo {
    public int callId;
    public int callerId;
    public String name;
    public EnumCollection.RELATIONSHIP_TYPE type;
    public EnumCollection.STRENGTH_TYPE strength;
    public EnumCollection.URGENCY_TYPE urgency;

    public CallerInfo(int callId, int callerId, String name, EnumCollection.RELATIONSHIP_TYPE type, EnumCollection.STRENGTH_TYPE strength, EnumCollection.URGENCY_TYPE urgency) {
        this.callId = callId;
        this.callerId = callerId;
        this.name = name;
        this.type = type;
        this.strength = strength;
        this.urgency = urgency;
    }

    @Override
    public String toString() {
        return "CallerInfo{" +
                "callId=" + callId +
                ", callerId=" + callerId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", strength=" + strength +
                ", urgency=" + urgency +
                '}';
    }
}
