package com.u.bops.util;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

/**
 * User: jinsong
 */
@DefaultKey("userTool")
@ValidScope(Scope.APPLICATION)
public class UserTools {

    public boolean isAuthenticated() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isAuthenticated() == true;
    }

    /**
     * 包含记住的用户.
     *
     * @return
     */
    public boolean isUser() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.getPrincipal() != null;
    }

    public Object getPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null ? subject.getPrincipal() : null;
    }

    public boolean hasRole(String role) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.hasRole(role) == true;
    }

    public boolean hasAnyRoles(String roleNames) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            for (String role : StringUtils.split(roleNames, ",")) {
                if (subject.hasRole(role.trim()) == true) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasPermission(String permission) {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isPermitted(permission);
    }



}
