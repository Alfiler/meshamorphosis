package data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Elements {

    public enum ElementType{
        Line, Triangle, Rectangle, Tetrahedral, Hexahedral, Wedge, Pyramid
    }

    public static class Element implements Comparable<Element>{
        private int[] conectedPoints;
        private ElementType type;
        //private Elements subElements;

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

		/*public boolean isSubElement(Element e){
			if(this.conectedPoints.length>=e.conectedPoints.length)
			for (int i = 0; i<subElements.size(); i++){
				if (subElements.get(i).compareTo(e)==0){
					return true;
				}
				if (subElements.get(i).isSubElement(e)){
					return true;
				}
			}
			return false;
		}*/

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
                if (js.length==2 && this.areConectedNodes(js[0], js[1])){
                    try {
                        return new Element(ElementType.Line, js);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    //TODO - for 3 dimensions
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
    private Hashtable<Integer, List<Element>> mNodes = new Hashtable<Integer, List<Element>>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Elements(){
        mElements = new ArrayList<Element>();
    }

    public Elements(List<Element> elements){
        mElements = new ArrayList<Element>();
        for (Element e:elements){
            this.add(e);
        }
    }

    public void add(Element e){
        lock.writeLock().lock();
        try {
            if (!mElements.contains(e)) {
                mElements.add(e);
                for (int j = 0; j < e.conectedPoints.length; j++) {
                    Integer n = e.conectedPoints[j];
                    if (!mNodes.containsKey(n)) {
                        mNodes.put(n, new ArrayList<Elements.Element>());
                    }
                    mNodes.get(n).add(e);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void add(Elements es){
        lock.writeLock().lock();
        try {
            for (int i=0; i<es.size(); i++){
                this.add(es.get(i));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Element get(int i){
        lock.readLock().lock();
        try{
            return mElements.get(i);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void remove(Element e){
        lock.writeLock().lock();
        try{
            mElements.remove(e);
            for (int i=0; i<e.conectedPoints.length; i++){
                int point= e.conectedPoints[i];
                mNodes.get(point).remove(e);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size(){
        lock.readLock().lock();
        try{
            return mElements.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean contains(Element e){
        lock.readLock().lock();
        try{
            return mElements.contains(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Element formAElement(int[] js){
        lock.readLock().lock();
        try{
            List<Element> elementsWithNode = mNodes.get(js[0]);
            for (Element e:elementsWithNode){
                Element result = e.formASubElement(js);
                if (result!=null){
                    return result;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Elements getElementsWithNode(int node){
        lock.readLock().lock();
        try{
            return new Elements(mNodes.get(new Integer(node)));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Integer> getNodes(){
        lock.readLock().lock();
        try{
            List<Integer> result = new ArrayList<Integer>();
            for (int i=0; i<this.size(); i++){
                result.addAll(this.get(i).getNodes());
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
}
