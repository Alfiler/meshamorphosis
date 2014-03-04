package formats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.Elements.ElementType;
import data.Mesh;

public class SU2 implements FormatInterface {

	private int dimensions;
	private int elem;
	private int points;

	public boolean read(Path in, Mesh m) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(in.toString()));
		String linea = getLine(sc);
		Pattern patern = Pattern.compile("(\\w+)=\\s*(\\d+)");
		Matcher match = patern.matcher(linea);
		System.out.println(match.matches()+" "+match.group(0)+" |"+match.group(1)+"|");
		if (!match.matches() || !match.group(1).matches("NDIME")){
			sc.close();
			return false;
		}
		dimensions = Integer.parseInt(match.group(2));
		//elements
		linea = getLine(sc);
		match = patern.matcher(linea);
		if (!match.matches() || !match.group(1).matches("NELEM")){
			sc.close();
			return false;
		}
		elem = Integer.parseInt(match.group(2));
		for (int i=0; i<elem; i++){
			linea = getLine(sc);
			String[] str_elements = linea.split("\t");
			ElementType et = null;
			ArrayList<Integer> lis = new ArrayList<Integer>();
			lis.add(Integer.parseInt(str_elements[1]));
			lis.add(Integer.parseInt(str_elements[2]));
			switch (Integer.parseInt(str_elements[0])){
			case 3: et = ElementType.Line; break;
			case 5: et = ElementType.Triangle; lis.add(Integer.parseInt(str_elements[3])); break;
			case 9: et = ElementType.Rectangle; lis.add(Integer.parseInt(str_elements[3])); lis.add(Integer.parseInt(str_elements[4])); break;
			case 10: et = ElementType.Tetrahedral; lis.add(Integer.parseInt(str_elements[3])); lis.add(Integer.parseInt(str_elements[4])); break;
			case 12: et = ElementType.Hexahedral; lis.add(Integer.parseInt(str_elements[3])); lis.add(Integer.parseInt(str_elements[4])); lis.add(Integer.parseInt(str_elements[5])); lis.add(Integer.parseInt(str_elements[6])); lis.add(Integer.parseInt(str_elements[7])); lis.add(Integer.parseInt(str_elements[8])); break;
			case 13: et = ElementType.Wedge; lis.add(Integer.parseInt(str_elements[3])); lis.add(Integer.parseInt(str_elements[4])); lis.add(Integer.parseInt(str_elements[5])); lis.add(Integer.parseInt(str_elements[6])); break;
			case 14: et = ElementType.Pyramid; lis.add(Integer.parseInt(str_elements[3])); lis.add(Integer.parseInt(str_elements[4])); lis.add(Integer.parseInt(str_elements[5])); break;
			}
			m.e.add(m.e.new Element(et, lis));
			
		}
		//points
		linea = getLine(sc);

		match = patern.matcher(linea);
		if (!match.matches() || !match.group(1).matches("NPOIN")){
			sc.close();
			return false;
		}
		points = Integer.parseInt(match.group(2));
		for (int i=0; i<points; i++){
			linea = getLine(sc);
			String[] str_points = linea.split("\t");
			System.out.println(str_points[1]+" | "+new BigDecimal(str_points[1])+" | "+(new BigDecimal(str_points[1])).toEngineeringString()+" | "+(new BigDecimal(str_points[1])).toString());
			switch (dimensions){
			case 1: m.p.add(new BigDecimal(str_points[1])); break;
			case 2: m.p.add(new BigDecimal(str_points[1]), new BigDecimal(str_points[2])); break;
			case 3: m.p.add(new BigDecimal(str_points[1]), new BigDecimal(str_points[2]), new BigDecimal(str_points[3])); break;
			}
		}
		sc.close();
		return false;
	}

	public boolean write(Path out, Mesh m) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(
				out, Charset.defaultCharset());
		writer.append("NDIME= "+m.p.getDimensions());
		writer.newLine();
		writer.append("NELEM= "+m.e.size());
		writer.newLine();

		for (int i=0; i<m.e.size(); i++){
			int id = 0;
			switch (m.e.get(i).getType()){
			case Line:id = 3; break;
			case Triangle:id = 5; break;
			case Rectangle:id = 9; break;
			case Tetrahedral:id = 10; break;
			case Hexahedral:id = 12; break;
			case Wedge:id = 13; break;
			case Pyramid:id = 14; break;
			}
			writer.append(Integer.toString(id));
			writer.append("\t");
			for (int j=0; j<m.e.get(i).getNumberOfPoints(); j++){
				writer.append(Integer.toString(m.e.get(i).get(j)));
				writer.append("\t");
			}
			writer.append(Integer.toString(i));
			writer.newLine();
		}

		writer.append("NPOIN= "+m.p.size());
		writer.newLine();

		for (int i=0; i<m.p.size(); i++){
			String linea = "";
			switch(m.p.getDimensions()){
			case 3: linea = "\t"+m.p.get(i).getZ().toEngineeringString()+linea;
			case 2: linea = "\t"+m.p.get(i).getY().toEngineeringString()+linea;
			case 1: linea = "\t"+m.p.get(i).getX().toEngineeringString()+linea;
			}
			linea = linea +"\t"+Integer.toString(i);
			writer.append(linea);
			writer.newLine();
		}
		writer.flush();
		writer.close();
		return true;
	}
	
	private String getLine(Scanner sc){
		String linea = sc.nextLine();
		while (linea.matches("\\s*%\\w*") || linea.matches("\\s*")){
			linea = sc.nextLine();
		}
		return linea;
	}
}
