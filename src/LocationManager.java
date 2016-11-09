import java.util.HashMap;

/**
 * Created by Himangshu on 10/21/16.
 * This class takes care of the Location Services. Currently the Noise level is fixed for one location.
 * Going forward it will be dynamic.
 */
public class LocationManager {
    private HashMap<String, Integer> locationToNoiseMap;
    private HashMap<String, EnumCollection.LOCATION_TYPE> locationToTypeMap;
    private HashMap<String, Integer> locationToBrightnessMap;
    public LocationManager(){
        //hunt, eb2, carmichael, oval, seminar, lab, meeting, party.
        locationToNoiseMap = new HashMap<>();
        locationToTypeMap = new HashMap<>();
        locationToBrightnessMap = new HashMap<>();



        locationToNoiseMap.put("hunt", 2);
        locationToBrightnessMap.put("hunt", 9);
        locationToTypeMap.put("hunt", EnumCollection.LOCATION_TYPE.LIBRARY);

        locationToNoiseMap.put("eb2",4);
        locationToBrightnessMap.put("eb2", 9);
        locationToTypeMap.put("eb2", EnumCollection.LOCATION_TYPE.CLASSROOM);

        locationToNoiseMap.put("carmichael", 7);
        locationToBrightnessMap.put("carmichael", 7);
        locationToTypeMap.put("carmichael", EnumCollection.LOCATION_TYPE.PARTY);

        locationToNoiseMap.put("oval", 8);
        locationToBrightnessMap.put("oval", 7);
        locationToTypeMap.put("oval", EnumCollection.LOCATION_TYPE.PARTY);

        locationToNoiseMap.put("seminar", 5);
        locationToBrightnessMap.put("seminar", 5);
        locationToTypeMap.put("seminar", EnumCollection.LOCATION_TYPE.MEETING);

        locationToNoiseMap.put("lab", 3);
        locationToBrightnessMap.put("lab", 8);
        locationToTypeMap.put("lab", EnumCollection.LOCATION_TYPE.LAB);

        locationToNoiseMap.put("meeting", 5);
        locationToBrightnessMap.put("meeting", 7);
        locationToTypeMap.put("meeting", EnumCollection.LOCATION_TYPE.MEETING);

        locationToNoiseMap.put("party", 10);
        locationToBrightnessMap.put("party", 3);
        locationToTypeMap.put("party", EnumCollection.LOCATION_TYPE.PARTY);
    }

    public void addLocation(String name, Integer noiseType, EnumCollection.LOCATION_TYPE locationType){
        locationToNoiseMap.put(name, noiseType);
        locationToTypeMap.put(name, locationType);
    }

    public Integer getNoiseLevelForLocation(String locKey){
        if(locationToNoiseMap.containsKey(locKey))
            return locationToNoiseMap.get(locKey);
        else
            return 5;//TODO: Decide the default things
    }



    public Integer getBrightnessLevelForLocation(String locKey){
        if(locationToBrightnessMap.containsKey(locKey))
            return locationToBrightnessMap.get(locKey);
        else
            return 5;//TODO: Decide the default things
    }



    public String getRandomPlacename(){
        String[] locs = new String[locationToNoiseMap.size()];
        int i = 0;
        for(String item:locationToNoiseMap.keySet()){
            locs[i] = item;
            i++;
        }
        return locs[(int) (Math.random() * locs.length)];
    }



    public EnumCollection.LOCATION_TYPE getLocationTypeForLocation(String locKey){
        if(locationToTypeMap.containsKey(locKey))
            return locationToTypeMap.get(locKey);
        else
            return EnumCollection.LOCATION_TYPE.OUTDOOR;
    }


}
