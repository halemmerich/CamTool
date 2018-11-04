package de.dieklaut.camtool.operations;

import de.dieklaut.camtool.Context;

public class MultiOperation extends AbstractOperation {
	
	private Operation[] ops;

	public MultiOperation(Operation ... ops) {
		this.ops = ops;
	}

	@Override
	public void perform(Context context) {
		for (Operation op : ops) {
			op.perform(context);
		}
	}

}
