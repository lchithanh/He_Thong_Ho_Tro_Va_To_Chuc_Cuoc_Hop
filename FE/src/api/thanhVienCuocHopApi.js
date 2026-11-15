import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function layThanhVienCuocHop(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/thanh-vien`, {
    token,
    signal,
  })

  if (!response.ok) {
    throw new Error('Không tải được danh sách thành viên.')
  }

  return response.json()
}

export async function moiThanhVienCuocHop(cuocHopId, nguoiDuocMoiId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/thanh-vien`, {
    method: 'POST',
    token,
    duLieu: { nguoiDuocMoiId },
  })

  if (!response.ok) {
    const duLieu = await response.json().catch(() => null)
    const thongBao = {
      403: 'Tài khoản này không có quyền mời thành viên.',
      404: 'Không tìm thấy người dùng hoặc cuộc họp.',
      409: 'Kiểm tra lịch xác nhận và xem người dùng đã có trong cuộc họp chưa.',
    }

    throw new Error(
      duLieu?.detail || duLieu?.message ||
      thongBao[response.status] || 'Không gửi được lời mời.',
    )
  }
}

export async function phanHoiLoiMoiCuocHop(cuocHopId, thamDu, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/thanh-vien/phan-hoi`, {
    method: 'PUT',
    token,
    duLieu: { thamDu },
  })

  if (!response.ok) {
    const duLieu = await response.json().catch(() => null)
    const thongBao = {
      401: 'Phiên đăng nhập không còn hợp lệ.',
      403: 'Tài khoản này không có quyền phản hồi lời mời.',
      404: 'Không tìm thấy cuộc họp hoặc lời mời của bạn.',
      409: 'Lời mời đã được phản hồi hoặc cuộc họp đã ngừng nhận phản hồi.',
    }

    throw new Error(
      duLieu?.detail || duLieu?.message ||
      thongBao[response.status] || 'Không gửi được phản hồi.',
    )
  }
}

export async function layNguoiCoTheMoi(cuocHopId, token, signal) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/thanh-vien/nguoi-co-the-moi`,
    {
      token,
      signal,
    },
  )

  if (!response.ok) {
    const duLieu = await response.json().catch(() => null)
    throw new Error(
      duLieu?.detail || duLieu?.message || 'Không tải được danh sách người có thể mời.',
    )
  }

  return response.json()
}

export async function layLoiMoiCuaToi(token, signal) {
  const response = await goiApi('/api/cuoc-hop/thanh-vien/loi-moi-cua-toi', {
    token,
    signal,
  })

  if (!response.ok) {
    throw new Error('Không tải được danh sách lời mời.')
  }

  return response.json()
}

export async function diemDanhThanhVienCuocHop(cuocHopId, nguoiDungId, coMat, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/thanh-vien/${nguoiDungId}/diem-danh`,
    {
      method: 'PUT',
      token,
      duLieu: { coMat },
    },
  )

  if (!response.ok) {
    const duLieu = await response.json().catch(() => null)
    throw new Error(
      duLieu?.detail || duLieu?.message || `Không điểm danh được (HTTP ${response.status}).`,
    )
  }
}

export async function ghiNhanRoiSom(cuocHopId, nguoiDungId, token) {
  const response = await goiApi(
    `/api/cuoc-hop/${cuocHopId}/thanh-vien/${nguoiDungId}/roi-som`,
    {
      method: 'PUT',
      token,
    },
  )

  if (!response.ok) {
    const duLieu = await response.json().catch(() => null)
    throw new Error(
      duLieu?.detail || duLieu?.message || `Không ghi nhận được thời điểm rời họp (HTTP ${response.status}).`,
    )
  }
}

export async function moiDanhSachThanhVienCuocHop(cuocHopId, nguoiDuocMoiIds, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/thanh-vien/moi-nhieu`, {
    method: 'POST', token, duLieu: { nguoiDuocMoiIds },
  })
  return docPhanHoiJson(response, `Không gửi được lời mời (HTTP ${response.status}).`)
}
