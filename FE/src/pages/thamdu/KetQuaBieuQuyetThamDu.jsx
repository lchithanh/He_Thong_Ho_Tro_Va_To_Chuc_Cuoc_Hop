import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { NhanHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NOI, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import BangKetQuaBieuQuyet from '../../components/BangKetQuaBieuQuyet.jsx'
import KhungGioBieuQuyet from '../../components/KhungGioBieuQuyet.jsx'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  layDanhSachBieuQuyetDaDongCuaToi,
  layKetQuaBieuQuyet,
} from '../../api/bieuQuyetApi.js'

export default function KetQuaBieuQuyetThamDu({ token, cuocHopId: cuocHopIdLoc, nghiQuyetId }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [phienDangXem, setPhienDangXem] = useState(null)
  const [ketQua, setKetQua] = useState(null)
  const [loiKetQua, setLoiKetQua] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachBieuQuyetDaDongCuaToi(token, controller.signal, cuocHopIdLoc)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setDanhSach(cuocHopIdLoc == null ? duLieu : duLieu.filter((muc) => muc.cuocHopId === cuocHopIdLoc))
          setLoiDanhSach('')
        }
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      })

    return () => controller.abort()
  }, [token, lanTai, phienDongBo, cuocHopIdLoc])

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
  }, [phienDangXem, token, phienDongBo])

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

  const danhSachLoc = nghiQuyetId == null ? danhSach : danhSach?.filter((muc) => muc.nghiQuyetId === nghiQuyetId)
  const nhomTheoCuocHop = new Map()
  for (const phien of danhSachLoc ?? []) {
    const nhom = nhomTheoCuocHop.get(phien.cuocHopId) ?? []
    nhom.push(phien)
    nhomTheoCuocHop.set(phien.cuocHopId, nhom)
  }

  return (
    <section className={LOP_THE + ' space-y-4'}>
      <TieuDeHoSo Icon={Vote} cap={cuocHopIdLoc ? 'h3' : 'h2'}
        thaoTac={<button type="button" onClick={taiLaiDanhSach} className={LOP_NUT_PHU}>Tải lại</button>}>
        Kết quả biểu quyết
      </TieuDeHoSo>

      {loiDanhSach && <p role="alert">{loiDanhSach}</p>}
      {danhSach === null && !loiDanhSach && (
        <p role="status">Đang tải danh sách kết quả…</p>
      )}
      {danhSachLoc?.length === 0 && <p className={LOP_CHU_PHU}>Chưa có biểu quyết đã đóng.</p>}

      {[...nhomTheoCuocHop.entries()].map(([cuocHopId, cacPhien]) => (
        <section key={cuocHopId} className="space-y-3">
          {!cuocHopIdLoc && <h3 className="font-semibold">{cacPhien[0].tieuDeCuocHop || `Cuộc họp #${cuocHopId}`}</h3>}
          {cacPhien.map((muc) => {
            const dangXem = phienDangXem?.bieuQuyetId === muc.bieuQuyetId

            return (
              <article
                key={muc.bieuQuyetId}
                className={LOP_NOI + ' space-y-3'}
              >
                <TieuDeHoSo Icon={Vote} cap="h4" thaoTac={<NhanHoSo mau="xanh">Đã đóng</NhanHoSo>}>
                  {muc.tieuDeBieuQuyet ?? muc.tieuDe}
                </TieuDeHoSo>
                <KhungGioBieuQuyet muc={muc} />

                <button
                  type="button"
                  onClick={() => xemKetQua(muc)}
                  disabled={dangXem && !ketQua && !loiKetQua}
                  className={LOP_NUT_PHU}
                >
                  Xem kết quả
                </button>

                {dangXem && (
                  <div className="space-y-2 border-t border-slate-200 phong-toi:border-phong-vien pt-3 text-sm">
                    {loiKetQua && <p role="alert">{loiKetQua}</p>}
                    {!ketQua && !loiKetQua && <p role="status">Đang tải kết quả…</p>}

                    {ketQua && <BangKetQuaBieuQuyet ketQua={ketQua} />}
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



