import { useEffect, useState } from 'react'
import { layHoSoThuKyLanhDao, layKeHoachChoToiDuyet } from '../api/cuocHopApi.js'
import { useDongBoDuLieu } from '../services/useDongBoDuLieu.js'
import { canChinhSuaKeHoach, canChuanBiCuocHopDuocGiao } from './thuky/hienThiDeXuat.js'

export default function ViecChoXuLy({ token, danhSach, loiDanhSach, cacMucXuLy = [], onMoMuc }) {
  const coRaSoat = cacMucXuLy.includes('ho-so-ra-soat')
  const coDuyet = cacMucXuLy.includes('ho-so-cho-duyet')
  const phienDongBo = useDongBoDuLieu()
  const [raSoat, setRaSoat] = useState(null)
  const [choDuyet, setChoDuyet] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    async function taiSoLuong(api, capNhat) {
      try {
        const hoSo = await api(token, controller.signal)
        if (!controller.signal.aborted) capNhat({ hoSo })
      } catch {
        if (!controller.signal.aborted) capNhat({ loi: true })
      }
    }
    if (coRaSoat) taiSoLuong(layHoSoThuKyLanhDao, setRaSoat)
    if (coDuyet) taiSoLuong(layKeHoachChoToiDuyet, setChoDuyet)
    return () => controller.abort()
  }, [token, coRaSoat, coDuyet, phienDongBo])

  const tuDanhSach = (loc) => loiDanhSach ? { loi: true } : danhSach && { hoSo: danhSach.filter(loc) }
  const mucXuLy = [
    {
      id: 'ho-so-chuyen-cap', ten: 'Chờ bạn chuyển cấp',
      ketQua: tuDanhSach((muc) => muc.yeuCauChoGuiCapTrenId != null),
    },
    { id: 'ho-so-ra-soat', ten: 'Chờ bạn tiếp nhận hoặc rà soát', ketQua: raSoat },
    { id: 'ho-so-cho-duyet', ten: 'Chờ bạn duyệt kế hoạch', ketQua: choDuyet },
    {
      id: 'phan-hoi-ke-hoach', ten: 'Kế hoạch cần chỉnh sửa',
      ketQua: tuDanhSach(canChinhSuaKeHoach),
    },
    {
      id: 'cuoc-hop-duoc-giao', ten: 'Cuộc họp cần chuẩn bị',
      ketQua: tuDanhSach(canChuanBiCuocHopDuocGiao),
    },
  ].filter((muc) => cacMucXuLy.includes(muc.id))

  if (!mucXuLy.length) return null

  return (
    <section aria-label="Việc chờ bạn xử lý" className="space-y-3 rounded-xl border border-slate-200 phong-toi:border-phong-vien bg-white phong-toi:bg-phong-mat p-5">
      <h2 className="font-bold text-slate-900 phong-toi:text-phong-chu">Việc chờ bạn xử lý</h2>
      <ul className="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
        {mucXuLy.map(({ id, ten, ketQua }) => (
          <li key={id} className="min-w-0">
            <button type="button" onClick={() => onMoMuc(id, true)}
              className="flex h-full w-full items-center justify-between gap-3 rounded-lg border border-slate-200 phong-toi:border-phong-vien p-3 text-left text-sm hover:border-emerald-400 hover:bg-emerald-50 phong-toi:hover:bg-phong-mat-noi">
              <span className="font-semibold text-slate-800 phong-toi:text-phong-chu">{ten}</span>
              <span className={ketQua?.loi ? 'text-xs text-amber-800 phong-toi:text-phong-vang' : 'font-semibold text-emerald-800 phong-toi:text-phong-xanh'}>
                {ketQua?.loi ? 'Chưa tải được' : ketQua?.hoSo?.length ?? '…'}
              </span>
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}
