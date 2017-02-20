package Semantic.Nodes.Expression;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class RhsNode extends ExpressionNode{
    @SerializedName("prodPartList")
    public List<ProdPartNode> prodPartList;

    @Override
    public void EvaluateSemantic() throws Exception {
        for(ProdPartNode prodPartNode : prodPartList)
        {
            prodPartNode.EvaluateSemantic();
        }
    }
}
