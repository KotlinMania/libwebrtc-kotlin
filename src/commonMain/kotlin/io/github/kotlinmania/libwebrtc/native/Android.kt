// port-lint: source native/android.rs
// Copyright 2025 LiveKit, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.github.kotlinmania.libwebrtc.native

public fun initializeAndroid(vm: Any?) {
    val jvmHandle = (vm as? Number)?.toLong() ?: (vm?.hashCode() ?: 1).toLong().let { if (it == 0L) 1L else it }
    WebrtcNativeBridge.initAndroid(jvmHandle)
}
