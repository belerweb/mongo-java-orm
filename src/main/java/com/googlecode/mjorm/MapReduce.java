package com.googlecode.mjorm;

import java.util.Map;
import java.util.Random;

import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand.OutputType;

/**
 * For MapReducing with {@link MongoDao}.
 */
public class MapReduce {

	private static final Random rand = new Random(System.currentTimeMillis());

	private String mapFunction;
	private String reduceFunction;
	private String finalizeFunction;
	private DBObject query;
	private DBObject sort;
	private Long limit;
	private String outputDBName;
	private String outputCollectionName = "mr_mjorm_"+this.hashCode()+"_"+(10000*rand.nextFloat());
	private Map<String, Object> scope;
	private Boolean verbose;
	private OutputType outputType = OutputType.REPLACE;

	/**
	 * Creates the {@link MapReduce}.
	 * @param mapFunction the function
	 * @param reduceFunction the function
	 */
	public MapReduce(String mapFunction, String reduceFunction) {
		this.mapFunction 	= mapFunction;
		this.reduceFunction	= reduceFunction;
	}

	/**
	 * Creates the {@link MapReduce}.
	 * @param mapFunction the function
	 * @param reduceFunction the function
	 * @param finalizeFunction the function
	 */
	public MapReduce(
		String mapFunction, String reduceFunction, String finalizeFunction) {
		this.mapFunction 		= mapFunction;
		this.reduceFunction		= reduceFunction;
		this.finalizeFunction	= finalizeFunction;
	}


	/**
	 * @return the mapFunction
	 */
	public String getMapFunction() {
		return mapFunction;
	}

	/**
	 * @param mapFunction the mapFunction to set
	 */
	public void setMapFunction(String mapFunction) {
		this.mapFunction = mapFunction;
	}

	/**
	 * @return the reduceFunction
	 */
	public String getReduceFunction() {
		return reduceFunction;
	}

	/**
	 * @param reduceFunction the reduceFunction to set
	 */
	public void setReduceFunction(String reduceFunction) {
		this.reduceFunction = reduceFunction;
	}

	/**
	 * @return the finalizeFunction
	 */
	public String getFinalizeFunction() {
		return finalizeFunction;
	}

	/**
	 * @param finalizeFunction the finalizeFunction to set
	 */
	public void setFinalizeFunction(String finalizeFunction) {
		this.finalizeFunction = finalizeFunction;
	}

	/**
	 * @return the query
	 */
	public DBObject getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(DBObject query) {
		this.query = query;
	}

	/**
	 * @return the sort
	 */
	public DBObject getSort() {
		return sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(DBObject sort) {
		this.sort = sort;
	}

	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the outputCollectionName
	 */
	public String getOutputCollectionName() {
		return outputCollectionName;
	}

	/**
	 * @param outputCollectionName the outputCollectionName to set
	 */
	public void setOutputCollectionName(String outputCollectionName) {
		this.outputCollectionName = outputCollectionName;
	}

	/**
	 * @return the outputType
	 */
	public OutputType getOutputType() {
		return outputType;
	}

	/**
	 * @param outputType the outputType to set
	 */
	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	/**
	 * @return the scope
	 */
	public Map<String, Object> getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(Map<String, Object> scope) {
		this.scope = scope;
	}

	/**
	 * @return the verbose
	 */
	public Boolean getVerbose() {
		return verbose;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * @return the outputDBName
	 */
	public String getOutputDBName() {
		return outputDBName;
	}

	/**
	 * @param outputDBName the outputDBName to set
	 */
	public void setOutputDBName(String outputDBName) {
		this.outputDBName = outputDBName;
	}

}
