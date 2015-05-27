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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author Clemens
 */
@SuppressWarnings("serial")
public class ReleaseHolder implements Serializable{
    
    private static ReleaseHolder releaseHolder;
    private Set<Release> releaseList = new HashSet<>();
    
    public synchronized static ReleaseHolder getInstance(){
        if (releaseHolder == null){
            releaseHolder = new ReleaseHolder();
        }
        return releaseHolder;
    }

    public Set<Release> getReleaseList() {
        return releaseList;
    }

    public void setReleaseList(Set<Release> releaseList) {
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
