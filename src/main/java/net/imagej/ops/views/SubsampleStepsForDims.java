package net.imagej.ops.views;

import org.scijava.ItemIO;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.AbstractFunctionOp;
import net.imagej.ops.Contingent;
import net.imagej.ops.Ops;
import net.imglib2.RandomAccessible;
import net.imglib2.view.SubsampleView;
import net.imglib2.view.Views;

@Plugin(type = Ops.View.Subsample.class, name = Ops.View.Subsample.NAME)
public class SubsampleStepsForDims<T> extends AbstractFunctionOp<RandomAccessible<T>, SubsampleView<T>>
		implements Contingent, Ops.View.Subsample {

	@Parameter(type = ItemIO.INPUT)
	private long[] steps;

	@Override
	public SubsampleView<T> compute(RandomAccessible<T> input) {
		return Views.subsample(input, steps);
	}

	@Override
	public boolean conforms() {
		return getInput().numDimensions() <= steps.length;
	}

}
