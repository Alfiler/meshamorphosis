package data;

import java.util.HashMap;
import java.util.Set;

public class Extra {

	public static class BorderMarkerInfo{
		public String name;
		public int ini, end;
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
