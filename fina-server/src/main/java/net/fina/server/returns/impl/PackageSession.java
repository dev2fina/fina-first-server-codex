package net.fina.server.returns.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.FiType;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.PackageLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnPackage;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Stateless
@Local(PackageLocal.class)
@Interceptors(RecordingAuditor.class)
public class PackageSession implements PackageLocal {
    @Inject
    private EntityManager em;
    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private FiLocal fiLocal;


    @Override

    public List<ReturnPackage> load() {
        List<ReturnPackage> result = em.createQuery("select p from IN_PACKAGES p", ReturnPackage.class).getResultList();
        for (ReturnPackage returnPackage : result) {
            for (FiType fiType : returnPackage.getFiTypes()) {
                fiType.getDescription();
            }
            for (ReturnDefinition returnDefinition : returnPackage.getReturnDefinitions()) {
                returnDefinition.getReturnType().getDescription();
                returnDefinition.getDescription();
                for (DefinitionTable definitionTable : returnDefinition.getDefinitionTables()) {
                    definitionTable.getCode();
                }
            }
        }
        return result;
    }

    @Override

    public ReturnPackage savePackage(ReturnPackage returnPackage) throws FinATypeException {
        if (!checkCodeUnique(returnPackage)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        return doSavePackage(returnPackage);
    }

    @Override

    public void deletePackage(long packageId) {
        ReturnPackage returnPackage = em.find(ReturnPackage.class, packageId);
        em.remove(returnPackage);
    }

    private ReturnPackage doSavePackage(ReturnPackage returnPackage) {
        Collection<FiType> fiTypes = returnPackage.getFiTypes();
        Collection<ReturnDefinition> definitions = returnPackage.getReturnDefinitions();
        List<Long> rdIds = new ArrayList<>();
        List<Long> ftIds = new ArrayList<>();

        if (definitions != null && !definitions.isEmpty()) {
            for (ReturnDefinition rd : definitions) {
                rdIds.add(rd.getId());
            }
            returnPackage.setReturnDefinitions(returnDefinitionLocal.loadReturnDefinitionsById(rdIds));
        }

        if (fiTypes != null && !fiTypes.isEmpty()) {
            for (FiType ft : fiTypes) {
                ftIds.add(ft.getId());
            }
            returnPackage.setFiTypes(fiLocal.loadFiTypesByIds(ftIds));
        }

        if (returnPackage.getId() == 0) {
            em.persist(returnPackage);
        } else {
            returnPackage = em.merge(returnPackage);
        }

        return returnPackage;
    }

    private boolean checkCodeUnique(ReturnPackage returnPackage) {
        return em.createNamedQuery("checkPackageCodeUnique").setParameter("code", returnPackage.getCode()).setParameter("id", returnPackage.getId()).getResultList().size() == 0;
    }
}
