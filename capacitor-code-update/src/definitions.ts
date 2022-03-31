/// <reference types="@capacitor/cli" />
import type { Plugin } from '@capacitor/core';

declare module '@capacitor/cli' {
  export interface PluginConfig {
    CapacitorCodeUpdate: {
      serverUrl: string,
      applicationId: string
    };
  }
}
export const LISTENER_DOWNLOAD_PROGRESS = 'downloadProgress';

export interface CapacitorCodeUpdatePlugin extends Plugin {
  checkUpdate(): Promise<Update | null>;

  download(option: Update): Promise<LocalPackage | null>;

  install(option: { installMode: InstallMode }): Promise<InstallResult>;
}

export enum InstallMode {
  IMMEDIATE,
  NEXT_RESUME,
  NEXT_RESTART
}

export interface CodeUpdateBaseOption {
  applicationId: string,
  serverUrl: string
}

export interface LocalPackage {
  version: string;
  applicationId: string;
  versionName: string;
  signature: string;
  updateType: string;
}

export interface InstallResult {
  success: boolean;
}

export interface Update extends CodeUpdateBaseOption {
  id: string,
  name: string,
  signature: string,
  downloadUrl: string,
  updateType: 'FULL_UPDATE' | 'INCREMENTAL_UPDATE',
}
