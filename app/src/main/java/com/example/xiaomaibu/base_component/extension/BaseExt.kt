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

package com.william.base_component.extension

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * @author William
 * @date 2020/4/18 20:52
 * Class Comment：Custom extension functions and extension properties.
 */

val Int.dp
    get() = this.toFloat().dp.toInt()

val Float.dp
    get() = getTypedValue(TypedValue.COMPLEX_UNIT_DIP, this)

val Float.sp
    get() = getTypedValue(TypedValue.COMPLEX_UNIT_SP, this)

private fun getTypedValue(unit: Int, value: Float): Float {
    return TypedValue.applyDimension(unit, value, Resources.getSystem().displayMetrics)
}

inline fun <reified T : ViewBinding> AppCompatActivity.bindingView(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        val viewBindClass = T::class.java
        val method = viewBindClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return@lazy method.invoke(null, layoutInflater) as T
    }

inline fun <reified T : ViewBinding> Fragment.bindingView(): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        val viewBindClass = T::class.java
        val method = viewBindClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return@lazy method.invoke(null, layoutInflater) as T
    }
