import { useState } from 'react'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right.mjs'
import LayoutDashboard from 'lucide-react/dist/esm/icons/layout-dashboard.mjs'
import LogOut from 'lucide-react/dist/esm/icons/log-out.mjs'
import Menu from 'lucide-react/dist/esm/icons/menu.mjs'
import X from 'lucide-react/dist/esm/icons/x.mjs'

const NHOM_DIEU_HUONG = [
  { ten: 'Cuộc họp', ids: ['tong-quan', 'lich-hop', 'nhom-lien-phong', 'de-xuat-cua-toi', 'de-xuat', 'cuoc-hop-duoc-giao', 'loi-moi'] },
  { ten: 'Xử lý hồ sơ', ids: ['ho-so-ra-soat', 'ho-so-chuyen-cap', 'phan-hoi-ke-hoach', 'ho-so-cho-duyet', 'van-ban-cho-phat-hanh'] },
  { ten: 'Biểu quyết và văn bản', ids: ['tao-bieu-quyet', 'tham-gia-bieu-quyet', 'van-ban-da-phat-hanh'] },
  { ten: 'Quản trị', ids: ['phong-ban', 'thiet-bi', 'phan-cong-thu-ky'] },
]

const MUC_TONG_QUAN = { id: 'tong-quan', ten: 'Tổng quan', BieuTuong: LayoutDashboard }

