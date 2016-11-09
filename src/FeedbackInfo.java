/**
 * Created by Himangshu on 10/23/16.
 */
public class FeedbackInfo {
    int userID;
    String userName;
    boolean isCaller;
    EnumCollection.RELATIONSHIP_TYPE relType;
    EnumCollection.FEEDBACK_TYPE feedback;
    EnumCollection.FEEDBACK_TYPE feedbackUpdated;

    public FeedbackInfo(int userID, String userName, boolean isCaller, EnumCollection.RELATIONSHIP_TYPE relType, EnumCollection.FEEDBACK_TYPE feedback, EnumCollection.FEEDBACK_TYPE feedbackUpdated) {
        this.userID = userID;
        this.userName = userName;
        this.isCaller = isCaller;
        this.relType = relType;
        this.feedback = feedback;
        this.feedbackUpdated = feedbackUpdated;
    }

    @Override
    public String toString() {
        return "FeedbackInfo{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", isCaller=" + isCaller +
                ", relType=" + relType +
                ", feedback=" + feedback +
                ", feedbackUpdated=" + feedbackUpdated +
                '}';
    }
}
