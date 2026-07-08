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
package com.rtbishop.look4sat.core.data.framework

import com.rtbishop.look4sat.core.domain.model.SatApiData
import com.rtbishop.look4sat.core.domain.repository.ISatlib
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.net.Socket

class Satlib(private val serverScope: CoroutineScope) : ISatlib {

    @Volatile private var currentData: SatApiData = SatApiData()
    @Volatile private var serverSocket: ServerSocket? = null

    companion object {
        const val DEFAULT_PORT = 4534
    }

    override fun start(port: Int) {
        serverScope.launch(Dispatchers.IO) {
            try {
                val socket = ServerSocket(port)
                serverSocket = socket
                println("satlib: listening on port $port")
                while (isActive) {
                    val client = runCatching { socket.accept() }.getOrNull() ?: break
                    launch { handleClient(client) }
                }
            } catch (e: Exception) {
                println("satlib: stopped - ${e.message}")
            }
        }
    }

    override fun stop() {
        runCatching { serverSocket?.close() }
        serverSocket = null
    }

    override fun updateSatData(data: SatApiData) {
        currentData = data
    }

    private fun handleClient(socket: Socket) {
        try {
            // Drain the HTTP request line so the client doesn't hang
            socket.getInputStream().bufferedReader().readLine()
            val body = buildJson(currentData)
            val bodyBytes = body.toByteArray(Charsets.UTF_8)
            val response = buildString {
                append("HTTP/1.1 200 OK\r\n")
                append("Content-Type: application/json; charset=utf-8\r\n")
                append("Content-Length: ${bodyBytes.size}\r\n")
                append("Access-Control-Allow-Origin: *\r\n")
                append("Connection: close\r\n")
                append("\r\n")
            }
            socket.getOutputStream().apply {
                write(response.toByteArray(Charsets.UTF_8))
                write(bodyBytes)
                flush()
            }
        } catch (e: Exception) {
            println("satlib: client error - ${e.message}")
        } finally {
            runCatching { socket.close() }
        }
    }

    private fun buildJson(data: SatApiData): String = buildString {
        append('{')
        append("\"version\":${data.version},")
        append("\"satName\":\"${data.satName.jsonEscape()}\",")
        append("\"catNum\":${data.catNum},")
        append("\"azimuthDeg\":${data.azimuthDeg},")
        append("\"elevationDeg\":${data.elevationDeg},")
        append("\"altitudeKm\":${data.altitudeKm},")
        append("\"distanceKm\":${data.distanceKm},")
        append("\"subSatLatDeg\":${data.subSatLatDeg},")
        append("\"subSatLonDeg\":${data.subSatLonDeg},")
        append("\"aboveHorizon\":${data.aboveHorizon},")
        append("\"txFrequencyHz\":${data.txFrequencyHz ?: "null"},")
        append("\"rxFrequencyHz\":${data.rxFrequencyHz ?: "null"},")
        append("\"ctcssTxToneHz\":${data.ctcssTxToneHz ?: "null"},")
        append("\"ctcsRxToneHz\":${data.ctcsRxToneHz ?: "null"},")
        append("\"mode\":${data.mode?.let { "\"${it.jsonEscape()}\"" } ?: "null"},")
        append("\"aosTime\":${data.aosTime},")
        append("\"losTime\":${data.losTime},")
        append("\"timestamp\":${data.timestamp}")
        append('}')
    }

    private fun String.jsonEscape(): String =
        replace("\\", "\\\\").replace("\"", "\\\"")
}
