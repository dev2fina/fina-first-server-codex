package net.fina.server.test.converter;

import junit.framework.TestCase;

import org.junit.Ignore;

@Ignore
public class ConverterTest extends TestCase {

    public void test() {
        System.out.println("Deprecated");
    }

//    private static final String RESOURCES_PATH = "src/test/resources/";
//    private static final String TEST_FILE = "converter/BNK12345abcMON032009ORI.xls";
//    private static final String TEST_ZIP_FILE = "converter/BNK12345abcMON032009ORI.zip";
//    private static final String DESTINATION_XMLS_FOLDER = "C:\\fina-server\\xmls\\";
//    private static final String SOURCE_XMLS_LOCATION = RESOURCES_PATH + "converter/xmls/";
//    private static final String TEST_PATTERN = "BNK[0-9]{5}[a-z]{3}(MON|QUART|YEAR)(01|02|03|04|05|06|07|08|09|10|11|12)[1-9][0-9][0-9][0-9](ORI|ORIG)";
//    private static Header header = new Header();
//    private static String SELECTED_MATRIX = "converter/Matrix.BNK.xls";
//    private static byte[] zipContent;
//    private static byte[] xlsContent;
//    private static Map<String, Object> property;
//    private static ExcelMatrixReader.Option option;
//
//    public void initProperty() {
//        Language language = new Language();
//        language.setCode("Junit Test, fina-server");
//        language.setDateFormat("dd, MM. yyyy");
//        language.setFontFace("Arail");
//        language.setHtmlCharSet("ASCII");
//        language.setId(83377778L);
//        language.setName("TEST");
//        language.setNumberFormat("#0.0");
//        language.setVersion(83377778);
//        language.setXmlEncoding("windows-1252");
//
//        property = new HashMap<String, Object>();
//        property.put("TEST.LANGUAGE", language);
//    }
//
//    /**
//     * Reads options sheet from Matrix.xls file
//     */
//    public void testReadMainOptions() throws Exception {
//        System.out.println("----------------------------------------------------");
//        System.out.println("-----------Reading options from Matrix.xls----------");
//        System.out.println("----------------------------------------------------");
//        try {
//            ExcelMatrixReader mainMatrixReader = new ExcelMatrixReader(RESOURCES_PATH + "converter/Matrix.xls", property);
//
//            List<ExcelMatrixReader.Option> options = mainMatrixReader.getOptions();
//
//            for (ExcelMatrixReader.Option opt : options) {
//                System.out.println(opt);
//                if (opt.getPattern() != null) {
//                    if (TEST_FILE.replace(".xls", "").matches(opt.getPattern())) {
//                        option = opt;
//                        SELECTED_MATRIX = opt.getMatrixForEachType();
//                        System.out.println("-----------Selected " + SELECTED_MATRIX + " ! -----------");
//                        System.out.println("");
//                        break;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        System.out.println("----------------------------------------------------");
//        System.out.println("");
//    }
//
//    public void testFileName() throws Exception {
//        System.out.println("-----------------------------------------");
//        System.out.println("-----------File Name validation----------");
//        System.out.println("-----------------------------------------");
//        boolean fileNameValid = TEST_FILE.toLowerCase().replace(".xls", "").matches(TEST_PATTERN.toLowerCase());
//        if (!fileNameValid) {
//            throw new Exception("File name is invalid, it does not match pattern, \n Pattern=" + TEST_PATTERN + " , file name= " + TEST_FILE);
//        } else {
//            System.out.println("--------File name is valid--------------");
//        }
//        System.out.println("");
//    }
//
//    public void testFileType() throws Exception {
//        System.out.println("-----------------------------------------");
//        System.out.println("-----------File Type validation----------");
//        System.out.println("-----------------------------------------");
//        boolean fileTypeValid = (TEST_FILE.toLowerCase().endsWith(".xls") || TEST_FILE.toLowerCase().endsWith(".xlsx") || TEST_FILE.toLowerCase().endsWith(".zip"));
//        if (fileTypeValid) {
//            System.out.println("File " + TEST_FILE + " is valid type  ");
//            System.out.println("-----------------------------------------");
//        } else {
//            throw new Exception("File " + TEST_FILE + " is illegal type");
//        }
//        System.out.println("");
//    }
//
//    public void testFileContent() throws InvalidFormatException, IOException, DcsTypeException {
//        System.out.println("-----------------------------------------");
//        System.out.println("-----------File Content validation-------");
//        System.out.println("-----------------------------------------");
//        try {
//            if (TEST_FILE.toLowerCase().trim().endsWith(".xls")) {
//                ExcelBaseReader excelReader = ExcelBaseReader.getInstance();
//                excelReader.init(RESOURCES_PATH + TEST_FILE, property);
//            } else if (TEST_FILE.toLowerCase().trim().endsWith(".zip")) {
//                ZipDocumentReader documentReader = new ZipDocumentReader(new HashMap<String, Object>());
//                documentReader.openZipAndValidate(TEST_FILE, zipContent);
//            } else if (TEST_FILE.toLowerCase().trim().endsWith(".xml")) {
//
//            } else {
//                //TODO:
//            }
//            System.out.println("File content of " + TEST_FILE + " is valid");
//        } finally {
//            System.out.println("");
//        }
//    }
//
//    public void testStructure() throws Exception {
//        System.out.println("-------------------------------------------");
//        System.out.println("-----------File Structure validation-------");
//        System.out.println("-------------------------------------------");
//        try {
//            xlsContent = readBytesFromAFile(new File(RESOURCES_PATH + TEST_FILE));
//
//            System.out.println("-------------------------------------------");
//            System.out.println("-----------SelectedMatrixPath = '" + (RESOURCES_PATH + SELECTED_MATRIX) + "'");
//            System.out.println("-------------------------------------------");
//
//
//            new ExcelDataFileReader(xlsContent, (RESOURCES_PATH + SELECTED_MATRIX), Conditions.EQUALS, property);
//            new ExcelMappingReader((RESOURCES_PATH + SELECTED_MATRIX), property);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw ex;
//        } finally {
//            System.out.println("-------------File Structure validation completed-----");
//            System.out.println("");
//        }
//
//    }
//
//    public void testHeaderGeneration() throws Exception {
//        System.out.println("-------------------------------------------");
//        System.out.println("-----------File Header Generation----------");
//        System.out.println("-------------------------------------------");
//        FileAnalyzer fileAnalizer = new FileAnalyzer(TEST_PATTERN, TEST_FILE, option, ((Language) property.get("TEST.LANGUAGE")));
//        header = fileAnalizer.getGeneratedHeader();
//        System.out.println("----------Header generation completed successfully--------");
//        System.out.println("");
//    }
//
//    public void testConvert() throws DcsTypeException {
//
//        try {
//            //List<UploadFile> files = new ArrayList<UploadFile>();
//            UploadFile file = readFile();
//
//            System.out.println("-----------------------------------------");
//            System.out.println("-----------Xml Returns Generation--------");
//            System.out.println("-----------------------------------------");
//            Map<String, Object> properties = new HashMap<String, Object>();
//            properties.put("dcs.main.matrix", RESOURCES_PATH + "converter/Matrix.xls");
//            properties.put("dcs.primary.matrix", RESOURCES_PATH + SELECTED_MATRIX);
//            properties.put("dcs.xml.header", header);
//
//            AbstractConverterFactory abstractConverterFactory = AbstractConverterFactory.getInstance();
//            AbstractConverter converter = abstractConverterFactory.createAbstractConverter(file, properties, DocumentType.EXCEL);
//            ConverterInfo info = converter.convert();
//            createXMLS(info.getReturns(), DESTINATION_XMLS_FOLDER);
//        } catch (Throwable ex) {
//            System.out.println(ex.getMessage());
//        } finally {
//            System.out.println("");
//        }
//    }
//
//    private void ZipConvert() throws DcsTypeException {
//        try {
//            List<UploadFile> files = new ArrayList<UploadFile>();
//            readCompressedFiles(files);
//            System.out.println("-----------------------------------------");
//            System.out.println("-----------Xml Returns Generation From Zip File--------");
//            System.out.println("-----------------------------------------");
//            Map<String, Object> properties = new HashMap<String, Object>();
//            properties.put("dcs.main.matrix", RESOURCES_PATH + "converter/Matrix.xls");
//            properties.put("dcs.primary.matrix", RESOURCES_PATH + SELECTED_MATRIX);
//            properties.put("dcs.xml.header", header);
//
////			AbstractConverterFactory abstractConverterFactory = AbstractConverterFactory.get();
////			AbstractConverter converter = abstractConverterFactory.createAbstractConverter(files, properties, DocumentType.ZIP);
////			ConverterInfo info = converter.convert();
////			createXMLS(info.getReturns(), DESTINATION_XMLS_FOLDER);
//        } finally {
//            System.out.println("");
//        }
//    }
//
//    public void testXmlComparisons() {
//        System.out.println("-----------------------------------------");
//        System.out.println("-----------Comparing Xml Files-----------");
//        System.out.println("-----------------------------------------");
//        List<String> returnHeadersErrorInfo = null;
//        List<String> returnItemsErrorInfo = null;
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance("net.fina.server.dcs.impl");
//            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//            File destinationXmlFolder = new File(DESTINATION_XMLS_FOLDER);
//            String destinationFiles[] = destinationXmlFolder.list();
//            LinkedHashMap<String, Return> destinationXmlFiles = new LinkedHashMap<String, Return>();
//            for (String destFile : destinationFiles) {
//                destinationXmlFiles.put(destFile, (Return) unmarshaller.unmarshal(new File(DESTINATION_XMLS_FOLDER + destFile)));
//            }
//
//            File sourceXmlFolder = new File(SOURCE_XMLS_LOCATION);
//            String sourceXmlFiles[] = sourceXmlFolder.list();
//            LinkedHashMap<String, Return> sourceXmlFilesMap = new LinkedHashMap<String, Return>();
//            for (String sourceXml : sourceXmlFiles) {
//                sourceXmlFilesMap.put(sourceXml, (Return) unmarshaller.unmarshal(new File(SOURCE_XMLS_LOCATION + sourceXml)));
//            }
//
//            if (destinationXmlFiles.size() > sourceXmlFilesMap.size()) {
//                throw new Exception("Destination and source xmls amount are different");
//            } else {
//                int counter = 0;
//                for (Map.Entry<String, Return> e : sourceXmlFilesMap.entrySet()) {
//                    String key = e.getKey();
//                    counter++;
//                    Return sourceReturn = e.getValue();
//                    Return destinationReturn = destinationXmlFiles.get(key);
//
//                    returnHeadersErrorInfo = checkReturnHeaders(key, sourceReturn, destinationReturn);
//                    returnItemsErrorInfo = checkReturnItems(key, sourceReturn, destinationReturn);
//
//                    if (returnHeadersErrorInfo.size() != 0 || returnItemsErrorInfo.size() != 0) {
//                        throw new Exception("Verification error for " + counter + "." + key);
//                    } else {
//                        System.out.println("Compared and verified " + counter + "." + key);
//                    }
//                }
//            }
//
//        } catch (Exception ex) {
//            // ex.printStackTrace();
//            System.out.println(ex.getMessage());
//            for (String s : returnHeadersErrorInfo) {
//                System.out.println("      " + s);
//            }
//            for (String i : returnItemsErrorInfo) {
//                System.out.println("      " + i);
//            }
//        } finally {
//            System.out.println("");
//        }
//    }
//
//    private List<String> checkReturnItems(String key, Return sourceReturn, Return destinationReturn) {
//        List<String> errorInfo = new ArrayList<String>();
//        List<Item> sourceItems = sourceReturn.getBody().getItems();
//        List<Item> destItems = destinationReturn.getBody().getItems();
//        if (sourceItems == null)
//            sourceItems = new ArrayList<Item>();
//        if (destItems == null)
//            destItems = new ArrayList<Item>();
//
//        if (sourceItems.size() == destItems.size()) {
//            for (int i = 0; i < sourceItems.size(); i++) {
//                Item sourceItem = sourceItems.get(i);
//                Item destItem = destItems.get(i);
//                String sourceItemCode = sourceItem.getItemCode();
//                String destItemCode = destItem.getItemCode();
//                Integer sourceItemRowNumber = sourceItem.getRow();
//                Integer destItemRowNumber = destItem.getRow();
//                String sourceItemVal = sourceItem.getValue();
//                String destItemVal = destItem.getValue();
//
//                if (sourceItemCode == null || destItemCode == null) {
//                    errorInfo.add("Source item code or destination item code for return " + key + " is null, source item info = " + sourceItem + " , " + destItem);
//                } else if (!sourceItemCode.equals(destItemCode)) {
//                    errorInfo.add("Source item code for return " + key + " does not equal destination returns item code " + sourceItemCode + " != " + destItemCode);
//                }
//
//                if (sourceItemRowNumber != destItemRowNumber) {
//                    errorInfo.add("Source item row number for return " + key + " does not equal destination returns row number, source item info = " + sourceItemCode + " | " + sourceItemRowNumber + ", destination item info = " + destItemCode + " | " + destItemRowNumber);
//                }
//
//                if (sourceItemVal == null || destItemVal == null) {
//                    errorInfo.add("Source item value or destination item value for return " + key + " is null, source item info = " + sourceItem + ", destination item info = " + destItem);
//                } else if (!sourceItemVal.equals(destItemVal)) {
//                    errorInfo.add("Source item value for return " + key + " does not equal destination returns value, source item info = " + sourceItem + ", destination item info = " + destItem);
//                }
//            }
//        } else {
//            errorInfo.add("Source items and destination items size are different, source return contains " + sourceItems.size() + ", destination return contains " + destItems.size());
//        }
//        return errorInfo;
//    }
//
//    private List<String> checkReturnHeaders(String name, Return sourceReturn, Return destReturn) {
//        List<String> errorInfo = new ArrayList<String>();
//        Header sourceHeader = sourceReturn.getHeader();
//        Header destHeader = destReturn.getHeader();
//
//        String sourceBankCode = sourceHeader.getBankCode();
//        String destBankCode = destHeader.getBankCode();
//        String sourceBankName = sourceHeader.getBankName();
//        String destBankName = destHeader.getBankName();
//        String sourceLng = sourceHeader.getLng();
//        String destLng = destHeader.getLng();
//        String sourcePeriodEnd = sourceHeader.getPeriodEnd();
//        String destPeriodEnd = destHeader.getPeriodEnd();
//        String sourcePeriodFrom = sourceHeader.getPeriodFrom();
//        String destPeriodFrom = destHeader.getPeriodFrom();
//        String sourceReturnCode = sourceHeader.getReturnCode();
//        String destReturnCode = destHeader.getReturnCode();
//        String sourceReturnName = sourceHeader.getReturnName();
//        String destReturnName = destHeader.getReturnName();
//        String sourceVersion = sourceHeader.getVer();
//        String destVersion = destHeader.getVer();
//        String sourceSigned = sourceHeader.getSigned();
//        String destSigned = destHeader.getSigned();
//
//        if (sourceBankCode == null || destBankCode == null) {
//            errorInfo.add("Bank code for source or destination return " + name + ", is null ");
//        } else if (!sourceBankCode.equals(destBankCode)) {
//            errorInfo.add("Bank code for return(s) " + name + " does not equal : " + sourceBankCode + " != " + destBankCode);
//        }
//
//        if (sourceBankName == null || destBankName == null) {
//            errorInfo.add("Bank name for source or destination return " + name + ", is null ");
//        } else if (!sourceBankName.equals(destBankName)) {
//            errorInfo.add("Bank name for return(s) " + name + " does not equal : " + sourceBankName + " != " + destBankName);
//        }
//
//        if (sourceLng == null || destLng == null) {
//            errorInfo.add("Language for source or destination return " + name + ", is null ");
//        } else if (!sourceLng.equals(destLng)) {
//            errorInfo.add("Language for return(s) " + name + " does not equal : " + sourceLng + " != " + destBankName);
//        }
//
//        if (sourcePeriodEnd == null || destPeriodEnd == null) {
//            errorInfo.add("Period End for source or destination return " + name + ", is null ");
//        } else if (!sourcePeriodEnd.equals(destPeriodEnd)) {
//            errorInfo.add("Period End for return(s) " + name + " does not equal : " + sourcePeriodEnd + " != " + destPeriodEnd);
//        }
//
//        if (sourcePeriodFrom == null || destPeriodFrom == null) {
//            errorInfo.add("Source Period From for source or destination return " + name + ", is null ");
//        } else if (!sourcePeriodFrom.equals(destPeriodFrom)) {
//            errorInfo.add("Period From for return(s) " + name + " does not equal : " + sourcePeriodFrom + " !=" + destPeriodFrom);
//        }
//
//        if (sourceReturnCode == null || destReturnCode == null) {
//            errorInfo.add("Return code for source or destination return " + name + ", is null ");
//        } else if (!sourceReturnCode.equals(destReturnCode)) {
//            errorInfo.add("Return Code for return(s) " + name + " does not equal : " + sourceReturnCode + " != " + destReturnCode);
//        }
//
//        if (sourceReturnName == null || destReturnName == null) {
//            errorInfo.add("Return name for source or destination return " + name + ", is null ");
//        } else if (!sourceReturnName.equals(destReturnName)) {
//            errorInfo.add("Language for return(s) " + name + " does not equal : " + sourceReturnName + " != " + destReturnName);
//        }
//
//        if (sourceVersion == null || destVersion == null) {
//            errorInfo.add("Version code for source or destination return " + name + ", is null ");
//        } else if (!sourceVersion.equals(destVersion)) {
//            errorInfo.add("Version Code for return(s) " + name + " does not equal : " + sourceVersion + " != " + destVersion);
//        }
//
//        if (sourceSigned == null || destSigned == null) {
//            errorInfo.add("Signed for source or destination return " + name + ", is null ");
//        } else if ((!sourceSigned.equals("null")) || (!destSigned.equals("null"))) {
//            errorInfo.add("Signed parameter for return(s) " + name + " are not null, source signed=" + sourceSigned + ", destination signed=" + destSigned);
//        }
//        return errorInfo;
//    }
//
//    // // Returns the contents of the file in a byte array.
//    // private static void getBytesFromFile(File file) throws IOException {
//    // InputStream is = new FileInputStream(file);
//    //
//    // // Get the size of the file
//    // long length = file.length();
//    //
//    // // Create the byte array to hold the data
//    // xlsContent = new byte[(int) length];
//    //
//    // // Read in the bytes
//    // int offset = 0;
//    // int numRead = 0;
//    // while (offset < xlsContent.length && (numRead = is.read(xlsContent,
//    // offset, xlsContent.length - offset)) >= 0) {
//    // offset += numRead;
//    // }
//    //
//    // // Ensure all the bytes have been read in
//    // if (offset < xlsContent.length) {
//    // is.close();
//    // throw new IOException("Could not completely read file " +
//    // file.getName());
//    // }
//    //
//    // // Close the input stream and return bytes
//    // is.close();
//    //
//    // }
//
//    private static byte[] readBytesFromAFile(File file) {
//        int start = 0;
//        int length = 1024;
//        int offset = -1;
//        byte[] buffer = new byte[length];
//        try {
//            // convert the file content into a byte array
//            FileInputStream fileInuptStream = new FileInputStream(file);
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInuptStream);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            while ((offset = bufferedInputStream.read(buffer, start, length)) != -1) {
//                byteArrayOutputStream.write(buffer, start, offset);
//            }
//            bufferedInputStream.close();
//            byteArrayOutputStream.flush();
//            buffer = byteArrayOutputStream.toByteArray();
//            byteArrayOutputStream.close();
//        } catch (FileNotFoundException fileNotFoundException) {
//            fileNotFoundException.printStackTrace();
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//        return buffer;
//    }
//
//    private static UploadFile readFile() {
//        UploadFile file = new UploadFile();
//        try {
//
//            // getBytesFromFile(new File(RESOURCES_PATH + TEST_FILE));
//            xlsContent = readBytesFromAFile(new File(RESOURCES_PATH + TEST_FILE));
//            file.setUploadedFile(xlsContent);
//            file.setFileName(TEST_FILE);
//            file.setStatus("0");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return file;
//    }
//
//    private static void readCompressedFiles(List<UploadFile> files) {
//        try {
//            UploadFile file = new UploadFile();
//            file.setUploadedFile(readBytesFromAFile(new File(RESOURCES_PATH + TEST_ZIP_FILE)));
//            file.setFileName(TEST_ZIP_FILE);
//            file.setStatus("0");
//            files.add(file);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private static void createXMLS(List<Return> returns, String pathFolder) {
//        try {
//            for (int i = 0; i < returns.size(); i++) {
//
//                JAXBContext context = JAXBContext.newInstance("net.fina.server.dcs.impl");
//                Marshaller marshaller = context.createMarshaller();
//                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//                marshaller.marshal(returns.get(i), new File(pathFolder + "Return_" + returns.get(i).getHeader().getReturnCode() + ".xml"));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
}
