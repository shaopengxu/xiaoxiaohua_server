package com.u.bops.biz.dal.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GeneralMapper<T> {
    public int countByParam(Map<String, ? extends Object> param);

    public List<T> queryByParam(Map<String, ? extends Object> param);

    public T getByParam(Map<String, Object> param);

    public int updateById(T data);

    public int deleteById(long id);

    public long insert(T data);

}
