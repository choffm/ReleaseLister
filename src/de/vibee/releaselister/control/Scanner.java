/*
 * Copyright (C) 2012 Clemens clemens@vibee.de
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

package de.vibee.releaselister.control;


import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import de.vibee.releaselister.model.Release;
import de.vibee.releaselister.model.PathHolder;
import de.vibee.releaselister.model.ReleaseHolder;
import de.vibee.releaselister.model.ReleasePath;
import de.vibee.releaselister.view.ActionFrame;
import de.vibee.releaselister.view.ReleaseLister;


/**
 * Scanner is searching for MP3 Releases which comply with certain rules.
 * Only those MP3 Releases are being added, which match scene rules for naming
 * and contain at least an sfv file (nfo optional).
 * @author Clemens
 */
public class Scanner extends InterruptableRunnable implements Runnable {
    
    private int counter;
    private boolean readTagOption;
    private Set<Release> scannedList;
    private ActionFrame actionFrame;
    ReleaseLister mainWindow;
    long time;
    
    public Scanner(boolean readTagOption, ReleaseLister mainWindow){
        this.readTagOption = readTagOption;
        scannedList = new HashSet<>();
        this.mainWindow = mainWindow;
    }

    /**
     * Recursive scan function, scans the input path recursively for MP3 Releases
     * @param root path where the scan starts recursively
     * @return List of MP3Releases which have been found in current directory
     */
    public void scanForReleases(File root){
        List<File> dirs = new LinkedList<>();
        
        if (root != null){
            
            for (File f : root.listFiles()){
                
                if (this.isInterrupted()){
                    return;
                }
                
                if (f != null){
                    
                    if (f.isDirectory()){
                        
                        if (Pattern.matches(".+-.+-[0-9][0-9]..-.+", f.getName())){ 
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
                            	if (f.getName().contains("-FLAC-")){
                            		scannedList.add(new Release(f,sfv,nfo, readTagOption, Release.FLAC));
                            	}
                            	else{
                            		scannedList.add(new Release(f,sfv,nfo, readTagOption, Release.MP3));
                            	}
                                
                                counter++;
                                long timeTaken = System.currentTimeMillis() - time;
                                actionFrame.setStatusLabelText("Found " +  counter + " Releases (" + ((counter*1000)/(timeTaken)) + " Releases/s)");
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
            mainWindow.setStatusLabel("Path not found");
        }
        
        if (!dirs.isEmpty()){
            
            for (File f : dirs){
                if (this.isInterrupted()){
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
        time = System.currentTimeMillis();
        actionFrame = new ActionFrame(mainWindow);
        actionFrame.getProgressBar().setIndeterminate(true);
        actionFrame.getProgressBar().setMinimum(0);
        actionFrame.getProgressBar().setMaximum(1);
        
        
        actionFrame.setTitle("Searching for Releases...");
        actionFrame.setStatusLabelText("Found 0 Releases");
        actionFrame.setVisible(true);
        
        
        for (ReleasePath aktPath : PathHolder.getInstance().getPathList()) {
            try{
                if (this.isInterrupted()){
                    break;
                }
                if(aktPath.isScanned()){
                    int value = JOptionPane.showConfirmDialog(null, aktPath.getPath().getAbsolutePath() + "had already been scanned. Scan again?", "Really?", JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.YES_OPTION){
                        scanForReleases(aktPath.getPath());
                    }
                    else{
                        for (Release m : ReleaseHolder.getInstance().getReleaseList()){
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
        
        if (this.isInterrupted()){
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
