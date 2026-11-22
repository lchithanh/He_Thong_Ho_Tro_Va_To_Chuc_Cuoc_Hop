import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function layKhachNgoaiCuocHop(cuocHopId, token, signal) {
  const response = await goiApi('/api/cuoc-hop/' + cuocHopId + '/khach-ngoai', { token, signal })
  return docPhanHoiJson(response, 'Không tải được danh sách khách ngoài.')
}

export async function moiKhachNgoaiQuaEmail(cuocHopId, token, duLieu) {
  const response = await goiApi('/api/cuoc-hop/' + cuocHopId + '/khach-ngoai', {
    method: 'POST',
    token,
    duLieu,
  })
  const loiMacDinh = response.status === 400
    ? 'Kiểm tra họ tên, địa chỉ email và thông tin khách mời.'
    : 'Không gửi được thư mời.'
  return docPhanHoiJson(response, loiMacDinh)
}

export async function chiaSeTaiLieuKhach(cuocHopId, token, form) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/khach-ngoai/tai-lieu`, {
    token, method: 'POST', body: form,
  })
  return docPhanHoiJson(response, 'Không chia sẻ được tài liệu.')
}

export async function taiLieuCoTheChiaSe(cuocHopId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/khach-ngoai/tai-lieu`, { token })
  return docPhanHoiJson(response, 'Không tải được danh sách tài liệu.')
}

export async function chiaSeTepCoSan(cuocHopId, token, taiLieuId) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/khach-ngoai/tai-lieu/${taiLieuId}/chia-se`, {
    token, method: 'POST',
  })
  return docPhanHoiJson(response, 'Không chia sẻ được tài liệu.')
}
