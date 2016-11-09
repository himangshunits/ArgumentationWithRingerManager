/**
 * Created by Himangshu on 10/21/16.
 */
public class NeighborInfo {
    public int id;
    public String name;
    public EnumCollection.RELATIONSHIP_TYPE type;
    public EnumCollection.STRENGTH_TYPE strength;
    public EnumCollection.RINGER_MODE currRingerMode;
    public EnumCollection.RINGER_MODE expectedRingerMode;

    public NeighborInfo(int id, String name, EnumCollection.RELATIONSHIP_TYPE type, EnumCollection.STRENGTH_TYPE strength, EnumCollection.RINGER_MODE currRingerMode, EnumCollection.RINGER_MODE expectedRingerMode) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.strength = strength;
        this.currRingerMode = currRingerMode;
        this.expectedRingerMode = expectedRingerMode;
    }

    @Override
    public String toString() {
        return "NeighborInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", strength=" + strength +
                ", currRingerMode=" + currRingerMode +
                ", expectedRingerMode=" + expectedRingerMode +
                '}';
    }
}
