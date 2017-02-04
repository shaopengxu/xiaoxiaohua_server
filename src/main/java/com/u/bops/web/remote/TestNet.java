package com.u.bops.web.remote;
import java.net.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by shpng on 2015/5/18.
 */
public class TestNet {


    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface
                .getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            if (null != netint.getHardwareAddress()) {
                List<InterfaceAddress> list = netint.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : list) {
                    String localip = interfaceAddress.getAddress().getHostAddress();
                    System.out.println(localip);
                }
            }
        }

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
