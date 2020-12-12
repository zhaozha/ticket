package com.qy.ticket.util;

import com.qy.ticket.exception.BusinessException;
import com.qy.ticket.exception.EumException;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.qy.ticket.exception.EumException.TOOL_ERROR;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午5:26
 **/
public class MapperUtil {
    /**
     * 通过一组属性获取对应记录（业务保证唯一）
     *
     * @param clasz
     * @param mapper
     * @param str
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T getOneByKVs(Class<T> clasz, Mapper<T> mapper, EumException e, Object... str) throws Exception {
        List<T> list = getListByKVs(clasz, mapper, str);
        if (CollectionUtils.isEmpty(list) || list.size() > 1) {
            if (e == null) {
                return null;
            } else {
                throw new BusinessException(e);
            }
        }
        return list.get(0);
    }


    /**
     * 通过一组属性获取对应集合
     *
     * @param clasz
     * @param mapper
     * @param str
     * @param <T>
     * @return
     */
    public static <T> List<T> getListByKVs(Class<T> clasz, Mapper<T> mapper, Object... str) throws Exception {
        List<Object> list = Arrays.asList(str);
        if (CollectionUtils.isEmpty(list) || list.size() % 2 != 0) {
            throw new BusinessException(TOOL_ERROR);
        }
        Example example = new Example(clasz, true, true);
        Example.Criteria criteria = example.createCriteria();
        for (int i = 0; i < list.size(); i += 2) {
            criteria.andEqualTo(list.get(i).toString(), list.get(i + 1));
        }
        List<T> ts = mapper.selectByExample(example);
        return CollectionUtils.isEmpty(ts) ? new ArrayList<>() : ts;
    }
}
