package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public abstract class AccessorNode {
    @SerializedName("IdNode")
    public ParserPK.Nodes.IdNode IdNode;
}
