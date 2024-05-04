/*
 * CyberHTTP for Java
 *
 * Copyright (C) Satoshi Konno 2002
 */

package vavi.net.http;

import java.io.IOException;
import java.util.EventListener;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * HTTPRequestListener.
 *
 * TODO url filter
 *
 * @author Satoshi Konno
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 12/13/02 first revision.
 */
@FunctionalInterface
public interface HttpRequestListener extends EventListener {

    /** */
    void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}

/* */
