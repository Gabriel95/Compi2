package Automata;

import Semantic.Types.NonTerminal;
import Semantic.Types.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 2/21/17.
 */
public class AutomataNode {

    public String name;
    public List<NodeLine> lineCollection;
    public Map<String, AutomataNode> Displacements;
    public Map<String, AutomataNode> GoTos;


    public AutomataNode() {
        this.lineCollection = new ArrayList<>();
        this.Displacements = new HashMap<>();
        this.GoTos = new HashMap<>();
    }
}
