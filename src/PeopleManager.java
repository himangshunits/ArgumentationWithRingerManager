import java.util.HashMap;
import java.util.Map;

/**
 * Created by Himangshu on 10/22/16.
 * This Class keeps track of all the people in the database, and also gives us opportunities to add new ones.
 */
public class PeopleManager {
    // This varibale keeps the mapping of the people names and their corresponding relationships and rel strengths
    private Map<String, EnumCollection.RELATIONSHIP_TYPE> nameToRelTypeMap;
    private Map<String, EnumCollection.STRENGTH_TYPE> nameToStengthMap;

    public PeopleManager() {
        nameToRelTypeMap = new HashMap<>();
        nameToStengthMap = new HashMap<>();

        /*
        * Arwen (Family), Bilbo (Friend), Ceorl (Colleague), Denethor
        (Stranger), Elrond (Family), Faramir (Friend), Gandalf (Colleague), Hurin (Stranger),
         Isildur (Family), Legolas (Friend), Maggot (Colleague), Nazgul (Stranger), Orophin
        (Family), Radagast (Friend), Sauron (Colleague)
        * */

        nameToRelTypeMap.put("Arwen", EnumCollection.RELATIONSHIP_TYPE.FAMILY);
        nameToStengthMap.put("Arwen", EnumCollection.STRENGTH_TYPE.HIGH);

        nameToRelTypeMap.put("Bilbo", EnumCollection.RELATIONSHIP_TYPE.FRIEND);
        nameToStengthMap.put("Bilbo", EnumCollection.STRENGTH_TYPE.HIGH);

        nameToRelTypeMap.put("Ceorl", EnumCollection.RELATIONSHIP_TYPE.COLLEAGUE);
        nameToStengthMap.put("Ceorl", EnumCollection.STRENGTH_TYPE.MEDIUM);


        nameToRelTypeMap.put("Denethor", EnumCollection.RELATIONSHIP_TYPE.STRANGER);
        nameToStengthMap.put("Denethor", EnumCollection.STRENGTH_TYPE.LOW);


        nameToRelTypeMap.put("Elrond", EnumCollection.RELATIONSHIP_TYPE.FAMILY);
        nameToStengthMap.put("Elrond", EnumCollection.STRENGTH_TYPE.MEDIUM);


        nameToRelTypeMap.put("Faramir", EnumCollection.RELATIONSHIP_TYPE.FRIEND);
        nameToStengthMap.put("Faramir", EnumCollection.STRENGTH_TYPE.LOW);


        nameToRelTypeMap.put("Gandalf", EnumCollection.RELATIONSHIP_TYPE.COLLEAGUE);
        nameToStengthMap.put("Gandalf", EnumCollection.STRENGTH_TYPE.HIGH);


        nameToRelTypeMap.put("Hurin", EnumCollection.RELATIONSHIP_TYPE.STRANGER);
        nameToStengthMap.put("Hurin", EnumCollection.STRENGTH_TYPE.LOW);


        nameToRelTypeMap.put("Isildur", EnumCollection.RELATIONSHIP_TYPE.FAMILY);
        nameToStengthMap.put("Isildur", EnumCollection.STRENGTH_TYPE.HIGH);


        nameToRelTypeMap.put("Legolas", EnumCollection.RELATIONSHIP_TYPE.FRIEND);
        nameToStengthMap.put("Legolas", EnumCollection.STRENGTH_TYPE.MEDIUM);


        nameToRelTypeMap.put("Maggot", EnumCollection.RELATIONSHIP_TYPE.COLLEAGUE);
        nameToStengthMap.put("Maggot", EnumCollection.STRENGTH_TYPE.HIGH);


        nameToRelTypeMap.put("Nazgul", EnumCollection.RELATIONSHIP_TYPE.STRANGER);
        nameToStengthMap.put("Nazgul", EnumCollection.STRENGTH_TYPE.LOW);


        nameToRelTypeMap.put("Orophin", EnumCollection.RELATIONSHIP_TYPE.FAMILY);
        nameToStengthMap.put("Orophin", EnumCollection.STRENGTH_TYPE.HIGH);


        nameToRelTypeMap.put("Radagast", EnumCollection.RELATIONSHIP_TYPE.FRIEND);
        nameToStengthMap.put("", EnumCollection.STRENGTH_TYPE.LOW);


        nameToRelTypeMap.put("Sauron", EnumCollection.RELATIONSHIP_TYPE.COLLEAGUE);
        nameToStengthMap.put("Sauron", EnumCollection.STRENGTH_TYPE.MEDIUM);
    }


    public void addPerson(String name, EnumCollection.RELATIONSHIP_TYPE type, EnumCollection.STRENGTH_TYPE strength){
        nameToRelTypeMap.put(name, type);
        nameToStengthMap.put(name, strength);
    }


    public EnumCollection.RELATIONSHIP_TYPE getRelTypeForName(String name){
        if(nameToRelTypeMap.containsKey(name))
            return nameToRelTypeMap.get(name);
        else
            return EnumCollection.RELATIONSHIP_TYPE.FRIEND;
    }


    public EnumCollection.STRENGTH_TYPE getStrengthForName(String name){
        if(nameToStengthMap.containsKey(name))
            return nameToStengthMap.get(name);
        else
            return EnumCollection.STRENGTH_TYPE.HIGH;
    }
}
