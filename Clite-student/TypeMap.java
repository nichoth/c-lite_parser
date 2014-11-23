import java.util.*;

public class TypeMap extends HashMap<Variable, Type> { 

// TypeMap is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.
	
	public void display() {
		Util.printIndent(1);
		System.out.println(this);
	}

}
