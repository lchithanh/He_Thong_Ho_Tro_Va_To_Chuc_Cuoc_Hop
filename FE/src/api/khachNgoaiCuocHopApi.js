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
