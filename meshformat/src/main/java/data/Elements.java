package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Elements {

	public enum ElementType{
		Line, Triangle, Rectangle, Tetrahedral, Hexahedral, Wedge, Pyramid
	}

	public static class Element implements Comparable<Element>{
		private ArrayList<Integer> conectedPoints;
		private ElementType type;

		public Element(ElementType type, ArrayList<Integer> listElem) throws Exception{
			boolean valid = type != null && listElem!=null;
			switch (type){
			case Line: valid = listElem.size()==2; break;
			case Triangle: valid = listElem.size()==3; break;
			case Rectangle:
			case Pyramid: valid = listElem.size()==4; break;
			case Tetrahedral: valid = listElem.size()==5; break;
			case Wedge: valid = listElem.size()==6; break;
			case Hexahedral: valid = listElem.size()==8; break;
			}
			if (!valid){
				throw new Exception("The type and the list of points aren't valid");
			}
			this.conectedPoints = listElem;
			this.type = type;
		}

		public int numberOfPoints(){
			switch (type){
			case Line: return(2); 
			case Triangle: return(3); 
			case Rectangle:
			case Pyramid: return(4); 
			case Tetrahedral: return(5); 
			case Wedge: return(6); 
			case Hexahedral: return(8);
			}
			return -1;
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

		public boolean hasNode(int node){
			return conectedPoints.contains(node);
		}

		private int positionOfNode(int node){
			if (!hasNode(node)){
				return -1;
			}
			int posnode = 0;
			while (conectedPoints.get(posnode)!=node){
				posnode++;
			}
			return posnode;
		}

		public boolean areConectedNodes(int node1, int node2){
			int pn1 = positionOfNode(node1);
			int pn2 = positionOfNode(node2);
			if (pn1>pn2){
				int aux= pn1;
				pn1=pn2;
				pn2=aux;
			}

			switch (this.type){
			case Line: return (pn1==0 && pn2==1) ; 
			case Triangle: return (pn1!=-1 && pn2!=-1);
			case Rectangle: switch (pn1){
			case 0: return pn2==1 || pn2==3;
			case 1: return pn2==2;
			case 2: return pn2==3;
			}
			case Tetrahedral: return (pn1!=-1 && pn2!=-1);
			case Hexahedral: switch (pn1){
			case 0: return pn2==1 || pn2==3 || pn2==4;
			case 1: return pn2==2 || pn2==5;
			case 2: return pn2==3 || pn2==6;
			case 3: return pn2==7;
			case 4: return pn2==5 || pn2==7;
			case 5: return pn2==6;
			case 6: return pn2==7;
			}
			case Wedge: switch (pn1){
			case 0: return pn2==1 || pn2==2 || pn2==3;
			case 1: return pn2==2 || pn2==4;
			case 2: return pn2==5;
			case 3: return pn2==4 || pn2==5;
			case 4: return pn2==5;
			}
			case Pyramid: switch (pn1){
			case 0: return pn2==1 || pn2==3 || pn2==4;
			case 1: return pn2==2 || pn2==4;
			case 2: return pn2==3 || pn2==4;
			case 3: return pn2==4;
			}
			default:
				break;
			}
			return false;

		}

		public int compareTo(Element o) {
			if (this.type == o.type){
				if (this.conectedPoints.containsAll(o.conectedPoints)){
					return 0;
				} else {
					int i=0;
					while ((o.conectedPoints.get(i)-this.conectedPoints.get(i))==0){
						i++;
					}
					return (o.conectedPoints.get(i)-this.conectedPoints.get(i));
				}
			}else{
				return this.type.compareTo(o.type);
			}
		}
		
		public String toString(String separator){
			String ret = conectedPoints.get(0).toString();
			for (int i=1; i<conectedPoints.size(); i++){
				ret = ret.concat(separator).concat(conectedPoints.get(i).toString());
			}
			return ret;
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
