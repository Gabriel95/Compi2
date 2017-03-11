package Automata;

import Semantic.Nodes.Expression.RhsNode;
import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Types.NonTerminal;
import Semantic.Types.SymbolTable;
import Semantic.Types.Terminal;

import java.util.*;

/**
 * Created by jpaz on 2/21/17.
 */
public class AutomataService {

    private static int name = 1;
    private static List<AutomataNode> aNodeList = new ArrayList<>();
    private static Map<String,List<String>> allLookUps = new HashMap<>();
    private static int dynamicSize = 1;

    public static List<AutomataNode> GetAutomata(List<ProductionNode> productionNodeList) throws Exception
    {
        List<ProductionNode> f = new ArrayList<>();
        f.addAll(productionNodeList);
        Map<String, List<RhsNode>> rhsGrammarTable =  GrammarService.GetGrammarTable(f);
        Map<String, List<List<String>>> grammarTable = ConvertionService.GrammarTable(productionNodeList);
        AutomataNode automataNode = CreateRootNode(productionNodeList,rhsGrammarTable);
        aNodeList.add(automataNode);
        int i = 0;
        while(i < dynamicSize)
        {
            GetNextNodes(aNodeList.get(i),rhsGrammarTable,grammarTable);
            i++;
        }
        List<AutomataNode> toReturn = ReduceAutomata(aNodeList);

        return toReturn;
    }

    private static List<AutomataNode> ReduceAutomata(List<AutomataNode> aNodeList) {

        List<String> donotEvaluate = new ArrayList<>();
        List<AutomataNode> reducedAutomata = new ArrayList<>();
        int name2 = 0;
        for(int i = 0; i < aNodeList.size(); i++)
        {
            for(int j = 0; j < aNodeList.size(); j++)
            {
                if(!aNodeList.get(i).name.equals(aNodeList.get(j).name) && !donotEvaluate.contains(aNodeList.get(i).name))
                {
                    if(CheckIfNodeIsEquivalent(aNodeList.get(i),aNodeList.get(j)))
                    {
                        for(int k = 0; k < aNodeList.get(i).lineCollection.size(); k++)
                        {
                            aNodeList.get(i).lineCollection.get(k).F.addAll(aNodeList.get(j).lineCollection.get(k).F);
                            Set<String> hs = new HashSet<>();
                            hs.addAll(aNodeList.get(i).lineCollection.get(k).F);
                            aNodeList.get(i).lineCollection.get(k).F.clear();
                            aNodeList.get(i).lineCollection.get(k).F.addAll(hs);
                        }
                        donotEvaluate.add(aNodeList.get(j).name);
                        for (AutomataNode automataNode : aNodeList)
                        {
                            for (Object o : automataNode.Displacements.entrySet()) {
                                Map.Entry pair = (Map.Entry) o;
                                if (((AutomataNode) pair.getValue()).name.equals(aNodeList.get(j).name))
                                    pair.setValue(aNodeList.get(i));
                            }

                            for (Object o : automataNode.GoTos.entrySet()) {
                                Map.Entry pair = (Map.Entry) o;
                                if (((AutomataNode) pair.getValue()).name.equals(aNodeList.get(j).name))
                                    pair.setValue(aNodeList.get(i));
                            }
                        }
                    }
                }
            }
            if(!donotEvaluate.contains(aNodeList.get(i).name))
            {
                aNodeList.get(i).name = "I" + name2;
                name2++;
                reducedAutomata.add(aNodeList.get(i));
            }
        }
        return reducedAutomata;
    }

