export const LOP_THE = 'min-w-0 rounded-xl border border-slate-200 bg-white p-3 sm:p-4 phong-toi:border-phong-vien phong-toi:bg-phong-mat'
export const LOP_NOI = 'min-w-0 rounded-xl border border-slate-200 bg-slate-50 p-3 phong-toi:border-phong-vien phong-toi:bg-phong-mat-noi'
export const LOP_CHU_PHU = 'text-sm text-slate-600 phong-toi:text-phong-chu-phu'
export const LOP_NUT_CHINH = 'inline-flex items-center justify-center gap-2 rounded-lg bg-blue-800 px-3 py-2 text-sm font-semibold text-white hover:bg-blue-900 disabled:opacity-60 phong-toi:bg-phong-nut phong-toi:enabled:hover:bg-phong-nut-hover'
export const LOP_NUT_PHU = 'inline-flex items-center justify-center gap-2 rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold hover:bg-slate-100 disabled:opacity-60 phong-toi:border-phong-vien phong-toi:hover:bg-phong-mat-noi'
export const LOP_NUT_XOA = LOP_NUT_PHU + ' text-rose-800 phong-toi:text-phong-do'
export const LOP_TRUONG = 'w-full rounded-lg border border-slate-300 px-3 py-2 text-sm phong-toi:truong-phong-hop'
export const LOP_NHAN_THE = 'inline-flex w-fit shrink-0 items-center rounded-full px-2.5 py-1 text-xs font-semibold'
export const LOP_MAU_NHAN = {
  thuong: 'bg-slate-100 text-slate-700 phong-toi:bg-phong-mat-noi phong-toi:text-phong-chu-phu',
  xanh: 'bg-blue-100 text-blue-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh',
  luc: 'bg-emerald-50 text-emerald-900 phong-toi:bg-phong-nhan-luc phong-toi:text-phong-luc',
  vang: 'bg-amber-50 text-amber-900 phong-toi:bg-phong-nhan-vang phong-toi:text-phong-vang',
  do: 'bg-rose-50 text-rose-900 phong-toi:bg-phong-nhan-do phong-toi:text-phong-do',
}

export function khopTuKhoa(ten, tuKhoa) {
  const chuanHoa = (giaTri) => giaTri.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '').replaceAll('đ', 'd').trim()
  return chuanHoa(ten ?? '').includes(chuanHoa(tuKhoa))
}

export const LOP_HOP_THOAI = 'fixed inset-0 m-auto max-h-[90dvh] w-[min(44rem,calc(100vw-2rem))] overflow-y-auto rounded-2xl border border-slate-200 bg-white p-4 text-slate-900 shadow-xl backdrop:bg-slate-950/70 sm:p-6 phong-toi:border-phong-vien phong-toi:bg-phong-mat phong-toi:text-phong-chu'
