import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function taoBieuQuyet(cuocHopId, duLieu, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/bieu-quyet`, {
    method: 'POST',
    token,
    duLieu,
  })

  return docPhanHoiJson(response, `Không tạo được biểu quyết (HTTP ${response.status}).`)
}

async function kiemTraPhanHoiBieuQuyet(response) {
  if (!response.ok) {
    await docPhanHoiJson(response, `Lỗi biểu quyết (HTTP ${response.status}).`)
  }
}

export async function capNhatBieuQuyet(cuocHopId, bieuQuyetId, duLieu, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}`, {
    method: 'PUT', token, duLieu,
  })
  await kiemTraPhanHoiBieuQuyet(response)
}

export async function xoaBieuQuyet(cuocHopId, bieuQuyetId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}`, {
    method: 'DELETE', token,
  })
  await kiemTraPhanHoiBieuQuyet(response)
}

export async function layDanhSachBieuQuyetCuaChuTri(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/bieu-quyet`, {
    token,
    signal,
  })

  await kiemTraPhanHoiBieuQuyet(response)
  return response.json()
}

export async function moBieuQuyet(cuocHopId, bieuQuyetId, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}/mo`,
    {
      method: 'POST',
      token,
    },
  )

  await kiemTraPhanHoiBieuQuyet(response)
}

export async function layChiTietBieuQuyet(cuocHopId, bieuQuyetId, token, signal) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}`,
    {
      token,
      signal,
    },
  )

  await kiemTraPhanHoiBieuQuyet(response)
  return response.json()
}

export async function guiPhieuBieuQuyet(cuocHopId, bieuQuyetId, luaChonId, token, lyDoKhongDongY) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}/phieu`,
    {
      method: 'POST',
      token,
      duLieu: { luaChonId, lyDoKhongDongY },
    },
  )

  await kiemTraPhanHoiBieuQuyet(response)
}

export async function dongBieuQuyet(cuocHopId, bieuQuyetId, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}/dong`,
    {
      method: 'POST',
      token,
    },
  )

  await kiemTraPhanHoiBieuQuyet(response)
  return response.json()
}

export async function layKetQuaBieuQuyet(cuocHopId, bieuQuyetId, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/bieu-quyet/${bieuQuyetId}/ket-qua`,
    {
      token,
    },
  )

  await kiemTraPhanHoiBieuQuyet(response)
  return response.json()
}

export async function layDanhSachBieuQuyetThamDu(token, signal, cuocHopId) {
  const boLoc = cuocHopId == null ? '' : '?' + new URLSearchParams({ cuocHopId })
  const response = await goiApi('/api/cuoc-hop/bieu-quyet/cua-toi' + boLoc, {
    token,
    signal,
  })

  await kiemTraPhanHoiBieuQuyet(response)
  return response.json()
}

export async function layDanhSachBieuQuyetDaDongCuaToi(token, signal, cuocHopId) {
  const boLoc = cuocHopId == null ? '' : '?' + new URLSearchParams({ cuocHopId })
  const response = await goiApi('/api/cuoc-hop/bieu-quyet/ket-qua-cua-toi' + boLoc, {
    token,
    signal,
  })

  await kiemTraPhanHoiBieuQuyet(response)
  return response.json()
}
