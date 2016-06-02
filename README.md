WarpDrive for 1.7
=========
[![Build Status](https://travis-ci.org/LemADEC/WarpDrive.svg?branch=MC1.7)](https://travis-ci.org/LemADEC/WarpDrive)

An update to the WarpDrive mod. Currently in progress.
Will work almost exactly like the original, but with improvements!


If you would like to help, find an issue and then fork the repository. If you can fix it, submit a pull request and we will accept it! This is valid even if you dont know how to code, modifications to textures, resources, wikis, and everything else are up for improvment.

See mcmod.info for credits




Installation
============
1. Download WarpDrive.jar from the (Curse website)[http://minecraft.curseforge.com/projects/warpdrive]  and put it in your mods folder
2. You'll need either ComputerCraft or OpenComputer.
3. EU and RF power are supported (including but not limited to IC2, Gregtech, AdvancedSolarPanel, AtomicScience, BigReactors, EnderIO, Thermal Expansion, ImmersiveEngineering). ICBM, MFFS, Advanced Repulsion System, Advanced Solar Panels and GraviSuite are supported.


Developping
===========

To setup you development environment:
1. From the WarpDrive mod folder, type:
```
./gradlew setupDecompWorkspace
./gradlew eclipse
```
2. Copy the WarpDrive/eclipse/mods folder to your target mods folder. Specificaly, you need the fake WarpDriveCore-dev.jar; it'll  force FML to run the integrated coremod which is required to  enable gravity and breathing hooks.
3. Import the code formating rules from Eclipse-nowrap.xml.
3. Start Minecraft with `GradleStart` class
