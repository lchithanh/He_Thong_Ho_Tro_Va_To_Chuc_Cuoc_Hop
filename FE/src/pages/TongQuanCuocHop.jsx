import { useEffect, useMemo, useState } from 'react'
import ArrowUpRight from 'lucide-react/dist/esm/icons/arrow-up-right.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import CheckCircle2 from 'lucide-react/dist/esm/icons/check-circle-2.mjs'
import CircleDot from 'lucide-react/dist/esm/icons/circle-dot.mjs'
import Clock3 from 'lucide-react/dist/esm/icons/clock-3.mjs'
import FileClock from 'lucide-react/dist/esm/icons/file-clock.mjs'
import ViecChoXuLy from './ViecChoXuLy.jsx'
import Inbox from 'lucide-react/dist/esm/icons/inbox.mjs'
import { layDanhSachDeXuat, layTrangThaiHienThiCuocHop } from '../api/cuocHopApi.js'

const TRANG_THAI = {
  NHAP: 'Bản nháp',
  CHO_TIEP_NHAN: 'Chờ tiếp nhận',
  DANG_CHUAN_BI: 'Đang chuẩn bị',
  CHO_DUYET: 'Chờ duyệt',
  DA_DUYET: 'Đã duyệt',
  DANG_DIEN_RA: 'Đang diễn ra',
  DA_KET_THUC: 'Đã kết thúc',
  TRA_LAI: 'Đã trả lại',
  TU_CHOI: 'Đã từ chối',
  DA_HUY: 'Đã hủy',
}

const THE_NHAN_TRANG_THAI = {
  CHO_TIEP_NHAN: 'bg-amber-50 text-amber-800',
  DANG_CHUAN_BI: 'bg-sky-50 text-sky-800',
  CHO_DUYET: 'bg-violet-50 text-violet-800',
  DA_DUYET: 'bg-green-50 text-green-800',
  DANG_DIEN_RA: 'bg-rose-50 text-rose-800',
  DA_KET_THUC: 'bg-slate-100 text-slate-700',
  TRA_LAI: 'bg-amber-50 text-amber-900',
  TU_CHOI: 'bg-red-50 text-red-800',
  DA_HUY: 'bg-red-50 text-red-800',
}

function dinhDangNgay(ngay) {
  if (!ngay) return '—'
  const ketQua = new Date(ngay)
  return Number.isNaN(ketQua.getTime())
    ? '—'
    : ketQua.toLocaleDateString('vi-VN', { day: '2-digit', month: 'short', year: 'numeric' })
}

