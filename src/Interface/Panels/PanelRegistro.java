package Interface.Panels;

import java.sql.Connection;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


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

public class PanelRegistro {
    /**
     * the panel frame
     */
    private JFrame myFrame;
    /**
     * the logged user
     */
    private int loggedUser;
    /**
     * the label for the panel name
     */
    private JLabel headerLabel;
    /**
     * the nombre text field
     */
    private JTextField txtNombre;
    /**
     * the email text field
     */
    private JTextField txtEmail;
    /**
     * logged user text field 
     */
    private JTextField txtLoggedUser;
    /**
     * the password text field
     */
    private JTextField txtPassword;
    /**
     * the current principal panel
     */
    private JPanel pPrincipal;
    /**
     * the cursor to allow transaction for commit or rollback
     */
    private Connection cursor;
    /**
     * the frame of the main panel
     */
    private JFrame mainFrame;
    /**
     * DAO class for account
     */
    private QueryDAO<Cuenta> cuentaDAO;
    /**
     * database configuration
     */
    private DbConfig myConfig;
    /**
     * constructor
     */
    public PanelRegistro(String frameTitle, int hight, int height, DbConfig nConfig, int pLoggedUser, Connection miCursor, JFrame nMainFrame, QueryDAO<Cuenta> nCuetaDAO) {
        loggedUser = pLoggedUser;
        myConfig = nConfig;
        cursor = miCursor;
        mainFrame = nMainFrame;
        cuentaDAO = nCuetaDAO;
        CreateUI(frameTitle, hight, height);
    }
    /**
     * set the panel components and its content
     * @return the panel with the content setted
     */
    private JPanel OptionsComponent() {
        JPanel pOptions = new JPanel();
        pOptions.setLayout(new GridLayout(4, 2));

        pOptions.add(new JLabel(" Nombre"));
        pOptions.add(txtNombre = new JTextField());
        pOptions.add(new JLabel(" Email"));
        pOptions.add(txtEmail = new JTextField());
        pOptions.add(new JLabel(" User_id"));

        pOptions.add(txtLoggedUser = new JTextField(String.valueOf(loggedUser)));
        txtLoggedUser.setEnabled(false);

        pOptions.add(new JLabel(" Password"));
        pOptions.add(txtPassword = new JTextField());

        return pOptions;
    }
    /**
     * implements the OKButton handler
     * <br> pre: </br> disable the main frame to insert the new register
     * <br> post: </br> enables the main frame and close the current frame
     * @param OKButton: panel OKButton to insert a new register to the database
     */
    private void OkButtonHandler(JButton OKButton) {
        OKButton.setMnemonic(KeyEvent.VK_ENTER);
            OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        String nombre = txtNombre.getText();
                        String email = txtEmail.getText();
                        String password = txtPassword.getText();
                        Cuenta nueva = new Cuenta(nombre, email, loggedUser, password);
                        nueva.setCreate_at();
                        String condition = "nombre: "  + nueva.getNombre() + ", user_id_fk" + nueva.getUser_id_fk();
                        if(JOptionPane.showConfirmDialog(myFrame, "Do you want to register?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                            cuentaDAO.InsertNewRegister(nueva, condition, "and", new CuentaBuilder());
                            if(mainFrame != null) {
                                mainFrame.setEnabled(true);
                                myFrame.dispose();
                            } else {
                                new PanelPrincipal(myConfig, loggedUser, myFrame, cursor, cuentaDAO);
                                myFrame.dispose();
                            }
                        }
                    } catch(Exception ex) {
                        System.out.println(ex);
                    } finally {
                        JOptionPane.showMessageDialog(myFrame, "reload the window to see the changes", "INFO", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
    }
    /**
     * implements the cancelButton handler
     * <br> post: </br> close the current frame and enables the main frame
     * @param cancelButton: panel cancelButton to go back to the mainFrame
     */
    private void CancelButtonHandler(JButton cancelButton) {
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(myFrame, "Do you want to cancel?", "Register operation",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    if(mainFrame != null) {
                        mainFrame.setEnabled(true);
                        myFrame.dispose();
                    } else {
                        myFrame.dispose();
                    }
                }
            }
        });
    }
    /**
     * sets the panel content for the options
     * <br> post: </br> ok button insert data cancel redirects to main frame
     */
    private JPanel OperationOptions() {

        JPanel options = new JPanel();
        options.setLayout(new FlowLayout());

        JButton OKButton = new JButton("OK");
        options.add(OKButton);
        OkButtonHandler(OKButton);

        JButton cancelButton = new JButton("cancel");
        options.add(cancelButton);
        CancelButtonHandler(cancelButton);
        return options;
    }
    /**
     * creates the ui for the current frame
     * @param frameTitle: frame title
     * @param hight: frame hight
     * @param height: frame height
     */
    public void CreateUI(String frameTitle, int hight, int height) {
        myFrame = new JFrame(frameTitle);
        myFrame.setSize(hight, height);
        myFrame.setLayout(new GridLayout(3, 1));

        myFrame.addWindowListener(new WindowAdapter() {
            // changes the close operation
            public void windowClosing(WindowEvent we) {
                myFrame.dispose();
                mainFrame.setEnabled(true);
            }
        });

        headerLabel = new JLabel("Register", JLabel.CENTER);

        pPrincipal = new JPanel();
        pPrincipal.setLayout(new GridLayout(2, 1));

        pPrincipal.add(OptionsComponent());

        pPrincipal.add(OperationOptions());

        myFrame.add(headerLabel);
        myFrame.add(pPrincipal);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
