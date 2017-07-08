package de.dieklaut.camtool.operations;

import de.dieklaut.camtool.Context;

public interface Operation {
	public void perform(Context context);
	public String getName();
}
