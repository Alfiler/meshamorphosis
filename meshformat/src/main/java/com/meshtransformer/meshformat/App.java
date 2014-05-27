package com.meshtransformer.meshformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.Extra;
import data.Extra.BorderMarkerInfo;
import data.Mesh;
import formats.GIDMSH;
import formats.SU2;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
	{

		Path f = Paths.get("C:\\Users\\Alf\\Desktop\\channel propio gid\\channel propio gid grande.msh"); 
		//Path out = Paths.get("C:\\SU2\\ejemplo\\mesh_NACA0012_inv.su2");
		Path out2 = Paths.get("C:\\SU2\\channel propio gid\\channel propio gid grande con bordes.txt");
		GIDMSH s = new GIDMSH();
		SU2 outs = new SU2();
		Mesh m = new Mesh();
		List<BorderMarkerInfo> border = new ArrayList<Extra.BorderMarkerInfo>();
		
		BorderMarkerInfo bmi = new Extra.BorderMarkerInfo();
		bmi.borderNodes = new int[]{0, 59};
		bmi.insideNode = 39;
		bmi.name = "inlet";
		border.add(bmi);
		
		bmi = new Extra.BorderMarkerInfo();
		bmi.borderNodes = new int[]{0, 165};
		bmi.name = "lower_wall";
		bmi.insideNode = 75;
		border.add(bmi);
		
		bmi = new Extra.BorderMarkerInfo();
		bmi.borderNodes = new int[]{165, 175};
		bmi.name = "outlet";
		bmi.insideNode = 174;
		border.add(bmi);
		
		bmi = new Extra.BorderMarkerInfo();
		bmi.borderNodes = new int[]{59, 175};
		bmi.name = "upper_wall";
		bmi.insideNode = 102;
		border.add(bmi);
		
		
		List<Integer> lista = extractBorderNodes(Paths.get("C:\\Users\\Alf\\Desktop\\listado borde.txt"));
				try {
					s.read(f, m);
					m.addBordersMarkers(lista, border);
					outs.write(out2, m);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}

	private static List<Integer> extractBorderNodes(Path in){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		try {
			Scanner s = new Scanner(new File(in.toString()));
			String line = "";

			while(s.hasNext()){
				line = s.nextLine();
				String[] coord = line.split("\\s+");
				ret.add(Integer.parseInt(coord[0])-1);
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
