import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import Radio from 'lucide-react/dist/esm/icons/radio.mjs'
import { layCuocHopDangDienRaCuaToi, thamGiaCuocHop } from '../../api/thanhVienCuocHopApi.js'

const TEN_HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }
const LOP_LIEN_KET = 'rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-blue-800 hover:bg-blue-50'

function dinhDangGio(moc) {
  return moc ? new Date(moc).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : ''
}

export default function CuocHopDangDienRa({ token, onMoHoSo, onMoBieuQuyet }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiThaoTac, setLoiThaoTac] = useState('')
  const [dangThamGiaId, setDangThamGiaId] = useState(null)
  const [dangXemId, setDangXemId] = useState(null)
  const [lanTai, setLanTai] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    async function taiDanhSach() {
      try {
        const duLieu = await layCuocHopDangDienRaCuaToi(token, controller.signal)
        if (controller.signal.aborted) return
        setDanhSach(duLieu)
        setLoiTai('')
      } catch (loi) {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      }
    }

    taiDanhSach()
    return () => controller.abort()
  }, [token, lanTai, phienDongBo])

  async function xuLyThamGia(id) {
    if (dangThamGiaId != null) return
    setDangThamGiaId(id)
    setLoiThaoTac('')
    try {
      const ketQua = await thamGiaCuocHop(id, token)
      setDanhSach((hienTai) => hienTai?.map((hop) => hop.cuocHopId === id
        ? { ...hop, trangThaiThamDu: 'CO_MAT', thoiGianDiemDanh: ketQua.thoiGianDiemDanh }
        : hop))
      setDangXemId(id)
      setLanTai((lan) => lan + 1)
    } catch (loi) {
      setLoiThaoTac(loi.message)
      if (loi.status === 409) setLanTai((lan) => lan + 1)
    } finally {
      setDangThamGiaId(null)
    }
  }

  return (
    <section aria-labelledby="cuoc-hop-dang-dien-ra" className="rounded-2xl border border-blue-100 bg-white">
      <div className="flex items-center gap-3 border-b border-slate-100 px-5 py-4">
        <span className="grid size-9 place-items-center rounded-lg bg-blue-50 text-blue-700">
          <Radio size={19} aria-hidden="true" />
        </span>
        <h2 id="cuoc-hop-dang-dien-ra" className="font-bold text-slate-900">Cuộc họp đang diễn ra</h2>
      </div>
      {loiTai && <p role="alert" className="px-5 py-3 text-sm text-red-800">{loiTai}</p>}
      {loiThaoTac && <p role="alert" className="px-5 py-3 text-sm text-red-800">{loiThaoTac}</p>}
      {!loiTai && danhSach === null && <p role="status" className="px-5 py-4 text-sm text-slate-600">Đang tải cuộc họp…</p>}
      {!loiTai && danhSach?.length === 0 && <p className="px-5 py-4 text-sm text-slate-600">Hiện chưa có cuộc họp nào đang diễn ra dành cho bạn.</p>}
      {danhSach?.length > 0 && (
        <ul className="divide-y divide-slate-100">
          {danhSach.map((hop) => {
            const id = hop.cuocHopId
            const daThamGia = hop.trangThaiThamDu === 'CO_MAT'
            const daRoi = Boolean(hop.thoiGianRoiCuocHop)
            const dangXem = dangXemId === id && daThamGia && !daRoi
            const thongTin = [
              hop.laChuTri ? 'Bạn là chủ trì' : hop.tenChuTri && `Chủ trì ${hop.tenChuTri}`,
              TEN_HINH_THUC[hop.loaiHinh],
              hop.thoiGianBatDau && `Bắt đầu lúc ${dinhDangGio(hop.thoiGianBatDau)}`,
            ].filter(Boolean).join(' · ')

            return (
              <li key={id} className="space-y-3 px-5 py-4">
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div className="min-w-0">
                    <h3 className="break-words text-sm font-semibold text-slate-900">{hop.tieuDe}</h3>
                    {thongTin && <p className="mt-1 text-xs text-slate-600">{thongTin}</p>}
                  </div>
                  <div className="flex flex-wrap items-center gap-2">
                    {daThamGia && (
                      <span role="status" className="rounded-full bg-green-50 px-2.5 py-1.5 text-xs font-semibold text-green-800">
                        {daRoi ? 'Đã rời cuộc họp' : `Có mặt${hop.thoiGianDiemDanh ? ' từ ' + dinhDangGio(hop.thoiGianDiemDanh) : ''}`}
                      </span>
                    )}
                    {!daThamGia && !daRoi && (
                      <button type="button" disabled={dangThamGiaId != null || Boolean(loiTai)} onClick={() => xuLyThamGia(id)}
                        className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800 disabled:opacity-50">
                        {dangThamGiaId === id ? 'Đang ghi nhận…' : 'Tham gia'}
                      </button>
                    )}
                    {daThamGia && !daRoi && (
                      <button type="button" aria-expanded={dangXem} onClick={() => setDangXemId(dangXem ? null : id)} className={LOP_LIEN_KET}>
                        {dangXem ? 'Thu gọn' : 'Xem nội dung họp'}
                      </button>
                    )}
                  </div>
                </div>
                {dangXem && (
                  <div className="space-y-3 rounded-xl bg-slate-50 p-4 text-sm">
                    {hop.mucTieu && <div><h4 className="font-semibold">Mục tiêu</h4><p className="whitespace-pre-wrap break-words">{hop.mucTieu}</p></div>}
                    {hop.noiDungDuKien && <div><h4 className="font-semibold">Nội dung dự kiến</h4><p className="whitespace-pre-wrap break-words">{hop.noiDungDuKien}</p></div>}
                    <div className="flex flex-wrap gap-2">
                      {hop.laChuTri && onMoHoSo && <button type="button" onClick={() => onMoHoSo(id)} className={LOP_LIEN_KET}>Mở hồ sơ điều hành</button>}
                      {onMoBieuQuyet && <button type="button" onClick={onMoBieuQuyet} className={LOP_LIEN_KET}>Biểu quyết và kết quả</button>}
                    </div>
                  </div>
                )}
              </li>
            )
          })}
        </ul>
      )}
    </section>
  )
}
