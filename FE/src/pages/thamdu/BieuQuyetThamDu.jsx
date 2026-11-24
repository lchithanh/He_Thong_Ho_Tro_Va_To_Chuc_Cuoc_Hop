import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import { NhanHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NOI, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import KhungGioBieuQuyet from '../../components/KhungGioBieuQuyet.jsx'
import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import {
  layChiTietBieuQuyet,
  layDanhSachBieuQuyetThamDu,
} from '../../api/bieuQuyetApi.js'
import FormBoPhieu from '../../components/FormBoPhieu.jsx'
import KetQuaBieuQuyetThamDu from './KetQuaBieuQuyetThamDu.jsx'

export default function BieuQuyetThamDu({ token, cuocHopId: cuocHopIdLoc, nghiQuyetId, chiPhienDangMo = false }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [phienDangXem, setPhienDangXem] = useState(null)
  const [chiTiet, setChiTiet] = useState(null)
  const [loiChiTiet, setLoiChiTiet] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layDanhSachBieuQuyetThamDu(token, controller.signal, cuocHopIdLoc)
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

    const controller = new AbortController()
    const { cuocHopId, bieuQuyetId } = phienDangXem

    layChiTietBieuQuyet(cuocHopId, bieuQuyetId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setChiTiet(duLieu)
          setLoiChiTiet('')
        }
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiChiTiet(loi.message)
      })

    return () => controller.abort()
  }, [phienDangXem, token, phienDongBo])

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

  const danhSachLoc = nghiQuyetId == null ? danhSach : danhSach?.filter((muc) => muc.nghiQuyetId === nghiQuyetId)
  const nhomTheoCuocHop = new Map()
  for (const phien of danhSachLoc ?? []) {
    const nhom = nhomTheoCuocHop.get(phien.cuocHopId) ?? []
    nhom.push(phien)
    nhomTheoCuocHop.set(phien.cuocHopId, nhom)
  }

  if (chiPhienDangMo && !loiDanhSach && !danhSachLoc?.length) return null

  return (
    <>
      <section className={LOP_THE + ' space-y-3' + (chiPhienDangMo ? ' mt-4' : '')}>
        <TieuDeHoSo Icon={Vote} cap={cuocHopIdLoc ? 'h3' : 'h2'}
          thaoTac={<button type="button" onClick={taiLaiDanhSach} className={LOP_NUT_PHU}>Tải lại</button>}>
          {cuocHopIdLoc ? 'Phiên đang mở' : 'Tham gia biểu quyết'}
        </TieuDeHoSo>

        {loiDanhSach && <p role="alert">{loiDanhSach}</p>}
        {!loiDanhSach && danhSach === null && <p role="status">Đang tải biểu quyết…</p>}
        {danhSachLoc?.length === 0 && <p className={LOP_CHU_PHU}>Hiện chưa có biểu quyết đang mở dành cho bạn.</p>}

        {[...nhomTheoCuocHop.entries()].map(([cuocHopId, cacPhien]) => (
          <section key={cuocHopId} className="space-y-3">
            {!cuocHopIdLoc && <h3 className="font-semibold">{cacPhien[0].tieuDeCuocHop || `Cuộc họp #${cuocHopId}`}</h3>}
            {cacPhien.map((muc) => (
              <article key={muc.bieuQuyetId} className={LOP_NOI + ' space-y-3'}>
                <TieuDeHoSo Icon={Vote} cap="h4" thaoTac={<NhanHoSo mau="luc">Đang mở</NhanHoSo>}>
                  {muc.tieuDeBieuQuyet ?? muc.tieuDe}
                </TieuDeHoSo>
                <KhungGioBieuQuyet muc={muc} />
                <button
                  type="button"
                  onClick={() => xemPhien(muc)}
                  disabled={phienDangXem?.bieuQuyetId === muc.bieuQuyetId && !loiChiTiet}
                  className={LOP_NUT_CHINH}
                >
                  Bỏ phiếu
                </button>

                {phienDangXem?.bieuQuyetId === muc.bieuQuyetId && (
                  <div className="space-y-2 border-t border-slate-200 phong-toi:border-phong-vien pt-3">
                    {loiChiTiet && <p role="alert">{loiChiTiet}</p>}
                    {!loiChiTiet && !chiTiet && <p role="status">Đang tải phương án…</p>}
                    {chiTiet && (
                      Array.isArray(luaChon) && luaChon.length > 0 ? (
                        <FormBoPhieu
                          key={muc.bieuQuyetId}
                          cuocHopId={muc.cuocHopId}
                          chiTiet={chiTiet}
                          luaChon={luaChon}
                          token={token}
                          hienTieuDe={false}
                        />
                      ) : (
                        <p role="alert">Không tải được các phương án bỏ phiếu.</p>
                      )
                    )}
                  </div>
                )}
              </article>
            ))}
          </section>
        ))}
      </section>
      {!chiPhienDangMo && <div className="mt-4"><KetQuaBieuQuyetThamDu token={token} cuocHopId={cuocHopIdLoc} nghiQuyetId={nghiQuyetId} /></div>}
    </>
  )
  
}
