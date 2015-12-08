/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2015 Board of Regents of the University of
 * Wisconsin-Madison, University of Konstanz and Brian Northan.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops;

/**
 * A binary {@link InplaceOp} which calculates a result from two given inputs,
 * storing it into the first input. In other words: the first input object is
 * mutated.
 * 
 * @author Leon Yang
 * @param <A> type of first input
 * @param <B> type of second input
 * @see BinaryFunctionOp
 * @see BinaryHybridOp
 */
public interface BinaryInplaceOp<A, B> extends InplaceOp<A>,
	BinaryComputerOp<A, B, A>
{

	/**
	 * Mutates the first input argument in-place.
	 * 
	 * @param input1 of the {@link BinaryInplaceOp}
	 * @param input2 of the {@link BinaryInplaceOp}
	 */
	void compute2(final A input1, final B input2);

	// -- InplaceOp methods --

	@Override
	default void compute(final A arg) {
		compute2(arg, in2());
	}

	// -- BinaryComputerOp methods --

	@Override
	default void compute(final A input, final A output) {
		InplaceOp.super.compute(input, output);
	}

	@Override
	default void compute2(final A input1, final B input2, final A output) {
		if (input1 != output) {
			throw new IllegalArgumentException("Input and output must match");
		}
		compute2(input1, in2());
	}

	// -- Threadable methods --

	@Override
	default BinaryInplaceOp<A, B> getIndependentInstance() {
		// NB: We assume the op instance is thread-safe by default.
		// Individual implementations can override this assumption if they
		// have state (such as buffers) that cannot be shared across threads.
		return this;
	}

}
