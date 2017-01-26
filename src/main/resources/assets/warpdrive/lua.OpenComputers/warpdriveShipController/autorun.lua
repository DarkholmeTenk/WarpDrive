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
  elseif eventName == "shipCoreCooldownDone" then
    ShowWarning("Ship core cooldown done")
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
  ShowMenu("0 Connections, 1 Ship core, X Exit")
end

----------- Configuration

function data_save()
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
  if data.core_summon == nil then data.core_summon = false; end
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


----------- Ship support

core_front = 0
core_right = 0
core_up = 0
core_back = 0
core_left = 0
core_down = 0
core_isInHyper = false
core_jumpCost = 0
core_shipSize = 0
core_movement = { 0, 0, 0 }
core_rotationSteps = 0

function core_boot()
  if ship == nil then
    return
  end
  
  Write("Booting Ship Core")
  
  if data.core_summon then
    ship.summon_all()
  end
  
  WriteLn("...")
  core_front, core_right, core_up = ship.dim_positive()
  core_back, core_left, core_down = ship.dim_negative()
  core_isInHyper = ship.isInHyperspace()
  core_rotationSteps = ship.rotationSteps()
  core_movement = { ship.movement() }
  if ship.direction ~= nil then
    ship.direction(666)
    ship.distance(0)
  end
  WriteLn("Ship core detected...")
  
  repeat
    pos = ship.position()
    os.sleep(0.3)
  until pos ~= nil
  X, Y, Z = ship.position()
  WriteLn("Ship position triangulated...")
  
  repeat
    isAttached = ship.isAttached()
    os.sleep(0.3)
  until isAttached ~= false
  WriteLn("Ship core linked...")
  
  repeat
    core_shipSize = ship.getShipSize()
    os.sleep(0.3)
  until core_shipSize ~= nil
  WriteLn("Ship size updated...")
  
  ship.mode(1)
end

function core_writeMovement()
  local message = " Movement         = "
  local count = 0
  if core_movement[1] > 0 then
    message = message .. core_movement[1] .. " front"
    count = count + 1
  elseif core_movement[1] < 0 then
    message = message .. (- core_movement[1]) .. " back"
    count = count + 1
  end
  if core_movement[2] > 0 then
    if count > 0 then message = message .. ", "; end
    message = message .. core_movement[2] .. " up"
    count = count + 1
  elseif core_movement[2] < 0 then
    if count > 0 then message = message .. ", "; end
    message = message .. (- core_movement[2]) .. " down"
    count = count + 1
  end
  if core_movement[3] > 0 then
    if count > 0 then message = message .. ", "; end
    message = message .. core_movement[3] .. " right"
    count = count + 1
  elseif core_movement[3] < 0 then
    if count > 0 then message = message .. ", "; end
    message = message .. (- core_movement[3]) .. " left"
    count = count + 1
  end
  
  if core_rotationSteps == 1 then
    if count > 0 then message = message .. ", "; end
    message = message .. "Turn right"
    count = count + 1
  elseif core_rotationSteps == 2 then
    if count > 0 then message = message .. ", "; end
    message = message .. "Turn back"
    count = count + 1
  elseif core_rotationSteps == 3 then
    if count > 0 then message = message .. ", "; end
    message = message .. "Turn left"
    count = count + 1
  end
  
  if count == 0 then
    message = message .. "(none)"
  end
  WriteLn(message)
end

function core_writeRotation()
  if core_rotationSteps == 0 then
    WriteLn(" Rotation         = Front")
  elseif core_rotationSteps == 1 then
    WriteLn(" Rotation         = Right +90")
  elseif core_rotationSteps == 2 then
    WriteLn(" Rotation         = Back 180")
  elseif core_rotationSteps == 3 then
    WriteLn(" Rotation         = Left -90")
  end
end

function core_computeNewCoordinates(cx, cy, cz)
  local res = { x = cx, y = cy, z = cz }
  local dx, dy, dz = ship.getOrientation()
  local worldMovement = { x = 0, y = 0, z = 0 }
  worldMovement.x = dx * core_movement[1] - dz * core_movement[3]
  worldMovement.y = core_movement[2]
  worldMovement.z = dz * core_movement[1] + dx * core_movement[3]
  core_actualDistance = math.ceil(math.sqrt(worldMovement.x * worldMovement.x + worldMovement.y * worldMovement.y + worldMovement.z * worldMovement.z))
  core_jumpCost = ship.getEnergyRequired(core_actualDistance)
  res.x = res.x + worldMovement.x
  res.y = res.y + worldMovement.y
  res.z = res.z + worldMovement.z
  return res
end

function core_warp()
  -- rs.setOutput(alarm_side, true)
  if readConfirmation() then
    -- rs.setOutput(alarm_side, false)
    ship.movement(core_movement[1], core_movement[2], core_movement[3])
    ship.rotationSteps(core_rotationSteps)
    ship.mode(1)
    ship.jump()
    -- ship = nil
  end
  -- rs.setOutput(alarm_side, false)
end

