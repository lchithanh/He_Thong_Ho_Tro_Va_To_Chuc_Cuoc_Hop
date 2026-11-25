import { useEffect, useId, useRef } from 'react'
import Plus from 'lucide-react/dist/esm/icons/plus.mjs'
import Search from 'lucide-react/dist/esm/icons/search.mjs'
import { LOP_HOP_THOAI, LOP_MAU_NHAN, LOP_NHAN_THE, LOP_NUT_CHINH, LOP_TRUONG } from './giaoDienPhongHop.js'
export function NhanHoSo({ mau = 'thuong', children }) {
  return <span className={LOP_NHAN_THE + ' ' + LOP_MAU_NHAN[mau]}>{children}</span>
}

export function TieuDeHoSo({ Icon, children, phu, thaoTac, cap: TieuDe = 'h3' }) {
  return (
    <div className="flex flex-wrap items-start justify-between gap-3">
      <div className="flex min-w-0 flex-1 basis-44 items-start gap-3">
        <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-blue-100 text-blue-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">
          <Icon size={18} aria-hidden="true" />
        </span>
        <div className="min-w-0">
          <TieuDe className="wrap-anywhere font-bold">{children}</TieuDe>
          {phu != null && <div className="mt-1 text-xs text-slate-600 phong-toi:text-phong-chu-phu">{phu}</div>}
        </div>
      </div>
      {thaoTac && <div className="flex max-w-full flex-wrap items-center gap-2">{thaoTac}</div>}
    </div>
  )
}

export function NutTaoHoSo({ children, ...props }) {
  return <button {...props} type="button" className={LOP_NUT_CHINH}><Plus size={16} aria-hidden="true" />{children}</button>
}

export function LocHoSo({ tuKhoa, onTuKhoa, nhanTim, boLoc, onBoLoc, cacMuc }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-3">
      <div role="group" aria-label="Lọc theo trạng thái" className="flex flex-wrap gap-2">
        {cacMuc.map(([ma, ten]) => (
          <button type="button" key={ma} aria-pressed={boLoc === ma} onClick={() => onBoLoc(ma)}
            className={'rounded-full px-3 py-1.5 text-xs font-semibold ' + (boLoc === ma
              ? 'bg-blue-800 text-white phong-toi:bg-phong-nut'
              : 'bg-slate-100 text-slate-700 hover:bg-slate-200 phong-toi:bg-phong-mat-noi phong-toi:text-phong-chu-phu phong-toi:hover:bg-phong-nhan-xanh')}>
            {ten}
          </button>
        ))}
      </div>
      <div className="relative w-full sm:max-w-64">
        <Search size={15} aria-hidden="true" className="pointer-events-none absolute top-1/2 left-3 -translate-y-1/2 text-slate-500 phong-toi:text-phong-chu-phu" />
        <input type="search" aria-label={nhanTim} placeholder={nhanTim} className={LOP_TRUONG + ' pl-9'}
          value={tuKhoa} onChange={(event) => onTuKhoa(event.target.value)} />
      </div>
    </div>
  )
}

export function HopThoaiHoSo({ tieuDe, Icon, dangLuu, onDong, children }) {
  const dialog = useRef(null)
  const id = useId()
  useEffect(() => {
    const phanTu = dialog.current
    phanTu.showModal()
    return () => phanTu.close()
  }, [])

  return (
    <dialog ref={dialog} aria-labelledby={id} className={LOP_HOP_THOAI}
      onCancel={(event) => {
        event.preventDefault()
        if (!dangLuu) onDong()
      }}>
      <div className="mb-4 border-b border-slate-200 pb-4 phong-toi:border-phong-vien">
        <TieuDeHoSo Icon={Icon}><span id={id}>{tieuDe}</span></TieuDeHoSo>
      </div>
      {children}
    </dialog>
  )
}
