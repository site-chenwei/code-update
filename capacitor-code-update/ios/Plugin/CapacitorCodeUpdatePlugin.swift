import Foundation
import Capacitor
import CommonCrypto
import SSZipArchive
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorCodeUpdatePlugin)
public class CapacitorCodeUpdatePlugin: CAPPlugin,URLSessionDownloadDelegate {
    private let CHECK_UPDATE="application/version/check"
    private var GUID: String = ""
    private var LATEST_VERSION_PATH: String = "latestVersionPath"
    private var LATEST_VERSION_ZIP_PATH = "latestVersionZipPath";
    private var CAP_SERVER_PATH = "serverBasePath";
    private var INSTALL_ON_RESUME = "inStallOnResume";
    private var INSTALL_ON_RESTART = "inStallOnReStart";
    private var VERSION_FOLDER = "versions";
    private var ZIP_FILE_NAME = "dist.zip";
    private var UN_ZIP_FILE_FOLDER = "public";
    private var PATCH_FILE_FOLDER = "patch";
    private var PATCH_FILE_NAME = "patched.zip";
    private var UPDATE_TYPE_INCREMENTAL = "INCREMENTAL_UPDATE";
    private var INDEX_FILE_NAME = "index.html";
    private var SERVER_URL = ""
    private var APPLICATION_ID=""
    private let URL_SESSION : URLSession = URLSession.shared
    private var downloadCall:CAPPluginCall? = nil
    private var version = ""
    private var updateType = ""
    private var singature = ""
    private var latestVersionPath = ""
    private var latestVersionZipPath = ""
    private var applicationId = ""
    private var versionName = ""
    
    public override func load() {
        if let uuid = UIDevice.current.identifierForVendor{
            GUID = uuid.uuidString
        }
        if var serverUrl = getConfigValue("serverUrl") as? String{
            if(!serverUrl.hasSuffix("/")){
                serverUrl = serverUrl + "/"
            }
            SERVER_URL=serverUrl
        }
        if let applicationId = getConfigValue("applicationId") as? String{
            APPLICATION_ID = applicationId
        }
        if UserDefaults.standard.string(forKey: INSTALL_ON_RESTART) != nil{
            UserDefaults.standard.removeObject(forKey: LATEST_VERSION_PATH)
            if let latestVersionPath=UserDefaults.standard.string(forKey: LATEST_VERSION_PATH){
                reload(latestVersionPath: latestVersionPath)
            }
        }
    }
    
    
    private func buildUrl(apiUrl: String, params: inout [String:String]) -> String{
        params.updateValue("IOS", forKey: "phoneOsType")
        params.updateValue(UIDevice.current.systemVersion, forKey: "phoneVersion")
        params.updateValue("APPLE", forKey: "phoneType")
        params.updateValue(GUID, forKey: "serialId")
        params.updateValue(Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "", forKey: "versionName")
        let serverUrl=SERVER_URL + apiUrl
        var query : String = "?"
        print(params)
        for (key,value) in params{
            query=query + key + "=" + value + "&"
        }
        print(query)
        query.removeLast()
        return serverUrl+query
    }
    public func fileMD5(_ path: String) -> String? {
        let handle = FileHandle(forReadingAtPath: path)
        if handle == nil {
            return nil
        }
        let ctx = UnsafeMutablePointer<CC_MD5_CTX>.allocate(capacity: MemoryLayout<CC_MD5_CTX>.size)
        CC_MD5_Init(ctx)
        var done = false
        while !done {
            let fileData = handle?.readData(ofLength: 256)
            fileData?.withUnsafeBytes {(bytes: UnsafePointer<CChar>)->Void in
                CC_MD5_Update(ctx, bytes, CC_LONG(fileData!.count))
            }
            if fileData?.count == 0 {
                done = true
            }
        }
        //unsigned char digest[CC_MD5_DIGEST_LENGTH];
        let digestLen = Int(CC_MD5_DIGEST_LENGTH)
        let digest = UnsafeMutablePointer<CUnsignedChar>.allocate(capacity: digestLen)
        CC_MD5_Final(digest, ctx);
        
        var hash = ""
        for i in 0..<digestLen {
            hash +=  String(format: "%02x", (digest[i]))
        }
        digest.deinitialize(count: 0)
        ctx.deinitialize(count: 0)
        return hash;

    }
    private func getLastZipPath() -> String{
        if let  zipPath1 = UserDefaults.standard.string(forKey: LATEST_VERSION_ZIP_PATH){
            if let libPath = NSSearchPathForDirectoriesInDomains(.libraryDirectory, .userDomainMask, true).first {
               let  zipPath = URL(fileURLWithPath: libPath, isDirectory: true)
                    .appendingPathComponent("NoCloud")
                    .appendingPathComponent("ionic_built_snapshots")
                    .appendingPathComponent(URL(fileURLWithPath: zipPath1, isDirectory: true).lastPathComponent).path
                return zipPath
            }
        }
        return ""
    }
    private func getSingature(path: String) -> String{
        var zipPath: String = path
        if (path == ""){
            zipPath = getLastZipPath()
        }
        if("" == zipPath){
            return ""
        }
        return fileMD5(zipPath) ?? ""
    }
    
