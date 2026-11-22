import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { doiLichHop } from '../../api/nghiepVuCuocHopApi.js'
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
            <strong>{muc.laLichDoi && muc.trangThai === 'DE_XUAT' ? 'Chờ xác nhận dời lịch' : TEN_TRANG_THAI[muc.trangThai] || muc.trangThai}</strong>
            <span className="text-slate-500">Lịch #{muc.lichHopId}</span>
            {onTrinhLai && !muc.laLichDoi && muc.trangThai === 'DE_XUAT' && Date.parse(muc.thoiGianBatDau) > thoiDiemTai && (
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
  const [lanTai, setLanTai] = useState(0)
  const [loi, setLoi] = useState('')
  const [dangTrinh, setDangTrinh] = useState(null)
  const [loiTrinh, setLoiTrinh] = useState('')
  const [lyDoTrinhLai, setLyDoTrinhLai] = useState('')

  async function trinhLai(lichHopId) {
    if (dangTrinh != null) return
    const lyDo = lyDoTrinhLai.trim()
    if (!lyDo) {
      setLoiTrinh('Nhập lý do chọn lịch thay thế trước khi trình.')
      return
    }
    setDangTrinh(lichHopId)
    setLoiTrinh('')
    try {
      const ketQua = await trinhLaiLichSauHuy(cuocHopId, lichHopId, token, lyDo)
      setLyDoTrinhLai('')
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
  }, [cuocHopId, token, phienDongBo, lanTai])

  const hienTai = lich?.filter((muc) => ['DA_XAC_NHAN', 'DE_XUAT'].includes(muc.trangThai))
  const choXacNhanLich = !loi && hienTai?.some(
    (muc) => muc.lichHopId === lichHopIdKeHoachDaDuyet && muc.trangThai === 'DE_XUAT',
  )

  const lichDaXacNhan = hienTai?.find((muc) => muc.trangThai === 'DA_XAC_NHAN')
  const lichDangDoi = hienTai?.find((muc) => muc.laLichDoi && muc.trangThai === 'DE_XUAT')
  const lichSu = lich?.filter((muc) => !hienTai.includes(muc))
  const hienNutTrinhLai = !loi && coQuyenTrinhLai && !hienTai?.some((muc) => muc.trangThai === 'DA_XAC_NHAN')
    && lichSu?.some((muc) => (muc.lichHopId === lichHopIdKeHoachDaDuyet || muc.laLichDoi)
      && muc.thoiGianXacNhan && muc.trangThai === 'DA_HUY')
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
      {coQuyenTrinhLai && lichDaXacNhan && !lichDangDoi && (
        <FormDoiLich lich={lichDaXacNhan} token={token} onDaGui={() => setLanTai((lan) => lan + 1)} />
      )}
      {lichDangDoi && <p role="status" className="text-amber-950">
        Đang chờ người có quyền xác nhận giờ mới. Cuộc họp tạm dừng mở trong thời gian chờ.
      </p>}
      {hienNutTrinhLai && (
        <div className="space-y-2 rounded-lg border border-amber-200 bg-amber-50 p-3">
          <p className="text-amber-950">Lịch đã duyệt bị hủy. Lần trình mới sẽ dùng tài liệu của kế hoạch đã duyệt.</p>
          <label className="block font-medium text-slate-800">
            Lý do đề xuất lịch thay thế
            <textarea value={lyDoTrinhLai} onChange={(event) => setLyDoTrinhLai(event.target.value)}
              disabled={dangTrinh != null} rows={2}
              className="mt-1 w-full rounded-lg border border-slate-300 bg-white px-3 py-2" />
          </label>
        </div>
      )}
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




function FormDoiLich({ lich, token, onDaGui }) {
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)

  async function gui(event) {
    event.preventDefault()
    const duLieu = Object.fromEntries(new FormData(event.currentTarget))
    setLoi('')
    setDangGui(true)
    try {
      await doiLichHop(lich.lichHopId, token, duLieu)
      onDaGui()
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  const truong = 'mt-1 w-full rounded border border-slate-300 px-2 py-1.5'
  return (
    <details className="rounded-lg border border-slate-200 p-3">
      <summary className="cursor-pointer font-semibold">Dời lịch họp</summary>
      <form onSubmit={gui} className="mt-3 space-y-2">
        <fieldset disabled={dangGui} className="grid gap-2 sm:grid-cols-2">
          <label>Bắt đầu mới<input name="thoiGianBatDau" type="datetime-local" required className={truong} /></label>
          <label>Kết thúc mới<input name="thoiGianKetThuc" type="datetime-local" required className={truong} /></label>
          <label className="sm:col-span-2">Lý do dời lịch
            <textarea name="lyDoThayDoi" required maxLength={500} className={truong} />
          </label>
          <button className="justify-self-start rounded bg-emerald-800 px-3 py-2 font-semibold text-white disabled:opacity-60">
            Gửi thông báo dời lịch
          </button>
        </fieldset>
        {loi && <p role="alert" className="text-red-800">{loi}</p>}
      </form>
    </details>
  )
}
