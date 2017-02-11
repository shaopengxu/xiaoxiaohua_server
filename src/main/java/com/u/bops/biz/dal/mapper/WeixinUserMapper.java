package com.u.bops.biz.dal.mapper;

import com.u.bops.biz.domain.WeixinUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public interface WeixinUserMapper extends GeneralMapper<WeixinUser>  {

    public WeixinUser getWeixinUser(@Param("openId") String openId);

}

