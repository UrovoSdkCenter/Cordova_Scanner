# Cordova Plugin Urovo Scanner
 Cordova Plugin Urovo Device Scanner

## Supported Platforms

- Android

## Installation

```bash
cordova plugin add [local_path]\cordova-plugin-urovo-scanner
```

## Usage

### register event

```js
const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
urovo.register(event);
```

### unregister event

```js
urovo.unregister();
```

### scan

```js
urovo.scan();
```

### cancel

stopScan

```js
urovo.cancel();
```

### setScanKey

setScanKey , keycode example:520-521-

```js
urovo.setScanKey(
		keycode,
		function(suc){
			showInfo("====function suc====");
		},
		function(err){
			showInfo("====function err====" + err);
		}
	);