package me.leozdgao.beaver.agent.utils;

import com.google.common.collect.Lists;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

public class IpUtils {
    private static boolean isNotLocalAddress(NetworkInterface nif, InetAddress adr) throws SocketException {
        return (adr != null) && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress());
    }
    public static Collection<InetAddress> getAllLocalIPs() throws SocketException
    {
        List<InetAddress> listAdr = Lists.newArrayList();
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();

        while (nifs.hasMoreElements())
        {
            NetworkInterface nif = nifs.nextElement();
            Enumeration<InetAddress> adrs = nif.getInetAddresses();
            while (adrs.hasMoreElements())
            {
                InetAddress adr = adrs.nextElement();
                if (isNotLocalAddress(nif, adr) && adr instanceof Inet4Address)
                {
                    listAdr.add(adr);
                }
            }
        }
        return listAdr;
    }
}
