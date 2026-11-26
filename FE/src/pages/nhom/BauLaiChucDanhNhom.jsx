import { useEffect, useState } from 'react'
import ArrowLeft from 'lucide-react/dist/esm/icons/arrow-left.mjs'
import CalendarDays from 'lucide-react/dist/esm/icons/calendar-days.mjs'
import ChevronRight from 'lucide-react/dist/esm/icons/chevron-right.mjs'
import Vote from 'lucide-react/dist/esm/icons/vote.mjs'
import X from 'lucide-react/dist/esm/icons/x.mjs'
import { HopThoaiHoSo } from '../../components/GiaoDienPhongHop.jsx'
import { LOP_CHU_PHU, LOP_NUT_CHINH, LOP_NUT_PHU, LOP_THE, LOP_TRUONG } from '../../components/giaoDienPhongHop.js'
import { docPhanHoiJson, goiApi } from '../../api/apiClient.js'
import { goiNhomLienPhong } from '../../api/nhomLienPhongApi.js'
import { useDongBoDuLieu } from '../../services/useDongBoDuLieu.js'
import { dinhDangNgayGio } from '../../utils/thoiGianCuocHop.js'

const CHUC_DANH = { TO_TRUONG: 'Tổ trưởng', THU_KY: 'Thư ký nhóm' }
const TRANG_THAI = {
  CHO_TIEP_NHAN: 'Chờ thư ký tiếp nhận', LAP_KE_HOACH: 'Thư ký lập kế hoạch',
  CHO_DUYET: 'Chờ Tổ trưởng duyệt', DA_DUYET: 'Đã duyệt kế hoạch', HOAN_TAT: 'Đã bàn giao chức danh',
  DA_HUY: 'Đã hủy', NHAP: 'Chưa mở', DANG_MO: 'Đang nhận phiếu', BAU_LAI: 'Cần bầu lại',
  CHO_XAC_NHAN: 'Chờ xác nhận kết quả', DA_XAC_NHAN: 'Đã xác nhận',
}
const TRUONG = LOP_TRUONG + ' mt-1 block w-full'
const KET_THUC = ['HOAN_TAT', 'DA_HUY']
const MAU = {
  DANG_MO: 'bg-blue-50 text-blue-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh',
  CHO_DUYET: 'bg-amber-50 text-amber-900 phong-toi:bg-phong-nhan-vang phong-toi:text-phong-vang',
  CHO_XAC_NHAN: 'bg-amber-50 text-amber-900 phong-toi:bg-phong-nhan-vang phong-toi:text-phong-vang',
  BAU_LAI: 'bg-amber-50 text-amber-900 phong-toi:bg-phong-nhan-vang phong-toi:text-phong-vang',
  HOAN_TAT: 'bg-emerald-50 text-emerald-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh',
  DA_XAC_NHAN: 'bg-emerald-50 text-emerald-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh',
}

function NhanTrangThai({ trangThai }) {
  const mau = MAU[trangThai] || 'bg-slate-100 text-slate-700 phong-toi:bg-phong-mat-noi phong-toi:text-phong-chu-phu'
  return <span className={mau + ' inline-block rounded-full px-2.5 py-1 text-xs font-semibold'}>
    {TRANG_THAI[trangThai] || trangThai}
  </span>
}

function tenHoSo(hs) {
  return [hs.bauToTruong && 'Tổ trưởng', hs.bauThuKy && 'Thư ký nhóm'].filter(Boolean).join(' và ')
}

function trangThaiHienThi(hs) {
  if (hs.trangThai !== 'DA_DUYET') return hs.trangThai
  const cacVong = Object.keys(CHUC_DANH).map((cd) => hs.cacVong.find((v) => v.chucDanh === cd))
  return ['DANG_MO', 'CHO_XAC_NHAN', 'NHAP', 'BAU_LAI'].find(
    (trangThai) => cacVong.some((v) => v?.trangThai === trangThai),
  ) || hs.trangThai
}

function NhomChon({ nhan, name, danhSach, macDinh = [], radio = false }) {
  return <fieldset className="space-y-2">
    <legend className="font-medium">{nhan}</legend>
    {danhSach.length === 0 && <p className="text-slate-600">Chưa có người đủ điều kiện.</p>}
    <div className="grid gap-2 sm:grid-cols-2">
      {danhSach.map((nguoi) => (
        <label key={nguoi.nguoiDungId} className="flex cursor-pointer items-center gap-2 rounded-lg border border-slate-200 px-3 py-2 has-checked:border-blue-400 has-checked:bg-blue-50 phong-toi:border-phong-vien phong-toi:has-checked:bg-phong-nhan-xanh">
          <input type={radio ? 'radio' : 'checkbox'} name={name} value={nguoi.nguoiDungId}
            required={radio} defaultChecked={macDinh.includes(nguoi.nguoiDungId)} />
          {nguoi.hoTen}
        </label>
      ))}
    </div>
  </fieldset>
}

