export function goiApi(duongDan, { token, duLieu, headers, ...tuyChon } = {}) {
  const tieuDe = new Headers(headers)

  if (token) {
    tieuDe.set('Authorization', `Bearer ${token}`)
  }

  if (duLieu !== undefined) {
    tieuDe.set('Content-Type', 'application/json')
    tuyChon.body = JSON.stringify(duLieu)
  }

  return fetch(duongDan, { ...tuyChon, headers: tieuDe })
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
