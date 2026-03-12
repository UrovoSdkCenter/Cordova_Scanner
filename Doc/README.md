# Cordova Plugin UROVO Scanner

Cordova plugin for UROVO barcode scanner, 提供一维/二维条码扫描功能。

## 平台支持

- Android

## 功能特性

- **条码扫描**: 支持1D/2D条码识别
- **扫描控制**: 启动/停止扫描操作
- **扫描键配置**: 自定义扫描触发按键
- **事件监听**: 扫描结果与按键事件回调
- **自动初始化**: 插件自动管理扫描器生命周期

## 安装

```bash
cordova plugin add path/to/cordova-plugin-urovo-scanner
```

## 快速开始

### 1. 引入插件

```javascript
const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
```

### 2. 注册扫描事件

```javascript
// 注册事件监听
urovo.register(function(result) {
    const data = JSON.parse(result);
    
    if (data.action === 'android.intent.ACTION_DECODE_DATA') {
        // 扫描结果
        console.log('Barcode:', data.barcode);
        console.log('Length:', data.type);  // 条码长度
    } else if (data.action === 'ACTION_KEYCODE_SCAN_PRESSED') {
        // 扫描按键事件
        console.log('Key Action:', data.keyAction);  // 'down' 或 'up'
        console.log('Key Code:', data.keyCode);
        console.log('Press Time:', data.pressTime);
    }
});
```

### 3. 执行扫描

```javascript
// 开始扫描
urovo.scan();

// 停止扫描
urovo.cancel();
```

### 4. 注销事件

```javascript
// 不再使用时注销事件
urovo.unregister();
```

## API文档

### register(callback)

注册扫描事件监听器。

**参数:**
- `callback`: 回调函数,接收扫描结果和按键事件

**回调数据格式:**

扫描结果事件:
```json
{
    "action": "android.intent.ACTION_DECODE_DATA",
    "barcode": "1234567890",    // 条码内容
    "type": 13                  // 条码长度
}
```

按键事件:
```json
{
    "action": "ACTION_KEYCODE_SCAN_PRESSED",
    "keyAction": "down",        // "down" - 按下, "up" - 抬起
    "keyCode": 520,             // 按键键值
    "pressTime": 1234567890     // 按下时间戳(ms)
}
```

**示例:**
```javascript
urovo.register(function(result) {
    const data = JSON.parse(result);
    if (data.action === 'android.intent.ACTION_DECODE_DATA') {
        alert('Scanned: ' + data.barcode + ', Length: ' + data.type);
    }
});
```

### unregister()

注销扫描事件监听器。

**示例:**
```javascript
urovo.unregister();
```

### scan()

启动扫描操作。触发扫描头出光进行条码识别。

**示例:**
```javascript
urovo.scan();
```

### cancel()

停止当前扫描操作。

**示例:**
```javascript
urovo.cancel();
```

### setScanKey(keycode, success, error)

配置扫描触发按键。设置哪些按键可以触发扫描头出光。

**参数:**
- `keycode`: 按键键值字符串,格式为 "key1-key2-key3-" (注意末尾的横杠)
- `success`: 成功回调
- `error`: 失败回调

**常用键值:**
- `520` - 侧边扫描键1
- `521` - 侧边扫描键2
- `522` - 顶部扫描键
- `523` - 其他扫描键

**示例:**
```javascript
// 设置520和521键可触发扫描
urovo.setScanKey(
    "520-521-",
    function(success) {
        console.log("按键设置成功");
    },
    function(error) {
        console.error("按键设置失败:", error);
    }
);

// 禁用所有扫描键
urovo.setScanKey(
    "0-",
    function() { console.log("扫描键已禁用"); },
    function(err) { console.error(err); }
);
```

## 完整使用示例

