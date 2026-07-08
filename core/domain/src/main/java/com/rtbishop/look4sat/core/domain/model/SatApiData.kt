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
package com.rtbishop.look4sat.core.domain.model

data class SatApiData(
    val version: Int = 1,
    val satName: String = "",
    val catNum: Int = 0,
    val azimuthDeg: Double = 0.0,
    val elevationDeg: Double = 0.0,
    val altitudeKm: Double = 0.0,
    val distanceKm: Double = 0.0,
    val subSatLatDeg: Double = 0.0,
    val subSatLonDeg: Double = 0.0,
    val aboveHorizon: Boolean = false,
    val txFrequencyHz: Long? = null,
    val rxFrequencyHz: Long? = null,
    val ctcssTxToneHz: Double? = null,
    val ctcsRxToneHz: Double? = null,
    val mode: String? = null,
    val aosTime: Long = 0L,
    val losTime: Long = 0L,
    val timestamp: Long = 0L
)
