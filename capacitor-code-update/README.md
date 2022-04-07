# capacitor-code-update

Capacitor-Code-Update

## Install

```bash
npm install capacitor-code-update
npx cap sync
```

## API

<docgen-index>

* [`checkUpdate()`](#checkupdate)
* [`download(...)`](#download)
* [`install(...)`](#install)
* [`addListener('downloadProgress', ...)`](#addlistenerdownloadprogress)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkUpdate()

```typescript
checkUpdate() => Promise<Update | null>
```

**Returns:** <code>Promise&lt;<a href="#update">Update</a> | null&gt;</code>

--------------------


### download(...)

```typescript
download(option: Update) => Promise<LocalPackage | null>
```

| Param        | Type                                      |
| ------------ | ----------------------------------------- |
| **`option`** | <code><a href="#update">Update</a></code> |

**Returns:** <code>Promise&lt;<a href="#localpackage">LocalPackage</a> | null&gt;</code>

--------------------


### install(...)

```typescript
install(option: { installMode: InstallMode; }) => Promise<InstallResult>
```

| Param        | Type                                                                  |
| ------------ | --------------------------------------------------------------------- |
| **`option`** | <code>{ installMode: <a href="#installmode">InstallMode</a>; }</code> |

**Returns:** <code>Promise&lt;<a href="#installresult">InstallResult</a>&gt;</code>

--------------------


### addListener('downloadProgress', ...)

```typescript
addListener(eventName: 'downloadProgress', listenerFunc: (percent: string) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                      |
| ------------------ | ----------------------------------------- |
| **`eventName`**    | <code>'downloadProgress'</code>           |
| **`listenerFunc`** | <code>(percent: string) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### Interfaces


#### Update

| Prop              | Type                                               |
| ----------------- | -------------------------------------------------- |
| **`id`**          | <code>string</code>                                |
| **`name`**        | <code>string</code>                                |
| **`signature`**   | <code>string</code>                                |
| **`downloadUrl`** | <code>string</code>                                |
| **`updateType`**  | <code>'FULL_UPDATE' \| 'INCREMENTAL_UPDATE'</code> |


#### LocalPackage

| Prop                | Type                |
| ------------------- | ------------------- |
| **`version`**       | <code>string</code> |
| **`applicationId`** | <code>string</code> |
| **`versionName`**   | <code>string</code> |
| **`signature`**     | <code>string</code> |
| **`updateType`**    | <code>string</code> |


#### InstallResult

| Prop          | Type                 |
| ------------- | -------------------- |
| **`success`** | <code>boolean</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Enums


#### InstallMode

| Members            |
| ------------------ |
| **`IMMEDIATE`**    |
| **`NEXT_RESUME`**  |
| **`NEXT_RESTART`** |

</docgen-api>
