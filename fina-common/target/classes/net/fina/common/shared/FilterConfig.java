package net.fina.common.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class FilterConfig implements Serializable {

	private Map<FilterConfigKey, Object> configs;

	private long langId;

	public FilterConfig() {
		configs = new HashMap<FilterConfigKey, Object>();
	}

	public FilterConfig(Map<FilterConfigKey, Object> filterConfig) {
		configs = filterConfig;
	}

	public Object getFilterParam(FilterConfigKey key) {
		return configs.get(key);
	}

	public void setFilterParam(FilterConfigKey key, Object value) {
		configs.put(key, value);
	}

	public long getLangId() {
		return langId;
	}

	public void setLangId(long langId) {
		this.langId = langId;
	}

}
