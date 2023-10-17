package com.etrak.shared_library.scale_service

import com.etrak.core.mc_service.McService

class ScaleService : McService(emulator = ScaleEmulator())