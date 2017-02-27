package Automata;

import java.util.List;

/**
 * Created by jpaz on 2/27/17.
 */
public class GrammarLine {
    public String Producer;
    public List<String> Productions;

    public GrammarLine(String producer, List<String> productions) {
        Producer = producer;
        Productions = productions;
    }
}
