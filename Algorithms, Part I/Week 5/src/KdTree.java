/**
 * 
 * @author etlove
 */
public class KdTree {
	/**
	 * 
	 */
	private static final boolean VERTICAL = true;
	private static final boolean LEFT = true;
	/**
	 * Root node;
	 */
	private Node root;
	/**
	 * Size of set;
	 */
	private int size = 0;
	/**
	 * Node object.
	 * @author etlove
	 */
	private class Node {
		private Point2D point;
		private RectHV rect;
		private boolean direction;
		private Node left;
		private Node right;
		/**
		 * 
		 * @param p
		 */
		public Node(Point2D point, RectHV rect, boolean direction) {
			this.point = point;
			this.rect = rect;
			this.direction = direction;
		}
		@Override
		public String toString() {
			return this.point.toString();
		}
	}
	/**
	 * Is the set empty?
	 * @return true if empty else false
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	/**
	 * number of points in the set.
	 * @return number of points in the set
	 */
	public int size() {
		return size;
	}
	/**
	 * add the point p to the set (if it is not already in the set).
	 * @param p
	 */
	public void insert(Point2D p) {
		root = insert(root, null, p);
	}
	
	private Node insert(Node node, Node parent, Point2D p) {
		if (node == null) {
			size++;
			//
			boolean direction;
			double xmin = 0.0, ymin = 0.0;
			double xmax = 1.0, ymax = 1.0;
			//
			if (parent != null) {
				direction = !parent.direction;
				if (parent.direction == VERTICAL) {
					ymin = parent.rect.ymin();
					ymax = parent.rect.ymax();
					if (p.x() < parent.point.x()) {
						xmin = parent.rect.xmin();
						xmax = parent.point.x();
					} else {
						xmin = parent.point.x();
						xmax = parent.rect.xmax();
					}
				} else {
					xmin = parent.rect.xmin();
					xmax = parent.rect.xmax();
					if (p.y() < parent.point.y()) {
						ymin = parent.rect.ymin();
						ymax = parent.point.y();
					} else {
						ymin = parent.point.y();
						ymax = parent.rect.ymax();
					}
				}
			} else {
				direction = VERTICAL;
			}
			RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
			return new Node(p, rect, direction);
		} else {
			if (node.point.equals(p)) return node;
		}
		
		boolean side = getSide(node, p);
		if (side == LEFT) {
			node.left = insert(node.left, node, p);
		} else {
			node.right = insert(node.right, node, p);
		}
		return node;
	}
	/**
	 * does the set contain the point p?
	 * @param p Point2D object
	 * @return true of p exists else false
	 */
	public boolean contains(Point2D p) {
		return getNode(root, p) != null;
	}
	/**
	 * 
	 * @param node
	 * @param p
	 * @return
	 */
	private Node getNode(Node node, Point2D p) {
		if (node == null) return null;
		
		if (node.point.equals(p)) return node;
		
		boolean side = getSide(node, p);
		if (side == LEFT) {
			return getNode(node.left, p);
		} else {
			return getNode(node.right, p);
		}
	}
	/**
	 * draw all of the points to standard draw.
	 */
	public void draw() {
		draw(root, null);
	}
	
	private void draw(Node node, Node parent) {
		if (node == null) return;
		// Draw point
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(.05);
		node.point.draw();
		// Draw line
		StdDraw.setPenRadius(.01);
		double x1, y1, x2, y2;
		if (node.direction == VERTICAL) {
			StdDraw.setPenColor(StdDraw.RED);
			x1 = node.point.x();
			x2 = node.point.x();
			if (parent != null) {
				if (node.point.y() < parent.point.y()) {
					y1 = parent.rect.ymin();
					y2 = parent.point.y();
				} else {
					y1 = parent.point.y();
					y2 = parent.rect.ymax();
				}
			} else {
				y1 = 0.0;
				y2 = 1.0;
			}
		} else {
			StdDraw.setPenColor(StdDraw.BLUE);
			if (node.point.x() < parent.point.x()) {
				x1 = parent.rect.xmin();
				x2 = parent.point.x();
			} else {
				x1 = parent.point.x();
				x2 = parent.rect.xmax();
			}
			y1 = node.point.y();
			y2 = node.point.y();
		}
		Point2D start = new Point2D(x1, y1);
		Point2D end = new Point2D(x2, y2);
		start.drawTo(end);
		// Draw left and right node
		draw(node.left, node);
		draw(node.right, node);
	}
	/**
	 * All points in the set that are inside the rectangle.
	 * @param rect
	 * @return iterator of all points
	 */
	public Iterable<Point2D> range(RectHV rect) {
		SET<Point2D> result = new SET<Point2D>();
		searchRange(root, rect, result);
		return result;
	}
	/**
	 * 
	 * @param node
	 * @param rect
	 * @param result
	 */
	private void searchRange(Node node, RectHV rect, SET<Point2D> result) {
		if (node == null || !node.rect.intersects(rect)) return;
		
		if (inRange(rect, node.point)) result.add(node.point);
		
		searchRange(node.left, rect, result);
		searchRange(node.right, rect, result);
	}
	/**
	 * A nearest neighbor in the set to p; null if set is empty.
	 * 
	 * @param p Point2D object
	 * @return nearest neighbor in the set to p; null if set is empty
	 */
	private Node candidate;
	public Point2D nearest(Point2D p) {
		if (root == null) return null;
		candidate = root;
		nearest(root, p);
		return candidate.point;
	}
	/**
	 * 
	 * @param candidate
	 * @param p
	 */
	private void nearest(Node node, Point2D p) {
		if (node == null) return;
		
		double distToCand = candidate.point.distanceSquaredTo(p);
		double distToRect = node.rect.distanceSquaredTo(p);
		
		if (distToCand < distToRect) return;
		
		double distToNode = node.point.distanceSquaredTo(p);
		if (distToNode < distToCand) {
			candidate = node;
		}
		
		Node primary, secondary;
		if (node.direction == VERTICAL) {
			if (node.point.y() < p.y()) {
				primary = node.right;
				secondary = node.left;
			} else {
				primary = node.left;
				secondary = node.right;
			}
		} else {
			if (node.point.x() < p.x()) {
				primary = node.right;
				secondary = node.left;
			} else {
				primary = node.left;
				secondary = node.right;
			}
		}
		nearest(primary, p);
		nearest(secondary, p);
	}
	/**
	 * Should the Point2D p be placed in the left node of node.
	 * @param node Node
	 * @param p Point2D
	 * @return true if p should be the left node of node
	 */
	private static boolean getSide(Node node, Point2D point) {
		if (node.direction == VERTICAL) {
			return (point.x() < node.point.x()) && LEFT;
		} else {
			return (point.y() < node.point.y()) && LEFT;
		}
	}
	/**
	 * 
	 * @param rect
	 * @param point
	 * @return
	 */
	private static boolean inRange(RectHV rect, Point2D point) {
		double x = point.x();
		double y = point.y();
		if (x >= rect.xmin() && x <= rect.xmax() &&
				y >= rect.ymin() && y <= rect.ymax()) {
			return true;
		} else {
			return false;
		}
	}
}
