package Semantic.Nodes.Expression;

import Semantic.Types.SymbolTable;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public abstract class IdNode extends ExpressionNode{
    @SerializedName("Name")
    public String Name;

    public void EvaluateSemantic() throws Exception {
        if(!SymbolTable.Instance().VariableExist(Name))
            throw new Exception("Variblae " + Name + " does not exist");
    }
}
