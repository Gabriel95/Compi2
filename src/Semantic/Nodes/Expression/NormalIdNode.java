package Semantic.Nodes.Expression;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public class NormalIdNode extends IdNode{
    @SerializedName("accessor")
    public AccessorNode accessor;
}