import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ClipboardCheck from 'lucide-react/dist/esm/icons/clipboard-check.mjs'
import FileCheck2 from 'lucide-react/dist/esm/icons/file-check-2.mjs'
import FileClock from 'lucide-react/dist/esm/icons/file-clock.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import Mail from 'lucide-react/dist/esm/icons/mail.mjs'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import Monitor from 'lucide-react/dist/esm/icons/monitor.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { LichHopCaNhan } from '../pages/thuky/LichHopThuKy.jsx'
import DanhSachDeXuatThuKy from '../pages/thuky/DanhSachDeXuatThuKy.jsx'
import KeHoachChoDuyet from '../pages/pheduyet/KeHoachChoDuyet.jsx'
import BienBanChoDuyet from '../pages/pheduyet/BienBanChoDuyet.jsx'
import KetLuanChoPhatHanh from '../pages/pheduyet/KetLuanChoPhatHanh.jsx'
import TaoBieuQuyet from '../pages/pheduyet/TaoBieuQuyet.jsx'
import PhanHoiLoiMoi from '../pages/thamdu/PhanHoiLoiMoi.jsx'
import BieuQuyetThamDu from '../pages/thamdu/BieuQuyetThamDu.jsx'
import KetLuanDaPhatHanh from '../pages/thamdu/KetLuanDaPhatHanh.jsx'
import DeXuatCuaToi from '../pages/dexuat/DeXuatCuaToi.jsx'
import PhanCongThuKy from '../pages/quantri/PhanCongThuKy.jsx'
import QuanLyPhongBan from '../pages/quantri/QuanLyPhongBan.jsx'
import QuanLyThietBi from '../pages/quantri/QuanLyThietBi.jsx'

const MUC_BAN_GIAO = { id: 'cuoc-hop-duoc-giao', ten: 'Cuộc họp được giao', Trang: DanhSachDeXuatThuKy, BieuTuong: ClipboardCheck }

const MUC_THU_KY = [
  { id: 'ho-so-ra-soat', ten: 'Hồ sơ chờ rà soát', Trang: DanhSachDeXuatThuKy, BieuTuong: FileClock },
  { id: 'phan-hoi-ke-hoach', ten: 'Phản hồi kế hoạch', Trang: DanhSachDeXuatThuKy, BieuTuong: Mail },
  { id: 'de-xuat', ten: 'Đề xuất cuộc họp', Trang: DanhSachDeXuatThuKy, BieuTuong: FileText },
]

const MUC_PHE_DUYET = [
  { id: 'ke-hoach-cho-duyet', ten: 'Kế hoạch chờ duyệt', Trang: KeHoachChoDuyet, BieuTuong: FileClock },
  { id: 'bien-ban-cho-duyet', ten: 'Biên bản chờ duyệt', Trang: BienBanChoDuyet, BieuTuong: ClipboardCheck },
  { id: 'ket-luan-cho-phat-hanh', ten: 'Kết luận chờ phát hành', Trang: KetLuanChoPhatHanh, BieuTuong: FileCheck2 },
  { id: 'tao-bieu-quyet', ten: 'Tạo biểu quyết', Trang: TaoBieuQuyet, BieuTuong: Vote },
]

const MUC_LICH_HOP = { id: 'lich-hop', ten: 'Lịch họp', Trang: LichHopCaNhan, BieuTuong: CalendarDays }

const MUC_THAM_DU = [
  { id: 'loi-moi', ten: 'Lời mời họp', Trang: PhanHoiLoiMoi, BieuTuong: Mail },
  { id: 'tham-gia-bieu-quyet', ten: 'Biểu quyết và kết quả', Trang: BieuQuyetThamDu, BieuTuong: Vote },
  { id: 'ket-luan-da-phat-hanh', ten: 'Kết luận đã phát hành', Trang: KetLuanDaPhatHanh, BieuTuong: FileCheck2 },
]

export function layMenuKhongGianLamViec(maVaiTro, laQuanLy) {
  if (maVaiTro === 'THU_KY') {
    return [MUC_LICH_HOP, MUC_BAN_GIAO, ...MUC_THU_KY, ...MUC_THAM_DU]
  } else if (laQuanLy) {
    return [
      MUC_LICH_HOP,
      { id: 'de-xuat-cua-toi', ten: 'Đề xuất của tôi', Trang: DeXuatCuaToi, BieuTuong: FileText },
      ...(maVaiTro === 'CAN_BO' ? [MUC_BAN_GIAO] : []),
      ...MUC_PHE_DUYET.filter(({ id }) => id !== 'tao-bieu-quyet'),
      ...MUC_THAM_DU,
    ]
  } else if (maVaiTro === 'CAN_BO') {
    return [
      MUC_LICH_HOP, MUC_BAN_GIAO,
      ...MUC_PHE_DUYET.filter(({ id }) => ['ket-luan-cho-phat-hanh', 'tao-bieu-quyet'].includes(id)),
      ...MUC_THAM_DU,
    ]
  } else if (maVaiTro === 'ADMIN') {
    return [
      { id: 'phong-ban', ten: 'Quản lý phòng ban', Trang: QuanLyPhongBan, BieuTuong: Building2 },
      { id: 'thiet-bi', ten: 'Quản lý thiết bị', Trang: QuanLyThietBi, BieuTuong: Monitor },
      { id: 'phan-cong-thu-ky', ten: 'Phân công thư ký', Trang: PhanCongThuKy, BieuTuong: Users },
    ]
  }

  return []
}
