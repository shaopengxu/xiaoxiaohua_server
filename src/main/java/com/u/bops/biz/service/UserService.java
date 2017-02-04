package com.u.bops.biz.service;

import com.google.common.collect.Maps;
import com.u.bops.biz.dal.mapper.UserRoleMapper;
import com.u.bops.biz.domain.UserRole;
import com.u.bops.biz.vo.Result;
import com.u.bops.biz.dal.mapper.UserMapper;
import com.u.bops.biz.domain.User;
import com.u.bops.common.constants.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jinsong
 */
@Service
public class UserService {

    //@Autowired
    private UserMapper userMapper;

    //@Autowired
    private UserRoleMapper userRoleMapper;

    //@Autowired
    private PasswordService passwordService;

    @Transactional
    public Result<Long> createUser(String username, String password, List<String> roles, int userType, long companyId) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return Result.error(ResultCode.MISSING_PARAM, "username&password required");
        }

        if (findUser(username) != null) {
            return Result.error(ResultCode.INVALID_PARAM, "username exist.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordService.encryptPassword(password));
        user.setUserType(userType);
        user.setCompanyId(companyId);
        userMapper.insert(user);

        if (CollectionUtils.isNotEmpty(roles)) {
            for (String role : roles) {
                UserRole userRole = new UserRole();
                userRole.setUsername(username);
                userRole.setRoleName(role);
                userRoleMapper.insert(userRole);
            }
        }
        return Result.success(user.getId());
    }

    public User findUser(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        Map<String, Object> param = Maps.newHashMapWithExpectedSize(1);
        param.put("username", username);
        User user = userMapper.getByParam(param);
        return user;
    }

    public User getUser(Long userId) {
        return userMapper.getByUserId(userId);
    }

    public void saveUser(User user) {
        userMapper.updateById(user);
    }

    public boolean isUserAdmin(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("roleName", "admin");
        UserRole userRole = userRoleMapper.getByParam(params);
        return userRole != null;
    }

    public User getAdmin() {
        Map<String, Object> params = new HashMap<>();
        params.put("roleName", "admin");
        UserRole userRole = userRoleMapper.getByParam(params);
        String userName = userRole.getUsername();
        User user = getUser(userName);
        return user;
    }

    public User getUser(String userName) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", userName);
        return userMapper.getByParam(params);
    }

    public void deleteUser(long userId) {
        userMapper.deleteById(userId);
    }

    public void updatePassword(User user, String password) {
        user.setPassword(passwordService.encryptPassword(password));
        userMapper.updatePassword(user);
    }

    public boolean updatePassword(User user, String password, String oldPassword) {

        //密码不一致，不允许修改
        if (!passwordService.passwordsMatch(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordService.encryptPassword(password));
        userMapper.updatePassword(user);
        return true;
    }
}
