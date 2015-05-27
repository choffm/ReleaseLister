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

package de.vibee.releaselister.view;

import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Clemens
 */
public class GuiComponents {
    
    private static GuiComponents guiComponents;

    public List<Component> getStartScanComponents() {
        return startScanComponents;
    }

    public void addStartScanComponents(Component c) {
        startScanComponents.add(c);
    }

    public List<Component> getStartCrcCheckComponents() {
        return startCrcCheckComponents;
    }

    public void addStartCrcCheckComponents(Component c) {
        startCrcCheckComponents.add(c);
    }



    public List<Component> getChangePathComponents() {
        return changePathComponents;
    }

    public void addChangePathComponents(Component c) {
        changePathComponents.add(c);
    }

    public List<Component> getExportTxtComponents() {
        return exportTxtComponents;
    }

    public void addExportTxtComponents(Component c) {
        exportTxtComponents.add(c);
    }

    public List<Component> getSelectAllComponents() {
        return selectAllComponents;
    }

    public void addSelectAllComponents(Component c) {
        selectAllComponents.add(c);
    }

    public List<Component> getClearListComponents() {
        return clearListComponents;
    }

    public void addClearListComponents(Component c) {
        clearListComponents.add(c);
    }

    public List<Component> getReadTagOptionComponents() {
        return readTagOptionComponents;
    }

    public void addReadTagOptionComponents(Component c) {
        readTagOptionComponents.add(c);
    }

    public List<Component> getAboutComponents() {
        return aboutComponents;
    }

    public void addAboutComponents(Component c) {
        aboutComponents.add(c);
    }
    
    public List<Component> getOpenNFOComponents() {
        return openNFOComponents;
    }
    
    public void addOpenNFOComponents(Component c) {
        openNFOComponents.add(c);
    }
    
    public void addExitComponents(Component c) {
        exitComponents.add(c);
    }
    
    public void setStartScanComponentsEnabled(boolean status) {
        for (Component c : startScanComponents){
            c.setEnabled(status);
        }
    }

    public void setStartCrcCheckComponentsEnabled(boolean status) {
        for (Component c : startCrcCheckComponents){
            c.setEnabled(status);
        }
    }



    public void setChangePathComponentsEnabled(boolean status) {
        for (Component c : changePathComponents){
            c.setEnabled(status);
        }
    }

    public void setExportTxtComponentsEnabled(boolean status) {
        for (Component c : exportTxtComponents){
            c.setEnabled(status);
        }
    }

    public void setSelectAllComponentsEnabled(boolean status) {
        for (Component c : selectAllComponents){
            c.setEnabled(status);
        }
    }



    public void setClearListComponentsEnabled(boolean status) {
        for (Component c : clearListComponents){
            c.setEnabled(status);
        }
    }

    public void setReadTagOptionComponentsEnabled(boolean status) {
        for (Component c : readTagOptionComponents){
            c.setEnabled(status);
        }
    }

    public void setAboutComponentsEnabled(boolean status) {
        for (Component c : aboutComponents){
            c.setEnabled(status);
        }
    }
    
    public void setOpenNFOComponentsEnabled(boolean status) {
        for (Component c : openNFOComponents){
            c.setEnabled(status);
        }
    }
    
    
    private List<Component> startScanComponents = new LinkedList<>();
    private List<Component> startCrcCheckComponents = new LinkedList<>();
    private List<Component> changePathComponents = new LinkedList<>();
    private List<Component> exportTxtComponents = new LinkedList<>();
    private List<Component> selectAllComponents = new LinkedList<>();
    private List<Component> clearListComponents = new LinkedList<>();
    private List<Component> readTagOptionComponents = new LinkedList<>();
    private List<Component> aboutComponents = new LinkedList<>();
    private List<Component> openNFOComponents = new LinkedList<>();
    private List<Component> exitComponents = new LinkedList<>();
    
    private GuiComponents(){
        
    }
    
    public synchronized static GuiComponents getInstance(){
        if (guiComponents == null){
            guiComponents = new GuiComponents();
        }
        return guiComponents;
    }
    
    
    
    
    
    
}
