CREATE DATABASE HeThongQuanLyBanLapTop

CREATE TABLE ThanhPho (
    MaTP NVARCHAR(50) NOT NULL PRIMARY KEY,
    TenTP NVARCHAR(100) NOT NULL,
    SoCuaHang INT
);

CREATE TABLE CuaHang (
    MaCuaHang NVARCHAR(50) NOT NULL PRIMARY KEY,
    TenCuaHang NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(200) NOT NULL,
    SoDienThoai NVARCHAR(15),
    MaTP NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_CuaHang_ThanhPho FOREIGN KEY (MaTP) REFERENCES ThanhPho(MaTP)
);

CREATE TABLE Laptop (
    MaLaptop NVARCHAR(50) NOT NULL PRIMARY KEY,
    TenLaptop NVARCHAR(100) NOT NULL,
    HangSX NVARCHAR(50) NOT NULL,
	CauHinh NVARCHAR(100) NOT NULL,
	KichThuocManHinh NVARCHAR(100) NOT NULL,
	TrongLuong NVARCHAR(100) NOT NULL,
    Gia FLOAT NOT NULL,
    MoTa NTEXT,
    MaCuaHang NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_Laptop_CuaHang FOREIGN KEY (MaCuaHang) REFERENCES CuaHang(MaCuaHang)
);

CREATE TABLE KhachHang (
    MaKhachHang NVARCHAR(50) NOT NULL PRIMARY KEY,
    TenKhachHang NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100),
    SoDienThoai NVARCHAR(15)
);
ALTER TABLE KhachHang
ADD MaTP NVARCHAR(50) NOT NULL;

ALTER TABLE KhachHang
ADD CONSTRAINT FK_KhachHang_ThanhPho FOREIGN KEY (MaTP) REFERENCES ThanhPho(MaTP);

CREATE TABLE DonHang (
    MaDonHang NVARCHAR(50) NOT NULL PRIMARY KEY,
    MaLaptop NVARCHAR(50) NOT NULL,
    MaCuaHang NVARCHAR(50) NOT NULL,
	MaKhachHang NVARCHAR(50) NOT NULL,
    MaThoiGian DATETIME NOT NULL,
    SoLuong INT NOT NULL,
    TongTien FLOAT NOT NULL,
    CONSTRAINT FK_GiaoDich_Laptop FOREIGN KEY (MaLaptop) REFERENCES Laptop(MaLaptop),
    CONSTRAINT FK_GiaoDich_CuaHang FOREIGN KEY (MaCuaHang) REFERENCES CuaHang(MaCuaHang),
	CONSTRAINT FK_GiaoDich_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
	CONSTRAINT FK_GiaoDich_ThoiGian FOREIGN KEY (MaThoiGian) REFERENCES ThoiGian(MaThoiGian),
);

CREATE TABLE ThoiGian(
	MaThoiGian DATETIME NOT NULL PRIMARY KEY,
	Ngay NVARCHAR(10) NOT NULL,
	Thang NVARCHAR(10) NOT NULL,
	Nam VARCHAR(10) NOT NULL
)

CREATE TABLE DoanhThu(
	MaDonHang NVARCHAR(50) NOT NULL,
	MaThoiGian DATETIME NOT NULL,
    TongTien FLOAT NOT NULL,
	CONSTRAINT FK_DoanhThu_DonHang FOREIGN KEY (MaDonHang) REFERENCES DonHang(MaDonHang),
	CONSTRAINT FK_DoanhThu_ThoiGian FOREIGN KEY (MaThoiGian) REFERENCES ThoiGian(MaThoiGian),
)

CREATE TABLE Users(
	 UserName NVARCHAR(50) NOT NULL PRIMARY KEY,
	 MaTP NVARCHAR(50) NOT NULL,
	 Pass NVARCHAR(50) NOT NULL,
	 CONSTRAINT FK_Users_ThanhPho FOREIGN KEY (MaTP) REFERENCES ThanhPho(MaTP)
)

