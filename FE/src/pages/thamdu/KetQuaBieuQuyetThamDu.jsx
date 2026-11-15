import { useEffect, useState } from 'react'
import {
  layDanhSachBieuQuyetDaDongCuaToi,
  layKetQuaBieuQuyet,
} from '../../api/bieuQuyetApi.js'

export default function KetQuaBieuQuyetThamDu({ token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [phienDangXem, setPhienDangXem] = useState(null)
  const [ketQua, setKetQua] = useState(null)
  const [loiKetQua, setLoiKetQua] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachBieuQuyetDaDongCuaToi(token, controller.signal)
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

    let conHieuLuc = true
    const { cuocHopId, bieuQuyetId } = phienDangXem

    layKetQuaBieuQuyet(cuocHopId, bieuQuyetId, token)
      .then((duLieu) => {
        if (conHieuLuc) setKetQua(duLieu)
      })
      .catch((loi) => {
        if (conHieuLuc) setLoiKetQua(loi.message)
      })

    return () => {
      conHieuLuc = false
    }
  }, [phienDangXem, token])

  function taiLaiDanhSach() {
    setDanhSach(null)
    setLoiDanhSach('')
    setPhienDangXem(null)
    setKetQua(null)
    setLoiKetQua('')
    setLanTai((lan) => lan + 1)
  }

  function xemKetQua(muc) {
    setKetQua(null)
    setLoiKetQua('')
    setPhienDangXem({
      cuocHopId: muc.cuocHopId,
      bieuQuyetId: muc.bieuQuyetId,
    })
  }

  const nhomTheoCuocHop = new Map()
  for (const phien of danhSach ?? []) {
    const nhom = nhomTheoCuocHop.get(phien.cuocHopId) ?? []
    nhom.push(phien)
    nhomTheoCuocHop.set(phien.cuocHopId, nhom)
  }

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-lg font-bold">Kết quả biểu quyết</h2>
        <button
          type="button"
          onClick={taiLaiDanhSach}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
        >
          Tải lại
        </button>
      </div>

      {loiDanhSach && <p role="alert">{loiDanhSach}</p>}
      {danhSach === null && !loiDanhSach && (
        <p role="status">Đang tải danh sách kết quả…</p>
      )}
      {danhSach?.length === 0 && <p>Chưa có biểu quyết đã đóng.</p>}

      {[...nhomTheoCuocHop.entries()].map(([cuocHopId, cacPhien]) => (
        <section key={cuocHopId} className="space-y-3 rounded-xl border border-slate-200 bg-slate-50 p-4">
          <h3 className="font-semibold">{cacPhien[0].tieuDeCuocHop || `Cuộc họp #${cuocHopId}`}</h3>
          {cacPhien.map((muc) => {
            const dangXem = phienDangXem?.bieuQuyetId === muc.bieuQuyetId

            return (
              <article
                key={muc.bieuQuyetId}
                className="space-y-2 rounded-lg border border-slate-200 p-3"
              >
                  <h4 className="text-sm font-semibold">{muc.tieuDeBieuQuyet}</h4>

                <button
                  type="button"
                  onClick={() => xemKetQua(muc)}
                  disabled={dangXem && !ketQua && !loiKetQua}
                  className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                >
                  Xem kết quả
                </button>

                {dangXem && (
                  <div className="space-y-2 border-t border-slate-200 pt-3 text-sm">
                    {loiKetQua && <p role="alert">{loiKetQua}</p>}
                    {!ketQua && !loiKetQua && <p role="status">Đang tải kết quả…</p>}

                    {ketQua && (
                      <>
                        <p>Tổng số phiếu: {ketQua.tongSoPhieu}</p>
                        {ketQua.tongSoPhieu === 0 && (
                          <p>Chưa có phiếu; chưa thể xác định kết quả.</p>
                        )}
                        {ketQua.hoaPhieu && (
                          <p>Hòa phiếu; chưa có phương án thắng.</p>
                        )}
                        {ketQua.luaChonThangId != null && (
                          <p>
                            Phương án nhiều phiếu nhất:{' '}
                            {ketQua.luaChon.find(
                              (luaChon) => luaChon.luaChonId === ketQua.luaChonThangId,
                            )?.noiDung}
                          </p>
                        )}

                        <ul className="space-y-1">
                          {ketQua.luaChon.map((luaChon) => (
                            <li key={luaChon.luaChonId}>
                              {luaChon.noiDung}: {luaChon.soPhieu} phiếu
                            </li>
                          ))}
                        </ul>
                      </>
                    )}
                  </div>
                )}
              </article>
            )
          })}
        </section>
      ))}
      
    </section>
  )
}



