package com.etrak.shared_library.di

import android.content.Context
import com.etrak.core.mc_service.McManager
import com.etrak.core.shutdown_service.ShutdownManager
import com.etrak.core.shutdown_service.ShutdownService
import com.etrak.shared_library.scale_service.Scale
import com.etrak.shared_library.scale_service.ScaleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideShutdownManager(
        @ApplicationContext context: Context
    ) = ShutdownManager(context).apply {
        start(ShutdownService.DEFAULT_DURATION)
    }

    @Provides
    @Singleton
    fun provideMcManager(@ApplicationContext context: Context) = McManager(
        context = context,
        service = ScaleService::class.java
    ).apply {
        start()
    }

    @Provides
    fun scale(mcManager: McManager) = Scale(mcManager)
}