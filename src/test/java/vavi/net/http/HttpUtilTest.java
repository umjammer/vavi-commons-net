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
import static org.junit.jupiter.api.Assertions.assertNull;


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
        assertEquals("aaa", params.get("id")[0]);
        assertEquals("2", params.get("second")[0]);

        uri = URI.create("https://example.com");
        params = HttpUtil.splitQuery(uri);
        assertEquals(null, params.get("id"));

        uri = URI.create("https://example.com?id=aaa&id=bbb");
        params = HttpUtil.splitQuery(uri);
        assertEquals("bbb", params.get("id")[1]);
    }

    @Test
    public void testParamsMap() throws Exception {
        URI uri = URI.create("scheme:///?id=test");

        Map<String, String[]> params = HttpUtil.splitQuery(uri);
        assertEquals("test", params.get("id")[0]);
    }

    @Test
    public void testParamsMapWithoutValue() throws Exception {
        URI uri = URI.create("scheme:///?id=");

        Map<String, String[]> params = HttpUtil.splitQuery(uri);
        assertNull(params.get("id")[0]);
    }

    @Test
    public void testNoQueryParamsMap() throws Exception {
        URI uri = URI.create("scheme:///");

        Map<String, String[]> params = HttpUtil.splitQuery(uri);
        assertNull(params.get("id"));
    }

    @Test
    public void testParamsMapWithSharp() throws Exception {
        URI uri = URI.create("scheme:///?id=test&second=2#sharp");

        Map<String, String[]> params = HttpUtil.splitQuery(uri);
        assertEquals("test", params.get("id")[0]);
        assertEquals("2", params.get("second")[0]);
    }
}

/* */
