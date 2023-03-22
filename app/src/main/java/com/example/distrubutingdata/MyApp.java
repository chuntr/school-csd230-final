package com.example.distrubutingdata;

import android.app.Application;

import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;

public class MyApp extends Application implements CameraXConfig.Provider {

    @Override
    public CameraXConfig getCameraXConfig() {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
                .setCameraExecutor(myExecutor)
                .setSchedulerHandler(mySchedulerHandler)
                .build();
    }
}
