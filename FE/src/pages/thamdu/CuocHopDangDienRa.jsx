import { useEffect, useState } from 'react'
import Radio from 'lucide-react/dist/esm/icons/radio.mjs'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { layCuocHopDangDienRaCuaToi, thamGiaCuocHop } from '../../api/thanhVienCuocHopApi.js'

export default function CuocHopDangDienRa({ token, onMoPhongHop, oTongQuan }) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiThamGia, setLoiThamGia] = useState('')
  const [dangThamGiaId, setDangThamGiaId] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    layCuocHopDangDienRaCuaToi(token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu)
        setLoiTai('')
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })
    return () => controller.abort()
  }, [token, phienDongBo])

  async function xuLyThamGia(id) {
    setDangThamGiaId(id)
    setLoiThamGia('')
    try {
      await thamGiaCuocHop(id, token)
      onMoPhongHop(id)
    } catch (loi) {
      setLoiThamGia(loi.message)
    } finally {
      setDangThamGiaId(null)
    }
  }

  if (!oTongQuan && !danhSach?.length) return null

  return (
    <section aria-labelledby="cuoc-hop-dang-dien-ra" className="rounded-2xl border border-emerald-200 phong-toi:border-phong-vien-xanh bg-emerald-50 phong-toi:bg-phong-nhan-xanh">
      <div className="flex items-center gap-3 px-4 py-3 sm:px-5">
        <span className="grid size-9 shrink-0 place-items-center rounded-lg bg-emerald-800 phong-toi:bg-phong-nut text-white">
          <Radio size={19} aria-hidden="true" />
        </span>
        <h2 id="cuoc-hop-dang-dien-ra" className="font-bold text-emerald-950 phong-toi:text-phong-chu" aria-live="polite">
          {danhSach?.length ? `Cuộc họp đang diễn ra (${danhSach.length})` : 'Cuộc họp đang diễn ra'}
        </h2>
      </div>
      {loiTai && oTongQuan && <p role="alert" className="px-5 pb-3 text-sm text-red-800 phong-toi:text-phong-do">Chưa tải được danh sách phòng họp của bạn. {loiTai}</p>}
      {loiThamGia && <p role="alert" className="px-5 pb-3 text-sm text-red-800 phong-toi:text-phong-do">{loiThamGia}</p>}
      {!loiTai && danhSach === null && <p role="status" className="px-5 pb-3 text-sm text-slate-600 phong-toi:text-phong-chu-phu">Đang kiểm tra cuộc họp…</p>}
      {!loiTai && danhSach?.length === 0 && (
        <p role="status" className="px-5 pb-3 text-sm text-slate-700 phong-toi:text-phong-chu-phu">
          Hiện chưa có phòng họp đang mở dành cho tài khoản của bạn.
        </p>
      )}
      {!!danhSach?.length && (
        <ul className="divide-y divide-emerald-100 phong-toi:divide-phong-vien overflow-hidden rounded-b-2xl border-t border-emerald-100 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat">
          {danhSach.map((hop) => {
            const id = hop.cuocHopId
            const chiMo = Boolean(hop.thoiGianTuDiemDanh || hop.thoiGianRoiCuocHop || hop.laThuKyToChuc)
            const vaiTro = hop.laChuTri ? 'Bạn là chủ trì' : hop.laThuKyToChuc ? 'Bạn là thư ký tổ chức' : 'Bạn là thành viên'
            return (
              <li key={id} className="flex flex-wrap items-center justify-between gap-3 px-4 py-3 sm:px-5">
                <div className="min-w-0">
                  <h3 className="break-words font-semibold text-slate-900 phong-toi:text-phong-chu">{hop.tieuDe}</h3>
                  <p className="mt-1 text-sm text-slate-700 phong-toi:text-phong-chu-phu">
                    {[vaiTro, hop.tenPhongHop, hop.thoiGianRoiCuocHop && 'Đã rời họp',
                      !hop.thoiGianRoiCuocHop && hop.thoiGianTuDiemDanh && 'Đã tự điểm danh'].filter(Boolean).join(' · ')}
                  </p>
                  {!chiMo && <p className="mt-1 text-sm text-slate-600 phong-toi:text-phong-chu-phu">
                    {hop.trangThaiMoi === 'CHO_PHAN_HOI'
                      ? 'Bấm Tham gia để xác nhận lời mời và tự điểm danh.'
                      : 'Bấm Tham gia để tự điểm danh.'}
                  </p>}
                </div>
                <button type="button" disabled={dangThamGiaId !== null}
                  onClick={() => chiMo ? onMoPhongHop(id) : xuLyThamGia(id)}
                  className="rounded-lg bg-emerald-800 phong-toi:bg-phong-nut px-4 py-2.5 text-sm font-semibold text-white hover:bg-emerald-900 phong-toi:enabled:hover:bg-phong-nut-hover disabled:opacity-60">
                  {dangThamGiaId === id ? 'Đang ghi nhận…' : chiMo ? 'Mở phòng họp' : 'Tham gia'}
                </button>
              </li>
            )
          })}
        </ul>
      )}
    </section>
  )
}
