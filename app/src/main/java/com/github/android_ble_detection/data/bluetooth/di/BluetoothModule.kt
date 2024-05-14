package com.github.android_ble_detection.data.bluetooth.di

import android.content.Context
import com.github.android_ble_detection.data.bluetooth.BluetoothLEController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object BluetoothModule {

    @Provides
    @ViewModelScoped
    fun provideBleController(@ApplicationContext context: Context): BluetoothLEController =
        BluetoothLEController(context)


}