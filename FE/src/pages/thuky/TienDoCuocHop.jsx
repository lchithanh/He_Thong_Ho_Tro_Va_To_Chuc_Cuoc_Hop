import { layTrangThaiHienThiCuocHop } from '../../api/cuocHopApi.js'

const CAC_BUOC = [
  { id: 'de-xuat', ten: 'Đề xuất' },
  { id: 'ke-hoach', ten: 'Lịch và kế hoạch' },
  { id: 'phe-duyet', ten: 'Phê duyệt' },
  { id: 'to-chuc', ten: 'Tổ chức' },
  { id: 'van-ban', ten: 'Văn bản sau họp' },
]

const BUOC_THEO_GIAI_DOAN = {
  NHAP: 0,
  CHO_TIEP_NHAN: 0,
  CHO_XAC_NHAN_LICH_DU_KIEN: 1,
  LAP_KE_HOACH: 1,
  CHO_TRUONG_PHONG: 2,
  CHO_GUI_CAP_TREN: 2,
  CHO_THU_KY_LANH_DAO: 2,
  THU_KY_LANH_DAO_XU_LY: 2,
  CHO_LANH_DAO: 2,
  THU_KY_TO_CHUC: 3,
  SAU_CUOC_HOP: 4,
}

const NOI_DUNG_BUOC = {
  NHAP: 'Đề xuất đang ở dạng bản nháp.',
  CHO_TIEP_NHAN: 'Đề xuất đang chờ thư ký tiếp nhận.',
  CHO_XAC_NHAN_LICH_DU_KIEN: 'Thư ký cần xác nhận lịch dự kiến trước khi lập kế hoạch.',
  LAP_KE_HOACH: 'Thư ký phụ trách đang hoàn thiện lịch và kế hoạch.',
  CHO_TRUONG_PHONG: 'Hồ sơ đang chờ Trưởng phòng xem xét.',
  CHO_GUI_CAP_TREN: 'Trưởng phòng đã đồng ý; hồ sơ đang chờ thư ký ban đầu chuyển cấp.',
  CHO_THU_KY_LANH_DAO: 'Hồ sơ đang chờ thư ký lãnh đạo tiếp nhận.',
  THU_KY_LANH_DAO_XU_LY: 'Thư ký lãnh đạo đang rà soát hồ sơ.',
  CHO_LANH_DAO: 'Hồ sơ đang chờ lãnh đạo phê duyệt.',
  THU_KY_TO_CHUC: 'Kế hoạch đã duyệt; thư ký được giao đang tổ chức cuộc họp.',
  SAU_CUOC_HOP: 'Cuộc họp đã kết thúc; hồ sơ đang được hoàn thiện.',
}

const TEN_VAI_TRO_XU_LY = {
  CHO_TIEP_NHAN: 'Thư ký phụ trách',
  CHO_XAC_NHAN_LICH_DU_KIEN: 'Thư ký phụ trách',
  LAP_KE_HOACH: 'Thư ký phụ trách',
  CHO_TRUONG_PHONG: 'Trưởng phòng',
  CHO_GUI_CAP_TREN: 'Thư ký ban đầu',
  CHO_THU_KY_LANH_DAO: 'Thư ký lãnh đạo được phân công',
  THU_KY_LANH_DAO_XU_LY: 'Thư ký lãnh đạo',
  CHO_LANH_DAO: 'Lãnh đạo được chọn',
  THU_KY_TO_CHUC: 'Thư ký được giao tổ chức',
  SAU_CUOC_HOP: 'Thư ký được giao tổ chức',
}

const BUOC_TU_TRANG_THAI = {
  TRA_LAI: 1,
  TU_CHOI: 2,
  DA_HUY: 3,
  DA_DUYET: 3,
  DANG_DIEN_RA: 3,
  DA_KET_THUC: 4,
}

const TRANG_THAI_DUNG = new Set(['TRA_LAI', 'TU_CHOI', 'DA_HUY'])

function layBuocHienTai(chiTiet, trangThai) {
  const buocTuQuyTrinh = BUOC_THEO_GIAI_DOAN[chiTiet.buocXuLyHienTai]
  if (buocTuQuyTrinh != null) return buocTuQuyTrinh
  return BUOC_TU_TRANG_THAI[trangThai] ?? 0
}

