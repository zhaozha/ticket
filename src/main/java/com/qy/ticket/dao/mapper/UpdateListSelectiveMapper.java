package com.qy.ticket.dao.mapper;

import com.qy.ticket.dao.provider.UpdateListSelectiveProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @author zhaozha
 * @date 2019/10/30 上午10:13
 */
@RegisterMapper
public interface UpdateListSelectiveMapper<T> {
    @Options(
            useGeneratedKeys = true
    )
    @UpdateProvider(
            type = UpdateListSelectiveProvider.class,
            method = "dynamicSQL"
    )
    int updateListSelective(List<? extends T> var1);
}
