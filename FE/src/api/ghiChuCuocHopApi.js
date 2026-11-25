import { docPhanHoiJson, goiApi } from './apiClient.js'

async function goiGhiChu(cuocHopId, duongDan, tuyChon) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ghi-chu${duongDan}`, tuyChon)
  return docPhanHoiJson(response, `Lỗi ghi chú cuộc họp (HTTP ${response.status}).`)
}

export function layGhiChuCuocHop(cuocHopId, token, signal) {
  return goiGhiChu(cuocHopId, '', { token, signal })
}

export function luuGhiChuCuocHop(cuocHopId, ghiChuId, duLieu, token) {
  return ghiChuId == null
    ? goiGhiChu(cuocHopId, '', { method: 'POST', duLieu, token })
    : goiGhiChu(cuocHopId, `/${ghiChuId}`, { method: 'PUT', duLieu, token })
}

export function xoaGhiChuCuocHop(cuocHopId, ghiChu, token) {
  return goiGhiChu(cuocHopId, `/${ghiChu.ghiChuId}?phienBan=${ghiChu.phienBan}`, { method: 'DELETE', token })
}

export function layGhiChuChoBienBan(cuocHopId, token) {
  return goiGhiChu(cuocHopId, '/ban-nhap-bien-ban', { token })
}
