Это простое приложение для Android, демонстрирующее возможность измерения расстояний между смартфоном и окружающими его устройствами Bluetooth LE, 
с применением фильтра Калмана для коррекции шумов (чем дольше работает поиск, тем точнее отсекает шумы фильтр).

Показывается, что несмотря на ненадежность и неточность оценки расстояния исключительно по силе сигнала (RSSI) Bluetooth на больших дистанциях,
возможно настроить обнаружение таким образом, чтобы стабильно определять, когда устройство (например, умные часы) появляется в 
небольшом заранее выбранном радиусе вокруг смартфона, находится в нём или покидает его (с аккуратным выбором констант для фильтра Калмана и преобразователя
RSSI в единицы измерения длины, калибруя по результатам реальных тестов для конкретных устройств).
Было протестировано с 2 разными смартфонами, что возможно надежно определять, когда умные часы (Xiaomi Smart Band 7) входят или покидают заранее выбранный радиус
10 см вокруг каждого устройства с высокой точностью (+-1 см).

Потенциальное применение такого обнаружения заключается, например, в возможности безопасного подтверждения финансовых операций, передачи данных и 
других действий пользователя на его смартфоне.

Использованные технологии и методы:
Android, Kotlin, Jetpack Compose, Dagger-Hilt, Coroutines, Flows, Gradle Version Catalogs, Bluetooth LE, Чистая Архитектура, MVI
--------------
(C)
- Тимофей К.


***EN:***

This is a simple Android application showcasing measurement of distances between the smartphone and nearby Bluetooth LE devices, 
with the application of a Kalman filter for noise correction (the longer the search is running, the more precise is the filtering).

It is demonstrated that even though the distance estimation solely via RSSI is unreliable and imprecise over long ranges, 
it is still possible to cofigure the detection so as to consistently determine whenever a device (e.g. a smart watch) moves inside a small chosen range around the smartphone,
stays in it or leaves it
(with careful selection of the constants for Kalman Filter and Rssi -> Distance Units converter, and real-life calibration for specific devices)

It was tested with 2 different smartphones to be possible to reliably detect whenever a smart watch (Xiaomi Smart Band 7) enters and leaves the preselected range of 10 cm 
of each of them with high precision (+-1 cm).

The potential application of such smart device proximity detection is, for example, in secure approval of monetary transactions, data transfer and other user actions on the user's smartphone 
by using the BLE device as a key and requiring it to be held close.

Used technologies:
Android, Kotlin, Jetpack Compose, Dagger-Hilt, Coroutines, Flows, Gradle Version Catalogs, Bluetooth LE, Clean Architecture, MVI
---------------
(C) 
- Tim K.
