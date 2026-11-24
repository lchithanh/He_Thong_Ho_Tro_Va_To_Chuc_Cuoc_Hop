import { useEffect, useRef, useState } from 'react'
import CloudUpload from 'lucide-react/dist/esm/icons/cloud-upload.mjs'
import FileSpreadsheet from 'lucide-react/dist/esm/icons/file-spreadsheet.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import ClipboardList from 'lucide-react/dist/esm/icons/clipboard-list.mjs'
import MessageSquare from 'lucide-react/dist/esm/icons/message-square.mjs'
import Search from 'lucide-react/dist/esm/icons/search.mjs'
import { LOP_THE, LOP_NUT_PHU } from '../../components/GiaoDienPhongHop.js'
import { chiaSeTepPhongHop, layHoSoPhongHop } from '../../api/hoSoPhongHopApi.js'
import TepHoSo from '../../components/TepHoSo.jsx'
import { NoiDungXemTruoc } from '../thuky/XemTruocTepKeHoach.jsx'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'

const CAC_NHOM = [
  { id: 'keHoach', ten: 'Kế hoạch & tài liệu họp' },
  { id: 'nghiQuyet', ten: 'Nghị quyết' },
  { id: 'chiaSe', ten: 'Tài liệu trong cuộc họp' },
  { id: 'bienBan', ten: 'Biên bản cuộc họp' },
  { id: 'ketLuan', ten: 'Kết luận cuộc họp' },
]
const TRANG_THAI = {
  NHAP: 'Đang cập nhật',
  DANG_BIEU_QUYET: 'Đã lập phiên biểu quyết',
  THONG_QUA: 'Đã thông qua',
  KHONG_THONG_QUA: 'Không thông qua',
  CHUA_DAT_DA_SO: 'Chưa đạt đa số',
  YEU_CAU_CHINH_SUA: 'Yêu cầu chỉnh sửa',
  CHO_DUYET: 'Chờ duyệt',
  DA_DUYET: 'Đã duyệt',
  BI_TRA_LAI: 'Đã trả lại',
  DA_PHAT_HANH: 'Đã phát hành',
}
const LOP_KHUNG = LOP_THE
const LOP_PHU = 'text-sm text-slate-600 phong-toi:text-phong-chu-phu'

const DINH_DANG = {
  pdf: { ten: 'PDF', mau: 'bg-red-50 text-red-800 phong-toi:bg-phong-nhan-do phong-toi:text-phong-do' },
  doc: { ten: 'Word', mau: 'bg-blue-50 text-blue-800 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh' },
  docx: { ten: 'Word', mau: 'bg-blue-50 text-blue-800 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh' },
  xls: { ten: 'Excel', mau: 'bg-green-50 text-green-800 phong-toi:bg-phong-nhan-luc phong-toi:text-phong-luc' },
  xlsx: { ten: 'Excel', mau: 'bg-green-50 text-green-800 phong-toi:bg-phong-nhan-luc phong-toi:text-phong-luc' },
}
const BO_LOC_TEP = ['Tất cả', 'Excel', 'PDF', 'Word']
const LOP_CHON = 'border-blue-800 bg-blue-800 text-white phong-toi:border-phong-vien-xanh phong-toi:bg-phong-menu-chon phong-toi:text-white'
const LOP_CHUA_CHON = 'border-slate-200 bg-slate-100 text-slate-700 hover:bg-blue-50 phong-toi:border-phong-vien phong-toi:bg-phong-mat-noi phong-toi:text-phong-chu-phu phong-toi:hover:bg-phong-nhan-xanh'
const LOP_O_NHAP = 'rounded-lg border border-slate-300 px-3 py-2 text-sm phong-toi:truong-phong-hop'
const TOI_DA_BYTE = 10 * 1024 * 1024

function layDinhDang(ten, dinhDang) {
  return (dinhDang || ten.split('.').at(-1)).toLowerCase()
}

function chuanHoaTimKiem(giaTri) {
  return giaTri.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLocaleLowerCase('vi').replaceAll('đ', 'd').trim()
}

