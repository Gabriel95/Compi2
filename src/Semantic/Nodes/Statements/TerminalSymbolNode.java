package Semantic.Nodes.Statements;

import Semantic.Nodes.Expression.IdNode;
import Semantic.Nodes.Statements.SymbolNode;
import Semantic.Types.SymbolTable;
import Semantic.Types.Terminal;

/**
 * Created by jpaz on 2/9/17.
 */
public class TerminalSymbolNode extends SymbolNode {
    @Override
    public void EvaluateSemantic() {
        for(IdNode idNode : declarationList)
        {
            SymbolTable.Instance().DeclareSymbol(idNode.Name,new Terminal());
        }
    }
}
