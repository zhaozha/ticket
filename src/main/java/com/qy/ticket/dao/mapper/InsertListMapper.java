package com.qy.ticket.dao.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.additional.insert.InsertListProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @author zhaozha
 * @date 2019/10/30 下午12:58
 */
@RegisterMapper
public interface InsertListMapper<T> {
    @Options(
            useGeneratedKeys = true
    )
    @InsertProvider(
            type = InsertListProvider.class,
            method = "dynamicSQL"
    )
    int insertList(List<? extends T> var1);
}
