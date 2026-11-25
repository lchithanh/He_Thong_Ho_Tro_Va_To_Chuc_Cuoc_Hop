import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import ListChecks from 'lucide-react/dist/esm/icons/list-checks.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import { TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import LienKetDeXuat from '../../components/LienKetDeXuat.jsx'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { layTrangThaiHienThiCuocHop } from '../../api/cuocHopApi.js'
import { TEN_PHAM_VI } from './hienThiDeXuat.js'
const TEN_BUOC = {
  LAP_KE_HOACH: 'Thư ký lập kế hoạch',
  CHO_TRUONG_PHONG: 'Chờ trưởng phòng duyệt',
  CHO_GUI_CAP_TREN: 'Thư ký ban đầu chọn lãnh đạo và chuyển hồ sơ',
  CHO_THU_KY_LANH_DAO: 'Chờ thư ký lãnh đạo tiếp nhận lần trình',
  THU_KY_LANH_DAO_XU_LY: 'Thư ký lãnh đạo đang rà soát',
  CHO_LANH_DAO: 'Chờ lãnh đạo duyệt',
  CHO_VAN_PHONG_DUYET: 'Chờ Văn phòng duyệt',
  THU_KY_TO_CHUC: 'Thư ký tổ chức cuộc họp',
  SAU_CUOC_HOP: 'Xử lý sau cuộc họp',
}

const THONG_BAO = {
  NHAP: 'Đề xuất đang được lưu dưới dạng bản nháp.',
  CHO_TIEP_NHAN: 'Đề xuất đang chờ thư ký tiếp nhận.',
  DANG_CHUAN_BI: 'Thư ký đang chuẩn bị kế hoạch cuộc họp.',
  CHO_GUI_CAP_TREN: 'Trưởng phòng đã đồng ý. Kế hoạch chưa được duyệt cuối; hồ sơ đang chờ thư ký ban đầu chuyển đến thư ký lãnh đạo.',
  CHO_DUYET: 'Hồ sơ đang trong quy trình phê duyệt.',
  TRA_LAI: 'Kế hoạch đã được trả lại để chỉnh sửa.',
  TU_CHOI: 'Hồ sơ đã bị từ chối.',
  DA_DUYET: 'Kế hoạch đã được duyệt. Xem lịch đi kèm trong mục Lịch họp.',
  DANG_DIEN_RA: 'Cuộc họp đang diễn ra.',
  DA_KET_THUC: 'Cuộc họp đã kết thúc.',
  DA_HUY: 'Cuộc họp đã được hủy.',
}

const HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }

function hienThiKhungGio(batDau, soPhut) {
  const mocBatDau = dinhDangNgayGio(batDau)
  if (!soPhut || !Number.isFinite(Date.parse(batDau))) return mocBatDau
  const ketThuc = new Date(Date.parse(batDau) + soPhut * 60_000)
  return `${mocBatDau} – ${dinhDangNgayGio(ketThuc)}`
}

function NhomThongTin({ Icon, ten, cacMuc }) {
  return (
    <section className="min-w-0 space-y-3 rounded-xl border border-slate-200 bg-white p-4">
      <TieuDeHoSo Icon={Icon} cap="h2">{ten}</TieuDeHoSo>
      <dl className="space-y-2">
        {cacMuc.map(([nhan, giaTri, laLienKet]) => (
          <div key={nhan} className="min-w-0 rounded-lg bg-slate-50 px-3 py-2">
            <dt className="text-xs font-semibold text-slate-600">{nhan}</dt>
            <dd className="mt-1 whitespace-pre-wrap wrap-anywhere text-sm text-slate-900">
              {laLienKet ? <LienKetDeXuat noiDung={giaTri} /> : giaTri || 'Chưa có thông tin'}
            </dd>
          </div>
        ))}
      </dl>
    </section>
  )
}

