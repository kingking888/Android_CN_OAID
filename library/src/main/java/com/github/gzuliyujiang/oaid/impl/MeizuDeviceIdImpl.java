/*
 * MIT License
 *
 * Copyright (c) 2020 贵州穿青人@李裕江 <1032694760@qq.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.gzuliyujiang.oaid.impl;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;

import java.util.Objects;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class MeizuDeviceIdImpl implements IDeviceId {
    private Context context;

    public MeizuDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        try {
            ProviderInfo pi = context.getPackageManager().resolveContentProvider("com.meizu.flyme.openidsdk", 0);
            if (pi != null) {
                return true;
            }
        } catch (Exception e) {
            Logger.print(e);
        }
        return false;
    }

    @Override
    public void doGet(@NonNull IGetter getter) {
        Uri uri = Uri.parse("content://com.meizu.flyme.openidsdk/");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, new String[]{"oaid"}, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            String ret = null;
            int valueIdx = cursor.getColumnIndex("value");
            if (valueIdx > 0) {
                ret = cursor.getString(valueIdx);
            }
            if (ret != null && ret.length() > 0) {
                getter.onDeviceIdGetComplete(ret);
            } else {
                getter.onDeviceIdGetError(new RuntimeException("OAID query failed"));
            }
        } catch (Exception e) {
            Logger.print(e);
            getter.onDeviceIdGetError(e);
        }
    }

}
