package Automata;

/**
 * Created by Gabriel Paz on 12-Mar-17.
 */
public class Label {
    public String Name;
    public String Type;
    public int position;

    public Label(String name, String type, int position) {
        Name = name;
        Type = type;
        this.position = position;
    }
}
