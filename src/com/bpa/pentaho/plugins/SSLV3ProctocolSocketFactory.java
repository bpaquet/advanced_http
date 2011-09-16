package com.bpa.pentaho.plugins;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

public class SSLV3ProctocolSocketFactory implements ProtocolSocketFactory {
	
	private SSLProtocolSocketFactory sslProtocolSocketFactory;
	
	public SSLV3ProctocolSocketFactory() {
		sslProtocolSocketFactory = new SSLProtocolSocketFactory();
	}

	private static Socket enableSSLV3(Socket socket) {
		SSLSocket sslSocket = (SSLSocket) socket;
		sslSocket.setEnabledProtocols(new String[] {"SSLv3"});
		return sslSocket;
	}
	
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return enableSSLV3(sslProtocolSocketFactory.createSocket(socket, host, port,
				autoClose));
	}

	public Socket createSocket(String host, int port, InetAddress localAddress,
			int localPort, HttpConnectionParams params) throws IOException,
			UnknownHostException, ConnectTimeoutException {
		return enableSSLV3(sslProtocolSocketFactory.createSocket(host, port, localAddress,
				localPort, params));
	}

	public Socket createSocket(String host, int port, InetAddress clientHost,
			int clientPort) throws IOException, UnknownHostException {
		return enableSSLV3(sslProtocolSocketFactory.createSocket(host, port, clientHost,
				clientPort));
	}

	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException {
		return enableSSLV3(sslProtocolSocketFactory.createSocket(host, port));
	}

	public boolean equals(Object obj) {
		return sslProtocolSocketFactory.equals(obj);
	}

	public int hashCode() {
		return sslProtocolSocketFactory.hashCode();
	}

	public String toString() {
		return sslProtocolSocketFactory.toString();
	}
	
	

}
