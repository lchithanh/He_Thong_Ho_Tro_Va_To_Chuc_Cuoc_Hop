import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function goiApiQuanTri(url, token, tuyChon = {}) {
  const response = await goiApi(`/api/admin/${url}`, { token, ...tuyChon })
  return docPhanHoiJson(response, `Không xử lý được yêu cầu (HTTP ${response.status}).`)
}
