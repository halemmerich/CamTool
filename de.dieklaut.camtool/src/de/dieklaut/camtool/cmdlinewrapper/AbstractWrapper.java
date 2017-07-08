package de.dieklaut.camtool.cmdlinewrapper;

public abstract class AbstractWrapper implements OperationWrapper{

	@Override
	public String getName() {
		return getClass().getSimpleName().replaceAll("Wrapper", "");
	}
	
}