function hienThiDungLuong(soByte) {
  if (!Number.isFinite(soByte)) return null
  if (soByte < 1024) return `${soByte} B`
  const dinhDang = new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 1 })
  return soByte < 1024 ** 2 ? `${dinhDang.format(soByte / 1024)} KB` : `${dinhDang.format(soByte / 1024 ** 2)} MB`
}

function demSoTaiLieu(hoSo, id) {
  if (!hoSo) return null
  if (id === 'bienBan' || id === 'ketLuan') return hoSo[id]?.taiLieu.length ?? 0
  if (id === 'nghiQuyet') return hoSo.nghiQuyet.filter((muc) => muc.taiLieuId).length
  return hoSo[id].length
}

function NhanTrangThai({ ma, ten }) {
  const mau = ['THONG_QUA', 'DA_DUYET', 'DA_PHAT_HANH'].includes(ma)
    ? DINH_DANG.xlsx.mau
    : LOP_CHUA_CHON
  return (
    <span className={`inline-flex w-fit rounded-full px-2 py-1 text-xs font-semibold ${mau}`}>
      {ten ?? TRANG_THAI[ma] ?? ma}
    </span>
  )
}

function TheTepHoSo({ tep, token, trangThai }) {
  const dinhDang = layDinhDang(tep.ten, tep.dinhDang)
  const { ten, mau } = DINH_DANG[dinhDang] ?? { ten: 'Tệp', mau: LOP_CHUA_CHON }
  const Icon = ten === 'Excel' ? FileSpreadsheet : FileText
  const thongTin = [
    tep.nguoiTaiLen, tep.ngayTaiLen && dinhDangNgayGio(tep.ngayTaiLen), hienThiDungLuong(tep.dungLuongByte),
  ].filter(Boolean)
  const xemNoiDung = dinhDang === 'pdf' ? undefined : (layTep) => (
    <div className="overflow-hidden rounded-lg border border-slate-200 bg-white text-slate-900">
      <NoiDungXemTruoc
        tep={{ taiLieuId: tep.taiLieuId, tenFileGoc: tep.tenTaiVe ?? tep.ten, dinhDangFile: dinhDang }}
        token={token}
        layTep={layTep}
      />
    </div>
  )
  const tomTat = (
    <div className="flex min-w-0 flex-1 basis-56 items-start gap-3">
      <span className={`grid size-10 shrink-0 place-items-center rounded-xl ${mau}`}><Icon size={18} aria-hidden="true" /></span>
      <div className="min-w-0 space-y-1">
        <div className="flex flex-wrap items-center gap-2">
          <p className="wrap-anywhere text-sm font-bold">{tep.ten}</p>
          {tep.duongDan && <span className={`rounded px-1.5 py-0.5 text-[0.65rem] font-bold uppercase ${mau}`}>{ten}</span>}
          {trangThai && <NhanTrangThai ma={trangThai.ma} ten={trangThai.ten} />}
        </div>
        {thongTin.length > 0 && <p className="wrap-anywhere text-xs text-slate-600 phong-toi:text-phong-chu-phu">{thongTin.join(' · ')}</p>}
        {tep.ghiChu && <p className="flex items-start gap-1.5 text-xs text-slate-700 phong-toi:text-phong-chu-phu">
          <MessageSquare size={13} className="mt-0.5 shrink-0" aria-hidden="true" />
          <span className="whitespace-pre-wrap wrap-anywhere">{tep.ghiChu}</span>
        </p>}
      </div>
    </div>
  )

  if (!tep.duongDan) return tomTat
  return <TepHoSo token={token} ten={tep.tenTaiVe ?? tep.ten} duongDan={tep.duongDan} tomTat={tomTat}
    xemPdf={dinhDang === 'pdf'} xemNoiDung={xemNoiDung} xemRong taiTrucTiep />
}

