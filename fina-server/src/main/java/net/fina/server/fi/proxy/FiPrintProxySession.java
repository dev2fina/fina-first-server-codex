package net.fina.server.fi.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.filter.FiFilter;
import net.fina.common.client.fis.FiGroupModel;
import net.fina.common.client.fis.FiModel;
import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.fis.RegionModel;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.fi.CriterionMetaModel;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.PeerGroupLocal;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.fi.entity.Region;
import net.fina.server.fi.model.CriterionModelHelper;
import net.fina.server.fi.util.RegionLevelPrintHelper;
import net.fina.server.license.api.LicenseLocal;
import net.fina.server.license.entity.Licence;
import net.fina.server.license.entity.LicenceType;
import net.fina.server.license.model.LicenseTypeMetaModel;
import net.fina.server.license.model.LicenseTypeMetaModelHelper;
import net.fina.server.misc.ObjectUtil;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.util.AbstractFilePrintUtil;
import org.jboss.ejb3.annotation.SecurityDomain;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.util.*;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_BANK_REVIEW)
public class FiPrintProxySession {
    private final int STATIC_NUMBER_OF_COLUMNS = 8;

    @Inject
    private FiLocal fiLocal;
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private PeerGroupLocal peerGroupLocal;
    @Inject
    private UserLocal userLocal;
    @Inject
    private RegionLocal regionLocal;
    @Inject
    private LicenseLocal licenseLocal;

    public byte[] printFIs() throws IOException {
        LanguageSampleModel language = ThreadLocalHolder.getLanguage();
        FiTableModel fiTableModel = getFiTableModel(language);

        return AbstractFilePrintUtil.generateXLSX(fiTableModel, MessagesUtil.getString("net.fina.web.fiPrint.sheetName", language.getCode()));
    }


    private FiTableModel getFiTableModel(LanguageSampleModel language) {
        String faxHeader = propertyLocal.getSystemProperty(PropertyKeys.FI_FAX_LABEL);
        faxHeader = (faxHeader != null ? faxHeader : MessagesUtil.getString("net.fina.fax", language.getCode()));

        // Criterions ========================================================================
        List<Criterion> criterions = peerGroupLocal.loadNodes();
        // key: column index; value: CriterionMetaModel
        Map<Integer, CriterionMetaModel> criterionMetaModelMap = new HashMap<>();
        // column index from where criterion columns are stored in the template
        int criterionColumnIndex = 5;
        for (Criterion criterion : criterions) {
            criterionMetaModelMap.put(criterionColumnIndex, CriterionModelHelper.toModel(criterion));
            criterionColumnIndex++;
        }

        // FIs ================================================================================
        Map<FiFilter, Object> filterStringMap = new HashMap<>();
        filterStringMap.put(FiFilter.USER_ID, userLocal.getCurrentUserId());
        filterStringMap.put(FiFilter.FI_IDS, userLocal.getCallerPrincipal().getFis());
        List<Fi> fis = fiLocal.load(filterStringMap);
        List<FiModel> fiModels = new ArrayList<>();
        for (Fi fi : fis) {
            fiModels.add(fiToModel(fi, language.getId()));
        }

        // Regions ============================================================================
        List<Region> regions = regionLocal.loadRegions();
        Map<Long, RegionModel> regionModelMap = new HashMap<>();
        for (Region region : regions) {
            RegionModel model = new RegionModel();
            model.setId(region.getId());
            model.setParentId(region.getParentId());
            model.setName(region.getDescription().getDescription(language.getId()));
            model.setCode(region.getCode());

            regionModelMap.put(model.getId(), model);
        }

        // region and level
        Map<Integer, String> regionLevelStore = regionLocal.getProperties(language.getId());
        int maxRegionLevel = Integer.parseInt(regionLevelStore.get(0));
        regionLevelStore.remove(0);

        // License Types =======================================================================
        List<LicenceType> licenceTypes = licenseLocal.loadLicenseTypes();
        Map<Integer, LicenseTypeMetaModel> licenseTypeModelMap = new HashMap<>();
        // first column index from where license types are stored in the template (e.g. keys: 2,4,6,8 ....)
        int licenseTypeColumnIndex = STATIC_NUMBER_OF_COLUMNS + criterionMetaModelMap.size() + (2 * maxRegionLevel);
        for (LicenceType licenceType : licenceTypes) {
            licenseTypeModelMap.put(licenseTypeColumnIndex, LicenseTypeMetaModelHelper.toModel(licenceType, language.getId()));
            licenseTypeColumnIndex += 2;
        }

        // 3 is first 3 static columns, after them starts region columns
        int regionStartIndex = 5 + criterionMetaModelMap.size();
        RegionLevelPrintHelper regionLevelPrintHelper = new RegionLevelPrintHelper(maxRegionLevel, regionLevelStore, regionModelMap, regionStartIndex, language.getCode());

        return new FiTableModel(fiModels, criterionMetaModelMap, licenseTypeModelMap, faxHeader, regionLevelPrintHelper, language.getId(), language.getCode());
    }

