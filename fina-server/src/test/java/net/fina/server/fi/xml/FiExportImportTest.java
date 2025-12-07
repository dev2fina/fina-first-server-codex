package net.fina.server.fi.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import net.fina.common.client.fis.FiReorganization;
import net.fina.common.client.fis.LicenceStatus;
import net.fina.server.fi.entity.Currency;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Management;
import net.fina.server.fi.entity.*;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.FinalBeneficiary;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.entity.metainfo.BusinessEntityType;
import net.fina.server.legalperson.entity.metainfo.EconomicEntityType;
import net.fina.server.legalperson.entity.metainfo.EquityFormType;
import net.fina.server.legalperson.entity.metainfo.ManagementFormType;
import net.fina.server.license.entity.Licence;
import net.fina.server.license.entity.LicenceHistory;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.person.entity.Person;
import net.fina.server.person.model.PersonStatus;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * vamekh on 9/16/16.
 */
public class FiExportImportTest {

    private final String OUT_FILE_PATH = "target/fis_export.xml";

    @Test
    public void TestExport() throws JAXBException, IOException {

        net.fina.server.fi.entity.FiType fiType = new FiType();
        fiType.setId(1);
        fiType.setCode("BNK");

        net.fina.server.fi.entity.Region region = new Region();
        region.setId(1);
        region.setCode("GEO");

        net.fina.server.i18n.helper.Description regionDesc = new Description();
        regionDesc.addDescription(1, "Shida Kartli");
        regionDesc.addDescription(2, "Shida Kartli");
        region.setDescription(regionDesc);

        net.fina.server.fi.entity.PeerGroup pg = new PeerGroup();
        pg.setId(1);
        pg.setCode("BIG");

        net.fina.server.i18n.helper.Description pgDesc = new Description();
        pgDesc.addDescription(1, "Big");
        pgDesc.addDescription(2, "Big");

        LicenceType licenceType = new LicenceType();
        licenceType.setId(1);
        licenceType.setCode("GL");

        Licence lic = new Licence();
        lic.setCode("GL");
        lic.setLicenseType(licenceType);
        lic.setCreationDate(new Date());
        lic.setLicenceStatus(LicenceStatus.ACTIVE);
        lic.setIsDefault(true);

        Management management = new Management();
        management.setId(1);
        management.setCode("CTO");

        FiManagement fiManagement = new FiManagement();
        fiManagement.setManagement(management);
        fiManagement.setAppointmentDate(new Date());

        net.fina.server.i18n.helper.Description managementNameDesc = new Description();
        managementNameDesc.addDescription(1, "Gela");
        managementNameDesc.addDescription(2, "Gela");
        fiManagement.setDescription(managementNameDesc);

        net.fina.server.i18n.helper.Description managementLastDesc = new Description();
        managementLastDesc.addDescription(1, "Bestaevi");
        managementLastDesc.addDescription(2, "Bestaevi");
        fiManagement.setLastDescription(managementLastDesc);

        net.fina.server.i18n.helper.Description postDesc = new Description();
        postDesc.addDescription(1, "Chief Tech Officer");
        postDesc.addDescription(2, "Chief Tech Officer");
        fiManagement.setPost(postDesc);

        fiManagement.setPhone("12345");
        fiManagement.setAppointmentDate(new Date());
        fiManagement.setDateOfApproval(new Date());
        fiManagement.setCancelDate(new Date());
        fiManagement.setResident(false);
        fiManagement.setPosition("Sanitar");
        fiManagement.setPortfolio("Portfoilio");
        fiManagement.setMail("someone@someone.com");
        fiManagement.setAddress("Planet Earth");
        fiManagement.setDependencyStatus(true);
        fiManagement.setDateOfApproval(new Date());

        FiPerson managementPerson = new FiPerson();
        Person mperson = new Person();
        managementPerson.setPerson(mperson);
        mperson.setIdentificationNumber("12313");
        mperson.setCitizenship(region);
        mperson.setStatus(PersonStatus.ACTIVE);
        mperson.setPassportNumber("44112");
        fiManagement.setFiPerson(managementPerson);

        List<FiManagementCommittee> fiManagementCommitteeList = new ArrayList<>();
        FiManagementCommittee committe = new FiManagementCommittee();
        fiManagement.setDescription(managementNameDesc);
        committe.setPosition(managementNameDesc);
        committe.setName(managementNameDesc);
        committe.setComment("comment");
        committe.setApprovalDate(new Date());
        committe.setElectionDate(new Date());

        fiManagementCommitteeList.add(committe);
        fiManagement.setFiManagementCommitteeList(fiManagementCommitteeList);

        FiBranch branch = new FiBranch();
        branch.setId(1);
        branch.setRegion(region);
        branch.setCreateDate(new Date());
        branch.setCode("CODE1");
        branch.setFiBranchType(new FiBranchType(1, "CODE1"));

        FiPerson managerPerson = new FiPerson();
        Person person = new Person();
        managerPerson.setPerson(person);
        person.setIdentificationNumber("12313");
        person.setCitizenship(region);
        person.setStatus(PersonStatus.ACTIVE);
        person.setPassportNumber("1122333");
        branch.setManagerAppointmentDate(new Date());
        branch.setManager(managerPerson);

        FiPerson accountantPersons = new FiPerson();
        Person accountant = new Person();
        accountantPersons.setPerson(accountant);
        accountant.setIdentificationNumber("123ee13");
        accountant.setCitizenship(region);
        accountant.setStatus(PersonStatus.ACTIVE);
        accountant.setPassportNumber("1234");
        branch.setChiefAccountantAppointmentDate(new Date());
        branch.setChiefAccountant(accountantPersons);

        List<Beneficiary> beneficiaries = new ArrayList<>();
        Beneficiary physicalBeneficiary = new Beneficiary();
        physicalBeneficiary.setActive(true);
        physicalBeneficiary.setCurrency(Currency.USD);
        physicalBeneficiary.setNominal(0.1);
        physicalBeneficiary.setShare(10);
        physicalBeneficiary.setCreationDate(new Date());
        physicalBeneficiary.setPhysicalPerson(person);

        List<FinalBeneficiary> finalBeneficiaries = new ArrayList<>();
        FinalBeneficiary finalBeneficiary = new FinalBeneficiary();
        finalBeneficiaries.add(finalBeneficiary);
        finalBeneficiary.setPerson(person);
        physicalBeneficiary.setFinalBeneficiaries(finalBeneficiaries);

        beneficiaries.add(physicalBeneficiary);

        Beneficiary legalBeneficiary = new Beneficiary();
        legalBeneficiary.setActive(true);
        legalBeneficiary.setCurrency(Currency.USD);
        legalBeneficiary.setNominal(0.2);
        legalBeneficiary.setShare(17.5);
        legalBeneficiary.setCreationDate(new Date());
        LegalPerson lp = new LegalPerson();
        lp.setIdentificationNumber("GELA_LLC");
        legalBeneficiary.setLegalPerson(lp);
        legalBeneficiary.setFinalBeneficiaries(finalBeneficiaries);


        beneficiaries.add(legalBeneficiary);


        net.fina.server.i18n.helper.Description branchNameDesc = new Description();
        branchNameDesc.addDescription(1, "Main Branch");
        branchNameDesc.addDescription(2, "Main Branch");
        branch.setName(branchNameDesc);

        net.fina.server.i18n.helper.Description branchShortNameDesc = new Description();
        branchShortNameDesc.addDescription(1, "Main");
        branchShortNameDesc.addDescription(2, "Main");
        branch.setShortName(branchShortNameDesc);

        net.fina.server.i18n.helper.Description branchAddressDesc = new Description();
        branchAddressDesc.addDescription(1, "Tbilisi");
        branchAddressDesc.addDescription(2, "Tbilisi");
        branch.setAddress(branchAddressDesc);

        LicenceHistory lh = new LicenceHistory();
        lh.setId(1);
        lh.setLicence(lic);
        lh.setChangeDate(new Date());
        lh.setLicenceStatus(LicenceStatus.REVOKED);
        lh.setChange("CLOSED");

        net.fina.server.fi.entity.Fi fi = new Fi();

        Language langEn = new Language();
        langEn.setId(1);
        langEn.setCode("en_US");

        Language langKa = new Language();
        langKa.setId(2);
        langKa.setCode("ka_GE");

        net.fina.server.i18n.helper.Description nameDesc = new Description();
        nameDesc.addDescription(1, "TBC Bank");
        nameDesc.addDescription(2, "TBC Bank");

        net.fina.server.i18n.helper.Description shortDesc = new Description();
        shortDesc.addDescription(1, "TBC");
        shortDesc.addDescription(2, "TBC");

        net.fina.server.i18n.helper.Description addressDesc = new Description();
        addressDesc.addDescription(1, "Tbilisi");
        addressDesc.addDescription(2, "Tbilisi");

        fi.setCode("001");
        fi.setFiType(fiType);
        fi.setDescription(nameDesc);
        fi.setShortName(shortDesc);
        fi.setPhone("777");
        fi.setSwiftCode("TB00GE");
        fi.setEmail("tbc@tbc.ge");
        fi.setAddressDescription(addressDesc);
        fi.setLicences(Collections.singletonList(lic));
        fi.setPeerGroup(Collections.singletonList(pg));
        fi.setContactPerson("gela afrasidze");
        fi.setRegistrationDate(new Date());
        fi.setCloseDate(new Date());
        fi.setReorganization(FiReorganization.ACQUISITION);
        fi.setRepresentativePerson("Givi");
        fi.setNumberOfEmploys(11);
        fi.setNumberOfMobileOffices(12);
        fi.setWebSite("www.avoe.ge");
        fi.setIdentificationCode("1221212121212");

        net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo metaInfo = new net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo();
        net.fina.server.i18n.helper.Description businessEntityTypeDescription = new Description();
        businessEntityTypeDescription.addDescription(1, "BUS");
        businessEntityTypeDescription.addDescription(2, "BUS2");

        metaInfo.setBusinessEntityType(new BusinessEntityType(0, "BUS", businessEntityTypeDescription));
        metaInfo.setEconomicEntityType(new EconomicEntityType(0, "ECO", businessEntityTypeDescription));
        metaInfo.setEquityFormType(new EquityFormType(0, "EQUI", businessEntityTypeDescription));
        metaInfo.setManagementFormType(new ManagementFormType(0, "MNGMG", businessEntityTypeDescription));

        fi.setFiAdditionalInfo(metaInfo);
        fi.setDisable(false);


        Map<String, Long> langIdMap = new HashMap<>();
        langIdMap.put(langEn.getCode(), langEn.getId());
        langIdMap.put(langKa.getCode(), langKa.getId());

        Map<String, Region> regionMapByCode = new HashMap<>();
        regionMapByCode.put(region.getCode(), region);

        Map<String, Long> branchTypeCodeIdMap = new HashMap<>();
        branchTypeCodeIdMap.put("CODE1", 1l);
        branchTypeCodeIdMap.put("CODE2", 2l);

        FiXmlHelper helper = new FiXmlHelper(langIdMap, regionMapByCode, branchTypeCodeIdMap, "dd/MM/yyyy");
        net.fina.server.fi.xml.Fi fiXml = helper.fiToXmlModel(fi, region.getCode(), Collections.singletonList(fiManagement), Collections.singletonList(branch), Collections.singletonList(lh), beneficiaries);
        Fis fis = new Fis();
        fis.getFi().add(fiXml);

        OutputStream out = new FileOutputStream(OUT_FILE_PATH);
        JAXBContext context = JAXBContext.newInstance(Fis.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(fis, out);

        out.flush();
        out.close();
    }

    @Test
    public void TestImport() throws Exception {

        net.fina.server.fi.entity.Region region = new Region();
        region.setId(1);
        region.setCode("GEO");

        Language langEn = new Language();
        langEn.setId(1);
        langEn.setCode("en_US");

        Language langKa = new Language();
        langKa.setId(2);
        langKa.setCode("ka_GE");

        Map<String, Long> langIdMap = new HashMap<>();
        langIdMap.put(langEn.getCode(), langEn.getId());
        langIdMap.put(langKa.getCode(), langKa.getId());

        Map<String, Region> regionMapByCode = new HashMap<>();
        regionMapByCode.put(region.getCode(), region);

        Map<String, Long> branchTypeCodeIdMap = new HashMap<>();
        branchTypeCodeIdMap.put("CODE1", 1l);
        branchTypeCodeIdMap.put("CODE2", 2l);

        FiXmlHelper helper = new FiXmlHelper(langIdMap, regionMapByCode, "dd/MM/yyyy", branchTypeCodeIdMap, 1);

        InputStream in = new FileInputStream(OUT_FILE_PATH);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int i;
        while ((i = in.read(buff, 0, 4096)) != -1) {
            byteArray.write(buff, 0, i);
        }
        Fis fis = FiXmlParser.getInstance().importFis(byteArray.toByteArray());
        List<Fi> fiList = new ArrayList<>();
        List<FiManagement> managementList = new ArrayList<>();
        List<FiBranch> fiBranchList = new ArrayList<>();
        List<LicenceHistory> licenceHistoryList = new ArrayList<>();
        List<Beneficiary> beneficiaries = new ArrayList<>();


        for (net.fina.server.fi.xml.Fi fi : fis.getFi()) {
            fiList.add(helper.xmlModelToFi(fi));
            fiBranchList.addAll(helper.xmlModelToFiBranchList(fi.getBranches(), new Fi()));
            managementList.addAll(helper.xmlModelToManagementList(fi.getManagements(), new Fi()));
            licenceHistoryList.addAll(helper.xmlModelToLicenseHistoryList(fi.getHistories()));
            beneficiaries.addAll(helper.xmlToBeneficiaries(fi.getBeneficiaries(), fi.getCode()));
        }

        assertEquals(1, fiList.size());
        assertEquals(1, fiBranchList.size());
        assertEquals(1, managementList.size());
        assertEquals(1, licenceHistoryList.size());

        Fi fi = fiList.get(0);
        assertEquals(1L, (long) fi.getRegionId());
        assertEquals("001", fi.getCode());
        assertEquals("TB00GE", fi.getSwiftCode());
        assertEquals(2, fi.getDescription().getDescriptions().size());
        assertEquals(2, fi.getShortName().getDescriptions().size());
        assertEquals(2, fi.getAddressDescription().getDescriptions().size());
        assertEquals(1, fi.getPeerGroup().size());
        assertEquals("BIG", ((List<PeerGroup>) fi.getPeerGroup()).get(0).getCode());
        assertEquals("777", fi.getPhone());
        assertEquals("tbc@tbc.ge", fi.getEmail());
        assertEquals(1, fi.getLicences().size());

        Licence lic = ((List<Licence>) fi.getLicences()).get(0);
        assertEquals("GL", lic.getCode());
        assertEquals("GL", lic.getLicenseType().getCode());
        assertTrue(lic.getIsDefault());

        FiManagement management = managementList.get(0);
        assertEquals("CTO", management.getManagement().getCode());
        assertEquals("12345", management.getPhone());
        assertEquals(2, management.getDescription().getDescriptions().size());
        assertEquals(2, management.getLastDescription().getDescriptions().size());
        assertEquals(2, management.getPost().getDescriptions().size());

        LicenceHistory lh = licenceHistoryList.get(0);
        assertEquals("GL", lh.getLicence().getCode());
        assertEquals(LicenceStatus.REVOKED, lh.getLicenceStatus());
        assertEquals("CLOSED", lh.getChange());

        FiBranch branch = fiBranchList.get(0);
        assertEquals("GEO", branch.getRegion().getCode());
        assertEquals(2, branch.getName().getDescriptions().size());
        assertEquals(2, branch.getShortName().getDescriptions().size());
        assertEquals(2, branch.getAddress().getDescriptions().size());
    }
}
