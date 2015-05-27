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

package de.vibee.releaselister.control;

import java.awt.Desktop;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

import de.vibee.releaselister.model.AudioFileWithChecksum;
import de.vibee.releaselister.model.Release;
import de.vibee.releaselister.model.ReleaseHolder;
import de.vibee.releaselister.view.LocatedFileChoser;
import de.vibee.releaselister.view.PathEditor;
import de.vibee.releaselister.view.ReleaseLister;

/**
*
* @author Clemens
*/
public class Actions {
	
	JTable table;
	DefaultTableModel tableModel;
	ReleaseLister mainWindow;
	
	public Actions(ReleaseLister mainWindow){
		this.mainWindow = mainWindow;
		this.table = mainWindow.getTable();
		this.tableModel = (DefaultTableModel) table.getModel();
	}

	public void copyReleaseNameActionPerformed(java.awt.event.ActionEvent evt) {

		String toClipboard = "";
		for (int i : table.getSelectedRows()) {
			toClipboard = toClipboard.concat((String) tableModel.getValueAt(table.convertRowIndexToModel(i), mainWindow.COL_RELEASENAME)) + "\n";
		}
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(toClipboard), null);
	}

	public void copyPathNameActionPerformed(java.awt.event.ActionEvent evt) {

		String toClipboard = "";
		for (int i : table.getSelectedRows()) {
			toClipboard = toClipboard.concat((String) tableModel.getValueAt(table.convertRowIndexToModel(i), mainWindow.COL_PATH)) + "\n";
		}
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(toClipboard), null);
	}

	public void moveToActionPerformed(java.awt.event.ActionEvent evt) {

		final JFileChooser jfc = new LocatedFileChoser();

		jfc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = jfc.showSaveDialog(jfc);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File moveDest = jfc.getSelectedFile();
			for (int i : table.getSelectedRows()) {
				try {
					Release toMove = (Release) tableModel.getValueAt(table.convertRowIndexToModel(i), mainWindow.COL_OBJECT);
					FileUtils.moveDirectoryToDirectory(toMove.getRelease(), moveDest, true);
					ReleaseHolder.getInstance().getReleaseList().remove(toMove);
				} catch (IOException ex) {
					Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, ex);
				}

			}

			mainWindow.updateTable();
		}

	}

	public void copyToActionPerformed(java.awt.event.ActionEvent evt) {

		final JFileChooser jfc = new LocatedFileChoser();
		jfc.setLocation(MouseInfo.getPointerInfo().getLocation());
		jfc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = jfc.showSaveDialog(jfc);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File moveDest = jfc.getSelectedFile();
			for (int i : table.getSelectedRows()) {
				try {
					Release toCopy = (Release) tableModel.getValueAt(table.convertRowIndexToModel(i), mainWindow.COL_OBJECT);
					FileUtils.copyDirectoryToDirectory(toCopy.getRelease(), moveDest);
				} catch (IOException ex) {
					Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

	}

	public void removeItemsFromTableActionPerformed(java.awt.event.ActionEvent evt) {
		int[] marked = table.getSelectedRows();
		Arrays.sort(marked);
		for (int i = marked.length - 1; i >= 0; i--) {
			tableModel.removeRow(table.convertRowIndexToModel(marked[i]));
		}
	}

	public void deleteReleasesActionPerformed(java.awt.event.ActionEvent evt) {
		int value = JOptionPane.showConfirmDialog(null, "Do you really want to delete the marked releases from hard disk?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION);
		if (value == JOptionPane.OK_OPTION) {
			int[] marked = table.getSelectedRows();
			Arrays.sort(marked);
			for (int i = marked.length - 1; i >= 0; i--) {
				Release r = (Release) tableModel.getValueAt(table.convertRowIndexToModel(marked[i]), mainWindow.COL_OBJECT);
				try {
					FileUtils.deleteDirectory(r.getRelease());
					ReleaseHolder.getInstance().getReleaseList().remove(r);
				} catch (IOException ex) {
					Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, ex);
				}

			}
			mainWindow.updateTable();
		}

	}

	public void readTagsStateChanged(java.awt.event.ItemEvent evt) {        
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			mainWindow.setReadTagOption(true);
		} else {
			mainWindow.setReadTagOption(false);
		}
	}    

	public void startScanActionPerformed(java.awt.event.ActionEvent evt) {        
		if (table.getRowCount() != 0) {
			int value = JOptionPane.showConfirmDialog(null, "This will clear "
					+ "the whole list and rescan the collection. Are you sure?",
					"Really?", JOptionPane.YES_NO_OPTION);
			if (value != JOptionPane.YES_OPTION) {
				return;
			}
		}

		InterruptableRunnable interruptableRunnable = new Scanner(mainWindow.getReadTagOption(), mainWindow);
		mainWindow.setInterruptableTunnable(interruptableRunnable);
		new Thread(interruptableRunnable).start();

	}    

	public void startCrcActionPerformed(java.awt.event.ActionEvent evt) {
		mainWindow.setEnabled(false);
		List<Release> toCheck = new LinkedList<>();
		boolean showConfirm = false;
		for (int i : table.getSelectedRows()) {
			if (!tableModel.getValueAt(table.convertRowIndexToModel(i), mainWindow.COL_CRC_OK).equals("")) {
				showConfirm = true;
				break;
			}
		}

		if (showConfirm) {
			Object[] options = {"Check all", "Check unverified", "Cancel"};

			int value = JOptionPane.showOptionDialog(null,
					"Some of the marked releases have already been checked. Do you want to verify them again "
							+ "or only the unverified ones?",
							"trouble in china town",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE,
							null,
							options,
							options[1]);
			if (value == 0) {
				for (int r : table.getSelectedRows()) {
					toCheck.add((Release) tableModel.getValueAt(table.convertRowIndexToModel(r), mainWindow.COL_OBJECT));
				}
				InterruptableRunnable interruptableRunnable = new CRCChecker(toCheck, mainWindow);
				new Thread(interruptableRunnable).start();

			} else if (value == 1) {
				for (int r : table.getSelectedRows()) {
					Release m = (Release) tableModel.getValueAt(table.convertRowIndexToModel(r), mainWindow.COL_OBJECT);
					if (!m.isCrcChecked()) {
						toCheck.add(m);
					}
				}
				if (toCheck.isEmpty()) {
					return;
				}
				InterruptableRunnable interruptableRunnable = new CRCChecker(toCheck, mainWindow);
				mainWindow.setInterruptableTunnable(interruptableRunnable);
				new Thread(interruptableRunnable).start();

			} else {
				mainWindow.setEnabled(true);
			}

		} else {
			for (int r : table.getSelectedRows()) {
				toCheck.add((Release) tableModel.getValueAt(table.convertRowIndexToModel(r), mainWindow.COL_OBJECT));
			}
			InterruptableRunnable interruptableRunnable = new CRCChecker(toCheck, mainWindow);
			mainWindow.setInterruptableTunnable(interruptableRunnable);
			new Thread(interruptableRunnable).start();
		}
	}

	public void changePathActionPerformed(java.awt.event.ActionEvent evt) {        
		new PathEditor(mainWindow).setVisible(true);
	}    

	public void saveActionPerformed(java.awt.event.ActionEvent evt) {
		final JFileChooser jfc = new LocatedFileChoser();
		jfc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".rlist")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return ".rlist";
			}
		});
		int returnVal = jfc.showSaveDialog(jfc);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String saveFile = jfc.getSelectedFile().getAbsolutePath();
			if (!saveFile.endsWith(".rlsit")) {
				saveFile = saveFile.concat(".rlist");
			}
			new Serializer().serialize(saveFile);
		}
	}

	public void loadActionPerformed(java.awt.event.ActionEvent evt) {
		final JFileChooser jfc = new LocatedFileChoser();
		jfc.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".rlist")) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return ".rlist";
			}
		});
		int returnVal = jfc.showSaveDialog(jfc);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String file = jfc.getSelectedFile().getAbsolutePath();

			new Serializer().deserialize(file);
		}
		mainWindow.updateTable();

	}

	public void exportTxtActionPerformed(java.awt.event.ActionEvent evt) {        

		

		mainWindow.isActive();
		JFileChooser jfc = new LocatedFileChoser();
		jfc.setDialogType(JFileChooser.SAVE_DIALOG);
		jfc.setDialogTitle("Speichern unter...");
		jfc.setSelectedFile(new File("ReleaseLister Export "
				+ System.getProperty("user.name") + ".txt"));
		int returnVal = jfc.showSaveDialog(jfc);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			FileWriter fstream = null;
			try {
				fstream = new FileWriter(jfc.getSelectedFile());
			} catch (IOException ex) {
				Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, ex);

				
			}

			if (fstream == null) {
				JDialog errorDialog = new JDialog();
				errorDialog.add(new JTextField("Error while saving"));

				
				return;
			}

			BufferedWriter out = new BufferedWriter(fstream);

			for (int i = 0; i < tableModel.getRowCount(); i++) {
				try {
					out.write((String) tableModel.getValueAt(table.convertRowIndexToModel(i), mainWindow.COL_RELEASENAME));
					out.newLine();
				} catch (IOException ex) {
					Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, ex);

					
				}
			}

			try {
				out.close();
				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(ReleaseLister.class.getName()).log(Level.SEVERE, null, ex);

				
			}

		}

		
	}    

	public void clearListActionPerformed(java.awt.event.ActionEvent evt) {        
		tableModel.fireTableDataChanged();
		ReleaseHolder.getInstance().getReleaseList().clear();
		mainWindow.updateTable();
		mainWindow.setStatusLabel("Status: Idle");
	}    

	public void selectAllActionPerformed(java.awt.event.ActionEvent evt) {        
		table.selectAll();
	}    

	public void aboutMenuPerformed(java.awt.event.ActionEvent evt) {        
		JOptionPane.showMessageDialog(null, "ReleaseLister " + mainWindow.getVersion() + " by vibee", "About", JOptionPane.NO_OPTION);
	}    

	public void openBrowserActionPerformed(ActionEvent evt) {

		if (table.getSelectedRows().length > 1) {
			String confirmDialog = "You are going to open "
					+ table.getSelectedRows().length + " Browser windows.";
			int value = JOptionPane.showConfirmDialog(null, confirmDialog, "Really?", JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.CANCEL_OPTION || value == JOptionPane.CLOSED_OPTION) {
				return;
			}

		}

		for (int i : table.getSelectedRows()) {
			Release f = (Release) tableModel.getValueAt(table.convertRowIndexToModel(i),
					mainWindow.COL_OBJECT);
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(f.getRelease());
				} catch (IOException ex) {
					Logger.getLogger(mainWindow.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	public void openNFOActionPerformed(ActionEvent evt) {

		if (table.getSelectedRows().length > 1) {
			String confirmDialog = "You are going to open "
					+ table.getSelectedRows().length + " NFO files.";
			int value = JOptionPane.showConfirmDialog(null, confirmDialog, "Really?", JOptionPane.OK_CANCEL_OPTION);
			if (value == JOptionPane.CANCEL_OPTION || value == JOptionPane.CLOSED_OPTION) {
				return;
			}

		}

		for (int i : table.getSelectedRows()) {
			Release f = (Release) tableModel.getValueAt(table.convertRowIndexToModel(i),
					mainWindow.COL_OBJECT);

			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(f.getNfo());
				} catch (IOException ex) {
					Logger.getLogger(mainWindow.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	public void playActionPerformed(ActionEvent evt) {

		for (int i : table.getSelectedRows()) {
			Release f = (Release) tableModel.getValueAt(table.convertRowIndexToModel(i),
					mainWindow.COL_OBJECT);

			if (Desktop.isDesktopSupported()) {
				try {
					File[] toPlay;
					if ((toPlay = f.getRelease().listFiles(new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							if (pathname.getName().endsWith(".m3u")) {
								return true;
							} else {
								return false;
							}
						}
					})).length > 0) {
						for (File playlist : toPlay) {
							Desktop.getDesktop().open(playlist);
						}
					} else {

						for (AudioFileWithChecksum mp3 : f.getAudioFiles()) {
							Desktop.getDesktop().open(mp3.getAudioFile());
						}

					}

				} catch (IOException ex) {
					Logger.getLogger(mainWindow.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}
	
	
}    	
