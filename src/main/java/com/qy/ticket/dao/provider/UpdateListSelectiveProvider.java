package com.qy.ticket.dao.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Iterator;
import java.util.Set;

/**
 * @author zhaozha
 * @date 2019/10/30 上午10:13
 */
public class UpdateListSelectiveProvider extends MapperTemplate {
    public UpdateListSelectiveProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public String updateListSelective(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"listNotEmptyCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck(list, '" + ms.getId() + " 方法参数为空')\"/>");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\";\" >");
        sql.append(SqlHelper.updateTable(entityClass, this.tableName(entityClass), "list[0]"));
        sql.append("<set>");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        Iterator var5 = columnList.iterator();

        while (var5.hasNext()) {
            EntityColumn column = (EntityColumn) var5.next();
            if (!column.isId() && column.isUpdatable()) {
                sql.append(SqlHelper.getIfNotNull("record", column, column.getColumnEqualsHolder("record") + ",", false));
            }
        }
        sql.append("</set>");
        sql.append("<where>");
        Set<EntityColumn> columnSet = EntityHelper.getPKColumns(entityClass);
        Iterator var6 = columnSet.iterator();
        while (var6.hasNext()) {
            EntityColumn column = (EntityColumn) var6.next();
            sql.append(" AND ").append(column.getColumnEqualsHolder("record"));
        }
        sql.append("</where>");
        sql.append("</foreach>");
        EntityHelper.setKeyProperties(EntityHelper.getPKColumns(entityClass), ms);
        return sql.toString();
    }
}

