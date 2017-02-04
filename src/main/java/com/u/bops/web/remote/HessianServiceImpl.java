package com.u.bops.web.remote;

import com.u.bops.biz.domain.*;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shpng on 2015/2/3.
 */
public class HessianServiceImpl implements HessianService {

    private static Logger logger = LoggerFactory.getLogger(HessianServiceImpl.class);



    public String sayHello(String username) {
        System.out.println(SecurityUtils.getSubject().getSession().getAttribute("IP"));
        logger.info("cam here hessian");
        return "Hello " + username;
    }


    public static void main(String[] args) throws UnknownHostException {
        System.out.println(Arrays.toString(getAllLocalHostIP()));
        System.out.println(InetAddress.getLocalHost().getHostAddress());
        System.out.println(InetAddress.getLocalHost().getAddress());
        System.out.println(InetAddress.getLocalHost().getCanonicalHostName());
        System.out.println(InetAddress.getLoopbackAddress().getHostAddress());
        System.out.println(InetAddress.getLoopbackAddress().getAddress());
        System.out.println(InetAddress.getLoopbackAddress().getCanonicalHostName());


    }

    public static String getLocalHostName() {
        String hostName;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (Exception ex) {
            hostName = "";
        }
        return hostName;
    }

    public static String[] getAllLocalHostIP() {
        String[] ret = null;
        try {
            String hostName = getLocalHostName();

            if (hostName.length() > 0) {
                InetAddress[] addrs = InetAddress.getAllByName(hostName);
                if (addrs.length > 0) {
                    ret = new String[addrs.length];
                    for (int i = 0; i < addrs.length; i++) {
                        ret[i] = addrs[i].getHostAddress();
                    }
                }
            }

        } catch (Exception ex) {
            ret = null;
        }
        return ret;
    }

}