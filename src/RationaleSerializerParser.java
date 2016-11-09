import java.util.Map;

/**
 * Created by Himangshu on 11/7/16.
 * This class handles the creation of the Rationale String Formation from the given rationales and also parses the incoming rationales
 * from other users.
 */
public class RationaleSerializerParser {

    // Connective can be AND or OR
    private static String serializeRationaleString(Map<ArgumentInfo, ArgumentInfo> causeToReasonMap, String connective){
        boolean first = true;
        StringBuilder result = new StringBuilder();
        for(Map.Entry item:causeToReasonMap.entrySet()){
            ArgumentInfo cause = (ArgumentInfo) item.getKey();
            ArgumentInfo reason = (ArgumentInfo) item.getValue();
            String argCause = "";
            String argReason = "";
            if (cause.predicate != null){
                argCause = cause.predicate + "(" + cause.contextKeyword + ")" + "-IS-" + cause.value;
            } else {
                argCause = cause.contextKeyword + "-IS-" + cause.value;
            }

            if (reason.predicate != null){
                argReason = reason.predicate + "(" + reason.contextKeyword + ")" + "-IS-" + reason.value;
            } else {
                argReason = reason.contextKeyword + "-IS-" + reason.value;
            }

            String causeReason = argCause + "+WHEN+" + argReason;
            if(first){
                result.append(causeReason);
                first = false;
            } else {
                result.append("+" + connective + "+" + argReason);
            }
        }
        return result.toString();
    }

    public static String getArgumentInFavor(Map<ArgumentInfo, ArgumentInfo> causeToReasonMap, String connective){
        return "ArgInFav(" + serializeRationaleString(causeToReasonMap, connective) + ")";
    }


    public static String getBothArguments(Map<ArgumentInfo, ArgumentInfo> causeToReasonMap1, String connective1,
                                          Map<ArgumentInfo, ArgumentInfo> causeToReasonMap2, String connective2){
        String result = "ArgInFav(" + serializeRationaleString(causeToReasonMap1, connective1) + ")" + "++" +
                "ArgInOpp(" + serializeRationaleString(causeToReasonMap2, connective2) + ")";
        return result;
    }

    public static RationaleInfo getRationaleStructFromString(String rationale) {
        // Parse the rationale and get the data.
        /**
         * ArgInFav(ringermode-IS-SILENT+WHEN+place-IS-HUNT+AND+noise-IS-2)
         * ++ArgInOpp(ringermode-IS-VIBRATE+WHEN+call_reason-IS-CASUAL+OR+Majority(expected_mode)-IS-SILENT)
         * We parse the Data and enter in the Final Structure Rational Info. Which has mainly Two Argument Infos.
         *  */
        // Split on "++" to get the Favor and Opp Arguments.
        String[] favorOppAgrs = rationale.split("\\++");
        String argInFavor = favorOppAgrs[0];
        String agrInOpp = favorOppAgrs[1];

        /**
         * List<ArgumentInfo> argsInFavor;
         EnumCollection.RINGER_MODE predictionInFavor;
         List<ArgumentInfo> argsInOpp;
         EnumCollection.RINGER_MODE predictionInOpp;
         String favorConnector;
         String oppConnector;
         * */


        return  null;





    }


    // Parser will look into the Rationale, and give the corresponding Data points to be considered by the RationaleManager.
    // RationaleManager will look the Data file, and replace it.





}
