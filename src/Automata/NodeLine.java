package Automata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpaz on 2/21/17.
 */
public class NodeLine {
    public String Producer;
    public List<String> Production;
    public int dot;
    public List<String> F;

    public NodeLine(String producer) {
        Production = new ArrayList<>();
        F = new ArrayList<>();
        dot = 0;
        Producer = producer;
    }
}
