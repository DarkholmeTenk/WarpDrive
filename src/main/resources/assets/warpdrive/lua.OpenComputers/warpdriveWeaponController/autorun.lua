local component = require("component")
local computer = require("computer")
local term = require("term")
local event = require("event")
local fs = require("filesystem")
local serialization = require("serialization")

if not term.isAvailable() then
  computer.beep()
  return
end

Style = {
  CDefault = 0xFFFFFF,
  BGDefault = 0x0000FF,

  CTitle = 0x000000,
  BGTitle = 0x00FFFF,

  CWarning = 0xFFFFFF,
  BGWarning = 0xFF0000,

  CSuccess = 0xFFFFFF,
  BGSuccess = 0x32CD32,

  CDisabled = 0x808080,
  BGDisabled = 0x0000FF
}

----------- Monitor support

-- cache colors to reduce GPU load
local gpu_frontColor = 0xFFFFFF
local gpu_backgroundColor = 0x000000
function SetMonitorColorFrontBack(frontColor, backgroundColor)
  if gpu_frontColor ~= frontColor then
    gpu_frontColor = frontColor
    component.gpu.setForeground(gpu_frontColor)
  end
  if gpu_backgroundColor ~= backgroundColor then
    gpu_backgroundColor = backgroundColor
    component.gpu.setBackground(gpu_backgroundColor)
  end
end