function LyDo({ nhan = 'Lý do' }) {
  return <label className="block text-sm">{nhan}
    <textarea name="lyDo" required maxLength={500} rows={2} className={TRUONG} />
  </label>
}

function VongBauLai({ vong, hs, trang, goi, dangGui, cuocHopId, cuTriCoMat = [] }) {
  const trongHop = cuocHopId != null
  const dangHop = hs.trangThaiCuocHop === 'DANG_DIEN_RA'
  const luotCu = hs.cuocHopId == null
  const daKetThucHop = hs.trangThaiCuocHop === 'DA_KET_THUC'
  const chucDanh = CHUC_DANH[vong.chucDanh]
  const trangThai = vong.trangThai
  const coTheChot = vong.chucDanh === 'TO_TRUONG'
    ? trang.lanhDaoPhuTrach
    : trang.toTruong && (!hs.bauToTruong || hs.cacVong.some((v) => v.chucDanh === 'TO_TRUONG' && v.trangThai === 'DA_XAC_NHAN'))
  const dangXuLy = trang.dangHoatDong && hs.trangThai === 'DA_DUYET'
  const daKiemPhieu = vong.ungVien.some((nguoi) => nguoi.soPhieu != null)
  const nguoiTrungCu = vong.ungVien.find((nguoi) => nguoi.nguoiDungId === vong.nguoiTrungCuId)
  const xuLyVong = (thaoTac, duLieu = {}) => goi(thaoTac, { ...duLieu, vongId: vong.vongId, cuocHopId })

  const soPhieuCan = Math.floor(vong.soCuTri / 2) + 1
  const nhieuPhieuNhat = Math.max(0, ...vong.ungVien.map((nguoi) => nguoi.soPhieu ?? 0))

  return <section className={LOP_THE + ' space-y-3'}>
    <div className="flex flex-wrap items-center justify-between gap-2">
      <h4 className="font-semibold">Bầu {chucDanh}</h4>
      <NhanTrangThai trangThai={trangThai} />
    </div>
    {vong.soCuTri > 0 && <p className={LOP_CHU_PHU}>
      {vong.soDaBoPhieu}/{vong.soCuTri} cử tri đã bỏ phiếu · Cần {soPhieuCan} phiếu để trúng cử
    </p>}
    {daKiemPhieu && <ul className="divide-y divide-slate-200 rounded-lg bg-slate-50 px-3 phong-toi:divide-phong-vien phong-toi:bg-phong-mat-noi">
      {[...vong.ungVien].sort((a, b) => b.soPhieu - a.soPhieu).map((nguoi) => (
        <li key={nguoi.nguoiDungId} className="flex items-center justify-between gap-3 py-2">
          <span>{nguoi.hoTen}</span><strong className="shrink-0 tabular-nums">{nguoi.soPhieu} phiếu</strong>
        </li>
      ))}
    </ul>}
    {trangThai === 'BAU_LAI' && (
      <p className={LOP_CHU_PHU}>
        {vong.soCuTri > 0 && nhieuPhieuNhat < soPhieuCan
          ? `Số phiếu cao nhất là ${nhieuPhieuNhat}/${vong.soCuTri}. Thư ký nhóm có thể tạo lượt mới.`
          : 'Thư ký nhóm có thể tạo lượt mới cho chức danh này.'}
      </p>
    )}
    {nguoiTrungCu && trangThai !== 'BAU_LAI' && <p>
      Đạt số phiếu · <strong>{nguoiTrungCu.hoTen}</strong>
      {vong.tenNguoiXacNhan && ` · ${vong.tenNguoiXacNhan} đã xác nhận vào ${dinhDangNgayGio(vong.ngayXacNhan)}`}
    </p>}

    {dangXuLy && trang.thuKy && trongHop && dangHop && trangThai === 'NHAP' && (
      <div className="space-y-3">
        <p className={LOP_CHU_PHU}>
          {cuTriCoMat.length > 0
            ? 'Danh sách cử tri lấy từ thành viên nhóm đang có mặt và được chốt khi mở lượt.'
            : 'Chưa có thành viên nhóm điểm danh để mở lượt.'}
        </p>
        <div className="flex flex-wrap gap-2">
          <button type="button" className={LOP_NUT_CHINH} disabled={dangGui || cuTriCoMat.length === 0}
            onClick={() => xuLyVong('mo-vong')}>Mở lượt bầu</button>
          <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => xuLyVong('bo-vong-nhap')}>Bỏ lượt nháp</button>
        </div>
      </div>
    )}
    {dangXuLy && (trongHop || luotCu) && trang.thanhVien && vong.duocBoPhieu && !vong.daBoPhieu && (
      <form className="space-y-2" onSubmit={(event) => {
        event.preventDefault()
        xuLyVong('bo-phieu', { ungVienId: Number(new FormData(event.currentTarget).get(`ungVien-${vong.vongId}`)) })
      }}>
        <NhomChon nhan="Chọn một ứng viên" name={`ungVien-${vong.vongId}`} danhSach={vong.ungVien} radio />
        <button className={LOP_NUT_CHINH} disabled={dangGui}>Gửi phiếu</button>
      </form>
    )}
    {vong.daBoPhieu && <p role="status">Bạn đã gửi phiếu.</p>}
    {dangXuLy && trangThai === 'DANG_MO' && (
      <div className="space-y-2">
        <p className={LOP_CHU_PHU}>
          {daKetThucHop ? 'Cuộc họp đã kết thúc. Thư ký nhóm cần đóng lượt ngay tại hồ sơ này để kiểm phiếu.'
            : 'Kết quả hiển thị sau khi thư ký nhóm đóng lượt và kiểm phiếu.'}
        </p>
        {trang.thuKy && (trongHop || luotCu || daKetThucHop) && (
          <button type="button" className={LOP_NUT_CHINH} disabled={dangGui}
            onClick={() => xuLyVong('dong-vong')}>Đóng lượt và kiểm phiếu</button>
        )}
      </div>
    )}
    {dangXuLy && !trongHop && trangThai === 'CHO_XAC_NHAN' && (
      coTheChot ? <div className="flex flex-wrap gap-2">
        <button type="button" className={LOP_NUT_CHINH} disabled={dangGui} onClick={() => xuLyVong('xac-nhan')}>Xác nhận kết quả</button>
        <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => xuLyVong('bau-lai')}>Yêu cầu bầu lại</button>
      </div> : <p>{vong.chucDanh === 'TO_TRUONG' ? 'Lãnh đạo phụ trách nhóm xác nhận kết quả.'
        : 'Tổ trưởng xác nhận kết quả bầu Thư ký sau khi kết quả bầu Tổ trưởng đã được xác nhận.'}</p>
    )}
  </section>
}

