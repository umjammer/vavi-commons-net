/*
 * CyberHTTP for Java
 *
 * Copyright (C) Satoshi Konno 2002-2003
 */

package vavi.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.event.EventListenerList;

import vavi.net.inet.InetServer;
import vavi.net.inet.SocketHandlerFactory;
import vavi.util.Debug;


/**
 * HTTPServer.
 *
 * @author Satoshi Konno
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 12/12/02 first revision. <br>
 *          10/20/03 Improved the HTTP server using multithreading. <br>
 */
public class HttpServer extends InetServer {

    /** */
    private final static String NAME = "Vavi HTTP";

    /** */
    private final static String VERSION = "1.0.1";

    /** */
    public final static int DEFAULT_PORT = 80;

    /** */
    public static String getName() {
        String name = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        return name + "/" + version + " " + NAME + "/" + VERSION;
    }

    /** Constructor */
    public HttpServer(String addr, int port) throws IOException {
        super(port);
        this.address = InetAddress.getByName(addr);
        setSocketHandlerFactory(socketHandlerFactory);
    }

    /** ServerSocket */
    private InetAddress address;

    /** httpRequest */
    private EventListenerList httpRequestListenerList = new EventListenerList();

    /** */
    public void addRequestListener(HttpRequestListener listener) {
        httpRequestListenerList.add(HttpRequestListener.class, listener);
    }

    /** */
    public void removeRequestListener(HttpRequestListener listener) {
        httpRequestListenerList.remove(HttpRequestListener.class, listener);
    }

    /** */
    protected void performRequestListener(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object[] listeners = httpRequestListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == HttpRequestListener.class) {
                ((HttpRequestListener) listeners[i + 1]).doService(request, response);
            }
        }
    }

    /** 実際のサーバータスク */
    private SocketHandlerFactory socketHandlerFactory = new SocketHandlerFactory() {
        public Runnable getSocketHandler(final Socket socket) {
Debug.println("<<<< <<<< <<<< <<<< <<<< <<<< <<<< <<<< <<<< ACCEPT: " + socket.getRemoteSocketAddress() + ": " + socket.hashCode());
            return new Runnable() {
                public void run() {
                    InputStream is;
                    OutputStream os = null;
Debug.println("==== ONE PROCESS START: " + socket.getRemoteSocketAddress() + ": " + socket.hashCode());
                    try {
                        is = socket.getInputStream();
                        os = socket.getOutputStream();

                        // request
                        HttpContext requestContext = new HttpContext();
                        requestContext.setRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
                        requestContext.setRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());
                        requestContext.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
                        requestContext.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());
                        HttpUtil.parseRequestHeader(is, requestContext);
//Debug.println("<<<< REQUEST:\n" + StringUtil.paramString(requestContext));
                        HttpServletRequest request = new HttpServletRequestAdapter(requestContext);
                        // response
                        HttpContext responseContext = new HttpContext();
                        responseContext.setRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
                        responseContext.setRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());
                        responseContext.setLocalHost(((InetSocketAddress) socket.getLocalSocketAddress()).getHostName());
                        responseContext.setLocalPort(((InetSocketAddress) socket.getLocalSocketAddress()).getPort());
                        responseContext.setProtocol(requestContext.getProtocol()); // TODO check
                        responseContext.setOutputStream(os);
                        HttpServletResponse response = new HttpServletResponseAdapter(responseContext);

                        performRequestListener(request, response);

                        if (!response.isCommitted()) {
                            response.flushBuffer();
                        }
//                  } catch (ServletException e) {
//                      HttpUtil.printErrorResponse(os, e);
//                  } catch (IOException e) {
//                      HttpUtil.printErrorResponse(os, e);
                    } catch (Exception e) {
Debug.println("==== ERROR RESPONSE: " + e);
                        // TODO response reset
                        HttpUtil.printErrorResponse(os, e);
                    } finally {
                        try {
                            os.flush();
Debug.println("==== ONE PROCESS DONE: " + socket.getRemoteSocketAddress());
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                }
            };
        }
    };

    /** */
    public void start() throws IOException {
        super.start();
Debug.println("+++ HTTP server: address: " + address + ", port: " + port);
    }

    /** */
    public void stop() throws IOException {
        super.stop();
    }
}

/* */
