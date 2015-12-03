package net.imagej.ops.geom.geom2d;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractFunctionOp;
import net.imagej.ops.Contingent;
import net.imagej.ops.Ops;
import net.imagej.ops.geom.GeometricOp;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.view.Views;

/**
 * Generic implementation of {@code geom.holedetection}.
 * 
 * Scanline Algorithm which uses the number of changes from background to
 * foreground color to determine if an object has holes.
 * 
 * @author Daniel Seebacher, University of Konstanz.
 */
@Plugin(type = Ops.Geometric.HoleDetection.class)
public class DefaultHoleDetection<T extends BooleanType<T>> extends AbstractFunctionOp<RandomAccessibleInterval<T>, Boolean>
		implements GeometricOp<RandomAccessibleInterval<T>, Boolean>, Ops.Geometric.HoleDetection, Contingent {

	@Parameter(required = false)
	private boolean whiteAsForeground = true;

	@Override
	public Boolean compute(RandomAccessibleInterval<T> input) {
		Cursor<T> inputCursor = Views.flatIterable(input).cursor();

		// for each row
		for (int y = 0; y < input.dimension(1); y++) {

			// count num changes from foreground to background and vice versa
			int numChanges = 0;

			// if white is foreground than black is background and vice versa
			boolean currentVal = !whiteAsForeground;

			// count number of changes from foreground color to background color
			for (int x = 0; x < input.dimension(0); x++) {
				boolean val = inputCursor.next().get();
				if (val != currentVal) {
					numChanges++;
					currentVal = val;
				}
			}

			// there can't be an odd number of changes
			if (numChanges % 2 != 0) {
				numChanges++;
			}

			// if there are more than 2 changes there is a hole
			if (numChanges > 2) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean conforms() {
		return in().numDimensions() == 2;
	}

}
