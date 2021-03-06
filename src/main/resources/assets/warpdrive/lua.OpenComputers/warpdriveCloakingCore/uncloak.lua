local component = require("component")

if not component.isAvailable("warpdriveCloakingCore") then
  print("No cloaking core detected")
else
  local cloakingcore = component.warpdriveCloakingCore
  cloakingcore.tier(2)
  cloakingcore.enable(true)
  if cloakingcore.isAssemblyValid() then
    print("Cloaking is disabled")
  else
    print("Invalid assembly")
    print()
    print("In each of the 6 directions, you need to place exactly 2 Cloaking device coils, for a total of 12 coils.")
    print("The 6 inner coils shall be exactly one block away from the core.")
    print("The cloaking field will extend 5 blocks past the outer 6 coils.")
    print("Power consumption scales with the amount of cloaked blocks.")
  end
end

print("")