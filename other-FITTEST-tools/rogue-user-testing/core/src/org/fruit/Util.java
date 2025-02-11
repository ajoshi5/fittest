/**************************************************************************************
*  Copyright (c) 2013, Universitat Politecnica de Valencia. All rights reserved.      *
*  This program and the accompanying materials are made available under the terms     *
*  of the 3-Clause BSD License which accompanies this distribution, and is available  *
*  at http://www.opensource.org/licenses/BSD-3-Clause. The research leading to these  *
*  results has received funding from the European Community`s Seventh Framework       *
*  Programme (FP7/2007-2013) under the grant agreement  FP7-257574 FITTEST.           *
**************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import org.fruit.Assert;
import org.fruit.alayer.DFNavigator;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Finder;
import org.fruit.alayer.HitTester;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Searcher;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.SystemStopException;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.SearchFlag;
import org.fruit.alayer.Spline;
import org.fruit.alayer.Tags;
import org.fruit.alayer.WidgetIterator;
import org.fruit.alayer.WidgetNotFoundException;
import org.fruit.alayer.devices.Mouse;
import java.lang.System;

/**
 * Utility methods.
 */
public final class Util {

	private Util(){}

	public static enum NullObject{ NullObject; }
	public static final NullObject Null = NullObject.NullObject;

	private static final String lineSep = System.getProperty("line.separator");


	public static final Shape InvisibleShape = new Shape() {
		private static final long serialVersionUID = 3038370744728060922L;
		public double x() { return 0; }
		public double y() { return 0; }
		public double width() { return 0; }
		public double height() { return 0; }
		public void paint(Canvas canvas, Pen pen){ }
		public boolean contains(double x, double y){ return false; }
	};

	public static final Visualizer NullVisualizer = new Visualizer(){
		private static final long serialVersionUID = 95857074998765550L;
		public void run(State s, Canvas c, Pen pen) {}	
	};

	public static final Searcher NullSearcher = new Searcher() {
		private static final long serialVersionUID = 7810562741429319061L;
		public SearchFlag apply(Widget widget,	UnFunc<Widget, SearchFlag> visitor) {
			return SearchFlag.OK;
		}
	};

	public static final Searcher AllSearcher = new Searcher(){
		private static final long serialVersionUID = 7410528704889829161L;
		public SearchFlag apply(Widget widget,	UnFunc<Widget, SearchFlag> visitor) {
			return visitor.apply(widget);
		}
	};

	public static final HitTester TrueTester = new HitTester(){ 
		private static final long serialVersionUID = -7254950185633885998L;
		public boolean apply(double x, double y){ return true; }
	};

	public static final HitTester FalseTester = new HitTester(){ 
		private static final long serialVersionUID = -7254950185633885998L;
		public boolean apply(double x, double y){ return false; }
	};

	public static boolean hitTest(Widget widget, double relX, double relY){
		Shape s = widget.get(Tags.Shape, null);
		if(s == null) return false;
		Point abs = relToAbs(s, relX, relY);
		return widget.get(Tags.HitTester, FalseTester).apply(abs.x(), abs.y());
	}

	public static Point relToAbs(Shape shape, double relX, double relY){
		return Point.from(shape.x() + shape.width() * relX, shape.y() + shape.height() * relY);
	}

	public static double relToAbsX(Shape shape, double relX){
		return shape.x() + shape.width() * relX;
	}

	public static double relToAbsY(Shape shape, double relY){
		return shape.y() + shape.height() * relY;
	}

	public static boolean contains(Widget widget, double x, double y){
		return contains(widget.get(Tags.Shape), x, y);
	}

	public static boolean contains(Shape shape, double x, double y){
		return shape == null ? false : shape.contains(x, y);
	}

	public static boolean containsRel(Shape shape, double relX, double relY){
		return shape == null ? false :
			shape.contains(shape.x() + relX * shape.width(), shape.y() + relY * shape.height());
	}

	public static boolean containsRel(Widget widget, double relX, double relY){
		return containsRel(widget.get(Tags.Shape), relX, relY);
	}

