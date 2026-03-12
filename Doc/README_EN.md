# Cordova Plugin UROVO Scanner

Cordova plugin for UROVO barcode scanner, providing 1D/2D barcode scanning functionality.

## Platform Support

- Android

## Features

- **Barcode Scanning**: Support for 1D/2D barcode recognition
- **Scan Control**: Start/stop scanning operations
- **Scan Key Configuration**: Customize scan trigger keys
- **Event Listening**: Scan result and key event callbacks
- **Auto Initialization**: Plugin automatically manages scanner lifecycle

## Installation

```bash
cordova plugin add path/to/cordova-plugin-urovo-scanner
```

## Quick Start

### 1. Import Plugin

```javascript
const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
```

### 2. Register Scan Events

```javascript
// Register event listener
urovo.register(function(result) {
    const data = JSON.parse(result);
    
    if (data.action === 'android.intent.ACTION_DECODE_DATA') {
        // Scan result
        console.log('Barcode:', data.barcode);
        console.log('Length:', data.type);  // Barcode length
    } else if (data.action === 'ACTION_KEYCODE_SCAN_PRESSED') {
        // Scan key event
        console.log('Key Action:', data.keyAction);  // 'down' or 'up'
        console.log('Key Code:', data.keyCode);
        console.log('Press Time:', data.pressTime);
    }
});
```

### 3. Start/Stop Scanning

```javascript
// Start scanning
urovo.startScan(
    function() {
        console.log('Scan started');
    },
    function(error) {
        console.error('Start scan error:', error);
    }
);

// Stop scanning
urovo.stopScan(
    function() {
        console.log('Scan stopped');
    },
    function(error) {
        console.error('Stop scan error:', error);
    }
);
```

## API Documentation

### register(successCallback)
Register scan event listener

```javascript
urovo.register(function(result) {
    const data = JSON.parse(result);
    console.log('Event:', data);
});
```

**Callback Data:**

**Scan Result Event:**
```json
{
    "action": "android.intent.ACTION_DECODE_DATA",
    "barcode": "1234567890",
    "type": 13
}
```

**Key Press Event:**
```json
{
    "action": "ACTION_KEYCODE_SCAN_PRESSED",
    "keyAction": "down",
    "keyCode": 520,
    "pressTime": 1234567890
}
```

**Properties:**
- `action`: Event type
  - `"android.intent.ACTION_DECODE_DATA"`: Scan result
  - `"ACTION_KEYCODE_SCAN_PRESSED"`: Scan key press
- `barcode`: Barcode content (scan result only)
- `type`: Barcode length in bytes (scan result only)
- `keyAction`: Key action (key event only)
  - `"down"`: Key pressed
  - `"up"`: Key released
- `keyCode`: Key code (key event only)
- `pressTime`: Press timestamp in ms (key event only)

**Note:** Uses KeepCallback mechanism - callback triggers for every event

### startScan(successCallback, errorCallback)
Start scanning

```javascript
urovo.startScan(
    function() {
        console.log('Started');
    },
    function(error) {
        console.error('Error:', error);
    }
);
```

**Description:** Initiates barcode scanning. Results returned via `register()` callback.

### stopScan(successCallback, errorCallback)
Stop scanning

```javascript
urovo.stopScan(
    function() {
        console.log('Stopped');
    },
    function(error) {
        console.error('Error:', error);
    }
);
```

### setScanKey(keyConfig, successCallback, errorCallback)
Configure scan trigger keys

```javascript
// Format: "keyCode-[enable|disable],keyCode-[enable|disable],..."

// Enable scan key 293
urovo.setScanKey(
    "293-enable",
    function() {
        console.log('Scan key 293 enabled');
    },
    function(err) {
        console.error(err);
    }
);

// Enable multiple keys
urovo.setScanKey(
    "293-enable,520-enable",
    function() {
        console.log('Multiple keys enabled');
    },
    function(err) {
        console.error(err);
    }
);

// Disable all scan keys
urovo.setScanKey(
    "0-",
    function() {
        console.log('All scan keys disabled');
    },
    function(err) {
        console.error(err);
    }
);
```

**Parameter Format:**
- Single key: `"keyCode-enable"` or `"keyCode-disable"`
- Multiple keys: `"293-enable,520-enable,280-disable"`
- Disable all: `"0-"`

**Common Key Codes:**
- `293`: Left scan key
- `520`: Right scan key  
- `280`: Side scan key
- Device-specific codes vary

