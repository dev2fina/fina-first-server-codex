package net.fina.server.fi.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.fi.entity.PeerGroup;

import java.util.List;

public interface PeerGroupLocal {

    List<Criterion> loadNodes();

    List<PeerGroup> loadChildren(long parentId);

    PeerGroup savePeerGroup(PeerGroup peerGroup) throws FinATypeException;

    Criterion saveCriterion(Criterion criterion) throws FinATypeException;

    List<String> setDefault(Long id);

    List<PeerGroup> loadPeerGroupByCodes(List<String> codes);

    Long getDefaultCriteriaId();

    PeerGroup getPeerGroupByCode(String code);

    Criterion loadCriterionById(long id);

    PeerGroup loadGroupById(long id);

    void deletePeerGroup(long id) throws FinATypeException;

    void deleteCriterion(long id) throws FinATypeException;


}
