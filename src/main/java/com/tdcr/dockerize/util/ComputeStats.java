package com.tdcr.dockerize.util;

import com.github.dockerjava.api.model.Statistics;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class ComputeStats {

    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private static String PERCENT_SYMBOL = "%";
    private static String EMPTY_STRING = "";

    public static String computeMemoryInPercent(Statistics stats) {
        long usage = ((Number)stats.getMemoryStats().get("max_usage")).longValue();
        long limit = ((Number) stats.getMemoryStats().get("limit")).longValue();
        String result = df2.format(((usage*1.0)/limit)*100);
        return result + PERCENT_SYMBOL;
    }

    public static String getMemory(Statistics stats, String param) {
        long size = ((Number) stats.getMemoryStats().get(param)).longValue();
        String hrSize = calculateSize(size);
        return hrSize;
    }

    private static String calculateSize(long size) {
        String hrSize = EMPTY_STRING;
        double k = size /1024;
        double m = (k)/1024;
        double g = (m)/1024;
        double t = (g)/1024;
        if (t > 1) {
            hrSize = df2.format(t).concat("TB");
        } else if (g > 1) {
            hrSize = df2.format(g).concat("GB");
        } else if (m > 1) {
            hrSize = df2.format(m).concat("MB");
        } else if (k >1 ){
            hrSize = df2.format(k).concat("KB");
        }else {
            hrSize = df2.format(size).concat("B");
        }
        return hrSize;
    }

    public static String calcCPU(Statistics stats) {
        return "";
    }

    public static String calcNetworkStats(Statistics stats) {
        int rxSize = ((Number)((Map)stats.getNetworks().get("eth0")).get("rx_bytes")).intValue();
        int txSize = ((Number)((Map)stats.getNetworks().get("eth0")).get("tx_bytes")).intValue();
        return calculateSize(rxSize)+"/"+calculateSize(txSize);
    }

    public static Map<String,String> computeStats(Statistics stats) {
        Map<String,String> statMap = new HashMap<>();
        String memPercent = ComputeStats.computeMemoryInPercent(stats);
        String memoryUsage = ComputeStats.getMemory(stats, "usage");
        String memoryLimit = ComputeStats.getMemory(stats, "limit");
        String cpuPercent = ComputeStats.calcCPU(stats);
        String netIO = ComputeStats.calcNetworkStats(stats);
        statMap.put("MEM %",memPercent);
        statMap.put("MEM_USAGE / LIMIT",memoryUsage +" / "+memoryLimit);
        statMap.put("CPU %","");
        statMap.put("NET I/O",calcNetworkStats(stats));
        return statMap;
    }
}
