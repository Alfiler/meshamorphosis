package com.meshtransformer.meshformat.data;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

import com.meshtransformer.meshformat.data.Elements.Element;
import com.meshtransformer.meshformat.data.Extra.BorderMarkerInfo;
import com.meshtransformer.meshformat.data.Extra.BorderMarkers;

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
        //Collections.sort(listOfNodes);

        BorderMarkers bm = new BorderMarkers();

        Elements borderElements = getAllElements(listOfNodes, getDimensions());
        final CountDownLatch b1 = new CountDownLatch(listOfBorders.size());
        for (BorderMarkerInfo bmi:listOfBorders){
            if (bmi.type== BorderMarkerInfo.BMIType.ALL_MARKER_NODES) {
                bm.add(bmi.name, getAllBorderMarker(bmi, borderElements, b1));
            } else {
                b1.countDown();
            }
        }
        try {
            b1.await();

            System.out.println("Total: "+borderElements.size());

            cleanBorderElements(borderElements, bm);

            final CountDownLatch b2 = new CountDownLatch(listOfBorders.size());
            for (BorderMarkerInfo bmi:listOfBorders){
                if (bmi.type== BorderMarkerInfo.BMIType.CONSTRAINED) {
                    bm.add(bmi.name, getConstrainedMarker(bmi, borderElements, b2));
                } else {
                    b2.countDown();
                }
            }
            b2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int numMarkerElement = 0;
        for (String s:bm.getTags()){
            numMarkerElement+=bm.get(s).size();
            System.out.println(s+" "+bm.get(s).size());
        }
        System.out.println(" Total found in markers: "+ numMarkerElement);
        this.putExtra(MARKER_TAGS, bm);
        return true;
    }

    private Elements cleanBorderElements(Elements borderElements, BorderMarkers bm) {
        for (String tag:bm.getTags()){
            for (int i=0; i<bm.get(tag).size(); i++){
                borderElements.remove(bm.get(tag).get(i));
            }
        }
        return borderElements;
    }

    private Elements getAllBorderMarker(final BorderMarkerInfo bmi, final Elements borderElements, final CountDownLatch b){
        final Elements listOfMarkerElements = new Elements();//the place to save the elements of this marker
        new Thread() {
            public void run() {

                for (int i=0; i<bmi.borderNodes.length; i++){
                    Elements e1 = borderElements.getElementsWithNode(bmi.borderNodes[i]);
                    for (int j=i+1; j<bmi.borderNodes.length; j++){
                        Elements e2 = e1.getElementsWithNode(bmi.borderNodes[j]);
                        if(e2.size()>0){
                            for (int k=j+1; k<bmi.borderNodes.length; k++){
                                Elements e3 = e2.getElementsWithNode(bmi.borderNodes[k]);
                                if (e3.size()==1 && e3.get(0).getType()== Elements.ElementType.Triangle){
                                    listOfMarkerElements.add(e3.get(0));
                                }
                                else {
                                    for (int l=k+1; l<bmi.borderNodes.length; l++){
                                        Elements e4 = e3.getElementsWithNode(bmi.borderNodes[k]);
                                        if (e4.size()==1 && e4.get(0).getType()== Elements.ElementType.Rectangle){
                                            listOfMarkerElements.add(e4.get(0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                b.countDown();
            }
        }.start();
        return listOfMarkerElements;
    }

    private Elements getConstrainedMarker(final BorderMarkerInfo bmi, final Elements borderElements, final CountDownLatch b){
        final Elements listOfMarkerElements = new Elements();//the place to save the elements of this marker
        new Thread() {
            public void run() {

                Stack<Integer> internalNodes = new Stack<Integer>();
                internalNodes.push(bmi.insideNode);
                Stack<Integer> usedNodes = new Stack<Integer>();

                while (!internalNodes.empty()) {
                    int node = internalNodes.pop();
                    usedNodes.push(node);
                    Elements elements = borderElements.getElementsWithNode(node);
                    List<Integer> nodes = elements.getNodes();
                    for (int n : nodes) {
                        if (!usedNodes.contains(n)) {
                            internalNodes.add(n);
                        }
                    }
                    listOfMarkerElements.add(elements);
                }

                b.countDown();

            }
        }.start();
        return listOfMarkerElements;
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
        for (int i=0; i<listOfNodes.size()-1; i++){
            Elements tempElements = mElements.getElementsWithNode(listOfNodes.get(i));
            for (int j=i+1; j<listOfNodes.size(); j++){
                Elements tempElements2 = tempElements.getElementsWithNode(listOfNodes.get(j));
                if (dimensions==2 && tempElements2.size()>0){
                    if (tempElements2.size()==1){
                        result.add(tempElements2.formASubElement(new int[]{listOfNodes.get(i), listOfNodes.get(j)}));
                    }
                    /*e = tempElements.formASubElement(new int[]{listOfNodes.get(i),listOfNodes.get(j)});
					if (e!=null){
						result.add(e);
					}*/
                } else if (dimensions==3 && tempElements2.size()>0){
                    for (int k=j+1; k<listOfNodes.size(); k++){
                        Elements tempElements3 = tempElements2.getElementsWithNode(listOfNodes.get(k));
                        if (tempElements3.size()==1){
                            Element e = tempElements3.formASubElement(new int[]{listOfNodes.get(i), listOfNodes.get(j),listOfNodes.get(k)});
                            if (e==null){
                                for (int l=k+1; k<listOfNodes.size() && e==null; l++){
                                    e = tempElements3.formASubElement(new int[]{listOfNodes.get(i), listOfNodes.get(j),listOfNodes.get(k), listOfNodes.get(l)});
                                }
                            }
                            if (e!=null) {
                                result.add(e);
                            }
                        }
						/*e = tempElements.formASubElement(new int[]{listOfNodes.get(i),listOfNodes.get(j),listOfNodes.get(k)});
						if (e!=null){
							result.add(e);
						}
						for (int l=k+1; l<listOfNodes.size(); l++){
							e = tempElements.formASubElement(new int[]{listOfNodes.get(i),listOfNodes.get(j),listOfNodes.get(k),listOfNodes.get(l)});
							if (e!=null && e.getType()==ElementType.Rectangle){
								result.add(e);
							}
						}*/
                    }
                }
            }
        }
        return result;
    }
}
