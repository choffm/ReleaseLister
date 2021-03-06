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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import de.vibee.releaselister.model.Release;
import de.vibee.releaselister.model.AudioFileWithChecksum;
import de.vibee.releaselister.view.ActionFrame;
import de.vibee.releaselister.view.ReleaseLister;

/**
 * The CRC Checker scans one release by parsing the sfv file and checking each 
 * mp3 file, which is listet in this sfv. Sets valid status for both release
 * and containing mp3 files. A release is valid if and only if all mp3s
 * exist and are valid.
 * @author Clemens
 */
public class CRCChecker extends InterruptableRunnable{
    
    private ActionFrame actionFrame;
    int counter = 0;
    int countValid = 0;
    int countInvalid = 0;
    List<Release> toCheck;
    long time = 0;
    long timeGone = 0;
    long size = 0;
    ReleaseLister mainWindow;
    
    public CRCChecker(List<Release> toCheck, ReleaseLister mainWindow){
    	this.mainWindow = mainWindow;
        this.toCheck = toCheck;
    }
    
    /**
     * Verifies an MP3 Release
     * @param release Release to verify
     */
    public void checkSfv(Release release){
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        if (!release.isReleaseComplete()){
            release.setReleaseIsValid(false);
            return;
        }
        
        boolean isValid = true;
        for (AudioFileWithChecksum m : release.getAudioFiles()){
            if (!m.getAudioFileExists()){
                isValid = false;
            }
            else{
                try {
                    if (m.getChecksum() == FileUtils.checksumCRC32(m.getAudioFile())){
                        m.setAudioFileIsValid(true);
                    }
                    else{
                        m.setAudioFileIsValid(false);
                        isValid = false;
                    }

                } catch (IOException ex) {
                    Logger.getLogger(CRCChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
        
        release.setReleaseIsValid(isValid);
        
    }

    @Override
    public void run() {
        
        actionFrame = new ActionFrame(mainWindow);
        actionFrame.setTitle("Verifying Releases...");
        actionFrame.getProgressBar().setIndeterminate(false);
        actionFrame.getProgressBar().setMinimum(0);
        actionFrame.getProgressBar().setMaximum(toCheck.size());
        actionFrame.getProgressBar().setValue(0);
        actionFrame.setVisible(true);
        time = System.currentTimeMillis() - 1;
        
        for (Release release : toCheck){
            timeGone = System.currentTimeMillis() - time;
            actionFrame.setStatusLabelText("Veryfied " + counter + " of " + 
                    toCheck.size() + " Releases (" + size / timeGone / 1024 + " MB/s)");
            if (this.isInterrupted()){
                actionFrame.setStatusLabelText("Verify aborted.");
                actionFrame.setOkButtonEnabled(true);
                return;
            }
                
            checkSfv(release);
            size += release.getSize();
            counter++;
            actionFrame.getProgressBar().setValue(counter);
            if (release.isValid()){
                countValid++;
            }
            else {
                countInvalid++;
            }
           
        }
        
        actionFrame.setStatusLabelText(countValid + " valid and " + countInvalid + 
                " invalid Releases verfied (" + size / timeGone / 1024 + " MB/s).");
        actionFrame.setAbortButtonEnabled(false);
        actionFrame.setOkButtonEnabled(true);
            
    }

}
