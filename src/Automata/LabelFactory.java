package Automata;
import java.util.Hashtable;
/**
 * Created by jpaz on 3/17/17.
 */
public class LabelFactory {
    static Hashtable<String,Integer> labelsCount = new Hashtable<>();

    public static String createLabel(String labelName) {
        if(!labelsCount.containsKey(labelName))
            labelsCount.put(labelName,0);
        int count = labelsCount.get(labelName)+1;
        String label = labelName+count;
        labelsCount.replace(labelName,count);
        return label;
    }
}
