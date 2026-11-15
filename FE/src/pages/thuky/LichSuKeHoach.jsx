import { useEffect, useState } from 'react'
import { layLichSuKeHoach } from '../../api/cuocHopApi.js'

const TEN_CAP = {
  TRUONG_PHONG: 'Cấp 1 · Trưởng phòng',
  LANH_DAO: 'Cấp cuối · Lãnh đạo',
}

const TEN_HANH_DONG = {
  TIEP_NHAN: 'Tiếp nhận hồ sơ',
  PHE_DUYET: 'Đồng ý',
  TU_CHOI: 'Từ chối',
  YEU_CAU_CHINH_SUA: 'Yêu cầu chỉnh sửa',
  TU_CHOI_TIEP_NHAN: 'Từ chối tiếp nhận',
  TRINH_LANH_DAO: 'Chuyển lãnh đạo duyệt',
  DONG_Y_TRINH_CAP_TREN: 'Đồng ý trình cấp trên',
  CHUYEN_CAP_TREN: 'Chuyển hồ sơ lên cấp trên',
}

const TEN_TRANG_THAI = {
  CHO_DUYET: 'Chờ duyệt',
  DA_DUYET: 'Đã duyệt',
  TRA_LAI: 'Cần chỉnh sửa',
  TU_CHOI: 'Bị từ chối',
  DA_HUY: 'Đã hủy',
}

function dinhDangThoiGian(thoiGian) {
  if (!thoiGian) return 'Chưa ghi nhận thời gian'
  const ngay = new Date(thoiGian)
  return Number.isNaN(ngay.getTime()) ? 'Chưa ghi nhận thời gian' : ngay.toLocaleString('vi-VN')
}

function tenHanhDong(ma) {
  return TEN_HANH_DONG[ma] || ma?.replaceAll('_', ' ') || 'Thao tác'
}

function tenTrangThai(ma) {
  return TEN_TRANG_THAI[ma] || ma?.replaceAll('_', ' ') || 'Chưa có trạng thái'
}

function LoTrinhLanTrinh({ lanTrinh, thuTu }) {
  const hanhDongs = [...(lanTrinh.hanhDongs || [])].sort((a, b) => (
    new Date(a.thoiGian || 0).getTime() - new Date(b.thoiGian || 0).getTime()
  ))

  return (
    <article className="rounded-xl border border-slate-200 bg-white p-4 sm:p-5">
      <header className="flex flex-wrap items-start justify-between gap-2">
        <div>
          <h3 className="font-bold text-slate-900">Lần trình {thuTu}</h3>
          <p className="mt-1 text-sm text-slate-600">
            {TEN_CAP[lanTrinh.capPheDuyet] || lanTrinh.capPheDuyet || 'Chưa xác định cấp duyệt'}
            {' · '}{dinhDangThoiGian(lanTrinh.thoiGianTrinh)}
          </p>
        </div>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
          {tenTrangThai(lanTrinh.trangThai)}
        </span>
      </header>

      {lanTrinh.yeuCauTruocId != null && (
        <p className="mt-3 text-xs text-slate-500">
          Liên kết lần trình trước · Yêu cầu #{lanTrinh.yeuCauTruocId}
        </p>
      )}
      {lanTrinh.ghiChuTrinh && (
        <p className="mt-3 whitespace-pre-wrap rounded-lg bg-slate-50 p-3 text-sm text-slate-700">
          <strong>Ghi chú khi trình:</strong> {lanTrinh.ghiChuTrinh}
        </p>
      )}

      {hanhDongs.length === 0 ? (
        <p className="mt-4 text-sm text-slate-500">Chưa có quyết định được ghi nhận cho lần trình này.</p>
      ) : (
        <ol className="mt-4 space-y-3 border-l border-slate-200 pl-4">
          {hanhDongs.map((hanhDong, index) => (
            <li key={`${hanhDong.nguoiThucHienId || 'unknown'}-${hanhDong.thoiGian || index}`} className="relative">
              <span className="absolute -left-[1.32rem] top-1.5 size-2 rounded-full bg-emerald-700" aria-hidden="true" />
              <p className="text-sm font-semibold text-slate-800">{tenHanhDong(hanhDong.hanhDong)}</p>
              <p className="mt-0.5 text-xs text-slate-500">
                {[hanhDong.hoTenNguoiThucHien, dinhDangThoiGian(hanhDong.thoiGian)].filter(Boolean).join(' · ')}
              </p>
              {hanhDong.yKien && (
                <p className="mt-2 whitespace-pre-wrap rounded-lg bg-slate-50 p-3 text-sm text-slate-700">
                  {hanhDong.yKien}
                </p>
              )}
            </li>
          ))}
        </ol>
      )}
    </article>
  )
}

export default function LichSuKeHoach({ cuocHopId, token }) {
  const [ketQua, setKetQua] = useState(null)
  const khoaYeuCau = `${cuocHopId}:${token}`

  useEffect(() => {
    const controller = new AbortController()

    layLichSuKeHoach(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setKetQua({ khoaYeuCau, lichSu: duLieu, loi: '' })
      })
      .catch((error) => {
        if (!controller.signal.aborted) setKetQua({ khoaYeuCau, lichSu: null, loi: error.message })
      })

    return () => controller.abort()
  }, [cuocHopId, khoaYeuCau, token])

  const ketQuaHienTai = ketQua?.khoaYeuCau === khoaYeuCau ? ketQua : null
  const lichSu = ketQuaHienTai?.lichSu ?? null
  const loi = ketQuaHienTai?.loi || ''

  return (
    <section aria-label="Lịch sử và phản hồi" className="space-y-4">
      <div>
        <h2 className="text-lg font-bold text-slate-900">Lịch sử và phản hồi</h2>
        <p className="mt-1 text-sm text-slate-600">
          Các lần trình và quyết định được giữ riêng để dễ đối chiếu.
        </p>
      </div>
      {loi ? (
        <p role="alert" className="rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-800">{loi}</p>
      ) : lichSu === null ? (
        <p role="status" className="text-sm text-slate-600">Đang tải lịch sử hồ sơ…</p>
      ) : lichSu.length === 0 ? (
        <p className="rounded-lg border border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">
          Chưa có lần trình kế hoạch nào.
        </p>
      ) : (
        <div className="space-y-3">
          {lichSu.map((lanTrinh, index) => (
            <LoTrinhLanTrinh
              key={lanTrinh.yeuCauPheDuyetId}
              lanTrinh={lanTrinh}
              thuTu={lichSu.length - index}
            />
          ))}
        </div>
      )}
    </section>
  )
}
