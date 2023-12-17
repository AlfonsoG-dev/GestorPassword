package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;
import Utils.QueryUtils;



public class PanelPrincipal {

    private JFrame myFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private QueryUtils queryUtils;
    private QueryDAO<Cuenta> cuentaDAO;

    public PanelPrincipal(String frameTitle, String tableTitle, int hight, int height, DbConfig myConfig) {
        queryUtils = new QueryUtils();
        cuentaDAO = new QueryDAO<>("cuenta", myConfig);
        CreateUI(frameTitle, tableTitle, hight, height, myConfig);
    }
    public Object[][] TableContent() {
        ArrayList<Cuenta> cuentaList = cuentaDAO.ReadAll(new CuentaBuilder());
        String results = "";
        for(Cuenta miCuenta: cuentaList) {
            if(miCuenta.getUpdate_at() != null && miCuenta.getUpdate_at().isEmpty() == false) {
                results = queryUtils.GetModelType(miCuenta.GetAllProperties(), true);
            } else {
                results = queryUtils.GetModelType(miCuenta.GetAllProperties(), true).replace("'", "") + ",null";
            }
        }
        Object[][] data = {
            results.split(",")
        };
        return data;
    }
    public JPanel TableComponent(String tableText) {
        headerLabel.setText(tableText);
        String modelColumns = queryUtils.GetModelColumns(new Cuenta().InitModel(), true);

        String[] columns = modelColumns.split(",");

        JTable mTable = new JTable(TableContent(), columns);
        JScrollPane scroll = new JScrollPane(mTable);
        scroll.setSize(300, 300);
        mTable.setFillsViewportHeight(true);
        controlPanel.add(scroll);
        return controlPanel;
    }
    public JPanel OptionsComponent(DbConfig myConfig) {
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PanelRegistro("Register", 500, 600, myConfig);
            }
        });

        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("delete");

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());
        optionPanel.add(insertButton);
        optionPanel.add(updateButton);
        optionPanel.add(deleteButton);
        return optionPanel;
    }
    public void CreateUI(String frameTitle, String tableTitle, int hight, int height, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));
        myFrame.setLocationRelativeTo(null);

        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        
        headerLabel = new JLabel("", JLabel.CENTER);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        myFrame.add(headerLabel);
        myFrame.add(TableComponent(tableTitle));
        myFrame.add(OptionsComponent(myConfig));
        
        myFrame.setVisible(true);
    }
}
