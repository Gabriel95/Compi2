package Automata;

import Semantic.Nodes.Expression.ProdPartNode;
import Semantic.Nodes.Expression.RhsNode;
import Semantic.Nodes.Statements.ProductionNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 2/21/17.
 */
public class ConvertionService {
    public static Map<String, List<List<String>>> GrammarTable(List<ProductionNode> productionNodeList)
    {
        Map<String, List<List<String>>> grammarTable = new HashMap<>();
        String root = productionNodeList.get(0).ntID.Name;
        List<String> augmented = new ArrayList<>();
        augmented.add(root);
        //augmented.add("$");
        List<List<String>> toAdd = new ArrayList<>();
        toAdd.add(augmented);
        grammarTable.put("S_prime",toAdd);
        for (ProductionNode node: productionNodeList) {
            List<List<String>> productions = new ArrayList<>();
            for(RhsNode rhsNode : node.RhsTokenList)
            {
                List<String> productionLine = new ArrayList<>();
                for(ProdPartNode prodPartNode : rhsNode.prodPartList)
                {
                    if(prodPartNode.symbolIdNode != null)
                        productionLine.add(prodPartNode.symbolIdNode.Name);
                }
                productions.add(productionLine);
            }
            grammarTable.put(node.ntID.Name,productions);
        }
        return grammarTable;
    }
}
