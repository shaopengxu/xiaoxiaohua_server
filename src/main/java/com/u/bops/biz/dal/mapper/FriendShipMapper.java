package com.u.bops.biz.dal.mapper;

import com.u.bops.biz.domain.FriendShip;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Shaopeng.Xu on 2017-02-07.
 */
public interface FriendShipMapper extends GeneralMapper<FriendShip> {

    FriendShip getFriendShip(@Param("openId") String openId, @Param("friendOpenId") String friendOpenId);

    void setDelete(@Param("openId") String openId, @Param("friendOpenId") String friendOpenId);

    void deleteFriendShip(@Param("openId") String openId, @Param("friendOpenId") String friendOpenId);
}
