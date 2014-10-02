package com.meshtransformer.meshformat.formats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.meshtransformer.meshformat.data.Elements;
import com.meshtransformer.meshformat.data.Elements.Element;
import com.meshtransformer.meshformat.data.Elements.ElementType;
import com.meshtransformer.meshformat.data.Extra.BorderMarkers;
import com.meshtransformer.meshformat.data.Mesh;

public class SU2 implements FormatInterface {

	private int dimensions;
	private int elem;
	private int points;

	public boolean read(Path in, Mesh m) throws Exception {
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
			int[] lis = new int[str_elements.length-1];
			lis[0]=Integer.parseInt(str_elements[1]);
			lis[1]=(Integer.parseInt(str_elements[2]));
			switch (Integer.parseInt(str_elements[0])){
			case 3: et = ElementType.Line; break;
			case 5: et = ElementType.Triangle; lis[2]=(Integer.parseInt(str_elements[3])); break;
			case 9: et = ElementType.Rectangle; lis[2]=(Integer.parseInt(str_elements[3])); lis[3]=(Integer.parseInt(str_elements[4])); break;
			case 10: et = ElementType.Tetrahedra; lis[2]=(Integer.parseInt(str_elements[3])); lis[3]=(Integer.parseInt(str_elements[4])); break;
			case 12: et = ElementType.Hexahedral; lis[2]=(Integer.parseInt(str_elements[3])); lis[3]=(Integer.parseInt(str_elements[4])); lis[4]=(Integer.parseInt(str_elements[5])); lis[5]=(Integer.parseInt(str_elements[6])); lis[6]=(Integer.parseInt(str_elements[7])); lis[7]=(Integer.parseInt(str_elements[8])); break;
			case 13: et = ElementType.Wedge; lis[2]=(Integer.parseInt(str_elements[3])); lis[3]=(Integer.parseInt(str_elements[4])); lis[4]=(Integer.parseInt(str_elements[5])); lis[5]=(Integer.parseInt(str_elements[6])); break;
			case 14: et = ElementType.Pyramid; lis[2]=(Integer.parseInt(str_elements[3])); lis[3]=(Integer.parseInt(str_elements[4])); lis[4]=(Integer.parseInt(str_elements[5])); break;
			}
			m.mElements.add(new Element(et, lis));

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
			case 1: m.mPoints.add(new BigDecimal(str_points[1])); break;
			case 2: m.mPoints.add(new BigDecimal(str_points[1]), new BigDecimal(str_points[2])); break;
			case 3: m.mPoints.add(new BigDecimal(str_points[1]), new BigDecimal(str_points[2]), new BigDecimal(str_points[3])); break;
			}
		}
		sc.close();
		return false;
	}

	public boolean write(Path out, Mesh m) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(
				out, Charset.defaultCharset());
		writer.append("NDIME= "+m.mPoints.getDimensions());
		writer.newLine();
		writer.append("NELEM= "+m.mElements.size());
		writer.newLine();

		for (int i=0; i<m.mElements.size(); i++){
			int id = correspondence(m.mElements.get(i).getType());

			writer.append(Integer.toString(id));
			writer.append("\t");
			for (int j=0; j<m.mElements.get(i).getNumberOfPoints(); j++){
				writer.append(Integer.toString(m.mElements.get(i).get(j)));
				writer.append("\t");
			}
			writer.append(Integer.toString(i));
			writer.newLine();
		}

		writer.append("NPOIN= "+m.mPoints.size());
		writer.newLine();

		DecimalFormat format = new DecimalFormat("0.##################E00");
		format.setMinimumFractionDigits(15);
		DecimalFormatSymbols temp = format.getDecimalFormatSymbols();
		temp.setDecimalSeparator('.');
		temp.setExponentSeparator("e");
		format.setDecimalFormatSymbols(temp);

		for (int i=0; i<m.mPoints.size(); i++){
			String linea = "";
			String sresult = "";
			switch(m.mPoints.getDimensions()){
			case 3: sresult = format.format(m.mPoints.get(i).getZ());
			if (!sresult.contains("e-")) { //don't blast a negative sign
				sresult = sresult.replace("e", "e+");
			}
			linea = "\t"+sresult+linea;
			case 2: sresult = format.format(m.mPoints.get(i).getY());
			if (!sresult.contains("e-")) { //don't blast a negative sign
				sresult = sresult.replace("e", "e+");
			}
			linea = "\t"+sresult+linea;
			case 1: sresult = format.format(m.mPoints.get(i).getX());
			if (!sresult.contains("e-")) { //don't blast a negative sign
				sresult = sresult.replace("e", "e+");
			}
			linea = "\t"+sresult+linea;
			}
			linea = linea +"\t"+Integer.toString(i);
			writer.append(linea);
			writer.newLine();
		}

		if (m.existExtra(Mesh.MARKER_TAGS)){
			BorderMarkers bm = (BorderMarkers) m.getExtra(Mesh.MARKER_TAGS);
			writer.append("NMARK= "+bm.getTags().size());
			writer.newLine();
			for (String tag:bm.getTags()){
				Elements tagborder = bm.get(tag);
				writer.append("MARKER_TAG= ".concat(tag));
				writer.newLine();
				writer.append("MARKER_ELEMS= ".concat(""+tagborder.size()));
				writer.newLine();
				for (int i=0; i<tagborder.size(); i++){
					writer.append(""+correspondence(tagborder.get(i).getType())+"\t"+tagborder.get(i).toString("\t"));
					writer.newLine();
				}
			}
		}

		writer.flush();
		writer.close();
		return true;
	}

	private String getLine(Scanner sc){
		String linea = sc.nextLine();
		while (linea.matches("\\s*%(\\w|\\W)*") || linea.matches("\\s*")){
			linea = sc.nextLine();
		}
		return linea;
	}
	private int correspondence(ElementType etype){
		switch (etype){
		case Line:return 3; 
		case Triangle:return 5; 
		case Rectangle:return 9; 
		case Tetrahedra:return 10;
		case Hexahedral:return 12; 
		case Wedge:return 13; 
		default :return 14; //pyramid
		}
	}
}