function CuocHopBauLai({ hs, thaoTac, coTheGan, thanhVien, cuocHopTrongNhom, dangGui }) {
  const daGan = hs.cuocHopId != null
  const cuocHop = cuocHopTrongNhom.find((hop) => hop.cuocHopId === hs.cuocHopId)
  const danhSach = cuocHopTrongNhom.filter((hop) => ['DA_DUYET', 'DANG_DIEN_RA'].includes(hop.trangThai))
  const dangHop = hs.trangThaiCuocHop === 'DANG_DIEN_RA'
  const conLuotDangMo = hs.cacVong.some((vong) => vong.trangThai === 'DANG_MO')
  const conBuocBau = hs.trangThai === 'DA_DUYET' && (
    [hs.bauToTruong && 'TO_TRUONG', hs.bauThuKy && 'THU_KY'].filter(Boolean).some((chucDanh) =>
      !['CHO_XAC_NHAN', 'DA_XAC_NHAN'].includes(hs.cacVong.find((v) => v.chucDanh === chucDanh)?.trangThai))
  )

  return <section className="space-y-3 rounded-xl border border-blue-200 bg-blue-50/70 p-4 phong-toi:border-phong-vien phong-toi:bg-phong-mat-noi">
    <div className="flex items-start gap-3">
      <span className="rounded-lg bg-white p-2 text-blue-800 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">
        <CalendarDays aria-hidden="true" size={18} />
      </span>
      <div className="min-w-0">
        <h4 className="font-semibold">Cuộc họp để bầu</h4>
        <p className={LOP_CHU_PHU + ' break-words'}>
          {daGan ? cuocHop?.tieuDe ?? 'Đã liên kết cuộc họp của nhóm' : 'Chờ thư ký chọn cuộc họp của nhóm.'}
        </p>
      </div>
    </div>
    {daGan && conBuocBau && (
      dangHop && thanhVien ? <a className={LOP_NUT_CHINH} href={`#cuoc-hop/${hs.cuocHopId}/bau-chon`}>
        Mở lượt bầu trong cuộc họp <ChevronRight aria-hidden="true" size={16} />
      </a> : <p className={LOP_CHU_PHU}>
        {dangHop ? 'Thành viên nhóm bỏ phiếu trong cuộc họp.'
          : hs.trangThaiCuocHop === 'DA_DUYET' ? 'Có thể mở lượt bầu khi cuộc họp bắt đầu.'
          : conLuotDangMo ? 'Cuộc họp đã kết thúc và lượt bầu đang chờ kiểm phiếu.'
            : 'Thư ký có thể chọn cuộc họp tiếp theo nếu cần tổ chức lượt mới.'}
      </p>
    )}
    {coTheGan && conBuocBau && danhSach.length > 0 && (!daGan || !dangHop) && (
      <form key={hs.cuocHopId} className="flex flex-wrap items-end gap-2" onSubmit={(event) => {
        event.preventDefault()
        const cuocHopId = Number(new FormData(event.currentTarget).get('cuocHopId'))
        if (cuocHopId) thaoTac('gan-cuoc-hop', { cuocHopId })
      }}>
        <label className="min-w-0 grow">
          {daGan ? 'Đổi cuộc họp' : 'Chọn cuộc họp'}
          <select className={TRUONG} required name="cuocHopId" defaultValue={cuocHop?.cuocHopId ?? ''}>
            <option value="" disabled>Chọn từ danh sách</option>
            {danhSach.map((hop) => <option key={hop.cuocHopId} value={hop.cuocHopId}>{hop.tieuDe}</option>)}
          </select>
        </label>
        <button className={LOP_NUT_PHU} disabled={dangGui}>Lưu cuộc họp</button>
      </form>
    )}
    {coTheGan && conBuocBau && danhSach.length === 0 && <p className={LOP_CHU_PHU}>
      Trong mục Đề xuất họp đã gửi, nhóm cần có một cuộc họp được duyệt để tổ chức bầu.
    </p>}
  </section>
}

