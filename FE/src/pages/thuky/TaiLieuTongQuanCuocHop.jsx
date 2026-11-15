import { useEffect, useState } from 'react'
import { layTaiLieuKeHoach } from '../../api/cuocHopApi.js'
import { layTaiLieuDeXuat, taiXuongTaiLieuDeXuat } from '../../api/taiLieuDeXuatApi.js'

export default function TaiLieuTongQuanCuocHop({ cuocHopId, token, loai }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [loiTaiXuong, setLoiTaiXuong] = useState('')
  const [dangTai, setDangTai] = useState(null)
  const laDeXuat = loai === 'de-xuat'

  useEffect(() => {
    const controller = new AbortController()
    const tai = laDeXuat ? layTaiLieuDeXuat : layTaiLieuKeHoach
    tai(cuocHopId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setDanhSach(duLieu) })
      .catch((error) => {
        if (!controller.signal.aborted) setLoi(error.status === 403 ? 'Bạn không có quyền xem nhóm tài liệu này.' : error.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, laDeXuat])

  async function taiXuong(taiLieu) {
    setDangTai(taiLieu.taiLieuId)
    setLoiTaiXuong('')
    try {
      await taiXuongTaiLieuDeXuat(cuocHopId, taiLieu, token)
    } catch (error) {
      setLoiTaiXuong(error.message)
    } finally {
      setDangTai(null)
    }
  }

  return (
    <section className="space-y-2 rounded-xl border border-slate-200 p-4 text-sm">
      <h2 className="font-semibold">{laDeXuat ? 'Tài liệu đính kèm đề xuất' : 'Danh mục tài liệu kế hoạch'}</h2>
      {loi ? <p role="alert" className="text-red-800">{loi}</p>
        : danhSach === null ? <p role="status">Đang tải tài liệu…</p>
          : danhSach.length === 0 && <p className="text-slate-600">Chưa có tài liệu.</p>}
      {loiTaiXuong && <p role="alert" className="text-red-800">{loiTaiXuong}</p>}
      {danhSach?.length > 0 && (
        <ul className="divide-y divide-slate-100">
          {danhSach.map((tep) => {
            const ten = tep.tenFileGoc || tep.tenTaiLieu || 'Tài liệu #' + tep.taiLieuId
            return (
              <li key={tep.taiLieuId} className="break-words py-2">
                {laDeXuat ? (
                  <button type="button" disabled={dangTai !== null} onClick={() => taiXuong(tep)}
                    className="text-left font-semibold text-emerald-800 underline disabled:opacity-60">
                    {dangTai === tep.taiLieuId ? 'Đang tải ' : 'Tải '}{ten}
                  </button>
                ) : ten}
              </li>
            )
          })}
        </ul>
      )}
    </section>
  )
}



