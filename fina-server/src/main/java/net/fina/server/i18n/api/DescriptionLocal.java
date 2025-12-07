package net.fina.server.i18n.api;

import jakarta.ejb.Local;

import net.fina.server.i18n.helper.Description;

@Local
public interface DescriptionLocal {

	public Description getDescription(long nameStrId);

	public long getNameStrId(String value, long langId);

	public void updateSysString(long nameStrId, long langId, Object value);

	void removeSysString(long nameStrId);
}
