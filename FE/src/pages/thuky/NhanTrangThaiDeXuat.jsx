import { TEN_TRANG_THAI } from './hienThiDeXuat.js'

const MAU_TRANG_THAI = {
  CHO_TIEP_NHAN: 'bg-amber-50 text-amber-900',
  DANG_CHUAN_BI: 'bg-amber-50 text-amber-900',
  CHO_GUI_CAP_TREN: 'bg-blue-50 text-blue-800',
  DA_DUYET: 'bg-green-50 text-green-800',
  DANG_DIEN_RA: 'bg-green-50 text-green-800',
  TRA_LAI: 'bg-amber-50 text-amber-900',
  TU_CHOI: 'bg-red-50 text-red-800',
  DA_HUY: 'bg-red-50 text-red-800',
}

export default function NhanTrangThai({ trangThai }) {
  const mau = MAU_TRANG_THAI[trangThai] ?? 'bg-slate-100 text-slate-700'

  return (
    <span className={`inline-flex items-center gap-1.5 whitespace-nowrap rounded-full px-2.5 py-1 text-xs font-semibold ${mau}`}>
      <span aria-hidden="true" className="size-1.5 rounded-full bg-current" />
      {TEN_TRANG_THAI[trangThai] ?? trangThai ?? '—'}
    </span>
  )
}
