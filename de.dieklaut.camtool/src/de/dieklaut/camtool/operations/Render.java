package de.dieklaut.camtool.operations;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;

/**
 * Performs all steps to create a {@link Result} from a sorting.
 * @author mboonk
 *
 */
public class Render extends AbstractOperation {

	String sortingName = Constants.DEFAULT_SORTING_NAME;
	
	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}
	
	@Override
	public void perform(Context context) {
		// FIXME implement

	}

}
