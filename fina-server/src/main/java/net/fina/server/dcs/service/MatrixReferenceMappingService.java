package net.fina.server.dcs.service;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMatrixReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.util.MappingUtil;
import net.fina.common.server.StatisticsLogger;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.jboss.logging.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.getFile;

/*
 *
 * Created by Otar Iantbelidze on 10/12/16.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class MatrixReferenceMappingService {

    private final String MDT_CODE_REGEX = "(?<=\\\")([a-zA-Z0-9_\\-\\.]+)(?=\\\")|(?<=\\')([a-zA-Z0-9_\\-\\.]+)(?=\\')";
    @Inject
    private Logger log;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    private Map<String, Map<String, String>> map = new HashMap<>();
    private Map<String, Long> subMatrix = new HashMap<>();
    private AtomicBoolean synced = new AtomicBoolean();
    private AtomicLong lastChanged = new AtomicLong(0);
    private HashMap<String, String> patternFileMap = new FastHashMap();
    private List<MatrixOptionBase> options = new ArrayList<>();
    private HashMap<String, String> variableMap = new FastHashMap();

    @Lock(LockType.WRITE)
    public void startSync(List<String> changes) {

        try (StatisticsLogger statLog = new StatisticsLogger("Matrix Mapping Service");) {
            statLog.logMessage("Get Matrix Path");
            String matrixPath = propertyLocal.getSystemProperty(PropertyKeys.MATRIX_PATH) + File.separator;
            statLog.logStage("Get Optiopn Sheets");
            options = new ExcelMatrixReader(matrixPath + "Matrix.xls", null).getOptions();

            if (changes.contains("Matrix.xls")) {
                changes.clear();
                map.clear();
                synced.set(false);
            }

            statLog.logStage("Get Sub Matrixs");
            Map<String, Long> subMatrixes = loadSubMatrixes(options, matrixPath);

            statLog.logStage("Reading Matrix Files");
            readAllMatrixFiles(matrixPath, options, subMatrixes, changes);
            statLog.logStage("Maping Mdt Variables to references");
            if (!synced.get() || changes.isEmpty()) {
                variableMap.clear();
                mapMDTVariables();
            }
            synced.set(true);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            synced.set(false);
        }
    }


    @Lock(LockType.WRITE)
    public Map<String, String> getReferenceMap(String fileName) {
        List<String> changes = getChangedMatrixNames();
        if (!synced.get() || !changes.isEmpty()) {
            synced.set(false);
            startSync(changes);
        }
        return getValueMap(fileName);
    }

    @Lock(LockType.READ)
    public Map<String, String> getValueMap(String fileName) {
        for (MatrixOptionBase o : options) {
            String s = o.getPattern().trim();
            Pattern pattern = Pattern.compile(s);
            if (pattern.matcher(fileName).matches()) {
                Map<String, String> result = map.get(s);
                if (result == null) {
                    return findMap(s.trim());
                }
                return result;
            }
        }
        return new HashMap<>();
    }

    private void readAllMatrixFiles(String path, List<MatrixOptionBase> options, Map<String, Long> subMatrix, List<String> changes) {
        try {
            File[] files = getFile(path).listFiles();
            long lastModified = 0;
            assert files != null;
            for (File f : files) {
                if (subMatrix.keySet().contains(f.getName().trim()) && !f.getName().trim().equals("Matrix.xls") && FilenameUtils.getExtension(f.getName().trim()).equals("xls") || FilenameUtils.getExtension(f.getName().trim()).equals("xlsx")) {
                    if (changes.isEmpty() || changes.contains(f.getName().trim())) {

                        lastModified = lastModified < f.lastModified() ? f.lastModified() : lastModified;
                        if (lastChanged.get() < lastModified) {
                            lastChanged.set(lastModified);
                        }
                        log.info("Reading FIle : " + f.getName());
                        Workbook w = WorkbookFactory.create(f);
                        for (int i = 0; i < w.getNumberOfSheets(); i++) {
                            Sheet sheet = w.getSheetAt(i);
                            if (sheet.getSheetName().equalsIgnoreCase("options")) {
                                continue;
                            }
                            Map<String, String> m = new HashMap<>();
                            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                                Row row = sheet.getRow(j);
                                if (row == null || row.getCell(0) == null || row.getCell(2) == null) continue;
                                String code = getStringCellValue(row.getCell(0), sheet.getSheetName(), f.getName(), row.getRowNum());
                                String reference = getStringCellValue(row.getCell(2), sheet.getSheetName(), f.getName(), row.getRowNum());
                                if (code == null || reference == null) {
                                    continue;
                                }
                                String message = code + " { " + sheet.getSheetName() + " : " + reference + "}";
                                m.put(code.trim(), message);
                            }

                            String key = compileAndGet(f.getName().trim(), options);
                            if (key != null && !key.isEmpty()) {
                                if (map.get(key) != null) {
                                    map.get(key).putAll(m);
                                } else {
                                    map.put(key, m);
                                }
                            }
                        }
                    }
                }
            }
            log.info("Matrixes size : " + map.size());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private List<String> getChangedMatrixNames() {
        List<String> changes = new ArrayList<>();
        if (lastChanged.get() == 0) return changes;
        try {
            String matrixPath = propertyLocal.getSystemProperty(PropertyKeys.MATRIX_PATH) + "" + File.separator;
            File matrixDirFile = getFile(matrixPath);
            File[] files = matrixDirFile.listFiles();
            long lastChecked = 0;
            assert files != null;
            for (File f : files) {
                if (FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("xls") || FilenameUtils.getExtension(f.getName()).equalsIgnoreCase("xlsx")) {
                    if (f.isFile() && (subMatrix.containsKey(f.getName().trim()) && subMatrix.get(f.getName().trim()) < f.lastModified())) {
                        changes.add(f.getName().trim());
                        log.info("Matrix Changes Detected : " + f.getName());
                    }
                    lastChecked = lastChecked < f.lastModified() ? f.lastModified() : lastChecked;
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return changes;
    }

    private String compileAndGet(String fileName, List<MatrixOptionBase> options) {
        String result = "";
        for (MatrixOptionBase o : options) {
            if (o.getMatrixForEachType().trim().equals(fileName)) {
                result = o.getPattern();
                return result;
            }
        }
        return result;
    }

    private Map<String, Long> loadSubMatrixes(List<MatrixOptionBase> options, String path) {
        subMatrix.clear();
        subMatrix.put("Matrix.xls", new File(path + "Matrix.xls").lastModified());
        options.stream().filter(o -> o.getMatrixForEachType() != null).forEach(o -> {
            try {
                subMatrix.put(o.getMatrixForEachType().trim(), new File(path + o.getMatrixForEachType().trim()).lastModified());
                patternFileMap.put(o.getPattern().trim(), o.getMatrixForEachType().trim());
            } catch (Throwable ignore) {
            }
        });
        return subMatrix;
    }

    public void mapMDTVariables() {
        Collection<MDTNode> mdtNodes = mdtNodeLocal.getMdtNodesFromCache();
        Pattern pattern = Pattern.compile(MDT_CODE_REGEX);
        Set<String> mappedCOdes = new HashSet<>();
        mdtNodes.stream().filter(node -> node.getType().equals(MDTNodeTypes.VARIABLE)).forEach(node -> {
            constructMapping(node, mappedCOdes, pattern);
        });
        log.info("Variables Size : " + variableMap.size());
    }

    private void constructMapping(MDTNode node, Set<String> mappedCOdes, Pattern pattern) {
        Set<String> extractedCodes = MappingUtil.extractMDTCodes(node.getEquation(), pattern);
        if (extractedCodes.isEmpty() || node.getDependentNodes() == null) {
            return;
        }
        mappedCOdes.add(node.getCode().trim());
        for (MDTNode n : node.getDependentNodes()) {
            if (n.getType() == MDTNodeTypes.VARIABLE && !mappedCOdes.contains(n.getCode().trim())) {
                if (node.getId() != n.getId() && node.getEquation() != null) {
                    constructMapping(n, mappedCOdes, pattern);
                }
            }
        }
        if (extractedCodes.isEmpty()) return;

        String key;
        for (Map.Entry<String, Map<String, String>> e : map.entrySet()) {
            String equation = "";
            for (String code : e.getValue().keySet()) {
                if (extractedCodes.contains(code.trim())) {
                    key = e.getKey();
                    equation = equation.isEmpty() ? node.getEquation() : equation;
                    equation = equation.replaceAll(code, e.getValue().get(code));
                    extractedCodes.remove(code);
                    if (extractedCodes.isEmpty()) {
                        map.get(key).put(node.getCode().trim(), equation);
                        variableMap.put(node.getCode().trim(), equation);
                        return;
                    }
                }
            }
        }
    }

    private String getStringCellValue(Cell cell, String sheetName, String fileName, int row) {
        try {
            return cell.getStringCellValue();
        } catch (IllegalStateException ex) {
            log.error("Cannot get String value from non String cell : { file : " + fileName + ", sheet : " + sheetName + ", Cell : " + CellReference.convertNumToColString(cell.getColumnIndex()) + (row + 1));
        }
        return null;
    }

    private Map<String, String> findMap(String value) {
        try {
            String matrixName = patternFileMap.get(value);
            for (MatrixOptionBase o : options) {
                if (matrixName != null && o.getMatrixForEachType().trim().equals(matrixName.trim()) && map.containsKey(o.getPattern().trim())) {
                    return map.get(o.getPattern().trim());
                }
            }
        } catch (Throwable ignore) {
        }
        return new HashMap<>();
    }

    public Map<String, Map<String, String>> getMap() {
        return map;
    }

    public AtomicBoolean getSynced() {
        return synced;
    }

    public void setSynced(AtomicBoolean synced) {
        this.synced = synced;
    }

    public HashMap<String, String> getVariableMap() {
        return variableMap;
    }
}
