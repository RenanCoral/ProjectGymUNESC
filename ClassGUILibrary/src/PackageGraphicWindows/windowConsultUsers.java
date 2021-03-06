package PackageGraphicWindows;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import PackageClassLibrary.Observable;
import PackageClassLibrary.Observer;
import PackageDatabase.dao.DAOuser;
import PackageDatabase.model.modelUser;

@SuppressWarnings("serial")
public class windowConsultUsers extends JDialog implements Observable{

	private JTable tblUsers;
	private JScrollPane spnUsers;
	private DefaultTableModel tblModelUsers = new DefaultTableModel(){public boolean isCellEditable(int row, int col) {return false;}};
	
	private JLabel lblUser = new JLabel();
	
	private JTextField txfSerchUser = new JTextField();
	
	private JButton btnConsult = new JButton();
	private JButton btnCancel = new JButton();
	private JButton btnDelete = new JButton();
	private JButton btnOk = new JButton();
	
	
	
	private List<Observer> listObs = new ArrayList<Observer>();
	
	List<Object> obj;
	
	private DAOuser dao;
	
	private HashMap<Integer, String> hsmProfile = new HashMap<Integer, String>();
	
	{
		hsmProfile.put(0, "Administrador");
		hsmProfile.put(1, "Cadastral");
		hsmProfile.put(2, "Financeiro");
	};
	
	JPanel panel = new JPanel();
	
	public windowConsultUsers (final Observer obs) { 

		AddObserver(obs);
		setSize(400,270);
		setTitle("Consular Clientes");
		
		setLocationRelativeTo(null);
		setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		
		
		buildWindow();
		actionsButtons();
		loadData();
		
	}
	
	public void buildWindow() {
		
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,5,5,5));
		setContentPane(panel);
		panel.setLayout(null);	
		
		tblModelUsers.addColumn("C�digo");
		tblModelUsers.addColumn("Usu�rio");
		tblModelUsers.addColumn("Senha");
		tblModelUsers.addColumn("Perfil");
		
		
		tblUsers = new JTable(tblModelUsers);
		spnUsers = new JScrollPane(tblUsers);
		spnUsers.setBounds(0, 65, 395, 167);
		panel.add(spnUsers);
		tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		lblUser = new JLabel("Usu�rio");
		lblUser.setBounds(10, 5, 100, 25);
		lblUser.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		panel.add(lblUser);
		
		txfSerchUser = new JTextField();
		txfSerchUser.setBounds(75, 5, 130, 25);
		panel.add(txfSerchUser);
		
		btnConsult = new JButton("Consultar");
		btnConsult.setBounds(210, 5, 78, 25);
		panel.add(btnConsult);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.setBounds(290, 5, 78, 25);
		panel.add(btnCancel);
		
		btnDelete = new JButton("Deletar");
		btnDelete.setBounds(210, 35, 78, 25);
		panel.add(btnDelete);
		
		btnOk = new JButton("OK");
		btnOk.setBounds(290, 35, 78, 25);
		panel.add(btnOk);
		
	}
	
	public void actionsButtons() {
		
		btnOk.addActionListener(e->{
			NotifyAllObserver();
			dispose();
		});
		
		btnConsult.addActionListener(e->{
			loadData();
		});
		
		btnDelete.addActionListener(e->{
			removeUserFromDatabase();
		});
		
		btnCancel.addActionListener(e->{
			dispose();
		});	
	}

	public void loadData() {
		while(tblModelUsers.getRowCount()>0) {
			tblModelUsers.removeRow(0);
		}
		
		try {
			obj = dao.Select(txfSerchUser.getText());	
			
			for(Object ob : obj) {
				modelUser user = (modelUser) ob;
				tblModelUsers.addRow(new String[] {""+user.getIdCode(), user.getUser(), user.getPassword(), hsmProfile.get(user.getProfile())});
			}
		}catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Erro Carregar dados na table:" + e1.getMessage());
		}
	}
	
	public void removeUserFromDatabase() {
		modelUser user = new modelUser();
		user.setIdCode(Integer.parseInt(tblUsers.getValueAt(tblUsers.getSelectedRow(), 0).toString()));
		
		try {
			if (user.getUser() == "admin") {return;}
			dao.Delete(user);
			JOptionPane.showMessageDialog(null, "Usu�rio Deletado");
			tblModelUsers.removeRow(tblUsers.getSelectedRow());
			
		}catch (Exception e) {
			JOptionPane.showInternalMessageDialog(null, "Erro Deletar Usu�rio:" + e.getMessage());
		}
		
	}

	@Override
	public void AddObserver(Observer obj) {
		listObs.add(obj);
	}

	@Override
	public void RemoveObserver(Observer obj) {
		listObs.remove(obj);
	}

	@Override
	public void NotifyAllObserver() {
		for(int i = 0; i < listObs.size(); i++) {
			Observer obs = listObs.get(i);
			obs.Update(obj.get(tblUsers.getSelectedRow()));
		}
	}
}
