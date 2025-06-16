package com.smzdm.abtest;

import java.util.*;

/**
 * 简化版AB测试SQL演示类
 * 不依赖外部库，展示核心SQL生成逻辑
 */
public class SimpleDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 简化版AB测试SQL生成演示 ===\n");
        
        // 演示基本的SQL模板替换
        demonstrateBasicSqlGeneration();
        
        // 演示参数化查询
        demonstrateParameterizedQuery();
    }
    
    /**
     * 演示基本的SQL生成
     */
    private static void demonstrateBasicSqlGeneration() {
        System.out.println("1. 基本SQL模板生成:");
        System.out.println("================================================================================");
        
        // 基础参数
        Map<String, String> params = new HashMap<>();
        params.put("startDate", "2025-04-01 00:00:00");
        params.put("endDate", "2025-04-14 10:00:00");
        params.put("tableName", "bi_app.ads_zdm_user_abtest_background_v4_d");
        params.put("scence", "new_user");
        params.put("controlGroups", "68_c1,68_b1,68_c,68_b");
        params.put("experimentGroups", "68_f1,68_d1,68_f,68_d");
        params.put("baselineGroups", "68_z64");
        
        // 生成简化的AB测试SQL
        String sql = generateSimpleABTestSql(params);
        System.out.println(sql);
        System.out.println("\n================================================================================\n");
    }
    
    /**
     * 演示参数化查询
     */
    private static void demonstrateParameterizedQuery() {
        System.out.println("2. 参数化查询生成:");
        System.out.println("================================================================================");
        
        // 指标字段列表
        List<String> metricFields = Arrays.asList(
            "app_search_synth_valid_order",
            "app_search_haojia_article_valid_cps", 
            "whole_valid_order",
            "whole_valid_gmv"
        );
        
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", "2025-04-01");
        params.put("endDate", "2025-04-14");
        params.put("tableName", "bi_app.ads_zdm_user_abtest_background_v4_d");
        params.put("scence", "new_user");
        params.put("metricFields", metricFields);
        
        String sql = generateMetricSql(params);
        System.out.println(sql);
        System.out.println("\n================================================================================\n");
    }
    
    /**
     * 生成简化的AB测试SQL
     */
    private static String generateSimpleABTestSql(Map<String, String> params) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("-- AB测试基础数据分析\n");
        sql.append("SELECT \n");
        sql.append("    concat('").append(params.get("startDate")).append("','~','").append(params.get("endDate")).append("') as date_range,\n");
        sql.append("    abtest,\n");
        sql.append("    cast(count(distinct device_id) as String) as user_num,\n");
        sql.append("    cast(avg(total_orders) as String) as avg_orders_per_user\n");
        sql.append("FROM (\n");
        sql.append("    SELECT \n");
        sql.append("        device_id,\n");
        sql.append("        CASE \n");
        sql.append("            WHEN hasAny(splitByString(',', abtest_collection), ['").append(params.get("controlGroups")).append("']) = 1 THEN '对照组'\n");
        sql.append("            WHEN hasAny(splitByString(',', abtest_collection), ['").append(params.get("baselineGroups")).append("']) = 1 THEN '基线流'\n");
        sql.append("            ELSE '实验组'\n");
        sql.append("        END as abtest,\n");
        sql.append("        sum(valid_order_count) as total_orders\n");
        sql.append("    FROM ").append(params.get("tableName")).append("\n");
        sql.append("    WHERE dt BETWEEN toDate('").append(params.get("startDate")).append("') AND toDate('").append(params.get("endDate")).append("')\n");
        sql.append("      AND scence = '").append(params.get("scence")).append("'\n");
        sql.append("      AND (hasAny(splitByString(',', abtest_collection), ['").append(params.get("controlGroups")).append("']) = 1\n");
        sql.append("           OR hasAny(splitByString(',', abtest_collection), ['").append(params.get("experimentGroups")).append("']) = 1\n");
        sql.append("           OR hasAny(splitByString(',', abtest_collection), ['").append(params.get("baselineGroups")).append("']) = 1)\n");
        sql.append("    GROUP BY device_id, abtest\n");
        sql.append(")\n");
        sql.append("GROUP BY abtest\n");
        sql.append("\n");
        sql.append("-- 实验组与对照组对比\n");
        sql.append("SELECT \n");
        sql.append("    concat('").append(params.get("startDate")).append("','~','").append(params.get("endDate")).append("') as date_range,\n");
        sql.append("    '实验组/对照组' as abtest,\n");
        sql.append("    '--' as user_num,\n");
        sql.append("    cast(round(\n");
        sql.append("        sum(if(abtest='实验组', avg_orders, 0)) / sum(if(abtest='对照组', avg_orders, 0)) - 1,\n");
        sql.append("        4\n");
        sql.append("    ) as String) as lift_ratio\n");
        sql.append("FROM (\n");
        sql.append("    SELECT abtest, avg(total_orders) as avg_orders\n");
        sql.append("    FROM (\n");
        sql.append("        SELECT \n");
        sql.append("            device_id,\n");
        sql.append("            CASE \n");
        sql.append("                WHEN hasAny(splitByString(',', abtest_collection), ['").append(params.get("controlGroups")).append("']) = 1 THEN '对照组'\n");
        sql.append("                WHEN hasAny(splitByString(',', abtest_collection), ['").append(params.get("baselineGroups")).append("']) = 1 THEN '基线流'\n");
        sql.append("                ELSE '实验组'\n");
        sql.append("            END as abtest,\n");
        sql.append("            sum(valid_order_count) as total_orders\n");
        sql.append("        FROM ").append(params.get("tableName")).append("\n");
        sql.append("        WHERE dt BETWEEN toDate('").append(params.get("startDate")).append("') AND toDate('").append(params.get("endDate")).append("')\n");
        sql.append("          AND scence = '").append(params.get("scence")).append("'\n");
        sql.append("        GROUP BY device_id, abtest\n");
        sql.append("    )\n");
        sql.append("    GROUP BY abtest\n");
        sql.append(");");
        
        return sql.toString();
    }
    
    /**
     * 生成指标SQL
     */
    @SuppressWarnings("unchecked")
    private static String generateMetricSql(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        List<String> metricFields = (List<String>) params.get("metricFields");
        
        sql.append("-- 多指标AB测试分析\n");
        sql.append("SELECT \n");
        sql.append("    '").append(params.get("startDate")).append("~").append(params.get("endDate")).append("' as date_range,\n");
        sql.append("    abtest_group,\n");
        
        for (int i = 0; i < metricFields.size(); i++) {
            String field = metricFields.get(i);
            sql.append("    cast(avg(").append(field).append(") as String) as avg_").append(field);
            if (i < metricFields.size() - 1) {
                sql.append(",");
            }
            sql.append("\n");
        }
        
        sql.append("FROM (\n");
        sql.append("    SELECT \n");
        sql.append("        device_id,\n");
        sql.append("        'AB测试组' as abtest_group,\n");
        
        for (int i = 0; i < metricFields.size(); i++) {
            String field = metricFields.get(i);
            sql.append("        sum(").append(field).append(") as ").append(field);
            if (i < metricFields.size() - 1) {
                sql.append(",");
            }
            sql.append("\n");
        }
        
        sql.append("    FROM ").append(params.get("tableName")).append("\n");
        sql.append("    WHERE dt BETWEEN toDate('").append(params.get("startDate")).append("') AND toDate('").append(params.get("endDate")).append("')\n");
        sql.append("      AND scence = '").append(params.get("scence")).append("'\n");
        sql.append("    GROUP BY device_id\n");
        sql.append(")\n");
        sql.append("GROUP BY abtest_group;");
        
        return sql.toString();
    }
} 