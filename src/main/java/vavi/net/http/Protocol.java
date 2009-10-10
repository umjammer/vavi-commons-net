/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;


/**
 * Protocol. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 061015 nsano initial version <br>
 */
public interface Protocol {

    /** */
    String getName();

    /** used in {@link Factory#getInstanceByRequestLine(String)} */
    boolean matchesRequestLine(String line);

    /**
     * @param line
     */
    boolean matchesResponseLine(String line);

    /**
     * must be set {@link HttpContext#protocol}
     * @param line
     * @param context
     */
    void parseRequestLine(String line, HttpContext context) throws IOException;

    /**
     * must be set {@link HttpContext#protocol}
     * @param line
     * @param context
     */
    void parseResponseLine(String line, HttpContext context) throws IOException;

    /**
     * @param ps
     * @param context
     * @return
     */
    void printResponseLine(PrintStream ps, HttpContext context);

    /**
     * @param ps
     * @param context
     */
    void printRequestLine(PrintStream ps, HttpContext context);

    /**
     * @param context
     */
    void parseRequestHeaders(HttpContext context);

    /**
     * @param context
     */
    void addRequestHeaders(HttpContext context);

    /** */
    class Factory {
        /** */
        private static List<String> classNames = new ArrayList<String>();

        /* TODO use properties file */
        static {
            classNames.add("vavi.net.http.HttpProtocol");
        }

        /** */
        public static Protocol getInstanceByRequestLine(String line) {
            for (String className : classNames) {
                Protocol protocol;
                try {
                    protocol = (Protocol) Class.forName(className).newInstance();
                } catch (Exception e) {
                    throw (RuntimeException) new IllegalStateException().initCause(e);
                }
                if (protocol.matchesRequestLine(line)) {
                    return protocol;
                }
            }
            throw new IllegalStateException("no protocol handler for: " + line);
//            return new DefaultProtocol(); // TODO
        }

        /** */
        public static Protocol getInstanceByResponseLine(String line) {
            for (String className : classNames) {
                Protocol protocol;
                try {
                    protocol = (Protocol) Class.forName(className).newInstance();
                } catch (Exception e) {
                    throw (RuntimeException) new IllegalStateException().initCause(e);
                }
                if (protocol.matchesResponseLine(line)) {
                    return protocol;
                }
            }
            throw new IllegalStateException("no protocol handler for: " + line);
//            return new DefaultProtocol(); // TODO
        }

        /** */
        public static Protocol getInstanceByName(String name) {
            for (String className : classNames) {
                Protocol protocol;
                try {
                    protocol = (Protocol) Class.forName(className).newInstance();
                } catch (Exception e) {
                    throw (RuntimeException) new IllegalStateException().initCause(e);
                }
                if (protocol.getName().equals(name)) {
                    return protocol;
                }
            }
            throw new IllegalStateException("no protocol handler for: " + name);
//            return new DefaultProtocol(); // TODO
        }
    }

    /** */
    class Util {
        /**
         * Date format pattern used to parse HTTP date headers in RFC 1123 format.
         */
        private static final String rfc1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

        /** */
        public static String toDateString(long date) {
            DateFormat df = new SimpleDateFormat(rfc1123, Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df.format(new Date(date));
        }

        /** */
        public static long toDateLong(String date) {
            try {
                DateFormat df = new SimpleDateFormat(rfc1123, Locale.US);
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                return df.parse(date).getTime();
            } catch (ParseException e) {
e.printStackTrace();
                return 0;
            }
        }

        /** TODO not thread safe */
        private static String urlEncoding = "UTF-8";

        /** TODO not thread safe */
        public static void setUrlEncoding(String urlEncoding) {
            Util.urlEncoding = urlEncoding;
        }

        /**
         * Parses uri then set {@link HttpContext#parameters}.
         * TODO not thread safe against {@link #urlEncoding}
         * @param uri
         * @param context request
         */
        public static void parseRequestURI(String uri, HttpContext context) throws IOException {
            int questionIndex = uri.indexOf('?');
            if (questionIndex < 0) {
                return;
            } else {
                uri = uri.substring(questionIndex + 1);
            }
        
            StringTokenizer st = new StringTokenizer(uri, "&");
            while (st.hasMoreTokens()) {
                String pair = st.nextToken();
                String name = null;
                String value = null;
                int equalIndex = pair.indexOf('=');
                if (equalIndex < 0) {
                    name = pair;
                } else {
                    name = URLDecoder.decode(pair.substring(0, equalIndex), urlEncoding); // TODO not thread safe
                    value = URLDecoder.decode(pair.substring(equalIndex + 1), urlEncoding); // TODO not thread safe
                }
                if (context.parameters.containsKey(name)) {
                    List<String> values = Arrays.asList(context.parameters.get(name));
                    values.add(value);
                    context.parameters.put(name, values.toArray(new String[values.size()]));
                } else {
                    context.parameters.put(name, new String[] { value });
                }
            }
        }
    }
}

/* */
