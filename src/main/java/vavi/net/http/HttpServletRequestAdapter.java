/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * HttpServletRequestAdapter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 050221 nsano initial version <br>
 */
public class HttpServletRequestAdapter implements HttpServletRequest {

    /** */
    private HttpContext context;

    /** */
    public HttpServletRequestAdapter(HttpContext context) {
        this.context = context;
    }

    /** TODO */
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    /** TODO */
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /** */
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    /** TODO */
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    /** TODO */
    public String getAuthType() {
        return null;
    }

    /** TODO */
    public String getContextPath() {
        return null;
    }

    public String getMethod() {
        return context.getMethod();
    }

    /** TODO */
    public String getPathInfo() {
        return null;
    }

    /** TODO */
    public String getPathTranslated() {
        return null;
    }

    /** TODO */
    public String getQueryString() {
        return null;
    }

    /** TODO */
    public String getRemoteUser() {
        return null;
    }

    /** */
    public String getRequestURI() {
        return context.getRequestURI();
    }

    /** TODO */
    public String getRequestedSessionId() {
        return null;
    }

    /** TODO */
    public String getServletPath() {
        return null;
    }

    /** */
    public int getIntHeader(String name) {
        return Integer.parseInt(context.getHeader(name));
    }

    /** */
    public long getDateHeader(String name) {
        return Protocol.Util.toDateLong(context.getHeader(name));
    }

    /** TODO */
    public boolean isUserInRole(String name) {
        return false;
    }

    /** TODO */
    public StringBuffer getRequestURL() {
        return null;
    }

    /** TODO */
    public Principal getUserPrincipal() {
        return null;
    }

    /** */
    public Enumeration<?> getHeaderNames() {
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.putAll(context.getHeaders());
        return hashtable.keys();
    }

    /** */
    private Cookie[] cookies;

    /** */
    public Cookie[] getCookies() {
        String cookieValue = getHeader("Cookie");
        if (cookieValue == null) {
            return null;
        }
        List<Cookie> cookieList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(cookieValue, "; ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int p = token.indexOf('=');
            String name, value;
            if (p == -1) {
                name = token;
                value = "";
            } else {
                name = token.substring(0, p);
                value = token.substring(p + 1);
            }
            cookieList.add(new Cookie(name, value));
        }
        cookies = cookieList.toArray(new Cookie[cookieList.size()]);
        return cookies;
    }

    /** TODO */
    public HttpSession getSession() {
        return null;
    }

    /** TODO */
    public HttpSession getSession(boolean arg0) {
        return null;
    }

    /** */
    public String getHeader(String name) {
        return context.getHeader(name);
    }

    /** TODO */
    public Enumeration<?> getHeaders(String name) {
        return null;
    }

    /** */
    public int getContentLength() {
        return getIntHeader("content-length");
    }

    public int getServerPort() {
        return context.getRemotePort();
    }

    /** TODO */
    public boolean isSecure() {
        return false;
    }

    /** */
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(context.getInputStream()));
    }

    /** */
    private String encoding = "ISO-8859-1";

    /** */
    public String getCharacterEncoding() {
        return encoding;
    }

    /** */
    public String getContentType() {
        return context.getHeader("content-type");
    }

    /** */
    public String getProtocol() {
        return context.getProtocol().getName();
    }

    /** TODO */
    public String getRemoteAddr() {
        return null;
    }

    /** */
    public String getRemoteHost() {
        return context.getRemoteHost();
    }

    /** TODO */
    public String getScheme() {
        return null;
    }

    /** TODO */
    public String getServerName() {
        return null;
    }

    /** */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /** */
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        this.encoding = encoding;
    }

    /** */
    public Enumeration<?> getAttributeNames() {
        Hashtable<String, Object> hashtable = new Hashtable<>();
        hashtable.putAll(attributes);
        return hashtable.keys();
    }

    /** TODO */
    public Enumeration<?> getLocales() {
        return null;
    }

    /** */
    public Enumeration<?> getParameterNames() {
        Hashtable<String, String[]> hashtable = new Hashtable<>();
        hashtable.putAll(context.getParameters());
        return hashtable.elements();
    }

    /** TODO */
    public Locale getLocale() {
        return null;
    }

    /** */
    public Map<?, ?> getParameterMap() {
        return context.parameters;
    }

    /** */
    private ServletInputStream servletInputStream;

    /**
     * @return 常に同じインスタンスが返ります。
     */
    public ServletInputStream getInputStream() throws IOException {
        if (servletInputStream == null) {

            if (context.getInputStream() instanceof ServletInputStream) {
                servletInputStream = (ServletInputStream) context.getInputStream();
            } else {
                servletInputStream = new ServletInputStream() {
                    InputStream is;
                    {
                        this.is = context.getInputStream();
//Debug.println("available-5: " + is.available() + ", " + is);
                    }
                    public int read() throws IOException {
                        return is.read();
                    }
                    public int available() throws IOException {
                        return is.available();
                    }
                };
            }
        }

        return servletInputStream;
    }

    /** */
    private Map<String, Object> attributes = new HashMap<>();

    /** */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /** */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /** */
    public String getParameter(String name) {
        String[] values = context.getParameters().get(name);
        return values != null ? values[0] : null;
    }

    /** */
    public String getRealPath(String path) {
        return null;
    }

    /** */
    public String[] getParameterValues(String name) {
        return context.getParameters().get(name);
    }

    /** TODO */
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    /* @see javax.servlet.ServletRequest#getLocalAddr() */
    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return null;
    }

    /* @see javax.servlet.ServletRequest#getLocalName() */
    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* @see javax.servlet.ServletRequest#getLocalPort() */
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* @see javax.servlet.ServletRequest#getRemotePort() */
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }
}

/* */
