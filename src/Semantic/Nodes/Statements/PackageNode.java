package Semantic.Nodes.Statements;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public class PackageNode extends StatementNode {
    @SerializedName("IdNode")
    public Semantic.Nodes.Expression.IdNode IdNode;

    @Override
    public void EvaluateSemantic() {

    }
}
