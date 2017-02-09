package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public class ProdPartNode {
    @SerializedName("symbolIdNode")
    public SymbolIdNode symbolIdNode;
    @SerializedName("JavaCodeNode")
    public JavaCodeNode JavaCodeNode;
}
