package Admin;

import java.io.IOException;
import java.net.Socket;

public class SocketManager {

    private static Socket laptopSocket;
    private static Socket cuaHangSocket;
    private static Socket donhangSocket;
    private static Socket khachhangSocket;

    public static Socket getLaptopSocket() throws IOException {
        if (laptopSocket == null || laptopSocket.isClosed()) {
            laptopSocket = new Socket("127.0.0.1", 1201);
        }
        return laptopSocket;
    }

    public static Socket getCuaHangSocket() throws IOException {
        if (cuaHangSocket == null || cuaHangSocket.isClosed()) {
            cuaHangSocket = new Socket("127.0.0.1", 1202);
        }
        return cuaHangSocket;
    }

    public static Socket getDonHangSocket() throws IOException {
        if (donhangSocket == null || donhangSocket.isClosed()) {
            donhangSocket = new Socket("127.0.0.1", 1203);
        }
        return donhangSocket;
    }

    public static Socket getKhachHangSocket() throws IOException {
        if (khachhangSocket == null || khachhangSocket.isClosed()) {
            khachhangSocket = new Socket("127.0.0.1", 1204);
        }
        return khachhangSocket;
    }
}
