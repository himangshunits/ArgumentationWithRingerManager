import java.util.List;

/**
 * Created by Himangshu on 11/9/16.
 */
public class CallInfo {
    /**
     * callId": "6",
     "callerId": "1009",
     "calleeId": "1",
     "reason": "urgent",
     "ringermode": "null",
     "rationale": "null",
     "feedbacks": []
     */
    Integer callId;
    Integer callerId;
    Integer calleeId;
    EnumCollection.URGENCY_TYPE reason;
    EnumCollection.RINGER_MODE ringerMode;
    String rationale;
    List<FeedbackInfo> feedbacks;

    public CallInfo(Integer callId, Integer callerId, Integer calleeId, EnumCollection.URGENCY_TYPE reason, EnumCollection.RINGER_MODE ringerMode, String rationale, List<FeedbackInfo> feedbacks) {
        this.callId = callId;
        this.callerId = callerId;
        this.calleeId = calleeId;
        this.reason = reason;
        this.ringerMode = ringerMode;
        this.rationale = rationale;
        this.feedbacks = feedbacks;
    }
}