-- dữ liệu table CuaHang
INSERT INTO CuaHang (MaCuaHang, TenCuaHang, DiaChi, SoDienThoai, MaTP) VALUES
-- Cửa hàng ở Đà Nẵng
('L88', 'Laptop88', '123 Le Loi, Đa Nang', '0909123456', 'TPDN'),
('KLDN', 'Kim Long Center', '456 Nguyen Van Linh, Đa Nang', '0909234567', 'TPDN'),
('FPTDN', 'FPT Shop', '7-9 Nguyen Van Thoai, Bac My Phu, Son Tra, Đa Nang 550000', '18006616', 'TPDN'),
('PLP', 'Phi Long Plaza', '52 Nguyen Van Linh, Hai Chau, Đa Nang', '02363888000', 'TPDN'),
('MET', 'Mega Technology', '130 Ham Nghi, Thac Gian, Thanh Kha, Đa Nang 550000', ' 02363689300', 'TPDN'),
('PVDN', 'Phong Vũ', '149-151 Ham Nghi, Q Thanh Khe, TP Đa Nang', '02363651222', 'TPDN'),
-- Cửa hàng ở Huế
('PA', 'Phúc Anh Computer', '789 Tran Hung Dao, Hue', '0909345678', 'TPH'),
('FPTH', 'FPT Shop', '10 Hung Vuong, TP. Hue, Thaa Thien Hue', '18006616', 'TPH'),
('TGDDH', 'Thế Giới Di Động', '539 Lac Long Quan, TT. Lang Co, H. Phu Loc, Thua Thien Hue', '18001060', 'TPH'),
('PLT', 'Phi Long Technolory', '46 - 48 Hung Vuong, Phu Hoi, TP. Hue, Thua Thien Hue', '02343977000', 'TPH'),
('VTS', 'Viettel Store', '19 Cach Mang Thang Tam, Tu Ha, Huong Tra, Thua Thien Hue', ' 18008123', 'TPH'),
-- Cửa hàng ở Hồ Chí Minh
('KAHCM', 'Kim Anh', '101 Bui Thi Xuan, Ho Chi Minh', '0909456789', 'TPHCM'),
('KLHCM', 'Kim Long Center', '202 Ly Thuong Kiet, Ho Chi Minh', '0909567890', 'TPHCM'),
('LVN', 'Laptops.vn', '303 Đường Nguyen Thi Minh Khai, Ho Chi Minh', '0909678901', 'TPHCM'),
('LW', 'Laptop World', ' 103/16 Nguyen Hong Đao, P. 14, Q. Tan Binh, Ho Chi Minh', '0903099138', 'TPHCM'),
('HCMC', 'TP.HCM Computer', '299 Minh Khai - Tu Son', '19001903', 'TPHCM'),
('AKC', 'An Khang Computer', '255 Quan Tan Binh, Thanh Pho Ho Chi Minh', '19001590', 'TPHCM');

SELECT * FROM CuaHang

-- dữ liệu table KhachHang
INSERT INTO KhachHang (MaKhachHang, TenKhachHang, Email, SoDienThoai, MaTP) VALUES
('KH01', 'Nguyen Quoc Khanh', 'kn03082003@gmail.com', '0935076617', 'TPDN'),
('KH02', 'Nguyen Duc Hao', 'haond21ad@gmail.com', '0912345679', 'TPDN'),
('KH03', 'Nguyen Van Đai', 'dainv21ad@gmail.com', '0912345680', 'TPDN'),
('KH04', 'Le Huu Đat', 'datlh21ad@yahoo.com', '0912345681', 'TPDN'),
('KH05', 'Hoang Anh Dung', 'dunghoang@11yahoo.com', '0912345682', 'TPDN'),
('KH06', 'Nguyen Thi Huong', 'huongnguyen@2120gmail.com', '0912345683', 'TPDN'),
('KH07', 'Tran Van Tung', 'tungtran@443gmail.com', '0912345684', 'TPDN'),
('KH08', 'Le Thi Hong', 'hongle@321312gmail.com', '0912345685', 'TPDN'),
('KH09', 'Pham Van Cuong', 'cuongpham@12312yahoo.com', '0912345686', 'TPDN'),
('KH10', 'Nguyen Thi Lan', 'lannguyen@333gmail.com', '0912345687', 'TPDN'),
('KH11', 'Tran Quoc Bao', 'baoquoc@55gmail.com', '0912345688','TPHCM'),
('KH12', 'Le Thi Phuong', 'phuongle@yahoo.com', '0912345689','TPHCM'),
('KH13', 'Pham Van Hung', 'hungpham@gmail.com', '0912345690','TPHCM'),
('KH14', 'Nguyen Thi Nhung', 'nhungnguyen@gmail.com', '0912345691','TPHCM'),
('KH15', 'Hoang Van Kien', 'kienhoang@yahoo.com', '0912345692','TPHCM'),
('KH16', 'Tran Thi Thu', 'thutran@gmail.com', '0912345693','TPHCM'),
('KH17', 'Le Van Minh', 'minhle@gmail.com', '0912345694','TPHCM'),
('KH18', 'Pham Thi Hoa', 'hoapham@yahoo.com', '0912345695','TPHCM'),
('KH19', 'Nguyen Van Phu', 'phunguyen@gmail.com', '0912345696','TPHCM'),
('KH20', 'Tran Thi Tuyet', 'tuyettran@yahoo.com', '0912345697','TPHCM'),
('KH21', 'Le Quoc Tuan', 'tuanle@gmail.com', '0912345698','TPH'),
('KH22', 'Pham Thi Lien', 'lienpham@gmail.com', '0912345699','TPH'),
('KH23', 'Nguyen Van Son', 'sonnguyen@yahoo.com', '0912345700','TPH'),
('KH24', 'Tran Thi Hoa', 'hoatran@456gmail.com', '0912345701','TPH'),
('KH25', 'Le Thi Dung', 'dungle@123gmail.com', '0912345702','TPH'),
('KH26', 'Pham Quoc Nam', 'nampham@0308yahoo.com', '0912345703','TPH'),
('KH27', 'Nguyen Thi Minh', 'minhnguyen@156gmail.com', '0912345704','TPH'),
('KH28', 'Tran Van Long', 'longtran@yahoo.com', '0912345705','TPH'),
('KH29', 'Le Thi Thao', 'thaole@888gmail.com', '0912345706','TPH'),
('KH30', 'Pham Van Khanh', 'khanhpham@yahoo.com', '0912345707','TPH');

