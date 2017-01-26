package cr0s.warpdrive.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import cr0s.warpdrive.WarpDrive;
import net.minecraft.util.math.BlockPos;

public final class JumpgatesRegistry {
    private File file;
    private ArrayList<Jumpgate> gates = new ArrayList<>();
    
    public JumpgatesRegistry() {
        file = new File("gates.txt");
        WarpDrive.logger.info("Opening gates file '" + file + "'");
        
        if (file != null && !file.exists()) {
        	try {
				file.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
        }
        
        try {
            loadGates();
        } catch (IOException exception) {
            Logger.getLogger(JumpgatesRegistry.class.getName()).log(Level.SEVERE, null, exception);
        }
    }
    
    public void saveGates() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(file));

        // Write each string in the array on a separate line
        for (Jumpgate jg : gates) {
            out.println(jg);
        }

        out.close();
    }
    
    public void loadGates() throws IOException {
    	WarpDrive.logger.info("Loading jump gates from gates.txt...");
        BufferedReader bufferedreader;
        bufferedreader = new BufferedReader(new FileReader(file));
        String s1;

        while ((s1 = bufferedreader.readLine()) != null) {
            gates.add(new Jumpgate(s1));
        }

        bufferedreader.close();
        WarpDrive.logger.info("Loaded " + gates.size() + " jump gates.");
    }
    
    public void addGate(Jumpgate jg) {
        gates.add(jg);
    }
    
    public boolean addGate(String name, BlockPos blockPos) {
        // Gate already exists
        if (findGateByName(name) != null) {
            return false;
        }

        addGate(new Jumpgate(name, blockPos));

        try {
            saveGates();
        } catch (IOException ex) {
            Logger.getLogger(JumpgatesRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
    
    public void removeGate(String name) {
        Jumpgate jg;

        for (int i = 0; i < gates.size(); i++) {
            jg = gates.get(i);

            if (jg.name.equalsIgnoreCase(name))
            {
                gates.remove(i);
                return;
            }
        }

        try {
            saveGates();
        } catch (IOException ex) {
            Logger.getLogger(JumpgatesRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Jumpgate findGateByName(String name) {
        for (Jumpgate jg : gates) {
            if (jg.name.equalsIgnoreCase(name)) {
                return jg;
            }
        }

        return null;
    }
    
    public String JumpgatesList() {
        String result = "";

        for (Jumpgate jg : gates) {
            result += jg.toNiceString() + "\n";
        }

        return result;
    }
    
    public String commaList() {
    	String result = "";
    	if (gates.isEmpty()) {
    		result += "<none> (check /generate to create one)";
    	} else {
	    	for (Jumpgate jg : gates) {
	    		result += jg.toNiceString() + ",";
	    	}
    	}
    	return result;
    }
    
    public Jumpgate findNearestGate(BlockPos blockPos) {
        double minDistance2 = -1;
        Jumpgate res = null;
        
        for (Jumpgate jg : gates) {
            double dX = jg.xCoord - blockPos.getX();
            double dY = jg.yCoord - blockPos.getY();
            double dZ = jg.zCoord - blockPos.getZ();
            double distance2 = dX * dX + dY * dY + dZ * dZ;

            if ((minDistance2 == -1) || (distance2 < minDistance2)) {
                minDistance2 = distance2;
                res = jg;
            }
        }
        
        return res;
    }
}
