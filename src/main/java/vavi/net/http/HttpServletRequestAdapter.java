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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;


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

    // TODO
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    // TODO
    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    // TODO
    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    // TODO
    @Override
    public String getAuthType() {
        return null;
    }

    // TODO
    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getMethod() {
        return context.getMethod();
    }

    // TODO
    @Override
    public String getPathInfo() {
        return null;
    }

    // TODO
    @Override
    public String getPathTranslated() {
        return null;
    }

    // TODO
    @Override
    public String getQueryString() {
        return null;
    }

    // TODO
    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return context.getRequestURI();
    }

    // TODO
    @Override
    public String getRequestedSessionId() {
        return null;
    }

    // TODO
    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        return Integer.parseInt(context.getHeader(name));
    }

    @Override
    public long getDateHeader(String name) {
        return Protocol.Util.toDateLong(context.getHeader(name));
    }

    // TODO
    @Override
    public boolean isUserInRole(String name) {
        return false;
    }

    // TODO
    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    // TODO
    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Hashtable<String, String> hashtable = new Hashtable<>(context.getHeaders());
        return hashtable.keys();
    }

    /** */
    private Cookie[] cookies;

    @Override
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

    // TODO
    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    // TODO
    @Override
    public HttpSession getSession(boolean arg0) {
        return null;
    }

    @Override
    public String getHeader(String name) {
        return context.getHeader(name);
    }

    // TODO
    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public int getContentLength() {
        return getIntHeader("content-length");
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public int getServerPort() {
        return context.getRemotePort();
    }

    // TODO
    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(context.getInputStream()));
    }

    /** */
    private String encoding = "ISO-8859-1";

    @Override
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public String getContentType() {
        return context.getHeader("content-type");
    }

    @Override
    public String getProtocol() {
        return context.getProtocol().getName();
    }

    // TODO
    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return context.getRemoteHost();
    }

    // TODO
    @Override
    public String getScheme() {
        return null;
    }

    // TODO
    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        this.encoding = encoding;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Hashtable<String, Object> hashtable = new Hashtable<>(attributes);
        return hashtable.keys();
    }

    // TODO
    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Hashtable<String, String[]> hashtable = new Hashtable<>(context.getParameters());
        return hashtable.keys();
    }

    // TODO
    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return context.parameters;
    }

    /** */
    private ServletInputStream servletInputStream;

    /**
     * @return returns the same instance always
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (servletInputStream == null) {

            if (context.getInputStream() instanceof ServletInputStream) {
                servletInputStream = (ServletInputStream) context.getInputStream();
            } else {
                servletInputStream = new ServletInputStream() {
                    @Override
                    public boolean isFinished() {
                        try {
                            return available() != 0;
                        } catch (IOException e) {
                            return false;
                        }
                    }

                    @Override
                    public boolean isReady() {
                        return true; // TODO
                    }

                    @Override
                    public void setReadListener(ReadListener readListener) {
                        // TODO
                    }

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
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public String getParameter(String name) {
        String[] values = context.getParameters().get(name);
        return values != null ? values[0] : null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return context.getParameters().get(name);
    }

    // TODO
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public String getRequestId() {
        return null;
    }

    @Override
    public String getProtocolRequestId() {
        return null;
    }

    @Override
    public ServletConnection getServletConnection() {
        return null;
    }

    @Override
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }
}
