ALTER PROCEDURE Login
    @TenThanhPho NVARCHAR(100)
AS
BEGIN
    DECLARE @MaTP NVARCHAR(50)
    
    -- Lấy MaTP từ bảng ThanhPho dựa trên TenTP
    SELECT @MaTP = MaTP
    FROM ThanhPho
    WHERE TenTP = @TenThanhPho

    -- Nếu không tìm thấy MaTP tương ứng, thoát khỏi stored procedure
    IF @MaTP IS NULL
    BEGIN
        PRINT 'Không tìm thấy thông tin cho thành phố đã chọn.'
        RETURN
    END

    -- Truy vấn dữ liệu từ bảng Laptop dựa trên MaCuaHang và MaTP
    SELECT *
    FROM Laptop l
    WHERE l.MaCuaHang IN (
        SELECT MaCuaHang
        FROM CuaHang
        WHERE MaTP = @MaTP
    )
END


-- tạo PROCEDURE nếu đăng nhập thành công với TenThanhPho thì nó sẽ lấy MaTP 
CREATE PROCEDURE Login_MATP
    @TenThanhPho NVARCHAR(100)
AS
BEGIN
    DECLARE @MaTP NVARCHAR(50)
    
    -- Lấy MaTP từ bảng ThanhPho dựa trên TenTP
    SELECT @MaTP = MaTP
    FROM ThanhPho
    WHERE TenTP = @TenThanhPho

    -- Nếu không tìm thấy MaTP tương ứng, thoát khỏi stored procedure
    IF @MaTP IS NULL
    BEGIN
        PRINT 'Không tìm thấy thông tin cho thành phố đã chọn.'
        RETURN
    END

    -- Trả về thông tin mã và tên thành phố đã chọn
    SELECT MaTP, TenTP
    FROM ThanhPho
    WHERE MaTP = @MaTP
END

EXEC Login_MATP 'Da Nang';


-- select table Laptop theo MaTP
SELECT l.MaLaptop, l.TenLaptop, l.HangSX, l.CauHinh, l.KichThuocManHinh, l.TrongLuong, l.Gia, l.MoTa, l.MaCuaHang
FROM Laptop l
INNER JOIN CuaHang ch ON l.MaCuaHang = ch.MaCuaHang
WHERE ch.MaTP = 'TPDN';


-- select table DonHang theo MaTP
SELECT 
    DH.MaDonHang,
    DH.MaLaptop,
    DH.MaCuaHang,
    DH.MaKhachHang,
    DH.MaThoiGian,
    DH.SoLuong,
    DH.TongTien
FROM DonHang DH
JOIN CuaHang CH ON DH.MaCuaHang = CH.MaCuaHang
JOIN ThanhPho TP ON CH.MaTP = TP.MaTP
WHERE TP.MaTP = 'TPDN';

-- tự động xoá cột doanh thu nếu xoá đơn hàng
ALTER TABLE DoanhThu
DROP CONSTRAINT FK_DoanhThu_DonHang;

ALTER TABLE DoanhThu
ADD CONSTRAINT FK_DoanhThu_DonHang FOREIGN KEY (MaDonHang)
REFERENCES DonHang(MaDonHang)
ON DELETE CASCADE;


-- thong ke so luong hang ban duoc
SELECT HangSX, SUM(SoLuong) AS TongSoLuong
FROM DonHang
JOIN Laptop ON DonHang.MaLaptop = Laptop.MaLaptop
GROUP BY HangSX;