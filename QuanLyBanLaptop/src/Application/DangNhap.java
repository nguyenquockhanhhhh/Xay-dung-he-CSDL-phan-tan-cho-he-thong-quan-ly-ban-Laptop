package Application;

import Admin.GiaoDienQuanLyLapTopServer;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSQL;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import javax.swing.table.DefaultTableModel;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import Application.TenThanhPho;

public class DangNhap extends JFrame {

    DefaultTableModel tableModel = new DefaultTableModel();
    Statement stmt;
    ResultSet rs;
    PreparedStatement pst;
    Connection conn = null;

    public DangNhap() {
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null);
        JComboBox<String> cbcity;
        conn = ConnectToSQL.connectToDatabase();
        load_chinhanh();
    }

    public void load_chinhanh() {
        try {
            pst = conn.prepareStatement("SELECT TenTP FROM ThanhPho");
            rs = pst.executeQuery();
            cbcity.removeAllItems(); // Clear previous items
            while (rs.next()) {
                cbcity.addItem(rs.getString(1)); // Add items from the ResultSet
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Make sure to close resources
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        dangnhap = new javax.swing.JPanel();
        signin = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        panel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        tf_username = new javax.swing.JTextField();
        pass = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        lab8 = new javax.swing.JLabel();
        lab7 = new javax.swing.JLabel();
        lab9 = new javax.swing.JLabel();
        cbcity = new javax.swing.JComboBox<>();
        lab10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setBackground(new java.awt.Color(51, 204, 255));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, -1, 37));

        dangnhap.setBackground(new java.awt.Color(51, 204, 255));
        dangnhap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dangnhapMouseClicked(evt);
            }
        });

        signin.setFont(new java.awt.Font("Segoe UI Black", 3, 18)); // NOI18N
        signin.setText("    Sign in");

        javax.swing.GroupLayout dangnhapLayout = new javax.swing.GroupLayout(dangnhap);
        dangnhap.setLayout(dangnhapLayout);
        dangnhapLayout.setHorizontalGroup(
            dangnhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dangnhapLayout.createSequentialGroup()
                .addComponent(signin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        dangnhapLayout.setVerticalGroup(
            dangnhapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dangnhapLayout.createSequentialGroup()
                .addComponent(signin, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE))
        );

        jPanel1.add(dangnhap, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 340, 100, 40));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8_Cancel_30px.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, -1, -1));

        panel1.setBackground(new java.awt.Color(51, 51, 51));
        panel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        panel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 320, 200, 10));

        jPanel4.setBackground(new java.awt.Color(102, 204, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(140, 5));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 190, 7));

        tf_username.setBackground(new java.awt.Color(51, 51, 51));
        tf_username.setForeground(new java.awt.Color(255, 255, 255));
        tf_username.setBorder(null);
        tf_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_usernameActionPerformed(evt);
            }
        });
        tf_username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tf_usernameKeyTyped(evt);
            }
        });
        panel1.add(tf_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 200, 30));

        pass.setBackground(new java.awt.Color(51, 51, 51));
        pass.setForeground(new java.awt.Color(255, 255, 255));
        pass.setBorder(null);
        pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passActionPerformed(evt);
            }
        });
        pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                passKeyTyped(evt);
            }
        });
        panel1.add(pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 290, 200, 30));
        panel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, 50));
        panel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 250, 200, 10));
        panel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 320, 200, 10));

        lab8.setFont(new java.awt.Font("Tahoma", 3, 24)); // NOI18N
        lab8.setForeground(new java.awt.Color(102, 204, 255));
        lab8.setText("QuanLyLaptop");
        panel1.add(lab8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 280, 50));

        lab7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8_Lock_32px.png"))); // NOI18N
        panel1.add(lab7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 40, 50));

        lab9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/city.png"))); // NOI18N
        panel1.add(lab9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 70));

        cbcity.setBackground(new java.awt.Color(153, 255, 255));
        cbcity.setFont(new java.awt.Font("Segoe UI Emoji", 3, 18)); // NOI18N
        cbcity.setForeground(new java.awt.Color(255, 51, 0));
        cbcity.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đa Nang", "Hue", "Ha Noi" }));
        cbcity.setBorder(null);
        cbcity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbcityActionPerformed(evt);
            }
        });
        panel1.add(cbcity, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 130, 210, 40));

        lab10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/icons8_User_35px_1.png"))); // NOI18N
        panel1.add(lab10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 40, 70));

        jPanel1.add(panel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 30, 320, 370));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/bg.jpg"))); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 780, 450));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
        jLabel4.setVisible(false);
        pass.setVisible(true);
    }//GEN-LAST:event_jLabel4MousePressed

    private void tf_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_usernameActionPerformed

    private void tf_usernameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_usernameKeyTyped
        //we have give the condition to Jtextfield for setVisible the label true
    }//GEN-LAST:event_tf_usernameKeyTyped

    private void passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passActionPerformed

    private void passKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passKeyTyped

        // TODO add your handling code here:
    }//GEN-LAST:event_passKeyTyped

    private void cbcityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbcityActionPerformed

    }//GEN-LAST:event_cbcityActionPerformed

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        System.exit(0);
    }//GEN-LAST:event_jLabel10MouseClicked

    private void dangnhapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dangnhapMouseClicked
        String name = tf_username.getText();
        String password = new String(pass.getPassword());
        String selectedCity = (String) cbcity.getSelectedItem();

        if (name.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tài khoản & mật khẩu không thể bỏ trống", "Thông Báo", JOptionPane.OK_OPTION);
            return;
        }

        try {
            stmt = conn.createStatement();

            // Truy vấn để kiểm tra tài khoản và mật khẩu
            String query = "SELECT * FROM Users WHERE UserName = '" + name + "' AND Pass = '" + password + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Đăng nhập thành công, lưu tên thành phố
                TenThanhPho.setTenthanhpho(selectedCity);
                JOptionPane.showMessageDialog(null, "Đăng nhập thành công", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);

                // Kiểm tra tên thành phố được chọn
                if (selectedCity.equals("Tru So Chinh")) {
                    // Mở giao diện chính cho trụ sở chính
                    GiaoDienQuanLyLapTopServer giaodienServer = new GiaoDienQuanLyLapTopServer();
                    giaodienServer.setVisible(true);
                } else {
                    // Mở giao diện cho các thành phố khác
                    GiaoDienQuanLyLapTop giaodien = new GiaoDienQuanLyLapTop();
                    giaodien.setVisible(true);
                }

                this.dispose(); // Đóng cửa sổ đăng nhập
            } else {
                JOptionPane.showMessageDialog(null, "Đăng nhập thất bại", "Thông Báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_dangnhapMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DangNhap().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbcity;
    private javax.swing.JPanel dangnhap;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lab10;
    private javax.swing.JLabel lab7;
    private javax.swing.JLabel lab8;
    private javax.swing.JLabel lab9;
    private javax.swing.JPanel panel1;
    private javax.swing.JPasswordField pass;
    private javax.swing.JLabel signin;
    private javax.swing.JTextField tf_username;
    // End of variables declaration//GEN-END:variables
}
