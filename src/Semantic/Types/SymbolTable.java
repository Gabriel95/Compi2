package Semantic.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gabriel Paz on 19-Feb-17.
 */
public class SymbolTable {
    public Map<String, CustomType> variables;
    public static List<SymbolTable> instance = new ArrayList<>();
    public SymbolTable() {
        variables = new HashMap<>();
    }

    public static SymbolTable Instance(){
        if(instance.size() == 0)
        {
            instance.add(0, new SymbolTable());
        }

        return instance.get(0);

    }

    public void DeclareSymbol(String name, CustomType type){
        variables.put(name,type);
    }

    public CustomType GetSymbolType(String name) throws Exception {
        for (SymbolTable symbolTable : instance)
        {
            if(symbolTable.variables.containsKey(name))
            {
                return symbolTable.variables.get(name);
            }
        }
        throw new Exception("Symbol " + name + "does not exist");
    }

    public boolean VariableExist(String name) {
        return Instance().variables.containsKey(name);
    }
}
