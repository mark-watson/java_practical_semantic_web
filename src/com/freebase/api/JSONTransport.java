/*
 * Copyright (c) 2009, Metaweb Technologies, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY METAWEB TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL METAWEB
 * TECHNOLOGIES OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.freebase.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.parser.ParseException;

import com.freebase.json.JSON;

abstract class JSONTransport {

  private static final int URL_SIZE_LIMIT = 2047;

  private static final int GET = 0;
  private static final int POST = 1;

  // ------------------------------------------------------------------------------

  protected abstract String getURL(String path);

  protected abstract void sign(HttpRequestBase method);

  // ------------------------------------------------------------------------------

  protected JSON get(String uri) {
    return get(uri, null, false);
  }

  protected JSON get(URI uri) {
    return get(uri, null, false);
  }

  protected JSON get(String uri, Map<String, String> headers) {
    return get(uri, headers, false);
  }

  protected JSON get(String uri, Map<String, String> headers, boolean sign) {
    try {
      return get(new URI(uri), headers, sign);
    } catch (URISyntaxException e) {
      throw new FreebaseException(e);
    }
  }

  protected JSON get(URI uri, Map<String, String> headers, boolean sign) {
    if (headers == null) headers = new HashMap<String, String>();
    return check_result(urlfetch(uri, GET, headers, null, sign));
  }

  protected JSON post(String uri, CharSequence content) {
    return post(uri, null, content, false);
  }

  protected JSON post(String uri, Map<String, String> headers, CharSequence content) {
    return post(uri, headers, content, false);
  }

  protected JSON post(String uri, Map<String, String> headers, CharSequence content, boolean sign) {
    try {
      return post(new URI(uri), headers, content, sign);
    } catch (URISyntaxException e) {
      throw new FreebaseException(e);
    }
  }

  protected JSON post(URI uri, CharSequence content) {
    return post(uri, null, content, false);
  }

  protected JSON post(URI uri, Map<String, String> headers, CharSequence content) {
    return post(uri, headers, content, false);
  }

  protected JSON post(URI uri, Map<String, String> headers, CharSequence content, boolean sign) {
    if (headers == null) headers = new HashMap<String, String>();
    if (!headers.containsKey("content-type")) {
      headers.put("content-type", "application/x-www-form-urlencoded");
    }
    headers.put("X-Requested-With", "1");
    return check_result(urlfetch(uri, POST, headers, content, sign));
  }

  private JSON urlfetch(URI uri, int protocol, Map<String, String> headers, CharSequence content, boolean sign) {
    JSON result = null;
    try {
      HttpClient httpclient = new DefaultHttpClient();
      HttpRequestBase method = null;
      if (protocol == GET) {
        method = new HttpGet(uri);
      } else {
        HttpPost httppost = new HttpPost(uri);
        httppost.setEntity(new StringEntity(content.toString()));
        method = httppost;
      }
      for (Map.Entry<String, String> e : headers.entrySet()) {
        method.setHeader(e.getKey(), e.getValue());
      }
      if (sign) {
        sign(method);
      }
      HttpResponse response = httpclient.execute(method);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        result = JSON.parse(new InputStreamReader(entity.getContent(), "UTF-8"));
      } else {
        result = JSON.o();
      }
    } catch (ClientProtocolException e) {
      throw new FreebaseException(e);
    } catch (IOException e) {
      throw new FreebaseException(e);
    } catch (IllegalStateException e) {
      throw new FreebaseException(e);
    } catch (ParseException e) {
      throw new FreebaseException(e);
    } catch (ClassCastException e) {
      throw new FreebaseException(e);
    }
    return check_result(result);
  }

  protected JSON invoke(String path, List<NameValuePair> params) {
    return invoke(path, params, false);
  }

  protected JSON invoke(String path, List<NameValuePair> params, boolean sign) {
    String content = URLEncodedUtils.format(params, "UTF-8");

    String url = getURL(path);
    if (url.length() + content.length() < URL_SIZE_LIMIT) {
      return get(url + "?" + content, null, sign);
    } else {
      return post(url, null, content, sign);
    }
  }

  protected static String join(Collection<String> s, String delimiter) {
    if (s.isEmpty()) return "";
    Iterator<String> iter = s.iterator();
    StringBuffer buffer = new StringBuffer(iter.next());
    while (iter.hasNext()) {
      buffer.append(delimiter).append(iter.next());
    }
    return buffer.toString();
  }

  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  protected static String stringify(Reader input) throws IOException {
    StringWriter output = new StringWriter();
    char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    long count = 0;
    int n = 0;
    while (-1 != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return output.toString();
  }

  protected static List<NameValuePair> transform_params(Map<String, String> params) {
    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
    if (params != null) {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
      }
    }
    return qparams;
  }

  protected static JSON jsonize(Object json) {
    if (json instanceof String) {
      try {
        json = JSON.parse((String) json);
      } catch (ParseException e) {
        throw new FreebaseException("Error during parsing, make sure it's a valid JSON object: " + e.getMessage());
      }
    }
    if (json instanceof JSON) {
      JSON j = (JSON) json;
      if (!j.isContainer()) {
        throw new FreebaseException("Top level JSON object an object or an array");
      }
    }
    return (JSON) json;
  }

  private JSON check_result(JSON result) {

    String status = result.get("status").string();
    String code = result.get("code").string();

    if (!"200 OK".equals(status) || !"/api/status/ok".equals(code)) {

      String message = code;
      if (!"200 OK".equals(status)) {
        message = "HTTP error: " + status;
      } else {
        JSON firstMessage = result.get("messages").get(0);
        if (firstMessage.has("message")) {
          message = code + ": " + firstMessage.get("message").string();
        }
      }

      throw new FreebaseException(message, result);
    }

    return result;
  }

}
