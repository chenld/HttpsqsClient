package org.httpsqs.client.ex;

import org.httpsqs.client.Base64;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class HttpsqsClientEx {
  private String _server;  //������IP��ַ
  private int _port;  //�������˿ں�
  private String _charset;  //HTTP�����ַ���
  private int _connectTimeout = 0;  //���ӳ�ʱ
  private int _readTimeout = 0;  //����ʱ

  private HttpClient _client = null;

  public static final String HTTPSQS_ERROR_PREFIX = "HTTPSQS_ERROR";

  /**
   * ����HTTP Sqs Client
   *
   * @param server         ������IP��ַ
   * @param port           �������˿ں�
   * @param charset        HTTP�����ַ���
   * @param connectTimeout ���ӳ�ʱ
   * @param readTimeout    ����ʱ
   */
  public HttpsqsClientEx(String server, int port, String charset, int connectTimeout, int readTimeout) {
    this._server = server;
    this._port = port;
    this._charset = charset;
    this._connectTimeout = connectTimeout;
    this._readTimeout = readTimeout;
    _client = new HttpClient();
  }

  public void open() {
    try {
      _client.openConnection();
    } catch (IOException e) {
      //
    }
  }

  public void close() {
    _client.closeConnection();
  }

  /**
   * ����ָ�����е�����������
   *
   * @param queue_name ������
   * @param num        ��������
   * @param user       �û���
   * @param pass       ����
   * @return �ɹ�: ����"HTTPSQS_MAXQUEUE_OK"
   *         <br>����: "HTTPSQS_MAXQUEUE_CANCEL"-����û�гɹ�
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String maxqueue(String queue_name, long num, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=maxqueue&num=" + num;
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }
//synctime

  /**
   * �޸Ķ�ʱˢ���ڴ滺�������ݵ����̵ļ��ʱ��
   *
   * @param queue_name ������
   * @param num        ���ʱ��(��)
   * @param user       �û���
   * @param pass       ����
   * @return �ɹ�: ����"HTTPSQS_SYNCTIME_OK"
   *         <br>����: "HTTPSQS_SYNCTIME_CANCEL"-����û�гɹ�
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String synctime(String queue_name, int num, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=synctime&num=" + num;
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �ֶ�ˢ���ڴ滺�������ݵ�����
   *
   * @param queue_name ������
   * @param user       �û���
   * @param pass       ����
   * @return �ɹ�: ����"HTTPSQS_FLUSH_OK"
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String flush(String queue_name, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=flush";
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ����ָ������
   *
   * @param queue_name ������
   * @param user       �û���
   * @param pass       ����
   * @return �ɹ�: ����"HTTPSQS_RESET_OK"
   *         <br>����: "HTTPSQS_RESET_ERROR"-����û�гɹ�
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String reset(String queue_name, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=reset";
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �鿴����״̬
   *
   * @param queue_name ������
   * @return �ɹ�: ���ض�����Ϣ
   *         <br>����: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String status(String queue_name) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=status";
      String result = _client.sendRequest(urlstr, null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ��JSO��ʽ,�鿴����״̬
   *
   * @param queue_name ������
   * @return �ɹ�: {"name":"������","maxqueue":��������,"putpos":����д���ֵ,"putlap":����д���ֵȦ��,"getpos":���л�ȡ��ֵ,"getlap":���л�ȡ��ֵȦ��,"unread":δ����Ϣ��}
   *         <br>����: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String statusJson(String queue_name) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=status_json";
      String result = _client.sendRequest(urlstr, null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �鿴ָ������λ�õ������
   *
   * @param queue_name ������
   * @param pos        λ��
   * @param auth       Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: ����ָ��λ�õĶ�������,���󷵻���"HTTPSQS_ERROR"��ͷ���ַ���
   *         <br>����: "HTTPSQS_ERROR_NOFOUND"-ָ������Ϣ������
   *         <br>��֤����: "HTTPSQS_AUTH_FAILED"
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String view(String queue_name, String pos, String auth) {
    try {
      StringBuilder urlstr = new StringBuilder("/?charset=" + this._charset + "&name=" + URLEncoder.encode(queue_name, _charset) + "&opt=view&pos=" + pos);
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, _charset));
      }

      String result = _client.sendRequest(urlstr.toString(), null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * ������
   *
   * @param queue_name ������
   * @param auth       Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: �����е���Ϣ����
   *         <br>����: "HTTPSQS_GET_END"-����Ϊ��
   *         <br>��֤����: "HTTPSQS_AUTH_FAILED"
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String get(String queue_name, String auth) {
    try {
      StringBuilder urlstr = new StringBuilder("/?charset=" + this._charset + "&name=" + URLEncoder.encode(queue_name, _charset) + "&opt=get");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, _charset));
      }

      String result = _client.sendRequest(urlstr.toString(), null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * �����
   *
   * @param queue_name ������
   * @param data       ����е���Ϣ����
   * @param auth       Sqs4j��get,put,view����֤����,������Ҫ��֤ʱ,����Ϊnull
   * @return �ɹ�: �����ַ���"HTTPSQS_PUT_OK"
   *         <br>����: "HTTPSQS_PUT_ERROR"-����д���; "HTTPSQS_PUT_END"-��������
   *         <br>��֤����: "HTTPSQS_AUTH_FAILED"
   *         <br>��������: ������"HTTPSQS_ERROR"��ͷ���ַ���
   */
  public String put(String queue_name, String data, String auth) {
    try {
      StringBuilder urlstr = new StringBuilder("/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=put");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, _charset));
      }

      String result = _client.sendRequest(urlstr.toString(), data, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * A replacement for java.net.URLConnection.
   */
  private class HttpClient {
    private String _hostAndPort;

    private Socket _socket = null;
    private BufferedWriter _writer;
    private InputStream _input;
    private byte[] _readBbuffer;
    private boolean _keepalive;


    public HttpClient() {
      _hostAndPort = _port == 80 ? _server : _server + ":" + _port;
    }

    protected void openConnection() throws IOException {
      if (_input == null) {
        InetSocketAddress socketAddress = new InetSocketAddress(_server, _port);
        _socket = new Socket();
        try {
          _socket.setSoTimeout(_readTimeout);
          _socket.setReuseAddress(true);
          _socket.setTcpNoDelay(true);
          _socket.setSoLinger(true, 0);
        } catch (Throwable thex) {
          //
        }

        _socket.connect(socketAddress, _connectTimeout);
        _writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream(), _charset));
        _input = _socket.getInputStream();
      }
    }

    protected void closeConnection() {
      if (_input != null) {
        try {
          _input.close();
        } catch (IOException ex1) {
        }
        _input = null;
      }

      if (_writer != null) {
        try {
          _writer.close();
        } catch (IOException ex) {
        }
        _writer = null;
      }

      if (_socket != null) {
        try {
          _socket.close();
        } catch (Exception ignore) {
        }
        _socket = null;
      }
    }

    /**
     * ��HTTP Header���ҵ��ַ�������,û�з��ַ���null
     *
     * @param contentType
     * @return
     */
    private String getCharsetFromContentType(String contentType) {
      if (contentType == null) {
        return null;
      }
      int start = contentType.indexOf("charset=");
      if (start < 0) {
        return null;
      }
      String encoding = contentType.substring(start + 8);
      int end = encoding.indexOf(';');
      if (end >= 0) {
        encoding = encoding.substring(0, end);
      }
      encoding = encoding.trim();
      if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\""))) {
        encoding = encoding.substring(1, encoding.length() - 1);
      }
      return (encoding.trim());
    }

    private static final String CR_NL = "\r\n";

    /**
     * ����HTTP���󲢶�ȡ���������ص�����,�����GET����Ͱ�request����Ϊnullֵ,�������ҪBASIC��֤,��user�Լ�pass����Ϊnullֵ
     *
     * @param pathAndQuery
     * @param request
     * @param user         �û���
     * @param pass         ����
     * @return �������ķ�����Ϣ
     */
    public String sendRequest(String pathAndQuery, String request, String user, String pass) throws IOException {
      this.openConnection();
      try {
        if (request != null) {
          _writer.write("POST " + pathAndQuery + " HTTP/1.1" + CR_NL);
        } else {
          _writer.write("GET " + pathAndQuery + " HTTP/1.1" + CR_NL);
        }

        _writer.write("User-Agent: " + "wstone" + CR_NL);
        _writer.write("Host: " + _hostAndPort + CR_NL);

        _writer.write("Connection: Keep-Alive" + CR_NL);
        _writer.write("Content-Type: text/plan;charset=" + _charset + CR_NL);
        if (user != null && pass != null) {
          _writer.write("Authorization: Basic " + new String(Base64.encodeBytes((user + ":" + pass).getBytes(_charset))) + CR_NL);  //��ҪBASIC��֤
        }

        if (request != null) {
          _writer.write("Content-Length: " + request.getBytes(_charset).length);  //����2��Ϊ�˼��϶���Ļس�����
        }

        _writer.write(CR_NL + CR_NL);
        if (request != null) {
          _writer.write(request);
        }
        _writer.flush();

        // start reading  server response headers
        String line = readLine(_input, _charset);
        int contentLength = -1;
        StringTokenizer tokens = new StringTokenizer(line);
        String httpversion = tokens.nextToken();
        String statusCode = tokens.nextToken();
        String statusMsg = tokens.nextToken("\n\r");
        _keepalive = "HTTP/1.1".equals(httpversion);
        if (!"200".equals(statusCode)) {
          throw new IOException("Unexpected Response from Server: " + statusMsg);
        }

        do {
          line = readLine(_input, _charset);
          if (line != null) {
            line = line.toLowerCase();
            if (line.startsWith("content-length:")) {
              contentLength = Integer.parseInt(line.substring(15).trim());
            }
            if (line.startsWith("connection:")) {
              _keepalive = line.indexOf("keep-alive") > -1;
            }
            if (line.startsWith("content-type")) {
              _charset = getCharsetFromContentType(line);
            }
          }
        } while (line != null && !line.equals(""));

        InputStream bodyInputStream;
        if (contentLength > 0) {
          bodyInputStream = new ContentLengthInputStream(_input, contentLength);
        } else {
          bodyInputStream = new ChunkedInputStream(_input);
        }

        BufferedReader bodyReader = new BufferedReader(new InputStreamReader(bodyInputStream, _charset));
        StringBuilder result = new StringBuilder();
        int i = 0;
        while ((line = bodyReader.readLine()) != null) {
          i++;
          if (i != 1) {
            result.append("\n");
          }
          result.append(line);
        }

        if (!_keepalive) {
          this.closeConnection();
        }

        return result.toString();
      } catch (IOException ioex) {
        this.closeConnection();
        throw ioex;
      }
    }

    /**
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
      closeConnection();
    }

    private static final int defaultByteBufferSize = 8192;

    private String readLine(InputStream input, String charset) throws IOException {
      if (_readBbuffer == null) {
        _readBbuffer = new byte[defaultByteBufferSize];
      }
      int next;
      int count = 0;
      while (true) {
        next = input.read();
        if (next < 0 || next == '\n') {
          break;
        }
        if (next != '\r') {
          _readBbuffer[count++] = (byte) next;
        }
        if (count >= _readBbuffer.length) {
          throw new IOException("HTTP Line too long, more than:" + _readBbuffer.length);
        }
      }
      return new String(_readBbuffer, 0, count, charset);
    }

  } //<-end HttpClient


}
