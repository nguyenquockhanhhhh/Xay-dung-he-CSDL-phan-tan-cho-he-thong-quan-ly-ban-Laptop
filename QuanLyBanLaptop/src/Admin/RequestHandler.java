package Admin;

import java.io.*;
import java.net.*;
import javax.swing.table.DefaultTableModel;

public class RequestHandler extends Thread {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dot;
    private DefaultTableModel model;

    public RequestHandler(Socket socket, DefaultTableModel model) {
        this.socket = socket;
        this.model = model;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dot = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Nhận hành động từ client
                String action = dis.readUTF();
                if (action.equals("insert")) {
                    handleInsert();
                } else if (action.equals("delete")) {
                    handleDelete();
                } else if (action.equals("update")) {
                    handleUpdate();
                } else if (action.equals("insertCuaHang")) {
                    handleInsertCuaHang();
                } else if (action.equals("updateCuaHang")) {
                    handleUpdateCuaHang();
                } else if (action.equals("deleteCuaHang")) {
                    handleDeleteCuaHang();
                } else if (action.equals("updateDonHang")) {
                    handleUpdateDonHang();
                } else if (action.equals("deleteDonHang")) {
                    handleDeleteDonHang();
                } else if (action.equals("insertKhachHang")) {
                    handleInsertKhachHang();
                } else if (action.equals("updateKhachHang")) {
                    handleUpdateKhachHang();
                } else if (action.equals("deleteKhachHang")) {
                    handleDeleteKhachHang();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close(); // Đóng kết nối client
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // insert Laptop
    private void handleInsert() throws IOException {
        String maLaptop = dis.readUTF();
        String tenLaptop = dis.readUTF();
        String hangSX = dis.readUTF();
        String cauhinh = dis.readUTF();
        String kichthuoc = dis.readUTF();
        String trongluong = dis.readUTF();
        String giaStr = dis.readUTF();
        String mota = dis.readUTF();
        String macuahang = dis.readUTF();

        // Cập nhật JTable trên server
        model.insertRow(0, new Object[]{
            maLaptop, tenLaptop, hangSX, cauhinh, kichthuoc, trongluong, giaStr, macuahang, mota
        });

        dot.writeUTF("success");
    }

    // insert Cuahang
    private void handleInsertCuaHang() throws IOException {
        String macuahang = dis.readUTF();
        String tencuahang = dis.readUTF();
        String diachi = dis.readUTF();
        String sdt = dis.readUTF();

        // Cập nhật JTable trên server
        model.insertRow(0, new Object[]{
            macuahang, tencuahang, diachi, sdt
        });

        dot.writeUTF("success");
    }
    
     // insert khachhang
    private void handleInsertKhachHang() throws IOException {
        String makhachhang = dis.readUTF();
        String tenkhachhang = dis.readUTF();
        String email = dis.readUTF();
        String sdt = dis.readUTF();

        // Cập nhật JTable trên server
        model.insertRow(0, new Object[]{
            makhachhang, tenkhachhang, email, sdt
        });

        dot.writeUTF("success");
    }

    // xoá laptop
    private void handleDelete() throws IOException {
        String maLaptop = dis.readUTF();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(maLaptop)) {
                model.removeRow(i);
                break;
            }
        }
        dot.writeUTF("success");
    }

    // xoá khachhang
    private void handleDeleteKhachHang() throws IOException {
        String makhachhang = dis.readUTF();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(makhachhang)) {
                model.removeRow(i);
                break;
            }
        }
        dot.writeUTF("success");
    }

    // xoá donhang
    private void handleDeleteDonHang() throws IOException {
        String madonhang = dis.readUTF();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(madonhang)) {
                model.removeRow(i);
                break;
            }
        }
        dot.writeUTF("success");
    }

    // xoá cuahang
    private void handleDeleteCuaHang() throws IOException {
        String macuahang = dis.readUTF();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(macuahang)) {
                model.removeRow(i);
                break;
            }
        }
        dot.writeUTF("success");
    }

    // sửa laptop
    private void handleUpdate() throws IOException {
        String maLaptop = dis.readUTF();
        String tenLaptop = dis.readUTF();
        String hangSX = dis.readUTF();
        String cauhinh = dis.readUTF();
        String kichthuoc = dis.readUTF();
        String trongluong = dis.readUTF();
        float gia = dis.readFloat();
        String mota = dis.readUTF();
        String macuahang = dis.readUTF();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(maLaptop)) {
                model.setValueAt(tenLaptop, i, 1);
                model.setValueAt(hangSX, i, 2);
                model.setValueAt(cauhinh, i, 3);
                model.setValueAt(kichthuoc, i, 4);
                model.setValueAt(trongluong, i, 5);
                model.setValueAt(gia, i, 6);
                model.setValueAt(macuahang, i, 7);
                model.setValueAt(mota, i, 8);
                break;
            }
        }
        dot.writeUTF("success");
    }

    // sửa cuahang
    private void handleUpdateCuaHang() throws IOException {
        String macuahang = dis.readUTF();
        String tencuahang = dis.readUTF();
        String diachi = dis.readUTF();
        String sdt = dis.readUTF();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(macuahang)) {
                model.setValueAt(tencuahang, i, 1);
                model.setValueAt(diachi, i, 2);
                model.setValueAt(sdt, i, 3);
                break;
            }
        }
        dot.writeUTF("success");
    }
    
      // sửa khach hang
    private void handleUpdateKhachHang() throws IOException {
        // Nhận dữ liệu từ client
        String makhachhang = dis.readUTF();
        String tenkhachhang = dis.readUTF();
        String email = dis.readUTF();
        String sdt = dis.readUTF();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(makhachhang)) {
                model.setValueAt(tenkhachhang, i, 1);
                model.setValueAt(email, i, 2);
                model.setValueAt(sdt, i, 3);
                break;
            }
        }
        dot.writeUTF("success");
    }

    // sửa donhang
    private void handleUpdateDonHang() throws IOException {
        // Nhận dữ liệu từ client
        String madonhang = dis.readUTF();
        String masanpham = dis.readUTF();
        String macuahang = dis.readUTF();
        String makhachhang = dis.readUTF();
        String mathoigian = dis.readUTF();
        String soluong = dis.readUTF();
        String tongtien = dis.readUTF();

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(madonhang)) {
                model.setValueAt(masanpham, i, 1);
                model.setValueAt(macuahang, i, 2);
                model.setValueAt(makhachhang, i, 3);
                model.setValueAt(mathoigian, i, 4);
                model.setValueAt(soluong, i, 5);
                model.setValueAt(tongtien, i, 6);
                break;
            }
        }
        dot.writeUTF("success");
    }
}