SELECT * FROM KhachHang 

-- dữ liệu table Laptop
-- Cửa hàng ở Đà Nẵng
INSERT INTO Laptop (MaLaptop, TenLaptop, HangSX, CauHinh, KichThuocManHinh, TrongLuong, Gia, MoTa, MaCuaHang) VALUES
('LT01', 'Dell Inspiron ', 'Dell', 'Intel i5, 8GB RAM, 256GB SSD', '15.6 inch', '1.5 kg', 15000000, '', 'FPTDN'),
('LT02', 'Asus ROG Flow X14 GV302XA-X13.R9512', 'Asus', 'Intel i7, 16GB RAM, 512GB SSD', '15 inch', '1.8 kg', 18000000, '', 'KLDN'),
('LT03', 'HP 245 G10', 'HP', 'Intel i3, 4GB RAM, 128GB SSD', '14 inch', '1.4 kg', 12000000, '', 'L88'),
('LT04', 'Acer Swift Lite 14 AI', 'Acer', 'Intel i5, 8GB RAM, 256GB SSD', '14 inch', '1.5 kg', 14000000, '', 'MET'),
('LT05', 'Macbook Pro M3 14 2024', 'Apple', 'M1 Chip, 8GB RAM, 256GB SSD', '13 inch', '1.2 kg', 48000000, '', 'PLP'),
('LT06', 'Dell Inspiron 16 Plus', 'Dell', 'Intel i5, 8GB RAM, 512GB SSD', '15.6 inch', '1.7 kg', 16000000, '', 'PVDN'),
('LT07', 'MSI Gaming Thin A15 B7UC-261VN R5', 'MSI', 'Intel i7, 16GB RAM, 1TB SSD', '17 inch', '2.2 kg', 30000000, '', 'PVDN'),
('LT08', 'Lenovo ThinkBook 16 G6 IRL', 'Lenovo', 'Intel i3, 4GB RAM, 128GB SSD', '14 inch', '1.6 kg', 18000000, '', 'FPTDN'),
('LT09', 'Dell Gaming', 'Dell', 'Intel i7, 16GB RAM, 512GB SSD', '15 inch', '1.8 kg', 22000000, '', 'MET'),
('LT10', 'Asus ROG Flow X12', 'Asus', 'Intel i5, 8GB RAM, 512GB SSD', '14 inch', '1.5 kg', 17000000, '', 'KLDN');

