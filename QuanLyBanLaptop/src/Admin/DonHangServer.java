package Admin;

import Application.*;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSQL;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSeverOther;
import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class DonHangServer extends javax.swing.JFrame {

    DefaultTableModel tableModel = new DefaultTableModel();
    Statement stmt;
    ResultSet rs;
    PreparedStatement pst;
    Connection conn = null;
    String matp = null;
    String servername = null;

    static ServerSocket ss;
    static Socket s;
    static DataInputStream dis;
    static DataOutputStream dot;

    public DonHangServer() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        get_tentpid();
        ketnoidensever();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showDonhang();
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ss = new ServerSocket(1203); 

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

    // design table
    public void table() {
        tblaptop.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblaptop.getTableHeader().setOpaque(false);
        tblaptop.getTableHeader().setBackground(new Color(32, 136, 203));
        tblaptop.getTableHeader().setForeground(new Color(255, 255, 255));
        tblaptop.setRowHeight(30);
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

// Hiển thị dữ liệu lên table
    public void showDonhang() {
        // Tạo mô hình dữ liệu cho bảng
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("MaDonHang");
        model.addColumn("MaLaptop");
        model.addColumn("MaCuaHang");
        model.addColumn("MaKhachHang");
        model.addColumn("MaThoiGian");
        model.addColumn("SoLuong");
        model.addColumn("TongTien");

        try {
            // Câu lệnh SQL lấy dữ liệu từ bảng DonHang
            String query = "SELECT MaDonHang, MaLaptop, MaCuaHang, MaKhachHang, MaThoiGian, SoLuong, TongTien FROM DonHang";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Thêm dữ liệu vào mô hình bảng
            while (rs.next()) {
                float tongiten = rs.getFloat("TongTien");
                int tienInt = (int) tongiten; // Chuyển giá trị tiền từ float sang int để hiển thị

                model.addRow(new Object[]{
                    rs.getString("MaDonHang"),
                    rs.getString("MaLaptop"),
                    rs.getString("MaCuaHang"),
                    rs.getString("MaKhachHang"),
                    rs.getString("MaThoiGian"),
                    rs.getString("SoLuong"),
                    tienInt
                });
            }

            // Gắn mô hình dữ liệu vào bảng JTable
            tblaptop.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        gd_sanpham = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        gd_doanhthu = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lb_tentp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lgout = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblaptop = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/coding.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/checklist.png"))); // NOI18N

        gd_sanpham.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        gd_sanpham.setForeground(new java.awt.Color(255, 255, 255));
        gd_sanpham.setText("Sản Phẩm");
        gd_sanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gd_sanphamMouseClicked(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/order.png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Đơn Hàng");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/earning.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Cửa Hàng");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        gd_doanhthu.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        gd_doanhthu.setForeground(new java.awt.Color(255, 255, 255));
        gd_doanhthu.setText("Doanh Thu");
        gd_doanhthu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gd_doanhthuMouseClicked(evt);
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

        lgout.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        lgout.setForeground(new java.awt.Color(255, 255, 255));
        lgout.setText("Đăng Xuất");
        lgout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lgoutMouseClicked(evt);
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
                                .addComponent(lgout)))))
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gd_sanpham))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gd_doanhthu))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)))
                .addGap(0, 27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(gd_sanpham))
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(gd_doanhthu))
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8))
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel13)
                    .addComponent(lgout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lb_tentp)
                .addGap(22, 22, 22))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1042, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel23.setBackground(new java.awt.Color(102, 0, 255));
        jLabel23.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 0, 204));
        jLabel23.setText("Danh sách đơn hàng");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addGap(26, 26, 26)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void gd_sanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gd_sanphamMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        GiaoDienQuanLyLapTopServer gdqllt = new GiaoDienQuanLyLapTopServer();
        gdqllt.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_gd_sanphamMouseClicked

    private void gd_doanhthuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gd_doanhthuMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DoanhThuServer doanhThu = new DoanhThuServer();
        doanhThu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_gd_doanhthuMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        CuaHangServer cuaHang = new CuaHangServer();
        cuaHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        KhachHangServer khachHang = new KhachHangServer();
        khachHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel12MouseClicked

    private void lgoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lgoutMouseClicked
        JOptionPane.showMessageDialog(null, "Đăng Xuất Thành Công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        TenThanhPho.setTenthanhpho("");
        DangNhap dangNhap = new DangNhap();
        dangNhap.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lgoutMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DonHangServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel exit;
    private javax.swing.JLabel gd_doanhthu;
    private javax.swing.JLabel gd_sanpham;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel lgout;
    private javax.swing.JTable tblaptop;
    // End of variables declaration//GEN-END:variables
}
