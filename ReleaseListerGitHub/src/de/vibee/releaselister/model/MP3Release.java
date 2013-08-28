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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

/**
 * Represents an MP3 Release and contains the path to the release, the sfv file,
 * the nfo (may be null), a list of all mp3s accoring to the sfv, the total size of
 * all mp3s included in this release, the bitrate and VBR status. Optional genre,
 * if tags have been read (is null else). readTag flag is set by the instantiater,
 * if readTag == true, genre will be read.
 * @author vibee
 */
public class MP3Release implements Serializable{
    
    private File release;
    private File sfv;
    private File nfo;
    private List<Mp3WithChecksum> mp3s;
    private long size = 0;
    private boolean readTag;
    private long bitrate;
    private boolean VBR;
    private String genre;
    private boolean releaseIsValid;
    private boolean releaseIsComplete;
    private boolean releaseIsCrcChecked;

    public boolean getReleaseIsComplete() {
        return releaseIsComplete;
    }

    public void setReleaseIsComplete(boolean releaseIsComplete) {
        this.releaseIsComplete = releaseIsComplete;
    }
    
    //Remove those characters from genre field, as the Tag lib is not working 100% correct
    private final String[] illegalGenreCharacters = {"(",")","0","1","2","3","4","5","6","7","8","9"};
    private final String[] spaceGenreCharacters = {"_"};

    
    public void setReleaseIsValid(boolean isValid){
        this.releaseIsValid = isValid;
        this.releaseIsCrcChecked = true;
    }
    
    public boolean isCrcChecked(){
        return releaseIsCrcChecked;
    }
    
    public boolean getReleaseIsValid(){
        return releaseIsValid;
    }
    
    public long getSize() {
        return size;
    }
    
    public String getGenre(){
        return genre;
    }

    public long getBitrate() {
        return bitrate;
    }

    public boolean IsVBR() {
        return VBR;
    }
    
    public boolean hasTag(){
        return readTag;
    }
        
    public MP3Release(File release, File sfv, File nfo, boolean readTag){
        this.release = release;
        this.sfv = sfv;
        if (nfo != null){
            this.nfo = nfo;
        }
        this.readTag = readTag;
        this.mp3s = new LinkedList<>();
        this.releaseIsCrcChecked = false;
        
        extractMp3sFromSfv();
        
        
        if (mp3s.size() > 0){
            for (Mp3WithChecksum mp3 : mp3s){
                if (mp3.getMp3Exists()){
                    size += mp3.getMp3().length();
                }
            }
            Mp3WithChecksum firstMp3 = null;
            for (Mp3WithChecksum m : mp3s){
                if (m.getMp3Exists()){
                    firstMp3 = m;
                    break;
                }
            }
            if (firstMp3 != null){
            try {
                MP3File firstFile = new MP3File(firstMp3.getMp3(), MP3File.LOAD_ALL, true);
                this.bitrate = firstFile.getAudioHeader().getBitRateAsNumber();
                this.VBR = firstFile.getAudioHeader().isVariableBitRate();
                if (readTag){
                    if (firstFile.hasID3v1Tag()){

                        genre = firstFile.getID3v1Tag().getFirst(FieldKey.GENRE);
                    }

                    if (genre == null && firstFile.hasID3v2Tag()){
                        genre = firstFile.getID3v2Tag().getFirst(FieldKey.GENRE);

                    }

                    if (genre != null){   

                        for (String s : illegalGenreCharacters){
                            while (genre.contains(s)){
                                genre = genre.replace(s, "");
                            }
                        }

                        for (String s : spaceGenreCharacters){
                            while(genre.contains(s)){
                                genre = genre.replace(s, " ");
                            }
                        }
                    }
                    else{
                        this.readTag = false;
                    }
                }
            } catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
                Logger.getLogger(MP3Release.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        }
        
    }
    
    private void extractMp3sFromSfv(){
       
        try{
            BufferedReader in = new BufferedReader(new FileReader(sfv));
            String zeile;
            this.releaseIsComplete = true;
            while ((zeile = in.readLine()) != null){
                if (!zeile.contains(";") && Pattern.matches(".+.mp3 +[A-Fa-f0-9]{8} *$", zeile)){
                    
                    while (zeile.charAt(0) == ' '){
                        zeile = zeile.substring(1);
                    }
                    int endOfMp3Declaration = zeile.lastIndexOf(".mp3") + 4;
                    File mp3File = new File(this.sfv.getParentFile() + File.separator + zeile.substring(0, endOfMp3Declaration));
                    String CRCString = zeile.substring(endOfMp3Declaration+1, zeile.length());
                    while (CRCString.contains(" ")){
                        CRCString = CRCString.replace(" ", "");
                    }

                    long crcExpected = Long.valueOf(CRCString, 16);
                    Mp3WithChecksum mp3 = new  Mp3WithChecksum(mp3File, crcExpected);
                    
                    if (!mp3.getMp3Exists()){
                        this.releaseIsComplete = false;
                    }
                    
                    this.mp3s.add(mp3);
                }
            }
            in.close();
            
            if (this.mp3s.isEmpty()){
                this.releaseIsComplete = false;
            }
            
        }
        catch(IOException | NumberFormatException e){
            
        }
      
    }
    

//    public MP3Release(File release){
//        this.release = release;
//    }
    public File getRelease() {
        return release;
    }
//    public void setRelease(File release) {
//        this.release = release;
//    }
    public File getSfv() {
        return sfv;
    }
//    public void setSfv(File sfvName) {
//        this.sfv = sfvName;
//    }
    public File getNfo() {
        return nfo;
    }
//    public void setNfo(File nfoName) {
//        this.nfo = nfoName;
//    }
    public List<Mp3WithChecksum> getMp3s() {
        return mp3s;
    }
//    public void setMp3s(List<Mp3WithChecksum> mp3s) {
//        this.mp3s = mp3s;
//    }
//    public void addMP3(Mp3WithChecksum mp3){
//        mp3s.add(mp3);
//    }
    
}