function HoSoBauLai({ hs, trang, nguoiDungId, goi, dangGui, cuocHopTrongNhom }) {
  const thaoTac = (ten, duLieu) => goi(`/${hs.yeuCauId}/${ten}`, duLieu)
  const trangThai = hs.trangThai
  const conXuLy = trang.dangHoatDong && !['HOAN_TAT', 'DA_HUY'].includes(trangThai)
  const ds = trang.thanhVienHoatDong
  const ten = (id) => ds.find((nguoi) => nguoi.nguoiDungId === id)?.hoTen ?? `#${id}`

  const vongMoi = Object.keys(CHUC_DANH).map((cd) => hs.cacVong.find((v) => v.chucDanh === cd)).filter(Boolean)
  const vongCu = hs.cacVong.filter((v) => !vongMoi.includes(v))
  const buoc = vongMoi.some((v) => v.trangThai === 'CHO_XAC_NHAN') ? 2 : trangThai === 'DA_DUYET' ? 1 : 0

  return <article className="space-y-4">
    {conXuLy && <ol aria-label="Tiến độ hồ sơ bầu lại" className="grid grid-cols-3 gap-2">
      {['Duyệt kế hoạch', 'Bầu trong cuộc họp', 'Xác nhận kết quả'].map((tenBuoc, i) => (
        <li key={tenBuoc} aria-current={i === buoc ? 'step' : undefined}
          className={'rounded-lg border p-2.5 text-xs font-semibold sm:text-sm ' + (i === buoc
            ? 'border-blue-200 bg-blue-50 text-blue-900 phong-toi:border-phong-vien-xanh phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh'
            : 'border-slate-200 text-slate-600 phong-toi:border-phong-vien phong-toi:text-phong-chu-phu')}>
          <span className="block sm:inline">{i + 1}. </span>{tenBuoc}
        </li>
      ))}
    </ol>}
    <div className={LOP_THE + ' space-y-3 bg-slate-50 phong-toi:bg-phong-mat-noi'}>
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h3 className="font-semibold">{tenHoSo(hs)}</h3>
        <NhanTrangThai trangThai={trangThaiHienThi(hs)} />
      </div>
      <dl className="grid gap-2 text-sm sm:grid-cols-2">
        <div><dt className={LOP_CHU_PHU}>Người đề xuất</dt><dd>{hs.tenNguoiDeXuat} · {dinhDangNgayGio(hs.ngayTao)}</dd></div>
        {hs.gioDuKien && <div><dt className={LOP_CHU_PHU}>Thời gian dự kiến</dt><dd>{dinhDangNgayGio(hs.gioDuKien)}</dd></div>}
        {hs.tenNguoiDuyet && <div><dt className={LOP_CHU_PHU}>Người duyệt kế hoạch</dt>
          <dd>{hs.tenNguoiDuyet} · {dinhDangNgayGio(hs.ngayDuyet)}</dd></div>}
        <div className="sm:col-span-2"><dt className={LOP_CHU_PHU}>Lý do bầu lại</dt>
          <dd className="whitespace-pre-wrap break-words">{hs.lyDo}</dd></div>
        {hs.phanHoi && <div className="sm:col-span-2"><dt className={LOP_CHU_PHU}>Phản hồi</dt><dd>{hs.phanHoi}</dd></div>}
      </dl>
    </div>

    {conXuLy && trang.thuKy && trangThai === 'CHO_TIEP_NHAN' && (
      <button type="button" className={LOP_NUT_CHINH} disabled={dangGui} onClick={() => thaoTac('tiep-nhan')}>Tiếp nhận yêu cầu</button>
    )}
    {conXuLy && trang.thuKy && trangThai === 'LAP_KE_HOACH' && (
      <form className="space-y-3" onSubmit={(event) => {
        event.preventDefault()
        const form = new FormData(event.currentTarget)
        thaoTac('trinh', {
          gioDuKien: form.get('gioDuKien'),
          ungVienToTruong: form.getAll('toTruong').map(Number),
          ungVienThuKy: form.getAll('thuKy').map(Number),
        })
      }}>
        <label className="block">Thời gian dự kiến
          <input className={TRUONG} type="datetime-local" name="gioDuKien" required
            defaultValue={hs.gioDuKien?.slice(0, 16)} />
        </label>
        {hs.bauToTruong && <NhomChon nhan="Ứng viên Tổ trưởng" name="toTruong"
          danhSach={ds.filter((v) => v.duocLamToTruong)} macDinh={hs.ungVienToTruong} />}
        {hs.bauThuKy && <NhomChon nhan="Ứng viên Thư ký nhóm" name="thuKy"
          danhSach={ds.filter((v) => v.duocLamThuKy)} macDinh={hs.ungVienThuKy} />}
        <button className={LOP_NUT_CHINH} disabled={dangGui}>Trình Tổ trưởng</button>
      </form>
    )}
    {trangThai !== 'LAP_KE_HOACH' && (
      <dl className="space-y-1">
        {hs.ungVienToTruong.length > 0 && <div><dt className="font-medium">Ứng viên Tổ trưởng</dt><dd>{hs.ungVienToTruong.map(ten).join(', ')}</dd></div>}
        {hs.ungVienThuKy.length > 0 && <div><dt className="font-medium">Ứng viên Thư ký nhóm</dt><dd>{hs.ungVienThuKy.map(ten).join(', ')}</dd></div>}
      </dl>
    )}
    {conXuLy && trang.toTruong && trangThai === 'CHO_DUYET' && (
      <div className="space-y-2">
        <button type="button" className={LOP_NUT_CHINH} disabled={dangGui} onClick={() => thaoTac('duyet')}>Duyệt kế hoạch</button>
        <details>
          <summary className="cursor-pointer">Trả lại kế hoạch</summary>
          <form className="mt-2 space-y-2" onSubmit={(event) => {
            event.preventDefault()
            thaoTac('tra-lai', { lyDo: new FormData(event.currentTarget).get('lyDo') })
          }}>
            <LyDo nhan="Lý do trả lại" />
            <button className={LOP_NUT_PHU} disabled={dangGui}>Trả lại cho thư ký</button>
          </form>
        </details>
      </div>
    )}
    {(trangThai === 'DA_DUYET' || hs.cuocHopId != null) && <CuocHopBauLai hs={hs} thaoTac={thaoTac} thanhVien={trang.thanhVien}
      coTheGan={conXuLy && trang.thuKy && !hs.cacVong.some((v) => v.trangThai === 'DANG_MO')}
      cuocHopTrongNhom={cuocHopTrongNhom} dangGui={dangGui} />}
    {vongMoi.map((vong) => <VongBauLai key={vong.vongId} vong={vong} hs={hs} trang={trang}
      goi={thaoTac} dangGui={dangGui} />)}
    {vongCu.length > 0 && <details className="space-y-3">
      <summary className="cursor-pointer text-sm font-semibold">Lịch sử lượt bầu ({vongCu.length})</summary>
      {vongCu.map((vong) => <VongBauLai key={vong.vongId} vong={vong} hs={hs} trang={trang}
        goi={thaoTac} dangGui={dangGui} />)}
    </details>}
    {conXuLy && !hs.cacVong.some((v) => v.trangThai === 'DANG_MO')
      && (trang.toTruong || (trangThai === 'CHO_TIEP_NHAN' && hs.nguoiDeXuatId === nguoiDungId)) && (
      <details>
        <summary className="cursor-pointer">Hủy hồ sơ bầu lại</summary>
        <form className="mt-2 space-y-2" onSubmit={(event) => {
          event.preventDefault()
          thaoTac('huy', { lyDo: new FormData(event.currentTarget).get('lyDo') })
        }}>
          <LyDo nhan="Lý do hủy" />
          <button className={LOP_NUT_PHU} disabled={dangGui}>Xác nhận hủy</button>
        </form>
      </details>
    )}
  </article>
}