function core_page_setMovement()
  ShowTitle("<==== Set movement ====>")
  SetCursorPos(1, 20)
  SetColorTitle()
  ShowMenu("Enter 0 to keep position on that axis")
  ShowMenu("Use - or n keys to move in opposite direction")
  ShowMenu("Press Enter to confirm")
  SetColorDefault()
  SetCursorPos(1, 3)
  
  core_movement[1] = core_page_setDistanceAxis(2, "Forward" , "Front", "Back", core_movement[1], math.abs(core_front + core_back + 1))
  core_movement[2] = core_page_setDistanceAxis(4, "Vertical", "Up"   , "Down", core_movement[2], math.abs(core_up + core_down + 1))
  core_movement[3] = core_page_setDistanceAxis(6, "Lateral" , "Right", "Left", core_movement[3], math.abs(core_left + core_right + 1))
  core_movement = { ship.movement(core_movement[1], core_movement[2], core_movement[3]) }
end

function core_page_setDistanceAxis(line, axis, positive, negative, userEntry, shipLength)
  local maximumDistance = shipLength + 127
  if core_isInHyper and line ~= 3 then
    maximumDistance = shipLength + 127 * 100
  end
  SetColorDisabled()
  SetCursorPos(3, line + 1)
  Write(positive .. " is " .. ( shipLength + 1) .. ", maximum is " ..  maximumDistance .. "      ")
  SetCursorPos(3, line + 2)
  Write(negative .. " is " .. (-shipLength - 1) .. ", maximum is " .. -maximumDistance .. "      ")
  
  SetColorDefault()
  repeat
    SetCursorPos(1, line)
    Write(axis .. " movement: ")
    userEntry = readInputNumber(userEntry)
    if userEntry ~= 0 and (math.abs(userEntry) <= shipLength or math.abs(userEntry) > maximumDistance) then
      ShowWarning("Wrong distance. Try again.")
    end
  until userEntry == 0 or (math.abs(userEntry) > shipLength and math.abs(userEntry) <= maximumDistance)
  SetCursorPos(1, line + 1)
  ClearLine()
  SetCursorPos(1, line + 2)
  ClearLine()
  
  return userEntry
end

function core_page_setRotation()
  local inputAbort = false
  local drun = true
  repeat
    ShowTitle("<==== Set rotation ====>")
    core_writeRotation()
    SetCursorPos(1, 21)
    SetColorTitle()
    ShowMenu("Use directional keys")
    ShowMenu("Press Enter to confirm")
    SetColorDefault()
    local params = { event.pull() }
    local eventName = params[1]
    local address = params[2]
    if address == nil then address = "none" end
    if eventName == "key_down" then
      local char = params[3]
      local keycode = params[4]
      if keycode == 200 then
        core_rotationSteps = 0
      elseif keycode == 203 then
        core_rotationSteps = 3
      elseif keycode == 205 then
        core_rotationSteps = 1
      elseif keycode == 208 then
        core_rotationSteps = 2
      elseif keycode == 28 then
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
  core_rotationSteps = ship.rotationSteps(core_rotationSteps)
end

function core_page_setDimensions()
  ShowTitle("<==== Set dimensions ====>")
  Write(" Front (".. core_front ..") : ")
  core_front = readInputNumber(core_front)
  Write(" Right (".. core_right ..") : ")
  core_right = readInputNumber(core_right)
  Write(" Up    (".. core_up ..") : ")
  core_up = readInputNumber(core_up)
  Write(" Back  (".. core_back ..") : ")
  core_back = readInputNumber(core_back)
  Write(" Left  (".. core_left ..") : ")
  core_left = readInputNumber(core_left)
  Write(" Down  (".. core_down ..") : ")
  core_down = readInputNumber(core_down)
  Write("Setting dimensions...")
  core_front, core_right, core_up = ship.dim_positive(core_front, core_right, core_up)
  core_back, core_left, core_down = ship.dim_negative(core_back, core_left, core_down)
  core_shipSize = ship.getShipSize()
  if core_shipSize == nil then core_shipSize = 0 end
end

function core_page_summon()
  ShowTitle("<==== Summon players ====>")
  local playersString, playersArray = ship.getAttachedPlayers()
  if #playersArray == 0 then
    WriteLn("~ no players registered ~")
    WriteLn("")
    SetColorTitle()
    ShowMenu("Press enter to exit")
    SetColorDefault()
    readInputNumber("")
    return
  end
  
  for i = 1, #playersArray do
    WriteLn(i .. ". " .. playersArray[i])
  end
  SetColorTitle()
  ShowMenu("Enter player number")
  ShowMenu("or press enter to summon everyone")
  SetColorDefault()
  
  Write(":")
  local input = readInputNumber("")
  if input == "" then
    ship.summon_all()
  else
    input = tonumber(input)
    ship.summon(input - 1)
  end
end

function core_page_jumpToBeacon()
  ShowTitle("<==== Jump to beacon ====>")
  
  Write("Enter beacon frequency: ")
  local freq = readInputText("")
  -- rs.setOutput(alarm_side, true)
  if readConfirmation() then
    -- rs.setOutput(alarm_side, false)
    ship.mode(4)
    ship.beaconFrequency(freq)
    ship.jump()
    -- ship = nil
  end
  -- rs.setOutput(alarm_side, false)
