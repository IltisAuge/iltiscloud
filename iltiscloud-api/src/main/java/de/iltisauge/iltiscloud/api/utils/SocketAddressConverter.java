package de.iltisauge.iltiscloud.api.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SocketAddressConverter {

	public static InetSocketAddress convertToInet(String hostAndPort) {
		final int index = hostAndPort.indexOf(":");
		final String host = hostAndPort.substring(0, index);
		final int port = Integer.parseInt(hostAndPort.substring(index + 1));
		return new InetSocketAddress(host, port);
	}

	public static InetSocketAddress convertToInet(SocketAddress socketAddress) {
		return SocketAddressConverter.convertToInet(socketAddress.toString().substring(1));
	}

	public static String convertInetToString(InetSocketAddress inetSocketAddress) {
		return inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
	}
}
