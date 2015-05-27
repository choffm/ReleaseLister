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

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import de.vibee.releaselister.control.Actions;
import de.vibee.releaselister.control.InterruptableRunnable;
import de.vibee.releaselister.control.Serializer;
import de.vibee.releaselister.model.Release;
import de.vibee.releaselister.model.ReleaseHolder;

/**
 *
 * @author Clemens
 */
public final class ReleaseLister extends javax.swing.JFrame {

	private ListSelectionListener tableSelectionListener;
	private DefaultTableModel tableModel;
	private GuiComponents guiComponents;
	private String autoSavePath = System.getProperty("user.home") + File.separator
			+ ".ReleaseLister" + File.separator + "autosave.rlist";
	;
	public final String version = "20130831-1";
	public final int COL_RELEASENAME = 0;
	public final int COL_AMOUNT_OF_FILES = 1;
	public final int COL_RELEASE_SIZE = 2;
	public final int COL_BITRATE = 3;
	public final int COL_GENRE = 4;
	public final int COL_PATH = 5;
	public final int COL_OBJECT = 6;
	public final int COL_CRC_OK = 7;
	public final int COL_ISCOMPLETE = 8;
	public final int COL_HASNFO = 9;
	private ImageIcon okIcon;
	private ImageIcon errorIcon;
	private boolean readTagOption;
	private InterruptableRunnable interruptableRunnable;
	private JLabel okIconLabelBright;
	private JLabel falseIconLabelBright;
	private JLabel okIconLabelDark;
	private JLabel falseIconLabelDark;
	private JLabel okIconLabelSelected;
	private JLabel falseIconLabelSelected;
	private PopUpMenu popupMenu;

	/**
	 * Creates new form ReleaseLister
	 */
	private ReleaseLister() {
		
		okIcon = new ImageIcon(ReleaseLister.class.getResource("/de/vibee/releaselister/images/oksmall.jpg"));
		errorIcon = new ImageIcon(ReleaseLister.class.getResource("/de/vibee/releaselister/images/badsmall.jpg"));
		Image logo = new ImageIcon(ReleaseLister.class.getResource("/de/vibee/releaselister/images/releaseLister.jpg")).getImage();        
		setIconImage(logo);
		setTitle("ReleaseLister " + version + " by vibee");
		initComponents();
		prerenderIconLabels();
		guiComponents = GuiComponents.getInstance();
		addMenuItemsToGuiCoponents();
		initTablePreSort();
		tableModel = (DefaultTableModel) table.getModel();
		new Serializer().deserialize(autoSavePath);
		updateTable();
		addListSelectionListener();
		setComperators();
		readTagOption = readTagsMenuCheckbox.isSelected();

		//Handle Save on exit
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				new Serializer().serialize(autoSavePath);
			}
		}));

		InputMap iMap = filterPane.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap aMap = filterPane.getActionMap();

		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		aMap.put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateTable();
			}
		});

		guiComponents.setStartCrcCheckComponentsEnabled(false);
		this.setVisible(true);

	}

	private void setPopupMenu(PopUpMenu popupMenu){
		this.popupMenu = popupMenu;
	}
	
	private void prerenderIconLabels() {

		okIconLabelBright = new JLabel(okIcon);
		okIconLabelBright.setBackground(Color.WHITE);
		okIconLabelBright.setOpaque(true);
		falseIconLabelBright = new JLabel(errorIcon);
		falseIconLabelBright.setBackground(Color.WHITE);
		falseIconLabelBright.setOpaque(true);
		okIconLabelDark = new JLabel(okIcon);
		okIconLabelDark.setBackground(table.getBackground());
		okIconLabelDark.setOpaque(true);
		falseIconLabelDark = new JLabel(errorIcon);
		falseIconLabelDark.setBackground(table.getBackground());
		falseIconLabelDark.setOpaque(true);
		okIconLabelSelected = new JLabel(okIcon);
		okIconLabelSelected.setBackground(table.getSelectionBackground());
		okIconLabelSelected.setOpaque(true);
		falseIconLabelSelected = new JLabel(errorIcon);
		falseIconLabelSelected.setBackground(table.getSelectionBackground());
		falseIconLabelSelected.setOpaque(true);

	}

