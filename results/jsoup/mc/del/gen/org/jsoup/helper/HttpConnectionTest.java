package org.jsoup.helper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.MultiLocaleRule;
import org.jsoup.MultiLocaleRule.MultiLocaleTest;
import org.jsoup.integration.ParseTest;
import org.junit.Rule;
import org.junit.Test;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

public class HttpConnectionTest {

    /* most actual network http connection tests are in integration */
    @Rule
    public MultiLocaleRule rule = new MultiLocaleRule();

    @Test(expected = IllegalArgumentException.class)
    @Ignore("Redundant Test Case (identified by JSR)")
    public void throwsExceptionOnParseWithoutExecute() throws IOException {
        Connection con = HttpConnection.connect("http://example.com");
        con.response().parse();
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore("Redundant Test Case (identified by JSR)")
    public void throwsExceptionOnBodyWithoutExecute() throws IOException {
        Connection con = HttpConnection.connect("http://example.com");
        con.response().body();
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore("Redundant Test Case (identified by JSR)")
    public void throwsExceptionOnBodyAsBytesWithoutExecute() throws IOException {
        Connection con = HttpConnection.connect("http://example.com");
        con.response().bodyAsBytes();
    }

    @Test
    @MultiLocaleTest
    @Ignore("Redundant Test Case (identified by JSR)")
    public void caseInsensitiveHeaders() {
        Connection.Response res = new HttpConnection.Response();
        res.header("Accept-Encoding", "gzip");
        res.header("content-type", "text/html");
        res.header("refErrer", "http://example.com");
        assertTrue(res.hasHeader("Accept-Encoding"));
        assertTrue(res.hasHeader("accept-encoding"));
        assertTrue(res.hasHeader("accept-Encoding"));
        assertTrue(res.hasHeader("ACCEPT-ENCODING"));
        assertEquals("gzip", res.header("accept-Encoding"));
        assertEquals("gzip", res.header("ACCEPT-ENCODING"));
        assertEquals("text/html", res.header("Content-Type"));
        assertEquals("http://example.com", res.header("Referrer"));
        res.removeHeader("Content-Type");
        assertFalse(res.hasHeader("content-type"));
        res.removeHeader("ACCEPT-ENCODING");
        assertFalse(res.hasHeader("Accept-Encoding"));
        res.header("ACCEPT-ENCODING", "deflate");
        assertEquals("deflate", res.header("Accept-Encoding"));
        assertEquals("deflate", res.header("accept-Encoding"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void headers() {
        Connection con = HttpConnection.connect("http://example.com");
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "text/html");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "http://example.com");
        con.headers(headers);
        assertEquals("text/html", con.request().header("content-type"));
        assertEquals("keep-alive", con.request().header("Connection"));
        assertEquals("http://example.com", con.request().header("Host"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void sameHeadersCombineWithComma() {
        Map<String, List<String>> headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("no-cache");
        values.add("no-store");
        headers.put("Cache-Control", values);
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        assertEquals("no-cache, no-store", res.header("Cache-Control"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void multipleHeaders() {
        Connection.Request req = new HttpConnection.Request();
        req.addHeader("Accept", "Something");
        req.addHeader("Accept", "Everything");
        req.addHeader("Foo", "Bar");
        assertTrue(req.hasHeader("Accept"));
        assertTrue(req.hasHeader("ACCEpt"));
        assertEquals("Something, Everything", req.header("accept"));
        assertTrue(req.hasHeader("fOO"));
        assertEquals("Bar", req.header("foo"));
        List<String> accept = req.headers("accept");
        assertEquals(2, accept.size());
        assertEquals("Something", accept.get(0));
        assertEquals("Everything", accept.get(1));
        Map<String, List<String>> headers = req.multiHeaders();
        assertEquals(accept, headers.get("Accept"));
        assertEquals("Bar", headers.get("Foo").get(0));
        assertTrue(req.hasHeader("Accept"));
        assertTrue(req.hasHeaderWithValue("accept", "Something"));
        assertTrue(req.hasHeaderWithValue("accept", "Everything"));
        assertFalse(req.hasHeaderWithValue("accept", "Something for nothing"));
        req.removeHeader("accept");
        headers = req.multiHeaders();
        assertEquals("Bar", headers.get("Foo").get(0));
        assertFalse(req.hasHeader("Accept"));
        assertTrue(headers.get("Accept") == null);
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void ignoresEmptySetCookies() {
        // prep http response header map
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Set-Cookie", Collections.<String>emptyList());
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        assertEquals(0, res.cookies().size());
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void ignoresEmptyCookieNameAndVals() {
        // prep http response header map
        Map<String, List<String>> headers = new HashMap<>();
        List<String> cookieStrings = new ArrayList<>();
        cookieStrings.add(null);
        cookieStrings.add("");
        cookieStrings.add("one");
        cookieStrings.add("two=");
        cookieStrings.add("three=;");
        cookieStrings.add("four=data; Domain=.example.com; Path=/");
        headers.put("Set-Cookie", cookieStrings);
        HttpConnection.Response res = new HttpConnection.Response();
        res.processResponseHeaders(headers);
        assertEquals(4, res.cookies().size());
        assertEquals("", res.cookie("one"));
        assertEquals("", res.cookie("two"));
        assertEquals("", res.cookie("three"));
        assertEquals("data", res.cookie("four"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void connectWithUrl() throws MalformedURLException {
        Connection con = HttpConnection.connect(new URL("http://example.com"));
        assertEquals("http://example.com", con.request().url().toExternalForm());
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore("Redundant Test Case (identified by JSR)")
    public void throwsOnMalformedUrl() {
        Connection con = HttpConnection.connect("bzzt");
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void userAgent() {
        Connection con = HttpConnection.connect("http://example.com/");
        assertEquals(HttpConnection.DEFAULT_UA, con.request().header("User-Agent"));
        con.userAgent("Mozilla");
        assertEquals("Mozilla", con.request().header("User-Agent"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void timeout() {
        Connection con = HttpConnection.connect("http://example.com/");
        assertEquals(30 * 1000, con.request().timeout());
        con.timeout(1000);
        assertEquals(1000, con.request().timeout());
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void referrer() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.referrer("http://foo.com");
        assertEquals("http://foo.com", con.request().header("Referer"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void method() {
        Connection con = HttpConnection.connect("http://example.com/");
        assertEquals(Connection.Method.GET, con.request().method());
        con.method(Connection.Method.POST);
        assertEquals(Connection.Method.POST, con.request().method());
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore("Redundant Test Case (identified by JSR)")
    public void throwsOnOddData() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.data("Name", "val", "what");
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void data() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.data("Name", "Val", "Foo", "bar");
        Collection<Connection.KeyVal> values = con.request().data();
        Object[] data = values.toArray();
        Connection.KeyVal one = (Connection.KeyVal) data[0];
        Connection.KeyVal two = (Connection.KeyVal) data[1];
        assertEquals("Name", one.key());
        assertEquals("Val", one.value());
        assertEquals("Foo", two.key());
        assertEquals("bar", two.value());
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void cookie() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.cookie("Name", "Val");
        assertEquals("Val", con.request().cookie("Name"));
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void inputStream() {
        Connection.KeyVal kv = HttpConnection.KeyVal.create("file", "thumb.jpg", ParseTest.inputStreamFrom("Check"));
        assertEquals("file", kv.key());
        assertEquals("thumb.jpg", kv.value());
        assertTrue(kv.hasInputStream());
        kv = HttpConnection.KeyVal.create("one", "two");
        assertEquals("one", kv.key());
        assertEquals("two", kv.value());
        assertFalse(kv.hasInputStream());
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void requestBody() {
        Connection con = HttpConnection.connect("http://example.com/");
        con.requestBody("foo");
        assertEquals("foo", con.request().requestBody());
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void encodeUrl() throws MalformedURLException {
        URL url1 = new URL("http://test.com/?q=white space");
        URL url2 = HttpConnection.encodeUrl(url1);
        assertEquals("http://test.com/?q=white%20space", url2.toExternalForm());
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void noUrlThrowsValidationError() throws IOException {
        HttpConnection con = new HttpConnection();
        boolean threw = false;
        try {
            con.execute();
        } catch (IllegalArgumentException e) {
            threw = true;
            assertEquals("URL must be specified to connect", e.getMessage());
        }
        assertTrue(threw);
    }

    @Test
    @Ignore("Redundant Test Case (identified by JSR)")
    public void handlesHeaderEncodingOnRequest() {
        Connection.Request req = new HttpConnection.Request();
        req.addHeader("xxx", "é");
    }
}
