package com.qy.ticket.dao.mapper;

import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * @author zhaozha
 * @date 2019/10/30 下午1:03
 */
@RegisterMapper
public interface QueryMapper<T> extends InsertListMapper<T>, UpdateListSelectiveMapper<T>, ReplaceListMapper<T>, DeleteByIdListMapper {
}
