/**
 * 
 */

/**
 * 
 * @author etlove
 */
public class PointSET {
	/**
	 * 
	 */
	private SET<Point2D> points;
	/**
	 * Construct an empty set of points.
	 */
	public PointSET() {
		points = new SET<Point2D>();
	}
	/**
	 * Is the set empty?
	 * 
	 * @return true if empty else false
	 */
	public boolean isEmpty() {
		return points.isEmpty();
	}
	/**
	 * number of points in the set
	 * 
	 * @return number of points in the set
	 */
	public int size() {
		return points.size();
	}
	/**
	 * add the point p to the set (if it is not already in the set)
	 * 
	 * @param p
	 */
	public void insert(Point2D p) {
		points.add(p);
	}
	/**
	 * does the set contain the point p?
	 * 
	 * @param p
	 *            Point2D object
	 * @return true of p exists else false
	 */
	public boolean contains(Point2D p) {
		return points.contains(p);
	}
	/**
	 * draw all of the points to standard draw.
	 */
	public void draw() {
		for (Point2D p : points) {
			p.draw();
		}
	}
	/**
	 * All points in the set that are inside the rectangle.
	 * @param rect rectangle
	 * @return iterator of all points
	 */
	public Iterable<Point2D> range(RectHV rect) {
		SET<Point2D> result = new SET<Point2D>();
		for (Point2D p : points) {
			if (inRange(rect, p)) {
				result.add(p);
			}
		}
		return result;
	}
	/**
	 * A nearest neighbor in the set to p; null if set is empty.
	 * @param p Point2D object
	 * @return nearest neighbor in the set to p; null if set is empty
	 */
	public Point2D nearest(Point2D p) {
		if (isEmpty()) return null;
		
		double min = Double.MAX_VALUE;
		Point2D candidate = null;
		for (Point2D point : points) {
			double distance = point.distanceTo(p);
			if (distance < min) {
				min = distance;
				candidate = point;
			}
		}
		return candidate;
	}
	/**
	 * Check if Point2D p is within the range of RectHV rect
	 * @param rect RectHV object
	 * @param p Point2D object
	 * @return true if within range else false
	 */
	private static boolean inRange(RectHV rect, Point2D p) {
		double x = p.x();
		double y = p.y();
		if (x >= rect.xmin() && x <= rect.xmax() &&
				y >= rect.ymin() && y <= rect.ymax()) {
			return true;
		} else {
			return false;
		}
	}
}
