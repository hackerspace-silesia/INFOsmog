
smogDetector=require("mq135")

local WIFI_SSID="CINiBA_ Hackathon"
local WIFI_PASSWORD="CINiBA-HK2017"
local ADRESS=""


function SendData()

    local htemperature = bmp085.temperature()
    local hpressure = bmp085.pressure()
    local smog= smogDetector.getPPM()
    --polacz w jeden strnig--
    local data= {temperature = htemperature/10,levelOfPollution=smog, pressure= hpressure }
    local jasonData = sjson.encoder(data)
    --debug data
    
    print("Dfdf")

    --jezeli jest polaczenie to wyslij na podany port i ip-----
    if wifi.sta.getip() ~= nil then
        http.post('http://155.158.2.54:8081/api/v1/sensor/save',
                    'Content-Type: application/json \r\n',
                    jasonData:read(),
                    function(code, data)
                        if (code < 0) then
                          print("HTTP request failed")
                        else
                          print(code, data)
                        end
                    end)
    end

end


local sda = 1
local scl=2
i2c.setup(0, sda, scl, i2c.SLOW)

bmp085.setup()

--Initialize pressure mp150 detector----
smogDetector.setRZero(24.21206) -- Put your sensor's value as measured with calibrate.lua here----
smogDetector.setRLoad(1.0) -- My Flying-Fish MQ135 module has 1 ohm resistor instead of 10 ohms----

 

wifi.setmode(wifi.STATION)
wifi.setphymode(wifi.PHYMODE_N)
wifistruct={}
wifistruct.ssid=WIFI_SSID
wifistruct.pwd=WIFI_PASSWORD
wifi.sta.config(wifistruct) 
wifi.sta.connect()



tmr.alarm(0, 600000, 1, SendData)
