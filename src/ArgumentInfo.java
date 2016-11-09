/**
 * Created by Himangshu on 11/7/16.
 */
public class ArgumentInfo {
    String contextKeyword;
    String predicate;
    String value;

    public ArgumentInfo(String contextKeyword, String predicate, String value) {
        this.contextKeyword = contextKeyword;
        this.predicate = predicate;
        this.value = value.toUpperCase();
    }
}
