import { useEffect, useState } from 'react'
import FileClock from 'lucide-react/dist/esm/icons/file-clock.mjs'
import { LocHoSo, NhanHoSo, TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { khopTuKhoa, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { layKeHoachChoToiDuyet } from '../../api/cuocHopApi.js'
import ChiTietKeHoachChoDuyet from './ChiTietKeHoachChoDuyet.jsx'

const CAC_CAP = [['tat-ca', 'Tất cả'], ['TRUONG_PHONG', 'Cấp trưởng phòng'], ['LANH_DAO', 'Cấp lãnh đạo']]
const TEN_CAP = { TRUONG_PHONG: 'Cấp trưởng phòng', LANH_DAO: 'Cấp lãnh đạo' }

export default function KeHoachChoDuyet({ token, cuocHopCanDuyet, viecDaChon }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [lanTai, setLanTai] = useState(0)
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('tat-ca')
  const [yeuCauDaChon, setYeuCauDaChon] = useState(undefined)
  const [thongBao, setThongBao] = useState('')
  const yeuCauMoTuHoSo = cuocHopCanDuyet == null ? null
    : danhSach?.find((muc) => String(muc.cuocHopId) === String(cuocHopCanDuyet))
  const yeuCauDangXem = yeuCauDaChon === undefined ? yeuCauMoTuHoSo : yeuCauDaChon
  const daLoc = danhSach?.filter((muc) => (boLoc === 'tat-ca' || muc.capPheDuyet === boLoc) && khopTuKhoa(muc.tieuDe, tuKhoa))

  useEffect(() => {
    const controller = new AbortController()
    layKeHoachChoToiDuyet(token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        const ids = viecDaChon && new Set(viecDaChon.map((muc) => muc.yeuCauPheDuyetId))
        setDanhSach(ids ? duLieu.filter((muc) => ids.has(muc.yeuCauPheDuyetId)) : duLieu)
        setLoi('')
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoi(error.message)
      })
    return () => controller.abort()
  }, [token, phienDongBo, viecDaChon, lanTai])

  function moChiTiet(keHoach) {
    setYeuCauDaChon(keHoach)
    setThongBao('')
  }

  if (yeuCauDangXem) {
    return <ChiTietKeHoachChoDuyet key={yeuCauDangXem.yeuCauPheDuyetId} keHoach={yeuCauDangXem} token={token}
      onDong={() => setYeuCauDaChon(null)}
      onDaXuLy={(id, noiDung) => {
        setDanhSach((ds) => ds?.filter((muc) => muc.yeuCauPheDuyetId !== id))
        setYeuCauDaChon(null)
        setThongBao(noiDung)
      }} />
  }

  return (
    <section className={LOP_THE + ' space-y-4'}>
      <TieuDeHoSo Icon={FileClock} cap="h2" phu={danhSach && `${danhSach.length} hồ sơ được giao cho bạn`}>
        Kế hoạch và lịch chờ duyệt
      </TieuDeHoSo>
      {thongBao && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-sm text-emerald-900">{thongBao}</p>}
      {!loi && danhSach && cuocHopCanDuyet != null && yeuCauDaChon === undefined && !yeuCauMoTuHoSo && (
        <p role="status" className="rounded-lg bg-slate-50 px-3 py-2 text-sm text-slate-700">
          Cuộc họp này hiện không có yêu cầu chờ bạn duyệt.
        </p>
      )}
      {loi && <div className="space-y-2 rounded-lg bg-rose-50 p-3">
        <p role="alert" className="text-sm text-rose-900">{loi}</p>
        <button type="button" className={LOP_NUT_PHU} onClick={() => setLanTai((lan) => lan + 1)}>Tải lại hồ sơ</button>
      </div>}
      {!loi && danhSach == null && <p role="status" className="text-sm text-slate-600">Đang tải kế hoạch…</p>}
      {Boolean(danhSach?.length) && <LocHoSo tuKhoa={tuKhoa} onTuKhoa={setTuKhoa} nhanTim="Tìm theo tên cuộc họp"
        boLoc={boLoc} onBoLoc={setBoLoc} cacMuc={CAC_CAP} />}
      {!loi && daLoc?.length === 0 && <p className="rounded-xl border border-dashed border-slate-300 p-4 text-sm text-slate-600">
        {danhSach.length ? 'Không tìm thấy hồ sơ phù hợp.' : 'Hiện chưa có kế hoạch được giao cho bạn duyệt.'}
      </p>}
      {Boolean(daLoc?.length) && (
        <ul className="divide-y divide-slate-200 rounded-xl border border-slate-200">
          {daLoc.map((keHoach) => (
            <li key={keHoach.yeuCauPheDuyetId} className="flex flex-wrap items-center justify-between gap-3 p-4">
              <div className="min-w-0 flex-1 basis-64 space-y-1.5">
                <div className="flex flex-wrap items-center gap-2">
                  <h3 className="wrap-anywhere font-semibold">{keHoach.tieuDe}</h3>
                  <NhanHoSo mau="vang">Chờ bạn duyệt</NhanHoSo>
                  {TEN_CAP[keHoach.capPheDuyet] && <NhanHoSo>{TEN_CAP[keHoach.capPheDuyet]}</NhanHoSo>}
                </div>
                {keHoach.thoiGianBatDau && <p className="text-sm text-slate-700">
                  Lịch gửi duyệt · {dinhDangNgayGio(keHoach.thoiGianBatDau)}
                  {keHoach.thoiGianKetThuc && ` – ${dinhDangNgayGio(keHoach.thoiGianKetThuc)}`}
                </p>}
                {keHoach.thoiGianTrinh && <p className="text-xs text-slate-600">Trình lúc {dinhDangNgayGio(keHoach.thoiGianTrinh)}</p>}
              </div>
              <button type="button" onClick={() => moChiTiet(keHoach)} className={LOP_NUT_PHU}>Xem hồ sơ để duyệt</button>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
