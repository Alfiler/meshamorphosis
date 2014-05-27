package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

public class Elements {

	public enum ElementType{
		Line, Triangle, Rectangle, Tetrahedral, Hexahedral, Wedge, Pyramid
	}

	public static class Element implements Comparable<Element>{
		private int[] conectedPoints;
		private ElementType type;
		private Elements subElements;

		public Element(ElementType type, int[] listElem) throws Exception{
			boolean valid = type != null && listElem!=null;
			switch (type){
			case Line: valid = listElem.length==2; break;
			case Triangle: valid = listElem.length==3; break;
			case Rectangle:
			case Pyramid: valid = listElem.length==4; break;
			case Tetrahedral: valid = listElem.length==5; break;
			case Wedge: valid = listElem.length==6; break;
			case Hexahedral: valid = listElem.length==8; break;
			}
			if (!valid){
				throw new Exception("The type and the list of points aren't valid");
			}
			this.conectedPoints = listElem;
			subElements = new Elements();
			switch (type){
			case Line: 	break;
			case Triangle: 	subElements.add(new Element(ElementType.Line, new int[]{listElem[0], listElem[1]}));
			subElements.add(new Element(ElementType.Line, new int[]{listElem[1], listElem[2]}));
			subElements.add(new Element(ElementType.Line, new int[]{listElem[2], listElem[0]}));
			break;
			case Rectangle:	subElements.add(new Element(ElementType.Line, new int[]{listElem[0], listElem[1]}));
			subElements.add(new Element(ElementType.Line, new int[]{listElem[1], listElem[2]}));
			subElements.add(new Element(ElementType.Line, new int[]{listElem[2], listElem[3]}));
			subElements.add(new Element(ElementType.Line, new int[]{listElem[3], listElem[0]}));
			break;
			case Pyramid:	subElements.add(new Element(ElementType.Triangle, new int[]{listElem[0], listElem[1], listElem[4]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[0], listElem[3], listElem[4]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[1], listElem[2], listElem[4]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[2], listElem[3], listElem[4]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[0],  listElem[1], listElem[2], listElem[3]}));
			break;
			case Tetrahedral: subElements.add(new Element(ElementType.Triangle, new int[]{listElem[0], listElem[1], listElem[2]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[0], listElem[1], listElem[3]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[0], listElem[2], listElem[3]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[1], listElem[2], listElem[3]}));
			break;
			case Wedge: 	subElements.add(new Element(ElementType.Triangle, new int[]{listElem[0], listElem[1], listElem[2]}));
			subElements.add(new Element(ElementType.Triangle, new int[]{listElem[3], listElem[4], listElem[5]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[0],  listElem[1], listElem[3], listElem[4]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[0],  listElem[2], listElem[5], listElem[3]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[1],  listElem[2], listElem[5], listElem[4]}));break;
			case Hexahedral:subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[0],  listElem[1], listElem[2], listElem[3]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[0],  listElem[1], listElem[5], listElem[4]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[0],  listElem[4], listElem[7], listElem[3]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[4],  listElem[5], listElem[6], listElem[7]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[1],  listElem[2], listElem[6], listElem[5]}));
			subElements.add(new Element(ElementType.Rectangle, new int[]{ listElem[2],  listElem[3], listElem[7], listElem[6]}));break;
			}
			this.type = type;
			this.order();
		}

		private void order(){
			if (type==ElementType.Line){
				int max = Math.max(conectedPoints[0], conectedPoints[1]);
				int min = Math.min(conectedPoints[0], conectedPoints[1]);
				conectedPoints[0]=min;
				conectedPoints[1]=max;
			}
			if (type==ElementType.Triangle){
				int med;
				int min;
				int max = Math.max(conectedPoints[2], Math.max(conectedPoints[0], conectedPoints[1]));
				if (max==conectedPoints[2]){
					med = Math.max(conectedPoints[0], conectedPoints[1]);
					min = Math.min(conectedPoints[0], conectedPoints[1]);
				}else{
					if (max==conectedPoints[1]){
						med = Math.max(conectedPoints[0], conectedPoints[2]);
						min = Math.min(conectedPoints[0], conectedPoints[2]);
					} else {
						med = Math.max(conectedPoints[2], conectedPoints[1]);
						min = Math.min(conectedPoints[2], conectedPoints[1]);
					}
				}
				conectedPoints[0]=min;
				conectedPoints[1]=med;
				conectedPoints[2]=max;
			}
			if (type==ElementType.Rectangle){
				int min = Math.min(Math.min(conectedPoints[0], conectedPoints[1]), Math.min(conectedPoints[2], conectedPoints[3]));
				int inicio=0;
				for (int i=0; i<conectedPoints.length; i++){
					if (conectedPoints[i]==min){
						inicio=i;
					}
				}
				boolean derecho = (Math.min(conectedPoints[(inicio+1)%4], conectedPoints[(inicio-1)%4]))==conectedPoints[(inicio+1)%4];
				int cP[]=new int[4];
				for (int i=0; i<4; i=derecho?i+1:i-1){
					cP[i]=conectedPoints[(i+inicio)%4];
				}
				conectedPoints=cP;
			}
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
			return conectedPoints.length;
		}

		public Integer get(int position){
			return conectedPoints[position];
		}

		public ElementType getType(){
			return type;
		}

		public boolean hasNode(int node){
			for (int i:conectedPoints){
				if (i==node){
					return true;
				}
			}
			return false;
		}

		private int positionOfNode(int node){
			if (!hasNode(node)){
				return -1;
			}
			int posnode = 0;
			while (conectedPoints[posnode]!=node){
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
				if (this.conectedPoints==o.conectedPoints){
					return 0;
				} else {
					int i=0;
					while ((o.conectedPoints[i]-this.conectedPoints[i])==0){
						i++;
					}
					return (o.conectedPoints[i]-this.conectedPoints[i]);
				}
			}else{
				return this.type.compareTo(o.type);
			}
		}

		public String toString(String separator){
			String ret = Integer.toString(conectedPoints[0]);
			for (int i=1; i<conectedPoints.length; i++){
				ret = ret.concat(separator).concat(Integer.toString(conectedPoints[i]));
			}
			return ret;
		}

		public int shareNodes(Element e){
			int conected = 0;
			for (Integer n1:this.conectedPoints){
				for (Integer n2:e.conectedPoints){
					if (n1==n2){
						conected++;
					}
				}
			}
			return conected;
		}

		public boolean isSubElement(Element e){
			for (int i = 0; i<subElements.size(); i++){
				if (subElements.get(i).compareTo(e)==0){
					return true;
				}
				if (subElements.get(i).isSubElement(e)){
					return true;
				}
			}
			return false;
		}

		public Element formASubElement(int[] js){
			if (js.length>this.numberOfPoints()){ //the mElements can't form something with the nodes
				return null;
			}
			for (int i=0; i<js.length; i++){ //all the nodes are in this mElements
				if (!this.hasNode(js[i])){
					return null;
				}
			}
			if (js.length==this.numberOfPoints()){ //all the nodes are the nodes of this mElements
				return this;
			} else {
				for (int i =0; i<this.subElements.size(); i++){
					Element sub = this.subElements.get(i).formASubElement(js);
					if (sub!=null){
						return sub;
					}
				}
			}
			return null;
		}

		public List<Integer> getNodes() {
			List<Integer> result = new ArrayList<Integer>();
			for (int i=0; i<conectedPoints.length; i++){
				result.add(conectedPoints[i]);
			}
			return result;
		}
	}

	private List<Element> mElements;

	public Elements(){
		mElements = new ArrayList<Element>();
	}

	public void add(Element e){
		if (!mElements.contains(e)){
			mElements.add(e);
		}
	}
	
	public void add(Elements es){
		for (int i=0; i<es.size(); i++){
			this.add(es.get(i));
		}
	}

	public Element get(int i){
		return mElements.get(i);
	}
	
	public void remove(Element e){
		mElements.remove(e);
	}

	public int size(){
		return mElements.size();
	}

	public boolean contains(Element e){
		return mElements.contains(e);
	}

	public Element formAElement(int[] js){
		for (int i=0; i<this.size(); i++){
			Element result = this.get(i).formASubElement(js);
			if (result!=null){
				return result;
			}
		}
		return null;
	}
	
	public Elements getElementsWithNode(int node){
		Elements result = new Elements();
		for (int i=0; i<this.size(); i++){
			if(this.get(i).hasNode(node)){
				result.add(this.get(i));
			}
		}
		return result;
	}
	
	public List<Integer> getNodes(){
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i<this.size(); i++){
			result.addAll(this.get(i).getNodes());
		}
		return result;
	}
}
