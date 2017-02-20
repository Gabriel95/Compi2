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
}
