# Scanner-Walk-Through - a GNetPlus Library for Java
[![](https://jitpack.io/v/morihofi/er750-scanner-walk-thought.svg)](https://jitpack.io/#morihofi/er750-scanner-walk-thought)

## Overview

The GNetPlus Library is a proof of concept (PoC) designed to facilitate the integration and manipulation of access control devices, 
specifically targeting compatibility with the GNetPlus protocol developed by Giga-TMS. This library serves as a bridge for developers 
looking to implement access control features such as LED/Buzzer control and relay operations in their applications. 

It encapsulates the complexities of the GNetPlus protocol, offering a simplified API for easy interaction with devices like the Promag ER750. 
While primarily tested with the Promag ER750, this library may also be compatible with other devices following the GNetPlus protocol.

### Features
- Scanner Emulation Server: Simulates access control devices, allowing for the testing and development of applications without the need for physical hardware.
- LED/Buzzer Control: Offers functions to manipulate the LED and Buzzer of an access control device, providing immediate feedback mechanisms.
- Relay Control: Enables control over relays, facilitating the locking and unlocking mechanisms typically associated with access control systems.
- GNetPlus Protocol Compatibility: Implements key aspects of the GNetPlus protocol as defined by Giga-TMS, incorporating both documented features and additional capabilities discovered through reverse engineering.
- Simple API: Designed with ease of use in mind, the API abstracts protocol complexities, making it straightforward for developers to integrate access control features into their applications.
- Tested with Promag ER750: While the library is developed and tested primarily with the Promag ER750 reader, it aims for broader compatibility with devices adhering to the GNetPlus protocol.
- CRC Check
### Interesting stuff I used
- https://www.gigatms.com.tw/dl_giga/RFID%20Products/13.56MHz/Mifare/PCR310U-00/TM970013_GNetPlusCommunicationProtocol_REV_D.pdf
- https://github.com/harishpillay/gnetplus/blob/master/gnetplus.py
- http://ftp.gigatms.com.tw/public/disks/disk5453/index.html (includes many PDFs, Software and the SDK)

## Usage
Just look at the `Main` class file, should be self explaining.