function DanhSachHoSo({ hoSo, chonHoSo }) {
  return <ul className="space-y-2">
    {hoSo.map((hs) => (
      <li key={hs.yeuCauId}>
        <button type="button" className={LOP_NUT_PHU + ' w-full justify-between text-left'}
          onClick={() => chonHoSo(hs.yeuCauId)}>
          <span className="min-w-0">
            <span className="block break-words">Bầu lại {tenHoSo(hs)}</span>
            <span className={LOP_CHU_PHU + ' block font-normal'}>{hs.tenNguoiDeXuat} · {dinhDangNgayGio(hs.ngayTao)}</span>
          </span>
          <span className="flex shrink-0 items-center gap-1.5">
            <NhanTrangThai trangThai={trangThaiHienThi(hs)} />
            <ChevronRight aria-hidden="true" size={16} />
          </span>
        </button>
      </li>
    ))}
  </ul>
}

export default function BauLaiChucDanhNhom({ nhomId, nguoiDungId, token, cuocHopTrongNhom = [] }) {
  const phienDongBo = useDongBoDuLieu()
  const [muc, setMuc] = useState(null)
  const [trang, setTrang] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [lanTai, setLanTai] = useState(0)
  const goc = `/${nhomId}/bau-lai`
  const mo = muc != null

  useEffect(() => {
    if (!mo) return
    const controller = new AbortController()
    goiNhomLienPhong(goc, token, { signal: controller.signal })
      .then((duLieu) => { if (!controller.signal.aborted) setTrang(duLieu) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [goc, mo, token, phienDongBo, lanTai])

  async function gui(duongDan, duLieu = {}) {
    if (dangGui) return false
    setDangGui(true)
    setLoi('')
    try {
      await goiNhomLienPhong(goc + duongDan, token, { method: 'POST', duLieu })
      setLanTai((lan) => lan + 1)
      return true
    } catch (error) {
      setLoi(error.message)
      return false
    } finally {
      setDangGui(false)
    }
  }

  const hoSo = trang?.hoSo ?? []
  const dangXuLy = hoSo.filter((hs) => !KET_THUC.includes(hs.trangThai))
  const lichSu = hoSo.filter((hs) => KET_THUC.includes(hs.trangThai))
  const daChon = hoSo.find((hs) => hs.yeuCauId === muc)
  const coQuyenXem = trang?.thanhVien || trang?.lanhDaoPhuTrach
  const coQuyenTao = trang?.thanhVien && trang.dangHoatDong && dangXuLy.length === 0
  const moiXacNhan = hoSo.flatMap((hs) => hs.cacVong)
    .filter((vong) => vong.trangThai === 'DA_XAC_NHAN' && vong.ngayXacNhan)
    .sort((a, b) => b.ngayXacNhan.localeCompare(a.ngayXacNhan) || b.vongId - a.vongId)[0]
  const tenNguoiMoi = moiXacNhan?.ungVien.find((v) => v.nguoiDungId === moiXacNhan.nguoiTrungCuId)?.hoTen

  function chuyenMuc(mucMoi) {
    setLoi('')
    setMuc(mucMoi)
  }

  return <section className={LOP_THE + ' space-y-3 text-sm'}>
    <div className="flex flex-wrap items-center justify-between gap-3">
      <div className="flex items-center gap-3">
        <Vote aria-hidden="true" size={21} className="text-blue-800 phong-toi:text-phong-xanh" />
        <div>
          <h3 className="font-semibold">Bầu lại chức danh</h3>
          {dangXuLy.length > 0 && <p className={LOP_CHU_PHU}>{dangXuLy.length} hồ sơ đang xử lý</p>}
        </div>
      </div>
      <button type="button" className={LOP_NUT_PHU} onClick={() => chuyenMuc('danh-sach')}>
        Mở hồ sơ bầu lại <ChevronRight aria-hidden="true" size={16} />
      </button>
    </div>
    {moiXacNhan && tenNguoiMoi && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-emerald-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">
      {moiXacNhan.chucDanh === 'TO_TRUONG' ? 'Lãnh đạo' : 'Tổ trưởng'} {moiXacNhan.tenNguoiXacNhan}
      {' đã xác nhận kết quả bầu cử. '}
      <strong>{tenNguoiMoi}</strong> chính thức giữ chức danh {CHUC_DANH[moiXacNhan.chucDanh]}.
    </p>}
    {mo && <HopThoaiHoSo
      tieuDe={muc === 'tao' ? 'Gửi yêu cầu bầu lại' : 'Bầu lại chức danh'}
      Icon={Vote} dangLuu={dangGui} onDong={() => chuyenMuc(null)}>
      <div className="space-y-4 text-sm">
        <div className="flex flex-wrap items-center justify-between gap-2">
          {muc !== 'danh-sach' ? (
            <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => chuyenMuc('danh-sach')}>
              <ArrowLeft aria-hidden="true" size={16} /> Về danh sách
            </button>
          ) : <span />}
          <button type="button" className={LOP_NUT_PHU} disabled={dangGui} onClick={() => chuyenMuc(null)}>
            <X aria-hidden="true" size={16} /> Đóng
          </button>
        </div>
        {loi && <p role="alert" className="rounded-lg bg-rose-50 p-3 text-rose-900 phong-toi:bg-phong-nhan-do phong-toi:text-phong-do">{loi}</p>}
        {!trang && !loi && <p role="status">Đang tải hồ sơ bầu lại…</p>}
        {trang && !coQuyenXem && <p>Hồ sơ dành cho thành viên và lãnh đạo phụ trách nhóm.</p>}
        {coQuyenXem && muc === 'danh-sach' && <>
          {coQuyenTao && <button type="button" className={LOP_NUT_CHINH} onClick={() => chuyenMuc('tao')}>
            Gửi yêu cầu bầu lại
          </button>}
          {dangXuLy.length > 0 && <section className="space-y-2">
            <h3 className="font-semibold">Đang xử lý</h3>
            <DanhSachHoSo hoSo={dangXuLy} chonHoSo={chuyenMuc} />
          </section>}
          {lichSu.length > 0 && <details className="space-y-2" open={dangXuLy.length === 0}>
            <summary className="cursor-pointer font-semibold">Lịch sử hồ sơ ({lichSu.length})</summary>
            <DanhSachHoSo hoSo={lichSu} chonHoSo={chuyenMuc} />
          </details>}
          {hoSo.length === 0 && <p className={LOP_CHU_PHU}>Nhóm chưa có yêu cầu bầu lại.</p>}
        </>}
        {coQuyenXem && daChon && <HoSoBauLai key={daChon.yeuCauId} hs={daChon} trang={trang}
          nguoiDungId={nguoiDungId} cuocHopTrongNhom={cuocHopTrongNhom} goi={gui} dangGui={dangGui} />}
        {muc === 'tao' && coQuyenTao && (
          <form className="space-y-4" onSubmit={async (event) => {
            event.preventDefault()
            const form = new FormData(event.currentTarget)
            if (await gui('', { bauToTruong: form.has('toTruong'), bauThuKy: form.has('thuKy'), lyDo: form.get('lyDo') })) {
              chuyenMuc('danh-sach')
            }
          }}>
            <fieldset className="flex flex-wrap gap-4">
              <legend className="mb-2 font-medium">Chức danh đề xuất bầu lại</legend>
              <label className="flex items-center gap-2"><input type="checkbox" name="toTruong" /> Tổ trưởng</label>
              <label className="flex items-center gap-2"><input type="checkbox" name="thuKy" /> Thư ký nhóm</label>
            </fieldset>
            <LyDo />
            <button className={LOP_NUT_CHINH} disabled={dangGui}>Gửi yêu cầu bầu lại</button>
          </form>
        )}
        {muc === 'tao' && trang && !coQuyenTao && <p className={LOP_CHU_PHU}>Hồ sơ bầu lại mới hiện không thể tạo.</p>}
        {typeof muc === 'number' && trang && !daChon && <p className={LOP_CHU_PHU}>Hồ sơ hiện không có trong danh sách của bạn.</p>}
      </div>
    </HopThoaiHoSo>}
  </section>
}

export function BauLaiTrongCuocHop({ cuocHopId, token }) {
  const phienDongBo = useDongBoDuLieu()
  const [duLieu, setDuLieu] = useState(null)
  const [loi, setLoi] = useState('')
  const [dangGui, setDangGui] = useState(false)
  const [lanTai, setLanTai] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    goiApi(`/api/cuoc-hop/${cuocHopId}/bau-lai-nhom`, { token, signal: controller.signal })
      .then((response) => docPhanHoiJson(response, 'Không thể tải lượt bầu của nhóm.'))
      .then((data) => { if (!controller.signal.aborted) setDuLieu(data) })
      .catch((error) => { if (!controller.signal.aborted) setLoi(error.message) })
    return () => controller.abort()
  }, [cuocHopId, token, phienDongBo, lanTai])

  async function xuLy(yeuCauId, thaoTac, noiDung) {
    if (dangGui) return
    setDangGui(true)
    setLoi('')
    try {
      await goiNhomLienPhong(`/${duLieu.nhomId}/bau-lai/${yeuCauId}/${thaoTac}`, token, {
        method: 'POST', duLieu: { ...noiDung, cuocHopId },
      })
      setLanTai((lan) => lan + 1)
    } catch (error) {
      setLoi(error.message)
    } finally {
      setDangGui(false)
    }
  }

  if (!duLieu) return loi ? <p role="alert" className={LOP_CHU_PHU}>{loi}</p> : null

  const { trang, cuTriCoMat } = duLieu
  return <section className={LOP_THE + ' space-y-4 border-blue-200 phong-toi:border-phong-vien'}>
    <header className="flex flex-wrap items-center justify-between gap-3">
      <div className="flex items-center gap-3">
        <span className="rounded-xl bg-blue-100 p-2.5 text-blue-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">
          <Vote aria-hidden="true" size={21} />
        </span>
        <div>
          <h2 className="font-semibold">Bầu lại chức danh nhóm</h2>
          <p className={LOP_CHU_PHU}>Tổ trưởng và Thư ký nhóm</p>
        </div>
      </div>
      <span className="rounded-full bg-blue-50 px-3 py-1 text-xs font-medium text-blue-900 phong-toi:bg-phong-nhan-xanh phong-toi:text-phong-xanh">
        {cuTriCoMat.length} thành viên nhóm đang có mặt
      </span>
    </header>
    {loi && <p role="alert" className="rounded-lg bg-rose-50 p-3 text-rose-900 phong-toi:bg-phong-nhan-do phong-toi:text-phong-do">{loi}</p>}
    {trang.thuKy && cuTriCoMat.length > 0 && <details className={LOP_CHU_PHU + ' rounded-lg bg-slate-50 p-3 phong-toi:bg-phong-mat-noi'}>
      <summary className="cursor-pointer font-semibold">Thành viên có mặt theo điểm danh</summary>
      <ul className="mt-2 grid gap-1 sm:grid-cols-2">
        {cuTriCoMat.map((tv) => <li key={tv.nguoiDungId}>{tv.hoTen}</li>)}
      </ul>
    </details>}
    {trang.hoSo.map((hs) => {
      const thaoTac = (ten, noiDung) => xuLy(hs.yeuCauId, ten, noiDung)
      const chucDanh = [hs.bauToTruong && 'TO_TRUONG', hs.bauThuKy && 'THU_KY'].filter(Boolean)
      return <div className={chucDanh.length > 1 ? "grid gap-3 lg:grid-cols-2" : "grid gap-3"} key={hs.yeuCauId}>
        {chucDanh.map((cd) => {
          const vong = hs.cacVong.find((v) => v.chucDanh === cd)
          const coTheTao = trang.thuKy && trang.dangHoatDong && hs.trangThai === 'DA_DUYET' &&
            hs.trangThaiCuocHop === 'DANG_DIEN_RA' && (!vong || vong.trangThai === 'BAU_LAI')
          return <div key={cd} className="space-y-2">
            {vong ? <VongBauLai vong={vong} hs={hs} trang={trang} goi={thaoTac} dangGui={dangGui}
              cuocHopId={cuocHopId} cuTriCoMat={cuTriCoMat} /> : <div className={LOP_THE + ' space-y-1'}>
              <h3 className="font-semibold">Bầu {CHUC_DANH[cd]}</h3>
              <p className={LOP_CHU_PHU}>Chờ thư ký nhóm tạo lượt bầu.</p>
            </div>}
            {coTheTao && <button type="button" className={LOP_NUT_CHINH} disabled={dangGui}
              onClick={() => thaoTac('tao-vong', { chucDanh: cd })}>
              {vong ? 'Tạo lượt bầu lại' : 'Tạo lượt bầu'}
            </button>}
            {vong?.trangThai === 'CHO_XAC_NHAN' && <p className={LOP_CHU_PHU}>
              Người có thẩm quyền xác nhận kết quả trong hồ sơ nhóm.
            </p>}
          </div>
        })}
      </div>
    })}
  </section>
}
