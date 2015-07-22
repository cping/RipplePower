package org.ripple.power.ui.btc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.ripple.power.Helper;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.btc.Alert;
import org.ripple.power.txns.btc.AlertListener;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.BlockStatus;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.txns.btc.ChainListener;
import org.ripple.power.txns.btc.ConnectionListener;
import org.ripple.power.txns.btc.NetParams;
import org.ripple.power.txns.btc.Peer;
import org.ripple.power.txns.btc.Sha256Hash;
import org.ripple.power.txns.btc.StoredBlock;
import org.ripple.power.ui.UIConfig;
import org.ripple.power.ui.table.AddressTable;

public class StatusPanel extends JPanel implements AlertListener, ChainListener, ConnectionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] serviceNames = {"Network"};

    private static final Class<?>[] blockColumnClasses = {
        Date.class, Integer.class, String.class, Integer.class, String.class};

    private static final String[] blockColumnNames = {
        "Date", "Height", "Block", "Version", "Status"};

    private static final int[] blockColumnTypes = {
        AddressTable.DATE, AddressTable.INTEGER, AddressTable.HASH, AddressTable.INTEGER, AddressTable.STATUS};

    private BlockTableModel blockTableModel;

    private JTable blockTable;

    private JScrollPane blockScrollPane;

    private static final Class<?>[] alertColumnClasses = {
        Integer.class, Date.class, String.class, String.class};

    private static final String[] alertColumnNames = {
        "ID", "Expires", "Status", "Message"};

    private static final int[] alertColumnTypes = {
        AddressTable.INTEGER, AddressTable.DATE, AddressTable.STATUS, AddressTable.MESSAGE};

    private AlertTableModel alertTableModel;

    private JTable alertTable;

    private JScrollPane alertScrollPane;

    private static final Class<?>[] connectionColumnClasses = {
        Date.class, String.class, Integer.class, String.class, String.class};

    private static final String[] connectionColumnNames = {
        "Connected", "Address", "Version", "Subversion", "Services"};

    private static final int[] connectionColumnTypes = {
        AddressTable.DATE, AddressTable.ADDRESS, AddressTable.INTEGER, AddressTable.SERVICES, AddressTable.SERVICES};

    private final ConnectionTableModel connectionTableModel;

    private final JTable connectionTable;

    private final JScrollPane connectionScrollPane;

    private final JLabel chainHeadField;

    private final JLabel chainHeightField;

    private final JLabel networkDifficultyField;

    private final JLabel peerConnectionsField;


    public StatusPanel() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(UIConfig.background);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel tablePane = new JPanel();
        tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.Y_AXIS));
        tablePane.setBackground(UIConfig.background);
        try {
            alertTableModel = new AlertTableModel(alertColumnNames, alertColumnClasses);
            alertTable = new AddressTable(alertTableModel, alertColumnTypes);
            alertTable.setRowSorter(new TableRowSorter<>(alertTableModel));
            alertTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            alertTable.setPreferredScrollableViewportSize(new Dimension(
                    alertTable.getPreferredScrollableViewportSize().width,
                    alertTable.getRowHeight()*3));
            alertScrollPane = new JScrollPane(alertTable);
            tablePane.add(Box.createGlue());
            tablePane.add(new JLabel("<html><h3><font color=white>Alerts</font></h3></html>"));
            tablePane.add(alertScrollPane);
        } catch (BlockStoreException exc) {
            BTCLoader.error("Block store exception while creating alert table", exc);
        }

        connectionTableModel = new ConnectionTableModel(connectionColumnNames, connectionColumnClasses);
        connectionTable = new AddressTable(connectionTableModel, connectionColumnTypes);
        connectionTable.setRowSorter(new TableRowSorter<>(connectionTableModel));
        connectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        connectionScrollPane = new JScrollPane(connectionTable);
        tablePane.add(Box.createGlue());
        tablePane.add(new JLabel("<html><h3><font color=white>Connections</font></h3></html>"));
        tablePane.add(connectionScrollPane);

        try {
            blockTableModel = new BlockTableModel(blockColumnNames, blockColumnClasses);
            blockTable = new AddressTable(blockTableModel, blockColumnTypes);
            blockTable.setRowSorter(new TableRowSorter<>(blockTableModel));
            blockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            blockScrollPane = new JScrollPane(blockTable);
            tablePane.add(Box.createGlue());
            tablePane.add(new JLabel("<html><h3><font color=white>Recent Blocks</font></h3></html>"));
            tablePane.add(blockScrollPane);
            tablePane.add(Box.createGlue());
        } catch (BlockStoreException exc) {
            BTCLoader.error("Block store exception while creating block status table", exc);
        }

        chainHeadField = new JLabel();
        JPanel chainHeadPane = new JPanel();
        chainHeadPane.add(Box.createGlue());
        chainHeadPane.add(chainHeadField);
        chainHeadPane.add(Box.createGlue());

        chainHeightField = new JLabel();
        JPanel chainHeightPane = new JPanel();
        chainHeightPane.add(Box.createGlue());
        chainHeightPane.add(chainHeightField);
        chainHeightPane.add(Box.createGlue());

        networkDifficultyField = new JLabel();
        JPanel networkDifficultyPane = new JPanel();
        networkDifficultyPane.add(Box.createGlue());
        networkDifficultyPane.add(networkDifficultyField);
        networkDifficultyPane.add(Box.createGlue());

        peerConnectionsField = new JLabel();
        JPanel peerConnectionsPane = new JPanel();
        peerConnectionsPane.add(Box.createGlue());
        peerConnectionsPane.add(peerConnectionsField);
        peerConnectionsPane.add(Box.createGlue());

        JPanel statusPane = new JPanel();
        statusPane.setLayout(new BoxLayout(statusPane, BoxLayout.Y_AXIS));
        statusPane.setOpaque(true);
        statusPane.setBackground(UIConfig.background);

        statusPane.add(chainHeadPane);
        statusPane.add(chainHeightPane);
        statusPane.add(networkDifficultyPane);
        statusPane.add(peerConnectionsPane);
        statusPane.add(Box.createVerticalStrut(20));
        add(statusPane, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        BTCLoader.blockChain.addListener((ChainListener)this);
        BTCLoader.networkHandler.addListener((ConnectionListener)this);
        BTCLoader.networkMessageListener.addListener((AlertListener)this);
        connectionTableModel.updateConnections();
        updateStatus();
    }

    @Override
    public void blockStored(StoredBlock storedBlock) {
        blockTableModel.blockStored(storedBlock);
    }

    @Override
    public void blockUpdated(StoredBlock storedBlock) {
        blockTableModel.blockStored(storedBlock);
    }

    @Override
    public void chainUpdated() {
        LSystem.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateStatus();
			}
		});
    }

    @Override
    public void connectionStarted(Peer peer, int count) {
        connectionTableModel.addConnection(peer);
        LSystem.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateStatus();
			}
		});
    }

    @Override
    public void connectionEnded(Peer peer, int count) {
        connectionTableModel.removeConnection(peer);
        LSystem.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateStatus();
			}
		});
    }

    @Override
    public void alertReceived(Alert alert) {
        alertTableModel.addAlert(alert);
    }

    private void updateStatus() {
        Sha256Hash chainHead = BTCLoader.blockStore.getChainHead();
        chainHeadField.setText(String.format("<html><b>Chain head: %s</b></html>",
                                             chainHead.toString()));
        int chainHeight = BTCLoader.blockStore.getChainHeight();
        chainHeightField.setText(String.format("<html><b>Chain height: %d</b></html>",
                                               chainHeight));
        BigInteger targetDifficulty = BTCLoader.blockStore.getTargetDifficulty();
        BigInteger networkDifficulty = NetParams.PROOF_OF_WORK_LIMIT.divide(targetDifficulty);
        String displayDifficulty = Helper.numberToShortString(networkDifficulty);
        networkDifficultyField.setText(String.format("<html><b>Network difficulty: %s</b></html>",
                                                     displayDifficulty));
        peerConnectionsField.setText(String.format("<html><b>Peer connections: %d</b></html>",
                                                   connectionTable.getRowCount()));
        LSystem.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				repaint();
			}
		});
    }

    private class BlockStatusComparator implements Comparator<BlockStatus> {

        public BlockStatusComparator() {
        }

        @Override
        public int compare(BlockStatus o1, BlockStatus o2) {
            long t1 = o1.getTimeStamp();
            long t2 = o2.getTimeStamp();
            return (t1==t2 ? 0 : (t1>t2 ? -1 : 1));
        }
    }

    private class BlockTableModel extends AbstractTableModel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

        private final String[] columnNames;

        private final Class<?>[] columnClasses;

        private BlockStatus[] blocks;

        private final Map<Sha256Hash, BlockStatus> blockMap = new HashMap<>(50);

        private final Map<Integer, BlockStatus> heightMap = new HashMap<>(50);

        private boolean refreshPending;

        public BlockTableModel(String[] columnNames, Class<?>[] columnClasses) throws BlockStoreException {
            super();
            this.columnNames = columnNames;
            this.columnClasses = columnClasses;
            blocks = (BlockStatus[])BTCLoader.blockStore.getBlockStatus(150).toArray(new BlockStatus[0]);
            Arrays.sort(blocks, new BlockStatusComparator());
            for (BlockStatus block : blocks) {
                blockMap.put(block.getHash(), block);
                if (block.isOnChain()){
                    heightMap.put(block.getHeight(), block);
                }
            }
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return columnClasses[column];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getRowCount() {
            return blocks.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object value;
            BlockStatus status;
            synchronized(blockMap) {
                status = blocks[row];
            }
     
            switch (column) {
                case 0:                           
                    value = new Date(status.getTimeStamp()*1000);
                    break;
                case 1:                           
                    value = status.isOnChain() ? status.getHeight() : 0;
                    break;
                case 2:                         
                    value = status.getHash().toString();
                    break;
                case 3:                           
                    value = status.getVersion();
                    break;
                case 4:                            
                    if (status.isOnChain())
                        value = "On Chain";
                    else if (status.isOnHold())
                        value = "Held";
                    else
                        value = "Ready";
                    break;
                default:
                    throw new IndexOutOfBoundsException("Table column "+column+" is not valid");
            }
            return value;
        }


        public void blockStored(StoredBlock storedBlock) {
            Sha256Hash blockHash = storedBlock.getHash();
            Integer blockHeight = storedBlock.getHeight();
            synchronized(blockMap) {
         
                BlockStatus blockStatus = blockMap.get(blockHash);
                if (blockStatus == null) {
                    blockStatus = new BlockStatus(blockHash, storedBlock.getBlock().getTimeStamp(),
                                                  storedBlock.getHeight(), storedBlock.getBlock().getVersion(),
                                                  storedBlock.isOnChain(), storedBlock.isOnHold());
                    BlockStatus[] newBlocks = new BlockStatus[blocks.length+1];
                    System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
                    newBlocks[blocks.length] = blockStatus;
                    Arrays.sort(newBlocks, new BlockStatusComparator());
                    blocks = newBlocks;
                    blockMap.put(blockHash, blockStatus);
                } else {
                    blockStatus.setHeight(storedBlock.getHeight());
                    blockStatus.setChain(storedBlock.isOnChain());
                    blockStatus.setHold(storedBlock.isOnHold());
                }
          
                if (storedBlock.isOnChain()) {
                    BlockStatus chkStatus = heightMap.get(blockHeight);
                    if (chkStatus == null) {
                        heightMap.put(blockHeight, blockStatus);
                    } else if (!chkStatus.getHash().equals(blockHash)) {
                        chkStatus.setChain(false);
                        chkStatus.setHeight(0);
                        heightMap.put(blockHeight, blockStatus);
                    }
                }
            }

            if (!refreshPending) {
                refreshPending = true;
                LSystem.invokeLater(new Runnable() {
					
					@Override
					public void run() {
					    fireTableDataChanged();
	                    refreshPending = false;
					}
				});
            }
        }
    }

    private class AlertTableModel extends AbstractTableModel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final String[] columnNames;

        private final Class<?>[] columnClasses;

        private final List<Alert> alertList;

        private boolean refreshPending = false;


        public AlertTableModel(String[] columnNames, Class<?>[] columnClasses) throws BlockStoreException {
            super();
            this.columnNames = columnNames;
            this.columnClasses = columnClasses;
            alertList = BTCLoader.blockStore.getAlerts();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return columnClasses[column];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getRowCount() {
            return alertList.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object value = null;
            Alert alert = alertList.get(alertList.size()-1-row);
            switch (column) {
                case 0:                     
                    value = alert.getID();
                    break;
                case 1:                     
                    value = new Date(alert.getExpireTime()*1000);
                    break;
                case 2:                   
                    if (alert.isCanceled())
                        value = "Canceled";
                    else if (alert.getExpireTime() < System.currentTimeMillis()/1000)
                        value = "Expired";
                    else
                        value = "";
                    break;
                case 3:                     
                    value = alert.getMessage();
                    break;
            }
            return value;
        }

        public void addAlert(Alert alert) {
            alertList.add(alert);
            if (!refreshPending) {
                refreshPending = true;
                LSystem.invokeLater(new Runnable() {
					
					@Override
					public void run() {

	                    fireTableDataChanged();
	                    refreshPending = false;
	                
					}
				});
            }
        }
    }

    private class ConnectionTableModel extends AbstractTableModel {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final String[] columnNames;

        private final Class<?>[] columnClasses;

        private final List<Peer> connectionList = new ArrayList<>(128);

 
        public ConnectionTableModel(String[] columnNames, Class<?>[] columnClasses) {
            super();
            this.columnNames = columnNames;
            this.columnClasses = columnClasses;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return columnClasses[column];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

     
        @Override
        public int getRowCount() {
            return connectionList.size();
        }

        @Override
        public Object getValueAt(int row, int column) {
            Object value = null;
            Peer peer = connectionList.get(row);
            switch (column) {
                case 0:                        
                    value = new Date(peer.getAddress().getTimeConnected()*1000);
                    break;
                case 1:                        
                    value = peer.getAddress().toString();
                    break;
                case 2:                         
                    value = peer.getVersion();
                    break;
                case 3:                         
                    value = peer.getUserAgent();
                    break;
                case 4:                        
                    long services = peer.getServices();
                    StringBuilder serviceString = new StringBuilder(32);
                    for (int i=0; i<serviceNames.length; i++) {
                        if ((services & (1<<i)) != 0)
                            serviceString.append(serviceNames[i]);
                    }
                    value = serviceString.toString();
                    break;
            }
            return value;
        }

        public void updateConnections() {
            List<Peer> connections = BTCLoader.networkHandler.getConnections();
            for(Peer peer:connections){
            	if(!connectionList.contains(peer)){
            		connectionList.add(peer);
            	}
            }
        }

        public void addConnection(Peer peer) {
            ConnectionUpdate updateTask = new ConnectionUpdate(connectionList, peer, true);
            LSystem.invokeLater(updateTask);
        }

        public void removeConnection(Peer peer) {
            ConnectionUpdate updateTask = new ConnectionUpdate(connectionList, peer, false);
            LSystem.invokeLater(updateTask);
        }
    }

    private class ConnectionUpdate implements Runnable {

        private final boolean addConnection;

        private final Peer peer;

        private final List<Peer> connectionList;

     
        public ConnectionUpdate(List<Peer> connectionList, Peer peer, boolean addConnection) {
            this.connectionList = connectionList;
            this.peer = peer;
            this.addConnection = addConnection;
        }


        @Override
        public void run() {

            if (addConnection) {
                if (!connectionList.contains(peer)){
                    connectionList.add(peer);
                }
            } else {
                connectionList.remove(peer);
            }
        
            connectionTableModel.fireTableDataChanged();
        }
    }
}

