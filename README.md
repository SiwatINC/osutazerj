# osu!tazer
This repository house the resources for building osu!tazer.

BOMs:
  - Arduino Yun Rev 2.0 Flashed with StandardFirmataYun at 115200 baudrate https://github.com/firmata/StandardFirmataYun
  - Arduino Motor Shield Rev 3.0
  - Geared DC Motor
  - A 3D printer with ~ 243g of Fillament
  - Generic Discharge-based High Voltage Generator
  
Software:
  - Build&Compile (Optional):
    - Eclipse with JDK14+
    - All of the library
  - Run:
    - MQTT Server
    - OSUSync: https://github.com/OsuSync/Sync
    - OsuDataDistributeRestful: https://github.com/OsuSync/OsuDataDistributeRestful
    - a valid config file (edit the provided one)
    
Utilized Library:
  - PyMata: https://github.com/MrYsLab/PyMata
  - Eclipse-Paho: https://www.eclipse.org/paho/
  - json-simple: https://github.com/fangyidong/json-simple
  
Utilized CAD Files:
  - Motor: https://grabcad.com/library/3-6v-dc-gearbox-electromotor-1

Where it all begin https://www.reddit.com/r/osugame/comments/k03i7i/osu_but_if_i_miss_i_get_electrocuted/


DISCLAIMER: If you managed to kill yourself, coffin is not included in this repository.

# THIS SECTION IS FOR NERDS
The control software is written in Java, API documentation is included in the project!

CALIBRATION CONSTANTS For 12V 1.5A DC SPS

Voltage Drop for PWM Duty Cycle 150/255: 12V -> 7.6V

Voltage Drop for PWM Duty Cycle 100/255: 12V -> 8.9V

Voltage Drop for PWM Duty Cycle 50/255: 12V -> 11.3V
