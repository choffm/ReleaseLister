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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import de.vibee.releaselister.model.MP3Release;
import de.vibee.releaselister.model.PathHolder;
import de.vibee.releaselister.model.ReleaseHolder;
import de.vibee.releaselister.model.ReleasePath;
import de.vibee.releaselister.view.ActionFrame;
import de.vibee.releaselister.view.ReleaseLister;


/**
 * Scanner is searching for MP3 Releases which comply with certain rules.
 * Only those MP3 Releases are being added, which match scene rules for naming
 * and contain at least an sfv file (nfo optional).
 * @author vibee
 */
public class Scanner extends InterruptableRunnable implements Runnable {
    
    private int counter;
    private boolean readTagOption;
    private List<MP3Release> scannedList;
    private ActionFrame actionFrame;
    
    public Scanner(boolean readTagOption){
        this.readTagOption = readTagOption;
        scannedList = new LinkedList<>();
    }

    /**
     * Recursive scan function, scans the input path recursively for MP3 Releases
     * @param path path where the scan starts recursively
     * @return List of MP3Releases which have been found in current directory
     */
    public void scanForReleases(File path){
        List<File> dirs = new LinkedList<>();
        
        if (path != null){
            
            for (File f : path.listFiles()){
                
                if (ReleaseLister.getInstance().getInterruptableRunnable().isInterrupted()){
                    return;
                }
                
                if (f != null){
                    
                    if (f.isDirectory()){
                        
                        if (Pattern.matches(".+-.+-[0-9][0-9]..-.+", f.getName()) && !f.getName().contains("-FLAC-")){ 
                            File sfv = null;
                            File nfo = null;
                            
                            for (File m : f.listFiles()){
                                if (m.isDirectory()){
                                    dirs.add(m);
                                }
                                else if (m.getName().endsWith(".sfv")){
                                    sfv = m;
                                }
                                else if (m.getName().endsWith(".nfo")){
                                    nfo = m;
                                }
                            }
                            
                            if (sfv != null){
                                scannedList.add(new MP3Release(f,sfv,nfo, readTagOption));
                                counter++;
                                actionFrame.setStatusLabelText("Found " +  counter + " Releases");
                            }
                            
                        }
                        
                        else{
                            dirs.add(f);
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        else{
            ReleaseLister.getInstance().setStatusLabel("Path not found");
        }
        
        if (dirs != null){
            
            for (File f : dirs){
                if (ReleaseLister.getInstance().getInterruptableRunnable().isInterrupted()){
                    return;
                }
                scanForReleases(f);
            }
            
        }
        
    }

    /**
     * Resets the counter. Is called once at scan start
     */
    public void resetCounter() {
        this.counter = 0;
    }
    
    @Override
    public void run() {
        resetCounter();
        actionFrame = new ActionFrame();
        actionFrame.getProgressBar().setIndeterminate(true);
        actionFrame.getProgressBar().setMinimum(0);
        actionFrame.getProgressBar().setMaximum(1);
        
        
        actionFrame.setTitle("Searching for Releases...");
        actionFrame.setStatusLabelText("Found 0 Releases");
        actionFrame.setVisible(true);
        
        
        for (ReleasePath aktPath : PathHolder.getInstance().getPathList()) {
            try{
                if (ReleaseLister.getInstance().getInterruptableRunnable().isInterrupted()){
                    break;
                }
                if(aktPath.isScanned()){
                    int value = JOptionPane.showConfirmDialog(null, aktPath.getPath().getAbsolutePath() + "had already been scanned. Scan again?", "Really?", JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.YES_OPTION){
                        scanForReleases(aktPath.getPath());
                    }
                    else{
                        for (MP3Release m : ReleaseHolder.getInstance().getReleaseList()){
                            if (m.getRelease().getAbsolutePath().length() >= aktPath.getPath().getAbsolutePath().length()){
                                if (m.getRelease().getAbsolutePath().substring(0, aktPath.getPath().getAbsolutePath().length()).compareTo(aktPath.getPath().getAbsolutePath()) == 0){
                                    scannedList.add(m);
                                }
                            }
                        }
                    }
                }
                else{
                    scanForReleases(aktPath.getPath());
                    aktPath.setScanned(true);
                }
                
                
            }
            catch (Exception e){
                Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        
        actionFrame.getProgressBar().setIndeterminate(false);
        
        if (ReleaseLister.getInstance().getInterruptableRunnable().isInterrupted()){
            actionFrame.setStatusLabelText("Search aborted.");
            actionFrame.setOkButtonEnabled(true);
            actionFrame.getProgressBar().setValue(0);
            
        }
        else{
            actionFrame.setAbortButtonEnabled(false);
            actionFrame.setOkButtonEnabled(true);
            ReleaseHolder.getInstance().setReleaseList(scannedList);
            actionFrame.getProgressBar().setValue(1);
        }
        
        
    }
    
}
