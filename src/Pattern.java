import java.util.ArrayList;
import java.util.List;

public class Pattern {

    private String name;
    private String[] pattern;

    public Pattern(String nazwa, String[] wzorzec) {
        this.name = nazwa;
        this.pattern = wzorzec;
    }

    //loads the example Pattern
    public static Pattern[] setPattern() {
        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern("Glider", new String[]{"0#0",
                "0##",
                "#0#"}));
        patterns.add(new Pattern("Glider", new String[]{"0#0",
                "0##",
                "#0#"}));
        patterns.add(new Pattern("Kok's Galaxy", new String[]{
                "##0######",
                "##0######",
                "##0000000",
                "##00000##",
                "##00000##",
                "##00000##",
                "0000000##",
                "######0##",
                "######0##"
        }));

        return patterns.toArray(new Pattern[patterns.size()]);
    }

    /**
     * class to handle patterns
     */

    public String getName() {
        return name;
    }

    void setName(String nazwa) {
        this.name = nazwa;
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setPattern(String[] wzorzec) {
        this.pattern = wzorzec;
    }

    public String toString() {
        return name;
    }
}