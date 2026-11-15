const TRANG_THAI_LICH = {
  DE_XUAT: { ten: 'Đề xuất', the: 'bg-amber-50 text-amber-900' },
  DA_XAC_NHAN: { ten: 'Đã xác nhận', the: 'bg-emerald-100 text-emerald-900' },
  TU_CHOI: { ten: 'Từ chối', the: 'bg-rose-50 text-rose-800' },
  DA_HUY: { ten: 'Đã hủy', the: 'bg-slate-100 text-slate-700' },
  THAY_THE: { ten: 'Đã thay thế', the: 'bg-slate-100 text-slate-700' },
}

export const CHIEU_CAO_GIO = 64

export function maNgay(ngay) {
  return [ngay.getFullYear(), ngay.getMonth() + 1, ngay.getDate()]
    .map((phan) => String(phan).padStart(2, '0'))
    .join('-')
}

export function dauThang(ngay) {
  return new Date(ngay.getFullYear(), ngay.getMonth(), 1)
}

function lichTrongNgay(danhSach, ngay) {
  const batDau = maNgay(ngay) + 'T00:00'
  const ngaySau = new Date(ngay.getFullYear(), ngay.getMonth(), ngay.getDate() + 1)
  const ketThuc = maNgay(ngaySau) + 'T00:00'
  return danhSach.filter((lich) =>
    lich.thoiGianBatDau < ketThuc + ':00' && lich.thoiGianKetThuc > batDau + ':00',
  )
}

export function trangThaiLich(lich) {
  return TRANG_THAI_LICH[lich.trangThai]
    ?? { ten: lich.trangThai, the: 'bg-slate-100 text-slate-700' }
}

export function congNgay(ngay, soNgay) {
  return new Date(ngay.getFullYear(), ngay.getMonth(), ngay.getDate() + soNgay)
}

function viTriLich(lich, ngay) {
  const id = maNgay(ngay)
  const phut = (moc) => Number(moc.slice(11, 13)) * 60
    + Number(moc.slice(14, 16)) + Number(moc.slice(17, 19) || 0) / 60
  return {
    batDau: lich.thoiGianBatDau.slice(0, 10) < id ? 0 : phut(lich.thoiGianBatDau),
    ketThuc: lich.thoiGianKetThuc.slice(0, 10) > id ? 1440 : phut(lich.thoiGianKetThuc),
  }
}

export function xepLichTrongNgay(danhSach, ngay) {
  const lichTrongCot = lichTrongNgay(danhSach, ngay)
    .map((lich) => ({ lich, ...viTriLich(lich, ngay) }))
    .sort((a, b) => a.batDau - b.batDau)
  const gioKetThucCuaCot = []

  return lichTrongCot.map((muc) => {
    const cotTrong = gioKetThucCuaCot.findIndex((gio) => gio <= muc.batDau)
    const cot = cotTrong === -1 ? gioKetThucCuaCot.length : cotTrong
    const ketThucHienThi = Math.max(muc.ketThuc, muc.batDau + 20 * 60 / CHIEU_CAO_GIO)
    gioKetThucCuaCot[cot] = ketThucHienThi
    return { ...muc, cot }
  }).map((muc) => ({ ...muc, soCot: gioKetThucCuaCot.length }))
}
