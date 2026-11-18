import { docPhanHoiJson, goiApi } from './apiClient.js'

function docPhanHoiKetLuan(response) {
  return docPhanHoiJson(response, `Lỗi kết luận (HTTP ${response.status}).`)
}

export async function layKetLuanTheoCuocHop(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan`, {
    token,
    signal,
  })

  if (response.status === 404) return null

  return docPhanHoiKetLuan(response)
}

export async function taoKetLuanNhap(cuocHopId, duLieu, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan`, {
    method: 'POST',
    token,
    duLieu,
  })

  return docPhanHoiKetLuan(response)
}

export async function capNhatKetLuanNhap(cuocHopId, duLieu, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan`, {
    method: 'PUT',
    token,
    duLieu,
  })

  return docPhanHoiKetLuan(response)
}

export async function trinhDuyetKetLuan(cuocHopId, ghiChuTrinh, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan/trinh-duyet`, {
    method: 'POST',
    token,
    duLieu: { ghiChuTrinh },
  })

  return docPhanHoiKetLuan(response)
}

export async function layTaiLieuKetLuan(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan/tai-lieu`, {
    token,
    signal,
  })

  return docPhanHoiKetLuan(response)
}

export async function taiLenTaiLieuKetLuan(cuocHopId, file, token) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan/tai-lieu`, {
    method: 'POST',
    token,
    body: formData,
  })

  return docPhanHoiKetLuan(response)
}

export async function layTepKetLuan({ cuocHopId }, taiLieuId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan/tai-lieu/${taiLieuId}/tai-xuong`, { token, signal })
  if (!response.ok) return docPhanHoiKetLuan(response)
  return response.blob()
}

export async function taiXuongTaiLieuKetLuan(cuocHopId, taiLieu, token) {
  const tep = await layTepKetLuan({ cuocHopId }, taiLieu.taiLieuId, token)
  const url = URL.createObjectURL(tep)
  const link = document.createElement('a')
  link.href = url
  link.download = taiLieu.tenFileGoc || `tai-lieu-ket-luan-${taiLieu.taiLieuId}`
  document.body.appendChild(link)
  link.click()
  link.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}

export async function layKetLuanChoToiPhatHanh(token, signal) {
  const response = await goiApi('/api/cuoc-hop/ket-luan/cho-toi-phat-hanh', {
    token,
    signal,
  })

  return docPhanHoiKetLuan(response)
}

export async function phatHanhKetLuan(cuocHopId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-luan/phat-hanh`, {
    method: 'POST',
    token,
  })

  return docPhanHoiKetLuan(response)
}

export async function xuLyDuyetKetLuan(ketLuan, thaoTac, yKien, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${ketLuan.cuocHopId}/ket-luan/yeu-cau/${ketLuan.yeuCauPheDuyetId}/${thaoTac}`,
    {
      method: 'POST',
      token,
      duLieu: { yKien },
    },
  )

  return docPhanHoiKetLuan(response)
}

export async function layKetLuanDaPhatHanhCuaToi(token, signal) {
  const response = await goiApi('/api/cuoc-hop/ket-luan/da-phat-hanh-cua-toi', {
    token,
    signal,
  })

  return docPhanHoiKetLuan(response)
}
