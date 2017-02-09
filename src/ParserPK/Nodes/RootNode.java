package ParserPK.Nodes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class RootNode extends StatementNode{
    @SerializedName("packageNode")
    public PackageNode packageNode;
    @SerializedName("importList")
    public List<ImportIdNode> importList;
    @SerializedName("codeParts")
    public List<CodePartNode> codeParts;
    @SerializedName("symbolList")
    public List<SymbolNode> symbolList;
    @SerializedName("productionList")
    public List<ProductionNode> productionList;

}