	public static void curve(Canvas canvas, Pen pen, Point[] points, int granularity){
		Assert.notNull(points, canvas);
		Assert.isTrue(points.length > 1 && granularity > 0);
		List<Point> intermediatePoints = Spline.evaluate(points, granularity);
		Point s = intermediatePoints.get(0);
		for(int i = 1; i < intermediatePoints.size(); i++){
			Point current = intermediatePoints.get(i);
			canvas.line(pen, s.x(), s.y(), current.x(), current.y());
			s = current;
		}
	}

	public static void curvedArrow(Canvas canvas, Pen pen, Point[] points, int granularity, double headWidth, double headLength){
		Assert.notNull(points, canvas);
		Assert.isTrue(points.length > 1 && granularity > 0);
		List<Point> intermediatePoints = Spline.evaluate(points, granularity);
		Point s = intermediatePoints.get(0);
		for(int i = 1; i < intermediatePoints.size() - 1; i++){
			Point current = intermediatePoints.get(i);
			canvas.line(pen, s.x(), s.y(), current.x(), current.y());
			s = current;
		}

		Point secondToLast = intermediatePoints.get(intermediatePoints.size() - 2);
		Point last = intermediatePoints.get(intermediatePoints.size() - 1);
		arrow(canvas, pen, secondToLast.x(), secondToLast.y(), last.x(), last.y(), headWidth, headLength);
	}

	public static void arrow(Canvas canvas, Pen pen, double x1, double y1, double x2, double y2, double headWidth, double headLength){
		Assert.notNull(canvas);
		Assert.isTrue(headWidth >= 0 && headLength >= 0);
		canvas.line(pen, x1, y1, x2, y2);    	
		double xDiff = x2 - x1;
		double yDiff = y2 - y1;
		double length = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		double baseX = length == 0 ? x2 - headLength : x2 - xDiff * headLength / length;
		double baseY = length == 0 ? y2 - headLength : y2 - yDiff * headLength / length;
		Point p1 = Util.OrthogonalPoint(baseX, baseY, x2, y2, headWidth * .5);
		Point p2 = Util.OrthogonalPoint(baseX, baseY, x2, y2, -headWidth * .5);
		canvas.line(pen, p1.x(), p1.y(), x2, y2);
		canvas.line(pen, p2.x(), p2.y(), x2, y2);
		canvas.line(pen, p1.x(), p1.y(), p2.x(), p2.y());
	}

	public static void clear(Canvas canvas){
		Assert.notNull(canvas);
		canvas.clear(canvas.x(), canvas.y(), canvas.width(), canvas.height());
	}

	public static Point center(Shape shape){
		Assert.notNull(shape);
		return Point.from(centerX(shape), centerY(shape));
	}

	public static double centerX(Shape shape){
		Assert.notNull(shape);
		return shape.x() + 0.5 * shape.width();
	}

	public static double centerY(Shape shape){
		Assert.notNull(shape);
		return shape.y() + 0.5 * shape.height();
	}

	public static void pause(double seconds){
		if(seconds <= 0) return;

		try {
			Thread.sleep(Math.round(seconds * 1000.0));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}		
	}

	public static double time(){
		return System.currentTimeMillis() / 1000.0;
	}

	public static void moveCursor(Mouse mouse, double x, double y, double duration){
		Assert.notNull(mouse);
		Assert.isTrue(duration >= 0);

		if(duration == 0){
			mouse.setCursor(x, y);
			return;
		}

		double deadline = time() + duration;
		Point cursor = mouse.cursor();
		double xDist = cursor.x() - x;
		double yDist = cursor.y() - y;
		double pos; 

		double time = time();
		while(time < deadline){
			pos = (deadline - time) / duration;
			mouse.setCursor(x + pos * xDist, y + pos * yDist);
			pause(0.001);
			time = time();
		}
		mouse.setCursor(x, y);
	}

	public static String abbreviate(String string, int maxLen, String abbreviation){
		Assert.notNull(string, abbreviation);
		Assert.isTrue(maxLen >= 0);
		return string.substring(0, Math.min(maxLen, string.length())) + (string.length() > maxLen ? abbreviation : "");
	}

