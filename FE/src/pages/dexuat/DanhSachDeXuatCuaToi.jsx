import { useState } from 'react'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import FileText from 'lucide-react/dist/esm/icons/file-text.mjs'
import Send from 'lucide-react/dist/esm/icons/send.mjs'
import { LocHoSo, NhanHoSo, NutTaoHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { khopTuKhoa, LOP_CHU_PHU, LOP_NUT_PHU, LOP_THE } from '../../components/giaoDienPhongHop.js'
import { layTrangThaiHienThiCuocHop } from '../../api/cuocHopApi.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'
import { TEN_PHAM_VI, TEN_TRANG_THAI } from '../thuky/hienThiDeXuat.js'

const CAC_LOC = [
  ['tat-ca', 'Tất cả'],
  ['NHAP', 'Bản nháp'],
  ['dang-xu-ly', 'Đang xử lý'],
  ['da-duyet', 'Đã duyệt'],
  ['TRA_LAI', 'Bị trả lại'],
]

const DANG_XU_LY = new Set(['CHO_TIEP_NHAN', 'DANG_CHUAN_BI', 'CHO_DUYET', 'CHO_GUI_CAP_TREN'])
const DA_DUYET = new Set(['DA_DUYET', 'DANG_DIEN_RA', 'DA_KET_THUC'])

const MAU_TRANG_THAI = {
  CHO_TIEP_NHAN: 'vang',
  DANG_CHUAN_BI: 'xanh',
  CHO_DUYET: 'xanh',
  CHO_GUI_CAP_TREN: 'xanh',
  TRA_LAI: 'vang',
  DA_DUYET: 'luc',
  DANG_DIEN_RA: 'luc',
  DA_KET_THUC: 'luc',
  TU_CHOI: 'do',
  DA_HUY: 'do',
}

function khopTrangThai(boLoc, trangThai) {
  if (boLoc === 'tat-ca') return true
  if (boLoc === 'dang-xu-ly') return DANG_XU_LY.has(trangThai)
  if (boLoc === 'da-duyet') return DA_DUYET.has(trangThai)
  return boLoc === trangThai
}

export default function DanhSachDeXuatCuaToi({
  danhSach, loiTai, loi, thongBao, dangGui, coThuKy, onTao, onTaiLai, onXemCuocHop, onGui, nutTao,
}) {
  const [tuKhoa, setTuKhoa] = useState('')
  const [boLoc, setBoLoc] = useState('tat-ca')
  const cacMucHienCo = danhSach?.map((muc) => ({ muc, trangThai: layTrangThaiHienThiCuocHop(muc) }))
  const cacMuc = cacMucHienCo?.filter(({ muc, trangThai }) => khopTrangThai(boLoc, trangThai)
    && khopTuKhoa([muc.tieuDe, muc.tenPhongBanChuTri].filter(Boolean).join(' '), tuKhoa))

  return (
    <section className="space-y-4" aria-labelledby="danh-sach-de-xuat">
      <header className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-slate-200 bg-white p-4 sm:p-6">
        <div className="flex min-w-0 items-start gap-3">
          <span className="grid size-11 shrink-0 place-items-center rounded-xl bg-blue-50 text-blue-900">
            <FileText size={21} aria-hidden="true" />
          </span>
          <div className="min-w-0">
            <h1 id="danh-sach-de-xuat" className="text-xl font-bold text-slate-900">Đề xuất cuộc họp</h1>
            <p className="mt-1 text-sm text-slate-600">
              {danhSach ? `${danhSach.length} đề xuất của bạn` : 'Theo dõi đề xuất đã tạo'}
            </p>
          </div>
        </div>
        <NutTaoHoSo ref={nutTao} onClick={onTao} disabled={dangGui}>Tạo đề xuất</NutTaoHoSo>
      </header>

      <div className={LOP_THE + ' space-y-4'}>
        {thongBao && <p role="status" className="rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-900">{thongBao}</p>}
        {(loiTai || loi) && <p role="alert" className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-900">{loiTai || loi}</p>}
        {loiTai && <button type="button" onClick={onTaiLai} className={LOP_NUT_PHU}>Tải lại danh sách</button>}
        {!loiTai && danhSach == null && <p role="status" className={LOP_CHU_PHU}>Đang tải đề xuất…</p>}
        {cacMucHienCo?.length > 0 && <LocHoSo tuKhoa={tuKhoa} onTuKhoa={setTuKhoa} nhanTim="Tìm tên cuộc họp hoặc phòng ban"
          boLoc={boLoc} onBoLoc={setBoLoc} cacMuc={CAC_LOC} />}

        {cacMuc?.length === 0 && !loiTai && (
          <p className="rounded-xl border border-dashed border-slate-300 p-5 text-sm text-slate-700">
            {cacMucHienCo.length ? 'Không tìm thấy đề xuất phù hợp.' : 'Bạn chưa có đề xuất cuộc họp.'}
          </p>
        )}
        {cacMuc?.length > 0 && (
          <ul className="divide-y divide-slate-200 overflow-hidden rounded-xl border border-slate-200">
            {cacMuc.map(({ muc, trangThai }) => (
              <li key={muc.cuocHopId} className="flex flex-wrap items-center gap-x-5 gap-y-3 p-4">
                <div className="min-w-0 flex-1 basis-64 space-y-2">
                  <div className="flex flex-wrap items-center gap-2">
                    <h2 className="min-w-0 wrap-anywhere text-sm font-bold text-slate-900">{muc.tieuDe}</h2>
                    {TEN_TRANG_THAI[trangThai] && <NhanHoSo mau={MAU_TRANG_THAI[trangThai]}>{TEN_TRANG_THAI[trangThai]}</NhanHoSo>}
                  </div>
                  <p className="text-sm text-slate-600">
                    {[TEN_PHAM_VI[muc.phamViCuocHop], muc.tenPhongBanChuTri,
                      muc.thoiLuongDuKienPhut && `${muc.thoiLuongDuKienPhut} phút dự kiến`].filter(Boolean).join(' · ')}
                  </p>
                  {muc.ngayTao && <p className="inline-flex items-center gap-1.5 text-xs text-slate-600">
                    <CalendarDays size={14} aria-hidden="true" /> Tạo lúc {dinhDangNgayGio(muc.ngayTao)}
                  </p>}
                  {muc.thoiGianChuyenCap && <p className="text-xs text-slate-600">
                    Đã chuyển cấp · {dinhDangNgayGio(muc.thoiGianChuyenCap)}
                  </p>}
                </div>
                <div className="flex max-w-full flex-wrap items-center gap-2">
                  {onXemCuocHop && <button type="button" className={LOP_NUT_PHU}
                    onClick={() => onXemCuocHop(muc.cuocHopId)}>Xem chi tiết</button>}
                  {muc.trangThai === 'NHAP' && (
                    <button type="button" className={LOP_NUT_PHU} disabled={dangGui || !coThuKy}
                      onClick={() => onGui(muc.cuocHopId)}><Send size={15} aria-hidden="true" /> Gửi thư ký</button>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  )
}
