import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function layHoSoPhongHop(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ho-so-phong-hop`, { token, signal })
  return docPhanHoiJson(response, 'Không tải được hồ sơ phòng họp.')
}

export async function chiaSeTepPhongHop(cuocHopId, file, token, ghiChu) {
  const body = new FormData()
  body.set('file', file)
  if (ghiChu?.trim()) body.set('ghiChu', ghiChu.trim())
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ho-so-phong-hop/chia-se`, {
    method: 'POST', token, body,
  })
  return docPhanHoiJson(response, 'Không tải lên được tài liệu.')
}
