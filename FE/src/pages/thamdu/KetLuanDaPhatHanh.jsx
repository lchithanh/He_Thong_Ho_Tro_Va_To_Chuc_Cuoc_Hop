import { useEffect, useState } from 'react'
import { layKetLuanDaPhatHanhCuaToi } from '../../api/ketLuanCuocHopApi.js'
import TaiLieuKetLuan from '../thuky/TaiLieuKetLuan.jsx'

export default function KetLuanDaPhatHanh({ token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [idDangXem, setIdDangXem] = useState(null)

  useEffect(() => {
    const controller = new AbortController()

    layKetLuanDaPhatHanhCuaToi(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token])

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold">Kết luận đã phát hành</h2>

      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}

      {!loi && danhSach === null && (
        <p role="status" className="text-sm text-slate-600">Đang tải kết luận…</p>
      )}

      {danhSach?.length === 0 && (
        <p className="text-sm text-slate-600">Bạn chưa có kết luận đã phát hành.</p>
      )}

      {danhSach?.map((ketLuan) => {
        const dangXem = idDangXem === ketLuan.ketLuanId

        return (
          <article key={ketLuan.ketLuanId} className="rounded-lg border border-slate-200 p-4">
            <div className="flex flex-wrap items-center justify-between gap-2">
              <h3 className="font-semibold">{ketLuan.tieuDeCuocHop || `Cuộc họp #${ketLuan.cuocHopId}`}</h3>
              <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-semibold text-emerald-800">Đã phát hành</span>
            </div>
            <button
              type="button"
              aria-expanded={dangXem}
              onClick={() => setIdDangXem(dangXem ? null : ketLuan.ketLuanId)}
              className="mt-2 text-sm font-semibold text-emerald-800"
            >
              {dangXem ? 'Đóng chi tiết' : 'Xem kết luận'}
            </button>

            {dangXem && (
              <article className="mt-3 space-y-4 rounded-xl border border-slate-100 bg-slate-50 p-4 sm:p-6">
                <header className="border-b border-slate-200 pb-3">
                  <p className="text-xs font-semibold text-emerald-800">Kết luận cuộc họp</p>
                  <h4 className="mt-1 text-lg font-bold">{ketLuan.tieuDe}</h4>
                </header>
                <p className="whitespace-pre-wrap break-words text-sm leading-7">
                  {ketLuan.noiDung || 'Xem nội dung trong tệp đính kèm.'}
                </p>

                <TaiLieuKetLuan
                  cuocHopId={ketLuan.cuocHopId}
                  token={token}
                  choPhepTaiLen={false}
                />
              </article>
            )}
          </article>
        )
      })}
      
    </section>
  )
}



