package Semantic.Nodes.Statements;

import Semantic.Nodes.Expression.IdNode;
import Semantic.Types.NonTerminal;
import Semantic.Types.SymbolTable;

/**
 * Created by jpaz on 2/9/17.
 */
public class NonTerminalSymbolNode extends SymbolNode {
    @Override
    public void EvaluateSemantic() {
        for(IdNode idNode : declarationList)
        {
            SymbolTable.Instance().DeclareSymbol(idNode.Name,new NonTerminal());
        }
    }
}
