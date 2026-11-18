import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  layLoiMoiCuaToi,
  phanHoiLoiMoiCuocHop,
} from '../../api/thanhVienCuocHopApi.js'

export default function PhanHoiLoiMoi({ token }) {
  const phienDongBo = useDongBoDuLieu()
  const [loiMoi, setLoiMoi] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiPhanHoi, setLoiPhanHoi] = useState('')
  const [cuocHopDangGui, setCuocHopDangGui] = useState(null)
  const [thongBao, setThongBao] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layLoiMoiCuaToi(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setLoiMoi(duLieu)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    return () => controller.abort()
  }, [token, phienDongBo])

  async function xuLyPhanHoi(cuocHop, thamDu) {
    if (cuocHopDangGui !== null) return

    setCuocHopDangGui(cuocHop.cuocHopId)
    setLoiPhanHoi('')
    setThongBao('')

    try {
      await phanHoiLoiMoiCuocHop(cuocHop.cuocHopId, thamDu, token)

      setLoiMoi((hienTai) =>
        hienTai.filter((muc) => muc.cuocHopId !== cuocHop.cuocHopId),
      )
      setThongBao(
        thamDu
          ? `Đã xác nhận tham dự: ${cuocHop.tieuDe}.`
          : `Đã từ chối lời mời: ${cuocHop.tieuDe}.`,
      )
    } catch (loi) {
      setLoiPhanHoi(loi.message)
    } finally {
      setCuocHopDangGui(null)
    }
  }

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold">Cuộc họp chờ phản hồi</h2>

      {loiTai && <p role="alert" className="text-sm text-red-800">{loiTai}</p>}
      {loiPhanHoi && <p role="alert" className="text-sm text-red-800">{loiPhanHoi}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}

      {!loiTai && loiMoi === null && (
        <p role="status" className="text-sm text-slate-600">Đang tải lời mời…</p>
      )}

      {loiMoi?.length === 0 && (
        <p className="text-sm text-slate-600">Bạn chưa có lời mời chờ phản hồi.</p>
      )}

      <ul className="space-y-3">
        {loiMoi?.map((cuocHop) => (
          <li key={cuocHop.cuocHopId} className="space-y-3 rounded-lg border border-slate-200 p-4">
            <div>
              <h3 className="font-semibold">{cuocHop.tieuDe}</h3>
              <p className="text-sm text-slate-600">
                {cuocHop.maCuocHop || `#${cuocHop.cuocHopId}`}
              </p>
              <p className="mt-2 text-sm font-medium text-slate-700">
                {cuocHop.thoiGianBatDau
                  ? `${new Date(cuocHop.thoiGianBatDau).toLocaleString('vi-VN')} – ${new Date(cuocHop.thoiGianKetThuc).toLocaleString('vi-VN')}`
                  : 'Chưa có thông tin giờ họp'}
                {' · '}{cuocHop.tenPhongHop || (cuocHop.loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : 'Chưa có thông tin phòng')}
              </p>
            </div>

            <details className="rounded-lg bg-slate-50 px-3 py-2 text-sm">
              <summary className="cursor-pointer font-semibold text-emerald-800">Xem chi tiết cuộc họp</summary>
              <dl className="mt-3 space-y-2">
                {[
                  ['Chủ trì', cuocHop.tenNguoiChuTri],
                  ['Mục tiêu', cuocHop.mucTieu],
                  ['Nội dung dự kiến', cuocHop.noiDungDuKien],
                ].map(([nhan, giaTri]) => (
                  <div key={nhan}><dt className="font-semibold">{nhan}</dt><dd className="whitespace-pre-wrap break-words">{giaTri || 'Chưa có thông tin'}</dd></div>
                ))}
              </dl>
            </details>
            <div className="flex flex-wrap gap-2">
              <button
                type="button"
                disabled={cuocHopDangGui !== null}
                onClick={() => xuLyPhanHoi(cuocHop, true)}
                className="rounded-lg bg-emerald-800 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60"
              >
                Xác nhận tham dự
              </button>

              <button
                type="button"
                disabled={cuocHopDangGui !== null}
                onClick={() => xuLyPhanHoi(cuocHop, false)}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold disabled:opacity-60"
              >
                Từ chối
              </button>
            </div>
          </li>
        ))}
      </ul>
    </section>
  )
}



