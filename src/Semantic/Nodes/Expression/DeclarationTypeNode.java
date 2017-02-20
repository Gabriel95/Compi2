package Semantic.Nodes.Expression;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public class DeclarationTypeNode extends IdNode{
    @SerializedName("accessor")
    public AccessorNode accessor;

    @Override
    public void EvaluateSemantic() {
        //do nothing
    }
}
