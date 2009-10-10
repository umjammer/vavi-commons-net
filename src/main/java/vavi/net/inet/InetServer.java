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

    /** �T�[�o�[�\�P�b�g */
    private ServerSocket serverSocket;
    /** �T�[�o�[ */
    private final ExecutorService server;
    /** �X���b�h�v�[�� */
    private final ExecutorService pool;
    /** */
    private int port;

    /**
     * �w�肵���|�[�g�ԍ��ŃT�[�o�[���N�����܂��B
     * ���ۍs���������Ƃ� {@link #setSocketHandlerFactory(SocketHandlerFactory)}
     * ���g�p���ēo�^���Ă��������B
     * @param port �T�[�o�[�̃|�[�g�ԍ�
     */
    public InetServer(int port) {
        this.port = port;
        pool = Executors.newCachedThreadPool();
        server = Executors.newSingleThreadExecutor();
    }

    /** �ڑ���҂X���b�h�p�̃^�X�N */
    private Runnable serverTask = new Runnable() {
        /** �ڑ���҂��C�x���g�����X�i�ɑ΂��Ĕ��s���܂��B */
        public void run() {
            while (true) {
                try {
                    acceptingList.add(pool.submit(socketHandlerFactory.getSocketHandler(serverSocket.accept())));
                } catch (Throwable t) {
                    // Throwable �ŏI�����܂���
Debug.println(t);
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
            }
        }
    };

    /**
     * �ڑ����ƂɋN������n���h���N���X���쐬����t�@�N�g���N���X�ł��B
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
    private List<Future<?>> acceptingList = new ArrayList<Future<?>>();

    /**
     * �T�[�o�[�X���b�h���J�n���܂��B
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
//Debug.println("Listening on " + serverSocket);
        serving = server.submit(serverTask);
    }

    /**
     * �T�[�o�[�X���b�h���~���܂��B
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
