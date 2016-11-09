import java.util.List;

/**
 * Created by Himangshu on 11/9/16.
 */
public class RationaleInfo {
    List<ArgumentInfo> argsInFavor;
    EnumCollection.RINGER_MODE predictionInFavor;
    List<ArgumentInfo> argsInOpp;
    EnumCollection.RINGER_MODE predictionInOpp;
    String favorConnector;
    String oppConnector;

    public RationaleInfo(List<ArgumentInfo> argsInFavor, EnumCollection.RINGER_MODE predictionInFavor, List<ArgumentInfo> argsInOpp, EnumCollection.RINGER_MODE predictionInOpp, String favorConnector, String oppConnector) {
        this.argsInFavor = argsInFavor;
        this.predictionInFavor = predictionInFavor;
        this.argsInOpp = argsInOpp;
        this.predictionInOpp = predictionInOpp;
        this.favorConnector = favorConnector;
        this.oppConnector = oppConnector;
    }

    @Override
    public String toString() {
        return "RationaleInfo{" +
                "argsInFavor=" + argsInFavor +
                ", predictionInFavor=" + predictionInFavor +
                ", argsInOpp=" + argsInOpp +
                ", predictionInOpp=" + predictionInOpp +
                ", favorConnector='" + favorConnector + '\'' +
                ", oppConnector='" + oppConnector + '\'' +
                '}';
    }
}
