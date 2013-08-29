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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author vibee
 */
public class ReleaseHolder implements Serializable{
    
    private static ReleaseHolder releaseHolder;
    private List<Release> releaseList = new LinkedList<>();
    
    public synchronized static ReleaseHolder getInstance(){
        if (releaseHolder == null){
            releaseHolder = new ReleaseHolder();
        }
        return releaseHolder;
    }

    public List<Release> getReleaseList() {
        return releaseList;
    }

    public void setReleaseList(List<Release> releaseList) {
        this.releaseList = releaseList;
    }
    
    public void addRelease(Release release){
        releaseList.add(release);
    }
    
    private ReleaseHolder(){
        
    }
    
    public synchronized static void setInstance(ReleaseHolder deserializedReleaseHolder){
        releaseHolder = deserializedReleaseHolder;
    }
    

    
}