function ThanhLocHoSo({ ten, soMuc, tuKhoa, setTuKhoa, goiY, nhanTimKiem, cacLuaChon, boLoc, setBoLoc, nhanBoLoc }) {
  return (
    <>
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h3 className="flex items-center gap-2 font-bold">{ten}
          <span className="min-w-6 rounded-full bg-slate-100 px-2 text-center text-xs leading-5 text-slate-700 phong-toi:bg-phong-mat-noi phong-toi:text-phong-chu-phu">{soMuc}</span>
        </h3>
        <div className="relative w-full sm:w-auto sm:min-w-56">
          <Search size={15} className="pointer-events-none absolute top-1/2 left-3 -translate-y-1/2 text-slate-500 phong-toi:text-phong-chu-phu" aria-hidden="true" />
          <input type="search" aria-label={nhanTimKiem} placeholder={goiY}
            value={tuKhoa} onChange={(event) => setTuKhoa(event.target.value)} className={`${LOP_O_NHAP} w-full pl-9`} />
        </div>
      </div>
      <div className="flex flex-wrap gap-2" role="group" aria-label={nhanBoLoc}>
        {cacLuaChon.map((loc) => (
          <button key={loc} type="button" aria-pressed={boLoc === loc} onClick={() => setBoLoc(loc)}
            className={`rounded-full border px-3 py-1 text-xs font-semibold ${boLoc === loc ? LOP_CHON : LOP_CHUA_CHON}`}>{loc}</button>
        ))}
      </div>
    </>
  )
}

function KhoTaiLieu({ ten, danhSach, token, rong = 'Chưa có tệp đính kèm.', coNguoiGui = false }) {
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('Tất cả')
  const tuKhoaDaChuanHoa = chuanHoaTimKiem(tuKhoa)
  const tepDaLoc = danhSach.filter((tep) => {
    const loai = DINH_DANG[layDinhDang(tep.ten, tep.dinhDang)]?.ten
    const noiDungTimKiem = chuanHoaTimKiem([tep.ten, tep.nguoiTaiLen, tep.ghiChu].filter(Boolean).join(' '))
    return (boLoc === 'Tất cả' || loai === boLoc) && noiDungTimKiem.includes(tuKhoaDaChuanHoa)
  })

  return (
    <section className={`${LOP_KHUNG} space-y-3`}>
      {danhSach.length > 0 ? (
        <ThanhLocHoSo ten={ten} soMuc={danhSach.length} tuKhoa={tuKhoa} setTuKhoa={setTuKhoa}
          nhanTimKiem={coNguoiGui ? 'Tìm tệp đã chia sẻ' : `Tìm trong ${ten.toLowerCase()}`}
          goiY={coNguoiGui ? 'Tìm tên tệp hoặc người gửi' : 'Tìm theo tên tệp'}
          cacLuaChon={BO_LOC_TEP} boLoc={boLoc} setBoLoc={setBoLoc} nhanBoLoc="Lọc theo định dạng tệp" />
      ) : <h3 className="font-bold">{ten}</h3>}
      {tepDaLoc.length > 0 ? (
        <ul className="space-y-2">
          {tepDaLoc.map((tep) => (
            <li key={tep.taiLieuId} className={LOP_KHUNG}>
              <TheTepHoSo tep={tep} token={token} />
            </li>
          ))}
        </ul>
      ) : <p role="status" className={LOP_PHU}>{danhSach.length ? 'Không tìm thấy tệp phù hợp.' : rong}</p>}
    </section>
  )
}

const TRANG_THAI_NGHI_QUYET = ['Tất cả', 'Dự thảo và đang xử lý', 'Đã thông qua']

