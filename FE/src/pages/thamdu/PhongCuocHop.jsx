import { useEffect, useState } from 'react'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import Radio from 'lucide-react/dist/esm/icons/radio.mjs'
import { ketThucCuocHop } from '../../api/cuocHopApi.js'
import { layPhongHop, thamGiaCuocHop } from '../../api/thanhVienCuocHopApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import TaoBieuQuyet from '../pheduyet/TaoBieuQuyet.jsx'
import DiemDanhThuKy from '../thuky/DiemDanhThuKy.jsx'
import BieuQuyetThamDu from './BieuQuyetThamDu.jsx'

const LOP_NUT = 'rounded-lg bg-emerald-800 px-4 py-2.5 text-sm font-semibold text-white hover:bg-emerald-900 disabled:opacity-60'
const LOP_KHUNG = 'rounded-2xl border border-slate-200 bg-white p-4 sm:p-6'
const TEN_HINH_THUC = { TRUC_TIEP: 'Trực tiếp', TRUC_TUYEN: 'Trực tuyến' }

export default function PhongCuocHop({ cuocHopId, nguoiDung, onQuayLai, onDaKetThuc }) {
  const { token } = nguoiDung
  const phienDongBo = useDongBoDuLieu()
  const [hop, setHop] = useState(null)
  const [loiTai, setLoiTai] = useState('')
  const [loiLuu, setLoiLuu] = useState('')
  const [thaoTac, setThaoTac] = useState(null)
  const [xacNhanKetThuc, setXacNhanKetThuc] = useState(false)
  const [muc, setMuc] = useState('')
  const [lanTai, setLanTai] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    layPhongHop(cuocHopId, token, controller.signal)
      .then((duLieu) => {
        if (controller.signal.aborted) return
        if (duLieu.trangThai === 'DA_KET_THUC') {
          onDaKetThuc(duLieu)
          return
        }
        setHop(duLieu)
        setLoiTai('')
      })
      .catch((loi) => {
        if (controller.signal.aborted) return
        if ([403, 404, 409].includes(loi.status)) setHop(null)
        setLoiTai(loi.message)
      })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo, lanTai, onDaKetThuc])

  async function xuLy(loai) {
    setLoiLuu('')
    setThaoTac(loai)
    try {
      if (loai === 'tham-gia') {
        const ketQua = await thamGiaCuocHop(cuocHopId, token)
        setHop((hienTai) => hienTai && {
          ...hienTai, thoiGianTuDiemDanh: ketQua.thoiGianDiemDanh,
        })
      } else {
        await ketThucCuocHop(cuocHopId, token)
      }
      setLanTai((lan) => lan + 1)
    } catch (loi) {
      setLoiLuu(loi.message)
      if (loi.status === 409) setLanTai((lan) => lan + 1)
    } finally {
      setThaoTac(null)
    }
  }

  const dangHop = hop?.trangThai === 'DANG_DIEN_RA'
  const laThuKy = hop?.laThuKyToChuc
  const laChuTri = hop?.laChuTri
  const laNguoiDieuHanh = laThuKy || laChuTri
  const daRoi = Boolean(hop?.thoiGianRoiCuocHop)
  const daTuDiemDanh = Boolean(hop?.thoiGianTuDiemDanh)
  const choThamGia = dangHop && !daRoi && !daTuDiemDanh
  const coMat = hop?.trangThaiThamDu === 'CO_MAT' && !daRoi
  const thongDiep = laThuKy ? 'Theo dõi lượt tự điểm danh và đối chiếu sự có mặt thực tế.'
    : laChuTri ? 'Điều hành nội dung và mở phiên biểu quyết khi cần.'
      : choThamGia ? 'Bấm Tham gia để tự điểm danh.'
        : 'Theo dõi nội dung cuộc họp và biểu quyết khi được mời.'

  const cacMuc = [
    ['noi-dung', 'Nội dung'],
    ...(laNguoiDieuHanh ? [['diem-danh', 'Danh sách và điểm danh']] : []),
    ...(laChuTri || coMat ? [['bieu-quyet', 'Biểu quyết']] : []),
  ]
  const mucHienTai = cacMuc.some(([id]) => id === muc) ? muc : laThuKy && dangHop ? 'diem-danh' : 'noi-dung'

  return (
    <div className="min-h-screen bg-slate-100 text-slate-950">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-3 px-4 py-4 sm:px-8">
          <button type="button" onClick={onQuayLai} className="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-3 py-2 text-sm font-semibold hover:bg-slate-100">
            <ArrowLeft size={16} aria-hidden="true" /> Về Tổng quan
          </button>
          <p className="text-sm font-medium text-slate-700">{nguoiDung.hoTen}</p>
        </div>
      </header>
      <main className="mx-auto max-w-7xl space-y-5 px-4 py-5 sm:px-8 sm:py-8">
        {loiTai && <p role="alert" className="rounded-xl bg-red-50 p-4 text-sm text-red-800">{loiTai}</p>}
        {!hop && !loiTai && <p role="status" className={LOP_KHUNG}>Đang mở phòng họp…</p>}
        {hop && (
          <>
            <section className="overflow-hidden rounded-2xl bg-[#0f2744] p-5 text-white sm:p-8">
              <div className="flex flex-wrap items-center gap-x-4 gap-y-2 text-sm text-slate-200">
                <span className="inline-flex items-center gap-2 rounded-full bg-white/10 px-3 py-1 font-semibold">
                  <Radio size={15} aria-hidden="true" /> {dangHop ? 'Đang diễn ra' : 'Đã kết thúc'}
                </span>
                <span>{laChuTri ? 'Bạn là chủ trì' : laThuKy ? 'Bạn là thư ký tổ chức' : 'Bạn là thành viên'}</span>
              </div>
              <h1 className="mt-4 break-words text-2xl font-bold tracking-tight sm:text-4xl">{hop.tieuDe}</h1>
              <p className="mt-2 flex flex-wrap gap-x-4 gap-y-1 text-sm text-slate-200">
                {hop.tenChuTri && <span>Chủ trì · {hop.tenChuTri}</span>}
                {hop.thoiGianBatDau && <span>Bắt đầu · {dinhDangNgayGio(hop.thoiGianBatDau)}</span>}
                {TEN_HINH_THUC[hop.loaiHinh] && <span>{TEN_HINH_THUC[hop.loaiHinh]}</span>}
                {hop.tenPhongHop && <span>{hop.tenPhongHop}</span>}
              </p>
            </section>

            <section className={LOP_KHUNG}>
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div aria-live="polite">
                  <h2 className="font-bold">Việc cần làm</h2>
                  <p className="mt-1 text-sm text-slate-700">{thongDiep}</p>
                  {choThamGia && hop.trangThaiMoi === 'CHO_PHAN_HOI' && <p className="mt-1 text-sm text-slate-600">Thao tác này đồng thời xác nhận lời mời.</p>}
                  {daTuDiemDanh && <p className="mt-1 text-sm text-emerald-800">Đã tự điểm danh lúc {dinhDangNgayGio(hop.thoiGianTuDiemDanh)}</p>}
                  {hop.trangThaiThamDu === 'VANG_MAT' && <p className="mt-1 text-sm text-rose-800">Thư ký ghi nhận bạn vắng mặt. Liên hệ thư ký nếu cần đối chiếu.</p>}
                  {daRoi && <p className="mt-1 text-sm text-amber-900">Đã ghi nhận rời họp lúc {dinhDangNgayGio(hop.thoiGianRoiCuocHop)}</p>}
                </div>
                <div className="flex flex-wrap gap-3">
                  {choThamGia && <button type="button" className={LOP_NUT} disabled={thaoTac !== null} onClick={() => xuLy('tham-gia')}>
                    {thaoTac === 'tham-gia' ? 'Đang điểm danh…' : 'Tham gia'}
                  </button>}
                  {dangHop && laNguoiDieuHanh && !xacNhanKetThuc && (
                    <button type="button" onClick={() => setXacNhanKetThuc(true)}
                      className="rounded-lg border border-rose-200 px-4 py-2.5 text-sm font-semibold text-rose-800 hover:bg-rose-50">
                      Kết thúc cuộc họp
                    </button>
                  )}
                </div>
              </div>
              {loiLuu && <p role="alert" className="mt-3 text-sm text-red-800">{loiLuu}</p>}
              {dangHop && laNguoiDieuHanh && xacNhanKetThuc && (
                <div className="mt-4 flex flex-wrap items-center gap-3 rounded-xl border border-rose-200 bg-rose-50 p-3">
                  <p className="grow text-sm text-rose-900">Xác nhận kết thúc để đóng điểm danh và chuyển sang xử lý văn bản sau họp.</p>
                  <button type="button" disabled={thaoTac !== null} onClick={() => setXacNhanKetThuc(false)} className="rounded-lg px-3 py-2 text-sm font-semibold">Quay lại</button>
                  <button type="button" disabled={thaoTac !== null} onClick={() => xuLy('ket-thuc')}
                    className="rounded-lg bg-rose-800 px-3 py-2 text-sm font-semibold text-white disabled:opacity-60">
                    {thaoTac === 'ket-thuc' ? 'Đang kết thúc…' : 'Xác nhận kết thúc'}
                  </button>
                </div>
              )}
            </section>

            <div className="grid min-w-0 gap-4 lg:grid-cols-[220px_minmax(0,1fr)]">
              <nav aria-label="Công cụ cuộc họp" className="flex flex-wrap items-start gap-2 self-start rounded-xl bg-[#eff4ff] p-3 lg:flex-col lg:items-stretch">
                {cacMuc.map(([id, ten]) => (
                  <button type="button" key={id} aria-pressed={id === mucHienTai} onClick={() => setMuc(id)}
                    className={id === mucHienTai
                      ? 'rounded-lg bg-[#0f2744] px-4 py-2.5 text-left text-sm font-semibold text-white'
                      : 'rounded-lg bg-white px-4 py-2.5 text-left text-sm font-semibold hover:bg-blue-50'}>
                    {ten}
                  </button>
                ))}
              </nav>

              <section className={LOP_KHUNG}>
                {mucHienTai === 'noi-dung' && (
                  <div className="grid gap-5 sm:grid-cols-2">
                    <div><h2 className="font-bold">Mục tiêu</h2><p className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">{hop.mucTieu || 'Chưa có mục tiêu.'}</p></div>
                    <div><h2 className="font-bold">Nội dung dự kiến</h2><p className="mt-2 whitespace-pre-wrap break-words text-sm text-slate-700">{hop.noiDungDuKien || 'Chưa có nội dung dự kiến.'}</p></div>
                  </div>
                )}
                {mucHienTai === 'diem-danh' && <DiemDanhThuKy cuocHopId={cuocHopId} token={token} readOnly={!laThuKy || !dangHop} />}
                {mucHienTai === 'bieu-quyet' && (laChuTri
                  ? <TaoBieuQuyet nguoiDung={nguoiDung} cuocHopId={cuocHopId} />
                  : <BieuQuyetThamDu token={token} cuocHopId={cuocHopId} />)}
              </section>
            </div>
          </>
        )}
      </main>
    </div>
  )
}
