package formats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Elements.Element;
import data.Elements.ElementType;
import data.Mesh;

public class GIDMSH implements FormatInterface{

	public boolean read(Path in, Mesh m) throws Exception {
		Scanner s = new Scanner(new File(in.toString()));
		String line = s.nextLine();
		ElementType[] values = ElementType.values();
		String regexElems = "(";
		for (ElementType e:values){
			regexElems += e.toString();
			if (!e.equals(values[values.length-1])){
				regexElems+="|";
			} else {
				regexElems+=")";
			}
		}
		String regexline = "MESH\\s+dimension\\s+\\d\\s+ElemType\\s+"+regexElems+"\\s+Nnode\\s+\\d\\s*";
		Pattern p = Pattern.compile(regexline);
		Matcher mat = p.matcher(line);
		if(!mat.matches()){
			s.close();
			return false;
		}
		ElementType e = ElementType.valueOf(mat.group(1));
		line = s.nextLine();
		if(!line.matches("Coordinates")){
			s.close();
			return false;
		}
		line = s.nextLine();
		while(!line.matches("End Coordinates")){
			String[] coord = line.split("\\s+");
			m.mPoints.add(new BigDecimal(coord[2]), new BigDecimal(coord[3]), new BigDecimal(coord[4]));
			line = s.nextLine();
		}
		s.nextLine();
		line = s.nextLine();
		if(!line.matches("Elements")){
			s.close();
			return false;
		}
		line = s.nextLine();
		while(!line.matches("End Elements")){
			String[] elemStr = line.split("\\s+");
			ArrayList<Integer> listElem = new ArrayList<Integer>();
			for (int i=1; i<elemStr.length; i++){
				listElem.add(Integer.parseInt(elemStr[i])-1);
			}
			m.mElements.add(new Element(e, listElem));
			line = s.nextLine();
		}
		s.close();
		return true;
	}

	public boolean write(Path out, Mesh m) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean getBordersFromNodesList(Path nodesListFile) throws FileNotFoundException{
		Scanner s = new Scanner(new File(nodesListFile.toString()));
		
		
		return true;
	}

}
