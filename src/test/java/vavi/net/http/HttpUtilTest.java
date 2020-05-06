/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.http;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * HttpUtilTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/06 umjammer initial version <br>
 */
class HttpUtilTest {

    @Test
    void test() throws Exception {
        URI uri = URI.create("https://example.com?id=aaa&second=2#sharp");
        Map<String, String[]> params = HttpUtil.splitQuery(uri);
        assertEquals(params.get("id")[0], "aaa");
        assertEquals(params.get("second")[0], "2");

        uri = URI.create("https://example.com");
        params = HttpUtil.splitQuery(uri);
        assertEquals(params.get("id"), null);

        uri = URI.create("https://example.com?id=aaa&id=bbb");
        params = HttpUtil.splitQuery(uri);
        assertEquals(params.get("id")[1], "bbb");
    }
}

/* */
