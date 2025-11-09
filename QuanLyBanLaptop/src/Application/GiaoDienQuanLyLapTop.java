package Application;

import Admin.SocketManager;
import Application.DangNhap;
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
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GiaoDienQuanLyLapTop extends javax.swing.JFrame {

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

    public GiaoDienQuanLyLapTop() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        get_tentpid();
        ketnoidensever();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showLaptop();
        connectToServer();
    }

    private void connectToServer() {
         try {
            // Lấy socket từ SocketManager
            s = SocketManager.getLaptopSocket();

            // Khởi tạo luồng dữ liệu
            dot = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // xoá nội dung tf sau khi crud
    public void xoa_noidungtf() {
        tf_malaptop.setText("");
        tf_tenlaptop.setText("");
        tf_hangsx.setText("");
        tf_cauhinh.setText("");
        tf_kichthuoc.setText("");
        tf_trongluong.setText("");
        tf_gia.setText("");
        ta_mota.setText("");
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
            String query = "SELECT MaLaptop, TenLaptop, HangSX, CauHinh, KichThuocManHinh, TrongLuong, Gia, MoTa, MaCuaHang FROM Laptop";

            PreparedStatement pstmt = conn.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();

            // Xóa dữ liệu cũ của ComboBox cb_macuahang trước khi cập nhật
            cb_macuahang.removeAllItems();

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
                            String maLaptop = tblaptop.getValueAt(selectedRow, 0).toString();
                            String tenLaptop = tblaptop.getValueAt(selectedRow, 1).toString();
                            String hangSX = tblaptop.getValueAt(selectedRow, 2).toString();
                            String cauHinh = tblaptop.getValueAt(selectedRow, 3).toString();
                            String kichThuoc = tblaptop.getValueAt(selectedRow, 4).toString();
                            String trongLuong = tblaptop.getValueAt(selectedRow, 5).toString();
                            String gia = tblaptop.getValueAt(selectedRow, 6).toString();
                            String moTa = tblaptop.getValueAt(selectedRow, 8).toString();

                            tf_malaptop.setText(maLaptop);
                            tf_tenlaptop.setText(tenLaptop);
                            tf_hangsx.setText(hangSX);
                            tf_cauhinh.setText(cauHinh);
                            tf_kichthuoc.setText(kichThuoc);
                            tf_trongluong.setText(trongLuong);
                            tf_gia.setText(gia);
                            ta_mota.setText(moTa);

                            // Chọn MaCuaHang tương ứng trong ComboBox cb_macuahang
                            String maCuaHang = (String) tblaptop.getValueAt(selectedRow, 7);
                            cb_macuahang.setSelectedItem(maCuaHang);
                        }
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void themSanPham() {
        String maLaptop = tf_malaptop.getText().trim();
        String tenLaptop = tf_tenlaptop.getText().trim();
        String hangSX = tf_hangsx.getText().trim();
        String cauhinh = tf_cauhinh.getText().trim();
        String kichthuoc = tf_kichthuoc.getText().trim();
        String trongluong = tf_trongluong.getText().trim();
        String giaStr = tf_gia.getText().trim();
        String mota = ta_mota.getText().trim();
        String macuahang = (String) cb_macuahang.getSelectedItem();

        // Kiểm tra các ô không được để trống
        if (maLaptop.isEmpty() || tenLaptop.isEmpty() || hangSX.isEmpty() || cauhinh.isEmpty()
                || kichthuoc.isEmpty() || trongluong.isEmpty() || giaStr.isEmpty() || macuahang == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra giá trị "gia" phải là một con số hợp lệ
        float gia;
        try {
            gia = Float.parseFloat(giaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Giá phải là một con số hợp lệ!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Kiểm tra mã laptop đã tồn tại
            String checkQuery = "SELECT COUNT(*) FROM Laptop WHERE MaLaptop = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, maLaptop);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Mã Laptop đã tồn tại. Vui lòng nhập mã khác!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Chèn sản phẩm mới nếu mã laptop không tồn tại
            String query = "INSERT INTO Laptop (MaLaptop, TenLaptop, HangSX, CauHinh, KichThuocManHinh, TrongLuong, Gia, MaCuaHang, MoTa) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, maLaptop);
            pstmt.setString(2, tenLaptop);
            pstmt.setString(3, hangSX);
            pstmt.setString(4, cauhinh);
            pstmt.setString(5, kichthuoc);
            pstmt.setString(6, trongluong);
            pstmt.setFloat(7, gia);
            pstmt.setString(8, macuahang);
            pstmt.setString(9, mota);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Lấy model của JTable
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();

                // Định dạng giá trị 'gia' để hiển thị đúng định dạng
                DecimalFormat df = new DecimalFormat("#,###"); // định dạng số có dấu phẩy
                String giaFormatted = df.format(gia);

                // Chèn sản phẩm mới vào đầu bảng (hàng số 0)
                model.insertRow(0, new Object[]{
                    maLaptop, tenLaptop, hangSX, cauhinh, kichthuoc, trongluong, giaFormatted, macuahang, mota
                });

                // Cập nhật dữ liệu vào các ô textfield
                tf_malaptop.setText(maLaptop);
                tf_tenlaptop.setText(tenLaptop);
                tf_hangsx.setText(hangSX);
                tf_cauhinh.setText(cauhinh);
                tf_kichthuoc.setText(kichthuoc);
                tf_trongluong.setText(trongluong);
                tf_gia.setText(giaFormatted);
                ta_mota.setText(mota);
                cb_macuahang.setSelectedItem(macuahang);

                JOptionPane.showMessageDialog(null, "Thêm Thành Công Sản Phẩm", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                sendInsertRequest(maLaptop, tenLaptop, hangSX, cauhinh, kichthuoc, trongluong, giaFormatted, mota, macuahang);
            } else {
                JOptionPane.showMessageDialog(null, "Thêm thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu thêm sản phẩm tới server qua socket
    public void sendInsertRequest(String maLaptop, String tenLaptop, String hangSX, String cauhinh, String kichthuoc,
            String trongluong, String giaFormatted, String mota, String macuahang) {
        try {
            dot.writeUTF("insert");
            dot.writeUTF(maLaptop);
            dot.writeUTF(tenLaptop);
            dot.writeUTF(hangSX);
            dot.writeUTF(cauhinh);
            dot.writeUTF(kichthuoc);
            dot.writeUTF(trongluong);
            dot.writeUTF(giaFormatted);
            dot.writeUTF(mota);
            dot.writeUTF(macuahang);
            dot.flush();

            // Nhận phản hồi từ server
            if (dis != null) {
                String response = dis.readUTF();
                if ("success".equals(response)) {
                    System.out.println("San pham duoc them thanh cong vao server.");
                }
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // cập nhập sản phẩm
    public void capNhatSanPham() {
        String maLaptop = tf_malaptop.getText().trim();
        String tenLaptop = tf_tenlaptop.getText().trim();
        String hangSX = tf_hangsx.getText().trim();
        String cauhinh = tf_cauhinh.getText().trim();
        String kichthuoc = tf_kichthuoc.getText().trim();
        String trongluong = tf_trongluong.getText().trim();
        String giaStr = tf_gia.getText().trim();
        String mota = ta_mota.getText().trim();
        String macuahang = (String) cb_macuahang.getSelectedItem();

        // Kiểm tra các ô không được để trống
        if (maLaptop.isEmpty() || tenLaptop.isEmpty() || hangSX.isEmpty() || cauhinh.isEmpty()
                || kichthuoc.isEmpty() || trongluong.isEmpty() || giaStr.isEmpty() || macuahang == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra giá trị "gia" phải là một con số hợp lệ
        float gia;
        try {
            gia = Float.parseFloat(giaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Giá phải là một con số hợp lệ!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "UPDATE Laptop SET TenLaptop = ?, HangSX = ?, CauHinh = ?, KichThuocManHinh = ?, TrongLuong = ?, Gia = ?,MaCuaHang = ?, MoTa = ? WHERE MaLaptop = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, tenLaptop);
            pstmt.setString(2, hangSX);
            pstmt.setString(3, cauhinh);
            pstmt.setString(4, kichthuoc);
            pstmt.setString(5, trongluong);
            pstmt.setFloat(6, gia);
            pstmt.setString(8, mota);
            pstmt.setString(7, macuahang);
            pstmt.setString(9, maLaptop);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showLaptop();
                xoa_noidungtf();
                JOptionPane.showMessageDialog(null, "Cập Nhập Thành Công Sản Phẩm", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                sendUpdateRequest(maLaptop, tenLaptop, hangSX, cauhinh, kichthuoc, trongluong, gia, mota, macuahang);
            } else {
                JOptionPane.showMessageDialog(null, "Cập Nhập thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gửi yêu cầu cập nhật sản phẩm tới server qua socket
    public void sendUpdateRequest(String maLaptop, String tenLaptop, String hangSX, String cauhinh, String kichthuoc,
            String trongluong, float gia, String mota, String macuahang) {
        try {
            dot.writeUTF("update");
            dot.writeUTF(maLaptop);
            dot.writeUTF(tenLaptop);
            dot.writeUTF(hangSX);
            dot.writeUTF(cauhinh);
            dot.writeUTF(kichthuoc);
            dot.writeUTF(trongluong);
            dot.writeFloat(gia); // Ghi giá là float
            dot.writeUTF(mota);
            dot.writeUTF(macuahang);
            dot.flush();

            // Nhận phản hồi từ server
            if (dis != null) {
                String response = dis.readUTF();
                if ("success".equals(response)) {
                    System.out.println("San pham duoc cap nhap thanh cong");
                } else {
                    System.out.println("Cap nhap that bai san pham");
                }
            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Xóa sản phẩm trên client
    public void xoaSanPham() throws SQLException {
        String maLaptop = tf_malaptop.getText().trim();

        // Kiểm tra mã sản phẩm không được để trống
        if (maLaptop.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn sản phẩm để xóa!", "Thông Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Gửi yêu cầu xóa sản phẩm tới server
            dot.writeUTF("delete");  // Gửi action "delete" để server biết
            dot.writeUTF(maLaptop); // Gửi mã sản phẩm cần xóa
            dot.flush();

            // Lắng nghe phản hồi từ server
            String response = dis.readUTF();
            if (response.equals("success")) {
                // Cập nhật lại JTable client sau khi xóa sản phẩm
                DefaultTableModel model = (DefaultTableModel) tblaptop.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).equals(maLaptop)) {
                        model.removeRow(i); // Xóa dòng khỏi JTable
                        break;
                    }
                }
                // Nếu bạn cần xóa dữ liệu trong cơ sở dữ liệu, thì tiếp tục thực hiện như sau:
                String query = "DELETE FROM Laptop WHERE MaLaptop = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, maLaptop);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showLaptop();  // Cập nhật lại bảng sản phẩm nếu cần thiết
                    xoa_noidungtf();  // Xóa nội dung các TextField
                    JOptionPane.showMessageDialog(null, "Xoá Thành Công Sản Phẩm", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Xoá thất bại vui lòng kiểm tra nội dung!", "Thông Báo", JOptionPane.OK_OPTION);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Xoá thất bại. Sản phẩm không tồn tại!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối tới server!", "Thông Báo", JOptionPane.ERROR_MESSAGE);
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
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        tf_malaptop = new javax.swing.JTextField();
        tf_tenlaptop = new javax.swing.JTextField();
        tf_hangsx = new javax.swing.JTextField();
        tf_cauhinh = new javax.swing.JTextField();
        tf_kichthuoc = new javax.swing.JTextField();
        tf_trongluong = new javax.swing.JTextField();
        tf_gia = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_mota = new javax.swing.JTextArea();
        cb_macuahang = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        themsanpham = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        updatesanpham = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        deletesanpham = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
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
                    .addComponent(jLabel4))
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lb_tentp)
                .addGap(21, 21, 21))
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

        tf_malaptop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_malaptop.setText("Mã sản phẩm");
        tf_malaptop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_malaptopMouseClicked(evt);
            }
        });
        tf_malaptop.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tf_malaptopInputMethodTextChanged(evt);
            }
        });

        tf_tenlaptop.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_tenlaptop.setText("Tên sản phẩm");
        tf_tenlaptop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_tenlaptopMouseClicked(evt);
            }
        });
        tf_tenlaptop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_tenlaptopActionPerformed(evt);
            }
        });

        tf_hangsx.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_hangsx.setText("Thương hiệu");
        tf_hangsx.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_hangsxMouseClicked(evt);
            }
        });
        tf_hangsx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_hangsxActionPerformed(evt);
            }
        });

        tf_cauhinh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_cauhinh.setText("Cấu hình");
        tf_cauhinh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_cauhinhMouseClicked(evt);
            }
        });
        tf_cauhinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_cauhinhActionPerformed(evt);
            }
        });

        tf_kichthuoc.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_kichthuoc.setText("Kích thước");
        tf_kichthuoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_kichthuocMouseClicked(evt);
            }
        });
        tf_kichthuoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_kichthuocActionPerformed(evt);
            }
        });

        tf_trongluong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_trongluong.setText("Trọng lượng");
        tf_trongluong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tf_trongluongMouseClicked(evt);
            }
        });
        tf_trongluong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf_trongluongActionPerformed(evt);
            }
        });

        tf_gia.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tf_gia.setText("Giá bán");
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

        ta_mota.setColumns(20);
        ta_mota.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ta_mota.setRows(5);
        ta_mota.setText("Mô tả\n");
        ta_mota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ta_motaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(ta_mota);

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
                        .addComponent(tf_malaptop, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tf_tenlaptop, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tf_hangsx, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tf_gia, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cb_macuahang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(tf_cauhinh, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tf_kichthuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tf_trongluong, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_malaptop, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_tenlaptop, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_hangsx, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tf_cauhinh, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_kichthuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tf_trongluong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cb_macuahang, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tf_gia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jLabel15.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 0, 204));
        jLabel15.setText("Chức năng");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(51, 51, 255), new java.awt.Color(0, 51, 255)));

        themsanpham.setBackground(new java.awt.Color(255, 204, 0));
        themsanpham.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        themsanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                themsanphamMouseClicked(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel17.setText("Thêm sản phẩm");

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/add.png"))); // NOI18N

        javax.swing.GroupLayout themsanphamLayout = new javax.swing.GroupLayout(themsanpham);
        themsanpham.setLayout(themsanphamLayout);
        themsanphamLayout.setHorizontalGroup(
            themsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, themsanphamLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addGap(18, 18, 18))
        );
        themsanphamLayout.setVerticalGroup(
            themsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(themsanphamLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(themsanphamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel17))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updatesanpham.setBackground(new java.awt.Color(255, 0, 51));
        updatesanpham.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        updatesanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updatesanphamMouseClicked(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel19.setText("Sửa sản phẩm");

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
        jLabel21.setText("Xoá sản phẩm");

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
                .addGap(26, 26, 26)
                .addComponent(themsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(updatesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deletesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(119, 119, 119))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(themsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updatesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(deletesanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 0, 204));
        jLabel16.setText("Thông tin sản phẩm");

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
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
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(20, 20, 20))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void tf_tenlaptopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_tenlaptopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_tenlaptopActionPerformed

    private void tf_hangsxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_hangsxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_hangsxActionPerformed

    private void tf_cauhinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_cauhinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_cauhinhActionPerformed

    private void tf_kichthuocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_kichthuocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_kichthuocActionPerformed

    private void tf_trongluongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_trongluongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_trongluongActionPerformed

    private void tf_giaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tf_giaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tf_giaActionPerformed

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void tf_malaptopInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tf_malaptopInputMethodTextChanged

    }//GEN-LAST:event_tf_malaptopInputMethodTextChanged

    private void tf_malaptopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_malaptopMouseClicked
        tf_malaptop.setText("");
    }//GEN-LAST:event_tf_malaptopMouseClicked

    private void tf_tenlaptopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_tenlaptopMouseClicked
        tf_tenlaptop.setText("");
    }//GEN-LAST:event_tf_tenlaptopMouseClicked

    private void tf_hangsxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_hangsxMouseClicked
        tf_hangsx.setText("");
    }//GEN-LAST:event_tf_hangsxMouseClicked

    private void tf_cauhinhMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_cauhinhMouseClicked
        tf_cauhinh.setText("");
    }//GEN-LAST:event_tf_cauhinhMouseClicked

    private void tf_kichthuocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_kichthuocMouseClicked
        tf_kichthuoc.setText("");
    }//GEN-LAST:event_tf_kichthuocMouseClicked

    private void tf_trongluongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_trongluongMouseClicked
        tf_trongluong.setText("");
    }//GEN-LAST:event_tf_trongluongMouseClicked

    private void tf_giaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tf_giaMouseClicked
        tf_gia.setText("");
    }//GEN-LAST:event_tf_giaMouseClicked

    private void ta_motaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ta_motaMouseClicked
        ta_mota.setText("");
    }//GEN-LAST:event_ta_motaMouseClicked

    private void themsanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_themsanphamMouseClicked
        themSanPham();
    }//GEN-LAST:event_themsanphamMouseClicked

    private void updatesanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updatesanphamMouseClicked
        capNhatSanPham();
    }//GEN-LAST:event_updatesanphamMouseClicked

    private void deletesanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deletesanphamMouseClicked
        try {
            xoaSanPham();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GiaoDienQuanLyLapTop.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_deletesanphamMouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DonHang donHang = new DonHang();
        donHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DoanhThu doanhThu = new DoanhThu();
        doanhThu.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void class_cuahangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_cuahangMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        CuaHang cuaHang = new CuaHang();
        cuaHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_class_cuahangMouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        KhachHang khachHang = new KhachHang();
        khachHang.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel12MouseClicked

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        JOptionPane.showMessageDialog(null, "Đăng Xuất Thành Công!", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
        TenThanhPho.setTenthanhpho("");
        DangNhap dangNhap = new DangNhap();
        dangNhap.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new GiaoDienQuanLyLapTop().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cb_macuahang;
    private javax.swing.JLabel class_cuahang;
    private javax.swing.JPanel deletesanpham;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel logout;
    private javax.swing.JTextArea ta_mota;
    private javax.swing.JTable tblaptop;
    private javax.swing.JTextField tf_cauhinh;
    private javax.swing.JTextField tf_gia;
    private javax.swing.JTextField tf_hangsx;
    private javax.swing.JTextField tf_kichthuoc;
    private javax.swing.JTextField tf_malaptop;
    private javax.swing.JTextField tf_tenlaptop;
    private javax.swing.JTextField tf_trongluong;
    private javax.swing.JPanel themsanpham;
    private javax.swing.JPanel updatesanpham;
    // End of variables declaration//GEN-END:variables
}