function NhomNghiQuyet({ danhSach, token, cuocHopId }) {
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('Tất cả')
  const tuKhoaDaChuanHoa = chuanHoaTimKiem(tuKhoa)
  const daLoc = danhSach.filter((muc) => chuanHoaTimKiem(muc.tieuDe).includes(tuKhoaDaChuanHoa))
  const nhomNghiQuyet = [
    { ten: TRANG_THAI_NGHI_QUYET[1], muc: daLoc.filter((muc) => muc.trangThai !== 'THONG_QUA') },
    { ten: TRANG_THAI_NGHI_QUYET[2], muc: daLoc.filter((muc) => muc.trangThai === 'THONG_QUA') },
  ].filter(({ ten, muc }) => muc.length > 0 && (boLoc === 'Tất cả' || ten === boLoc))

  return (
    <section className={`${LOP_KHUNG} space-y-4`}>
      {danhSach.length > 0 ? (
        <ThanhLocHoSo ten="Danh sách nghị quyết" soMuc={danhSach.length} tuKhoa={tuKhoa} setTuKhoa={setTuKhoa}
          nhanTimKiem="Tìm nghị quyết" goiY="Tìm theo tiêu đề nghị quyết"
          cacLuaChon={TRANG_THAI_NGHI_QUYET} boLoc={boLoc} setBoLoc={setBoLoc} nhanBoLoc="Lọc theo trạng thái nghị quyết" />
      ) : <h3 className="font-bold">Danh sách nghị quyết</h3>}
      {nhomNghiQuyet.map(({ ten, muc }) => (
        <section key={ten} className="space-y-2">
          <h4 className="font-semibold">{ten}</h4>
          <ul className="space-y-2">
            {muc.map((nghiQuyet) => (
              <li key={nghiQuyet.nghiQuyetId} className={LOP_KHUNG}>
                <TheTepHoSo token={token} tep={{
                  taiLieuId: nghiQuyet.taiLieuId,
                  ten: nghiQuyet.tieuDe,
                  tenTaiVe: nghiQuyet.tieuDe + '.pdf',
                  dinhDang: 'pdf',
                  duongDan: nghiQuyet.taiLieuId
                    ? `/api/cuoc-hop/${cuocHopId}/nghi-quyet/${nghiQuyet.nghiQuyetId}/tep` : null,
                }} trangThai={{
                  ma: nghiQuyet.trangThai,
                  ten: nghiQuyet.trangThai === 'NHAP'
                    ? (nghiQuyet.ngayCongBo ? 'Dự thảo đã công bố' : 'Bản nháp')
                    : undefined,
                }} />
              </li>
            ))}
          </ul>
        </section>
      ))}
      {nhomNghiQuyet.length === 0 && <p role="status" className={LOP_PHU}>
        {danhSach.length ? 'Không tìm thấy nghị quyết phù hợp.' : 'Chưa có nghị quyết được chia sẻ.'}
      </p>}
    </section>
  )
}

function NhomVanBan({ vanBan, token, ten }) {
  const tenThuong = ten.toLocaleLowerCase('vi')

  return (
    <div className="space-y-4">
      <section className={`${LOP_KHUNG} space-y-3`}>
        <div className="flex flex-wrap items-center justify-between gap-2">
          <h3 className="flex items-center gap-2 font-bold"><ClipboardList size={19} aria-hidden="true" /> {ten}</h3>
          {vanBan && <NhanTrangThai ma={vanBan.trangThai} />}
        </div>
        {!vanBan && <p className={LOP_PHU}>Chưa có {tenThuong}.</p>}
        {vanBan?.coTheXem && (
          <>
            <h4 className="wrap-anywhere font-semibold">{vanBan.tieuDe}</h4>
            {vanBan.noiDung && <p className="whitespace-pre-wrap wrap-anywhere text-sm leading-relaxed">{vanBan.noiDung}</p>}
          </>
        )}
        {vanBan && !vanBan.coTheXem && <p className={LOP_PHU}>
          Thành viên có thể xem nội dung và tệp sau khi văn bản được phát hành.
        </p>}
      </section>
      {vanBan?.coTheXem && <KhoTaiLieu ten={`Tệp đính kèm ${tenThuong}`} danhSach={vanBan.taiLieu} token={token} />}
    </div>
  )
}

