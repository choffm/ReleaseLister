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

package cc.de1.v.releaselister.model;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author vibee
 */
public class Mp3WithChecksum implements Serializable{
    
    private File mp3;
    private long checksum;
    private boolean mp3IsValid;
    private boolean mp3Exists;
    
    public Mp3WithChecksum(File mp3, long checksum){
        this.mp3 = mp3;
        this.checksum = checksum;
        this.mp3Exists = mp3.exists();
    }

    public boolean getMp3Exists() {
        return mp3Exists;
    }

    
    public boolean getMp3IsValid(){
        return mp3IsValid;
    }
    
    public void setMp3IsValid(boolean mp3IsValid){
        this.mp3IsValid = mp3IsValid;
    }
    
    public File getMp3(){
        return this.mp3;
    }
    
    public long getChecksum(){
        return this.checksum;
    }
    
}
