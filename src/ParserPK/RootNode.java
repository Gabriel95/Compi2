package ParserPK;

import java.util.List;

/**
 * Created by jpaz on 2/9/17.
 */
public class RootNode extends StatementNode{
    public PackageNode packageNode;
    public List<ImportIdNode> importList;
    public List<CodePartNode> codeParts;
}
