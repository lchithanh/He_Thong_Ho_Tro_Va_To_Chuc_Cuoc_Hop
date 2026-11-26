import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { useEffect, useState } from 'react'
import Paperclip from 'lucide-react/dist/esm/icons/paperclip.mjs'
import Send from 'lucide-react/dist/esm/icons/send.mjs'
import { TieuDeHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_NUT_CHINH, LOP_NUT_PHU } from '../../components/giaoDienPhongHop.js'
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
  lichHopIdKeHoachDaDuyet,
  loaiHinh,
  vaiTroNguoiDeXuat,
  onLapLich,
  lichMoiTaoId,
  choPhepThaoTac = true,
  choDoiLich = false,
}) {
  const phienDongBo = useDongBoDuLieu()
  const [danhSach, setDanhSach] = useState(null)
  const [lanTai, setLanTai] = useState(0)
  const [dangTaiLen, setDangTaiLen] = useState(false)
  const [loiDanhSach, setLoiDanhSach] = useState('')
  const [loiTaiLen, setLoiTaiLen] = useState('')

  const [danhSachLich, setDanhSachLich] = useState(null)
  const [loiLich, setLoiLich] = useState('')
  const [dangTaiLich, setDangTaiLich] = useState(true)
  const [lichDaChon, setLichDaChon] = useState(null)
  const lichHopIdDangChon = lichDaChon && lichDaChon.lichMoiTaoId === lichMoiTaoId
    ? lichDaChon.giaTri : String(lichMoiTaoId ?? '')
  const [thoiDiemTaiLich, setThoiDiemTaiLich] = useState(0)
  const [lanTaiLich, setLanTaiLich] = useState(0)

  const choPhanHoiDoiLich = danhSachLich?.some((lich) => lich.laLichDoi && lich.trangThai === 'DE_XUAT')
  const lichCoTheTrinh = danhSachLich?.filter((lich) =>
    ((lich.trangThai === 'DE_XUAT' && !lich.laLichDoi)
      || (lich.trangThai === 'DA_XAC_NHAN' && lich.lichHopId === lichHopIdKeHoachDaDuyet))
    && new Date(lich.thoiGianBatDau).getTime() > thoiDiemTaiLich
    && (loaiHinh !== 'TRUC_TIEP' || lich.phongHopId != null)
    && (loaiHinh !== 'TRUC_TUYEN' || lich.phongHopId == null),
  )
  useEffect(() => {
    const controller = new AbortController()
    layLichTheoCuocHop(cuocHopId, token, controller.signal)
      .then((lich) => {
        if (!controller.signal.aborted) {
          setDanhSachLich(lich)
          setThoiDiemTaiLich(Date.now())
          setLoiLich('')
        }
      })
      .catch((loi) => {
        if (!controller.signal.aborted) setLoiLich(loi.message)
      })
      .finally(() => {
        if (!controller.signal.aborted) setDangTaiLich(false)
      })
    return () => controller.abort()
  }, [cuocHopId, token, lanTaiLich, lichMoiTaoId, phienDongBo])

  const lichDangChon = lichCoTheTrinh?.find((lich) => String(lich.lichHopId) === lichHopIdDangChon)
  const dangDoiLich = lichHopIdKeHoachDaDuyet != null && lichDangChon != null
    && lichDangChon.lichHopId !== lichHopIdKeHoachDaDuyet
  const dangGiuLich = lichDangChon?.trangThai === 'DA_XAC_NHAN'
  const canNhapLyDo = dangGiuLich || (dangDoiLich && !lichDangChon.lyDoThayDoi?.trim())
  const nhanGhiChu = dangGiuLich ? 'Lý do cập nhật kế hoạch (bắt buộc)'
    : canNhapLyDo ? 'Lý do đề xuất lịch thay thế (bắt buộc)' : 'Ghi chú trình'
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
    if (dangTaiLen || !choPhepThaoTac) return

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
    if (!choPhepThaoTac || dangTrinh || daTrinh || dangTaiLen || !coChuTriHopLe || choPhanHoiDoiLich) return

    const form = new FormData(event.currentTarget)
    const duLieu = {
      lichHopId: Number(form.get('lichHopId')),
      taiLieuIds: form.getAll('taiLieuIds').map(Number),
      ghiChuTrinh: form.get('ghiChuTrinh').trim() || null,
      lanhDaoDuyetId: Number(form.get('lanhDaoDuyetId')) || null,
    }
    if (canNhapLyDo && !duLieu.ghiChuTrinh) {
      setLoiTrinh('Nhập lý do thay đổi trước khi trình.')
      return
    }
    const daChonLichPhuHop = lichCoTheTrinh?.some(
      (lich) => Number(lich.lichHopId) === duLieu.lichHopId && Date.parse(lich.thoiGianBatDau) > Date.now(),
    )

    if (!daChonLichPhuHop) {
      setLoiTrinh('Chọn lịch còn hiệu lực để trình.')
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
    <section className="space-y-4 rounded-2xl border border-slate-200 p-4 sm:p-5">
      <TieuDeHoSo Icon={Paperclip}>Tài liệu kế hoạch</TieuDeHoSo>

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
            className={LOP_NUT_CHINH}
          >
            {dangTaiLen ? 'Đang tải lên…' : 'Tải lên'}
          </button>
        </form>
      ) : (
        <p className="text-sm text-slate-600">{choDoiLich
          ? 'Tạm khóa tải tệp và trình kế hoạch trong lúc lịch mới đang chờ xử lý.'
          : 'Lưu nội dung cập nhật trước khi tải tệp mới hoặc trình kế hoạch.'}</p>
      )}

      {loiTaiLen && <p role="alert" className="text-sm text-red-800">{loiTaiLen}</p>}

      {choPhepThaoTac && danhSach !== null && !loiDanhSach && (
  <form onSubmit={xuLyTrinh} className="space-y-4 rounded-xl border border-blue-100 bg-blue-50/55 p-4">
    <TieuDeHoSo Icon={Send} cap="h4">Trình duyệt kế hoạch và lịch</TieuDeHoSo>
    <p className="text-sm text-slate-600">
      {luaChonKeHoach?.thuKyNhomCoDinh
        ? luaChonKeHoach.capDuyetCuoiNhom === 'LANH_DAO'
          ? 'Tổ trưởng xem xét trước. Sau khi Tổ trưởng đồng ý, thư ký nhóm chuyển hồ sơ lên lãnh đạo.'
          : 'Thư ký nhóm trình trực tiếp Tổ trưởng duyệt kế hoạch và lịch.'
        : vaiTroNguoiDeXuat === 'LANH_DAO'
        ? 'Hồ sơ gửi thẳng tới lãnh đạo đề xuất để duyệt kế hoạch và lịch.'
        : luaChonKeHoach?.canChonLanhDao
          ? 'Hồ sơ gửi trưởng phòng trước. Sau khi trưởng phòng đồng ý, thư ký ban đầu chuyển hồ sơ đến thư ký lãnh đạo để rà soát và trình duyệt cuối.'
          : 'Hồ sơ gửi trưởng phòng để duyệt kế hoạch và lịch.'}
    </p>
    {luaChonKeHoach?.capDuyetCuoiNhom === 'LANH_DAO' && (
      <label className="block text-sm font-medium">
        Lãnh đạo duyệt sau Tổ trưởng
        <select name="lanhDaoDuyetId" required className="mt-1 block w-full rounded-lg border border-slate-300 p-2"
          key={luaChonKeHoach.nguoiDuyetNhomId}
          defaultValue={luaChonKeHoach.lanhDaoDuyet.some((nguoi) => nguoi.nguoiDungId === luaChonKeHoach.nguoiDuyetNhomId)
            ? luaChonKeHoach.nguoiDuyetNhomId
            : luaChonKeHoach.lanhDaoDuyet.length === 1 ? luaChonKeHoach.lanhDaoDuyet[0].nguoiDungId : ''}>
          <option value="">Chọn người duyệt</option>
          {luaChonKeHoach.lanhDaoDuyet.map((nguoi) => <option key={nguoi.nguoiDungId} value={nguoi.nguoiDungId}>
            {nguoi.hoTen} · {nguoi.chucVu}
          </option>)}
        </select>
        {!luaChonKeHoach.lanhDaoDuyet.length && <span role="alert">Chưa có tài khoản đúng thẩm quyền.</span>}
      </label>
    )}
    {lichHopIdKeHoachDaDuyet != null && <p className="rounded-lg bg-amber-50 p-3 text-sm text-amber-900">
      Nếu chỉ sửa nội dung, chọn Giữ lịch đã xác nhận.
    </p>}

    <button
      type="button"
      disabled={dangTaiLich}
      onClick={() => {
        setDangTaiLich(true)
        setLoiLich('')
        setLanTaiLich((lanHienTai) => lanHienTai + 1)
      }}
      className={LOP_NUT_PHU}
    >
      {dangTaiLich ? 'Đang tải lịch…' : 'Làm mới danh sách lịch'}
    </button>

    {choPhanHoiDoiLich && <p className="text-sm text-amber-950">Đang lấy phản hồi lịch mới. Mở lịch này trong Lịch họp để xử lý dời lịch.</p>}
    <fieldset disabled={dangTrinh || daTrinh || dangTaiLich || choPhanHoiDoiLich} className="space-y-3">
      {loiLich ? (
  <p role="alert" className="text-sm text-red-800">{loiLich}</p>
) : lichCoTheTrinh == null ? (
  <p role="status" className="text-sm text-slate-600">{dangTaiLich ? 'Đang tải lịch…' : 'Chưa tải được danh sách lịch.'}</p>
) : lichCoTheTrinh.length > 0 ? (
  <label className="block text-sm font-medium">
    Lịch của lần trình
    <select
      name="lichHopId"
      required
      value={lichDangChon ? lichHopIdDangChon : ''}
      onChange={(event) => setLichDaChon({ lichMoiTaoId, giaTri: event.target.value })}
      className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
    >
      <option value="">Chọn lịch để trình</option>
      {lichCoTheTrinh.map((lich) => (
        <option key={lich.lichHopId} value={lich.lichHopId}>
          {lich.trangThai === 'DA_XAC_NHAN' ? 'Giữ lịch đã xác nhận' : 'Lịch đề xuất'}
          {' · '}{new Date(lich.thoiGianBatDau).toLocaleString('vi-VN')}
          {' – '}{new Date(lich.thoiGianKetThuc).toLocaleString('vi-VN')}
          {' · '}{lich.tenPhongHop
            || (loaiHinh === 'TRUC_TUYEN' ? 'Trực tuyến' : 'Chưa có tên phòng')}
        </option>
      ))}
    </select>
  </label>
) : (
  <div className="space-y-2">
    <p className="text-sm text-amber-900">
      Chưa có lịch còn hiệu lực để trình. Kiểm tra thời gian và phòng họp.
    </p>
    {choPhepThaoTac && onLapLich && (
      <button
        type="button"
        onClick={() => onLapLich(cuocHopId)}
        className={LOP_NUT_PHU}
      >
        Tạo lịch đề xuất
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
        {nhanGhiChu}
        <textarea
          name="ghiChuTrinh"
          required={canNhapLyDo}
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
      disabled={dangTrinh || daTrinh || dangTaiLen || !coChuTriHopLe || !lichCoTheTrinh?.length || choPhanHoiDoiLich}
      className={LOP_NUT_CHINH}
    >
      {dangTrinh ? 'Đang trình…' : daTrinh ? 'Đã trình kế hoạch và lịch' : 'Trình duyệt kế hoạch và lịch'}
    </button>
  </form>
)}
    </section>
    </XemTruocTepKeHoach>
  )
}



