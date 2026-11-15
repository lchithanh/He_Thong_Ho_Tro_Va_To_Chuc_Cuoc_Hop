import { docPhanHoiJson, goiApi } from './apiClient.js'

export async function layDanhSachDeXuat(token, signal) {
  const response = await goiApi('/api/cuoc-hop/de-xuat', {
    method: 'GET',
    token,
    signal,
  })

  if (!response.ok) {
    const loi = new Error('Không tải được danh sách đề xuất.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}

export async function layChiTietDeXuat(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/de-xuat`, {
    token,
    signal,
  })

  if (!response.ok) {
    const loi = new Error('Không tải được chi tiết đề xuất.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}

export async function layLichSuKeHoach(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ke-hoach/lich-su`, {
    token,
    signal,
  })

  return docPhanHoiJson(response, 'Không tải được lịch sử trình duyệt kế hoạch.')
}

export async function tiepNhanDeXuat(cuocHopId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/tiep-nhan`, {
    method: 'POST',
    token,
  })

  if (!response.ok) {
    const loi = new Error('Không tiếp nhận được đề xuất.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}

export async function capNhatKeHoach(cuocHopId, duLieu, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ke-hoach`, {
    method: 'PUT',
    token,
    duLieu,
  })

  return docPhanHoiJson(response, 'Không cập nhật được kế hoạch.')
}

function docPhanHoiTaiLieu(response) {
  return docPhanHoiJson(response, 'Không xử lý được tài liệu kế hoạch.')
}

export async function layTaiLieuKeHoach(cuocHopId, token, signal) {
  const response = await goiApi(`/api/tai-lieu-ke-hoach/cuoc-hop/${cuocHopId}`, {
    token,
    signal,
  })

  return docPhanHoiTaiLieu(response)
}

export async function taiLenTaiLieuKeHoach(cuocHopId, file, token) {
  const formData = new FormData()
  formData.append('file', file)

  const response = await goiApi(`/api/tai-lieu-ke-hoach/cuoc-hop/${cuocHopId}`, {
    method: 'POST',
    token,
    body: formData,
  })

  return docPhanHoiTaiLieu(response)
}

export async function trinhKeHoach(cuocHopId, duLieu, token) {
  const response = await goiApi(`/api/quy-trinh-ke-hoach/${cuocHopId}/trinh`, {
    method: 'POST',
    token,
    duLieu,
  })

  return docPhanHoiJson(response, 'Không trình duyệt được kế hoạch.')
}

export async function guiKeHoachLenCapTren(cuocHopId, yeuCauId, duLieu, token) {
  const response = await goiApi(
    `/api/quy-trinh-ke-hoach/${cuocHopId}/yeu-cau/${yeuCauId}/gui-cap-tren`,
    {
      method: 'POST',
      token,
      duLieu,
    },
  )

  return docPhanHoiJson(response, 'Không chuyển được hồ sơ lên cấp trên.')
}

export async function layKeHoachChoToiDuyet(token, signal) {
  const response = await goiApi('/api/quy-trinh-ke-hoach/cua-toi/cho-duyet', {
    token,
    signal,
  })

  if (!response.ok) {
    const loi = new Error('Không tải được danh sách kế hoạch chờ duyệt.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}

export async function layHoSoThuKyLanhDao(token, signal) {
  const response = await goiApi('/api/quy-trinh-ke-hoach/thu-ky-lanh-dao/ho-so', {
    token,
    signal,
  })

  return docPhanHoiJson(response, 'Không tải được hồ sơ thư ký lãnh đạo.')
}

export function layTrangThaiHienThiCuocHop(cuocHop) {
  if (cuocHop.trangThai === 'DANG_CHUAN_BI'
      && cuocHop.buocXuLyHienTai === 'CHO_GUI_CAP_TREN') {
    return 'CHO_GUI_CAP_TREN'
  }

  if (
    cuocHop.trangThai === 'DANG_CHUAN_BI'
    && cuocHop.buocXuLyHienTai === 'LAP_KE_HOACH'
    && cuocHop.phanHoiKeHoach?.trangThai === 'TRA_LAI'
  ) {
    return 'TRA_LAI'
  }

  const buocChoDuyet = [
    'CHO_TRUONG_PHONG',
    'CHO_GUI_CAP_TREN',
    'CHO_THU_KY_LANH_DAO',
    'THU_KY_LANH_DAO_XU_LY',
    'CHO_LANH_DAO',
  ]

  if (cuocHop.trangThai === 'DANG_CHUAN_BI' && buocChoDuyet.includes(cuocHop.buocXuLyHienTai)) {
    return 'CHO_DUYET'
  }

  return cuocHop.trangThai
}

export async function layChiTietKeHoachChoDuyet(yeuCauId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/ke-hoach/cho-duyet/${yeuCauId}`, {
    token,
    signal,
  })

  if (!response.ok) {
    const loi = new Error('Không tải được chi tiết kế hoạch.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}

export async function xuLyYeuCauKeHoach(cuocHopId, yeuCauId, thaoTac, yKien, token) {
  const response = await goiApi(
    `/api/quy-trinh-ke-hoach/${cuocHopId}/yeu-cau/${yeuCauId}/${thaoTac}`,
    {
      method: 'POST',
      token,
      duLieu: { yKien },
    },
  )

  return docPhanHoiJson(response, 'Không xử lý được kế hoạch.')
}

function docPhanHoiLichHop(response) {
  return docPhanHoiJson(response, 'Không xử lý được lịch họp.')
}

export async function layDanhSachLichHop(token, signal) {
  const response = await goiApi('/api/lich-hop', { token, signal })
  return docPhanHoiLichHop(response)
}

export async function layLichTheoCuocHop(cuocHopId, token, signal) {
  const response = await goiApi(`/api/lich-hop/cuoc-hop/${cuocHopId}`, {
    token,
    signal,
  })

  return docPhanHoiLichHop(response)
}

export async function taoLichHop(duLieu, token) {
  const response = await goiApi('/api/lich-hop', {
    method: 'POST',
    token,
    duLieu,
  })

  return docPhanHoiLichHop(response)
}

export async function layKeHoachToiDaDuyet(token, signal) {
  const response = await goiApi('/api/quy-trinh-ke-hoach/cua-toi/da-duyet', {
    token,
    signal,
  })

  return docPhanHoiLichHop(response)
}

export async function xacNhanLichHop(lichHopId, token) {
  const response = await goiApi(`/api/lich-hop/${lichHopId}/xac-nhan`, {
    method: 'PUT',
    token,
  })

  return docPhanHoiLichHop(response)
}

export async function batDauCuocHop(cuocHopId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/bat-dau`, {
    method: 'POST',
    token,
  })

  return docPhanHoiJson(response, 'Không bắt đầu được cuộc họp.')
}

export async function ketThucCuocHop(cuocHopId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ket-thuc`, {
    method: 'POST',
    token,
  })

  return docPhanHoiJson(
    response,
    `Không kết thúc được cuộc họp (HTTP ${response.status}).`,
  )
}

export async function taoDeXuatCuocHop(duLieu, token) {
  const response = await goiApi('/api/cuoc-hop/de-xuat', {
    method: 'POST',
    token,
    duLieu,
  })

  return docPhanHoiJson(response, 'Không tạo được đề xuất.')
}

export async function guiDeXuatCuocHop(cuocHopId, token) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/gui-de-xuat`, {
    method: 'POST',
    token,
    duLieu: {},
  })

  return docPhanHoiJson(response, 'Không gửi được đề xuất.')
}

const API_PHAN_CONG_THU_KY = '/api/admin/phan-cong-thu-ky'

export async function layDuLieuPhanCongThuKy(token, signal) {
  const [nguoiDung, phongBan, phanCong] = await Promise.all(
    ['/api/admin/users', '/api/admin/departments', API_PHAN_CONG_THU_KY].map(async (url) => {
      const response = await goiApi(url, { token, signal })
      return docPhanHoiJson(response, 'Không tải được dữ liệu phân công thư ký.')
    }),
  )

  return { nguoiDung, phongBan, phanCong }
}

export async function luuPhanCongThuKy(nguoiDuocHoTroId, thuKyId, token) {
  const response = await goiApi(API_PHAN_CONG_THU_KY, {
    method: 'PUT',
    token,
    duLieu: { nguoiDuocHoTroId, thuKyId },
  })

  return docPhanHoiJson(
    response,
    response.status === 409
      ? 'Thư ký này đã được phân công cho người khác.'
      : 'Không lưu được phân công thư ký.',
  )
}

export async function huyPhanCongThuKy(nguoiDuocHoTroId, token) {
  const response = await goiApi(`${API_PHAN_CONG_THU_KY}/${nguoiDuocHoTroId}`, {
    method: 'DELETE',
    token,
  })

  return docPhanHoiJson(response, 'Không bỏ được phân công thư ký.')
}

export async function layLuaChonKeHoach(cuocHopId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/${cuocHopId}/ke-hoach/lua-chon`, {
    token,
    signal,
  })

  return docPhanHoiJson(response, 'Không tải được danh sách người dùng cho kế hoạch.')
}

export async function layPhongHopDeXuat(cuocHopId, token, khoangGio, signal) {
  const truyVan = khoangGio ? '?' + new URLSearchParams(khoangGio) : ''
  const response = await goiApi(`/api/lich-hop/cuoc-hop/${cuocHopId}/phong-hop${truyVan}`, {
    token,
    signal,
  })

  return docPhanHoiLichHop(response)
}

export async function layTaiLieuLanTrinh(yeuCauId, token, signal) {
  const response = await goiApi(`/api/tai-lieu-ke-hoach/yeu-cau/${yeuCauId}`, {
    token,
    signal,
  })

  return docPhanHoiTaiLieu(response)
}

export async function taiXuongTaiLieuLanTrinh(yeuCauId, taiLieu, token) {
  const response = await goiApi(
    `/api/tai-lieu-ke-hoach/yeu-cau/${yeuCauId}/tai-lieu/${taiLieu.taiLieuId}/tai-xuong`,
    { token },
  )

  if (!response.ok) return docPhanHoiTaiLieu(response)

  const url = URL.createObjectURL(await response.blob())
  const lienKet = document.createElement('a')
  lienKet.href = url
  lienKet.download = taiLieu.tenFileGoc || taiLieu.tenTaiLieu || 'tai-lieu-ke-hoach'
  document.body.append(lienKet)
  lienKet.click()
  lienKet.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}

export async function layChiTietLichCalendar(lichHopId, token, signal) {
  const response = await goiApi(`/api/lich-hop/${lichHopId}/chi-tiet-calendar`, {
    token,
    signal,
  })

  return docPhanHoiLichHop(response)
}

export async function xuLyLichKhongDuyet(lichHopId, thaoTac, lyDoTuChoi, token) {
  const response = await goiApi(`/api/lich-hop/${lichHopId}/${thaoTac}`, {
    method: 'PUT',
    token,
    duLieu: { lyDoTuChoi },
  })

  return docPhanHoiLichHop(response)
}

export async function layHoSoBanGiao(yeuCauId, token, signal) {
  const response = await goiApi(`/api/cuoc-hop/ke-hoach/da-duyet/${yeuCauId}`, {
    token,
    signal,
  })

  return docPhanHoiJson(response, 'Không tải được hồ sơ bàn giao.')
}