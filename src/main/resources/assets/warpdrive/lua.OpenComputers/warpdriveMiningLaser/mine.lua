local component = require("component")
local computer = require("computer")
local term = require("term")

if not term.isAvailable() then
  computer.beep()
  return
end

mininglasers = {}
for address,type in component.list("warpdriveMiningLaser", true) do
  print("Wrapping " .. address)
  table.insert(mininglasers, component.proxy(address))
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
layerOffset = 1
onlyOres = false
silktouch = false
args = {...}
if #args > 0 then
  if args[1] == "help" or args[1] == "?" then
    print("Usage: mine <layerOffset> <onlyOres> <silktouch>")
    print()
    print("Miner always mine below it, down to bedrock.")
	print("Set layerOffset to define starting level.")
    print("Power consumption will be much lower in space.")
    print("Mining only ores is faster but more expensive...")
    print("Mining laser can't go through forcefields.")
    print("Mined chests will drop their contents.")
    print()
	noExit = false
  else
    layerOffset = tonumber( args[1] ) or 1
  end
  
  if #args > 1 then
    if args[2] == "true" or args[2] == "1" then
      onlyOres = true
    end
  end
  
  if #args > 2 then
    if args[3] == "true" or args[3] == "1" then
      silktouch = true
    end
  end
end

if #mininglasers == 0 then
  computer.beep()
  textOut(1, 2, "No mining laser detected", 0xFFFFFF, 0xFF0000)
  noExit = false
end
if noExit then
  for key,mininglaser in pairs(mininglasers) do
    statusString, isActive = mininglaser.state()
    if not isActive then
      mininglaser.offset(layerOffset)
      mininglaser.onlyOres(onlyOres)
      mininglaser.silktouch(silktouch)
      
      mininglaser.start()
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
    for key,mininglaser in pairs(mininglasers) do
      status, isActive, energy, currentLayer, mined, total = mininglaser.state()
      
      term.clear()
      textOut(1, 1, label .. " - Mining laser " .. key .. " of " .. #mininglasers, 0x0000FF, 0x00FF00)
      textOut(1, 3, "Status: " .. status .. "   ", 0x0000FF, 0x000000)
      textOut(1, 4, "Energy level is " .. energy .. " EU", 0x0000FF, 0x000000)
      textOut(1, 7, "Mined " .. mined .. " out of " .. total .. " blocks at layer " .. currentLayer .. "   ", 0xFFFFFF, 0x000000)
      
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
