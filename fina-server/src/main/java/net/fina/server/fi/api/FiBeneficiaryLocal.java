package net.fina.server.fi.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.util.FiBeneficiaryFilterType;
import net.fina.server.legalperson.entity.Beneficiary;

import java.util.List;
import java.util.Map;

public interface FiBeneficiaryLocal {
    long count(long fiId);

    List<Beneficiary> load(long fiId, int offset, int limit, FiBeneficiaryFilterType filterType, String filterString);

    Beneficiary create(Beneficiary beneficiary, long fiId) throws FinATypeException;

    Beneficiary update(Beneficiary beneficiary, long fiId) throws FinATypeException;

    void delete(long fiId, long beneficiaryId);

    Map<FiBeneficiaryFilterType, Long> countByType(long fiId);

    Beneficiary getById(long beneficiaryId) throws FinATypeException;
}
