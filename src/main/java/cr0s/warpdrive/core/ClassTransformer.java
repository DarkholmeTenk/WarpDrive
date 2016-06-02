package cr0s.warpdrive.core;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassTransformer implements net.minecraft.launchwrapper.IClassTransformer {
	private HashMap<String, String> nodeMap = new HashMap<>();
	
	private static final String GRAVITY_MANAGER_CLASS = "cr0s/warpdrive/GravityManager";
	private static final String CLOAK_MANAGER_CLASS = "cr0s/warpdrive/data/CloakManager";
	private boolean debugLog = false;
	
	public ClassTransformer() {
		nodeMap.put("EntityLivingBase.class", "sv");
		nodeMap.put("moveEntityWithHeading.name", "func_70612_e");
		nodeMap.put("moveEntityWithHeading.desc", "(FF)V");
		
		nodeMap.put("EntityItem.class", "xk");
		nodeMap.put("onUpdate.name", "func_70071_h_");
		nodeMap.put("onUpdate.desc", "()V");
		
		nodeMap.put("WorldClient.class", "bjf");
		nodeMap.put("func_147492_c.name", "func_147492_c");
		nodeMap.put("func_147492_c.desc", "(IIILnet/minecraft/block/Block;I)Z");
		nodeMap.put("setBlock.name", "func_147465_d");
		
		nodeMap.put("Chunk.class", "apx");
		nodeMap.put("fillChunk.name", "func_76607_a");
		nodeMap.put("fillChunk.desc", "([BIIZ)V");
		nodeMap.put("generateHeightMap.name", "func_76590_a");
		nodeMap.put("generateHeightMap.desc", "()V");
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (nodeMap == null) {
			FMLLoadingPlugin.logger.info("Nodemap is null, transformation cancelled");
			return bytes;
		}
		
		// if (debugLog) { FMLLoadingPlugin.logger.info("Checking " + name); }
		switch (transformedName) {
			case "net.minecraft.entity.EntityLivingBase":
				bytes = transformMinecraftEntityLivingBase(bytes);
				break;
			case "net.minecraft.entity.item.EntityItem":
				bytes = transformMinecraftEntityItem(bytes);
				break;
			case "com.creativemd.itemphysic.physics.ServerPhysic":
				bytes = transformItemPhysicEntityItem(bytes);
				break;
			case "micdoodle8.mods.galacticraft.core.util.WorldUtil":
				bytes = transformGalacticraftWorldUtil(bytes);
				break;
			case "net.minecraft.client.multiplayer.WorldClient":
				bytes = transformMinecraftWorldClient(bytes);
				break;
			case "net.minecraft.world.chunk.Chunk":
				bytes = transformMinecraftChunk(bytes);
				break;
		}
		
		return bytes;
	}
	
	private byte[] transformMinecraftEntityLivingBase(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		int operationCount = 1;
		int injectedCount = 0;
		Iterator methods = classNode.methods.iterator();
		
		do {
			if (!methods.hasNext()) {
				break;
			}
			
			MethodNode methodNode = (MethodNode) methods.next();
			// if (debugLog) { FMLLoadingPlugin.logger.info("- Method " + methodNode.name + " " + methodNode.desc); }
			
			if ( (methodNode.name.equals(nodeMap.get("moveEntityWithHeading.name")) || methodNode.name.equals("moveEntityWithHeading"))
			  && methodNode.desc.equals(nodeMap.get("moveEntityWithHeading.desc")) ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodNode.instructions.size()) {
					AbstractInsnNode abstractNode = methodNode.instructions.get(instructionIndex);
					
					if (abstractNode instanceof LdcInsnNode) {
						LdcInsnNode nodeAt = (LdcInsnNode) abstractNode;
						
						if (nodeAt.cst.equals(Double.valueOf(0.080000000000000002D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getGravityForEntity",
									"(Lnet/minecraft/entity/Entity;)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
					}
					
					instructionIndex++;
				}
			}
		} while (true);
		
		if (injectedCount != operationCount) {
			FMLLoadingPlugin.logger.info("Injection failed for " + classNode.name + " (" + injectedCount + " / " + operationCount + "), aborting...");
		} else {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			bytes = writer.toByteArray();
			FMLLoadingPlugin.logger.info("Successful injection in " + classNode.name);
		}
		return bytes;
	}
	
	private byte[] transformMinecraftEntityItem(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		int operationCount = 2;
		int injectedCount = 0;
		Iterator methods = classNode.methods.iterator();
		
		do {
			if (!methods.hasNext()) {
				break;
			}
			
			MethodNode methodNode = (MethodNode) methods.next();
			// if (debugLog) { FMLLoadingPlugin.logger.info("- Method " + methodNode.name + " " + methodNode.desc); }
			
			if ( (methodNode.name.equals(nodeMap.get("onUpdate.name")) || methodNode.name.equals("onUpdate"))
			  && methodNode.desc.equals(nodeMap.get("onUpdate.desc")) ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodNode.instructions.size()) {
					AbstractInsnNode abstractNode = methodNode.instructions.get(instructionIndex);
					
					if (abstractNode instanceof LdcInsnNode) {
						LdcInsnNode nodeAt = (LdcInsnNode) abstractNode;
						
						if (nodeAt.cst.equals(Double.valueOf(0.039999999105930328D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getItemGravity",
									"(L" + "net/minecraft/entity/item/EntityItem" + ";)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
						
						if (nodeAt.cst.equals(Double.valueOf(0.98000001907348633D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getItemGravity2",
									"(L" + "net/minecraft/entity/item/EntityItem" + ";)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
					}
					
					instructionIndex++;
				}
			}
		} while (true);
		
		if (injectedCount != operationCount) {
			FMLLoadingPlugin.logger.info("Injection failed for " + classNode.name + " (" + injectedCount + " / " + operationCount + "), aborting...");
		} else {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			bytes = writer.toByteArray();
			FMLLoadingPlugin.logger.info("Successful injection in " + classNode.name);
		}
		return bytes;
	}
	
	private byte[] transformItemPhysicEntityItem(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		int operationCount = 2;
		int injectedCount = 0;
		Iterator methods = classNode.methods.iterator();
		
		do {
			if (!methods.hasNext()) {
				break;
			}
			
			MethodNode methodNode = (MethodNode) methods.next();
			// if (debugLog) { FMLLoadingPlugin.logger.info("- Method " + methodNode.name + " " + methodNode.desc); }
			
			if ( (methodNode.name.equals("update"))
			  && methodNode.desc.equals("(Lnet/minecraft/entity/item/EntityItem;)V") ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodNode.instructions.size()) {
					AbstractInsnNode abstractNode = methodNode.instructions.get(instructionIndex);
					
					if (abstractNode instanceof LdcInsnNode) {
						LdcInsnNode nodeAt = (LdcInsnNode) abstractNode;
						
						if (nodeAt.cst.equals(Double.valueOf(0.04D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getItemGravity",
									"(L" + "net/minecraft/entity/item/EntityItem" + ";)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
						
						if (nodeAt.cst.equals(Double.valueOf(0.98D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getItemGravity2",
									"(L" + "net/minecraft/entity/item/EntityItem" + ";)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
					}
					
					instructionIndex++;
				}
			}
		} while (true);
		
		if (injectedCount != operationCount) {
			FMLLoadingPlugin.logger.info("Injection failed for " + classNode.name + " (" + injectedCount + " / " + operationCount + "), aborting...");
		} else {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			bytes = writer.toByteArray();
			FMLLoadingPlugin.logger.info("Successful injection in " + classNode.name);
		}
		return bytes;
	}
	
	private byte[] transformGalacticraftWorldUtil(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		int operationCount = 3 + 2 + 0;
		int injectedCount = 0;
		Iterator methods = classNode.methods.iterator();
		
		do {
			if (!methods.hasNext()) {
				break;
			}
			
			MethodNode methodNode = (MethodNode) methods.next();
			// if (debugLog) { FMLLoadingPlugin.logger.info("- Method " + methodNode.name + " " + methodNode.desc); }

			// Entities gravity
			if ( (methodNode.name.equals("getGravityForEntity"))
			  && methodNode.desc.equals("(Lnet/minecraft/entity/Entity;)D") ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodNode.instructions.size()) {
					AbstractInsnNode abstractNode = methodNode.instructions.get(instructionIndex);
					
					if (abstractNode instanceof LdcInsnNode) {
						LdcInsnNode nodeAt = (LdcInsnNode) abstractNode;
						
						if (nodeAt.cst.equals(Double.valueOf(0.08D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getGravityForEntity",
									"(Lnet/minecraft/entity/Entity;)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
					}
					
					instructionIndex++;
				}
			}
			
			// Items gravity
			if ( (methodNode.name.equals("getItemGravity"))
			  && methodNode.desc.equals("(Lnet/minecraft/entity/item/EntityItem;)D") ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodNode.instructions.size()) {
					AbstractInsnNode abstractNode = methodNode.instructions.get(instructionIndex);
					
					if (abstractNode instanceof LdcInsnNode) {
						LdcInsnNode nodeAt = (LdcInsnNode) abstractNode;
						
						if (nodeAt.cst.equals(Double.valueOf(0.03999999910593033D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getItemGravity",
									"(L" + "net/minecraft/entity/item/EntityItem" + ";)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
						/*
						if (nodeAt.cst.equals(Double.valueOf(0.98D))) {
							VarInsnNode beforeNode = new VarInsnNode(Opcodes.ALOAD, 0);
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									GRAVITY_MANAGER_CLASS,
									"getItemGravity2",
									"(L" + "net/minecraft/entity/item/EntityItem" + ";)D",
									false);
							methodNode.instructions.insertBefore(nodeAt, beforeNode);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
						/**/
					}
					
					instructionIndex++;
				}
			}
		} while (true);
		
		if (injectedCount < operationCount) {// https://github.com/micdoodle8/Galacticraft/commit/d0e7e9ae932092e8a4584bac43338d2dc1bbfe23 added 2 occurrences
			FMLLoadingPlugin.logger.info("Injection failed for " + classNode.name + " (" + injectedCount + " / " + operationCount + "), aborting...");
		} else {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			bytes = writer.toByteArray();
			FMLLoadingPlugin.logger.info("Successful injection in " + classNode.name);
		}
		return bytes;
	}
	
	private byte[] transformMinecraftWorldClient(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		int operationCount = 1;
		int injectedCount = 0;
		Iterator methods = classNode.methods.iterator();
		
		do {
			if (!methods.hasNext()) {
				break;
			}
			
			MethodNode methodNode = (MethodNode) methods.next();
			// if (debugLog) { FMLLoadingPlugin.logger.info("- Method " + methodNode.name + " " + methodNode.desc); }
			
			if ( (methodNode.name.equals(nodeMap.get("func_147492_c.name")) || methodNode.name.equals("func_147492_c"))
			  && methodNode.desc.equals(nodeMap.get("func_147492_c.desc")) ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodNode.instructions.size()) {
					AbstractInsnNode abstractNode = methodNode.instructions.get(instructionIndex);
					
					if (abstractNode instanceof MethodInsnNode) {
						MethodInsnNode nodeAt = (MethodInsnNode) abstractNode;
						
						if (nodeAt.name.equals(nodeMap.get("setBlock.name")) || nodeAt.name.equals("setBlock")) {
							MethodInsnNode overwriteNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									CLOAK_MANAGER_CLASS,
									"onBlockChange",
									"(IIILnet/minecraft/block/Block;II)Z",
									false);
							methodNode.instructions.set(nodeAt, overwriteNode);
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodNode.name + " " + methodNode.desc); }
							injectedCount++;
						}
					}
					
					instructionIndex++;
				}
			}
		} while (true);
		
		if (injectedCount != operationCount) {
			FMLLoadingPlugin.logger.info("Injection failed for " + classNode.name + " (" + injectedCount + " / " + operationCount + "), aborting...");
		} else {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			bytes = writer.toByteArray();
			FMLLoadingPlugin.logger.info("Successful injection in " + classNode.name);
		}
		return bytes;
	}
	
	private byte[] transformMinecraftChunk(byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		int operationCount = 1;
		int injectedCount = 0;
		Iterator methods = classNode.methods.iterator();
		
		do {
			if (!methods.hasNext()) {
				break;
			}
			
			MethodNode methodnode = (MethodNode) methods.next();
			if (debugLog) { FMLLoadingPlugin.logger.info("- Method " + methodnode.name + " " + methodnode.desc); }
			
			if ( (methodnode.name.equals(nodeMap.get("fillChunk.name")) || methodnode.name.equals("fillChunk"))
			  && methodnode.desc.equals(nodeMap.get("fillChunk.desc")) ) {
				if (debugLog) { FMLLoadingPlugin.logger.info("Method found!"); }
				
				int instructionIndex = 0;
				
				while (instructionIndex < methodnode.instructions.size()) {
					AbstractInsnNode abstractNode = methodnode.instructions.get(instructionIndex);
					if (debugLog) { deasm(abstractNode); }
					
					if (abstractNode instanceof MethodInsnNode) {
						MethodInsnNode nodeAt = (MethodInsnNode) abstractNode;
						
						if ( (nodeAt.name.equals(nodeMap.get("generateHeightMap.name")) || nodeAt.name.equals("generateHeightMap"))
						  && nodeAt.desc.equals(nodeMap.get("generateHeightMap.desc")) ) {
							MethodInsnNode insertMethodNode = new MethodInsnNode(
									Opcodes.INVOKESTATIC,
									CLOAK_MANAGER_CLASS,
									"onFillChunk",
									"(Lnet/minecraft/world/chunk/Chunk;)V",
									false);
							methodnode.instructions.insertBefore(nodeAt, insertMethodNode);
							instructionIndex++;
							
							VarInsnNode insertVarNode = new VarInsnNode(Opcodes.ALOAD, 0);
							methodnode.instructions.insertBefore(nodeAt, insertVarNode);
							instructionIndex++;
							
							if (debugLog) { FMLLoadingPlugin.logger.info("Injecting into " + classNode.name + "." + methodnode.name + " " + methodnode.desc); }
							injectedCount++;
						}
					}
					
					instructionIndex++;
				}
			}
		} while (true);
		
		if (injectedCount != operationCount) {
			FMLLoadingPlugin.logger.info("Injection failed for " + classNode.name + " (" + injectedCount + " / " + operationCount + "), aborting...");
		} else {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS); // | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(writer);
			bytes = writer.toByteArray();
			FMLLoadingPlugin.logger.info("Successful injection in " + classNode.name);
		}
		return bytes;
	}
	
	private static void deasm(AbstractInsnNode abstractNode) {
		if (abstractNode instanceof VarInsnNode) {
			VarInsnNode node = (VarInsnNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Var " + node.var);
			
		} else if (abstractNode instanceof LabelNode) {
			LabelNode node = (LabelNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Label " + node.getLabel());
			
		} else if (abstractNode instanceof LineNumberNode) {
			LineNumberNode node = (LineNumberNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Line " + node.line);
			
		} else if (abstractNode instanceof InsnNode) {
			InsnNode node = (InsnNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Instruction " + node);
			
		} else if (abstractNode instanceof LdcInsnNode) {
			LdcInsnNode node = (LdcInsnNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Load " + node.cst);
			
		} else if (abstractNode instanceof FieldInsnNode) {
			FieldInsnNode node = (FieldInsnNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Field " + node.owner + " " + node.name + " " + node.desc);
			
		} else if (abstractNode instanceof MethodInsnNode) {
			MethodInsnNode node = (MethodInsnNode) abstractNode;
			FMLLoadingPlugin.logger.info("  + Method " + node.owner + " " + node.name + " " + node.desc);
			
		} else {
			FMLLoadingPlugin.logger.info("  + Instruction " + abstractNode.getOpcode() + " " + abstractNode.getType() + " " + abstractNode);
		}

	}
}