//	public synchronized static ReleaseLister getInstance() {
//		if (releaseLister == null) {
//			releaseLister = new ReleaseLister();
//		}
//		return releaseLister;
//	}

	private void initTablePreSort() {
		table.getColumnModel().removeColumn(table.getColumnModel().getColumn(COL_OBJECT));
		List<RowSorter.SortKey> sortKeys = new LinkedList<>();
		sortKeys.add(new RowSorter.SortKey(COL_RELEASENAME, SortOrder.ASCENDING));
		DefaultRowSorter sorter = ((DefaultRowSorter) table.getRowSorter());
		sorter.setSortKeys(sortKeys);
		sorter.setSortsOnUpdates(true);
	}

	/**
	 * Sets the width of specified column
	 *
	 * @param col Column to change width
	 * @param min
	 * @param preferred
	 * @param max
	 */
	private void setColWidth(int col, int min, int preferred, int max) {
		if (min >= 0) {
			table.getColumnModel().getColumn(col).setMinWidth(min);
		}
		if (preferred >= 0) {
			table.getColumnModel().getColumn(col).setPreferredWidth(preferred);
		}
		if (max >= 0) {
			table.getColumnModel().getColumn(col).setMaxWidth(max);
		}
	}

	/*
	 * sets all columns widths
	 */
	private void setcolWidth() {
		setColWidth(COL_RELEASENAME, -1, 800, -1);
		setColWidth(COL_AMOUNT_OF_FILES, -1, 45, -1);
		setColWidth(COL_RELEASE_SIZE, -1, 80, -1);
		setColWidth(COL_BITRATE, -1, 100, -1);
		setColWidth(COL_GENRE, -1, 100, -1);
		setColWidth(COL_PATH, -1, 100, -1);
		setColWidth(COL_CRC_OK, -1, 60, -1);
		setColWidth(COL_ISCOMPLETE, 70, 70, 70);
		setColWidth(COL_HASNFO, 50, 50, 50);
	}

	/*
	 * Adds all Menu Items to global gui component handler
	 */
	private void addMenuItemsToGuiCoponents() {
		guiComponents.addAboutComponents(aboutMenuItem);
		guiComponents.addChangePathComponents(changePathMenuItem);
		guiComponents.addClearListComponents(clearListMenuItem);
		guiComponents.addExportTxtComponents(exportTxtMenuItem);
		guiComponents.addReadTagOptionComponents(readTagsMenuCheckbox);
		guiComponents.addSelectAllComponents(selectAllMenuItem);
		guiComponents.addStartCrcCheckComponents(startCrcMenuItem);
		guiComponents.addStartScanComponents(startScanMenuItem);
		guiComponents.addExitComponents(exitMenuItem);
	}

	/**
	 * Clears and feeds the table with filtered data of ReleaseHolder
	 */
	public void updateTable() {

		clearTable();
		List<String> filterList = new LinkedList<>();
		String filter = filterPane.getText();

		while (filter.contains(" ")) {
			if (filter.charAt(0) == ' ') {
				filter = filter.substring(1);
			} else {
				filterList.add(filter.substring(0, filter.indexOf(" ")));
				filter = filter.substring(filter.indexOf(" ") + 1);
			}
		}

		filterList.add(filter);

		long releaseHolderSize = 0;
		long releaseTableSize = 0;
		int verified = 0;
		int ok = 0;
		int notOk = 0;
		for (Release m : ReleaseHolder.getInstance().getReleaseList()) {
			releaseHolderSize += m.getSize();
			boolean accepted = true;
			for (String s : filterList) {
				if (!m.getRelease().getName().toLowerCase().contains(s.toLowerCase())) {
					accepted = false;
					break;
				}
			}
			if (accepted) {
				String displaySize = new DecimalFormat("0.00").format(m.getSize() / 1048576) + " MB";
				String bitrateField = String.valueOf(m.getBitrate()).concat("kb/s");
				if (m.isVBR()) {
					bitrateField = bitrateField.concat(" VBR");
				}
				String genre = "";
				String crcStatus = "";
				if (m.isCrcChecked()) {
					verified++;
					if (m.isValid()) {
						crcStatus = "OK";
						ok++;
					} else {
						crcStatus = "FALSE";
						notOk++;
					}
				}
				if (m.getGenre() != null) {
					genre = m.getGenre();
				}

				tableModel.addRow(new Object[]{m.getRelease().getName(), m.getAudioFiles().size(),
						displaySize, bitrateField, genre, m.getRelease().getAbsolutePath(),
						m, crcStatus, m.isReleaseComplete(), !(m.getNfo() == null)});
				releaseTableSize += m.getSize();
			}

		}

		releaseHolderSize /= 1048576;
		releaseTableSize /= 1048576;


		StringBuilder status = new StringBuilder();

		status.append("Showing ");
		status.append(table.getRowCount());
		status.append(" (");
		status.append(releaseTableSize > 10000 ? releaseTableSize / 1024 + " GB)" : releaseTableSize + " MB)");
		status.append(" of ");
		status.append(ReleaseHolder.getInstance().getReleaseList().size());
		status.append(" Releases (");
		status.append(releaseHolderSize > 10000 ? releaseHolderSize / 1024 + " GB)" : releaseHolderSize + " MB)");        
		status.append(" | ");
		status.append(verified);
		status.append(" Verified, ");
		status.append(ok);
		status.append(" valid, ");
		status.append(notOk);
		status.append(" bad releases.");
		statusLabel.setText(status.toString());

		guiComponents.setStartCrcCheckComponentsEnabled(false);

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		table = new javax.swing.JTable();
		jScrollPane2 = new javax.swing.JScrollPane();
		filterPane = new javax.swing.JTextPane();
		statusLabel = new javax.swing.JLabel();
		filterButton = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		startScanMenuItem = new javax.swing.JMenuItem();
		startCrcMenuItem = new javax.swing.JMenuItem();
		changePathMenuItem = new javax.swing.JMenuItem();
		loadMenuItem = new javax.swing.JMenuItem();
		saveMenuItem1 = new javax.swing.JMenuItem();
		exportTxtMenuItem = new javax.swing.JMenuItem();
		exitMenuItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		selectAllMenuItem = new javax.swing.JMenuItem();
		clearListMenuItem = new javax.swing.JMenuItem();
		optionsMenu = new javax.swing.JMenu();
		readTagsMenuCheckbox = new javax.swing.JCheckBoxMenuItem();
		helpMenu = new javax.swing.JMenu();
		aboutMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		table.setDefaultRenderer(String.class, new StringCellRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanCellRenderer());
		table.setAutoCreateRowSorter(true);
		table.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] {

				},
				new String [] {
						"Release Name", "Files", "Size", "Bitrate", "Genre", "Path", "MP3Release", "CRC OK", "Complete", "NFO"
				}
				) {
			Class[] types = new Class [] {
					java.lang.String.class, java.lang.Integer.class, java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, de.vibee.releaselister.model.Release.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class
			};
			boolean[] canEdit = new boolean [] {
					false, false, false, false, false, false, false, false, false, false
			};

			@Override
			public Class getColumnClass(int columnIndex) {

				return types [columnIndex];
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit [columnIndex];
			}
		});
		table.setColumnSelectionAllowed(false);
		table.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		table.setSelectionBackground(new java.awt.Color(153, 204, 255));
		table.setSelectionForeground(new java.awt.Color(0, 0, 0));
		table.getTableHeader().setReorderingAllowed(false);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				tableMouseClicked(evt);
			}
		});
		jScrollPane1.setViewportView(table);
		table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setcolWidth();

		filterPane.setAutoscrolls(false);
		jScrollPane2.setViewportView(filterPane);

		statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		statusLabel.setText("ReleaseLister");

		filterButton.setText("Filter");
		filterButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				filterButtonActionPerformed(evt);
			}
		});

		fileMenu.setText("File");
		fileMenu.setMaximumSize(new java.awt.Dimension(40, 32767));
		fileMenu.setMinimumSize(new java.awt.Dimension(40, 19));

		startScanMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		startScanMenuItem.setText("Start Scan");
		startScanMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startScanMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(startScanMenuItem);

		startCrcMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		startCrcMenuItem.setText("Start CRC Check");
		startCrcMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startCrcMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(startCrcMenuItem);

		changePathMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		changePathMenuItem.setText("Change Paths");
		changePathMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changePathMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(changePathMenuItem);

		loadMenuItem.setText("Load");
		loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(loadMenuItem);

		saveMenuItem1.setText("Save");
		saveMenuItem1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveMenuItem1ActionPerformed(evt);
			}
		});
		fileMenu.add(saveMenuItem1);

		exportTxtMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		exportTxtMenuItem.setText("Export as .txt");
		exportTxtMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportTxtMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exportTxtMenuItem);

		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		editMenu.setText("Edit");
		editMenu.setMaximumSize(new java.awt.Dimension(40, 32767));
		editMenu.setMinimumSize(new java.awt.Dimension(40, 19));

		selectAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
		selectAllMenuItem.setText("Select all");
		selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectAllMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(selectAllMenuItem);

		clearListMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		clearListMenuItem.setText("Clear List");
		clearListMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clearListMenuItemActionPerformed(evt);
			}
		});
		editMenu.add(clearListMenuItem);

		menuBar.add(editMenu);

		optionsMenu.setText("Options");

		readTagsMenuCheckbox.setSelected(true);
		readTagsMenuCheckbox.setText("Read Tags");
		readTagsMenuCheckbox.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				readTagsMenuCheckboxItemStateChanged(evt);
			}
		});
		optionsMenu.add(readTagsMenuCheckbox);

		menuBar.add(optionsMenu);

		helpMenu.setText("Help");
		helpMenu.setMaximumSize(new java.awt.Dimension(40, 32767));
		helpMenu.setMinimumSize(new java.awt.Dimension(40, 0));

		aboutMenuItem.setText("About");
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 895, Short.MAX_VALUE)
								.addGroup(layout.createSequentialGroup()
										.addComponent(jScrollPane2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(filterButton))
										.addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGap(12, 12, 12)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(filterButton))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(statusLabel)
								.addGap(4, 4, 4))
				);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
		updateTable();
	}//GEN-LAST:event_filterButtonActionPerformed

	private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
		if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
			int i;
			if (!table.isRowSelected(i = table.rowAtPoint(evt.getPoint()))) {
				table.clearSelection();
				table.addRowSelectionInterval(i, i);
			}
			popupMenu.showPopup();
		}
	}//GEN-LAST:event_tableMouseClicked

	private void readTagsMenuCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_readTagsMenuCheckboxItemStateChanged
		new Actions(this).readTagsStateChanged(evt);
	}//GEN-LAST:event_readTagsMenuCheckboxItemStateChanged

	private void startScanMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startScanMenuItemActionPerformed
		new Actions(this).startScanActionPerformed(evt);
	}//GEN-LAST:event_startScanMenuItemActionPerformed

	private void startCrcMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startCrcMenuItemActionPerformed
		new Actions(this).startCrcActionPerformed(evt);
	}//GEN-LAST:event_startCrcMenuItemActionPerformed

	private void changePathMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePathMenuItemActionPerformed
		new Actions(this).changePathActionPerformed(evt);
	}//GEN-LAST:event_changePathMenuItemActionPerformed

	private void exportTxtMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportTxtMenuItemActionPerformed
		new Actions(this).exportTxtActionPerformed(evt);
	}//GEN-LAST:event_exportTxtMenuItemActionPerformed

	private void clearListMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearListMenuItemActionPerformed
		new Actions(this).clearListActionPerformed(evt);
	}//GEN-LAST:event_clearListMenuItemActionPerformed

	private void selectAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllMenuItemActionPerformed
		new Actions(this).selectAllActionPerformed(evt);
	}//GEN-LAST:event_selectAllMenuItemActionPerformed

	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
		new Actions(this).aboutMenuPerformed(evt);
	}//GEN-LAST:event_aboutMenuItemActionPerformed

	private void saveMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItem1ActionPerformed
		new Actions(this).saveActionPerformed(evt);
	}//GEN-LAST:event_saveMenuItem1ActionPerformed

	private void loadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMenuItemActionPerformed
		new Actions(this).loadActionPerformed(evt);
	}//GEN-LAST:event_loadMenuItemActionPerformed

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
		System.exit(0);
	}//GEN-LAST:event_exitMenuItemActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
				javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ReleaseLister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ReleaseLister releaseLister = new ReleaseLister();
				PopUpMenu popupMenu = new PopUpMenu(releaseLister);
				releaseLister.setPopupMenu(popupMenu);
			}
		});

	}
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JMenuItem changePathMenuItem;
	private javax.swing.JMenuItem clearListMenuItem;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JMenuItem exportTxtMenuItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JButton filterButton;
	private javax.swing.JTextPane filterPane;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JMenuItem loadMenuItem;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenu optionsMenu;
	private javax.swing.JCheckBoxMenuItem readTagsMenuCheckbox;
	private javax.swing.JMenuItem saveMenuItem1;
	private javax.swing.JMenuItem selectAllMenuItem;
	private javax.swing.JMenuItem startCrcMenuItem;
	private javax.swing.JMenuItem startScanMenuItem;
	private javax.swing.JLabel statusLabel;
	private javax.swing.JTable table;
	// End of variables declaration//GEN-END:variables

	/**
	 * Clears the whole table
	 */
	public void clearTable() {
		while (tableModel.getRowCount() != 0) {
			tableModel.removeRow(0);
		}
	}


	private void setComperators(){
		TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>)table.getRowSorter();
		rowSorter.setComparator(COL_BITRATE, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int i1 = Integer.valueOf(o1.split("k")[0]);
				int i2 = Integer.valueOf(o2.split("k")[0]);
				return i1-i2;
			}
		});
		rowSorter.setComparator(COL_RELEASE_SIZE, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				float i1 = Float.valueOf(o1.split(" ")[0]);
				float i2 = Float.valueOf(o2.split(" ")[0]);
				if (i1 < i2) return -1;
				if (i1 > i2) return 1;
				return 0;
			}
		});
		table.setRowSorter(rowSorter);
	}

	/*
	 * Adds selection listener to table. Sets mark, unmark, openNfo components
	 * depending on selection
	 */
	private void addListSelectionListener() {

		tableSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				boolean cancheck = false;

				boolean canOpenNFO = false;
				for (int row : table.getSelectedRows()) {
					if (tableModel.getValueAt(table.convertRowIndexToModel(row), COL_ISCOMPLETE) == Boolean.TRUE) {
						cancheck = true;
					}

					if (tableModel.getValueAt(table.convertRowIndexToModel(row), COL_HASNFO) == Boolean.TRUE) {
						canOpenNFO = true;
					}

					if (cancheck && canOpenNFO) {
						break;
					}
				}

				guiComponents.setStartCrcCheckComponentsEnabled(cancheck);
				guiComponents.setOpenNFOComponentsEnabled(canOpenNFO);

			}
		};
		table.getSelectionModel().addListSelectionListener(tableSelectionListener);

	}

	/*
	 * ---------------------------------------
	 * ---------Custom CellRenderers----------
	 * ---------------------------------------
	 */
	private class BooleanCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value instanceof Boolean) {
				boolean enabled = ((Boolean) value).booleanValue();


				if (column == table.convertColumnIndexToView(COL_ISCOMPLETE)
						|| column == table.convertColumnIndexToView(COL_HASNFO)) {
					if (enabled) {
						return (isSelected ? okIconLabelSelected : (row % 2 == 0 ? okIconLabelBright : okIconLabelDark));
					} else {
						return (isSelected ? falseIconLabelSelected : (row % 2 == 0 ? falseIconLabelBright : falseIconLabelDark));
					}
				}

			}
			return super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
		}
	}

	private class StringCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value instanceof String && column == ReleaseLister.this.table.convertColumnIndexToView(COL_CRC_OK)) {
				String text = ((String) value);

				JLabel label;
				if (text.compareTo("OK") == 0) {
					label = new JLabel("OK");
					label.setBackground(new Color(156, 207, 84));
				} else if (text.compareTo("FALSE") == 0) {
					label = new JLabel("FALSE");
					label.setBackground(new Color(255, 128, 128));
				} else {
					label = new JLabel("");
					label.setBackground(isSelected ? ReleaseLister.this.table.getSelectionBackground()
							: (row % 2 == 0 ? Color.white : ReleaseLister.this.table.getBackground()));
				}

				label.setOpaque(true);
				label.setHorizontalAlignment(SwingConstants.CENTER);

				return label;

			} else {
				return super.getTableCellRendererComponent(table,
						value, isSelected, hasFocus, row, column);
			}
		}
	}

	/*
	 * ---------------------------------------
	 * ---------Getters and Setters-----------
	 * ---------------------------------------
	 */
	public JTable getTable() {
		return this.table;
	}

	public void setStatusLabel(String text) {
		statusLabel.setText(text);
	}

	public String getVersion() {
		return version;
	}

	public boolean getReadTagOption() {
		return readTagOption;
	}

	public void setReadTagOption(boolean readTagOption) {
		this.readTagOption = readTagOption;
	}

	public InterruptableRunnable getInterruptableRunnable() {
		return interruptableRunnable;
	}
	
	public void setInterruptableTunnable(InterruptableRunnable process){
		this.interruptableRunnable = process;
	}


}
