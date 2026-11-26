import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ClipboardCheck from 'lucide-react/dist/esm/icons/clipboard-check.mjs'
import FileCheck2 from 'lucide-react/dist/esm/icons/file-check-2.mjs'
import FileClock from 'lucide-react/dist/esm/icons/file-clock.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import Mail from 'lucide-react/dist/esm/icons/mail.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { LichHopCaNhan } from '../pages/thuky/LichHopThuKy.jsx'
import DanhSachDeXuatThuKy from '../pages/thuky/DanhSachDeXuatThuKy.jsx'
import { HoSoChoDuyet, VanBanChoPhatHanh } from '../pages/pheduyet/HoSoVanBan.jsx'
import TaoBieuQuyet from '../pages/pheduyet/TaoBieuQuyet.jsx'
import PhanHoiLoiMoi from '../pages/thamdu/PhanHoiLoiMoi.jsx'
import BieuQuyetThamDu from '../pages/thamdu/BieuQuyetThamDu.jsx'
import VanBanDaPhatHanh from '../pages/thamdu/VanBanDaPhatHanh.jsx'
import DeXuatCuaToi from '../pages/dexuat/DeXuatCuaToi.jsx'

import NhomLienPhong from '../pages/nhom/NhomLienPhong.jsx'

const MUC_NHOM = { id: 'nhom-lien-phong', ten: 'Nhóm liên phòng', Trang: NhomLienPhong, BieuTuong: Users }

const MUC_BAN_GIAO = { id: 'cuoc-hop-duoc-giao', ten: 'Cuộc họp được giao', Trang: DanhSachDeXuatThuKy, BieuTuong: ClipboardCheck }

const MUC_CHUYEN_CAP = { id: 'ho-so-chuyen-cap', ten: 'Hồ sơ chờ chuyển cấp', Trang: DanhSachDeXuatThuKy, BieuTuong: FileClock }

const MUC_DE_XUAT_DUOC_GIAO = { id: 'de-xuat', ten: 'Đề xuất được giao', Trang: DanhSachDeXuatThuKy, BieuTuong: FileText }

const MUC_RA_SOAT = { id: 'ho-so-ra-soat', ten: 'Hồ sơ chờ rà soát', Trang: DanhSachDeXuatThuKy, BieuTuong: FileClock }

const MUC_THU_KY = [
  MUC_RA_SOAT,
  { id: 'phan-hoi-ke-hoach', ten: 'Phản hồi kế hoạch', Trang: DanhSachDeXuatThuKy, BieuTuong: Mail },
  { ...MUC_DE_XUAT_DUOC_GIAO, ten: 'Đề xuất cuộc họp' },
]

const MUC_VAN_BAN_CHU_TRI = [
  { id: 'ho-so-cho-duyet', ten: 'Hồ sơ chờ duyệt', Trang: HoSoChoDuyet, BieuTuong: FileClock },
  { id: 'van-ban-cho-phat-hanh', ten: 'Văn bản chờ phát hành', Trang: VanBanChoPhatHanh, BieuTuong: FileCheck2 },
]

const MUC_TAO_BIEU_QUYET = { id: 'tao-bieu-quyet', ten: 'Tạo biểu quyết', Trang: TaoBieuQuyet, BieuTuong: Vote }

const MUC_LICH_HOP = { id: 'lich-hop', ten: 'Lịch họp', Trang: LichHopCaNhan, BieuTuong: CalendarDays }

const MUC_THAM_DU = [
  { id: 'loi-moi', ten: 'Lời mời họp', Trang: PhanHoiLoiMoi, BieuTuong: Mail },
  { id: 'tham-gia-bieu-quyet', ten: 'Biểu quyết và kết quả', Trang: BieuQuyetThamDu, BieuTuong: Vote },
  { id: 'van-ban-da-phat-hanh', ten: 'Văn bản đã phát hành', Trang: VanBanDaPhatHanh, BieuTuong: FileCheck2 },
]

export function layMenuKhongGianLamViec(maVaiTro, laQuanLy, laTruongVanPhong) {
  if (maVaiTro === 'THU_KY') {
    return [MUC_LICH_HOP, MUC_NHOM, MUC_BAN_GIAO, ...MUC_THU_KY, MUC_CHUYEN_CAP, ...MUC_VAN_BAN_CHU_TRI, ...MUC_THAM_DU]
  } else if (laQuanLy) {
    return [
      MUC_LICH_HOP, MUC_NHOM,
      { id: 'de-xuat-cua-toi', ten: 'Đề xuất của tôi', Trang: DeXuatCuaToi, BieuTuong: FileText },
      ...(maVaiTro === 'CAN_BO' ? [MUC_BAN_GIAO, MUC_DE_XUAT_DUOC_GIAO] : []),
      ...(laTruongVanPhong ? [MUC_RA_SOAT] : []),
      MUC_CHUYEN_CAP, ...MUC_VAN_BAN_CHU_TRI,
      ...MUC_THAM_DU,
    ]
  } else if (maVaiTro === 'CAN_BO') {
    return [
      MUC_LICH_HOP, MUC_NHOM, MUC_BAN_GIAO, MUC_DE_XUAT_DUOC_GIAO,
      MUC_CHUYEN_CAP, ...MUC_VAN_BAN_CHU_TRI, MUC_TAO_BIEU_QUYET,
      ...MUC_THAM_DU,
    ]
  }

  return []
}
