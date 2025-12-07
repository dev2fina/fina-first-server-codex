package net.fina.server.matrix.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.StatisticsLogger;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.matrix.api.MatrixLocal;
import net.fina.server.matrix.api.SubMatrixLocal;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.matrix.model.MatrixImportModel;
import net.fina.server.matrix.model.MatrixModel;
import net.fina.server.matrix.model.helper.MatrixModelHelper;
import net.fina.server.matrix.util.MatrixImportUtil;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.MENU_MATRIX)
public class MatrixProxySession {

    @Inject
    private MatrixLocal matrixLocal;

    @Inject
    private ReturnVersionLocal versionLocal;

    @Inject
    private PeriodLocal periodLocal;

    @Inject
    private FiLocal fiLocal;

    @Inject
    private ReturnDefinitionLocal definitionLocal;

    @EJB
    private MDTCacheManager mdtCacheManager;

    @Inject
    private SubMatrixLocal subMatrixLocal;

    @Inject
    private PropertyLocal propertyLocal;

    public List<MatrixModel> loadAll() {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return MatrixModelHelper.toModels(matrixLocal.load(), langId);
    }

    public MatrixModel loadMatrixById(long id) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return MatrixModelHelper.toModel(matrixLocal.loadMatrixById(id), langId);
    }

    public MatrixModel save(MatrixModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        String plainPassword = model.getPassword();

        MatrixModel res = MatrixModelHelper.toModel(matrixLocal.save(MatrixModelHelper.toEntity(model)), langId);
        res.setPassword(plainPassword);
        return res;
    }

    public void delete(long id) throws FinATypeException {
        matrixLocal.delete(id);
    }

    public byte[] importMatrices(String path) throws FinATypeException {
        if (path == null || path.isBlank()) {
            path = propertyLocal.getSystemProperty(PropertyKeys.MATRIX_PATH);
        }
        try (StatisticsLogger logger = new StatisticsLogger()) {
            logger.logMessage("Init import data");

            Map<String, MDTNode> mdtNodeMap = mdtCacheManager.getMdtNodesByCode();
            Map<String, Long> periodTypeCodeIdMap = periodLocal.loadPeriodTypeCodeIdMap();
            Map<String, Long> versionCodeIdMap = versionLocal.loadRetrunVersionCodeIdMap();
            Map<String, Long> fiTypeCodeIdMap = fiLocal.loadFiTypeCodeIdMap();
            Map<String, ReturnDefinition> definitionsCodeIdMap = definitionLocal.loadReturnDefinitionCodeObjectMap();

            logger.logMessage("Converting to entities...");

            MatrixImportModel matrixImportModel = MatrixImportUtil.convertToEntities(path, mdtNodeMap,
                    definitionsCodeIdMap, fiTypeCodeIdMap, versionCodeIdMap, periodTypeCodeIdMap);

            List<Matrix> mainMatrices = matrixImportModel.getMainMatrices();
            List<SubMatrix> subMatrices = matrixImportModel.getSubMatrices();

            logger.logMessage("Convert Finished, Main Matrix size :" + mainMatrices.size() + ", Sub Matrix size :" + subMatrices.size());
            logger.logMessage("Importing matrices...");
            List<String> mainMatrixWarnings = matrixLocal.importMatrices(mainMatrices);
            List<String> subMatrixWarnings = subMatrixLocal.importSubMatrices(subMatrices);

            logger.logMessage("Import Finished");
            return constructErrorLogMessage(mainMatrixWarnings, subMatrixWarnings);
        }
    }

    private byte[] constructErrorLogMessage(List<String> mainMatrixWarnings, List<String> subMatrixWarnings) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream))) {
            if (!mainMatrixWarnings.isEmpty() || !subMatrixWarnings.isEmpty()) {
                if (!mainMatrixWarnings.isEmpty()) {
                    writer.write("Main Matrix Warnings:");
                    writer.newLine();
                    writer.newLine();
                    for (String line : mainMatrixWarnings) {
                        writer.write(line);
                        writer.newLine();
                    }
                }

                if (!subMatrixWarnings.isEmpty()) {
                    writer.newLine();
                    writer.write("Sub Matrix Warnings:");
                    writer.newLine();
                    writer.newLine();
                    for (String line : subMatrixWarnings) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                writer.flush();
                return byteArrayOutputStream.toByteArray();
            }
            return new byte[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