export default function TongQuanCuocHop({
  token,
  nguoiDung,
  coTheMoDanhSach,
  onMoDanhSach,
  onXemCuocHop,
  cacMucXuLy,
  onMoMuc,
}) {
  const [xemTatCa, setXemTatCa] = useState(false)
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachDeXuat(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token])

  const thongKe = useMemo(() => {
    const cacCuocHop = danhSach || []
    return [
      { ten: 'Chờ tiếp nhận', ma: 'CHO_TIEP_NHAN', BieuTuong: Inbox, mau: 'amber' },
      { ten: 'Đang chuẩn bị', ma: 'DANG_CHUAN_BI', BieuTuong: FileClock, mau: 'sky' },
      { ten: 'Chờ duyệt', ma: 'CHO_DUYET', BieuTuong: Clock3, mau: 'violet' },
      { ten: 'Đã duyệt', ma: 'DA_DUYET', BieuTuong: CheckCircle2, mau: 'emerald' },
      { ten: 'Đang diễn ra', ma: 'DANG_DIEN_RA', BieuTuong: CircleDot, mau: 'rose' },
      { ten: 'Đã trả lại', ma: 'TRA_LAI', BieuTuong: FileClock, mau: 'amber' },
      { ten: 'Đã từ chối', ma: 'TU_CHOI', BieuTuong: CircleDot, mau: 'rose' },
      { ten: 'Đã kết thúc', ma: 'DA_KET_THUC', BieuTuong: CalendarDays, mau: 'slate' },
    ].map((muc) => ({
      ...muc,
      soLuong: cacCuocHop.filter((cuocHop) => layTrangThaiHienThiCuocHop(cuocHop) === muc.ma).length,
    }))
  }, [danhSach])

  const ganDay = useMemo(
    () => [...(danhSach || [])]
      .sort((a, b) => new Date(b.ngayTao || 0) - new Date(a.ngayTao || 0))
      .slice(0, xemTatCa ? (danhSach?.length ?? 0) : 5),
    [danhSach, xemTatCa],
  )

  return (
    <div className="space-y-6">
      <header className="flex flex-wrap items-end justify-between gap-3 rounded-xl border border-slate-200 bg-white px-5 py-4 sm:px-6">
        <h1 className="text-2xl font-bold tracking-tight text-[#102a43]">Tổng quan cuộc họp</h1>
        <p className="text-sm font-medium text-slate-600">{nguoiDung.tenPhongBan || nguoiDung.tenVaiTro || nguoiDung.maVaiTro}</p>
      </header>

      <ViecChoXuLy token={token} danhSach={danhSach} loiDanhSach={loi}
        cacMucXuLy={cacMucXuLy} onMoMuc={onMoMuc} />


      <section aria-label="Thống kê trạng thái cuộc họp">
        <div className="mb-3 flex items-center justify-between gap-3">
          <div>
            <h2 className="text-base font-bold text-slate-900">Tình hình cuộc họp</h2>
            <p className="mt-1 text-sm text-slate-500">
              {danhSach ? `${danhSach.length} hồ sơ trong danh sách của bạn` : 'Đang đồng bộ dữ liệu'}
            </p>
          </div>
        </div>

        {loi && (
          <p role="alert" className="mb-4 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-800">
            {loi}
          </p>
        )}

        <div className="grid gap-3 grid-cols-2 sm:grid-cols-3 xl:grid-cols-4">
          {thongKe.map(({ ten, soLuong, BieuTuong, mau }) => {
            const mauThe = {
              amber: 'bg-amber-50 text-amber-700',
              sky: 'bg-sky-50 text-sky-700',
              violet: 'bg-violet-50 text-violet-700',
              emerald: 'bg-green-50 text-green-700',
              rose: 'bg-rose-50 text-rose-700',
              slate: 'bg-slate-100 text-slate-600',
            }[mau]

            return (
              <article key={ten} className="rounded-2xl border border-slate-200/80 bg-white p-4 shadow-sm shadow-slate-900/[0.02]">
                <div className="flex items-start justify-between gap-3">
                  <span className={`grid size-10 place-items-center rounded-xl ${mauThe}`}>
                    <BieuTuong size={19} />
                  </span>
                  <span className="text-2xl font-bold tabular-nums text-slate-900">
                    {danhSach === null ? '—' : soLuong}
                  </span>
                </div>
                <p className="mt-4 text-sm font-semibold text-slate-700">{ten}</p>
              </article>
            )
          })}
        </div>
      </section>

      <section className="overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-sm shadow-slate-900/[0.02]">
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-100 px-5 py-4 sm:px-6">
          <div>
            <h2 className="font-bold text-slate-900">{xemTatCa ? 'Danh sách cuộc họp' : 'Cuộc họp gần đây'}</h2>
            <p className="mt-1 text-sm text-slate-500">Các hồ sơ mới cập nhật trong danh sách của bạn</p>
          </div>
          {onXemCuocHop && danhSach?.length > 5 && (
            <button type="button" onClick={() => setXemTatCa((hienTai) => !hienTai)}
              className="rounded-lg px-3 py-2 text-sm font-semibold text-emerald-800 hover:bg-emerald-50">
              {xemTatCa ? 'Chỉ xem gần đây' : 'Xem tất cả'}
            </button>
          )}
          {coTheMoDanhSach && (
            <button
              type="button"
              onClick={onMoDanhSach}
              className="inline-flex items-center gap-1.5 rounded-lg px-3 py-2 text-sm font-semibold text-emerald-800 transition hover:bg-emerald-50 focus-visible:outline focus-visible:outline-2 focus-visible:outline-emerald-700"
            >
              Mở danh sách
              <ArrowUpRight size={16} />
            </button>
          )}
        </div>

        {danhSach === null && !loi ? (
          <p role="status" className="px-6 py-10 text-center text-sm text-slate-500">Đang tải dữ liệu cuộc họp…</p>
        ) : ganDay.length === 0 ? (
          <div className="px-6 py-10 text-center">
            <CalendarDays className="mx-auto size-9 text-slate-300" />
            <p className="mt-3 text-sm font-semibold text-slate-700">Chưa có cuộc họp nào</p>
            <p className="mt-1 text-sm text-slate-500">Các đề xuất và cuộc họp của bạn sẽ hiển thị tại đây.</p>
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {ganDay.map((cuocHop) => {
              const trangThai = layTrangThaiHienThiCuocHop(cuocHop)
              return (
                <article key={cuocHop.cuocHopId} className="flex flex-wrap items-center justify-between gap-3 px-5 py-4 transition hover:bg-slate-50/70 sm:px-6">
                  <div className="min-w-0">
                    <p className="truncate text-sm font-semibold text-slate-900">{cuocHop.tieuDe}</p>
                    <p className="mt-1 text-xs text-slate-500">
                      {cuocHop.maCuocHop || `#${cuocHop.cuocHopId}`} · {dinhDangNgay(cuocHop.ngayTao)}
                    </p>
                  </div>
                  <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${THE_NHAN_TRANG_THAI[trangThai] || 'bg-slate-100 text-slate-700'}`}>
                    {TRANG_THAI[trangThai] || trangThai}
                  </span>
                  {onXemCuocHop && (
                    <button type="button" className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-emerald-800 hover:bg-emerald-50"
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
    </div>
  )
}



