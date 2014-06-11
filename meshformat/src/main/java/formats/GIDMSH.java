package formats;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Elements;
import data.Elements.Element;
import data.Elements.ElementType;
import data.Mesh;

public class GIDMSH implements FormatInterface{

    public boolean read(Path in, Mesh m) throws Exception {
        Scanner s = new Scanner(new File(in.toString()));
        //Extract the element type in the header of the file
        try {
            ElementType e = extractElementType(s);

            //Start of extracting the nodes with coordinates
            boolean resul = extractNodes(s, m);
            if (!resul) {
                return false;
            }
            //Start of extracting the elements
            resul = extractElements(s, m, e);
            if (!resul) {
                return false;
            }
        } finally {
            s.close();
        }
        return true;
    }

    private ElementType extractElementType(Scanner s){
        String line = s.nextLine();
        ElementType[] values = ElementType.values();
        String regexHead= "(";
        for (ElementType e:values){
            regexHead += e.toString();
            if (!e.equals(values[values.length-1])){
                regexHead+="|";
            } else {
                regexHead+=")";
            }
        }
        String regexline = "MESH\\s+dimension\\s+\\d\\s+ElemType\\s+"+regexHead+"\\s+Nnode\\s+\\d\\s*";
        Pattern p = Pattern.compile(regexline);
        Matcher mat = p.matcher(line);
        if(!mat.matches()){
            s.close();
        }
        return ElementType.valueOf(mat.group(1));
    }

    private boolean extractNodes(Scanner s, Mesh m){
        String line = s.nextLine();
        if(!line.matches("Coordinates")){
            s.close();
            return false;
        }
        line = s.nextLine();
        while(!line.matches("End Coordinates")){
            String[] coord = line.split("\\s+");
            int ini=coord[0].length()==0?1:0;
            m.mPoints.add(new BigDecimal(coord[ini+1]), new BigDecimal(coord[ini+2]), new BigDecimal(coord[ini+3]));
            line = s.nextLine();
        }
        s.nextLine();
        return true;
    }

    private boolean extractElements(Scanner s, Mesh m, ElementType e) throws Exception {
        String line = s.nextLine();
        if(!line.matches("Elements")){
            s.close();
            return false;
        }
        line = s.nextLine();
        final ArrayBlockingQueue<int[]> lines = new ArrayBlockingQueue<int[]>(100);
        CyclicBarrier b = new CyclicBarrier(2);
        new convertLine(lines, m.mElements, e, b).start();
        while(!line.matches("End Elements")){
            String[] elemStr = line.split("\\s+");
            int[] listElem = new int[elemStr.length-1];
            for (int i=1; i<elemStr.length; i++){
                listElem[i-1]=(Integer.parseInt(elemStr[i])-1);//zero based
            }
            /*m.mElements.add(new Element(e, listElem));*/
            lines.put(listElem);
            line = s.nextLine();
        }
        lines.put(new int[0]);
        b.await();
        return true;
    }

    private class convertLine extends Thread{
        private ArrayBlockingQueue<int[]> lines;
        private CyclicBarrier b;
        private Elements elements;
        private ElementType e;

        public convertLine(ArrayBlockingQueue<int[]> lines, Elements elements, ElementType e, CyclicBarrier b) {
            this.lines = lines;
            this.elements = elements;
            this.e = e;
            this.b = b;
        }
        public void run(){
            int[] listElem;
            try {
                listElem = lines.take();
                while(!(listElem.length==0)){
                    elements.add(new Element(e, listElem));
                    listElem = lines.take();
                }

                b.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public boolean write(Path out, Mesh m) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

}
