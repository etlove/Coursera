import java.awt.Color;

public class SeamCarver {
	
	private Picture picture;
	private double[] distTo;
	private double[] energy;
	private int[] edgeTo;

	public SeamCarver(Picture picture) {
		this.picture = new Picture(picture);
	}
	/**
	 * current picture.
	 * @return
	 */
	public Picture picture() {
		return new Picture(this.picture);
	}
	/**
	 * width  of current picture.
	 * @return
	 */
	public int width() {
		return picture.width();
	}
	/**
	 * height of current picture.
	 * @return
	 */
	public int height() {
		return picture.height();
	}
	/**
	 * energy of pixel at column x and row y in current picture.
	 * @param x
	 * @param y
	 * @return
	 */
	public double energy(int x, int y) {
		if (x < 0 || x >= width() || y < 0 || y >= height()) {
			throw new IndexOutOfBoundsException();
		}

		if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
			return 3 * 255 * 255;
		}

		return getGradient(picture.get(x-1, y), picture.get(x+1, y))
				+ getGradient(picture.get(x, y-1), picture.get(x, y+1));
	}
	/**
	 * 
	 * @param clrA
	 * @param clrB
	 * @return
	 */
	private int getGradient(Color clrA, Color clrB) {
		int r = clrA.getRed() - clrB.getRed(),
			g = clrA.getGreen() - clrB.getGreen(),
			b = clrA.getBlue() - clrB.getBlue();

		return r * r + g * g + b * b;
	}
	/**
	 * sequence of indices for horizontal seam in current picture.
	 * @return
	 */
	public int[] findHorizontalSeam() {
		int size = width() * height();

		energy = new double[size];
		distTo = new double[size];
		edgeTo = new int[size];
		int pos;

		for (int y = 0; y < height(); y++) {
			for (int x = 0; x < width(); x++) {
				pos = getPosition(x, y);

				if (x == 0) distTo[pos] = 0;
				else distTo[pos] = Double.POSITIVE_INFINITY;

				energy[pos] = energy(x, y);
				edgeTo[pos] = -1;
			}
		}

		for (int x = 0; x < width() - 1; x++) {
			for (int y = 0; y < height(); y++) {
				pos = getPosition(x, y);
				if (y > 0) release(pos, getPosition(x+1, y-1));
				release(pos, getPosition(x+1, y));
				if (y < height()-1) release(pos, getPosition(x+1, y+1));
			}
		}


		double min = Double.POSITIVE_INFINITY;
		int end = 0;

		for (int y = 0; y < height(); y++) {
			if (distTo[getPosition(width() - 1, y)] < min) {
				min = distTo[getPosition(width() - 1, y)];
				end = getPosition(width() - 1, y);
			}
		}
		
		return getHorizontalSeam(end);
	}
	/**
	 * sequence of indices for vertical   seam in current picture
	 * @return
	 */
	public int[] findVerticalSeam() {
		int size = width() * height();

		energy = new double[size];
		distTo = new double[size];
		edgeTo = new int[size];
		int pos;

		for (int x = 0; x < width(); x++) {
			for (int y = 0; y < height(); y++) {
				pos = getPosition(x, y);

				if (y == 0) distTo[pos] = 0;
				else distTo[pos] = Double.POSITIVE_INFINITY;

				energy[pos] = energy(x, y);
				edgeTo[pos] = -1;
			}
		}

		for (int y = 0; y < height() - 1; y++) {
			for (int x = 0; x < width(); x++) {
				pos = getPosition(x, y);
				if (x > 0) release(pos, getPosition(x-1, y+1));
				release(pos, getPosition(x, y+1));
				if (x < width()-1) release(pos, getPosition(x+1, y+1));
			}
		}

		double min = Double.POSITIVE_INFINITY;
		int end = 0;

		for (int x = 0; x < width(); x++) {
			if (distTo[getPosition(x, height()-1)] < min) {
				min = distTo[getPosition(x, height()-1)];
				end = getPosition(x, height() - 1);
			}
		}
		
		return getVerticalSeam(end);
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int getPosition(int x, int y) {
		return width() * y + x;
	}
	/**
	 * 
	 * @param pos
	 * @return
	 */
	private int getX(int pos) {
		return pos % width();
	}
	/**
	 * 
	 * @param pos
	 * @return
	 */
	private int getY(int pos) {
		return pos / width();
	}
	/**
	 * 
	 * @param from
	 * @param to
	 */
	private void release(int v, int w) {
		if (distTo[w] > distTo[v] + energy[w]) {
			distTo[w] = distTo[v] + energy[w];
			edgeTo[w] = v;
		}
	}
	/**
	 * 
	 * @param end
	 * @return
	 */
	private int[] getHorizontalSeam(int end) {
		int[] result = new int[width()];
		int temp = end;

		while (temp > -1) {
			result[getX(temp)] = getY(temp);
			temp = edgeTo[temp];
		}

		return result;
	}
	/**
	 * 
	 * @param end
	 * @return
	 */
	private int[] getVerticalSeam(int end) {
		int[] result = new int[height()];
		int temp = end;

		while (temp > -1) {
			result[getY(temp)] = getX(temp);
			temp = edgeTo[temp];
		}

		return result;
	}
	/**
	 * remove horizontal seam from current picture.
	 * @param a
	 */
	public void removeHorizontalSeam(int[] a) {
		if (height() < 0) throw new IllegalArgumentException();
		if (a.length != width()) throw new IllegalArgumentException();

		Picture result = new Picture(width(), height() - 1);
		int temp = a[0];

		for (int x = 0; x < width(); x++) {
			if (a[x] < 0 || a[x] >= height()) throw new IndexOutOfBoundsException();
			if (a[x] < temp - 1 || a[x] > temp + 1) throw new IllegalArgumentException();

			temp = a[x];

			for (int y = 0; y < height() - 1; y++) {
				if (y < temp) result.set(x, y, picture.get(x, y));
				else result.set(x, y, picture.get(x, y+1));
			}
		}
		
		distTo = null;
		edgeTo = null;
		energy = null;

		picture = result;
	}
	/**
	 * remove vertical   seam from current picture.
	 * @param a
	 */
	public void removeVerticalSeam(int[] a) {
		if (width() < 0) throw new IllegalArgumentException();
		if (a.length != height()) throw new IllegalArgumentException();

		Picture result = new Picture(width() - 1, height());
		int temp = a[0];

		for (int y = 0; y < height(); y++) {
			if (a[y] < 0 || a[y] >= width()) throw new IndexOutOfBoundsException();
			if (a[y] < temp - 1 || a[y] > temp + 1) throw new IllegalArgumentException();

			temp = a[y];

			for (int x = 0; x < width() - 1; x++) {
				if (x < temp) result.set(x, y, picture.get(x, y));
				else result.set(x, y, picture.get(x+1, y));
			}
		}

		distTo = null;
		edgeTo = null;
		energy = null;

		picture = result;
	}
}
