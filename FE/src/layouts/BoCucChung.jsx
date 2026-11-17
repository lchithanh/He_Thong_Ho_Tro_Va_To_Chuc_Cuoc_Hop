import { useState } from 'react'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right.mjs'
import LayoutDashboard from 'lucide-react/dist/esm/icons/layout-dashboard.mjs'
import LogOut from 'lucide-react/dist/esm/icons/log-out.mjs'

export default function BoCucChung({
  nguoiDung,
  onDangXuat,
  dieuHuong = [],
  mucDangChon,
  onChonMuc,
  children,
}) {
  const vaiTro = nguoiDung.tenVaiTro || nguoiDung.maVaiTro
  const chuCaiDau = nguoiDung.hoTen?.trim()?.split(/\s+/).at(-1)?.[0] || 'U'
  const [ngayHienTai] = useState(() => new Date().toLocaleDateString('vi-VN', {
    weekday: 'long',
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }))

  return (
    <div className="min-h-screen bg-[#f8fafc] text-slate-900 md:grid md:grid-cols-[232px_minmax(0,1fr)]">
      <aside className="z-20 bg-[#102a43] text-white md:sticky md:top-0 md:h-screen md:overflow-y-auto">
        <div className="flex items-center gap-3 px-5 py-5 md:px-6 md:py-7">
          <span className="grid size-11 shrink-0 place-items-center rounded-lg bg-white/10 text-emerald-100">
            <CalendarDays size={23} strokeWidth={1.8} />
          </span>
          <span>
            <span className="block text-sm font-bold tracking-wide">HỌP HÀNH CHÍNH</span>
            <span className="mt-1 block text-xs text-emerald-100/70">Điều phối cuộc họp</span>
          </span>
        </div>

        <nav aria-label="Điều hướng chính" className="flex gap-1 overflow-x-auto px-3 pb-3 md:flex-col md:px-4 md:pb-6">
          <p className="hidden px-3 pb-2 pt-3 text-[10px] font-bold uppercase tracking-[0.18em] text-emerald-100/50 md:block">
            Không gian làm việc
          </p>
          <button
            type="button"
            aria-current={mucDangChon === 'tong-quan' ? 'page' : undefined}
            onClick={() => onChonMuc('tong-quan')}
            className={`flex shrink-0 items-center gap-3 rounded-md px-3 py-2.5 text-left text-sm font-semibold transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-white ${
              mucDangChon === 'tong-quan'
                ? 'bg-white text-[#102a43] shadow-sm'
                : 'text-emerald-50/80 hover:bg-white/10 hover:text-white'
            }`}
          >
            <LayoutDashboard size={18} />
            <span>Tổng quan</span>
          </button>

          {dieuHuong.map(({ id, ten, BieuTuong }) => {
            const BieuTuongMuc = BieuTuong
            const dangChon = mucDangChon === id

            return (
              <button
                key={id}
                type="button"
                aria-current={dangChon ? 'page' : undefined}
                onClick={() => onChonMuc(id)}
                className={`flex shrink-0 items-center gap-3 rounded-md px-3 py-2.5 text-left text-sm font-medium transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-white ${
                  dangChon
                    ? 'bg-white/15 text-white'
                    : 'text-emerald-50/75 hover:bg-white/10 hover:text-white'
                }`}
              >
                {BieuTuongMuc ? <BieuTuongMuc size={18} /> : <ChevronRight size={18} />}
                <span>{ten}</span>
              </button>
            )
          })}
        </nav>

      </aside>

      <div className="min-w-0">
        <header className="sticky top-0 z-10 flex min-h-16 items-center justify-between gap-4 border-b border-slate-200/80 bg-white/95 px-4 backdrop-blur md:px-8">
          <div className="min-w-0">
            <p className="text-xs font-medium capitalize text-slate-500">{ngayHienTai}</p>
            <p className="mt-1 truncate text-sm font-semibold text-slate-800">
              Không gian làm việc <span className="mx-1 text-slate-300">/</span>{' '}
              {mucDangChon === 'tong-quan'
                ? 'Tổng quan'
                : dieuHuong.find((muc) => muc.id === mucDangChon)?.ten}
            </p>
          </div>

          <div className="flex shrink-0 items-center gap-3">
            <div className="hidden text-right sm:block">
              <p className="max-w-48 truncate text-sm font-semibold text-slate-900">{nguoiDung.hoTen}</p>
              <p className="mt-0.5 text-xs text-slate-500">{vaiTro}</p>
            </div>
            <span className="grid size-10 place-items-center rounded-full bg-emerald-100 text-sm font-bold text-emerald-900">
              {chuCaiDau.toLocaleUpperCase('vi')}
            </span>
            <button
              type="button"
              onClick={onDangXuat}
              className="inline-flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-600 transition hover:border-slate-300 hover:bg-slate-50 hover:text-slate-900 focus-visible:outline focus-visible:outline-2 focus-visible:outline-emerald-700"
            >
              <LogOut size={16} />
              <span className="hidden sm:inline">Đăng xuất</span>
            </button>
          </div>
        </header>

        <main className="mx-auto max-w-[1600px] space-y-5 p-4 sm:p-6 xl:px-8">
          {children}
        </main>
      </div>
    </div>
  )
}
