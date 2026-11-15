import { goiApi } from './apiClient.js'

export async function dangNhap(taiKhoan, matKhau) {
  const response = await goiApi('/api/auth/login', {
    method: 'POST',
    duLieu: { taiKhoan, matKhau },
  })

  if (!response.ok) {
    const loi = new Error('Đăng nhập thất bại.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}

export async function layThongTinNguoiDung(token, signal) {
  const response = await goiApi('/api/auth/me', {
    token,
    signal,
  })

  if (!response.ok) {
    const loi = new Error('Không xác thực được phiên đăng nhập.')
    loi.status = response.status
    throw loi
  }

  return response.json()
}