    @objc func download(_ call: CAPPluginCall){
        if(downloadCall != nil){
            call.reject("已有正在处理中的下载操作")
        }
        let downloadUrl = call.getString("downloadUrl") ?? ""
        let version = call.getString("id") ?? ""
        let updateType = call.getString("updateType") ?? ""
        let signature = call.getString("signature") ?? ""
        if(downloadUrl == "" || version == "" || updateType == "" || signature == "" ){
            call.reject("不正确的下载请求")
        }
        self.version = version
        self.updateType = updateType
        self.singature = signature
        self.applicationId = call.getString("applicationId", "")
        self.versionName = call.getString("name","")
        downloadCall=call
        let url = URL(string: downloadUrl)!
        let session: URLSession = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: nil)
        let task = session.downloadTask(with: url)
        task.resume()
    }
    
    //下载代理方法，下载结束
    public func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,didFinishDownloadingTo location: URL) {
    
        //输出下载文件原来的存放目录
        print("location:\(location)")
        //location位置转换
        let locationPath = location.path
        //拷贝到用户目录
        let documentsURL = FileManager.default.urls(for: .libraryDirectory, in: .userDomainMask)[0]
        let libraryUrl = FileManager.default.urls(for: .libraryDirectory, in: .userDomainMask)[0].appendingPathComponent("NoCloud/ionic_built_snapshots")
        try? FileManager.default.createDirectory(atPath: libraryUrl.path, withIntermediateDirectories: true)
        let fileFloder = documentsURL.appendingPathComponent(VERSION_FOLDER).appendingPathComponent(version)
        var zipFilePath:String = fileFloder.appendingPathComponent(ZIP_FILE_NAME).path
        var unZipFilePath:String = fileFloder.appendingPathComponent(UN_ZIP_FILE_FOLDER).path
        let unZipFilePathPerist:String = libraryUrl.appendingPathComponent(self.version).path
        let ZipFilePathPerist:String = libraryUrl.appendingPathComponent(self.version+".zip").path
        let fileManager = FileManager.default
        try? fileManager.createDirectory(atPath: fileFloder.path, withIntermediateDirectories: true)
        try? fileManager.createDirectory(atPath: unZipFilePath, withIntermediateDirectories: true)
        if(fileManager.fileExists(atPath: unZipFilePathPerist)){
            try? fileManager.removeItem(atPath: unZipFilePathPerist)
        }
        if(fileManager.fileExists(atPath: zipFilePath)){
            try? fileManager.removeItem(atPath: zipFilePath)
        }
        try? fileManager.moveItem(atPath: locationPath, toPath: zipFilePath)
        if(self.updateType == "INCREMENTAL_UPDATE"){
            let zipPath = getLastZipPath()
            if(zipPath == ""){
                downloadCall?.reject("热更新失败")
                return
            }
            let patchedFolderPath = fileFloder.appendingPathComponent(PATCH_FILE_FOLDER)
            try? fileManager.createDirectory(atPath: patchedFolderPath.path, withIntermediateDirectories: true)
            let patchedFilePath=fileFloder.appendingPathComponent(PATCH_FILE_NAME).path
            if(fileManager.fileExists(atPath: patchedFilePath)){
               try? fileManager.removeItem(atPath: patchedFilePath)
            }
            fileManager.createFile(atPath: patchedFilePath, contents: nil)
            //BSPatch.beginPatch(zipPath, patchedFilePath, zipFilePath);
            BsPatch.patch(zipPath, newPath: patchedFilePath, patchPath: zipFilePath)
            zipFilePath = patchedFilePath
        }
        if (getSingature(path: zipFilePath) != self.singature){
            self.downloadCall?.reject("文件MD5验证失败")
            self.downloadCall = nil
            self.singature = ""
            self.updateType = ""
            self.version = ""
            return
        }
        SSZipArchive.unzipFile(atPath: zipFilePath, toDestination: unZipFilePath);
        unZipFilePath = findIndex(path: unZipFilePath)
        if(unZipFilePath == "" || !unZipFilePath.hasSuffix(INDEX_FILE_NAME)){
            self.downloadCall?.reject("更新资源文件不正确")
            self.downloadCall = nil
            self.singature = ""
            self.updateType = ""
            self.version = ""
            return
        }
        unZipFilePath = unZipFilePath.replacingOccurrences(of: INDEX_FILE_NAME, with: "", options: .literal, range: nil)
        try! fileManager.moveItem(atPath: unZipFilePath, toPath: unZipFilePathPerist)
        try! fileManager.moveItem(atPath: zipFilePath, toPath: ZipFilePathPerist)
        unZipFilePath = unZipFilePathPerist
        zipFilePath = ZipFilePathPerist
        self.latestVersionPath = unZipFilePath
        self.latestVersionZipPath = zipFilePath
        var result = [String:String]()
        result.updateValue(self.version, forKey: "version")
        result.updateValue(self.applicationId, forKey: "applicationId")
        result.updateValue(self.versionName, forKey: "versionName")
        result.updateValue(self.singature, forKey: "singature")
        result.updateValue(self.updateType, forKey: "updateType")
        self.downloadCall?.resolve(result)
    }
    
    private func findIndex(path:String) -> String{
        let fileManaGer = FileManager.default
        let contentArrays = try? fileManaGer.contentsOfDirectory(atPath: path)
        var  array = [String]()
        if let contents = contentArrays {
            for item in contents{
                if (item.hasSuffix(INDEX_FILE_NAME)){
                    return path + "/" + item
                }
                array.append( path + "/" + item)
            }
        }
        for item in array{
            let path = findIndex(path: item)
            if(path != "" && path.hasSuffix(INDEX_FILE_NAME)){
                return path
            }
        }
        return ""
    }
    //下载代理方法，监听下载进度
    public func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,
                        didWriteData bytesWritten: Int64, totalBytesWritten: Int64,
                        totalBytesExpectedToWrite: Int64) {
        //获取进度
        let written:CGFloat = (CGFloat)(totalBytesWritten)
        let total:CGFloat = (CGFloat)(totalBytesExpectedToWrite)
        let pro:CGFloat = 90/written/total
        self.notifyListeners("downloadProgress", data: ["percent" : pro])
    }
         
        //下载代理方法，下载偏移
    public func urlSession(_ session: URLSession, downloadTask: URLSessionDownloadTask,
        didResumeAtOffset fileOffset: Int64, expectedTotalBytes: Int64) {
        //下载偏移，主要用于暂停续传
    }
    
    
    
    @objc func checkUpdate(_ call: CAPPluginCall) {
        if(SERVER_URL == "" || APPLICATION_ID == ""){
            call.reject("未配置热更新服务器路径或应用ID")
        }
        print(SERVER_URL)
        var params=[String:String]()
        params.updateValue(APPLICATION_ID, forKey: "applicationId")
        params.updateValue(getSingature(path: ""), forKey: "signature")
        print(params)
        let serverUrl = buildUrl(apiUrl: CHECK_UPDATE, params: &params)
        print(serverUrl)
        let url : URL = URL(string: serverUrl)!
        let task = URL_SESSION.dataTask(with: url,completionHandler: { data, response, error in
            if let error = error{
                print(error)
                call.reject("检查更新失败")
                return
            }
            if let data = data {
                if let json = try? JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? NSDictionary{
                    if let jsonData = json["data"] as? [String:Any]{
                        call.resolve(jsonData)
                        return
                    }
                }
                
            }
            call.reject("检查更新失败")
        })
        task.resume()
    }
    @objc  func becomeActive(noti:Notification){
        if UserDefaults.standard.string(forKey: INSTALL_ON_RESUME) != nil{
            UserDefaults.standard.removeObject(forKey: INSTALL_ON_RESUME)
            if let path = UserDefaults.standard.string(forKey: LATEST_VERSION_PATH){
                reload(latestVersionPath: path)
            }
        }
    }
    @objc public func install(_ call: CAPPluginCall){
        let installMode = call.getInt("installMode") ?? 1
        UserDefaults.standard.set(latestVersionPath, forKey: LATEST_VERSION_PATH)
        UserDefaults.standard.set(latestVersionZipPath, forKey: LATEST_VERSION_ZIP_PATH)
        if( installMode == 2){
            UserDefaults.standard.set("1", forKey: INSTALL_ON_RESTART)
        }else if( installMode == 1){
            UserDefaults.standard.set("1", forKey: INSTALL_ON_RESUME)
            UserDefaults.standard.set(latestVersionPath, forKey: CAP_SERVER_PATH)
            NotificationCenter.default.addObserver(self, selector:#selector(becomeActive), name: UIApplication.didBecomeActiveNotification, object: nil)
        }
        var result = [String:Any]()
        result.updateValue(true, forKey: "success")
        call.resolve(result)
        UserDefaults.standard.synchronize()
        if(installMode == 0){
            reload(latestVersionPath: latestVersionPath)
        }
    
    }
    
    public func reload(latestVersionPath: String){
        UserDefaults.standard.set(latestVersionPath, forKey: CAP_SERVER_PATH)
        if let viewController = bridge?.viewController as? CAPBridgeViewController {
            viewController.setServerBasePath(path: latestVersionPath)
        }
    }
    
    
    
}
