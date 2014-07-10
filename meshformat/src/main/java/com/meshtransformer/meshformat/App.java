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
public class App {
    public static void main(String[] args) {

        Path f = Paths.get("C:\\Users\\Alf\\Dropbox\\Modelos gid\\Pantalones 3d\\Pantalones 0,1.gid\\Pantalones.msh");
        //Path out = Paths.get("C:\\SU2\\ejemplo\\mesh_NACA0012_inv.su2");
        Path out2 = Paths.get("C:\\Users\\Alf\\Dropbox\\Modelos gid\\Pantalones 3d\\Pantalones 0,1.gid\\Pantalones.SU2");
        Path bmipath = Paths.get("C:\\Users\\Alf\\Dropbox\\Modelos gid\\Pantalones 3d\\Pantalones 0,1.gid\\BMI.txt");
        Path borderpath = Paths.get("C:\\Users\\Alf\\Dropbox\\Modelos gid\\Pantalones 3d\\Pantalones 0,1.gid\\borders.txt");
        GIDMSH s = new GIDMSH();
        SU2 outs = new SU2();
        Mesh m = new Mesh();
        List<BorderMarkerInfo> border = null;

        List<Integer> lista = extractBorderNodes(borderpath);
        try {
            border = readBMI(bmipath,false);
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

    private static List<Integer> extractBorderNodes(Path in) {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        try {
            Scanner s = new Scanner(new File(in.toString()));
            String line = "";

            while (s.hasNext()) {
                line = s.nextLine();
                String[] coord = line.split("\\s+");
                ret.add(Integer.parseInt(coord[0]) - 1);
            }
            s.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }


    private static List<BorderMarkerInfo> readBMI(Path in, boolean zeroBased) throws FileNotFoundException {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        Scanner s = new Scanner(new File(in.toString()));
        String line = s.nextLine();

        List<BorderMarkerInfo> border = new ArrayList<Extra.BorderMarkerInfo>();

        int numBMI = Integer.parseInt(line);
        line = s.nextLine();
        for (int i = 0; i < numBMI; i++) {
            BorderMarkerInfo bmi = new Extra.BorderMarkerInfo();
            bmi.name = line;
            line = s.nextLine();
            bmi.type = line.equals("1")? BorderMarkerInfo.BMIType.ALL_MARKER_NODES:BorderMarkerInfo.BMIType.CONSTRAINED;
            line = s.nextLine();
            bmi.insideNode = Integer.parseInt(line)- (zeroBased ? 0 : 1);
            boolean hasnode = true;

            List<Integer> borderNodes = new ArrayList<Integer>();
            while (hasnode) {
                if (s.hasNext()) {
                    line = s.nextLine();
                    String nodeStr = line.split(" ")[0];
                    try {
                        int num = Integer.parseInt(nodeStr);
                        num = num - (zeroBased ? 0 : 1);
                        borderNodes.add(num);
                    } catch (NumberFormatException e) {
                        hasnode = false;
                    }
                    bmi.borderNodes = borderNodes.toArray(new Integer[0]);
                } else {
                    hasnode = false;
                }
            }
            border.add(bmi);
        }
        return border;
    }
}
