package com.meshtransformer.meshformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		
		Path f = Paths.get("C:\\Users\\Alf\\Desktop\\cañerias.gid\\ca2.msh"); 
		Path out = Paths.get("C:\\SU2\\ejemplo\\mesh_NACA0012_inv.su2");
		Path out2 = Paths.get("C:\\yepeee2.txt");
		GIDMSH s = new GIDMSH();
		SU2 outs = new SU2();
		Mesh m = new Mesh();
		try {
			outs.read(out, m);
			outs.write(out2, m);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
