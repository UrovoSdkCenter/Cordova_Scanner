cordova.define("com.urovoscanner.CordovaPluginUrovoScanner", function(require, exports, module) {
var exec = cordova.require("cordova/exec");

module.exports = {
    register: function (successCallback) {
        exec(successCallback, null, 'CordovaPluginUrovoScanner', 'register', []);
    },
    unregister: function () {
            exec(null, null, 'CordovaPluginUrovoScanner', 'unregister', []);
        },
    scan: function () {
        exec(null, null, 'CordovaPluginUrovoScanner', 'scan', []);
    },
    cancel: function () {
        exec(null, null, 'CordovaPluginUrovoScanner', 'cancel', []);
    },
    setScanKey: function (args,suc,err) {
        exec(suc, err, 'CordovaPluginUrovoScanner', 'setScanKey', [args]);
    }
};
});
