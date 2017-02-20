import Semantic.Nodes.Expression.ProdPartNode;
import Semantic.Nodes.Expression.RhsNode;
import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Types.SymbolTable;
import Semantic.Types.Terminal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jpaz on 2/20/17.
 */
public class GrammarService {
    public static Map<String, List<RhsNode>> GetGrammarTable(List<ProductionNode> productionNodeList)
    {
        Map<String, List<RhsNode>> grammarTable = new HashMap<>();
        for(ProductionNode productionNode : productionNodeList)
        {
            grammarTable.put(productionNode.ntID.Name,productionNode.RhsTokenList);
        }
        return grammarTable;
    }
    public static Map<String, List<String>> FirstTable (List<ProductionNode> productionNodeList) throws Exception {
        Map<String, List<String>> firstTable = new HashMap<>();
        Map<String, List<RhsNode>> grammarTable = GetGrammarTable(productionNodeList);

        for(ProductionNode productionNode : productionNodeList)
        {
            firstTable.put(productionNode.ntID.Name,GetFirst(productionNode.ntID.Name,grammarTable));
        }
        return firstTable;
    }

    private static List<String> GetFirst(String x, Map<String, List<RhsNode>> grammarTable) throws Exception {
        List<String> FirstList = new ArrayList<>();
        if(SymbolTable.Instance().GetSymbolType(x) instanceof Terminal)
        {
            FirstList.add(x);
            return FirstList;
        }
        List<RhsNode> rhsNodeList = grammarTable.get(x);
        for(RhsNode rhsNode : rhsNodeList)
        {
            for(ProdPartNode prodPartNode : rhsNode.prodPartList)
            {
                List<String> resultY = GetFirst(prodPartNode.symbolIdNode.Name, grammarTable);
                FirstList.addAll(resultY);
                if(!resultY.contains("ɛ")) break;
            }
        }
        return FirstList;
    }


}
