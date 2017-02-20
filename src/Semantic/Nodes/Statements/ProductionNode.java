package Semantic.Nodes.Statements;

import Semantic.Nodes.Expression.NonTerminalIdNode;
import Semantic.Nodes.Expression.RhsNode;
import Semantic.Types.NonTerminal;
import Semantic.Types.SymbolTable;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class ProductionNode extends StatementNode{
    @SerializedName("RhsTokenList")
    public List<RhsNode> RhsTokenList;
    @SerializedName("ntID")
    public NonTerminalIdNode ntID;

    @Override
    public void EvaluateSemantic() throws Exception {
        ntID.EvaluateSemantic();
        if(! (SymbolTable.Instance().GetSymbolType(ntID.Name) instanceof NonTerminal))
            throw new Exception(ntID.Name + " is not a Non Terminal");
        for(RhsNode rhsNode : RhsTokenList)
        {
            rhsNode.EvaluateSemantic();
        }
    }
}
