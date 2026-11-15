import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function layTaiLieuDeXuat(cuocHopId, token, signal) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/tai-lieu-de-xuat`,
    { token, signal },
  )

  return docPhanHoiJson(response, 'Không tải được danh sách tài liệu đề xuất.')
}

export async function taiXuongTaiLieuDeXuat(cuocHopId, taiLieu, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/tai-lieu-de-xuat/${taiLieu.taiLieuId}/tai-xuong`,
    { token },
  )

  if (!response.ok) {
    return docPhanHoiJson(response, 'Không tải xuống được tài liệu đề xuất.')
  }

  const url = URL.createObjectURL(await response.blob())
  const lienKet = document.createElement('a')
  lienKet.href = url
  lienKet.download = taiLieu.tenFileGoc || taiLieu.tenTaiLieu || 'tai-lieu-de-xuat'

  document.body.append(lienKet)
  lienKet.click()
  lienKet.remove()

  setTimeout(() => URL.revokeObjectURL(url), 1000)
}