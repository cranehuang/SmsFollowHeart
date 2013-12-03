package followheart.utils;

import java.util.Comparator;

public class ComparatorPY implements Comparator<String>{  
	  
    @Override  
    public int compare(String lhs,String rhs) {  
        
        return lhs.compareToIgnoreCase(rhs);  
    }  
}  
