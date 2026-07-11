/*
 * Look4Sat. Amateur radio satellite tracker and pass predictor.
 * Copyright (C) 2019-2026 Arty Bishop and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.rtbishop.look4sat.core.domain.usecase

import com.rtbishop.look4sat.core.domain.model.AudioSource
import kotlinx.coroutines.flow.Flow

interface IAudioCapture {
    val sampleRate: Int

    /** Emits FloatArray buffers continuously until the flow is canceled.
     *  [captureToken] is an opaque Android MediaProjection required for [AudioSource.Internal]. */
    fun audioFlow(source: AudioSource = AudioSource.Mic, captureToken: Any? = null): Flow<FloatArray>

    /** Called before the screen-capture consent dialog for [AudioSource.Internal].
     *  Android 14+ requires the foreground service to be running before createScreenCaptureIntent(). */
    fun prepareInternalCapture() {}

    /** Called when the user denies the consent dialog (so the foreground service doesn't leak). */
    fun cancelInternalCapture() {}
}
