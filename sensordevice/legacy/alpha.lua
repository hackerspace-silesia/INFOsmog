
smogDetector=require("mq135")

local WIFI_SSID="CINiBA_ Hackathon"
local WIFI_PASSWORD="CINiBA-HK2017"
local IP="155.158.2.54"
local PORT= 1098

function SendData()

    local temperature = bmp085.temperature()
    local pressure = bmp085.pressure()
    local smog= smogDetector.getPPM()
    --polacz w jeden strnig
    local data= "temperature: " .. tostring(temperature) .. " pressure: " .. tostring(pressure).." smog: "..tostring(smog)
    
    --debug data
    print (data)
    print(wifi.sta.getip())

    --jezeli jest polaczenie to wyslij na podany port i ip
    if wifi.sta.getip() ~= nil then
        srv:send(PORT,IP,data)
    end

end

-- Initialize the pin
local sda, scl = 1, 2
i2c.setup(0, sda, scl, i2c.SLOW)
--initialize bmp085 pressure temp detector
bmp085.setup()

--Initialize pressure mp150 detector
smogDetector.setRZero(24.21206) -- Put your sensor's value as measured with calibrate.lua here
smogDetector.setRLoad(1.0) -- My Flying-Fish MQ135 module has 1 ohm resistor instead of 10 ohms

 
--initialize wifi
wifi.setmode(wifi.STATION)
wifi.setphymode(wifi.PHYMODE_N)
wifi.sta.config({ssid= WIFI_SSID,  pwd=WIFI_PASSWORD}) 
wifi.sta.connect()

--initialize udp connection
srv = net.createUDPSocket()



tmr.alarm(0, 2000, 1, SendData)
