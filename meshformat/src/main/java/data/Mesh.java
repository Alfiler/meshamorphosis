package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import data.Elements.Element;
import data.Elements.ElementType;
import data.Extra.BorderMarkerInfo;
import data.Extra.BorderMarkers;

// TODO: Auto-generated Javadoc
/**
 * The Class Mesh.
 */
public class Mesh {

	/** The marker tags. */
	public static String MARKER_TAGS = "MARKER_TAGS";

	/** The points. */
	public Points mPoints;

	/** The elements. */
	public Elements mElements;

	/** The extras of the mesh. */
	private HashMap<String, Object> extras;

	/**
	 * Instantiates a new mesh.
	 */
	public Mesh(){
		mPoints = new Points();
		mElements = new Elements();
		extras = new HashMap<String, Object>();
	}

	/**
	 * Put extra.
	 *
	 * @param name the name
	 * @param object the object
	 */
	public void putExtra(String name, Object object){
		extras.put(name, object);
	}

	/**
	 * Exist a extra.
	 *
	 * @param name of the extra
	 * @return true, if successful
	 */
	public boolean existExtra(String name){
		return extras.containsKey(name);
	}

	/**
	 * Gets the extra.
	 *
	 * @param name the name
	 * @return the extra
	 */
	public Object getExtra(String name){
		return extras.get(name);
	}

	/**
	 * Gets the dimensions of the mesh.
	 *
	 * @return the dimensions
	 */
	public int getDimensions(){
		if (mPoints==null){
			return -1;
		}
		return mPoints.getDimensions();
	}

	/**
	 * Adds the borders markers.
	 *
	 * @param listOfNodes the list of border nodes of the mesh
	 * @param listOfBorders the information of the borders
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean addBordersMarkers(List<Integer> listOfNodes,List<BorderMarkerInfo> listOfBorders){
		if (mElements==null || mPoints ==null){
			return false;
		}
		//removes repeated nodes of the list
		for (int i=0; i<listOfNodes.size(); i++){
			for (int j=i+1; j<listOfNodes.size(); j++){
				if (listOfNodes.get(i).equals(listOfNodes.get(j))){
					listOfNodes.remove(j);
					j--;
				}
			}
		}
		Collections.sort(listOfNodes);

		BorderMarkers bm = new BorderMarkers();

		Elements listOfBorderElements = getAllBorderElements(listOfNodes, getDimensions());
		for (BorderMarkerInfo bmi:listOfBorders){
			Stack<Integer> internalNodes = new Stack<Integer>();
			internalNodes.push(bmi.insideNode);
			Stack<Integer> usedNodes = new Stack<Integer>();
			for(int bn:bmi.borderNodes){ //border nodes added as used nodes to decrease complexity in code
				usedNodes.add(bn);
			}
			Elements listOfMarkerElements = new Elements();//the place to save the elements of this marker
			while (!internalNodes.empty()){
				int node = internalNodes.pop();
				usedNodes.push(node);
				Elements elements =  listOfBorderElements.getElementsWithNode(node);
				List<Integer> nodes = elements.getNodes();
				for (int n:nodes){
					if (!usedNodes.contains(n)){
						internalNodes.add(n);
					}
				}
				listOfMarkerElements.add(elements);
			}
			bm.add(bmi.name, listOfMarkerElements);
		}
		this.putExtra(MARKER_TAGS, bm);
		return true;
	}

	/**
	 * Gets the elements of a given dimension of the mesh formed by a list of nodes.
	 *
	 * @param listOfNodes the list of nodes
	 * @param dimensions the dimensions
	 * @return the all elements
	 */
	private Elements getAllElements(List<Integer> listOfNodes, int dimensions){
		Elements result = new Elements();
		Element e = null;
		for (int i=0; i<listOfNodes.size(); i++){
			int counter = 0;
			for (int j=i+1; j<listOfNodes.size(); j++){
				if (dimensions==2){
					e = mElements.formAElement(new int[]{listOfNodes.get(i),listOfNodes.get(j)});
					if (e!=null){
						result.add(e);
						counter++;
					}
				} else if (dimensions==3){
					for (int k=j+1; k<listOfNodes.size(); k++){
						e = mElements.formAElement(new int[]{listOfNodes.get(i),listOfNodes.get(j),listOfNodes.get(k)});
						if (e!=null){
							result.add(e);
						}
						for (int l=k+1; l<listOfNodes.size(); l++){
							e = mElements.formAElement(new int[]{listOfNodes.get(i),listOfNodes.get(j),listOfNodes.get(k),listOfNodes.get(l)});
							if (e!=null && e.getType()==ElementType.Rectangle){
								result.add(e);
							}
						}
					}
				}
			}

		}
		return result;
	}

	private Elements getAllBorderElements(List<Integer> listOfNodes, int dimensions){
		Elements result = getAllElements(listOfNodes, dimensions);
		for(int i=0; i<listOfNodes.size(); i++){
			Elements e1 = result.getElementsWithNode(listOfNodes.get(i));
			if (dimensions==2 && e1.size()>2){
				for (int j=i+1; j<listOfNodes.size(); j++){
					Elements e2 = result.getElementsWithNode(listOfNodes.get(j));
					if (e2.size()>2){
						Element notBorderElement = result.formAElement(new int[]{listOfNodes.get(i), listOfNodes.get(j)});
						if (notBorderElement!=null){
							result.remove(notBorderElement);
						}
					}
				}
			}
		}
		return result;
	}
}
