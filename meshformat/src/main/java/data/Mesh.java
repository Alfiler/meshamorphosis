package data;

import java.util.HashMap;

public class Mesh {
	public Points p;
	public Elements e;
	private HashMap<String, Object> extras;
	
	public Mesh(){
		p = new Points();
		e = new Elements();
		extras = new HashMap<String, Object>();
	}
	
	public void putExtra(String name, Object object){
		extras.put(name, object);
	}
	
	public Object getExtra(String name){
		return extras.get(name);
	}
}
