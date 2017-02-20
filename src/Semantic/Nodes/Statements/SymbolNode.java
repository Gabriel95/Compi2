package Semantic.Nodes.Statements;

import Semantic.Nodes.Expression.DeclarationTypeNode;
import Semantic.Nodes.Expression.IdNode;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public abstract class SymbolNode extends StatementNode{
    @SerializedName("className")
    public DeclarationTypeNode className;
    @SerializedName("declarationList")
    public List<IdNode> declarationList;
}
