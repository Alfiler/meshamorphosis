package data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Points {

	private List<Point> pointsList;
	private int dimensions = -1;
	private BigDecimal bdzerovalue = new BigDecimal(0.0);
	
	public class Point {
		private BigDecimal x = new BigDecimal(0.0);
		private BigDecimal y = new BigDecimal(0.0);
		private BigDecimal z = new BigDecimal(0.0);
		
		public Point(BigDecimal double1, BigDecimal double2, BigDecimal double3){
			this.x = double1;
			this.y = double2;
			this.z = double3;
		}
		
		public Point(BigDecimal x, BigDecimal y){
			this.x = x;
			this.y = y;
		}
		
		public Point(BigDecimal x){
			this.x = x;
		}

		public BigDecimal getX() {
			return x;
		}

		public void setX(BigDecimal x) {
			this.x = x;
		}

		public BigDecimal getY() {
			return y;
		}

		public void setY(BigDecimal y) {
			this.y = y;
		}

		public BigDecimal getZ() {
			return z;
		}

		public void setZ(BigDecimal z) {
			this.z = z;
		}
	}
	
	public Points(){
		pointsList = new ArrayList<Point>();
	}
	
	public int getDimensions(){
		if (dimensions==-1){
		int  i=0;
		boolean xzero = false; //false if all the dimension is zero
		boolean yzero = false;
		boolean zzero = false;
		while (i<pointsList.size() && (!xzero || !yzero || !zzero)){
			xzero = xzero || (pointsList.get(i).getX().compareTo(bdzerovalue)!=0);
			yzero = yzero || (pointsList.get(i).getY().compareTo(bdzerovalue)!=0);
			zzero = zzero || (pointsList.get(i).getZ().compareTo(bdzerovalue)!=0);
			i++;
		}
		int dim = 0;
		if (xzero){
			dim++;
		}
		if (yzero){
			dim++;
		}
		if (zzero){
			dim++;
		}
		dimensions= dim;
		}
		return dimensions;
	}
	
	public Point get(int i){
		return pointsList.get(i);
	}
	
	public boolean add(BigDecimal double1, BigDecimal double2, BigDecimal double3){
		dimensions =-1;
		return pointsList.add(new Point(double1, double2, double3));
	}
	
	public boolean add(BigDecimal double1, BigDecimal double2){
		dimensions =-1;
		return pointsList.add(new Point(double1, double2));
	}
	
	public boolean add(BigDecimal double1){
		dimensions =-1;
		return pointsList.add(new Point(double1));
	}
	
	public int size(){
		return pointsList.size();
	}
}
