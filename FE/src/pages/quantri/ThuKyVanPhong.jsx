import { useEffect, useState } from 'react'
import { goiQuyenVanPhong } from '../../api/nhomLienPhongApi.js'

export default function ThuKyVanPhong({ token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangLuu, setDangLuu] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    goiQuyenVanPhong('', token, { signal: controller.signal })
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoi(error.message)
      })
    return () => controller.abort()
  }, [token])

  async function capNhat(nguoi) {
    setDangLuu(nguoi.nguoiDungId)
    setLoi('')
    const vanPhong = !nguoi.vanPhong
    try {
      await goiQuyenVanPhong(`/${nguoi.nguoiDungId}`, token, { method: 'PUT', duLieu: { vanPhong } })
      setDanhSach((ds) => ds.map((muc) => muc.nguoiDungId === nguoi.nguoiDungId ? { ...muc, vanPhong } : muc))
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangLuu(null)
    }
  }

  return <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-4 text-slate-900">
    <h1 className="text-lg font-bold">Quyền thư ký Văn phòng</h1>
    <p className="text-sm">Cấp quyền điều phối nhóm liên phòng và duyệt kế hoạch họp thường theo phân công.</p>
    {loi && <p role="alert" className="text-sm text-rose-800">{loi}</p>}
    {!loi && danhSach === null && <p role="status">Đang tải tài khoản thư ký…</p>}
    {danhSach?.length === 0 && <p className="text-sm">Chưa có tài khoản thư ký đang hoạt động.</p>}
    <ul className="divide-y divide-slate-200">
      {danhSach?.map((nguoi) => <li key={nguoi.nguoiDungId} className="flex items-center justify-between gap-3 py-3">
        <div><p className="font-semibold">{nguoi.hoTen}</p>
          <p className="text-sm">{nguoi.vanPhong ? 'Có quyền Văn phòng' : 'Thư ký'}</p>
        </div>
        <button type="button" disabled={dangLuu !== null} onClick={() => capNhat(nguoi)}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold hover:bg-slate-50 disabled:opacity-60">
          {dangLuu === nguoi.nguoiDungId ? 'Đang lưu…' : nguoi.vanPhong ? 'Thu hồi quyền' : 'Cấp quyền'}
        </button>
      </li>)}
    </ul>
  </section>
}
