package site.chenwei.codeupdate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CapacitorCodeUpdate {
    private final Context context;
    private final CapacitorCodeUpdatePlugin plugin;
    private final SharedPreferences prefs;
    private final SharedPreferences prefsOfUpdate;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences.Editor editorOfUpdate;
    private static final String LATEST_VERSION_PATH = "latestVersionPath";
    private static final String LATEST_VERSION_ZIP_PATH = "latestVersionZipPath";
    private static final String CAP_SERVER_PATH = "serverBasePath";
    private static final String INSTALL_ON_RESUME = "inStallOnResume";
    private static final String INSTALL_ON_RESTART = "inStallOnReStart";
    private static final String VERSION_FOLDER = "versions";
    private static final String ZIP_FILE_NAME = "dist.zip";
    private static final String UN_ZIP_FILE_FOLDER = "public";
    private static final String PATCH_FILE_FOLDER = "patch";
    private static final String PATCH_FILE_NAME = "patched.zip";
    private static final String UPDATE_TYPE_INCREMENTAL = "INCREMENTAL_UPDATE";
    private static final String INDEX_FILE_NAME = "index.html";
    private static final String UPDATE_TYPE_FULL = "FULL_UPDATE";
    private static final String GUID_NAME = "code_update_guid";
    private static final List<String> IGNORE_FILES = Arrays.asList("__MACOSX", ".idea", ".git");
    public static String GUID = "";
    public static String APP_VERSION = "";
    private String applicationId = "";
    private static final String APPLICATION_ID_NAME = "applicationId";
    private String applicationVersionId = "";
    private static final String APPLICATION_VERSION_ID_NAME = "applicationVersionId";
    private String serverUrl = "";
    private String SERVER_URL_NAME = "serverUrl";

    public CapacitorCodeUpdate(Context context, CapacitorCodeUpdatePlugin plugin) {
        this.context = context;
        this.plugin = plugin;
        this.prefs = context.getSharedPreferences("CapWebViewSettings", Activity.MODE_PRIVATE);
        this.prefsOfUpdate = context.getSharedPreferences("CapacitorUpdateSettings", Activity.MODE_PRIVATE);
        this.editor = prefs.edit();
        this.editorOfUpdate = prefsOfUpdate.edit();
        initGuid();
        getVersionName();
        initUpdateSettings();
    }

    private void initUpdateSettings() {
        this.applicationId = this.plugin.getConfig().getString(APPLICATION_ID_NAME, "");
        this.applicationVersionId = this.prefsOfUpdate.getString(APPLICATION_VERSION_ID_NAME, "");
        this.serverUrl = this.plugin.getConfig().getString(SERVER_URL_NAME, "");
    }

    private void initGuid() {
        GUID = Settings.Secure.getString(this.context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    private void getVersionName() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            APP_VERSION = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public JSObject checkUpdate() {
        try {
            this.editorOfUpdate.putString(SERVER_URL_NAME, serverUrl);
            Map<String, String> params = new HashMap<>();
            params.put("applicationId", applicationId);
            params.put("signature", getSignature());
            String url = CodeUpdateNetWorker.buildUrl(serverUrl, CodeUpdateNetWorker.CHECK_UPDATE, params);
            JSONObject jsonObject = CodeUpdateNetWorker.get(url);
            return JSObject.fromJSONObject(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSignature() {
        String latestVersionZipPath = this.prefs.getString(LATEST_VERSION_ZIP_PATH, "");
        if ("".equals(latestVersionZipPath)) {
            return "";
        }
        try {
            return DigestUtils.md5Hex(new FileInputStream(latestVersionZipPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public JSObject download(JSObject data) {
        try {
            String downloadUrl = data.getString("downloadUrl");
            String version = data.getString("id");
            String updateType = data.getString("updateType");
            String signature = data.getString("signature");
            String fileFolder = this.context.getFilesDir() + File.separator + VERSION_FOLDER + File.separator + version;
            String zipFilePath = fileFolder + File.separator + ZIP_FILE_NAME;
            String unZipFilePath = fileFolder + File.separator + UN_ZIP_FILE_FOLDER;
            if (!downloadFile(downloadUrl, zipFilePath)) {
                return null;
            }
            if (UPDATE_TYPE_INCREMENTAL.equals(updateType)) {
                String latestVersionZipPath = this.prefs.getString(LATEST_VERSION_ZIP_PATH, "");
                File file = new File(latestVersionZipPath);
                if (file.exists()) {
                    String patchedFileFolder = this.context.getFilesDir() + File.separator + VERSION_FOLDER + File.separator + version + File.separator + PATCH_FILE_FOLDER;
                    new File(patchedFileFolder).mkdirs();
                    String patchedFilePath = patchedFileFolder + File.separator + PATCH_FILE_NAME;
                    File file1 = new File(patchedFilePath);
                    if (file1.exists()) {
                        file1.delete();
                    }
                    file1.createNewFile();
                    BSPatch.patch(latestVersionZipPath, patchedFilePath, zipFilePath);
                    zipFilePath = patchedFilePath;
                }
            }
            if (!signatureCheck(signature, zipFilePath)) {
                return null;
            }
            if (!unZipFile(zipFilePath, unZipFilePath)) {
                return null;
            }
            unZipFilePath = findIndex(new File(unZipFilePath));
            if (StringUtils.isBlank(unZipFilePath)) {
                return null;
            }
            this.editor.putString(LATEST_VERSION_PATH, unZipFilePath);
            this.editor.putString(LATEST_VERSION_ZIP_PATH, zipFilePath);
            JSObject jsObject = new JSObject();
            jsObject.put("version", version);
            jsObject.put("applicationId", data.getString("applicationId"));
            jsObject.put("versionName", data.getString("name"));
            jsObject.put("signature", signature);
            jsObject.put("updateType", updateType);
            this.editorOfUpdate.putString(APPLICATION_ID_NAME, data.getString("applicationId"));
            this.editorOfUpdate.putString(APPLICATION_VERSION_ID_NAME, version);
            return jsObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean downloadFile(String url, String destPath) {
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            InputStream is = u.openStream();
            DataInputStream dis = new DataInputStream(is);
            long totalLength = uc.getContentLength();
            int buffLength = 1024;
            byte[] buffer = new byte[buffLength];
            int length;
            File downFile = new File(destPath);
            downFile.getParentFile().mkdirs();
            if (downFile.exists()) {
                downFile.delete();
            }
            downFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(downFile);
            int readedLength = buffLength;
            int percent = 0;
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                int newPercent = (int) (90 / (readedLength * 100) / totalLength);
                if (totalLength > 1 && newPercent != percent) {
                    percent = newPercent;
                    this.plugin.notifyDownload(String.valueOf(percent));
                    Log.i(CapacitorCodeUpdate.class.getCanonicalName(), "已下载" + percent + "%");
                }
                readedLength += length;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean unZipFile(String zipFilePath, String destPath) {
        try {
            File targetDirectory = new File(destPath);
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry zipEntry;
            String szName = "";
            if (!targetDirectory.exists()) {
                targetDirectory.mkdir();
            }
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(destPath + File.separator + szName);
                    folder.mkdirs();
                } else {
                    Log.i("tag", destPath + File.separator + szName);
                    File file = new File(destPath + File.separator + szName);
                    if (!file.exists() && file.getParentFile() != null) {
                        Log.i("tag", "Create the file:" + destPath + File.separator + szName);
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[2048];
                    while ((len = inZip.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean signatureCheck(String signature, String zipFilePath) {
        try {
            String s = DigestUtils.md5Hex(new FileInputStream(zipFilePath));
            return StringUtils.equals(s, signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String findIndex(File file) {
        List<File> fileList = new ArrayList<>();
        if (!file.exists()) {
            return "";
        }
        if (file.isFile()) {
            if (StringUtils.equals(INDEX_FILE_NAME, file.getName())) {
                return file.getParent();
            }
            return "";
        }
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (IGNORE_FILES.contains(file1.getName())) {
                continue;
            }
            if (file1.isDirectory()) {
                fileList.add(file1);
                continue;
            }
            String index = findIndex(file1);
            if (StringUtils.isNoneBlank(index)) {
                return index;
            }
        }
        for (File file1 : fileList) {
            String index = findIndex(file1);
            if (StringUtils.isNoneBlank(index)) {
                return index;
            }
        }
        return "";
    }

    public JSObject install(JSObject data) {
        Integer installModeInteger = data.getInteger("installMode");
        int installMode = installModeInteger == null ? 4 : installModeInteger;
        JSObject jsObject = new JSObject();
        jsObject.put("success", false);
        String latestVersionPath = this.prefs.getString(LATEST_VERSION_PATH, "");
        String latestVersionZipPath = this.prefs.getString(LATEST_VERSION_ZIP_PATH, "");
        if (StringUtils.isNoneBlank(latestVersionPath, latestVersionZipPath)) {
            syncStatus("1");
            this.editor.putString(CAP_SERVER_PATH, latestVersionPath);
            this.editor.commit();
            this.editorOfUpdate.commit();
            switch (installMode) {
                case 0:
                    this.plugin.getBridge().reload();
                    syncStatus("2");
                    break;
                case 2:
                    this.editor.putString(INSTALL_ON_RESTART, "1");
                    this.editor.commit();
                    break;
                default:
                    this.editor.putString(INSTALL_ON_RESUME, "1");
                    this.editor.commit();
                    break;
            }
        }
        return jsObject;
    }

    public void onResume() {
        String installOnResume = this.prefs.getString(INSTALL_ON_RESUME, "");
        if (StringUtils.equals(installOnResume, "1")) {
            String latestVersionPath = this.prefs.getString(LATEST_VERSION_PATH, "");
            this.editor.remove(INSTALL_ON_RESUME);
            this.editor.commit();
            this.plugin.getBridge().setServerBasePath(latestVersionPath);
            this.plugin.getBridge().reload();
            syncStatus("2");
        }
    }

    public void onRestart() {
        String installOnResume = this.prefs.getString(INSTALL_ON_RESUME, "");
        String installOnRestart = this.prefs.getString(INSTALL_ON_RESTART, "");
        if (StringUtils.equals(installOnResume, "1") || StringUtils.equals(installOnRestart, "1")) {
            String latestVersionPath = this.prefs.getString(LATEST_VERSION_PATH, "");
            this.editor.remove(INSTALL_ON_RESUME);
            this.editor.remove(INSTALL_ON_RESTART);
            this.editor.commit();
            this.plugin.getBridge().setServerBasePath(latestVersionPath);
            this.plugin.getBridge().reload();
            syncStatus("2");
        }
    }

    private void syncStatus(String type) {
        new Thread() {
            @Override
            public void run() {
                try {
                    initUpdateSettings();
                    HashMap<String, String> params = new HashMap<>();
                    params.put(APPLICATION_ID_NAME, applicationId);
                    params.put(APPLICATION_VERSION_ID_NAME, applicationVersionId);
                    params.put("updateStatus", type);
                    String url = CodeUpdateNetWorker.buildUrl(serverUrl, CodeUpdateNetWorker.SYNC_STATUS, params);
                    JSONObject jsonObject = CodeUpdateNetWorker.get(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
