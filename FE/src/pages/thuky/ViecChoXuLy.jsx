import { useEffect, useState } from 'react'
import { layHoSoThuKyLanhDao, layKeHoachChoToiDuyet } from '../api/cuocHopApi.js'

export default function ViecChoXuLy({ token, danhSach, loiDanhSach, cacMucXuLy = [], onMoMuc }) {
  const coRaSoat = cacMucXuLy.includes('ho-so-ra-soat')
  const coDuyet = cacMucXuLy.includes('ke-hoach-cho-duyet')
  const [raSoat, setRaSoat] = useState(null)
  const [choDuyet, setChoDuyet] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    async function taiSoLuong(api, capNhat) {
      try {
        const hoSo = await api(token, controller.signal)
        if (!controller.signal.aborted) capNhat({ soLuong: hoSo.length })
      } catch {
        if (!controller.signal.aborted) capNhat({ loi: true })
      }
    }
    if (coRaSoat) taiSoLuong(layHoSoThuKyLanhDao, setRaSoat)
    if (coDuyet) taiSoLuong(layKeHoachChoToiDuyet, setChoDuyet)
    return () => controller.abort()
  }, [token, coRaSoat, coDuyet])

  const tuDanhSach = (loc) => loiDanhSach ? { loi: true } : danhSach && { soLuong: danhSach.filter(loc).length }
  const mucXuLy = [
    {
      id: 'ho-so-cho-chuyen-cap', ten: 'Chờ bạn chuyển cấp',
      ketQua: tuDanhSach((muc) => muc.yeuCauChoGuiCapTrenId != null),
    },
    { id: 'ho-so-ra-soat', ten: 'Chờ bạn tiếp nhận hoặc rà soát', ketQua: raSoat },
    { id: 'ke-hoach-cho-duyet', ten: 'Chờ bạn duyệt kế hoạch', ketQua: choDuyet },
    {
      id: 'phan-hoi-ke-hoach', ten: 'Kế hoạch cần chỉnh sửa',
      ketQua: tuDanhSach((muc) => muc.duocLapKeHoach && muc.phanHoiKeHoach?.trangThai === 'TRA_LAI'),
    },
    {
      id: 'cuoc-hop-duoc-giao', ten: 'Cuộc họp cần chuẩn bị',
      ketQua: tuDanhSach((muc) => muc.yeuCauBanGiaoId != null && (
        (muc.duocToChuc && muc.buocXuLyHienTai === 'THU_KY_TO_CHUC') || muc.duocLapKeHoach
      )),
    },
  ].filter((muc) => cacMucXuLy.includes(muc.id))

  if (!mucXuLy.length) return null

  return (
    <section aria-label="Việc chờ bạn xử lý" className="space-y-3 rounded-xl border border-slate-200 bg-white p-5">
      <h2 className="font-bold text-slate-900">Việc chờ bạn xử lý</h2>
      <ul className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
        {mucXuLy.map(({ id, ten, ketQua }) => (
          <li key={id} className="min-w-0">
            <button type="button" onClick={() => onMoMuc(id)}
              className="flex h-full w-full items-center justify-between gap-3 rounded-lg border border-slate-200 p-3 text-left text-sm hover:border-emerald-400 hover:bg-emerald-50">
              <span className="font-semibold text-slate-800">{ten}</span>
              <span className={ketQua?.loi ? 'text-xs text-amber-800' : 'font-semibold text-emerald-800'}>
                {ketQua?.loi ? 'Chưa tải được' : ketQua?.soLuong ?? '…'}
              </span>
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}
