package Application;

import Admin.SocketManager;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSQL;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSeverOther;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CuaHang extends javax.swing.JFrame {

    DefaultTableModel tableModel = new DefaultTableModel();
    Statement stmt;
    ResultSet rs;
    PreparedStatement pst;
    Connection conn = null;
    String matp = null;
    String servername = null;

    static Socket s;
    static DataInputStream dis;
    static DataOutputStream dot;

    public CuaHang() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        get_tentpid();
        ketnoidensever();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showCuaHang();
        connectToServer();
    }

    private void connectToServer() {
         try {
            // Lấy socket từ SocketManager
            s = SocketManager.getCuaHangSocket();

            // Khởi tạo luồng dữ liệu
            dot = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void refreshCuaHangData() {
        // Your code to fetch and update the JTable goes here
        try {
            String query = "SELECT * FROM CuaHang";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();
            model.setRowCount(0); // Clear the table

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaCuaHang"),
                    rs.getString("TenCuaHang"),
                    rs.getString("DiaChi"),
                    rs.getString("SoDienThoai")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // xoá nội dung tf sau khi crud
    public void xoa_noidungtf() {
        tf_macuahang.setText("");
        tf_tencuahang.setText("");
        ta_diachi.setText("");
        tf_sodienthoai.setText("");
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

    // hiển thị dữ liệu lên table
    public void showCuaHang() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("MaCuaHang");
        model.addColumn("TenCuaHang");
        model.addColumn("DiaChi");
        model.addColumn("SoDienThoai");

        try {
            String query = "SELECT MaCuaHang, TenCuaHang, DiaChi, SoDienThoai FROM CuaHang";

            PreparedStatement pstmt = conn.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();

            // Xóa dữ liệu cũ
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaCuaHang"),
                    rs.getString("TenCuaHang"),
                    rs.getString("DiaChi"),
                    rs.getString("SoDienThoai")
                });

            }

            tblaptop.setModel(model);

            tblaptop.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int selectedRow = tblaptop.getSelectedRow();
                        if (selectedRow != -1) {
                            String macuahang = tblaptop.getValueAt(selectedRow, 0).toString();
                            String tencuahang = tblaptop.getValueAt(selectedRow, 1).toString();
                            String diachi = tblaptop.getValueAt(selectedRow, 2).toString();
                            String sodienthoai = tblaptop.getValueAt(selectedRow, 3).toString();

                            tf_macuahang.setText(macuahang);
                            tf_tencuahang.setText(tencuahang);
                            ta_diachi.setText(diachi);
                            tf_sodienthoai.setText(sodienthoai);
                        }
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // thêm cửa hàng
    public void themCuaHang() {
        String macuahang = tf_macuahang.getText().trim();
        String tencuahang = tf_tencuahang.getText().trim();
        String diachi = ta_diachi.getText().trim();
        String sdt = tf_sodienthoai.getText().trim();

        // Kiểm tra các ô dữ liệu không được để trống
        if (macuahang.isEmpty() || tencuahang.isEmpty() || diachi.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra định dạng số điện thoại (chỉ cho phép chữ số và từ 8 đến 11 số)
        if (!sdt.matches("\\d{8,11}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải là chữ số và có độ dài từ 8 đến 11 số!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Kiểm tra MaCuaHang đã tồn tại chưa
            String checkQuery = "SELECT COUNT(*) FROM CuaHang WHERE MaCuaHang = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, macuahang);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Mã Cửa Hàng đã tồn tại. Vui lòng nhập mã khác!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Thêm cửa hàng mới nếu mã không tồn tại
            String query = "INSERT INTO CuaHang (MaCuaHang, TenCuaHang, DiaChi, SoDienThoai, MaTP) VALUES (?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, macuahang);
            pstmt.setString(2, tencuahang);
            pstmt.setString(3, diachi);
            pstmt.setString(4, sdt);
            pstmt.setString(5, matp);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Lấy model của JTable
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();

                // Chèn cửa hàng mới vào đầu bảng (hàng số 0)
                model.insertRow(0, new Object[]{macuahang, tencuahang, diachi, sdt});
                xoa_noidungtf();

                JOptionPane.showMessageDialog(null, "Thêm Thành Công Cửa Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                sendInsertRequest(macuahang, tencuahang, diachi, sdt);
            } else {
                JOptionPane.showMessageDialog(null, "Thêm thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Gửi yêu cầu thêm sản phẩm tới server qua socket
    public void sendInsertRequest(String macuahang, String tencuahang, String diachi, String sdt) {
        try {
            dot.writeUTF("insertCuaHang");
            dot.writeUTF(macuahang);
            dot.writeUTF(tencuahang);
            dot.writeUTF(diachi);
            dot.writeUTF(sdt);
            dot.flush();

            // Nhận phản hồi từ server
            if (dis != null) {
                String response = dis.readUTF();
                if ("success".equals(response)) {
                    System.out.println("Cua hang duoc them thanh cong vao server.");
                }
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // cập nhập cửa hàng
    public void capNhapCuaHang() {
        String macuahang = tf_macuahang.getText();
        String tencuahang = tf_tencuahang.getText();
        String diachi = ta_diachi.getText();
        String sdt = tf_sodienthoai.getText();

        // Kiểm tra các ô dữ liệu không được để trống
        if (macuahang.isEmpty() || tencuahang.isEmpty() || diachi.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE CuaHang SET TenCuaHang = ?, DiaChi = ?, SoDienThoai = ?, MaTP = ? WHERE MaCuaHang = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tencuahang);
            pstmt.setString(2, diachi);
            pstmt.setString(3, sdt);
            pstmt.setString(4, matp);
            pstmt.setString(5, macuahang);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showCuaHang();
                xoa_noidungtf();
                JOptionPane.showMessageDialog(null, "Cập Nhập Thành Công Cửa Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                sendUpdateRequest(macuahang, tencuahang, diachi, sdt);
            } else {
                JOptionPane.showMessageDialog(null, "Cập Nhập thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu cập nhật sản phẩm tới server qua socket
    public void sendUpdateRequest(String macuahang, String tencuahang, String diachi, String sdt) {
        try {
            dot.writeUTF("updateCuaHang");
            dot.writeUTF(macuahang);
            dot.writeUTF(tencuahang);
            dot.writeUTF(diachi);
            dot.writeUTF(sdt);
            dot.flush();

            // Nhận phản hồi từ server
            if (dis != null) {
                String response = dis.readUTF();
                if ("success".equals(response)) {
                    System.out.println("Cua hang duoc cap nhap thanh cong");
                } else {
                    System.out.println("Cap nhap that bai cua hang");
                }
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Xóa cửa hàng trên client
    public void xoaCuaHang() throws SQLException {
        String macuahang = tf_macuahang.getText().trim();

        // Kiểm tra mã cửa hàng không được để trống
        if (macuahang.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn cửa hàng để xóa!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Gửi yêu cầu xóa cửa hàng tới server
            dot.writeUTF("deleteCuaHang"); // Gửi action "deleteStore" để server biết
            dot.writeUTF(macuahang);     // Gửi mã cửa hàng cần xóa
            dot.flush();

            // Lắng nghe phản hồi từ server
            String response = dis.readUTF();
            if (response.equals("success")) {
                // Cập nhật lại JTable client sau khi xóa cửa hàng
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).equals(macuahang)) {
                        model.removeRow(i); // Xóa dòng khỏi JTable
                        break;
                    }
                }
                // Xóa dữ liệu trong cơ sở dữ liệu nếu cần
                String query = "DELETE FROM CuaHang WHERE MaCuaHang = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, macuahang);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showCuaHang();  // Cập nhật lại bảng cửa hàng nếu cần thiết
                    xoa_noidungtf();  // Xóa nội dung các TextField
                    JOptionPane.showMessageDialog(null, "Xoá Thành Công Cửa Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Xoá thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Xoá thất bại. Cửa hàng không tồn tại!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối tới server!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        class_giaodiensanphaam = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        class_donhang = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        class_doanhthu = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lb_tentp = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        tf_macuahang = new javax.swing.JTextField();
        tf_tencuahang = new javax.swing.JTextField();
        tf_sodienthoai = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_diachi = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        themcuahang = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        suacuahang = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        xoacuahang = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblaptop = new javax.swing.JTable();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel5.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/coding.png"))); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/checklist.png"))); // NOI18N

        class_giaodiensanphaam.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        class_giaodiensanphaam.setForeground(new java.awt.Color(255, 255, 255));
        class_giaodiensanphaam.setText("Sản Phẩm");
        class_giaodiensanphaam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_giaodiensanphaamMouseClicked(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/order.png"))); // NOI18N

        class_donhang.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        class_donhang.setForeground(new java.awt.Color(255, 255, 255));
        class_donhang.setText("Đơn Hàng");
        class_donhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_donhangMouseClicked(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/earning.png"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Cửa Hàng");

        class_doanhthu.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        class_doanhthu.setForeground(new java.awt.Color(255, 255, 255));
        class_doanhthu.setText("Doanh Thu");
        class_doanhthu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_doanhthuMouseClicked(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/shop.png"))); // NOI18N

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/people.png"))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Khách Hàng");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        lb_tentp.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        lb_tentp.setForeground(new java.awt.Color(255, 51, 51));
        lb_tentp.setText("Tru So Chinh");

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/logout.png"))); // NOI18N

        logout.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        logout.setForeground(new java.awt.Color(255, 255, 255));
        logout.setText("Đăng Xuất");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel1))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lb_tentp)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logout)))))
                .addContainerGap(16, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(class_giaodiensanphaam))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(class_donhang))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(class_doanhthu))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addGap(0, 23, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(class_giaodiensanphaam))
                .addGap(39, 39, 39)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(class_donhang))
                .addGap(41, 41, 41)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(class_doanhthu))
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9))
                .addGap(44, 44, 44)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(42, 42, 42)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14)
                    .addComponent(logout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lb_tentp)
                .addGap(21, 21, 21))
        );

        jPanel6.setBackground(new java.awt.Color(0, 153, 153));

        jLabel40.setFont(new java.awt.Font("Times New Roman", 3, 28)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Hệ Thống Quản Lý LapTop");

        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/switch.png"))); // NOI18N
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 562, Short.MAX_VALUE)
                .addComponent(exit)
                .addGap(0, 0, 0))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exit)
                    .addComponent(jLabel40))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        tf_macuahang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_macuahang.setText("Mã cửa hàng");
        tf_macuahang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_macuahangMouseClicked(evt);
            }
        });
        tf_macuahang.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tf_macuahangInputMethodTextChanged(evt);
            }
        });

        tf_tencuahang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_tencuahang.setText("Tên cửa hàng");
        tf_tencuahang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_tencuahangMouseClicked(evt);
            }
        });
        tf_tencuahang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_tencuahangActionPerformed(evt);
            }
        });

        tf_sodienthoai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_sodienthoai.setText("Số điện thoại");
        tf_sodienthoai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_sodienthoaiMouseClicked(evt);
            }
        });
        tf_sodienthoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_sodienthoaiActionPerformed(evt);
            }
        });

        ta_diachi.setColumns(20);
        ta_diachi.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ta_diachi.setRows(5);
        ta_diachi.setText("Địa chỉ cửa hàng");
        ta_diachi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ta_diachiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ta_diachi);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_macuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_tencuahang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_sodienthoai, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_macuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_tencuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_sodienthoai, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        themcuahang.setBackground(new java.awt.Color(255, 204, 0));
        themcuahang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        themcuahang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                themcuahangMouseClicked(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel41.setText("Thêm cửa hàng");

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/add.png"))); // NOI18N

        javax.swing.GroupLayout themcuahangLayout = new javax.swing.GroupLayout(themcuahang);
        themcuahang.setLayout(themcuahangLayout);
        themcuahangLayout.setHorizontalGroup(
            themcuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, themcuahangLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41)
                .addGap(18, 18, 18))
        );
        themcuahangLayout.setVerticalGroup(
            themcuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(themcuahangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(themcuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(jLabel41))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        suacuahang.setBackground(new java.awt.Color(255, 0, 51));
        suacuahang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        suacuahang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                suacuahangMouseClicked(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel43.setText("Sửa cửa hàng");

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/refresh-page-option.png"))); // NOI18N

        javax.swing.GroupLayout suacuahangLayout = new javax.swing.GroupLayout(suacuahang);
        suacuahang.setLayout(suacuahangLayout);
        suacuahangLayout.setHorizontalGroup(
            suacuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, suacuahangLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel43)
                .addGap(18, 18, 18))
        );
        suacuahangLayout.setVerticalGroup(
            suacuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(suacuahangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(suacuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(jLabel43))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        xoacuahang.setBackground(new java.awt.Color(255, 0, 255));
        xoacuahang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        xoacuahang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                xoacuahangMouseClicked(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel45.setText("Xoá cửa hàng");

        jLabel46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/clear.png"))); // NOI18N

        javax.swing.GroupLayout xoacuahangLayout = new javax.swing.GroupLayout(xoacuahang);
        xoacuahang.setLayout(xoacuahangLayout);
        xoacuahangLayout.setHorizontalGroup(
            xoacuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, xoacuahangLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel45)
                .addGap(18, 18, 18))
        );
        xoacuahangLayout.setVerticalGroup(
            xoacuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xoacuahangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xoacuahangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel46)
                    .addComponent(jLabel45))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(themcuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(suacuahang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(xoacuahang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(119, 119, 119))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(themcuahang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(suacuahang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(xoacuahang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

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

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 942, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel47.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(51, 0, 204));
        jLabel47.setText("Chức năng");

        jLabel48.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(51, 0, 204));
        jLabel48.setText("Danh sách cửa hàng");

        jLabel49.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(51, 0, 204));
        jLabel49.setText("Thông tin cửa hàng");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void class_donhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_donhangMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DonHang donHang = new DonHang();
        donHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_class_donhangMouseClicked

    private void class_doanhthuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_doanhthuMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DoanhThu doanhThu = new DoanhThu();
        doanhThu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_class_doanhthuMouseClicked

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void tf_macuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_macuahangMouseClicked
        tf_macuahang.setText("");
    }//GEN-LAST:event_tf_macuahangMouseClicked

    private void tf_macuahangInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tf_macuahangInputMethodTextChanged

    }//GEN-LAST:event_tf_macuahangInputMethodTextChanged

    private void tf_tencuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_tencuahangMouseClicked
        tf_tencuahang.setText("");
    }//GEN-LAST:event_tf_tencuahangMouseClicked

    private void tf_tencuahangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_tencuahangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_tencuahangActionPerformed

    private void tf_sodienthoaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_sodienthoaiMouseClicked
        tf_sodienthoai.setText("");
    }//GEN-LAST:event_tf_sodienthoaiMouseClicked

    private void tf_sodienthoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_sodienthoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_sodienthoaiActionPerformed

    private void ta_diachiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ta_diachiMouseClicked
        ta_diachi.setText("");
    }//GEN-LAST:event_ta_diachiMouseClicked

    private void themcuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_themcuahangMouseClicked
        themCuaHang();
    }//GEN-LAST:event_themcuahangMouseClicked

    private void suacuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_suacuahangMouseClicked
        capNhapCuaHang();
    }//GEN-LAST:event_suacuahangMouseClicked

    private void xoacuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xoacuahangMouseClicked
        try {
            xoaCuaHang();
        } catch (SQLException ex) {
            Logger.getLogger(CuaHang.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_xoacuahangMouseClicked

    private void class_giaodiensanphaamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_giaodiensanphaamMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        GiaoDienQuanLyLapTop gd = new GiaoDienQuanLyLapTop();
        gd.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_class_giaodiensanphaamMouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        KhachHang khachHang = new KhachHang();
        khachHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel13MouseClicked

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        JOptionPane.showMessageDialog(null, "Đăng Xuất Thành Công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        TenThanhPho.setTenthanhpho("");
        DangNhap dangNhap = new DangNhap();
        dangNhap.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CuaHang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel class_doanhthu;
    private javax.swing.JLabel class_donhang;
    private javax.swing.JLabel class_giaodiensanphaam;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel logout;
    private javax.swing.JPanel suacuahang;
    private javax.swing.JTextArea ta_diachi;
    private javax.swing.JTable tblaptop;
    private javax.swing.JTextField tf_macuahang;
    private javax.swing.JTextField tf_sodienthoai;
    private javax.swing.JTextField tf_tencuahang;
    private javax.swing.JPanel themcuahang;
    private javax.swing.JPanel xoacuahang;
    // End of variables declaration//GEN-END:variables
}
