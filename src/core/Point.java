package core;


public class Point {
	
	private double lat;
	
	private double lng;

	public Point(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}
	
	public Point(double[] coord) {
		super();
		this.lat = coord[0];
		this.lng = coord[1];
	}

	public Point(com.vividsolutions.jts.geom.Point point) {
		this.lat = point.getX();
		this.lng = point.getY();
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "("+lat + " " + lng+")";
	}

	

}
