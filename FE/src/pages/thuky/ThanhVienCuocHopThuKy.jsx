import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import Building2 from 'lucide-react/dist/esm/icons/building-2.mjs'
import UserPlus from 'lucide-react/dist/esm/icons/user-plus.mjs'
import Users from 'lucide-react/dist/esm/icons/users.mjs'
import {
  layNguoiCoTheMoi,
  layThanhVienCuocHop,
  moiDanhSachThanhVienCuocHop,
} from '../../api/thanhVienCuocHopApi.js'

import { layLichTheoCuocHop } from '../../api/cuocHopApi.js'

const TEN_TRANG_THAI_MOI = {
  CHO_PHAN_HOI: 'Chờ phản hồi',
  DA_XAC_NHAN: 'Đã xác nhận',
  TU_CHOI: 'Từ chối',
  DA_HUY: 'Đã hủy',
}

const TEN_VAI_TRO = {
  THAM_DU: 'Người tham dự',
  THU_KY: 'Thư ký',
  THU_KY_LANH_DAO: 'Thư ký lãnh đạo',
  CHU_TRI: 'Chủ trì',
}

function mauTrangThaiMoi(trangThai) {
  if (trangThai === 'DA_XAC_NHAN') return 'bg-emerald-50 text-emerald-800'
  if (trangThai === 'TU_CHOI' || trangThai === 'DA_HUY') return 'bg-rose-50 text-rose-800'
  return 'bg-amber-50 text-amber-900'
}