	public static Point OrthogonalPoint(double x1, double y1, double x2, double y2, double r){		
		Point ret;

		if(x1 == x2){
			ret = y1 > y2 ? Point.from(x1 - r, y1) : Point.from(x1 + r, y1);
		}else if(y1 == y2){
			ret = x1 > x2 ? Point.from(x1, y1 + r) : Point.from(x1, y1 - r);
		}else{
			double m = -(x1 - x2) / (y1 - y2);
			double n = y1 - m * x1;
			double p = (2 * m * n - 2 * m * y1 - 2 * x1) / (1 + m * m);
			double q = (-2 * n * y1 + y1 * y1 - r * r + n * n + x1 * x1) / (1 + m * m);			
			double s1 = - p * .5 + Math.sqrt(p * p * .25 - q) * (r > 0 ? 1 : -1);
			double s2 = - p * .5 - Math.sqrt(p * p * .25 - q) * (r > 0 ? 1 : -1);
			double dm = (y1 - y2) / Math.abs(x1 - x2);
			double s = dm > 0 ? s2 : s1;
			ret = Point.from(s, m * s + n);
		}
		return ret;
	}

	public static int childIndex(Widget child){
		Assert.notNull(child);
		Widget parent = child.parent();
		if(parent == null)
			return 0;

		int idx = 0;
		for(int i = 0; i < parent.childCount(); i++){
			if(parent.child(i) == child)
				return idx;
			idx++;
		}

		throw new IllegalStateException("Corrupt WidgetTree (widget is not contained in its parent's child set)!");
	}

	public static int depth(Widget widget){
		Assert.notNull(widget);
		widget = widget.parent();
		int depth = 0;
		while(widget != null){
			widget = widget.parent();
			depth++;
		}
		return depth;
	}

	public static List<Widget> ancestors(Widget widget){
		Assert.notNull(widget);
		List<Widget> ret = Util.newArrayList();
		while((widget = widget.parent()) != null)
			ret.add(widget);
		return ret;		
	}

	public static double area(Shape shape){
		Assert.notNull(shape);
		return shape.width() * shape.height();
	}	

	public static int size(Iterator<?> it){
		Assert.notNull(it);
		int ret = 0;
		for(; it.hasNext(); it.next())
			ret++;
		return ret;
	}

	public static int size(Iterable<?> iter){ return size(iter.iterator()); }

	public static String treeDesc(Widget root, int indent, Tag<?>... tags){
		Assert.notNull(root, tags);
		StringBuilder sb = new StringBuilder();
		for(Widget w : makeIterable(new WidgetIterator(root, new DFNavigator()))){
			for(int i = 0; i < depth(w) * indent; i++)
				sb.append(' ');

			for(Tag<?> t : tags)
				sb.append(w.get(t, null)).append(", ");
			sb.append(Util.lineSep());
		}
		return sb.toString();
	}

	public static int[] indexPath(Widget widget){
		int size = depth(widget);
		int[] ret = new int[size];

		for(int i = size - 1; i >= 0; i--){
			ret[i] = childIndex(widget);			
			widget = widget.parent();
		}
		return ret;		
	}

	public static String indexString(Widget widget){
		return Arrays.toString(indexPath(widget));
	}

	public static Widget widgetFromPoint(State state, double x, double y) throws WidgetNotFoundException{
		Widget ret = widgetFromPoint(state, x, y, null);
		if(ret == null) throw new WidgetNotFoundException();
		return ret;
	}

