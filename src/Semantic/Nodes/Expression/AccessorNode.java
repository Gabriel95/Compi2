package Semantic.Nodes.Expression;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public abstract class AccessorNode extends ExpressionNode{
    @SerializedName("IdNode")
    public Semantic.Nodes.Expression.IdNode IdNode;

    public void EvaluateSemantic() {
        //System.out.println("Not Implemented");
    }

    public String getName() {
        if(IdNode instanceof DeclarationTypeNode)
            return ((DeclarationTypeNode)IdNode).getName();
        if(IdNode instanceof ImportIdNode)
            return ((ImportIdNode)IdNode).getName();
        if(IdNode instanceof NormalIdNode)
            return ((NormalIdNode)IdNode).getName();
        if(IdNode instanceof StarId)
            return "*";
        return IdNode.Name;
    }
}
