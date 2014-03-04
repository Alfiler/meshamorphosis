package data;

import java.util.ArrayList;
import java.util.List;

public class Elements {
	
	public enum ElementType{
		Line, Triangle, Rectangle, Tetrahedral, Hexahedral, Wedge, Pyramid
	}
	
	public class Element{
		private ArrayList<Integer> conectedPoints;
		private ElementType type;
		
		public Element(ElementType type, ArrayList<Integer> listElem){
			this.conectedPoints = listElem;
			this.type = type;
		}
		
		public int getNumberOfPoints(){
			return conectedPoints.size();
		}
		
		public Integer get(int position){
			return conectedPoints.get(position);
		}
		
		public ElementType getType(){
			return type;
		}
	}

	private List<Element> element;
	
	public Elements(){
		element = new ArrayList<Element>();
	}
	
	public void add(Element e){
		element.add(e);
	}
	
	public Element get(int i){
		return element.get(i);
	}
	
	public int size(){
		return element.size();
	}
}
