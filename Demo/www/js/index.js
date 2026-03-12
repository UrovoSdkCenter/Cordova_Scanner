/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

// Wait for the deviceready event before using any of Cordova's device APIs.
// See https://cordova.apache.org/docs/en/latest/cordova/events/events.html#deviceready

document.addEventListener('deviceready', onDeviceReady, false);

function onDeviceReady() {
    // Cordova is now initialized. Have fun!

    console.log('Running cordova-' + cordova.platformId + '@' + cordova.version);
    //document.getElementById('deviceready').classList.add('ready');
	 document.getElementById("bt_scan").addEventListener("click", scan);
	 document.getElementById("bt_cancel").addEventListener("click", cancel);
	 document.getElementById("bt_setScanKey").addEventListener("click", setScanKey);

	 //Register Event BroadcastReceiver
	 const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
	 urovo.register(function(event){
         var action = event['action'];
         var info =JSON.stringify(event);
         console.log("====event===="+ info);

         if(action=="android.intent.ACTION_DECODE_DATA"){//SCAN RESULT
            info ="SCAN RESULT"
                +"<br>"
                +"[barcode] : "+event['barcode']
                +"<br>"
                +"[barcodeType] : "+event['type'];
         }
         else if(action == "ACTION_KEYCODE_SCAN_PRESSED"){//KEY PRESS
                info = "KEY_PRESS"
                +"<br>"
                +"[keyAction] : "+event['keyAction']
                +"<br>"
                +"[keyCode] : "+event['keyCode']
                +"<br>"
                +"[pressTime] : "+event['pressTime'];
         }
         showInfo("====event====<br>" +info);
	 });

	 //call Unregister Event
     //    urovo.unregister();

}

function setScanKey(){
	showInfo("setScanKey.");
	var key = document.getElementById("input_keycode").value;
	if(key==""){
		showInfo("pls input keycode");
		return;
	}
	const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
	urovo.setScanKey(
		key,
		function(suc){
			showInfo("====function suc====");
		},
		function(err){
			showInfo("====function err====" + err);
		}
	);
}

function scan(){
	showInfo("waiting for scan.");
	const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
	urovo.scan(
		function(suc){
			showInfo("====function suc====" + JSON.stringify(suc));
		},
		function(err){
			showInfo("====function err====" + err);
		}
	);
}

function cancel(){
    const urovo = window.cordova.plugins.CordovaPluginUrovoScanner;
	urovo.cancel();
}

function showInfo(msg){
	if(msg){
		document.getElementById("info").innerHTML=msg;
	}
}