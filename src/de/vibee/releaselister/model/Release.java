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

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;


/**
 * Represents an MP3 Release and contains the path to the release, the sfv file,
 * the nfo (may be null), a list of all mp3s accoring to the sfv, the total size of
 * all mp3s included in this release, the bitrate and VBR status. Optional genre,
 * if tags have been read (is null else). readTag flag is set by the instantiater,
 * if readTag == true, genre will be read.
 * @author Clemens
 */
public class Release implements Serializable{

	public final static String MP3 = "MP3";
	public final static String FLAC = "FLAC";

	private File release;
	private File sfv;
	private File nfo;
	private List<AudioFileWithChecksum> audioFiles;
	private float size = 0;
	private boolean readTag;
	private long bitrate;
	private boolean VBR;
	private String genre;
	private boolean valid;
	private boolean releaseIsComplete = true;
	private boolean releaseIsCrcChecked;    
	private String type;


	public boolean isReleaseComplete() {
		return releaseIsComplete;
	}


	public void setComplete(boolean releaseIsComplete) {
		this.releaseIsComplete = releaseIsComplete;
	}


	public void setReleaseIsValid(boolean isValid){
		this.valid = isValid;
		this.releaseIsCrcChecked = true;
	}


	public boolean isCrcChecked(){
		return releaseIsCrcChecked;
	}


	public boolean isValid(){
		return valid;
	}


	public float getSize() {
		return size;
	}


	public String getGenre(){
		return genre;
	}


	public long getBitrate() {
		return bitrate;
	}

	public boolean isVBR() {
		return VBR;
	}


	public boolean hasTag(){
		return readTag;
	}

	/**
	 * 
	 * @param release Path to release
	 * @param sfv Path to sfv file
	 * @param nfo Path to nfo file
	 * @param readTag True if tag shall be read for genre information (slightly slower)
	 */
	public Release(File release, File sfv, File nfo, boolean readTag, String type){


		this.type = type;
		this.release = release;
		this.sfv = sfv;

		if (nfo != null){
			this.nfo = nfo;
		}

		this.audioFiles = new LinkedList<>();
		this.releaseIsCrcChecked = false;

		extractAudioFilesFromSfv(type);


		if (audioFiles.size() > 0){
			for (AudioFileWithChecksum audioFile : audioFiles){
				if (audioFile.getAudioFileExists()){
					size += audioFile.getAudioFile().length();
				}
			}

			for (AudioFileWithChecksum m : audioFiles){
				if (m.getAudioFileExists()){
					try {
						AudioFile af = AudioFileIO.read(m.getAudioFile());
						this.bitrate = Long.valueOf(af.getAudioHeader().getBitRate().replaceAll("[^\\d.]", ""));
						this.VBR = af.getAudioHeader().isVariableBitRate();
						if (readTag && af.getTag() != null){

							if (af.getTag().hasField(FieldKey.GENRE)){
								this.genre = af.getTag().getFirst(FieldKey.GENRE);
								this.readTag = true;
							}

							else{
								this.readTag = false;
							}
						}
						else{
							this.readTag = false;
						}
					} catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotReadException ex) {
						Logger.getLogger(Release.class.getName()).log(Level.SEVERE, null, ex);
					}
					return;
				}
			}

		}

	}


	private void extractAudioFilesFromSfv(String type){
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(sfv), 8192);
			String line;
			while ((line = br.readLine()) != null){
				if (type == FLAC){
					if (!line.contains(";") && Pattern.matches(".+.flac +[A-Fa-f0-9]{8} *$", line)){
						line = line.trim();
						int endOfFileDeclaration = line.lastIndexOf(".flac") + 5;
						File audioFile = new File(this.sfv.getParentFile() + File.separator + line.substring(0, endOfFileDeclaration));
						String CRCString = line.substring(endOfFileDeclaration+1, line.length());
						CRCString = CRCString.trim();

						long crcExpected = Long.valueOf(CRCString, 16);
						AudioFileWithChecksum track = new  AudioFileWithChecksum(audioFile, crcExpected, AudioFileWithChecksum.FLAC);

						if (!track.getAudioFileExists()){
							this.releaseIsComplete = false;
						}

						this.audioFiles.add(track);
					}
				}
				else if (type == MP3){
					if (!line.contains(";") && Pattern.matches(".+.mp3 +[A-Fa-f0-9]{8} *$", line)){

						line = line.trim();
						int endOfAudioFileDeclaration = line.lastIndexOf(".mp3") + 4;
						File mp3File = new File(this.sfv.getParentFile() + File.separator + line.substring(0, endOfAudioFileDeclaration));
						String CRCString = line.substring(endOfAudioFileDeclaration+1, line.length());
						CRCString = CRCString.trim();

						long crcExpected = Long.valueOf(CRCString, 16);
						AudioFileWithChecksum mp3 = new  AudioFileWithChecksum(mp3File, crcExpected, AudioFileWithChecksum.MP3);

						if (!mp3.getAudioFileExists()){
							this.releaseIsComplete = false;
						}

						this.audioFiles.add(mp3);
					}
				}

			}

			if (this.audioFiles.isEmpty()){
				this.releaseIsComplete = false;
			}

		}
		catch(IOException | NumberFormatException e){
			System.out.println("Error while reading sfv file.");
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


	public String getType(){
		return type;
	}

	public File getRelease() {
		return release;
	}


	public File getSfv() {
		return sfv;
	}


	public File getNfo() {
		return nfo;
	}


	public List<AudioFileWithChecksum> getAudioFiles() {
		return audioFiles;
	}




}
