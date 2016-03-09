/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * HttpContext. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050731 nsano initial version <br>
 */
public class HttpContext {
    /** <name, value> name must be lower case! */
    protected Map<String, String> headers = new HashMap<>();
    
    /** <name, value(s)> name is case insensitive */
    protected Map<String, String[]> parameters = new HashMap<>();

    /** HTTP status code */
    protected int status;

    /** HTTP status message */
    protected String statusMessage;

    /**
     * request: used in a host header
     * response: set port of {@link java.net.Socket#getRemoteSocketAddress()}
     */
    protected String remoteHost;

    /**
     * request: used in a host header,
     * response: set address of {@link java.net.Socket#getRemoteSocketAddress()}
     */
    protected int remotePort;

    /** used at method line */
    protected String requestURI;

    /**
     * request: set address of {@link java.net.Socket#getLocalSocketAddress()}
     */
    protected String localHost;

    /**
     * request: set port of {@link java.net.Socket#getLocalSocketAddress()}
     */
    protected int localPort;

    /** method */
    protected String method;

    /** protocol */
    protected Protocol protocol;

    /**
     * @param name
     * @return
     */
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    /**
     * @param name
     * @param value
     */
    public void setHeader(String name, String value) {
        headers.put(name.toLowerCase(), value);
    }

    /**
     * @param name
     * @param value
     */
    protected void setIntHeader(String name, int value) {
        headers.put(name.toLowerCase(), String.valueOf(value));
    }

    /**
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return Returns the statusMessage.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage The statusMessage to set.
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @return Returns the localHost.
     */
    public String getLocalHost() {
        return localHost;
    }

    /**
     * @param localHost The localHost to set.
     */
    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    /**
     * @return Returns the localPort.
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * @param localPort The localPort to set.
     */
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    /**
     * @return Returns the remoteHost.
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * @param remoteHost The remoteHost to set.
     */
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    /**
     * @return Returns the remotePort.
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * @param remotePort The remotePort to set.
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * @return Returns the requestURI.
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * @param requestURI The requestURI to set.
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return Returns the headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * @return Returns the parameters.
     */
    public Map<String, String[]> getParameters() {
        return parameters;
    }

    /**
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @param protocol
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    /** */
    public Protocol getProtocol() {
        return  protocol;
    }

    /** Set {@link java.net.Socket#getInputStream()} after header parsing */
    protected InputStream is;

    /** */
    public InputStream getInputStream() {
        return is;
    }

    /** */
    public void setInputStream(InputStream is) {
        this.is = is;
    }

    /** Set {@link java.net.Socket#getOutputStream()} */
    protected OutputStream os;

    /** */
    public OutputStream getOutputStream() {
        return os;
    }

    /** */
    public void setOutputStream(OutputStream os) {
        this.os = os;
    }
}

/* */
