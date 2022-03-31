import { WebPlugin } from '@capacitor/core';

import type { CapacitorCodeUpdatePlugin, InstallResult, LocalPackage, Update, InstallMode } from './definitions';

export class CapacitorCodeUpdateWeb extends WebPlugin implements CapacitorCodeUpdatePlugin {
  checkUpdate(): Promise<Update | null> {
    return Promise.resolve(null);
  }

  download(option: Update): Promise<LocalPackage | null> {
    console.log('Code_Update_WEB_Download:' + option);
    return Promise.resolve(null);
  }

  install(option: { installMode: InstallMode }): Promise<InstallResult> {
    console.log('Code_Update_WEB_Install:' + option);
    return Promise.resolve({ success: false });
  }


}
