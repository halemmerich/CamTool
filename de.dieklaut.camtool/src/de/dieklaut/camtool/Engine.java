package de.dieklaut.camtool;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.dieklaut.camtool.cmdlinewrapper.OperationWrapper;

/**
 * This class holds all available features reachable from the command line.
 * 
 * @author mboonk
 *
 */
public class Engine {
	private List<OperationWrapper> availableOperations;
	
	public Engine(OperationWrapper ...operations) {
		availableOperations = Arrays.asList(operations);
	}

	public List<OperationWrapper> getOperationWrappers() {
		return Collections.unmodifiableList(availableOperations);
	}
}
