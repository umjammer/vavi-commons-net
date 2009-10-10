/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.IOException;
import java.io.PrintStream;


/**
 * DefaultProtocol. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 061015 nsano initial version <br>
 */
public class DefaultProtocol implements Protocol {

    /* */
    public String getName() {
        return "UNKNOWN";
    }

    /* */
    public boolean matchesRequestLine(String line) {
        return false;
    }

    /* */
    public boolean matchesResponseLine(String line) {
        return false;
    }

    /* */
    public void parseRequestLine(String line, HttpContext context) throws IOException {
        context.setProtocol(this);
        context.setRequestURI("");
        context.setMethod("");
    }

    /* */
    public void parseResponseLine(String line, HttpContext context) throws IOException {
    }

    /* */
    public void printResponseLine(PrintStream ps, HttpContext context) {
        ps.print(getName());
        ps.print(' ');
//Debug.println("status: " + context.getStatus() + ", " + context.hashCode());
        ps.print(context.getStatus());
        ps.print(' ');
        ps.print(context.getStatusMessage());
    }

    /* */
    public void printRequestLine(PrintStream ps, HttpContext context) {
        ps.print(context.getMethod());
        ps.print(' ');
        ps.print(context.getRequestURI());
    }

    /* for server */
    public void parseRequestHeaders(HttpContext context) {
    }

    /* for client */
    public void addRequestHeaders(HttpContext context) {
    }
}

/* */
