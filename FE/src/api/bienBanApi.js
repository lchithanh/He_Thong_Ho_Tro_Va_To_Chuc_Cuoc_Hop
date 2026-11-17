import { docPhanHoiJson, goiApi } from './apiClient.js'

function docPhanHoiBienBan(response) {
  return docPhanHoiJson(response, `Lỗi biên bản (HTTP ${response.status}).`)
}

export async function layBienBanTheoCuocHop(cuocHopId, token, signal) {
  const response = await goiApi(`/api/bien-ban/cuoc-hop/${cuocHopId}`, {
    token,
    signal,
  })

  if (response.status === 404) return null

  return docPhanHoiBienBan(response)
}

export async function taoBienBanCuocHop(cuocHopId, duLieu, token) {
  const response = await goiApi(`/api/bien-ban/cuoc-hop/${cuocHopId}`, {
    method: 'POST',
    token,
    duLieu,
  })

  return docPhanHoiBienBan(response)
}

export async function capNhatBienBan(bienBanId, duLieu, token) {
  const response = await goiApi(`/api/bien-ban/${bienBanId}`, {
    method: 'PUT',
    token,
    duLieu,
  })

  return docPhanHoiBienBan(response)
}

export async function layLichSuPhienBan(bienBanId, token, signal) {
  const response = await goiApi(`/api/bien-ban/${bienBanId}/phien-ban`, {
    token,
    signal,
  })

  return docPhanHoiBienBan(response)
}

export async function trinhDuyetBienBan(bienBanId, ghiChuTrinh, token) {
  const response = await goiApi(`/api/bien-ban/${bienBanId}/trinh-duyet`, {
    method: 'POST',
    token,
    duLieu: { ghiChuTrinh },
  })

  return docPhanHoiBienBan(response)
}

export async function layBienBanChoToiDuyet(token, signal) {
  const response = await goiApi('/api/bien-ban/cho-toi-duyet', {
    token,
    signal,
  })

  return docPhanHoiBienBan(response)
}

export async function xuLyDuyetBienBan(bienBan, thaoTac, yKien, token) {
  const response = await goiApi(`/api/bien-ban/${bienBan.bienBanId}/${thaoTac}`, {
    method: 'POST',
    token,
    duLieu: { yeuCauPheDuyetId: bienBan.yeuCauPheDuyetId, yKien },
  })

  return docPhanHoiBienBan(response)
}

export async function phatHanhBienBan(cuocHopId, token) {
  const response = await goiApi(`/api/bien-ban/cuoc-hop/${cuocHopId}/phat-hanh`, {
    method: 'POST',
    token,
  })

  return docPhanHoiBienBan(response)
}

export async function layBienBanChoToiPhatHanh(token, signal) {
  return docPhanHoiBienBan(await goiApi('/api/bien-ban/cho-toi-phat-hanh', { token, signal }))
}

export async function layTaiLieuBienBan(cuocHopId, token, signal) {
  return docPhanHoiBienBan(await goiApi(`/api/bien-ban/cuoc-hop/${cuocHopId}/tai-lieu`, { token, signal }))
}

export async function taiLenTaiLieuBienBan(cuocHopId, file, token) {
  const body = new FormData()
  body.append('file', file)
  return docPhanHoiBienBan(await goiApi(`/api/bien-ban/cuoc-hop/${cuocHopId}/tai-lieu`, {
    method: 'POST', body, token,
  }))
}

export async function taiXuongTaiLieuBienBan(cuocHopId, taiLieu, token) {
  const response = await goiApi(
    `/api/bien-ban/cuoc-hop/${cuocHopId}/tai-lieu/${taiLieu.taiLieuId}/tai-xuong`, { token },
  )
  if (!response.ok) return docPhanHoiBienBan(response)
  const url = URL.createObjectURL(await response.blob())
  const lienKet = document.createElement('a')
  lienKet.href = url
  lienKet.download = taiLieu.tenFileGoc || 'tai-lieu-bien-ban'
  document.body.append(lienKet)
  lienKet.click()
  lienKet.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