## Complete Example

```javascript
document.addEventListener('deviceready', function() {
    const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
    
    // Register scan events
    urovo.register(function(result) {
        const data = JSON.parse(result);
        
        if (data.action === 'android.intent.ACTION_DECODE_DATA') {
            // Handle scan result
            displayResult(data.barcode, data.type);
        } else if (data.action === 'ACTION_KEYCODE_SCAN_PRESSED') {
            // Handle key event
            if (data.keyAction === 'down') {
                console.log('Scan key pressed');
                startScanning();
            } else if (data.keyAction === 'up') {
                console.log('Scan key released');
                stopScanning();
            }
        }
    });
    
    // Configure scan keys
    urovo.setScanKey(
        "293-enable,520-enable",
        function() {
            console.log('Scan keys configured');
        },
        function(error) {
            console.error('Config error:', error);
        }
    );
    
    function startScanning() {
        urovo.startScan(
            function() {
                console.log('Scanning...');
                document.getElementById('status').textContent = 'Scanning...';
            },
            function(error) {
                console.error('Start error:', error);
            }
        );
    }
    
    function stopScanning() {
        urovo.stopScan(
            function() {
                console.log('Stopped');
                document.getElementById('status').textContent = 'Ready';
            },
            function(error) {
                console.error('Stop error:', error);
            }
        );
    }
    
    function displayResult(barcode, length) {
        console.log('Scanned:', barcode);
        console.log('Length:', length, 'bytes');
        
        document.getElementById('result').textContent = barcode;
        document.getElementById('length').textContent = length + ' bytes';
    }
    
    // Manual scan button
    document.getElementById('scanBtn').addEventListener('click', function() {
        startScanning();
        
        // Auto-stop after 5 seconds
        setTimeout(function() {
            stopScanning();
        }, 5000);
    });
});
```

## HTML Example

```html
<!DOCTYPE html>
<html>
<head>
    <title>Scanner Demo</title>
</head>
<body>
    <h1>UROVO Scanner</h1>
    
    <div id="status">Ready</div>
    
    <button id="scanBtn">Start Scan</button>
    
    <div class="result-area">
        <h3>Scan Result:</h3>
        <div id="result">-</div>
        <div id="length">-</div>
    </div>
    
    <script type="text/javascript" src="cordova.js"></script>
    <script type="text/javascript">
        document.addEventListener('deviceready', function() {
            const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
            
            urovo.register(function(result) {
                const data = JSON.parse(result);
                
                if (data.action === 'android.intent.ACTION_DECODE_DATA') {
                    document.getElementById('result').textContent = data.barcode;
                    document.getElementById('length').textContent = data.type + ' bytes';
                }
            });
        });
    </script>
</body>
</html>
```

## Important Notes

1. **Auto Initialization**:
   - Plugin automatically initializes scanner on load
   - No manual init/release needed
   - Lifecycle managed by plugin

2. **Event Registration**:
   - Must call `register()` before scanning
   - Uses KeepCallback - callback fires for all events
   - Parse JSON result to get event data

3. **Data Format**:
   - `barcode`: Barcode content string
   - `type`: Barcode LENGTH in bytes (not barcode type!)
   - From Java: `BARCODE_STRING_TAG` and `BARCODE_LENGTH_TAG`

4. **Scan Keys**:
   - Hardware scan keys work automatically
   - Use `setScanKey()` to enable/disable keys
   - Key codes vary by device model

5. **Key Events**:
   - `keyAction`: "down" (pressed) or "up" (released)
   - `keyCode`: Integer key code
   - `pressTime`: Millisecond timestamp

6. **Multiple Events**:
   - Single callback handles both scan results and key events
   - Check `data.action` to differentiate event types

## Troubleshooting

### No Scan Results
- Verify `register()` was called
- Check callback function is correct
- Confirm scanner initialized (automatic)

### Hardware Keys Not Working
- Check key configuration with `setScanKey()`
- Verify correct key codes for device
- Some keys may be disabled by default

### Callback Not Firing
- Ensure `register()` called before scanning
- Check for JavaScript errors
- Verify result is valid JSON

## Requirements

- **Cordova**: 6.0+
- **Android**: 4.4+ (API 19+)
- **Dependencies**: android.device.ScanManager

## Technical Support

For technical support, please contact UROVO technical support team.

## License

Copyright © UROVO Technology Co., Ltd.
