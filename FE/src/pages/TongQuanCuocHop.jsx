import { dinhDangNgayGio } from '../utils/thoiGianCuocHop.js'
import { useEffect, useMemo, useState } from 'react'
import ArrowUpRight from 'lucide-react/dist/esm/icons/arrow-up-right.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import CheckCircle2 from 'lucide-react/dist/esm/icons/check-circle-2.mjs'
import CircleDot from 'lucide-react/dist/esm/icons/circle-dot.mjs'
import ViecChoXuLy from './ViecChoXuLy.jsx'
import { useDongBoDuLieu } from '../services/useDongBoDuLieu.js'
import { layDanhSachDeXuat, layTrangThaiHienThiCuocHop } from '../api/cuocHopApi.js'

const TRANG_THAI = {
  NHAP: 'Bản nháp',
  CHO_TIEP_NHAN: 'Chờ tiếp nhận',
  DANG_CHUAN_BI: 'Đang chuẩn bị',
  CHO_DUYET: 'Chờ duyệt',
  CHO_GUI_CAP_TREN: 'Chờ gửi cấp trên',
  DA_DUYET: 'Đã duyệt',
  DANG_DIEN_RA: 'Đang diễn ra',
  DA_KET_THUC: 'Đã kết thúc',
  TRA_LAI: 'Đã trả lại',
  TU_CHOI: 'Đã từ chối',
  DA_HUY: 'Đã hủy',
}

const THE_THONG_KE = [
  { ten: 'Tổng số hồ sơ', ma: '', BieuTuong: CalendarDays, mau: 'bg-sky-50 phong-toi:bg-phong-nhan-xanh text-sky-700 phong-toi:text-phong-xanh' },
  { ten: 'Đang diễn ra', ma: 'DANG_DIEN_RA', BieuTuong: CircleDot, mau: 'bg-rose-50 phong-toi:bg-phong-nhan-do text-rose-700 phong-toi:text-phong-do' },
  { ten: 'Đã duyệt', ma: 'DA_DUYET', BieuTuong: CheckCircle2, mau: 'bg-green-50 phong-toi:bg-phong-nhan-luc text-green-700 phong-toi:text-phong-luc' },
  { ten: 'Đã kết thúc', ma: 'DA_KET_THUC', BieuTuong: CalendarDays, mau: 'bg-slate-100 phong-toi:bg-phong-mat-noi text-slate-600 phong-toi:text-phong-chu-phu' },
]

const THE_NHAN_TRANG_THAI = {
  CHO_TIEP_NHAN: 'bg-amber-50 phong-toi:bg-phong-nhan-vang text-amber-800 phong-toi:text-phong-vang',
  DANG_CHUAN_BI: 'bg-sky-50 phong-toi:bg-phong-nhan-xanh text-sky-800 phong-toi:text-phong-xanh',
  CHO_DUYET: 'bg-violet-50 phong-toi:bg-phong-nhan-tim text-violet-800 phong-toi:text-phong-tim',
  CHO_GUI_CAP_TREN: 'bg-violet-50 phong-toi:bg-phong-nhan-tim text-violet-800 phong-toi:text-phong-tim',
  DA_DUYET: 'bg-green-50 phong-toi:bg-phong-nhan-luc text-green-800 phong-toi:text-phong-luc',
  DANG_DIEN_RA: 'bg-rose-50 phong-toi:bg-phong-nhan-do text-rose-800 phong-toi:text-phong-do',
  DA_KET_THUC: 'bg-slate-100 phong-toi:bg-phong-mat-noi text-slate-700 phong-toi:text-phong-chu-phu',
  TRA_LAI: 'bg-amber-50 phong-toi:bg-phong-nhan-vang text-amber-900 phong-toi:text-phong-vang',
  TU_CHOI: 'bg-red-50 phong-toi:bg-phong-nhan-do text-red-800 phong-toi:text-phong-do',
  DA_HUY: 'bg-red-50 phong-toi:bg-phong-nhan-do text-red-800 phong-toi:text-phong-do',
}

