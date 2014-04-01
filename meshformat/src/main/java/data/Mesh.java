package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import data.Elements.Element;
import data.Elements.ElementType;
import data.Extra.BorderMarkerInfo;
import data.Extra.BorderMarkers;

public class Mesh {
	public static String MARKER_TAGS = "MARKER_TAGS";
	
	public Points mPoints;
	public Elements mElements;
	private HashMap<String, Object> extras;

	public Mesh(){
		mPoints = new Points();
		mElements = new Elements();
		extras = new HashMap<String, Object>();
	}

	public void putExtra(String name, Object object){
		extras.put(name, object);
	}
	
	public boolean existExtra(String name){
		return extras.containsKey(name);
	}

	public Object getExtra(String name){
		return extras.get(name);
	}

	public int getDimensions(){
		if (mPoints==null){
			return -1;
		}
		return mPoints.getDimensions();
	}

	public boolean addBordersMarkers(List<Integer> listOfNodes,List<BorderMarkerInfo> listOfBorders) throws Exception{
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

		List<Element> listOfBorderElements = new ArrayList<Elements.Element>();
		for (int elements = 0; elements<mElements.size(); elements++){
			//the node is part of this element
			Element element = mElements.get(elements);
			for (int i=0; i<listOfNodes.size(); i++){
				if (element.hasNode(listOfNodes.get(i))){
					for (int j=i+1; j<listOfNodes.size(); j++){//lets search the rest
						if (element.hasNode(listOfNodes.get(j))){//are from the same element
							if (element.areConectedNodes(listOfNodes.get(i), listOfNodes.get(j))){//are connected between them

								ArrayList<Integer> templist = new ArrayList<Integer>();
								templist.add(listOfNodes.get(i));
								templist.add(listOfNodes.get(j));
								listOfBorderElements.add(new Element(ElementType.Line, templist));

							}
						}
					}
				}
			}
		}

		Collections.sort(listOfBorderElements);
		System.out.println(listOfBorderElements.size());

		for (int i=0; i<listOfBorderElements.size(); i++){
			for (int j=i+1; j<listOfBorderElements.size(); j++){
				if (0==listOfBorderElements.get(i).compareTo(listOfBorderElements.get(j))){
					listOfBorderElements.remove(j);
					j--;
				}
			}
		}

		if (getDimensions()==2){
			HashMap<Integer, ArrayList<Element>> pointCounter = new HashMap<Integer, ArrayList<Element>>();
			ArrayList<Integer> nonlinearNodes = new ArrayList<Integer>();
			for (Element e:listOfBorderElements){
				for(int i=0; i<e.numberOfPoints(); i++){
					if (pointCounter.containsKey(e.get(i))) {
						pointCounter.get(e.get(i)).add(e);

						if (pointCounter.get(e.get(i)).size()==3){
							nonlinearNodes.add(e.get(i));
						}

					} else {
						ArrayList<Element> elemArray = new ArrayList<Element>();
						elemArray.add(e);
						pointCounter.put(e.get(i), elemArray);
					}
				}
			}

			Collections.sort(nonlinearNodes);
			for (int i=0; i< nonlinearNodes.size(); i++){
				int valueI = nonlinearNodes.get(i);
				for (int j=i+1; j<nonlinearNodes.size(); j++){
					int valueJ = nonlinearNodes.get(j);
					for(Element e:pointCounter.get(valueI)){
						if (e.areConectedNodes(valueI, valueJ)){
							listOfBorderElements.remove(e);
						}
					}
				}
			}

			BorderMarkers borderMarkers = new Extra.BorderMarkers();
			putExtra(MARKER_TAGS, borderMarkers);

			for (BorderMarkerInfo border:listOfBorders){
				Elements extraBorder = new Elements();
				Element be = null;
				int other = 0;
				boolean isFirst = false;
				for (int becount = 0; becount<listOfBorderElements.size() && !isFirst; becount++){
					be=listOfBorderElements.get(becount);
					if (be.get(0)==border.ini || be.get(1)==border.ini){
						if (be.get(0)==border.ini){
							other = 1;
						}
						isFirst = true;
						for (BorderMarkerInfo j:listOfBorders){
							if (border!=j){
								isFirst = isFirst && !(be.get(other)!=j.ini || be.get(other)!=j.end);
							}
						}
					}
				}

				Element lastElement = be;
				int nextValue = be.get(other);
				extraBorder.add(be);
				while (nextValue!=border.end){//if the end wasn't found
					for (int counter=0; counter < listOfBorderElements.size() && nextValue!=border.end; counter++){
						Element borderElement = listOfBorderElements.get(counter);
						if (borderElement!=lastElement){
							if (listOfBorderElements.get(counter).get(0)==nextValue){
								lastElement = listOfBorderElements.get(counter);
								nextValue = lastElement.get(1);
								extraBorder.add(lastElement);
							} else if (listOfBorderElements.get(counter).get(1)==nextValue){
								lastElement = listOfBorderElements.get(counter);
								nextValue = lastElement.get(0);
								extraBorder.add(lastElement);
							}
						}
					}
				}


				//insert the elements of the border in extras
				borderMarkers.add(border.name, extraBorder);
			}


			//putExtra(name, object);
			return true;
		}

		for (Element e:listOfBorderElements){
			String s = "";
			for (int i=0; i<e.getNumberOfPoints(); i++){
				s += " "+e.get(i);
			}
		}

		System.out.println(listOfBorderElements.size());

		Element orig = listOfBorderElements.get(0);
		List<Element> loop = new ArrayList<Elements.Element>();
		loop.add(orig);
		while (orig.get(1).compareTo(loop.get(loop.size()-1).get(0))!=0 && orig.get(1).compareTo(loop.get(loop.size()-1).get(1))!=0 ){

		}

		return true;
	}
}
