package net.fina.server.returns.api;

import java.util.List;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.returns.entity.ReturnType;

public interface ReturnTypeLocal {
	ReturnType getReturnTypeByCode(String code);

	List<ReturnType> loadReturnTypes();

	ReturnType save(ReturnType returnType) throws FinATypeException;

	void delete(long id) throws FinATypeException;

    boolean checkReturnTypeCodeUnique(ReturnType entity);

	ReturnType getReturnTypeByReturnId(long returnId);

    byte[] getFormat(long id);

    void saveFormat(long id, byte[] format);

	String getReturnCodeById(long id);
}
