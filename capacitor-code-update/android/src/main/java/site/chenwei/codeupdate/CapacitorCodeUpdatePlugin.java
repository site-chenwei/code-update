package site.chenwei.codeupdate;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CapacitorCodeUpdate")
public class CapacitorCodeUpdatePlugin extends Plugin {

    private CapacitorCodeUpdate implementation;
    private static final String DOWNLOAD_PROGRESS_LISTENER = "downloadProgress";

    @Override
    public void load() {
        super.load();
        this.implementation = new CapacitorCodeUpdate(getContext(), this);
    }

    @PluginMethod
    public void checkUpdate(PluginCall call) {
        call.resolve(implementation.checkUpdate());
    }

    @PluginMethod
    public void download(PluginCall call) {
        JSObject data = call.getData();
        call.resolve(implementation.download(data));
    }

    @PluginMethod
    public void install(PluginCall call) {
        JSObject data = call.getData();
        call.resolve(implementation.install(data));
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        this.implementation.onResume();
    }

    @Override
    protected void handleOnRestart() {
        super.handleOnRestart();
        this.implementation.onRestart();
    }

    public void notifyDownload(String percent) {
        JSObject jsObject = new JSObject();
        jsObject.put("percent", percent);
        notifyListeners(DOWNLOAD_PROGRESS_LISTENER, jsObject);
    }
}
