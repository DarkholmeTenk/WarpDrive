local component = require("component")
local computer = require("computer")
local term = require("term")

if not term.isAvailable() then
  computer.beep()
  return
end

treefarms = {}
for address,type in component.list("warpdriveLaserTreeFarm", true) do
  print("Wrapping " .. address)
  table.insert(treefarms, component.proxy(address))
end

function textOut(x, y, text, fg, bg)
  if term.isAvailable() then
    local w, h = component.gpu.getResolution()
    if w then
      component.gpu.setBackground(bg)
      component.gpu.setForeground(fg)
      component.gpu.set(x, y, text)
      component.gpu.setBackground(0x000000)
    end
  end
end


noExit = true
breakLeaves = true
tapTrees = true
silktouch = false
args = {...}
if #args > 0 then
  if args[1] == "help" or args[1] == "?" then
    print("Usage: farm <breakLeaves> <tapTrees> <silktouch>")
    print()
    print("Farmer always farm above it.")
    print("Use 'true' or '1' to enable an option.")
    print("Use 'false' or '0' to disable an option.")
    print("Default is to break leaves and tap rubber trees.")
    print("Sapplings will be automatically replanted.")
    print("Farming automatically stops when inventory is full.")
    print()
    noExit = false
  else
    if args[1] == "false" or args[1] == "0" then
      breakLeaves = false
    end
  end
  
  if #args > 1 then
    if args[2] == "false" or args[2] == "0" then
      tapTrees = false
    end
  end
  
  if #args > 2 then
    if args[3] == "true" or args[3] == "1" then
      silktouch = true
    end
  end
end

if #treefarms == 0 then
  computer.beep()
  textOut(1, 2, "No laser tree farm detected", 0xFFFFFF, 0xFF0000)
  noExit = false
end
if noExit then
  for key,treefarm in pairs(treefarms) do
    statusString, isActive = treefarm.state()
    if not isActive then
      treefarm.breakLeaves(breakLeaves)
      treefarm.tapTrees(tapTrees)
      treefarm.silktouch(silktouch)
      
      treefarm.start()
    end
  end
  os.sleep(1)
end

local file = io.open("/etc/hostname")
if file then
  label = file:read("*l")
  file:close()
else
  label = "" .. computer.address()
end

if noExit then
  repeat
    isActive = false
    for key,treefarm in pairs(treefarms) do
      status, isActive, energy, totalHarvested, currentValuable, totalValuables = treefarm.state()
      
      term.clear()
      textOut(1, 1, label .. " - Laser tree farm " .. key .. " of " .. #treefarms, 0x0000FF, 0x00FF00)
      textOut(1, 3, "Status: " .. status .. "   ", 0x0000FF, 0x000000)
      textOut(1, 4, "Energy level is " .. energy .. " EU", 0x0000FF, 0x000000)
      textOut(1, 7, "Farmed " .. currentValuable .. " out of " .. totalValuables .. " blocks   ", 0xFFFFFF, 0x000000)
      textOut(1, 9, "Harvested " .. totalHarvested .. " items and counting...   ", 0xFFFFFF, 0x000000)
      
      if isActive then
        os.sleep(1)
      else
        os.sleep(0.1)
      end
    end
  until not isActive
end

textOut(1, 1, "", 0xFFFFFF, 0x000000)

print("")
print("")
