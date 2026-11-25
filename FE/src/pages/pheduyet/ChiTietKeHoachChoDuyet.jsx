import { useEffect, useRef, useState } from 'react'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import { LOP_NUT_PHU } from '../../components/giaoDienPhongHop.js'
import LichCuaHoSo from '../thuky/LichCuaHoSo.jsx'
import HoSoDaTrinh from '../thuky/HoSoDaTrinh.jsx'
import TienDoCuocHop from '../thuky/TienDoCuocHop.jsx'
import LichSuKeHoach from '../thuky/LichSuKeHoach.jsx'

export default function ChiTietKeHoachChoDuyet({ keHoach, token, onDong, onDaXuLy }) {
  const [dangXuLy, setDangXuLy] = useState(false)
  const [mucBoSung, setMucBoSung] = useState(null)
  const tieuDe = useRef(null)
  const { cuocHopId, yeuCauPheDuyetId } = keHoach
  const capTruongPhong = keHoach.capPheDuyet === 'TRUONG_PHONG'

  useEffect(() => { tieuDe.current?.focus() }, [])

  return (
    <section className="space-y-4" aria-labelledby="ten-ho-so-duyet">
      <button type="button" onClick={onDong} disabled={dangXuLy} className={LOP_NUT_PHU + ' bg-white'}>
        <ArrowLeft size={16} aria-hidden="true" /> Về danh sách chờ duyệt
      </button>
      <header className="space-y-1 rounded-2xl bg-[#102a43] p-4 text-white sm:p-6">
        <p className="text-sm text-blue-100">Duyệt kế hoạch và lịch</p>
        <h2 id="ten-ho-so-duyet" ref={tieuDe} tabIndex={-1} className="wrap-anywhere text-xl font-bold">{keHoach.tieuDe}</h2>
      </header>
      <TienDoCuocHop
        chiTiet={{
          trangThai: 'DANG_CHUAN_BI',
          buocXuLyHienTai: capTruongPhong ? 'CHO_TRUONG_PHONG' : 'CHO_LANH_DAO',
        }}
        viecTiepTheo={capTruongPhong ? 'Lần trình cấp trưởng phòng đang chờ bạn xử lý.' : 'Lần trình cấp lãnh đạo đang chờ bạn xử lý.'}
      />
      <HoSoDaTrinh
        yeuCauId={yeuCauPheDuyetId}
        token={token}
        yeuCauDuyet={keHoach}
        onDangXuLy={setDangXuLy}
        onDaXuLy={onDaXuLy}
      />
      <div className="flex flex-wrap gap-2">
        {[['lich-su', 'Xem lịch sử trình duyệt'], ['lich', 'Xem các lịch của cuộc họp']].map(([id, ten]) => (
          <button key={id} type="button" className={LOP_NUT_PHU + ' bg-white'} aria-expanded={mucBoSung === id}
            onClick={() => setMucBoSung((muc) => muc === id ? null : id)}>{ten}</button>
        ))}
      </div>
      {mucBoSung === 'lich-su' && <LichSuKeHoach cuocHopId={cuocHopId} token={token} />}
      {mucBoSung === 'lich' && <LichCuaHoSo cuocHopId={cuocHopId} token={token} />}
    </section>
  )
}
