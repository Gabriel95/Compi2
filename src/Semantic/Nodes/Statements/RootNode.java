package Semantic.Nodes.Statements;

import Semantic.Nodes.Expression.ImportIdNode;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class RootNode extends StatementNode {
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

    @Override
    public void EvaluateSemantic() throws Exception {
        packageNode.EvaluateSemantic();

//        for (ImportIdNode importIdNode : importList)
//        {
//            importIdNode.EvaluateSemantic();
//        }

        for (CodePartNode code : codeParts)
        {
            code.EvaluateSemantic();
        }

        for(SymbolNode sym : symbolList)
        {
            sym.EvaluateSemantic();
        }

        for(ProductionNode productionNode : productionList)
        {
            productionNode.EvaluateSemantic();
        }
    }
}
