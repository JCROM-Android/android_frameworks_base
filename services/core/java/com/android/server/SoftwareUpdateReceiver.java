/*
 * Copyright (C) 2013 JCROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import java.io.File;

import java.io.IOException;

public class SoftwareUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "SoftwareUpdate";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // The reboot call is blocking, so we need to do it on another thread.
        Thread thr = new Thread("Reboot") {
            @Override
            public void run() {
                try {
					File UpdateFile = new File("/cache/jcrom.zip");
                    File GappsFile = new File("/cache/gapps.zip");
                    SystemProperties.set("sys.shutdown.user.requested", "true");
                    RecoverySystem.installPackage(context, UpdateFile, GappsFile);
                    Log.wtf(TAG, "Still running after software update?!");
                } catch (IOException e) {
                    Slog.e(TAG, "Can't perform software update", e);
                }
            }
        };
        thr.start();
    }
}

