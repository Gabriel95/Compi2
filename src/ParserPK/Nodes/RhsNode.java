package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class RhsNode {
    @SerializedName("prodPartList")
    public List<ProdPartNode> prodPartList;
}