export default function ThanhVienCuocHopThuKy({
  cuocHopId,
  token,
  choPhepMoi = true,
  onTrangThaiLich,
  onLapLich,
  lichHopIdKeHoachDaDuyet,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [nguoiChonIds, setNguoiChonIds] = useState([])
  const [tuKhoa, setTuKhoa] = useState('')
  const [xacNhanMoi, setXacNhanMoi] = useState(false)
  const [dangMoi, setDangMoi] = useState(false)
  const [loiTai, setLoiTai] = useState('')
  const [loiMoi, setLoiMoi] = useState('')
  const [thongBao, setThongBao] = useState('')
  const [nguoiCoTheMoi, setNguoiCoTheMoi] = useState(null)
  const [coLichDaXacNhan, setCoLichDaXacNhan] = useState(null)
  const [lichKeHoachDaDuyet, setLichKeHoachDaDuyet] = useState(null)
  const [lichKeHoachQuaHan, setLichKeHoachQuaHan] = useState(false)
  const [loiNguoiCoTheMoi, setLoiNguoiCoTheMoi] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    layThanhVienCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setDanhSach(duLieu)
        setLoiTai('')
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiTai(loi.message)
      })

    if (choPhepMoi) {
      layLichTheoCuocHop(cuocHopId, token, controller.signal)
        .then((lich) => {
          if (controller.signal.aborted) return
          const lichDuocDuyet = lich.find(
            (muc) => muc.lichHopId === lichHopIdKeHoachDaDuyet,
          ) ?? null
          const daXacNhan = lichDuocDuyet?.trangThai === 'DA_XAC_NHAN'
          const thoiGianBatDau = lichDuocDuyet?.thoiGianBatDau
          const lichChuaBatDau = Boolean(
            thoiGianBatDau && new Date(thoiGianBatDau).getTime() > new Date().getTime(),
          )
          setLichKeHoachDaDuyet(lichDuocDuyet)
          setLichKeHoachQuaHan(Boolean(
            thoiGianBatDau && !lichChuaBatDau,
          ))
          const coLichHopLe = daXacNhan && lichChuaBatDau
          setCoLichDaXacNhan(coLichHopLe)
          onTrangThaiLich?.(coLichHopLe)
          if (coLichHopLe) return layNguoiCoTheMoi(cuocHopId, token, controller.signal)
        })
        .then((duLieu) => {
          if (controller.signal.aborted) return
          setNguoiCoTheMoi(duLieu ?? null)
          setLoiNguoiCoTheMoi('')
        })
        .catch((loi) => {
          if (controller.signal.aborted) return
          setNguoiCoTheMoi(null)
          setLoiNguoiCoTheMoi(loi.message)
        })
    }

    return () => controller.abort()
  }, [cuocHopId, token, lanTai, choPhepMoi, onTrangThaiLich, lichHopIdKeHoachDaDuyet, phienDongBo])

  const nguoiChon = (nguoiCoTheMoi ?? []).filter((nguoi) => nguoiChonIds.includes(nguoi.nguoiDungId))
  const ketQuaTim = nguoiCoTheMoi?.filter((nguoi) =>
    [nguoi.hoTen, nguoi.chucVu, nguoi.tenPhongBan].filter(Boolean).join(' ')
      .toLocaleLowerCase('vi').includes(tuKhoa.trim().toLocaleLowerCase('vi')),
  )

  function chonNguoi(id, daChon) {
    setXacNhanMoi(false)
    setNguoiChonIds((hienTai) => daChon ? [...hienTai, id] : hienTai.filter((muc) => muc !== id))
  }

  async function xuLyMoi(event) {
    event.preventDefault()
    if (dangMoi || !choPhepMoi || !coLichDaXacNhan || !nguoiChon.length) return
    if (!xacNhanMoi) {
      setXacNhanMoi(true)
      return
    }

    setDangMoi(true)
    setLoiMoi('')
    setThongBao('')

    try {
      await moiDanhSachThanhVienCuocHop(cuocHopId, nguoiChon.map((nguoi) => nguoi.nguoiDungId), token)
      const thanhVienMoi = nguoiChon.map((nguoi) => ({
        ...nguoi,
        vaiTroTaiCuocHop: 'THAM_DU',
        trangThaiMoi: 'CHO_PHAN_HOI',
        trangThaiThamDu: 'CHUA_DIEM_DANH',
      }))
      setDanhSach((hienTai) => {
        const idsHienTai = new Set((hienTai || []).map((nguoi) => nguoi.nguoiDungId))
        return [
          ...(hienTai || []),
          ...thanhVienMoi.filter((nguoi) => !idsHienTai.has(nguoi.nguoiDungId)),
        ]
      })
      setNguoiChonIds([])
      setXacNhanMoi(false)
      setThongBao(`Đã mời ${nguoiChon.length} người tham dự cuộc họp.`)
      setLanTai((hienTai) => hienTai + 1)
    } catch (loi) {
      setLoiMoi(loi.message)
    } finally {
      setDangMoi(false)
    }
  }

  const lichCuChoXacNhan = lichKeHoachDaDuyet?.trangThai === 'DE_XUAT' && !lichKeHoachQuaHan

  return (
    <section className="space-y-4">
      {choPhepMoi && coLichDaXacNhan === null && (
        <p role="status" className="text-sm text-slate-600">Đang kiểm tra lịch họp…</p>
      )}
      {choPhepMoi && coLichDaXacNhan === false && (
        <div className="space-y-3 rounded-xl border border-amber-200 bg-amber-50 p-4">
          <div>
            <p className="text-sm font-semibold text-amber-950">
              {lichKeHoachQuaHan
                ? 'Lịch trong bản kế hoạch đã duyệt đã bắt đầu hoặc đã qua'
                : lichCuChoXacNhan
                  ? 'Lịch của hồ sơ cũ đang chờ xác nhận'
                  : 'Cần đề xuất lịch họp mới'}
            </p>
            <p className="mt-1 text-sm text-amber-900">
              {lichKeHoachQuaHan
                ? `Lịch #${lichKeHoachDaDuyet?.lichHopId} đã bắt đầu hoặc đã qua. Hãy chọn lịch tương lai trên Calendar, cập nhật bản kế hoạch và trình duyệt lại; lời mời sẽ mở khi kế hoạch và lịch mới được duyệt.`
                : lichCuChoXacNhan
                  ? `Người đã duyệt hồ sơ có thể xác nhận lịch #${lichKeHoachDaDuyet.lichHopId} trên Calendar.`
                  : 'Hãy đề xuất lịch tương lai, cập nhật kế hoạch và trình duyệt lại trước khi mời.'}
            </p>
          </div>
          {onLapLich && (
            <button
              type="button"
              onClick={() => onLapLich(cuocHopId)}
              className="rounded-lg border border-amber-300 bg-white px-3 py-2 text-sm font-semibold text-amber-950 transition hover:bg-amber-100"
            >
              {lichCuChoXacNhan ? 'Xem lịch trên Calendar' : 'Đề xuất lại lịch'}
            </button>
          )}
        </div>
      )}
      {choPhepMoi && coLichDaXacNhan && !loiNguoiCoTheMoi && (
        <form onSubmit={xuLyMoi} className="space-y-3">
          <label className="block text-sm font-semibold text-slate-700">
            Tìm thành viên
            <input type="search" value={tuKhoa} onChange={(event) => setTuKhoa(event.target.value)}
              disabled={dangMoi}
              placeholder="Tên, chức vụ hoặc phòng ban"
              className="mt-1 block w-full rounded-lg border border-slate-200 px-3 py-2" />
          </label>
          <fieldset disabled={dangMoi} className="space-y-2">
            <legend className="text-sm font-semibold text-slate-900">
              Chọn người tham dự · Đã chọn {nguoiChon.length}
            </legend>
            {nguoiCoTheMoi === null ? <p role="status" className="text-sm">Đang tải người dùng…</p>
              : !ketQuaTim.length && <p className="text-sm">Không có người phù hợp.</p>}
            <div className="max-h-80 space-y-2 overflow-y-auto pr-1">
              {ketQuaTim?.map((nguoi) => (
                <label
                  key={nguoi.nguoiDungId}
                  className={`flex cursor-pointer items-start gap-3 rounded-xl border p-3 text-sm transition ${
                    nguoiChonIds.includes(nguoi.nguoiDungId)
                      ? 'border-emerald-500 bg-emerald-50'
                      : 'border-slate-200 bg-white hover:border-emerald-300'
                  }`}
                >
                  <input type="checkbox" className="mt-1 size-4 accent-emerald-800" checked={nguoiChonIds.includes(nguoi.nguoiDungId)}
                    onChange={(event) => chonNguoi(nguoi.nguoiDungId, event.target.checked)} />
                  <span className="min-w-0 flex-1">
                    <span className="block font-semibold text-slate-900">{nguoi.hoTen}</span>
                    <span className="mt-1 grid gap-x-3 gap-y-1 text-xs text-slate-600 sm:grid-cols-2">
                      <span><strong>Phòng ban:</strong> {nguoi.tenPhongBan || 'Chưa phân công'}</span>
                      <span><strong>Chức vụ:</strong> {nguoi.chucVu || 'Chưa cập nhật'}</span>
                      <span><strong>Vai trò mời:</strong> {TEN_VAI_TRO.THAM_DU}</span>
                    </span>
                  </span>
                </label>
              ))}
            </div>
          </fieldset>
          {xacNhanMoi && (
            <div className="space-y-3 rounded-xl border border-emerald-200 bg-emerald-50 p-3 text-sm">
              <div>
                <p className="font-bold text-emerald-950">Kiểm tra trước khi gửi · {nguoiChon.length} người</p>
                <p className="mt-1 text-xs text-emerald-900">
                  Sau khi xác nhận, lời mời sẽ xuất hiện trong mục Lời mời họp của từng người.
                </p>
              </div>
              <ul className="max-h-52 space-y-2 overflow-y-auto">
                {nguoiChon.map((nguoi) => (
                  <li key={nguoi.nguoiDungId} className="rounded-lg bg-white p-3">
                    <p className="font-semibold text-slate-900">{nguoi.hoTen}</p>
                    <p className="mt-1 text-xs leading-5 text-slate-600">
                      Phòng ban: {nguoi.tenPhongBan || 'Chưa phân công'}
                      {' · '}Chức vụ: {nguoi.chucVu || 'Chưa cập nhật'}
                      {' · '}Vai trò: {TEN_VAI_TRO.THAM_DU}
                    </p>
                  </li>
                ))}
              </ul>
              <button
                type="button"
                disabled={dangMoi}
                className="text-sm font-semibold text-emerald-900 underline underline-offset-2"
                onClick={() => setXacNhanMoi(false)}
              >
                Quay lại chọn
              </button>
            </div>
          )}
          <button
            type="submit"
            disabled={dangMoi || !nguoiChon.length}
            className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-emerald-800 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-emerald-900 disabled:cursor-not-allowed disabled:opacity-55"
          >
            <UserPlus size={16} />
            {dangMoi ? 'Đang gửi lời mời…' : xacNhanMoi ? 'Xác nhận mời' : 'Mời thành viên'}
          </button>
        </form>
      )}

      {choPhepMoi && loiNguoiCoTheMoi && <p role="alert" className="rounded-lg bg-red-50 p-2.5 text-xs leading-5 text-red-800">{loiNguoiCoTheMoi}</p>}
      {loiMoi && <p role="alert" className="rounded-lg bg-red-50 p-2.5 text-xs leading-5 text-red-800">{loiMoi}</p>}
      {loiTai && <p role="alert" className="rounded-lg bg-red-50 p-2.5 text-xs leading-5 text-red-800">{loiTai}</p>}
      {thongBao && <p role="status" className="rounded-lg bg-emerald-50 p-2.5 text-xs leading-5 text-emerald-800">{thongBao}</p>}

      <div className="border-t border-slate-100 pt-4">
        <div className="mb-3 flex items-center justify-between gap-2">
          <h3 className="text-sm font-bold text-slate-900">Danh sách thành viên và lời mời</h3>
          <span className="inline-flex items-center gap-1 rounded-full bg-slate-100 px-2 py-1 text-xs font-semibold text-slate-600">
            <Users size={13} />
            {danhSach?.length ?? '—'}
          </span>
        </div>

        {!loiTai && danhSach === null && (
          <p role="status" className="text-sm text-slate-500">Đang tải thành viên…</p>
        )}
        {danhSach?.length === 0 && (
          <p className="text-sm text-slate-500">Chưa có thành viên được mời.</p>
        )}
        {danhSach?.length > 0 && (
          <ul className="max-h-72 space-y-2 overflow-y-auto pr-1">
            {danhSach.map((thanhVien) => (
              <li key={thanhVien.nguoiDungId} className="flex items-start gap-3 rounded-xl border border-slate-200 bg-white p-3">
                <span className="grid size-9 shrink-0 place-items-center rounded-full bg-emerald-50 text-emerald-800">
                  <Building2 size={15} />
                </span>
                <span className="min-w-0 flex-1">
                  <span className="block break-words text-sm font-semibold text-slate-900">{thanhVien.hoTen}</span>
                  <span className="mt-1 grid gap-x-3 gap-y-1 text-xs text-slate-600 sm:grid-cols-2">
                    <span><strong>Phòng ban:</strong> {thanhVien.tenPhongBan || 'Chưa phân công'}</span>
                    <span><strong>Chức vụ:</strong> {thanhVien.chucVu || 'Chưa cập nhật'}</span>
                    <span><strong>Vai trò:</strong> {TEN_VAI_TRO[thanhVien.vaiTroTaiCuocHop] || thanhVien.vaiTroTaiCuocHop || 'Chưa xác định'}</span>
                  </span>
                </span>
                {thanhVien.vaiTroTaiCuocHop === 'THAM_DU' && (
                  <span className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold ${mauTrangThaiMoi(thanhVien.trangThaiMoi)}`}>
                    {TEN_TRANG_THAI_MOI[thanhVien.trangThaiMoi] || thanhVien.trangThaiMoi || 'Chưa có trạng thái'}
                  </span>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  )
}
