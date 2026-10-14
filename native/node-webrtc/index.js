const path = require('path');
let addon = null;
try {
    addon = require('./build/Release/webrtc_native.node');
} catch (e) {
    try {
        addon = require('./build/Debug/webrtc_native.node');
    } catch (e2) {
        throw new Error('Failed to load native webrtc_native.node addon: ' + e.message);
    }
}
module.exports = addon;
