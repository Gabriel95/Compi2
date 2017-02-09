package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class SymbolNode {
    @SerializedName("className")
    public DeclarationTypeNode className;
    @SerializedName("declarationList")
    public List<IdNode> declarationList;
}
