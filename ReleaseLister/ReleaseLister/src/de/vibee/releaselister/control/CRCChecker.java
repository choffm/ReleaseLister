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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import de.vibee.releaselister.model.MP3Release;
import de.vibee.releaselister.model.Mp3WithChecksum;
import de.vibee.releaselister.view.ActionFrame;
import de.vibee.releaselister.view.ReleaseLister;

/**
 * The CRC Checker scans one release by parsing the sfv file and checking each 
 * mp3 file, which is listet in this sfv. Sets valid status for both release
 * and containing mp3 files. A release is valid if and only if all mp3s
 * exist and are valid.
 * @author vibee
 */
public class CRCChecker extends InterruptableRunnable{
    
    private ActionFrame actionFrame;
    int counter = 0;
    int countValid = 0;
    int countInvalid = 0;
    List<MP3Release> toCheck;
    long time = 0;
    long timeGone = 0;
    long size = 0;
    
    public CRCChecker(List<MP3Release> toCheck){
        this.toCheck = toCheck;
    }
    
    /**
     * Verifies an MP3 Release
     * @param release Release to verify
     */
    public void checkSfv(MP3Release release){
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        if (!release.getReleaseIsComplete()){
            release.setReleaseIsValid(false);
            return;
        }
        
        boolean isValid = true;
        for (Mp3WithChecksum m : release.getMp3s()){
            if (!m.getMp3Exists()){
                isValid = false;
            }
            else{
                try {
                    if (m.getChecksum() == FileUtils.checksumCRC32(m.getMp3())){
                        m.setMp3IsValid(true);
                    }
                    else{
                        m.setMp3IsValid(false);
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
        
        actionFrame = new ActionFrame();
        actionFrame.setTitle("Verifying Releases...");
        actionFrame.getProgressBar().setIndeterminate(false);
        actionFrame.getProgressBar().setMinimum(0);
        actionFrame.getProgressBar().setMaximum(toCheck.size());
        actionFrame.getProgressBar().setValue(0);
        actionFrame.setVisible(true);
        time = System.currentTimeMillis() - 1;
        
        for (MP3Release release : toCheck){
            timeGone = System.currentTimeMillis() - time;
            actionFrame.setStatusLabelText("Veryfied " + counter + " of " + 
                    toCheck.size() + " Releases (" + (long)(size / timeGone) / 1024 + " MB/s)");
            if (ReleaseLister.getInstance().getInterruptableRunnable().isInterrupted()){
                actionFrame.setStatusLabelText("Verify aborted.");
                actionFrame.setOkButtonEnabled(true);
                return;
            }
                
            checkSfv(release);
            size += release.getSize();
            counter++;
            actionFrame.getProgressBar().setValue(counter);
            if (release.getReleaseIsValid()){
                countValid++;
            }
            else {
                countInvalid++;
            }
           
        }
        
        actionFrame.setStatusLabelText(countValid + " valid and " + countInvalid + 
                " invalid Releases verfied (" + (long)(size / timeGone) / 1024 + " MB/s).");
        actionFrame.setAbortButtonEnabled(false);
        actionFrame.setOkButtonEnabled(true);
            
    }

}
