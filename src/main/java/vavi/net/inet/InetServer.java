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

import vavi.util.Debug;


/**
 * Simple Internet Server.
 *
 * @author <a href=mailto:vavivavi@yahoo.co.jp>nsano</a>
 * @version 0.00 030314 nsano initial version <br>
 *          0.01 030318 nsano complete <br>
 */
public class InetServer {

    /** サーバーソケット */
    private ServerSocket serverSocket;
    /** サーバー */
    private final ExecutorService server;
    /** スレッドプール */
    private final ExecutorService pool;
    /** */
    protected int port;

    /**
     * 指定したポート番号でサーバーを起動します。
     * 実際行いたいことは {@link #setSocketHandlerFactory(SocketHandlerFactory)}
     * を使用して登録してください。
     * @param port サーバーのポート番号
     */
    public InetServer(int port) {
        this.port = port;
        pool = Executors.newCachedThreadPool();
        server = Executors.newSingleThreadExecutor();
    }

    /** 接続を待つスレッド用のタスク */
    private Runnable serverTask = new Runnable() {
        /** 接続を待ちイベントをリスナに対して発行します。 */
        public void run() {
            while (true) {
                try {
                    acceptingList.add(pool.submit(socketHandlerFactory.getSocketHandler(serverSocket.accept())));
                } catch (Throwable t) {
                    if (!isRunning()) {
                        break;
                    }
                    // Throwable で終了しません
Debug.println(t);
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
            }
        }
    };

    /**
     * 接続ごとに起動するハンドラクラスを作成するファクトリクラスです。
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
     * サーバースレッドを開始します。
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
//Debug.println("Listening on " + serverSocket);
        serving = server.submit(serverTask);
    }

    /**
     * サーバースレッドを停止します。
     */
    public void stop() throws IOException {
        try {
            serving.cancel(true);
            for (Future<?> accepting : acceptingList) {
                accepting.cancel(false);
            }
Debug.println("Shutdown");
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
