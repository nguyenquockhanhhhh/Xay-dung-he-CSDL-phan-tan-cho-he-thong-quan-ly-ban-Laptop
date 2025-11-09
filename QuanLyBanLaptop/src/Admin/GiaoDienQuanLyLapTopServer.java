package Admin;

import Admin.RequestHandler;
import Application.CuaHang;
import Application.DangNhap;
import Application.DoanhThu;
import Application.DonHang;
import Application.KhachHang;
import Application.TenThanhPho;
import Application.TenThanhPho;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSQL;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSeverOther;
import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GiaoDienQuanLyLapTopServer extends javax.swing.JFrame {

    DefaultTableModel tableModel = new DefaultTableModel();
    Statement stmt;
    ResultSet rs;
    PreparedStatement pst;
    Connection conn = null;
    String matp = null;
    String servername = "";

    // 
    static ServerSocket ss;
    static Socket s;
    static DataInputStream dis;
    static DataOutputStream dot;

    public GiaoDienQuanLyLapTopServer() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showLaptop();
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ss = new ServerSocket(1201);

                while (true) {
                    Socket socket = ss.accept(); // Chờ client kết nối
                    DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();
                    RequestHandler handler = new RequestHandler(socket, model);
                    handler.start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // lấy MaTP dựa vào tên thành phố sau khi login thành công
    public void get_tentpid() {
        String tenthanhpho = TenThanhPho.getTenthanhpho();

        if (tenthanhpho.isEmpty()) {
            DangNhap dangNhap = new DangNhap();
            dangNhap.setVisible(true);
        }
        lb_tentp.setText(tenthanhpho);

        try {
            conn = ConnectToSQL.connectToDatabase(); // Mở kết nối tại đây
            stmt = conn.createStatement();
            String query = "EXEC Login_MATP '" + tenthanhpho + "'";
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                // Lấy ID thành phố từ kết quả truy vấn
                matp = rs.getString("MaTP");
                System.out.println("ID TP : " + matp);
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng đăng nhập lại", "Thông Báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // lấy sever name
    public void ketnoidensever() {
        if (matp.equals("TPDN")) {
            servername = "NGUYENQUOCKHANH\\SEVER1";
        } else if (matp.equals("TPH")) {
            servername = "NGUYENQUOCKHANH\\SEVER2";
        } else if (matp.equals("TPHCM")) {
            servername = "NGUYENQUOCKHANH\\SEVER3";
        } else {
            servername = "NGUYENQUOCKHANH\\SEVER";
        }
    }

    // design table
    public void table() {
        tblaptop.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblaptop.getTableHeader().setOpaque(false);
        tblaptop.getTableHeader().setBackground(new Color(32, 136, 203));
        tblaptop.getTableHeader().setForeground(new Color(255, 255, 255));
        tblaptop.setRowHeight(30);
    }

    public void showLaptop() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("MaLaptop");
        model.addColumn("TenLaptop");
        model.addColumn("HangSX");
        model.addColumn("CauHinh");
        model.addColumn("KichThuocManHinh");
        model.addColumn("TrongLuong");
        model.addColumn("Gia");
        model.addColumn("MaCuaHang");
        model.addColumn("MoTa");

        try {
            String query = "SELECT MaLaptop, TenLaptop, HangSX, CauHinh, KichThuocManHinh, TrongLuong, Gia, MaCuaHang, MoTa FROM Laptop";

            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                float gia = rs.getFloat("Gia");
                int giaInt = (int) gia;

                model.addRow(new Object[]{
                    rs.getString("MaLaptop"),
                    rs.getString("TenLaptop"),
                    rs.getString("HangSX"),
                    rs.getString("CauHinh"),
                    rs.getString("KichThuocManHinh"),
                    rs.getString("TrongLuong"),
                    giaInt,
                    rs.getString("MaCuaHang"),
                    rs.getString("MoTa")
                });
            }

            tblaptop.setModel(model); // Hiển thị dữ liệu lên JTable

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbdonhang = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        class_cuahang = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lb_tentp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblaptop = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/coding.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/checklist.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Sản Phẩm");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/order.png"))); // NOI18N

        lbdonhang.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        lbdonhang.setForeground(new java.awt.Color(255, 255, 255));
        lbdonhang.setText("Đơn Hàng");
        lbdonhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbdonhangMouseClicked(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/earning.png"))); // NOI18N

        class_cuahang.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        class_cuahang.setForeground(new java.awt.Color(255, 255, 255));
        class_cuahang.setText("Cửa Hàng");
        class_cuahang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_cuahangMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Doanh Thu");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/shop.png"))); // NOI18N

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/people.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Khách Hàng");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });

        lb_tentp.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        lb_tentp.setForeground(new java.awt.Color(255, 51, 51));
        lb_tentp.setText("Tru So Chinh");

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logout.png"))); // NOI18N

        logout.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("Đăng Xuất");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lb_tentp)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logout)))))
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbdonhang))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(class_cuahang)))
                .addGap(0, 23, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(lbdonhang))
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(class_cuahang))
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(logout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(lb_tentp)
                .addGap(20, 20, 20))
        );

        jPanel4.setBackground(new java.awt.Color(0, 153, 153));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 3, 28)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Hệ Thống Quản Lý LapTop");

        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/switch.png"))); // NOI18N
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exit)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        tblaptop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblaptop.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblaptop.setFocusable(false);
        tblaptop.setRowHeight(25);
        tblaptop.setSelectionBackground(new java.awt.Color(232, 57, 95));
        tblaptop.setSurrendersFocusOnKeystroke(true);
        tblaptop.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblaptop);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1021, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jLabel23.setBackground(new java.awt.Color(102, 0, 255));
        jLabel23.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 0, 204));
        jLabel23.setText("Danh sách sản phẩm");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        JOptionPane.showMessageDialog(null, "Đăng Xuất Thành Công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        TenThanhPho.setTenthanhpho("");
        DangNhap dangNhap = new DangNhap();
        dangNhap.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutMouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        KhachHangServer khachHang = new KhachHangServer();
        khachHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DoanhThuServer doanhThu = new DoanhThuServer();
        doanhThu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void class_cuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_cuahangMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        CuaHangServer cuaHang = new CuaHangServer();
        cuaHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_class_cuahangMouseClicked

    private void lbdonhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbdonhangMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DonHangServer donHang = new DonHangServer();
        donHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbdonhangMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new GiaoDienQuanLyLapTopServer().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel class_cuahang;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel lbdonhang;
    private javax.swing.JLabel logout;
    private javax.swing.JTable tblaptop;
    // End of variables declaration//GEN-END:variables
}