export default function ThongTinCuocHop({ chiTiet }) {
  const trangThai = layTrangThaiHienThiCuocHop(chiTiet)
  const buoc = chiTiet.buocXuLyHienTai
  const deXuat = chiTiet.thongTinDeXuat
  const khungGio = deXuat?.khungGioDeXuat ?? []
  const diaDiem = [
    ['Địa điểm đề xuất', deXuat?.diaDiemDeXuat],
    ['Nền tảng dự kiến', deXuat?.nenTangTrucTuyen],
    ['Liên kết tham gia dự kiến', deXuat?.lienKetTrucTuyen, true],
    ['Hướng dẫn tham gia', deXuat?.huongDanTrucTuyen],
  ].filter(([, noiDung]) => noiDung)

  return (
    <div className="space-y-4">
      {(THONG_BAO[trangThai] || TEN_BUOC[buoc]) && (
        <div className="rounded-xl border border-blue-100 bg-blue-50 p-4 text-sm">
          {THONG_BAO[trangThai] && <p className="font-semibold text-blue-950">{THONG_BAO[trangThai]}</p>}
          {TEN_BUOC[buoc] && <p className="mt-1 text-slate-700">Bước hiện tại · {TEN_BUOC[buoc]}</p>}
        </div>
      )}
      <div className="grid items-start gap-4 lg:grid-cols-2">
        <NhomThongTin Icon={FileText} ten="Nội dung đề xuất" cacMuc={[
          ['Mục đích', chiTiet.mucTieu],
          ['Kết quả kỳ vọng', deXuat?.ketQuaKyVong],
          ['Phạm vi', TEN_PHAM_VI[chiTiet.phamViCuocHop] || chiTiet.phamViCuocHop],
          ['Ngày tạo', chiTiet.ngayTao && dinhDangNgayGio(chiTiet.ngayTao)],
          ...(chiTiet.thoiGianChuyenCap ? [['Đã chuyển cấp', dinhDangNgayGio(chiTiet.thoiGianChuyenCap)]] : []),
        ]} />
        <NhomThongTin Icon={CalendarDays} ten="Thời gian và địa điểm dự kiến" cacMuc={[
          ['Hình thức', HINH_THUC[chiTiet.loaiHinh] || chiTiet.loaiHinh],
          ['Thời lượng dự kiến', chiTiet.thoiLuongDuKienPhut && `${chiTiet.thoiLuongDuKienPhut} phút`],
          ...(khungGio.length ? khungGio.map((batDau, i) => [
            i === 0 ? 'Khung giờ ưu tiên' : `Khung giờ phương án ${i + 1}`,
            hienThiKhungGio(batDau, chiTiet.thoiLuongDuKienPhut),
          ]) : [['Khung giờ dự kiến', 'Chưa có khung giờ đề xuất']]),
          ...diaDiem,
        ]} />
        <NhomThongTin Icon={Users} ten="Thành phần và phụ trách" cacMuc={[
          ['Người đề xuất', chiTiet.tenNguoiDeXuat],
          ['Phòng ban chủ trì', chiTiet.tenPhongBanChuTri],
          ['Chủ trì', chiTiet.tenNguoiChuTri || chiTiet.tenChuTri || 'Chưa phân công'],
          ['Thư ký cuộc họp', chiTiet.tenThuKyCuocHop || 'Chưa phân công'],
          ['Bắt buộc tham dự', deXuat?.thanhPhanBatBuoc],
          ['Tham dự để nắm thông tin', deXuat?.thanhPhanTuyChon],
          ['Khách mời bên ngoài', chiTiet.coKhachMoiBenNgoai == null ? null : chiTiet.coKhachMoiBenNgoai ? 'Có' : 'Không'],
        ]} />
        <NhomThongTin Icon={ListChecks} ten="Chương trình và chuẩn bị" cacMuc={[
          ['Chương trình dự kiến', chiTiet.noiDungDuKien],
          ['Yêu cầu chuẩn bị trước', deXuat?.yeuCauChuanBi],
          ...(deXuat?.lienKetTaiLieu ? [['Liên kết tài liệu', deXuat.lienKetTaiLieu, true]] : []),
        ]} />
      </div>
    </div>
  )
}
