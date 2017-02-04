package com.u.bops.biz.dal.mapper;

import com.u.bops.biz.domain.User;

public interface UserMapper extends GeneralMapper<User> {

    User getByUserId(long id);

    User getByUserName(String userName);

    void updatePassword(User user);

}
