#DataDealer
Приложение для сбора данных от электронных устройств по различным коммуникационным протоколам и последующей передачи по MQTT.

##Как запустить
+ Установить MQTT брокер. Например Mosquito. [Подробнее](http://nthn.me/posts/2012/mqtt.html)
+ Установить librxtx-java. `sudo apt-get install librxtx-java`
+ Добавить пользователя ОС в группу dialout. `sudo adduser user group`
+ Установить права на файл COM-порта `sudo chmod 666 /dev/ttyUSB0`
+ Скомпилировать в jar файл
+ Запустить с дополнительной линковкой библиотеки RxTx `java -Djava.library.path=/usr/lib/jni -jar ...`

##Как пользоваться
+ Сконфигурировать опросный лист с помощью отправки специального MQTT сообщения в топик smiu/DD/updateConfig
+ Запустить опрос с помощью отправки MQTT сообщения `run` в топик smiu/DD/engine

##Описание конфигурационного сообщения
Конфигурационное сообщение имеет формат JSON.

###Пример

    {
       "protocols": [
         {
           "type"     : "MODBUS_MASTER",
           "name"     : "modbus_in",
           "portName" : "/dev/ttyUSB0",
           "baudRate" : 19200,
           "databits" : 8,
           "stopbits" : 1,
           "parity"   : "none",
           "encoding" : "rtu",
           "echo"     : false,
           "timePause": 10,
           "slaves"   : [
             {
               "unitId"    : 1,
               "mbFunc"    : "READ_COIL_REGS_1",
               "mbRegType" : "BIT",
               "offset"    : 0,
               "length"    : 6,
               "transDelay": 50,
               "name"      : "slave1_1"
             },
             {
               "unitId"    : 1,
               "mbFunc"    : "READ_HOLDING_REGS_3",
               "mbRegType" : "INT16",
               "offset"    : 1,
               "length"    : 3,
               "transDelay": 50,
               "name"      : "slave1_2"
             }
           ]
         }
       ]
     }

