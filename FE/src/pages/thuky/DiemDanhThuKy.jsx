import { useEffect, useMemo, useState } from 'react'
import CheckCircle2 from 'lucide-react/dist/esm/icons/check-circle-2.mjs'
import Clock3 from 'lucide-react/dist/esm/icons/clock-3.mjs'
import LogOut from 'lucide-react/dist/esm/icons/log-out.mjs'
import UserRoundCheck from 'lucide-react/dist/esm/icons/user-round-check.mjs'
import UserRoundX from 'lucide-react/dist/esm/icons/user-round-x.mjs'
import {
  diemDanhThanhVienCuocHop,
  ghiNhanRoiSom,
  layThanhVienCuocHop,
} from '../../api/thanhVienCuocHopApi.js'
import { layLichTheoCuocHop } from '../../api/cuocHopApi.js'

const TEN_TRANG_THAI = {
  CHUA_DIEM_DANH: 'Chưa điểm danh',
  CO_MAT: 'Có mặt',
  VANG_MAT: 'Vắng mặt',
}

function dinhDangGio(thoiGian) {
  if (!thoiGian) return 'Chưa ghi nhận'
  const ngay = new Date(thoiGian)
  return Number.isNaN(ngay.getTime())
    ? 'Chưa ghi nhận'
    : ngay.toLocaleString('vi-VN', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
}

export default function DiemDanhThuKy({ cuocHopId, token, readOnly = false }) {
  const [thanhVien, setThanhVien] = useState(null)
  const [batDauDuKien, setBatDauDuKien] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiDiemDanh, setLoiDiemDanh] = useState('')
  const [dangLuuId, setDangLuuId] = useState(null)
  const [dangRoiSomId, setDangRoiSomId] = useState(null)

  useEffect(() => {
    const controller = new AbortController()

    layThanhVienCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setThanhVien(duLieu.filter((muc) => muc.vaiTroTaiCuocHop === 'THAM_DU'))
        }
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    layLichTheoCuocHop(cuocHopId, token, controller.signal)
      .then((danhSachLich) => {
        if (controller.signal.aborted) return
        const lichDaXacNhan = danhSachLich.find((lich) => lich.trangThai === 'DA_XAC_NHAN')
        setBatDauDuKien(lichDaXacNhan?.thoiGianBatDau || null)
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai((hienTai) => hienTai || loi.message)
      })

    return () => controller.abort()
  }, [cuocHopId, token])

  const thongKe = useMemo(() => {
    const danhSach = thanhVien || []
    return {
      tong: danhSach.length,
      coMat: danhSach.filter((muc) => muc.trangThaiThamDu === 'CO_MAT').length,
      vangMat: danhSach.filter((muc) => muc.trangThaiThamDu === 'VANG_MAT').length,
      roiSom: danhSach.filter((muc) => muc.thoiGianRoiCuocHop).length,
    }
  }, [thanhVien])

  async function capNhatDanhSachSauKhiLuu() {
    try {
      const duLieu = await layThanhVienCuocHop(cuocHopId, token)
      setThanhVien(duLieu.filter((muc) => muc.vaiTroTaiCuocHop === 'THAM_DU'))
    } catch (loi) {
      setLoiDiemDanh(`Đã lưu, nhưng chưa tải lại được danh sách. ${loi.message}`)
    }
  }

  async function xuLyDiemDanh(nguoiDungId, coMat) {
    if (dangLuuId !== null || dangRoiSomId !== null || readOnly) return

    setDangLuuId(nguoiDungId)
    setLoiDiemDanh('')

    try {
      await diemDanhThanhVienCuocHop(cuocHopId, nguoiDungId, coMat, token)
      await capNhatDanhSachSauKhiLuu()
    } catch (loi) {
      setLoiDiemDanh(loi.message)
    } finally {
      setDangLuuId(null)
    }
  }

  async function xuLyRoiSom(nguoiDungId) {
    if (dangLuuId !== null || dangRoiSomId !== null || readOnly) return

    setDangRoiSomId(nguoiDungId)
    setLoiDiemDanh('')

    try {
      await ghiNhanRoiSom(cuocHopId, nguoiDungId, token)
      await capNhatDanhSachSauKhiLuu()
    } catch (loi) {
      setLoiDiemDanh(loi.message)
    } finally {
      setDangRoiSomId(null)
    }
  }

  const mocBatDau = batDauDuKien ? new Date(batDauDuKien).getTime() : null

  return (
    <section className="space-y-5">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.14em] text-emerald-800">Tham dự</p>
          <h2 className="mt-1 text-xl font-bold text-slate-900">Điểm danh cuộc họp</h2>
          <p className="mt-1 text-sm text-slate-500">
            {mocBatDau
              ? `Lịch bắt đầu ${dinhDangGio(batDauDuKien)}`
              : 'Chưa có giờ bắt đầu đã xác nhận để đối chiếu giờ đến.'}
          </p>
        </div>
        {readOnly && (
          <span className="rounded-full bg-slate-100 px-3 py-1.5 text-xs font-semibold text-slate-600">
            Chế độ xem
          </span>
        )}
      </div>

      {loiTai && <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-800">{loiTai}</p>}
      {loiDiemDanh && <p role="alert" className="rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-800">{loiDiemDanh}</p>}

      {thanhVien !== null && (
        <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          {[
            { ten: 'Được mời', so: thongKe.tong, BieuTuong: UserRoundCheck, mau: 'bg-slate-100 text-slate-600' },
            { ten: 'Có mặt', so: thongKe.coMat, BieuTuong: CheckCircle2, mau: 'bg-emerald-50 text-emerald-700' },
            { ten: 'Vắng mặt', so: thongKe.vangMat, BieuTuong: UserRoundX, mau: 'bg-rose-50 text-rose-700' },
            { ten: 'Rời sớm', so: thongKe.roiSom, BieuTuong: LogOut, mau: 'bg-amber-50 text-amber-700' },
          ].map(({ ten, so, BieuTuong, mau }) => (
            <div key={ten} className="flex items-center gap-3 rounded-xl border border-slate-200/80 bg-white p-3">
              <span className={`grid size-9 place-items-center rounded-lg ${mau}`}><BieuTuong size={17} /></span>
              <span>
                <span className="block text-lg font-bold leading-5 text-slate-900">{so}</span>
                <span className="mt-1 block text-xs font-medium text-slate-500">{ten}</span>
              </span>
            </div>
          ))}
        </div>
      )}

      {!loiTai && thanhVien === null && (
        <p role="status" className="rounded-xl bg-slate-50 p-5 text-sm text-slate-600">Đang tải danh sách thành viên…</p>
      )}

      {thanhVien?.length === 0 && (
        <p className="rounded-xl bg-slate-50 p-5 text-sm text-slate-600">Chưa có người tham dự để điểm danh.</p>
      )}

      {thanhVien?.length > 0 && (
        <div className="overflow-hidden rounded-xl border border-slate-200/80">
          <ul className="divide-y divide-slate-100">
            {thanhVien.map((muc) => {
              const mocDiemDanh = muc.thoiGianDiemDanh ? new Date(muc.thoiGianDiemDanh).getTime() : null
              const denMuon = muc.trangThaiThamDu === 'CO_MAT'
                && mocBatDau !== null
                && mocDiemDanh !== null
                && !Number.isNaN(mocDiemDanh)
                && mocDiemDanh > mocBatDau

              return (
                <li key={muc.nguoiDungId} className="flex flex-col gap-3 bg-white p-4 sm:flex-row sm:items-center sm:justify-between">
                  <div className="flex min-w-0 items-start gap-3">
                    <span className="grid size-10 shrink-0 place-items-center rounded-full bg-emerald-100 text-sm font-bold text-emerald-900">
                      {muc.hoTen?.trim()?.split(/\s+/).at(-1)?.[0]?.toLocaleUpperCase('vi') || '?'}
                    </span>
                    <div className="min-w-0">
                      <p className="truncate text-sm font-semibold text-slate-900">{muc.hoTen}</p>
                      <div className="mt-1 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-500">
                        <span className="inline-flex items-center gap-1">
                          <Clock3 size={13} />
                          {TEN_TRANG_THAI[muc.trangThaiThamDu] ?? muc.trangThaiThamDu}
                        </span>
                        {muc.thoiGianDiemDanh && <span>Điểm danh {dinhDangGio(muc.thoiGianDiemDanh)}</span>}
                        {muc.thoiGianRoiCuocHop && <span>Rời họp {dinhDangGio(muc.thoiGianRoiCuocHop)}</span>}
                        {denMuon && <span className="rounded-full bg-amber-50 px-2 py-0.5 font-semibold text-amber-800">Đến muộn</span>}
                        {muc.thoiGianRoiCuocHop && <span className="rounded-full bg-orange-50 px-2 py-0.5 font-semibold text-orange-800">Rời sớm</span>}
                      </div>
                    </div>
                  </div>

                  {!readOnly && (
                    <div className="flex flex-wrap gap-2 sm:justify-end">
                      <button
                        type="button"
                        disabled={dangLuuId !== null || dangRoiSomId !== null || muc.trangThaiThamDu === 'CO_MAT'}
                        onClick={() => xuLyDiemDanh(muc.nguoiDungId, true)}
                        className="rounded-lg bg-emerald-800 px-3 py-2 text-xs font-semibold text-white transition hover:bg-emerald-900 disabled:cursor-not-allowed disabled:opacity-45"
                      >
                        {dangLuuId === muc.nguoiDungId ? 'Đang lưu…' : 'Có mặt'}
                      </button>
                      <button
                        type="button"
                        disabled={dangLuuId !== null || dangRoiSomId !== null || muc.trangThaiThamDu === 'VANG_MAT' || Boolean(muc.thoiGianRoiCuocHop)}
                        onClick={() => xuLyDiemDanh(muc.nguoiDungId, false)}
                        className="rounded-lg border border-slate-200 px-3 py-2 text-xs font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-45"
                      >
                        Vắng mặt
                      </button>
                      {muc.trangThaiThamDu === 'CO_MAT' && !muc.thoiGianRoiCuocHop && (
                        <button
                          type="button"
                          disabled={dangLuuId !== null || dangRoiSomId !== null}
                          onClick={() => xuLyRoiSom(muc.nguoiDungId)}
                          className="rounded-lg border border-amber-200 px-3 py-2 text-xs font-semibold text-amber-800 transition hover:bg-amber-50 disabled:cursor-not-allowed disabled:opacity-45"
                        >
                          {dangRoiSomId === muc.nguoiDungId ? 'Đang lưu…' : 'Ghi nhận rời sớm'}
                        </button>
                      )}
                    </div>
                  )}
                </li>
              )
            })}
          </ul>
        </div>
      )}
    </section>
  )
}
