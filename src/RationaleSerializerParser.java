import com.sun.deploy.util.StringUtils;

import java.util.LinkedList;
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


    private static String WordCapitalizer(String name){
        name = name.toUpperCase();
        StringBuilder result = new StringBuilder();
        result.append(name.charAt(0));
        result.append(name.substring(1).toLowerCase());
        return result.toString();
    }


    public static RationaleInfo getRationaleStructFromString(String rationale) {
        // Parse the rationale and get the data.
        /**
         * ArgInFav(ringermode-IS-SILENT+WHEN+place-IS-HUNT+AND+noise-IS-2)
         * ++ArgInOpp(ringermode-IS-VIBRATE+WHEN+call_reason-IS-CASUAL+OR+Majority(expected_mode)-IS-SILENT)
         * We parse the Data and enter in the Final Structure Rational Info. Which has mainly Two Argument Infos.
         *  */
        // Split on "++" to get the Favor and Opp Arguments.
        String[] favorOppAgrs = rationale.split("\\+\\+");
        String argInFavor = favorOppAgrs[0].substring(9, favorOppAgrs[0].length() - 1);
        String argInOpp = favorOppAgrs[1].substring(9, favorOppAgrs[1].length() - 1);

        /**
         * List<ArgumentInfo> argsInFavor;
         EnumCollection.RINGER_MODE predictionInFavor;
         List<ArgumentInfo> argsInOpp;
         EnumCollection.RINGER_MODE predictionInOpp;
         String favorConnector;
         String oppConnector;
         * */
        //ArgInFav(ringermode-IS-SILENT+WHEN+place-IS-HUNT+AND+noise-IS-2)

        EnumCollection.RINGER_MODE predictionInFavor = EnumCollection.RINGER_MODE.valueOf(WordCapitalizer(argInFavor.split("\\+")[0].split("\\-")[2]));
        String favorConnector = null;
        LinkedList<ArgumentInfo> argsInnFavorList = new LinkedList<>();
        argInFavor = argInFavor.split("\\+WHEN\\+")[1];
        if(argInFavor.contains("AND") && !argInFavor.contains("OR")){
            favorConnector = "AND";
            String[] splitArgs = argInFavor.split("\\+AND\\+");
            for(String item:splitArgs){
                if(item.contains("(")){
                    // With predicate
                    String[] data = item.split("-");
                    // extract predicate from data[0]
                    argsInnFavorList.add(new ArgumentInfo(data[0].split("\\(")[1].substring(0, data[0].split("\\(")[1].length() - 1),
                            data[0].split("\\(")[0], data[2]));
                } else {
                    String[] data = item.split("-");
                    argsInnFavorList.add(new ArgumentInfo(data[0], null, data[2]));
                }
            }
        } else if (!argInFavor.contains("AND") && argInFavor.contains("OR")){
            favorConnector = "OR";
            String[] splitArgs = argInFavor.split("\\+OR\\+");
            for(String item:splitArgs){
                if(item.contains("(")){
                    // With predicate
                    String[] data = item.split("-");
                    // extract predicate from data[0]
                    argsInnFavorList.add(new ArgumentInfo(data[0].split("\\(")[1].substring(0, data[0].split("\\(")[1].length() - 1),
                            data[0].split("\\(")[0], data[2]));
                } else {
                    String[] data = item.split("-");
                    argsInnFavorList.add(new ArgumentInfo(data[0], null, data[2]));
                }
            }

        } else {
            System.out.println("Contains Both AND and OR in Argument! Not Supported");
        }


        // Process ArgsInOpp

        EnumCollection.RINGER_MODE predictionInOpp = EnumCollection.RINGER_MODE.valueOf(WordCapitalizer(argInOpp.split("\\+")[0].split("\\-")[2]));
        String oppConnector = null;
        LinkedList<ArgumentInfo> argsInnOppList = new LinkedList<>();
        argInOpp = argInOpp.split("\\+WHEN\\+")[1];
        if(argInOpp.contains("AND") && !argInOpp.contains("OR")){
            oppConnector = "AND";
            String[] splitArgs = argInOpp.split("\\+AND\\+");
            for(String item:splitArgs){
                if(item.contains("(")){
                    // With predicate
                    String[] data = item.split("-");
                    // extract predicate from data[0]
                    argsInnOppList.add(new ArgumentInfo(data[0].split("\\(")[1].substring(0, data[0].split("\\(")[1].length() - 1),
                            data[0].split("\\(")[0], data[2]));
                } else {
                    String[] data = item.split("-");
                    argsInnOppList.add(new ArgumentInfo(data[0], null, data[2]));
                }
            }
        } else if (!argInOpp.contains("AND") && argInOpp.contains("OR")){
            oppConnector = "OR";
            String[] splitArgs = argInOpp.split("\\+OR\\+");
            for(String item:splitArgs){
                if(item.contains("(")){
                    // With predicate
                    String[] data = item.split("-");
                    // extract predicate from data[0]
                    argsInnOppList.add(new ArgumentInfo(data[0].split("\\(")[1].substring(0, data[0].split("\\(")[1].length() - 1),
                            data[0].split("\\(")[0], data[2]));
                } else {
                    String[] data = item.split("-");
                    argsInnOppList.add(new ArgumentInfo(data[0], null, data[2]));
                }
            }
        } else {
            System.out.println("Contains Both AND and OR in Argument! Not Supported");
        }

        RationaleInfo result = new RationaleInfo(argsInnFavorList, predictionInFavor,argsInnOppList, predictionInOpp, favorConnector, oppConnector);
        return result;
    }


    // Parser will look into the Rationale, and give the corresponding Data points to be considered by the RationaleManager.
    // RationaleManager will look the Data file, and replace it.





}
