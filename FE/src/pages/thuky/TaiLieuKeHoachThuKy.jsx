import { useEffect, useState } from 'react'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import XemTruocTepKeHoach from './XemTruocTepKeHoach.jsx'
import {
  layChiTietDeXuat,
  layLichTheoCuocHop,
  layTaiLieuKeHoach,
  taiLenTaiLieuKeHoach,
  trinhKeHoach,
} from '../../api/cuocHopApi.js'

export default function TaiLieuKeHoachThuKy({
  cuocHopId,
  token,
  onDaTrinh,
  luaChonKeHoach,
  nguoiChuTriId,
  loaiHinh,
  vaiTroNguoiDeXuat,
  onLapLich,
  choPhepThaoTac = true,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [dangTaiLen, setDangTaiLen] = useState(false)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [loiTaiLen, setLoiTaiLen] = useState('')

  const [lichDeXuat, setLichDeXuat] = useState(null)
  const [loiLich, setLoiLich] = useState('')
  const [dangTaiLich, setDangTaiLich] = useState(true)
  const [lanTaiLich, setLanTaiLich] = useState(0)

  const lichCoTheTrinh = lichDeXuat?.filter((lich) =>
    new Date(lich.thoiGianBatDau).getTime() > Date.now()
    && (loaiHinh !== 'TRUC_TIEP' || lich.phongHopId != null)
    && (loaiHinh !== 'TRUC_TUYEN' || lich.phongHopId == null),
  )
  useEffect(() => {
    const controller = new AbortController()
    layLichTheoCuocHop(cuocHopId, token, controller.signal)
      .then((lich) => {
        if (!controller.signal.aborted) {
          setLichDeXuat(lich.filter((muc) => muc.trangThai === 'DE_XUAT'))
        }
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiLich(loi.message)
      })
      .finally(() => {
        if (!controller.signal.aborted) setDangTaiLich(false)
      })
    return () => controller.abort()
  }, [cuocHopId, token, lanTaiLich, phienDongBo])

  const [dangTrinh, setDangTrinh] = useState(false)
  const [daTrinh, setDaTrinh] = useState(false)
  const [loiTrinh, setLoiTrinh] = useState('')
  const coChuTriHopLe = luaChonKeHoach?.nguoiChuTri.some(
    (nguoi) => nguoi.nguoiDungId === nguoiChuTriId,
  )

    
  useEffect(() => {
    const controller = new AbortController()

    async function taiDanhSach() {
      try {
        const duLieu = await layTaiLieuKeHoach(
          cuocHopId, token, controller.signal,
        )
        if (controller.signal.aborted) return

        setDanhSach(duLieu)
        setLoiDanhSach('')
      } catch (loi) {
        if (!controller.signal.aborted) setLoiDanhSach(loi.message)
      }
    }

    taiDanhSach()
    return () => controller.abort()
  }, [cuocHopId, token, lanTai, phienDongBo])

  async function xuLyTaiLen(event) {
    event.preventDefault()
    if (dangTaiLen) return

    const form = event.currentTarget
    const file = new FormData(form).get('file')

    if (!file?.name) {
      setLoiTaiLen('Vui lòng chọn tài liệu.')
      return
    }

    setDangTaiLen(true)
    setLoiTaiLen('')

    try {
      await taiLenTaiLieuKeHoach(cuocHopId, file, token)
      form.reset()
      setLanTai((lanHienTai) => lanHienTai + 1)
    } catch (loi) {
      setLoiTaiLen(loi.message)
    } finally {
      setDangTaiLen(false)
    }
  }

  async function xuLyTrinh(event) {
    event.preventDefault()
    if (dangTrinh || daTrinh || dangTaiLen || !coChuTriHopLe) return

    const form = new FormData(event.currentTarget)
    const duLieu = {
      lichHopId: Number(form.get('lichHopId')),
      taiLieuIds: form.getAll('taiLieuIds').map(Number),
      ghiChuTrinh: form.get('ghiChuTrinh').trim() || null,
      lanhDaoDuyetId: null,
    }
    const daChonLichPhuHop = lichCoTheTrinh?.some(
      (lich) => Number(lich.lichHopId) === duLieu.lichHopId,
    )

    if (!daChonLichPhuHop) {
      setLoiTrinh('Chọn một lịch đề xuất còn hiệu lực để trình.')
      return
    }

    setDangTrinh(true)
    setLoiTrinh('')

    try {
      await trinhKeHoach(cuocHopId, duLieu, token)
      setDaTrinh(true)
    } catch (loi) {
      setLoiTrinh(loi.message)
      setDangTrinh(false)
      return
    }

    try {
      const chiTietMoi = await layChiTietDeXuat(cuocHopId, token)
      onDaTrinh(chiTietMoi)
    } catch {
      setLoiTrinh('Đã trình kế hoạch và lịch. Hãy tải lại trang để xem bước xử lý mới.')
    } finally {
      setDangTrinh(false)
    }
  }
  return (
    <XemTruocTepKeHoach danhSach={danhSach} cuocHopId={cuocHopId} token={token} loiDanhSach={loiDanhSach}>
    <section className="space-y-4 border-t border-slate-200 pt-5">
      <h3 className="font-semibold">Tải tài liệu lên</h3>

      {choPhepThaoTac ? (
        <form onSubmit={xuLyTaiLen} className="flex flex-wrap items-end gap-3">
          <label className="grid gap-1 text-sm font-medium">
            Chọn tài liệu
            <input
              name="file"
              type="file"
              accept=".pdf,.doc,.docx,.xls,.xlsx"
              required
              className="max-w-full rounded-lg border border-slate-300 p-2"
            />
          </label>

          <button
            type="submit"
            disabled={dangTaiLen}
            className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
          >
            {dangTaiLen ? 'Đang tải lên…' : 'Tải lên'}
          </button>
        </form>
      ) : (
        <p className="text-sm text-slate-600">Lưu nội dung cập nhật trước khi tải tệp mới hoặc trình kế hoạch.</p>
      )}

      {loiTaiLen && <p role="alert" className="text-sm text-red-800">{loiTaiLen}</p>}

      {choPhepThaoTac && danhSach !== null && !loiDanhSach && (
  <form onSubmit={xuLyTrinh} className="space-y-4 border-t border-slate-200 pt-4">
    <h4 className="font-semibold">Trình duyệt kế hoạch và lịch</h4>
    <p className="text-sm text-slate-600">
      {vaiTroNguoiDeXuat === 'LANH_DAO'
        ? 'Hồ sơ gửi thẳng tới lãnh đạo đề xuất để duyệt kế hoạch và lịch.'
        : luaChonKeHoach?.canChonLanhDao
          ? 'Hồ sơ gửi trưởng phòng trước. Sau khi trưởng phòng đồng ý, thư ký sẽ chọn lãnh đạo nhận hồ sơ.'
          : 'Hồ sơ gửi trưởng phòng để duyệt kế hoạch và lịch.'}
    </p>
    <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-900">
      Chọn lịch đề xuất còn hiệu lực. Khi người có thẩm quyền duyệt cuối, hệ thống sẽ xác nhận luôn lịch này.
    </p>

    <button
      type="button"
      disabled={dangTaiLich}
      onClick={() => {
        setDangTaiLich(true)
        setLoiLich('')
        setLanTaiLich((lanHienTai) => lanHienTai + 1)
      }}
      className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-700 disabled:opacity-60"
    >
      {dangTaiLich ? 'Đang tải lịch…' : 'Làm mới danh sách lịch'}
    </button>

    <fieldset disabled={dangTrinh || daTrinh || dangTaiLich} className="space-y-3">
      {loiLich ? (
  <p role="alert" className="text-sm text-red-800">{loiLich}</p>
) : lichCoTheTrinh == null ? (
  <p role="status" className="text-sm text-slate-600">{dangTaiLich ? 'Đang tải lịch…' : 'Chưa tải được danh sách lịch.'}</p>
) : lichCoTheTrinh.length > 0 ? (
  <label className="block text-sm font-medium">
    Chọn lịch đề xuất để trình
    <select
      name="lichHopId"
      required
      defaultValue=""
      className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
    >
      <option value="">Chọn lịch để trình</option>
      {lichCoTheTrinh.map((lich) => (
        <option key={lich.lichHopId} value={lich.lichHopId}>
          {new Date(lich.thoiGianBatDau).toLocaleString('vi-VN')}
          {' – '}{new Date(lich.thoiGianKetThuc).toLocaleString('vi-VN')}
          {' · '}{lich.tenPhongHop
            || (loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : `Phòng #${lich.phongHopId}`)}
        </option>
      ))}
    </select>
  </label>
) : (
  <div className="space-y-2">
    <p className="text-sm text-amber-900">
      Chưa có lịch đề xuất phù hợp để trình. Kiểm tra thời gian và phòng họp.
    </p>
    {choPhepThaoTac && onLapLich && (
      <button
        type="button"
        onClick={() => onLapLich(cuocHopId)}
        className="rounded-lg border px-3 py-2 text-sm font-semibold"
      >
        Đề xuất lịch trên Calendar
      </button>
    )}
  </div>
)}

      {danhSach.length > 0 && (
        <div className="space-y-2 text-sm">
          <p className="font-medium">Tệp đính kèm lần trình (tùy chọn)</p>
          {danhSach.map((taiLieu) => (
            <label key={taiLieu.taiLieuId} className="flex items-start gap-2">
              <input type="checkbox" name="taiLieuIds" value={taiLieu.taiLieuId}
                defaultChecked={!taiLieu.daDuocTrinh} className="mt-1" />
              <span className="break-all">{taiLieu.tenFileGoc || taiLieu.tenTaiLieu}</span>
            </label>
          ))}
        </div>
      )}

      <label className="block text-sm font-medium">
        Ghi chú trình
        <textarea
          name="ghiChuTrinh"
          rows={2}
          className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
        />
      </label>
    </fieldset>

    {luaChonKeHoach && !coChuTriHopLe && (
      <p className="text-sm text-amber-900">Chọn người chủ trì hợp lệ và lưu kế hoạch trước khi trình.</p>
    )}
    {loiTrinh && <p role="alert" className="text-sm text-red-800">{loiTrinh}</p>}

    <button
      type="submit"
      disabled={dangTrinh || daTrinh || dangTaiLen || !coChuTriHopLe || !lichCoTheTrinh?.length}
      className="rounded-lg bg-emerald-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60"
    >
      {dangTrinh ? 'Đang trình…' : daTrinh ? 'Đã trình kế hoạch và lịch' : 'Trình duyệt kế hoạch và lịch'}
    </button>
  </form>
)}
    </section>
    </XemTruocTepKeHoach>
  )
}