    private static void GetNextNodes(AutomataNode automataNode, Map<String, List<RhsNode>> rhsGrammarTable, Map<String, List<List<String>>> grammarTable) throws Exception
    {
        List<String> doneSymbols = new ArrayList<>();
        for(NodeLine nodeLine : automataNode.lineCollection)
        {
            if(nodeLine.dot < nodeLine.Production.size())
            {
                if(!doneSymbols.contains(nodeLine.Production.get(nodeLine.dot)))
                {
                    AutomataNode node = GoTo(nodeLine.Production.get(nodeLine.dot),automataNode.lineCollection, rhsGrammarTable, grammarTable);
                    node = ProcessPossibleNewNode(node);
                    if(SymbolTable.Instance().GetSymbolType(nodeLine.Production.get(nodeLine.dot)) instanceof Terminal)
                    {
                        automataNode.Displacements.put(nodeLine.Production.get(nodeLine.dot),node);
                    }
                    else
                    {
                        automataNode.GoTos.put(nodeLine.Production.get(nodeLine.dot),node);
                    }
                    doneSymbols.add(nodeLine.Production.get(nodeLine.dot));
                    allLookUps.clear();
                }
            }
        }
    }

    private static AutomataNode ProcessPossibleNewNode(AutomataNode node)
    {

        for(AutomataNode automataNode : aNodeList)
        {
            if(CheckIfNodeIsSame(automataNode,node))
                return automataNode;
        }
        node.name = "I" + name;
        name++;
        aNodeList.add(node);
        dynamicSize = aNodeList.size();
        return node;
    }

    private static boolean CheckIfNodeIsEquivalent(AutomataNode automataNode, AutomataNode node)
    {
        if(automataNode.lineCollection.size() != node.lineCollection.size())
            return false;

        for(int i = 0; i < automataNode.lineCollection.size(); i++)
        {
            if(!automataNode.lineCollection.get(i).Producer.equals(node.lineCollection.get(i).Producer))
                return false;

            if(automataNode.lineCollection.get(i).Production.size() != node.lineCollection.get(i).Production.size())
                return false;

            List<String> temp1 = new ArrayList<>(automataNode.lineCollection.get(i).Production);
            List<String> temp2 = new ArrayList<>(node.lineCollection.get(i).Production);

            Collections.sort(temp1);
            Collections.sort(temp2);

            if(!temp1.equals(temp2))
                return false;

            if(automataNode.lineCollection.get(i).dot != node.lineCollection.get(i).dot)
                return false;

        }

        return true;
    }

    private static boolean CheckIfNodeIsSame(AutomataNode automataNode, AutomataNode node)
    {
        if(automataNode.lineCollection.size() != node.lineCollection.size())
            return false;

        for(int i = 0; i < automataNode.lineCollection.size(); i++)
        {
                if(!automataNode.lineCollection.get(i).Producer.equals(node.lineCollection.get(i).Producer))
                    return false;

                if(automataNode.lineCollection.get(i).Production.size() != node.lineCollection.get(i).Production.size())
                    return false;

                List<String> temp1 = new ArrayList<>(automataNode.lineCollection.get(i).Production);
                List<String> temp2 = new ArrayList<>(node.lineCollection.get(i).Production);

                Collections.sort(temp1);
                Collections.sort(temp2);

                if(!temp1.equals(temp2))
                    return false;

                temp1 = new ArrayList<>(automataNode.lineCollection.get(i).F);
                temp2 = new ArrayList<>(node.lineCollection.get(i).F);

                Collections.sort(temp1);
                Collections.sort(temp2);

                if(!temp1.equals(temp2))
                    return false;

                if(automataNode.lineCollection.get(i).dot != node.lineCollection.get(i).dot)
                    return false;

        }

        return true;
    }

