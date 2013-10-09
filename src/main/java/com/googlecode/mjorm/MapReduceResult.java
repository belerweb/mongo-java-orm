package com.googlecode.mjorm;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;

/**
 * The result of a map reduce operation.
 */
public class MapReduceResult {

	private DBCollection resultCollection;
	private Long numObjectsScanned;
	private Long numEmits;
	private Long numObjectsOutput;
	private Long timeMillis;

	/**
	 * Creates the {@link MapReduceResult}.
	 * @param result the {@link MapReduceOutput}
	 */
	public MapReduceResult(MapReduceOutput output) {
		this.resultCollection = output.getOutputCollection();
		CommandResult result = output.getCommandResult();
		if (result.containsField("counts")) {
			DBObject counts = (DBObject)result.get("counts");
			this.numObjectsScanned	= new Long(counts.get("input").toString());
			this.numEmits 			= new Long(counts.get("emit").toString());
			this.numObjectsOutput 	= new Long(counts.get("output").toString());
		}
		if (result.containsField("timeMillis")) {
			this.timeMillis = new Long(result.get("timeMillis").toString());
		}
	}

	/**
	 * @return the resultCollection
	 */
	public DBCollection getResultCollection() {
		return resultCollection;
	}

	/**
	 * @return the numObjectsScanned
	 */
	public Long getNumObjectsScanned() {
		return numObjectsScanned;
	}

	/**
	 * @return the numEmits
	 */
	public Long getNumEmits() {
		return numEmits;
	}

	/**
	 * @return the numObjectsOutput
	 */
	public Long getNumObjectsOutput() {
		return numObjectsOutput;
	}

	/**
	 * @return the timeMillis
	 */
	public Long getTimeMillis() {
		return timeMillis;
	}

}
