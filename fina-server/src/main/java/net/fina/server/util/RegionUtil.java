package net.fina.server.util;

import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Region;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionUtil {
    private static Logger log = Logger.getLogger(RegionUtil.class);

    public static String getRegionalAddress(List<Region> regions, Fi fi, long langId, String regionSeparator, boolean appendLastAddress) {
        StringBuilder sb = new StringBuilder();
        try {

            Map<Long, Region> regionMap = new HashMap<>();
            for (Region r : regions) {
                regionMap.put(r.getId(), r);
            }
            long regionId = fi.getRegionId() != null ? fi.getRegionId() : 0;
            Region region = regionMap.get(regionId);
            if(region==null){
                return "";
            }
            sb.append(region.getDescription().getDescription(langId))
                    .append(regionSeparator);
            int i = 0;

            while (i < regions.size()) {
                i++;
                regionId = regionMap.get(regionId).getParentId();
                if (regionId == 0) break;
                String addr = regionMap.get(regionId).getDescription().getDescription(langId);
                String tmp = sb.toString();
                sb.replace(0, sb.length(), addr);
                sb.append(regionSeparator).append(tmp);
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        String resultRegion = appendLastAddress ? sb.append(fi.getAddressDescription().getDescription(langId)).toString() : sb.toString();

        if (!appendLastAddress && resultRegion.length() > 0) {
            resultRegion = resultRegion.substring(0, resultRegion.length() - regionSeparator.length());
        }

        return resultRegion;
    }

    public String getFullRegionName(Map<Long, Region> regionIds, Long regionId, long langId, StringBuilder result) {
        if (regionId == null) {
            return null;
        }
        result = (result != null ? result : new StringBuilder());
        Region region = regionIds.get(regionId);
        if (region != null && regionId > 0) {
            result.append(regionIds.get(regionId).getDescription().getDescription(langId))
                    .append("; ");
            return getFullRegionName(regionIds, region.getParentId(), langId, result);
        }
        return result.toString();
    }

}
