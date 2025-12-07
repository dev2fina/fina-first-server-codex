package net.fina.server.reg.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.classifier.api.MDTCatalogLocal;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingOptionAdapter;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.reg.api.RegFileProcessorBaseLocal;
import net.fina.server.reg.api.RegProcessor;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputTypeEnum;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegFileType;
import net.fina.server.reg.qualifier.RegExcelProcessor;
import net.fina.server.reg.qualifier.RegFinaXmlProcessor;
import net.fina.server.reg.qualifier.RegXmlProcessor;
import net.fina.server.reg.util.RegUtil;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(RegFileProcessorBaseLocal.class)
public class RegFileProcessorBaseSession implements RegFileProcessorBaseLocal {
    @Inject
    private Logger log;
    @Inject
    @Any
    private Instance<RegProcessor> regFileProcessors;
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private ProcessingStoreLocal processingStoreLocal;
    @Inject
    private MDTCatalogLocal catalogLocal;

    @Override
    public List<InputsMetaModel> prepareInputs(List<MatrixMappingOptionAdapter> primaryOptions, List<String> definitionCodes, long langId) throws FinATypeException {
        Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();
        int regColumnMaxLength = getStringColumnMaxLength();

        Map<Long, List<ComparisonItem>> comparisons = processingStoreLocal.loadComparisons();
        List<InputsMetaModel> inputsModels = new ArrayList<>();

        for (MatrixMappingOptionAdapter option : primaryOptions) {
            InputsMetaModel inputsMetaModel;

            if (definitionCodes.contains(option.getReturnCode())) {
                Map<String, String> mdtCodeCellReferenceMap = option.getMdtCodeCellReferenceMap();
                Set<String> mdtCodes = mdtCodeCellReferenceMap.keySet();
                MDTNode parentNode = mdtNodeLocal.findParentByCode(mdtCodes.stream().findFirst().get().trim());
                List<MDTNode> mdtNodes = allMdtNodesByParentId.get(parentNode.getId());

                //validate mdt nodes and matrix mappings
                validateMdtNodeMatrixMappings(mdtNodes, mdtCodes);

                inputsMetaModel = new InputsMetaModel();
                inputsMetaModel.setSheetName(option.getSheetName().trim());
                inputsMetaModel.setReturnCode(option.getReturnCode().trim());
                inputsMetaModel.setTableName(parentNode.getCode());
                inputsMetaModel.setStartRow(option.getStartRow());

                List<InputMetaModel> inputMetaModels = new ArrayList<>();
                mdtNodes.sort((n1, n2) -> (int) (n1.getSequence() - n2.getSequence()));

                Map<Long, MDTCatalogMetaModel> catalogMap = new HashMap<>();

                List<MDTCatalog> catalogList = catalogLocal.load(-1, -1, null);
                catalogList.forEach(c -> catalogMap.put(c.getCatalogNode().getId(), new MDTCatalogMetaModel(c.getId(), c.getCatalogNode().getDescription().getDescription(langId), c.getCode())));


                for (MDTNode n : mdtNodes) {
                    InputMetaModel inputMetaModel = new InputMetaModel();
                    inputMetaModel.setColumn(mdtCodeCellReferenceMap.get(n.getCode().trim()));
                    inputMetaModel.setDescription(n.getDescription().getDescription(langId));
                    inputMetaModel.setCode(n.getCode());
                    inputMetaModel.setTypeEnum(RegUtil.getTypeEnum(n.getDataType()));
                    inputMetaModel.setType(inputMetaModel.getTypeEnum().getJavaType());
                    inputMetaModel.setKey(n.isKey());
                    inputMetaModel.setOptional(!n.isKey() && !n.isRequired());
                    inputMetaModel.setMdtNode(n);

                    if (inputMetaModel.getTypeEnum().equals(InputTypeEnum.STRING)) {
                        inputMetaModel.setLength(regColumnMaxLength);
                    }

                    inputMetaModel.setComparisons(comparisons.get(n.getId()));

                    if (n.getType().equals(MDTNodeTypes.LIST)) {
                        Set<String> listElItems = new HashSet<>();
                        long parentNodeId = Long.parseLong(n.getEquation().trim());
                        List<MDTNode> dataNodes = allMdtNodesByParentId.get(parentNodeId);
                        if (n.getEquation() != null && dataNodes != null) {
                            for (MDTNode listElNode : dataNodes) {
                                listElItems.add(listElNode.getEquation());
                            }

                            MDTCatalogMetaModel catalogMetaModel = catalogMap.get(parentNodeId);
                            if (catalogMetaModel != null) {
                                inputMetaModel.setCatalog(catalogMetaModel);
                            }
                        }

                        inputMetaModel.setListElementItems(listElItems);
                    }

                    inputMetaModels.add(inputMetaModel);
                }

                inputsMetaModel.setInputs(inputMetaModels);
                inputsMetaModel.setColumnPrecisionMap(option.getColumnPrecisionMap());
                inputsModels.add(inputsMetaModel);
            }
        }

        return inputsModels;
    }

