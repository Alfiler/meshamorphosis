package data;

import java.util.HashMap;
import java.util.Set;

public class Extra {

	public static class BorderMarkerInfo{

        public enum BMIType{
            ALL_MARKER_NODES, CONSTRAINED
        }

		public String name;
		public Integer[] borderNodes;
		public int insideNode;
        public BMIType type;
		
		public boolean isBorderNode(int node){
			for (int i:borderNodes){
				if (i==node){
					return true;
				}
			}
			return false;
		}
	}
	
	public static class BorderMarkers{
		public HashMap<String, Elements> marks;
		
		public BorderMarkers(){
			marks = new HashMap<String, Elements>();
		}
		
		public void add(String tag, Elements elements){
			marks.put(tag, elements);
		}
		
		public Elements get(String tag){
			return marks.get(tag);
		}
		
		public Set<String> getTags(){
			return marks.keySet();
		}
	}
}
