package de.vibee.releaselister.view;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.vibee.releaselister.control.Actions;

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
	
	private JDialog invoker = new JDialog();
	
	final ReleaseLister mainWindow;
	
	
	public PopUpMenu(final ReleaseLister mainWindow){	
		
		for (JMenuItem c : popupMenuItems) {
			this.add(c);
		}
		invoker.setUndecorated(true);
		invoker.setVisible(false);
		this.setVisible(false);
		invoker.add(this);
		invoker.pack();
		this.mainWindow = mainWindow;
		initPopupMenu();
		
		
	}
	
	public void initPopupMenu(){
		
		openBrowserPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).openBrowserActionPerformed(evt);
				hidePopup();
			}
		});

		openNFOPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).openNFOActionPerformed(evt);
				hidePopup();
			}
		});

		playPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).playActionPerformed(evt);
				hidePopup();
			}
		});


		copyNamePopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).copyReleaseNameActionPerformed(evt);
				hidePopup();
			}
		});

		copyPathPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).copyPathNameActionPerformed(evt);
				hidePopup();
			}
		});

		moveToPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).moveToActionPerformed(evt);
				hidePopup();
			}
		});

		copyToPopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).copyToActionPerformed(evt);
				hidePopup();
			}
		});

		deletePopupItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				new Actions(mainWindow).deleteReleasesActionPerformed(evt);
				hidePopup();
			}
		});

		for (final JMenuItem c : popupMenuItems) {
			c.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent evt) {
					c.setOpaque(true);
					c.setBackground(Color.LIGHT_GRAY);
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent evt) {
					uncolorPopupItems(c);
				}
			});
		}

		invoker.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				invoker.setVisible(false);
				PopUpMenu.this.setVisible(false);
			}

			@Override
			public void focusGained(FocusEvent e) {
				invoker.setVisible(true);
				PopUpMenu.this.setVisible(true);
			}
		});
		
		this.setLocation(MouseInfo.getPointerInfo().getLocation());
		invoker.setLocation(MouseInfo.getPointerInfo().getLocation());

	}
	
	protected void showPopup() {
		this.setLocation(MouseInfo.getPointerInfo().getLocation());
		invoker.setLocation(MouseInfo.getPointerInfo().getLocation());

		invoker.setVisible(true);
	}
	
	protected void hidePopup(){
		invoker.setVisible(false);
	}

	private void uncolorPopupItems(JMenuItem j) {
		j.setOpaque(false);
		j.setBackground(Color.green); //totally idiotic but it works
	}
	
}
