/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * HttpServletResponseAdapter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 050221 nsano initial version <br>
 */
public class HttpServletResponseAdapter implements HttpServletResponse {

    /** */
    private HttpContext context;

    /** */
    public HttpServletResponseAdapter(HttpContext context) {
        this.context = context;
    }

    /** */
    public void sendError(int error) throws IOException {
        context.setStatus(error);
    }

    /** */
    public void setStatus(int status) {
        context.setStatus(status);
    }

    /** */
    public void sendError(int error, String message) throws IOException {
        context.setStatus(error);
        context.setStatusMessage(message);
    }

    /** */
    public void setStatus(int status, String message) {
    }

    /** */
    public void sendRedirect(String url) throws IOException {
        context.setStatus(SC_MOVED_PERMANENTLY);
        context.setStatusMessage(url); // TODO correction
    }

    /** */
    public boolean containsHeader(String name) {
        return context.headers.containsKey(name);
    }

    /** */
    public void addIntHeader(String name, int value) {
        context.setHeader(name, String.valueOf(value));
    }

    /** */
    public void setIntHeader(String name, int value) {
        context.setHeader(name, String.valueOf(value));
    }

    /** */
    public void addDateHeader(String name, long value) {
        context.setHeader(name, Protocol.Util.toDateString(value));
    }

    /** */
    public void setDateHeader(String name, long value) {
        context.setHeader(name, Protocol.Util.toDateString(value));
    }

    /** */
    public void addCookie(Cookie cookie) {
        setHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue());
    }

    /** TODO check */
    public String encodeRedirectURL(String url) {
        try {
            return URLEncoder.encode(url, encoding);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /** */
    public String encodeRedirectUrl(String url) {
        return url;
    }

    /** TODO check */
    public String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, encoding);
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    /** */
    public String encodeUrl(String url) {
        return url;
    }

    /** */
    public void addHeader(String name, String value) {
        context.setHeader(name, value);
    }

    /** */
    public void setHeader(String name, String value) {
        context.setHeader(name, value);
    }

    /** */
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream(8192);

    /** TODO when no buffering */
    public int getBufferSize() {
        return buffer.size();
    }

    /** */
    private boolean committed = false;

    /** */
    private boolean nowFlushing = false;

    /** TODO buffer を使用しない場合 */
    public void flushBuffer() throws IOException {
//new Exception("*** DUMMY ***").printStackTrace();
        if (nowFlushing) {
//Debug.println("NOW FLUSHING");
            return;
        }
        nowFlushing = true;
        if (committed) {
//Debug.println("ALREADY COMMITED: " + committed);
            return;
        }
        // status, header
Debug.println(Level.FINE, "-------- response: ");
String rootLevel = LogManager.getLogManager().getProperty(".level");
if (Arrays.asList("FINE", "FINER", "FINEST").stream().anyMatch(l -> l.equals(rootLevel))) {
 HttpUtil.printResponseHeader(System.err, context);
}
        HttpUtil.printResponseHeader(new PrintStream(context.getOutputStream()), context);
        // content
Debug.println(Level.FINE, "flushing content length: " + buffer.size() + "\n" + StringUtil.getDump(buffer.toByteArray(), 128));
Debug.println(Level.FINE, "-------- response: ");
        context.getOutputStream().write(buffer.toByteArray());
        context.getOutputStream().flush();
//Debug.println("flushing done");
        committed = true;
//Debug.println("COMMITED: " + committed);
        nowFlushing = false;
    }

    /** */
    public void reset() {
        if (committed) {
            throw new IllegalStateException("already committed");
        }
        // TODO clear status, header
        resetBuffer();
    }

    /** */
    public void resetBuffer() {
        if (committed) {
            throw new IllegalStateException("already committed");
        }
        buffer.reset();
        committed = false;
    }

    /** */
    public boolean isCommitted() {
//Debug.println("IS COMMITED: " + committed);
        return committed;
    }

    /** */
    public void setBufferSize(int size) {
        if (committed) {
            throw new IllegalStateException("already committed");
        }
    }

    /** */
    public void setContentLength(int length) {
        context.setHeader("content-length", String.valueOf(length));
    }

    /** */
    private boolean usedWriter = false;

    /** */
    private PrintWriter printWriter;

    /** */
    public PrintWriter getWriter() throws IOException {
        if (usedOutputStream) {
            throw new IllegalStateException("#getOutputStream() already called.");
        }
        usedWriter = true;
        if (printWriter == null) {
            printWriter = new PrintWriter(new OutputStreamWriter(buffer, encoding));
        }
        return printWriter;
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
    public void setContentType(String type) {
        if (!usedWriter) {
            StringTokenizer st = new StringTokenizer(type, "; ");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int p = token.indexOf('=');
                if (p >= 0) {
                    String name = token.substring(0, p).trim().toLowerCase();
                    if ("charset".equals(name)) {
                        encoding = token.substring(p + 1).trim();
                        break;
                    } else {
Debug.println(Level.FINE, "name: " + name);
                    }
                } else {
                    String name = token.trim().toLowerCase();
Debug.println(Level.FINE, "name: " + name);
                }
            }
        }
        context.setHeader("content-type", type);
    }

    /** */
    private Locale locale;

    /** */
    public Locale getLocale() {
        return locale;
    }

    /** */
    public void setLocale(Locale locale) {
        // TODO set content-type
        this.locale = locale;
    }

    /** */
    private boolean usedOutputStream = false;

    /** */
    private ServletOutputStream servletOutputStream;

    /**
     * @return 常に同じインスタンスが返ります。
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (usedWriter) {
            throw new IllegalStateException("#getWriter() already called.");
        }
        usedOutputStream = true;
        if (servletOutputStream == null) {
            if (context.getOutputStream() instanceof ServletOutputStream) {
                servletOutputStream = (ServletOutputStream) context.getOutputStream();
            } else {
                servletOutputStream = new ServletOutputStream() {
                    public void write(int b) throws IOException {
                        buffer.write(b);
                    }
                    public void flush() throws IOException {
                        // TODO 要リファクタ
                        // 親インスタンスを使用してるから汚くなってる
                        flushBuffer();
                    }
                };
            }
        }
        return servletOutputStream;
    }

    /** */
    public void setOutputStream(OutputStream outputStream) {
        context.setOutputStream(outputStream);
    }

    /* @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String) */
    public void setCharacterEncoding(String charset) {
        // TODO Auto-generated method stub

    }
}

/* */
