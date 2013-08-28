/*
 * Copyright (C) 2012 vibee clemens@v.de1.cc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cc.de1.v.releaselister.control;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.vibee.releaselister.model.PathHolder;
import de.vibee.releaselister.model.ReleaseHolder;

/**
 * Accepted are zip files with .list ending if and only if they contain one file
 * called "rlr" and one file called "rlp"
 * @author vibee
 */
public class Serializer {

    /**
     * Serializes PathHolder and ReleaseHolder to output File
     * @param outputFile output .rlist file
     */
    public void serialize(String outputFile) {
        ZipOutputStream zipout = null;
        ObjectOutputStream oos = null;
        try {
            String outFilename = outputFile;
            if (!new File(outputFile).getParentFile().exists()){
                new File(outputFile).getParentFile().mkdirs();
            }
            zipout = new ZipOutputStream(new FileOutputStream(outFilename));
            zipout.putNextEntry(new ZipEntry("rlp"));
            oos = new ObjectOutputStream(zipout);
            oos.writeObject(PathHolder.getInstance());
            zipout.putNextEntry(new ZipEntry("rlr"));
            oos = new ObjectOutputStream(zipout);
            oos.writeObject(ReleaseHolder.getInstance());

        } catch (IOException ex) {
            Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            try {
                zipout.close();
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    /**
     * Deserializes a .rlist file to PathHolder and ReleaseHolder objects
     * and sets PathHolder and ReleaseHolder instances, if inputFile is a valid 
     * ReleaseLister file
     * @param inputFile 
     */
    public void deserialize(String inputFile) {
        if (new File(inputFile).exists()){
            ZipInputStream zipin = null;
            try {
                PathHolder ph = null;
                ReleaseHolder rh = null;
                 zipin = new ZipInputStream(new FileInputStream(new File(inputFile)));
                ZipEntry zipentry = zipin.getNextEntry();
                while (zipentry != null){
                    if (zipentry.getName().compareTo("rlr") == 0){
                        ObjectInputStream ois = new ObjectInputStream(zipin);
                        rh = (ReleaseHolder)ois.readObject();
                    }
                    else if (zipentry.getName().compareTo("rlp") == 0){
                        ObjectInputStream ois = new ObjectInputStream(zipin);
                        ph = (PathHolder)ois.readObject();
                    }
                    else{
                        System.out.println("No ReleaseLister File");
                        return;
                    }
                    zipentry = zipin.getNextEntry();
                }
                if (rh != null && ph != null){
                    PathHolder.setInstance(ph);
                    ReleaseHolder.setInstance(rh);
                }
                else{
                    System.out.println("No ReleaseLister File");
                }
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally{
                try {
                    zipin.close();
                } catch (IOException ex) {
                    Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
  
