/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vavi.io.UtilInputStream;
import vavi.util.Debug;


/**
 * HttpUtil. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 050914 nsano initial version <br>
 */
public final class HttpUtil {

    /** */
    private static final String defaultEncoding = "ISO-8859-1";

    //----

    /**
     * (for server)
     * use {@link InputStream#available()}
     * @param context request are copied into  
     * @see HttpContext#setMethod()
     * @see HttpContext#setRequestURI(String)
     * @see HttpContext#setProtocol(String)
     * @see HttpContext#setInputStream(InputStream)
     */
    public static void parseRequestHeader(InputStream is, HttpContext context) throws IOException {
System.err.println("-------- request: " + context.getRemoteHost() + ":" + context.getRemotePort() + " >> " + context.getLocalHost() + ":" + context.getLocalPort() + " (" + is.available() + " bytes available)");
//Debug.println(Debug.getCallerMethod(2));
//Debug.println("available-1: " + is.available());
        UtilInputStream reader = new UtilInputStream(is); 
        String line = reader.readLine();
System.err.println("request line: " + line);
        Protocol protocol = Protocol.Factory.getInstanceByRequestLine(line);
        protocol.parseRequestLine(line, context);

//Debug.println("available-2: " + is.available());
        // headers
        parseRequestHeaders(reader, context);
        protocol.parseRequestHeaders(context);

        // content
        context.setInputStream(is);
System.err.println("-------- " + is.available() + " bytes left");
    }

    /** */
    private static String encoding = "JISAutoDetect";

    /** */
    public static void setEncoding(String encoding) {
        HttpUtil.encoding = encoding;
    }

    /**
     * (for client)
     * don't use {@link InputStream#available()}!
     * @param context responses are copied into  
     * @see HttpContext#setProtocol(String)
     * @see HttpContext#setStatus(int)
     * @see HttpContext#setInputStream(InputStream)
     */
    @SuppressWarnings("resource")
    public static void parseResponseHeader(InputStream is, HttpContext context) throws IOException {
        UtilInputStream reader = new UtilInputStream(is, defaultEncoding); 
//Debug.println("available-1: " + is.available());
        String line = reader.readLine();
        if (line == null) {
            throw new EOFException("no status line");
        }
        Protocol protocol = Protocol.Factory.getInstanceByResponseLine(line);
        protocol.parseResponseLine(line, context);

        // headers
        parseResponseHeaders(reader, context);
//Debug.println("available-2: " + is.available());
System.err.println("--------");

        // content
        context.setInputStream(is);
    }

    /**
     * Parses request then set {@link HttpContext#headers}.
     * (for server)
     * @param context responses are copied into  
     */
    private static void parseRequestHeaders(UtilInputStream reader, HttpContext context) throws IOException {
        while (reader.available() > 0) {
            String line = reader.readLine();
            if (line == null || line.length() == 0) {
//Debug.println("may be end of header: " + line);
                break; 
            }
            String[] pair = parseHeader(line);
            context.setHeader(pair[0], pair[1]);
        }
    }

    /**
     * Parses response then set {@link HttpContext#headers}.
     * (for client)
     * @param context responses are copied into  
     */
    private static void parseResponseHeaders(UtilInputStream reader, HttpContext context) throws IOException {
        while (true) {
            String line = reader.readLine();
            if (line == null || line.length() == 0) {
//Debug.println("may be end of header: " + line);
                break; 
            }
            String[] pair = parseHeader(line);
            context.setHeader(pair[0], pair[1]);
        }
    }

    /**
     * Parses a header line.
     * @param line a header line
     * @return pair 0: name (set to lower case), 1: value
     */
    private static String[] parseHeader(String line) throws IOException {
        int p = line.indexOf(":");
        if (p < 0) {
            throw new IllegalArgumentException("no header separator: " + line);
        } else {
            String name = line.substring(0, p).trim().toLowerCase();
            String value = line.substring(p + 1).trim();
            // TODO special for japanese
            byte[] valueBytes = value.getBytes(defaultEncoding);
//System.err.println("header: " + name + ":\n" + StringUtil.getDump(valueBytes));
            String tmp = new String(valueBytes, "UTF-8");
            if (tmp.indexOf('?') >= 0) {
                tmp = new String(valueBytes, encoding);
            }
            value = tmp;
            //
System.err.println("header: " + name + ": " + value);
            return new String[] { name, value };
        }
    }

    /**
     * Prints HTTP response header.
     * (for server)
     * @param ps output stream
     * @param context response
     */
    public static void printResponseHeader(PrintStream ps, HttpContext context) {

        // status line
        context.getProtocol().printResponseLine(ps, context);
        ps.print("\r\n");

        // headers
        printHeaders(ps, context);

        // eoh
        ps.print("\r\n");
    }

    /** */
    private static void printHeaders(PrintStream ps, HttpContext context) {
        for (Entry<String, String> entry : context.getHeaders().entrySet()) {
            ps.print(entry.getKey());
            ps.print(": ");
            ps.print(entry.getValue() == null ? "" : entry.getValue());
            ps.print("\r\n");
        }
    }