    private FiModel fiToModel(Fi fi, long langId) {
        FiModel model = new FiModel();
        ObjectUtil.copyProperties(fi, model);

        model.setLevel(1);

        model.setName(fi.getDescription().getDescription(langId));
        model.setNameStrId(fi.getDescription().getNameStrId());

        model.setShortNameString(fi.getShortName().getDescription(langId));
        model.setShortNameStrId(fi.getShortName().getNameStrId());

        model.setAddressString(fi.getAddressDescription().getDescription(langId));
        model.setAddressStrId(fi.getAddressDescription().getNameStrId());
        model.setDisable(fi.isDisable());
        model.setDisable(fi.isDisable());

        // Type
        FiTypeModel fiTypeModel = new FiTypeModel();
        fiTypeModel.setId(fi.getFiType().getId());
        fiTypeModel.setName(fi.getFiType().getDescription().getDescription(langId));
        fiTypeModel.setNameStrId(fi.getFiType().getDescription().getNameStrId());
        fiTypeModel.setCode(fi.getFiType().getCode());
        fiTypeModel.setVersion(fi.getFiType().getVersion());

        model.setFiTypeModel(fiTypeModel);

        // Fi Groups
        List<FiGroupModel> fiGroupModels = new ArrayList<>();
        FiGroupModel fiGroupModel;
        Collection<PeerGroup> peerGroups = fi.getPeerGroup();
        for (PeerGroup peerGroup : peerGroups) {
            fiGroupModel = new FiGroupModel();
            ObjectUtil.copyProperties(peerGroup, fiGroupModel);
            fiGroupModel.setName(peerGroup.getDescription().getDescription(langId));
            fiGroupModel.setNameStrId(peerGroup.getDescription().getNameStrId());
            fiGroupModel.setType(FiGroupModel.Type.CHILD);
            fiGroupModels.add(fiGroupModel);
        }

        model.setFiGroupModels(fiGroupModels);

        return model;
    }

    private class FiTableModel extends AbstractTableModel {
        private final String faxHeader;
        private final RegionLevelPrintHelper regionLevelPrintHelper;
        private int dynamicColumnIndex = 5;
        private List<FiModel> fiModels;
        private Map<Integer, CriterionMetaModel> criterionMetaModelMap;
        private Map<Integer, LicenseTypeMetaModel> licenseTypeModelMap;
        private long langId;
        private String langCode;

        private FiTableModel(List<FiModel> fiModels, Map<Integer, CriterionMetaModel> criterionMetaModelMap,
                             Map<Integer, LicenseTypeMetaModel> licenseTypeModelMap, String faxHeader,
                             RegionLevelPrintHelper regionLevelPrintHelper, long langId, String langCode) {
            this.fiModels = fiModels;
            this.criterionMetaModelMap = criterionMetaModelMap;
            dynamicColumnIndex = regionLevelPrintHelper.getLastColumnIndex();
            this.licenseTypeModelMap = licenseTypeModelMap;
            this.faxHeader = faxHeader;
            this.regionLevelPrintHelper = regionLevelPrintHelper;
            this.langId = langId;
            this.langCode = langCode;
        }

        @Override
        public int getRowCount() {
            return fiModels.size();
        }

        @Override
        public int getColumnCount() {
            // licenseTypeModelMap() * 2 - *2 because each license type have 2 columns one for license code and second for creation date
            return STATIC_NUMBER_OF_COLUMNS + criterionMetaModelMap.size() + (2 * licenseTypeModelMap.size()) + regionLevelPrintHelper.getRegionColumnCount();
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return MessagesUtil.getString("net.fina.code", langCode);
            }

