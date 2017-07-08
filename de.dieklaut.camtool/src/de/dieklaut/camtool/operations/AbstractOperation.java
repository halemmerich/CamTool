package de.dieklaut.camtool.operations;

public abstract class AbstractOperation implements Operation{
	
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
}