function Write(text)
  if term.isAvailable() then
    local w, h = component.gpu.getResolution()
    if w then
      local xt, yt = term.getCursor()
      component.gpu.set(xt, yt, text)
      SetCursorPos(xt + #text, yt)
    end
  end
end

function SetCursorPos(x, y)
  if term.isAvailable() then
    term.setCursor(x, y)
  end
end

function SetColorDefault()
  SetMonitorColorFrontBack(Style.CDefault, Style.BGDefault)
end

function SetColorTitle()
  SetMonitorColorFrontBack(Style.CTitle, Style.BGTitle)
end

function SetColorWarning()
  SetMonitorColorFrontBack(Style.CWarning, Style.BGWarning)
end

function SetColorSuccess()
  SetMonitorColorFrontBack(Style.CSuccess, Style.BGSuccess)
end

function SetColorDisabled()
  SetMonitorColorFrontBack(Style.CDisabled, Style.BGDisabled)
end

function Clear()
  clearWarningTick = -1
  SetColorDefault()
  term.clear()
  SetCursorPos(1, 1)
end

function ClearLine()
  SetColorDefault()
  term.clearLine()
  SetCursorPos(1, 1)
end

function WriteLn(text)
  if term.isAvailable() then
    Write(text)
    local x, y = term.getCursor()
    local width, height = component.gpu.getResolution()
    if y > height - 1 then
      y = 1
    end
    SetCursorPos(1, y + 1)
  end
end

function WriteCentered(y, text)
  if term.isAvailable() then
    local sizeX, sizeY = component.gpu.getResolution()
    if sizeX then
      component.gpu.set((sizeX - text:len()) / 2, y, text)
    end
    local xt, yt = term.getCursor()
    SetCursorPos(1, yt + 1)
  end
end

function ShowTitle(text)
  Clear()
  SetColorTitle()
  WriteCentered(1, text)
  SetColorDefault()
end

function ShowMenu(text)
  if term.isAvailable() then
    Write(text)
    local sizeX, sizeY = component.gpu.getResolution()
    local xt, yt = term.getCursor()
    for i = xt, sizeX do
      Write(" ")
    end
    SetCursorPos(1, yt + 1)
  end
end

local clearWarningTick = -1
function ShowWarning(text)
  if term.isAvailable() then
    local sizeX, sizeY = component.gpu.getResolution()
    SetCursorPos(1, sizeY)
    ClearLine()
    SetColorWarning()
    SetCursorPos((sizeX - text:len() - 2) / 2, sizeY)
    Write(" " .. text .. " ")
    SetColorDefault()
    clearWarningTick = 5
  end
end
function ClearWarning()
  if clearWarningTick > 0 then
    clearWarningTick = clearWarningTick - 1
  elseif clearWarningTick == 0 then
    if term.isAvailable() then
      SetColorDefault()
      local sizeX, sizeY = component.gpu.getResolution()
      SetCursorPos(1, sizeY)
      ClearLine()
      clearWarningTick = -1
    end
  end
end

----------- Formatting & popups

function FormatFloat(value, nbchar)
  local str = "?"
  if value ~= nil then
    str = string.format("%g", value)
  end
  if nbchar ~= nil then
    str = string.sub("               " .. str, -nbchar)
  end
  return str
end
function FormatInteger(value, nbchar)
  local str = "?"
  if value ~= nil then
    str = string.format("%d", value)
  end
  if nbchar ~= nil then
    str = string.sub("               " .. str, -nbchar)
  end
  return str
end

function boolToYesNo(bool)
  if bool then
    return "YES"
  else
    return "no"
  end
end

function readInputNumber(currentValue)
  local inputAbort = false
  local input = string.format(currentValue)
  if input == "0" then
    input = ""
  end
  local x, y = term.getCursor()
  repeat
    ClearWarning()
    SetColorDefault()
    SetCursorPos(x, y)
    Write(input .. "            ")
    input = string.sub(input, -9)
    
    local params = { event.pull() }
    local eventName = params[1]
    local address = params[2]
    if address == nil then address = "none" end
    if eventName == "key_down" then
      local char = params[3]
      local keycode = params[4]
      if char >= 49 and char <= 57 then -- 1 to 9
        input = input .. string.format(char - 48)
      elseif keycode >= 2 and keycode <= 10 then -- 1 to 9
        input = input .. string.format(keycode - 1)
      elseif char == 48 or keycode == 11 then -- 0
        input = input .. "0"
      elseif char == 45 or char == 78 or char == 110
        or keycode == 74 or keycode == 12 or keycode == 49 then -- - on numeric keypad or - on US top or n letter
        if string.sub(input, 1, 1) == "-" then
          input = string.sub(input, 2)
        else
          input = "-" .. input
        end
      elseif char == 43 or keycode == 78 then -- +
        if string.sub(input, 1, 1) == "-" then
          input = string.sub(input, 2)
        end
      elseif char == 8 then -- Backspace
        input = string.sub(input, 1, string.len(input) - 1)
      elseif char == 0 and keycode == 211 then -- Delete
        input = ""
      elseif char == 13 then -- Enter
        inputAbort = true
      elseif char ~= 0 then
        ShowWarning("Key " .. char .. " " .. keycode .. " is invalid")
      end
    elseif eventName == "interrupted" then
      inputAbort = true
    elseif not common_event(eventName, params[3]) then
      ShowWarning("Event '" .. eventName .. "', " .. address .. " is unsupported")
    end
  until inputAbort
  SetCursorPos(1, y + 1)
  if input == "" or input == "-" then
    return currentValue
  else
    return tonumber(input)
  end
end

function readInputText(currentValue)
  local inputAbort = false
  local input = string.format(currentValue)
  local x, y = term.getCursor()
  repeat
    ClearWarning()
    SetColorDefault()
    SetCursorPos(x, y)
    Write(input .. "                              ")
    input = string.sub(input, -30)

    local params = { event.pull() }
    local eventName = params[1]
    local address = params[2]
    if address == nil then address = "none" end
    if eventName == "key_down" then
      local char = params[3]
      local keycode = params[4]
      if char >= 32 and char <= 127 then -- any ASCII table minus controls and DEL
        input = input .. string.char(char)
      elseif char == 8 then -- Backspace
        input = string.sub(input, 1, string.len(input) - 1)
      elseif char == 0 and keycode == 211 then -- Delete
        input = ""
      elseif char == 13 then -- Enter
        inputAbort = true
      elseif char ~= 0 then
        ShowWarning("Key " .. char .. " " .. keycode .. " is invalid")
      end
    elseif eventName == "interrupted" then
      inputAbort = true
    elseif not common_event(eventName, params[3]) then
      ShowWarning("Event '" .. eventName .. "', " .. address .. " is unsupported")
    end
  until inputAbort
  SetCursorPos(1, y + 1)
  if input == "" then
    return currentValue
  else
    return input
  end
end

function readConfirmation(msg)
  if msg == nil then
    ShowWarning("Are you sure? (y/n)")
  else
    ShowWarning(msg)
  end
  repeat
    local params = { event.pull() }
    local eventName = params[1]
    local address = params[3]
    if address == nil then address = "none" end
    if eventName == "key_down" then
      local char = params[3]
      if char == 89 or char == 121 or keycode == 21 then -- Y
        return true
      else
        return false
      end
    elseif eventName == "interrupted" then
      return false
    elseif not common_event(eventName, params[3]) then
      ShowWarning("Event '" .. eventName .. "', " .. address .. " is unsupported")
    end
  until false
end

----------- commons: menu, event handlers, etc.

function common_event(eventName, param)
  if eventName == "redstone" then
  --    redstone_event(param)
  elseif eventName == "timer" then
  elseif eventName == "laserScanning" then
    laser_sendEvent()
  elseif eventName == "laserSend" then
  elseif eventName == "key_up" then
  elseif eventName == "touch" then
  elseif eventName == "drop" then
  elseif eventName == "drag" then
  elseif eventName == "component_added" then
  elseif eventName == "component_removed" then
  elseif eventName == "component_unavailable" then
    -- ShowWarning("Event '" .. eventName .. "', " .. param .. " is unsupported")
  else
    return false
  end
  return true
end

function menu_common()
  SetCursorPos(1, 23)
  SetColorTitle()
  ShowMenu("1 - Laser batteries, 2 - Laser stations, X Exit")
end

----------- Configuration

function data_save()
  data.laser_batteries = {}
  for key,laserbattery in pairs(laser_batteries) do
    data.laser_batteries[key] = {
      name = laserbattery.name,
      frequency = laserbattery.frequency,
      headAddress = laserbattery.headAddress,
      boosterAddresses = laserbattery.boosterAddresses }
  end
  data.laser_stations = {}
  for key,laserstation in pairs(laser_stations) do
    data.laser_stations[key] = {
      name = laserstation.name,
      cameraAddress = laserstation.cameraAddress,
      batteries = {}}
    for batteryKey,laserbattery in pairs(laser_stations[key].batteries) do
      data.laser_stations[key].batteries[batteryKey] = {
        name = laserbattery.name,
        enabled = laserbattery.enabled }
    end
  end
  
  local file = fs.open("/disk/shipdata.txt", "w")
  if file ~= nil then
    file:write(serialization.serialize(data))
    file:close()
  else
    ShowWarning("No file system")
    os.sleep(3)
  end
end

function data_read()
  data = { }
  if fs.exists("/disk/shipdata.txt") then
    local file = fs.open("/disk/shipdata.txt", "r")
    local size = fs.size("/disk/shipdata.txt")
    local rawData = file:read(size)
    if rawData ~= nil then
      data = serialization.unserialize(rawData)
    end
    file:close()
	if data == nil then data = {}; end
  end
  if data.laser_batteries == nil then data.laser_batteries = {}; end
  laser_batteries = data.laser_batteries
  for batteryKey,laserbattery in pairs(laser_batteries) do
    if laserbattery.headAddress ~= nil then
      laserbattery.head = component.proxy(laserbattery.headAddress)
    end
    if laserbattery.name == nil then
      laserbattery.name = "noname"
    end
    if laserbattery.boosterAddresses == nil then
      laserbattery.boosterAddresses = {}
    end
    laserbattery.boosters = {}
    for boosterKey,boosterAddress in pairs(laserbattery.boosterAddresses) do
      laserbattery.boosters[boosterKey] = component.proxy(boosterAddress)
    end
  end
  if data.laser_stations == nil then data.laser_stations = {}; end
  laser_stations = data.laser_stations
  for batteryKey,laserstation in pairs(laser_stations) do
    if laserstation.cameraAddress ~= nil then
      laserstation.camera = component.proxy(laserstation.cameraAddress)
    end
    if laserstation.name == nil then
      laserstation.name = "noname"
    end
    if laserstation.batteries == nil then
      laserstation.batteries = {}
    end
  end
  if data.laser_firingMode == nil then data.laser_firingMode = "boosted"; end
  if data.laser_firingScatter == nil then data.laser_firingScatter = false; end
end

function data_setName()
  if ship ~= nil then
    ShowTitle("<==== Set ship name ====>")
  else
    ShowTitle("<==== Set name ====>")
  end
  
  SetCursorPos(1, 2)
  Write("Enter ship name: ")
  label = readInputText(label)
  -- FIXME os.setComputerLabel(label)
  if ship ~= nil then
    ship.coreFrequency(label)
  end
  -- FIXME computer.shutdown(true)
end

function string_split(source, sep)
  local sep = sep or ":"
  local fields = {}
  local pattern = string.format("([^%s]+)", sep)
  source:gsub(pattern, function(c) fields[#fields + 1] = c end)
  return fields
end


----------- Attack lasers support

laser_batteryIndex = 1
laser_batteries = {}
laser_stationIndex = 1
laser_stations = {}

function laser_getDescription(address)
  if address == nil then
    return "~not defined~"
  end
  local laser = component.proxy(address)
  if laser == nil or laser.position == nil then
    return "~invalid~"
  end
  local x, y, z = laser.position()
  return "@ " .. FormatInteger(x, 7) .. " " .. FormatInteger(y, 3) .. " " .. FormatInteger(z, 7)
end

function laser_getName(address)
  return address
end

function laser_battery_key(char, keycode)
  if char == 65 or char == 97 or keycode == 30 then -- A
    table.insert(laser_batteries, {
      name = "noname",
      frequency = -1,
      headAddress = nil,
      boosterAddresses = {},
      head = nil,
      boosters = {} })
    laser_batteryIndex = #laser_batteries
    data_save()
    return true
  elseif char == 82 or char == 114 or keycode == 19 then -- R
    table.remove(laser_batteries, laser_batteryIndex)
    -- laser_batteryIndex = laser_batteryIndex - 1
    data_save()
    return true
  elseif char == 70 or char == 102 or keycode == 33 then -- F
    if data.laser_firingMode == "boosted" then data.laser_firingMode = "single"
    elseif data.laser_firingMode == "single" then data.laser_firingMode = "multi"
    elseif data.laser_firingMode == "multi" then data.laser_firingMode = "boosted" end
    data_save()
    return true
  elseif char == 83 or char == 115 or keycode == 31 then -- S
    data.laser_firingScatter = not data.laser_firingScatter
    data_save()
    return true
  elseif char == 76 or char == 108 or keycode == 38 then -- L
    laser_battery_linkLasers()
    data_save()
    return true
  elseif char == 67 or char == 99 or keycode == 46 then -- C
    laser_battery_config()
    data_save()
    return true
  elseif keycode == 200 or keycode == 203 or keycode == 74 then -- Up or Left or -
    laser_batteryIndex = laser_batteryIndex - 1
    return true
  elseif keycode == 208 or keycode == 205 or keycode == 78 then -- Down or Right or +
    laser_batteryIndex = laser_batteryIndex + 1
    return true
  end
  return false
end

function laser_battery_getCurrent()
  if laser_batteries ~= nil then
    if laser_batteryIndex > #laser_batteries then
      laser_batteryIndex = 1
    elseif laser_batteryIndex < 1 then
      laser_batteryIndex = #laser_batteries
    end
    return laser_batteries[laser_batteryIndex]
  else
    return nil
  end
end

function laser_battery_getByName(name)
  if laser_batteries ~= nil and name ~= nil then
    for key, laserbattery in pairs(laser_batteries) do
      if laserbattery.name == name then
        return laserbattery
      end
    end
  end
  return { frequency = -1, boosterAddresses = {}, name = "-not defined-", boosters = {} }
end

function laser_battery_page()
  ShowTitle(label .. " - Laser batteries")
  
  local laserbattery = laser_battery_getCurrent()
  
  -- SetCursorPos(1, 2)
  if #laser_batteries == 0 then
    SetColorDisabled()
    WriteCentered(2, "No laser battery defined, press A to add one")
  elseif laserbattery == nil then
    SetColorWarning()
    WriteCentered(2, "Laser battery " .. laser_batteryIndex .. " of " .. #laser_batteries .. " is not defined")
  else
    SetColorDefault()
    WriteCentered(2, "Laser battery " .. laser_batteryIndex .. " of " .. #laser_batteries .. ": " .. laserbattery.name)
    
    SetCursorPos(1, 3)
    local headX = 0
    local headY = 0
    local headZ = 0
    local headFrequency = -1
    if laserbattery.head == nil then
      SetColorDisabled()
      Write("Laser head is not defined, press L to link one")
    else
      SetColorDefault()
      headX, headY, headZ = laserbattery.head.position()
      headFrequency = laserbattery.head.beamFrequency()
      WriteLn("Laser head:")
      if headFrequency == -1 then
        SetColorWarning()
      end
      Write(FormatInteger(headFrequency, 5))
      SetColorDefault()
      Write(" @ " .. FormatInteger(headX, 7) .. " " .. FormatInteger(headY, 3) .. " " .. FormatInteger(headZ, 7)
        .. " " .. string.sub(laserbattery.headAddress, 10, 100))
    end
    
    SetCursorPos(1, 5)
    if laserbattery.boosters == nil or #laserbattery.boosters == 0 then
      SetColorDisabled()
      Write("Boosting lasers aren't set, press L to link one")
    else
      SetColorDefault()
      Write("Boosting lasers:")
      for key,booster in pairs(laserbattery.boosters) do
        SetCursorPos(1, 5 + key)
        local x, y, z = booster.position()
        if booster.beamFrequency() == headFrequency and headFrequency ~= -1 then
          SetColorDefault()
        else
          SetColorWarning()
        end
        Write(FormatInteger(booster.beamFrequency(), 5))
        SetColorDefault()
        Write(" @ " .. FormatInteger(x - headX, 7) .. " " .. FormatInteger(y - headY, 3) .. " " .. FormatInteger(z - headZ, 7)
          .. " " .. string.sub(laserbattery.boosterAddresses[key], 10, 100))
      end
    end
  end
  
  SetColorDefault()
  SetCursorPos(1, 13)
  Write("  -----------------------------------------------")
  SetCursorPos(1, 14)
  Write("Firing mode: " .. data.laser_firingMode)
  SetCursorPos(30, 14)
  Write("Scatter mode: ")
  if data.laser_firingScatter then
    SetColorSuccess()
    Write("ON")
    SetColorDefault()
  else
    Write("off")
  end
  
  SetColorTitle()
  SetCursorPos(1, 18)
  ShowMenu("A/R/C - Add/Remove/Configure selected battery")
  ShowMenu("Arrows - Select battery, L - Link lasers")
  ShowMenu("F - change Firing mode, S - toggle Scatter mode")
end

function laser_battery_config()
  local laserbattery = laser_battery_getCurrent()
  if laserbattery == nil then
    return
  end
  ShowTitle(label .. " - Laser battery configuration")
  SetColorDefault()
  SetCursorPos(1, 3)
  Write("Battery name (" .. laserbattery.name .. "): ")
  laserbattery.name = readInputText(laserbattery.name)
  
  if laserbattery.head == nil then
    return
  end
  SetCursorPos(1, 4)
  local headX, headY, headZ = laserbattery.head.position()
  local headFrequency = laserbattery.head.beamFrequency()
  Write("Head @ " .. FormatInteger(headX, 7) .. " " .. FormatInteger(headY, 3) .. " " .. FormatInteger(headZ, 7))
  SetCursorPos(1, 5)
  Write("With " .. #laserbattery.boosters .. " boosters")
  
  SetCursorPos(1, 7)
  Write("Battery beam frequency (" .. FormatInteger(headFrequency, 5) .. "): ")
  local frequency = readInputNumber(headFrequency)
  SetCursorPos(1, 8)
  local newFrequency = laserbattery.head.beamFrequency(frequency)
  Write("Head beam frequency set to " .. newFrequency)
  for key,booster in pairs(laserbattery.boosters) do
    SetCursorPos(1, 8 + (key % 5))
    newFrequency = booster.beamFrequency(frequency)
    Write("Booster beam frequency set to " .. newFrequency)
  end
end

function laser_battery_linkLasers()
  local laserbattery = laser_battery_getCurrent()
  if laserbattery == nil then
    return
  end
  ShowTitle(label .. " - Linking lasers")
  
  SetColorDisabled()
  SetCursorPos(1, 3)
  WriteLn("Help: Use arrows to select lasers.")
  WriteLn("Press backspace/delete to remove laser.")
  WriteLn("Press enter to validate.")
  WriteLn("Validate with '-no laser-' to stop adding lasers.")
  
  SetColorDefault()
  if laserbattery.boosters == nil then
    laserbattery.boosters = {}
  end
  WriteCentered(2, "Battery '" .. laserbattery.name .. "' with " .. #laserbattery.boosters .. " boosters")
  
  SetCursorPos(1, 8)
  if laserbattery.headAddress == nil then
    Write("Laser head (not defined): ")
  else
    Write("Laser head (" .. laserbattery.headAddress .. "): ")
  end
  laserbattery.headAddress = readInputEnum(laserbattery.headAddress, laserAddresses, laser_getName, laser_getDescription, "-no laser-")
  if laserbattery.headAddress == nil then
    laserbattery.head = nil
  else
    laserbattery.head = component.proxy(laserbattery.headAddress)
    
    SetCursorPos(1, 9)
    local headX, headY, headZ = laserbattery.head.position()
    local headFrequency = laserbattery.head.beamFrequency()
    Write("Head @ " .. FormatInteger(headX, 7) .. " " .. FormatInteger(headY, 3) .. " " .. FormatInteger(headZ, 7))
  end
  
  local key = 1
  local actualAddress = nil
  repeat
    SetCursorPos(1, 10 + (key % 5))
    Write("Booster #" .. key)
    actualAddress = nil
    if key <= #laserbattery.boosterAddresses then
      actualAddress = laserbattery.boosterAddresses[key]
      Write(" (" .. actualAddress .. "): ")
    else
      Write(" (-no laser-): ")
    end
    actualAddress  = readInputEnum(actualAddress, laserAddresses, laser_getName, laser_getDescription, "-no laser-")
    if actualAddress == nil then
      if key > #laserbattery.boosterAddresses then
        key = -1
      else
        table.remove(laserbattery.boosterAddresses, key)
        table.remove(laserbattery.boosters, key)
      end
    else
      laserbattery.boosterAddresses[key] = actualAddress
      laserbattery.boosters[key] = component.proxy(actualAddress)
      key = key + 1
    end
  until key == -1
end

function laser_battery_getName(laserbattery)
  return laserbattery.name
end

function laser_battery_getDescription(laserbattery)
  local msg = ""
  local x, y, z
  local found = false
  if laserbattery.head == nil then
    msg = msg .. 0
  else
    x, y, z = laserbattery.head.position()
    found = true
    msg = msg .. 1
  end
  msg = msg .. "+"
  if laserbattery.boosters == nil then
    msg = msg .. 0
  else
    msg = msg .. #laserbattery.boosters
    if not found then
      for key,booster in pairs(laserbattery.boosters) do
        if booster ~= nil and not found then
          x, y, z = booster.position()
          found = true
        end
      end
    end
  end
  return string.sub(laserbattery.name .. "                    ", 1, 20)
    .. msg
    .. " @ " .. FormatInteger(x, 7) .. " " .. FormatInteger(y, 3) .. " " .. FormatInteger(z, 7)
end

function laser_station_key(char, keycode)
  if char == 65 or char == 97 or keycode == 30 then -- A
    table.insert(laser_stations, { name = "noname", cameraFrequency = -1, camera = nil, batteries = {} })
    laser_stationIndex = #laser_stations
    data_save()
    return true
  elseif char == 82 or char == 114 or keycode == 19 then -- R
    table.remove(laser_stations, laser_stationIndex)
    -- laser_stationIndex = laser_stationIndex - 1
    data_save()
    return true
  elseif char == 70 or char == 102 or keycode == 33 then -- F
    if data.laser_firingMode == "boosted" then data.laser_firingMode = "single"
    elseif data.laser_firingMode == "single" then data.laser_firingMode = "multi"
    elseif data.laser_firingMode == "multi" then data.laser_firingMode = "boosted" end
    data_save()
    return true
  elseif char == 83 or char == 115 or keycode == 31 then -- S
    data.laser_firingScatter = not data.laser_firingScatter
    data_save()
    return true
  elseif char == 76 or char == 108 or keycode == 38 then -- L
    laser_station_linkBatteries()
    data_save()
    return true
  elseif char == 67 or char == 99 or keycode == 46 then -- C
    laser_station_config()
    data_save()
    return true
  elseif keycode == 200 or keycode == 203 or keycode == 74 then -- Up or Left or -
    laser_stationIndex = laser_stationIndex - 1
    return true
  elseif keycode == 208 or keycode == 205 or keycode == 78 then -- Down or Right or +
    laser_stationIndex = laser_stationIndex + 1
    return true
  end
  return false
end

function laser_station_getCurrent()
  if laser_stations ~= nil then
    if laser_stationIndex > #laser_stations then
      laser_stationIndex = 1
    elseif laser_stationIndex < 1 then
      laser_stationIndex = #laser_stations
    end
    return laser_stations[laser_stationIndex]
  else
    return nil
  end
end

function laser_station_page()
  ShowTitle(label .. " - Laser stations")
  
  local laserstation = laser_station_getCurrent()
  
  -- SetCursorPos(1, 2)
  if #laser_stations == 0 then
    SetColorDisabled()
    WriteCentered(2, "No laser stations configured")
  elseif laserstation == nil then
    SetColorWarning()
    WriteCentered(2, "Laser station " .. laser_stationIndex .. " of " .. #laser_stations .. " is invalid")
  else
    SetColorDefault()
    WriteCentered(2, "'" .. laserstation.name .. "' (" .. laser_stationIndex .. " of " .. #laser_stations .. ")")
    
    SetCursorPos(1, 3)
    if laserstation.camera == nil then
      SetColorDisabled()
      Write("Laser camera is not defined")
    else
      SetColorDefault()
      local camX, camY, camZ = laserstation.camera.position()
      local camVideoChannel = laserstation.camera.videoChannel()
      WriteLn("Laser camera:")
      if camVideoChannel == -1 then
        SetColorWarning()
      end
      Write(FormatInteger(camVideoChannel, 5))
      SetColorDefault()
      Write(" @ " .. FormatInteger(camX, 7) .. " " .. FormatInteger(camY, 3) .. " " .. FormatInteger(camZ, 7)
        .. " " .. string.sub(laserstation.cameraAddress, 10, 100))
    end
    
    SetCursorPos(1, 5)
    if laserstation.batteries == nil or #laserstation.batteries == 0 then
      SetColorDisabled()
      Write("Laser batteries aren't set, press L to link one")
    else
      SetColorDefault()
      Write("Laser batteries:")
      for key,battery in pairs(laserstation.batteries) do
        SetCursorPos(1, 5 + key)
        laserbattery = laser_battery_getByName(battery.name)
        if battery.enabled then
          SetColorSuccess()
        else
          SetColorDisabled()
        end
        Write(laser_battery_getDescription(laserbattery))
      end
    end
  end
  
  SetColorDefault()
  SetCursorPos(1, 13)
  Write("  -----------------------------------------------")
  SetCursorPos(1, 14)
  Write("Firing mode: " .. data.laser_firingMode)
  SetCursorPos(30, 14)
  Write("Scatter mode: ")
  if data.laser_firingScatter then
    SetColorSuccess()
    Write("ON")
    SetColorDefault()
  else
    Write("off")
  end
  
  SetColorTitle()
  SetCursorPos(1, 18)
  ShowMenu("A/R/C - Add/Remove/Configure laser station")
  ShowMenu("Arrows - select station, L - Link batteries")
  ShowMenu("F - change Firing mode, S - toggle Scatter mode")
end

function laser_station_config()
  local laserstation = laser_station_getCurrent()
  if laserstation == nil then
    return
  end
  ShowTitle(label .. " - Laser station configuration")
  SetColorDefault()
  SetCursorPos(1, 3)
  WriteLn("Laser station name (" .. laserstation.name .. "): ")
  laserstation.name = readInputText(laserstation.name)
  
  if laserstation.camera == nil then
    return
  end
  SetCursorPos(1, 5)
  local camX, camY, camZ = laserstation.camera.position()
  local camVideoChannel = laserstation.camera.videoChannel()
  Write("Camera @ " .. FormatInteger(camX, 7) .. " " .. FormatInteger(camY, 3) .. " " .. FormatInteger(camZ, 7))
  SetCursorPos(1, 6)
  Write("With " .. #laserstation.batteries .. " batteries")
  
  SetCursorPos(1, 8)
  Write("Camera video channel (" .. FormatInteger(camVideoChannel, 5) .. "): ")
  local camVideoChannel = readInputNumber(camVideoChannel)
  SetCursorPos(1, 9)
  local camVideoChannel = laserstation.camera.videoChannel(camVideoChannel)
  laserstation.camera.beamFrequency(1420)
  Write("Camera video channel set to " .. camVideoChannel)
  
  SetCursorPos(1, 11)
  Write("Battery enabling: ")
  for key,battery in pairs(laserstation.batteries) do
    laserbattery = laser_battery_getByName(battery.name)
    local msg = laser_battery_getDescription(laserbattery)
    
    SetCursorPos(1, 11 + (key % 5))
    if battery.enabled then
      SetColorSuccess()
      Write(msg)
      battery.enabled = readConfirmation(" Keep battery '" .. battery.name .. "' enabled? (y/n)")
    else
      SetColorDisabled()
      Write(msg)
      battery.enabled = readConfirmation(" Enable battery '" .. battery.name .. "'? (y/n)")
    end
    SetCursorPos(1, 11 + (key % 5))
    if battery.enabled then
      SetColorSuccess()
      Write(msg)
    else
      SetColorDisabled()
      Write(msg)
    end
  end
end

function laser_station_linkBatteries()
  local laserstation = laser_station_getCurrent()
  if laserstation == nil then
    return
  end
  ShowTitle(label .. " - Linking batteries")
  
  SetColorDisabled()
  SetCursorPos(1, 3)
  WriteLn("Instruction: use arrows to select batteries.")
  WriteLn("Press backspace/delete to remove battery.")
  WriteLn("Press enter to validate.")
  WriteLn("Validate '-no battery-' to stop adding batteries.")
  
  SetColorDefault()
  if laserstation.batteries == nil then
    laserstation.batteries = {}
  end
  WriteCentered(2, "Camera '" .. laserstation.name .. "' with " .. #laserstation.batteries .. " batteries linked")
  
  SetCursorPos(1, 8)
  if laserstation.cameraAddress == nil then
    Write("Laser camera (not defined): ")
  else
    Write("Laser camera (" .. laserstation.cameraAddress .. "): ")
  end
  
  laserstation.cameraAddress = readInputEnum(laserstation.cameraAddress, lasercamAddresses, laser_getName, laser_getDescription, "-no laser camera-")
  if laserstation.cameraAddress == nil then
    laserstation.camera = nil
  else
    laserstation.camera = component.proxy(laserstation.cameraAddress)
    
    SetCursorPos(1, 9)
    local camX, camY, camZ = laserstation.camera.position()
    local camVideoChannel = laserstation.camera.videoChannel()
    Write("Camera @ " .. FormatInteger(camX, 7) .. " " .. FormatInteger(camY, 3) .. " " .. FormatInteger(camZ, 7))
  end
  
  local key = 1
  local currentName = nil
  repeat
    SetCursorPos(1, 10 + (key % 5))
    Write("Battery #" .. key)
    currentName = nil
    if key <= #laserstation.batteries then
      currentName = laserstation.batteries[key].name
      Write(" (" .. currentName .. "): ")
    else
      Write(" (-no battery-): ")
    end
    currentName  = readInputEnum(currentName, laser_batteries, laser_battery_getName, laser_battery_getDescription, "-no battery-")
    if currentName == nil then
      if key > #laserstation.batteries then
        key = -1
      else
        table.remove(laserstation.batteries, key)
      end
    else
      laserstation.batteries[key] = { name = currentName, enabled = true }
      key = key + 1
    end
  until key == -1
end

function readInputEnum(currentValue, list, toValue, toDescription, noValue)
  local inputAbort = false
  local inputKey = nil
  local input = nil
  local inputDescription = nil
  local x, y = term.getCursor()
  
  SetCursorPos(1, 17)
  for key,entry in pairs(list) do
    if toValue(entry) == currentValue then
      inputKey = key
    end
  end
  
  repeat
    ClearWarning()
    SetColorDefault()
    SetCursorPos(x, y)
    if #list == 0 then
      inputKey = nil
    end
    if inputKey == nil then
      if currentValue ~= nil then
        input = noValue
        inputDescription = "Press enter to return previous entry"
      else
        input = noValue
        inputDescription = "Press enter to close listing"
      end
    else
      if inputKey < 1 then
        inputKey = #list
      elseif inputKey > #list then
        inputKey = 1
      end
      
      input = toValue(list[inputKey])
      inputDescription = toDescription(list[inputKey])
    end
    SetColorDefault()
    Write(input .. "                                                  ")
    SetColorDisabled()
    SetCursorPos(1, y + 1)
    Write(inputDescription .. "                                                  ")
    
    local params = { event.pull() }
    local eventName = params[1]
    local address = params[2]
    if address == nil then address = "none" end
    if eventName == "key_down" then
      local char = params[3]
      local keycode = params[4]
      if char == 8 or (char == 0 and keycode == 211) then -- Backspace or Delete
        inputKey = nil
      elseif keycode == 200 or keycode == 203 or char == 43 or keycode == 78 then -- Up or Left or +
        if inputKey == nil then
          inputKey = 1
        else
          inputKey = inputKey - 1
        end
      elseif keycode == 208 or keycode == 205 or char == 45 or keycode == 74 then -- Down or Right or -
        if inputKey == nil then
          inputKey = 1
        else
          inputKey = inputKey + 1
        end
      elseif char == 13 then -- Enter
        inputAbort = true
      else
        ShowWarning("Key " .. char .. " " .. keycode .. " is invalid")
      end
    elseif eventName == "interrupted" then
      inputAbort = true
    elseif not common_event(eventName, params[3]) then
      ShowWarning("Event '" .. eventName .. "', " .. address .. " is unsupported")
    end
  until inputAbort
  SetCursorPos(1, y + 1)
  ClearLine()
  if inputKey == nil then
    return nil
  else
    return toValue(list[inputKey])
  end
end

function laser_battery_shoot(batteryName, targetX, targetY, targetZ)
  local laserbattery = laser_battery_getByName(batteryName)
  
  if laserbattery == nil then
    ShowWarning("Invalid battery name '" .. batteryName .. "'")
    return
  end
  
  local x, y, z
  if data.laser_firingMode == "boosted" and laserbattery.head ~= nil and laserbattery.boosters ~= nil then
    local frequency = laserbattery.head.beamFrequency()
    local headX, headY, headZ = laserbattery.head.position()
    
    for key,booster in pairs(laserbattery.boosters) do
      booster.beamFrequency(frequency)
    end
    os.sleep(0)
    for key,booster in pairs(laserbattery.boosters) do
      x, y, z = booster.position()
      booster.emitBeam(headX - x, headY - y, headZ - z)
    end
    os.sleep(0.1)
    if data.laser_firingScatter then
      headX = headX + math.random(-1, 1)
      headY = headY + math.random(-1, 1)
      headZ = headZ + math.random(-1, 1)
    end
    laserbattery.head.emitBeam(targetX - headX, targetY - headY, targetZ - headZ)
  elseif data.laser_firingMode == "single" and laserbattery.head ~= nil then
    x, y, z = laserbattery.head.position()
    if data.laser_firingScatter then
      x = x + math.random(-1, 1)
      y = y + math.random(-1, 1)
      z = z + math.random(-1, 1)
    end
    laserbattery.head.emitBeam(targetX - x, targetY - y, targetZ - z)
  else
    if laserbattery.head ~= nil then
      x, y, z = laserbattery.head.position()
      if data.laser_firingScatter then
        x = x + math.random(-1, 1)
        y = y + math.random(-1, 1)
        z = z + math.random(-1, 1)
      end
      laserbattery.head.emitBeam(targetX - x, targetY - y, targetZ - z)
    end
    
    if laserbattery.boosters ~= nil then
      for key,booster in pairs(laserbattery.boosters) do
        x, y, z = booster.position()
        if data.laser_firingScatter then
          x = x + math.random(-1, 1)
          y = y + math.random(-1, 1)
          z = z + math.random(-1, 1)
        end
        booster.emitBeam(targetX - x, targetY - y, targetZ - z)
      end
    end
  end
end

function laser_boot()
  -- nothing
end

function laser_sendEvent()
  if #laser_stations == 0 then
    ShowWarning("No laser station configured")
  elseif #laser_batteries == 0 then
    ShowWarning("No laser battery configured")
  else
    ShowWarning("Laser sent!")
    for key,laserstation in pairs(laser_stations) do
      if laserstation.camera ~= nil then
        local resType, x, y, z, id, meta, res = laserstation.camera.getScanResult()
        if res ~= -1 then
          ShowWarning("Firing at " .. x .. " " .. y .. " " .. z)
          for batteryKey,battery in pairs(laserstation.batteries) do
            if battery.enabled then
              laser_battery_shoot(battery.name, x, y, z)
            end
          end
        end
      end
    end
  end
end

----------- Boot sequence

math.randomseed(os.time())
label = computer.address()
if not label then
  label = "" .. computer.address()
end

-- read configuration
data_read()

-- initial scanning
ShowTitle(label .. " - Connecting...")
WriteLn("")

-- clear previous events
repeat
until event.pull(0) == nil

laserAddresses = {}
lasercamAddresses = {}
for address, componentType in component.list() do
  os.sleep(0)
  Write("Checking " .. componentType .. " ")
  if componentType == "warpdriveLaserCamera" then
    Write("wrapping!")
    local laserCam = component.proxy(address)
    table.insert(lasercamAddresses, address)
  elseif componentType == "warpdriveLaser" then
    Write("wrapping!")
    local laser = component.proxy(address)
    table.insert(laserAddresses, address)
  end
  WriteLn("")
end

if not computer.address() and ship ~= nil then
  data_setName()
end

-- peripherals status
function connections_page()
  ShowTitle(label .. " - Connections")
  
  WriteLn("")
  if #laserAddresses == 0 then
    SetColorDisabled()
    WriteLn("No attack laser detected")
  elseif #laserAddresses == 1 then
    SetColorSuccess()
    WriteLn("1 attack laser detected")
  else
    SetColorSuccess()
    WriteLn(#laserAddresses .. " attack lasers detected")
  end

  if #lasercamAddresses == 0 then
    SetColorDisabled()
    WriteLn("No laser camera detected")
  elseif #lasercamAddresses == 1 then
    SetColorSuccess()
    WriteLn("1 laser camera detected")
  else
    SetColorSuccess()
    WriteLn(#lasercamAddresses .. " laser cameras detected")
  end
  
  WriteLn("")
  SetColorTitle()
  WriteLn("Please refer to below menu for keyboard controls")
  WriteLn("For example, press 1 to access Laser batteries page")
end

-- peripheral boot up
Clear()
connections_page()
SetColorDefault()
WriteLn("")
os.sleep(0)
laser_boot()
os.sleep(0)

-- main loop
abort = false
refresh = true
page = connections_page
keyHandler = nil
repeat
  ClearWarning()
  if refresh then
    Clear()
    page()
    menu_common()
    refresh = false
  end
  params = { event.pull() }
  eventName = params[1]
  address = params[2]
  if address == nil then address = "none" end
  -- WriteLn("...")
  -- WriteLn("Event '" .. eventName .. "', " .. address .. ", " .. params[3] .. " received")
  -- os.sleep(0.2)
  if eventName == "key_down" then
    char = params[3]
    keycode = params[4]
    if char == 88 or char == 120 or keycode == 45 then -- x for eXit
      abort = true
    elseif char == 48 or keycode == 11 or keycode == 82 then -- 0
      page = connections_page
      keyHandler = nil
      refresh = true
    elseif char == 49 or keycode == 2 or keycode == 79 then -- 1
      page = laser_battery_page
      keyHandler = laser_battery_key
      refresh = true
    elseif char == 50 or keycode == 3 or keycode == 80 then -- 2
      page = laser_station_page
      keyHandler = laser_station_key
      refresh = true
    elseif keyHandler ~= nil and keyHandler(char, keycode) then
      refresh = true
      os.sleep(0)
    elseif char == 0 then -- control chars
      refresh = false
      os.sleep(0)
    else
      ShowWarning("Key " .. char .. " " .. keycode .. " is invalid")
      os.sleep(0.2)
    end
  elseif eventName == "interrupted" then
    abort = true
  elseif not common_event(eventName, params[3]) then
    ShowWarning("Event '" .. eventName .. "', " .. address .. " is unsupported")
    refresh = true
    os.sleep(0.2)
  end
until abort

-- clear screens on exit
SetMonitorColorFrontBack(0xFFFFFF, 0x000000)
term.clear()
SetCursorPos(1, 1)
WriteLn("Program terminated")
WriteLn("Type reboot to restart it")
