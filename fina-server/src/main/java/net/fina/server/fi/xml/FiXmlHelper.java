package net.fina.server.fi.xml;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.FiReorganization;
import net.fina.common.client.fis.LicenceStatus;
import net.fina.common.server.util.CommonUtil;
import net.fina.messages.MessagesUtil;
import net.fina.server.fi.entity.Currency;
import net.fina.server.fi.entity.*;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.FinalBeneficiary;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.entity.metainfo.*;
import net.fina.server.license.entity.Licence;
import net.fina.server.license.entity.LicenceHistory;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.person.entity.Person;
import net.fina.server.person.entity.PersonPosition;
import org.jboss.logging.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * vamekh on 9/16/16.
 */
public class FiXmlHelper {
    private final Logger log = Logger.getLogger(getClass().getName());

    private final SimpleDateFormat df;
    private final Map<String, Long> langIdMap;
    private final Map<Long, String> langCodeMap;
    private final Map<String, net.fina.server.fi.entity.Region> regionMapByCode;
    private final Map<String, Long> branchTypeCodeIdMap;
    private int regionMaxLevel;
    private Map<Long, Region> regionLevelStore;

    public FiXmlHelper(Map<String, Long> langIdMap, Map<String, net.fina.server.fi.entity.Region> regionMapByCode, Map<String, Long> branchTypeCodeIdMap, String dateFormat) {
        this.langIdMap = langIdMap;
        this.langCodeMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : langIdMap.entrySet()) {
            langCodeMap.put(entry.getValue(), entry.getKey());
        }
        this.regionMapByCode = regionMapByCode;
        this.branchTypeCodeIdMap = branchTypeCodeIdMap;
        df = new SimpleDateFormat(dateFormat);
    }

    public FiXmlHelper(Map<String, Long> langIdMap, Map<String, net.fina.server.fi.entity.Region> regionMapByCode, String dateFormat, Map<String, Long> branchTypeCodeIdMap, int regionMaxLevel) {
        this(langIdMap, regionMapByCode, branchTypeCodeIdMap, dateFormat);
        this.regionMaxLevel = regionMaxLevel;
        regionLevelStore = new HashMap<>();
    }

    public net.fina.server.fi.entity.Fi xmlModelToFi(Fi fiXml) throws ParseException, FinATypeException {
        regionMapByCode.values().stream().forEach(region -> regionLevelStore.put(region.getId(), region));

        net.fina.server.fi.entity.Fi fi = new net.fina.server.fi.entity.Fi();

        if (fiXml.getCode() == null || fiXml.getCode().trim().isEmpty()) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.exception.codeNull"));
        }

        if (fiXml.getType() == null || fiXml.getType().trim().isEmpty()) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.exception.typeNull"));
        }

        if (fiXml.getRegion() == null || fiXml.getRegion().trim().isEmpty()) {
            throw new FinATypeException(MessagesUtil.getString("net.fina.exception.regionNull"));
        }

        fi.setCode(fiXml.getCode());
        fi.setFiType(new FiType(fiXml.getType()));
        setRegion(fi, fiXml.getRegion());
        fi.setDescription(xmlDescToDesc(fiXml.getName()));
        fi.setShortName(xmlDescToDesc(fiXml.getShortname()));
        fi.setAddressDescription(xmlDescToDesc(fiXml.getAddress()));
        fi.setSwiftCode(fiXml.getSwift());
        fi.setPhone(fiXml.getPhone());
        fi.setFax(fiXml.getFax());
        fi.setEmail(fiXml.getEmail());
        fi.setLegalForm(fiXml.getLegalForm());
        fi.setIdentificationCode(fiXml.getIdentificationCode());
        fi.setContactPerson(fi.getContactPerson());
        fi.setRegistrationDate(parseDate(fiXml.getRegistrationDate()));
        fi.setCloseDate(parseDate(fiXml.getCloseDate()));
        fi.setReorganization(fiXml.getReorganization() != null && !fiXml.getReorganization().isEmpty() ? FiReorganization.valueOf(fiXml.getReorganization().toUpperCase()) : null);
        fi.setRepresentativePerson(fiXml.getRepresentativePerson());
        fi.setNumberOfEmploys(fiXml.getNumberOfEmploys());
        fi.setNumberOfMobileOffices(fiXml.getNumberOfMobileOffices());
        fi.setWebSite(fiXml.getWebSite());
        fi.setDisable(fiXml.isDisable());

        LegalPersonMetaInfo fiMetaInfo = fiXml.getMetaInfo();
        if (fiMetaInfo != null) {
            net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo metaInfo = new net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo();

            metaInfo.setBusinessEntityType(convertToLegalEntityType(fiMetaInfo.getBusinessEntityType(), BusinessEntityType.class));
            metaInfo.setEconomicEntityType(convertToLegalEntityType(fiMetaInfo.getEconomicEntityType(), EconomicEntityType.class));
            metaInfo.setEquityFormType(convertToLegalEntityType(fiMetaInfo.getEquityFormType(), EquityFormType.class));
            metaInfo.setManagementFormType(convertToLegalEntityType(fiMetaInfo.getManagementFormType(), ManagementFormType.class));

            fi.setFiAdditionalInfo(metaInfo);
        }

        List<PeerGroup> peerGroupList = new ArrayList<>();
        if (fiXml.getGroups() != null) {
            for (Group group : fiXml.getGroups().getGroup()) {
                PeerGroup pg = new PeerGroup();
                pg.setCode(group.getCode());
                peerGroupList.add(pg);
            }
        }
        fi.setPeerGroup(peerGroupList);

        List<Licence> licenseList = new ArrayList<>();
        if (fiXml.getLicenses() != null) {
            for (License lic : fiXml.getLicenses().getLicense()) {
                Licence license = new Licence();
                license.setCode(lic.getCode());
                license.setLicenseType(new LicenceType(lic.getTypeCode()));
                license.setCreationDate(parseDate(lic.getLicDate()));
                license.setDateOfChange(parseDate(lic.getChangeDate()));
                license.setIsDefault(lic.isDefault());
                license.setLicenceStatus(lic.getStatus() != null ? LicenceStatus.valueOf(lic.getStatus().toString()) : null);
                license.setReasons(xmlDescToDesc(lic.getReason()));
                licenseList.add(license);
            }
        }
        fi.setLicences(licenseList);

        return fi;
    }

    public List<FiManagement> xmlModelToManagementList(Managements managements, net.fina.server.fi.entity.Fi fi) throws ParseException, FinATypeException {
        List<FiManagement> managementList = new ArrayList<>();
        if (managements != null) {
            for (Management management : managements.getManagement()) {
                FiManagement m = new FiManagement();
                m.setManagement(new net.fina.server.fi.entity.Management(management.getCode()));
                m.setPhone(management.getPhone());
                m.setAppointmentDate(parseDate(management.getAppointmentDate()));
                m.setCancelDate(parseDate(management.getCancelDate()));
                m.setDescription(xmlDescToDesc(management.getFirstName()));
                m.setLastDescription(xmlDescToDesc(management.getLastName()));
                m.setPost(xmlDescToDesc(management.getPost()));
                m.setRegistrationId1(xmlDescToDesc(management.getRegFirst()));
                m.setRegistrationId2(xmlDescToDesc(management.getRegSecond()));
                m.setRegistrationId2(xmlDescToDesc(management.getRegThird()));
                m.setCommectId1(xmlDescToDesc(management.getCommentFirst()));
                m.setCommectId2(xmlDescToDesc(management.getCommentSecond()));
                m.setResident(management.isResident());
                m.setDisable(management.isDisable());
                m.setPosition(management.getPosition());
                m.setPortfolio(management.getPortfolio());
                m.setNumberOfReportingEmployees(management.getNumberOfReportingEmployees());
                m.setMail(management.getMail());
                m.setAddress(management.getAddress());
                m.setDependencyStatus(management.isDependencyStatus());
                m.setDateOfApproval(parseDate(management.getDateOfApproval()));

                if (management.getPerson() != null) {
                    net.fina.server.fi.xml.Person managementPerson = management.getPerson();
                    FiPerson fiPerson = new FiPerson();
                    fiPerson.setFi(fi);
                    Person person = new Person();
                    person.setIdentificationNumber(managementPerson.getPersonIdNumber());
                    if (regionMapByCode.get(managementPerson.getCountryCode()) == null) {
                        throw new FinATypeException(String.format("Region with code [ %s ] does not exist, Management person: [ %s ], Fi: [ %s ]", managementPerson.getCountryCode(), person.getIdentificationNumber(), fi.getCode()));
                    }
                    person.setCountry(regionMapByCode.getOrDefault(managementPerson.getCountryCode(), null));
                    person.setPositions(xmlPositionsToPositions(managementPerson.getPersonPositions().getPositions(), person));
                    person.setName(xmlDescToDesc(managementPerson.getName()));
                    person.setResidentStatus(managementPerson.getStatus());
                    person.setStatus(managementPerson.getActiveStatus());
                    person.setPassportNumber(managementPerson.getPassportNumber());

                    fiPerson.setPerson(person);
                    m.setFiPerson(fiPerson);
                }

                if (management.getManagementCommittees() != null) {
                    List<FiManagementCommittee> fiManagementCommitteeList = new ArrayList<>();
                    m.setFiManagementCommitteeList(fiManagementCommitteeList);

                    management.getManagementCommittees().getCommittees().forEach(c -> {
                        FiManagementCommittee managementCommittee = new FiManagementCommittee();
                        managementCommittee.setComment(c.comment);
                        managementCommittee.setElectionDate(parseDate(c.getElectionDate()));
                        managementCommittee.setApprovalDate(parseDate(c.getApprovalDate()));
                        managementCommittee.setName(xmlDescToDesc(c.getName()));
                        managementCommittee.setPosition(xmlDescToDesc(c.getPosition()));

                        fiManagementCommitteeList.add(managementCommittee);
                    });
                }
                managementList.add(m);
            }
        }

        return managementList;
    }

    public List<FiBranch> xmlModelToFiBranchList(Branches branches, net.fina.server.fi.entity.Fi fi) throws ParseException {
        List<FiBranch> fiBranchList = new ArrayList<>();
        if (branches != null) {
            for (Branch branch : branches.getBranch()) {
                FiBranch br = new FiBranch();
                br.setCode(branch.getCode());
                br.setAddress(xmlDescToDesc(branch.getAddress()));
                br.setName(xmlDescToDesc(branch.getName()));
                br.setShortName(xmlDescToDesc(branch.getShortname()));
                br.setCreateDate(parseDate(branch.getCreateDate()));
                br.setChangeDate(parseDate(branch.getChangeDate()));
                br.setComment(xmlDescToDesc(branch.getComment()));
                br.setRegion(regionMapByCode.getOrDefault(branch.getRegion(), null));
                br.setDisable(branch.isDisable());
                br.setCloseDate(parseDate(branch.getCloseDate()));
                br.setSuspensionDate(parseDate(branch.getSuspensionDate()));
                br.setRenewalDate(parseDate(branch.getRenewalDate()));
                br.setEmail(branch.getEmail());
                br.setPhone(branch.getPhone());
                br.setRegistrationNumber(branch.getRegistrationNumber());
                br.setStorageAvailable(branch.isStorageAvailable());

                if (branch.getTypeCode() != null && !branch.getTypeCode().trim().isEmpty() && branchTypeCodeIdMap.containsKey(branch.getTypeCode().trim())) {
                    br.setFiBranchType(new FiBranchType(branchTypeCodeIdMap.get(branch.getTypeCode().trim())));
                }

                if (branch.getChiefAccountant() != null) {
                    br.setChiefAccountantAppointmentDate(parseDate(branch.getChiefAccountant().getAppointmentDate()));

                    FiPerson accountantPerson = new FiPerson();
                    accountantPerson.setFi(fi);

                    Person person = new Person();
                    person.setIdentificationNumber(branch.getChiefAccountant().getPersonIdNumber());
                    person.setCountry(regionMapByCode.getOrDefault(branch.getChiefAccountant().getCountryCode(), null));

                    accountantPerson.setPerson(person);

                    br.setChiefAccountant(accountantPerson);
                }

                if (branch.getManager() != null) {
                    br.setManagerAppointmentDate(parseDate(branch.getManager().getAppointmentDate()));

                    FiPerson accountantPerson = new FiPerson();
                    accountantPerson.setFi(fi);

                    Person person = new Person();
                    person.setIdentificationNumber(branch.getManager().getPersonIdNumber());
                    person.setCountry(regionMapByCode.getOrDefault(branch.getManager().getCountryCode(), null));

                    accountantPerson.setPerson(person);

                    br.setManager(accountantPerson);
                }

                fiBranchList.add(br);
            }
        }

        return fiBranchList;
    }

    public List<LicenceHistory> xmlModelToLicenseHistoryList(Histories histories) throws ParseException {
        List<LicenceHistory> licenceHistoryList = new ArrayList<>();
        if (histories != null) {
            for (History history : histories.getHistory()) {
                LicenceHistory lh = new LicenceHistory();
                Licence licence = new Licence();
                licence.setCode(history.getLicenseCode());
                lh.setLicence(licence);
                lh.setLicenceStatus(LicenceStatus.values()[history.getStatus().ordinal()]);
                lh.setChangeDate(parseDate(history.getChangeDate()));
                lh.setChange(history.getValue());
                licenceHistoryList.add(lh);
            }
        }

        return licenceHistoryList;
    }

    public Fi fiToXmlModel(net.fina.server.fi.entity.Fi fi, String region, List<FiManagement> fiManagements, List<FiBranch> fiBranches, List<LicenceHistory> licenseHistories, List<Beneficiary> beneficiaries) {
        Fi fiXml = new Fi();
        fiXml.setCode(fi.getCode());
        fiXml.setType(fi.getFiType().getCode());
        fiXml.setName(descToXmlDesc(fi.getDescription()));
        fiXml.setShortname(descToXmlDesc(fi.getShortName()));
        fiXml.setAddress(descToXmlDesc(fi.getAddressDescription()));
        fiXml.setSwift(fi.getSwiftCode());
        fiXml.setPhone(fi.getPhone());
        fiXml.setFax(fi.getFax());
        fiXml.setEmail(fi.getEmail());
        fiXml.setIdentificationCode(fi.getIdentificationCode());
        fiXml.setLegalForm(fi.getLegalForm());
        fiXml.setRegion(region);
        fiXml.setContactPerson(fi.getContactPerson());
        fiXml.setRegistrationDate(formatDate(fi.getRegistrationDate()));
        fiXml.setCloseDate(formatDate(fi.getCloseDate()));
        fiXml.setReorganization(fi.getReorganization() != null ? fi.getReorganization().name() : "");
        fiXml.setRepresentativePerson(fi.getRepresentativePerson());
        fiXml.setNumberOfEmploys(fi.getNumberOfEmploys());
        fiXml.setNumberOfMobileOffices(fi.getNumberOfMobileOffices());
        fiXml.setWebSite(fi.getWebSite());
        fiXml.setDisable(fi.isDisable());

        net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo fiMetaInfo = fi.getFiAdditionalInfo();
        if (fiMetaInfo != null) {
            LegalPersonMetaInfo metaInfo = new LegalPersonMetaInfo();
            BusinessEntityType beType = fiMetaInfo.getBusinessEntityType();
            EconomicEntityType economicEntityType = fiMetaInfo.getEconomicEntityType();
            EquityFormType equityFormType = fiMetaInfo.getEquityFormType();
            ManagementFormType managementFormType = fiMetaInfo.getManagementFormType();

            if (beType != null) {
                metaInfo.setBusinessEntityType(getCodeDescriptionModel(beType));
            }
            if (economicEntityType != null) {
                metaInfo.setEconomicEntityType(getCodeDescriptionModel(economicEntityType));
            }
            if (equityFormType != null) {
                metaInfo.setEquityFormType(getCodeDescriptionModel(equityFormType));
            }
            if (managementFormType != null) {
                metaInfo.setManagementFormType(getCodeDescriptionModel(managementFormType));
            }

            fiXml.setMetaInfo(metaInfo);
        }

        Groups groups = new Groups();
        for (PeerGroup pg : fi.getPeerGroup()) {
            groups.getGroup().add(new Group(pg.getCode()));
        }
        fiXml.setGroups(groups);

        Licenses licenses = new Licenses();
        for (Licence lic : fi.getLicences()) {
            net.fina.server.fi.xml.License xmlLicense = new License();
            xmlLicense.setCode(lic.getCode());
            xmlLicense.setTypeCode(lic.getLicenseType().getCode());
            xmlLicense.setDefault(lic.getIsDefault());
            xmlLicense.setReason(descToXmlDesc(lic.getReasons()));
            xmlLicense.setLicDate(formatDate(lic.getCreationDate()));
            xmlLicense.setChangeDate(formatDate(lic.getDateOfChange()));
            xmlLicense.setStatus(LicenseStatus.valueOf(lic.getLicenceStatus().toString()));
            licenses.getLicense().add(xmlLicense);
        }
        fiXml.setLicenses(licenses);

        Managements managements = new Managements();
        for (FiManagement m : fiManagements) {
            if (m.getManagement() == null) {
                continue;
            }
            Management mgmt = new Management(m.getManagement().getCode(), descToXmlDesc(m.getDescription()), descToXmlDesc(m.getLastDescription()), m.getPhone(), formatDate(m.getAppointmentDate()), formatDate(m.getCancelDate()), descToXmlDesc(m.getPost()), descToXmlDesc(m.getRegistrationId1()), descToXmlDesc(m.getRegistrationId2()), descToXmlDesc(m.getRegistrationId3()), descToXmlDesc(m.getCommectId1()), descToXmlDesc(m.getCommectId2()), m.isResident(), m.isDisable());
            mgmt.setPosition(m.getPosition());
            mgmt.setAddress(m.getAddress());
            mgmt.setAppointmentDate(formatDate(m.getAppointmentDate()));
            mgmt.setCancelDate(formatDate(m.getCancelDate()));
            mgmt.setDateOfApproval(formatDate(m.getDateOfApproval()));
            mgmt.setPortfolio(m.getPortfolio());
            mgmt.setMail(m.getMail());
            if (m.getFiPerson() != null) {
                mgmt.setPerson(new net.fina.server.fi.xml.Person(m.getFiPerson().getPerson()));
                mgmt.getPerson().setName(descToXmlDesc(m.getFiPerson().getPerson().getName()));
            }


            managements.getManagement().add(mgmt);
        }
        fiXml.setManagements(managements);

        Histories histories = new Histories();
        for (LicenceHistory lh : licenseHistories) {
            histories.getHistory().add(new History(lh.getLicence().getCode(), formatDate(lh.getChangeDate()), LicenseStatus.values()[lh.getLicenceStatus().ordinal()], lh.getChange()));
        }
        fiXml.setHistories(histories);

        Branches branches = new Branches();
        for (FiBranch fiBranch : fiBranches) {
            String regionCode = fiBranch.getRegion() != null ? fiBranch.getRegion().getCode() : null;
            String creationDate = formatDate(fiBranch.getCreateDate());
            String changeDate = formatDate(fiBranch.getChangeDate());
            Branch br = new Branch(fiBranch.getCode(), descToXmlDesc(fiBranch.getName()), descToXmlDesc(fiBranch.getShortName()), descToXmlDesc(fiBranch.getAddress()), regionCode, creationDate, changeDate, descToXmlDesc(fiBranch.getComment()), fiBranch.isDisable());
            br.setDisable(fiBranch.isDisable());
            br.setCloseDate(formatDate(fiBranch.getCloseDate()));
            br.setSuspensionDate(formatDate(fiBranch.getSuspensionDate()));
            br.setRenewalDate(formatDate(fiBranch.getRenewalDate()));
            br.setEmail(fiBranch.getEmail());
            br.setPhone(fiBranch.getPhone());
            br.setRegistrationNumber(fiBranch.getRegistrationNumber());
            br.setStorageAvailable(fiBranch.getStorageAvailable() != null ? fiBranch.getStorageAvailable() : false);

            if (fiBranch.getFiBranchType() != null) {
                br.setTypeCode(fiBranch.getFiBranchType().getCode());
            }

            if (fiBranch.getManager() != null) {
                br.setManager(getBranchManager(fiBranch.getManager(), fiBranch.getManagerAppointmentDate()));
            }

            if (fiBranch.getChiefAccountant() != null) {
                br.setChiefAccountant(getBranchManager(fiBranch.getChiefAccountant(), fiBranch.getChiefAccountantAppointmentDate()));
            }

            branches.getBranch().add(br);
        }
        fiXml.setBranches(branches);

        if (beneficiaries != null) {
            Beneficiaries xmlBeneficiaries = new Beneficiaries();
            for (Beneficiary b : beneficiaries) {
                net.fina.server.fi.xml.Beneficiary xmlBeneficiary = new net.fina.server.fi.xml.Beneficiary();
                xmlBeneficiary.setShare(b.getShare());
                xmlBeneficiary.setNominal(b.getNominal());
                xmlBeneficiary.setActive(b.isActive());
                if (b.getCurrency() != null) {
                    xmlBeneficiary.setCurrency(b.getCurrency().name());
                }
                xmlBeneficiary.setCreationDate(formatDate(b.getCreationDate()));

                if (b.getPhysicalPerson() != null) {
                    xmlBeneficiary.setIdentificationNumber(b.getPhysicalPerson().getIdentificationNumber());
                    xmlBeneficiary.setCountryCode(b.getPhysicalPerson().getCountry().getCode());
                    xmlBeneficiary.setBeneficiaryType("PHYSICAL");

                } else if (b.getLegalPerson() != null) {
                    xmlBeneficiary.setIdentificationNumber(b.getLegalPerson().getIdentificationNumber());
                    xmlBeneficiary.setBeneficiaryType("LEGAL");
                } else {
                    continue;
                }

                FinalBeneficiaries finalBeneficiaries = new FinalBeneficiaries();
                for (FinalBeneficiary fb : b.getFinalBeneficiaries()) {
                    net.fina.server.fi.xml.FinalBeneficiary xmlFb = new net.fina.server.fi.xml.FinalBeneficiary();
                    if (fb.getPerson() != null) {
                        xmlFb.setPhysicalPerson(new net.fina.server.fi.xml.Person(fb.getPerson()));
                        xmlFb.getPhysicalPerson().setName(descToXmlDesc(fb.getPerson().getName()));
                        finalBeneficiaries.getFinalBeneficiaries().add(xmlFb);
                        xmlBeneficiary.setFinalBeneficiaries(finalBeneficiaries);
                    }
                }

                xmlBeneficiaries.getBeneficiary().add(xmlBeneficiary);
            }

            fiXml.setBeneficiaries(xmlBeneficiaries);
        }

        return fiXml;
    }

    private BranchManager getBranchManager(FiPerson fiPerson, Date appointmentDate) {
        BranchManager manager = new BranchManager();
        Person managerPerson = fiPerson.getPerson();
        Region managerCountry = managerPerson.getCountry();
        manager.setCountryCode(managerCountry != null ? managerCountry.getCode() : null);
        manager.setPersonIdNumber(managerPerson.getIdentificationNumber());
        manager.setAppointmentDate(formatDate(appointmentDate));

        return manager;
    }

    private Descriptions descToXmlDesc(net.fina.server.i18n.helper.Description description) {
        Descriptions descriptions = new Descriptions();
        if (description != null) {
            for (Entry<String, Long> entry : langIdMap.entrySet()) {
                String desc = description.getDescription(entry.getValue());
                if (desc != null && !desc.isEmpty()) {
                    descriptions.getDescription().add(new net.fina.server.fi.xml.Description(entry.getKey(), desc));
                }
            }
        }

        return descriptions;
    }

    private net.fina.server.i18n.helper.Description xmlDescToDesc(Descriptions descriptions) {
        net.fina.server.i18n.helper.Description desc = new net.fina.server.i18n.helper.Description();
        if (descriptions != null) {
            for (net.fina.server.fi.xml.Description description : descriptions.getDescription()) {
                Long langId = langIdMap.get(description.getLangCode().trim());
                if (langId != null) {
                    desc.addDescription(langId, description.getValue());
                }
            }
        }

        return desc;
    }

    private void setRegion(net.fina.server.fi.entity.Fi fi, String regionCode) throws FinATypeException {
        if (!regionMapByCode.containsKey(regionCode.trim())) {
            throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.fi.exception.regionNotFound"), regionCode));
        }
        Region region = regionMapByCode.get(regionCode.trim());
        if (getCurRegionLevelDepth(region) != regionMaxLevel) {
            throw new FinATypeException(CommonUtil.compileMessageWithParams(MessagesUtil.getString("net.fina.fi.exception.region.invalidLevel"), regionCode));
        }
        fi.setRegionId(region.getId());
    }

    private int getCurRegionLevelDepth(Region r) {
        int depthCounter = 1;
        while (r != null && r.getParentId() > 0) {
            r = regionLevelStore.get(r.getParentId());
            depthCounter++;
        }
        return depthCounter;
    }

    private String formatDate(Date date) {
        return date != null ? df.format(date) : "";
    }

    private Date parseDate(String date) {
        try {
            return date == null || date.isEmpty() ? null : df.parse(date);
        } catch (Throwable t) {
            log.error("Invalid Date Format : " + date);
        }

        return null;
    }

    private CodeDescription getCodeDescriptionModel(BaseLegalEntityType type) {
        CodeDescription descriptionCodeModel = new CodeDescription();
        descriptionCodeModel.setCode(type.getCode());
        if (type.getDescription() != null) {
            Descriptions descriptions = new Descriptions();
            descriptionCodeModel.setDescriptions(descriptions);
            for (Map.Entry<Long, String> entry : type.getDescription().getDescriptions().entrySet()) {
                descriptions.getDescription().add(new Description(langCodeMap.get(entry.getKey()), entry.getValue()));
            }

            return descriptionCodeModel;
        }
        return null;
    }

    public List<Beneficiary> xmlToBeneficiaries(Beneficiaries beneficiaries, String fiCode) throws FinATypeException {
        if (beneficiaries == null) {
            return new ArrayList<>();
        }
        List<Beneficiary> result = new ArrayList<>();
        for (net.fina.server.fi.xml.Beneficiary xb : beneficiaries.getBeneficiary()) {
            Beneficiary b = new Beneficiary();
            b.setActive(xb.isActive());
            b.setCreationDate(parseDate(xb.getCreationDate()));
            if (xb.getCurrency() != null) {
                b.setCurrency(Currency.valueOf(xb.getCurrency().toUpperCase()));
            }
            b.setNominal(xb.getNominal());
            b.setShare(xb.getShare());

            if (xb.getBeneficiaryType().equalsIgnoreCase("LEGAL")) {
                LegalPerson lp = new LegalPerson();
                lp.setIdentificationNumber(xb.getIdentificationNumber());
                b.setLegalPerson(lp);
            } else if (xb.getBeneficiaryType().equalsIgnoreCase("PHYSICAL")) {
                Person person = new Person();
                person.setIdentificationNumber(xb.identificationNumber);
                if (regionMapByCode.get(xb.getCountryCode()) == null) {
                    throw new FinATypeException(String.format("Region with code [ %s ] does not exist, Beneficiary person: [ %s ], Fi: [ %s ]", xb.getCountryCode(), person.getIdentificationNumber(), fiCode));
                }
                person.setCitizenship(regionMapByCode.getOrDefault(xb.getCountryCode().trim(), null));
                b.setPhysicalPerson(person);
            }

            if (xb.getFinalBeneficiaries() != null && xb.getBeneficiaryType().equalsIgnoreCase("LEGAL")) {
                List<FinalBeneficiary> finalBeneficiaries = new ArrayList<>();
                for (net.fina.server.fi.xml.FinalBeneficiary fb : xb.getFinalBeneficiaries().getFinalBeneficiaries()) {
                    FinalBeneficiary finalBeneficiary = new FinalBeneficiary();
                    Person person = new Person();
                    person.setIdentificationNumber(fb.getPhysicalPerson().getPersonIdNumber());
                    if (regionMapByCode.getOrDefault(fb.getPhysicalPerson().getCountryCode().trim(), null) != null) {
                        person.setCountry(regionMapByCode.getOrDefault(fb.getPhysicalPerson().getCountryCode().trim(), null));
                    }
                    finalBeneficiary.setPerson(person);
                    finalBeneficiaries.add(finalBeneficiary);
                }
                b.setFinalBeneficiaries(finalBeneficiaries);
            }

            result.add(b);
        }
        return result;
    }

    private List<PersonPosition> xmlPositionsToPositions(List<net.fina.server.fi.xml.PersonPosition> positions, Person person) {
        List<PersonPosition> res = new ArrayList<>();
        for (net.fina.server.fi.xml.PersonPosition position : positions) {
            PersonPosition entityPosition = new PersonPosition();
            entityPosition.setPosition(xmlDescToDesc(position.getPositionDescriptions()));
            entityPosition.setPerson(person);

            LegalPerson lp = new LegalPerson();
            lp.setIdentificationNumber(position.getLpIdNumber());

            entityPosition.setCompany(lp);
            res.add(entityPosition);
        }

        return res;
    }

    private <T extends BaseLegalEntityType> T convertToLegalEntityType(CodeDescription codeDescription, Class<T> entityType) {
        try {
            T entity = entityType.getDeclaredConstructor().newInstance();
            entity.setCode(codeDescription.getCode());
            entity.setDescription(xmlDescToDesc(codeDescription.getDescriptions()));
            return entity;
        } catch (Exception e) {
            return null;
        }
    }


}