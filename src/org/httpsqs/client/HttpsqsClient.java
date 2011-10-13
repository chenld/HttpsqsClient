package org.httpsqs.client;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class HttpsqsClient {
  private String server; //������IP��ַ
  private int port; //�������˿ں�
  private String charset; //HTTP�����ַ���
  private int connectTimeout = 0; //���ӳ�ʱ
  private int readTimeout = 0; //����ʱ

  public static final String HTTPSQS_ERROR_PREFIX = "HTTPSQS_ERROR"; //Sqs4J��������ǰ׺

  /**
   * ����HTTP Sqs Client
   * 
   * @param server
   *          ������IP��ַ
   * @param port
   *          �������˿ں�
   * @param charset
   *          HTTP�����ַ���
   * @param connectTimeout
   *          ���ӳ�ʱ
   * @param readTimeout
   *          ����ʱ
   */
  public HttpsqsClient(String server, int port, String charset, int connectTimeout, int readTimeout) {
    this.server = server;
    this.port = port;
    this.charset = charset;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  /**
   * ����HTTP��GET����,�������ҪBASIC��֤,��user�Լ�pass����Ϊnullֵ
   * 
   * @param urlstr
   *          �����URL
   * @param user
   *          �û���
   * @param pass
   *          ����
   * @return �������ķ�����Ϣ
   */
  private String doGetProcess(String urlstr, String user, String pass) {
    URL url = null;
    try {
      url = new URL(urlstr);
    } catch (MalformedURLException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    }

    BufferedReader reader = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(false);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);
      if (user != null && pass != null) {
        conn.setRequestProperty("Authorization", "Basic "
            + new String(Base64.encodeBytes((user + ":" + pass).getBytes(charset)))); //��ҪBASIC��֤
      }

      conn.connect();

      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      String line;
      StringBuilder result = new StringBuilder();

      int i = 0;
      while ((line = reader.readLine()) != null) {
        i++;
        if (i != 1) {
          result.append("\n");
        }
        result.append(line);
      }
      return result.toString();
    } catch (IOException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * ����HTTP��GET����,�������ҪBASIC��֤,��user�Լ�pass����Ϊnullֵ
   * 
   * @param urlstr
   *          �����URL
   * @param user
   *          �û���
   * @param pass
   *          ����
   * @return �������ķ�����Ϣ
   */
  private SqsMsg doGetProcessEx(String urlstr, String user, String pass) {
    URL url = null;
    try {
      url = new URL(urlstr);
    } catch (MalformedURLException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    }

    BufferedReader reader = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(false);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);
      if (user != null && pass != null) {
        conn.setRequestProperty("Authorization", "Basic "
            + new String(Base64.encodeBytes((user + ":" + pass).getBytes(charset)))); //��ҪBASIC��֤
      }

      conn.connect();

      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      String line;
      StringBuilder result = new StringBuilder();

      int i = 0;
      while ((line = reader.readLine()) != null) {
        i++;
        if (i != 1) {
          result.append("\n");
        }
        result.append(line);
      }

      long getPos = -1;
      try {
        getPos = Long.parseLong(conn.getHeaderField("Pos"));
      } catch (Throwable e) {
        getPos = -1;
      }

      return new SqsMsg(getPos, result.toString());
    } catch (IOException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * ����ָ�����е�����������
   * 
   * @param queue_name
   *          ������
   * @param num
   *          ��������
   * @param user
   *          �û���
   * @param pass
   *          ����
   * @return �ɹ�: ����"HTTPSQS_MAXQUEUE_OK" <br>
   *         ����: "HTTPSQS_MAXQUEUE_CANCEL"-����û�гɹ� <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String maxqueue(String queue_name, long num, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=maxqueue&num=" + num;
      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  //synctime

  /**
   * �޸Ķ�ʱˢ���ڴ滺�������ݵ����̵ļ��ʱ��
   * 
   * @param queue_name
   *          ������
   * @param num
   *          ���ʱ��(��)
   * @param user
   *          �û���
   * @param pass
   *          ����
   * @return �ɹ�: ����"HTTPSQS_SYNCTIME_OK" <br>
   *         ����: "HTTPSQS_SYNCTIME_CANCEL"-����û�гɹ� <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String synctime(String queue_name, int num, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=synctime&num=" + num;
      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �ֶ�ˢ���ڴ滺�������ݵ�����
   * 
   * @param queue_name
   *          ������
   * @param user
   *          �û���
   * @param pass
   *          ����
   * @return �ɹ�: ����"HTTPSQS_FLUSH_OK" <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String flush(String queue_name, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=flush";
      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ����ָ������
   * 
   * @param queue_name
   *          ������
   * @param user
   *          �û���
   * @param pass
   *          ����
   * @return �ɹ�: ����"HTTPSQS_RESET_OK" <br>
   *         ����: "HTTPSQS_RESET_ERROR"-����û�гɹ� <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String reset(String queue_name, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=reset";

      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �鿴����״̬
   * 
   * @param queue_name
   *          ������
   * @return �ɹ�: ���ض�����Ϣ <br>
   *         ����: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String status(String queue_name) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=status";

      result = this.doGetProcess(urlstr, null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ��JSO��ʽ,�鿴����״̬
   * 
   * @param queue_name
   *          ������
   * @return �ɹ�:
   *         {"name":"������","maxqueue":��������,"putpos":����д���ֵ,"putlap":����д���ֵȦ��
   *         ,"getpos":���л�ȡ��ֵ,"getlap":���л�ȡ��ֵȦ��,"unread":δ����Ϣ��} <br>
   *         ����: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String statusJson(String queue_name) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=status_json";

      result = this.doGetProcess(urlstr, null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �鿴ָ������λ�õ������
   * 
   * @param queue_name
   *          ������
   * @param pos
   *          λ��
   * @param auth
   *          Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: ����ָ��λ�õĶ�������,���󷵻���"HTTPSQS_ERROR"��ͷ���ַ��� <br>
   *         ����: "HTTPSQS_ERROR_NOFOUND"-ָ������Ϣ������ <br>
   *         ��֤����: "HTTPSQS_AUTH_FAILED" <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String view(String queue_name, long pos, String auth) {
    String result = null;
    try {
      StringBuilder urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?charset=" + this.charset
          + "&name=" + URLEncoder.encode(queue_name, charset) + "&opt=view&pos=" + pos);
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      result = this.doGetProcess(urlstr.toString(), null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ������
   * 
   * @param queue_name
   *          ������
   * @param auth
   *          Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: �����е���Ϣ���� <br>
   *         ����: "HTTPSQS_GET_END"-����Ϊ�� <br>
   *         ��֤����: "HTTPSQS_AUTH_FAILED" <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String get(String queue_name, String auth) {
    String result = null;
    try {
      StringBuilder urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?charset=" + this.charset
          + "&name=" + URLEncoder.encode(queue_name, charset) + "&opt=get");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      result = this.doGetProcess(urlstr.toString(), null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ������
   * 
   * @param queue_name
   *          ������
   * @param auth
   *          Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: �����е���Ϣ���� <br>
   *         ����: "HTTPSQS_GET_END"-����Ϊ�� <br>
   *         ��֤����: "HTTPSQS_AUTH_FAILED" <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public SqsMsg getEx(String queue_name, String auth) {
    SqsMsg result = null;
    try {
      StringBuilder urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?charset=" + this.charset
          + "&name=" + URLEncoder.encode(queue_name, charset) + "&opt=get");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      result = this.doGetProcessEx(urlstr.toString(), null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage());
    }
  }

  /**
   * �����
   * 
   * @param queue_name
   *          ������
   * @param data
   *          ����е���Ϣ����
   * @param auth
   *          Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: �����ַ���"HTTPSQS_PUT_OK" <br>
   *         ����: "HTTPSQS_PUT_ERROR"-����д���; "HTTPSQS_PUT_END"-�������� <br>
   *         ��֤����: "HTTPSQS_AUTH_FAILED" <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String put(String queue_name, String data, String auth) {
    StringBuilder urlstr;
    URL url;
    try {
      urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?name="
          + URLEncoder.encode(queue_name, charset) + "&opt=put");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      url = new URL(urlstr.toString());
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    } catch (MalformedURLException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    }
    URLConnection conn;

    OutputStreamWriter writer = null;
    try {
      conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);

      //conn.setRequestProperty("Authorization","Basic "+ new String(Base64.encodeBytes((user+":"+pass).getBytes(charset))));  //��ҪBASIC��֤�Ŀ��Լ���

      conn.connect();

      writer = new OutputStreamWriter(conn.getOutputStream(), charset);
      writer.write(URLEncoder.encode(data, charset));
      writer.flush();
    } catch (IOException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ex) {
        }
      }
    }

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      return reader.readLine();
    } catch (IOException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * �����
   * 
   * @param queue_name
   *          ������
   * @param data
   *          ����е���Ϣ����
   * @param auth
   *          Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: �����ַ���"HTTPSQS_PUT_OK" <br>
   *         ����: "HTTPSQS_PUT_ERROR"-����д���; "HTTPSQS_PUT_END"-�������� <br>
   *         ��֤����: "HTTPSQS_AUTH_FAILED" <br>
   *         ��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public SqsMsg putEx(String queue_name, String data, String auth) {
    StringBuilder urlstr;
    URL url;
    try {
      urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?name="
          + URLEncoder.encode(queue_name, charset) + "&opt=put");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      url = new URL(urlstr.toString());
    } catch (UnsupportedEncodingException ex) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage());
    } catch (MalformedURLException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    }
    URLConnection conn;

    OutputStreamWriter writer = null;
    try {
      conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);

      //conn.setRequestProperty("Authorization","Basic "+ new String(Base64.encodeBytes((user+":"+pass).getBytes(charset))));  //��ҪBASIC��֤�Ŀ��Լ���

      conn.connect();

      writer = new OutputStreamWriter(conn.getOutputStream(), charset);
      writer.write(URLEncoder.encode(data, charset));
      writer.flush();
    } catch (IOException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ex) {
        }
      }
    }

    long putPos = -1;
    try {
      putPos = Long.parseLong(conn.getHeaderField("Pos"));
    } catch (Throwable e) {
      putPos = -1;
    }

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      return new SqsMsg(putPos, reader.readLine());
    } catch (IOException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }
}