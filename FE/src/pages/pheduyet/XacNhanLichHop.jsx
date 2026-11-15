import { useEffect, useState } from 'react'
import {
  layKeHoachToiDaDuyet,
  layLichTheoCuocHop,
  xacNhanLichHop,
} from '../../api/cuocHopApi.js'

export default function XacNhanLichHop({ token }) {
  const [keHoach, setKeHoach] = useState(null)
  const [cuocHopId, setCuocHopId] = useState('')
  const [loi, setLoi] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layKeHoachToiDaDuyet(token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setKeHoach(duLieu)
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [token])

  const cuocHop = [
    ...new Map((keHoach ?? []).map((muc) => [muc.cuocHopId, muc])).values(),
  ]

  return (
    <section className="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="text-lg font-bold">Xác nhận lịch họp</h2>

      {loi ? (
        <p role="alert" className="text-sm text-red-800">{loi}</p>
      ) : keHoach === null ? (
        <p role="status" className="text-sm text-slate-600">Đang tải kế hoạch…</p>
      ) : cuocHop.length === 0 ? (
        <p className="text-sm text-slate-600">Bạn chưa có kế hoạch đã duyệt.</p>
      ) : (
        <label className="block text-sm font-medium">
          Cuộc họp đã duyệt
          <select
            value={cuocHopId}
            onChange={(event) => setCuocHopId(event.target.value)}
            className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
          >
            <option value="">Chọn cuộc họp</option>
            {cuocHop.map((muc) => (
              <option key={muc.cuocHopId} value={muc.cuocHopId}>
                #{muc.cuocHopId} · {muc.tieuDe}
              </option>
            ))}
          </select>
        </label>
      )}

      {cuocHopId && (
        <DanhSachLichXacNhan
          key={cuocHopId}
          cuocHopId={cuocHopId}
          token={token}
        />
      )}
    </section>
  )
}

const TEN_TRANG_THAI = {
  DE_XUAT: 'Đề xuất',
  DA_XAC_NHAN: 'Đã xác nhận',
  TU_CHOI: 'Từ chối',
  DA_HUY: 'Đã hủy',
  THAY_THE: 'Đã thay thế',
}

function DanhSachLichXacNhan({ cuocHopId, token }) {
  const [danhSach, setDanhSach] = useState(null)
  const [loi, setLoi] = useState('')
  const [lichDangXuLy, setLichDangXuLy] = useState(null)
  const [thongBao, setThongBao] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layLichTheoCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) setDanhSach(duLieu)
      })
      .catch((loiTai) => {
        if (!controller.signal.aborted) setLoi(loiTai.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token])

  async function xuLyXacNhan(lichHopId) {
    if (lichDangXuLy !== null) return

    setLichDangXuLy(lichHopId)
    setLoi('')
    setThongBao('')

    try {
      const lichMoi = await xacNhanLichHop(lichHopId, token)
      setDanhSach((hienTai) =>
        hienTai.map((lich) =>
          lich.lichHopId === lichHopId ? lichMoi : lich,
        ),
      )
      setThongBao(`Đã xác nhận lịch #${lichHopId}.`)
    } catch (loiXacNhan) {
      setLoi(loiXacNhan.message)
    } finally {
      setLichDangXuLy(null)
    }
  }

  return (
    <div className="space-y-3">
      {loi && <p role="alert" className="text-sm text-red-800">{loi}</p>}
      {thongBao && <p role="status" className="text-sm text-emerald-800">{thongBao}</p>}

      {!loi && danhSach === null && (
        <p role="status" className="text-sm text-slate-600">Đang tải lịch…</p>
      )}
      {danhSach?.length === 0 && (
        <p className="text-sm text-slate-600">Thư ký chưa đề xuất lịch.</p>
      )}

      <ul className="divide-y divide-slate-200">
        {danhSach?.map((lich) => (
          <li key={lich.lichHopId} className="flex flex-wrap items-center justify-between gap-3 py-3">
            <div className="text-sm">
              <p className="font-semibold">
                Lịch #{lich.lichHopId} · {TEN_TRANG_THAI[lich.trangThai] || lich.trangThai}
              </p>
              <p className="mt-1 text-slate-600">
                {lich.tenPhongHop || 'Trực tuyến'} ·{' '}
                {new Date(lich.thoiGianBatDau).toLocaleString('vi-VN')} –{' '}
                {new Date(lich.thoiGianKetThuc).toLocaleString('vi-VN')}
              </p>
            </div>

            {lich.trangThai === 'DE_XUAT' && (
              <button
                type="button"
                disabled={lichDangXuLy !== null}
                onClick={() => xuLyXacNhan(lich.lichHopId)}
                className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
              >
                {lichDangXuLy === lich.lichHopId ? 'Đang xác nhận…' : 'Xác nhận lịch'}
              </button>
            )}
          </li>
        ))}
      </ul>
    </div>
  )
}