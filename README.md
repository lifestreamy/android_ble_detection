***This is a simple demonstrative Android application showcasing measurement of relative distances from the nearby Bluetooth LE devices to the smartphone.***

It is demonstrated that even though the distance estimation via RSSI is unreliable and imprecise over long ranges, 
it is possible to successfully detect (with careful choice of constants for Kalman Filter and Rssi -> Distance converter by real-life calibration)
whether a device (a smart watch) is inside a small range of the smartphone.

Displaying usage of Kalman Filter for smoothing out received signal strength values to account for static noise, 
ambient noise, obstacles between receiver and transmitter and their relative movement.

It is demonstrated that it is possible to reliably detect when a smart watch (tested with Xiaomi Smart Band 7) enters select range of up to 10 cm
of the smartphone.

The potential application of such smart device proximity detection is, for example, in approving secure transactions and other user actions on the user's smartphone 
by using the BLE device as a key and requiring it to be held close.

Technologies:
Android, Kotlin, Jetpack Compose, Dagger-Hilt, Coroutines, Flows, Gradle Version Catalogs, Bluetooth LE
---------------
(C) 
- Tim K.
- @lifestreamy
- timkorelov@gmail.com
