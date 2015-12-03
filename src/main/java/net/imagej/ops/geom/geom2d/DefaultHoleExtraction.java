package net.imagej.ops.geom.geom2d;

import java.util.ArrayList;
import java.util.List;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractFunctionOp;
import net.imagej.ops.Contingent;
import net.imagej.ops.FunctionOp;
import net.imagej.ops.Ops;
import net.imagej.ops.create.img.CreateImgFromInterval;
import net.imagej.ops.geom.GeometricOp;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.BooleanType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Generic implementation of {@code geom.holedetection}.
 * 
 * Scanline Algorithm which uses the number of changes from background to
 * foreground color to determine if an object has holes. Returns an image with
 * the surrounding object removed and only the holes remaining. If no holes have
 * been found this method returns null.
 * 
 * @author Daniel Seebacher, University of Konstanz.
 */
@Plugin(type = Ops.Geometric.HoleExtraction.class)
public class DefaultHoleExtraction<T extends BooleanType<T>>
		extends AbstractFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>
		implements GeometricOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>, Ops.Geometric.HoleExtraction, Contingent {

	@Parameter(required = false)
	private boolean whiteAsForeground = true;

	private FunctionOp<RandomAccessibleInterval<T>, Img> imgCreateFunc;

	@Override
	public void initialize() {

		imgCreateFunc = ops().function(CreateImgFromInterval.class, Img.class, in(), Util.getTypeFromInterval(in()));

		super.initialize();
	}

	@Override
	public RandomAccessibleInterval<T> compute(RandomAccessibleInterval<T> input) {

		Img<T> out = imgCreateFunc.compute(input);

		Cursor<T> inputCursor = Views.flatIterable(input).cursor();
		Cursor<T> outputCursor = Views.flatIterable(out).cursor();

		for (int y = 0; y < input.dimension(1); y++) {
			List<Integer> changes = new ArrayList<Integer>();

			boolean currentVal = !whiteAsForeground;

			// go through input image, check number of changes from
			// background to foreground to background etc
			for (int x = 0; x < input.dimension(0); x++) {
				boolean val = inputCursor.next().get();
				if (val != currentVal) {
					changes.add(x);
					currentVal = val;
				}
			}

			// there can't be an odd number of changes
			if (changes.size() % 2 != 0) {
				changes.add((int) input.dimension(0));
			}

			// if there is no hole go to next row
			if (changes.size() <= 2) {
				for (int x = 0; x < input.dimension(0); x++) {
					outputCursor.fwd();
				}
			} else {
				// move to start position
				for (int x = 0; x < changes.get(1); x++) {
					outputCursor.fwd();
				}

				// draw white between index 1 - 2, 3 - 4, ....
				// draw black between index 0 - 1, 2 - 3, ....
				int index = 1;
				boolean draw = true;
				for (int x = changes.get(index); x < input.dimension(0); x++) {

					// if x is bigger than the last change pos were
					// outside of the object
					if (x >= changes.get(changes.size() - 1)) {
						draw = false;
					}
					// check if we went over a change position, update
					// index and draw
					else if (x == changes.get(index + 1)) {
						draw = !draw;
						index++;
					}

					outputCursor.fwd();
					if (draw) {
						outputCursor.get().set(true);
					}
				}
			}

		}

		return out;
	}

	@Override
	public boolean conforms() {
		return in().numDimensions() == 2;
	}

}
