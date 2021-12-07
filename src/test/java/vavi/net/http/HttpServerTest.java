/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * HttpServerTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/21 umjammer initial version <br>
 */
class HttpServerTest {

    @Test
    void test() throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);

        HttpServer server = new HttpServer("localhost", 23456);
        server.addRequestListener((req, res) -> {
System.err.println("here: " + req.getRequestURI());
            res.setContentType("plain/text");
            OutputStream os = res.getOutputStream();
            os.write("hello world".getBytes());
            os.flush();
            os.close();
            cdl.countDown();
        });

System.err.println("start1");
        server.start();
        HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:23456/").openConnection();
        InputStream is = (InputStream) conn.getContent();
        cdl.await();
System.err.println("content: " + is);
System.err.println("available: " + is.available());
        byte[] b = new byte[is.available()];
        int i = 0;
        while (true) {
            int r = is.read(); // cannnot block read, why?
//System.err.println("read: " + r);
            if (r == -1) {
                break;
            }
            b[i++] = (byte) r;
        }
        assertArrayEquals("hello world".getBytes(), b);

System.err.println("stop");
        server.stop();
        assertThrows(Exception.class, () -> {
            HttpURLConnection conn2 = (HttpURLConnection) new URL("http://localhost:23456/").openConnection();
            conn2.getContent();
        });

System.err.println("start2");
        server.start();
        conn = (HttpURLConnection) new URL("http://localhost:23456/").openConnection();
        conn.getContent();
        server.stop();
    }
}

/* */
