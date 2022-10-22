/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.inet;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * Simple Internet Server.
 *
 * @author <a href=mailto:umjammer@gmail.com>nsano</a>
 * @version 0.00 030314 nsano initial version <br>
 *          0.01 030318 nsano complete <br>
 */
public class InetServer {

    /** server socket */
    private ServerSocket serverSocket;
    /** server */
    private final ExecutorService server;
    /** thread pool */
    private final ExecutorService pool;
    /** */
    protected int port;

    /**
     * Starts a server using specified port number.
     * Register what you want to do using {@link #setSocketHandlerFactory(SocketHandlerFactory)}.
     * @param port port number of the server
     */
    public InetServer(int port) {
        this.port = port;
        pool = Executors.newCachedThreadPool();
        server = Executors.newSingleThreadExecutor();
    }

    /** the task waiting for a connection */
    private Runnable serverTask = new Runnable() {
        /** waiting for a connection, and fire an event to the listener */
        public void run() {
            while (true) {
                try {
                    acceptingList.add(pool.submit(socketHandlerFactory.getSocketHandler(serverSocket.accept())));
                } catch (Throwable t) {
                    if (!isRunning()) {
                        break;
                    }
                    // not terminate by Throwable
Debug.println(t);
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
            }
        }
    };

    /**
     * The factory class that executes for each connection.
     * @see #setSocketHandlerFactory(SocketHandlerFactory)
     */
    private SocketHandlerFactory socketHandlerFactory;

    /**
     * @param socketHandlerFactory The socketHandlerFactory to set.
     */
    public void setSocketHandlerFactory(SocketHandlerFactory socketHandlerFactory) {
        this.socketHandlerFactory = socketHandlerFactory;
    }

    /** */
    private Future<?> serving;

    /** */
    private List<Future<?>> acceptingList = new ArrayList<>();

    /**
     * Starts a server thread.
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
//Debug.println("Listening on " + serverSocket);
        serving = server.submit(serverTask);
    }

    /**
     * Stops a server thread.
     */
    public void stop() throws IOException {
        try {
            serving.cancel(true);
            for (Future<?> accepting : acceptingList) {
                accepting.cancel(false);
            }
Debug.println(Level.FINE, "Shutdown");
        } finally {
            serverSocket.close();
        }
    }

    /** */
    public boolean isRunning() {
        return !serving.isCancelled();
    }
}

/* */