-- Cửa hàng ở Hồ Chí Minh
INSERT INTO Laptop (MaLaptop, TenLaptop, HangSX, CauHinh, KichThuocManHinh, TrongLuong, Gia, MoTa, MaCuaHang) VALUES
('LT11', 'HP 240 G9 9E5W3PT', 'HP', 'Intel i5, 8GB RAM, 256GB SSD', '15 inch', '1.6 kg', 15000000, '', 'AKC'),
('LT12', 'Asus Zenbook 14 OLED', 'Asus', 'Intel i7, 16GB RAM, 512GB SSD', '14 inch', '1.4 kg', 20000000, '', 'KLHCM'),
('LT13', 'Dell Inspiron 3511', 'Dell', 'Intel i3, 4GB RAM, 128GB SSD', '14 inch', '1.3 kg', 11000000, '', 'LVN'),
('LT14', 'Gaming Acer Nitro 5 Tiger AN515 58 52SP', 'Acer', 'Intel i7, 8GB RAM, 256GB SSD', '15 inch', '1.5 kg', 17000000, '', 'KAHCM'),
('LT15', 'MacBook Air M3 13 inch 2024', 'Apple', 'M1 Chip, 8GB RAM, 512GB SSD', '13 inch', '1.2 kg', 32000000, '', 'LW'),
('LT16', 'Lenovo Ideapad Slim 5 14IAH8 83BF002NVN', 'Lenovo', 'Intel i5, 8GB RAM, 256GB SSD', '14 inch', '1.4 kg', 14000000, '', 'AKC'),
('LT17', 'MSI Prestige 15 A11SCX-209VN', 'MSI', 'Intel i9, 16GB RAM, 1TB SSD', '17 inch', '2.3 kg', 35000000, '', 'KAHCM'),
('LT18', 'HP Pavilion 15-EG2083TU 7C0W9PA', 'HP', 'Intel i3, 4GB RAM, 128GB SSD', '15 inch', '1.6 kg', 12000000, '', 'HCMC'),
('LT19', 'Dell Gaming G15 ', 'Dell', 'Intel i7, 16GB RAM, 512GB SSD', '15 inch', '1.7 kg', 21000000, '', 'LW'),
('LT20', 'Asus Vivobook 15 OLED', 'Asus', 'Intel i5, 8GB RAM, 512GB SSD', '15 inch', '1.6 kg', 18000000, '', 'LVN');

-- Cửa hàng ở Huế 
INSERT INTO Laptop (MaLaptop, TenLaptop, HangSX, CauHinh, KichThuocManHinh, TrongLuong, Gia, MoTa, MaCuaHang) VALUES
('LT21', 'MacBook Pro 14 M3', 'Apple', 'M1 Chip, 8GB RAM, 256GB SSD', '13 inch', '1.1 kg', 30000000, '', 'FPTH'),
('LT22', 'Dell Inspiron 3530', 'Dell', 'Intel i5, 8GB RAM, 256GB SSD', '15 inch', '1.6 kg', 16000000, '', 'PA'),
('LT23', 'HP Envy X360 15M-ES1013', 'HP', 'Intel i3, 4GB RAM, 128GB SSD', '14 inch', '1.4 kg', 13000000, '', 'PLT'),
('LT24', 'Acer Gaming Aspire 7 A715-76-53PJ', 'Acer', 'Intel i7, 16GB RAM, 512GB SSD', '14 inch', '1.4 kg', 19000000, '', 'TGDDH'),
('LT25', 'Asus Vivobook X1504ZA-NJ1039W', 'Asus', 'Intel i5, 8GB RAM, 256GB SSD', '15.6 inch', '1.6 kg', 14000000, '', 'VTS'),
('LT26', 'MSI Gaming Sword 16 HX B14VFKG-460VN', 'MSI', 'Intel i9, 32GB RAM, 1TB SSD', '17 inch', '2.4 kg', 37000000, '', 'TGDDH'),
('LT27', 'MacBook Pro 14 inch M3 2023', 'Apple', 'M1 Pro Chip, 16GB RAM, 512GB SSD', '14 inch', '1.3 kg', 40000000, '', 'FPTH'),
('LT28', 'HP Pavilion 14-DV2073TU 7C0P2PA', 'HP', 'Intel i3, 4GB RAM, 128GB SSD', '15 inch', '1.7 kg', 12500000, '', 'PA'),
('LT29', 'Dell Latitude 3440', 'Dell', 'Intel i7, 16GB RAM, 512GB SSD', '15 inch', '1.9 kg', 23000000, '', 'VTS'),
('LT30', 'Asus Zenbook Q415MA', 'Asus', 'Intel i5, 8GB RAM, 512GB SSD', '15 inch', '1.6 kg', 17500000, '', 'TGDDH');

SELECT * FROM Laptop