export default function BoCucChung({
  nguoiDung,
  onDangXuat,
  dieuHuong = [],
  mucDangChon,
  onChonMuc,
  children,
  giaoDienToi = false,
}) {
  const vaiTro = nguoiDung.truongPhong && nguoiDung.maVaiTro === 'CAN_BO'
    ? 'Trưởng phòng'
    : nguoiDung.tenVaiTro || nguoiDung.maVaiTro
  const chuCaiDau = nguoiDung.hoTen?.trim()?.split(/\s+/).at(-1)?.[0] || 'U'
  const cacMuc = [MUC_TONG_QUAN, ...dieuHuong]
  const tenMucDangChon = cacMuc.find(({ id }) => id === mucDangChon)?.ten || 'Tổng quan'
  const [moDieuHuong, setMoDieuHuong] = useState(false)
  const [ngayHienTai] = useState(() => new Date().toLocaleDateString('vi-VN', {
    weekday: 'long',
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }))

  const nhomMenu = NHOM_DIEU_HUONG.map(({ ten, ids }) => ({
    ten,
    muc: cacMuc.filter(({ id }) => ids.includes(id)),
  })).filter(({ muc }) => muc.length > 0)
  const mucKhac = cacMuc.filter(({ id }) => !NHOM_DIEU_HUONG.some(({ ids }) => ids.includes(id)))
  if (mucKhac.length > 0) nhomMenu.push({ ten: 'Khác', muc: mucKhac })

  return (
    <div className={`bo-cuc-cuoc-hop md:grid md:grid-cols-[244px_minmax(0,1fr)] ${giaoDienToi ? 'khong-gian-toi mat-phong-toi lg:mat-do-cuoc-hop lg:grid-cols-[17rem_minmax(0,1fr)]' : 'bg-[#f5f7fb] text-slate-900 lg:grid-cols-[328px_minmax(0,1fr)]'}`}>
      <aside className="border-b border-slate-200 bg-[#eef3fa] phong-toi:border-[#385585] phong-toi:bg-phong-menu phong-toi:text-white md:sticky md:top-0 md:h-[var(--chieu-cao-khong-gian)] md:overflow-y-auto md:border-r md:border-b-0">
        <div className="flex items-center gap-3 px-4 py-4 md:mx-3 md:border-b md:border-slate-200 md:phong-toi:border-phong-vien md:py-6">
          <span className="grid size-10 shrink-0 place-items-center rounded-xl bg-[#102a43] phong-toi:bg-phong-nut text-white">
            <CalendarDays size={21} strokeWidth={1.8} aria-hidden="true" />
          </span>
          <span className="min-w-0 flex-1">
            <span className="block text-sm font-bold text-[#102a43] phong-toi:text-phong-chu">Họp hành chính</span>
            <span className="block text-xs text-slate-600 phong-toi:text-phong-menu-chu-phu">Điều phối cuộc họp</span>
          </span>
          <button
            type="button"
            aria-label={moDieuHuong ? 'Đóng menu' : 'Mở menu'}
            aria-expanded={moDieuHuong}
            aria-controls="dieu-huong-chinh"
            onClick={() => setMoDieuHuong((dangMo) => !dangMo)}
            className="grid size-9 shrink-0 place-items-center rounded-lg border border-slate-300 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat text-slate-700 phong-toi:text-phong-chu-phu md:hidden"
          >
            {moDieuHuong ? <X size={18} /> : <Menu size={18} />}
          </button>
        </div>

        <nav
          id="dieu-huong-chinh"
          aria-label="Điều hướng chính"
          className={`${moDieuHuong ? 'block' : 'hidden'} space-y-5 px-3 pb-5 md:block md:pt-5`}
        >
          {nhomMenu.map(({ ten, muc }) => (
            <div key={ten}>
              <p className="mb-1 px-3 text-xs font-semibold text-slate-600 phong-toi:text-phong-menu-chu-phu">{ten}</p>
              <ul className="space-y-0.5">
                {muc.map(({ id, ten: tenMuc, BieuTuong = ChevronRight }) => {
                  const dangChon = mucDangChon === id
                  return (
                    <li key={id}>
                      <button
                        type="button"
                        aria-current={dangChon ? 'page' : undefined}
                        onClick={() => { setMoDieuHuong(false); onChonMuc(id) }}
                        className={`flex w-full items-center gap-2.5 rounded-lg border-l-2 px-3 py-2.5 text-left text-sm transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue-700 phong-toi:focus-visible:outline-phong-focus ${dangChon
                          ? 'border-blue-700 bg-white font-semibold text-blue-900 shadow-sm phong-toi:border-[#8fb8ff] phong-toi:bg-phong-menu-chon phong-toi:text-white'
                          : 'border-transparent font-medium text-slate-700 hover:bg-white/75 hover:text-slate-950 phong-toi:text-phong-menu-chu phong-toi:hover:bg-phong-menu-hover phong-toi:hover:text-white'}`}
                      >
                        <BieuTuong size={17} className="shrink-0" aria-hidden="true" />
                        <span>{tenMuc}</span>
                      </button>
                    </li>
                  )
                })}
              </ul>
            </div>
          ))}
        </nav>
      </aside>

      <div className="min-w-0">
        <header className="sticky top-0 z-10 flex min-h-16 items-center justify-between gap-3 lg:min-h-[84px] border-b border-slate-200 phong-toi:border-phong-vien bg-white/95 phong-toi:bg-phong-mat px-4 backdrop-blur lg:px-8">
          <div className="min-w-0">
            {giaoDienToi
              ? <h1 className="text-base font-bold">Tổng quan cuộc họp</h1>
              : <p className="truncate text-sm font-semibold text-slate-900 phong-toi:text-phong-chu">{tenMucDangChon}</p>}
            <p className="mt-0.5 truncate text-xs capitalize text-slate-500 phong-toi:text-phong-chu-phu">{ngayHienTai}</p>
          </div>
          <div className="flex shrink-0 items-center gap-2.5">
            <span aria-hidden="true" className="grid size-9 place-items-center rounded-full bg-blue-100 phong-toi:bg-phong-nhan-xanh text-sm font-bold text-blue-900 phong-toi:text-phong-xanh">
              {chuCaiDau.toLocaleUpperCase('vi')}
            </span>
            <div className="hidden min-w-0 sm:block">
              <p className="max-w-48 truncate text-sm font-semibold">{nguoiDung.hoTen}</p>
              <p className="text-xs text-slate-500 phong-toi:text-phong-chu-phu">{vaiTro}</p>
            </div>
            <button
              type="button"
              aria-label="Đăng xuất"
              title="Đăng xuất"
              onClick={onDangXuat}
              className="ml-1 grid size-9 place-items-center rounded-lg border border-slate-200 phong-toi:border-phong-vien text-slate-600 phong-toi:text-phong-chu-phu transition hover:bg-slate-100 phong-toi:hover:bg-phong-mat-noi focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue-700 phong-toi:focus-visible:outline-phong-focus"
            >
              <LogOut size={16} aria-hidden="true" />
            </button>
          </div>
        </header>

        <main className="mx-auto max-w-[2180px] space-y-5 p-4 sm:p-6 xl:px-8">
          {children}
        </main>
      </div>
    </div>
  )
}
