package com.huan.edu.tvplayer.http.request;

import android.os.Handler;
import android.text.TextUtils;

import com.huan.edu.tvplayer.http.request.callback.HttpCallBack;
import com.huan.edu.tvplayer.http.util.DebugUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;



/**
 * Created by linlongxin on 2015/12/26.
 */
public class HttpConnection {

    public static final int NO_NETWORK = 999;
    private static HttpConnection instance;
    private HttpURLConnection urlConnection;
    private Handler handler = new Handler();
    private Map<String, String> header;
    private String logUrl;

    private HttpConnection() {
    }

    protected static HttpConnection getInstance() {
        if (instance == null) {
            synchronized (HttpConnection.class) {
                if (instance == null)
                    instance = new HttpConnection();
            }
        }
        return instance;
    }

    public enum RequestType {
        GET("GET"), POST("POST");
        private String requestType;

        RequestType(String type) {
            this.requestType = type;
        }
    }

    /**
     * 设置请求头header
     *
     * @param header 请求头内容
     */
    protected void setHttpHeader(Map<String, String> header) {
        this.header = header;
    }

    /**
     * 网络请求
     *
     * @param type     请求方式{POST,GET}
     * @param param    请求的参数，HashMap键值对的形式
     * @param callback 请求返回的回调
     */
    protected synchronized void quest(String url, RequestType type, Map<String, String> param, final HttpCallBack callback) {

        logUrl = url;
        final int respondCode;
        //打印请求日志
        final int requestTime = DebugUtils.requestLog(logUrl);   //打印log，请求的参数，地址
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(30 * 1000);
            urlConnection.setReadTimeout(30 * 1000);
            urlConnection.setRequestMethod(String.valueOf(type));

            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            //POST请求参数：因为POST请求的参数在写在流里面
            if (type.equals(RequestType.POST)) {
                String s = "";
                String key;
                String value;
                if (param != null) {
                    for (Map.Entry<String, String> map : param.entrySet()) {
                        key = TextUtils.isEmpty(map.getKey()) ? "" : URLEncoder.encode(map.getKey(), "UTF-8");
                        value = TextUtils.isEmpty(map.getValue()) ? "" : URLEncoder.encode(map.getValue(), "UTF-8");
                        s += "&" + key + "=" + value;
                    }
                }
                OutputStream ops = urlConnection.getOutputStream();
                ops.write(s.getBytes());
                ops.flush();
                ops.close();

                logUrl += s;
            }

            //对HttpURLConnection对象的一切配置都必须要在connect()函数执行之前完成。
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            respondCode = urlConnection.getResponseCode();

            //请求失败
            if (respondCode != HttpURLConnection.HTTP_OK) {
                in = urlConnection.getErrorStream();
                final int finalRespondCode = respondCode;
                final String info = readInputStream(in);
                in.close();
                //回调：错误信息返回主线程
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.failure(finalRespondCode, info);
                            callback.getRequestTimes(respondCode, info, requestTime);
                        }
                    }
                });
                return;
            } else {
                final String result = readInputStream(in);
                in.close();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.success(result);
                            callback.getRequestTimes(respondCode, result, requestTime);
                        }
                    }
                });
            }

        } catch (final IOException e1) {
            e1.printStackTrace();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.failure(NO_NETWORK, "抛出异常,没有连接网络");
                        callback.getRequestTimes("抛出异常：" + e1.getMessage(), requestTime);
                    }
                }
            });
        }
    }

    /**
     * 读取输入流信息，转化成String
     *
     * @param in
     * @return
     */
    private String readInputStream(InputStream in) {
        String result = "";
        String line;
        if (in != null) {
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));
            try {
                while ((line = bin.readLine()) != null) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


}