export default function HoSoTaiLieuPhongHop({ cuocHopId, token, onQuanLyHoSo }) {
  const phienDongBo = useDongBoDuLieu()
  const [nhom, setNhom] = useState('keHoach')
  const [hoSo, setHoSo] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [thongBaoChiaSe, setThongBaoChiaSe] = useState(null)
  const [dangTaiLen, setDangTaiLen] = useState(false)
  const [tepDaChon, setTepDaChon] = useState(null)
  const [ghiChu, setGhiChu] = useState('')
  const [dangKeoTep, setDangKeoTep] = useState(false)
  const [lanChiaSe, setLanChiaSe] = useState(0)
  const oChonTep = useRef(null)
  const [lanTai, setLanTai] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    layHoSoPhongHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        setHoSo(duLieu)
        setLoiTai('')
      })
      .catch((loi) => {
        if (controller.signal.aborted) return
        if (loi.status === 403 || loi.status === 404) setHoSo(null)
        setLoiTai(loi.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo, lanTai])

  function chonTep(file) {
    if (!file || dangTaiLen) return
    if (!DINH_DANG[layDinhDang(file.name)] || file.size > TOI_DA_BYTE || file.size === 0) {
      setThongBaoChiaSe({ loi: true, noiDung: 'Chọn tệp PDF, Word hoặc Excel có nội dung, dung lượng tối đa 10 MB.' })
      setTepDaChon(null)
      return
    }
    setThongBaoChiaSe(null)
    setTepDaChon(file)
  }

  function thaTep(event) {
    event.preventDefault()
    setDangKeoTep(false)
    if (dangTaiLen) return
    if (event.dataTransfer.files.length > 1) {
      setThongBaoChiaSe({ loi: true, noiDung: 'Kéo thả một tệp mỗi lần.' })
      return
    }
    chonTep(event.dataTransfer.files[0])
  }

  async function taiLen(event) {
    event.preventDefault()
    if (dangTaiLen || !hoSo?.coTheTaiLen || !tepDaChon) return
    setDangTaiLen(true)
    setThongBaoChiaSe(null)
    try {
      await chiaSeTepPhongHop(cuocHopId, tepDaChon, token, ghiChu)
      setTepDaChon(null)
      setGhiChu('')
      setLanChiaSe((lan) => lan + 1)
      setLanTai((lan) => lan + 1)
      setThongBaoChiaSe({ noiDung: 'Đã chia sẻ tài liệu cho thành viên cuộc họp.' })
    } catch (loi) {
      setThongBaoChiaSe({ loi: true, noiDung: loi.message })
    } finally {
      setDangTaiLen(false)
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <nav aria-label="Nhóm tài liệu phòng họp" className="flex flex-wrap gap-2">
          {CAC_NHOM.map(({ id, ten }) => {
            const soTep = demSoTaiLieu(hoSo, id)
            return (
              <button key={id} type="button" aria-label={ten} aria-pressed={nhom === id} onClick={() => setNhom(id)}
                className={`inline-flex items-center gap-1.5 rounded-lg border px-3 py-2 text-sm font-semibold ${nhom === id ? LOP_CHON : LOP_CHUA_CHON}`}>
                {ten}
                {soTep != null && <span aria-hidden="true"
                  className={`min-w-5 rounded-full px-1.5 text-center text-xs leading-5 ${nhom === id
                    ? 'bg-amber-300 text-slate-900' : 'bg-slate-200 text-slate-700 phong-toi:bg-phong-nen phong-toi:text-phong-chu-phu'}`}>{soTep}</span>}
              </button>
            )
          })}
        </nav>
        {onQuanLyHoSo && <button type="button" onClick={onQuanLyHoSo}
          className={LOP_NUT_PHU}>
          Quản lý hồ sơ
        </button>}
      </div>

      {loiTai && <p role="alert" className="text-sm text-red-800 phong-toi:text-phong-do">{loiTai}</p>}
      {!hoSo && !loiTai && <p role="status" className={LOP_PHU}>Đang tải hồ sơ cuộc họp…</p>}
      {hoSo && nhom === 'keHoach' && (
        <div className="space-y-4">
          {hoSo.noiDungDuKien && <section className={LOP_KHUNG}>
            <h3 className="flex items-center gap-2 font-bold"><ClipboardList size={19} aria-hidden="true" /> Chương trình họp</h3>
            <p className="mt-2 whitespace-pre-wrap wrap-anywhere text-sm">{hoSo.noiDungDuKien}</p>
          </section>}
          <KhoTaiLieu ten="Tài liệu kế hoạch đã duyệt" danhSach={hoSo.keHoach} token={token}
            rong="Kế hoạch đã duyệt chưa có tệp đính kèm." />
        </div>
      )}
      {hoSo && nhom === 'nghiQuyet' && <NhomNghiQuyet danhSach={hoSo.nghiQuyet} token={token} cuocHopId={cuocHopId} />}
      {hoSo && nhom === 'chiaSe' && (
        <div className="space-y-4">
          {hoSo.coTheTaiLen && (
            <section className={`${LOP_KHUNG} space-y-3`}>
              <div className="flex flex-wrap items-start justify-between gap-2">
                <div>
                  <h3 className="flex items-center gap-2 font-bold"><CloudUpload size={19} aria-hidden="true" /> Tải lên và chia sẻ tệp</h3>
                  <p className={LOP_PHU}>PDF, Word, Excel · tối đa 10 MB mỗi tệp</p>
                </div>
                <span className="rounded-full bg-green-50 px-2 py-1 text-xs font-semibold text-green-800 phong-toi:bg-phong-nhan-luc phong-toi:text-phong-luc">
                  Thành viên được chia sẻ
                </span>
              </div>
              <form onSubmit={taiLen} className="space-y-3">
                <div onDragOver={(event) => {
                  event.preventDefault()
                  if (!dangTaiLen && Array.from(event.dataTransfer.types).includes('Files')) setDangKeoTep(true)
                }} onDragLeave={(event) => {
                  if (!(event.relatedTarget instanceof Node) || !event.currentTarget.contains(event.relatedTarget)) setDangKeoTep(false)
                }} onDrop={thaTep}
                  className={`flex flex-wrap items-center justify-center gap-2 rounded-xl border border-dashed p-4 ${dangKeoTep
                    ? 'border-blue-600 bg-blue-50 phong-toi:border-phong-focus phong-toi:bg-phong-nhan-xanh'
                    : 'border-slate-400 phong-toi:border-phong-vien'}`}>
                  <input ref={oChonTep} type="file" name="file" hidden disabled={dangTaiLen} accept=".pdf,.doc,.docx,.xls,.xlsx"
                    onChange={(event) => {
                      chonTep(event.currentTarget.files[0])
                      event.currentTarget.value = ''
                    }} />
                  <button type="button" disabled={dangTaiLen} onClick={() => oChonTep.current?.click()}
                    className={`rounded-lg border px-3 py-2 text-sm font-semibold disabled:opacity-60 ${LOP_CHUA_CHON}`}>
                    Chọn tệp từ máy
                  </button>
                  <p className={LOP_PHU}>hoặc kéo thả một tệp vào khung này</p>
                  {tepDaChon && (
                    <div className="flex w-full flex-wrap items-center justify-center gap-2">
                      <p className="min-w-0 wrap-anywhere text-sm font-semibold">{tepDaChon.name}</p>
                      <button type="button" disabled={dangTaiLen} className="text-sm underline disabled:opacity-60"
                        onClick={() => setTepDaChon(null)}>Bỏ tệp đã chọn</button>
                    </div>
                  )}
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <input type="text" name="ghiChu" aria-label="Ghi chú tài liệu" placeholder="Ghi chú cho tài liệu (tùy chọn)"
                    maxLength={500} value={ghiChu} onChange={(event) => setGhiChu(event.target.value)} disabled={dangTaiLen}
                    className={`${LOP_O_NHAP} min-w-0 flex-1 basis-56`} />
                  <button type="submit" disabled={dangTaiLen || !tepDaChon}
                    className="rounded-lg bg-blue-800 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60 phong-toi:bg-phong-nut">
                    {dangTaiLen ? 'Đang tải lên…' : 'Tải lên và chia sẻ'}
                  </button>
                </div>
              </form>
              {thongBaoChiaSe && <p role={thongBaoChiaSe.loi ? 'alert' : 'status'}
                className={thongBaoChiaSe.loi ? 'text-sm text-red-800 phong-toi:text-phong-do' : LOP_PHU}>{thongBaoChiaSe.noiDung}</p>}
            </section>
          )}
          <KhoTaiLieu key={lanChiaSe} ten="Tệp đã chia sẻ" danhSach={hoSo.chiaSe} token={token} coNguoiGui
            rong="Chưa có tệp nào được chia sẻ trong cuộc họp." />
        </div>
      )}
      {hoSo && nhom === 'bienBan' && <NhomVanBan vanBan={hoSo.bienBan} token={token} ten="Biên bản cuộc họp" />}
      {hoSo && nhom === 'ketLuan' && <NhomVanBan vanBan={hoSo.ketLuan} token={token} ten="Kết luận cuộc họp" />}
    </div>
  )
}
