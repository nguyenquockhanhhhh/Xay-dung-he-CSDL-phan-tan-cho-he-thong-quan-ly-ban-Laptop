
package Application;

import java.io.Serializable;

public class StoreData implements Serializable {
    private String macuahang;
    private String tencuahang;
    private String diachi;
    private String sodienthoai;

    public StoreData(String macuahang, String tencuahang, String diachi, String sodienthoai) {
        this.macuahang = macuahang;
        this.tencuahang = tencuahang;
        this.diachi = diachi;
        this.sodienthoai = sodienthoai;
    }

    public String getMacuahang() {
        return macuahang;
    }

    public void setMacuahang(String macuahang) {
        this.macuahang = macuahang;
    }

    public String getTencuahang() {
        return tencuahang;
    }

    public void setTencuahang(String tencuahang) {
        this.tencuahang = tencuahang;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    
}
