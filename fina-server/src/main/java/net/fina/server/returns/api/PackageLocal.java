package net.fina.server.returns.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.returns.entity.ReturnPackage;

import java.util.List;

public interface PackageLocal {

    List<ReturnPackage> load();

    ReturnPackage savePackage(ReturnPackage returnPackage) throws FinATypeException;

    void deletePackage(long packageId);
}
