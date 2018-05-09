@file:JvmName("MainKt")

package at.ac.tuwien.big

import at.ac.tuwien.big.api.WebController

/**
 * Set of hosts required for all services
 */
data class HostConfig(
        val influx: String,
        val mqtt: String,
        val objectTracker: String)

val default = HostConfig("127.0.0.1", "localhost", "localhost")
val docker = HostConfig("influx", "mqtt", "object-tracker")

const val simSensor = "Sensor-Simulation"
const val sensor = "Sensor"
const val detectionCamera = "DetectionCamera"
const val pickupCamera = "PickupCamera"
const val simActuator = "Actuator-Simulation"
const val actuator = "Actuator"

fun main(args: Array<String>) {

    val hosts = if (args.firstOrNull() == "--docker") {
        docker
    } else {
        default
    }

    val sensors = listOf(simSensor, sensor, detectionCamera, pickupCamera)
    val actuators = listOf(simActuator, actuator)
    val objectTracker = ObjectTracker(hosts.objectTracker)
    val influx = TimeSeriesDatabase(hosts.influx)
    val mqtt = MQTT(hosts.mqtt, sensors, actuators)
    val controller = MessageController(mqtt, objectTracker, influx)
    val web = WebController(mqtt, controller, influx)
    controller.start()
    web.start()
}