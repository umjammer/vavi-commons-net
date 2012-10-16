/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import vavi.util.Debug;


/**
 * HttpProtocol. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 061015 nsano initial version <br>
 */
public class HttpProtocol implements Protocol {

    /** HTTP version */
    private boolean isHttp11 = false;

    /** */
    public void setHttp11(boolean isHttp11) {
        this.isHttp11 = isHttp11;
    }

    /** */
    public boolean isHttp11() {
        return isHttp11;
    }

    /* */
    public String getName() {
        return "HTTP";
    }

    /* TODO もっと厳密に "-" 含めてるよ */
    public boolean matchesRequestLine(String line) {
        return line.matches("([\\w-]+)\\s+(\\S+)\\s+(HTTP\\/\\d\\.\\d)\\s*");
    }

    /* */
    public boolean matchesResponseLine(String line) {
        return line.matches("(HTTP\\/\\d\\.\\d)\\s+(\\d+)\\s*(.*)\\s*");
    }

    /* */
    public void parseRequestLine(String line, HttpContext context) throws IOException {
        StringTokenizer st = new StringTokenizer(line, " \t");
        if (!st.hasMoreTokens()) {
            throw new IllegalArgumentException("no method");
        }
        context.setMethod(st.nextToken());
System.err.println(" method: " + context.getMethod());
        if (!st.hasMoreTokens()) {
            throw new IllegalArgumentException("no path");
        }
        String uri = st.nextToken();
        context.setRequestURI(uri);
System.err.println(" requestURI: " + context.getRequestURI());
        Util.parseRequestURI(context.getRequestURI(), context);
        if (!st.hasMoreTokens()) {
            throw new IllegalArgumentException("no protocol");
        }
        String protocol = st.nextToken();
        float version = 1.0f;
        int p = protocol.indexOf('/');
        if (p >= 0) {
            try {
                version = Float.parseFloat(protocol.substring(p + 1));
                if (version == 1.1f) {
                    this.isHttp11 = true;
                }
            } catch (NumberFormatException e) {
Debug.println("unknown version: " + protocol.substring(p + 1));
            }
            protocol = protocol.substring(0, p); // dummy
        }
        context.setProtocol(this);
System.err.println(" protocol: " + context.getProtocol().getName() + ", version: " + version + ", " + isHttp11);
    }

    /* */
    public void parseResponseLine(String line, HttpContext context) throws IOException {
        StringTokenizer st = new StringTokenizer(line, " \t");
        if (!st.hasMoreTokens()) {
            throw new IllegalArgumentException("no protocol");
        }
        st.nextToken(); // for dummy
        context.setProtocol(this);
System.err.println("-------- response: " + context.getRemoteHost() + ":" + context.getRemotePort() + " << " + context.getLocalHost() + ":" + context.getLocalPort()/* + ": " + encoding*/);
System.err.println("response line: " + line);
System.err.println(" protocol: " + context.getProtocol().getName());
        if (!st.hasMoreTokens()) {
            throw new IllegalArgumentException("no status");
        }
        context.setStatus(Integer.parseInt(st.nextToken()));
System.err.println(" status: " + context.getStatus());
        String statusMessage = "";
        while (st.hasMoreTokens()) {
            statusMessage += st.nextToken() + " ";
        }
        context.setStatusMessage(statusMessage.trim());
System.err.println(" statusMessage: " + context.getStatusMessage());
    }

    /** */
    public void printResponseLine(PrintStream ps, HttpContext context) {
        ps.print(getName());
        ps.print('/');
        ps.print(isHttp11 ? "1.1" : "1.0");
        ps.print(' ');
//Debug.println("status: " + context.getStatus() + ", " + context.hashCode());
        ps.print(context.getStatus());
        ps.print(' ');
        if (context.getStatusMessage() == null) { // TODO really need default messages?
            switch (context.getStatus()) {
            case 100:
                ps.print("Continue");
                break;
            case 200:
                ps.print("OK");
                break;
            case 206:
                ps.print("Partial Content");
                break;
            case 400:
                ps.print("Bad Request");
                break;
            case 404:
                ps.print("Not Found");
                break;
            case 416:
                ps.print("Invalid Range");
                break;
            case 500:
                ps.print("Internal Server Error");
                break;
            case 503:
                ps.print("Service Unavailable");
                break;
            default:
                break;
            }
        } else {
            ps.print(context.getStatusMessage());
        }
    }

    /* */
    public void printRequestLine(PrintStream ps, HttpContext context) {
        ps.print(context.getMethod());
        ps.print(' ');
        ps.print(context.getRequestURI());
        ps.print(' ');
        ps.print(getName());
        ps.print('/');
        ps.print(isHttp11 ? "1.1" : "1.0");
    }

    /* for server */
    public void parseRequestHeaders(HttpContext context) {
//Debug.println(hashCode() + ": isHttp11: " + isHttp11);
        if (isHttp11) {
            String host = context.getHeader("HOST");
            if (host == null) {
                throw new IllegalArgumentException("no host header");
            }
            // TODO analyze
        }
    }

    /* for client */
    public void addRequestHeaders(HttpContext context) {
        if (isHttp11) {
            context.setHeader("HOST", context.getRemoteHost() + ":" + context.getRemotePort());
        }
    }
}

/* */
