const CHU_KY_CAP_NHAT_MS = 10000
const DO_TRE_GOM_CAP_NHAT_MS = 120
const TEN_KENH = 'quan-ly-cuoc-hop-cap-nhat'

let phienBan = 0
let henTaiLai
let chuKy
let kenh
const nguoiTheoDoi = new Set()

function henCapNhat() {
  if (henTaiLai !== undefined || nguoiTheoDoi.size === 0) return

  henTaiLai = window.setTimeout(() => {
    henTaiLai = undefined
    phienBan += 1
    for (const capNhat of nguoiTheoDoi) capNhat()
  }, DO_TRE_GOM_CAP_NHAT_MS)
}

function capNhatKhiDangXem() {
  if (document.visibilityState !== 'hidden') henCapNhat()
}

export function capNhatKhiMayChuBaoThayDoi() {
  capNhatKhiDangXem()
}

export function docPhienDongBo() {
  return phienBan
}

export function theoDoiDongBo(capNhat) {
  nguoiTheoDoi.add(capNhat)
  if (nguoiTheoDoi.size === 1) {
    if (typeof window.BroadcastChannel === 'function') {
      kenh = new window.BroadcastChannel(TEN_KENH)
      kenh.onmessage = capNhatKhiDangXem
    }
    chuKy = window.setInterval(capNhatKhiDangXem, CHU_KY_CAP_NHAT_MS)
    window.addEventListener('focus', capNhatKhiDangXem)
    window.addEventListener('online', capNhatKhiDangXem)
    document.addEventListener('visibilitychange', capNhatKhiDangXem)
  }

  return () => {
    nguoiTheoDoi.delete(capNhat)
    if (nguoiTheoDoi.size > 0) return

    window.clearInterval(chuKy)
    window.clearTimeout(henTaiLai)
    henTaiLai = undefined
    kenh?.close()
    kenh = undefined
    window.removeEventListener('focus', capNhatKhiDangXem)
    window.removeEventListener('online', capNhatKhiDangXem)
    document.removeEventListener('visibilitychange', capNhatKhiDangXem)
  }
}

export function thongBaoDaThayDoiDuLieu() {
  if (typeof window === 'undefined') return
  henCapNhat()
  // Các tab chỉ chia sẻ tín hiệu tải lại, mọi truy vấn dùng phiên đăng nhập của chính tab đó.
  kenh?.postMessage('cap-nhat')
}
