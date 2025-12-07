package net.fina.server.returns.model;

import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.returns.entity.DefinitionTable;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

public class ReturnReviewConfigModel {
    private List<DefinitionTable> tables;
    private Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap;
    private long langId;
    private byte[] template;
    private DateFormat df;
    private DateFormat dateTimeFormat;
    private ConvertOptions convertOptions;

    private ReturnReviewConfigModel() {
    }


    public Map<ProcessReturnInfo, RDataMetaModel> getInfoRDataMetaModelMap() {
        return infoRDataMetaModelMap;
    }

    public List<DefinitionTable> getTables() {
        return tables;
    }

    public long getLangId() {
        return langId;
    }

    public byte[] getTemplate() {
        return template;
    }

    public DateFormat getDf() {
        return df;
    }

    public DateFormat getDateTimeFormat() {
        return dateTimeFormat;
    }

    public ConvertOptions getConvertOptions() {
        return convertOptions;
    }

    public static class Builder {
        private Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap;
        private List<DefinitionTable> tables;
        private long langId;
        private byte[] template;
        private DateFormat df;
        private DateFormat dateTimeFormat;
        private ConvertOptions convertOptions;

        public Builder infoRDataMetaModelMap(Map<ProcessReturnInfo, RDataMetaModel> infoRDataMetaModelMap) {
            this.infoRDataMetaModelMap = infoRDataMetaModelMap;
            return this;
        }

        public Builder tables(List<DefinitionTable> tables) {
            this.tables = tables;
            return this;
        }

        public Builder langId(long langId) {
            this.langId = langId;
            return this;
        }

        public Builder template(byte[] template) {
            this.template = template;
            return this;
        }

        public Builder df(DateFormat df, DateFormat dateTimeFormat) {
            this.df = df;
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public Builder convertOptions(ConvertOptions convertOptions) {
            this.convertOptions = convertOptions;
            return this;
        }

        public ReturnReviewConfigModel build() {
            ReturnReviewConfigModel configModel = new ReturnReviewConfigModel();
            configModel.infoRDataMetaModelMap = this.infoRDataMetaModelMap;
            configModel.tables = this.tables;
            configModel.langId = this.langId;
            configModel.template = this.template;
            configModel.df = this.df;
            configModel.dateTimeFormat = this.dateTimeFormat;
            configModel.convertOptions = this.convertOptions;

            return configModel;
        }
    }
}