-- dữ liệu table ThoiGian
INSERT INTO ThoiGian (MaThoiGian, Ngay, Thang, Nam) VALUES 
('2024-08-01', '01', '08', '2024'),
('2024-08-05', '05', '08', '2024'),
('2024-08-29', '29', '08', '2024'),
('2024-09-01', '01', '09', '2024'),
('2024-09-05', '05', '09', '2024'),
('2024-09-29', '29', '09', '2024'),
('2024-10-03', '03', '10', '2024'),
('2024-10-05', '05', '10', '2024'),
('2024-10-20', '20', '10', '2024'),
('2024-07-15', '15', '07', '2024'),
('2024-07-05', '05', '07', '2024'),
('2024-06-25', '25', '06', '2024'),
('2024-06-10', '10', '06', '2024'),
('2024-05-20', '20', '05', '2024'),
('2024-05-05', '05', '05', '2024'),
('2024-04-15', '15', '04', '2024'),
('2024-04-01', '01', '04', '2024'),
('2024-03-20', '20', '03', '2024'),
('2024-03-05', '05', '03', '2024');


SELECT * FROM ThoiGian


-- table DonHang
INSERT INTO DonHang (MaDonHang, MaLaptop, MaCuaHang, MaKhachHang, MaThoiGian, SoLuong, TongTien) VALUES 
('DH001', 'LT01', 'FPTDN', 'KH01', '2024-08-01', 2, 30000000),
('DH002', 'LT02', 'KLDN', 'KH02', '2024-08-05', 1, 18000000),
('DH003', 'LT03', 'L88', 'KH03', '2024-08-29', 3, 36000000),
('DH004', 'LT04', 'MET', 'KH04', '2024-09-01', 1, 14000000),
('DH005', 'LT05', 'PLP', 'KH05', '2024-09-05', 2, 96000000),
('DH006', 'LT06', 'PVDN', 'KH06', '2024-09-29', 4, 64000000),
('DH007', 'LT07', 'PVDN', 'KH07', '2024-10-03', 1, 30000000),
('DH008', 'LT08', 'FPTDN', 'KH08', '2024-10-05', 2, 36000000),
('DH009', 'LT09', 'MET', 'KH09', '2024-10-20', 3, 66000000),
('DH010', 'LT10', 'KLDN', 'KH10', '2024-10-20', 1, 17000000),
('DH011', 'LT11', 'AKC', 'KH11', '2024-05-20', 2, 30000000),
('DH012', 'LT12', 'KLHCM', 'KH12', '2024-10-20', 1, 20000000),
('DH013', 'LT13', 'LVN', 'KH13', '2024-10-03', 4, 44000000),
('DH014', 'LT14', 'KAHCM', 'KH14', '2024-10-03', 3, 51000000),
('DH015', 'LT15', 'LW', 'KH15', '2024-10-05', 1, 32000000),
('DH016', 'LT16', 'AKC', 'KH16', '2024-10-20', 2, 28000000),
('DH017', 'LT17', 'KAHCM', 'KH17', '2024-05-05', 1, 35000000),
('DH018', 'LT18', 'HCMC', 'KH18', '2024-09-05', 3, 36000000),
('DH019', 'LT19', 'LW', 'KH19', '2024-03-20', 2, 42000000),
('DH020', 'LT20', 'LVN', 'KH20', '2024-09-29', 1, 18000000),
('DH021', 'LT21', 'FPTH', 'KH21', '2024-07-15', 3, 90000000),
('DH022', 'LT22', 'PA', 'KH22', '2024-07-05', 2, 32000000),
('DH023', 'LT23', 'PLT', 'KH23', '2024-06-25', 1, 13000000),
('DH024', 'LT24', 'TGDDH', 'KH24', '2024-06-10', 4, 76000000),
('DH025', 'LT25', 'VTS', 'KH25', '2024-05-20', 3, 42000000),
('DH026', 'LT26', 'TGDDH', 'KH26', '2024-05-05', 2, 74000000),
('DH027', 'LT27', 'FPTH', 'KH27', '2024-04-15', 1, 40000000),
('DH028', 'LT28', 'PA', 'KH28', '2024-04-01', 4, 50000000),
('DH029', 'LT29', 'VTS', 'KH29', '2024-03-20', 2, 46000000),
('DH030', 'LT30', 'TGDDH', 'KH30', '2024-03-20', 3, 52500000),
('DH031', 'LT18', 'HCMC', 'KH11', '2024-03-05', 1, 12000000),
('DH032', 'LT01', 'FPTDN', 'KH08', '2024-10-20', 1, 15000000),
('DH033', 'LT02', 'KLDN', 'KH09', '2024-10-05', 1, 18000000),
('DH034', 'LT03', 'L88', 'KH10', '2024-10-03', 1, 12000000),
('DH035', 'LT04', 'MET', 'KH05', '2024-09-29', 1, 14000000),
('DH036', 'LT05', 'PLP', 'KH06', '2024-09-05', 1, 48000000),
('DH037', 'LT06', 'PVDN', 'KH07', '2024-09-01', 1, 16000000),
('DH038', 'LT07', 'PVDN', 'KH02', '2024-08-29', 1, 30000000),
('DH039', 'LT08', 'FPTDN', 'KH01', '2024-08-05', 1, 18000000),
('DH040', 'LT09', 'MET', 'KH03', '2024-08-01', 1, 22000000),
('DH041', 'LT10', 'KLDN', 'KH04', '2024-07-15', 1, 17000000),
('DH042', 'LT11', 'AKC', 'KH12', '2024-07-05', 1, 15000000),
('DH043', 'LT12', 'KLHCM', 'KH13', '2024-06-25', 1, 20000000),
('DH044', 'LT13', 'LVN', 'KH14', '2024-06-10', 1, 11000000),
('DH045', 'LT14', 'KAHCM', 'KH15', '2024-05-20', 2, 34000000),
('DH046', 'LT15', 'LW', 'KH16', '2024-05-05', 3, 96000000),
('DH047', 'LT16', 'AKC', 'KH17', '2024-04-15', 4, 56000000),
('DH048', 'LT17', 'KAHCM', 'KH18', '2024-04-01', 5, 175000000),
('DH049', 'LT18', 'HCMC', 'KH19', '2024-03-20', 2, 24000000),
('DH050', 'LT30', 'TGDDH', 'KH22', '2024-03-05', 3, 52500000);