export default function TongQuanCuocHop({
  token,
  coTheMoDanhSach,
  onSoanVanBan,
  onMoDanhSach,
  onXemCuocHop,
  cacMucXuLy,
  coQuyenDuyetKeHoach,
  onMoMuc,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [xemTatCa, setXemTatCa] = useState(false)
  const [locTrangThai, setLocTrangThai] = useState('')
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setDanhSach(duLieu)
          setLoi('')
        }
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token, phienDongBo])

  const thongKe = useMemo(() => {
    const cacCuocHop = danhSach ?? []
    return THE_THONG_KE.map((muc) => ({
      ...muc,
      soLuong: muc.ma
        ? cacCuocHop.filter((hop) => layTrangThaiHienThiCuocHop(hop) === muc.ma).length
        : cacCuocHop.length,
    }))
  }, [danhSach])

  const danhSachDaLoc = useMemo(
    () => (danhSach ?? [])
      .filter((hop) => !locTrangThai || layTrangThaiHienThiCuocHop(hop) === locTrangThai)
      .sort((a, b) => new Date(b.ngayTao || 0) - new Date(a.ngayTao || 0)),
    [danhSach, locTrangThai],
  )
  const ganDay = xemTatCa ? danhSachDaLoc : danhSachDaLoc.slice(0, 5)

  function chonTrangThai(trangThai) {
    setLocTrangThai(trangThai)
    setXemTatCa(false)
  }

  return (
    <div className="space-y-6">
      {danhSach?.some((hop) => hop.trangThai === 'DA_KET_THUC' && hop.duocToChuc && !hop.daHoanTatHoSo) && (
        <section className="rounded-xl border border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat p-5">
          <h2 className="font-bold">Văn bản sau cuộc họp</h2>
          <ul className="mt-3 divide-y divide-slate-100 phong-toi:divide-phong-vien">
            {danhSach.filter((hop) => hop.trangThai === 'DA_KET_THUC' && hop.duocToChuc && !hop.daHoanTatHoSo).map((hop) => (
              <li key={hop.cuocHopId} className="flex flex-wrap items-center justify-between gap-3 py-3">
                <div>
                  <p className="font-semibold">{hop.tieuDe}</p>
                  <p className="text-sm text-slate-600 phong-toi:text-phong-chu-phu">Kết thúc · {dinhDangNgayGio(hop.thoiGianKetThucThucTe)}</p>
                </div>
                <div className="flex gap-2">
                  {['bien-ban', 'ket-luan'].map((tab) => (
                    <button type="button" key={tab} className="rounded-lg border border-slate-200 phong-toi:border-phong-vien px-3 py-2 text-sm font-semibold text-blue-900 phong-toi:text-phong-xanh hover:bg-blue-50 phong-toi:hover:bg-phong-mat-noi"
                      onClick={() => onSoanVanBan(hop.cuocHopId, tab)}>
                      {tab === 'bien-ban' ? 'Mở biên bản' : 'Mở kết luận'}
                    </button>
                  ))}
                </div>
              </li>
            ))}
          </ul>
        </section>
      )}

      <section aria-label="Thống kê trạng thái cuộc họp">
        <div className="mb-3 flex items-center justify-between gap-3">
          <div>
            <h2 className="text-base font-bold text-slate-900 phong-toi:text-phong-chu">Tình hình cuộc họp</h2>
            <p className="mt-1 text-sm text-slate-500 phong-toi:text-phong-chu-phu">
              {danhSach ? `${danhSach.length} hồ sơ trong danh sách của bạn` : 'Đang đồng bộ dữ liệu'}
            </p>
          </div>
        </div>

        {loi && (
          <p role="alert" className="mb-4 rounded-xl border border-red-200 phong-toi:border-phong-vien-do bg-red-50 phong-toi:bg-phong-nhan-do p-4 text-sm text-red-800 phong-toi:text-phong-do">
            {loi}
          </p>
        )}

        <div className="grid gap-3 grid-cols-2 sm:grid-cols-3 xl:grid-cols-4">
          {thongKe.map(({ ten, ma, soLuong, BieuTuong, mau }) => (
            <button key={ma} type="button" aria-pressed={locTrangThai === ma}
              className="rounded-2xl phong-toi:hover:border-[#86adf4] phong-toi:aria-pressed:border-[#86adf4] phong-toi:aria-pressed:bg-[#1e3355] border border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat p-4 text-left"
              onClick={() => chonTrangThai(ma)}>
              <div className="flex flex-wrap items-start justify-between gap-3">
                <span className={`grid size-10 place-items-center rounded-xl ${mau}`}>
                  <BieuTuong size={19} aria-hidden="true" />
                </span>
                <span className="text-2xl font-bold tabular-nums">{danhSach === null ? '—' : soLuong}</span>
              </div>
              <span className="mt-3 block text-sm font-semibold text-slate-700 phong-toi:text-phong-chu-phu">{ten}</span>
            </button>
          ))}
        </div>
      </section>

      <section className="overflow-hidden rounded-2xl border border-slate-200/80 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat shadow-sm shadow-slate-900/[0.02]">
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 phong-toi:border-phong-vien px-5 py-4 sm:px-6">
          <div>
            <h2 className="font-bold text-slate-900 phong-toi:text-phong-chu">{xemTatCa ? 'Danh sách cuộc họp' : 'Cuộc họp gần đây'}</h2>

          </div>
          <label className="flex flex-wrap items-center gap-2 text-sm text-slate-600 phong-toi:text-phong-chu-phu">
            Trạng thái
            <select value={locTrangThai} onChange={(event) => chonTrangThai(event.target.value)}
              className="rounded-lg border border-slate-300 px-3 py-2 phong-toi:truong-phong-hop">
              <option value="">Tất cả</option>
              {Object.entries(TRANG_THAI).map(([ma, ten]) => <option key={ma} value={ma}>{ten}</option>)}
            </select>
          </label>
          {danhSachDaLoc.length > 5 && (
            <button type="button" onClick={() => setXemTatCa((hienTai) => !hienTai)}
              className="rounded-lg px-3 py-2 text-sm font-semibold text-emerald-800 phong-toi:text-phong-xanh hover:bg-emerald-50 phong-toi:hover:bg-phong-mat-noi">
              {xemTatCa ? 'Chỉ xem gần đây' : 'Xem tất cả'}
            </button>
          )}
          {coTheMoDanhSach && (
            <button
              type="button"
              onClick={onMoDanhSach}
              className="inline-flex items-center gap-1.5 rounded-lg px-3 py-2 text-sm font-semibold text-emerald-800 phong-toi:text-phong-xanh transition hover:bg-emerald-50 phong-toi:hover:bg-phong-mat-noi focus-visible:outline focus-visible:outline-2 focus-visible:outline-emerald-700 phong-toi:focus-visible:outline-phong-focus"
            >
              Mở danh sách
              <ArrowUpRight size={16} />
            </button>
          )}
        </div>

        {danhSach === null && !loi ? (
          <p role="status" className="px-6 py-10 text-center text-sm text-slate-500 phong-toi:text-phong-chu-phu">Đang tải dữ liệu cuộc họp…</p>
        ) : ganDay.length === 0 ? (
          <div className="px-6 py-10 text-center">
            <CalendarDays className="mx-auto size-9 text-slate-300 phong-toi:text-phong-chu-phu" />
            <p className="mt-3 text-sm font-semibold text-slate-700 phong-toi:text-phong-chu-phu">{locTrangThai ? 'Không có cuộc họp ở trạng thái này' : 'Chưa có cuộc họp nào'}</p>

          </div>
        ) : (
          <div className="divide-y divide-slate-100 phong-toi:divide-phong-vien">
            {ganDay.map((cuocHop) => {
              const trangThai = layTrangThaiHienThiCuocHop(cuocHop)
              return (
                <article key={cuocHop.cuocHopId} className="flex flex-wrap items-center justify-between gap-3 px-5 py-4 transition hover:bg-slate-50/70 phong-toi:hover:bg-phong-mat-noi sm:px-6">
                  <div className="min-w-0 flex-1 basis-48">
                    <p className="wrap-anywhere font-semibold text-slate-900 phong-toi:text-phong-chu">{cuocHop.tieuDe}</p>
                    <p className="mt-1 text-xs text-slate-500 phong-toi:text-phong-chu-phu">
                      {cuocHop.maCuocHop || `#${cuocHop.cuocHopId}`}{cuocHop.ngayTao && ` · Tạo ngày ${dinhDangNgayGio(cuocHop.ngayTao)}`}
                    </p>
                  </div>
                  <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${THE_NHAN_TRANG_THAI[trangThai] || 'bg-slate-100 phong-toi:bg-phong-mat-noi text-slate-700 phong-toi:text-phong-chu-phu'}`}>
                    {TRANG_THAI[trangThai] || trangThai}
                  </span>
                  {onXemCuocHop && (
                    <button type="button" className="rounded-lg border border-slate-200 phong-toi:border-phong-vien px-3 py-2 text-sm font-semibold text-emerald-800 phong-toi:text-phong-xanh hover:bg-emerald-50 phong-toi:hover:bg-phong-mat-noi"
                      aria-label={`Xem chi tiết ${cuocHop.tieuDe}`} onClick={() => onXemCuocHop(cuocHop.cuocHopId)}>
                      Xem chi tiết
                    </button>
                  )}
                </article>
              )
            })}
          </div>
        )}
      </section>

      <ViecChoXuLy token={token} danhSach={danhSach} loiDanhSach={loi}
        cacMucXuLy={cacMucXuLy} coQuyenDuyetKeHoach={coQuyenDuyetKeHoach} onMoMuc={onMoMuc} />
    </div>
  )
}



