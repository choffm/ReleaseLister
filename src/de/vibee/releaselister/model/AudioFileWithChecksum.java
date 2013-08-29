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

package de.vibee.releaselister.model;

import java.io.File;
import java.io.Serializable;


/**
 *
 * @author vibee
 */
public class AudioFileWithChecksum implements Serializable{
    
	public final static String MP3 = "MP3";
	public final static String FLAC = "FLAC";
	
	private File audioFile;
    private long checksum;
    private boolean audioFileIsValid;
    private boolean audioFileExists;
    private String type;
    
    public AudioFileWithChecksum(File audioFile, long checksum, String type){
        this.audioFile = audioFile;
        this.checksum = checksum;
        this.audioFileExists = audioFile.exists();
        this.type = type;
    }
    

    public boolean getAudioFileExists() {
        return audioFileExists;
    }

    

	public boolean getAudioFileIsValid(){
        return audioFileIsValid;
    }
    

	public void setAudioFileIsValid(boolean audioFileIsValid){
        this.audioFileIsValid = audioFileIsValid;
    }
    

	public File getAudioFile(){
        return this.audioFile;
    }
    
 
	public long getChecksum(){
        return this.checksum;
    }
	
	public String getType(){
		return type;
	}

    
}
