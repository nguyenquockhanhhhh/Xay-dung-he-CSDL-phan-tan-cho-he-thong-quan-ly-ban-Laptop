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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class DonHang extends javax.swing.JFrame {

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

    public DonHang() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        get_tentpid();
        ketnoidensever();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showDonhang();
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Lấy socket từ SocketManager
            s = SocketManager.getDonHangSocket();

            // Khởi tạo luồng dữ liệu
            dot = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // xoá nội dung tf sau khi crud
    public void xoa_noidungtf() {
        tf_madonhang.setText("");
        tf_masanpham.setText("");
        tf_makhachhang.setText("");
        tf_mathoigian.setText("");
        tf_soluong.setText("");
        tf_gia.setText("");
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
    public void showDonhang() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("MaDonHang");
        model.addColumn("MaLaptop");
        model.addColumn("MaCuaHang");
        model.addColumn("MaKhachHang");
        model.addColumn("MaThoiGian");
        model.addColumn("SoLuong");
        model.addColumn("TongTien");

        try {
            String query = "SELECT MaDonHang,MaLaptop,MaCuaHang,MaKhachHang,MaThoiGian,SoLuong,TongTien FROM DonHang";

            PreparedStatement pstmt = conn.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();
            // Xóa dữ liệu cũ của ComboBox cb_macuahang trước khi cập nhật
            cb_macuahang.removeAllItems();

            while (rs.next()) {
                float tongiten = rs.getFloat("TongTien");
                int tienInt = (int) tongiten;

                model.addRow(new Object[]{
                    rs.getString("MaDonHang"),
                    rs.getString("MaLaptop"),
                    rs.getString("MaCuaHang"),
                    rs.getString("MaKhachHang"),
                    rs.getString("MaThoiGian"),
                    rs.getString("SoLuong"),
                    tienInt
                });

                // Thêm MaCuaHang vào ComboBox cb_macuahang
                cb_macuahang.addItem(rs.getString("MaCuaHang"));
            }

            tblaptop.setModel(model); // tblaptop là JTable trong giao diện của bạn

            tblaptop.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int selectedRow = tblaptop.getSelectedRow();
                        if (selectedRow != -1) {
                            String madonhang = tblaptop.getValueAt(selectedRow, 0).toString();
                            String malaptop = tblaptop.getValueAt(selectedRow, 1).toString();
                            String makhachhang = tblaptop.getValueAt(selectedRow, 3).toString();
                            String mathoigian = tblaptop.getValueAt(selectedRow, 4).toString();
                            String soluong = tblaptop.getValueAt(selectedRow, 5).toString();
                            String gia = tblaptop.getValueAt(selectedRow, 6).toString();

                            tf_madonhang.setText(madonhang);
                            tf_masanpham.setText(malaptop);
                            tf_makhachhang.setText(makhachhang);
                            tf_mathoigian.setText(mathoigian);
                            tf_soluong.setText(soluong);
                            tf_gia.setText(gia);

                            // Chọn MaCuaHang tương ứng trong ComboBox cb_macuahang
                            String maCuaHang = (String) tblaptop.getValueAt(selectedRow, 2);
                            cb_macuahang.setSelectedItem(maCuaHang);
                        }
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // cập nhật sản phẩm
    public void capNhapdonhang() {
        String madonhang = tf_madonhang.getText().trim();
        String masanpham = tf_masanpham.getText().trim();
        String makhachhang = tf_makhachhang.getText().trim();
        String mathoigian = tf_mathoigian.getText().trim();
        String soluong = tf_soluong.getText().trim();
        String tongtienStr = tf_gia.getText().trim();
        String macuahang = (String) cb_macuahang.getSelectedItem();

        // Kiểm tra các ô dữ liệu không được để trống
        if (madonhang.isEmpty() || masanpham.isEmpty() || makhachhang.isEmpty()
                || mathoigian.isEmpty() || soluong.isEmpty() || tongtienStr.isEmpty() || macuahang == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra dữ liệu cột số lượng
        if (!soluong.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Số lượng phải là một số nguyên dương!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int soLuongInt = Integer.parseInt(soluong);

        // Kiểm tra tổng tiền phải là một số hợp lệ
        float tongtien;
        try {
            tongtien = Float.parseFloat(tongtienStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Tổng tiền phải là một con số hợp lệ!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cập nhật cơ sở dữ liệu tại client
            String updateQuery = "UPDATE DonHang SET "
                    + "MaLaptop = ?, "
                    + "MaCuaHang = ?, "
                    + "MaKhachHang = ?, "
                    + "MaThoiGian = ?, "
                    + "SoLuong = ?, "
                    + "TongTien = ? "
                    + "WHERE MaDonHang = ?";

            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, masanpham);
            pstmt.setString(2, macuahang);
            pstmt.setString(3, makhachhang);
            pstmt.setString(4, mathoigian);
            pstmt.setInt(5, soLuongInt);
            pstmt.setFloat(6, tongtien);
            pstmt.setString(7, madonhang);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showDonhang(); // Hiển thị lại danh sách đơn hàng
                xoa_noidungtf(); // Xóa nội dung các ô nhập
                JOptionPane.showMessageDialog(null, "Cập Nhật Thành Công Đơn Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);

                // Gửi yêu cầu cập nhật lên server
                sendUpdateRequest(madonhang, masanpham, macuahang, makhachhang, mathoigian, String.valueOf(soLuongInt), String.valueOf(tongtien));
            } else {
                JOptionPane.showMessageDialog(null, "Cập Nhật thất bại, vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu cập nhật sản phẩm tới server qua socket
    public void sendUpdateRequest(String madonhang, String masanpham, String macuahang, String makhachhang, String mathoigian, String soluong, String tongtien) {
        try {
            // Gửi yêu cầu cập nhật thông tin đơn hàng tới server
            dot.writeUTF("updateDonHang"); // Thông báo loại yêu cầu
            dot.writeUTF(madonhang);
            dot.writeUTF(masanpham);
            dot.writeUTF(macuahang);
            dot.writeUTF(makhachhang);
            dot.writeUTF(mathoigian);
            dot.writeUTF(soluong);
            dot.writeUTF(tongtien);
            dot.flush();

            // Nhận phản hồi từ server
            String response = dis.readUTF();
            if ("success".equals(response)) {
                System.out.println("Don hang duoc cap nhap thanh cong");
            } else {
                System.out.println("Don hang cap nhap khong thanh cong");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối tới server!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Xóa đơn hàng
    public void xoaSanPham() throws SQLException {
        String madonhang = tf_madonhang.getText().trim();

        // Kiểm tra mã đơn hàng không được để trống
        if (madonhang.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn đơn hàng để xóa!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Gửi yêu cầu xóa đơn hàng tới server
            dot.writeUTF("deleteDonHang");
            dot.writeUTF(madonhang);     // Gửi mã đơn hàng cần xóa
            dot.flush();

            // Lắng nghe phản hồi từ server
            String response = dis.readUTF();
            if (response.equals("success")) {
                // Cập nhật lại JTable client sau khi xóa đơn hàng
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).equals(madonhang)) {
                        model.removeRow(i); // Xóa dòng khỏi JTable
                        break;
                    }
                }
                // Xóa dữ liệu trong cơ sở dữ liệu nếu cần
                String query = "DELETE FROM DonHang WHERE MaDonHang = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, madonhang);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showDonhang();  // Cập nhật lại bảng đơn hàng nếu cần thiết
                    xoa_noidungtf();  // Xóa nội dung các TextField
                    JOptionPane.showMessageDialog(null, "Xoá Thành Công Đơn Hàng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Xoá thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Xoá thất bại. Đơn hàng không tồn tại!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
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
        jPanel3 = new javax.swing.JPanel();
        tf_madonhang = new javax.swing.JTextField();
        tf_masanpham = new javax.swing.JTextField();
        tf_makhachhang = new javax.swing.JTextField();
        tf_mathoigian = new javax.swing.JTextField();
        tf_soluong = new javax.swing.JTextField();
        tf_gia = new javax.swing.JTextField();
        cb_macuahang = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        updatesanpham = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        deletesanpham = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblaptop = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addGap(28, 28, 28))
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

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        tf_madonhang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_madonhang.setText("Mã Đơn Hàng");
        tf_madonhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_madonhangMouseClicked(evt);
            }
        });
        tf_madonhang.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tf_madonhangInputMethodTextChanged(evt);
            }
        });

        tf_masanpham.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_masanpham.setText("Mã Sản Phẩm");
        tf_masanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_masanphamMouseClicked(evt);
            }
        });
        tf_masanpham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_masanphamActionPerformed(evt);
            }
        });

        tf_makhachhang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_makhachhang.setText("Mã Khách Hàng");
        tf_makhachhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_makhachhangMouseClicked(evt);
            }
        });
        tf_makhachhang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_makhachhangActionPerformed(evt);
            }
        });

        tf_mathoigian.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_mathoigian.setText("Mã Thời Gian");
        tf_mathoigian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_mathoigianMouseClicked(evt);
            }
        });
        tf_mathoigian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_mathoigianActionPerformed(evt);
            }
        });

        tf_soluong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_soluong.setText("Số Lượng");
        tf_soluong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_soluongMouseClicked(evt);
            }
        });
        tf_soluong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_soluongActionPerformed(evt);
            }
        });

        tf_gia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_gia.setText("Tổng Tiền");
        tf_gia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_giaMouseClicked(evt);
            }
        });
        tf_gia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_giaActionPerformed(evt);
            }
        });

        cb_macuahang.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cb_macuahang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LW", "AKC", "KAHCM", "HCMC", " " }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tf_madonhang, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_masanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(cb_macuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_gia, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tf_makhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tf_mathoigian, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addComponent(tf_soluong, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cb_macuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tf_madonhang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tf_masanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tf_makhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tf_mathoigian, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tf_soluong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tf_gia, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jLabel15.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 0, 204));
        jLabel15.setText("Chức năng");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        updatesanpham.setBackground(new java.awt.Color(255, 0, 51));
        updatesanpham.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        updatesanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updatesanphamMouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel19.setText("Sửa Đơn Hàng");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/refresh-page-option.png"))); // NOI18N

        javax.swing.GroupLayout updatesanphamLayout = new javax.swing.GroupLayout(updatesanpham);
        updatesanpham.setLayout(updatesanphamLayout);
        updatesanphamLayout.setHorizontalGroup(
            updatesanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, updatesanphamLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addGap(18, 18, 18))
        );
        updatesanphamLayout.setVerticalGroup(
            updatesanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updatesanphamLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(updatesanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        deletesanpham.setBackground(new java.awt.Color(255, 0, 255));
        deletesanpham.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        deletesanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deletesanphamMouseClicked(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel21.setText("Xoá đơn hàng");

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/clear.png"))); // NOI18N

        javax.swing.GroupLayout deletesanphamLayout = new javax.swing.GroupLayout(deletesanpham);
        deletesanpham.setLayout(deletesanphamLayout);
        deletesanphamLayout.setHorizontalGroup(
            deletesanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deletesanphamLayout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addGap(18, 18, 18))
        );
        deletesanphamLayout.setVerticalGroup(
            deletesanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deletesanphamLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deletesanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(deletesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(updatesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updatesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deletesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1010, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jLabel23.setBackground(new java.awt.Color(102, 0, 255));
        jLabel23.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 0, 204));
        jLabel23.setText("Danh sách đơn hàng");

        jLabel16.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 0, 204));
        jLabel16.setText("Thông tin đơn hàng");

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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(39, 39, 39)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(26, 26, 26)))
                .addComponent(jLabel23)
                .addGap(2, 2, 2)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void tf_madonhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_madonhangMouseClicked
        tf_madonhang.setText("");
    }//GEN-LAST:event_tf_madonhangMouseClicked

    private void tf_madonhangInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tf_madonhangInputMethodTextChanged

    }//GEN-LAST:event_tf_madonhangInputMethodTextChanged

    private void tf_masanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_masanphamMouseClicked
        tf_masanpham.setText("");
    }//GEN-LAST:event_tf_masanphamMouseClicked

    private void tf_masanphamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_masanphamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_masanphamActionPerformed

    private void tf_makhachhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_makhachhangMouseClicked
        tf_makhachhang.setText("");
    }//GEN-LAST:event_tf_makhachhangMouseClicked

    private void tf_makhachhangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_makhachhangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_makhachhangActionPerformed

    private void tf_mathoigianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_mathoigianMouseClicked
        tf_mathoigian.setText("");
    }//GEN-LAST:event_tf_mathoigianMouseClicked

    private void tf_mathoigianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_mathoigianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_mathoigianActionPerformed

    private void tf_soluongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_soluongMouseClicked
        tf_soluong.setText("");
    }//GEN-LAST:event_tf_soluongMouseClicked

    private void tf_soluongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_soluongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_soluongActionPerformed

    private void tf_giaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_giaMouseClicked
        tf_gia.setText("");
    }//GEN-LAST:event_tf_giaMouseClicked

    private void tf_giaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_giaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_giaActionPerformed

    private void deletesanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deletesanphamMouseClicked
        try {
            xoaSanPham();
        } catch (SQLException ex) {
            Logger.getLogger(DonHang.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_deletesanphamMouseClicked

    private void updatesanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updatesanphamMouseClicked
        capNhapdonhang();
    }//GEN-LAST:event_updatesanphamMouseClicked

    private void gd_sanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gd_sanphamMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        GiaoDienQuanLyLapTop gdqllt = new GiaoDienQuanLyLapTop();
        gdqllt.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_gd_sanphamMouseClicked

    private void gd_doanhthuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gd_doanhthuMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DoanhThu doanhThu = new DoanhThu();
        doanhThu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_gd_doanhthuMouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        CuaHang cuaHang = new CuaHang();
        cuaHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        KhachHang khachHang = new KhachHang();
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
                new DonHang().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cb_macuahang;
    private javax.swing.JPanel deletesanpham;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel gd_doanhthu;
    private javax.swing.JLabel gd_sanpham;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel lgout;
    private javax.swing.JTable tblaptop;
    private javax.swing.JTextField tf_gia;
    private javax.swing.JTextField tf_madonhang;
    private javax.swing.JTextField tf_makhachhang;
    private javax.swing.JTextField tf_masanpham;
    private javax.swing.JTextField tf_mathoigian;
    private javax.swing.JTextField tf_soluong;
    private javax.swing.JPanel updatesanpham;
    // End of variables declaration//GEN-END:variables
}