    private static AutomataNode GoTo(String s, List<NodeLine> lineCollection, Map<String, List<RhsNode>> rhsGrammarTable, Map<String, List<List<String>>> grammarTable) throws Exception
    {
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
                                automataNode.lineCollection.addAll(GetClosure(node,grammarTable,rhsGrammarTable));
                                doneSymbols.add(node.Production.get(node.dot));
                            }
                        }
                    }
                }
            }
        }
        return automataNode;
    }

    private static AutomataNode CreateRootNode(List<ProductionNode> productionNodeList, Map<String, List<RhsNode>> firstTable) throws Exception
    {
        Map<String, List<List<String>>> grammarTable = ConvertionService.GrammarTable(productionNodeList);
        AutomataNode automataNode = new AutomataNode();
        automataNode.name = "I0";
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

    private static List<NodeLine> GetClosure(NodeLine nodeLine, Map<String, List<List<String>>> grammarTable, Map<String, List<RhsNode>> rhsGrammarTable) throws Exception
    {
        List<NodeLine> closure = new ArrayList<>();
        String postDot = nodeLine.Production.get(nodeLine.dot);
        List<String> F = GetF(nodeLine, rhsGrammarTable);
        List<List<String>> productions = grammarTable.get(postDot);
        for(List<String> production : productions)
        {
            if(!postDot.equals("S_prime"))
            {
                NodeLine toAdd = new NodeLine(postDot);
                toAdd.Production.addAll(production);
                toAdd.F.addAll(F);
                if(toAdd.Production.get(toAdd.dot).equals("ɛ"))
                {
                    toAdd.dot++;
                }
                closure.add(toAdd);
                //TODO
                if(toAdd.dot < toAdd.Production.size())
                {
                    if(toAdd.Production.get(toAdd.dot).equals(postDot) && toAdd.dot + 1 < toAdd.Production.size())
                    {
                        int i = 1;
                        while(true)
                        {
                            List<String> first = GrammarService.GetFirst(toAdd.Production.get(toAdd.dot + i),rhsGrammarTable);
                            toAdd.F.addAll(first);
                            if(!first.contains("ɛ"))
                            {
                                break;
                            }
                            if(toAdd.dot + i + 1 == toAdd.Production.size())
                            {
                                toAdd.F.addAll(nodeLine.F);
                                Set<String> hs = new HashSet<>();
                                hs.addAll(toAdd.F);
                                toAdd.F.clear();
                                toAdd.F.addAll(hs);
                                break;
                            }
                            i++;
                        }

                        if(allLookUps.containsKey(toAdd.Producer))
                        {
                            allLookUps.get(toAdd.Producer).addAll(toAdd.F);
                            Set<String> hs = new HashSet<>();
                            hs.addAll(allLookUps.get(toAdd.Producer));
                            allLookUps.get(toAdd.Producer).clear();
                            allLookUps.get(toAdd.Producer).addAll(hs);
                        }
                        else
                        {
                            allLookUps.put(toAdd.Producer,toAdd.F);
                        }
                    }
                    if(allLookUps.containsKey(toAdd.Producer))
                    {
                        toAdd.F.addAll(allLookUps.get(toAdd.Producer));
                        Set<String> hs = new HashSet<>();
                        hs.addAll(toAdd.F);
                        toAdd.F.clear();
                        toAdd.F.addAll(hs);
                    }
                    if(SymbolTable.Instance().GetSymbolType(toAdd.Production.get(toAdd.dot)) instanceof NonTerminal
                            && !toAdd.Production.get(toAdd.dot).equals(postDot)){
                        closure.addAll(GetClosure(toAdd,grammarTable, rhsGrammarTable));
                    }
                }
            }
        }
        return closure;
    }

    private static List<String> GetF(NodeLine nodeLine, Map<String, List<RhsNode>> rhsGrammarTable) throws Exception
    {
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
            int i = 1;
            while(true)
            {
                List<String> first = GrammarService.GetFirst(nodeLine.Production.get(dot + i),rhsGrammarTable);
                F.addAll(first);
                if(!first.contains("ɛ"))
                {
                    break;
                }
                if(dot + i + 1 == nodeLine.Production.size())
                {
                    F.addAll(nodeLine.F);
                    break;
                }
                i++;
            }
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(F);
        F.clear();
        F.addAll(hs);
        return F;
    }
}