    /**
     * "HOST" header is created by remoteHost and remortPort.
     * protocol must be set.
     */
    public static void printRequestHeader(PrintStream ps, HttpContext context) {

        // method line
        context.getProtocol().printRequestLine(ps, context);
        ps.print("\r\n");

        // special header
        context.getProtocol().addRequestHeaders(context);

        // headers
        printHeaders(ps, context);

        // eoh
        ps.print("\r\n");
    }

    /**
     * Posts only header. use {@link Socket} and closed after done.
     * and response stream is hacked.
     * (for client)
     * request {@link HttpContext#localHost}, {@link HttpContext#localPort} will be set.
     * @param request must be set {@link HttpContext#remoteHost}, {@link HttpContext#remotePort}
     * @return response {@link HttpContext}: {@link HttpContext#remoteHost},
     *         {@link HttpContext#remotePort},
     *         {@link HttpContext#localHost},
     *         {@link HttpContext#localPort},
     *         {@link HttpContext#is} have been set.
     */
    public static HttpContext postRequest(HttpContext request) throws IOException {
Debug.println(">>>> " + request.getMethod() + " " + request.getRemoteHost() + ":" + request.getRemotePort() + request.getRequestURI());
        Socket socket = null;
        try {
            socket = new Socket(request.getRemoteHost(), request.getRemotePort());

            // 1. request
            OutputStream os = socket.getOutputStream();

            request.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
            request.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());

            // request temporary
//System.err.println("-------- request: " + request.getMethod());
//printRequestHeader(System.err, request);
//System.err.println("--------");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);

            request.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
            request.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());

            printRequestHeader(ps, request);

            ps.flush();

            // request header
            os.write(baos.toByteArray());
            os.flush();

            // 2. response
            InputStream is = getFixedInputStream(socket.getInputStream()); // TODO why needs fixed...

            HttpContext response = new HttpContext(); 
            response.setRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
            response.setRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());
            response.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
            response.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());

            parseResponseHeader(is, response);

            return response;
        } finally {
            socket.close();
Debug.println(">>>> " + request.getMethod() + " done");
        }
    }

    /**
     * Posts only header. plain.
     * (for client)
     * request {@link HttpContext#localHost}, {@link HttpContext#localPort} will be set.
     * @param request must be set {@link HttpContext#remoteHost}, {@link HttpContext#remotePort}
     * @return response {@link HttpContext}: {@link HttpContext#remoteHost},
     *         {@link HttpContext#remotePort},
     *         {@link HttpContext#localHost},
     *         {@link HttpContext#localPort},
     *         {@link HttpContext#is} have been set.
     */
    public static HttpContext postRequest(HttpContext request, Socket socket) throws IOException {
        // 1. request
        OutputStream os = socket.getOutputStream();
        PrintStream ps = new PrintStream(os);

        request.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
        request.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());

        printRequestHeader(ps, request);

        ps.flush();

        // 2. response
        InputStream is = socket.getInputStream();

        HttpContext response = new HttpContext(); 
        response.setRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
        response.setRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());
        response.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
        response.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());

        parseResponseHeader(is, response);

        return response;
    }

    /**
     * for debug.
     * (for client)
     */
    private static InputStream getFixedInputStream(InputStream is) throws IOException {
Debug.println("available: " + is.available());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            int c = is.read();
            if (c == -1) {
                break;
            }
            baos.write(c);
        }
System.out.println("-------- response content: " + baos.size() + " bytes");
System.out.println(new String(baos.toByteArray()));
System.out.println("--------");
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Posts only HTTP header.
     * (for server) 
     */
    public static void postResponse(HttpContext response) throws IOException {
        OutputStream os = response.getOutputStream();
System.err.println("-------- response: " + response.getMethod() + ": " + os);
//      PrintStream ps = new PrintStream(os);
//      printResponseHeader(ps, response);
//      ps.flush();
        os.flush();
System.err.println("--------");
    }

    /**
     * Copies context to request.
     * @param request source
     * @param context destination 
     */
    public static void copy(HttpServletRequest request, HttpContext context) {
        Enumeration<?> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = request.getHeader(name);
            context.setHeader(name, value);
        }
        context.parameters = request.getParameterMap();

        try {
            context.setInputStream(request.getInputStream());
//Debug.println("input stream: " + request.getInputStream().available() + ", " + request.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        context.setMethod(request.getMethod());
        context.setProtocol(Protocol.Factory.getInstanceByName(request.getProtocol())); // TODO http11 とかは？
        context.setRequestURI(request.getRequestURI());
    }

    /**
     * HTTP error response.
     * @param os
     * @param e
     */
    public static void printErrorResponse(OutputStream os, Exception e) {
        try {
            PrintStream ps = new PrintStream(os);
            HttpContext errorContext = new HttpContext();
            errorContext.setProtocol(new HttpProtocol()); // TODO want response context
            errorContext.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            printResponseHeader(ps, errorContext);
            e.printStackTrace(ps);
Debug.printStackTrace(e);
            ps.flush();
            os.flush();
        } catch (Exception ioe) {
Debug.printStackTrace(ioe);
        }
    }

    /** @return true when statusCode is 2xx */
    public static boolean isStatusCodeSucces(int statusCode) {
        return statusCode / 100 == 2;
    }
}

/* */
