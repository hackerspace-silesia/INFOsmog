# INFOsmog embedded station
Air quality control station communicating with INFOsmog server. Data is transmitted to INFOsmog.pl where user can check their personal air quality station status. Designed for nodeMCU with mq135 carbon dioxide detector and bmp180 barometer for Hacksilesia 2017. Tested and run on ESP8266.



###Connection to NodeMCU
**MQ135** - carbon dioxide detector
GND to GND
VCC to 3V3
AO to Ao
**BMP180** - barometer & thermometer
VIN to 3V3
GND to GND
SCL to D2(GPIO4)
SDA to D1 (GPIO5)
	
![pinouts](https://imgur.com/YVJnTtA.jpg)
###Firmware dependencies:

 - ADP for MQ135 
 - BMP085 for BMP180
 - file
 - GPIO
 - net
 - node
 - I2C
 - SJSON
 - timer
 - HTTP
 - UART
 - WIFI

#####MQ135  module used: https://github.com/tricoos/m135-lua