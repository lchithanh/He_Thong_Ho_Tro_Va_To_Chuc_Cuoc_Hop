import { useEffect, useState } from 'react'
import {
  layChiTietBieuQuyet,
  layDanhSachBieuQuyetThamDu,
} from '../../api/bieuQuyetApi.js'
import FormBoPhieu from '../../components/FormBoPhieu.jsx'
import KetQuaBieuQuyetThamDu from './KetQuaBieuQuyetThamDu.jsx'

export default function BieuQuyetThamDu({ token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [phienDangXem, setPhienDangXem] = useState(null)
  const [chiTiet, setChiTiet] = useState(null)
  const [loiChiTiet, setLoiChiTiet] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachBieuQuyetThamDu(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      })

    return () => controller.abort()
  }, [token, lanTai])

  useEffect(() => {
    if (!phienDangXem) return

    const controller = new AbortController()
    const { cuocHopId, bieuQuyetId } = phienDangXem

    layChiTietBieuQuyet(cuocHopId, bieuQuyetId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setChiTiet(duLieu)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiChiTiet(loi.message)
      })

    return () => controller.abort()
  }, [phienDangXem, token])

  function taiLaiDanhSach() {
    setDanhSach(null)
    setLoiDanhSach('')
    setPhienDangXem(null)
    setChiTiet(null)
    setLoiChiTiet('')
    setLanTai((hienTai) => hienTai + 1)
  }

  function xemPhien(muc) {
    setChiTiet(null)
    setLoiChiTiet('')
    setPhienDangXem({ ...muc })
  }

  const luaChon = chiTiet?.luaChon ?? chiTiet?.danhSachLuaChon

  const nhomTheoCuocHop = new Map()
  for (const phien of danhSach ?? []) {
    const nhom = nhomTheoCuocHop.get(phien.cuocHopId) ?? []
    nhom.push(phien)
    nhomTheoCuocHop.set(phien.cuocHopId, nhom)
  }

  return (
    <>
      <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-lg font-bold">Tham gia biểu quyết</h2>
        <button
          type="button"
          onClick={taiLaiDanhSach}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
        >
          Tải lại
        </button>
      </div>

      {loiDanhSach && <p role="alert">{loiDanhSach}</p>}
      {!loiDanhSach && danhSach === null && <p role="status">Đang tải biểu quyết…</p>}
      {danhSach?.length === 0 && <p>Hiện chưa có biểu quyết đang mở dành cho bạn.</p>}

      {[...nhomTheoCuocHop.entries()].map(([cuocHopId, cacPhien]) => (
        <section key={cuocHopId} className="space-y-3 rounded-xl border border-slate-200 bg-slate-50 p-4">
          <h3 className="font-semibold">{cacPhien[0].tieuDeCuocHop || `Cuộc họp #${cuocHopId}`}</h3>
          {cacPhien.map((muc) => (
            <div key={muc.bieuQuyetId} className="space-y-2 rounded-lg border border-slate-200 p-3">
              <h4 className="text-sm font-semibold">{muc.tieuDeBieuQuyet}</h4>
              <button
                type="button"
                onClick={() => xemPhien(muc)}
                disabled={phienDangXem?.bieuQuyetId === muc.bieuQuyetId && !loiChiTiet}
                className="rounded-lg bg-emerald-800 px-3 py-2 text-sm text-white disabled:opacity-60"
              >
                Bỏ phiếu
              </button>

              {phienDangXem?.bieuQuyetId === muc.bieuQuyetId && (
                <div className="space-y-2 border-t border-slate-200 pt-3">
                  {loiChiTiet && <p role="alert">{loiChiTiet}</p>}
                  {!loiChiTiet && !chiTiet && <p role="status">Đang tải phương án…</p>}
                  {chiTiet && (
                    Array.isArray(luaChon) && luaChon.length > 0 ? (
                      <FormBoPhieu
                        cuocHopId={muc.cuocHopId}
                        chiTiet={chiTiet}
                        luaChon={luaChon}
                        token={token}
                      />
                    ) : (
                      <p role="alert">Không tải được các phương án bỏ phiếu.</p>
                    )
                  )}
                </div>
              )}
            </div>
          ))}
        </section>
      ))}
      </section>
      <KetQuaBieuQuyetThamDu token={token} />
    </>
  )
  
}



