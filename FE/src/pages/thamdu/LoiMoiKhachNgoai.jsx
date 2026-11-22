import { useEffect, useState } from 'react'
import { phanHoiLoiMoiKhach, xemLoiMoiKhach } from '../../api/nghiepVuCuocHopApi.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'

const NUT = 'rounded-lg border border-slate-300 px-4 py-2.5 text-sm font-semibold disabled:opacity-60'

export default function LoiMoiKhachNgoai() {
  const [loiMoi, setLoiMoi] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const ma = window.location.hash.slice(1)

  useEffect(() => {
    const controller = new AbortController()
    xemLoiMoiKhach(ma, controller.signal)
      .then(setLoiMoi)
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [ma])

  async function phanHoi(thamDu) {
    setDangGui(true)
    setLoi('')
    try {
      setLoiMoi(await phanHoiLoiMoiKhach(ma, thamDu))
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  return (
    <main className="min-h-screen bg-slate-100 px-4 py-10 text-slate-950">
      <article className="mx-auto max-w-2xl overflow-hidden rounded-2xl bg-white shadow-sm">
        <header className="space-y-2 bg-[#0f2744] p-6 text-white sm:p-8">
          <p className="text-sm text-slate-200">Lời mời họp dành cho khách</p>
          <h1 className="break-words text-2xl font-bold sm:text-3xl">{loiMoi?.tieuDe || 'Thông tin lời mời'}</h1>
        </header>
        <div className="space-y-5 p-6 sm:p-8">
          {loi && <p role="alert" className="text-red-800">{loi}</p>}
          {!loiMoi && !loi && <p role="status">Đang tải lời mời…</p>}
          {loiMoi && (
            <>
              <p>Xin chào <strong>{loiMoi.hoTen}</strong>.</p>
              {loiMoi.trangThaiLich === 'CHO_DOI_LICH' && <p role="status">Cuộc họp đang chờ xác nhận giờ mới.</p>}
              {loiMoi.trangThaiLich === 'DA_HUY' && <p role="status">Lịch họp đã hủy hoặc chưa có giờ họp được xác nhận.</p>}
              {loiMoi.trangThaiLich === 'DA_XAC_NHAN' && (
                <>
                  <dl className="grid gap-3 sm:grid-cols-2">
                    {[
                      ['Bắt đầu (giờ Việt Nam)', dinhDangNgayGio(loiMoi.batDau)],
                      ['Kết thúc (giờ Việt Nam)', dinhDangNgayGio(loiMoi.ketThuc)],
                      ['Chủ trì', loiMoi.chuTri],
                      ['Địa điểm', loiMoi.diaDiem],
                    ].filter(([, giaTri]) => giaTri).map(([nhan, giaTri]) => (
                      <div key={nhan} className="rounded-lg bg-slate-50 p-3">
                        <dt className="text-xs text-slate-600">{nhan}</dt>
                        <dd className="font-semibold">{giaTri}</dd>
                      </div>
                    ))}
                  </dl>
                  {loiMoi.noiDung && <section>
                    <h2 className="font-bold">Nội dung cuộc họp</h2>
                    <p className="mt-1 whitespace-pre-wrap break-words">{loiMoi.noiDung}</p>
                  </section>}
                  {loiMoi.duongDanHop && <a href={loiMoi.duongDanHop} target="_blank" rel="noreferrer"
                    className="font-semibold text-emerald-800 underline">Mở phòng họp trực tuyến</a>}
                  {loiMoi.duocPhanHoi && <div className="flex flex-wrap gap-2">
                    <button className={NUT + ' bg-emerald-800 text-white'} type="button" disabled={dangGui}
                      onClick={() => phanHoi(true)}>Xác nhận tham dự</button>
                    <button className={NUT} type="button" disabled={dangGui}
                      onClick={() => phanHoi(false)}>Từ chối tham dự</button>
                  </div>}
                  {loiMoi.trangThaiMoi === 'DONG_Y' && <p role="status">Bạn đã xác nhận tham dự.</p>}
                  {loiMoi.trangThaiMoi === 'TU_CHOI' && <p role="status">Bạn đã từ chối tham dự.</p>}
                </>
              )}
            </>
          )}
        </div>
      </article>
    </main>
  )
}
