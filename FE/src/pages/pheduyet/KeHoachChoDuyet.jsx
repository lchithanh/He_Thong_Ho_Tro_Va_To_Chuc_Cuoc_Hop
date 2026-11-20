import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { layKeHoachChoToiDuyet } from '../../api/cuocHopApi.js'
import ChiTietKeHoachChoDuyet from './ChiTietKeHoachChoDuyet.jsx'

export default function KeHoachChoDuyet({ token, cuocHopCanDuyet, viecDaChon }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [yeuCauDaChon, setYeuCauDangXem] = useState(undefined)
  const yeuCauMoTuHoSo = cuocHopCanDuyet == null ? null
    : danhSach?.find((muc) => String(muc.cuocHopId) === String(cuocHopCanDuyet))
  const yeuCauDangXem = yeuCauDaChon === undefined
    ? yeuCauMoTuHoSo?.yeuCauPheDuyetId
    : yeuCauDaChon
  const [thongBao, setThongBao] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function taiDanhSach() {
      try {
        const duLieu = await layKeHoachChoToiDuyet(token, controller.signal)
        if (controller.signal.aborted) return
        const ids = viecDaChon && new Set(viecDaChon.map((muc) => muc.yeuCauPheDuyetId))
        setDanhSach(ids ? duLieu.filter((muc) => ids.has(muc.yeuCauPheDuyetId)) : duLieu)
        setLoi('')
      } catch (loiTaiDanhSach) {
        if (!controller.signal.aborted) setLoi(loiTaiDanhSach.message)
      }
    }

    taiDanhSach()
    return () => controller.abort()
  }, [token, phienDongBo, viecDaChon])

  return (
    <section className="space-y-4 rounded-xl border border-[#e2e8f0] bg-white p-5">
      <h2 className="text-lg font-bold">Kế hoạch chờ tôi duyệt</h2>
      {thongBao && (
        <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-900">
          {thongBao}
        </p>
      )}
      {!loi && danhSach && cuocHopCanDuyet != null && yeuCauDaChon === undefined && !yeuCauMoTuHoSo && (
        <p role="status" className="rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-700">
          Cuộc họp này hiện không có yêu cầu chờ bạn duyệt.
        </p>
      )}
      {loi ? (
        <p role="alert" className="rounded-lg bg-red-50 p-4 text-sm text-red-800">
          {loi}
        </p>
      ) : danhSach === null ? (
        <p role="status" className="text-sm text-slate-600">Đang tải kế hoạch…</p>
      ) : danhSach.length === 0 ? (
        <p className="rounded-lg border border-slate-200 bg-white p-4 text-sm text-slate-600">
          Hiện chưa có kế hoạch được giao cho bạn duyệt.
        </p>
      ) : (
        <ul className="space-y-3">
          {danhSach.map((keHoach) => (
            <li
              key={keHoach.yeuCauPheDuyetId}
              className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-4"
            >
              <div>
                <h3 className="font-semibold">{keHoach.tieuDe}</h3>
                <p className="text-sm text-slate-600">Trình duyệt · {dinhDangNgayGio(keHoach.thoiGianTrinh)}</p>
                <p className="mt-1 text-sm text-slate-600">
                  Cuộc họp #{keHoach.cuocHopId} · Yêu cầu #{keHoach.yeuCauPheDuyetId}
                </p>
              </div>
              <span className="rounded-full bg-amber-50 px-3 py-1 text-xs font-semibold text-amber-900">
                Chờ duyệt
              </span>
              <button
                type="button"
                onClick={() => setYeuCauDangXem(keHoach.yeuCauPheDuyetId)}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-emerald-800"
              >
                Xem chi tiết
              </button>

              {yeuCauDangXem === keHoach.yeuCauPheDuyetId && (
                <div className="min-w-0 basis-full">
                  <ChiTietKeHoachChoDuyet
                    keHoach={keHoach}
                    token={token}
                    onDong={() => setYeuCauDangXem(null)}
                    onDaXuLy={(yeuCauId, noiDung) => {
                      setDanhSach((hienTai) =>
                        hienTai.filter((muc) => muc.yeuCauPheDuyetId !== yeuCauId),
                      )
                      setYeuCauDangXem(null)
                      setThongBao(noiDung)
                    }}
                  />
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