            if (column == 1) {
                return MessagesUtil.getString("net.fina.shortName", langCode);
            }

            if (column == 2) {
                return MessagesUtil.getString("net.fina.name", langCode);
            }

            if (column == 3) {
                return MessagesUtil.getString("net.fina.node.disable.status", langCode);
            }

            if (column == 4) {
                return MessagesUtil.getString("net.fina.fiType", langCode);
            }

            if (criterionMetaModelMap.get(column) != null) {
                List<DescriptionMetaModel> descriptions = criterionMetaModelMap.get(column).getDescriptions();
                for (DescriptionMetaModel desc : descriptions) {
                    if (desc.getLangId() == langId) {
                        return desc.getDescription();
                    }
                }
                return criterionMetaModelMap.get(column).getCode();
            }

            if (regionLevelPrintHelper.getHeaderAt(column) != null) {
                return regionLevelPrintHelper.getHeaderAt(column);
            }

            if (column - 1 == dynamicColumnIndex) {
                return MessagesUtil.getString("net.fina.email", langCode);
            }

            if (column - 2 == dynamicColumnIndex) {
                return MessagesUtil.getString("net.fina.phone", langCode);
            }

            if (column - 3 == dynamicColumnIndex) {
                return MessagesUtil.getString("net.fina.address", langCode);
            }

            if (column - 4 == dynamicColumnIndex) {
                return faxHeader;
            }

            if (licenseTypeModelMap.get(column) != null) {
                return licenseTypeModelMap.get(column).getCode() + " " + MessagesUtil.getString("net.fina.code", langCode);
            } else if (licenseTypeModelMap.get(column - 1) != null) {
                return licenseTypeModelMap.get(column - 1).getCode() + " " + MessagesUtil.getString("net.fina.web.fiPrint.date", langCode);
            }

            return "";
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return fiModels.get(rowIndex).getCode();
            } else if (columnIndex == 1) {
                return fiModels.get(rowIndex).getShortNameString();
            } else if (columnIndex == 2) {
                return fiModels.get(rowIndex).getName();
            } else if (columnIndex == 3) {
                return fiModels.get(rowIndex).isDisable();
            } else if (columnIndex == 4) {
                return fiModels.get(rowIndex).getFiTypeModel().getCode();
            }

            if (criterionMetaModelMap.get(columnIndex) != null) {
                CriterionMetaModel metaModel = criterionMetaModelMap.get(columnIndex);
                List<FiGroupModel> fiGroupModels = fiModels.get(rowIndex).getFiGroupModels();

                for (FiGroupModel model : fiGroupModels) {
                    if (model.getParentId() == metaModel.getId()) {
                        return model.getCode();
                    }
                }
                return "";
            }

            if (regionLevelPrintHelper.getHeaderAt(columnIndex) != null) {
                Long regionId = fiModels.get(rowIndex).getRegionId();
                return regionId != null ? regionLevelPrintHelper.getValueAt(columnIndex, regionId) : "n/a";
            }

            if (columnIndex - 1 == dynamicColumnIndex) {
                return fiModels.get(rowIndex).getEmail();
            }

            if (columnIndex - 2 == dynamicColumnIndex) {
                return fiModels.get(rowIndex).getPhone();
            }

            if (columnIndex - 3 == dynamicColumnIndex) {
                return fiModels.get(rowIndex).getAddressString();
            }

            if (columnIndex - 4 == dynamicColumnIndex) {
                return fiModels.get(rowIndex).getFax();
            }

            if (licenseTypeModelMap.get(columnIndex) != null) {
                LicenseTypeMetaModel licenseTypeModel = licenseTypeModelMap.get(columnIndex);
                for (Licence licence : fiLocal.loadFiLicences(fiModels.get(rowIndex).getId())) {
                    if (licence.getLicenseType().getId() == licenseTypeModel.getId()) {
                        return licence.getCode();
                    }
                }
                return "";
            } else if (licenseTypeModelMap.get(columnIndex - 1) != null) {
                LicenseTypeMetaModel licenseTypeModel = licenseTypeModelMap.get(columnIndex - 1);
                for (Licence licence : fiLocal.loadFiLicences(fiModels.get(rowIndex).getId())) {
                    if (licence.getLicenseType().getId() == licenseTypeModel.getId()) {
                        return licence.getCreationDate();
                    }
                }
                return "";
            }

            return "";
        }
    }
}
