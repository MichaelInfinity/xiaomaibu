/*
 * Copyright WeiLianYang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.william.base_component.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Outline
import android.net.Uri
import android.os.Build
import android.os.Process
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.View
import android.view.ViewOutlineProvider
import com.example.xiaomaibu.userInfo_Activity
import com.william.base_component.extension.dp
import com.william.base_component.extension.logD
import com.william.base_component.extension.logE
import java.util.*

fun Uri?.isNotEmpty(): Boolean {
    return this != null && this.toString().isNotEmpty()
}

