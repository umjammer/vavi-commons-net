/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;

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
        HttpServer server = new HttpServer("localhost", 23456);
        server.addRequestListener((req, res) -> {
            res.setContentType("plain/text");
            OutputStream os = res.getOutputStream();
            os.write("hello world".getBytes());
            os.flush();
        });
        server.start();
        HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:23456/").openConnection();
        conn.getContent();
        server.stop();

        assertThrows(Exception.class, () -> {
            HttpURLConnection conn2 = (HttpURLConnection) new URL("http://localhost:23456/").openConnection();
            conn2.getContent();
        });

        server.start();
        conn = (HttpURLConnection) new URL("http://localhost:23456/").openConnection();
        conn.getContent();
        server.stop();
    }

}

/* */
