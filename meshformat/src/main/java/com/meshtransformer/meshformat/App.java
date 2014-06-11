package com.meshtransformer.meshformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        Path f = Paths.get("C:\\SU2\\Esfera\\esfera.msh");
        //Path out = Paths.get("C:\\SU2\\ejemplo\\mesh_NACA0012_inv.su2");
        Path out2 = Paths.get("C:\\SU2\\Esfera\\esfera3.SU2");
        GIDMSH s = new GIDMSH();
        SU2 outs = new SU2();
        Mesh m = new Mesh();
        List<BorderMarkerInfo> border = new ArrayList<Extra.BorderMarkerInfo>();

        BorderMarkerInfo bmi = new Extra.BorderMarkerInfo();
        bmi.borderNodes = new int[]{25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40};
        bmi.insideNode = 45;
        bmi.name = "grande";
        border.add(bmi);

        bmi = new Extra.BorderMarkerInfo();
        bmi.borderNodes = new int[]{26, 27, 28, 34, 35, 39, 40, 1, 3, 6, 13, 14};
        bmi.insideNode = 8;
        bmi.name = "pequeño";
        border.add(bmi);

        bmi = new Extra.BorderMarkerInfo();
        bmi.borderNodes = new int[]{1, 3, 6, 13, 14, 35, 39, 25, 29, 30, 31, 32, 33, 36, 37, 38};
        bmi.insideNode = 2;
        bmi.name = "mediano";
        border.add(bmi);

        List<Integer> lista = extractBorderNodes(Paths.get("C:\\SU2\\Esfera\\esfera_bordes.txt"));
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
