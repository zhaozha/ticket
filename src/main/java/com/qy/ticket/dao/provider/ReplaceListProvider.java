package com.qy.ticket.dao.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Iterator;
import java.util.Set;

import static tk.mybatis.mapper.mapperhelper.SqlHelper.getDynamicTableName;

/**
 * @author zhaozha
 * @date 2019/10/30 下午1:00
 */
public class ReplaceListProvider extends MapperTemplate {
    public ReplaceListProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String replaceList(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"listNotEmptyCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck(list, '" + ms.getId() + " 方法参数为空')\"/>");
        sql.append(replaceIntoTable(entityClass, this.tableName(entityClass), "list[0]"));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        Iterator var5 = columnList.iterator();

        while (var5.hasNext()) {
            EntityColumn column = (EntityColumn) var5.next();
            if (column.isInsertable()) {
                sql.append(column.getColumnHolder("record") + ",");
            }
        }

        sql.append("</trim>");
        sql.append("</foreach>");
        EntityHelper.setKeyProperties(EntityHelper.getPKColumns(entityClass), ms);
        return sql.toString();
    }

    /**
     * replace into tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @param parameterName 动态表名的参数名
     * @return
     */
    public static String replaceIntoTable(Class<?> entityClass, String defaultTableName, String parameterName) {
        StringBuilder sql = new StringBuilder();
        sql.append("REPLACE INTO ");
        sql.append(getDynamicTableName(entityClass, defaultTableName, parameterName));
        sql.append(" ");
        return sql.toString();
    }
}
