package net.fina.server.returns.model.helper;


import net.fina.common.client.returns.PeriodModel;
import net.fina.common.client.returns.PeriodTypeModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.model.PeriodMetaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PeriodModelHelper {
    public static List<PeriodMetaModel> toModel(List<Period> periods) {
        List<PeriodMetaModel> result = new ArrayList<>();

        for (Period period : periods) {
            result.add(new PeriodMetaModel().setEntity(period));
        }

        return result;
    }

    public static PeriodModel toModel(Period period, long langId) {
        PeriodModel result = new PeriodModel();
        result.setId(period.getId());
        result.setPeriodNumber(period.getPeriodNumber());
        result.setPeriodType(toModel(period.getPeriodType(), langId));
        result.setFromDate(period.getFromDate());
        result.setToDate(period.getToDate());
        result.setVersion(period.getVersion());

        return result;
    }

    public static Period toPeriod(PeriodModel period, long langId) {
        Period result = new Period();
        result.setId(period.getId());
        result.setPeriodNumber(period.getPeriodNumber());
        result.setPeriodType(toEntity(period.getPeriodType(), langId));
        result.setFromDate(period.getFromDate());
        result.setToDate(period.getToDate());
        result.setVersion(period.getVersion());

        return result;
    }

    public static List<PeriodModel> toModel(List<Period> periods, long langId) {
        return periods.stream().map(p -> toModel(p, langId)).collect(Collectors.toList());
    }

    public static PeriodTypeModel toModel(PeriodType periodType, long langId) {
        PeriodTypeModel result = new PeriodTypeModel();
        result.setId(periodType.getId());
        result.setCode(periodType.getCode());
        result.setPeriodType(periodType.getPeriodType());
        result.setName(periodType.getDescription().getDescription(langId));
        result.setNameStrId(periodType.getDescription().getNameStrId());
        result.setVersion(periodType.getVersion());
        return result;
    }

    public static PeriodType toEntity(PeriodTypeModel periodType, long langId) {
        PeriodType result = new PeriodType();
        result.setId(periodType.getId());
        result.setCode(periodType.getCode());
        result.setPeriodType(periodType.getPeriodType());
        result.setDescription(new Description(langId, periodType.getNameStrId(), periodType.getName()));
        result.setVersion(periodType.getVersion());

        return result;
    }

    public static List<PeriodTypeModel> toModels(List<PeriodType> periodTypes, long langId) {
        return periodTypes.stream().map(pt -> toModel(pt, langId)).collect(Collectors.toList());
    }
}