```javascript
// 页面初始化
document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
    
    // 配置扫描按键
    urovo.setScanKey("520-521-", 
        function() {
            console.log("Scan key configured");
        }, 
        function(err) {
            console.error("Set scan key failed:", err);
        }
    );
    
    // 注册扫描事件
    urovo.register(handleScanResult);
    
    // 添加按钮事件
    document.getElementById('scanBtn').addEventListener('click', function() {
        urovo.scan();
    });
    
    document.getElementById('stopBtn').addEventListener('click', function() {
        urovo.cancel();
    });
}

function handleScanResult(result) {
    const data = JSON.parse(result);
    
    switch(data.action) {
        case 'android.intent.ACTION_DECODE_DATA':
            // 显示扫描结果
            document.getElementById('result').innerText = 
                'Barcode: ' + data.barcode + ', Length: ' + data.type;
            break;
            
        case 'ACTION_KEYCODE_SCAN_PRESSED':
            // 处理按键事件
            if (data.keyAction === 'down') {
                console.log('Scan key pressed:', data.keyCode);
                // 按键按下时自动触发扫描
                urovo.scan();
            } else {
                console.log('Scan key released:', data.keyCode);
            }
            break;
    }
}

// 页面卸载时注销
document.addEventListener('pause', function() {
    const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
    urovo.unregister();
}, false);
```

## 工作原理

### 初始化流程

插件在`pluginInitialize`时自动执行以下操作:

1. 创建 `ScanManager` 实例
2. 检查扫描器状态,如未开启则调用`openScanner()`
3. 设置输出模式为 `0` (广播模式)

### 扫描流程

1. 调用 `register()` 注册广播接收器,监听两个广播:
   - `android.intent.ACTION_DECODE_DATA` (扫描结果)
   - `ACTION_KEYCODE_SCAN_PRESSED` (按键事件)
2. 调用 `scan()` 触发 `scanManager.startDecode()`
3. 扫描成功后,Android系统发送广播
4. `UrovoBroadcastReceiver` 接收广播并构造JSON数据
5. 通过KeepCallback回调机制将数据传递到JavaScript层

### 广播数据解析

**ACTION_DECODE_DATA 广播包含:**
- `barcode` (byte[]): 条码原始字节数据
- `barcode_string` (String): 条码字符串
- `barcodeType` (byte): 条码类型标识
- `length` (int): 条码长度

**插件JSON输出:**
- `action`: 广播Action名称
- `barcode`: 条码字符串内容 (从`barcode_string`获取)
- `type`: 条码长度 (从`length`获取)

## 注意事项

1. **自动初始化**: 插件会自动初始化扫描器,无需手动开启
2. **事件注册**: 使用扫描功能前必须先调用 `register()` 注册事件
3. **资源释放**: 页面暂停或退出时应调用 `unregister()` 释放资源
4. **按键配置**: `setScanKey()` 的键值字符串必须以 `-` 结尾
5. **广播模式**: 插件使用广播模式(mode 0),与RFID功能互不冲突
6. **单次扫描**: 每次 `scan()` 只触发一次扫描,扫描成功后自动停止
7. **KeepCallback**: register方法使用KeepCallback,可持续接收多次回调
8. **JSON解析**: 回调数据为JSON字符串,需使用`JSON.parse()`解析

## 支持的条码类型

插件支持UROVO设备支持的所有1D和2D条码类型,包括但不限于:

**1D条码:**
- Code 128
- Code 39
- Code 93  
- EAN-8/13
- UPC-A/E
- Codabar
- Interleaved 2 of 5
- Industrial 2 of 5

**2D条码:**
- QR Code
- Data Matrix
- PDF417
- Aztec Code
- MaxiCode

具体支持的条码类型取决于设备硬件配置。

## 故障排查

### 扫描无响应
- 检查是否已调用 `register()` 注册事件
- 确认设备扫描头是否正常工作
- 检查扫描键配置是否正确
- 确认`getScannerState()`返回true

### 无法接收扫描结果
- 确认回调函数是否正确处理JSON数据
- 检查广播接收器是否已注销(`unregister()`)
- 查看Android日志确认广播是否发送
- 确认action字段值是否匹配

### setScanKey 设置无效
- 确认键值格式是否正确(必须以 `-` 结尾)
- 检查键值是否为设备支持的扫描键(如520、521等)
- 确认success/error回调是否被触发

## 版本要求

- **Android**: API 19+ (Android 4.4+)
- **Cordova**: 7.0+
- **依赖SDK**: android.device.ScanManager (platform_sdk_v4.1.0326.jar)

## 技术支持

如需技术支持,请联系UROVO技术支持团队。

## License

Copyright © UROVO Technology Co., Ltd.
