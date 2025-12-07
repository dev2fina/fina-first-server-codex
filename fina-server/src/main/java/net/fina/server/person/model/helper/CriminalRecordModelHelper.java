package net.fina.server.person.model.helper;

import net.fina.server.i18n.helper.Description;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.model.CriminalRecordMetaModel;

import java.util.ArrayList;
import java.util.List;

public class CriminalRecordModelHelper {
    public static CriminalRecordMetaModel toModel(CriminalRecord entity, long langId) {
        CriminalRecordMetaModel model = new CriminalRecordMetaModel();

        model.setId(entity.getId());
        model.setCourtDecision(entity.getCourtDecision().getDescription(langId));
        model.setCourtDecisionNameStrId(entity.getCourtDecision().getNameStrId());
        model.setCourtDecisionNumber(entity.getCourtDecisionNumber());
        model.setCourtDecisionDate(entity.getCourtDecisionDate());
        model.setFineAmount(entity.getFineAmount());
        model.setType(entity.getType().getDescription(langId));
        model.setTypeStrId(entity.getType().getNameStrId());
        model.setPunishmentDate(entity.getPunishmentDate());
        model.setPunishmentStartDate(entity.getPunishmentStartDate());
        model.setCurrency(entity.getCurrency());

        return model;
    }

    public static CriminalRecord toEntity(CriminalRecordMetaModel model, long langId) {
        CriminalRecord entity = new CriminalRecord();

        entity.setId(model.getId());
        entity.setCourtDecision(new Description(langId, model.getCourtDecisionNameStrId(), model.getCourtDecision()));
        entity.setCourtDecisionNumber(model.getCourtDecisionNumber());
        entity.setCourtDecisionDate(model.getCourtDecisionDate());
        entity.setFineAmount(model.getFineAmount());
        entity.setPunishmentDate(model.getPunishmentDate());
        entity.setPunishmentStartDate(model.getPunishmentStartDate());
        entity.setType(new Description(langId, model.getTypeStrId(), model.getType()));
        model.setPunishmentStartDate(model.getPunishmentStartDate());
        entity.setCurrency(model.getCurrency());

        return entity;
    }

    public static List<CriminalRecordMetaModel> toModels(List<CriminalRecord> list, long langId) {
        List<CriminalRecordMetaModel> result = new ArrayList<>();
        list.forEach(cr -> result.add(toModel(cr, langId)));
        return result;
    }

    public static List<CriminalRecord> toEntities(List<CriminalRecordMetaModel> list, long langId) {
        List<CriminalRecord> result = new ArrayList<>();
        list.forEach(cr -> result.add(toEntity(cr, langId)));
        return result;
    }
}
