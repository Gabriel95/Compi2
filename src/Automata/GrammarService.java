package Automata;

import Semantic.Nodes.Expression.ProdPartNode;
import Semantic.Nodes.Expression.RhsNode;
import Semantic.Nodes.Statements.ProductionNode;
import Semantic.Types.SymbolTable;
import Semantic.Types.Terminal;

import java.util.*;

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
    public static Map<String, List<String>> FirstTable (List<ProductionNode> productionNodeList) throws Exception
    {
        Map<String, List<String>> firstTable = new HashMap<>();
        Map<String, List<RhsNode>> grammarTable = GetGrammarTable(productionNodeList);
        for(ProductionNode productionNode : productionNodeList)
        {
            firstTable.put(productionNode.ntID.Name,GetFirst(productionNode.ntID.Name,grammarTable));
        }
        return firstTable;
    }

    private static List<RhsNode> OrderProductions(String name, List<RhsNode> rhsNodeList)
    {
        int i = 0;
        int j = 0;

        while(i < rhsNodeList.size())
        {
            while(j < rhsNodeList.get(i).prodPartList.size())
            {
                if(rhsNodeList.get(i).prodPartList.get(j).symbolIdNode != null)
                {
                    if(rhsNodeList.get(i).prodPartList.get(j).symbolIdNode.Name.equals(name))
                    {
                        if(j + 1 < rhsNodeList.get(i).prodPartList.size())
                        {
                            RhsNode rhsNode = rhsNodeList.get(i);
                            rhsNodeList.remove(i);
                            rhsNodeList.add(rhsNode);
                        }
                        else
                        {
                            j++;
                        }
                    }
                    else
                    {
                        j++;
                    }
                }
                else
                {
                    j++;
                }
            }
            i++;
        }
        return rhsNodeList;
    }

    public static List<String> GetFirst(String x, Map<String, List<RhsNode>> grammarTable) throws Exception
    {
        List<String> FirstList = new ArrayList<>();
        if(SymbolTable.Instance().GetSymbolType(x) instanceof Terminal)
        {
            FirstList.add(x);
            return FirstList;
        }
        List<RhsNode> rhsNodeList = grammarTable.get(x);
        rhsNodeList = OrderProductions(x, rhsNodeList);
        for(RhsNode rhsNode : rhsNodeList)
        {
            for(ProdPartNode prodPartNode : rhsNode.prodPartList)
            {
                List<String> resultY;
                if(prodPartNode.symbolIdNode != null)
                    if(prodPartNode.symbolIdNode.Name.equals(x))
                    {
                        if(!FirstList.contains("ɛ"))
                            break;
                    }
                    else
                    {
                        resultY = GetFirst(prodPartNode.symbolIdNode.Name, grammarTable);
                        FirstList.addAll(resultY);
                        if(!resultY.contains("ɛ"))
                            break;
                    }
            }
        }
        return FirstList;
    }

    public static Map<String,List<String>> FollowTable (List<ProductionNode> productionNodeList) throws Exception
    {
        Map<String, List<String>> followTable = new HashMap<>();
        String root = productionNodeList.get(0).ntID.Name;
        for(ProductionNode productionNode : productionNodeList)
        {
            followTable.put(productionNode.ntID.Name,GetFollow(productionNode.ntID.Name,productionNodeList,root));
        }
        return followTable;
    }

    public static List<String> GetFollow(String name, List<ProductionNode> productionNodeList, String root) throws Exception
    {

        List<String> followList = new ArrayList<>();
        if(name.equals(root))
        {
//            followList.add("$");
        }
        for(ProductionNode productionNode : productionNodeList)
        {
            for(RhsNode rhsNode : productionNode.RhsTokenList)
            {
                for(int i = 0; i < rhsNode.prodPartList.size(); i++)
                {
                    if(rhsNode.prodPartList.get(i).symbolIdNode != null)
                    {
                        if(rhsNode.prodPartList.get(i).symbolIdNode.Name.equals(name))
                        {
                            if(i + 1 == rhsNode.prodPartList.size())
                            {
                                if(!productionNode.ntID.Name.equals(name))
                                {
                                    followList.addAll(GetFollow(productionNode.ntID.Name,productionNodeList,root));
                                }
                            }
                            else if(rhsNode.prodPartList.get(i+1) == null)
                            {
                                if(!productionNode.ntID.Name.equals(name))
                                {
                                    followList.addAll(GetFollow(productionNode.ntID.Name,productionNodeList,root));
                                }
                            }
                            else
                            {
                                List<String> result = GetFirst(rhsNode.prodPartList.get(i + 1).symbolIdNode.Name, GetGrammarTable(productionNodeList));
                                if(result.contains("ɛ"))
                                {
                                    result.remove("ɛ");
                                    followList.addAll(GetFollow(productionNode.ntID.Name,productionNodeList,root));
                                }
                                followList.addAll(result);
                            }
                        }
                    }
                }
            }
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(followList);
        followList.clear();
        followList.addAll(hs);
        return followList;
    }

    public static List<GrammarLine> GetNonSimplifiedGrammarTable(List<ProductionNode> productionNodeList)
    {
        List<GrammarLine> nonSimplifiedGrammarTable = new ArrayList<>();
        for(ProductionNode productionNode : productionNodeList)
        {
            for(RhsNode rhsNode : productionNode.RhsTokenList)
            {
                List<String> toAdd = new ArrayList<>();
                for(ProdPartNode prodPartNode : rhsNode.prodPartList)
                {
                    if(prodPartNode.symbolIdNode != null)
                        toAdd.add(prodPartNode.symbolIdNode.Name);
                }
                nonSimplifiedGrammarTable.add(new GrammarLine(productionNode.ntID.Name, toAdd));
            }
        }
        return nonSimplifiedGrammarTable;
    }


    public static String GetReduction(NodeLine nodeLine, List<GrammarLine> grammar)
    {
        for(int i = 0; i < grammar.size(); i++)
        {
            if(nodeLine.Production.equals(grammar.get(i).Productions))
            {
                return "r" + (i + 1);
            }
        }
        return null;
    }
}
