import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { layLichTheoCuocHop, trinhLaiLichSauHuy } from '../../api/cuocHopApi.js'

const TEN_TRANG_THAI = {
  DA_XAC_NHAN: 'Đã xác nhận',
  DE_XUAT: 'Đang đề xuất',
  TU_CHOI: 'Đã từ chối',
  DA_HUY: 'Đã hủy',
  THAY_THE: 'Đã thay thế',
}

function dinhDangThoiGian(thoiGian) {
  return thoiGian ? new Date(thoiGian).toLocaleString('vi-VN') : 'Chưa có thông tin'
}

function DanhSachLich({ lich, loaiHinh, onTrinhLai, dangTrinh, thoiDiemTai }) {
  return (
    <ul className="divide-y divide-slate-100">
      {lich.map((muc) => (
        <li key={muc.lichHopId} className="space-y-1.5 py-3">
          <div className="flex flex-wrap items-center gap-2">
            <strong>{TEN_TRANG_THAI[muc.trangThai] || muc.trangThai}</strong>
            <span className="text-slate-500">Lịch #{muc.lichHopId}</span>
            {onTrinhLai && muc.trangThai === 'DE_XUAT' && Date.parse(muc.thoiGianBatDau) > thoiDiemTai && (
              <button type="button" disabled={dangTrinh != null}
                className="rounded-lg bg-emerald-800 px-3 py-2 text-xs font-semibold text-white disabled:opacity-60"
                onClick={() => onTrinhLai(muc.lichHopId)}>
                {dangTrinh === muc.lichHopId ? 'Đang trình…' : 'Trình duyệt lại lịch'}
              </button>
            )}
          </div>
          <dl className="grid gap-x-3 gap-y-1.5 sm:grid-cols-2">
            {[
              ['Bắt đầu', dinhDangThoiGian(muc.thoiGianBatDau)],
              ['Kết thúc', dinhDangThoiGian(muc.thoiGianKetThuc)],
              ['Địa điểm', muc.tenPhongHop || (muc.phongHopId != null ? 'Phòng #' + muc.phongHopId : loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : 'Chưa có thông tin phòng')],
              ['Người đề xuất lịch', muc.tenNguoiDeXuat || (muc.nguoiDeXuatId != null ? 'Mã người dùng #' + muc.nguoiDeXuatId : null)],
              ['Người xác nhận lịch', muc.tenNguoiXacNhan || (muc.nguoiXacNhanId != null ? 'Mã người dùng #' + muc.nguoiXacNhanId : null)],
              ['Thời điểm xác nhận', muc.thoiGianXacNhan && dinhDangThoiGian(muc.thoiGianXacNhan)],
              ['Lý do thay đổi', muc.lyDoThayDoi],
            ].filter(([, giaTri]) => giaTri).map(([nhan, giaTri]) => (
              <div key={nhan} className="min-w-0">
                <dt className="text-xs text-slate-500">{nhan}</dt>
                <dd className="whitespace-pre-wrap break-words">{giaTri}</dd>
              </div>
            ))}
          </dl>
        </li>
      ))}
    </ul>
  )
}

export default function LichCuaHoSo({
  cuocHopId, loaiHinh, token, lichHopIdKeHoachDaDuyet, coQuyenTrinhLai, onDaTrinhLai,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [duLieuLich, setDuLieuLich] = useState(null)
  const lich = duLieuLich?.danhSach
  const [loi, setLoi] = useState('')
  const [dangTrinh, setDangTrinh] = useState(null)
  const [loiTrinh, setLoiTrinh] = useState('')

  async function trinhLai(lichHopId) {
    if (dangTrinh != null) return
    setDangTrinh(lichHopId)
    setLoiTrinh('')
    try {
      const ketQua = await trinhLaiLichSauHuy(cuocHopId, lichHopId, token)
      onDaTrinhLai?.(ketQua)
    } catch (error) {
      setLoiTrinh(error.message)
    } finally {
      setDangTrinh(null)
    }
  }
  useEffect(() => {
    const controller = new AbortController()
    layLichTheoCuocHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (!controller.signal.aborted) {
          setDuLieuLich({ danhSach: duLieu, thoiDiemTai: Date.now() })
          setLoi('')
        }
      })
      .catch((error) => {
        if (!controller.signal.aborted) setLoi(error.status === 403 ? 'Bạn không có quyền xem lịch của cuộc họp này.' : error.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo])

  const hienTai = lich?.filter((muc) => ['DA_XAC_NHAN', 'DE_XUAT'].includes(muc.trangThai))
  const choXacNhanLich = !loi && hienTai?.some(
    (muc) => muc.lichHopId === lichHopIdKeHoachDaDuyet && muc.trangThai === 'DE_XUAT',
  )

  const lichSu = lich?.filter((muc) => !hienTai.includes(muc))
  const hienNutTrinhLai = !loi && coQuyenTrinhLai && !hienTai?.some((muc) => muc.trangThai === 'DA_XAC_NHAN')
    && lichSu?.some((muc) => muc.lichHopId === lichHopIdKeHoachDaDuyet && muc.trangThai === 'DA_HUY')
  return (
    <section className="space-y-2 rounded-xl border border-slate-200 p-4 text-sm">
      <div className="flex flex-wrap items-center gap-2">
        <h2 className="font-semibold">Lịch họp</h2>
        {choXacNhanLich && (
          <span className="rounded-full bg-amber-100 px-2 py-0.5 text-xs font-semibold text-amber-900">
            Hồ sơ cũ chờ xác nhận lịch
          </span>
        )}
      </div>      
        {loi ? <p role="alert" className="text-red-800">{loi}</p>
        : !duLieuLich ? <p role="status">Đang tải lịch…</p>
          : hienTai.length === 0 && <p className="text-slate-600">Chưa có lịch hiện hành.</p>}
      {loiTrinh && <p role="alert" className="text-red-800">{loiTrinh}</p>}
      {hienNutTrinhLai && <p className="text-slate-600">
        Lịch đã duyệt bị hủy. Bạn có thể trình lịch đề xuất mới cùng kế hoạch và tài liệu đã lưu.
      </p>}
      {hienTai?.length > 0 && <DanhSachLich lich={hienTai} loaiHinh={loaiHinh}
        onTrinhLai={hienNutTrinhLai ? trinhLai : undefined} dangTrinh={dangTrinh}
        thoiDiemTai={duLieuLich.thoiDiemTai} />}
      {lichSu?.length > 0 && (
        <details className="border-t border-slate-100 pt-2">
          <summary className="cursor-pointer font-medium text-slate-700">Lịch sử thay đổi lịch ({lichSu.length})</summary>
          <DanhSachLich lich={lichSu} loaiHinh={loaiHinh} />
        </details>
      )}
    </section>
  )
}



