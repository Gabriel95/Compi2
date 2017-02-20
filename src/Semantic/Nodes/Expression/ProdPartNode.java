package Semantic.Nodes.Expression;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public class ProdPartNode extends ExpressionNode{
    @SerializedName("symbolIdNode")
    public SymbolIdNode symbolIdNode;
    @SerializedName("JavaCodeNode")
    public Semantic.Nodes.JavaCodeNode JavaCodeNode;

    @Override
    public void EvaluateSemantic() throws Exception {
        if(symbolIdNode != null)
            symbolIdNode.EvaluateSemantic();
    }
}
