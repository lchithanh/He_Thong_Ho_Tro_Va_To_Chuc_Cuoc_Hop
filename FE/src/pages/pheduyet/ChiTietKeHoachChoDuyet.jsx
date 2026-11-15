import { useEffect, useState } from 'react'
import { LichCuaHoSo } from '../thuky/DanhSachDeXuatThuKy.jsx'
import {
  layChiTietKeHoachChoDuyet,
  layTaiLieuLanTrinh,
  taiXuongTaiLieuLanTrinh,
} from '../../api/cuocHopApi.js'
import ThaoTacKeHoach from '../thuky/ThaoTacKeHoach.jsx'
import TienDoCuocHop from '../thuky/TienDoCuocHop.jsx'
import LichSuKeHoach from '../thuky/LichSuKeHoach.jsx'

export default function ChiTietKeHoachChoDuyet({
  keHoach,
  token,
  onDong,
  onDaXuLy,
}) {
  const [chiTiet, setChiTiet] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [dangXuLy, setDangXuLy] = useState(false)

  const [taiLieu, setTaiLieu] = useState(null)
  const [loiTaiLieu, setLoiTaiLieu] = useState('')
  const [loiTaiXuong, setLoiTaiXuong] = useState('')
  const [dangTaiXuong, setDangTaiXuong] = useState(null)

  const { cuocHopId, yeuCauPheDuyetId } = keHoach

  useEffect(() => {
    const controller = new AbortController()

    layChiTietKeHoachChoDuyet(yeuCauPheDuyetId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setChiTiet(duLieu)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    layTaiLieuLanTrinh(yeuCauPheDuyetId, token, controller.signal)
      .then((duLieu) => { if (!controller.signal.aborted) setTaiLieu(duLieu) })
      .catch((loi) => { if (!controller.signal.aborted) setLoiTaiLieu(loi.message) })

    return () => controller.abort()
  }, [yeuCauPheDuyetId, token])

  async function taiXuong(tep) {
    setLoiTaiXuong('')
    setDangTaiXuong(tep.taiLieuId)
    try {
      await taiXuongTaiLieuLanTrinh(yeuCauPheDuyetId, tep, token)
    } catch (loi) {
      setLoiTaiXuong(loi.message)
    } finally {
      setDangTaiXuong(null)
    }
  }

  return (
    <section className="space-y-4 border-t border-slate-200 pt-4">
      <div className="flex items-center justify-between gap-3">
        <h3 className="font-semibold">Chi tiết kế hoạch</h3>
        <button
          type="button"
          onClick={onDong}
          disabled={dangXuLy}
          className="text-sm font-medium text-slate-600"
        >
          Đóng chi tiết
        </button>
      </div>

      {loiTai ? (
        <p role="alert" className="text-sm text-red-800">{loiTai}</p>
      ) : chiTiet === null ? (
        <p role="status" className="text-sm text-slate-600">Đang tải chi tiết…</p>
      ) : (
        <>
          <header className="rounded-xl bg-slate-50 p-4">
            <p className="text-xs text-slate-600">Cuộc họp #{cuocHopId}</p>
            <p className="mt-1 font-semibold">{keHoach.tieuDe}</p>
          </header>
          <TienDoCuocHop
            chiTiet={{
              trangThai: 'DANG_CHUAN_BI',
              buocXuLyHienTai: keHoach.capPheDuyet === 'TRUONG_PHONG'
                ? 'CHO_TRUONG_PHONG'
                : 'CHO_LANH_DAO',
            }}
            viecTiepTheo={`Lần trình cấp ${keHoach.capPheDuyet === 'TRUONG_PHONG' ? '1' : 'cuối'} đang chờ bạn xử lý.`}
          />
          <LichCuaHoSo cuocHopId={cuocHopId} token={token} />
          <h4 className="font-semibold">Nội dung kế hoạch</h4>
          <dl className="space-y-3 text-sm">
            {[
              ['Người đề xuất', chiTiet.tenNguoiDeXuat],
              ['Người lập và trình', chiTiet.tenNguoiTrinh],
              ['Người chủ trì', chiTiet.tenChuTri],
              ['Thư ký cuộc họp', chiTiet.tenThuKyCuocHop],
              ['Lịch gửi duyệt', chiTiet.thoiGianBatDau && `${new Date(chiTiet.thoiGianBatDau).toLocaleString('vi-VN')} – ${new Date(chiTiet.thoiGianKetThuc).toLocaleString('vi-VN')}`],
              ['Phòng', chiTiet.tenPhongHop],
              ['Mục tiêu', chiTiet.mucTieu],
              ['Nội dung dự kiến', chiTiet.noiDungDuKien],
              ['Ghi chú trình', chiTiet.ghiChuTrinh],
              ['Lần trình cấp 1 liên kết', chiTiet.yeuCauTruocId == null ? null : `Yêu cầu #${chiTiet.yeuCauTruocId}`],
            ].map(([nhan, giaTri]) => (
              <div key={nhan}>
                <dt className="font-semibold">{nhan}</dt>
                <dd className="mt-1 whitespace-pre-wrap">{giaTri || '—'}</dd>
              </div>
            ))}
          </dl>

          <section className="space-y-2 rounded-lg border border-slate-200 p-3 text-sm">
            <h4 className="font-semibold">Tài liệu đã trình</h4>
            {loiTaiLieu ? <p role="alert" className="text-red-800">{loiTaiLieu}</p>
              : taiLieu === null ? <p role="status">Đang tải tài liệu…</p>
                : taiLieu.length === 0 && <p>Thư ký chưa gửi kèm tài liệu trong lần trình này.</p>}
            {taiLieu?.length > 0 && (
              <ul className="divide-y divide-slate-100">
                {taiLieu.map((tep) => (
                  <li key={tep.taiLieuId} className="flex flex-wrap items-center justify-between gap-2 py-2">
                    <span className="min-w-0 break-all">{tep.tenFileGoc || tep.tenTaiLieu}</span>
                    <button type="button" disabled={dangTaiXuong !== null} onClick={() => taiXuong(tep)}
                      className="rounded-lg border border-slate-200 px-3 py-1.5 font-semibold text-emerald-800 disabled:opacity-50">
                      {dangTaiXuong === tep.taiLieuId ? 'Đang tải…' : 'Tải xuống'}
                    </button>
                  </li>
                ))}
              </ul>
            )}
            {loiTaiXuong && <p role="alert" className="text-red-800">{loiTaiXuong}</p>}
          </section>

          <details className="rounded-xl border border-slate-200 p-4">
            <summary className="cursor-pointer font-semibold text-slate-800">
              Lịch sử các lần trình và quyết định
            </summary>
            <div className="mt-4">
              <LichSuKeHoach cuocHopId={cuocHopId} token={token} />
            </div>
          </details>

          <ThaoTacKeHoach
  key={yeuCauPheDuyetId}
  cuocHopId={cuocHopId}
  yeuCauPheDuyetId={yeuCauPheDuyetId}
  token={token}
  cheDo="duyet"
  choPhepTrinhCapTren={keHoach.choPhepTrinhCapTren}
  onDangXuLy={setDangXuLy}
  onDaXuLy={onDaXuLy}
/>
        </>
      )}
    </section>
  )
}



