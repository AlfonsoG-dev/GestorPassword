package Interface.Panels;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Conexion.Query.QueryDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Mundo.Cuentas.CuentaBuilder;

public class PanelUpdate {

    private JFrame myFrame;
    private int loggedUser;
    private JPanel pPrincipal;
    private JTextField txtIdPk;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtUserId;
    private JTextField txtPassword;

    public PanelUpdate(String frameTitle, int hight, int height, Cuenta updateCuenta, DbConfig myConfig) {
        loggedUser = updateCuenta.getUser_id_fk();
        CreateUI(frameTitle, hight, height, updateCuenta, myConfig);
    }
    private JPanel OptionsComponent(Cuenta updateCuenta) {

        txtUserId = new JTextField(String.valueOf(updateCuenta.getUser_id_fk()));
        txtUserId.setEditable(false);

        txtIdPk = new JTextField(String.valueOf(updateCuenta.getId_pk()));
        txtIdPk.setEditable(false);

        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(5, 2));
        pOptions.add(new JLabel("ID_pk"));
        pOptions.add(txtIdPk);
        pOptions.add(new JLabel("Nombre"));
        pOptions.add(txtNombre = new JTextField(updateCuenta.getNombre()));
        pOptions.add(new JLabel("Email"));
        pOptions.add(txtEmail = new JTextField(updateCuenta.getEmail()));
        pOptions.add(new JLabel("User_Id_fk"));
        pOptions.add(txtUserId);
        pOptions.add(new JLabel("Password"));
        pOptions.add(txtPassword = new JTextField(updateCuenta.getPassword()));
        return pOptions;
    }

    private void OkButtonHandler(JButton OKButton, DbConfig myConfig, Cuenta toUpdate) {
        OKButton.setMnemonic(KeyEvent.VK_ENTER);
        OKButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(txtNombre.getText() != null && txtNombre.getText() != toUpdate.getNombre()) {
                    toUpdate.setNombre(txtNombre.getText());
                }
                if(txtEmail.getText() != null && txtEmail.getText() != toUpdate.getEmail()) {
                    toUpdate.setEmail(txtEmail.getText());
                }
                if(txtPassword.getText() != null && txtPassword.getText() != toUpdate.getPassword()) {
                    toUpdate.setPassword(txtPassword.getText());
                }
                toUpdate.setUpdate_at();
                try {
                    QueryDAO<Cuenta> cuentaDAO = new QueryDAO<Cuenta>("cuenta", myConfig);
                    String condition = "id_pk: " + toUpdate.getId_pk();
                    if(JOptionPane.showConfirmDialog(myFrame, "Do you want to update?", "Update operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                        cuentaDAO.UpdateRegister(toUpdate, condition, "and", new CuentaBuilder());
                        new PanelPrincipal(myConfig, loggedUser);
                        myFrame.setVisible(false);
                    }
                } catch(Exception er) {
                    System.err.println(er);
                }
            }
        });
    }
    private void CancelButtonHandler(JButton cancelButton, DbConfig myConfig) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to cancel?", "Update operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    myFrame.setVisible(false);
                    new PanelPrincipal(myConfig, loggedUser);
                }
            }
        });
    }
    private JPanel OperationsComponent(DbConfig myConfig, Cuenta toUpdate) {
        JPanel pOperations = new JPanel();
        pOperations.setLayout(new FlowLayout());
        JButton oKButton = new JButton("OK");
        pOperations.add(oKButton);
        OkButtonHandler(oKButton, myConfig, toUpdate);


        JButton cancelButton = new JButton("Cancel");
        pOperations.add(cancelButton);
        CancelButtonHandler(cancelButton, myConfig);
        return pOperations;
    }
    public void CreateUI(String frameTitle, int hight, int height, Cuenta updateCuenta, DbConfig myConfig) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        JLabel headerLabel = new JLabel("Update", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent(updateCuenta));
        pPrincipal.add(OperationsComponent(myConfig, updateCuenta));

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
