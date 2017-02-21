package Automata;

import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Nodes.Statements.RootNode;
import Semantic.Types.NonTerminal;
import Semantic.Types.SymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 2/21/17.
 */
public class AutomataService {

    public static int name = 1;

    public static AutomataNode GetAutomata(List<ProductionNode> productionNodeList) throws Exception {
        List<ProductionNode> f = new ArrayList<>();
        f.addAll(productionNodeList);
        Map<String, List<String>> firstTable =  GrammarService.FirstTable(f);
        Map<String, List<List<String>>> grammarTable = ConvertionService.GrammarTable(productionNodeList);
        AutomataNode automataNode = CreateRootNode(productionNodeList,firstTable);
        GetNextNodes(automataNode,firstTable,grammarTable);
        return automataNode;
    }

    public static void GetNextNodes(AutomataNode automataNode, Map<String, List<String>> firstTable, Map<String, List<List<String>>> grammarTable) throws Exception {
        List<String> doneSymbols = new ArrayList<>();

        for(NodeLine nodeLine : automataNode.lineCollection)
        {
            if(nodeLine.dot < nodeLine.Production.size())
            {
                if(!doneSymbols.contains(nodeLine.Production.get(nodeLine.dot)))
                {
                    AutomataNode node = GoTo(nodeLine.Production.get(nodeLine.dot),automataNode.lineCollection, firstTable, grammarTable);
                    doneSymbols.add(nodeLine.Production.get(nodeLine.dot));
                }
            }
        }
    }

    private static AutomataNode GoTo(String s, List<NodeLine> lineCollection, Map<String, List<String>> firstTable, Map<String, List<List<String>>> grammarTable) throws Exception {
        AutomataNode automataNode = new AutomataNode();
        List<String> doneSymbols = new ArrayList<>();
        for(NodeLine nodeLine : lineCollection)
        {
            if(nodeLine.dot < nodeLine.Production.size())
            {
                if(nodeLine.Production.get(nodeLine.dot).equals(s))
                {
                    NodeLine node = new NodeLine(nodeLine.Producer);
                    node.Production.addAll(nodeLine.Production);
                    node.dot = nodeLine.dot + 1;
                    node.F.addAll(nodeLine.F);
                    automataNode.lineCollection.add(node);
                    if(node.dot < node.Production.size())
                    {
                        if(!doneSymbols.contains(node.Production.get(node.dot)))
                        {
                            if(SymbolTable.Instance().GetSymbolType(node.Production.get(node.dot)) instanceof NonTerminal)
                            {
                                automataNode.lineCollection.addAll(GetClosure(node,grammarTable,firstTable));
                                doneSymbols.add(node.Production.get(node.dot));
                            }
                        }
                    }
                }
            }
        }
        return automataNode;
    }

    public static AutomataNode CreateRootNode(List<ProductionNode> productionNodeList, Map<String, List<String>> firstTable) throws Exception
    {
        Map<String, List<List<String>>> grammarTable = ConvertionService.GrammarTable(productionNodeList);
        AutomataNode automataNode = new AutomataNode();
        automataNode.name = "IO";
        NodeLine nodeLine = new NodeLine("S_prime");
        nodeLine.F.add("$");
        nodeLine.Production.addAll(grammarTable.get("S_prime").get(0));
        automataNode.lineCollection.add(nodeLine);

        if(SymbolTable.Instance().GetSymbolType(nodeLine.Production.get(nodeLine.dot)) instanceof NonTerminal)
        {
            List<NodeLine> closure = GetClosure(nodeLine, grammarTable, firstTable);
            automataNode.lineCollection.addAll(closure);
        }

        return automataNode;
    }

    private static List<NodeLine> GetClosure(NodeLine nodeLine, Map<String, List<List<String>>> grammarTable, Map<String, List<String>> firstTable) throws Exception {
        List<NodeLine> closure = new ArrayList<>();
        String postDot = nodeLine.Production.get(nodeLine.dot);
        List<String> F = GetF(nodeLine,firstTable);
        List<List<String>> productions = grammarTable.get(postDot);

        for(List<String> production : productions)
        {
            NodeLine toAdd = new NodeLine(postDot);
            toAdd.Production.addAll(production);
            toAdd.F.addAll(F);
            closure.add(toAdd);
            if(SymbolTable.Instance().GetSymbolType(toAdd.Production.get(toAdd.dot)) instanceof NonTerminal
                    && !toAdd.Production.get(toAdd.dot).equals(postDot)){
                closure.addAll(GetClosure(toAdd,grammarTable,firstTable));
            }
        }
        return closure;
    }

    private static List<String> GetF(NodeLine nodeLine, Map<String, List<String>> firstTable) {
        List<String> F = new ArrayList<>();
        int dot = nodeLine.dot;
        if(dot + 1 == nodeLine.Production.size())
        {
            F.addAll(nodeLine.F);
        }
        else if(nodeLine.Production.get(dot + 1).equals("ɛ"))
        {
            F.addAll(nodeLine.F);
        }
        else
        {
            F.addAll(firstTable.get(nodeLine.Production.get(dot + 1)));
            F.addAll(nodeLine.F);
        }
        return F;
    }
}