	public static Widget widgetFromPoint(State state, double x, double y, Widget defaultValue){
		Assert.notNull(state);			
		List<Widget> candidates = new ArrayList<Widget>(widgetsFromPoint(state, x, y));
		if(candidates.isEmpty())
			return defaultValue;

		Comparator<Widget> comp = new Comparator<Widget>(){
			final static int WORSE = -1, BETTER = 1, EVEN = 0;
			public int compare(Widget w1, Widget w2) {
				Shape s1 = w1.get(Tags.Shape, null);
				Shape s2 = w2.get(Tags.Shape, null);
				double a1 = s1 == null ? -1 : Util.area(s1);
				double a2 = s2 == null ? -1 : Util.area(s2);
				return a1 < a2 ? WORSE : (a1 > a2 ? BETTER : EVEN);
			}
		};

		Collections.sort(candidates, comp);
		return candidates.get(0);
	}

	public static Set<Widget> widgetsFromPoint(State state, double x, double y){
		Set<Widget> ret = new HashSet<Widget>();
		for(Widget w : Assert.notNull(state)){			
			if(w.get(Tags.HitTester, Util.FalseTester).apply(x, y))
				ret.add(w);
		}    	
		return ret;
	}

	public static boolean isAncestorOf(Widget ancestor, Widget of){
		while(of != null){
			if((of = of.parent()) == ancestor)
				return true;
		}
		return false;
	}

	public static Iterable<Widget> makeIterable(Widget root){
		return makeIterable(new WidgetIterator(root));
	}

	public static <T> Iterable<T> makeIterable(final Iterator<T> it){
		return new Iterable<T>(){ public Iterator<T> iterator(){ return it; } };
	}

	public static List<Widget> targets(State state, Action action) throws WidgetNotFoundException{
		Assert.notNull(state, action);
		List<Finder> targetFinders = action.get(Tags.Targets, new ArrayList<Finder>());
		List<Widget> ret = Util.newArrayList();
		for(Finder f : targetFinders)
			ret.add(f.apply(state));
		return ret;
	}

	public static List<File> getAllFiles(File dir, String extension) {
		ArrayList<File> fileList = Util.newArrayList();
		getAllFiles(dir, extension, fileList);
		return fileList;
	}

