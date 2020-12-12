package com.qy.ticket.dao.mapper;

import com.qy.ticket.dao.provider.ReplaceListProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @author zhaozha
 * @date 2019/10/30 下午12:58
 */
@RegisterMapper
public interface ReplaceListMapper<T> {
    @Options(
            useGeneratedKeys = true
    )
    @InsertProvider(
            type = ReplaceListProvider.class,
            method = "dynamicSQL"
    )
    int replaceList(List<? extends T> var1);
}