function layViecTiepTheo(chiTiet, trangThai) {
  if (chiTiet.duocLapKeHoach) {
    return trangThai === 'CHO_TIEP_NHAN'
      ? 'Bạn có thể tiếp nhận hồ sơ để bắt đầu lập kế hoạch.'
      : 'Bạn có thể tiếp tục hoàn thiện lịch và kế hoạch.'
  }
  if (chiTiet.yeuCauChoGuiCapTrenId != null) {
    return 'Bạn có thể chọn lãnh đạo và chuyển hồ sơ lên cấp trên.'
  }
  if (chiTiet.duocToChuc && trangThai === 'DA_DUYET') {
    return 'Bạn được giao tiếp nhận bàn giao và tổ chức cuộc họp.'
  }
  return null
}

export default function TienDoCuocHop({ chiTiet, viecTiepTheo }) {
  if (!chiTiet) return null

  const trangThai = layTrangThaiHienThiCuocHop(chiTiet)
  const buocHienTai = layBuocHienTai(chiTiet, trangThai)
  const daHoanTatHoSo = chiTiet.daHoanTatHoSo === true
  const noiDungBuoc = daHoanTatHoSo
    ? 'Hồ sơ đã hoàn tất. Biên bản và kết luận đã được phát hành.'
    : NOI_DUNG_BUOC[chiTiet.buocXuLyHienTai]
  const nguoiDangXuLy = chiTiet.tenNguoiDangXuLy
    || TEN_VAI_TRO_XU_LY[chiTiet.buocXuLyHienTai]
  const thongTinTiepTheo = viecTiepTheo || layViecTiepTheo(chiTiet, trangThai)
  const dungQuyTrinh = TRANG_THAI_DUNG.has(trangThai)

  return (
    <section
      aria-label="Tiến độ và người xử lý hồ sơ"
      className="space-y-4 rounded-xl border border-[#d9e2ec] bg-[#f0f4f8] p-4 sm:p-5"
    >
      <div className="flex flex-wrap items-start justify-between gap-2">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-[#486581]">
            Tiến độ hồ sơ{!daHoanTatHoSo && ` · Bước ${buocHienTai + 1}/${CAC_BUOC.length}`}
          </p>
          <p className="mt-1 text-sm font-bold text-[#102a43]" aria-live="polite">
            {noiDungBuoc || (trangThai === 'TRA_LAI'
              ? 'Kế hoạch cần chỉnh sửa trước khi trình lại.'
              : trangThai === 'TU_CHOI'
                ? 'Hồ sơ đã bị từ chối.'
                : trangThai === 'DA_HUY'
                  ? 'Cuộc họp đã bị hủy.'
                  : 'Chưa có thông tin bước xử lý.')}
          </p>
        </div>
        {!daHoanTatHoSo && nguoiDangXuLy && (
          <p className="rounded-full bg-white px-3 py-1 text-xs font-medium text-slate-700">
            Đang chờ: {nguoiDangXuLy}
          </p>
        )}
      </div>

      <ol className="grid grid-cols-2 gap-2 sm:grid-cols-5" aria-label="Các giai đoạn">
        {CAC_BUOC.map((buoc, index) => {
          const daHoanTat = daHoanTatHoSo || index < buocHienTai
          const dangThucHien = index === buocHienTai && !daHoanTat
          const mauSac = daHoanTat
            ? 'border-green-200 bg-green-50 text-green-900'
            : dangThucHien
              ? dungQuyTrinh
                ? 'border-amber-300 bg-amber-50 text-amber-950'
                : 'border-[#334e68] bg-[#243b53] text-white'
              : 'border-slate-200 bg-white text-slate-500'

          return (
            <li
              key={buoc.id}
              aria-current={dangThucHien ? 'step' : undefined}
              className={`flex min-h-12 items-center gap-2 rounded-lg border px-2 py-2 text-xs font-semibold ${mauSac}`}
            >
              <span aria-hidden="true" className="flex size-5 shrink-0 items-center justify-center rounded-full bg-current/10">
                {daHoanTat ? '✓' : index + 1}
              </span>
              <span>{buoc.ten}</span>
              {daHoanTat && <span className="sr-only"> (hoàn thành)</span>}
            </li>
          )
        })}
      </ol>

      {thongTinTiepTheo && (
        <p className="rounded-lg border border-emerald-200 bg-white px-3 py-2 text-sm font-medium text-emerald-900">
          {thongTinTiepTheo}
        </p>
      )}
    </section>
  )
}
