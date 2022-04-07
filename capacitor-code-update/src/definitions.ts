/// <reference types="@capacitor/cli" />
import type { Plugin } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core/types/definitions';

declare module '@capacitor/cli' {
  export interface PluginConfig {
    CapacitorCodeUpdate: {
      serverUrl: string,
      applicationId: string
    };
  }
}

export interface CapacitorCodeUpdatePlugin extends Plugin {
  checkUpdate(): Promise<Update | null>;

  download(option: Update): Promise<LocalPackage | null>;

  install(option: { installMode: InstallMode }): Promise<InstallResult>;

  addListener(eventName: 'downloadProgress', listenerFunc: (percent: string) => void): Promise<PluginListenerHandle>;
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