	public static void getAllFiles(File dir, String extension, List<File> fileList) {
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(extension))
				fileList.add(f);
			if (f.isDirectory())
				getAllFiles(f, extension, fileList);
		}
	}

    public static String readFile(File path) {
        try {
            return new Scanner(path, "UTF-8").useDelimiter("\\A").next();
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

	public static File createTempDir(){
		return createTempDir("org.fruit.", Long.toString(System.nanoTime()));
	}

	public static File createTempDir(String pref, String suff){
		File ret;
		try {
			ret = File.createTempFile(pref, suff);
			if(!ret.delete() || !ret.mkdir())
				return null;
			return ret;
		} catch (IOException e) {
			return null;
		}
	}

	public static File createTempFile(){
		return createTempFile("org.fruit.", Long.toString(System.nanoTime()), null);
	}

	public static File createTempFile(String content){
		return createTempFile("org.fruit.", Long.toString(System.nanoTime()), content);
	}

	public static File createTempFile(String pref, String suff, String content){
		File dest;
		try {
			dest = File.createTempFile(pref, suff);			
			if(content != null){
				PrintWriter out = new PrintWriter(dest);
				out.print(content);
				out.close();
			}
			return dest;
		} catch (IOException e) {
			return null;
		}
	}

	public static void saveToFile(String content, String file) throws IOException{
		Assert.notNull(content, file);
		File dest = new File(file);
		if(dest.exists())
			dest.delete();
		PrintWriter out = new PrintWriter(dest);
		out.print(content);
		out.close();
	}


	public static void delete(String fileOrDirectory) throws IOException{
		delete(new File(fileOrDirectory));
	}

	public static void delete(File fileOrDirectory) throws IOException{
		Assert.notNull(fileOrDirectory);

		if(!fileOrDirectory.exists())
			return;

		// it is a directory --> delete all contained files and directories first
		if(fileOrDirectory.isDirectory()){	
			File[] files = fileOrDirectory.listFiles();
			if(files != null) {
				for(File f: files)
					delete(f);
			}
		}

		if(!fileOrDirectory.delete())
			throw new IOException("Unable to delete " + fileOrDirectory.getAbsolutePath());
	}

	public static void copyToDirectory(String fileOrDirectory, String destDir) throws IOException{
		copyToDirectory(new File(fileOrDirectory), new File(destDir), null);
	}

	public static void copyToDirectory(String fileOrDirectory, String destDir, String targetName) throws IOException{
		copyToDirectory(new File(fileOrDirectory), new File(destDir), targetName);
	}

	public static void copyToDirectory(File fileOrDirectory, File destDir, String targetName) throws IOException{
		Assert.notNull(fileOrDirectory, destDir);

		if(targetName == null)
			targetName = fileOrDirectory.getName();

		if(!destDir.exists()){
			if(!destDir.mkdirs())
				throw new IOException("Unable to create directory " + destDir.getAbsolutePath());
		}

		if(fileOrDirectory.isFile()){
			File destFile = new File(destDir.getAbsolutePath() + File.separator + targetName);
			if(!destFile.exists()){
				if(!destFile.createNewFile())
					throw new IOException("Unable to create file " + destFile.getAbsolutePath());
			}

			FileChannel source = null;
			FileChannel destination = null;

			try {
				source = new FileInputStream(fileOrDirectory).getChannel();
				destination = new FileOutputStream(destFile).getChannel();
				destination.transferFrom(source, 0, source.size());
			}finally {
				if(source != null)
					source.close();
				if(destination != null)
					destination.close();
			}
		}else if(fileOrDirectory.isDirectory()){
			File copyDir = new File(destDir.getAbsolutePath() + File.separator + targetName);

			if(!copyDir.exists()){
				if(!copyDir.mkdir())
					throw new IOException("Unable to create directory " + copyDir.getAbsolutePath());
			}

			File[] files = fileOrDirectory.listFiles();
			if(files != null) {
				for(File f: files)
					copyToDirectory(f, copyDir, null);
			}
		}else{
			throw new IOException("Unable to copy " + fileOrDirectory);
		}
	}

	public static boolean isMember(State state, Widget widget){
		return Assert.notNull(widget.root()) == state;
	}

	public static double length(double x1, double y1, double x2, double y2){
		double a = x1 - x2;
		double b = y1 - y2;
		return Math.sqrt(a * a + b * b);
	}

	public static boolean equals(Object o1, Object o2){
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}

	public static int hashCode(Object o){
		return o == null ? 0 : o.hashCode();
	}

	public static String toString(Object o){
		if(o == null)
			return "null";

		if(o instanceof boolean[]){
			return Arrays.toString((boolean[]) o);
		}else if(o instanceof byte[]){
			return Arrays.toString((byte[]) o);
		}else if(o instanceof char[]){
			return Arrays.toString((char[]) o);
		}else if(o instanceof short[]){
			return Arrays.toString((short[]) o);
		}else if(o instanceof int[]){
			return Arrays.toString((int[]) o);
		}else if(o instanceof long[]){
			return Arrays.toString((long[]) o);
		}else if(o instanceof float[]){
			return Arrays.toString((float[]) o);
		}else if(o instanceof double[]){
			return Arrays.toString((double[]) o);
		}else if(o instanceof Object[]){
			return Arrays.toString((Object[]) o);
		}else{
			return o.toString();
		}
	}

	public static File generateUniqueFile(String dir, String prefix){
		Assert.notNull(dir, prefix);
		int i = 0;
		File f;
		while((f = new File(dir + File.separator + prefix + i)).exists())
			i++;
		return f;
	}

	public static String lineSep(){ return lineSep; }
	public static String dateString(){ return dateString("yyyy_MM_dd__HH_mm_ss"); }

	public static String dateString(String format){
		Assert.notNull(format);
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}

	public static boolean stop(SUT system){
		try{
			if(system == null)
				return true;
			system.stop();
			return true;
		}catch(SystemStopException ste){
			return false;
		}
	}

	public static <T> ArrayList<T> newArrayList(){ return new ArrayList<T>(); }
	public static <T> HashSet<T> newHashSet(){ return new HashSet<T>(); }
	public static <K, V> HashMap<K, V> newHashMap(){ return new HashMap<K, V>(); }
}
