package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public class SymbolIdNode extends IdNode{
    @SerializedName("label")
    public LabelIdNode label;
}
