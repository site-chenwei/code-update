package site.chenwei.codeupdate;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CodeUpdateNetWorker {
    public static final String CHECK_UPDATE = "/application/version/check";
    public static final String SYNC_STATUS = "/application/version/status/sync";
    private static final int CONNECTION_TIME_OUT_SECONDS = 5;
    private static final int READ_TIME_OUT_SECONDS = 10;
    private static String baseUrl = "";
    private static final String SUCCESS = "S000000";

    public static JSONObject get(String urlStr) throws Exception {
        if (urlStr == null || "".equals(urlStr)) {
            return null;
        }
        URL url = new URL(urlStr);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(CONNECTION_TIME_OUT_SECONDS * 1000);
        urlConnection.setReadTimeout(READ_TIME_OUT_SECONDS * 1000);
        urlConnection.connect();
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            String read = read(inputStream);
            JSONObject jsonObject = new JSONObject(read);
            String code = (String) jsonObject.get("code");
            if (SUCCESS.equals(code)) {
                return (JSONObject) jsonObject.get("data");
            }
        }
        return null;
    }

    public static String buildUrl(String serverUrl, String uri, Map<String, String> params) {
        addPublicParams(params);
        if (serverUrl == null || "".equals(serverUrl)) {
            if ("".equals(baseUrl)) {
                return "";
            }
            serverUrl = baseUrl;
        } else {
            baseUrl = serverUrl;
        }
        if (serverUrl.lastIndexOf("/") != serverUrl.length() - 1) {
            serverUrl = serverUrl + "/";
        }
        if (uri.indexOf("/") == 0) {
            uri = uri.substring(1);
        }
        serverUrl = serverUrl + uri;
        StringBuilder sb = new StringBuilder();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            if (params.get(key) == null) {
                continue;
            }
            sb = sb.append(key).append("=").append(URLEncoder.encode(params.get(key))).append("&");
        }
        String paramsStr = sb.substring(0, sb.length() - 1);
        return serverUrl + "?" + paramsStr;
    }


    //从流中读取数据
    private static String read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return new String(outStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void addPublicParams(Map<String, String> params) {
        params.put("phoneOsType", "ANDROID");
        params.put("phoneVersion", android.os.Build.VERSION.RELEASE);
        params.put("phoneType", android.os.Build.BRAND);
        params.put("serialId", CapacitorCodeUpdate.GUID);
        params.put("versionName", CapacitorCodeUpdate.APP_VERSION);
    }
}
