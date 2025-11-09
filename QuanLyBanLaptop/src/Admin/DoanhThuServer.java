package Admin;

import Application.*;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSQL;
import QuanLyLapTop.ConnectToSQLSever.ConnectToSeverOther;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.*;
import javax.swing.JPanel;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class DoanhThuServer extends javax.swing.JFrame {

    DefaultTableModel tableModel = new DefaultTableModel();
    DefaultTableModel tableModel1 = new DefaultTableModel();
    Statement stmt;
    ResultSet rs;
    PreparedStatement pst;
    Connection conn = null;
    String matp = null;
    String servername = null;
   
    public DoanhThuServer() {
        // Xóa khung cửa sổ
        setUndecorated(true);
        initComponents();
        setLocationRelativeTo(null); // Đặt giao diện ở giữa màn hình
        table();
        get_tentpid();
        ketnoidensever();
        conn = ConnectToSeverOther.connectToDatabaseOther(servername);
        showDoanhThu();   
        layTongSoLuongDonHang();
        layTongSoLuongKhachHang();
        layTongSoLuongSanPham();
        layTongSoLuongCuaHang();
    }
    
    // design table
    public void table()
    {
        tblaptop.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        tblaptop.getTableHeader().setOpaque(false);
        tblaptop.getTableHeader().setBackground(new Color(32,136,203));
        tblaptop.getTableHeader().setForeground(new Color(255,255,255));
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
    }}

    
    // lấy sever name
     public void ketnoidensever()
    {
        if (matp.equals("TPDN")) {
            servername = "NGUYENQUOCKHANH\\SEVER1";
        } else if (matp.equals("TPH")) {
            servername = "NGUYENQUOCKHANH\\SEVER2";
        } else if (matp.equals("TPHCM")) {
            servername = "NGUYENQUOCKHANH\\SEVER3";
        }else
        {
            servername = "NGUYENQUOCKHANH\\SEVER";
        }
    }

     // hiển thị dữ liệu lên table
    public void showDoanhThu() {
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("MaDonHang");
    model.addColumn("TenCuaHang");
    model.addColumn("TenLapTop");
    model.addColumn("MaThoiGian");
    model.addColumn("TongTien");

    try {
        String query = "SELECT dt.MaDonHang, ch.TenCuaHang, l.TenLaptop, dt.MaThoiGian, dt.TongTien " +
                       "FROM DoanhThu dt " +
                       "JOIN DonHang dh ON dt.MaDonHang = dh.MaDonHang " +
                       "JOIN CuaHang ch ON dh.MaCuaHang = ch.MaCuaHang " +
                       "JOIN Laptop l ON dh.MaLaptop = l.MaLaptop";

        PreparedStatement pstmt = conn.prepareStatement(query);

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            float gia = rs.getFloat("TongTien");
            int giaInt = (int) gia;

            model.addRow(new Object[] {
                rs.getString("MaDonHang"),
                rs.getString("TenCuaHang"),
                rs.getString("TenLapTop"),
                rs.getString("MaThoiGian"),
                giaInt,
            });
        }
        tableModel1 = model;
        tblaptop.setModel(model); 
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
   
    // biểu đồ tổng doanh thu theo từng cửa hàng
    public void doanhthutheotungcuahang(DefaultTableModel model) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Lấy dữ liệu từ model và thêm vào dataset
        for (int i = 0; i < model.getRowCount(); i++) {
            String tenCuaHang = (String) model.getValueAt(i, 1); // Lấy tên cửa hàng
            int tongTien = (int) model.getValueAt(i, 4); // Lấy tổng tiền

            dataset.addValue(tongTien, "Tổng Tiền", tenCuaHang);
        }

        // Tạo biểu đồ dạng cột
        JFreeChart barChart = ChartFactory.createBarChart(
                "Doanh Thu Theo Cửa Hàng", // Tiêu đề biểu đồ
                "Cửa Hàng", // Trục X
                "Tổng Tiền (VNĐ)", // Trục Y
                dataset // Dữ liệu
        );

        // Thiết lập màu sắc cho các cột
        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                // Đổi màu theo chỉ số cột (vòng lặp màu)
                Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA};
                return colors[column % colors.length];
            }
        };
        plot.setRenderer(renderer);

        // Thiết lập khoảng cách giữa các cột
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        // Tạo panel biểu đồ
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Thêm biểu đồ vào JFrame
        JFrame chartFrame = new JFrame("Biểu Đồ Doanh Thu");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
        chartFrame.setVisible(true);
    }
    
    // thống kê hãng sản xuất
    public void thongKeHangSX() {
    DefaultPieDataset dataset = new DefaultPieDataset();

    try {
        String query = "SELECT HangSX, SUM(SoLuong) AS SoLuongBan " +
               "FROM DonHang " +
               "JOIN Laptop ON DonHang.MaLaptop = Laptop.MaLaptop " +
               "GROUP BY HangSX;";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String hangSX = rs.getString("HangSX");
            int soLuongBan = rs.getInt("SoLuongBan");

            dataset.setValue(hangSX, soLuongBan); // Thêm dữ liệu vào dataset
        }

        drawPieChart(dataset);

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
    // biểu đồ tròn thể hiện doanh số bán được theo hãng sản xuất
    private void drawPieChart(DefaultPieDataset dataset) {
    JFreeChart pieChart = ChartFactory.createPieChart(
            "Thống Kê Số Lượng Bán Theo Hãng Sản Xuất", // Tiêu đề biểu đồ
            dataset, // Dữ liệu
            true, // Có hiển thị chú thích không
            true, // Có hiển thị tooltip không
            false // Có hiển thị URL không
    );

      // Lấy đối tượng PiePlot từ biểu đồ
    PiePlot plot = (PiePlot) pieChart.getPlot();
    
    // Thiết lập định dạng nhãn cho các phần của biểu đồ
    plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

    // Tạo panel biểu đồ
    ChartPanel chartPanel = new ChartPanel(pieChart);
    chartPanel.setPreferredSize(new Dimension(800, 600));

        // Thêm biểu đồ vào JFrame
        JFrame chartFrame = new JFrame("Biểu Đồ Doanh Thu");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
        chartFrame.setVisible(true);
    }
    
    // số lượng đơn hàng
    public void layTongSoLuongDonHang() {
    try {
        String query = "SELECT COUNT(MaDonHang) AS TongSoLuong FROM DonHang";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int tongSoLuong = rs.getInt("TongSoLuong");
            // Cập nhật JLabel để hiển thị tổng số lượng
            lbtongsoluongdonhang.setText(String.valueOf(tongSoLuong));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
    // số lượng khách hàng
    public void layTongSoLuongKhachHang() {
    try {
        String query = "SELECT COUNT(MaKhachHang) AS SoLuongKhachHang FROM KhachHang";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int tongSoLuong = rs.getInt("SoLuongKhachHang");
            // Cập nhật JLabel để hiển thị tổng số lượng
            lbtongsoluongkhachhang.setText(String.valueOf(tongSoLuong));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
     // số lượng sản phẩm
    public void layTongSoLuongSanPham() {
    try {
        String query = "SELECT COUNT(MaLapTop) AS SoLuongLapTop FROM Laptop";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int tongSoLuong = rs.getInt("SoLuongLapTop");
            // Cập nhật JLabel để hiển thị tổng số lượng
           lbtongsoluongsanpham.setText(String.valueOf(tongSoLuong));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
    
     // số lượng cửa hàng
    public void layTongSoLuongCuaHang() {
    try {
        String query = "SELECT COUNT(MaCuaHang) AS SoLuongCuaHang FROM CuaHang";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int tongSoLuong = rs.getInt("SoLuongCuaHang");
            // Cập nhật JLabel để hiển thị tổng số lượng
           lbtongsoluongcuahang.setText(String.valueOf(tongSoLuong));
        }

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
        class_giaodiensanpham = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        class_donhang = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
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
        jLabel15 = new javax.swing.JLabel();
        thongkedoanhthu = new javax.swing.JButton();
        thongkehangsx = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lbtongsoluongdonhang = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lbtongsoluongkhachhang = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lbtongsoluongsanpham = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lbtongsoluongcuahang = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lbsoluongcuahang = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/coding.png"))); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/checklist.png"))); // NOI18N

        class_giaodiensanpham.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        class_giaodiensanpham.setForeground(new java.awt.Color(255, 255, 255));
        class_giaodiensanpham.setText("Sản Phẩm");
        class_giaodiensanpham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_giaodiensanphamMouseClicked(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/order.png"))); // NOI18N

        class_donhang.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        class_donhang.setForeground(new java.awt.Color(255, 255, 255));
        class_donhang.setText("Đơn Hàng");
        class_donhang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                class_donhangMouseClicked(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/earning.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Cửa Hàng");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Consolas", 3, 22)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Doanh Thu");

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
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(class_giaodiensanpham))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(class_donhang))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)))
                .addGap(0, 23, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lb_tentp)
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
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lgout))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(class_giaodiensanpham))
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(class_donhang))
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
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
                .addGap(24, 24, 24))
        );

        jPanel4.setBackground(new java.awt.Color(0, 153, 153));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
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
                .addGap(47, 47, 47)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(exit)
                    .addComponent(jLabel2))
                .addContainerGap())
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 940, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jLabel15.setBackground(new java.awt.Color(255, 51, 51));
        jLabel15.setFont(new java.awt.Font("Times New Roman", 3, 30)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 51, 51));
        jLabel15.setText("Doanh Thu Bán Hàng");

        thongkedoanhthu.setBackground(new java.awt.Color(255, 255, 204));
        thongkedoanhthu.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        thongkedoanhthu.setText("Doanh thu cửa hàng");
        thongkedoanhthu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                thongkedoanhthuMouseClicked(evt);
            }
        });

        thongkehangsx.setBackground(new java.awt.Color(51, 51, 255));
        thongkehangsx.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        thongkehangsx.setForeground(new java.awt.Color(255, 255, 255));
        thongkehangsx.setText("Thống kê hãng sãn xuất");
        thongkehangsx.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                thongkehangsxMouseClicked(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        lbtongsoluongdonhang.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lbtongsoluongdonhang.setText("0");

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/coins (1).png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbtongsoluongdonhang, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel14))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lbtongsoluongdonhang)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 0, 51));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText("Số lượng đơn hàng");

        jPanel7.setBackground(new java.awt.Color(0, 0, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 204, 0));
        jLabel6.setText("Số lượng khách hàng");

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));

        lbtongsoluongkhachhang.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lbtongsoluongkhachhang.setText("0");

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/customer-review.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbtongsoluongkhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel16))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lbtongsoluongkhachhang)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel18.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(153, 153, 153));
        jLabel18.setText("Số lượng sản phẩm");

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));

        lbtongsoluongsanpham.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lbtongsoluongsanpham.setText("0");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/box_1.png"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbtongsoluongsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel17))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lbtongsoluongsanpham)))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));

        lbtongsoluongcuahang.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lbtongsoluongcuahang.setText("0");

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/shops_1.png"))); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbtongsoluongcuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel19))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lbtongsoluongcuahang)))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        lbsoluongcuahang.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        lbsoluongcuahang.setForeground(new java.awt.Color(255, 204, 51));
        lbsoluongcuahang.setText("Số lượng cửa hàng");

        jPanel12.setBackground(new java.awt.Color(102, 204, 0));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 201, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(255, 0, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(57, 57, 57)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(67, 67, 67)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(56, 56, 56)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lbsoluongcuahang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(20, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(342, 342, 342)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(185, 185, 185)
                        .addComponent(thongkedoanhthu, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)
                        .addComponent(thongkehangsx, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbsoluongcuahang)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thongkedoanhthu, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thongkehangsx, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void class_donhangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_donhangMouseClicked
        TenThanhPho.setTenthanhpho(lb_tentp.getText());
        DonHangServer donHang = new DonHangServer();
        donHang.setVisible(true);
        this.dispose(); 
    }//GEN-LAST:event_class_donhangMouseClicked

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void class_giaodiensanphamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_class_giaodiensanphamMouseClicked
         TenThanhPho.setTenthanhpho(lb_tentp.getText());
         GiaoDienQuanLyLapTopServer gdqllt = new GiaoDienQuanLyLapTopServer();
         gdqllt.setVisible(true);
         this.dispose(); 
    }//GEN-LAST:event_class_giaodiensanphamMouseClicked

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
        JOptionPane.showMessageDialog(null, "Đăng Xuất Thành Công!", "Thông Báo",JOptionPane.INFORMATION_MESSAGE);
        TenThanhPho.setTenthanhpho("");
        DangNhap dangNhap = new DangNhap();
        dangNhap.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lgoutMouseClicked

    private void thongkedoanhthuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_thongkedoanhthuMouseClicked
            doanhthutheotungcuahang(tableModel1);
    }//GEN-LAST:event_thongkedoanhthuMouseClicked

    private void thongkehangsxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_thongkehangsxMouseClicked
            thongKeHangSX();
    }//GEN-LAST:event_thongkehangsxMouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DoanhThuServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel class_donhang;
    private javax.swing.JLabel class_giaodiensanpham;
    private javax.swing.JLabel exit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_tentp;
    private javax.swing.JLabel lbsoluongcuahang;
    private javax.swing.JLabel lbtongsoluongcuahang;
    private javax.swing.JLabel lbtongsoluongdonhang;
    private javax.swing.JLabel lbtongsoluongkhachhang;
    private javax.swing.JLabel lbtongsoluongsanpham;
    private javax.swing.JLabel lgout;
    private javax.swing.JTable tblaptop;
    private javax.swing.JButton thongkedoanhthu;
    private javax.swing.JButton thongkehangsx;
    // End of variables declaration//GEN-END:variables
}
