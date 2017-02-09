package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class ProductionNode {
    @SerializedName("RhsTokenList")
    public List<RhsNode> RhsTokenList;
    @SerializedName("ntID")
    public NonTerminalIdNode ntID;
}
