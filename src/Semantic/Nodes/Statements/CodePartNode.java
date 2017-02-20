package Semantic.Nodes.Statements;

import Semantic.Nodes.JavaCodeNode;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jpaz on 2/9/17.
 */
public abstract class CodePartNode extends StatementNode{
    @SerializedName("javaCodeNode")
    public JavaCodeNode javaCodeNode;
}
