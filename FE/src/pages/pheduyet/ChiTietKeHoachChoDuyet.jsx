import { useState } from 'react'
import LichCuaHoSo from '../thuky/LichCuaHoSo.jsx'
import HoSoDaTrinh from '../thuky/HoSoDaTrinh.jsx'
import TienDoCuocHop from '../thuky/TienDoCuocHop.jsx'
import LichSuKeHoach from '../thuky/LichSuKeHoach.jsx'

export default function ChiTietKeHoachChoDuyet({ keHoach, token, onDong, onDaXuLy }) {
  const [dangXuLy, setDangXuLy] = useState(false)
  const { cuocHopId, yeuCauPheDuyetId } = keHoach

  return (
    <section className="space-y-4 border-t border-slate-200 pt-4">
      <div className="flex items-center justify-between gap-3">
        <h3 className="font-semibold">Chi tiết kế hoạch</h3>
        <button type="button" onClick={onDong} disabled={dangXuLy} className="text-sm font-medium text-slate-600">
          Đóng chi tiết
        </button>
      </div>
      <TienDoCuocHop
        chiTiet={{
          trangThai: 'DANG_CHUAN_BI',
          buocXuLyHienTai: keHoach.capPheDuyet === 'TRUONG_PHONG' ? 'CHO_TRUONG_PHONG' : 'CHO_LANH_DAO',
        }}
        viecTiepTheo={keHoach.capPheDuyet === 'TRUONG_PHONG'
          ? 'Lần trình cấp 1 đang chờ bạn xử lý.'
          : 'Lần trình cấp cuối đang chờ bạn xử lý.'}
      />
      <HoSoDaTrinh
        key={yeuCauPheDuyetId}
        yeuCauId={yeuCauPheDuyetId}
        token={token}
        yeuCauDuyet={keHoach}
        onDangXuLy={setDangXuLy}
        onDaXuLy={onDaXuLy}
      >
        <details className="rounded-lg border border-slate-200 bg-white p-4">
          <summary className="cursor-pointer font-semibold">Lịch sử các lần trình và quyết định</summary>
          <div className="mt-4"><LichSuKeHoach cuocHopId={cuocHopId} token={token} /></div>
        </details>
        <details className="rounded-lg border border-slate-200 bg-white p-4">
          <summary className="cursor-pointer font-semibold">Các lịch của cuộc họp</summary>
          <div className="mt-4"><LichCuaHoSo cuocHopId={cuocHopId} token={token} /></div>
        </details>
      </HoSoDaTrinh>
    </section>
  )
}
