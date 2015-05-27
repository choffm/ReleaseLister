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

import java.awt.MouseInfo;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.vibee.releaselister.control.Actions;

/**
*
* @author Clemens
*/
public class PopUpMenu extends JPopupMenu{
	
	protected JMenuItem openBrowserPopupItem = new JMenuItem("Open in Browser");
	protected JMenuItem openNFOPopupItem = new JMenuItem("Open NFO");
	protected JMenuItem playPopupItem = new JMenuItem("Play");
	protected JMenuItem copyNamePopupItem = new JMenuItem("Copy Release Name(s)");
	protected JMenuItem copyPathPopupItem = new JMenuItem("Copy Path(s)");
	protected JMenuItem moveToPopupItem = new JMenuItem("Move Release(s) to...");
	protected JMenuItem copyToPopupItem = new JMenuItem("Copy Release(s) to...");
	protected JMenuItem deletePopupItem = new JMenuItem("Delete Release(s)");

	private JMenuItem[] popupMenuItems = {
			openBrowserPopupItem, openNFOPopupItem, playPopupItem,
			copyNamePopupItem, copyPathPopupItem, moveToPopupItem,
			copyToPopupItem, deletePopupItem
	};
	
	
	final ReleaseLister mainWindow;
	
	
	public PopUpMenu(final ReleaseLister mainWindow){	
		
		for (JMenuItem c : popupMenuItems) {
			this.add(c);
		}

		this.setVisible(false);
		this.mainWindow = mainWindow;
		initPopupMenu();
		
		
	}
	
	public void initPopupMenu(){
		
		openBrowserPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).openBrowserActionPerformed(evt);
			}
		});

		openNFOPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).openNFOActionPerformed(evt);
			}
		});

		playPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).playActionPerformed(evt);
			}
		});


		copyNamePopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).copyReleaseNameActionPerformed(evt);
			}
		});

		copyPathPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).copyPathNameActionPerformed(evt);
			}
		});

		moveToPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).moveToActionPerformed(evt);
			}
		});

		copyToPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).copyToActionPerformed(evt);
			}
		});

		deletePopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).deleteReleasesActionPerformed(evt);
			}
		});


	}
	
	protected void showPopup() {
		this.show(mainWindow, MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
	}
	

	
}
