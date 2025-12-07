package net.fina.server.fi.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.FiBranchType;

import java.util.List;

public interface FiBranchTypeLocal {

    List<FiBranchType> load();

    List<FiBranchType> loadWithCount(long fiId, boolean includeAll);

    FiBranchType get(long id);

    FiBranchType save(FiBranchType fiBranchType) throws FinATypeException;

    void delete(long fiBranchTypeId) throws FinATypeException;
}
