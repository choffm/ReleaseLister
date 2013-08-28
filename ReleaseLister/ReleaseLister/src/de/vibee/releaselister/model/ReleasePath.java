/*
 * Copyright (C) 2012 vibee
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
public class ReleasePath implements Serializable{
    
    private File path;
    private boolean scanned = false;

    
    public ReleasePath(File path){
        this.path = path;
    }
    
    public void setScanned(boolean scanned){
        this.scanned = scanned;
    }
    
    public boolean isScanned(){
        return scanned;
    }
    
    public File getPath(){
        return path;
    }
    
}
