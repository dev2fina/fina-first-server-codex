package net.fina.server.fi.util;

import net.fina.common.client.fis.RegionModel;
import net.fina.messages.MessagesUtil;

import java.util.*;

public class RegionLevelPrintHelper {
    private final int maxLevel;
    // key: column index; value: level name
    private final Map<Integer, String> columnAndNameStore;
    // key: column index; value: level index;
    private final Map<Integer, Integer> columnAndLevelIndexStore;
    private final Map<Long, RegionModel> regionModelStore;
    // key: level index; value: level name;
    private final Map<Integer, String> regionLevelStore;
    private int regionStartColumnIndex;
    private int regionLevelCounter = 2;

    private String langCode;

    public RegionLevelPrintHelper(int maxLevel, Map<Integer, String> regionLevelStore, Map<Long, RegionModel> regionModelStore, int regionStartColumnIndex, String langCode) {
        this.maxLevel = maxLevel;
        this.regionLevelStore = regionLevelStore;
        this.regionModelStore = regionModelStore;
        columnAndNameStore = new HashMap<>();
        columnAndLevelIndexStore = new HashMap<>();
        this.regionStartColumnIndex = regionStartColumnIndex;
        this.langCode = langCode;
        initRegionLevels();
    }

    /**
     * initializing stores
     */
    private void initRegionLevels() {
        List<Integer> levels = new ArrayList<>(regionLevelStore.keySet());
        Collections.sort(levels);

        int levelIndex = 1;
        for (int i = levels.size() - 1; i >= 0; i--) {
            int level = levels.get(i);

            // region name column for level
            columnAndNameStore.put(regionStartColumnIndex, (regionLevelStore.get(level) == null ? "NONAME" : regionLevelStore.get(level)) + " " + MessagesUtil.getString("net.fina.name", langCode));
            columnAndLevelIndexStore.put(regionStartColumnIndex, levelIndex);
            regionStartColumnIndex++;

            // region code column for level
            columnAndNameStore.put(regionStartColumnIndex, (regionLevelStore.get(level) == null ? "NONAME" : regionLevelStore.get(level)) + " " + MessagesUtil.getString("net.fina.code", langCode));
            columnAndLevelIndexStore.put(regionStartColumnIndex, levelIndex);
            regionStartColumnIndex++;

            levelIndex++;
        }
        // remove last
        regionStartColumnIndex--;
    }

    /**
     * Return header (level name) at passed column
     *
     * @param column column index, on which level name is needed
     * @return header (level name) at passed column
     */
    public String getHeaderAt(int column) {
        return columnAndNameStore.get(column);
    }

    /**
     * Returns RegionModel for passed level and region
     *
     * @param regionId id of the region which should be checked
     * @param level    level of the region which should be checked
     * @param count    level counter
     * @return RegionModel for passed level and region
     */
    public RegionModel getRegionModelAtLevel(long regionId, int level, Integer count) {
        count = (count == null) ? 1 : count;
        RegionModel model = regionModelStore.get(regionId);
        if (count == level) {
            return model;
        } else if (model.getParentId() != 0) {
            count++;
            return getRegionModelAtLevel(model.getParentId(), level, count);
        }
        return null;
    }

    /**
     * Return depth of the FI region. How many parents + itself region has
     *
     * @param regionId id of the region which should be checked
     * @param depth    level depth
     * @return depth of the FI region. How many parents + itself region has
     */
    public int getFiRegionLevelDepth(long regionId, Integer depth) {
        depth = (depth == null || depth < 0) ? 0 : depth;
        RegionModel model = regionModelStore.get(regionId);
        depth++;
        if (model == null || model.getParentId() == 0) {
            return depth;
        } else {
            return getFiRegionLevelDepth(model.getParentId(), depth);
        }
    }

    /**
     * Return Region code or name if exists, empty String if does not
     *
     * @param columnIndex index of the column on which value is needed
     * @param fiRegionId  region id of the FI used in this column
     * @return region name or code if exists, empty String if not exists
     */
    public String getValueAt(int columnIndex, long fiRegionId) {
        int level = columnAndLevelIndexStore.get(columnIndex);

        int fiRegionDepth = getFiRegionLevelDepth(fiRegionId, 0);
        int diff = maxLevel - fiRegionDepth;
        if (diff != 0 && level <= diff) {
            regionLevelCounter++;
            return "";
        } else if (diff != 0) {
            level = columnAndLevelIndexStore.get(columnIndex - 2);
        }

        RegionModel model = getRegionModelAtLevel(fiRegionId, level, 1);
        if (model != null) {
            String result = regionLevelCounter % 2 == 0 ? model.getName() : model.getCode();
            regionLevelCounter++;
            return result;
        }

        regionLevelCounter++;
        return "";
    }

    /**
     * returns last region column index, after increasing regionStartColumnIndex
     *
     * @return last region record column index
     */
    public int getLastColumnIndex() {
        return regionStartColumnIndex;
    }

    /**
     * returns total count of the region columns
     *
     * @return number of region columns
     */
    public int getRegionColumnCount() {
        return columnAndLevelIndexStore.size();
    }
}
