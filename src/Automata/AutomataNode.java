package Automata;

import Semantic.Types.NonTerminal;
import Semantic.Types.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpaz on 2/21/17.
 */
public class AutomataNode {

    public String name;
    public List<NodeLine> lineCollection;

    public AutomataNode() {
        this.lineCollection = new ArrayList<>();
    }
}
