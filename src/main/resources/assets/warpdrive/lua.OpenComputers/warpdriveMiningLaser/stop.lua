local component = require("component")
local computer = require("computer")
local term = require("term")

if not term.isAvailable() then
  computer.beep()
  return
end

mininglasers = {}
for address,type in component.list("warpdriveMiningLaser", true) do
  table.insert(mininglasers, component.proxy(address))
end

local file = io.open("/etc/hostname")
if file then
  label = file:read("*l")
  file:close()
else
  label = "" .. computer.address()
end

term.clear()

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


textOut(1, 1, label, 0x00FF00, 0x000000)
print("")

if #mininglasers == 0 then
  computer.beep()
  textOut(1, 2, "No mining laser detected", 0xFF0000, 0x000000)
else
  for key,mininglaser in pairs(mininglasers) do
	statusString, isActive = mininglaser.state()
    if not isActive then
      textOut(1, 2 + key, "Mining laser " .. key .. " of " .. #mininglasers .. " is already stopped", 0xFFFFFF, 0xFF0000)
    else
      mininglaser.stop()
      textOut(1, 2 + key, "Mining laser " .. key .. " of " .. #mininglasers .. " has been stopped", 0x0000FF, 0x00FF00)
	end
	print("")
  end
end
textOut(1, 1, "", 0xFFFFFF, 0x000000)

print("")
print("")
