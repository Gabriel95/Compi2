package Automata;

import java.util.List;

/**
 * Created by jpaz on 2/27/17.
 */
public class GrammarLine {
    public final List<Label> labelList;
    public final List<String> javaCodeList;
    public String Producer;
    public List<String> Productions;

    public GrammarLine(String producer, List<String> productions, List<Label> labelList, List<String> javaCodeList) {
        Producer = producer;
        Productions = productions;
        this.labelList = labelList;
        this.javaCodeList = javaCodeList;
    }
}
