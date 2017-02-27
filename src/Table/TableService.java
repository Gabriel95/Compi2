package Table;

import Automata.AutomataNode;
import Automata.GrammarLine;
import Automata.GrammarService;
import Automata.NodeLine;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 2/27/17.
 */
public class TableService {

    public static RowSortedTable<String, String, String> GetTable(List<AutomataNode> nodeList, List<GrammarLine> grammar)
    {
        RowSortedTable<String, String, String> toReturn = TreeBasedTable.create();
        for (AutomataNode node : nodeList) {

            for (Object o : node.Displacements.entrySet())
            {
                Map.Entry pair = (Map.Entry) o;
                String symbol = (String) pair.getKey();
                AutomataNode aNode = (AutomataNode) pair.getValue();
                toReturn.put(node.name.replace("I", ""), symbol, "d" + aNode.name.replace("I", ""));

            }

            for (Object o : node.GoTos.entrySet())
            {
                Map.Entry pair = (Map.Entry) o;
                String symbol = (String) pair.getKey();
                AutomataNode aNode = (AutomataNode) pair.getValue();
                toReturn.put(node.name.replace("I", ""), symbol, aNode.name.replace("I", ""));

            }

            for(NodeLine nodeLine : node.lineCollection)
            {
                if(nodeLine.dot == nodeLine.Production.size())
                {
                    for ( String f :nodeLine.F)
                    {
                        if(nodeLine.Producer.equals("S_prime"))
                        {
                            toReturn.put(node.name.replace("I", ""),f, "Accepted");
                        }
                        else
                        {
                            toReturn.put(node.name.replace("I", ""),f, GrammarService.GetReduction(nodeLine,grammar));
                        }
                    }
                }
            }
        }
        return toReturn;
    }

}