    @Override
    public int getStringColumnMaxLength() {
        int maxLength = 4000;
        try {
            String maxLengthValue = propertyLocal.getSystemProperty(PropertyKeys.REG_STRING_COLUMN_MAX_LENGTH);
            if (maxLengthValue != null) {
                maxLength = Integer.parseInt(maxLengthValue);
            }
        } catch (Throwable t) {
            log.error("Invalid value or missing SYS_PROPERTY [net.fina.reg.string.column.maxLength]");
            log.error(t.getMessage(), t);
        }
        return maxLength;
    }

    @Override
    public List<String> checkDefinitionCode(List<MatrixMappingOptionAdapter> options, List<String> definitionCodes) {
        List<String> wrongCodes = new ArrayList<>();
        List<String> rghtDefinitionCodes = new ArrayList<>();


        for (MatrixMappingOptionAdapter option : options) {
            String code = option.getReturnCode();
            if (!definitionCodes.contains(code)) {
                wrongCodes.add(code);
            } else {
                rghtDefinitionCodes.add(code);
            }
        }
        if (!wrongCodes.isEmpty()) {
            log.error("Wrong return codes: " + wrongCodes);
            String sb = "${net.fina.dcs.converter.incorrectReturnCode}" + " : " + String.join(",", wrongCodes);
            DcsTypeException ex = new DcsTypeException(sb);
            ex.setType(DcsTypeException.Type.GENERAL_ERROR);
            throw ex;
        }

        return rghtDefinitionCodes;
    }

    @Override
    public RegProcessor getInstance(String extension) {
        RegFileType fileType = RegFileType.getTypeOrDefault(extension);
        switch (fileType) {
            case XML:
                return regFileProcessors.select(new AnnotationLiteral<RegXmlProcessor>() {
                }).get();
            case EXCEL:
                return regFileProcessors.select(new AnnotationLiteral<RegExcelProcessor>() {
                }).get();
            case FINA:
                return regFileProcessors.select(new AnnotationLiteral<RegFinaXmlProcessor>() {
                }).get();
        }

        return regFileProcessors.select(new AnnotationLiteral<RegExcelProcessor>() {
        }).get();
    }

    @Override
    public String getReason(String reason) {
        if (reason != null) {
            if (reason.length() > 255) {
                StringBuilder sb = new StringBuilder(reason);
                sb.replace(251, sb.length(), "...");
                reason = sb.toString();
            }
        }
        return reason;
    }


    private void validateMdtNodeMatrixMappings(List<MDTNode> mdtNodes, Set<String> matrixMdtCodes) throws FinATypeException {
        List<String> mdtNodeCodes = mdtNodes.stream().map(MDTNode::getCode).collect(Collectors.toList());
        if (mdtNodes.size() > matrixMdtCodes.size()) {
            List<String> unmappedMdtNodes = mdtNodeCodes.stream().filter(code -> !matrixMdtCodes.contains(code.trim())).collect(Collectors.toList());
            //this codes is not mapped in matrix
            throw new FinATypeException("Following mdt codes is not mapped in matrix " + unmappedMdtNodes);
        } else if (mdtNodes.size() < matrixMdtCodes.size()) {
            //matrix has more code mappings then actual mdt nodes , just ignore and log
            log.warn("Reg File Processing : matrix has more code mappings then actual mdt nodes ");
        }

        if (!new HashSet<>(mdtNodeCodes).containsAll(matrixMdtCodes)) {
            throw new FinATypeException("Following mdt codes from matrix are not valid  " + matrixMdtCodes.stream().filter(c -> !mdtNodeCodes.contains(c)).collect(Collectors.toList()));
        }

    }
}