SELECT * FROM DonHang

-- table DoanhThu
INSERT INTO DoanhThu (MaDonHang, MaThoiGian, TongTien) VALUES
('DH001', '2024-08-01', 30000000),
('DH002', '2024-08-05', 18000000),
('DH003', '2024-08-29', 36000000),
('DH004', '2024-09-01', 14000000),
('DH005', '2024-09-05', 96000000),
('DH006', '2024-09-29', 64000000),
('DH007', '2024-10-03', 30000000),
('DH008', '2024-10-05', 36000000),
('DH009', '2024-10-20', 66000000),
('DH010', '2024-10-20', 17000000),
('DH011', '2024-05-20', 30000000),
('DH012', '2024-10-20', 20000000),
('DH013', '2024-10-03', 44000000),
('DH014', '2024-10-03', 51000000),
('DH015', '2024-10-05', 32000000),
('DH016', '2024-10-20', 28000000),
('DH017', '2024-05-05', 35000000),
('DH018', '2024-09-05', 36000000),
('DH019', '2024-03-20', 42000000),
('DH020', '2024-09-29', 18000000),
('DH021', '2024-07-15', 90000000),
('DH022', '2024-07-05', 32000000),
('DH023', '2024-06-25', 13000000),
('DH024', '2024-06-10', 76000000),
('DH025', '2024-05-20', 42000000),
('DH026', '2024-05-05', 74000000),
('DH027', '2024-04-15', 40000000),
('DH028', '2024-04-01', 50000000),
('DH029', '2024-03-20', 46000000),
('DH030', '2024-03-20', 52500000),
('DH031', '2024-03-05', 12000000),
('DH032', '2024-10-20', 15000000),
('DH033', '2024-10-05', 18000000),
('DH034', '2024-10-03', 12000000),
('DH035', '2024-09-29', 14000000),
('DH036', '2024-09-05', 48000000),
('DH037', '2024-09-01', 16000000),
('DH038', '2024-08-29', 30000000),
('DH039', '2024-08-05', 18000000),
('DH040', '2024-08-01', 22000000),
('DH041', '2024-07-15', 17000000),
('DH042', '2024-07-05', 15000000),
('DH043', '2024-06-25', 20000000),
('DH044', '2024-06-10', 11000000),
('DH045', '2024-05-20', 34000000),
('DH046', '2024-05-05', 96000000),
('DH047', '2024-04-15', 56000000),
('DH048', '2024-04-01', 175000000),
('DH049', '2024-03-20', 24000000),
('DH050', '2024-03-05', 52500000);

SELECT * FROM DoanhThu


SELECT HangSX, SUM(SoLuong) AS TongSoLuong
FROM DonHang
JOIN Laptop ON DonHang.MaLaptop = Laptop.MaLaptop
GROUP BY HangSX;


