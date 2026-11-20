const dinhDang = new Intl.DateTimeFormat('vi-VN', {
  day: '2-digit', month: '2-digit', year: 'numeric',
  hour: '2-digit', minute: '2-digit',
})

export function dinhDangNgayGio(giaTri) {
  const ngay = giaTri ? new Date(giaTri) : null
  return ngay && Number.isFinite(ngay.getTime()) ? dinhDang.format(ngay) : 'Chưa ghi nhận'
}

export function soPhutGiuaHaiMoc(batDau, ketThuc) {
  if (!batDau || !ketThuc) return null
  const chenhLech = new Date(ketThuc) - new Date(batDau)
  return Number.isFinite(chenhLech) && chenhLech >= 0 ? Math.round(chenhLech / 60000) : null
}
