/*
 * CyberHTTP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package vavi.net.http;

import java.io.IOException;
import java.util.EventListener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HTTPRequestListener.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 12/13/02 first revision.
 */
public interface HttpRequestListener extends EventListener {
    /** */
    void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}

/* */