end

function core_page_jumpToGate()
  ShowTitle("<==== Jump to Jumpgate ====>")
  
  Write("Enter jumpgate name: ")
  local name = readInputText("")
  -- rs.setOutput(alarm_side, true)
  if readConfirmation() then
    -- rs.setOutput(alarm_side, false)
    ship.mode(6)
    ship.targetJumpgate(name)
    ship.jump()
    -- ship = nil
  end
  -- rs.setOutput(alarm_side, false)
end

function core_page()
  ShowTitle(label .. " - Ship status")
  if ship ~= nil then
    WriteLn("")
    X, Y, Z = ship.position()
    WriteLn("Core:")
    WriteLn(" x, y, z          = " .. X .. ", " .. Y .. ", " .. Z)
    local energy, energyMax = ship.energy()
    if energy == nil then energy = 0 end
    if energyMax == nil then energyMax = 1 end
    WriteLn(" Energy           = " .. math.floor(energy / energyMax * 100) .. " % (" .. energy .. "EU)")
    local playersString, playersArray = ship.getAttachedPlayers()
    if playersString == "" then players = "-" end
    WriteLn(" Attached players = " .. playersString)
    WriteLn("")
    WriteLn("Dimensions:")
    WriteLn(" Front, Right, Up = " .. FormatInteger(core_front) .. ", " .. FormatInteger(core_right) .. ", " .. FormatInteger(core_up))
    WriteLn(" Back, Left, Down = " .. FormatInteger(core_back) .. ", " .. FormatInteger(core_left) .. ", " .. FormatInteger(core_down))
    WriteLn(" Size             = " .. core_shipSize .. " blocks")
    WriteLn("")
    WriteLn("Warp data:")
    core_writeMovement()
    local dest = core_computeNewCoordinates(X, Y, Z)
    WriteLn(" Distance         = " .. core_actualDistance .. " (" .. core_jumpCost .. "EU, " .. math.floor(energy / core_jumpCost) .. " jumps)")
    WriteLn(" Dest.coordinates = " .. FormatInteger(dest.x) .. ", " .. FormatInteger(dest.y) .. ", " .. FormatInteger(dest.z))
    if data.core_summon then
      WriteLn(" Summon after     = Yes")
    else
      WriteLn(" Summon after     = No")
    end
  else
    ShowWarning("No ship controller detected")
  end
  
  SetCursorPos(1, 20)
  SetColorTitle()
  ShowMenu("D - Dimensions, N - set ship Name, M - set Movement")
  ShowMenu("J - Jump, G - jump through Gate, B - jump to Beacon")
  ShowMenu("H - Hyperspace, C - summon Crew, T - Toggle summon")
end

function core_key(char, keycode)
  if char == 77 or char == 109 then -- M
    core_page_setMovement()
    core_page_setRotation()
    return true
  elseif char == 84 or char == 116 then -- T
    if data.core_summon then
      data.core_summon = false
    else
      data.core_summon = true
    end
    data_save()
    return true
  elseif char == 68 or char == 100 then -- D
    core_page_setDimensions()
    return true
  elseif char == 74 or char == 106 then -- J
    core_warp()
    return true
  elseif char == 67 or char == 99 or keycode == 46 then -- C
    core_page_summon()
    return true
  elseif char == 66 or char == 98 then -- B
    core_page_jumpToBeacon()
    return true
  elseif char == 71 or char == 103 then -- G
    core_page_jumpToGate()
    return true
  elseif char == 72 or char == 104 then -- H
    -- rs.setOutput(alarm_side, true)
    if readConfirmation() then
      -- rs.setOutput(alarm_side, false)
      ship.mode(5)
      ship.jump()
      -- ship = nil
    end
    -- rs.setOutput(alarm_side, false)
    return true
  elseif char == 78 or char == 110 then -- N
    data_setName()
    return true
  end
  return false
end

----------- Boot sequence

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

ship = nil
for address, componentType in component.list() do
  os.sleep(0)
  Write("Checking " .. componentType .. " ")
  if componentType == "warpdriveShipController" then
    Write("wrapping!")
    ship = component.proxy(address)
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
  if ship == nil then
    SetColorDisabled()
    WriteLn("No ship controller detected")
  else
    SetColorSuccess()
    WriteLn("Ship controller detected")
  end
  
  WriteLn("")
  SetColorTitle()
  WriteLn("Please refer to below menu for keyboard controls")
  WriteLn("For example, press 1 to access Ship core page")
end

-- peripheral boot up
Clear()
connections_page()
SetColorDefault()
WriteLn("")
os.sleep(0)
core_boot()
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
      page = core_page
      keyHandler = core_key
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

-- exiting
if data.core_summon then
  data.core_summon = false
  data_save()
end

if ship ~= nil then
  ship.mode(0)
end

-- clear screens on exit
SetMonitorColorFrontBack(0xFFFFFF, 0x000000)
term.clear()
SetCursorPos(1, 1)
WriteLn("Program terminated")
WriteLn("Type reboot to restart it")
