import { docPhanHoiJson, goiApi } from './apiClient.js'

async function goi(duongDan, tuyChon) {
  const response = await goiApi(duongDan, tuyChon)
  return docPhanHoiJson(response, 'Không thực hiện được thao tác. Vui lòng thử lại.')
}

export function goiBauChon(cuocHopId, token, duongDan = '', tuyChon = {}) {
  return goi(`/api/cuoc-hop/${cuocHopId}/bau-chon${duongDan}`, { token, ...tuyChon })
}

export function goiNghiQuyet(cuocHopId, token, duongDan = '', tuyChon = {}) {
  return goi(`/api/cuoc-hop/${cuocHopId}/nghi-quyet${duongDan}`, { token, ...tuyChon })
}

export function xemLoiMoiKhach(ma, signal) {
  return goi('/api/loi-moi-khach', { headers: { 'X-Ma-Loi-Moi': ma }, signal, cache: 'no-store' })
}

export function phanHoiLoiMoiKhach(ma, thamDu) {
  return goi('/api/loi-moi-khach/phan-hoi', {
    method: 'POST', headers: { 'X-Ma-Loi-Moi': ma }, duLieu: { thamDu },
  })
}

export function doiLichHop(lichHopId, token, duLieu) {
  return goi(`/api/lich-hop/${lichHopId}/doi-lich`, { token, method: 'POST', duLieu })
}

export function layCuocHopCoNghiQuyet(token, signal) {
  return goi('/api/cuoc-hop/nghi-quyet/cua-toi', { token, signal })
}
