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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author vibee
 */
public class PathHolder implements Serializable{
    private static PathHolder pathHolder;
    private Set<ReleasePath> pathList = new HashSet<>();

    public Set<ReleasePath> getPathList() {
        for (ReleasePath r : pathList){
        }
        return pathList;
    }

    public void setPathList(Set<ReleasePath> pathList) {
        this.pathList = pathList;
    }
    
    private PathHolder(){
//        pathList = new LinkedList<>();
    }
    
//    public PathHolder getPathHolder(){
//        return this;
//    }
    
    public synchronized static PathHolder getInstance(){
        if (pathHolder == null){
            pathHolder = new PathHolder();
        }
        return pathHolder;
    }
    
    public synchronized static void setInstance(PathHolder deserializedPathHolder){
        pathHolder = deserializedPathHolder;
    }
    
    public void addPath(ReleasePath path){
        pathList.add(path);
    }
    
    
}
