import { thongBaoDaThayDoiDuLieu } from '../services/dongBoDuLieu.js'

export async function goiApi(duongDan, { token, duLieu, headers, ...tuyChon } = {}) {
  const tieuDe = new Headers(headers)

  if (token) {
    tieuDe.set('Authorization', `Bearer ${token}`)
  }

  if (duLieu !== undefined) {
    tieuDe.set('Content-Type', 'application/json')
    tuyChon.body = JSON.stringify(duLieu)
  }

  const phanHoi = await fetch(duongDan, { ...tuyChon, headers: tieuDe })
  const phuongThuc = tuyChon.method?.toUpperCase()
  if (phanHoi.ok && token && ['POST', 'PUT', 'PATCH', 'DELETE'].includes(phuongThuc)) {
    thongBaoDaThayDoiDuLieu()
  }

  return phanHoi
}

export async function docPhanHoiJson(response, thongBaoMacDinh) {
  const duLieu = await response.json().catch(() => null)

  if (!response.ok) {
    const loi = new Error(duLieu?.detail || duLieu?.message || thongBaoMacDinh)
    loi.status = response.status
    throw loi
  }

  return duLieu
}
