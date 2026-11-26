import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function goiNhomLienPhong(duongDan, token, tuyChon = {}) {
  const response = await goiApi(`/api/nhom-lien-phong${duongDan}`, { token, ...tuyChon })
  return docPhanHoiJson(response, 'Không thể xử lý nhóm liên phòng.')
}
