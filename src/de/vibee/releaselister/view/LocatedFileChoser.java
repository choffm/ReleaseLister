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
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 *
 * @author Clemens
 */
public class LocatedFileChoser extends JFileChooser {
    @Override
    public JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dlg = super.createDialog(parent);
        dlg.setLocation(MouseInfo.getPointerInfo().getLocation());
        return dlg;
    }
}
