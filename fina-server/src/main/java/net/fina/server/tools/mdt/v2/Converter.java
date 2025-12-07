package net.fina.server.tools.mdt.v2;

import net.fina.server.mdt.xml.v2.Node;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: nick Date: 1/28/13 Time: 12:52 AM To change
 * this template use File | Settings | File Templates.
 */
public interface Converter extends Serializable {

	public enum SHEET_STATUS {
		UNKNOWN, PROCESED
	}

	public Map<String, Node> convert() throws Exception;

	public List<String> getSheetNames();
}
