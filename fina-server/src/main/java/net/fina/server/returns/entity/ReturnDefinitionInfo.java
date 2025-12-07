package net.fina.server.returns.entity;

import java.io.Serializable;
import java.util.List;


/**
 * Keeps information about size and chunck data
 * 
 * @author David Chokhonelidze
 * @version 0.1
 * 
 */
@SuppressWarnings("serial")
public class ReturnDefinitionInfo implements Serializable {
	private long size;
	private List<ReturnDefinition> loadedDefinitions;

	/**
	 * 
	 * @return quantity of rows
	 */
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return List of ReturnDefinition entities
	 */
	public List<ReturnDefinition> getLoadedDefinitions() {
		return loadedDefinitions;
	}

	public void setLoadedDefinitions(List<ReturnDefinition> loadedDefinitions) {
		this.loadedDefinitions = loadedDefinitions;
	}

}
