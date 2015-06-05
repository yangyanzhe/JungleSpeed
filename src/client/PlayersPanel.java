package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class PlayersPanel extends JPanel {
	static final int ICON_WIDTH = 50, NAME_WIDTH = 150, SCORE_WIDTH = 100;
	static final int TABLE_WIDTH = ICON_WIDTH + NAME_WIDTH + SCORE_WIDTH;
	static final int TABLE_HEIGHT = GamePanel.GAME_HEIGHT / 2;
	static final Color BACKGROUND_COLOR = new Color(46,110,163);
	
	Client client;
	
	JPanel labelPanel;
	PlayerTableModel playerModel;
	SortedTableModel sortedPlayerModel;
	JTable playerTable;
	JScrollPane scrollPane;
	
	public PlayersPanel(Client client) {
		this.client = client;
		init();
	}
	
	public void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.add(new JLabel("在线玩家"));
		labelPanel.add(Box.createGlue());
		this.add(labelPanel);
		
		String[] columnName = { "头像", "昵称", "积分" };
		playerModel = new PlayerTableModel(columnName, null);
		playerTable = new JTable(playerModel);
//		sortedPlayerModel = new SortedTableModel(playerTable.getModel());
//		playerTable.setModel(sortedPlayerModel);
		
//		playerTable.getTableHeader().addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent event) {
//				if (event.getClickCount() < 2) {
//					return;
//				}
//				int tableColumn = playerTable.columnAtPoint(event.getPoint());
//				int modelColumn = playerTable.convertColumnIndexToModel(tableColumn);
//				sortedPlayerModel.sort(modelColumn);
//			}
//		});
		
		playerTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
		playerTable.setDefaultRenderer((new String()).getClass(), new StringRenderer());
		
		playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerTable.getTableHeader().setReorderingAllowed(false);
		
		scrollPane = new JScrollPane(playerTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		TableColumn col;
		col = playerTable.getColumn(columnName[0]);
		col.setPreferredWidth(ICON_WIDTH);
		col = playerTable.getColumn(columnName[1]);
		col.setPreferredWidth(NAME_WIDTH);
		col = playerTable.getColumn(columnName[2]);
		col.setPreferredWidth(SCORE_WIDTH);
		scrollPane.setPreferredSize(new Dimension(TABLE_WIDTH, 
				TABLE_HEIGHT - labelPanel.getPreferredSize().height
				 			 - getInsets().top - getInsets().bottom));
		
		this.add(scrollPane);
	}
	
	public void addPlayer(ImageIcon image, String name, int score) {
		playerModel.addRow(new Object[] { image, name, (new Integer(score)).toString() });
	}
	
	public void removePlayer(String name) {
		int rowsNum = playerModel.getRowCount();
		for (int i = 0; i < rowsNum; i++) {
			if (playerModel.getValueAt(i, 1).equals(name)) {
				playerModel.removeRow(i);
				break;
			}
		}
	}
}

@SuppressWarnings("serial")
class PlayerTableModel extends DefaultTableModel {
	public PlayerTableModel(String[] columnNames, Object[][] cells) {
		super(cells, columnNames);
	}
	
	public Class getColumnClass(int c) {
		switch (c) {
			case 0:
				return (new ImageIcon()).getClass();
			default:
				return (new String()).getClass();
		}
	}
	
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}

@SuppressWarnings("serial")
class SortedTableModel extends AbstractTableModel {
	private TableModel model;
	private int sortColumn;
	private Row[] rows;
	
	public SortedTableModel(TableModel model) {
		this.model = model;
		rows = new Row[model.getRowCount()];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new Row(i);
		}
	}
	
	public void sort(int c) {
		sortColumn = c;
		java.util.Arrays.sort(rows);
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return model.getColumnCount();
	}

	@Override
	public Object getValueAt(int r, int c) {
		return model.getValueAt(rows[r].index, c);
	}
	public boolean isCellEditable(int r, int c) {
		return model.isCellEditable(rows[r].index, c);
	}
	public void setValueAt(Object aValue, int r, int c) {
		model.setValueAt(aValue, rows[r].index, c);
	}
	
	private class Row implements Comparable<Row> {
		public int index;
		public Row(int index) {
			this.index = index;
		}
		public int compareTo(Row other) {
			Object a = model.getValueAt(index, sortColumn);
			Object b = model.getValueAt(other.index, sortColumn);
			if (a instanceof Comparable) {
				return ((Comparable)a).compareTo(b);
			}
			else {
				return a.toString().compareTo(b.toString());
			}
		}
	}
} 

@SuppressWarnings("serial")
class ImageRenderer extends JPanel implements TableCellRenderer {
	private Image image;
	private Color background;
	final int ICON_WIDTH = 20, ICON_HEIGHT = 20;
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value != null) {
			image = ((ImageIcon)value).getImage();
		}
		if (isSelected) {
			background = PlayersPanel.BACKGROUND_COLOR;
        }
        else {
            background = Color.white;
        }
		return this;
	}
	
	public void paint(Graphics g) {
		g.setColor(background);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, (getWidth()-ICON_WIDTH)/2, (getHeight()-ICON_HEIGHT)/2, 
				ICON_WIDTH, ICON_HEIGHT, null);
	}
}

@SuppressWarnings("serial")
class StringRenderer extends JLabel implements TableCellRenderer {
	public StringRenderer() {
		setOpaque(true);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setText((String)value);
		if (isSelected) {
            setForeground(Color.white);
            setBackground(PlayersPanel.BACKGROUND_COLOR);
        }
        else {
            setForeground(Color.black);
            setBackground(Color.white);
        }
		return this;
	}
	
}