package Application;

import Admin.SocketManager;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSQL;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSeverOther;
import java.awt.Color;
import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

public class KhachHang extends javax.swing.JFrame {

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

    public KhachHang() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        get_tentpid();
        ketnoidensever();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showKhachHang();
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Lấy socket từ SocketManager
            s = SocketManager.getKhachHangSocket();// Khởi tạo luồng dữ liệu
            dot = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // xoá nội dung tf sau khi crud
    public void xoa_noidungtf() {
        tf_makhachhang.setText("");
        tf_tenkhachhang.setText("");
        tf_email.setText("");
        tf_sdt.setText("");
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
    public void showKhachHang() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("MaKhachHang");
        model.addColumn("TenKhachHang");
        model.addColumn("Email");
        model.addColumn("SoDienThoai");

        try {
            String query = "SELECT MaKhachHang, TenKhachHang, Email, SoDienThoai FROM KhachHang";

            PreparedStatement pstmt = conn.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaKhachHang"),
                    rs.getString("TenKhachHang"),
                    rs.getString("Email"),
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
                            String makh = tblaptop.getValueAt(selectedRow, 0).toString();
                            String tenkh = tblaptop.getValueAt(selectedRow, 1).toString();
                            String email = tblaptop.getValueAt(selectedRow, 2).toString();
                            String sodienthoai = tblaptop.getValueAt(selectedRow, 3).toString();

                            tf_makhachhang.setText(makh);
                            tf_tenkhachhang.setText(tenkh);
                            tf_email.setText(email);
                            tf_sdt.setText(sodienthoai);
                        }
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // thêm khách hàng
    public void themkh() {
        String makh = tf_makhachhang.getText().trim();
        String tenkh = tf_tenkhachhang.getText().trim();
        String email = tf_email.getText().trim();
        String sdt = tf_sdt.getText().trim();

        // Kiểm tra các ô không được để trống
        if (makh.isEmpty() || tenkh.isEmpty() || email.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,}$")) {
            JOptionPane.showMessageDialog(null, "Email không đúng định dạng!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra số điện thoại là số từ 8 đến 11 chữ số
        if (!sdt.matches("\\d{8,11}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải là số và có từ 8 đến 11 chữ số!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Kiểm tra mã khách hàng đã tồn tại
            String checkQuery = "SELECT COUNT(*) FROM KhachHang WHERE MaKhachHang = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, makh);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Mã khách hàng đã tồn tại. Vui lòng nhập mã khác!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Thêm khách hàng nếu mã khách hàng chưa tồn tại
            String query = "INSERT INTO KhachHang (MaKhachHang, TenKhachHang, Email, SoDienThoai, MaTP) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, makh);
            pstmt.setString(2, tenkh);
            pstmt.setString(3, email);
            pstmt.setString(4, sdt);
            pstmt.setString(5, matp);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Lấy model của JTable
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();

                // Chèn khách hàng mới vào đầu bảng (hàng số 0)
                model.insertRow(0, new Object[]{makh, tenkh, email, sdt});
                xoa_noidungtf();
                JOptionPane.showMessageDialog(null, "Thêm Thành Công Khách Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                sendInsertRequest(makh, tenkh, email, sdt);
            } else {
                JOptionPane.showMessageDialog(null, "Thêm thất bại, vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu thêm sản phẩm tới server qua socket
    public void sendInsertRequest(String makhachhang, String tenkhachhang, String email, String sdt) {
        try {
            dot.writeUTF("insertKhachHang");
            dot.writeUTF(makhachhang);
            dot.writeUTF(tenkhachhang);
            dot.writeUTF(email);
            dot.writeUTF(sdt);
            dot.flush();

            // Nhận phản hồi từ server
            if (dis != null) {
                String response = dis.readUTF();
                if ("success".equals(response)) {
                    System.out.println("Khach hang duoc them thanh cong vao server.");
                }
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // cập nhập khách hàng
    public void capNhapKhachHang() {
        String makh = tf_makhachhang.getText().trim();
        String tenkh = tf_tenkhachhang.getText().trim();
        String email = tf_email.getText().trim();
        String sdt = tf_sdt.getText().trim();

        // Kiểm tra các ô không được để trống
        if (makh.isEmpty() || tenkh.isEmpty() || email.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra định dạng email
        if (!email.matches("^[\\w-\\.]+@[\\w-\\.]+\\.[a-z]{2,}$")) {
            JOptionPane.showMessageDialog(null, "Email không đúng định dạng!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra số điện thoại là số từ 8 đến 11 chữ số
        if (!sdt.matches("\\d{8,11}")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại phải là số và có từ 8 đến 11 chữ số!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String query = "UPDATE KhachHang SET TenKhachHang = ?, Email = ?, SoDienThoai = ?, MaTP = ? WHERE MaKhachHang = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tenkh);
            pstmt.setString(2, email);
            pstmt.setString(3, sdt);
            pstmt.setString(4, matp);
            pstmt.setString(5, makh);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showKhachHang();
                xoa_noidungtf();
                JOptionPane.showMessageDialog(null, "Cập Nhập Thành Công Khách Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                sendUpdateRequest(makh, tenkh, email, sdt);
            } else {
                JOptionPane.showMessageDialog(null, "Cập Nhập thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu cập nhật sản phẩm tới server qua socket
    public void sendUpdateRequest(String makhachhang, String tenkhachhang, String email, String sdt) {
        try {
            // Gửi yêu cầu cập nhật thông tin đơn hàng tới server
            dot.writeUTF("updateKhachHang"); // Thông báo loại yêu cầu
            dot.writeUTF(makhachhang);
            dot.writeUTF(tenkhachhang);
            dot.writeUTF(email);
            dot.writeUTF(sdt);
            dot.flush();

            // Nhận phản hồi từ server
            String response = dis.readUTF();
            if ("success".equals(response)) {
                System.out.println("Khach hang duoc cap nhap thanh cong");
            } else {
                System.out.println("Khach hang cap nhap khong thanh cong");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối tới server!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xoá khách hàng
    public void xoaKH() {
        String makhachhang = tf_makhachhang.getText().trim();

        // Kiểm tra mã khách hàng không được để trống
        if (makhachhang.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn khách hàng để xóa!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Gửi yêu cầu xóa khách hàng tới server
            dot.writeUTF("deleteKhachHang"); // Gửi action "deleteKhachHang" để server biết
            dot.writeUTF(makhachhang); // Gửi mã khách hàng cần xóa
            dot.flush();

            // Lắng nghe phản hồi từ server
            String response = dis.readUTF();
            if (response.equals("success")) {
                // Cập nhật lại JTable client sau khi xóa khách hàng
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).equals(makhachhang)) {
                        model.removeRow(i); // Xóa dòng khỏi JTable
                        break;
                    }
                }
                xoa_noidungtf(); // Xóa nội dung các TextField
                JOptionPane.showMessageDialog(null, "Xoá Thành Công Khách Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Xoá thất bại. Khách hàng không tồn tại hoặc lỗi khác!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối tới server!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        giaodiensanpham = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        class_cuahang = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lb_tentp = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        logout = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        exit1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tf_makhachhang = new javax.swing.JTextField();
        tf_tenkhachhang = new javax.swing.JTextField();
        tf_email = new javax.swing.JTextField();
        tf_sdt = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        themkhachhang = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        updatekhachhang = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        deletekhachhang = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblaptop = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/coding.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/checklist.png"))); // NOI18N

        giaodiensanpham.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        giaodiensanpham.setForeground(new java.awt.Color(255, 255, 255));
        giaodiensanpham.setText("Sản Phẩm");
        giaodiensanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                giaodiensanphamMouseClicked(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/order.png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Đơn Hàng");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
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
                        .addComponent(giaodiensanpham))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
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
                    .addComponent(giaodiensanpham))
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(lb_tentp)
                .addGap(20, 20, 20))
        );

        jPanel5.setBackground(new java.awt.Color(0, 153, 153));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 3, 28)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Hệ Thống Quản Lý LapTop");

        exit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/switch.png"))); // NOI18N
        exit1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exit1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exit1)
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exit1)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        tf_makhachhang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_makhachhang.setText("Mã khách hàng");
        tf_makhachhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_makhachhangMouseClicked(evt);
            }
        });
        tf_makhachhang.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tf_makhachhangInputMethodTextChanged(evt);
            }
        });

        tf_tenkhachhang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_tenkhachhang.setText("Tên khách hàng");
        tf_tenkhachhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_tenkhachhangMouseClicked(evt);
            }
        });
        tf_tenkhachhang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_tenkhachhangActionPerformed(evt);
            }
        });

        tf_email.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_email.setText("Email");
        tf_email.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_emailMouseClicked(evt);
            }
        });
        tf_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_emailActionPerformed(evt);
            }
        });

        tf_sdt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_sdt.setText("Số điện thoại");
        tf_sdt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_sdtMouseClicked(evt);
            }
        });
        tf_sdt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_sdtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tf_makhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_email, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tf_sdt)
                    .addComponent(tf_tenkhachhang, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_tenkhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_makhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_email, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_sdt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        themkhachhang.setBackground(new java.awt.Color(255, 204, 0));
        themkhachhang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        themkhachhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                themkhachhangMouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel17.setText("Thêm khách hàng");

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/add.png"))); // NOI18N

        javax.swing.GroupLayout themkhachhangLayout = new javax.swing.GroupLayout(themkhachhang);
        themkhachhang.setLayout(themkhachhangLayout);
        themkhachhangLayout.setHorizontalGroup(
            themkhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, themkhachhangLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addGap(18, 18, 18))
        );
        themkhachhangLayout.setVerticalGroup(
            themkhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(themkhachhangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(themkhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updatekhachhang.setBackground(new java.awt.Color(255, 0, 51));
        updatekhachhang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        updatekhachhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updatekhachhangMouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel19.setText("Sửa khách hàng");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/refresh-page-option.png"))); // NOI18N

        javax.swing.GroupLayout updatekhachhangLayout = new javax.swing.GroupLayout(updatekhachhang);
        updatekhachhang.setLayout(updatekhachhangLayout);
        updatekhachhangLayout.setHorizontalGroup(
            updatekhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, updatekhachhangLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addGap(18, 18, 18))
        );
        updatekhachhangLayout.setVerticalGroup(
            updatekhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updatekhachhangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(updatekhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        deletekhachhang.setBackground(new java.awt.Color(255, 0, 255));
        deletekhachhang.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        deletekhachhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deletekhachhangMouseClicked(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel21.setText("Xoá khách hàng");

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/clear.png"))); // NOI18N

        javax.swing.GroupLayout deletekhachhangLayout = new javax.swing.GroupLayout(deletekhachhang);
        deletekhachhang.setLayout(deletekhachhangLayout);
        deletekhachhangLayout.setHorizontalGroup(
            deletekhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deletekhachhangLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addGap(18, 18, 18))
        );
        deletekhachhangLayout.setVerticalGroup(
            deletekhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deletekhachhangLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deletekhachhangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(themkhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(updatekhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deletekhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(119, 119, 119))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(themkhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatekhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(deletekhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

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

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 966, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 0, 204));
        jLabel16.setText("Chức năng");

        jLabel23.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 0, 204));
        jLabel23.setText("Danh sách khách hàng");

        jLabel24.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(51, 0, 204));
        jLabel24.setText("Thông tin khách hàng");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(32, 32, 32)))))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DonHang donHang = new DonHang();
        donHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel6MouseClicked

    private void class_cuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_cuahangMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        CuaHang cuaHang = new CuaHang();
        cuaHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_class_cuahangMouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DoanhThu doanhThu = new DoanhThu();
        doanhThu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void exit1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exit1MouseClicked
        System.exit(0);
    }//GEN-LAST:event_exit1MouseClicked

    private void themkhachhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_themkhachhangMouseClicked
        themkh();
    }//GEN-LAST:event_themkhachhangMouseClicked

    private void updatekhachhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updatekhachhangMouseClicked
        capNhapKhachHang();
    }//GEN-LAST:event_updatekhachhangMouseClicked

    private void deletekhachhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deletekhachhangMouseClicked
        xoaKH();
    }//GEN-LAST:event_deletekhachhangMouseClicked

    private void tf_sdtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_sdtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_sdtActionPerformed

    private void tf_sdtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_sdtMouseClicked
        tf_sdt.setText("");
    }//GEN-LAST:event_tf_sdtMouseClicked

    private void tf_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_emailActionPerformed

    private void tf_emailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_emailMouseClicked
        tf_email.setText("");
    }//GEN-LAST:event_tf_emailMouseClicked

    private void tf_tenkhachhangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_tenkhachhangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_tenkhachhangActionPerformed

    private void tf_tenkhachhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_tenkhachhangMouseClicked
        tf_tenkhachhang.setText("");
    }//GEN-LAST:event_tf_tenkhachhangMouseClicked

    private void tf_makhachhangInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tf_makhachhangInputMethodTextChanged

    }//GEN-LAST:event_tf_makhachhangInputMethodTextChanged

    private void tf_makhachhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_makhachhangMouseClicked
        tf_makhachhang.setText("");
    }//GEN-LAST:event_tf_makhachhangMouseClicked

    private void giaodiensanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_giaodiensanphamMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        GiaoDienQuanLyLapTop gdqllt = new GiaoDienQuanLyLapTop();
        gdqllt.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_giaodiensanphamMouseClicked

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
                new KhachHang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel class_cuahang;
    private javax.swing.JPanel deletekhachhang;
    private javax.swing.JLabel exit1;
    private javax.swing.JLabel giaodiensanpham;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel logout;
    private javax.swing.JTable tblaptop;
    private javax.swing.JTextField tf_email;
    private javax.swing.JTextField tf_makhachhang;
    private javax.swing.JTextField tf_sdt;
    private javax.swing.JTextField tf_tenkhachhang;
    private javax.swing.JPanel themkhachhang;
    private javax.swing.JPanel updatekhachhang;
    // End of variables declaration//GEN-END:variables
}
